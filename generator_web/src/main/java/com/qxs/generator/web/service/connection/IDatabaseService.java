package com.qxs.generator.web.service.connection;

import java.util.List;

import com.qxs.generator.web.model.connection.Connection;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.DatabaseName;
import com.qxs.generator.web.model.connection.Ssh;

/**
 * 要生成代码的数据库连接信息
 * 
 * @author qixingshen
 * **/
public interface IDatabaseService {
	
	/**
	 * 根据连接参数查询数据库信息
	 * @param connection 连接参数
	 * @return Database 
	 * **/
	Database findByConnection(Connection connection);
	
	/**
	 * 读取数据库列表
	 * @param 数据库连接参数
	 * @param ssh ssh配置信息
	 * @return List<DatabaseName> 数据库信息列表 
	 * **/
	List<DatabaseName> findDatabaseNameList(Database database,Ssh ssh);
	
	/**
	 * 保存链接数据库信息
	 * @param database 数据库信息
	 * **/
	Database saveAndFlush(Database database);
	
	/**
	 * 删除
	 * @param database 数据库信息
	 * @return void
	 * **/
	void delete(Database database);
}
