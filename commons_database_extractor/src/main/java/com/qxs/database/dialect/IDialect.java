package com.qxs.database.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.qxs.database.model.Column;
import com.qxs.database.model.Database;
import com.qxs.database.model.Table;

/**
 * 数据库方言接口
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-22
 * @version Revision: 1.0
 */
public interface IDialect {
	
	/***
	 * 获取所有库名
	 * @param connection 数据库连接
	 * @param databaseVersion 数据库版本号
	 * @return List<Database> 
	 * **/
	List<Database> getDatabases(Connection connection, String databaseVersion) throws SQLException;
	/***
	 * 获取所有的表名
	 * @param connection 数据库连接
	 * @param databaseName 数据库名称
	 * @return List<String> 
	 * **/
	List<String> getAllTableNames(Connection connection,String databaseName) throws SQLException;
	
	/**
	 * 获取表信息
	 * @param connection 数据库连接
	 * @param databaseName 数据库名称
	 * @param tableName 表名
	 * @return Table
	 * **/
	Table getTable(Connection connection,String databaseName,String tableName) throws SQLException;
	
	/**
	 * 获取列信息
	 * @param connection 数据库连接
	 * @param databaseName 数据库名称
	 * @param tableName 表名
	 * @return List<Column>
	 * **/
	List<Column> getColumns(Connection connection,String databaseName,String tableName) throws SQLException;
	/***
	 * 抽取数据库名称
	 * @param url 数据库连接
	 * @return String 
	 * **/
	String extractDatabaseName(String url);
}
