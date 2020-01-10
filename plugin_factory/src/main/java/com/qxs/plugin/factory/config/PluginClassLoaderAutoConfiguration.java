package com.qxs.plugin.factory.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;

import com.qxs.plugin.factory.PluginClassLoader;

/**
 * @author qixingshen
 * **/
@Configuration
@Order(10000)
public class PluginClassLoaderAutoConfiguration {

	@Bean
	@ConditionalOnMissingClass("com.qxs.devtools.config.SpringBootDevToolsClassLoaderAutoConfiguration")
	@ConditionalOnMissingBean(ClassLoader.class)
	@Lazy
	@Scope(scopeName=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ClassLoader classLoader(ClassLoader classLoader,String path) throws Exception {
		return new PluginClassLoader(path);
	}
}
