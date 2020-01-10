package com.qxs.generator.web.util;

import org.springframework.web.servlet.support.RequestContext;

/**
 * 国际化资源信息获取工具类(处理了乱码问题)
 * @author qxs
 * @date 2017-07-11
 * **/
public class I18nMessageUtil {
	
	/**
	 * 不支持的异常信息编号字符
	 * **/
	private static final String NOT_SUPPORT_CHARS = "[^0-9a-zA-Z_.]+";
	
	/**
	 * 获取国际化资源信息
	 * @param code 国际化资源信息代码
	 * @return String
	 * **/
	public static String getMessage(String code){
		//处理不支持的字符
		code = code.replaceAll(NOT_SUPPORT_CHARS, "");
		
		return new RequestContext(RequestUtil.getRequest()).getMessage(code);
	}
}
