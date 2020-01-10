package com.qxs.generator.web.controller.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.annotation.ResourceAccessRole;
import com.qxs.generator.web.model.plugin.PluginChangeHistory;
import com.qxs.generator.web.service.plugin.IPluginChangeHistoryService;

/**
 * 插件变更记录控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-10
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/plugin/change/history")
public class PluginChangeHistoryController {
	
	@InitBinder("pluginChangeHistory")
    public void initBinderPluginChangeHistory(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("pluginChangeHistory.");
    }
	
	@Autowired
	private IPluginChangeHistoryService pluginChangeHistoryService;
	
	/**
	 * 插件变更记录首页
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping({"", "/", "/index"})
	public String index() {
		return "plugin/changeHistory/index";
	}
	
	/**
	* 获取插件变更记录列表数据
	* @return Page<Generate>
	**/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/getList")
	@ResponseBody
	public Page<PluginChangeHistory> getList(@RequestParam(required = false) String search,@RequestParam(required = false)Integer offset, @RequestParam(required = false)Integer limit,
			@RequestParam(required = false)String sort, @RequestParam(required = false)String order) {
		return pluginChangeHistoryService.findList(search, offset, limit, sort, order);
	}
}
