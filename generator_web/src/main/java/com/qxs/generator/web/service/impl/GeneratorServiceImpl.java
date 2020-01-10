package com.qxs.generator.web.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.gson.Gson;
import com.jcraft.jsch.JSchException;
import com.qxs.base.model.Table;
import com.qxs.base.util.SpringBeanRegisterUtil;
import com.qxs.base.util.SshClient;
import com.qxs.base.util.code.compiler.JavaStringCompiler;
import com.qxs.database.extractor.IExtractor;
import com.qxs.generator.web.config.DbUrlWarpper;
import com.qxs.generator.web.config.datasource.SQLiteDataSource;
import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.GenerateResult;
import com.qxs.generator.web.model.GenerateResult.Status;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.model.log.Generate;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.UserPlugin;
import com.qxs.generator.web.service.IGeneratorService;
import com.qxs.generator.web.service.log.IGenerateService;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.generator.web.service.user.IUserPluginService;
import com.qxs.generator.web.service.user.IUserService;
import com.qxs.generator.web.util.DataSourceUtil;
import com.qxs.generator.web.util.DateUtil;
import com.qxs.generator.web.util.EncryptUtil;
import com.qxs.plugin.factory.generator.IGenerator;
import com.qxs.plugin.factory.model.PluginConfig;

/**
 * @author qixingshen
 * **/
