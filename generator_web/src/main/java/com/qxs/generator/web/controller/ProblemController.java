package com.qxs.generator.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 问题反馈控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2019-3-26
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/problem")
public class ProblemController {
	
	@GetMapping({"/","/index"})
	public String index(Model model){
		return "problem/index";
	}
	
}
