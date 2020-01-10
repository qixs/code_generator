package com.qxs.generator.web.service.user;

import com.qxs.generator.web.model.user.UserCustomPluginTemp;

/**
 * 自定义用户插件中间表
 * **/
public interface IUserCustomPluginTempService {

	/**
	 * 新建自定义用户插件中间表信息
	 * @param userPluginId 用户插件id
	 * **/
	UserCustomPluginTemp newUserCustomPluginTemp(String userPluginId);
	/**
	 * 根据id查询自定义用户插件中间表信息
	 * @param id 临时表id
	 * @return UserCustomPluginTemp
	 * **/
	UserCustomPluginTemp getById(String id);
	/**
	 * 保存自定义插件配置
	 * **/
	void save(UserCustomPluginTemp userCustomPluginTemp);
	/**
	 * 保存到插件表
	 * **/
	void savePlugin(UserCustomPluginTemp userCustomPluginTemp);
	/**
	 * 生成代码
	 * @param userCustomPluginTemp 
	 * **/
	String generateCode(UserCustomPluginTemp userCustomPluginTemp);
}
