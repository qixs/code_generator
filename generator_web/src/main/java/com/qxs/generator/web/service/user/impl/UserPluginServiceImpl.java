package com.qxs.generator.web.service.user.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.qxs.generator.web.annotation.ChangeEntityComment;
import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.config.SystemParameter;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.UserCustomPluginTemp;
import com.qxs.generator.web.model.user.UserPlugin;
import com.qxs.generator.web.model.user.UserPluginChangeHistory;
import com.qxs.generator.web.model.user.UserPluginChangeHistoryDetail;
import com.qxs.generator.web.repository.user.IUserPluginRepository;
import com.qxs.generator.web.service.IClassService;
import com.qxs.generator.web.service.config.ISystemParameterService;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.generator.web.service.user.IUserCustomPluginTempService;
import com.qxs.generator.web.service.user.IUserPluginChangeHistoryDetailService;
import com.qxs.generator.web.service.user.IUserPluginChangeHistoryService;
import com.qxs.generator.web.service.user.IUserPluginService;
import com.qxs.generator.web.service.user.IUserService;
import com.qxs.generator.web.service.version.IVersionService;
import com.qxs.generator.web.util.DateUtil;

@Service
public class UserPluginServiceImpl implements IUserPluginService {

	private transient final Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 **/
	public static final String CACHE_KEY = "'userPluginCache_'";
	/**
	 * value属性表示使用哪个缓存策略
	 */
	public static final String CACHE_NAME = "userPlugin";
	
	@Autowired
	private IUserPluginRepository userPluginRepository;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private IUserPluginChangeHistoryService userPluginChangeHistoryService;
	@Autowired
	private IUserPluginChangeHistoryDetailService userPluginChangeHistoryDetailService;
	@Autowired
	private IClassService classService;
	@Autowired
	private IUserCustomPluginTempService userCustomPluginTempService;
	@Autowired
	private IVersionService versionService;
	@Autowired
	private ISystemParameterService systemParameterService;

	@Transactional
	@Override
	public UserPlugin getById(String id) {
		return userPluginRepository.getOne(id);
	}

