package com.qxs.generator.web.service.config;

import com.qxs.generator.web.model.config.SystemParameter;

/**
 * 系统参数配置信息
 * **/
public interface ISystemParameterService {

	/**
	 * 系统参数配置信息条数
	 *
	 * @return int 条数
	 * **/
	long count();
	/**
	 * 获取系统参数配置信息(只有一条数据)
	 *
	 * @return SystemParameter 系统参数配置信息
	 * **/
	SystemParameter findSystemParameter();
	/**
	 * 保存系统参数配置信息
	 *
	 * @param systemParameter 系统参数配置信息
	 * @return void
	 * **/
	void save(SystemParameter systemParameter);

}
