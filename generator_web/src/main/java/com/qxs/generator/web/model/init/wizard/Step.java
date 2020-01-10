package com.qxs.generator.web.model.init.wizard;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 系统初始化步骤配置
 * **/
@Entity
@Table(name = "init_wizard_step")
public class Step {
	/**
	 * id
	 * **/
	@Id
	private String id;
	/**
	 * 步骤号
	 * **/
	private int stepNum;
	/**
	 * 步骤名
	 * **/
	private String stepName;
	/**
	 * 步骤url
	 * **/
	private String stepUrl;
	
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
	 * 步骤号
	 * **/
	public int getStepNum() {
		return stepNum;
	}
	/**
	 * 步骤号
	 * **/
	public void setStepNum(int stepNum) {
		this.stepNum = stepNum;
	}
	/**
	 * 步骤名
	 * **/
	public String getStepName() {
		return stepName;
	}
	/**
	 * 步骤名
	 * **/
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	/**
	 * 步骤url
	 * **/
	public String getStepUrl() {
		return stepUrl;
	}
	/**
	 * 步骤url
	 * **/
	public void setStepUrl(String stepUrl) {
		this.stepUrl = stepUrl;
	}
	@Override
	public String toString() {
		return String.format("Step{id = %s, stepNum = %s, stepName = %s, stepUrl = %s}", id, stepNum, stepName, stepUrl);
	}
}
