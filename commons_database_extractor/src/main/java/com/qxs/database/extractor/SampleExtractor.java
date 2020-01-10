package com.qxs.database.extractor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.qxs.base.database.config.DataTypeConfig;
import com.qxs.database.dialect.IDialect;
import com.qxs.database.model.Column;
import com.qxs.database.model.Database;
import com.qxs.database.model.Table;

/**
 * 数据抽取实现类
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-22
 * @version Revision: 1.0
 */
@Service
public class SampleExtractor implements IExtractor{
		
	private final transient Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 需要忽略的系统表
	 * **/
	private final static Map<String,List<String>> IGNORE_TABLE_NAMES;

	static{
		Map<String,List<String>> map = new HashMap<>();

		//sqlite
		map.put("sqlite", Lists.newArrayList("sqlite_sequence"));
		//postgresql
		map.put("postgresql", Lists.newArrayList("ha_health_check"));

		IGNORE_TABLE_NAMES = Collections.unmodifiableMap(map);
	}
	
	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public List<Database> extractorDatabases(DataSource dataSource) throws SQLException{
		Assert.notNull(dataSource,"dataSource不能为空");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			logger.info("connection ClassLoader:[{}]"+dataSource.getClass().getClassLoader().getClass().getName());
			//根据数据库连接匹配数据库类型
			DatabaseMetaData metaData = connection.getMetaData();
			
			String databaseType = metaData.getDatabaseProductName();
			String databaseVersion = metaData.getDatabaseProductVersion();
			
			//数据库连接
			String databaseUrl = metaData.getURL();
			
			logger.debug("数据库类型:{} 数据库版本:{} 数据库连接:{}",databaseType,databaseVersion,databaseUrl);
			
			//获取数据库方言
			//sqlserver获取到的数据库类型是[Microsoft SQL Server],需要删除所有的空格,否则无法获取到bean
			IDialect dialect = applicationContext.getBean(databaseType.toLowerCase().replaceAll("\\s", "")+"Dialect",IDialect.class);
			
			logger.debug("数据库方言:{}",dialect.getClass().getName());
			
			return dialect.getDatabases(connection, databaseVersion);
		}finally {
			try {
				if(connection != null && !connection.isClosed()) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				logger.error("数据库连接释放失败:{}",connection);
			}
		}
	}

	@Override
	public List<com.qxs.base.model.Table> extractorTables(DataSource dataSource){
		return extractorTables(dataSource, (String)null);
	}

	@Override
	public List<com.qxs.base.model.Table> extractorTables(DataSource dataSource, String extractorTableName){
		return StringUtils.hasLength(extractorTableName) ? 
				extractorTables(dataSource, new String[] {extractorTableName}) 
				: extractorTables(dataSource, new String[] {});
	}

	@Override
	public List<com.qxs.base.model.Table> extractorTables(DataSource dataSource,String[] extractorTableNames){
		Assert.notNull(dataSource,"dataSource不能为空");
		Connection connection = null;
		List<String> tableNames = null;
		try {
			connection = dataSource.getConnection();
			logger.info("connection ClassLoader:[{}]"+dataSource.getClass().getClassLoader().getClass().getName());
			//根据数据库连接匹配数据库类型
			DatabaseMetaData metaData = connection.getMetaData();
			
			String databaseType = metaData.getDatabaseProductName();
			String databaseVersion = metaData.getDatabaseProductVersion();
			
			//数据库连接
			String databaseUrl = metaData.getURL();
			
			logger.debug("数据库类型:{} 数据库版本:{} 数据库连接:{}",databaseType,databaseVersion,databaseUrl);
			
			//获取数据库方言
			//sqlserver获取到的数据库类型是[Microsoft SQL Server],需要删除所有的空格,否则无法获取到bean
			IDialect dialect = applicationContext.getBean(databaseType.toLowerCase().replaceAll("\\s", "")+"Dialect",IDialect.class);
			
			logger.debug("数据库方言:{}",dialect.getClass().getName());
			
			//数据库名称
			String databaseName = dialect.extractDatabaseName(databaseUrl);
			
			//抽取指定的表
			if(extractorTableNames != null && extractorTableNames.length > 0) {
				tableNames = Arrays.asList(extractorTableNames);
			}else {
				//抽取所有的表,需要从数据库查询出所有的表
				tableNames = dialect.getAllTableNames(connection,databaseName);
				
				//释放数据库连接
				connection.close();
			}
			
			List<com.qxs.base.model.Table> tables = new ArrayList<com.qxs.base.model.Table>(tableNames.size());

			List<String> ignoreTableNames = IGNORE_TABLE_NAMES.get(databaseType.toLowerCase().replaceAll("\\s", ""));
			//循环获取表信息及表结构
			for(String tableName : tableNames) {
				//忽略系统表
				if(ignoreTableNames != null && ignoreTableNames.contains(tableName)){
					continue;
				}
				//获取表信息,包括表注释及表是否是视图
				connection = dataSource.getConnection();
				Table table = dialect.getTable(connection,databaseName,tableName);
				//释放数据库连接
				connection.close();
				
				//获取列信息
				connection = dataSource.getConnection();
				List<Column> columns = dialect.getColumns(connection,databaseName,tableName);
				//释放数据库连接
				connection.close();
				
				List<com.qxs.base.model.Column> cols = new ArrayList<com.qxs.base.model.Column>(columns.size());
				com.qxs.base.model.Column primaryKeyColumn = null;
				for(Column c : columns) {
					com.qxs.base.model.Column col = new com.qxs.base.model.Column(c.getName(),c.getType(),
							DataTypeConfig.getJavaType(databaseType.toLowerCase().replaceAll("\\s", "").toLowerCase(), 
									c.getType()), c.getAutoIncrement(), c.getNullable(),c.getIsPrimaryKey(), 
							c.getDefaultValue(), c.getComment());
					cols.add(col);
					
					if(c.getIsPrimaryKey()) {
						primaryKeyColumn = col;
					}
				}
				
				//如果未找到主键字段则认为第一个字段为主键字段
				if(primaryKeyColumn == null) {
					logger.warn("[{}]表无主键字段,使用第一个字段作为主键字段,字段名:[{}]", tableName, cols.get(0).getName());
					primaryKeyColumn = cols.get(0);
				}
				
				tables.add(new com.qxs.base.model.Table(table.getName(), table.getComment(), table.getView(), cols, primaryKeyColumn));
			}
			
			return tables;
		}catch(SQLException e) {
			logger.error("数据库表抽取失败:{}",tableNames,e);
			throw new RuntimeException(e);
		}finally {
			try {
				if(connection != null && !connection.isClosed()) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				logger.error("数据库连接释放失败:{}",connection);
			}
		}
	}
}
