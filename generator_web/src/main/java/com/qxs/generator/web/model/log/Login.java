package com.qxs.generator.web.model.log;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;
import com.qxs.generator.web.model.user.User;

/**
 * 用户登录日志 log_login
 * 
 * @author qixingshen
 * @date 2018-05-29
 **/
@Entity
@Table(name = "log_login")
public class Login {

	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 用户
	 **/
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
	private User user;
	
	private String userId;
	/**
	 * 登录日期
	 **/
	private String loginDate;
	/**
	 * 登录ip
	 */
	private String loginIp;
	/**
	 * 退出时间
	 */
	private String exitDate;

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
	 * 用户
	 **/
	public User getUser() {
		return user;
	}
	/**
	 * 用户
	 **/
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 登录日期
	 **/
	public String getLoginDate() {
		return loginDate;
	}

	/**
	 * 登录日期
	 **/
	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

	/**
	 * 登录ip
	 */
	public String getLoginIp() {
		return loginIp;
	}

	/**
	 * 登录ip
	 */
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	/**
	 * 退出时间
	 */
	public String getExitDate() {
		return exitDate;
	}

	/**
	 * 退出时间
	 */
	public void setExitDate(String exitDate) {
		this.exitDate = exitDate;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return String.format("Login{id = %s , loginDate = %s , loginIp = %s , exitDate = %s}", 
				id, loginDate, loginIp, exitDate);
	}
}
