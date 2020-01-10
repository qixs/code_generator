package com.qxs.database.dialect.mariadb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.stereotype.Service;

import com.qxs.database.dialect.AbstractDialect;
import com.qxs.database.dialect.mysql.MySQLDialect;
import com.qxs.database.model.Column;
import com.qxs.database.model.Database;
import com.qxs.database.model.Table;

/**
 * MariaDB方言
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-23
 * @version Revision: 1.0
 */
@Service("mariadbDialect")
public class MariaDBDialect extends AbstractDialect {
	/**
	 * 获取数据库sql文件名
	 * **/
	private static final String SQL_DATABASE_FILE_NAME = "database.sql";
	/**
	 * 获取所有的表名sql文件名
	 * **/
	private static final String SQL_ALL_TABLE_NAMES_FILE_NAME = "all_table_names.sql";
	/**
	 * 获取表信息sql文件名
	 * **/
	private static final String SQL_TABLE_FILE_NAME = "table.sql";
	/**
	 * 获取列信息sql文件名
	 * **/
	private static final String SQL_COLUMNS_FILE_NAME = "columns.sql";
	/**
	 * 获取数据库名信息sql
	 * **/
	private static final String SQL_DATABASE;
	/**
	 * 获取所有的表名sql
	 * **/
	private static final String SQL_ALL_TABLE_NAMES;
	/**
	 * 获取表信息sql
	 * **/
	private static final String SQL_TABLE;
	/**
	 * 获取列信息sql
	 * **/
	private static final String SQL_COLUMNS;
	
	private static final List<String> IGNORE_DATABASE_NAME_LIST = 
			Arrays.asList("information_schema", "mysql", "performance_schema");
	
	static {
		SQL_DATABASE = read(MySQLDialect.class,SQL_DATABASE_FILE_NAME);
		SQL_ALL_TABLE_NAMES = read(MariaDBDialect.class,SQL_ALL_TABLE_NAMES_FILE_NAME);
		SQL_TABLE = read(MariaDBDialect.class,SQL_TABLE_FILE_NAME);
		SQL_COLUMNS = read(MariaDBDialect.class,SQL_COLUMNS_FILE_NAME);
	}
	
	@Override
	public List<Database> getDatabases(Connection connection, String databaseVersion) throws SQLException {
		SqlRunner sqlRunner = new SqlRunner(connection);
		List<Map<String, Object>> list = sqlRunner.selectAll(SQL_DATABASE);
		List<Database> databases = new ArrayList<Database>(list.size());
		for(Map<String, Object> map : list) {
			String databaseName = map.values().iterator().next().toString();
			if(IGNORE_DATABASE_NAME_LIST.contains(databaseName)) {
				continue;
			}
			
			databases.add(new Database(databaseName, databaseName, null));
		}
		return databases;
	}
	
	@Override
	public final List<String> getAllTableNames(Connection connection,String databaseName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);
		List<Map<String, Object>> list = sqlRunner.selectAll(getAllTableNamesSql(),databaseName);
		List<String> tableNames = new ArrayList<String>(list.size());
		for(Map<String, Object> map : list) {
			tableNames.add(map.values().iterator().next().toString());
		}
		return tableNames;
	}
	
	@Override
	public final Table getTable(Connection connection,String databaseName,String tableName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);
		Map<String, Object> map = sqlRunner.selectOne(getTableSql(),databaseName,tableName);
		return map2Bean(Table.class, map);
	}

	@Override
	public final List<Column> getColumns(Connection connection,String databaseName,String tableName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);
		List<Map<String, Object>> list = sqlRunner.selectAll(getColumnsSql(),databaseName,tableName);
		
		List<Column> columns = new ArrayList<Column>(list.size());
		for(Map<String, Object> map : list){
			columns.add(map2Bean(Column.class, map));
		}
		return columns;
	}
	
	@Override
	protected String getAllTableNamesSql() {
		return SQL_ALL_TABLE_NAMES;
	}

	@Override
	protected String getTableSql() {
		return SQL_TABLE;
	}

	@Override
	protected String getColumnsSql() {
		return SQL_COLUMNS;
	}
	@Override
	public String extractDatabaseName(String url) {
		//jdbc:mysql://localhost:3306/test?characterEncoding=UTF-8
		//mysql第一个问号(?)之前的最后一个斜杠(/)之后到第一个问号(?)之前的即为数据库名称
		String temp = url.substring(0,url.indexOf("?"));
		return temp.substring(temp.lastIndexOf("/") + 1);
	}
}
