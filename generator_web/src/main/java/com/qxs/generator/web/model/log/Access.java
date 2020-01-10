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
 * 用户访问日志 log_login
 * 
 * @author qixingshen
 * @date 2018-05-29
 **/
@Entity
@Table(name = "log_access")
public class Access {

	/**
	 * 主键
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
	 * 用户
	 * **/
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
	private User user;
	/**
	 * 系统版本号
	 **/
	private String systemVersion;
	/**
	 * 访问时间
	 **/
	private String accessDate;
	/**
	 * 执行时间
	 */
	private String time;
	/**
	 * 参数
	 */
	private String parameters;
	/**
	 * 访问url
	 */
	private String url;
	/**
	 * 结果类型
	 */
	private String resultType;
	/**
	 * 结果
	 */
	private String result;
	/**
	 * 异常信息
	 */
	private String exception;

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
	 * 访问时间
	 **/
	public String getAccessDate() {
		return accessDate;
	}
	/**
	 * 访问时间
	 **/
	public void setAccessDate(String accessDate) {
		this.accessDate = accessDate;
	}
	/**
	 * 执行时间
	 */
	public String getTime() {
		return time;
	}
	/**
	 * 执行时间
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * 参数
	 */
	public String getParameters() {
		return parameters;
	}
	/**
	 * 参数
	 */
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	/**
	 * 访问url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 访问url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 结果类型
	 */
	public String getResultType() {
		return resultType;
	}
	/**
	 * 结果类型
	 */
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	/**
	 * 结果
	 */
	public String getResult() {
		return result;
	}
	/**
	 * 结果
	 */
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * 异常信息
	 */
	public String getException() {
		return exception;
	}
	/**
	 * 异常信息
	 */
	public void setException(String exception) {
		this.exception = exception;
	}
	/**
	 * 系统版本号
	 **/
	public String getSystemVersion() {
		return systemVersion;
	}
	/**
	 * 系统版本号
	 **/
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return String.format("Access{id = %s , userId = %s , systemVersion = %s , accessDate = %s , time = %s , parameters = %s , "
				+ "url = %s , resultType = %s , result = %s , exception = %s}", 
				id, userId, systemVersion, accessDate, time, parameters, url, resultType, result, exception);
	}
	
}
