package com.qxs.generator.web.controller.config.edit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.user.UserPlugin;
import com.qxs.generator.web.service.IClassService;
import com.qxs.generator.web.service.IGeneratorService;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.generator.web.service.user.IUserPluginService;

/**
 * 在线编辑插件参数控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-4-22
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/config/edit")
public class ConfigEditController {
	
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private IUserPluginService userPluginService;
	@Autowired
	private IGeneratorService generatorService;
	@Autowired
	private IClassService classService;
	
	@InitBinder("plugin")
    public void initBinderPlugin(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("plugin.");
    }
	
	/**
	 * 插件源码编辑页面
	 * **/
	@GetMapping("/plugin/{groupName}/{pluginName}")
	public String pluginSource(@PathVariable String groupName, @PathVariable String pluginName, Model model) {
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		model.addAttribute("plugin", plugin);
		return "util/configEdit";
	}
	
	/**
	 * 生成代码
	 * **/
	@PostMapping("/plugin/generateCode")
	@ResponseBody
	public String generateCode(Plugin plugin) {
		return generatorService.pluginGenerateCode(plugin);
	}
	
	/**
	 * 用户插件源码编辑页面
	 * **/
	@GetMapping("/user/plugin/{groupName}/{pluginName}")
	public String userPluginIndex(@PathVariable String groupName, @PathVariable String pluginName, Model model) {
		UserPlugin plugin = userPluginService.getPluginByName(groupName, pluginName);
		model.addAttribute("plugin", plugin);
		return "util/sourceEdit";
	}
	
	
	/**
	 * 生成代码
	 * **/
	@PostMapping("/user/plugin/generateCode")
	@ResponseBody
	public String userGenerateCode(@RequestParam String source, @RequestParam String groupName, @RequestParam String pluginName) {
		//生成字节码
		String classContent = classService.userGenerateClassContent(source, groupName, pluginName);
		
		return generatorService.userGenerateCode(classContent, groupName, pluginName);
	}
}
