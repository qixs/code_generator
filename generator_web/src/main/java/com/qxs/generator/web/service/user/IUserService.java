package com.qxs.generator.web.service.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.qxs.generator.web.model.user.User;

/**
 * 用户信息维护service
 * 
 * @author qixingshen
 * @date 2018-5-31
 **/
public interface IUserService extends UserDetailsService{

	/**
	 * 添加用户
	 * 
	 * @param user
	 *            用户信息
	 * @return int 添加的用户的id
	 **/
	String insert(User user, String passwordRepeat);
	/**
	 * 添加超级用户
	 * 
	 * @param user 用户信息
	 * @param captcha 验证码
	 * @param repeatPassword 重复密码
	 * @return int 添加的用户的id
	 **/
	String insertAdmin(User user,String captcha,String repeatPassword);

	/**
	 * 更新用户
	 * 
	 * @param user
	 *            用户信息
	 * @return int 更新的用户的id
	 **/
	String update(User user);

	/**
	 * 禁用用户
	 * 
	 * @param id
	 *            用户id
	 * @return int 更新的用户的id
	 **/
	String disable(String id);

	/**
	 * 启用用户
	 * 
	 * @param id
	 *            用户id
	 * @return int 更新的用户的id
	 **/
	String enable(String id);

	/**
	 * 根据id查询用户信息
	 * 
	 * @param id 用户id
	 * @return User 用户信息
	 **/
	User findById(String id);

	/**
	 * 查询用户列表
	 * 
	 * @param search
	 *            查询内容
	 * @return List<User> 用户信息
	 **/
	Page<User> findList(User user, String search, Integer offset, Integer limit,String sort, String order);
	/**
	 * 根据user对象查询用户信息
	 * 
	 * @param search
	 *            查询内容
	 * @return List<User> 用户信息
	 **/
	List<User> findList(User user);
	/**
	 * 根据用户名查询用户信息
	 * 
	 * @param username 用户名
	 * @return User 用户信息
	 **/
	User findByUsername(String username);
	/**
	 * 获取超级管理员
	 * 
	 * @return User 超级管理员
	 * **/
	User findAdmin();
	/**
	 * 发送验证码
	 * @param username 用户名
	 *
	 * @return void
	 * **/
	void sendAdminCaptcha(String username);
	/**
	 * 发送验证码
	 * @param username 用户名
	 *
	 * @return void
	 * **/
	void sendCaptcha(String username);
	/**
	 * 当前登录用户用户信息
	 * @return User 当前登录用户 
	 * **/
	User userInfo();
	/**
	 * 修改密码
	 * @param oldPassword 原密码
	 * @param captcha 验证码
	 * @param newPassword 新密码
	 * @param newPasswordRepeat 重复密码
	 * **/
	void changePassword(String oldPassword, String captcha, String newPassword, String newPasswordRepeat);
	/**
	 * 重置密码
	 * @param userId 用户id
	 * @param newPassword 新密码
	 * @param newPasswordRepeat 重复密码
	 * **/
	void resetPassword(String userId, String newPassword, String newPasswordRepeat);
	/**
	 * 激活用户
	 * **/
	void active(String username, String checkCode, HttpServletRequest request, HttpServletResponse response);

	/**
	 * 根据用户名更新用户
	 *
	 * @param user
	 *            用户信息
	 * @return int 更新的用户的id
	 **/
	String updateByUsername(User user);
}
