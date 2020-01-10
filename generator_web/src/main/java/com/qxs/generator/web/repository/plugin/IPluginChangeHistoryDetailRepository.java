package com.qxs.generator.web.repository.plugin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.plugin.PluginChangeHistoryDetail;

@Repository
public interface IPluginChangeHistoryDetailRepository 
	extends JpaRepository<PluginChangeHistoryDetail, String>,JpaSpecificationExecutor<PluginChangeHistoryDetail> {
	
}