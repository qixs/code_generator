package com.qxs.generator.web.controller.captcha.geetest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.service.config.IGeetestService;

/**
 * geetest验证码控制器(http://www.geetest.com/)
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-6-28
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/captcha/geetest")
public class CaptchaGeetestController {
	
	@Autowired
	private IGeetestService geetestService;
	
	/**
	 * 获取验证码
	 * **/
	@GetMapping("/register")
	@ResponseBody
	public String register() {
		return geetestService.register();
	}
	
}
