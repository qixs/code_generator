package com.qxs.generator.web.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.service.user.IUserService;

/**
 * 用户信息控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-31
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/user/info")
public class UserInfoController {
	
	@InitBinder("user")
    public void initBinderUser(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("user.");
    }
	@InitBinder("pageable")
    public void initBinderPageable(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("pageable.");
    }
	
	@Autowired
	private IUserService userService;
	
	/***
	* 用户信息首页
	**/
	@GetMapping({ "", "/", "/index" })
	public String index(Model model) {
		model.addAttribute("user", userService.userInfo());
		return "userInfo/index";
	}
	
	/***
	* 用户信息首页
	**/
	@GetMapping("/edit")
	public String edit(Model model) {
		model.addAttribute("user", userService.userInfo());
		return "userInfo/edit";
	}
	
	/***
	* 用户信息保存
	**/
	@PutMapping
	@ResponseBody
	public User update(User user) {
		userService.update(user);
		return user;
	}
	
	/***
	* 用户信息首页
	**/
	@GetMapping("/password")
	public String password(Model model) {
		model.addAttribute("user", userService.userInfo());
		return "userInfo/password";
	}
	
	/***
	* 用户信息保存
	**/
	@PostMapping("/changePassword")
	@ResponseBody
	public void changePassword(@RequestParam String oldPassword, @RequestParam String captcha,
			@RequestParam String newPassword, @RequestParam String newPasswordRepeat) {
		userService.changePassword(oldPassword, captcha, newPassword, newPasswordRepeat);
	}
	
	@ModelAttribute
    public User userUpdate(@RequestParam(value = "user.id" , required = false) String id) {
		if(id != null) {
			User user = userService.findById(id);
			if(user == null) {
				throw new BusinessException("未查询到用户信息，用户可能已经被删除");
			}
			return user;
		}
        return new User();
    }  
	
	/**
	 * 当前登录用户用户信息
	 * **/
	@GetMapping("/userInfoModal")
	public String userInfoModal(Model model) {
		model.addAttribute("user", userService.userInfo());
		return "userInfo/userInfoModal";
	}
	/**
	 * 发送邮箱验证码
	 * **/
	@PostMapping("/sendCaptcha")
	@ResponseBody
	public void sendAdminCaptcha(@RequestParam String username){
		userService.sendCaptcha(username);
	}
}
