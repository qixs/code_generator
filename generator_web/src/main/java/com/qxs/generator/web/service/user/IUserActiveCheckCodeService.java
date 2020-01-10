package com.qxs.generator.web.service.user;

import com.qxs.generator.web.model.user.UserActiveCheckCode;

/**
 * 用户插件表service
 * 
 * @author qixingshen
 * @date 2018-5-31
 **/
public interface IUserActiveCheckCodeService {
	
	/**
	 * 插入
	 * 
	 * @param userActiveCheckCode 校验码信息
	 * 
	 * @return UserActiveCheckCode
	 * **/
	UserActiveCheckCode insert(UserActiveCheckCode userActiveCheckCode);
	
	/**
	 * 根据条件查询校验码信息
	 * 
	 * @param userActiveCheckCode 校验码信息
	 * 
	 * @return UserActiveCheckCode
	 * **/
	UserActiveCheckCode find(UserActiveCheckCode userActiveCheckCode);
	
	/**
	 * 生成校验码
	 * **/
	String generateCheckCode();
}
