package com.qxs.generator.web.config.security.resources;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 资源权限过滤器(例如直接通过url访问指定路径)
 * @author qxs
 * @date 2017-09-01
 */
public class ResourceSecurityInterceptor extends AbstractSecurityInterceptor
		implements Filter {

	private AntPathRequestMatcher[] permitUrls;
	
	private AntPathRequestMatcher loginAntPathRequestMatcher = new AntPathRequestMatcher("/login");
	
	private SecurityMetadataSource securityMetadataSource;
	
	public ResourceSecurityInterceptor(SecurityMetadataSource securityMetadataSource,
			AccessDecisionManager accessDecisionManager,AuthenticationManager authenticationManager,String[] permitUrls) {
		this.securityMetadataSource = securityMetadataSource;
		setAccessDecisionManager(accessDecisionManager);
		setAuthenticationManager(authenticationManager);
		if(permitUrls != null){
			this.permitUrls = new AntPathRequestMatcher[permitUrls.length];
			for(int i = 0 , length = permitUrls.length ; i < length ; i ++){
				this.permitUrls[i] = new AntPathRequestMatcher(permitUrls[i]);
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		//静态资源文件
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		boolean flag = false;
		if(permitUrls != null){
			for (AntPathRequestMatcher matcher : permitUrls) {
				if (matcher.matches(httpServletRequest)) {
					flag = true;
					break;
				}
			}
		}
		if(loginAntPathRequestMatcher.matches(httpServletRequest)){
			flag = true;
		}
		
		FilterInvocation fi = new FilterInvocation(request, response, chain);
		
		InterceptorStatusToken token = flag ? null : super.beforeInvocation(fi);

		try {
			fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			super.afterInvocation(token, null);
		}
	}

	@Override
	public Class<? extends Object> getSecureObjectClass() {
		return FilterInvocation.class;
	}

	@Override
	public SecurityMetadataSource obtainSecurityMetadataSource() {
		return this.securityMetadataSource;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
