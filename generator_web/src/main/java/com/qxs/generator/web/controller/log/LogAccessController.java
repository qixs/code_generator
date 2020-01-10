package com.qxs.generator.web.controller.log;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.annotation.ResourceAccessRole;
import com.qxs.generator.web.model.log.Access;
import com.qxs.generator.web.service.log.IAccessService;

/**
 * 请求日志控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-29
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/log/access")
public class LogAccessController {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S");
	
	/**
	 * 访问日志文件名
	 * **/
	private String fileName = String.format("code_generator_access_log_%s.html", DATE_FORMAT.format(new Date()));
	
	@Autowired
	private IAccessService accessService;
	
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/index")
	public String index() {
		return "log/access/index";
	}
	/**
	 * 日志详情
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") String id,Model model) {
		
		model.addAttribute("access", accessService.getById(id));
		
		return "log/access/detail";
	}
	
	/**
	* 获取列表数据
	* @param user 查询条件实体
	* @return PageList<Login>
	**/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/getList")
	@ResponseBody
	public Page<Access> getList(String search,@RequestParam(required = false)Integer offset, @RequestParam(required = false)Integer limit,
			@RequestParam(required = false)String sort, @RequestParam(required = false)String order) {
		return accessService.findList(search, offset, limit, sort, order);
	}
	
	/**
	* 下载访问日志信息
	**/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/downloadAccessLog/{id}")
	public Object downloadAccessLog(@PathVariable String id, HttpServletResponse response)  
			throws IOException{
		byte[] bytes = accessService.generateAccessLogFile(id);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	    headers.setContentDispositionFormData("attachment",URLEncoder.encode(fileName, "UTF-8")); 
	    response.setHeader("Set-Cookie", "fileDownload=true; path=/");
	    
	    return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
	}
}
