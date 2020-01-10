package com.qxs.database.dialect.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.qxs.database.dialect.AbstractDialect;
import com.qxs.database.model.Column;
import com.qxs.database.model.Table;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

/**
 * SQLite方言
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-22
 * @version Revision: 1.0
 */
@Service("sqliteDialect")
public class SQLiteDialect extends AbstractDialect {
	/**
	 * 需要忽略的系统表
	 * **/
	private static final List<String> IGNORE_TABLE_NAMES = Arrays.asList(new String[] {"sqlite_sequence"});
	
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
		SQL_ALL_TABLE_NAMES = read(SQLiteDialect.class,SQL_ALL_TABLE_NAMES_FILE_NAME);
		SQL_TABLE = read(SQLiteDialect.class,SQL_TABLE_FILE_NAME);
		SQL_COLUMNS = read(SQLiteDialect.class,SQL_COLUMNS_FILE_NAME);
	}

	@Override
	public final List<String> getAllTableNames(Connection connection,String databaseName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);
		List<Map<String, Object>> list = sqlRunner.selectAll(getAllTableNamesSql());
		List<String> tableNames = new ArrayList<String>(list.size());
		for(Map<String, Object> map : list) {
			String tableName = map.values().iterator().next().toString();
			if(IGNORE_TABLE_NAMES.contains(tableName)) {
				continue;
			}
			tableNames.add(tableName);
		}
		return tableNames;
	}
	
	@Override
	public final Table getTable(Connection connection,String databaseName,String tableName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);
		Map<String, Object> map = sqlRunner.selectOne(getTableSql(),tableName);
		return map2Bean(Table.class, map);
	}

	@Override
	public final List<Column> getColumns(Connection connection,String databaseName,String tableName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);

		Map<String,List<String>> columnSpecStrings = new LinkedCaseInsensitiveMap<>();
		try {
			String createTableSql = "select sql from sqlite_master where tbl_name='" + tableName + "' and type='table'";
			List<Map<String, Object>> l = sqlRunner.selectAll(createTableSql);
			if(null == l || l.isEmpty()){
				return null;
			}
			CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(l.get(0).get("SQL").toString());
			List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
			for(ColumnDefinition columnDefinition : columnDefinitions){
				String columnName = columnDefinition.getColumnName();
				columnName = columnName.startsWith("\"") ? columnName.substring(1) : columnName;
				columnName = columnName.endsWith("\"") ? columnName.substring(0,columnName.length() - 1) : columnName;
				columnSpecStrings.put(columnName,columnDefinition.getColumnSpecStrings());
			}
		} catch (JSQLParserException e) {
			e.printStackTrace();
		}

		List<Map<String, Object>> list = sqlRunner.selectAll(getColumnsSql().replace("?", tableName));

		List<Column> columns = new ArrayList<Column>(list.size());
		for(Map<String, Object> map : list){
			Column column = new Column();
			Map<String,Object> tempMap = new LinkedCaseInsensitiveMap<Object>(map.size());
			tempMap.putAll(map);
			map = tempMap;
			
			String type = map.get("type").toString();
			
			column.setName(map.get("name").toString());
			column.setType(type.indexOf("(") > 0 ? type.substring(0,type.indexOf("(")) : type);
			column.setAutoIncrement(containsIgnoreCase(columnSpecStrings.get(column.getName()),"AUTOINCREMENT"));
			column.setNullable("1".equals(map.get("notnull").toString().trim()) ? false : true);
			column.setIsPrimaryKey("1".equals(map.get("pk").toString().trim()) ? true : false);
			column.setDefaultValue(map.get("dflt_value") == null ? null : map.get("dflt_value").toString());
			column.setComment("");
			
			columns.add(column);
		}
		return columns;
	}

	private boolean containsIgnoreCase(List<String> list,String str){
		if(null == list){
			return false;
		}
		for(String l : list){
			if(l.trim().equalsIgnoreCase(str)){
				return true;
			}
		}
		return false;
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
}
