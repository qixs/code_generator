package com.qxs.generator.web.service.rest.password;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 重置密码service
 * 
 * @author qixingshen
 * @date 2018-07-07
 * @version 1.0
 * **/
public interface IRestPasswordService {
	
	/**
	 * 发送重置密码邮件
	 * 
	 * @param username 用户名
	 * @param enhencedValidate 是否校验验证码
	 * 
	 * @return void
	 * **/
	void sendMail(String username,boolean enhencedValidate,HttpServletRequest request,HttpServletResponse response);
	
	/**
	 * 验证校验码是否正确
	 * @param username 用户名
	 * @param checkCode 校验码
	 * **/
	void validCheckCode(String username,String checkCode,HttpServletRequest request,HttpServletResponse response);
	/**
	 * 重置密码
	 * @param username 用户名
	 * @param checkCode 校验码
	 * @param password 密码
	 * @param repeatPassword 重复密码
	 * @param request
	 * @param response
	 * 
	 * @return void
	 * **/
	void restPassword(String username,String checkCode,String password, String repeatPassword, HttpServletRequest request,HttpServletResponse response);
}
