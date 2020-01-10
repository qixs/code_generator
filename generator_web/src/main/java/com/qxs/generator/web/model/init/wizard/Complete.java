package com.qxs.generator.web.model.init.wizard;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 系统初始化-完成初始化登记表
 * **/
@Entity
@Table(name = "init_wizard_complete")
public class Complete {
	
	/**
	 * 状态: 未完成初始化
	 * **/
	public static final int STATUS_NOT_COMPLETE = 0;
	/**
	 * 状态: 已完成初始化
	 * **/
	public static final int STATUS_COMPLETE = 1;
	
	/**
	 * id
	 * **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 完成初始化状态
	 * **/
	private int status;
	
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
	 * 完成初始化状态
	 * **/
	public int getStatus() {
		return status;
	}
	/**
	 * 完成初始化状态
	 * **/
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return String.format("Complete{id = %s, status = %s}", id, status);
	}
}
