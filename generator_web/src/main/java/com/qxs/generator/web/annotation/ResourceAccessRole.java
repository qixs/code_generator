package com.qxs.generator.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 资源权限配置  尝试使用此方法http://www.cnblogs.com/ranger2016/p/3914146.html
 * https://stackoverflow.com/questions/5225832/spring-security-hasrolerole-admin-in-config-and-preauthorizepermitall-n
 * **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceAccessRole {
	String[] value();
}
