package com.qxs.generator.web.controller.connection;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.qxs.base.database.config.DatabaseTypeConfig;
import com.qxs.base.database.config.DatabaseTypeConfig.DatabaseType;
import com.qxs.database.model.Table;
import com.qxs.generator.web.config.DbUrlWarpper;
import com.qxs.generator.web.model.connection.Connection;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.DatabaseName;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.service.connection.IConnectionService;
import com.qxs.generator.web.service.connection.IDatabaseService;
import com.qxs.generator.web.service.connection.IGenerateParameterService;
import com.qxs.generator.web.service.connection.ISshService;

/**
 * 代码生成链接控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-4-22
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/generator/connection")
public class ConnectionController {
	
	@Autowired
	private IConnectionService connectionService;
	@Autowired
	private IDatabaseService databaseService;
	@Autowired
	private ISshService sshService;
	@Autowired
	private IGenerateParameterService generateParameterService;
	
	@InitBinder("database")
    public void initBinderDatabase(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("database.");
    }
	@InitBinder("ssh")
    public void initBinderSSH(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("ssh.");
    }
	@InitBinder("generateParameter")
    public void initBinderGenerateParameter(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("generateParameter.");
    }
	@InitBinder("connection")
    public void initBinderConnection(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("connection.");
    }
	
	/**
	 * 生成器首页
	 * **/
	@GetMapping({"","/","/index"})
	public String index(Model model) {
		List<Connection> connections = connectionService.listByUser();
		model.addAttribute("connections", connections);
		return "generator/index";
	}
	
	/**
	 * 数据库配置页签
	 * **/
	@GetMapping("/index/database")
	public String database(Connection connection,Model model) {
		List<DatabaseType> databaseTypeList = DatabaseTypeConfig.getDatabaseTypeConfig();
		
		Database database = null;
		if(StringUtils.hasLength(connection.getId())) {
			database = databaseService.findByConnection(connection);
		}
		
		DatabaseType databaseType = DatabaseTypeConfig.getDatabaseType(database == null ? databaseTypeList.get(0).getDbType() : database.getType());
		model.addAttribute("databaseTypes", databaseTypeList);
		model.addAttribute("databaseType", databaseType);
		
		if(DatabaseTypeConfig.WidgetType.SELECT.equals(databaseType.getDatabaseWidgetType())) {
			List<DatabaseName> databaseNames = null;
			if(StringUtils.isEmpty(connection.getId())) {
				database = new Database();
				database.setUrl("localhost");
				database.setPort(databaseType.getPort());
				database.setUsername(databaseType.getUsername());
				
				databaseNames = Lists.newArrayList(new DatabaseName(null, "请先录入数据库地址", null));
			}else {
				databaseNames = databaseService.findDatabaseNameList(
						database, sshService.findByConnection(connection));
			}
			model.addAttribute("databaseNames", databaseNames);
		}else {
			if(StringUtils.isEmpty(connection.getId())) {
				database = new Database();
				database.setUrl("localhost");
				database.setPort(databaseType.getPort());
				database.setUsername(databaseType.getUsername());
			}
		}
		
		model.addAttribute("database", database);

		return "generator/database";
	}

	/**
	 * 包装数据库连接字符串
	 * **/
	@PostMapping("/database/warpConnectionUrl")
	@ResponseBody
	public String warpConnectionUrl(Database database) {
		return DbUrlWarpper.warp(database.getType(), database.getUrl(), database.getPort(), database.getDatabaseName());
	}
	
	/**
	 * 包装数据库连接字符串
	 * **/
	@GetMapping("/database/getDatabaseTypeConfig/{dbType}")
	@ResponseBody
	public DatabaseType getDatabaseTypeConfig(@PathVariable String dbType) {
		return DatabaseTypeConfig.getDatabaseType(dbType);
	}
	
	/**
	 * 读取数据库列表
	 * 
	 * **/
	@PostMapping("/database/findDatabaseNameList")
	@ResponseBody
	public List<DatabaseName> findDatabaseNameList(Database database, Ssh ssh){
		return databaseService.findDatabaseNameList(database, ssh);
	}
	
	/**
	 * ssh配置页签
	 * **/
	@GetMapping("/index/ssh")
	public String ssh(Connection connection,Model model) {
		model.addAttribute("ssh", sshService.findByConnection(connection));
		return "generator/ssh";
	}
	
	
	/**
	 * 生成代码参数配置页签
	 * **/
	@GetMapping("/index/generateParameter")
	public String generateParameter(Connection connection,Model model) {
		model.addAttribute("generateParameter", generateParameterService.findByConnection(connection));
		return "generator/generateParameter";
	}
	
	/**
	 * 获取数据库下所有的表
	 * **/
	@GetMapping("/findTables")
	@ResponseBody
	public List<Table> findTables(Database database,Ssh ssh) {
		return generateParameterService.findTables(database, ssh);
	}
	
	
	/**
	 * 测试连接是否成功
	 * **/
	@PostMapping("/validConnection")
	@ResponseBody
	public void validConnection(Database database,Ssh ssh) {
		connectionService.validConnection(database, ssh);
	}
	
	/**
	 * 新增链接
	 * **/
	@PostMapping
	@ResponseBody
	public String insert(Connection connection,Database database,Ssh ssh,GenerateParameter generateParameter) {
		return connectionService.insert(connection, database, ssh, generateParameter);
	}
	/**
	 * 更新链接
	 * **/
	@PutMapping
	@ResponseBody
	public String update(Connection connection,Database database,Ssh ssh,GenerateParameter generateParameter) {
		return connectionService.update(connection, database, ssh, generateParameter);
	}
	
	/**
	 * 删除链接
	 * **/
	@DeleteMapping("/{id}")
	@ResponseBody
	public void delete(@PathVariable String id) {
		connectionService.deleteById(id);
	}
	
	/**
	 * 加载所有的链接
	 * **/
	@GetMapping("/findConnections")
	@ResponseBody
	public List<Connection> findConnections() {
		return connectionService.listByUser();
	}
}
