package com.qxs.generator.web.service.init.wizard;

import java.util.List;

import com.qxs.generator.web.model.init.wizard.Step;

/**
 * @author qixingshen
 * **/
public interface IStepService {
	/**
	 * 根据步骤号获取步骤配置信息
	 * **/
	Step findStepByStepNum(int stepNum);
	
	/***
	 * 校验步骤配置信息是否正确
	 * @return void
	 * **/
	void validStep();
	
	/**
	 * 获取最大步骤号
	 * @return int 
	 * **/
	long maxStepNum();
	
	/**
	 * 获取所有的初始化向导中所有步骤的的url列表
	 * @return List<String> 初始化向导中配置的所有的步骤url列表
	 * **/
	List<String> findStepUrlList();
	/**
	 * 获取所有的初始化向导所有步骤列表
	 * @return List<String> 所有的初始化向导所有步骤列表
	 * **/
	List<Step> findStepList();
}
