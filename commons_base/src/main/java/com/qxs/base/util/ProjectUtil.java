package com.qxs.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.CodeSource;
import java.security.ProtectionDomain;

public class ProjectUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectUtil.class);
	
	private static final String PROJECT_PATH_PREFIX = "file:/";
	
	private static final String PROJECT_PATH_SUFFIX = ".jar!/BOOT-INF/classes!/";
	/**
	 * 获取当前项目地址
	 * @param clazz class
	 * @return String 当前项目地址
	 * **/
	public static String getProjectPath(Class<?> clazz) {
		String projectPath = null;
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();
		LOGGER.debug("protectionDomain:[{}]", protectionDomain);

		CodeSource codeSource = protectionDomain.getCodeSource();
		LOGGER.debug("codeSource:[{}]", codeSource);

		java.net.URL url = codeSource.getLocation();

		LOGGER.debug("当前系统url path:[{}]", url.getPath());

		try {
			projectPath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		projectPath = projectPath.replaceAll("\\\\", "/");
		if(!projectPath.endsWith("/")){
			projectPath = projectPath + "/";
		}

		if(projectPath.startsWith(PROJECT_PATH_PREFIX)) {
			projectPath = projectPath.substring(PROJECT_PATH_PREFIX.length() - 1);
		}

		if(OS.getOs() == OS.WINDOWS && projectPath.startsWith("/")) {
			projectPath = projectPath.substring(1);
		}

		//如果是springboot jar项目
		if(projectPath.endsWith(PROJECT_PATH_SUFFIX)) {
			LOGGER.debug("当前项目是Springboot项目:[{}]", projectPath);
			projectPath = projectPath.substring(0, projectPath.length() - PROJECT_PATH_SUFFIX.length() + 4);
			LOGGER.debug("截取完成之后的地址:[{}]", projectPath);
		}else{
			LOGGER.debug("当前项目不是Springboot项目:[{}]", projectPath);
		}

		LOGGER.debug("projectPath:[{}]", projectPath);

		return projectPath;
	}

	public static boolean isSpringBootProject(Class<?> clazz){
		String projectPath = null;
		java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();

		LOGGER.debug("当前系统url path:[{}]", url.getPath());

		try {
			projectPath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		projectPath = projectPath.replaceAll("\\\\", "/");
		if(!projectPath.endsWith("/")){
			projectPath = projectPath + "/";
		}

		if(projectPath.startsWith(PROJECT_PATH_PREFIX)) {
			projectPath = projectPath.substring(PROJECT_PATH_PREFIX.length() - 1);
		}

		if(OS.getOs() == OS.WINDOWS && projectPath.startsWith("/")) {
			projectPath = projectPath.substring(1);
		}

		//如果是springboot jar项目
		if(projectPath.endsWith(PROJECT_PATH_SUFFIX)) {
			LOGGER.debug("当前项目是Springboot项目:[{}]", projectPath);
			projectPath = projectPath.substring(0, projectPath.length() - PROJECT_PATH_SUFFIX.length() + 4);
			LOGGER.debug("截取完成之后的地址:[{}]", projectPath);

			return true;
		}
		LOGGER.debug("当前项目不是Springboot项目:[{}]", projectPath);
		return false;
	}
}
