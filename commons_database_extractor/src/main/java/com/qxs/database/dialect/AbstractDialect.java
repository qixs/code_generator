package com.qxs.database.dialect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.sqlite.date.DateFormatUtils;
import org.yaml.snakeyaml.reader.UnicodeReader;

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
public abstract class AbstractDialect implements IDialect {
	
	public static final Logger logger = LoggerFactory.getLogger(AbstractDialect.class);
	
	@Override
	public List<Database> getDatabases(Connection connection, String databaseVersion) throws SQLException {
		throw new UnsupportedOperationException("不支持的方法");
	}
	
	/**
	 * 获取所有表名称列表查询sql
	 * @return String 
	 * **/
	protected abstract String getAllTableNamesSql();
	
	@Override
	public List<String> getAllTableNames(Connection connection,String databaseName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);
		List<Map<String, Object>> list = sqlRunner.selectAll(getAllTableNamesSql());
		List<String> tableNames = new ArrayList<String>(list.size());
		for(Map<String, Object> map : list) {
			tableNames.add(map.values().iterator().next().toString());
		}
		return tableNames;
	}
	
	/**
	 * 获取所有表名称列表查询sql
	 * @return String 
	 * **/
	protected abstract String getTableSql();
	
	@Override
	public Table getTable(Connection connection,String databaseName,String tableName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);
		Map<String, Object> map = sqlRunner.selectOne(getTableSql(),tableName);
		return map2Bean(Table.class, map);
	}

	/**
	 * 获取所有表名称列表查询sql
	 * @return String 
	 * **/
	protected abstract String getColumnsSql();
	
	@Override
	public List<Column> getColumns(Connection connection,String databaseName,String tableName) throws SQLException{
		SqlRunner sqlRunner = new SqlRunner(connection);
		List<Map<String, Object>> list = sqlRunner.selectAll(getColumnsSql(),tableName);
		
		List<Column> columns = new ArrayList<Column>(list.size());
		for(Map<String, Object> map : list){
			columns.add(map2Bean(Column.class, map));
		}
		return columns;
	}
	
	/***
	 * 读取sql
	 * 
	 * **/
	protected static final String read(Class<?> clazz,String fileName) {
		String path = clazz.getPackage().getName().replaceAll("\\.", "/");
		InputStream inputStream = clazz.getResourceAsStream(String.format("/%s/%s", path,fileName));
		
		StringBuilder sb = new StringBuilder();
		
		UnicodeReader reader = new UnicodeReader(inputStream);
		
		BufferedReader in = new BufferedReader(reader);
		String str = "";
		try {
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RuntimeException(e);
		} finally{
			try {
				if(in != null) {
					in.close();
					in = null;
				}
				if(reader != null) {
					reader.close();
					reader = null;
				}
				if(inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/***
	 * 把map转换为实体
	 * @param clazz 要转换的实体的class
	 * @param map 
	 * @return T 实体
	 * **/
	protected <T> T map2Bean(Class<T> clazz,Map<String,Object> map){
		Map<String,Object> tempMap = new LinkedCaseInsensitiveMap<Object>(map.size());
		tempMap.putAll(map);
		map = tempMap;
		
		T t = null;
		try {
			t = clazz.newInstance();
		} catch (InstantiationException e1) {
			logger.error(e1.getMessage(),e1);
			throw new RuntimeException(e1);
		} catch (IllegalAccessException e1) {
			logger.error(e1.getMessage(),e1);
			throw new RuntimeException(e1);
		}
		Field[] fields = t.getClass().getDeclaredFields();
		for(Field field : fields) {
			//不可修改的
			if(Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			//字段名
			String fieldName = field.getName();
			//字段值
			Object value = map.get(fieldName);
			if(value != null) {
				//赋值方法(setter)
				String setMethodName = "set"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
				
				try {
					Method setMethod = clazz.getDeclaredMethod(setMethodName, value.getClass());
					setMethod.invoke(t, value);
				} catch (NoSuchMethodException e) {
					logger.error(String.format("对象转换失败,class:%s map:%s",clazz,map),e);
				} catch (SecurityException e) {
					logger.error(String.format("对象转换失败,class:%s map:%s",clazz,map),e);
				} catch (IllegalAccessException e) {
					logger.error(String.format("对象转换失败,class:%s map:%s",clazz,map),e);
				} catch (IllegalArgumentException e) {
					logger.error(String.format("对象转换失败,class:%s map:%s",clazz,map),e);
				} catch (InvocationTargetException e) {
					logger.error(String.format("对象转换失败,class:%s map:%s",clazz,map),e);
				}
				
			}
		}
		return t;
	}

	@Override
	public String extractDatabaseName(String url) {
		//SQLite数据库不需要数据库名
		return null;
	}
	
	/**
	 * 计算当前时区,返回+8:00
	 * **/
	protected String getTimeZone() {
		return DateFormatUtils.format(new Date(), "ZZ");
	}
}
