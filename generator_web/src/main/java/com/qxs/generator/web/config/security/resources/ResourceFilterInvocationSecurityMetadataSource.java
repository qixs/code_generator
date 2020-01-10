package com.qxs.generator.web.config.security.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.google.common.collect.Lists;
import com.qxs.generator.web.annotation.ResourceAccessRole;
import com.qxs.generator.web.exception.BusinessException;

/**
 * 获取资源对应角色的方法
 */
@Service
public class ResourceFilterInvocationSecurityMetadataSource implements
		FilterInvocationSecurityMetadataSource {
	
	@Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}
	
	// 根据URL，找到相关的权限配置。
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object)
			throws IllegalArgumentException {
		FilterInvocation filterInvocation = (FilterInvocation) object;
		HttpServletRequest request = filterInvocation.getRequest();
		
		//websocket请求
		if("Upgrade".equalsIgnoreCase(request.getHeader("Connection")) && "websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
			return Lists.newArrayList(new SecurityConfig("ROLE_USER"));
		}
		
		try {
			HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
			if (handlerExecutionChain == null){
				throw new BusinessException(request.getRequestURI() + "请求地址不存在");
			}
			HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
			ResourceAccessRole resourceAccessRole = handlerMethod.getMethod().getAnnotation(ResourceAccessRole.class);
			if(resourceAccessRole != null) {
				String[] roles = resourceAccessRole.value();
				List<ConfigAttribute> configAttributes = new ArrayList<>(roles.length);
				for(String role : roles) {
					configAttributes.add(new SecurityConfig(role));
				}
				return configAttributes;
			}else {
				return Lists.newArrayList(new SecurityConfig("ROLE_USER"));
			}
			
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}
