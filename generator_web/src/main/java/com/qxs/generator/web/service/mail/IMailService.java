package com.qxs.generator.web.service.mail;

import org.springframework.web.multipart.MultipartFile;

import com.qxs.generator.web.model.config.Email;

/**
 * 发送邮件服务
 * 
 * @author qixingshen
 * @date 2018-07-05
 * @version 1.0
 * **/
public interface IMailService {
	
	/**
	 * 发送邮件(异步)
	 * 
	 * @param to 收件人
	 * @param cc 抄送人
	 * @param subject 主题
	 * @param attachment 附件
	 * @param text 邮件内容
	 * @param email email参数配置信息
	 * 
	 * @return void
	 * **/
	void asyncSend(String to,String cc,String subject,MultipartFile attachment,String text,Email email);
	/**
	 * 发送邮件(异步)
	 * 
	 * @param to 收件人
	 * @param subject 主题
	 * @param text 邮件内容
	 * @param email email参数配置信息
	 * 
	 * @return void
	 * **/
	void asyncSend(String to,String subject,String text,Email email);
	
	/**
	 * 发送邮件(同步)
	 * 
	 * @param to 收件人
	 * @param cc 抄送人
	 * @param subject 主题
	 * @param attachment 附件
	 * @param text 邮件内容
	 * @param email email参数配置信息
	 * 
	 * @return void
	 * **/
	void send(String to,String cc,String subject,MultipartFile attachment,String text,Email email);
	/**
	 * 发送邮件(同步)
	 * 
	 * @param to 收件人
	 * @param subject 主题
	 * @param text 邮件内容
	 * @param email email参数配置信息
	 * 
	 * @return void
	 * **/
	void send(String to,String subject,String text,Email email);
}
