package com.qxs.database.dialect.sqlserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.stereotype.Service;

import com.qxs.database.dialect.AbstractDialect;
import com.qxs.database.model.Database;

/**
 * SQLServer方言
 * 
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @date 2012-7-18下午01:27:44
 * @version Revision: 1.0
 */
@Service("microsoftsqlserverDialect")
public class SqlServerDialect extends AbstractDialect {
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
	
	static {
		SQL_DATABASE = read(SqlServerDialect.class,SQL_DATABASE_FILE_NAME);
		SQL_ALL_TABLE_NAMES = read(SqlServerDialect.class,SQL_ALL_TABLE_NAMES_FILE_NAME);
		SQL_TABLE = read(SqlServerDialect.class,SQL_TABLE_FILE_NAME);
		SQL_COLUMNS = read(SqlServerDialect.class,SQL_COLUMNS_FILE_NAME);
	}
	public SqlServerDialect() {
		super();
	}

	@Override
	public List<Database> getDatabases(Connection connection, String databaseVersion) throws SQLException {
		SqlRunner sqlRunner = new SqlRunner(connection);
		List<Map<String, Object>> list = sqlRunner.selectAll(SQL_DATABASE);
		List<Database> databases = new ArrayList<Database>(list.size());
		for(Map<String, Object> map : list) {
			String databaseName = map.values().iterator().next().toString();
			databases.add(new Database(databaseName, databaseName, null));
		}
		return databases;
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
		
		return null;
	}
	
}
