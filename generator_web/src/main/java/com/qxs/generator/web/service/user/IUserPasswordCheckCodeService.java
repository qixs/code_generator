package com.qxs.generator.web.service.user;

import com.qxs.generator.web.model.user.UserPasswordCheckCode;

/**
 * 用户插件表service
 * 
 * @author qixingshen
 * @date 2018-5-31
 **/
public interface IUserPasswordCheckCodeService {
	
	/**
	 * 插入
	 * 
	 * @param userPasswordCheckCode 校验码信息
	 * 
	 * @return UserPasswordCheckCode
	 * **/
	UserPasswordCheckCode insert(UserPasswordCheckCode userPasswordCheckCode);
	
	/**
	 * 根据条件查询校验码信息
	 * 
	 * @param userPasswordCheckCode 校验码信息
	 * 
	 * @return UserPasswordCheckCode
	 * **/
	UserPasswordCheckCode find(UserPasswordCheckCode userPasswordCheckCode);
	
	/**
	 * 生成校验码
	 * **/
	String generateCheckCode();
}
