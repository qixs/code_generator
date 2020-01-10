package com.qxs.generator.web.service.user.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.qxs.base.model.Table;
import com.qxs.base.util.SpringBeanRegisterUtil;
import com.qxs.base.util.code.compiler.JavaStringCompiler;
import com.qxs.database.extractor.IExtractor;
import com.qxs.generator.web.config.datasource.SQLiteDataSource;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.UserCustomPluginTemp;
import com.qxs.generator.web.model.user.UserPlugin;
import com.qxs.generator.web.repository.user.IUserCustomPluginTempRepository;
import com.qxs.generator.web.service.IClassService;
import com.qxs.generator.web.service.user.IUserCustomPluginTempService;
import com.qxs.generator.web.service.user.IUserPluginService;
import com.qxs.generator.web.util.JavaSourceCodeUtil;
import com.qxs.plugin.factory.generator.IGenerator;
import com.qxs.plugin.factory.model.PluginConfig;

@Service
public class UserCustomPluginTempServiceImpl implements IUserCustomPluginTempService {
	
	private transient final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String[] TEST_GENERATE_CODE_REMOVE_PREFIXS = new String[] {"test_", "c_"};
	private static final String[] TEST_GENERATE_CODE_TABLE_NAMES = new String[] {"test_generate_code_test_table"};

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private IUserCustomPluginTempRepository userCustomPluginTempRepository;
	@Autowired
	private IUserPluginService userPluginService;
	@Autowired
	private IClassService classService;
	@Autowired
	private IExtractor extractor;
	@Autowired
	private DataSource dataSource;

	@Transactional
	@Override
	public UserCustomPluginTemp newUserCustomPluginTemp(String userPluginId) {
		UserCustomPluginTemp userCustomPluginTemp = new UserCustomPluginTemp();
		
		if(StringUtils.hasLength(userPluginId)) {
			UserPlugin userPlugin = userPluginService.getById(userPluginId);
			userCustomPluginTemp.setUserPluginId(userPluginId);
			userCustomPluginTemp.setGroupName(userPlugin.getGroupName());
			userCustomPluginTemp.setName(userPlugin.getName());
			userCustomPluginTemp.setDescription(userPlugin.getDescription());
			userCustomPluginTemp.setTemplateContent(userPlugin.getTemplateContent());
			userCustomPluginTemp.setGenerator(userPlugin.getGenerator());
			userCustomPluginTemp.setGeneratorSourceContent(userPlugin.getGeneratorSourceContent());
			userCustomPluginTemp.setGeneratorContent(userPlugin.getGeneratorContent());
			userCustomPluginTemp.setFileRelativeDir(userPlugin.getFileRelativeDir());
			userCustomPluginTemp.setFileSuffix(userPlugin.getFileSuffix());
			userCustomPluginTemp.setPrefix(userPlugin.getPrefix());
			userCustomPluginTemp.setSuffix(userPlugin.getSuffix());
			userCustomPluginTemp.setDependencies(userPlugin.getDependencies());
			userCustomPluginTemp.setCustom(userPlugin.getCustom());
			
		}
		userCustomPluginTempRepository.saveAndFlush(userCustomPluginTemp);
		
		return userCustomPluginTemp;
	}

	@Transactional
	@Override
	public UserCustomPluginTemp getById(String id) {
		return userCustomPluginTempRepository.getOne(id);
	}

	@Transactional
	@Override
	public void save(UserCustomPluginTemp userCustomPluginTemp) {
		userCustomPluginTempRepository.save(userCustomPluginTemp);
	}

