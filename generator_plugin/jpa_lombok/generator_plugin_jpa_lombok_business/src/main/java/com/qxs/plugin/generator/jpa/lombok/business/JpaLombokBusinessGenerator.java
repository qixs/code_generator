package com.qxs.plugin.generator.jpa.lombok.business;

import com.qxs.base.model.Column;
import com.qxs.base.model.Table;
import com.qxs.plugin.factory.exception.CodeGenerateException;
import com.qxs.plugin.factory.exception.PluginNotFoundException;
import com.qxs.plugin.factory.generator.AbstractGenerator;
import com.qxs.plugin.factory.generator.IGenerator;
import com.qxs.plugin.factory.model.PluginConfig;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;
import java.util.Map;

/**
 * business层代码生成器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
public class JpaLombokBusinessGenerator extends AbstractGenerator implements IGenerator {

	private static final String ENTITY_PLUGIN_NAME = "entity";

	private static final String SERVICE_INTERFACE_PLUGIN_NAME = "serviceInterface";

	@Override
	protected TemplateModel wrap(PluginConfig[] allPluginConfigs,PluginConfig pluginConfig, Table table, String[] removePrefixs)
			throws TemplateModelException {
		BeansWrapper wrapper = beansWrapper();
		Map<String, Object> map = beanToMap(table);

		List<Column> columns = table.getColumns();
		for(Column column : columns) {
			//设置java字段名
			column.setJavaName(formatName(column.getName(), removePrefixs));
		}
		// 生成代码人员名称
		map.put("author", getAuthor());
		// 生成代码时间
		map.put("createDate", getDate());

		String className = formatName(table.getName(), removePrefixs);
		className = className.substring(0, 1).toUpperCase() + className.substring(1);

		String prefix = pluginConfig.getPrefix();
		String suffix = pluginConfig.getSuffix();

		map.put("prefix", prefix);
		map.put("suffix", suffix);
		// 类名
		map.put("className", className);

		// entity插件配置
		String entityClassName = className;

		PluginConfig entityPluginConfig = getPluginConfig(allPluginConfigs, pluginConfig.getGroupName(), ENTITY_PLUGIN_NAME);
		if(null == entityPluginConfig) {
			throw new PluginNotFoundException("插件["+ENTITY_PLUGIN_NAME+"]未找到");
		}
		entityClassName = entityPluginConfig.getPrefix() + entityClassName + entityPluginConfig.getSuffix();

		map.put("entityClassName", entityClassName);
		map.put("entityClassParameterName",
				entityClassName.substring(0, 1).toLowerCase() + entityClassName.substring(1));

		//主键字段
		Column primaryKeyColumn = table.getPrimaryKeyColumn();
		if(primaryKeyColumn != null){
			String javaType = primaryKeyColumn.getJavaType();
			if("byte[]".equals(javaType)){
				throw new CodeGenerateException("主键字段不支持byte[]类型,表名:[" + table.getName() + "],字段名:[" + primaryKeyColumn.getName() + "]");
			}
			//java中基本数据类型第一位都是小写字母,包装类型及java类都是以大写字母开头,所以认为只要是小写字母开头就认为是基本数据类型,大写字母开头即认为是java类型

			char first = primaryKeyColumn.getJavaTypeClassSimpleName().charAt(0);

			logger.debug("primaryKeyColumn javaType:[{}]", javaType);

			if(first >= 65 && first <= 90){
				//大写字母
				Class<?> clazz = null;
				try {
					clazz = Class.forName(javaType);
				} catch (ClassNotFoundException e) {
					throw new CodeGenerateException("不支持的类型,java类型:[" + javaType + "],表名:[" + table.getName() + "],字段名:[" + primaryKeyColumn.getName() + "]");
				}
				//非lang包下的类需要import
				if(!javaType.startsWith(String.class.getPackage().getName())) {
					map.put("primaryKeyColumnImportClassName", javaType);
				}
				map.put("primaryKeyColumnClassName", clazz.getSimpleName());
			}else if(first >= 97 && first <= 122){
				//小写字母
				map.put("primaryKeyColumnClassName", javaType);
			}else{
				throw new CodeGenerateException("不支持的类型,java类型:[" + javaType + "],表名:[" + table.getName() + "],字段名:[" + primaryKeyColumn.getName() + "]");
			}
		}else {
			map.remove("primaryKeyColumn");
		}

		//serviceInterface插件
		String serviceInterfaceClassName = className;

		PluginConfig serviceInterfacePluginConfig = getPluginConfig(allPluginConfigs, pluginConfig.getGroupName(), SERVICE_INTERFACE_PLUGIN_NAME);
		if(null == serviceInterfacePluginConfig) {
			throw new PluginNotFoundException("插件["+SERVICE_INTERFACE_PLUGIN_NAME+"]未找到");
		}
		serviceInterfaceClassName = serviceInterfacePluginConfig.getPrefix() + serviceInterfaceClassName + serviceInterfacePluginConfig.getSuffix();
		String serviceInterfaceClassParameterName = className.substring(0, 1).toLowerCase() + className.substring(1) + serviceInterfacePluginConfig.getSuffix();

		map.put("serviceInterfaceClassName", serviceInterfaceClassName);

		map.put("serviceInterfaceClassParameterName", serviceInterfaceClassParameterName);


		logger.debug("生成代码的数据:[{}]", map);

		return wrapper.wrap(map);
	}

}
