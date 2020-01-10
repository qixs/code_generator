package com.qxs.generator.web.service.plugin.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.qxs.base.util.OS;
import com.qxs.generator.web.annotation.ChangeEntityComment;
import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.plugin.PluginChangeHistory;
import com.qxs.generator.web.model.plugin.PluginChangeHistoryDetail;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.UserPlugin;
import com.qxs.generator.web.repository.plugin.IPluginRepository;
import com.qxs.generator.web.service.IClassService;
import com.qxs.generator.web.service.plugin.IPluginChangeHistoryDetailService;
import com.qxs.generator.web.service.plugin.IPluginChangeHistoryService;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.generator.web.service.user.IUserPluginService;
import com.qxs.generator.web.service.user.IUserService;
import com.qxs.generator.web.service.version.IVersionService;
import com.qxs.generator.web.util.DateUtil;
import com.qxs.plugin.factory.PluginLoader;
import com.qxs.plugin.factory.PluginParameterKeys;
import com.qxs.plugin.factory.exception.PluginLoadException;
import com.qxs.plugin.factory.exception.PluginNotFoundException;
import com.qxs.plugin.factory.model.PluginConfig;
import com.qxs.plugin.factory.parser.PluginConfigParser;

/**
 * @author qixingshen
 **/
@Service
public class PluginServiceImpl implements IPluginService {

	private transient final Logger logger = LoggerFactory.getLogger(getClass());

	private static final byte[] JAR_MAGIC = { 'P', 'K', 3, 4 };

	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 **/
	public static final String CACHE_KEY = "'pluginCache_'";
	/**
	 * value属性表示使用哪个缓存策略
	 */
	public static final String CACHE_NAME = "plugin";

	@Autowired
	private PluginLoader pluginLoader;
	@Autowired
	private IPluginRepository pluginRepository;
	@Autowired
	private IVersionService versionService;
	@Autowired
	private IPluginChangeHistoryService pluginChangeHistoryService;
	@Autowired
	private IPluginChangeHistoryDetailService pluginChangeHistoryDetailService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IClassService classService;
	@Autowired
	private IUserPluginService userPluginService;

	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void reloadAllPlugin() {
		String pluginDir = System.getProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME);
		if(StringUtils.isEmpty(pluginDir)) {
			logger.error("插件地址为空，不能加载插件列表");
			return;
		}
		
		// 初始化插件信息
		pluginLoader.loadAllPlugin(pluginDir);

		// 插件目录的插件信息
		List<PluginConfig> pluginConfigList = pluginLoader.getPluginConfigList();
		
		List<String> pluginConfigNameList = new ArrayList<>(pluginConfigList.size());

		for (PluginConfig pluginConfig : pluginConfigList) {
			pluginConfigNameList.add(getPluginName(pluginConfig));
		}

		//校验插件依存关系
		pluginConfigList.stream().forEach(pluginConfig -> {
			if(StringUtils.hasLength(pluginConfig.getDependencies())) {
				String[] dependencies = pluginConfig.getDependencies().split(",");
				for(String dependency : dependencies) {
					if(StringUtils.hasLength(dependency) && !pluginConfigNameList.contains(getPluginName(pluginConfig, dependency))) {
						logger.error("[{}]插件不存在，请核实", dependency);
						
						System.exit(0);
					}
				}
			}
		});
		

		// 只处理可用状态的插件,禁用状态的插件不处理,如果要使用则必须手动置为可用
		// 数据库中存储的插件信息
		List<Plugin> pluginList = findPluginList(null,null);
		// 新增的和更新的插件
		List<Plugin> insertPluginList = new ArrayList<>(pluginConfigList.size());
		
		List<String> pluginNameList = new ArrayList<>(pluginList.size());
		for (Plugin plugin : pluginList) {
			pluginNameList.add(getPluginName(plugin));
		}

		// 循环插件目录的插件,组装需要插入的插件
		pluginConfigList.forEach(pluginConfig -> {
			Plugin plugin = new Plugin();
			plugin.setSystemVersion(versionService.findVersion().getVersion());
			plugin.setUpdateDate(DateUtil.currentDate());
			plugin.setGroupName(pluginConfig.getGroupName());
			plugin.setName(pluginConfig.getName());
			plugin.setDescription(pluginConfig.getDescription());
			plugin.setTemplatePath(pluginConfig.getTemplatePath());
			plugin.setGenerator(pluginConfig.getGenerator());
			plugin.setFileRelativeDir(pluginConfig.getFileRelativeDir());
			plugin.setFileSuffix(pluginConfig.getFileSuffix());
			plugin.setPrefix(pluginConfig.getPrefix());
			plugin.setSuffix(pluginConfig.getSuffix());
			plugin.setPluginPath(pluginConfig.getPluginPath());
			plugin.setDependencies(pluginConfig.getDependencies());

			// 读取模板文件内容
			plugin.setTemplateContent(readTemplateContent(pluginConfig));
			plugin.setGeneratorSourceContent(readGeneratorSourceContent(pluginConfig.getPluginPath(), pluginConfig.getGenerator()));
			plugin.setGeneratorContent(formatArray(readGeneratorContent(pluginConfig.getPluginPath(), pluginConfig.getGenerator())));

			// 插件在数据库不存在(只处理新增插件,不处理历史插件)
			if (!pluginNameList.contains(getPluginName(pluginConfig))) {
				plugin.setCreateDate(DateUtil.currentDate());
				plugin.setCreateUserId("");
				plugin.setCreateUserName("系统更新");
				plugin.setUpdateUserId("");
				plugin.setUpdateUserName(plugin.getCreateUserName());

				plugin.setStatus(IntConstants.STATUS_ENABLE.getCode());

				insertPluginList.add(plugin);
			}
		});

