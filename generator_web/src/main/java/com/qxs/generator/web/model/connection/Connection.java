package com.qxs.generator.web.model.connection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 生成代码连接记录
 * 
 * @author qixingshen
 **/
@Entity
@Table(name = "connection")
public class Connection {
	/**
	 * id
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 用户id
	 **/
	private String userId;
	/**
	 * 连接名
	 **/
	private String connectionName;
	
	public Connection() {
		super();
	}

	public Connection(String id) {
		super();
		this.id = id;
	}

	/**
	 * id
	 **/
	public String getId() {
		return id;
	}

	/**
	 * id
	 **/
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 用户id
	 **/
	public String getUserId() {
		return userId;
	}

	/**
	 * 用户id
	 **/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 连接名
	 **/
	public String getConnectionName() {
		return connectionName;
	}

	/**
	 * 连接名
	 **/
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	@Override
	public String toString() {
		return "Connection [id=" + id + ", userId=" + userId + ", connectionName=" + connectionName + "]";
	}

}
