package com.qxs.generator.web.controller.config.system.parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qxs.generator.web.annotation.ResourceAccessRole;
import com.qxs.generator.web.model.config.SystemParameter;
import com.qxs.generator.web.service.config.ISystemParameterService;

/**
 * 系统参数控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2019-3-21
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/config/system/parameter")
public class SystemParameterController {
	
	@InitBinder("systemParameter")
    public void initBinderSystemParameter(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("systemParameter.");
    }

	@Autowired
	private ISystemParameterService systemParameterService;

	/**
	 * 系统参数配置首页
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping({"","/","/index"})
	public String index(Model model) {
		
		model.addAttribute("systemParameter", systemParameterService.findSystemParameter());
		
		return "config/system/parameter/index";
	}
	
	/**
	 * 更新系统参数配置信息
	 * 
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@PostMapping
	public void update(SystemParameter systemParameter) {
		systemParameterService.save(systemParameter);
	}

}
