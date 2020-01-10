package com.qxs.generator.web.aop.log;

import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qxs.generator.web.GeneratorApplication;
import com.qxs.generator.web.controller.InitWizardController;
import com.qxs.generator.web.controller.LicenseController;
import com.qxs.generator.web.controller.LoginController;
import com.qxs.generator.web.controller.captcha.geetest.CaptchaGeetestController;
import com.qxs.generator.web.controller.user.ForgetPasswordController;
import com.qxs.generator.web.controller.user.UserActiveController;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.log.Access;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.service.log.IAccessService;
import com.qxs.generator.web.service.version.IVersionService;
import com.qxs.generator.web.util.DateUtil;
import com.qxs.generator.web.util.RequestUtil;

/**
 * 登记访问日志切面
 * 
 * @author qixingshen
 */
@Aspect
@Component
public class LogAccessAspect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogAccessAspect.class);
	
	private static String ASPECT_PACKAGE_NAME = GeneratorApplication.class.getPackage().getName();
	
	@Autowired
	private IAccessService accessService;
	@Autowired
	private IVersionService versionService;
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Around("("
			+ "@annotation(org.springframework.web.bind.annotation.RequestMapping) || "
			+ "@annotation(org.springframework.web.bind.annotation.GetMapping) || "
			+ "@annotation(org.springframework.web.bind.annotation.PostMapping) || "
			+ "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
			+ "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || "
			+ "@annotation(org.springframework.web.bind.annotation.PatchMapping)"
			+ ")")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
		//如果是访问的spring boot自带的controller则直接跳过
		if(!joinPoint.getTarget().getClass().getName().startsWith(ASPECT_PACKAGE_NAME) ||
				joinPoint.getTarget().getClass().equals(LoginController.class) ||
				joinPoint.getTarget().getClass().equals(CaptchaGeetestController.class) ||
				joinPoint.getTarget().getClass().equals(ForgetPasswordController.class) ||
				joinPoint.getTarget().getClass().equals(InitWizardController.class) ||
				joinPoint.getTarget().getClass().equals(UserActiveController.class) ||
				joinPoint.getTarget().getClass().equals(LicenseController.class)) {
			try {
				return joinPoint.proceed();
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(),e);
				throw e;
			}
		}
		
		Access access = new Access();
		
		long time = System.currentTimeMillis();
        Object result = null;
        try {
        	result = joinPoint.proceed();
        } catch (Throwable e) {
        	LOGGER.error(e.getMessage(),e);
        	//登记异常信息
        	access.setException(formatException(e));
        	throw e;
        }finally {
            HttpServletRequest request = RequestUtil.getRequest();
            
            String[] resultTypes = new ServletWebRequest(request).getHeaderValues(HttpHeaders.ACCEPT);
            
            SecurityContext securityContext = SecurityContextHolder.getContext();
    		
    		Authentication authentication = securityContext.getAuthentication();
    		
    		if(authentication == null) {
    			throw new BusinessException("未获取到登录用户");
    		}
    		
    		User user = (User) authentication.getPrincipal();
    		
            access.setUserId(user.getId());
            access.setSystemVersion(versionService.findVersion().getVersion());
            access.setAccessDate(DateUtil.currentDate());
            //方法体执行时间
            access.setTime(String.valueOf(System.currentTimeMillis() - time) + "ms");
            access.setParameters(formatParameters(joinPoint.getArgs()));
            access.setUrl(applicationContext.getBean(UrlPathHelper.class).getLookupPathForRequest(request));
            access.setResultType(resultTypes != null && resultTypes.length > 0 ? resultTypes[0] : null);
            access.setResult(formatResult(result));
            
            accessService.insert(access);
            
        }
        
        return result;
    }
	/**
	 * 格式化异常信息
	 * **/
	private String formatException(Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.getClass().getName() + ": " + e.getMessage() + "\r\n");
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		for(StackTraceElement stackTraceElement : stackTraceElements) {
			sb.append("\t" + stackTraceElement.toString() + "\r\n");
		}
		
		return sb.toString();
	}
	/**
	 * 格式化参数
	 * @throws Exception 
	 * **/
	private String formatParameters(Object[] args) throws Exception {
		HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(RequestUtil.getRequest());
		HandlerMethod handlerMethod = (HandlerMethod) handlerExecutionChain.getHandler();
		Parameter[] parameters = handlerMethod.getMethod().getParameters();
		
		Map<String,Object> map = new LinkedHashMap<>();
		for(int i = 0 ; i < args.length ; i ++) {
			Object arg = args[i];
			if(arg != null && arg.getClass().getName().startsWith("org.springframework")) {
				map.put(parameters[i].getName(), arg.getClass().getSimpleName());
			}else {
				map.put(parameters[i].getName(), arg);
			}
		}
		
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
          @Override
          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
              return fieldAttributes.getDeclaredType().equals(Class.class);
          }
          @Override  
          public boolean shouldSkipClass(Class<?> clazz) {
        	  return clazz.equals(Class.class);
          }  
		}).create();  
		
		return gson.toJson(map);
	}
	
	/**
	 * 格式化结果
	 * **/
	private String formatResult(Object result) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
          @Override
          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
              return fieldAttributes.getDeclaredType().equals(Class.class);
          }
          @Override  
          public boolean shouldSkipClass(Class<?> clazz) {
        	  return clazz.equals(Class.class);
          }  
		}).create();  
		
		return gson.toJson(new Result(result));
	}
	
	private static class Result {
		private Object result;
		private Result(Object result) {
			this.result = result;
		}
		@SuppressWarnings("unused")
		public Object getResult() {
			return result;
		}
	}
}
