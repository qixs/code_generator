package com.qxs.base.util;


import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 当前环境的Environment工具类
 * 
 * @author qxs
 * @date 2017-06-12
 * **/
public final class EnvironmentUtil {

	/**
	 * 获取Environment
	 * 
	 * @return Environment
	 * **/
	public static Environment getEnvironment() {
		ApplicationContext applicationContext = ApplicationContextUtil
				.getApplicationContext();

		Environment environment = applicationContext.getBean(Environment.class);

		return environment;
	}

	/**
	 * 判断参数是否存在
	 * 
	 * @param key
	 *            参数的键
	 * 
	 * @return boolean
	 * **/
	public static boolean containsProperty(String key) {
		return containsProperty(key, getEnvironment());
	}

	/**
	 * 判断参数是否存在
	 * 
	 * @param key
	 *            参数的键
	 * @param environment
	 *            环境的Environment
	 * 
	 * @return boolean
	 * **/
	public static boolean containsProperty(String key, Environment environment) {
		return environment.containsProperty(key);
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * 
	 * @return String
	 * **/
	public static String getProperty(String key) {
		return getProperty(key, getEnvironment());
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param environment
	 *            环境的Environment
	 * 
	 * @return String
	 * **/
	public static String getProperty(String key, Environment environment) {
		return environment.getProperty(key);
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param defaultValue
	 *            默认值
	 * 
	 * @return String
	 * **/
	public static String getProperty(String key, String defaultValue) {
		return getProperty(key, defaultValue, getEnvironment());
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param defaultValue
	 *            默认值
	 * @param environment
	 *            环境的Environment
	 * 
	 * @return String
	 * **/
	public static String getProperty(String key, String defaultValue,
			Environment environment) {
		return environment.getProperty(key, defaultValue);
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param targetType
	 *            参数值的类型
	 * 
	 * @return T
	 * **/
	public static <T> T getProperty(String key, Class<T> targetType) {
		return getProperty(key, targetType, getEnvironment());
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param targetType
	 *            参数值的类型
	 * @param environment
	 *            环境的Environment
	 * 
	 * @return T
	 * **/
	public static <T> T getProperty(String key, Class<T> targetType,
			Environment environment) {
		return environment.getProperty(key, targetType);
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param targetType
	 *            参数值的类型
	 * @param defaultValue
	 *            默认值
	 * 
	 * @return T
	 * **/
	public static <T> T getProperty(String key, Class<T> targetType,
			T defaultValue) {
		return getProperty(key, targetType, defaultValue, getEnvironment());
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param targetType
	 *            参数值的类型
	 * @param defaultValue
	 *            默认值
	 * @param environment
	 *            环境的Environment
	 * 
	 * @return T
	 * **/
	public static <T> T getProperty(String key, Class<T> targetType,
			T defaultValue, Environment environment) {
		return environment.getProperty(key, targetType, defaultValue);
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * 
	 * @return String
	 * **/
	public static String getRequiredProperty(String key)
			throws IllegalStateException {
		return getRequiredProperty(key, getEnvironment());
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param environment
	 *            环境的Environment
	 * 
	 * @return String
	 * **/
	public static String getRequiredProperty(String key, Environment environment)
			throws IllegalStateException {
		return environment.getRequiredProperty(key);
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param targetType
	 *            参数值的class
	 * 
	 * @return T
	 * **/
	public static <T> T getRequiredProperty(String key, Class<T> targetType)
			throws IllegalStateException {
		return getRequiredProperty(key, targetType, getEnvironment());
	}

	/**
	 * 获取参数的值
	 * 
	 * @param key
	 *            参数的键
	 * @param targetType
	 *            参数值的class
	 * @param environment
	 *            环境的Environment
	 * 
	 * @return T
	 * **/
	public static <T> T getRequiredProperty(String key, Class<T> targetType,
			Environment environment) throws IllegalStateException {
		return environment.getRequiredProperty(key, targetType);
	}

	/**
	 * 解析参数
	 * 
	 * @param text
	 *            要解析的字符串
	 * 
	 * @return String
	 * **/
	public static String resolvePlaceholders(String text) {
		return resolvePlaceholders(text, getEnvironment());
	}

	/**
	 * 解析参数
	 * 
	 * @param text
	 *            要解析的字符串
	 * @param environment
	 *            环境的Environment
	 * 
	 * @return String
	 * **/
	public static String resolvePlaceholders(String text,
			Environment environment) {
		return environment.resolvePlaceholders(text);
	}

	/**
	 * 解析参数
	 * 
	 * @param text
	 *            要解析的字符串
	 * 
	 * @return String
	 * **/
	public static String resolveRequiredPlaceholders(String text)
			throws IllegalArgumentException {
		return resolveRequiredPlaceholders(text, getEnvironment());
	}

	/**
	 * 解析参数
	 * 
	 * @param text
	 *            要解析的字符串
	 * @param environment
	 *            环境的Environment
	 * 
	 * @return String
	 * **/
	public static String resolveRequiredPlaceholders(String text,
			Environment environment) throws IllegalArgumentException {
		return environment.resolveRequiredPlaceholders(text);
	}
}
