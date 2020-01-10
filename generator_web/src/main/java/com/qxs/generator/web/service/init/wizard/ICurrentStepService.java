package com.qxs.generator.web.service.init.wizard;

/**
 * @author qixingshen
 * **/
public interface ICurrentStepService {
	
	/**
	 * 保存步骤号
	 * 
	 * @param stepNum 步骤号
	 * 
	 * @return void
	 * **/
	void save(int stepNum);
	
	/**
	 * 获取当前步骤号
	 * **/
	int currentStepNum();
}
