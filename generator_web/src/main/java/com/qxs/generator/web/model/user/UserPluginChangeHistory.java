package com.qxs.generator.web.model.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 用户插件表变更历史 user_plugin_change_history
 * 
 * @author qixingshen
 * @date 2018-06-01
 **/
@Entity
@Table(name = "user_plugin_change_history")
public class UserPluginChangeHistory {

	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	
	/**
	 * 用户插件信息
	 * **/
	private String pluginId;
	/**
	 * 插件组名,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	private String pluginGroupName;
	/**
	 * 插件名,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	private String pluginName;
	/**
	 * 插件描述,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	private String pluginDescription;
	/**
	 * 插件依赖,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	private String pluginDependencies;
	/**
	 * 变更人id(插件所属人id)
	 * **/
	private String userId;
	/**
	 * 变更时间
	 * **/
	private String updateDate;
	/**
	 * 变更明细
	 * **/
	@Transient
	private List<UserPluginChangeHistoryDetail> changeHistoryDetailList;
	/**
	 * 主键
	 * **/
	public String getId() {
		return id;
	}
	/**
	 * 主键
	 * **/
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 变更人id(插件所属人id)
	 * **/
	public String getUserId() {
		return userId;
	}
	/**
	 * 变更人id(插件所属人id)
	 * **/
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * 变更时间
	 * **/
	public String getUpdateDate() {
		return updateDate;
	}
	/**
	 * 变更时间
	 * **/
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	/**
	 * 变更明细
	 * **/
	public List<UserPluginChangeHistoryDetail> getChangeHistoryDetailList() {
		return changeHistoryDetailList;
	}
	/**
	 * 变更明细
	 * **/
	public void setChangeHistoryDetailList(List<UserPluginChangeHistoryDetail> changeHistoryDetailList) {
		this.changeHistoryDetailList = changeHistoryDetailList;
	}
	/**
	 * 用户插件信息
	 * **/
	public String getPluginId() {
		return pluginId;
	}
	/**
	 * 用户插件信息
	 * **/
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	
	/**
	 * 添加变更明细
	 * **/
	public void addChangeHistoryDetail(UserPluginChangeHistoryDetail changeHistoryDetail) {
		if(this.changeHistoryDetailList == null) {
			this.changeHistoryDetailList = new ArrayList<>();
		}
		
		this.changeHistoryDetailList.add(changeHistoryDetail);
	}

	public String getPluginGroupName() {
		return pluginGroupName;
	}

	public void setPluginGroupName(String pluginGroupName) {
		this.pluginGroupName = pluginGroupName;
	}

	/**
	 * 插件名,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	public String getPluginName() {
		return pluginName;
	}
	/**
	 * 插件名,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	/**
	 * 插件描述,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	public String getPluginDescription() {
		return pluginDescription;
	}
	/**
	 * 插件描述,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	public void setPluginDescription(String pluginDescription) {
		this.pluginDescription = pluginDescription;
	}
	/**
	 * 插件依赖,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	public String getPluginDependencies() {
		return pluginDependencies;
	}
	/**
	 * 插件依赖,冗余,删除自定义插件时必须使用该字段确定唯一
	 * **/
	public void setPluginDependencies(String pluginDependencies) {
		this.pluginDependencies = pluginDependencies;
	}
	@Override
	public String toString() {
		return String.format("UserPluginChangeHistory{id = %s , updateDate = %s , "
				+ "changeHistoryDetailList = %s}", id, updateDate, changeHistoryDetailList);
	}
}
