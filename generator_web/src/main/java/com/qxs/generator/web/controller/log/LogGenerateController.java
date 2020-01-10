package com.qxs.generator.web.controller.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.model.log.Generate;
import com.qxs.generator.web.service.log.IGenerateService;

/**
 * 生成代码日志控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-29
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/log/generate")
public class LogGenerateController {

	@Autowired
	private IGenerateService generateService;
	
	@GetMapping("/index")
	public String index() {
		return "log/generate/index";
	}
	/**
	 * 日志详情
	 * **/
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") String id,Model model) {
		
		model.addAttribute("generate", generateService.getById(id));
		
		return "log/generate/detail";
	}
	
	/**
	* 获取列表数据
	* @return PageList<Login>
	**/
	@GetMapping("/getList")
	@ResponseBody
	public Page<Generate> getList(String search,@RequestParam(required = false)Integer offset, @RequestParam(required = false)Integer limit,
			@RequestParam(required = false)String sort, @RequestParam(required = false)String order) {
		return generateService.findList(search, offset, limit, sort, order);
	}
	
}
