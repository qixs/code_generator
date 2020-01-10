package com.qxs.plugin.generator.mybatis.plus.lombok.mapper;

import java.util.List;
import java.util.Map;

import com.qxs.base.model.Column;
import com.qxs.base.model.Table;
import com.qxs.plugin.factory.exception.PluginNotFoundException;
import com.qxs.plugin.factory.generator.AbstractGenerator;
import com.qxs.plugin.factory.generator.IGenerator;
import com.qxs.plugin.factory.model.PluginConfig;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * dao代码生成器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
public class MybatisPlusLombokMapperGenerator extends AbstractGenerator implements IGenerator {

	private static final String ENTITY_PLUGIN_NAME = "entity";

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


		logger.debug("生成代码的数据:[{}]", map);

		return wrapper.wrap(map);
	}

}
