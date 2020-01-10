package com.qxs.generator.web.service.user;

import java.util.List;

import com.qxs.generator.web.model.user.UserPluginChangeHistoryDetail;

/**
 * 用户插件变更明细表service
 * 
 * @author qixingshen
 * @date 2018-5-31
 **/
public interface IUserPluginChangeHistoryDetailService {
	/**
	 * 插入变更明细
	 * @param pluginChangeHistoryDetails 变更明细
	 * @return void
	 * **/
	void insert(List<UserPluginChangeHistoryDetail> pluginChangeHistoryDetails);
	
	/**
	 * 查询插件变更明细记录
	 * @param pluginChangeHistoryId 插件变更id
	 * @return List<UserPluginChangeHistoryDetail>
	 * **/
	List<UserPluginChangeHistoryDetail> findList(String pluginChangeHistoryId);
}
