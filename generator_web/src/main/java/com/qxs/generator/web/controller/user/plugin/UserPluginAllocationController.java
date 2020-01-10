package com.qxs.generator.web.controller.user.plugin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.qxs.generator.web.model.user.UserPlugin;
import com.qxs.generator.web.service.user.IUserPluginService;

/**
 * 用户插件分配控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-31
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/user/plugin/allocation")
public class UserPluginAllocationController {
	
	@InitBinder("userPlugin")
    public void initBinderUser(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("userPlugin.");
    }
	
	@Autowired
	private IUserPluginService userPluginService;
	
	/***
	 * 用户插件分配首页
	 * **/
	@GetMapping("/index")
	public String index() {
		
		return "user/plugin/allocation";
	}
	
	/**
	 * 获取插件列表，如果已经分配给该用户则需要置状态为已分配
	 * **/
	@GetMapping("/findList/{userId}")
	@ResponseBody
	public List<UserPlugin> findList(@PathVariable String userId){
		return userPluginService.findUserAllocationPluginList(userId);
	}
	
	/**
	 * 分配插件
	 * @param userId 用户id
	 * @param pluginName 插件名
	 * **/
	@PostMapping("/allocation/{userId}/{groupName}/{pluginName}")
	@ResponseBody
	public void allocation(@PathVariable String userId, @PathVariable String groupName, @PathVariable String pluginName) {
		userPluginService.allocation(userId, groupName, pluginName);
	}
	
	/**
	 * 取消分配权限（如果收回用户针对该插件的使用权则需要删除该用户下所有的该插件信息，包括已禁用状态的插件，不包括自定义插件）
	 * @param userId 用户id
	 * @param pluginName 插件名
	 * **/
	@PostMapping("/recovery/{userId}/{pluginName}")
	@ResponseBody
	public void recovery(@PathVariable String userId, @PathVariable String pluginName) {
		userPluginService.recovery(userId, pluginName);
	}
}
