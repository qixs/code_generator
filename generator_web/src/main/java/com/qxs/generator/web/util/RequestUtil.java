package com.qxs.generator.web.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求工具类
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-22
 * @version Revision: 1.0
 * **/
public final class RequestUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtil.class);
	
	/**
	 * 获取HttpServletRequest
	 * @return HttpServletRequest 
	 * **/
	public static HttpServletRequest getRequest(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		return request;
	}
	
	/**
	 * 获取request的header信息
	 * 
	 * @return MultiValueMap<String, String>
	 * **/
	public static MultiValueMap<String, String> getRequestHeaders() {
		HttpServletRequest request = getRequest();
		Enumeration<String> enumerations = request.getHeaderNames();
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		while (enumerations.hasMoreElements()) {
			String headerName = enumerations.nextElement();
			Enumeration<String> headerValues = request.getHeaders(headerName);
			headers.put(headerName, Collections.list(headerValues));
		}
		return headers;
	}
	
	/**
	 * 获取token
	 * @param attributeName 属性名
	 * @return Object 属性值
	 * **/
	public static String getCsrfToken(){
		HttpServletRequest request = getRequest();
		String token = request.getHeader("_csrf");
		if(StringUtils.isEmpty(token)){
			token = request.getHeader("_csrf_header");
		}
		if(StringUtils.isEmpty(token)){
			token = request.getHeader("X-XSRF-TOKEN");
		}
		if(StringUtils.isEmpty(token)){
			token = request.getParameter("_csrf");
		}
		if(StringUtils.isEmpty(token)){
			token = request.getParameter("_csrf_header");
		}
		if(StringUtils.isEmpty(token)){
			token = request.getParameter("X-XSRF-TOKEN");
		}
		//TODO
		
//		if(StringUtils.isEmpty(token)){
//			Object csrfToken = SessionUtil.getAttribute(HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN"));
//			if(csrfToken == null){
//				CsrfTokenRepository csrfTokenRepository = ApplicationContextUtil.getApplicationContext().getBean(CsrfTokenRepository.class);
//				csrfToken = csrfTokenRepository.loadToken(request);
//			}
//			if(csrfToken == null){
//				return null;
//			}
//			
//			return ((CsrfToken)csrfToken).getToken();
//		}
//		
		return token;
	}
	public static String getIpAddr() {
		return getIpAddr(getRequest());
	}
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
			
			LOGGER.debug("Proxy-Client-IP is not null:[{}]",ipAddress);
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
			
			LOGGER.debug("WL-Proxy-Client-IP is not null:[{}]",ipAddress);
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			
			LOGGER.debug("RemoteAddr is not null:[{}]",ipAddress);
			//如果是本机地址
			if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		return ipAddress;
	}
}
