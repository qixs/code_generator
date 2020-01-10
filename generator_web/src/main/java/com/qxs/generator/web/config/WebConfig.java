package com.qxs.generator.web.config;

import java.nio.charset.Charset;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.common.collect.Lists;
import com.qxs.generator.web.config.tag.dialect.SansitiveEncryptDialect;

/***
 * 基础的web配置信息
 * @author qxs
 * @date 2017-06-12
 * **/
@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	
	/**
	 * exception文件
	 * **/
	protected static final String EXCEPTION_CONFIG_LOCATIONS = "classpath*:exception/**/*.xml";
	/**
	 * exception文件
	 * **/
	protected static final String BUTTONS_CONFIG_LOCATIONS = "classpath*:buttons/**/*.xml";
	
	/**
	 * 默认的编码
	 * **/
	protected static final String DEFAULT_CHARSET_NAME = "UTF-8";
	
	/**
	 * 默认的编码
	 * **/
	protected static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);
	/**
	 * i18n默认的basename
	 * **/
	protected static final String DEFAULT_BASENAME = "classpath:i18n/language";
	
	/**
	 * 编码过滤器
	 * **/
	@Bean
    public FilterRegistrationBean<CharacterEncodingFilter>  registerCharacterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();  
        characterEncodingFilter.setEncoding(DEFAULT_CHARSET_NAME);
        
        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<CharacterEncodingFilter>();
        registrationBean.setFilter(characterEncodingFilter);
        registrationBean.setUrlPatterns(Lists.newArrayList("/*"));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    } 
	
	@Bean
	public SansitiveEncryptDialect sansitiveEncryptDialect(){
		return new SansitiveEncryptDialect();
	}
	
//	/**
//	 * thymeleaf  jar包提取出来,不能配置该项,否则会导致th:replace报错
//	 * **/
//	@Bean
//	public ClassLoaderTemplateResolver classLoaderTemplateResolver() {
//		ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
//	    return classLoaderTemplateResolver;
//	}
//	/**
//	 * 
//	 * **/
//	@Bean
//	public EmbeddedServletContainerCustomizer containerCustomizer() {
//		return new EmbeddedServletContainerCustomizer() {
//			@Override
//			public void customize(ConfigurableEmbeddedServletContainer container) {
//				//session
////				container.setSessionTimeout(60);
//				
//				//错误页面
//				ErrorPage error401Page = new ErrorPage(HttpStatus.FORBIDDEN, "/error/403");
//	            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404");
//	            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");
//	            container.addErrorPages(error401Page, error404Page, error500Page);
//			}
//		};
//	}
	
//	
//	/**
//	 * Converter
//	 * 
//	 * ByteArrayHttpMessageConverter: 负责读取二进制格式的数据和写出二进制格式的数据；
//	 * StringHttpMessageConverter：负责读取字符串格式的数据和写出二进制格式的数据；
//	 * ResourceHttpMessageConverter：负责读取资源文件和写出资源文件数据； 
//	 * FormHttpMessageConverter：负责读取form提交的数据（能读取的数据格式为 application/x-www-form-urlencoded，不能读取multipart/form-data格式数据）；负责写入application/x-www-from-urlencoded和multipart/form-data格式的数据；
//	 * MappingJacksonHttpMessageConverter:  负责读取和写入json格式的数据；
//	 * SouceHttpMessageConverter：负责读取和写入 xml 中javax.xml.transform.Source定义的数据；
//	 * Jaxb2RootElementHttpMessageConverter：负责读取和写入xml 标签格式的数据；
//	 * AtomFeedHttpMessageConverter：负责读取和写入Atom格式的数据；
//	 * RssChannelHttpMessageConverter：负责读取和写入RSS格式的数据
//	 * 
//	 * **/
//	@Bean
//	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
//		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//		mappingJackson2HttpMessageConverter.setDefaultCharset(DEFAULT_CHARSET);
//		mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Lists
//				.newArrayList(MediaType.TEXT_HTML,
//						MediaType.APPLICATION_JSON_UTF8));
//		return mappingJackson2HttpMessageConverter;
//	}
//	@Bean
//	public StringHttpMessageConverter stringHttpMessageConverter(){
//		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(
//				DEFAULT_CHARSET);
//		stringHttpMessageConverter.setSupportedMediaTypes(Lists
//				.newArrayList(MediaType.TEXT_HTML));
//		return stringHttpMessageConverter;
//	}
//	
}
