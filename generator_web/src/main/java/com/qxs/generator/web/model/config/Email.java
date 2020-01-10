package com.qxs.generator.web.model.config;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;
import com.qxs.generator.web.validate.group.Create;
import com.qxs.generator.web.validate.group.Update;

/**
 * 邮件服务器配置信息
 * **/
@Entity
@Table(name = "config_email")
public class Email {
	/**
	 * 不使用SSL
	 * **/
	public static final int SSL_NOT_USE = 0;
	/**
	 * 使用SSL
	 * **/
	public static final int SSL_USE = 1;
	
	/**
	 * id
	 * **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 发件人
	 * **/
	@NotNull(message = "发件人不能为空", groups = {Create.class, Update.class})
	@Pattern(regexp="^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$", groups = {Create.class, Update.class})
	private String emailFrom;
	/**
	 * 密码
	 * **/
	@NotNull(message = "密码不能为空", groups = {Create.class, Update.class})
	private String password;
	/**
	 * 发件服务器地址
	 * **/
	@NotNull(message = "发件服务器地址不能为空", groups = {Create.class, Update.class})
	private String host;
	/**
	 * 发件服务器端口号
	 * **/
	@NotNull(message = "发件服务器端口号不能为空", groups = {Create.class, Update.class})
	private Integer port;
	/**
	 * ssl
	 * **/
	private int ssl;
	
	public Email() {}
	
	public Email(Integer port,int ssl) {
		this.port = port;
		this.ssl = ssl;
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
	/**
	 * 发件人
	 * **/
	public String getEmailFrom() {
		return emailFrom;
	}
	/**
	 * 发件人
	 * **/
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	/**
	 * 密码
	 * **/
	public String getPassword() {
		return password;
	}
	/**
	 * 密码
	 * **/
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 发件服务器地址
	 * **/
	public String getHost() {
		return host;
	}
	/**
	 * 发件服务器地址
	 * **/
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * 发件服务器端口号
	 * **/
	public Integer getPort() {
		return port;
	}
	/**
	 * 发件服务器端口号
	 * **/
	public void setPort(Integer port) {
		this.port = port;
	}
	/**
	 * ssl
	 * **/
	public int getSsl() {
		return ssl;
	}
	/**
	 * ssl
	 * **/
	public void setSsl(int ssl) {
		this.ssl = ssl;
	}
	
	@Override
	public String toString() {
		return String.format("Email{id = %s, emailFrom = %s, host = %s, port = %s, ssl = %s}",
				id, emailFrom, host, port, ssl);
	}
}
