package com.qxs.generator.web.controller.user.plugin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.model.user.UserPluginChangeHistoryDetail;
import com.qxs.generator.web.service.user.IUserPluginChangeHistoryDetailService;

/**
 * 插件变更明细控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-10
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/user/plugin/change/history/detail")
public class UserPluginChangeHistoryDetailController {
	
	@Autowired
	private IUserPluginChangeHistoryDetailService userPluginChangeHistoryDetailService;
	
	/**
	 * 插件变更明细首页
	 * **/
	@GetMapping("/index/{pluginChangeHistoryId}")
	public String index(@PathVariable String pluginChangeHistoryId, Model model) {
		model.addAttribute("pluginChangeHistoryId", pluginChangeHistoryId);
		return "user/plugin/changeHistory/detail";
	}
	
	/**
	* 获取插件变更明细列表数据
	* @return Page<Generate>
	**/
	@GetMapping("/getList/{pluginChangeHistoryId}")
	@ResponseBody
	public List<UserPluginChangeHistoryDetail> getList(@PathVariable String pluginChangeHistoryId) {
		return userPluginChangeHistoryDetailService.findList(pluginChangeHistoryId);
	}
}
