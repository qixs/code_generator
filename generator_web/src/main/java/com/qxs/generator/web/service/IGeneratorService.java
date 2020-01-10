package com.qxs.generator.web.service;

import org.springframework.web.socket.WebSocketSession;

import com.qxs.generator.web.model.GenerateResult;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.model.plugin.Plugin;

/**
 * @author qixingshen
 * **/
public interface IGeneratorService {
	
	/**
	 * 生成代码
	 * @param database 数据库配置信息
	 * @param ssh ssh配置信息
	 * @param generateParameter 生成参数
	 * **/
	GenerateResult generate(WebSocketSession session, Database database, Ssh ssh, GenerateParameter generateParameter);
	/**
	 * 生成代码
	 * @param database 数据库配置信息
	 * @param ssh ssh配置信息
	 * @param generateParameter 生成参数
	 * @param log 是否登记生成日志(通过生成日志再次生成不登记日志)
	 * **/
	GenerateResult generate(WebSocketSession session, Database database, Ssh ssh, GenerateParameter generateParameter, boolean log);
	/**
	 * 取本地测试表生成代码样例
	 * @param classContent 字节码
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * @return 代码样例
	 * **/
	String sourceGenerateCode(String classContent, String groupName, String pluginName);
	/**
	 * 取本地测试表生成代码样例
	 * @param template 模板
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * @return 代码样例
	 * **/
	String templateGenerateCode(String template, String groupName, String pluginName);
	/**
	 * 取本地测试表生成代码样例
	 * @param plugin 插件参数
	 * @return 代码样例
	 * **/
	String pluginGenerateCode(Plugin plugin);
	
	/**
	 * 取本地测试表生成代码样例(用户插件)
	 * @param classContent 字节码
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * @return 代码样例
	 * **/
	String userGenerateCode(String classContent, String groupName, String pluginName);
	/**
	 * 取本地测试表生成代码样例
	 * @param template 模板
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * @return 代码样例
	 * **/
	String userTemplateGenerateCode(String template, String groupName, String pluginName);
}
