package com.qxs.generator.web.service.user.impl;

import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.model.user.UserActiveCheckCode;
import com.qxs.generator.web.repository.user.IUserActiveCheckCodeRepository;
import com.qxs.generator.web.service.config.ISystemParameterService;
import com.qxs.generator.web.service.user.IUserActiveCheckCodeService;
import com.qxs.generator.web.util.DateUtil;

@Service
public class UserActiveCheckCodeServiceImpl implements IUserActiveCheckCodeService {
	
	private static int CHECK_CODE_LENGTH = 160;
	private static String[] CHECK_CODE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

	
	@Autowired
	private IUserActiveCheckCodeRepository userActiveCheckCodeRepository;

	@Autowired
	private ISystemParameterService systemParameterService;

	@Transactional
	@Override
	public UserActiveCheckCode insert(UserActiveCheckCode userActiveCheckCode) {
		//设置当前用户所有校验码为失效状态
		userActiveCheckCodeRepository.updateStatusByUserId(IntConstants.STATUS_DISABLE.getCode(), userActiveCheckCode.getUserId());
		
		userActiveCheckCode.setSendDate(DateUtil.currentDate());
		userActiveCheckCode.setValidateMinutes(systemParameterService.findSystemParameter().getUserActiveMinutes());
		userActiveCheckCode.setStatus(IntConstants.STATUS_ENABLE.getCode());
		
		return userActiveCheckCodeRepository.saveAndFlush(userActiveCheckCode);
	}

	@Override
	public UserActiveCheckCode find(UserActiveCheckCode userActiveCheckCode) {
		Assert.notNull(userActiveCheckCode,"userActiveCheckCode参数不能为空");
		
		return userActiveCheckCodeRepository.findOne(Example.of(userActiveCheckCode)).orElse(null);
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
