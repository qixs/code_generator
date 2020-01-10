package com.qxs.generator.web.model.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;
import com.qxs.generator.web.validate.group.Update;

/**
 * 创建超级管理员验证码 user_admin_captcha
 * 
 * @author qixingshen
 * @date 2018-08-22
 **/
@Entity
@Table(name = "user_admin_captcha")
public class UserAdminCaptcha {

	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	@NotBlank(groups = { Update.class })
	private String id;

	/**
	 * 用户名
	 **/
	private String username;

	/**
	 * 验证码
	 **/
	private String captcha;
	/**
	 * 发送日期
	 **/
	private String sendDate;
	/**
	 * 校验码有效时间
	 **/
	private Integer validateMinutes;
	/**
	 * 校验码状态
	 **/
	private Integer status;

	/**
	 * 主键
	 **/
	public String getId() {
		return id;
	}

	/**
	 * 主键
	 **/
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 用户名
	 **/
	public String getUsername() {
		return username;
	}
	/**
	 * 用户名
	 **/
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 验证码
	 **/
	public String getCaptcha() {
		return captcha;
	}
	/**
	 * 验证码
	 **/
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	/**
	 * 发送日期
	 **/
	public String getSendDate() {
		return sendDate;
	}

	/**
	 * 发送日期
	 **/
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * 校验码有效时间
	 **/
	public Integer getValidateMinutes() {
		return validateMinutes;
	}

	/**
	 * 校验码有效时间
	 **/
	public void setValidateMinutes(Integer validateMinutes) {
		this.validateMinutes = validateMinutes;
	}

	/**
	 * 校验码状态
	 **/
	public Integer getStatus() {
		return status;
	}

	/**
	 * 校验码状态
	 **/
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format(
				"UserAdminCaptcha{id = %s , username = %s , captcha = %s , sendDate = %s , validateMinutes = %s , status = %s}",
				id, username, captcha, sendDate, validateMinutes, status);
	}

}
