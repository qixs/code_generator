package com.qxs.generator.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.qxs.generator.web.exception.BusinessException;

/**
 * java源码工具类
 * **/
public final class JavaSourceCodeUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaSourceCodeUtil.class);

	/**
	 * 抽取java全路径名
	 * **/
	public static String getClassFullName(String javaSourceCode) {
		String packageName = getClassPackageName(javaSourceCode);
		if(StringUtils.hasLength(packageName)) {
			return String.format("%s.%s", packageName, getClassSimpleName(javaSourceCode));
		}else {
			return getClassSimpleName(javaSourceCode);
		}
		
	}
	/**
	 * 抽取java类名
	 * **/
	public static String getClassSimpleName(String javaSourceCode) {
		Assert.hasText(javaSourceCode, "源码不能为空");
		String source = compress(javaSourceCode);
		if(source.indexOf("class ") >= 0) {
			LOGGER.debug("检测到[class ]字样");
			//抽取class 到其下标之后的第一个大左括号({)
			String className = source.substring(source.indexOf("class "), source.indexOf("{", source.indexOf("package ")) + 1).trim();
			LOGGER.debug("抽取出的类名:[{}]", className);
			
			className = className.substring("class ".length(), className.indexOf("{")).trim();
			
			//如果含有空格则证明是继承其他类或实现了接口
			if(className.indexOf(" ") > 0) {
				className = className.substring(0, className.indexOf(" ")).trim();
			}
			LOGGER.debug("真实类名:[{}]", className);
			
			return className;
		}else {
			LOGGER.debug("未检测到[class ]字样，无法抽取类名");
			throw new BusinessException("未抽取到类名");
		}
	}
	/**
	 * 抽取java包名
	 * **/
	public static String getClassPackageName(String javaSourceCode) {
		Assert.hasText(javaSourceCode, "源码不能为空");
		String source = compress(javaSourceCode);
		if(source.indexOf("package ") >= 0) {
			LOGGER.debug("检测到[package ]字样");
			//抽取package 到其下标之后的第一个英文分号(;)
			String packageName = source.substring(source.indexOf("package "), source.indexOf(";", source.indexOf("package ")) + 1).trim();
			LOGGER.debug("抽取出的包名:[{}]", packageName);
			
			packageName = packageName.substring("package ".length(), packageName.indexOf(";")).trim();
			
			LOGGER.debug("真实包名:[{}]", packageName);
			
			return packageName;
		}else {
			LOGGER.debug("未检测到[package ]字样，无法抽取包名");
			return null;
		}
	}
	
	private static String compress(String javaSourceCode) {
		String source = javaSourceCode.replaceAll("\\s", " ").replaceAll("\\s+", " ");
		LOGGER.debug("源码:[{}]", javaSourceCode);
		LOGGER.debug("压缩之后的代码:[{}]", source);
		
		return source;
	}
}
