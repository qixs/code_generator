package com.qxs.generator.web.service.user;

import org.springframework.data.domain.Page;

import com.qxs.generator.web.model.user.UserPluginChangeHistory;

/**
 * 用户插件变更表service
 * 
 * @author qixingshen
 * @date 2018-5-31
 **/
public interface IUserPluginChangeHistoryService {

	/**
	 * 登记变更明细信息
	 * @param pluginChangeHistory 变更信息
	 * @return int 更新条数
	 * **/
	String insert(UserPluginChangeHistory pluginChangeHistory);
	
	/**
	 * 查询插件变更记录列表
	 * 
	 * @param search
	 *            查询内容
	 * @return List<UserPluginChangeHistory> 插件变更记录
	 **/
	Page<UserPluginChangeHistory> findList(String search, Integer offset, Integer limit, String sort, String order);
}
