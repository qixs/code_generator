package com.qxs.generator.web.controller.user.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.model.user.UserPluginChangeHistory;
import com.qxs.generator.web.service.user.IUserPluginChangeHistoryService;

/**
 * 插件变更记录控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-10
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/user/plugin/change/history")
public class UserPluginChangeHistoryController {
	
	@Autowired
	private IUserPluginChangeHistoryService userPluginChangeHistoryService;
	
	/**
	 * 插件变更记录首页
	 * **/
	@GetMapping({"", "/", "/index"})
	public String index() {
		return "user/plugin/changeHistory/index";
	}
	
	/**
	* 获取插件变更记录列表数据
	* @return Page<Generate>
	**/
	@GetMapping("/getList")
	@ResponseBody
	public Page<UserPluginChangeHistory> getList(String search,@RequestParam(required = false)Integer offset, @RequestParam(required = false)Integer limit,
			@RequestParam(required = false)String sort, @RequestParam(required = false)String order) {
		return userPluginChangeHistoryService.findList(search, offset, limit, sort, order);
	}
}
