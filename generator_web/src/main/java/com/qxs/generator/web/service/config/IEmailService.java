package com.qxs.generator.web.service.config;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.qxs.generator.web.model.config.Email;

/**
 * 邮件服务器配置信息配置信息
 * **/
public interface IEmailService {
	
	/**
	 * 邮件配置信息条数
	 * 
	 * @return int 条数
	 * **/
	long count();
	/**
	 * 获取邮件配置信息(只有一条数据)
	 * 
	 * @return Email 邮件配置信息
	 * **/
	Email findEmail();
	/**
	 * 保存邮件配置信息
	 * 
	 * @param email 邮件配置信息
	 * @return void
	 * **/
	void save(Email email);
	
	/**
	 * 发送邮件
	 * @param emailFrom 发件人
	 * @param emailTo 收件人
	 * @param emailCarbonCopy 抄送
	 * @param subject 主题
	 * @param attachment 附件
	 * @param content 邮件内容
	 * **/
	@PostMapping("/send")
	public void send(String emailFrom,String emailTo,String emailCarbonCopy, String subject,
			MultipartFile attachment,String content);
	
}
