package com.qxs.generator.web.service.plugin;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.plugin.factory.model.PluginConfig;

/**
 * @author qixingshen
 * **/
public interface IPluginService {
	
	/**
	 * 重新加载新添加的插件(历史插件信息不动)
	 * 
	 * @return void
	 * **/
	void reloadAllPlugin();

	/**
	 * 获取所有的插件组名列表
	 *
	 * @return List<String> 插件列表
	 * **/
	List<String> findPluginGroupNameList();
	/**
	 * 获取所有的插件列表(按照名称正序排序)
	 * @param groupName 组名
	 * 
	 * @return List<Plugin> 插件列表
	 * **/
	List<Plugin> findPluginList(String groupName);
	/**
	 * 获取所有的插件列表
	 * 
	 * @param plugin 插件信息
	 * @param sort 排序字段信息
	 * 
	 * @return List<Plugin> 插件列表
	 * **/
	List<Plugin> findPluginList(Plugin plugin,Sort sort);
	/**
	 * 禁用单个插件
	 * 
	 * @param id 插件id
	 * 
	 * @return int 禁用个数
	 * **/
	int disablePlugin(String id);
	/**
	 * 禁用多个插件
	 * 
	 * @param ids 插件id集合
	 * 
	 * @return int 禁用个数
	 * **/
	int disablePlugins(List<String> ids);
	/**
	 * 启用单个插件
	 * 
	 * @param id 插件id
	 * 
	 * @return int 启用个数
	 * **/
	int enablePlugin(String id);
	/**
	 * 启用多个插件
	 * 
	 * @param ids 插件id集合
	 * 
	 * @return int 启用个数
	 * **/
	int enablePlugins(List<String> ids);
	
	/**
	 * 根据插件名称获取插件信息
	 * @param pluginGroupName 插件组名
	 * @param pluginName 插件名称
	 * @return Plugin
	 * **/
	Plugin getPluginByName(String pluginGroupName, String pluginName);
	/**
	 * 根据插件名称集合获取插件信息
	 * @param pluginGroupName 插件组名
	 * @param pluginNames 插件名称集合
	 * @return Plugin
	 * **/
	List<Plugin> findPluginByNames(String pluginGroupName, List<String> pluginNames);
	/**
	 * 上传插件
	 * @param file 插件包
	 * **/
	boolean uploadPlugin(MultipartFile file);
	/**
	 * 保存插件生成器源代码
	 * @param pluginGroupName 插件组名
	 * @param pluginName 插件名称
	 * @param source 代码生成器源码
	 * **/
	void savePluginGeneratorSourceContent(String pluginGroupName, String pluginName, String source);
	/**
	 * 保存插件生成器模板
	 * @param pluginGroupName 插件组名
	 * @param pluginName 插件名称
	 * @param templateContent 插件生成器模板
	 * **/
	void savePluginGeneratorTemplateContent(String pluginGroupName, String pluginName, String templateContent);
	/**
	 * 保存插件生成器参数
	 * @param plugin 插件信息
	 * **/
	void savePluginConfig(Plugin plugin);
	/**
	 * 代码生成器恢复初始值
	 * @param pluginGroupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	String loadPluginSource(String pluginGroupName, String pluginName);
	/**
	 * 模板恢复初始值
	 * @param pluginGroupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	String loadPluginTemplate(String pluginGroupName, String pluginName);
	/**
	 * 参数恢复初始值
	 * @param pluginGroupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	Plugin loadPluginConfig(String pluginGroupName, String pluginName);
	/**
	 * 代码生成器恢复为系统插件值
	 * @param pluginGroupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	String loadSystemSource(String pluginGroupName, String pluginName);
	/**
	 * 模板恢复为系统插件值
	 * @param pluginGroupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	String loadSystemTemplate(String pluginGroupName, String pluginName);
	/**
	 * 参数恢复为系统插件值
	 * @param pluginGroupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	PluginConfig loadSystemConfig(String pluginGroupName, String pluginName);
}
