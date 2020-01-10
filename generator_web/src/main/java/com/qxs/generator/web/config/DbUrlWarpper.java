package com.qxs.generator.web.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.reader.UnicodeReader;

import com.google.common.collect.Maps;
import com.qxs.generator.web.exception.BusinessException;

import freemarker.core.InvalidReferenceException;
import freemarker.core.NonHashException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import jodd.io.UnicodeInputStream;

/***
 * 数据库url包装器
 * @author qixingshen
 * @date 2018-06-15
 * **/
public final class DbUrlWarpper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DbUrlWarpper.class);
	
	protected static final Version FREEMARKER_VERSION = Configuration.VERSION_2_3_27;
	
	private static final Map<String,String> URL_FORMAT = new HashMap<>();
	
	static {
		URL_FORMAT.put("mariadb", "jdbc:mysql://${url}:${port}/${databaseName}?characterEncoding=UTF-8&serverTimezone=UTC");
		URL_FORMAT.put("mysql", "jdbc:mysql://${url}:${port}/${databaseName}?characterEncoding=UTF-8&serverTimezone=UTC");
		URL_FORMAT.put("oracle", "jdbc:oracle:thin:@${url}:${port}:${databaseName}");
		URL_FORMAT.put("postgresql", "jdbc:postgresql://${url}:${port}/${databaseName}");
		URL_FORMAT.put("sqlserver", "jdbc:sqlserver://${url}:${port};DatabaseName=${databaseName}");
	}
	
	/**
	 * 包装数据库连接地址
	 * 
	 * @param url 数据库地址
	 * @param port 数据库端口
	 * @param databaseName 数据库名称
	 * 
	 * @return String 数据库连接地址
	 * **/
	public static String warp(String dbType,String url,Integer port,String databaseName) {
		String templateContent = URL_FORMAT.get(dbType.toLowerCase());
		if(templateContent == null) {
			throw new BusinessException("获取数据库连接地址模板出错，数据库类型：" + dbType);
		}
		
		Map<String,Object> dataModel = Maps.newHashMap();
		dataModel.put("url", url == null ? "" : url);
		dataModel.put("port", port == null ? "" : port);
		dataModel.put("databaseName", databaseName == null ? "" : databaseName);
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		ByteArrayInputStream templateByteArrayStream = new ByteArrayInputStream(templateContent.getBytes());
		
		Configuration cfg = new Configuration(FREEMARKER_VERSION);
		//设置数字格式,不自动格式化
		cfg.setNumberFormat("#");
		
		UnicodeReader unicodeReader = new UnicodeReader(new UnicodeInputStream(templateByteArrayStream, "utf-8"));
		
		BufferedReader reader = new BufferedReader(unicodeReader);
		Writer out = null;
		
		try {
			Template template = new Template("", reader, cfg);
			out = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
			
			template.process(dataModel, out);
		}catch(InvalidReferenceException e) {
			LOGGER.error("数据库地址获取失败",e);
			LOGGER.error("数据库类型: {}  数据库连接模板信息: {}",URL_FORMAT.get(dbType));
			throw new BusinessException(e.getMessage(),e);
		}catch(NonHashException e) {
			LOGGER.error("数据库地址获取失败",e);
			LOGGER.error("数据库类型: {}  数据库连接模板信息: {}",URL_FORMAT.get(dbType));
			throw new BusinessException(e.getMessage(),e);
		} catch (IOException e) {
			LOGGER.error("数据库地址获取失败",e);
			LOGGER.error("数据库类型: {}  数据库连接模板信息: {}",URL_FORMAT.get(dbType));
			throw new BusinessException(e.getMessage(),e);
		} catch (TemplateException e) {
			LOGGER.error("数据库地址获取失败",e);
			LOGGER.error("数据库类型: {}  数据库连接模板信息: {}",URL_FORMAT.get(dbType));
			throw new BusinessException(e.getMessage(),e);
		}finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null) {
				try {
					unicodeReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(templateByteArrayStream != null) {
				try {
					templateByteArrayStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return new String(byteArrayOutputStream.toByteArray());
	}
}
