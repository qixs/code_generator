package com.qxs.generator.web.controller.user.plugin;

import java.util.List;

import com.qxs.generator.web.service.config.ISystemParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.user.UserPlugin;
import com.qxs.generator.web.service.user.IUserPluginService;

/**
 * 用户插件信息控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-31
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/user/plugin")
public class UserPluginController {
	
	@InitBinder("userPlugin")
    public void initBinderUser(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("userPlugin.");
    }
	
	@Autowired
	private IUserPluginService userPluginService;
	@Autowired
	private ISystemParameterService systemParameterService;
	
	/**
	* 查询用户插件列表
	* @param userPlugin 查询条件实体
	* @return List<UserPlugin>
	**/
	@GetMapping("/findUserPluginList")
	@ResponseBody
	public List<UserPlugin> getList(UserPlugin userPlugin) {
		return userPluginService.findUserPluginList(userPlugin, null);
	}

	/***
	 * 用户插件首页
	 * **/
	@GetMapping("/index")
	public String index(Model model) {
		model.addAttribute("enableUserCustomPlugin", systemParameterService.findSystemParameter().getEnableUserCustomPlugin());
		return "user/plugin/index";
	}
	
	/**
	* 获取列表数据
	* @return Page<User>
	**/
	@GetMapping("/findList")
	@ResponseBody
	public Page<UserPlugin> findList(String search,
			@RequestParam(required = false)Integer offset, @RequestParam(required = false)Integer limit,
			@RequestParam(required = false)String sort, @RequestParam(required = false)String order) {
		
		return userPluginService.findList(search, offset, limit, sort, order);
	}

	/**
	 * 禁用插件
	 * **/
	@PostMapping("/disablePlugin/{pluginId}")
	@ResponseBody
	public int disablePlugin(@PathVariable("pluginId")String pluginId) {
		return userPluginService.disablePlugin(pluginId);
	}
	
	/**
	 * 启用插件
	 * **/
	@PostMapping("/enablePlugin/{pluginId}")
	@ResponseBody
	public int enablePlugin(@PathVariable("pluginId")String pluginId) {
		return userPluginService.enablePlugin(pluginId);
	}
	
	/**
	 * 删除插件
	 * **/
	@DeleteMapping("/{pluginId}")
	public void delete(@PathVariable("pluginId")String pluginId) {
		//校验用户自定义插件权限
		userPluginService.checkCustomPluginConfig();

		userPluginService.delete(pluginId);
	}
	
	/**
	 * 插件恢复默认
	 * **/
	@PostMapping("/setDefault/{pluginId}")
	@ResponseBody
	public void setDefault(@PathVariable("pluginId")String pluginId) {
		//校验用户自定义插件权限
		userPluginService.checkCustomPluginConfig();

		userPluginService.setDefault(pluginId);
	}
	
	/**
	 * 代码生成器恢复默认
	 * **/
	@GetMapping("/loadDefaultSource")
	@ResponseBody
	public String loadDefaultSource(@RequestParam String groupName, @RequestParam String pluginName) {
		//校验用户自定义插件权限
		userPluginService.checkCustomPluginConfig();

		return userPluginService.loadDefaultSource(groupName, pluginName);
	}
	/**
	 * 模板恢复默认
	 * **/
	@GetMapping("/loadDefaultTemplate")
	@ResponseBody
	public String loadDefaultTemplate(@RequestParam String groupName, @RequestParam String pluginName) {
		return userPluginService.loadDefaultTemplate(groupName, pluginName);
	}
	/**
	 * 保存插件生成器源代码
	 * **/
	@PostMapping("/savePluginGeneratorSourceContent")
	@ResponseBody
	public void savePluginGeneratorSourceContent(@RequestParam String groupName, @RequestParam String pluginName, @RequestParam("code") String source) {
		userPluginService.savePluginGeneratorSourceContent(groupName, pluginName, source);
	}
	/**
	 * 保存插件生成器模板
	 * **/
	@PostMapping("/savePluginGeneratorTemplateContent")
	@ResponseBody
	public void savePluginGeneratorTemplateContent(@RequestParam String groupName, @RequestParam String pluginName, @RequestParam("code") String templateContent) {
		userPluginService.savePluginGeneratorTemplateContent(groupName, pluginName, templateContent);
	}
	

	/**
	 * 代码生成器恢复为系统插件值
	 * **/
	@GetMapping("/loadSystemSource")
	@ResponseBody
	public String loadSystemSource(@RequestParam String groupName, @RequestParam String pluginName) {
		return userPluginService.loadSystemSource(groupName, pluginName);
	}
	/**
	 * 模板恢复为系统插件值
	 * **/
	@GetMapping("/loadSystemTemplate")
	@ResponseBody
	public String loadSystemTemplate(@RequestParam String groupName, @RequestParam String pluginName) {
		return userPluginService.loadSystemTemplate(groupName, pluginName);
	}
	/**
	 * 参数恢复为系统插件值
	 * **/
	@GetMapping("/loadSystemConfig")
	@ResponseBody
	public Plugin loadSystemConfig(@RequestParam String groupName, @RequestParam String pluginName) {
		return userPluginService.loadSystemConfig(groupName, pluginName);
	}
	
	/**
	 * 保存自定义插件
	 * **/
	@PostMapping("/saveCustomPlugin")
	@ResponseBody
	public String saveCustomPlugin(@RequestParam String tempId) {
		return userPluginService.savePlugin(tempId);
	}
}
