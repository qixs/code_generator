//package com.qxs.database.extractor;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.qxs.base.model.Column;
//import com.qxs.base.model.Table;
//
//
///**
// * @author qixingshen
// * **/
////@RunWith(SpringJUnit4ClassRunner.class)
////@ContextConfiguration({"classpath*:test.xml"}) //加载配置文件  
//public class SampleExtractorTest {
//	
//	@Autowired
//	private IExtractor extractor;
//	
////	@Test
//	public void extractorTablesMysql() {
//		DruidDataSource dataSource = new DruidDataSource();
//		dataSource.setUrl("jdbc:mysql://211.159.174.152:10000/test?characterEncoding=UTF-8");
//		dataSource.setUsername("test");
//		dataSource.setPassword("q123456Q!");
//		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//		
//		List<Table> tables = extractor.extractorTables(dataSource);
//		
//		Assert.assertNotNull(tables);
//		Assert.assertTrue(tables.size() > 0);
//		
//		for(Table table : tables) {
//			List<Column> columns = table.getColumns();
//			for(Column column : columns) {
//				String clazz = column.getJavaType();
//				Assert.assertNotNull(clazz);
//			}
//		}
//	}
//	@Test
//	public void extractorTablesOracle() {
//		DruidDataSource dataSource = new DruidDataSource();
//
//		dataSource.setUrl("jdbc:oracle:thin:@172.20.109.110:1521:XE");
//		dataSource.setUsername("c##root");
//		dataSource.setPassword("root");
//		dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
//		
//		List<Table> tables = extractor.extractorTables(dataSource);
//		
//		Assert.assertNotNull(tables);
//		Assert.assertTrue(tables.size() > 0);
//		
//		for(Table table : tables) {
//			List<Column> columns = table.getColumns();
//			for(Column column : columns) {
//				String clazz = column.getJavaType();
//				if(clazz == null) {
//					throw new RuntimeException();
//				}
//				Assert.assertNotNull(clazz);
//			}
//		}
//	}
////	@Test
//	public void extractorTablesSqlserver() {
//		DruidDataSource dataSource = new DruidDataSource();
//
//		dataSource.setUrl("jdbc:sqlserver://192.168.88.128:1433;DatabaseName=test");
//		dataSource.setUsername("sa");
//		dataSource.setPassword("123456");
//		dataSource.setValidationQueryTimeout(60);
//		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		
//		List<Table> tables = extractor.extractorTables(dataSource);
//		
//		Assert.assertNotNull(tables);
//		Assert.assertTrue(tables.size() > 0);
//		
//		for(Table table : tables) {
//			List<Column> columns = table.getColumns();
//			for(Column column : columns) {
//				String clazz = column.getJavaType();
//				if(clazz == null) {
//					throw new RuntimeException();
//				}
//				Assert.assertNotNull(clazz);
//			}
//		}
//	}
//	
//	
//	@Test
//	public void extractorTablesMariadb() {
//		DruidDataSource dataSource = new DruidDataSource();
//
//		dataSource.setUrl("jdbc:mysql://211.159.174.152:10000/test?characterEncoding=UTF-8");
//		dataSource.setUsername("test");
//		dataSource.setPassword("q123456Q!");
//		dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
//		
//		List<Table> tables = extractor.extractorTables(dataSource);
//		
//		Assert.assertNotNull(tables);
//		Assert.assertTrue(tables.size() > 0);
//		
//		for(Table table : tables) {
//			List<Column> columns = table.getColumns();
//			for(Column column : columns) {
//				String clazz = column.getJavaType();
//				if(clazz == null) {
//					throw new RuntimeException();
//				}
//				Assert.assertNotNull(clazz);
//			}
//		}
//	}
//	
////	@Test
//	public void extractorTablesPostgresql() {
//		DruidDataSource dataSource = new DruidDataSource();
//
//		dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
//		dataSource.setUsername("qxs");
////		dataSource.setPassword("123456");
//		dataSource.setDriverClassName("org.postgresql.Driver");
//		
//		List<Table> tables = extractor.extractorTables(dataSource);
//		
//		Assert.assertNotNull(tables);
//		Assert.assertTrue(tables.size() > 0);
//		
//		for(Table table : tables) {
//			List<Column> columns = table.getColumns();
//			for(Column column : columns) {
//				String clazz = column.getJavaType();
//				if(clazz == null) {
//					throw new RuntimeException();
//				}
//				Assert.assertNotNull(clazz);
//			}
//		}
//	}
//	@Test
//	public void extractorTablesSqlite() {
//		Connection connection = initSqliteDb();
//		
//	    Assert.assertNotNull(connection);
//		
//		DruidDataSource dataSource = new DruidDataSource();
//
//		dataSource.setUrl("jdbc:sqlite:test_sqlite.db");
//		dataSource.setDriverClassName("org.sqlite.JDBC");
//		
//		List<Table> tables = extractor.extractorTables(dataSource);
//		
//		Assert.assertNotNull(tables);
//		Assert.assertTrue(tables.size() > 0);
//		
//		for(Table table : tables) {
//			List<Column> columns = table.getColumns();
//			for(Column column : columns) {
//				String clazz = column.getJavaType();
//				if(clazz == null) {
//					throw new RuntimeException();
//				}
//				Assert.assertNotNull(clazz);
//			}
//		}
//	}
//	
//	private Connection initSqliteDb() {
//		Connection c = null;
//	    try {
//	      Class.forName("org.sqlite.JDBC");
//	      c = DriverManager.getConnection("jdbc:sqlite:test_sqlite.db");
//	      
//	      if(tableIsExists(c, "test")) {
//	    	  //表存在则删除重建
//	    	  String sql = "drop table test";
//	    	  Statement stmt = c.createStatement();
//	    	  stmt.executeUpdate(sql);
//	    	  stmt.close();
//	      }
//	      
//	      Statement stmt = c.createStatement();
//	      String sql = "CREATE TABLE \"test\" ("
//	      		+ "\"integer\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
//	      		+ "\"text\" text(100) NOT NULL,"
//	      		+ "\"blob\" blob(50))";
//	      stmt.executeUpdate(sql);
//	      stmt.close();
//	      
//	    } catch ( Exception e ) {
//	      throw new RuntimeException(e);
//	    }
//	    return c;
//	}
//	
//	private boolean tableIsExists(Connection c,String tableName) throws SQLException {
//		Statement stmt = c.createStatement();
//		//先判断是否已经存在test表,如果存在则删除重建
//		String sql = "select count(*) from sqlite_master where type='table' and name = '"+tableName+"'";
//	    ResultSet rs = stmt.executeQuery(sql);
//	    rs.next();
//	    
//		int count = rs.getInt(1);
//		
//		rs.close();
//		stmt.close();
//		return count > 0;
//	}
//}
