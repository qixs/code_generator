package com.qxs.generator.web.model.init.wizard;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 系统初始化当前步骤
 * **/
@Entity
@Table(name = "init_wizard_current_step")
public class CurrentStep {
	/**
	 * id
	 * **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	/**
	 * 当前步骤
	 * **/
	private int currentStepNum;
	
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
	 * 当前步骤
	 * **/
	public int getCurrentStepNum() {
		return currentStepNum;
	}
	/**
	 * 当前步骤
	 * **/
	public void setCurrentStepNum(int currentStepNum) {
		this.currentStepNum = currentStepNum;
	}
	
	@Override
	public String toString() {
		return String.format("CurrentStep{id = %s, currentStepNum = %s}", id, currentStepNum);
	}
}
