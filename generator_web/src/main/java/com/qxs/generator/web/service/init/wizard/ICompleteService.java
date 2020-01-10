package com.qxs.generator.web.service.init.wizard;

import com.qxs.generator.web.model.init.wizard.Complete;

/**
 * @author qixingshen
 * **/
public interface ICompleteService {
	
	/**
	 * 获取当前系统初始化完成状态
	 * @return Complete
	 * **/
	Complete findComplete();
	
	/**
	 * 保存系统初始化状态为已完成
	 * @return void
	 * **/
	void save();
}
