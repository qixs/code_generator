package com.qxs.generator.web.model.user;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.annotation.ChangeEntityComment;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 用户插件表 plugin
 * 
 * @author qixingshen
 * @date 2018-06-01
 **/
@Entity
@Table(name = "user_plugin")
public class UserPlugin implements Cloneable{
	
	public static final int CUSTOM_STATUS_IS_NOT_CUSTOM = 0;
	public static final int CUSTOM_STATUS_IS_CUSTOM = 1;
	
	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	
	/**
	 * 用户
	 * **/
	@ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
	private User user;
	/**
	 * 用户id
	 * **/
	private String userId;
	/**
	 * 最后一次更新插件版本号
	 * **/
	@ChangeEntityComment("最后一次更新插件版本号")
	private String systemVersion;
	
	/**
	 * 创建时间
	 * **/
	@ChangeEntityComment("创建时间")
	private String createDate;
	
	/**
	 * 更新时间
	 * **/
	private String updateDate;
	/**
	 * 插件组名
	 * **/
	@ChangeEntityComment("插件组名")
	private String groupName;
	/**
	 * 插件名称
	 * **/
	@ChangeEntityComment("插件名称")
	private String name;
	/**
	 * 插件描述
	 * **/
	@ChangeEntityComment("插件描述")
	private String description;
	/**
	 * 插件模板文件路径
	 * **/
	@ChangeEntityComment("插件模板文件路径")
	private String templatePath;
	/**
	 * 插件模板内容
	 * **/
	@ChangeEntityComment("插件模板内容")
	private String templateContent;
	/**
	 * 插件生成器全路径名
	 * **/
	@ChangeEntityComment("插件生成器全路径名")
	private String generator;
	/**
	 * 插件生成器源码
	 * **/
	@ChangeEntityComment("插件生成器源码")
	private String generatorSourceContent;
	/**
	 * 插件生成器内容
	 * **/
	@ChangeEntityComment("插件生成器内容")
	private String generatorContent;
	/**
	 * 生成的代码在zip文件中的相对目录(不包括生成的文件名)
	 * **/
	@ChangeEntityComment("生成的代码在zip文件中的相对目录(不包括生成的文件名)")
	private String fileRelativeDir;
	/**
	 * 生成的代码文件后缀名
	 * **/
	@ChangeEntityComment("生成的代码文件后缀名")
	private String fileSuffix;
	/**
	 * 类和文件名前缀
	 * **/
	@ChangeEntityComment("类和文件名前缀")
	private String prefix;
	/**
	 * 类和文件名后缀
	 * **/
	@ChangeEntityComment("类和文件名后缀")
	private String suffix;
	/**
	 * 插件地址
	 * **/
	@ChangeEntityComment("插件地址")
	private String pluginPath;
	/**
	 * 依赖插件,以英文逗号(,)分割
	 * **/
	@ChangeEntityComment("依赖插件")
	private String dependencies;
	/**
	 * 状态
	 * **/
	@ChangeEntityComment("状态")
	private Integer status;
	/**
	 * 是否是自定义插件
	 * **/
	private Integer custom;
	
	/**
	 * 已分配权限状态
	 * **/
	@Transient
	private Integer allocationStatus;
	
	/**
	 * 变更信息
	 * **/
	@Transient
	private List<UserPluginChangeHistory> changeHistoryList;
	
