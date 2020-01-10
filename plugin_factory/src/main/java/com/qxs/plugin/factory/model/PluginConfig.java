package com.qxs.plugin.factory.model;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Transient;

/**
 * 配置文件实体
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
public class PluginConfig implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String ROOT_TAG_NAME = "plugin";
	
	public static final String PLUGIN_FILE_NAME = "plugin.yml";

	/**
	 * 插件组名
	 * **/
	private String groupName;
	/**
	 * 插件名称
	 * **/
	private String name;
	/**
	 * 插件介绍
	 * **/
	private String description;
	/***
	 * 插件模板文件路径
	 * */
	private String templatePath;
	/***
	 * 插件生成器全路径名
	 * */
	private String generator;
	/**
	 * 生成的代码在zip文件中的相对目录(不包括生成的文件名)
	 * **/
	private String fileRelativeDir;
	/**
	 * 生成的代码文件后缀名
	 * **/
	private String fileSuffix;
	/**
	 * 类名前缀和文件名前缀
	 * **/
	private String prefix;
	/**
	 * 类名后缀和文件名后缀
	 * **/
	private String suffix;
	/***
	 * 插件生成器class
	 * */
	private Class<?> generatorClass;
	/***
	 * 插件地址
	 * */
	private String pluginPath;
	/**
	 * 依赖的插件,以英文逗号(,)分割
	 * **/
	private String dependencies;
	
	/**
	 * 生成代码的时候的beanId
	 * **/
	@Transient
	private String generateBeanId;
	
	/**
	 * 模板流
	 * **/
	@Transient
	private byte[] templateBytes;

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
	 * 插件介绍
	 * **/
	public String getDescription() {
		return description;
	}
	/**
	 * 插件介绍
	 * **/
	public void setDescription(String description) {
		this.description = description;
	}
	/***
	 * 插件模板文件路径
	 * */
	public String getTemplatePath() {
		return templatePath;
	}
	/***
	 * 插件模板文件路径
	 * */
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
	/***
	 * 插件生成器全路径名
	 * */
	public String getGenerator() {
		return generator;
	}
	/***
	 * 插件生成器全路径名
	 * */
	public void setGenerator(String generator) {
		this.generator = generator;
	}
	/***
	 * 插件生成器class
	 * */
	public Class<?> getGeneratorClass() {
		return generatorClass;
	}
	/***
	 * 插件生成器class
	 * */
	public void setGeneratorClass(Class<?> generatorClass) {
		this.generatorClass = generatorClass;
	}
	/***
	 * 插件地址
	 * */
	public String getPluginPath() {
		return pluginPath;
	}
	/***
	 * 插件地址
	 * */
	public void setPluginPath(String pluginPath) {
		this.pluginPath = pluginPath;
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
	 * 类名前缀和文件名前缀
	 * **/
	public String getPrefix() {
		return prefix == null ? "" : prefix.trim();
	}
	/**
	 * 类名前缀和文件名前缀
	 * **/
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * 类名后缀和文件名后缀
	 * **/
	public String getSuffix() {
		return suffix == null ? "" : suffix.trim();
	}
	/**
	 * 类名后缀和文件名后缀
	 * **/
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	/**
	 * 依赖的插件,以英文逗号(,)分割
	 * **/
	public String getDependencies() {
		return dependencies;
	}
	/**
	 * 依赖的插件,以英文逗号(,)分割
	 * **/
	public void setDependencies(String dependencies) {
		this.dependencies = dependencies;
	}
	/**
	 * 生成代码的时候的beanId
	 * **/
	public String getGenerateBeanId() {
		return generateBeanId;
	}
	/**
	 * 生成代码的时候的beanId
	 * **/
	public void setGenerateBeanId(String generateBeanId) {
		this.generateBeanId = generateBeanId;
	}
	/**
	 * 模板流
	 * **/
	public byte[] getTemplateBytes() {
		return templateBytes;
	}
	/**
	 * 模板流
	 * **/
	public void setTemplateBytes(byte[] templateBytes) {
		this.templateBytes = templateBytes;
	}

	@Override
	public String toString() {
		return "PluginConfig{" +
				"groupName='" + groupName + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", templatePath='" + templatePath + '\'' +
				", generator='" + generator + '\'' +
				", fileRelativeDir='" + fileRelativeDir + '\'' +
				", fileSuffix='" + fileSuffix + '\'' +
				", prefix='" + prefix + '\'' +
				", suffix='" + suffix + '\'' +
				", generatorClass=" + generatorClass +
				", pluginPath='" + pluginPath + '\'' +
				", dependencies='" + dependencies + '\'' +
				", generateBeanId='" + generateBeanId + '\'' +
				", templateBytes=" + Arrays.toString(templateBytes) +
				'}';
	}
}
