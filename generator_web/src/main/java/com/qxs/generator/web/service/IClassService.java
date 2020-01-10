package com.qxs.generator.web.service;

import com.qxs.generator.web.model.user.UserPlugin;

/**
 * 字节码服务
 * @author qixingshen
 * **/
public interface IClassService {
	
	/**
	 * 生成字节码
	 * @param source 源代码
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * 
	 * @return String 字节码
	 * **/
	String generateClassContent(String source, String groupName, String pluginName);
	/**
	 * 生成字节码
	 * @param source 源代码
	 * @param className 类名
	 * 
	 * @return String 字节码
	 * **/
	String generateClassContentByClassName(String source, String className);
	/**
	 * 用户插件生成字节码
	 * @param source 源代码
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * 
	 * @return String 字节码
	 * **/
	String userGenerateClassContent(String source, String groupName, String pluginName);
	/**
	 * 用户插件生成字节码
	 * @param source 源代码
	 * @param userPlugin 插件信息
	 * 
	 * @return String 字节码
	 * **/
	String userGenerateClassContent(String source, UserPlugin userPlugin);
}
