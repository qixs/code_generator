package com.qxs.generator.web.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 协议控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-29
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/license")
public class LicenseController {
	
	@GetMapping({"/","/index"})
	public String index(Model model){
		//读取license
		String license = null;
		try {
			license = IOUtils.resourceToString("/static/license.txt", Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("license", license);
		return "license/license";
	}
	
}
