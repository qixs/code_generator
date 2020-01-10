package com.qxs.generator.web.config.security.filter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.qxs.generator.web.service.config.ISystemParameterService;

/**
 * 生成代码限流filter
 * 
 * @author qixingshen
 * @date 2019-03-22
 * **/
public final class GenerateCodeSemaphoreFilter extends OrderedRequestContextFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCodeSemaphoreFilter.class);

	private static Semaphore SEMAPHORE = null;

	private static ExecutorService executorService = new ThreadPoolExecutor(1, 1,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	/**
	 * 静态资源文件配置信息
	 * **/
	@Value("${spring.security.permitUrls:}")
	private String[] permitUrls;

	private AntPathRequestMatcher[] permitUrlMatchers;

	@Autowired
	private ISystemParameterService systemParameterService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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

		try {
			String requestURI = request.getRequestURI();
			if("/generator/generate".equals(requestURI)){
				int maxTaskCount = systemParameterService.findSystemParameter().getMaxTaskCount();
				if(SEMAPHORE == null){
					initSemaphore(maxTaskCount);
				}

				if(SEMAPHORE.getQueueLength() > maxTaskCount * 10){
					//跳转到错误页面
					//服务器忙
					response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器忙，请稍后重试");
					return;
				}

				try{
					SEMAPHORE.acquire();
					super.doFilterInternal(request, response, filterChain);
				}catch (Exception e){
					LOGGER.error("生成代码失败", e);
					throw e;
				}finally {
					SEMAPHORE.release();
				}
			}else{
				super.doFilterInternal(request, response, filterChain);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static synchronized void initSemaphore(int maxTaskCount){
		if(SEMAPHORE == null){
			SEMAPHORE = new Semaphore(maxTaskCount);
		}
	}

	public static synchronized void updateSemaphore(int oldMaxTaskCount, int newMaxTaskCount){
		executorService.submit(()->{
			//自旋，在无请求调用生成代码时再重置SEMAPHORE
			while(!SEMAPHORE.hasQueuedThreads() && SEMAPHORE.availablePermits() == oldMaxTaskCount){
				synchronized (Semaphore.class){
					initSemaphore(newMaxTaskCount);
				}
			}
		});
	}
}
