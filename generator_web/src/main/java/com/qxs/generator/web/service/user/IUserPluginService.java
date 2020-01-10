package com.qxs.generator.web.service.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.google.common.collect.Lists;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.user.UserPlugin;

/**
 * 用户插件表service
 * 
 * @author qixingshen
 * @date 2018-5-31
 **/
public interface IUserPluginService {
	
	/**
	 * 根据id获取用户插件信息
	 * @param id 用户插件id
	 * @return UserPlugin
	 * **/
	UserPlugin getById(String id);
	/**
	 * 根据对象查询所有的用户插件列表
	 * @param userPlugin 
	 * @return List<UserPlugin>
	 * **/
	List<UserPlugin> findList(UserPlugin userPlugin);
	/**
	 * 禁用单个插件
	 * 
	 * @param id 插件id
	 * 
	 * @return int 禁用个数
	 * **/
	default int disablePlugin(String id) {
		return disablePlugins(Lists.newArrayList(id));
	}
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
	default int enablePlugin(String id) {
		return enablePlugins(Lists.newArrayList(id));
	}
	/**
	 * 启用多个插件
	 * 
	 * @param ids 插件id集合
	 * 
	 * @return int 启用个数
	 * **/
	int enablePlugins(List<String> ids);
	
	/**
	 * 查询用户插件列表
	 * 
	 * @param userPlugin 插件对象
	 * @param sort 排序字段信息
	 * 
	 * @return List<UserPlugin>
	 * 
	 * **/
	List<UserPlugin> findUserPluginList(UserPlugin userPlugin,Sort sort);
	/**
	 * 删除用户插件信息
	 * @param userPlugins 用户插件列表
	 * @return void 
	 * **/
	void deleteUserPlugins(List<UserPlugin> userPlugins);
	/**
	 * 根据插件名称获取插件信息
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * @return Plugin
	 * **/
	UserPlugin getPluginByName(String groupName, String pluginName);
	
	/**
	 * 获取插件列表，如果已经分配给该用户则需要置状态为已分配
	 * **/
	List<UserPlugin> findUserAllocationPluginList(String userId);
	
	/**
	 * 分配插件
	 * @param userId 用户id
	 * @param groupName 插件组名
	 * @param pluginName 插件名
	 * **/
	void allocation(String userId, String groupName, String pluginName);
	
	/**
	 * 取消分配权限（如果收回用户针对该插件的使用权则需要删除该用户下所有的该插件信息，包括已禁用状态的插件，不包括自定义插件）
	 * @param userId 用户id
	 * @param pluginName 插件名
	 * **/
	void recovery(String userId, String pluginName);
	
	/**
	 * 批量新增用户插件
	 * @param userPlugins 用户插件
	 * **/
	void batchInsert(List<UserPlugin> userPlugins);
	
	/**
	 * 查询用户插件列表
	 * 
	 * @param search
	 *            查询内容
	 * @return Page<UserPlugin> 用户插件信息
	 **/
	Page<UserPlugin> findList(String search, Integer offset, Integer limit,String sort, String order);
	
	/**
	 * 删除插件
	 * @param pluginId 插件id
	 * **/
	void delete(String pluginId);
	
	/**
	 * 插件恢复默认
	 * @param pluginId 插件id
	 * **/
	void setDefault(String pluginId);
	/**
	 * 代码生成器恢复默认
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	String loadDefaultSource(String groupName, String pluginName);
	/**
	 * 模板恢复默认
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	String loadDefaultTemplate(String groupName, String pluginName);
	/**
	 * 保存插件生成器源代码
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * @param source 代码生成器源码
	 * **/
	void savePluginGeneratorSourceContent(String groupName, String pluginName, String source);
	/**
	 * 保存插件生成器模板
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * @param templateContent 插件生成器模板
	 * **/
	void savePluginGeneratorTemplateContent(String groupName, String pluginName, String templateContent);
	
	/**
	 * 代码生成器恢复为系统插件值
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	String loadSystemSource(String groupName, String pluginName);
	/**
	 * 模板恢复为系统插件值
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	String loadSystemTemplate(String groupName, String pluginName);
	/**
	 * 参数恢复为系统插件值
	 * @param groupName 插件组名
	 * @param pluginName 插件名称
	 * **/
	Plugin loadSystemConfig(String groupName, String pluginName);
	
	/**
	 * 保存插件
	 * @param tempId 自定义插件中间表id
	 * **/
	String savePlugin(String tempId);
	
	/**
	 * 根据插件名称查询所有的插件
	 * @param groupName 插件组名
	 * @param pluginNames 插件名称集合
	 * **/
	List<UserPlugin> findByPluginNames(String groupName, String[] pluginNames);

	/**
	 * 校验用户自定义插件权限
	 * **/
	void checkCustomPluginConfig();
}
