package com.qxs.generator.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.qxs.base.util.ApplicationContextUtil;
import com.qxs.base.util.ProjectUtil;
import com.qxs.plugin.factory.PluginParameterKeys;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动服务
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-29
 * @version Revision: 1.0
 */
@SpringBootApplication(exclude= {FreeMarkerAutoConfiguration.class})
@EnableCaching
@EnableScheduling
@ComponentScan("com.qxs")
public class GeneratorApplication{

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorApplication.class);

	public static void main(String[] args) {
		String projectPath = ProjectUtil.getProjectPath(GeneratorApplication.class);
		if(ProjectUtil.isSpringBootProject(GeneratorApplication.class)){
			LOGGER.info("当前项目是Spring boot项目，jar文件目录：[{}]", projectPath);
			projectPath = projectPath.substring(0, projectPath.lastIndexOf("/") + 1);
			LOGGER.info("当前项目是Spring boot项目，项目目录：[{}]", projectPath);
		}

		//插件地址
		String pluginsPath = projectPath + "plugins/";

		LOGGER.info("插件地址：[{}]", pluginsPath);

		System.setProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME, pluginsPath);

		ApplicationContext applicationContext = SpringApplication.run(GeneratorApplication.class, args);

		ApplicationContextUtil.setApplicationContext(applicationContext);
	}
	
	
}
