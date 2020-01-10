package com.qxs.base.util;

import org.springframework.context.ApplicationContext;

/**
 * 当前环境的ApplicationContext工具类
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-22
 * @version Revision: 1.0
 * **/
public final class ApplicationContextUtil {
	
	private static ApplicationContext context;
	
	public static void setApplicationContext(ApplicationContext applicationContext) {
		context = applicationContext;
	}
	/**
	 * 获取ApplicationContext
	 * 
	 * @return ApplicationContext
	 * **/
	public static ApplicationContext getApplicationContext(){
		return context;
	}
}
