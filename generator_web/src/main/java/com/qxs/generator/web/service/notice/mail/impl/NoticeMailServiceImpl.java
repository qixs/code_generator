package com.qxs.generator.web.service.notice.mail.impl;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.qxs.generator.web.model.config.Email;
import com.qxs.generator.web.service.config.IEmailService;
import com.qxs.generator.web.service.ip.IpService;
import com.qxs.generator.web.service.mail.IMailService;
import com.qxs.generator.web.service.notice.mail.INoticeMailService;
import com.qxs.generator.web.util.RequestUtil;

/**
 * @author qixingshen
 **/
@Service
public class NoticeMailServiceImpl implements INoticeMailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NoticeMailServiceImpl.class);

	/**
	 * 重置密码
	 * **/
	@Value("${notice.email.templates.resetPassword.subject:代码生成器重置密码}")
	private String resetPasswordSubject;
	@Value("${notice.email.templates.resetPassword.template:notice/email/templates/resetPassword}")
	private String resetPasswordTemplate;
	/**
	 * 重置密码成功提醒
	 * **/
	@Value("${notice.email.templates.resetPasswordSuccess.subject:代码生成器重置密码}")
	private String resetPasswordSuccessSubject;
	@Value("${notice.email.templates.resetPasswordSuccess.template:notice/email/templates/resetPasswordSuccess}")
	private String resetPasswordSuccessTemplate;
	
	/**
	 * 修改密码成功提醒邮件
	 * **/
	@Value("${notice.email.templates.passwordModifySuccess.subject:代码生成器修改密码成功提醒}")
	private String passwordModifySuccessSubject;
	@Value("${notice.email.templates.passwordModifySuccess.template:notice/email/templates/passwordModifySuccess}")
	private String passwordModifySuccessTemplate;

	/**
	 * 账号激活邮件
	 * **/
	@Value("${notice.email.templates.activeAccount.subject:代码生成器账号激活}")
	private String activeAccountSubject;
	@Value("${notice.email.templates.activeAccount.template:notice/email/templates/activeAccount}")
	private String activeAccountTemplate;

    /**
	 * 测试邮件
	 * **/
	@Value("${notice.email.templates.testMail.subject:代码生成器测试邮件}")
	private String testMailSubject;
	@Value("${notice.email.templates.testMail.template:notice/email/templates/testMail}")
	private String testMailTemplate;
	
	/**
	 * 创建超级管理员账号验证码邮件
	 * **/
	@Value("${notice.email.templates.adminCaptcha.subject:创建超级管理员账号验证码}")
	private String adminCaptchaSubject;
	@Value("${notice.email.templates.adminCaptcha.template:notice/email/templates/adminCaptcha}")
	private String adminCaptchaTemplate;
	
	@Value("${notice.helpUrl:}")
	private String helpUrl;

	@Autowired
	private IMailService mailService;
	
	@Autowired
	private IEmailService emailService;
	
	@Autowired
	private IpService ipService;
	
	@Autowired
	private TemplateEngine templateEngine;

	@Override
	public void sendResetPasswordMail(String to, int validateMinutes, String resetPasswordUrl) {
		Assert.hasLength(resetPasswordSubject, "重置密码主题不能为空");
		Assert.hasLength(resetPasswordTemplate, "重置密码模板不能为空");

		Map<String,String> variable = new HashMap<>(4);
		variable.put("resetPasswordUrl", resetPasswordUrl);
		variable.put("to", to);
		variable.put("validateMinutes", validateMinutes + "");
		variable.put("ip", RequestUtil.getIpAddr());
		variable.put("ipAddress", ipService.findIpAddress(variable.get("ip")));
		
		String text = readTemplate(resetPasswordTemplate,variable);
		
		LOGGER.debug("收件人:[{}]  主题:[{}]  邮件内容:[{}]", to, resetPasswordSubject, text);

		mailService.asyncSend(to, resetPasswordSubject, text, emailService.findEmail());
	}

	@Override
	public void sendResetPasswordSuccessMail(String to, String operateUsername) {
		Assert.hasLength(resetPasswordSuccessSubject, "重置密码成功主题不能为空");
		Assert.hasLength(resetPasswordSuccessTemplate, "重置密码成功模板不能为空");

		Map<String,String> variable = new HashMap<>(4);
		variable.put("to", to);
		variable.put("operateUsername", operateUsername);
		variable.put("date", new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date()));
		variable.put("ip", RequestUtil.getIpAddr());
		variable.put("ipAddress", ipService.findIpAddress(variable.get("ip")));
		
		String text = readTemplate(resetPasswordSuccessTemplate,variable);
		
		LOGGER.debug("收件人:[{}]  主题:[{}]  邮件内容:[{}]", to, resetPasswordSuccessSubject, text);

		mailService.asyncSend(to, resetPasswordSuccessSubject, text, emailService.findEmail());
	}

	@Override
	public void sendPasswordModifySuccessMail(String to) {
		Assert.hasLength(passwordModifySuccessSubject, "修改密码成功主题不能为空");
		Assert.hasLength(passwordModifySuccessTemplate, "修改密码成功模板不能为空");

		Map<String,String> variable = new HashMap<>(4);
		variable.put("to", to);
		variable.put("date", new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date()));
		variable.put("ip", RequestUtil.getIpAddr());
		variable.put("ipAddress", ipService.findIpAddress(variable.get("ip")));
		
		String text = readTemplate(passwordModifySuccessTemplate,variable);
		
		LOGGER.debug("收件人:[{}]  主题:[{}]  邮件内容:[{}]", to, passwordModifySuccessSubject, text);

		mailService.asyncSend(to, passwordModifySuccessSubject, text, emailService.findEmail());
	}

	@Override
	public void sendActiveAccountMail(String to, int validateMinutes, String activeUrl) {
		Assert.hasLength(activeAccountSubject, "账号创建成功主题不能为空");
		Assert.hasLength(activeAccountTemplate, "账号创建成功模板不能为空");

		Map<String,String> variable = new HashMap<>(3);
		variable.put("validateMinutes", validateMinutes+"");
		variable.put("activeUrl", activeUrl);
		variable.put("to", to);
		
		String text = readTemplate(activeAccountTemplate,variable);
		
		LOGGER.debug("收件人:[{}]  主题:[{}]  邮件内容:[{}]", to, activeAccountSubject, text);

		mailService.asyncSend(to, activeAccountSubject, text, emailService.findEmail());
	}
	
	@Override
	public void sendValidMail(String testMail, Email email) {
		mailService.send(testMail, testMailSubject, readTemplate(testMailTemplate, null), email);
	}
	
	private <V> String readTemplate(String templatePath,Map<String,V> variable) {
		// 构造上下文(Model)
		Context context = new Context();
		context.setVariable("helpUrl", helpUrl);
		
		if(variable != null) {
			for(Map.Entry<String,V> entry: variable.entrySet()) {
				context.setVariable(entry.getKey(), entry.getValue());
			}
		}
		
		// 渲染模板
		StringWriter write = new StringWriter();
		templateEngine.process(templatePath, context, write);
		return write.toString();
	}

	@Override
	public void sendAdminCaptchaMail(String to, int validateMinutes, String captcha) {
		Assert.hasLength(adminCaptchaSubject, "创建超级管理员账号验证码主题不能为空");
		Assert.hasLength(adminCaptchaTemplate, "创建超级管理员账号验证码模板不能为空");

		Map<String,String> variable = new HashMap<>(4);
		variable.put("captcha", captcha);
		variable.put("to", to);
		variable.put("validateMinutes", validateMinutes + "");
		variable.put("date", new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒").format(new Date()));
		variable.put("ip", RequestUtil.getIpAddr());
		variable.put("ipAddress", ipService.findIpAddress(variable.get("ip")));
		
		String text = readTemplate(adminCaptchaTemplate,variable);
		
		LOGGER.debug("收件人:[{}]  主题:[{}]  邮件内容:[{}]", to, adminCaptchaSubject, text);
		
		mailService.asyncSend(to, adminCaptchaSubject, text, emailService.findEmail());
	}

}
