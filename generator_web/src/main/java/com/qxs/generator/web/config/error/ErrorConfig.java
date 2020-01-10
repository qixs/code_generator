package com.qxs.generator.web.config.error;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 错误请求 配置项
 * @date 2018-12-07
 * **/
@Configuration
public class ErrorConfig {
    /**
     * 404错误页面配置
     * **/
    @Bean
    public ErrorViewResolver error404ViewResolver(){
        ErrorViewResolver errorViewResolver = new ErrorViewResolver() {
            @Override
            public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
                if(status.equals(HttpStatus.NOT_FOUND)){
                    return new ModelAndView("error/404", model);
                };
                return null;
            }
        };
        return errorViewResolver;
    }

    /**
     * 403错误页面配置
     * **/
    @Bean
    public ErrorViewResolver error403ViewResolver(){
        ErrorViewResolver errorViewResolver = new ErrorViewResolver() {
            @Override
            public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
                if(status.equals(HttpStatus.FORBIDDEN)){
                    return new ModelAndView("error/403", model);
                };
                return null;
            }
        };
        return errorViewResolver;
    }

    /**
     * 500错误页面配置
     * **/
    @Bean
    public ErrorViewResolver error500ViewResolver(){
        ErrorViewResolver errorViewResolver = new ErrorViewResolver() {
            @Override
            public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
                if(status.equals(HttpStatus.INTERNAL_SERVER_ERROR)){
                    return new ModelAndView("error/500", model);
                };
                return null;
            }
        };
        return errorViewResolver;
    }
}