	@Override
	public List<UserPlugin> findList(UserPlugin userPlugin) {
		if(userPlugin == null) {
			userPlugin = new UserPlugin();
		}
		
		return userPluginRepository.findAll(Example.of(userPlugin));
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
		List<UserPlugin> plugins = userPluginRepository.findAllById(ids);
		//要禁用的插件列表
		List<String> disablePluginNames = plugins.stream().map(UserPlugin::getName).collect(Collectors.toList());
		
		plugins.stream().forEach(plugin -> {
			// 校验插件是否是可用状态
			if (plugin.getStatus() == IntConstants.STATUS_DISABLE.getCode()) {
				throw new BusinessException("插件“" + plugin.getName() + "”已经是禁用状态");
			}
			
			//如果该插件被其他插件依赖则需要先禁用依赖该插件的插件,否则报错
			
			//获取所有依赖该插件的插件列表
			List<UserPlugin> pluginList = userPluginRepository.findAll(new Specification<UserPlugin>() {
				private static final long serialVersionUID = 1L;
				@Override
				public Predicate toPredicate(Root<UserPlugin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					List<Predicate> list = new ArrayList<Predicate>();
					list.add(cb.like((root.get("dependencies").as(String.class)), String.format("%%%s%%", plugin.getName())));
					
					Predicate[] p = new Predicate[list.size()];
					query.where(cb.and(list.toArray(p)));
					
					return query.getRestriction();
				}
			});
			
			if(pluginList != null && !pluginList.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for(UserPlugin p : pluginList) {
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
					throw new BusinessException("插件“"+plugin.getName()+"”被以下插件依赖，请先禁用以下插件，插件组：" + plugin.getGroupName() + "，插件列表：" + sb.toString());
				}
			}
		});
		
		// 更新插件状态
		userPluginRepository.updateStatusByIds(IntConstants.STATUS_DISABLE.getCode(), ids);

		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		// 登记变更明细
		ids.stream().forEach(id ->{
			UserPlugin userPlugin = userPluginRepository.getOne(id);
			// 变更记录
			UserPluginChangeHistory changeHistory = new UserPluginChangeHistory();
			changeHistory.setPluginId(id);
			changeHistory.setPluginGroupName(userPlugin.getGroupName());
			changeHistory.setPluginName(userPlugin.getName());
			changeHistory.setPluginDescription(userPlugin.getDescription());
			changeHistory.setPluginDependencies(userPlugin.getDependencies());
			changeHistory.setUserId(user.getId());
			changeHistory.setUpdateDate(DateUtil.currentDate());

			// 变更明细
			UserPluginChangeHistoryDetail changeHistoryDetail = new UserPluginChangeHistoryDetail();
			changeHistoryDetail.setChangeFieldName("status");
			changeHistoryDetail.setChangeFieldComment(getPluginFieldComment(changeHistoryDetail.getChangeFieldName()));
			changeHistoryDetail.setChangeBefore(String.valueOf(IntConstants.STATUS_ENABLE.getCode()));
			changeHistoryDetail.setChangeAfter(String.valueOf(IntConstants.STATUS_DISABLE.getCode()));

			changeHistory.addChangeHistoryDetail(changeHistoryDetail);

			//登记变更记录
			updateChangeHistory(changeHistory);
		});

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
		List<UserPlugin> plugins = userPluginRepository.findAllById(ids);
		
		List<String> enablePluginNames = plugins.stream().map(UserPlugin::getName).collect(Collectors.toList());
		
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
					List<UserPlugin> pluginList = userPluginRepository.findAll(new Specification<UserPlugin>() {
						private static final long serialVersionUID = 1L;
						@Override
						public Predicate toPredicate(Root<UserPlugin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
					for(UserPlugin p : pluginList) {
						if(p.getStatus() == IntConstants.STATUS_DISABLE.getCode()) {
							if(sb.length() > 0) {
								sb.append("、");
							}
							sb.append(p.getName());
						}
					}
					if(sb.length() > 0) {
						throw new BusinessException("插件“" + plugin.getName() + "”启用失败，因为所依赖的插件是禁用状态，请先启用以下插件：" + sb.toString() + "  插件组：" + plugin.getGroupName());
					}
				}
			}
			
		});
		
		// 更新插件状态
		userPluginRepository.updateStatusByIds(IntConstants.STATUS_ENABLE.getCode(), ids);

		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		//登记变更明细
		ids.stream().forEach(id -> {
			UserPlugin userPlugin = userPluginRepository.getOne(id);
			// 变更记录
			UserPluginChangeHistory changeHistory = new UserPluginChangeHistory();
			changeHistory.setPluginId(id);
			changeHistory.setPluginGroupName(userPlugin.getGroupName());
			changeHistory.setPluginName(userPlugin.getName());
			changeHistory.setPluginDescription(userPlugin.getDescription());
			changeHistory.setPluginDependencies(userPlugin.getDependencies());
			changeHistory.setUserId(user.getId());
			changeHistory.setUpdateDate(DateUtil.currentDate());

			// 变更明细
			UserPluginChangeHistoryDetail changeHistoryDetail = new UserPluginChangeHistoryDetail();
			changeHistoryDetail.setChangeFieldName("status");
			changeHistoryDetail.setChangeFieldComment(getPluginFieldComment(changeHistoryDetail.getChangeFieldName()));
			changeHistoryDetail.setChangeBefore(String.valueOf(IntConstants.STATUS_DISABLE.getCode()));
			changeHistoryDetail.setChangeAfter(String.valueOf(IntConstants.STATUS_ENABLE.getCode()));

			changeHistory.addChangeHistoryDetail(changeHistoryDetail);

			//登记变更记录
			updateChangeHistory(changeHistory);
		});
		
