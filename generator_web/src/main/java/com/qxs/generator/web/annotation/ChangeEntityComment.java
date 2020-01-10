package com.qxs.generator.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 插件注释注解
 * @author qixingshen
 * @date 2018-06-01
 * **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeEntityComment {
	/**
	 * 注释
	 * **/
	String value();
}
