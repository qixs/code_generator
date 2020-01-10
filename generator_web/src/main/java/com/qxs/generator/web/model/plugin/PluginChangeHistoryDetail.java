package com.qxs.generator.web.model.plugin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 插件表变更历史明细  plugin_change_history_detail
 * 
 * @author qixingshen
 * @date 2018-06-01
 **/
@Entity
@Table(name = "plugin_change_history_detail")
public class PluginChangeHistoryDetail {
	
	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 变更主表
	 * **/
	@ManyToOne
    @JoinColumn(name = "changeHistoryId", nullable = false, insertable = false, updatable = false)
	private PluginChangeHistory changeHistory;
	/**
	 * 变更主表id
	 * **/
	private String changeHistoryId;
	/**
	 * 变更项名称(@ChangeEntityComment注解中的内容)
	 * **/
	private String changeFieldComment;
	/**
	 * 变更项字段名
	 * **/
	private String changeFieldName;
	/**
	 * 变更前的值
	 * **/
	private String changeBefore;
	/**
	 * 变更后的值
	 * **/
	private String changeAfter;
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
	 * 变更项名称(@ChangeEntityComment注解中的内容)
	 * **/
	public String getChangeFieldComment() {
		return changeFieldComment;
	}
	/**
	 * 变更主表
	 * **/
	public PluginChangeHistory getChangeHistory() {
		return changeHistory;
	}
	/**
	 * 变更主表
	 * **/
	public void setChangeHistory(PluginChangeHistory changeHistory) {
		this.changeHistory = changeHistory;
	}
	/**
	 * 变更项名称(@ChangeEntityComment注解中的内容)
	 * **/
	public void setChangeFieldComment(String changeFieldComment) {
		this.changeFieldComment = changeFieldComment;
	}
	/**
	 * 变更项字段名
	 * **/
	public String getChangeFieldName() {
		return changeFieldName;
	}
	/**
	 * 变更项字段名
	 * **/
	public void setChangeFieldName(String changeFieldName) {
		this.changeFieldName = changeFieldName;
	}
	/**
	 * 变更前的值
	 * **/
	public String getChangeBefore() {
		return changeBefore;
	}
	/**
	 * 变更前的值
	 * **/
	public void setChangeBefore(String changeBefore) {
		this.changeBefore = changeBefore;
	}
	/**
	 * 变更后的值
	 * **/
	public String getChangeAfter() {
		return changeAfter;
	}
	/**
	 * 变更后的值
	 * **/
	public void setChangeAfter(String changeAfter) {
		this.changeAfter = changeAfter;
	}
	/**
	 * 变更主表id
	 * **/
	public String getChangeHistoryId() {
		return changeHistoryId;
	}
	/**
	 * 变更主表id
	 * **/
	public void setChangeHistoryId(String changeHistoryId) {
		this.changeHistoryId = changeHistoryId;
	}
	
	@Override
	public String toString() {
		return String.format("PluginChangeHistoryDetail{id = %s , changeHistory = %s , changeFieldComment = %s , changeFieldName = %s , changeBefore = %s , changeAfter = %s}", id, changeHistory, changeFieldComment, changeFieldName, changeBefore, changeAfter);
	}
}