		return ids.size();
	}
	
	/**
	 * 登记变更明细
	 **/
	private void updateChangeHistory(UserPluginChangeHistory changeHistory) {
		if (changeHistory != null) {
			userPluginChangeHistoryService.insert(changeHistory);
			
			changeHistory.getChangeHistoryDetailList().stream().forEach(pluginChangeHistoryDetail -> {
				pluginChangeHistoryDetail.setChangeHistoryId(changeHistory.getId());
			});
				
			userPluginChangeHistoryDetailService.insert(changeHistory.getChangeHistoryDetailList());
		}
	}
	
	private String getPluginFieldComment(String fieldName) {
		try {
			Field field = Plugin.class.getDeclaredField(fieldName);
			if(!field.isAccessible()) {
				field.setAccessible(true);
			}
			return field.getAnnotation(ChangeEntityComment.class).value();
		} catch (NoSuchFieldException e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<UserPlugin> findUserPluginList(UserPlugin userPlugin, Sort sort) {
		if(userPlugin == null) {
			userPlugin = new UserPlugin();
		}
		
		//如果是超级用户则不根据用户进行过滤,直接查询所有可用的插件,否则查询该用户名下插件
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		List<UserPlugin> userPlugins = null;
		if(user.getAdmin() == User.ADMIN_STATUS_IS_ADMIN) {
			//超级用户查询所有可用插件
			Plugin plugin = new Plugin();
			plugin.setStatus(userPlugin.getStatus());
			List<Plugin> plugins = pluginService.findPluginList(plugin, null);
			userPlugins = new ArrayList<>(plugins.size());
			for(Plugin p : plugins) {
				UserPlugin up = new UserPlugin();
				up.setGroupName(p.getGroupName());
				up.setName(p.getName());
				up.setDescription(p.getDescription());
				up.setTemplatePath(p.getTemplatePath());
				up.setTemplateContent(p.getTemplateContent());
				up.setGenerator(p.getGenerator());
				up.setGeneratorSourceContent(p.getGeneratorSourceContent());
				up.setGeneratorContent(p.getGeneratorContent());
				up.setFileRelativeDir(p.getFileRelativeDir());
				up.setFileSuffix(p.getFileSuffix());
				up.setPrefix(p.getPrefix());
				up.setSuffix(p.getSuffix());
				up.setPluginPath(p.getPluginPath());
				up.setDependencies(p.getDependencies());
				up.setStatus(p.getStatus());
				
				userPlugins.add(up);
			}
		}else {
			userPlugin.setUser(userService.findById(user.getId()));
			userPlugins = userPluginRepository.findAll(Example.of(userPlugin), sort == null ? Sort.by(Direction.ASC, "groupName", "name") : sort);
		}
		
		return userPlugins;
	}

	@Transactional
	@Override
	public void deleteUserPlugins(List<UserPlugin> userPlugins) {
		userPluginRepository.deleteAll(userPlugins);
	}

	@Transactional
	@Override
	public UserPlugin getPluginByName(String groupName, String pluginName) {
		UserPlugin userPlugin = new UserPlugin();
		userPlugin.setGroupName(groupName);
		userPlugin.setName(pluginName);
		userPlugin.setUserId(getUserId());
		
		return userPluginRepository.findOne(Example.of(userPlugin)).orElse(null);
	}

	@Transactional
	@Override
	public List<UserPlugin> findUserAllocationPluginList(String userId) {
		//获取所有的可用状态的插件
		Plugin p = new Plugin();
		p.setStatus(IntConstants.STATUS_ENABLE.getCode());
		List<Plugin> plugins = pluginService.findPluginList(p, null);
		
		//获取该用户下所有已经分配的插件
		UserPlugin up = new UserPlugin();
		up.setUserId(userId);
		List<String> userPluginNameList = userPluginRepository.findAll(Example.of(up))
				.stream().map(UserPlugin::getName).collect(Collectors.toList());
		
		//组装成UserPlugin
		List<UserPlugin> userPlugins = new ArrayList<>(plugins.size());
		plugins.stream().forEach(plugin -> {
			UserPlugin userPlugin = new UserPlugin();
			userPlugin.setName(plugin.getName());
			userPlugin.setDescription(plugin.getDescription());
			userPlugin.setDependencies(plugin.getDependencies());
			userPlugin.setTemplatePath(plugin.getTemplatePath());
			userPlugin.setGenerator(plugin.getGenerator());
			userPlugin.setFileRelativeDir(plugin.getFileRelativeDir());
			userPlugin.setPluginPath(plugin.getPluginPath());
			userPlugin.setPrefix(plugin.getPrefix());
			userPlugin.setSuffix(plugin.getSuffix());
			userPlugin.setFileSuffix(plugin.getFileSuffix());
			
			if(userPluginNameList.contains(plugin.getName())) {
				//设置状态
				userPlugin.setAllocationStatus(1);
			}
			userPlugins.add(userPlugin);
		});
		
		return userPlugins;
	}

	@Transactional
	@Override
	public void allocation(String userId, String groupName, String pluginName) {
		//如果用户是当前登录用户则不允许分配插件
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		if(user.getId().equals(userId)) {
			throw new BusinessException("不能为当前登录用户分配插件");
		}
		
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		Assert.notNull(plugin, "未找到插件“" + pluginName + "”");
		Assert.isTrue(plugin.getStatus() == IntConstants.STATUS_ENABLE.getCode(), "“" + pluginName + "”未启用");
		
		UserPlugin userPlugin = new UserPlugin();
		//用户id
		userPlugin.setUserId(userId);
		//最后一次更新插件版本号
		userPlugin.setSystemVersion(plugin.getSystemVersion());
		//创建时间
		userPlugin.setCreateDate(DateUtil.currentDate());
		//更新时间
		userPlugin.setUpdateDate(DateUtil.currentDate());
		//插件名称
		userPlugin.setName(plugin.getName());
		//插件描述
		userPlugin.setDescription(plugin.getDescription());
		//插件模板文件路径
		userPlugin.setTemplatePath(plugin.getTemplatePath());
		//插件模板内容
		userPlugin.setTemplateContent(plugin.getTemplateContent());
		//插件生成器全路径名
		userPlugin.setGenerator(plugin.getGenerator());
		//插件生成器源码
		userPlugin.setGeneratorSourceContent(plugin.getGeneratorSourceContent());
		//插件生成器内容
		userPlugin.setGeneratorContent(plugin.getGeneratorContent());
		//生成的代码在zip文件中的相对目录(不包括生成的文件名)
		userPlugin.setFileRelativeDir(plugin.getFileRelativeDir());
		//生成的代码文件后缀名
		userPlugin.setFileSuffix(plugin.getFileSuffix());
		//类和文件名前缀
		userPlugin.setPrefix(plugin.getPrefix());
		//类和文件名后缀
		userPlugin.setSuffix(plugin.getSuffix());
		//插件地址
		userPlugin.setPluginPath(plugin.getPluginPath());
		//依赖插件,以英文逗号(,)分割
		userPlugin.setDependencies(plugin.getDependencies());
		//状态
		userPlugin.setStatus(IntConstants.STATUS_ENABLE.getCode());
		//是否是自定义插件
		userPlugin.setCustom(UserPlugin.CUSTOM_STATUS_IS_NOT_CUSTOM);
		
		userPluginRepository.saveAndFlush(userPlugin);

	}

	@Transactional
	@Override
	public void recovery(String userId, String pluginName) {
		UserPlugin userPlugin = new UserPlugin();
		userPlugin.setUserId(userId);
		userPlugin.setName(pluginName);
		//只处理非自定义的插件
		userPlugin.setCustom(UserPlugin.CUSTOM_STATUS_IS_NOT_CUSTOM);
		
		List<UserPlugin> userPlugins = userPluginRepository.findAll(Example.of(userPlugin));
		userPluginRepository.deleteInBatch(userPlugins);
	}

	@Transactional
	@Override
	public void batchInsert(List<UserPlugin> userPlugins) {
		userPluginRepository.saveAll(userPlugins);
	}

	@Transactional
	@Override
	public Page<UserPlugin> findList(String search,Integer offset, Integer limit,String sort, String order) {
		Sort s = Sort.unsorted();
		Pageable pageable = null;
		if(StringUtils.hasLength(sort)) {
			s = new Sort(Direction.fromOptionalString(order).orElse(Direction.ASC), sort);
		}
		if(offset != null) {
			pageable = PageRequest.of(offset / limit, limit, s);
		}
		UserPlugin userPlugin = new UserPlugin();
		userPlugin.setUserId(getUserId());
		
		return StringUtils.isEmpty(search) ? 
				userPluginRepository.findAll(Example.of(userPlugin), pageable) : userPluginRepository.findAll(
						new Specification<UserPlugin>() {
							private static final long serialVersionUID = -7443994505461831568L;
							@Override
							public Predicate toPredicate(Root<UserPlugin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
								List<Predicate> orPredicate = new ArrayList<>();
				                if(StringUtils.hasLength(search)){
									orPredicate.add(cb.like(root.get("groupName").as(String.class), "%"+search+"%"));
				                	orPredicate.add(cb.like(root.get("name").as(String.class), "%"+search+"%"));
				                	orPredicate.add(cb.like(root.get("description").as(String.class), "%"+search+"%"));
				                }
				                
				                List<Predicate> andPredicate = new ArrayList<>();
				                //用户id字段
				                andPredicate.add(cb.equal(root.get("userId").as(String.class), userPlugin.getUserId()));
				                
				                return query.where(cb.and(andPredicate.toArray(new Predicate[andPredicate.size()])),
				                		cb.or(orPredicate.toArray(new Predicate[orPredicate.size()]))).getRestriction();
							}
						}, pageable);
	}
	
	@Transactional
	@Override
	public void delete(String pluginId) {
		UserPlugin userPlugin = userPluginRepository.getOne(pluginId);
		
		//只能删除自定义插件
		if(userPlugin.getCustom() == UserPlugin.CUSTOM_STATUS_IS_NOT_CUSTOM) {
			throw new BusinessException("只能删除自定义插件");
		}
		
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		//登记变更日志
		// 变更记录
		UserPluginChangeHistory changeHistory = new UserPluginChangeHistory();
		changeHistory.setPluginId(pluginId);
		changeHistory.setPluginGroupName(userPlugin.getGroupName());
		changeHistory.setPluginName(userPlugin.getName());
		changeHistory.setPluginDescription(userPlugin.getDescription());
		changeHistory.setPluginDependencies(userPlugin.getDependencies());
		changeHistory.setUserId(user.getId());
		changeHistory.setUpdateDate(DateUtil.currentDate());

		// 变更明细
		UserPluginChangeHistoryDetail changeHistoryDetail = new UserPluginChangeHistoryDetail();
		changeHistoryDetail.setChangeFieldName("");
		changeHistoryDetail.setChangeFieldComment("");
		changeHistoryDetail.setChangeBefore("删除自定义插件");
		changeHistoryDetail.setChangeAfter("删除自定义插件");

		changeHistory.addChangeHistoryDetail(changeHistoryDetail);

		//登记变更记录
		updateChangeHistory(changeHistory);
		
		userPluginRepository.delete(userPlugin);
		
	}

	@Transactional
	@Override
	public void setDefault(String pluginId) {
		UserPlugin userPlugin = userPluginRepository.getOne(pluginId);
		
		//自定义插件不支持恢复默认
		if(userPlugin.getCustom() == UserPlugin.CUSTOM_STATUS_IS_CUSTOM) {
			throw new BusinessException("自定义插件不支持恢复为初始值");
		}
		
		Plugin plugin = pluginService.getPluginByName(userPlugin.getGroupName(), userPlugin.getName());
		
		userPlugin.setUserId(getUserId());
		userPlugin.setSystemVersion(plugin.getSystemVersion());
		userPlugin.setCreateDate(DateUtil.currentDate());
		userPlugin.setUpdateDate(DateUtil.currentDate());
		userPlugin.setName(plugin.getName());
		userPlugin.setDescription(plugin.getDescription());
		userPlugin.setTemplatePath(plugin.getTemplatePath());
		userPlugin.setTemplateContent(plugin.getTemplateContent());
		userPlugin.setGenerator(plugin.getGenerator());
		userPlugin.setGeneratorSourceContent(plugin.getGeneratorSourceContent());
		userPlugin.setGeneratorContent(plugin.getGeneratorContent());
		userPlugin.setFileRelativeDir(plugin.getFileRelativeDir());
		userPlugin.setFileSuffix(plugin.getFileSuffix());
		userPlugin.setPrefix(plugin.getPrefix());
		userPlugin.setSuffix(plugin.getSuffix());
		userPlugin.setPluginPath(plugin.getPluginPath());
		userPlugin.setDependencies(plugin.getDependencies());
		userPlugin.setStatus(IntConstants.STATUS_ENABLE.getCode());
		userPlugin.setCustom(UserPlugin.CUSTOM_STATUS_IS_NOT_CUSTOM);
		
		userPluginRepository.saveAndFlush(userPlugin);
	}
	
	@Transactional
	@Override
	public String loadDefaultSource(String groupName, String pluginName) {
		UserPlugin plugin = getPluginByName(groupName, pluginName);
		
		return plugin.getGeneratorSourceContent().trim();
	}

	@Transactional
	@Override
	public String loadDefaultTemplate(String groupName, String pluginName) {
		UserPlugin plugin = getPluginByName(groupName, pluginName);
		
		return plugin.getTemplateContent().trim();
	}
	
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public void savePluginGeneratorSourceContent(String groupName, String pluginName, String source) {
		UserPlugin plugin = getPluginByName(groupName, pluginName);
		
		//如果源代码和插件之前的源代码不一致则保存
		if(!source.equals(plugin.getGeneratorSourceContent())) {
			//根据源代码生成字节码
			//生成字节码
			String classContent = classService.generateClassContentByClassName(source, plugin.getGenerator());
			
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			User user = (User) authentication.getPrincipal();
			
			// 变更记录
			UserPluginChangeHistory changeHistory = new UserPluginChangeHistory();
			changeHistory.setPluginId(plugin.getId());
			changeHistory.setPluginGroupName(plugin.getGroupName());
			changeHistory.setPluginName(plugin.getName());
			changeHistory.setPluginDescription(plugin.getDescription());
			changeHistory.setPluginDependencies(plugin.getDependencies());
			changeHistory.setUserId(user.getId());
			changeHistory.setUpdateDate(DateUtil.currentDate());

			// 变更明细(源代码)
			UserPluginChangeHistoryDetail changeSource = new UserPluginChangeHistoryDetail();
			changeSource.setChangeFieldName("generatorSourceContent");
			changeSource.setChangeFieldComment(getPluginFieldComment(changeSource.getChangeFieldName()));
			changeSource.setChangeBefore(plugin.getGeneratorSourceContent());
			changeSource.setChangeAfter(source);

			changeHistory.addChangeHistoryDetail(changeSource);
			
			// 变更明细(字节码)
			UserPluginChangeHistoryDetail changeClass = new UserPluginChangeHistoryDetail();
			changeClass.setChangeFieldName("generatorContent");
			changeClass.setChangeFieldComment(getPluginFieldComment(changeClass.getChangeFieldName()));
			changeClass.setChangeBefore(plugin.getGeneratorContent());
			changeClass.setChangeAfter(classContent);

			changeHistory.addChangeHistoryDetail(changeClass);

			//登记变更记录
			updateChangeHistory(changeHistory);
			
			plugin.setGeneratorSourceContent(source);
			plugin.setGeneratorContent(classContent); 
			
			userPluginRepository.saveAndFlush(plugin);
		}		
	}

	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public void savePluginGeneratorTemplateContent(String groupName, String pluginName, String templateContent) {
		UserPlugin plugin = getPluginByName(groupName, pluginName);
		
		//如果要保存的模板内容和之前的模板内容不一致则保存
		if(!templateContent.equals(plugin.getTemplateContent())) {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			User user = (User) authentication.getPrincipal();
			
			// 变更记录
			UserPluginChangeHistory changeHistory = new UserPluginChangeHistory();
			changeHistory.setPluginId(plugin.getId());
			changeHistory.setPluginGroupName(plugin.getGroupName());
			changeHistory.setPluginName(plugin.getName());
			changeHistory.setPluginDescription(plugin.getDescription());
			changeHistory.setPluginDependencies(plugin.getDependencies());
			changeHistory.setUserId(user.getId());
			changeHistory.setUpdateDate(DateUtil.currentDate());

			// 变更明细
			UserPluginChangeHistoryDetail changeHistoryDetail = new UserPluginChangeHistoryDetail();
			changeHistoryDetail.setChangeFieldName("templateContent");
			changeHistoryDetail.setChangeFieldComment(getPluginFieldComment(changeHistoryDetail.getChangeFieldName()));
			changeHistoryDetail.setChangeBefore(plugin.getTemplateContent());
			changeHistoryDetail.setChangeAfter(templateContent);

			changeHistory.addChangeHistoryDetail(changeHistoryDetail);

			//登记变更记录
			updateChangeHistory(changeHistory);
			
			plugin.setTemplateContent(templateContent);
			
			userPluginRepository.saveAndFlush(plugin);
		}
	}
	
	@Transactional
	@Override
	public String loadSystemSource(String groupName, String pluginName) {
		UserPlugin userPlugin = getPluginByName(groupName, pluginName);
		if(userPlugin.getCustom() == UserPlugin.CUSTOM_STATUS_IS_CUSTOM) {
			throw new BusinessException("自定义插件无法恢复系统插件配置");
		}
		
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		return plugin.getGeneratorSourceContent().trim();
	}

	@Transactional
	@Override
	public String loadSystemTemplate(String groupName, String pluginName) {
		UserPlugin userPlugin = getPluginByName(groupName, pluginName);
		if(userPlugin.getCustom() == UserPlugin.CUSTOM_STATUS_IS_CUSTOM) {
			throw new BusinessException("自定义插件无法恢复系统插件配置");
		}
		
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		return plugin.getTemplateContent().trim();
	}

	@Transactional
	@Override
	public Plugin loadSystemConfig(String groupName, String pluginName) {
		UserPlugin userPlugin = getPluginByName(groupName, pluginName);
		if(userPlugin.getCustom() == UserPlugin.CUSTOM_STATUS_IS_CUSTOM) {
			throw new BusinessException("自定义插件无法恢复系统插件配置");
		}
		
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		return plugin;
	}
	
	@Transactional
	@Override
	public String savePlugin(String tempId) {
		UserCustomPluginTemp temp = userCustomPluginTempService.getById(tempId);
		//插件id
		String pluginId = temp.getUserPluginId();
		//校验插件名是否重复
		String pluginName = temp.getName();
		UserPlugin up = getPluginByName(temp.getGroupName(), pluginName);
		//如果根据插件名称查询到插件且id不是当前插件则报错
		if((StringUtils.isEmpty(pluginId) && up != null) 
				|| (StringUtils.hasLength(pluginId) && up != null && !up.getId().equals(pluginId))) {
			throw new BusinessException("插件名不能重复");
		}
		
		UserPlugin userPlugin = null;
		UserPlugin oldPlugin = null;
		//修改插件
		if(StringUtils.hasLength(pluginId)) {
			userPlugin = userPluginRepository.getOne(pluginId);
			oldPlugin = userPlugin.clone();
		}else {
			//新增插件
			userPlugin = new UserPlugin();
			userPlugin.setCreateDate(DateUtil.currentDate());
			userPlugin.setStatus(IntConstants.STATUS_ENABLE.getCode());
			userPlugin.setCustom(UserPlugin.CUSTOM_STATUS_IS_CUSTOM);
			userPlugin.setUserId(getUserId());
			userPlugin.setSystemVersion(versionService.findVersion().getVersion());
		}
		userPlugin.setUpdateDate(DateUtil.currentDate());
		userPlugin.setGroupName(temp.getGroupName());
		userPlugin.setName(temp.getName());
		userPlugin.setDescription(temp.getDescription());
		userPlugin.setTemplateContent(temp.getTemplateContent());
		userPlugin.setGenerator(temp.getGenerator());
		userPlugin.setGeneratorSourceContent(temp.getGeneratorSourceContent());
		userPlugin.setFileRelativeDir(temp.getFileRelativeDir());
		userPlugin.setFileSuffix(temp.getFileSuffix());
		userPlugin.setPrefix(temp.getPrefix());
		userPlugin.setSuffix(temp.getSuffix());
		userPlugin.setDependencies(temp.getDependencies());

		String generatorContent = null;
		//生成字节码
		try{
			generatorContent = classService.userGenerateClassContent(temp.getGeneratorSourceContent(), userPlugin);
		}catch (RuntimeException e){
			logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}

		userPlugin.setGeneratorContent(generatorContent);
		
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		//修改状态需要登记更新日志
		if(StringUtils.hasLength(pluginId)) {
			// 比对原来存在的插件信息和插件目录下的插件信息,如果不一致则需要更新插件信息
			List<UserPluginChangeHistoryDetail> changeHistoryDetailList = new ArrayList<>();
	
			Field[] fields = UserPlugin.class.getDeclaredFields();
			for (Field field : fields) {
				Object oldValue = getFieldValue(oldPlugin, field);
				Object newValue = getFieldValue(userPlugin, field);
	
				// 前后结果不一致
				if (!isSame(oldValue, newValue)) {
					if (!field.isAccessible()) {
						field.setAccessible(true);
					}
					// 只对比带ChangeEntityComment注解的字段
					ChangeEntityComment comment = field.getAnnotation(ChangeEntityComment.class);
					if (comment != null) {
						UserPluginChangeHistoryDetail changeHistoryDetail = new UserPluginChangeHistoryDetail();
						changeHistoryDetail.setChangeFieldName(field.getName());
						changeHistoryDetail.setChangeFieldComment(comment.value());
						changeHistoryDetail.setChangeBefore(oldValue == null ? null : String.valueOf(oldValue));
						changeHistoryDetail.setChangeAfter(newValue == null ? null : String.valueOf(newValue));
	
						changeHistoryDetailList.add(changeHistoryDetail);
					}
				}
			}
	
			if (changeHistoryDetailList.size() > 0) {
				// 变更记录
				UserPluginChangeHistory changeHistory = new UserPluginChangeHistory();
				changeHistory.setPluginId(userPlugin.getId());
				changeHistory.setPluginGroupName(userPlugin.getGroupName());
				changeHistory.setPluginName(userPlugin.getName());
				changeHistory.setPluginDescription(userPlugin.getDescription());
				changeHistory.setPluginDependencies(userPlugin.getDependencies());
				changeHistory.setUserId(user.getId());
				changeHistory.setUpdateDate(DateUtil.currentDate());
				changeHistory.setChangeHistoryDetailList(changeHistoryDetailList);
				
				//登记变更记录
				updateChangeHistory(changeHistory);
			}
		}
		
		return userPluginRepository.saveAndFlush(userPlugin).getId();
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
	
	@Override
	public List<UserPlugin> findByPluginNames(String groupName, String[] pluginNames) {
		Assert.notEmpty(pluginNames, "插件名称集合不能为空");
		
		return userPluginRepository.findAllByNames(groupName, Arrays.asList(pluginNames), getUserId());
	}

	private String getUserId() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		return ((User) authentication.getPrincipal()).getId();
	}

	@Override
	public void checkCustomPluginConfig() {
		if(SystemParameter.EnableUserCustomPlugin.DISABLE.getValue() ==
				systemParameterService.findSystemParameter().getEnableUserCustomPlugin()){
			throw new BusinessException("不允许自定义插件");
		}
	}
}
