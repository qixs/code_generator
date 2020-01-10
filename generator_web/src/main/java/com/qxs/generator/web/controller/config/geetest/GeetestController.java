package com.qxs.generator.web.controller.config.geetest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.annotation.ResourceAccessRole;
import com.qxs.generator.web.model.config.Geetest;
import com.qxs.generator.web.service.config.IGeetestService;

/**
 * geetest验证码参数控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-4-22
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/config/geetest")
public class GeetestController {
	
	@InitBinder("geetest")
    public void initBinderGeetest(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("geetest.");
    }
	
	@Autowired
	private IGeetestService geetestService;
	
	/**
	 * Geetest验证码参数配置首页
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping({"","/","/index"})
	public String index() {
		return "config/geetest/index";
	}
	
	/**
	 * 新增Geetest验证码参数
	 * 
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@PostMapping
	public void insert(Geetest geetest) {
		geetestService.insert(geetest);
	}
	
	/**
	 * 删除Geetest验证码参数
	 * 
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@DeleteMapping("/{id}")
	public void delete(@PathVariable String id) {
		geetestService.deleteById(id);
	}
	
	/**
	 * 查询Geetest验证码参数列表
	 * 
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/findList")
	@ResponseBody
	public List<Geetest> findList() {
		return geetestService.findAll(Sort.by(Direction.ASC, "id"));
	}
}
