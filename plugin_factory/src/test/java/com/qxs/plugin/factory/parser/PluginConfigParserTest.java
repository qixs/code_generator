package com.qxs.plugin.factory.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.junit.Assert;
import org.junit.Test;

import com.qxs.base.util.ProjectUtil;
import com.qxs.plugin.factory.exception.PluginConfigParseException;
import com.qxs.plugin.factory.model.PluginConfig;

/**
 * @author qixingshen
 * **/
public class PluginConfigParserTest {
	
	private static final String CONFIG_FILE_NAME = "plugin.yml";
	private static final String EXCEPTION_CONFIG_FILE_NAME = "exception-plugin.yml";
	
	private static final String JAR_CONFIG_FILE_NAME = "generator_plugin_entity-1.0.jar";

	
	
	/**
	 * 解析普通的配置文件
	 * @throws IOException 
	 * **/
	@Test
	public void parseException() throws IOException {
		String projectPath = ProjectUtil.getProjectPath(getClass());
		
		InputStream inputStream = new FileInputStream(projectPath+EXCEPTION_CONFIG_FILE_NAME);
		boolean f = false;
		try {
			new PluginConfigParser(inputStream).parse();
		}catch(PluginConfigParseException e){
			f = true;
		}
		
		Assert.assertTrue(f);
		
		inputStream.close();
	}
	
	/**
	 * 解析普通的配置文件
	 * @throws IOException 
	 * **/
	@Test
	public void parseConfig() throws IOException {
		String projectPath = ProjectUtil.getProjectPath(getClass());
		
		InputStream inputStream = new FileInputStream(projectPath+CONFIG_FILE_NAME);
		PluginConfig pluginConfig = new PluginConfigParser(inputStream).parse();
		
		Assert.assertNotNull(pluginConfig.getName());
		Assert.assertNotNull(pluginConfig.getDescription());
		Assert.assertNotNull(pluginConfig.getGenerator());
		
		inputStream.close();
	}
	/**
	 * 解析普通的配置文件
	 * @throws IOException 
	 * **/
	@Test
	public void parseConfigPath1() throws IOException {
		String projectPath = ProjectUtil.getProjectPath(getClass());
		
		PluginConfig pluginConfig = new PluginConfigParser(projectPath+CONFIG_FILE_NAME).parse();
		
		Assert.assertNotNull(pluginConfig.getName());
		Assert.assertNotNull(pluginConfig.getDescription());
		Assert.assertNotNull(pluginConfig.getGenerator());
		
	}
	/**
	 * 解析普通的配置文件
	 * @throws IOException 
	 * **/
	@Test
	public void parseConfigPath2() throws IOException {
		String projectPath = ProjectUtil.getProjectPath(getClass());
		
		PluginConfig pluginConfig = new PluginConfigParser(projectPath,CONFIG_FILE_NAME).parse();
		
		Assert.assertNotNull(pluginConfig.getName());
		Assert.assertNotNull(pluginConfig.getDescription());
		Assert.assertNotNull(pluginConfig.getGenerator());
		
	}
	/**
	 * 解析jar文件内的配置文件
	 * @throws IOException 
	 * **/
	@Test
	@SuppressWarnings("resource")
	public void parseJarFileConfigPath() throws IOException {
		String projectPath = ProjectUtil.getProjectPath(getClass());
		
		JarFile jarFile = new JarFile(projectPath+JAR_CONFIG_FILE_NAME);
		ZipEntry zipEntry = jarFile.getEntry(CONFIG_FILE_NAME);
		InputStream inputStream = jarFile.getInputStream(zipEntry);
		
		PluginConfig pluginConfig = new PluginConfigParser(inputStream).parse();
		
		Assert.assertNotNull(pluginConfig.getName());
		Assert.assertNotNull(pluginConfig.getDescription());
		Assert.assertNotNull(pluginConfig.getGenerator());
		
		inputStream.close();
	}
	/**
	 * 解析jar文件内的配置文件
	 * @throws IOException 
	 * **/
	@Test
	public void parseJarFileConfig() throws IOException {
		String projectPath = ProjectUtil.getProjectPath(getClass());
		
		PluginConfig pluginConfig = new PluginConfigParser((projectPath+JAR_CONFIG_FILE_NAME),CONFIG_FILE_NAME,true).parse();
		
		Assert.assertNotNull(pluginConfig.getName());
		Assert.assertNotNull(pluginConfig.getDescription());
		Assert.assertNotNull(pluginConfig.getGenerator());
		
	}
}
