package com.qxs.generator.web.model.plugin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.annotation.ChangeEntityComment;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 插件表 plugin
 * 
 * @author qixingshen
 * @date 2018-06-01
 **/
@Entity
@Table(name = "plugin")
public class Plugin implements Cloneable{
	
	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 最后一次更新插件版本号
	 * **/
	@ChangeEntityComment("最后一次更新插件版本号")
	private String systemVersion;
	
	/**
	 * 创建时间
	 * **/
	private String createDate;
	/**
	 * 创建人id(可为空,如果是系统重启自动加载插件则获取不到创建人id)
	 * **/
	private String createUserId;
	/**
	 * 创建人姓名(创建人姓名或系统更新)
	 * **/
	private String createUserName;
	/**
	 * 更新时间
	 * **/
	private String updateDate;
	/**
	 * 更新人id(可为空,如果是系统重启自动加载插件则获取不到更新人id)
	 * **/
	private String updateUserId;
	/**
	 * 更新人姓名(更新人姓名或系统更新)
	 * **/
	private String updateUserName;
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
	 * 变更信息
	 * **/
	@Transient
	private transient PluginChangeHistory changeHistory;
	
	/**
	 * 主键
	 **/
	public String getId() {
		return id;
	}
	/**
	 * 主键
	 **/
	public Plugin setId(String id) {
		this.id = id;
		return this;
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
	public Plugin setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
		return this;
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
	public Plugin setCreateDate(String createDate) {
		this.createDate = createDate;
		return this;
	}
	/**
	 * 创建人id(可为空,如果是系统重启自动加载插件则获取不到创建人id)
	 * **/
	public String getCreateUserId() {
		return createUserId;
	}
	/**
	 * 创建人id(可为空,如果是系统重启自动加载插件则获取不到创建人id)
	 * **/
	public Plugin setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
		return this;
	}
	/**
	 * 创建人姓名(创建人姓名或系统更新)
	 * **/
	public String getCreateUserName() {
		return createUserName;
	}
	/**
	 * 创建人姓名(创建人姓名或系统更新)
	 * **/
	public Plugin setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
		return this;
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
	public Plugin setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
		return this;
	}
	/**
	 * 更新人id(可为空,如果是系统重启自动加载插件则获取不到更新人id)
	 * **/
	public String getUpdateUserId() {
		return updateUserId;
	}
	/**
	 * 更新人id(可为空,如果是系统重启自动加载插件则获取不到更新人id)
	 * **/
	public Plugin setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
		return this;
	}
	/**
	 * 更新人姓名(更新人姓名或系统更新)
	 * **/
	public String getUpdateUserName() {
		return updateUserName;
	}
	/**
	 * 更新人姓名(更新人姓名或系统更新)
	 * **/
	public Plugin setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
		return this;
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
	public Plugin setName(String name) {
		this.name = name;
		return this;
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
	public Plugin setDescription(String description) {
		this.description = description;
		return this;
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
	public Plugin setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
		return this;
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
	public Plugin setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
		return this;
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
	public Plugin setGenerator(String generator) {
		this.generator = generator;
		return this;
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
	public Plugin setFileRelativeDir(String fileRelativeDir) {
		this.fileRelativeDir = fileRelativeDir;
		return this;
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
	public Plugin setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
		return this;
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
	public Plugin setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
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
	public Plugin setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
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
	public Plugin setPluginPath(String pluginPath) {
		this.pluginPath = pluginPath;
		return this;
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
	public Plugin setStatus(Integer status) {
		this.status = status;
		return this;
	}
	/**
	 * 变更信息
	 * **/
	public PluginChangeHistory getChangeHistory() {
		return changeHistory;
	}
	/**
	 * 变更信息
	 * **/
	public void setChangeHistory(PluginChangeHistory changeHistory) {
		this.changeHistory = changeHistory;
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
	
	@Override
	public Plugin clone() {
		try {
			return (Plugin) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public String toString() {
		return "Plugin{" +
				"id='" + id + '\'' +
				", systemVersion='" + systemVersion + '\'' +
				", createDate='" + createDate + '\'' +
				", createUserId='" + createUserId + '\'' +
				", createUserName='" + createUserName + '\'' +
				", updateDate='" + updateDate + '\'' +
				", updateUserId='" + updateUserId + '\'' +
				", updateUserName='" + updateUserName + '\'' +
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
				", changeHistory=" + changeHistory +
				'}';
	}
}
