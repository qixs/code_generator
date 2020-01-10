package com.qxs.generator.web.controller.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qxs.generator.web.service.config.ISystemParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.qxs.generator.web.service.rest.password.IRestPasswordService;


/**
 * 重置密码控制器
 * @author qixingshen
 * @date 2018-07-07
 * **/
@Controller
@RequestMapping("/forgetPassword")
public class ForgetPasswordController {
	
	@Autowired
	private IRestPasswordService restPasswordService;
	@Autowired
	private ISystemParameterService systemParameterService;
	
	/**
	 * 忘记密码主页
	 * 
	 * @return String
	 * 
	 * **/
	@GetMapping
	public String index(){
		return "forgetPassword/index";
	}
	
	/**
	 * 发送邮件
	 * 
	 * @param username 用户名
	 * 
	 * @return String
	 * 
	 * **/
	@PostMapping("/sendMail")
	public String sendMail(@RequestParam String username,HttpServletRequest request,HttpServletResponse response, Model model){
		
		restPasswordService.sendMail(username, true, request, response);

		model.addAttribute("username", username);
		model.addAttribute("resetPasswordMinutes", systemParameterService.findSystemParameter().getResetPasswordMinutes());
		return "forgetPassword/sendSuccess";
	}
	
	/**
	 * 重发邮件
	 * 
	 * @param username 用户名
	 * 
	 * @return String
	 * **/
	@PostMapping("/repeatSendMail")
	@ResponseBody
	public void repeatSendMail(@RequestParam String username,HttpServletRequest request,HttpServletResponse response){
		
		restPasswordService.sendMail(username, false, request, response);
	}
	
	/**
	 * 重置密码页面
	 * 
	 * @param username 用户名
	 * @param checkCode 校验码
	 * 
	 * @return String
	 * **/
	@GetMapping("/restPassword/{username}/{checkCode}")
	public String restPassword(@PathVariable String username, @PathVariable String checkCode,
			HttpServletRequest request,HttpServletResponse response,Model model){
		//验证校验码
		restPasswordService.validCheckCode(username, checkCode, request, response);
		
		model.addAttribute("username", username);
		model.addAttribute("checkCode", checkCode);
		
		return "forgetPassword/restPassword";
	}
	
	/**
	 * 重置密码
	 * 
	 * @param username 用户名
	 * @param checkCode 校验码
	 * @param password 密码
	 * 
	 * @return String
	 * **/
	@PostMapping("/restPassword")
	public String restPassword(@RequestParam String username,@RequestParam String checkCode,@RequestParam String password,@RequestParam String repeatPassword, HttpServletRequest request,HttpServletResponse response){
		
		restPasswordService.restPassword(username, checkCode, password, repeatPassword, request, response);

		return "forgetPassword/restPasswordSuccess";
	}
}
