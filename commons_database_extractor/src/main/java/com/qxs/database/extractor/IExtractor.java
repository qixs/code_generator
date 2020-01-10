package com.qxs.database.extractor;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.qxs.base.model.Table;
import com.qxs.database.model.Database;


/**
 * 数据抽取接口
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-22
 * @version Revision: 1.0
 */
public interface IExtractor {
	/**
	 * 抽取数据库列表
	 * @param dataSource 数据库连接池
	 * @return List<Database> 数据库列表
	 * **/
	List<Database> extractorDatabases(DataSource dataSource) throws SQLException;
	/**
	 * 抽取表结构
	 * @param dataSource 数据库连接池
	 * @return List<Table>
	 * **/
	List<Table> extractorTables(DataSource dataSource);
	/**
	 * 抽取表结构
	 * @param dataSource 数据库连接池
	 * @param extractorTableName 要抽取的表名称
	 * @return List<Table>
	 * **/
	List<Table> extractorTables(DataSource dataSource,String extractorTableName);
	/**
	 * 抽取表结构
	 * @param dataSource 数据库连接池
	 * @param extractorTableNames 要抽取的表名称数组
	 * @return List<Table>
	 * **/
	List<Table> extractorTables(DataSource dataSource,String[] extractorTableNames);
}
