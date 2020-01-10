package com.qxs.generator.web.service.plugin;

import org.springframework.data.domain.Page;

import com.qxs.generator.web.model.plugin.PluginChangeHistory;

/**
 * @author qixingshen
 * **/
public interface IPluginChangeHistoryService {
	
	/**
	 * 登记变更明细信息
	 * @param pluginChangeHistory 变更信息
	 * @return int 更新条数
	 * **/
	String insert(PluginChangeHistory pluginChangeHistory);
	/**
	 * 查询插件变更记录列表
	 * 
	 * @param search
	 *            查询内容
	 * @return List<PluginChangeHistory> 插件变更记录
	 **/
	Page<PluginChangeHistory> findList(String search, Integer offset, Integer limit, String sort, String order);
}
