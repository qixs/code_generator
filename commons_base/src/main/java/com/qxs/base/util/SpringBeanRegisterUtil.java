package com.qxs.base.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * bean注册工具类
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-22
 * @version Revision: 1.0
 */
public class SpringBeanRegisterUtil {
	
	/**
     * 注册bean
     * @param clazz 所注册bean的class
     * @return void
     */
	public static void registerBean(String id,Class<?> clazz,ApplicationContext applicationContext) {
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        
        BeanDefinition beanDefinition=beanDefinitionBuilder.getBeanDefinition();
        ((DefaultListableBeanFactory)context.getBeanFactory()).registerBeanDefinition(id, beanDefinition);
    }
	
    /**
     * 移除bean
     * @param clazz 所移除的bean的class
     * @return void
     */
    public static void unregisterBean(String beanId, ApplicationContext applicationContext){
    	ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
    	((DefaultListableBeanFactory)context.getBeanFactory()).removeBeanDefinition(beanId);
    }
//    /**
//     * 注册bean
//     * @param clazz 所注册bean的class
//     * @return void
//     */
//	public static void registerBean(Class<?> clazz) {
//        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
//        
//        BeanDefinition beanDefinition=beanDefinitionBuilder.getBeanDefinition();
//        // register the bean
//        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext)ApplicationContextUtil.getApplicationContext();
//        ((DefaultListableBeanFactory)applicationContext.getBeanFactory()).registerBeanDefinition(clazz.getName(),beanDefinition);
//    }
//	
//    /**
//     * 移除bean
//     * @param clazz 所移除的bean的class
//     * @return void
//     */
//    public static void unregisterBean(Class<?> clazz){
//    	ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext)ApplicationContextUtil.getApplicationContext();
//    	((DefaultListableBeanFactory)applicationContext.getBeanFactory()).removeBeanDefinition(clazz.getName());
//    }
}
