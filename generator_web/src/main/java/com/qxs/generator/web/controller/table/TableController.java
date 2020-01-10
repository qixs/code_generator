package com.qxs.generator.web.controller.table;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.base.model.Table;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.service.table.ITableService;

/**
 * 表信息控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-17
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/table")
public class TableController {
	
	@InitBinder("database")
    public void initBinderDatabase(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("database.");
    }
	@InitBinder("ssh")
    public void initBinderSSH(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("ssh.");
    }
	
	@Autowired
	private ITableService tableService;
	
	@GetMapping("/getTableList")
	@ResponseBody
	public List<Table> getTableList(Database database, Ssh ssh){
		return tableService.getTableList(database, ssh);
	}
}
