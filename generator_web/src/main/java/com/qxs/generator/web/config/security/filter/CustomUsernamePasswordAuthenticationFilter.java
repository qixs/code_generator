package com.qxs.generator.web.config.security.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.qxs.generator.web.constant.Constants;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.service.config.IGeetestService;

/**
 * 验证码校验过滤器	
 * 
 * @author qxs
 * @version 1.0
 */
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomUsernamePasswordAuthenticationFilter.class);
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Autowired
	private IGeetestService geetestService;
	
	/***
	 * 退出配置信息
	 * **/
	private LogoutConfigurer<HttpSecurity> logout;
	
	public CustomUsernamePasswordAuthenticationFilter(LogoutConfigurer<HttpSecurity> logout){
		super();
		this.logout = logout;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) {
		if (!"POST".equals(request.getMethod())) {
			throw new AuthenticationServiceException("Authentication method not supported: "+ request.getMethod());
		}
		//如果需要校验验证码
		HttpSession session = request.getSession();
		Object errorNum = session.getAttribute(Constants.CAPTCHA_SESSION_ERROR_NUM_KEY);
		if(errorNum == null) {
			errorNum = 0;
			session.setAttribute(Constants.CAPTCHA_SESSION_ERROR_NUM_KEY, errorNum);
		}
		if(((int)errorNum) >= Constants.CAPTCHA_ERROR_MAX_NUM) {
			try{
				geetestService.enhencedValidate();
			}catch(BusinessException e){
				request.getSession().setAttribute("loginErrorMessage", e.getMessage());
				
				try {
					Field field = LogoutConfigurer.class.getField("logoutUrl");
					if(!field.isAccessible()) {
						field.setAccessible(true);
					}
					//退出url
					String logoutUrl = field.get(logout).toString();
					
				    //退出认证中心
					redirectStrategy.sendRedirect(request, response, logoutUrl);
					
				} catch (UnsupportedEncodingException e1) {
					LOGGER.error(e1.getMessage(),e);
				} catch (IOException e1) {
					LOGGER.error(e1.getMessage(),e);
				} catch (IllegalArgumentException e1) {
					LOGGER.error(e1.getMessage(),e);
				} catch (IllegalAccessException e1) {
					LOGGER.error(e1.getMessage(),e);
				} catch (NoSuchFieldException e1) {
					LOGGER.error(e1.getMessage(),e);
				} catch (SecurityException e1) {
					LOGGER.error(e1.getMessage(),e);
				}
				
				return null;
			}
		}
		
		return super.attemptAuthentication(request, response);
	}

}