	@Transactional
	@Override
	public void savePlugin(UserCustomPluginTemp userCustomPluginTemp) {
		//校验插件名称、全路径、依赖插件是否发生变更

		UserCustomPluginTemp ucpt = userCustomPluginTempRepository.getOne(userCustomPluginTemp.getId());
		//如果不是自定义插件
		if(ucpt.getCustom() != null && UserPlugin.CUSTOM_STATUS_IS_NOT_CUSTOM == ucpt.getCustom()){
			if(!ucpt.getName().equals(userCustomPluginTemp.getName())){
				throw new BusinessException("不能修改非自定义插件插件名");
			}
			if(!ucpt.getGenerator().equals(userCustomPluginTemp.getGenerator())){
				throw new BusinessException("不能修改非自定义插件生成器全路径");
			}

			if(!StringUtils.hasLength(userCustomPluginTemp.getDependencies())){
				userCustomPluginTemp.setDependencies(null);
			}
			if((ucpt.getDependencies() != null && !ucpt.getDependencies().equals(userCustomPluginTemp.getDependencies())) ||
					(userCustomPluginTemp.getDependencies() != null && !userCustomPluginTemp.getDependencies().equals(ucpt.getDependencies()))){
				throw new BusinessException("不能修改非自定义插件依赖插件名");
			}
		}

		//先保存到插件临时表
		userCustomPluginTempRepository.save(userCustomPluginTemp);
		//保存到插件表
		userPluginService.savePlugin(userCustomPluginTemp.getId());
	}

