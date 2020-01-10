package com.qxs.generator.web.model.plugin;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 插件表变更历史 plugin_change_history
 * 
 * @author qixingshen
 * @date 2018-06-01
 **/
@Entity
@Table(name = "plugin_change_history")
public class PluginChangeHistory {
	
	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 插件id
	 * **/
	private String pluginId;
	/**
	 * 插件
	 **/
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pluginId", insertable = false, updatable = false)
	private Plugin plugin;
	
	/**
	 * 变更人id(可为空,如果是系统重启自动加载插件则获取不到变更人id)
	 * **/
	private String updateUserId;
	/**
	 * 变更人姓名(变更人姓名或系统更新)
	 * **/
	private String updateUserName;
	/**
	 * 变更时间
	 * **/
	private String updateDate;
	/**
	 * 变更明细
	 * **/
	@Transient
	private List<PluginChangeHistoryDetail> changeHistoryDetailList = new ArrayList<>();
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
	 * 插件id
	 * **/
	public String getPluginId() {
		return pluginId;
	}
	/**
	 * 插件id
	 * **/
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	/**
	 * 变更人id(可为空,如果是系统重启自动加载插件则获取不到变更人id)
	 * **/
	public String getUpdateUserId() {
		return updateUserId;
	}
	/**
	 * 变更人id(可为空,如果是系统重启自动加载插件则获取不到变更人id)
	 * **/
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	/**
	 * 变更人姓名(变更人姓名或系统更新)
	 * **/
	public String getUpdateUserName() {
		return updateUserName;
	}
	/**
	 * 变更人姓名(变更人姓名或系统更新)
	 * **/
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
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
	 * 插件
	 **/
	public Plugin getPlugin() {
		return plugin;
	}
	/**
	 * 插件
	 **/
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	/**
	 * 变更明细
	 * **/
	public List<PluginChangeHistoryDetail> getChangeHistoryDetailList() {
		return changeHistoryDetailList;
	}
	/**
	 * 变更明细
	 * **/
	public void setChangeHistoryDetailList(List<PluginChangeHistoryDetail> changeHistoryDetailList) {
		this.changeHistoryDetailList = changeHistoryDetailList;
		//设置changeHistoryDetail的changeHistory属性才能级联保存
		if(changeHistoryDetailList != null) {
			changeHistoryDetailList.stream().forEach(changeHistoryDetail -> {
				changeHistoryDetail.setChangeHistory(this);
			});
		}
	}
	/**
	 * 添加变更明细
	 * **/
	public void addChangeHistoryDetail(PluginChangeHistoryDetail changeHistoryDetail) {
		if(this.changeHistoryDetailList == null) {
			this.changeHistoryDetailList = new ArrayList<>();
		}
		//设置changeHistoryDetail的changeHistory属性才能级联保存
		changeHistoryDetail.setChangeHistory(this);
		
		this.changeHistoryDetailList.add(changeHistoryDetail);
	}
	

	@Override
	public String toString() {
		return String.format("PluginChangeHistory{id = %s , pluginId = %s , updateUserId = %s , updateUserName = %s , "
				+ "updateDate = %s , changeHistoryDetailList = %s}", 
				id, pluginId, updateUserId, updateUserName, updateDate, changeHistoryDetailList);
	}
}
