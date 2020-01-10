package com.qxs.generator.web.service.plugin;

import java.util.List;

import com.qxs.generator.web.model.plugin.PluginChangeHistoryDetail;

/**
 * @author qixingshen
 * **/
public interface IPluginChangeHistoryDetailService {

	/**
	 * 插入变更明细
	 * @param pluginChangeHistoryDetails 变更明细
	 * @return void
	 * **/
	void insert(List<PluginChangeHistoryDetail> pluginChangeHistoryDetails);
	
	/**
	 * 查询插件变更明细记录
	 * @param pluginChangeHistoryId 插件变更id
	 * @return List<PluginChangeHistoryDetail>
	 * **/
	List<PluginChangeHistoryDetail> findList(String pluginChangeHistoryId);
	
}
