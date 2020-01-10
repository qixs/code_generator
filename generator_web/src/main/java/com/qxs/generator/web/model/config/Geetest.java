package com.qxs.generator.web.model.config;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.qxs.generator.web.validate.group.Create;
import com.qxs.generator.web.validate.group.Update;

/**
 * Geetest 验证码配置信息
 * **/
@Entity
@Table(name = "config_geetest")
public class Geetest {
	/**
	 * id
	 * **/
	@Id
	@NotNull(message = "id不能为空", groups = {Create.class, Update.class})
	@NotEmpty(message = "id不能为空", groups = {Create.class, Update.class})
	private String id;
	/**
	 * key
	 * **/
	@NotNull(message = "key不能为空", groups = {Create.class, Update.class})
	@NotEmpty(message = "key不能为空", groups = {Create.class, Update.class})
	private String key;
	/**
	 * 权重 必须大于0
	 * **/
	@NotNull(message = "权重不能为空", groups = {Create.class, Update.class})
	@Min(value = 1, message = "权重不能小于1", groups = {Create.class, Update.class})
	private Integer weight;
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
	 * key
	 * **/
	public String getKey() {
		return key;
	}
	/**
	 * key
	 * **/
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * 权重
	 * **/
	public Integer getWeight() {
		return weight;
	}
	/**
	 * 权重
	 * **/
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	@Override
	public String toString() {
		return String.format("Geetest{id = %s, key = %s, weight = %s}", id, key, weight);
	}
}