	@Override
	public String generateCode(UserCustomPluginTemp userCustomPluginTemp) {
		//校验生成器全路径是否为空
		if(StringUtils.isEmpty(userCustomPluginTemp.getGenerator())) {
			throw new BusinessException("生成器全路径不能为空");
		}
		//校验模板源码不能为空
		if(StringUtils.isEmpty(userCustomPluginTemp.getTemplateContent())) {
			throw new BusinessException("模板源码不能为空");
		}
		//生成器源码不能为空
		if(StringUtils.isEmpty(userCustomPluginTemp.getGeneratorSourceContent())) {
			throw new BusinessException("生成器源码不能为空");
		}
		//抽取插件源码和生成器全路径对比是否一致
		String classFullName = JavaSourceCodeUtil.getClassFullName(userCustomPluginTemp.getGeneratorSourceContent());
		logger.debug("根据源码抽取出的类名:[{}]", classFullName);
		if(!userCustomPluginTemp.getGenerator().equals(classFullName)) {
			throw new BusinessException("生成器全路径和根据源码抽取出的类名不一致，请核实，生成器类名：“" + 
					userCustomPluginTemp.getGenerator() + "” 抽取出的类名：“" + classFullName + "”");
		}
		
		List<UserPlugin> userPlugins = new ArrayList<>();
		//如果依赖其他插件则需要先查询出其他插件
		if(StringUtils.hasLength(userCustomPluginTemp.getDependencies())) {
			String[] dependencies = userCustomPluginTemp.getDependencies().split(",");
			
			userPlugins.addAll(userPluginService.findByPluginNames(userCustomPluginTemp.getGroupName() ,dependencies));
			
			Assert.isTrue(dependencies.length == userPlugins.size(), 
					"依赖插件数量和查询出的插件数量不一致，依赖插件数量：" + dependencies.length + "， 查询出的插件数量：" + userPlugins.size() + "，请核实依赖插件名是否错误");
		}
		
		List<String> registerBeanIds = new ArrayList<>();
		
		try {
			//生成字节码
			String classContent = classService.generateClassContentByClassName(userCustomPluginTemp.getGeneratorSourceContent(), userCustomPluginTemp.getGenerator());
			
			UserPlugin up = new UserPlugin();
			up.setGroupName(userCustomPluginTemp.getGroupName());
			up.setName(userCustomPluginTemp.getName());
			up.setGenerator(userCustomPluginTemp.getGenerator());
			up.setGeneratorContent(classContent);
			up.setTemplateContent(userCustomPluginTemp.getTemplateContent());
			up.setPrefix(userCustomPluginTemp.getPrefix());
			up.setSuffix(userCustomPluginTemp.getSuffix());
			
			userPlugins.add(up);
//
//			//按照插件依赖关系重新整理插件顺序
//			sortByDependencies(userPlugins);
//
			Map<String,byte[]> byteMap = new LinkedHashMap<>();
			//放入所有的插件的字节码
			for(int i = 0 , length = userPlugins.size() ; i < length ; i ++) {
				UserPlugin userPlugin = userPlugins.get(i);
				byteMap.put(userPlugin.getGenerator(), stringToBytes(userPlugin.getGeneratorContent()));
			}
			
			//传入字节码生成
			if(StringUtils.hasLength(classContent)) {
				//当前类加入map
				byteMap.put(userCustomPluginTemp.getGenerator(), stringToBytes(classContent));
			}
			
	
			//所有的插件
			PluginConfig[] pluginConfigs = new PluginConfig[userPlugins.size()];
			//要生成代码的插件
			PluginConfig generatePluginConfig = null;
			
			for(int i = 0 , length = userPlugins.size() ; i < length ; i ++) {
				UserPlugin userPlugin = userPlugins.get(i);
				
				PluginConfig pluginConfig = new PluginConfig();
				pluginConfig.setGroupName(userPlugin.getGroupName());
				pluginConfig.setName(userPlugin.getName());
				pluginConfig.setDescription(userPlugin.getDescription());
				pluginConfig.setTemplatePath(userPlugin.getTemplatePath());
				pluginConfig.setGenerator(userPlugin.getGenerator());
				pluginConfig.setFileRelativeDir(userPlugin.getFileRelativeDir());
				pluginConfig.setFileSuffix(userPlugin.getFileSuffix());
				pluginConfig.setPrefix(userPlugin.getPrefix());
				pluginConfig.setSuffix(userPlugin.getSuffix());
				pluginConfig.setPluginPath(userPlugin.getPluginPath());
				
				JavaStringCompiler compiler = new JavaStringCompiler();
				
				String generator = pluginConfig.getGenerator();
				
				Class<?> generatorClass = null;
				try {
					generatorClass = compiler.loadClass(generator,byteMap);
				} catch (ClassNotFoundException e) {
					throw new BusinessException(e);
				} catch (IOException e) {
					throw new BusinessException(e);
				}
				pluginConfig.setGeneratorClass(generatorClass);
				
				//beanId带上随机数,避免多个generator相同互相影响
				String beanId = String.format("%s_%s", UUID.randomUUID(), generatorClass.getName());
				SpringBeanRegisterUtil.registerBean(beanId, generatorClass, applicationContext);
				
				pluginConfig.setGenerateBeanId(beanId);
				
				pluginConfigs[i] = pluginConfig;
				if(userCustomPluginTemp.getName().equals(pluginConfig.getName())) {
					pluginConfig.setTemplateBytes(userPlugin.getTemplateContent().getBytes());
					pluginConfig.setDescription(userCustomPluginTemp.getDescription());
					pluginConfig.setFileRelativeDir(userCustomPluginTemp.getFileRelativeDir());
					pluginConfig.setFileSuffix(userCustomPluginTemp.getFileSuffix());
					pluginConfig.setPrefix(userCustomPluginTemp.getPrefix());
					pluginConfig.setSuffix(userCustomPluginTemp.getSuffix());
					
					generatePluginConfig = pluginConfig;
				}
				
				registerBeanIds.add(beanId);
			}
			
			//生成代码时数据库会创建多个connection,使用信号量会造成系统卡死,不使用信号量
			SQLiteDataSource.setIgnoreSemaphore(true);
			
			//生成代码
			//抽取所有表结构
			List<Table> tables = extractor.extractorTables(dataSource, TEST_GENERATE_CODE_TABLE_NAMES);
			
			Assert.notEmpty(tables,"未获取到表信息");
			
			//如果是超级用户则不根据用户进行过滤,直接查询所有可用的插件,否则查询该用户名下插件
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			User user = (User) authentication.getPrincipal();
			
			IGenerator generator = (IGenerator) applicationContext.getBean(generatePluginConfig.getGenerateBeanId());
			generator.setDataSource(dataSource);
			generator.setAuthor(user.getName());
			
			byte[] bytes = generator.generate(pluginConfigs, generatePluginConfig, tables.get(0), 
					TEST_GENERATE_CODE_REMOVE_PREFIXS, new ByteArrayInputStream(generatePluginConfig.getTemplateBytes()));
			
			return new String(bytes);
		} finally {
			for(String beanId : registerBeanIds) {
				SpringBeanRegisterUtil.unregisterBean(beanId, applicationContext);
			}
			
			SQLiteDataSource.clearIgnoreSemaphore();
		}
	}
	
	private byte[] stringToBytes(String content) {
		String[] byteString = content.split(",");
		byte[] bytes = new byte[byteString.length];
		for(int k = 0 ; k < byteString.length ; k ++) {
			bytes[k] = Byte.valueOf(byteString[k]);
		}
		return bytes;
	}
}
