package com.qxs.generator.web.model.version;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 版本号表 version
 * 
 * @author qixingshen
 * @date 2018-05-29
 **/
@Entity
@Table(name = "version")
public class Version {

	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 版本号
	 **/
	private String version;
	/**
	 * 更新日期
	 **/
	private String updateDate;
	/**
	 * 状态
	 */
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
	 * 版本号
	 **/
	public String getVersion() {
		return version;
	}

	/**
	 * 版本号
	 **/
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 更新日期
	 **/
	public String getUpdateDate() {
		return updateDate;
	}

	/**
	 * 更新日期
	 **/
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * 状态
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 状态
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format("Version{id = %s ,version = %s ,updateDate = %s ,status = %s}",
				id,version,updateDate,status);
	}
}
