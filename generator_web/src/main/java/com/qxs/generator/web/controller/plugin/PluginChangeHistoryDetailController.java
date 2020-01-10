package com.qxs.generator.web.controller.plugin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.annotation.ResourceAccessRole;
import com.qxs.generator.web.model.plugin.PluginChangeHistoryDetail;
import com.qxs.generator.web.service.plugin.IPluginChangeHistoryDetailService;

/**
 * 插件变更明细控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-10
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/plugin/change/history/detail")
public class PluginChangeHistoryDetailController {
	
	@InitBinder("pluginChangeHistory")
    public void initBinderPluginChangeHistory(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("pluginChangeHistory.");
    }
	
	@Autowired
	private IPluginChangeHistoryDetailService pluginChangeHistoryDetailService;
	
	/**
	 * 插件变更明细首页
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/index/{pluginChangeHistoryId}")
	public String index(@PathVariable String pluginChangeHistoryId, Model model) {
		model.addAttribute("pluginChangeHistoryId", pluginChangeHistoryId);
		return "plugin/changeHistory/detail";
	}
	
	/**
	* 获取插件变更明细列表数据
	* @return Page<Generate>
	**/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/getList/{pluginChangeHistoryId}")
	@ResponseBody
	public List<PluginChangeHistoryDetail> getList(@PathVariable String pluginChangeHistoryId) {
		return pluginChangeHistoryDetailService.findList(pluginChangeHistoryId);
	}
}
