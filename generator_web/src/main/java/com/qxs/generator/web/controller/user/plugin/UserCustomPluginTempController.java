package com.qxs.generator.web.controller.user.plugin;

import com.qxs.generator.web.service.user.IUserPluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.model.user.UserCustomPluginTemp;
import com.qxs.generator.web.service.user.IUserCustomPluginTempService;

/**
 * 自定义用户插件中间表
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-31
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/user/custom/plugin/temp")
public class UserCustomPluginTempController {
	
	@InitBinder("userCustomPluginTemp")
    public void initBinderUserCustomPluginTemp(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("userCustomPluginTemp.");
    }
	
	@Autowired
	private IUserCustomPluginTempService userCustomPluginTempService;
	@Autowired
	private IUserPluginService userPluginService;
	
	/**
	* 用户自定义插件modal
	* @param id 中间表信息id
	* @param userPluginId 用户插件id
	**/
	@GetMapping("/index")
	public String index(@RequestParam(required = false) String id, 
			@RequestParam(required = false) String userPluginId, Model model) {
		//校验用户自定义插件权限
		userPluginService.checkCustomPluginConfig();

		//如果id为空则新建中间信息
		if(!StringUtils.hasLength(id)) {
			UserCustomPluginTemp userCustomPluginTemp = userCustomPluginTempService.newUserCustomPluginTemp(userPluginId);
			id = userCustomPluginTemp.getId();
		}
		
		model.addAttribute("id", id);
		return "/user/plugin/custom/index";
	}

	/**
	* 用户自定义插件config
	**/
	@GetMapping("/config")
	public String config(@RequestParam String id,Model model) {
		UserCustomPluginTemp userCustomPluginTemp = userCustomPluginTempService.getById(id);
		
		model.addAttribute("userCustomPluginTemp", userCustomPluginTemp);
		return "/user/plugin/custom/config";
	}
	
	/**
	* 用户自定义插件template
	**/
	@GetMapping("/template")
	public String template(@RequestParam String id,Model model) {
		UserCustomPluginTemp userCustomPluginTemp = userCustomPluginTempService.getById(id);
		
		model.addAttribute("userCustomPluginTemp", userCustomPluginTemp);
		return "/user/plugin/custom/template";
	}
	
	/**
	* 用户自定义插件source
	**/
	@GetMapping("/source")
	public String source(@RequestParam String id,Model model) {
		UserCustomPluginTemp userCustomPluginTemp = userCustomPluginTempService.getById(id);
		
		model.addAttribute("userCustomPluginTemp", userCustomPluginTemp);
		return "/user/plugin/custom/source";
	}
	/**
	 * 保存自定义插件配置
	 * **/
	@PostMapping("/save")
	@ResponseBody
	public void save(UserCustomPluginTemp userCustomPluginTemp) {
		userCustomPluginTempService.save(userCustomPluginTemp); 
	}
	/**
	 * 保存到插件表
	 * **/
	@PostMapping("/savePlugin")
	@ResponseBody
	public void savePlugin(UserCustomPluginTemp userCustomPluginTemp) {
		userCustomPluginTempService.savePlugin(userCustomPluginTemp); 
	}
	
	@PostMapping("/generateCode")
	@ResponseBody
	public String generateCode(UserCustomPluginTemp userCustomPluginTemp) {
		return userCustomPluginTempService.generateCode(userCustomPluginTemp);
	}
	
}
