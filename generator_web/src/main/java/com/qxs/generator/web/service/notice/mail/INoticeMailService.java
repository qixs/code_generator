package com.qxs.generator.web.service.notice.mail;

import com.qxs.generator.web.model.config.Email;

/**
 * 发送邮件服务
 * 
 * @author qixingshen
 * @date 2018-07-05
 * @version 1.0
 * **/
public interface INoticeMailService {
	
	/**
	 * 发送重置密码邮件
	 * 
	 * @param to 收件人
	 * @param resetPasswordUrl 重置密码界面
	 * @param validateMinutes 有效分钟数
	 * 
	 * @return void
	 * **/
	void sendResetPasswordMail(String to,int validateMinutes,String resetPasswordUrl);
	/**
	 * 发送重置密码成功提醒邮件
	 * 
	 * @param to 收件人
	 * @param operateUsername 操作人
	 * 
	 * @return void
	 * **/
	void sendResetPasswordSuccessMail(String to, String operateUsername);
	
	/**
	 * 发送密码修改成功提醒邮件
	 * 
	 * @param to 收件人
	 * 
	 * @return void
	 * **/
	void sendPasswordModifySuccessMail(String to);
	
	/**
	 * 发送账号创建成功激活邮件
	 * 
	 * @param to 收件人
	 * @param validateMinutes 分钟数
	 * @param activeUrl 账号激活地址
	 * 
	 * @return void
	 * **/
	void sendActiveAccountMail(String to,int validateMinutes,String activeUrl);
	
	/**
	 * 发送测试邮件
	 * @param to 测试邮件地址
	 * @param email 邮件配置信息
	 * 
	 * **/
	void sendValidMail(String to, Email email);
	
	/**
	 * 发送创建超级管理员账号验证码邮件
	 * 
	 * @param to 收件人
	 * @param captcha 验证码
	 * @param validateMinutes 有效分钟数
	 * 
	 * @return void
	 * **/
	void sendAdminCaptchaMail(String to,int validateMinutes,String captcha);
}
