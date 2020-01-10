package com.qxs.generator.web.model.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 自定义用户插件中间表
 * 
 * @author qixingshen
 * @date 2018-06-01
 **/
@Entity
@Table(name = "user_custom_plugin_temp")
public class UserCustomPluginTemp {
	
	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 用户插件id
	 * **/
	private String userPluginId;
	/**
	 * 插件组名
	 * **/
	private String groupName;
	/**
	 * 插件名称
	 * **/
	private String name;
	/**
	 * 插件描述
	 * **/
	private String description;
	/**
	 * 插件模板内容
	 * **/
	private String templateContent;
	/**
	 * 插件生成器全路径名
	 * **/
	private String generator;
	/**
	 * 插件生成器源码
	 * **/
	private String generatorSourceContent;
	/**
	 * 插件生成器内容
	 * **/
	private String generatorContent;
	/**
	 * 生成的代码在zip文件中的相对目录(不包括生成的文件名)
	 * **/
	private String fileRelativeDir;
	/**
	 * 生成的代码文件后缀名
	 * **/
	private String fileSuffix;
	/**
	 * 类和文件名前缀
	 * **/
	private String prefix;
	/**
	 * 类和文件名后缀
	 * **/
	private String suffix;
	/**
	 * 依赖插件,以英文逗号(,)分割
	 * **/
	private String dependencies;
	/**
	 * 是否是自定义插件
	 * **/
	private Integer custom;
	
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
	 * 用户插件id
	 * **/
	public String getUserPluginId() {
		return userPluginId;
	}
	/**
	 * 用户插件id
	 * **/
	public void setUserPluginId(String userPluginId) {
		this.userPluginId = userPluginId;
	}

	public String getGroupName() {
		return groupName;
	}

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

	@Override
	public String toString() {
		return "UserCustomPluginTemp{" +
				"id='" + id + '\'' +
				", userPluginId='" + userPluginId + '\'' +
				", groupName='" + groupName + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", templateContent='" + templateContent + '\'' +
				", generator='" + generator + '\'' +
				", generatorSourceContent='" + generatorSourceContent + '\'' +
				", generatorContent='" + generatorContent + '\'' +
				", fileRelativeDir='" + fileRelativeDir + '\'' +
				", fileSuffix='" + fileSuffix + '\'' +
				", prefix='" + prefix + '\'' +
				", suffix='" + suffix + '\'' +
				", dependencies='" + dependencies + '\'' +
				", custom=" + custom +
				'}';
	}
}
