package com.qxs.generator.web.controller.tools;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.base.formatter.jsqlparser.JSQLParserFormatter;

/**
 * sql格式化工具
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2019-3-21
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/tools/sql/format")
public class SqlFormatController {

	@GetMapping({"/","/index"})
	public String index(Model model){
		return "tools/sql/format/index";
	}

	@PostMapping({"/format"})
	@ResponseBody
	public String format(@RequestParam String sql){
		return JSQLParserFormatter.format(sql);
	}
	
}
