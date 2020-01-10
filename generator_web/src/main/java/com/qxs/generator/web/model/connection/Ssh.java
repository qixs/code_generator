package com.qxs.generator.web.model.connection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 生成代码ssh连接信息
 * 
 * @author qixingshen
 * **/
@Entity
@Table(name = "connection_ssh")
public class Ssh {
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
	 * ssh连接地址
	 * **/
	private String host;
	/**
	 * ssh连接端口号
	 * **/
	private Integer port;
	/**
	 * ssh连接用户名
	 * **/
	private String username;
	/**
	 * ssh连接密码
	 * **/
	private String password;

	public Ssh() {
		super();
	}
	public Ssh(String connectionId) {
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
	 * ssh连接地址
	 * **/
	public String getHost() {
		return host;
	}
	/**
	 * ssh连接地址
	 * **/
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * ssh连接端口号
	 * **/
	public Integer getPort() {
		return port;
	}
	/**
	 * ssh连接端口号
	 * **/
	public void setPort(Integer port) {
		this.port = port;
	}
	/**
	 * ssh连接用户名
	 * **/
	public String getUsername() {
		return username;
	}
	/**
	 * ssh连接用户名
	 * **/
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * ssh连接密码
	 * **/
	public String getPassword() {
		return password;
	}
	/**
	 * ssh连接密码
	 * **/
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "Ssh [id=" + id + ", connectionId=" + connectionId + ", host=" + host + ", port=" + port + ", username="
				+ username + ", password=" + password + "]";
	}
	
	
}
