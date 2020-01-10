package com.qxs.generator.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 首页控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-29
 * @version Revision: 1.0
 */
@Controller
public class IndexController {
	@InitBinder("ssh")
    public void initBinderSSH(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("ssh.");
    }
	@GetMapping({"/","/index"})
	public String index(Model model){
		return "index";
	}
	
}
