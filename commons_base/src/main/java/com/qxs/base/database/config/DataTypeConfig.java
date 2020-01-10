package com.qxs.base.database.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 数据库字段和java字段映射配置
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-23
 * @version Revision: 1.0
 */
public class DataTypeConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataTypeConfig.class);

	/**
	 * 字段数据类型配置文件名称
	 **/
	private static final String CONFIG_FILE_NAME = "data_type_config.xml";

	/**
	 * 数据库字段映射配置
	 **/
	private static final Map<String, Map<String, String>> DATA_TYPE_CONFIG;

	static {
		Map<String, Map<String, String>> dataTypeConfig = readDataTypeConfig();

		DATA_TYPE_CONFIG = Collections.unmodifiableMap(dataTypeConfig);

		LOGGER.debug("字段数据类型配置:{}", DATA_TYPE_CONFIG);
	}
	/**
	 * 读取数据库字段和java字段映射配置
	 * **/
	private static Map<String, Map<String, String>> readDataTypeConfig() {
		Map<String, Map<String, String>> dataTypeConfig = new HashMap<String, Map<String, String>>();
		
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
				
				NodeList fieldTypeNodeList = dbTypeNode.getChildNodes();
				Map<String,String> fieldTypeMap = new LinkedCaseInsensitiveMap<String>();
				for(int k = 0; k < fieldTypeNodeList.getLength(); k++) {
					Node fieldTypeNode = fieldTypeNodeList.item(k);
					//如果是element
					if(fieldTypeNode.getNodeType() == Node.ELEMENT_NODE) {
						NamedNodeMap attributes = fieldTypeNode.getAttributes();
						String jdbcType = attributes.getNamedItem("jdbcType").getNodeValue().trim();
						
						String javaType = attributes.getNamedItem("javaType").getNodeValue().trim();
						
						if(StringUtils.hasLength(jdbcType) && StringUtils.hasLength(javaType)){
							if(fieldTypeMap.containsKey(jdbcType)) {
								LOGGER.error("已经存在字段类型,数据库类型:{},jdbcType:{},javaType:{}",dbType,jdbcType,fieldTypeMap.get(jdbcType));
								throw new RuntimeException("已经存在字段类型");
							}
							fieldTypeMap.put(jdbcType, javaType);
						}
					}
					
				}
				
				dataTypeConfig.put(dbType, fieldTypeMap);
			}
		} catch (XPathExpressionException e) {
			LOGGER.error("字段数据类型配置文件读取失败",e);
			throw new RuntimeException(e);
		} 
		
		return dataTypeConfig;
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
	 * 获取数据库字段和java字段映射配置
	 * @param dbType 数据库类型
	 * @param fieldDataType fieldDataType 字段类型
	 * @return String
	 * **/
	public static String getJavaType(String dbType,String fieldDataType){
		Assert.notNull(dbType,"dbType参数不能为空");
		Assert.notNull(fieldDataType,"fieldDataType参数不能为空");
		
		if(!DATA_TYPE_CONFIG.containsKey(dbType)) {
			return null;
		}
		return DATA_TYPE_CONFIG.get(dbType).get(fieldDataType);
	}
}
