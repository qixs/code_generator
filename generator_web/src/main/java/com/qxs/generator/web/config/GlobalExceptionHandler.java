package com.qxs.generator.web.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qxs.base.exception.SQLFormatException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.qxs.generator.web.config.security.resources.ResourceAccessDeniedException;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.plugin.factory.exception.CodeGenerateException;
import com.qxs.plugin.factory.exception.PluginConfigParseException;
import com.qxs.plugin.factory.exception.PluginLoadException;
import com.qxs.plugin.factory.exception.PluginNotFoundException;
import com.qxs.plugin.factory.exception.PluginUnloadException;

/**
 * 全局的异常处理器
 * @author qixingshen
 * @date 2018-08-23
 * **/
@ControllerAdvice
public class GlobalExceptionHandler {
	
	protected transient Log log = LogFactory.getLog(getClass());
	
	/**
	 * 异常处理
	 * **/
	@ExceptionHandler
	@ResponseBody
	public Map<String,Object> exception(HttpServletRequest request, HttpServletResponse response, Exception e) {
		response.setCharacterEncoding("UTF-8");
		
		String msg = null;
		if (e instanceof BusinessException
				|| e instanceof CodeGenerateException
				|| e instanceof PluginConfigParseException
				|| e instanceof PluginLoadException
				|| e instanceof PluginNotFoundException
				|| e instanceof PluginUnloadException
				|| e instanceof ResourceAccessDeniedException
				|| e instanceof IllegalArgumentException
				|| e instanceof SQLFormatException) {
			msg = e.getMessage();
			// 输出异常
			log.error(msg, e);
			
		}else if (e.getCause() != null && e.getCause() instanceof BusinessException) {
			msg = e.getCause().getMessage();
			// 输出异常
			log.error(msg, e);
			
		}else if (e instanceof MissingServletRequestPartException) {
			//上传文件
			msg = e.getMessage();
			// 输出异常
			log.error(msg, e);
			
		}else if (e instanceof BindException) {
			List<ObjectError> errors = ((BindException) e).getBindingResult().getAllErrors();
			StringBuilder sb = new StringBuilder();
			
			for(int i = 0 , length = errors.size() ; i < length ; i ++){
				if(log.isDebugEnabled()){
					log.debug("异常信息: " + errors.get(i).getDefaultMessage().trim());
				}
				
				String message = errors.get(i).getDefaultMessage().trim();
				
				if(errors.size() == 1){
					sb.append(message);
					break;
				}else{
					if(i > 0){
						sb.append("<br/>");
					}
					sb.append(String.format("%d. %s", i + 1,message));
				}
			}
			msg = sb.toString();
			// 输出异常
			log.debug(msg,e);
		} else if(e instanceof MethodArgumentTypeMismatchException){
			msg = "参数错误";
			// 输出异常
			log.error(msg, e);
		} else if(e instanceof HttpRequestMethodNotSupportedException){
			msg = e.getMessage();
			try {
				response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value(), msg);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// 输出异常
			log.error(msg, e);
		} else if (e instanceof Throwable || e instanceof Exception) {
			msg = "业务处理异常";
			// 输出异常
			log.error(msg, e);
		}
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("errorMessage", msg);
		return map;
	}
	
}
