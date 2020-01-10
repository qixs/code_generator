package com.qxs.generator.web.service.user;

import com.qxs.generator.web.model.user.UserAdminCaptcha;

/**
 * 超级管理员验证码service
 * 
 * @author qixingshen
 * @date 2018-8-22
 **/
public interface IUserAdminCaptchaService {
	
	/**
	 * 插入
	 * 
	 * @param userPasswordCheckCode 验证码信息
	 * 
	 * @return UserPasswordCheckCode
	 * **/
	UserAdminCaptcha insert(UserAdminCaptcha userAdminCaptcha);
	
	/**
	 * 根据条件查询验证码信息
	 * 
	 * @param userPasswordCheckCode 验证码信息
	 * 
	 * @return UserPasswordCheckCode
	 * **/
	UserAdminCaptcha find(UserAdminCaptcha userAdminCaptcha);
	
	/**
	 * 生成验证码
	 * **/
	String generateCaptcha();
	
	/**
	 * 使验证码失效
	 * **/
	void setInvalidCaptcha(UserAdminCaptcha userAdminCaptcha);
}