		// 需要插入或更新的插件列表
		if (insertPluginList.size() > 0) {
			
			//校验插件生成器是否重复
			insertPluginList.stream().forEach(plugin ->{
				Plugin p = new Plugin();
				p.setGenerator(plugin.getGenerator());
				
				if(pluginRepository.count(Example.of(p)) > 0) {
					logger.error("插件[{}]的生成器[{}]已经存在，请先修改插件的生成器名字", plugin.getName(), plugin.getGenerator());
					System.exit(0);
				}
			});
			
			//校验要插入的数据的生成器是否重复(插件名不处理，靠插件研发人员自己处理)
			List<String> generators = new ArrayList<>(insertPluginList.size());
			for(Plugin plugin : insertPluginList) {
				if(generators.contains(plugin.getGenerator())) {
					logger.error("插件[{}]的生成器[{}]重复，请先修改插件的生成器名字", plugin.getName(), plugin.getGenerator());
					System.exit(0);
				}
				generators.add(plugin.getGenerator());
			}
			
			// 更新插件表
			pluginRepository.saveAll(insertPluginList);
			// 更新变更明细
			for (Plugin plugin : insertPluginList) {
				updateChangeHistory(plugin.getChangeHistory());
			}
		}
	}

	/**
	 * 获取插件名称，格式：组名@@@@插件名
	 * **/
	private String getPluginName(PluginConfig pluginConfig){
		return String.format("%s@@@@%s", pluginConfig.getGroupName(), pluginConfig.getName());
	}
	/**
	 * 获取插件名称，格式：组名@@@@插件名
	 * **/
	private String getPluginName(Plugin plugin){
		return String.format("%s@@@@%s", plugin.getGroupName(), plugin.getName());
	}
	/**
	 * 获取插件名称，格式：组名@@@@插件名
	 * **/
	private String getPluginName(PluginConfig pluginConfig, String pluginName){
		return String.format("%s@@@@%s", pluginConfig.getGroupName(), pluginName);
	}
	
	@Transactional
	@Override
	public List<String> findPluginGroupNameList() {
		return pluginRepository.findPluginGroupNameList();
	}

	@Transactional
	@Override
	public List<Plugin> findPluginList(String groupName) {
		Plugin plugin = new Plugin();
		if(StringUtils.hasLength(groupName)){
			plugin.setGroupName(groupName);
		}

		return  findPluginList(plugin, new Sort(Direction.ASC, "groupName", "name"));
	}

	private String formatArray(Object obj) {
		byte[] bytes = (byte[]) obj;
		StringBuilder sb = new StringBuilder();
		for(byte b : bytes) {
			if(sb.length() > 0) {
				sb.append(",");
			}
			sb.append(b);
		}
		return sb.toString();
	}

	private boolean isJar(String path) {
		InputStream is = null;
		byte[] buffer = new byte[JAR_MAGIC.length];
		try {
			is = new FileInputStream(path);
			is.read(buffer, 0, JAR_MAGIC.length);
			if (Arrays.equals(buffer, JAR_MAGIC)) {
				logger.debug("Found JAR: [{}]", path);
				return true;
			}
		} catch (Exception e) {
			// Failure to read the stream means this is not a JAR
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
		return false;
	}

	/**
	 * 读取插件生成器二进制内容
	 **/
	private String readGeneratorSourceContent(String jarPath, String name) {
		// 判断是否是jar文件
		if (!isJar(jarPath)) {
			throw new PluginLoadException("插件加载失败，[" + jarPath + "]文件读取失败");
		}

		InputStream inputStream = null;
		JarInputStream jarInputStream = null;
		JarFile jarFile = null;
		String source = null;
		try {
			jarFile = new JarFile(jarPath);

			logger.debug("path:[{}]", jarPath);

			String temp = name.replaceAll("\\.", "/") + ".java";
			jarInputStream = new JarInputStream(new FileInputStream(jarPath));
			JarEntry jarEntry = jarInputStream.getNextJarEntry();
			while (jarEntry != null) {
				if (!jarEntry.isDirectory() && jarEntry.getName().equals(temp)) {
					break;
				}
				jarEntry = jarInputStream.getNextJarEntry();
			}

			logger.debug("name:[{}],  jarEntry:[{}]", name, jarEntry);

			if (jarEntry != null) {
				inputStream = jarFile.getInputStream(jarEntry);
				source = StreamUtils.copyToString(inputStream,Charset.forName("UTF-8"));
			}

		} catch (MalformedURLException e) {
			throw new BusinessException(e);
		} catch (IOException e) {
			throw new BusinessException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jarInputStream != null) {
				try {
					jarInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return source;
	}
	
	/**
	 * 读取插件生成器二进制内容
	 **/
	private byte[] readGeneratorContent(String jarPath, String name) {
		// 判断是否是jar文件
		if (!isJar(jarPath)) {
			throw new PluginLoadException("插件加载失败，[" + jarPath + "]文件读取失败");
		}

		InputStream inputStream = null;
		JarInputStream jarInputStream = null;
		JarFile jarFile = null;
		byte[] bytes = null;
		try {
			jarFile = new JarFile(jarPath);

			logger.debug("path:[{}]", jarPath);

			String temp = name.replaceAll("\\.", "/") + ".class";
			jarInputStream = new JarInputStream(new FileInputStream(jarPath));
			JarEntry jarEntry = jarInputStream.getNextJarEntry();
			while (jarEntry != null) {
				if (!jarEntry.isDirectory() && jarEntry.getName().equals(temp)) {
					break;
				}
				jarEntry = jarInputStream.getNextJarEntry();
			}

			logger.debug("name:[{}],  jarEntry:[{}]", name, jarEntry);

			if (jarEntry != null) {
				inputStream = jarFile.getInputStream(jarEntry);
				bytes = StreamUtils.copyToByteArray(inputStream);
			}

		} catch (MalformedURLException e) {
			throw new BusinessException(e);
		} catch (IOException e) {
			throw new BusinessException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jarInputStream != null) {
				try {
					jarInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bytes;
	}

	private void coverNewPluginValue(Plugin newPlugin, Plugin oldPlugin) {
		Field[] fields = newPlugin.getClass().getDeclaredFields();
		for (Field field : fields) {
			Object value = getFieldValue(newPlugin, field);
			if (value != null) {
				setFieldValue(oldPlugin, field, value);
			}
		}
	}

	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public int disablePlugin(String id) {
		return disablePlugins(Lists.newArrayList(id));
	}

	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public int disablePlugins(List<String> ids) {
		List<Plugin> plugins = pluginRepository.findAllById(ids);
		//要禁用的插件列表
		List<String> disablePluginNames = plugins.stream().map(Plugin::getName).collect(Collectors.toList());
		
		plugins.stream().forEach(plugin -> {
			// 校验插件是否是可用状态
			if (plugin.getStatus() == IntConstants.STATUS_DISABLE.getCode()) {
				throw new BusinessException("插件“" + plugin.getName() + "”已经是禁用状态");
			}
			
			//如果该插件被其他插件依赖则需要先禁用依赖该插件的插件,否则报错
			
			//获取所有依赖该插件的插件列表
			List<Plugin> pluginList = pluginRepository.findAll(new Specification<Plugin>() {
				private static final long serialVersionUID = 1L;
				@Override
				public Predicate toPredicate(Root<Plugin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					List<Predicate> list = new ArrayList<Predicate>();
					list.add(cb.like((root.get("dependencies").as(String.class)), String.format("%%%s%%", plugin.getName())));
					list.add(cb.equal(root.get("groupName").as(String.class), plugin.getGroupName()));
					
					Predicate[] p = new Predicate[list.size()];
					query.where(cb.and(list.toArray(p)));
					
					return query.getRestriction();
				}
			});
			
			if(pluginList != null && !pluginList.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for(Plugin p : pluginList) {
					if(p.getStatus() == IntConstants.STATUS_ENABLE.getCode()) {
						//如果插件在需要禁用的插件列表则跳过
						if(disablePluginNames.contains(p.getName())) {
							continue;
						}
						if(sb.length() > 0) {
							sb.append("、");
						}
						sb.append(p.getName());
					}
				}
				if(sb.length() > 0) {
					throw new BusinessException("插件“"+plugin.getName()+"”被以下插件依赖，请先禁用“" + plugin.getGroupName() + "”组中的以下插件，插件列表：" + sb.toString());
				}
			}
			
			//如果已经有用户在使用该插件则需要提示已经有用户在使用插件，并提示用户名
			UserPlugin up = new UserPlugin();
			up.setName(plugin.getName());
			//所有用到该插件的用户
			List<UserPlugin> userPlugins = userPluginService.findList(up);
			if(!userPlugins.isEmpty()) {
				List<String> usernameList = new ArrayList<>(userPlugins.size());
				StringBuilder sb = new StringBuilder();
				for(UserPlugin userPlugin : userPlugins) {
					String username = userPlugin.getUser().getUsername();
					if(!usernameList.contains(username)) {
						usernameList.add(username);
						if(sb.length() > 0) {
							sb.append("、");
						}
						sb.append(username);
					}
				}
				
				throw new BusinessException("插件“"+plugin.getName()+"”已经被使用，请先解除该插件和以下用户的关系：" + sb.toString());
			}
		});
		
		// 更新插件状态
		pluginRepository.updateStatusByIds(IntConstants.STATUS_DISABLE.getCode(), ids);

		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		// 登记变更明细
		for (String id : ids) {
			// 变更记录
			PluginChangeHistory changeHistory = new PluginChangeHistory();
			changeHistory.setPluginId(id);
			changeHistory.setUpdateUserId(user.getId());
			changeHistory.setUpdateUserName(user.getName());
			changeHistory.setUpdateDate(DateUtil.currentDate());

			// 变更明细
			PluginChangeHistoryDetail changeHistoryDetail = new PluginChangeHistoryDetail();
			changeHistoryDetail.setChangeFieldName("status");
			changeHistoryDetail.setChangeFieldComment(getPluginFieldComment(changeHistoryDetail.getChangeFieldName()));
			changeHistoryDetail.setChangeBefore(String.valueOf(IntConstants.STATUS_ENABLE.getCode()));
			changeHistoryDetail.setChangeAfter(String.valueOf(IntConstants.STATUS_DISABLE.getCode()));

			changeHistory.addChangeHistoryDetail(changeHistoryDetail);

			//登记变更记录
			updateChangeHistory(changeHistory);
		}

		return ids.size();
	}
	
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public int enablePlugin(String id) {
		return enablePlugins(Lists.newArrayList(id));
	}

	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public int enablePlugins(List<String> ids) {
		List<Plugin> plugins = pluginRepository.findAllById(ids);
		
		List<String> enablePluginNames = plugins.stream().map(Plugin::getName).collect(Collectors.toList());
		
		//如果该插件依赖其他插件则需要先启用该插件所依赖的插件
		plugins.stream().forEach(plugin -> {
			// 校验插件是否是可用状态
			if (plugin.getStatus() == IntConstants.STATUS_ENABLE.getCode()) {
				throw new BusinessException("插件“" + plugin.getName() + "”已经是可用状态");
			}
			
			if(StringUtils.hasLength(plugin.getDependencies())) {
				String[] dependencies = plugin.getDependencies().split(",");
				//所有依赖的插件且不在当次启用插件列表的插件
				List<String> dependencyPluginNames = new ArrayList<>(dependencies.length);
				for(String dependency : dependencies) {
					if(StringUtils.isEmpty(dependency) || enablePluginNames.contains(dependency)) {
						continue;
					}
					dependencyPluginNames.add(dependency);
				}
				
				if(!dependencyPluginNames.isEmpty()) {
					List<Plugin> pluginList = pluginRepository.findAll(new Specification<Plugin>() {
						private static final long serialVersionUID = 1L;
						@Override
						public Predicate toPredicate(Root<Plugin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
							List<Predicate> list = new ArrayList<Predicate>();
							
							for(String dependencyPluginName : dependencyPluginNames) {
								list.add(cb.equal((root.get("name").as(String.class)), String.format("%s", dependencyPluginName)));
							}
							
							Predicate[] p = new Predicate[list.size()];
							query.where(cb.or(list.toArray(p)));
							
							return query.getRestriction();
						}
					});
					
					StringBuilder sb = new StringBuilder();
					for(Plugin p : pluginList) {
						if(p.getStatus() == IntConstants.STATUS_DISABLE.getCode()) {
							if(sb.length() > 0) {
								sb.append("、");
							}
							sb.append(p.getName());
						}
					}
					if(sb.length() > 0) {
						throw new BusinessException("插件“" + plugin.getName() + "”启用失败，因为所依赖的插件是禁用状态，请先启用“" + plugin.getGroupName() + "”组中的以下插件：" + sb.toString());
					}
				}
			}
			
		});
		
		// 更新插件状态
		pluginRepository.updateStatusByIds(IntConstants.STATUS_ENABLE.getCode(), ids);

		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		// 登记变更明细
		for (String id : ids) {
			// 变更记录
			PluginChangeHistory changeHistory = new PluginChangeHistory();
			changeHistory.setPluginId(id);
			changeHistory.setUpdateUserId(user.getId());
			changeHistory.setUpdateUserName(user.getName());
			changeHistory.setUpdateDate(DateUtil.currentDate());

			// 变更明细
			PluginChangeHistoryDetail changeHistoryDetail = new PluginChangeHistoryDetail();
			changeHistoryDetail.setChangeFieldName("status");
			changeHistoryDetail.setChangeFieldComment(getPluginFieldComment(changeHistoryDetail.getChangeFieldName()));
			changeHistoryDetail.setChangeBefore(String.valueOf(IntConstants.STATUS_DISABLE.getCode()));
			changeHistoryDetail.setChangeAfter(String.valueOf(IntConstants.STATUS_ENABLE.getCode()));

			changeHistory.addChangeHistoryDetail(changeHistoryDetail);

			//登记变更记录
			updateChangeHistory(changeHistory);
		}

		return ids.size();
	}
	@Transactional
	@Override
	public Plugin getPluginByName(String pluginGroupName, String pluginName) {
		List<Plugin> plugins = findPluginByNames(pluginGroupName, Lists.newArrayList(pluginName));
		if(plugins.size() > 1) {
			throw new BusinessException("根据插件名称“"+pluginName+"”查询到多个插件，请排查原因");
		}
		return plugins.size() > 0 ? plugins.get(0) : null;
	}

	/**
	 * 登记变更明细
	 **/
	private void updateChangeHistory(PluginChangeHistory changeHistory) {
		if (changeHistory != null) {
			pluginChangeHistoryService.insert(changeHistory);
			
			changeHistory.getChangeHistoryDetailList().stream().forEach(pluginChangeHistoryDetail -> {
				pluginChangeHistoryDetail.setChangeHistoryId(changeHistory.getId());
			});
			
			pluginChangeHistoryDetailService.insert(changeHistory.getChangeHistoryDetailList());
		}
	}

	private boolean isSame(Object value1, Object value2) {
		// 如果value1和value2全为null则返回true
		if (value1 == value2) {
			return true;
		}
		// value1或value2有一方不为空
		if (null == value1 || null == value2) {
			return false;
		}
		
		return value1.equals(value2);
	}

	/**
	 * 获取字段的值
	 **/
	private Object getFieldValue(Object obj, Field field) {
		Class<?> clazz = obj.getClass();
		String fieldName = field.getName();
		String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		try {
			Method method = clazz.getMethod(getMethodName);
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			return method.invoke(obj);
		} catch (NoSuchMethodException e) {
			logger.debug("未获取到方法名字:[{}]", getMethodName, e);
		} catch (SecurityException e) {
			logger.debug("未获取到方法名字:[{}]", getMethodName, e);
		} catch (IllegalAccessException e) {
			logger.debug("方法访问失败:[{}]", getMethodName, e);
		} catch (IllegalArgumentException e) {
			logger.debug("方法访问失败:[{}]", getMethodName, e);
		} catch (InvocationTargetException e) {
			logger.debug("方法访问失败:[{}]", getMethodName, e);
		}
		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			logger.debug("字段访问失败:[{}]", field.getName(), e);
		} catch (IllegalAccessException e) {
			logger.debug("字段访问失败:[{}]", field.getName(), e);
		}
		return null;
	}

	/**
	 * 设置字段的值
	 **/
	private void setFieldValue(Object obj, Field field, Object value) {
		if (value == null) {
			return;
		}
		Class<?> clazz = obj.getClass();
		String fieldName = field.getName();
		String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		try {
			Method method = clazz.getMethod(setMethodName, value.getClass());
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			method.invoke(obj, value);
		} catch (NoSuchMethodException e) {
			logger.debug("未获取到方法名字:[{}]", setMethodName, e);
		} catch (SecurityException e) {
			logger.debug("未获取到方法名字:[{}]", setMethodName, e);
		} catch (IllegalAccessException e) {
			logger.debug("方法访问失败:[{}]", setMethodName, e);
		} catch (IllegalArgumentException e) {
			logger.debug("方法访问失败:[{}]", setMethodName, e);
		} catch (InvocationTargetException e) {
			logger.debug("方法访问失败:[{}]", setMethodName, e);
		}
		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			logger.debug("字段访问失败:[{}]", field.getName(), e);
		} catch (IllegalAccessException e) {
			logger.debug("字段访问失败:[{}]", field.getName(), e);
		}
	}

	private String getPluginFieldComment(String fieldName) {
		try {
			Field field = Plugin.class.getDeclaredField(fieldName);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			return field.getAnnotation(ChangeEntityComment.class).value();
		} catch (NoSuchFieldException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(e);
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(e);
		}
	}

	/**
	 * 读取模板文件内容
	 **/
	@SuppressWarnings("resource")
	private String readTemplateContent(PluginConfig pluginConfig) {
		// 模板地址
		String templatePath = pluginConfig.getTemplatePath();

		if(!StringUtils.hasLength(templatePath)){
			throw new BusinessException("模板地址不能为空");
		}

		// 插件地址
		String pluginPath = pluginConfig.getPluginPath();

		InputStream templateStream = null;
		try {
			JarFile jarFile = new JarFile(pluginPath);
			java.util.zip.ZipEntry zipEntry = jarFile.getEntry(templatePath);
			templateStream = jarFile.getInputStream(zipEntry);
		} catch (IOException e) {
			logger.error("读取插件模板流失败", e);
		}

		if (templateStream == null) {
			throw new BusinessException("读取插件模板流失败");
		}

		try {
			return new String(StreamUtils.copyToByteArray(templateStream), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("读取插件模板流失败", e);
			throw new BusinessException("读取插件模板流失败");
		} catch (IOException e) {
			logger.error("读取插件模板流失败", e);
			throw new BusinessException("读取插件模板流失败");
		}
	}

	@Transactional
	@Override
	public List<Plugin> findPluginList(Plugin plugin, Sort sort) {
		plugin = plugin == null ? new Plugin() : plugin;
		if(!StringUtils.hasLength(plugin.getGroupName())){
            plugin.setGroupName(null);
        }
		sort = sort == null ? new Sort(Direction.ASC, "groupName", "name") : sort;
		return plugin == null ? pluginRepository.findAll(sort) : pluginRepository.findAll(Example.of(plugin), sort);
	}

	@Transactional
	@Override
	public List<Plugin> findPluginByNames(String pluginGroupName, List<String> pluginNames) {
		return pluginRepository.findByNameIn(pluginGroupName, pluginNames);
	}

	/**
	 * 必须加锁,保证线程安全
	 **/
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public synchronized boolean uploadPlugin(MultipartFile file) {
		String tempFilePath = randomPath();
		FileOutputStream fileOutputStream = null;
		PluginConfig pluginConfig = null;
		try {
			// 文件不能为空
			if (file.getSize() == 0) {
				throw new PluginNotFoundException("插件信息为空");
			}
			byte[] bytes = file.getBytes();

			// 先暂存jar文件到插件目录,文件名为随机,尝试加载插件,加载失败则直接删除上传的文件
			fileOutputStream = new FileOutputStream(tempFilePath);
			fileOutputStream.write(bytes);
			fileOutputStream.flush();
			fileOutputStream.close();
			fileOutputStream = null;

			String pluginDir = System.getProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME);

			PluginConfigParser pluginConfigParser = new PluginConfigParser(tempFilePath, PluginConfig.PLUGIN_FILE_NAME,
					true);
			pluginConfig = pluginConfigParser.parse();

			String pluginGroupName = pluginConfig.getGroupName();
			String pluginName = pluginConfig.getName();
			// 获取插件名称,因为新上传的插件jar包文件名可能和之前的不一致,所以需要根据插件名判断
			PluginConfig oldPluginConfig = pluginLoader.getPluginConfig(pluginGroupName, pluginName);
			// 存在所上传的插件
			if (oldPluginConfig != null) {
				// 移动原文件到历史目录,文件名加上时间标记及序列号(1,2,3,4,5)
				File historyDirectory = new File(
						String.format("%s/../history_plugin/%s/%s", pluginDir, oldPluginConfig.getGroupName(), oldPluginConfig.getName()));
				if (!historyDirectory.exists()) {
					historyDirectory.mkdirs();
				}

				File oldPlugin = new File(oldPluginConfig.getPluginPath());

				// 获取文件历史插件文件路径
				String nextHistoryPluginPath = nextHistoryPluginPath(oldPlugin, historyDirectory);

				FileUtils.copyFile(oldPlugin, new File(nextHistoryPluginPath));
			}
			
			//必须加上插件名称前缀,避免插件名不一致,文件名一致导致插件被覆盖问题
			File targetFile = new File(String.format("%s/%s/%s_%s", pluginDir, pluginConfig.getGroupName(), pluginConfig.getName() ,file.getOriginalFilename()));

			// 迁移临时目录中的插件到插件目录
			FileUtils.copyFile(new File(tempFilePath), targetFile);

			// 在windows下线程执行完毕才会执行复制操作,所以需要异步实行
			if (OS.getOs() == OS.WINDOWS) {
				ThreadFactory threadFactory = new ThreadFactoryBuilder().build();
				ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
						new SynchronousQueue<Runnable>(), threadFactory);
				Future<PluginConfig> future = executor.submit(new Callable<PluginConfig>() {
					@Override
					public PluginConfig call() throws Exception {
						PluginConfig pluginConfig = pluginLoader.loadPluginConfig(targetFile.getAbsolutePath());
						return pluginConfig;
					}
				});

				try {
					pluginConfig = future.get();
				} catch (InterruptedException e) {
					throw new PluginLoadException("插件加载失败，[" + file.getOriginalFilename() + "]文件读取失败", e);
				} catch (ExecutionException e) {
					throw new PluginLoadException("插件加载失败，[" + file.getOriginalFilename() + "]文件读取失败", e);
				}
			} else {
				// linux
				pluginConfig = pluginLoader.loadPluginConfig(targetFile.getAbsolutePath());
			}

			pluginLoader.addPlugin(pluginConfig);
			// 修改临时文件名为原文件名
		} catch (IOException e) {
			throw new PluginLoadException("插件加载失败，[" + file.getOriginalFilename() + "]文件读取失败", e);
		} catch (PluginLoadException e) {
			throw new PluginLoadException("插件加载失败，[" + file.getOriginalFilename() + "]文件读取失败", e);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			new File(tempFilePath).delete();

		}
		
		//更新数据库中的插件信息
		Plugin oldPlugin = getPluginByName(pluginConfig.getGroupName(), pluginConfig.getName());
		
		SecurityContext securityContext = SecurityContextHolder.getContext();
		
		Authentication authentication = securityContext.getAuthentication();
		
		User user = null;
		//如果用户未登录则获取管理员用户

		logger.debug("用户：[{}]", authentication);

		if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			user = userService.findAdmin();
		}else {
			user = (User) authentication.getPrincipal();
		}
		
		//如果oldPlugin为空则认为是新增插件
		if(oldPlugin == null) {
			Plugin plugin = new Plugin();
			plugin.setSystemVersion(versionService.findVersion().getVersion());
			plugin.setUpdateDate(DateUtil.currentDate());
			plugin.setGroupName(pluginConfig.getGroupName());
			plugin.setName(pluginConfig.getName());
			plugin.setDescription(pluginConfig.getDescription());
			plugin.setTemplatePath(pluginConfig.getTemplatePath());
			plugin.setGenerator(pluginConfig.getGenerator());
			plugin.setFileRelativeDir(pluginConfig.getFileRelativeDir());
			plugin.setFileSuffix(pluginConfig.getFileSuffix());
			plugin.setPrefix(pluginConfig.getPrefix());
			plugin.setSuffix(pluginConfig.getSuffix());
			plugin.setPluginPath(pluginConfig.getPluginPath());
			plugin.setDependencies(pluginConfig.getDependencies());

			// 读取模板文件内容
			plugin.setTemplateContent(readTemplateContent(pluginConfig));
			plugin.setGeneratorSourceContent(readGeneratorSourceContent(pluginConfig.getPluginPath(), pluginConfig.getGenerator()));
			plugin.setGeneratorContent(formatArray(readGeneratorContent(pluginConfig.getPluginPath(), pluginConfig.getGenerator())));

			// 插件在数据库不存在
			plugin.setCreateDate(DateUtil.currentDate());
			plugin.setCreateUserId(user.getId());
			plugin.setCreateUserName(user.getUsername());
			plugin.setUpdateUserId(user.getId());
			plugin.setUpdateUserName(user.getUsername());

			plugin.setStatus(IntConstants.STATUS_ENABLE.getCode());

			pluginRepository.saveAndFlush(plugin);
			
		}else {
			Plugin plugin = new Plugin();
			plugin.setSystemVersion(versionService.findVersion().getVersion());
			plugin.setUpdateDate(DateUtil.currentDate());
			plugin.setGroupName(pluginConfig.getGroupName());
			plugin.setName(pluginConfig.getName());
			plugin.setDescription(pluginConfig.getDescription());
			plugin.setTemplatePath(pluginConfig.getTemplatePath());
			plugin.setGenerator(pluginConfig.getGenerator());
			plugin.setFileRelativeDir(pluginConfig.getFileRelativeDir());
			plugin.setFileSuffix(pluginConfig.getFileSuffix());
			plugin.setPrefix(pluginConfig.getPrefix());
			plugin.setSuffix(pluginConfig.getSuffix());
			plugin.setPluginPath(pluginConfig.getPluginPath());
			plugin.setStatus(oldPlugin.getStatus());
			plugin.setDependencies(pluginConfig.getDependencies());
			
			// 读取模板文件内容
			plugin.setTemplateContent(readTemplateContent(pluginConfig));
			plugin.setGeneratorSourceContent(readGeneratorSourceContent(pluginConfig.getPluginPath(), pluginConfig.getGenerator()));
			plugin.setGeneratorContent(formatArray(readGeneratorContent(pluginConfig.getPluginPath(), pluginConfig.getGenerator())));
	
			
			// 比对原来存在的插件信息和插件目录下的插件信息,如果不一致则需要更新插件信息
			List<PluginChangeHistoryDetail> changeHistoryDetailList = new ArrayList<>();
	
			Field[] fields = Plugin.class.getDeclaredFields();
			for (Field field : fields) {
				Object oldValue = getFieldValue(oldPlugin, field);
				Object newValue = getFieldValue(plugin, field);
	
				// 前后结果不一致
				if (!isSame(oldValue, newValue)) {
					if (!field.isAccessible()) {
						field.setAccessible(true);
					}
					// 只对比带ChangeEntityComment注解的字段
					ChangeEntityComment comment = field.getAnnotation(ChangeEntityComment.class);
					if (comment != null) {
						PluginChangeHistoryDetail changeHistoryDetail = new PluginChangeHistoryDetail();
						changeHistoryDetail.setChangeFieldName(field.getName());
						changeHistoryDetail.setChangeFieldComment(comment.value());
						changeHistoryDetail.setChangeBefore(oldValue == null ? null : String.valueOf(oldValue));
						changeHistoryDetail.setChangeAfter(newValue == null ? null : String.valueOf(newValue));
	
						changeHistoryDetailList.add(changeHistoryDetail);
					}
				}
			}
	
			if (changeHistoryDetailList.size() > 0) {
				// 覆盖之前的插件的信息
				coverNewPluginValue(plugin, oldPlugin);
	
				// 变更记录
				PluginChangeHistory changeHistory = new PluginChangeHistory();
				changeHistory.setPluginId(oldPlugin.getId());
				changeHistory.setUpdateUserId(user.getId());
				changeHistory.setUpdateUserName(user.getName());
				changeHistory.setUpdateDate(DateUtil.currentDate());
				changeHistory.setChangeHistoryDetailList(changeHistoryDetailList);
	
				oldPlugin.setChangeHistory(changeHistory);
			}
			
			//因为更新了之前的插件信息,所以直接更新数据库之前插件即可
			pluginRepository.saveAndFlush(oldPlugin);
			// 更新变更明细
			updateChangeHistory(oldPlugin.getChangeHistory());
			
		}
		
		return true;
	}

	
	private String nextHistoryPluginPath(File oldPlugin, File historyDirectory) {
		String oldPluginFileName = oldPlugin.getName();
		String oldPluginFileNamePrefix = String.format("%s_%s_",
				oldPluginFileName.substring(0, oldPluginFileName.lastIndexOf(".")),
				new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S").format(new Date()));

		// 获取下一个文件名
		File[] historyPluginFiles = historyDirectory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(oldPluginFileNamePrefix);
			}
		});

		String historyDirectoryPath = historyDirectory.getAbsolutePath();
		historyDirectoryPath = historyDirectoryPath.replaceAll("\\\\", "/");
		historyDirectoryPath = historyDirectoryPath.endsWith("/") ? historyDirectoryPath : historyDirectoryPath + "/";

		if (historyPluginFiles.length == 0) {
			return String.format("%s%s0.jar", historyDirectoryPath, oldPluginFileNamePrefix);
		}

		// 最后修改文件,注:如果时间相同则取序列号最大的
		File lastModifyFile = historyPluginFiles[0];
		long lastModifyDate = lastModifyFile.lastModified();
		for (File file : historyPluginFiles) {
			// 比较文件最后修改时间
			if (file.lastModified() > lastModifyDate) {
				// 当前文件最后修改时间大于上一个文件最后修改时间,直接取当前文件
				lastModifyFile = file;
				lastModifyDate = lastModifyFile.lastModified();
			} else if (file.lastModified() == lastModifyDate) {
				// 当前文件最后修改时间等于上一个文件最后修改时间,比较两个文件的序列号
				String preFileNameSequence = lastModifyFile.getName().substring(oldPluginFileNamePrefix.length(),
						lastModifyFile.getName().lastIndexOf("."));
				String currentFileNameSequence = file.getName().substring(oldPluginFileNamePrefix.length(),
						file.getName().lastIndexOf("."));
				// 转换成int类型进行比较
				// 如果上一个文件的序列号小于当前文件序列号则取当前文件作为最后修改文件
				if (Integer.valueOf(preFileNameSequence) < Integer.valueOf(currentFileNameSequence)) {
					lastModifyFile = file;
					lastModifyDate = lastModifyFile.lastModified();
				}
			}
		}

		String fileNameSequence = lastModifyFile.getName().substring(oldPluginFileNamePrefix.length(),
				lastModifyFile.getName().lastIndexOf("."));
		return String.format("%s%s%s.jar", historyDirectoryPath, oldPluginFileNamePrefix, fileNameSequence);
	}

	private String randomPath() {
		String targetPath = System.getProperty("user.home");
		targetPath = targetPath.replaceAll("\\\\", "/");
		targetPath = targetPath.endsWith("/") ? targetPath : targetPath + "/";
		return targetPath + new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S_").format(new Date())
				+ UUID.randomUUID().toString() + ".jar";
	}

	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public void savePluginGeneratorSourceContent(String pluginGroupName, String pluginName, String source) {
		Plugin plugin = getPluginByName(pluginGroupName, pluginName);
		
		//如果源代码和插件之前的源代码不一致则保存
		if(!source.equals(plugin.getGeneratorSourceContent())) {
			//根据源代码生成字节码
			//生成字节码
			String classContent = classService.generateClassContent(source, pluginGroupName, pluginName);
			
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			User user = (User) authentication.getPrincipal();
			
			// 变更记录
			PluginChangeHistory changeHistory = new PluginChangeHistory();
			changeHistory.setPluginId(plugin.getId());
			changeHistory.setUpdateUserId(user.getId());
			changeHistory.setUpdateUserName(user.getName());
			changeHistory.setUpdateDate(DateUtil.currentDate());

			// 变更明细(源代码)
			PluginChangeHistoryDetail changeSource = new PluginChangeHistoryDetail();
			changeSource.setChangeFieldName("generatorSourceContent");
			changeSource.setChangeFieldComment(getPluginFieldComment(changeSource.getChangeFieldName()));
			changeSource.setChangeBefore(plugin.getGeneratorSourceContent());
			changeSource.setChangeAfter(source);

			changeHistory.addChangeHistoryDetail(changeSource);
			
			// 变更明细(字节码)
			PluginChangeHistoryDetail changeClass = new PluginChangeHistoryDetail();
			changeClass.setChangeFieldName("generatorContent");
			changeClass.setChangeFieldComment(getPluginFieldComment(changeClass.getChangeFieldName()));
			changeClass.setChangeBefore(plugin.getGeneratorContent());
			changeClass.setChangeAfter(classContent);

			changeHistory.addChangeHistoryDetail(changeClass);

			//登记变更记录
			updateChangeHistory(changeHistory);
			
			plugin.setGeneratorSourceContent(source);
			plugin.setGeneratorContent(classContent); 
			
			pluginRepository.saveAndFlush(plugin);
		}		
	}

	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public void savePluginGeneratorTemplateContent(String pluginGroupName, String pluginName, String templateContent) {
		Plugin plugin = getPluginByName(pluginGroupName, pluginName);
		
		//如果要保存的模板内容和之前的模板内容不一致则保存
		if(!templateContent.equals(plugin.getTemplateContent())) {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			User user = (User) authentication.getPrincipal();
			
			// 变更记录
			PluginChangeHistory changeHistory = new PluginChangeHistory();
			changeHistory.setPluginId(plugin.getId());
			changeHistory.setUpdateUserId(user.getId());
			changeHistory.setUpdateUserName(user.getName());
			changeHistory.setUpdateDate(DateUtil.currentDate());

			// 变更明细
			PluginChangeHistoryDetail changeHistoryDetail = new PluginChangeHistoryDetail();
			changeHistoryDetail.setChangeFieldName("templateContent");
			changeHistoryDetail.setChangeFieldComment(getPluginFieldComment(changeHistoryDetail.getChangeFieldName()));
			changeHistoryDetail.setChangeBefore(plugin.getTemplateContent());
			changeHistoryDetail.setChangeAfter(templateContent);

			changeHistory.addChangeHistoryDetail(changeHistoryDetail);

			//登记变更记录
			updateChangeHistory(changeHistory);
			
			plugin.setTemplateContent(templateContent);
			
			pluginRepository.saveAndFlush(plugin);
		}
	}

	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public void savePluginConfig(Plugin plugin) {
		Plugin p = getPluginByName(plugin.getGroupName(), plugin.getName());
		
		Plugin oldPlugin = p.clone();
		
		p.setDescription(plugin.getDescription());
		p.setFileRelativeDir(plugin.getFileRelativeDir());
		p.setFileSuffix(plugin.getFileSuffix());
		p.setPrefix(plugin.getPrefix());
		p.setSuffix(plugin.getSuffix());

		pluginRepository.saveAndFlush(p);
		
		//对比差异并登记日志
		
		// 比对原来存在的插件信息和插件目录下的插件信息,如果不一致则需要更新插件信息
		List<PluginChangeHistoryDetail> changeHistoryDetailList = new ArrayList<>();

		Field[] fields = Plugin.class.getDeclaredFields();
		for (Field field : fields) {
			Object oldValue = getFieldValue(oldPlugin, field);
			Object newValue = getFieldValue(p, field);

			// 前后结果不一致
			if (!isSame(oldValue, newValue)) {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				// 只对比带ChangeEntityComment注解的字段
				ChangeEntityComment comment = field.getAnnotation(ChangeEntityComment.class);
				if (comment != null) {
					PluginChangeHistoryDetail changeHistoryDetail = new PluginChangeHistoryDetail();
					changeHistoryDetail.setChangeFieldName(field.getName());
					changeHistoryDetail.setChangeFieldComment(comment.value());
					changeHistoryDetail.setChangeBefore(oldValue == null ? null : String.valueOf(oldValue));
					changeHistoryDetail.setChangeAfter(newValue == null ? null : String.valueOf(newValue));

					changeHistoryDetailList.add(changeHistoryDetail);
				}
			}
		}

		if (changeHistoryDetailList.size() > 0) {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			User user = (User) authentication.getPrincipal();
			
			// 变更记录
			PluginChangeHistory changeHistory = new PluginChangeHistory();
			changeHistory.setPluginId(oldPlugin.getId());
			changeHistory.setUpdateUserId(user.getId());
			changeHistory.setUpdateUserName(user.getName());
			changeHistory.setUpdateDate(DateUtil.currentDate());
			changeHistory.setChangeHistoryDetailList(changeHistoryDetailList);
			
			// 更新变更明细
			updateChangeHistory(changeHistory);
		}
	}

	@Transactional
	@Override
	public String loadPluginSource(String pluginGroupName, String pluginName) {
		Plugin plugin = getPluginByName(pluginGroupName, pluginName);
		
		return plugin.getGeneratorSourceContent().trim();
	}

	@Transactional
	@Override
	public String loadPluginTemplate(String pluginGroupName, String pluginName) {
		Plugin plugin = getPluginByName(pluginGroupName, pluginName);
		
		return plugin.getTemplateContent().trim();
	}

	@Transactional
	@Override
	public Plugin loadPluginConfig(String pluginGroupName, String pluginName) {
		Plugin plugin = getPluginByName(pluginGroupName, pluginName);
		
		return plugin;
	}

	@Transactional
	@Override
	public String loadSystemSource(String pluginGroupName, String pluginName) {
		Plugin plugin = getPluginByName(pluginGroupName, pluginName);
		
		PluginConfig pluginConfig = pluginLoader.loadPluginConfig(plugin.getPluginPath());

		return readGeneratorSourceContent(pluginConfig.getPluginPath(), pluginConfig.getGenerator()).trim();
	}

	@Transactional
	@Override
	public String loadSystemTemplate(String pluginGroupName, String pluginName) {
		Plugin plugin = getPluginByName(pluginGroupName, pluginName);
		
		PluginConfig pluginConfig = pluginLoader.loadPluginConfig(plugin.getPluginPath());

		return readTemplateContent(pluginConfig).trim();
	}

	@Transactional
	@Override
	public PluginConfig loadSystemConfig(String pluginGroupName, String pluginName) {
		Plugin plugin = getPluginByName(pluginGroupName, pluginName);
		
		PluginConfig pluginConfig = pluginLoader.loadPluginConfig(plugin.getPluginPath());
		
		return pluginConfig;
	}

}
