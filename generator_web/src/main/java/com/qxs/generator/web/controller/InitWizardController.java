package com.qxs.generator.web.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.qxs.generator.web.model.config.Email;
import com.qxs.generator.web.model.config.Geetest;
import com.qxs.generator.web.model.init.wizard.Step;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.User.CreateAdmin;
import com.qxs.generator.web.service.config.IEmailService;
import com.qxs.generator.web.service.config.IGeetestService;
import com.qxs.generator.web.service.init.wizard.ICurrentStepService;
import com.qxs.generator.web.service.init.wizard.IInitWizardService;
import com.qxs.generator.web.service.init.wizard.IStepService;
import com.qxs.generator.web.service.notice.mail.INoticeMailService;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.generator.web.service.user.IUserService;
import com.qxs.generator.web.validate.group.Create;

/**
 * 初始化向导控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-31
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/init/wizard")
public class InitWizardController {
	
	@InitBinder("email")
    public void initBinderEmail(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("email.");
    }
	@InitBinder("user")
    public void initBinderUser(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("user.");
    }
    @InitBinder("geetest")
    public void initBinderGeetest(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("geetest.");
    }
	
	@Autowired
	private IStepService stepService;
	@Autowired
	private ICurrentStepService currentStepService;
	@Autowired
	private IInitWizardService initWizardService;
	@Autowired
	private IEmailService emailService;
	@Autowired
	private INoticeMailService noticeMailService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private IGeetestService geetestService;
	
	/**
	 * 初始化导航-首页
	 * **/
	@GetMapping("/index")
	public String index(Model model){
		List<Step> stepList = stepService.findStepList();
		Step step = stepService.findStepByStepNum(currentStepService.currentStepNum());
		
		model.addAttribute("stepList", stepList);
		model.addAttribute("currentStep", step);
		
		return "init/wizard/index";
	}
	
	/**
	 * 初始化导航-根据步骤号获取步骤信息
	 * @param stepNum 步骤号
	 * @return Step
	 * **/
	@GetMapping("/{stepNum}")
	public @ResponseBody Step findStepByStepNum(@PathVariable int stepNum){
		Step step = stepService.findStepByStepNum(stepNum);
		
		return step;
	}
	
	/**
	 * 初始化导航-获取步骤列表
	 * @return List<Step>
	 * **/
	@GetMapping("/findStepList")
	public @ResponseBody List<Step> findStepList(){
		List<Step> stepList = stepService.findStepList();
		
		return stepList;
	}
	
	/**
	 * 初始化导航-设置当前步骤
	 * **/
	@PostMapping("/updateStepNum/{stepNum}")
	@ResponseBody
	public void updateCurrentStepNum(@PathVariable int stepNum){
		currentStepService.save(stepNum);
	}
	/**
	 * 初始化导航-许可协议页面
	 * **/
	@GetMapping("/license")
	public String license(Model model){
		//读取license
		String license = null;
		try {
			license = IOUtils.resourceToString("/static/license.txt", Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("license", license);
		return "init/wizard/license";
	}
	/**
	 * 初始化导航-设置超级用户页面
	 * **/
	@GetMapping("/admin")
	public String admin(Model model){
		User user = userService.findAdmin();
		
		model.addAttribute("user", user);
		return "init/wizard/admin";
	}
	/**
	 * 初始化导航-发送超级用户验证码
	 * **/
	@PostMapping("/sendAdminCaptcha")
	@ResponseBody
	public void sendAdminCaptcha(@RequestParam String username){
		userService.sendAdminCaptcha(username);
	}
	/**
	 * 初始化导航-设置超级用户
	 * **/
	@PostMapping("/admin")
	@ResponseBody
	public String saveAdmin(@Validated({CreateAdmin.class})User user,@RequestParam String captcha,@RequestParam(required = false) String repeatPassword){
		return userService.insertAdmin(user, captcha, repeatPassword);
	}
	
	/**
	 * 初始化导航-选择启用插件
	 * **/
	@GetMapping("/enablePlugins")
	public String enablePlugins(Model model){
		List<String> groupNameList = pluginService.findPluginGroupNameList();

		model.addAttribute("groupNameList", groupNameList);
		
		return "init/wizard/enablePlugins";
	}
	
	/**
	 * 初始化导航-插件列表
	 * **/
	@GetMapping("/pluginList")
	public @ResponseBody List<Plugin> pluginList(@RequestParam(required = false) String pluginGroupName){
		List<Plugin> pluginList = pluginService.findPluginList(pluginGroupName);
		
		return pluginList;
	}
	
	/**
	 * 上传插件
	 * **/
	@PostMapping("/uploadPlugin")
	@ResponseBody
	public boolean uploadPlugin(@RequestParam("file") MultipartFile file) {
		return pluginService.uploadPlugin(file);
	}
	
	/**
	 * 禁用插件
	 * **/
	@PostMapping("/disablePlugin/{pluginId}")
	@ResponseBody
	public int disablePlugin(@PathVariable("pluginId")String pluginId) {
		return pluginService.disablePlugin(pluginId);
	}
	
	/**
	 * 启用插件
	 * **/
	@PostMapping("/enablePlugin/{pluginId}")
	@ResponseBody
	public int enablePlugin(@PathVariable("pluginId")String pluginId) {
		return pluginService.enablePlugin(pluginId);
	}
	
	/**
	 * 初始化导航-配置邮件服务器页面
	 * **/
	@GetMapping("/mail")
	public String mail(Model model){
		Email email = emailService.findEmail();
		model.addAttribute("email", email == null ? new Email(25, 0) : email);
		return "init/wizard/mail";
	}
	
	/**
	 * 初始化导航-配置邮件服务器
	 * **/
	@PostMapping("/mail")
	@ResponseBody
	public void saveMail(@Validated({Create.class}) Email email){
		emailService.save(email);
	}
	
	/**
	 * 初始化导航-发送测试邮件
	 * **/
	@PostMapping("/sendValidMail")
	@ResponseBody
	public void sendValidMail(@Validated({Create.class}) Email email,@RequestParam String testMail){
		noticeMailService.sendValidMail(testMail, email);
	}
	
	/**
	 * 初始化导航-配置geetest页面
	 * **/
	@GetMapping("/geetest")
	public String geetest(Model model){
		return "init/wizard/geetest";
	}
	
	/**
	 * 初始化导航-geetest列表
	 * **/
	@GetMapping("/geetestList")
	public @ResponseBody List<Geetest> geetestList(){
		List<Geetest> geetestList = geetestService.findAll(Sort.by(Direction.ASC, "id"));
		
		return geetestList;
	}
	
	/**
	 * 初始化导航-添加geetest配置信息
	 * **/
	@PostMapping("/addGeetest")
	public @ResponseBody String addGeetest(Geetest geetest){
		geetestService.insert(geetest);
		return geetest.getId();
	}
	
	/**
	 * 初始化导航-删除geetest配置信息
	 * **/
	@PostMapping("/deleteGeetest/{id}")
	public @ResponseBody String deleteGeetest(@PathVariable String id){
		geetestService.deleteById(id);
		return id;
	}
	
	/**
	 * 初始化导航-完成初始化
	 * **/
	@PostMapping("/complete")
	public @ResponseBody void complete(){
		initWizardService.complete();
	}
}
