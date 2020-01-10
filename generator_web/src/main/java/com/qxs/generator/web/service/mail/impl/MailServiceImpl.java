package com.qxs.generator.web.service.mail.impl;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.config.Email;
import com.qxs.generator.web.service.mail.IMailService;


/**
 * @author qixingshen
 * **/
@Service
public class MailServiceImpl implements IMailService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

	private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9_\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
	
//	@Value("${notice.email.from:}")
//	private String from;
//	
//	@Value("${notice.email.password:}")
//	private String password;
//	
	
//	
//	@Value("${notice.email.host:}")
//	private String host;
//	
//	@Value("${notice.email.port:25}")
//	private int port;
//	
//	@Value("${notice.email.ssl:false}")
//	private boolean ssl;
	
	@Value("${notice.email.personal:代码生成器}")
	private String personal;
	@Value("${notice.email.defaultEncoding:GBK}")
	private String defaultEncoding;

	@Override
	@Async
	public void asyncSend(String to,String cc,String subject,MultipartFile attachment,String text,Email email) {
		Assert.hasLength(email.getEmailFrom(), "发件人不能为空");
		Assert.hasLength(email.getPassword(), "发件人密码不能为空");
		Assert.hasLength(email.getHost(), "发件服务器不能为空");
		Assert.hasLength(defaultEncoding, "默认编码不能为空");
		
		Properties javaMailProperties = new Properties();
		
		javaMailProperties.put("mail.smtp.timeout", 30000);
		
		if(email.getSsl() == Email.SSL_USE) {
			javaMailProperties.put("mail.smtp.auth", "true");
			javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(email.getHost());
        mailSender.setPort(email.getPort());
        mailSender.setUsername(email.getEmailFrom());
        mailSender.setPassword(email.getPassword());
        mailSender.setDefaultEncoding(defaultEncoding);
        mailSender.setJavaMailProperties(javaMailProperties);
        
        MimeMessage msg = mailSender.createMimeMessage();
        try {
        	MimeMessageHelper helper = new MimeMessageHelper(msg, 
        			MimeMessageHelper.MULTIPART_MODE_MIXED, mailSender.getDefaultEncoding());
			
        	helper.setFrom(email.getEmailFrom(), personal);
			
			//收件人
			String[] tos = to.split(",");
			for(String t : tos) {
				if(!PATTERN.matcher(t).matches()) {
					throw new BusinessException("“" + t + "”不是邮箱地址");
				}
				helper.addTo(t);
			}
			
			//抄送人
			if(StringUtils.hasLength(cc)) {
				String[] ccs = cc.split(",");
				for(String c : ccs) {
					if(!PATTERN.matcher(c).matches()) {
						throw new BusinessException("“" + c + "”不是邮箱地址");
					}
					helper.addCc(c);
				}
			}
			
	        //主题
			helper.setSubject(subject);
	        //正文
	        helper.setText(text, true);
	        //附件
	        if(attachment != null) {
	        	helper.addAttachment(attachment.getOriginalFilename(), attachment);
	        }
	        
        	mailSender.send(msg);
        	
        	LOGGER.debug("收件人:[{}]  邮件主题:[{}]  邮件内容:[{}]", to, subject, text);
		} catch (MailException e) {
			LOGGER.error("邮件发送失败，收件人:[{}]  邮件主题:[{}]  邮件内容:[{}]", to, subject, text, e);
			throw new BusinessException(e);
		} catch (MessagingException e) {
			LOGGER.error("邮件发送失败，收件人:[{}]  邮件主题:[{}]  邮件内容:[{}]", to, subject, text, e);
			throw new BusinessException(e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("邮件发送失败，收件人:[{}]  邮件主题:[{}]  邮件内容:[{}]", to, subject, text, e);
			throw new BusinessException(e);
		}
	}

	@Override
	@Async
	public void asyncSend(String to,String subject,String text,Email email) {
		asyncSend(to, null, subject, null, text, email);
	}

	@Override
	public void send(String to,String cc,String subject,MultipartFile attachment,String text,Email email) {
		asyncSend(to, cc, subject, attachment, text, email);
	}
	
	@Override
	public void send(String to, String subject, String text, Email email) {
		asyncSend(to, null, subject, null, text, email);
	}
}
