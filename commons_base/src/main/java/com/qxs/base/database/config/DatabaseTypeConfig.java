package com.qxs.base.database.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 数据库类型配置信息
 * **/
public class DatabaseTypeConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTypeConfig.class);

	/**
	 * 数据库类型配置文件名称
	 **/
	private static final String CONFIG_FILE_NAME = "database_type_config.xml";

	/**
	 * 数据库字段映射配置
	 **/
	private static List<DatabaseType> DATABASE_TYPE_LIST;

	static {
		List<DatabaseType> databaseTypeList = readDatabaseTypeConfig();

		DATABASE_TYPE_LIST = Collections.unmodifiableList(databaseTypeList);

		LOGGER.debug("数据库类型配置:{}", DATABASE_TYPE_LIST);
	}
	/**
	 * 读取数据库类型配置信息
	 * **/
	private static List<DatabaseType> readDatabaseTypeConfig() {
		List<DatabaseType> databaseTypeList = new ArrayList<>();
		
		Document document = getDocument();
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			Node configNode = ((NodeList) xPath.evaluate("config", document,XPathConstants.NODESET)).item(0);
			//数据库类型list
			NodeList dbTypeNodeList = configNode.getChildNodes();
			for (int i = 0; i < dbTypeNodeList.getLength(); i++) {
				Node dbTypeNode = dbTypeNodeList.item(i);
				if(dbTypeNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				//数据库类型  如mysql  oracle
				String dbType = dbTypeNode.getNodeName();
				
				Node typeNameNode = ((NodeList) xPath.evaluate("typeName", dbTypeNode,XPathConstants.NODESET)).item(0);
				String typeName = typeNameNode.getTextContent().trim();
				Node driverNode = ((NodeList) xPath.evaluate("driver", dbTypeNode,XPathConstants.NODESET)).item(0);
				String driver = driverNode.getTextContent().trim();
				Node portNode = ((NodeList) xPath.evaluate("port", dbTypeNode,XPathConstants.NODESET)).item(0);
				String port = portNode.getTextContent().trim();
				Node usernameNode = ((NodeList) xPath.evaluate("username", dbTypeNode,XPathConstants.NODESET)).item(0);
				String username = usernameNode.getTextContent().trim();
				Node validSqlNode = ((NodeList) xPath.evaluate("validSql", dbTypeNode,XPathConstants.NODESET)).item(0);
				String validSql = validSqlNode.getTextContent().trim();
				Node defaultDatabaseNameNode = ((NodeList) xPath.evaluate("defaultDatabaseName", dbTypeNode,XPathConstants.NODESET)).item(0);
				String defaultDatabaseName = defaultDatabaseNameNode.getTextContent().trim();
				
				Node databaseWidgetTypeNode = ((NodeList) xPath.evaluate("databaseWidgetType", dbTypeNode,XPathConstants.NODESET)).item(0);
				String databaseWidgetType = databaseWidgetTypeNode.getTextContent().trim();
				
				WidgetType widgetType = WidgetType.valueOf(databaseWidgetType.toUpperCase().trim());
				
				Node databaseWidgetDescNode = ((NodeList) xPath.evaluate("databaseWidgetDesc", dbTypeNode,XPathConstants.NODESET)).item(0);
				String databaseWidgetDesc = databaseWidgetDescNode.getTextContent().trim();
				
				databaseTypeList.add(
						new DatabaseType(typeName,dbType, driver,Integer.valueOf(port), username, validSql, defaultDatabaseName, widgetType, databaseWidgetDesc)
						);
			}
		} catch (XPathExpressionException e) {
			LOGGER.error("字段数据类型配置文件读取失败",e);
			throw new RuntimeException(e);
		} 
		
		return databaseTypeList;
	}
	
	private static Document getDocument() {
		String path = DataTypeConfig.class.getPackage().getName().replaceAll("\\.", "/");
		InputStream inputStream = DataTypeConfig.class
				.getResourceAsStream(String.format("/%s/%s", path, CONFIG_FILE_NAME));
		
		Document document = null;
		try {
			DocumentBuilder dbd = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = dbd.parse(inputStream);
		} catch (ParserConfigurationException e) {
			LOGGER.error("字段数据类型配置文件读取失败",e);
		} catch (SAXException e) {
			LOGGER.error("字段数据类型配置文件读取失败",e);
		} catch (IOException e) {
			LOGGER.error("字段数据类型配置文件读取失败",e);
		}
		
		return document;
	}
	
	/**
	 * 获取所有的数据库配置信息
	 * 
	 * @return List<DatabaseType>
	 * **/
	public static List<DatabaseType> getDatabaseTypeConfig(){
		return DATABASE_TYPE_LIST;
	}
	
	/**
	 * 根据数据库类型获取数据库配置信息
	 * 
	 * @return DatabaseType
	 * **/
	public static DatabaseType getDatabaseType(String dbType){
		return DATABASE_TYPE_LIST.stream().filter(databaseType -> databaseType.getDbType().equals(dbType)).findFirst().orElseThrow(()-> new RuntimeException("不存在的数据库类型:" + dbType));
	}
	
	public final static class DatabaseType{
		/**
		 * 数据库类型
		 * **/
		private String dbType;
		
		private String typeName;
		/**
		 * 驱动
		 * **/
		private String driver;
		/**
		 * 端口
		 * **/
		private int port;
		/**
		 * 用户名
		 * **/
		private String username;
		/**
		 * 校验sql
		 * **/
		private String validSql;
		/**
		 * 默认连接的数据库（mysql）
		 * **/
		private String defaultDatabaseName;
		/**
		 * 数据库字段控件类型
		 * **/
		private WidgetType databaseWidgetType;
		/**
		 * 数据库字段控件类型描述
		 * **/
		private String databaseWidgetDesc;
		
		public DatabaseType(String typeName,String dbType,String driver,int port,String username,String validSql,String defaultDatabaseName, 
				WidgetType databaseWidgetType, String databaseWidgetDesc) {
			this.dbType = dbType;
			this.typeName = typeName;
			this.driver = driver;
			this.port = port;
			this.username = username;
			this.validSql = validSql;
			this.defaultDatabaseName = defaultDatabaseName;
			this.databaseWidgetType = databaseWidgetType;
			this.databaseWidgetDesc = databaseWidgetDesc;
		}
		
		public String getDbType() {
			return dbType;
		}

		public String getTypeName() {
			return typeName;
		}

		public String getDriver() {
			return driver;
		}
		public int getPort() {
			return port;
		}
		public String getUsername() {
			return username;
		}

		public String getValidSql() {
			return validSql;
		}

		public String getDefaultDatabaseName() {
			return defaultDatabaseName;
		}

		public WidgetType getDatabaseWidgetType() {
			return databaseWidgetType;
		}

		public String getDatabaseWidgetDesc() {
			return databaseWidgetDesc;
		}

		@Override
		public String toString() {
			return "DatabaseType [dbType=" + dbType + ", typeName=" + typeName + ", driver=" + driver + ", port=" + port
					+ ", username=" + username + ", validSql=" + validSql + ", defaultDatabaseName="
					+ defaultDatabaseName + ", databaseWidgetType=" + databaseWidgetType + ", databaseWidgetDesc="
					+ databaseWidgetDesc + "]";
		}
	}
	
	public enum WidgetType{
		SELECT,INPUT;
	}
}
