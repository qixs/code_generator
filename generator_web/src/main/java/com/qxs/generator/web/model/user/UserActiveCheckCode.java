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
 * 用户激活件校验码 user_active_check_code
 * 
 * @author qixingshen
 * @date 2018-07-07
 **/
@Entity
@Table(name = "user_active_check_code")
public class UserActiveCheckCode {

	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	@NotBlank(groups = { Update.class })
	private String id;

	/**
	 * 用户id
	 **/
	private String userId;

	/**
	 * 用户激活邮件校验码
	 **/
	private String checkCode;
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
	 * 用户激活邮件校验码
	 **/
	public String getCheckCode() {
		return checkCode;
	}

	/**
	 * 用户激活邮件校验码
	 **/
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
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
				"UserActiveCheckCode{id = %s , userId = %s , checkCode = %s , sendDate = %s , validateMinutes = %s , status = %s}",
				id, userId, checkCode, sendDate, validateMinutes, status);
	}

}
