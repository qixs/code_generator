package com.qxs.generator.web.controller.plugin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.plugin.factory.model.PluginConfig;

/**
 * 插件控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-10
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/plugin")
public class PluginController {
	
	@InitBinder("plugin")
    public void initBinderPlugin(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("plugin.");
    }
	
	@Autowired
	private IPluginService pluginService;
	
	/**
	 * 插件首页
	 * **/
	@GetMapping({"", "/", "/index"})
	public String index(Model model) {
		List<String> groupNameList = pluginService.findPluginGroupNameList();

		model.addAttribute("groupNameList", groupNameList);

		return "plugin/index";
	}
	/**
	 * 获取插件分组列表
	 * **/
	@GetMapping("/findPluginGroupNameList")
	@ResponseBody
	public List<String> findPluginGroupNameList() {
		return pluginService.findPluginGroupNameList();
	}
	/**
	 * 获取插件列表(所有的插件)
	 * **/
	@GetMapping("/findPluginList")
	@ResponseBody
	public List<Plugin> getPluginList(Plugin plugin,Sort sort) {
		return pluginService.findPluginList(plugin, sort);
	}
	/**
	 * 获取插件信息
	 * **/
	@GetMapping("/getPluginByName/{groupName}/{pluginName}")
	@ResponseBody
	public Plugin getPluginByName(@PathVariable("groupName")String groupName, @PathVariable("pluginName")String pluginName) {
		return pluginService.getPluginByName(groupName, pluginName);
	}
	
	/**
	 * 禁用插件
	 * **/
	@PostMapping("/disablePlugin/{pluginId}")
	@ResponseBody
	public int disablePlugin(@PathVariable("pluginId")String pluginId) {
		return pluginService.disablePlugin(pluginId);
	}
	
	/**
	 * 启用插件
	 * **/
	@PostMapping("/enablePlugin/{pluginId}")
	@ResponseBody
	public int enablePlugin(@PathVariable("pluginId")String pluginId) {
		return pluginService.enablePlugin(pluginId);
	}
	
	/**
	 * 上传插件
	 * **/
	@PostMapping("/uploadPlugin")
	@ResponseBody
	public boolean uploadPlugin(@RequestParam("file") MultipartFile file) {
		
		return pluginService.uploadPlugin(file);
	}
	
	/**
	 * 保存插件生成器源代码
	 * **/
	@PostMapping("/savePluginGeneratorSourceContent")
	@ResponseBody
	public void savePluginGeneratorSourceContent(@RequestParam String groupName, @RequestParam String pluginName, @RequestParam("code") String source) {
		pluginService.savePluginGeneratorSourceContent(groupName, pluginName, source);
	}
	/**
	 * 保存插件生成器模板
	 * **/
	@PostMapping("/savePluginGeneratorTemplateContent")
	@ResponseBody
	public void savePluginGeneratorTemplateContent(@RequestParam String groupName, @RequestParam String pluginName, @RequestParam("code") String templateContent) {
		pluginService.savePluginGeneratorTemplateContent(groupName, pluginName, templateContent);
	}
	/**
	 * 保存插件生成器参数
	 * **/
	@PostMapping("/savePluginConfig")
	@ResponseBody
	public void savePluginConfig(Plugin plugin) {
		pluginService.savePluginConfig(plugin);
	}
	/**
	 * 代码生成器恢复初始值
	 * **/
	@GetMapping("/loadPluginSource")
	@ResponseBody
	public String loadPluginSource(@RequestParam String groupName, @RequestParam String pluginName) {
		return pluginService.loadPluginSource(groupName, pluginName);
	}
	/**
	 * 模板恢复初始值
	 * **/
	@GetMapping("/loadPluginTemplate")
	@ResponseBody
	public String loadPluginTemplate(@RequestParam String groupName, @RequestParam String pluginName) {
		return pluginService.loadPluginTemplate(groupName, pluginName);
	}
	/**
	 * 参数恢复初始值
	 * **/
	@GetMapping("/loadPluginConfig")
	@ResponseBody
	public Plugin loadPluginConfig(@RequestParam String groupName, @RequestParam String pluginName) {
		return pluginService.loadPluginConfig(groupName, pluginName);
	}
	
	/**
	 * 代码生成器恢复为系统插件值
	 * **/
	@GetMapping("/loadSystemSource")
	@ResponseBody
	public String loadSystemSource(@RequestParam String groupName, @RequestParam String pluginName) {
		return pluginService.loadSystemSource(groupName, pluginName);
	}
	/**
	 * 模板恢复为系统插件值
	 * **/
	@GetMapping("/loadSystemTemplate")
	@ResponseBody
	public String loadSystemTemplate(@RequestParam String groupName, @RequestParam String pluginName) {
		return pluginService.loadSystemTemplate(groupName, pluginName);
	}
	/**
	 * 参数恢复为系统插件值
	 * **/
	@GetMapping("/loadSystemConfig")
	@ResponseBody
	public PluginConfig loadSystemConfig(@RequestParam String groupName, @RequestParam String pluginName) {
		return pluginService.loadSystemConfig(groupName, pluginName);
	}
	
	@ModelAttribute
	public Sort sort(@RequestParam(required = false)String sortFieldName,@RequestParam(required = false)String sortType) {
		if(StringUtils.hasLength(sortFieldName)) {
			return new Sort(Direction.fromOptionalString(sortType).orElse(Direction.ASC), sortFieldName);
		}
		return null;
	}
}
