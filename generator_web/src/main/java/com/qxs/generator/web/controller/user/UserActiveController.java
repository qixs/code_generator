package com.qxs.generator.web.controller.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qxs.generator.web.service.user.IUserService;

/**
 * 用户激活控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-31
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/user/active")
public class UserActiveController {
	
	@Value("${notice.helpUrl:}")
	private String helpUrl;
	
	@Autowired
	private IUserService userService;
	
	/***
	* 激活用户
	**/
	@GetMapping("/{username}/{checkCode}")
	public String active(@PathVariable String username,@PathVariable String checkCode,
			HttpServletRequest request,HttpServletResponse response, Model model) {
		userService.active(username, checkCode, request, response);
		
		model.addAttribute("username", username);
		model.addAttribute("helpUrl", helpUrl);
		return "user/activeSuccess";
	}
	
	/***
	* 激活用户失败
	**/
	@GetMapping("/fail/{username}")
	public String active(@PathVariable String username, HttpSession session, Model model) {
		String activeErrorMessage = (String) session.getAttribute("activeErrorMessage");
		session.removeAttribute("activeErrorMessage");
		
		model.addAttribute("username", username);
		model.addAttribute("helpUrl", helpUrl);
		model.addAttribute("activeErrorMessage", activeErrorMessage);
		return "user/activeFail";
	}

}
