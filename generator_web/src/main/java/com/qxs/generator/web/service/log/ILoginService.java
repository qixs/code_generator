package com.qxs.generator.web.service.log;

import org.springframework.data.domain.Page;

import com.qxs.generator.web.model.log.Login;
import com.qxs.generator.web.model.user.User;

public interface ILoginService {
	
	/**
	 * 登记登录日志
	 * @param user 用户
	 * @return int 日志id
	 * **/
	String login(User user);
	
	/**
	 * 登记退出日志
	 * @param user 用户 
	 * @return int 日志id
	 * **/
	String logout(User user);
	/**
	 * 查询日志列表
	 * 
	 * @param search
	 *            查询内容
	 * @return List<Login> 用户信息
	 **/
	Page<Login> findList(String search, Integer offset, Integer limit, 
			String sort, String order);
}
