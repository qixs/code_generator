package com.qxs.generator.web.model.connection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 要生成代码的数据库连接信息
 * 
 * @author qixingshen
 * **/
@Entity
@Table(name = "connection_database")
public class Database implements Cloneable{
	
	/**
	 * id
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	
	/**
	 * 数据库连接主表信息
	 * **/
	private String connectionId;
	
	/**
	 * 数据库类型
	 * **/
	private String type;
	/**
	 * 数据库驱动
	 * **/
	private String driver;
	/**
	 * 数据库地址
	 * **/
	private String url;
	/**
	 * 数据库端口号
	 * **/
	private Integer port;
	/**
	 * 数据库用户名
	 * **/
	private String username;
	/**
	 * 数据库密码
	 * **/
	private String password;
	/**
	 * 数据库名
	 * **/
	private String databaseName;
	/**
	 * 数据库连接字符串
	 * **/
	private String connectionUrl;
	
	public Database() {
		super();
	}
	public Database(String connectionId) {
		super();
		this.connectionId = connectionId;
	}
	/**
	 * id
	 * **/
	public String getId() {
		return id;
	}
	/**
	 * id
	 * **/
	public void setId(String id) {
		this.id = id;
	}
	
	public String getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
	/**
	 * 数据库类型
	 * **/
	public String getType() {
		return type;
	}
	/**
	 * 数据库类型
	 * **/
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 数据库驱动
	 * **/
	public String getDriver() {
		return driver;
	}
	/**
	 * 数据库驱动
	 * **/
	public void setDriver(String driver) {
		this.driver = driver;
	}
	/**
	 * 数据库地址
	 * **/
	public String getUrl() {
		return url;
	}
	/**
	 * 数据库地址
	 * **/
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 数据库端口号
	 * **/
	public Integer getPort() {
		return port;
	}
	/**
	 * 数据库端口号
	 * **/
	public void setPort(Integer port) {
		this.port = port;
	}
	/**
	 * 数据库用户名
	 * **/
	public String getUsername() {
		return username;
	}
	/**
	 * 数据库用户名
	 * **/
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 数据库密码
	 * **/
	public String getPassword() {
		return password;
	}
	/**
	 * 数据库密码
	 * **/
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 数据库名
	 * **/
	public String getDatabaseName() {
		return databaseName;
	}
	/**
	 * 数据库名
	 * **/
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	/**
	 * 数据库连接字符串
	 * **/
	public String getConnectionUrl() {
		return connectionUrl;
	}
	/**
	 * 数据库连接字符串
	 * **/
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	@Override
	public Database clone() {
		try {
			return (Database)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new BusinessException(e);
		}
	}
	@Override
	public String toString() {
		return "Database [id=" + id + ", connectionId=" + connectionId + ", type=" + type + ", driver=" + driver
				+ ", url=" + url + ", port=" + port + ", username=" + username + ", password=" + password
				+ ", databaseName=" + databaseName + ", connectionUrl=" + connectionUrl + "]";
	}
	
	
}
