package com.qxs.generator.web.controller.config.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.qxs.generator.web.annotation.ResourceAccessRole;
import com.qxs.generator.web.model.config.Email;
import com.qxs.generator.web.service.config.IEmailService;
import com.qxs.generator.web.service.notice.mail.INoticeMailService;
import com.qxs.generator.web.validate.group.Create;

/**
 * email参数控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-4-22
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/config/email")
public class EmailController {
	
	@InitBinder("email")
    public void initBinderEmail(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("email.");
    }
	
	@Value("${technicalSupport.email}")
	private String technicalSupportEmail;
	
	@Autowired
	private IEmailService emailService;
	@Autowired
	private INoticeMailService noticeMailService;
	
	@GetMapping("/emailModal")
	public String emailModal(@RequestParam String subject,@RequestParam(required = false) String content, @RequestParam(required = false) String emailCarbonCopy, Model model) {
		Email email = emailService.findEmail();
		
		model.addAttribute("email", email);
		model.addAttribute("technicalSupportEmail", technicalSupportEmail);
		model.addAttribute("subject", subject);
		model.addAttribute("content", content);
		model.addAttribute("emailCarbonCopy", emailCarbonCopy);
		return "config/email/emailModal";
	}
	
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
	@ResponseBody
	public void send(@RequestParam String emailFrom,
			@RequestParam String emailTo,
			@RequestParam(required = false) String emailCarbonCopy,
			@RequestParam(required = false) String subject,
			@RequestParam(required = false) MultipartFile attachment,
			@RequestParam(required = false) String content) {
		
		emailService.send(emailFrom, emailTo, emailCarbonCopy, subject, attachment, content);
	}
	
	/**
	 * 邮箱配置首页
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping({"","/","/index"})
	public String index(Model model) {
		
		model.addAttribute("email", emailService.findEmail());
		
		return "config/email/index";
	}
	
	/**
	 * 更新邮箱配置信息
	 * 
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@PostMapping
	public void update(Email email) {
		emailService.save(email);
	}
	
	/**
	 * 初始化导航-发送测试邮件
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@PostMapping("/sendValidMail")
	@ResponseBody
	public void sendValidMail(@Validated({Create.class}) Email email,@RequestParam String testMail){
		noticeMailService.sendValidMail(testMail, email);
	}
	
}
