package com.qxs.generator.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.qxs.generator.web.constant.Constants;


/**
 * 登录退出控制器
 * @author qixingshen
 * @date 2018-06-19
 * **/
@Controller
public class LoginController{
	
	/**
	 * 登录
	 * **/
	@GetMapping({"/login", "/logout"})
	public String login(HttpServletRequest request, Model model){
		//如果是登陆状态则重定向到index
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		
		//已登录
		if(authentication != null && authentication instanceof UsernamePasswordAuthenticationToken) {
			return "redirect:/index";
		}
		
		HttpSession session = request.getSession();
		Object errorNum = session.getAttribute(Constants.CAPTCHA_SESSION_ERROR_NUM_KEY);
		if(errorNum == null) {
			errorNum = 0;
			session.setAttribute(Constants.CAPTCHA_SESSION_ERROR_NUM_KEY, errorNum);
		}
		
		model.addAttribute("showCaptcha", ((int)errorNum) >= Constants.CAPTCHA_ERROR_MAX_NUM);
		
		return "login";
	}
}
