package com.qxs.generator.web.service.user.impl;

import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.model.user.UserPasswordCheckCode;
import com.qxs.generator.web.repository.user.IUserPasswordCheckCodeRepository;
import com.qxs.generator.web.service.config.ISystemParameterService;
import com.qxs.generator.web.service.user.IUserPasswordCheckCodeService;
import com.qxs.generator.web.util.DateUtil;

@Service
public class UserPasswordCheckCodeServiceImpl implements IUserPasswordCheckCodeService {
	
	private static int CHECK_CODE_LENGTH = 160;
	private static String[] CHECK_CODE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

	
	@Autowired
	private IUserPasswordCheckCodeRepository userPasswordCheckCodeRepository;

	@Autowired
	private ISystemParameterService systemParameterService;

	@Transactional
	@Override
	public UserPasswordCheckCode insert(UserPasswordCheckCode userPasswordCheckCode) {
		//设置当前用户所有校验码为失效状态
		userPasswordCheckCodeRepository.updateStatusByUserId(IntConstants.STATUS_DISABLE.getCode(), userPasswordCheckCode.getUserId());
		
		userPasswordCheckCode.setSendDate(DateUtil.currentDate());
		userPasswordCheckCode.setValidateMinutes(systemParameterService.findSystemParameter().getResetPasswordMinutes());
		userPasswordCheckCode.setStatus(IntConstants.STATUS_ENABLE.getCode());
		
		return userPasswordCheckCodeRepository.saveAndFlush(userPasswordCheckCode);
	}

	@Override
	public UserPasswordCheckCode find(UserPasswordCheckCode userPasswordCheckCode) {
		Assert.notNull(userPasswordCheckCode,"userPasswordCheckCode参数不能为空");
		
		return userPasswordCheckCodeRepository.findOne(Example.of(userPasswordCheckCode)).orElse(null);
	}

	@Override
	public String generateCheckCode() {
		StringBuilder sb = new StringBuilder();
		int checkCodeLength = CHECK_CODE_LENGTH;
		Random random = new Random();
		for(int i = 0 ; i < checkCodeLength ; i ++) {
			sb.append(CHECK_CODE_CHARS[random.nextInt(CHECK_CODE_CHARS.length)]);
		}
		return sb.toString();
	}
}
