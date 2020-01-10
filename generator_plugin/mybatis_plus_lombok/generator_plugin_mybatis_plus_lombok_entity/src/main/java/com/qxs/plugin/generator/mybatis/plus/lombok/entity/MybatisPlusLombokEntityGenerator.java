package com.qxs.plugin.generator.mybatis.plus.lombok.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.qxs.plugin.factory.model.PluginConfig;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qxs.base.model.Column;
import com.qxs.base.model.Table;
import com.qxs.plugin.factory.generator.AbstractGenerator;
import com.qxs.plugin.factory.generator.IGenerator;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * entity代码生成器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
public class MybatisPlusLombokEntityGenerator extends AbstractGenerator implements IGenerator {
	
	@Override
	protected TemplateModel wrap(PluginConfig[] allPluginConfigs,PluginConfig pluginConfig, Table table, String[] removePrefixs) throws TemplateModelException{
		BeansWrapper wrapper = beansWrapper();
		Map<String,Object> map = beanToMap(table);
		
		//组装import信息
		List<String> imports = new ArrayList<String>();
		List<Column> columns = table.getColumns();
		for(Column column : columns) {
			//设置java字段名
			column.setJavaName(formatName(column.getName(), removePrefixs));
			column.setMethodName(column.getJavaName().substring(0, 1).toUpperCase() + column.getJavaName().substring(1));
			
			String importClassName = column.getJavaType();
			//不导入java.lang目录下的类
			if(importClassName.startsWith(String.class.getPackage().getName())) {
				continue;
			}
			//不导入基本数据类型
			//java中基本数据类型第一位都是小写字母,包装类型及java类都是以大写字母开头,所以认为只要是小写字母开头就认为是基本数据类型,大写字母开头即认为是java类型
			char first = column.getJavaTypeClassSimpleName().charAt(0);
			if(first >= 97 && first <= 122){
				continue;
			}
			//如果类名已经存在则不导入
			if(imports.contains(importClassName)) {
				continue;
			}
			
			//如果是Date类型则需要引入日期转换注解的引用
			if(column.getJavaType().endsWith(Date.class.getName()) && !imports.contains(JsonFormat.class.getName())) {
				imports.add(JsonFormat.class.getName());
				imports.add(DateTimeFormat.class.getName());
			}
			
			imports.add(importClassName);
		}
		
		//排序需要导入的类名
		Collections.sort(imports);
		map.put("imports", imports);
		
		//生成代码人员名称
		map.put("author", getAuthor());
		//生成代码时间
		map.put("createDate", getDate());
		
		String className = formatName(table.getName(), removePrefixs);
		className = className.substring(0, 1).toUpperCase() + className.substring(1);

		String prefix = pluginConfig.getPrefix();
		String suffix = pluginConfig.getSuffix();

		map.put("prefix",prefix);
		map.put("suffix",suffix);
		//类名
		map.put("className", className);
		
		long random = new Random(System.nanoTime()).nextLong();
		if(random < 1000000000000000000L) {
			random = random * 10;
		}
		if(random > 0) {
			random = random * -1;
		}
		map.put("serialVersionUID", random + "L");
		
		logger.debug("生成代码的数据:[{}]", map);
		
		return wrapper.wrap(map);
	}

}
