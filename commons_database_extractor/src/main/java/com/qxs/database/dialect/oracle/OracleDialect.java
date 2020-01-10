package com.qxs.database.dialect.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.stereotype.Service;

import com.qxs.database.dialect.AbstractDialect;
import com.qxs.database.model.Column;
import com.qxs.database.model.Table;

/**
 * Oracle方言
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-22
 * @version Revision: 1.0
 */
@Service("oracleDialect")
public class OracleDialect extends AbstractDialect {
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
	
	static {
		SQL_ALL_TABLE_NAMES = read(OracleDialect.class,SQL_ALL_TABLE_NAMES_FILE_NAME);
		SQL_TABLE = read(OracleDialect.class,SQL_TABLE_FILE_NAME);
		SQL_COLUMNS = read(OracleDialect.class,SQL_COLUMNS_FILE_NAME);
	}
	public OracleDialect() {
		super();
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
	public Table getTable(Connection connection,String databaseName,String tableName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);
		Map<String, Object> map = sqlRunner.selectOne(getTableSql(),tableName.toUpperCase());
		return map2Bean(Table.class, map);
	}
	@Override
	public List<Column> getColumns(Connection connection, String databaseName, String tableName) throws SQLException {
		List<Column> columns = super.getColumns(connection, databaseName, tableName.toUpperCase());
		for(Column column : columns) {
			String type = column.getType();
			if(type.indexOf("(") >0 && type.indexOf(")") > 0) {
				type = type.substring(0,type.indexOf("("));
				column.setType(type);
			}
		}
		return columns;
	}

	@Override
	public String extractDatabaseName(String url) {
		//jdbc:oracle:thin:@localhost:1521:testdb
		logger.debug("数据库连接:{}",url);
		//oracle数据库最后一个冒号(:)后面的即为数据库名称
		return url.substring(url.lastIndexOf(":") + 1);
	}
}