	/**
	 * 主键
	 **/
	public String getId() {
		return id;
	}
	/**
	 * 主键
	 **/
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 最后一次更新插件版本号
	 * **/
	public String getSystemVersion() {
		return systemVersion;
	}
	/**
	 * 最后一次更新插件版本号
	 * **/
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
	/**
	 * 创建时间
	 * **/
	public String getCreateDate() {
		return createDate;
	}
	/**
	 * 创建时间
	 * **/
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
	/**
	 * 更新时间
	 * **/
	public String getUpdateDate() {
		return updateDate;
	}
	/**
	 * 更新时间
	 * **/
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	/**
	 * 插件组名
	 * **/
	public String getGroupName() {
		return groupName;
	}
	/**
	 * 插件组名
	 * **/
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * 插件名称
	 * **/
	public String getName() {
		return name;
	}
	/**
	 * 插件名称
	 * **/
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 插件描述
	 * **/
	public String getDescription() {
		return description;
	}
	/**
	 * 插件描述
	 * **/
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 插件模板文件路径
	 * **/
	public String getTemplatePath() {
		return templatePath;
	}
	/**
	 * 插件模板文件路径
	 * **/
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
	/**
	 * 插件模板内容
	 * **/
	public String getTemplateContent() {
		return templateContent;
	}
	/**
	 * 插件模板内容
	 * **/
	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}
	/**
	 * 插件生成器全路径名
	 * **/
	public String getGenerator() {
		return generator;
	}
	/**
	 * 插件生成器全路径名
	 * **/
	public void setGenerator(String generator) {
		this.generator = generator;
	}
	/**
	 * 插件生成器源码
	 * **/
	public String getGeneratorSourceContent() {
		return generatorSourceContent;
	}
	/**
	 * 插件生成器源码
	 * **/
	public void setGeneratorSourceContent(String generatorSourceContent) {
		this.generatorSourceContent = generatorSourceContent;
	}
	/**
	 * 插件生成器内容
	 * **/
	public String getGeneratorContent() {
		return generatorContent;
	}
	/**
	 * 插件生成器内容
	 * **/
	public void setGeneratorContent(String generatorContent) {
		this.generatorContent = generatorContent;
	}
	/**
	 * 生成的代码在zip文件中的相对目录(不包括生成的文件名)
	 * **/
	public String getFileRelativeDir() {
		return fileRelativeDir;
	}
	/**
	 * 生成的代码在zip文件中的相对目录(不包括生成的文件名)
	 * **/
	public void setFileRelativeDir(String fileRelativeDir) {
		this.fileRelativeDir = fileRelativeDir;
	}
	/**
	 * 生成的代码文件后缀名
	 * **/
	public String getFileSuffix() {
		return fileSuffix;
	}
	/**
	 * 生成的代码文件后缀名
	 * **/
	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}
	/**
	 * 类和文件名前缀
	 * **/
	public String getPrefix() {
		return prefix;
	}
	/**
	 * 类和文件名前缀
	 * **/
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * 类和文件名后缀
	 * **/
	public String getSuffix() {
		return suffix;
	}
	/**
	 * 类和文件名后缀
	 * **/
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	/**
	 * 插件地址
	 * **/
	public String getPluginPath() {
		return pluginPath;
	}
	/**
	 * 插件地址
	 * **/
	public void setPluginPath(String pluginPath) {
		this.pluginPath = pluginPath;
	}
	/**
	 * 状态
	 * **/
	public Integer getStatus() {
		return status;
	}
	/**
	 * 状态
	 * **/
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 变更信息
	 * **/
	public List<UserPluginChangeHistory> getChangeHistoryList() {
		return changeHistoryList;
	}
	/**
	 * 变更信息
	 * **/
	public void setChangeHistoryList(List<UserPluginChangeHistory> changeHistoryList) {
		this.changeHistoryList = changeHistoryList;
	}
	/**
	 * 用户
	 * **/
	public User getUser() {
		return user;
	}
	/**
	 * 用户
	 * **/
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * 依赖插件,以英文逗号(,)分割
	 * **/
	public String getDependencies() {
		return dependencies;
	}
	/**
	 * 依赖插件,以英文逗号(,)分割
	 * **/
	public void setDependencies(String dependencies) {
		this.dependencies = dependencies;
	}
	/**
	 * 用户id
	 * **/
	public String getUserId() {
		return userId;
	}
	/**
	 * 用户id
	 * **/
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * 是否是自定义插件
	 * **/
	public Integer getCustom() {
		return custom;
	}
	/**
	 * 是否是自定义插件
	 * **/
	public void setCustom(Integer custom) {
		this.custom = custom;
	}
	/**
	 * 已分配权限状态
	 * **/
	public Integer getAllocationStatus() {
		return allocationStatus;
	}
	/**
	 * 已分配权限状态
	 * **/
	public void setAllocationStatus(Integer allocationStatus) {
		this.allocationStatus = allocationStatus;
	}
	
	@Override
	public UserPlugin clone(){
		try {
			return (UserPlugin) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public String toString() {
		return "UserPlugin{" +
				"id='" + id + '\'' +
				", user=" + user +
				", userId='" + userId + '\'' +
				", systemVersion='" + systemVersion + '\'' +
				", createDate='" + createDate + '\'' +
				", updateDate='" + updateDate + '\'' +
				", groupName='" + groupName + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", templatePath='" + templatePath + '\'' +
				", templateContent='" + templateContent + '\'' +
				", generator='" + generator + '\'' +
				", generatorSourceContent='" + generatorSourceContent + '\'' +
				", generatorContent='" + generatorContent + '\'' +
				", fileRelativeDir='" + fileRelativeDir + '\'' +
				", fileSuffix='" + fileSuffix + '\'' +
				", prefix='" + prefix + '\'' +
				", suffix='" + suffix + '\'' +
				", pluginPath='" + pluginPath + '\'' +
				", dependencies='" + dependencies + '\'' +
				", status=" + status +
				", custom=" + custom +
				", allocationStatus=" + allocationStatus +
				", changeHistoryList=" + changeHistoryList +
				'}';
	}
}
