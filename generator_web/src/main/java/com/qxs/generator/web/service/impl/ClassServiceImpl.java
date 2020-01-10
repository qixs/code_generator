package com.qxs.generator.web.service.impl;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.qxs.base.util.code.compiler.JavaStringCompiler;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.user.UserPlugin;
import com.qxs.generator.web.service.IClassService;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.generator.web.service.user.IUserPluginService;

/**
 * @author qixingshen
 * **/
@Service
public class ClassServiceImpl implements IClassService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private IUserPluginService userPluginService;

	@Override
	public String generateClassContent(String source, String groupName, String pluginName) {
		if(!StringUtils.hasLength(source)) {
			throw new BusinessException("源码为空");
		}
		
		//获取插件配置信息
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		
		return generateContent(source, plugin.getGenerator());
	}
	
	@Override
	public String generateClassContentByClassName(String source, String className) {
		Assert.hasLength(source, "源码为空");
		Assert.hasLength(className, "类名为空");
		
		return generateContent(source, className);
	}
	
	private String generateContent(String source,String className) {
		String fileName = className.substring(className.lastIndexOf(".") + 1) + ".java";
		
		JavaStringCompiler compiler = new JavaStringCompiler();

		logger.debug("generateContent class loader:[{}]", Thread.currentThread().getContextClassLoader());

		try {
			Map<String, byte[]> bytesMap = compiler.compile(fileName, source);
			StringBuilder sb = new StringBuilder();
			for(byte b : bytesMap.values().iterator().next()) {
				if(sb.length() > 0) {
					sb.append(",");
				}
				sb.append(b);
			}
			return sb.toString();
		} catch (IOException e) {
			logger.error("生成字节码文件出错:[{}]", source, e);
			throw new BusinessException(e);
		} catch (RuntimeException e) {
			logger.error("生成字节码文件出错:[{}]", source, e);
			throw new BusinessException(e);
		}
	}

	@Override
	public String userGenerateClassContent(String source, String groupName, String pluginName) {
		UserPlugin userPlugin = userPluginService.getPluginByName(groupName, pluginName);
		return userGenerateClassContent(source, userPlugin);
	}

	@Override
	public String userGenerateClassContent(String source, UserPlugin userPlugin) {
		return generateContent(source, userPlugin.getGenerator());
	}
	
}
