package com.qxs.generator.web.model.log;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 生成代码文件日志 log_generate
 * 
 * @author qixingshen
 * @date 2018-06-11
 **/
@Entity
@Table(name = "log_generate")
public class Generate {
	
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
	 * 生成代码开始时间
	 **/
	private String generateStartDate;
	/**
	 * 生成代码结束时间
	 **/
	private String generateStopDate;
	/**
	 * 生成代码耗费总时间(单位:毫秒(ms))
	 **/
	private String generateTime;
	/**
	 * 生成代码的数据库参数(密码为加密之后的)
	 **/
	private String generateParameterDatabase;
	/**
	 * 生成代码的ssh连接参数(密码为加密之后的)
	 **/
	private String generateParameterSsh;
	/**
	 * 生成代码的生成配置参数
	 **/
	private String generateParameterParameter;
	/**
	 * 生成结果 见Status
	 **/
	private Integer generateResult;
	/**
	 * 失败原因
	 **/
	private String failReason;
	
	public Generate() {
		super();
	}

	public Generate(String generateStartDate, String generateStopDate, String generateTime,
			String generateParameterDatabase, String generateParameterSsh, String generateParameterParameter,
			Integer generateResult, String failReason) {
		super();
		this.generateStartDate = generateStartDate;
		this.generateStopDate = generateStopDate;
		this.generateTime = generateTime;
		this.generateParameterDatabase = generateParameterDatabase;
		this.generateParameterSsh = generateParameterSsh;
		this.generateParameterParameter = generateParameterParameter;
		this.generateResult = generateResult;
		this.failReason = failReason;
	}

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
	 * 生成代码开始时间
	 **/
	public String getGenerateStartDate() {
		return generateStartDate;
	}

	/**
	 * 生成代码开始时间
	 **/
	public void setGenerateStartDate(String generateStartDate) {
		this.generateStartDate = generateStartDate;
	}

	/**
	 * 生成代码结束时间
	 **/
	public String getGenerateStopDate() {
		return generateStopDate;
	}

	/**
	 * 生成代码结束时间
	 **/
	public void setGenerateStopDate(String generateStopDate) {
		this.generateStopDate = generateStopDate;
	}

	/**
	 * 生成代码耗费总时间(单位:毫秒(ms))
	 **/
	public String getGenerateTime() {
		return generateTime;
	}

	/**
	 * 生成代码耗费总时间(单位:毫秒(ms))
	 **/
	public void setGenerateTime(String generateTime) {
		this.generateTime = generateTime;
	}

	/**
	 * 生成代码的数据库参数(密码为加密之后的)
	 **/
	public String getGenerateParameterDatabase() {
		return generateParameterDatabase;
	}

	/**
	 * 生成代码的数据库参数(密码为加密之后的)
	 **/
	public void setGenerateParameterDatabase(String generateParameterDatabase) {
		this.generateParameterDatabase = generateParameterDatabase;
	}

	/**
	 * 生成代码的ssh连接参数(密码为加密之后的)
	 **/
	public String getGenerateParameterSsh() {
		return generateParameterSsh;
	}

	/**
	 * 生成代码的ssh连接参数(密码为加密之后的)
	 **/
	public void setGenerateParameterSsh(String generateParameterSsh) {
		this.generateParameterSsh = generateParameterSsh;
	}

	/**
	 * 生成代码的生成配置参数
	 **/
	public String getGenerateParameterParameter() {
		return generateParameterParameter;
	}

	/**
	 * 生成代码的生成配置参数
	 **/
	public void setGenerateParameterParameter(String generateParameterParameter) {
		this.generateParameterParameter = generateParameterParameter;
	}

	/**
	 * 生成结果 见Status
	 **/
	public Integer getGenerateResult() {
		return generateResult;
	}

	/**
	 * 生成结果 见Status
	 **/
	public void setGenerateResult(Integer generateResult) {
		this.generateResult = generateResult;
	}

	/**
	 * 失败原因
	 **/
	public String getFailReason() {
		return failReason;
	}

	/**
	 * 失败原因
	 **/
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	@Override
	public String toString() {
		return String.format(
				"Login{id = %s , userId = %s , generateStartDate = %s , generateStopDate = %s , "
						+ "generateTime = %s , generateParameterDatabase = %s , generateParameterSsh = %s , "
						+ "generateParameterParameter = %s , generateResult = %s, failReason = %s}",
				id, userId, generateStartDate, generateStopDate, generateTime, generateParameterDatabase,
				generateParameterSsh, generateParameterParameter, generateResult, failReason);
	}
	
	public static enum Status {
		SUCCESS(1), FAIL(0);
		
		private int status;
		private Status(int status) {
			this.status = status;
		}
		public int getStatus() {
			return status;
		}
	}
}
