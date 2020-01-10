package com.qxs.generator.web.service.connection;

import java.util.List;

import com.qxs.generator.web.model.connection.Connection;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;

/**
 * 生成代码连接记录
 * 
 * @author qixingshen
 **/
public interface IConnectionService {

	/**
	 * 获取属于当前用户的所有的连接信息列表
	 * 
	 * @return List<Connection>
	 **/
	List<Connection> listByUser();

	/**
	 * 根据id查询链接信息
	 * @param id 链接id
	 * 
	 * @return Connection 
	 * **/
	Connection getById(String id);
	/**
	 * 新增链接
	 * 
	 * @param connection 连接信息
	 * @param database 数据库配置信息
	 * @param ssh ssh配置信息
	 * @param generateParameter 生成参数
	 **/
	String insert(Connection connection, Database database, Ssh ssh, GenerateParameter generateParameter);

	/**
	 * 更新链接
	 * 
	 * @param connection 连接信息
	 * @param database  数据库配置信息
	 * @param ssh ssh配置信息
	 * @param generateParameter 生成参数
	 **/
	String update(Connection connection, Database database, Ssh ssh, GenerateParameter generateParameter);
	
	/**
	 * 删除链接
	 * @param id 链接id
	 * @return int 删除成功条数
	 * **/
	void deleteById(String id);

	/**
	 * 测试连接是否成功
	 * @param database 数据库配置信息
	 * @param ssh ssh配置信息
	 * **/
	void validConnection(Database database,Ssh ssh);
		
}
