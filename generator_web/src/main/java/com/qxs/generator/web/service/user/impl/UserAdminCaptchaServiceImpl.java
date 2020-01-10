package com.qxs.generator.web.service.user.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.model.user.UserAdminCaptcha;
import com.qxs.generator.web.repository.user.IUserAdminCaptchaRepository;
import com.qxs.generator.web.service.config.ISystemParameterService;
import com.qxs.generator.web.service.user.IUserAdminCaptchaService;
import com.qxs.generator.web.util.DateUtil;

@Service
public class UserAdminCaptchaServiceImpl implements IUserAdminCaptchaService {

	@Autowired
	private IUserAdminCaptchaRepository userAdminCaptchaRepository;
	@Autowired
	private ISystemParameterService systemParameterService;

	@Transactional
	@Override
	public UserAdminCaptcha insert(UserAdminCaptcha userAdminCaptcha) {
		//设置当前用户所有校验码为失效状态
		userAdminCaptchaRepository.updateStatusByUserId(IntConstants.STATUS_DISABLE.getCode(), userAdminCaptcha.getUsername());
		
		userAdminCaptcha.setSendDate(DateUtil.currentDate());
		userAdminCaptcha.setValidateMinutes(systemParameterService.findSystemParameter().getCaptchaExpireMinutes());
		userAdminCaptcha.setStatus(IntConstants.STATUS_ENABLE.getCode());
		
		return userAdminCaptchaRepository.saveAndFlush(userAdminCaptcha);
	}

	@Override
	public UserAdminCaptcha find(UserAdminCaptcha userAdminCaptcha) {
		Assert.notNull(userAdminCaptcha,"userAdminCaptcha参数不能为空");
		
		return userAdminCaptchaRepository.findOne(Example.of(userAdminCaptcha)).orElse(null);
	}

	@Override
	public String generateCaptcha() {
		//验证码
		StringBuilder captcha = new StringBuilder();
		Random random = new Random();
		for(int i = 0 ; i < 6 ; i ++) {
			captcha.append(random.nextInt(10));
		}
		return captcha.toString();
	}

	/**
	 * 该方法不能走事务
	 * **/
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void setInvalidCaptcha(UserAdminCaptcha userAdminCaptcha) {
		userAdminCaptcha.setStatus(IntConstants.STATUS_DISABLE.getCode());
		
		userAdminCaptchaRepository.saveAndFlush(userAdminCaptcha);
	}
	
	

}
