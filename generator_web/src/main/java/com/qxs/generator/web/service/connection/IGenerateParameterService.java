package com.qxs.generator.web.service.connection;

import java.util.List;

import com.qxs.database.model.Table;
import com.qxs.generator.web.model.connection.Connection;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;

/**
 * 生成代码参数
 * 
 * @author qixingshen
 * **/
public interface IGenerateParameterService {
	/**
	 * 根据连接参数查询生成参数信息
	 * @param connection 连接参数
	 * @return GenerateParameter 
	 * **/
	GenerateParameter findByConnection(Connection connection);
	/**
	 * 保存链接生成参数信息
	 * @param generateParameter 生成参数信息
	 * **/
	GenerateParameter saveAndFlush(GenerateParameter generateParameter);

	/**
	 * 删除
	 * @param generateParameter 生成参数信息
	 * @return void
	 * **/
	void delete(GenerateParameter generateParameter);
	/**
	 * 查询数据库下的表
	 * @param database 数据库配置信息
	 * @param ssh ssh配置信息
	 * **/
	List<Table> findTables(Database database,Ssh ssh);
}
