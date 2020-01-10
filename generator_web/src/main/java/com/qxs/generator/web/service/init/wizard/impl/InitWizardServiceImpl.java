package com.qxs.generator.web.service.init.wizard.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.config.Email;
import com.qxs.generator.web.model.config.Geetest;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.service.config.IEmailService;
import com.qxs.generator.web.service.config.IGeetestService;
import com.qxs.generator.web.service.init.wizard.ICompleteService;
import com.qxs.generator.web.service.init.wizard.ICurrentStepService;
import com.qxs.generator.web.service.init.wizard.IInitWizardService;
import com.qxs.generator.web.service.init.wizard.IStepService;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.generator.web.service.user.IUserService;

@Service
public class InitWizardServiceImpl implements IInitWizardService{

	@Autowired
	private ICompleteService completeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private IEmailService emailService;
	@Autowired
	private IGeetestService geetestService;
	@Autowired
	private IStepService stepService;
	@Autowired
	private ICurrentStepService currentStepService;
	
	@Transactional
	@Override
	public void complete() {
		//校验超级用户，超级用户必须设置且只能设置一个
		User user = new User();
		
		user.setAdmin(User.ADMIN_STATUS_IS_ADMIN);
		List<User> adminUserList = userService.findList(user);
		//超级管理员列表不能为空
		if(adminUserList.isEmpty()) {
			throw new BusinessException("超级管理员设置错误：超级管理员不能为空");
		}
		//超级管理员列表不能为空
		if(adminUserList.size() > 1) {
			throw new BusinessException("超级管理员设置错误：超级管理员不能有多个");
		}
		
		//校验启用的插件
		//必须启用插件
		Plugin p = new Plugin();
		p.setStatus(IntConstants.STATUS_ENABLE.getCode());
		List<Plugin> plugins = pluginService.findPluginList(p, Sort.by(Direction.ASC, "name"));
		if(plugins.isEmpty()) {
			throw new BusinessException("插件设置错误：未查询到已启用的插件");
		}
		
		List<String> pluginNames = plugins.stream().map(Plugin::getName).collect(Collectors.toList());
		
		//校验插件的依赖关系,如dao依赖entity插件,如果启用dao插件则必须启用entity插件
		plugins.forEach(plugin -> {
			String dependency = plugin.getDependencies();
			if(StringUtils.hasLength(dependency)) {
				String[] dependencies = dependency.split(",");
				for(int i = 0 , length = dependencies.length ; i < length ; i ++) {
					if(!pluginNames.contains(dependencies[i])) {
						throw new BusinessException(String.format("[%s]插件依赖[%s]插件，必须启用[%s]插件", plugin.getName(),dependencies[i],dependencies[i]));
					}
				}
			}
		});
		
		//校验邮件服务器配置
		Email email = emailService.findEmail();
		if(email == null) {
			throw new BusinessException("邮件配置错误：未查询到邮件配置信息");
		}
		
		//校验geetest
		List<Geetest> geetests = geetestService.findAll(Sort.by(Direction.ASC, "id"));
		if(geetests.isEmpty()) {
			throw new BusinessException("Geetest配置错误：未查询到Geetest配置信息");
		}
		
		//校验当前步骤表中的步骤号是否和最大步骤号一致
		int currentStepNum = currentStepService.currentStepNum();
		long maxStepNum = stepService.maxStepNum();
		if(currentStepNum != maxStepNum) {
			throw new BusinessException("当前初始化步骤号不等于系统最大步骤号，请重新初始化");
		}
		
		//登记完成初始化表
		completeService.save();
	}
	
}