@Service
public class GeneratorServiceImpl implements IGeneratorService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorServiceImpl.class);
	
	private static final String[] TEST_GENERATE_CODE_REMOVE_PREFIXS = new String[] {"test_", "c_"};
	private static final String[] TEST_GENERATE_CODE_TABLE_NAMES = new String[] {"test_generate_code_test_table"};

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private IUserPluginService userPluginService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private IGenerateService generateService;
	@Autowired
	private IExtractor extractor;
	@Autowired
	private DataSource dataSource;
	
	/**
	 * 生成代码线程数，
	 * **/
	private static ExecutorService CACHED_THREAD_POOL;
	
	static {
		int processors = Runtime.getRuntime().availableProcessors();
		CACHED_THREAD_POOL = new ThreadPoolExecutor(processors, processors * 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
	}
	
	@Transactional
	@Override
	public GenerateResult generate(WebSocketSession session, Database database, Ssh ssh,GenerateParameter generateParameter) {
		return generate(session, database, ssh, generateParameter, true);
	}
	
	@Transactional
	@Override
	public GenerateResult generate(WebSocketSession session, Database database, Ssh ssh,GenerateParameter generateParameter, boolean log) {
		User user = (User)((UsernamePasswordAuthenticationToken) session.getPrincipal()).getPrincipal();
		
		//生成代码开始时间
		String generateStartDate = DateUtil.currentDate();
		//生成代码开始毫秒数
		long generateStartTimeMillis = System.currentTimeMillis();
		
		Gson gson = new Gson();
		
		List<String> registerBeanIds = new ArrayList<>();
		DruidDataSource dataSource = DataSourceUtil.getDataSource(database);
		
		SshClient sshClient = null;
		try {
			if(ssh != null && StringUtils.hasLength(ssh.getHost())) {
				sshClient = new SshClient(ssh.getHost(),ssh.getPort(), ssh.getUsername(), ssh.getPassword());
				sshClient.connect();
				
				int port = sshClient.forward(database.getUrl(), database.getUrl(), database.getPort());
				
				String url = DbUrlWarpper.warp(database.getType(), database.getUrl(), port,database.getDatabaseName());
				
				dataSource.setUrl(url);
			}
			
			String[] pluginNames = generateParameter.getPluginNames() == null ? null : generateParameter.getPluginNames().split(",");
			//获取用户可用插件列表
			UserPlugin up = new UserPlugin();
			
			List<UserPlugin> userPlugins = null;
			if(user.getAdmin() == User.ADMIN_STATUS_IS_ADMIN) {
				//超级用户查询所有可用插件
				Plugin plugin = new Plugin();
				plugin.setStatus(IntConstants.STATUS_ENABLE.getIntCode());
				List<Plugin> plugins = pluginService.findPluginList(plugin, null);
				userPlugins = new ArrayList<>(plugins.size());
				for(Plugin p : plugins) {
					UserPlugin userPlugin = new UserPlugin();
					userPlugin.setGroupName(p.getGroupName());
					userPlugin.setName(p.getName());
					userPlugin.setDescription(p.getDescription());
					userPlugin.setTemplatePath(p.getTemplatePath());
					userPlugin.setTemplateContent(p.getTemplateContent());
					userPlugin.setGenerator(p.getGenerator());
					userPlugin.setGeneratorSourceContent(p.getGeneratorSourceContent());
					userPlugin.setGeneratorContent(p.getGeneratorContent());
					userPlugin.setFileRelativeDir(p.getFileRelativeDir());
					userPlugin.setFileSuffix(p.getFileSuffix());
					userPlugin.setPrefix(p.getPrefix());
					userPlugin.setSuffix(p.getSuffix());
					userPlugin.setPluginPath(p.getPluginPath());
					userPlugin.setDependencies(p.getDependencies());
					userPlugin.setStatus(p.getStatus());
					
					userPlugins.add(userPlugin);
				}
			}else {
				up.setUser(userService.findById(user.getId()));
				up.setStatus(IntConstants.STATUS_ENABLE.getIntCode());
				userPlugins = userPluginService.findUserPluginList(up, null);
			}
			
			//校验插件是否可用
			for(String pluginName : pluginNames) {
				String[] names = pluginName.split("\\.");
				Optional<UserPlugin> userPluginOptional = userPlugins.stream().filter(p -> p.getGroupName().equals(names[0]) && p.getName().equals(names[1])).findFirst();
				if(!userPluginOptional.isPresent()) {
					if(log) {
						//处理密码参数
						if(StringUtils.hasLength(database.getPassword())) {
							database.setPassword(EncryptUtil.desEncode(database.getPassword(), user.getId()));
						}
						if(ssh != null && StringUtils.hasLength(ssh.getPassword())) {
							ssh.setPassword(EncryptUtil.desEncode(ssh.getPassword(), user.getId()));
						}
						
						//登记日志
						generateService.insert(user, new Generate(generateStartDate, DateUtil.currentDate(), 
								(System.currentTimeMillis() - generateStartTimeMillis) + "ms", 
								gson.toJson(database), gson.toJson(ssh),gson.toJson(generateParameter), 
								Generate.Status.FAIL.getStatus(), "插件“" + pluginName + "”不存在"));
					}
					
					return new GenerateResult(Status.FAIL, "插件“" + pluginName + "”不存在");
				}
				UserPlugin userPlugin = userPluginOptional.get();
				if(userPlugin.getStatus() == IntConstants.STATUS_DISABLE.getCode()) {
					if(log) {
						//处理密码参数
						if(StringUtils.hasLength(database.getPassword())) {
							database.setPassword(EncryptUtil.desEncode(database.getPassword(), user.getId()));
						}
						if(ssh != null && StringUtils.hasLength(ssh.getPassword())) {
							ssh.setPassword(EncryptUtil.desEncode(ssh.getPassword(), user.getId()));
						}
						
						//登记日志
						generateService.insert(user, new Generate(generateStartDate, DateUtil.currentDate(), 
								(System.currentTimeMillis() - generateStartTimeMillis) + "ms", 
								gson.toJson(database), gson.toJson(ssh),gson.toJson(generateParameter), 
								Generate.Status.FAIL.getStatus(), "插件“" + pluginName + "”不可用"));
					}
					
					return new GenerateResult(Status.FAIL, "插件“" + pluginName + "”不可用");
				}
			}
			

//			//按照插件依赖关系重新整理插件顺序
//			sortByDependencies(userPlugins);
			
			Map<String,byte[]> byteMap = new LinkedHashMap<>();
			//放入所有的插件的字节码
			for(int i = 0 , length = userPlugins.size() ; i < length ; i ++) {
				UserPlugin userPlugin = userPlugins.get(i);
				byteMap.put(userPlugin.getGenerator(), stringToBytes(userPlugin.getGeneratorContent()));
			}
			
			//要生成代码的插件名称列表
			List<String> generatePluginNameList = Arrays.asList(pluginNames);
			//所有的插件
			PluginConfig[] pluginConfigs = new PluginConfig[userPlugins.size()];
			//要生成代码的插件列表
			List<PluginConfig> generatePluginConfigs = new ArrayList<PluginConfig>();
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
				
				//当前类加入map
				Map<String,byte[]> byteMapClone = new LinkedHashMap<>(byteMap);
				byteMapClone.put(userPlugin.getGenerator(), stringToBytes(userPlugin.getGeneratorContent()));
				
				JavaStringCompiler compiler = new JavaStringCompiler();
				
				Class<?> generatorClass = null;
				try {
					generatorClass = compiler.loadClass(userPlugin.getGenerator(), byteMapClone);
				} catch (ClassNotFoundException e) {
					throw new BusinessException(e);
				} catch (IOException e) {
					throw new BusinessException(e);
				}
				
				pluginConfig.setGeneratorClass(generatorClass);
				
				//每个用户注册一个bean,避免多个用户之间bean互相注册,因为支持不同用户相同插件但是插件内容不一致
				String beanId = String.format("%s_%s", session.getId(), generatorClass.getName());
				SpringBeanRegisterUtil.registerBean(beanId, generatorClass, applicationContext);
				
				pluginConfig.setGenerateBeanId(beanId);
				
				pluginConfigs[i] = pluginConfig;
				if(generatePluginNameList.contains(pluginConfig.getGroupName() + "." + pluginConfig.getName())) {
					pluginConfig.setTemplateBytes(userPlugin.getTemplateContent().getBytes());
					generatePluginConfigs.add(pluginConfig);
				}
				
				registerBeanIds.add(beanId);
			}
			
			generateCodeFile(session, dataSource, 
					pluginConfigs, generatePluginConfigs, 
					generateParameter.getRemovePrefixs() == null ? null : generateParameter.getRemovePrefixs().split(","), 
					generateParameter.getTableNames() == null ? null : generateParameter.getTableNames().split(","));
			if(log) {
				//处理密码参数
				if(StringUtils.hasLength(database.getPassword())) {
					database.setPassword(EncryptUtil.desEncode(database.getPassword(), user.getId()));
				}
				if(ssh != null && StringUtils.hasLength(ssh.getPassword())) {
					ssh.setPassword(EncryptUtil.desEncode(ssh.getPassword(), user.getId()));
				}
				
				//登记生成参数,支持重新生成
				generateService.insert(user, new Generate(generateStartDate, DateUtil.currentDate(), 
						(System.currentTimeMillis() - generateStartTimeMillis) + "ms", 
						gson.toJson(database), gson.toJson(ssh),gson.toJson(generateParameter), 
						Generate.Status.SUCCESS.getStatus(),  null));
			}
			
			return new GenerateResult(GenerateResult.Status.SUCCESS);
		} catch (JSchException e) {
			LOGGER.error(e.getMessage(),e);
			if(log) {
				//处理密码参数
				if(StringUtils.hasLength(database.getPassword())) {
					database.setPassword(EncryptUtil.desEncode(database.getPassword(), user.getId()));
				}
				if(ssh != null && StringUtils.hasLength(ssh.getPassword())) {
					ssh.setPassword(EncryptUtil.desEncode(ssh.getPassword(), user.getId()));
				}
				
				//登记日志
				generateService.insert(user, new Generate(generateStartDate, DateUtil.currentDate(), 
						(System.currentTimeMillis() - generateStartTimeMillis) + "ms", 
						gson.toJson(database), gson.toJson(ssh),gson.toJson(generateParameter), 
						Generate.Status.FAIL.getStatus(),  "ssh服务器连接失败:"+e.getMessage()));
			}
			
			return new GenerateResult(Status.FAIL, "ssh服务器连接失败:"+e.getMessage());
		} catch (BindException e) {
			LOGGER.error(e.getMessage(),e);
			if(log) {
				//处理密码参数
				if(StringUtils.hasLength(database.getPassword())) {
					database.setPassword(EncryptUtil.desEncode(database.getPassword(), user.getId()));
				}
				if(ssh != null && StringUtils.hasLength(ssh.getPassword())) {
					ssh.setPassword(EncryptUtil.desEncode(ssh.getPassword(), user.getId()));
				}
				
				//登记日志
				generateService.insert(user, new Generate(generateStartDate, DateUtil.currentDate(), 
						(System.currentTimeMillis() - generateStartTimeMillis) + "ms", 
						gson.toJson(database), gson.toJson(ssh),gson.toJson(generateParameter), 
						Generate.Status.FAIL.getStatus(),  "ssh服务器创建隧道失败:"+e.getMessage()));
			}
			
			return new GenerateResult(Status.FAIL, "ssh服务器创建隧道失败:"+e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
			return new GenerateResult(Status.FAIL, "向客户端发送消息失败:" + e.getMessage());
		} finally {
			if(sshClient != null) {
				sshClient.disconnect();
			}
			if(dataSource != null) {
				dataSource.close();
			}
			for(String beanId : registerBeanIds) {
				SpringBeanRegisterUtil.unregisterBean(beanId, applicationContext);
			}
		}
	}
	
	/***
	 * 生成代码方法
	 * 
	 * @param dataSource 数据库连接信息
	 * @param pluginConfigs 可生成代码的所有插件的配置信息
	 * @param generatePluginConfigs 要生成代码的插件的配置信息
	 * @param removePrefixs 需要删除的前缀
	 * @param tableNames 要生成的表名,如果为空则认为生成所有表
	 * 
	 * **/
	public void generateCodeFile(WebSocketSession session, DataSource dataSource, PluginConfig[] pluginConfigs,
			List<PluginConfig> generatePluginConfigs,String[] removePrefixs,String... tableNames) throws IOException{
		Assert.notNull(dataSource, "dataSource参数不能为空");
		Assert.notEmpty(pluginConfigs, "插件配置不能为空");
		
		//如果是超级用户则不根据用户进行过滤,直接查询所有可用的插件,否则查询该用户名下插件
		User user = (User)((UsernamePasswordAuthenticationToken) session.getPrincipal()).getPrincipal();
		
		//抽取所有表结构
		List<Table> tables = extractor.extractorTables(dataSource, tableNames);
		
		Assert.notEmpty(tables,"未获取到表信息");
		
		//向客户端发送生成代码进度
		session.sendMessage(new TextMessage(String.format("生成代码插件个数：%s 生成代码表个数：%s 总计要生成的代码文件个数：%s", 
				generatePluginConfigs.size(), tables.size(), generatePluginConfigs.size() * tables.size())));
		
		session.sendMessage(new TextMessage("{\"generateTotalNum\":" + generatePluginConfigs.size() * tables.size() + "}"));
		
		//已经生成的个数
		LongAdder generatedNum = new LongAdder();
		
		final CountDownLatch countDownLatch = new CountDownLatch(generatePluginConfigs.size() * tables.size());
		
		for(PluginConfig pluginConfig : generatePluginConfigs) {
			IGenerator generator = (IGenerator) applicationContext.getBean(pluginConfig.getGenerateBeanId());
			generator.setDataSource(dataSource);
			generator.setAuthor(user.getName());
			//生成代码
			for(Table table : tables) {
				
				CACHED_THREAD_POOL.execute(new Runnable() {
					@Override
					public void run() {
						//当前已经生成的文件个数
						generatedNum.increment();
						sendMessage(session, "{\"generatedNum\":" + generatedNum.intValue() + "}");

						byte[] bytes = generator.generate(pluginConfigs,pluginConfig,table,removePrefixs, new ByteArrayInputStream(pluginConfig.getTemplateBytes()));
						String fileRelativePath = generator.getFileRelativePath(table,pluginConfig,removePrefixs);
						
						sendMessage(session, "file:" + fileRelativePath + ":content:" + new String(bytes));
						
						countDownLatch.countDown();
					}
				});
			}
		}
		
		try {
			countDownLatch.await();
			System.out.println();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void sendMessage(WebSocketSession session, String message) {
		try {
			session.sendMessage(new TextMessage(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String sourceGenerateCode(String classContent, String groupName, String pluginName) {
		//获取插件配置信息
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		
		return generateCode(classContent, null, null, plugin);
	}

	@Override
	public String templateGenerateCode(String template, String groupName, String pluginName) {
		//获取插件配置信息
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		
		return generateCode(null, template, null, plugin);
	}

	@Override
	public String pluginGenerateCode(Plugin plu) {
		Plugin plugin = pluginService.getPluginByName(plu.getGroupName(), plu.getName());
		
		return generateCode(null, null, plu, plugin);
	}

	@Override
	public String userGenerateCode(String classContent, String groupName, String pluginName) {
		UserPlugin userPlugin = userPluginService.getPluginByName(groupName, pluginName);
		Plugin plugin = new Plugin();
		plugin.setGroupName(userPlugin.getGroupName());
		plugin.setName(userPlugin.getName());
		plugin.setDescription(userPlugin.getDescription());
		plugin.setTemplatePath(userPlugin.getTemplatePath());
		plugin.setTemplateContent(userPlugin.getTemplateContent());
		plugin.setGenerator(userPlugin.getGenerator());
		plugin.setGeneratorSourceContent(userPlugin.getGeneratorSourceContent());
		plugin.setGeneratorContent(userPlugin.getGeneratorContent());
		plugin.setFileRelativeDir(userPlugin.getFileRelativeDir());
		plugin.setFileSuffix(userPlugin.getFileSuffix());
		plugin.setPrefix(userPlugin.getPrefix());
		plugin.setSuffix(userPlugin.getSuffix());
		plugin.setPluginPath(userPlugin.getPluginPath());
		plugin.setDependencies(userPlugin.getDependencies());
		plugin.setStatus(userPlugin.getStatus());
		
		return generateCode(classContent, null, null, plugin);
	}

	@Override
	public String userTemplateGenerateCode(String template, String groupName, String pluginName) {
		UserPlugin userPlugin = userPluginService.getPluginByName(groupName, pluginName);
		Plugin plugin = new Plugin();
		plugin.setGroupName(userPlugin.getGroupName());
		plugin.setName(userPlugin.getName());
		plugin.setDescription(userPlugin.getDescription());
		plugin.setTemplatePath(userPlugin.getTemplatePath());
		plugin.setTemplateContent(userPlugin.getTemplateContent());
		plugin.setGenerator(userPlugin.getGenerator());
		plugin.setGeneratorSourceContent(userPlugin.getGeneratorSourceContent());
		plugin.setGeneratorContent(userPlugin.getGeneratorContent());
		plugin.setFileRelativeDir(userPlugin.getFileRelativeDir());
		plugin.setFileSuffix(userPlugin.getFileSuffix());
		plugin.setPrefix(userPlugin.getPrefix());
		plugin.setSuffix(userPlugin.getSuffix());
		plugin.setPluginPath(userPlugin.getPluginPath());
		plugin.setDependencies(userPlugin.getDependencies());
		plugin.setStatus(userPlugin.getStatus());

		return generateCode(null, template, null, plugin);
	}

	private String generateCode(String classContent, String template, Plugin plu, Plugin plug) {
		List<String> registerBeanIds = new ArrayList<>();
		String groupName = plug.getGroupName();
		String pluginName = plug.getName();
		try {
			//获取用户可用插件列表
			UserPlugin up = new UserPlugin();
			
			//如果是超级用户则不根据用户进行过滤,直接查询所有可用的插件,否则查询该用户名下插件
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			User user = (User) authentication.getPrincipal();
			
			List<UserPlugin> userPlugins = null;
			if(user.getAdmin() == User.ADMIN_STATUS_IS_ADMIN) {
				LOGGER.debug("当前用户是超级管理员：[{}]", user.getName());

				//超级用户查询所有可用插件
				Plugin plugin = new Plugin();
				plugin.setStatus(IntConstants.STATUS_ENABLE.getIntCode());
				plugin.setGroupName(groupName);
				List<Plugin> plugins = pluginService.findPluginList(plugin, null);
				userPlugins = new ArrayList<>(plugins.size());
				for(Plugin p : plugins) {
					UserPlugin userPlugin = new UserPlugin();
					userPlugin.setGroupName(p.getGroupName());
					userPlugin.setName(p.getName());
					userPlugin.setDescription(p.getDescription());
					userPlugin.setTemplatePath(p.getTemplatePath());
					userPlugin.setTemplateContent(p.getTemplateContent());
					userPlugin.setGenerator(p.getGenerator());
					userPlugin.setGeneratorSourceContent(p.getGeneratorSourceContent());
					userPlugin.setGeneratorContent(p.getGeneratorContent());
					userPlugin.setFileRelativeDir(p.getFileRelativeDir());
					userPlugin.setFileSuffix(p.getFileSuffix());
					userPlugin.setPrefix(p.getPrefix());
					userPlugin.setSuffix(p.getSuffix());
					userPlugin.setPluginPath(p.getPluginPath());
					userPlugin.setDependencies(p.getDependencies());
					userPlugin.setStatus(p.getStatus());
					
					userPlugins.add(userPlugin);
				}

				LOGGER.debug("当前用户可访问插件列表：[{}]", userPlugins);
			}else {
				up.setUser(userService.findById(user.getId()));
				up.setStatus(IntConstants.STATUS_ENABLE.getIntCode());
				up.setGroupName(groupName);
				userPlugins = userPluginService.findUserPluginList(up, null);
			}
			
			//校验插件是否可用
			Optional<UserPlugin> userPluginOptional = userPlugins.stream().filter(p -> p.getName().equals(pluginName)).findFirst();
			if(!userPluginOptional.isPresent()) {
				throw new BusinessException("插件“" + pluginName + "”不存在或已禁用");
			}
			
//			//按照插件依赖关系重新整理插件顺序
//			sortByDependencies(userPlugins);
			
			Map<String,byte[]> byteMap = new LinkedHashMap<>();
			//放入所有的插件的字节码
			for(int i = 0 , length = userPlugins.size() ; i < length ; i ++) {
				UserPlugin userPlugin = userPlugins.get(i);
				byteMap.put(userPlugin.getGenerator(), stringToBytes(userPlugin.getGeneratorContent()));
			}
			
			//传入字节码生成
			if(StringUtils.hasLength(classContent)) {
				//当前类加入map
				byteMap.put(plug.getGenerator(), stringToBytes(classContent));
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
				
				//带上随机数,避免多个generator相同bean互相注册
				String beanId = String.format("%s_%s", UUID.randomUUID(), generatorClass.getName());
				SpringBeanRegisterUtil.registerBean(beanId, generatorClass, applicationContext);
				
				pluginConfig.setGenerateBeanId(beanId);
				
				pluginConfigs[i] = pluginConfig;
				if(groupName.equals(pluginConfig.getGroupName()) && pluginName.equals(pluginConfig.getName())) {
					//传入模板代码生成
					if(StringUtils.hasLength(template)) {
						pluginConfig.setTemplateBytes(template.getBytes());
					}else {
						pluginConfig.setTemplateBytes(userPlugin.getTemplateContent().getBytes());
					}
					
					//参数
					if(plu != null) {
						pluginConfig.setDescription(plu.getDescription());
						pluginConfig.setFileRelativeDir(plu.getFileRelativeDir());
						pluginConfig.setFileSuffix(plu.getFileSuffix());
						pluginConfig.setPrefix(plu.getPrefix());
						pluginConfig.setSuffix(plu.getSuffix());
					}
					
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
//	/**
//	 * 根据依赖关系树重新排序插件列表
//	 * **/
//	private void sortByDependencies(List<UserPlugin> userPlugins) {
//		//先按照分组进行排序
//		userPlugins.sort(new Comparator<UserPlugin>() {
//			@Override
//			public int compare(UserPlugin o1, UserPlugin o2) {
//				return o1.getGroupName().compareTo(o2.getGroupName());
//			}
//		});
//
//		Map<String,UserPlugin> plugins = new HashMap<>(userPlugins.size());
//		userPlugins.stream().forEach(p -> plugins.put(p.getName(), p));
//
//		//循环插件列表,把当前插件依赖的插件插入到当前插件之前,因为list数量小,暂不考虑性能问题
//		for(int i=0; i < userPlugins.size() ; i++) {
//			for(int j = 0; j < userPlugins.size() - 1; j ++) {
//				//如果下标为j的插件依赖下标为j+1的插件则需要交换顺序
//				if(userPlugins.get(j).getGroupName().equals(userPlugins.get(j + 1).getGroupName()) &&
//						String.format(",%s,", userPlugins.get(j).getDependencies()).indexOf(String.format(",%s,", userPlugins.get(j + 1).getName())) >= 0) {
//					UserPlugin userPlugin = userPlugins.get(j);
//					userPlugins.set(j, userPlugins.get(j + 1));
//					userPlugins.set(j + 1, userPlugin);
//				}
//			}
//		}
//	}
//
	private byte[] stringToBytes(String content) {
		String[] byteString = content.split(",");
		byte[] bytes = new byte[byteString.length];
		for(int k = 0 ; k < byteString.length ; k ++) {
			bytes[k] = Byte.valueOf(byteString[k]);
		}
		return bytes;
	}
}
