package com.qxs.generator.web.controller.source.edit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
 * 在线编辑源码控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-4-22
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/source/edit")
public class SourceEditController {
	
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private IUserPluginService userPluginService;
	@Autowired
	private IGeneratorService generatorService;
	@Autowired
	private IClassService classService;
	
	/**          插件           **/
	/**
	 * 插件源码编辑页面
	 * **/
	@GetMapping("/plugin/source/{groupName}/{pluginName}")
	public String pluginSource(@PathVariable String groupName, @PathVariable String pluginName, Model model) {
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		model.addAttribute("pluginName", plugin.getName());
		model.addAttribute("code", plugin.getGeneratorSourceContent());
		return "util/sourceEdit";
	}
	
	/**
	 * 生成代码
	 * **/
	@PostMapping("/plugin/source/generateCode")
	@ResponseBody
	public String sourceGenerateCode(@RequestParam String source, @RequestParam String groupName, @RequestParam String pluginName) {
		//生成字节码
		String classContent = classService.generateClassContent(source, groupName, pluginName);
		
		return generatorService.sourceGenerateCode(classContent, groupName, pluginName);
	}
	
	/**
	 * 插件模板编辑页面
	 * **/
	@GetMapping("/plugin/template/{groupName}/{pluginName}")
	public String pluginTemplate(@PathVariable String groupName, @PathVariable String pluginName, Model model) {
		Plugin plugin = pluginService.getPluginByName(groupName, pluginName);
		model.addAttribute("pluginName", plugin.getName());
		model.addAttribute("code", plugin.getTemplateContent());
		return "util/sourceEdit";
	}
	
	/**
	 * 生成代码
	 * **/
	@PostMapping("/plugin/template/generateCode")
	@ResponseBody
	public String templateGenerateCode(@RequestParam String source, @RequestParam String groupName, @RequestParam String pluginName) {
		
		return generatorService.templateGenerateCode(source, groupName, pluginName);
	}
	
	
	
	/**         用户插件         **/
	
	/**
	 * 用户插件源码编辑页面
	 * **/
	@GetMapping("/user/plugin/source/{groupName}/{pluginName}")
	public String userPluginSource(@PathVariable String groupName, @PathVariable String pluginName, Model model) {
		//校验是否允许自定义模板
		userPluginService.checkCustomPluginConfig();

		UserPlugin plugin = userPluginService.getPluginByName(groupName, pluginName);
		model.addAttribute("plugin", plugin);
		model.addAttribute("code", plugin.getGeneratorSourceContent());
		model.addAttribute("customPlugin", plugin.getCustom());
		return "util/sourceEdit";
	}
	
	
	/**
	 * 生成代码
	 * **/
	@PostMapping("/user/plugin/source/generateCode")
	@ResponseBody
	public String userSourceGenerateCode(@RequestParam String source, @RequestParam String groupName, @RequestParam String pluginName) {
		//生成字节码
		String classContent = classService.userGenerateClassContent(source, groupName, pluginName);
		
		return generatorService.userGenerateCode(classContent, groupName, pluginName);
	}
	

	/**
	 * 插件模板编辑页面
	 * **/
	@GetMapping("/user/plugin/template/{groupName}/{pluginName}")
	public String userPluginTemplate(@PathVariable String groupName, @PathVariable String pluginName, Model model) {
		//校验是否允许自定义模板
		userPluginService.checkCustomPluginConfig();

		UserPlugin plugin = userPluginService.getPluginByName(groupName, pluginName);
		model.addAttribute("pluginName", plugin.getName());
		model.addAttribute("code", plugin.getTemplateContent());
		model.addAttribute("customPlugin", plugin.getCustom());
		return "util/sourceEdit";
	}
	
	/**
	 * 生成代码
	 * **/
	@PostMapping("/user/plugin/template/generateCode")
	@ResponseBody
	public String userTemplateGenerateCode(@RequestParam String source, @RequestParam String groupName, @RequestParam String pluginName) {
		
		return generatorService.userTemplateGenerateCode(source, groupName, pluginName);
	}
	
}
