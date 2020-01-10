package com.qxs.generator.web.controller.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.annotation.ResourceAccessRole;
import com.qxs.generator.web.model.log.Login;
import com.qxs.generator.web.service.log.ILoginService;

/**
 * 登录日志控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-29
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/log/login")
public class LogLoginController {
	
	@Autowired
	private ILoginService loginService;
	
	
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/index")
	public String index() {
		return "log/login/index";
	}
	
	/**
	* 获取列表数据
	* @param user 查询条件实体
	* @return PageList<Login>
	**/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/getList")
	@ResponseBody
	public Page<Login> getList(String search,@RequestParam(required = false)Integer offset, @RequestParam(required = false)Integer limit,
			@RequestParam(required = false)String sort, @RequestParam(required = false)String order) {
		return loginService.findList(search, offset, limit, sort, order);
	}
}
