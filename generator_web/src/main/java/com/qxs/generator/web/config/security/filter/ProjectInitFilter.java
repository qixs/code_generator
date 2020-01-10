package com.qxs.generator.web.config.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import com.qxs.generator.web.model.init.wizard.Complete;
import com.qxs.generator.web.service.init.wizard.ICompleteService;
import com.qxs.generator.web.service.init.wizard.ICurrentStepService;
import com.qxs.generator.web.service.init.wizard.IStepService;

/**
 * 项目已经初始化filter
 * 
 * @author qixingshen
 * @date 2018-07-25
 * **/
@Component
public class ProjectInitFilter extends OrderedRequestContextFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectInitFilter.class);

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	/**
	 * 静态资源文件配置信息
	 * **/
	@Value("${spring.security.permitUrls:}")
	private String[] permitUrls;
	
	private AntPathRequestMatcher[] permitUrlMatchers;
	
	@Autowired
	private IStepService stepService;
	@Autowired
	private ICurrentStepService currentStepService;
	@Autowired
	private ICompleteService completeService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (permitUrls != null && permitUrlMatchers == null) {
			this.permitUrlMatchers = new AntPathRequestMatcher[permitUrls.length];
			for (int i = 0, length = permitUrls.length; i < length; i++) {
				this.permitUrlMatchers[i] = new AntPathRequestMatcher(
						permitUrls[i]);
			}
		}
		if (permitUrlMatchers != null) {
			for (AntPathRequestMatcher matcher : permitUrlMatchers) {
				if (matcher.matches(request)) {
					super.doFilterInternal(request, response, filterChain);
					return;
				}
			}
		}
		
		//只判断get请求
		if (!"GET".equalsIgnoreCase(request.getMethod())) {
			super.doFilterInternal(request, response, filterChain);
		}else {
			Complete complete = completeService.findComplete();
			//如果登记了complete表则且状态是STATUS_COMPLETE则认为初始化完毕
			if(complete != null && Complete.STATUS_COMPLETE == complete.getStatus()) {
				LOGGER.debug("当前系统已经完成初始化操作");
				
				super.doFilterInternal(request, response, filterChain);
			}else {
				int currentStepNum = currentStepService.currentStepNum();
				long maxStepNum = stepService.maxStepNum();
				LOGGER.debug("当前系统未完成初始化操作， 当前步骤号[{}]，系统初始化最大步骤号[{}]，判断当前请求url是否在导航配置表中存在", currentStepNum, maxStepNum);
				String requestURI = request.getRequestURI();
				if(stepService.findStepUrlList().contains(requestURI)) {
					LOGGER.debug("当前请求url在导航配置表中存在，当前请求url:[{}]", requestURI);
					super.doFilterInternal(request, response, filterChain);
				}else {
					LOGGER.debug("当前请求url在导航配置表中不存在，执行初始化操作，当前请求url:[{}]", requestURI);
					
					//校验步骤号是否正确
					stepService.validStep();
					
					//需要执行系统初始化操作
					
					//跳转到对应的页面
					redirectStrategy.sendRedirect(request, response, "/init/wizard/index");
					
					return;
				}
			}
		}
	}

}
