package com.qxs.generator.web.service.connection;

import com.qxs.generator.web.model.connection.Connection;
import com.qxs.generator.web.model.connection.Ssh;

/**
 * 生成代码ssh连接信息
 * 
 * @author qixingshen
 * **/
public interface ISshService {
	/**
	 * 根据连接参数查询ssh信息
	 * @param connection 连接参数
	 * @return Ssh 
	 * **/
	Ssh findByConnection(Connection connection);
	/**
	 * 保存链接ssh信息
	 * @param ssh ssh链接信息
	 * **/
	Ssh saveAndFlush(Ssh ssh);

	/**
	 * 删除
	 * @param ssh ssh
	 * @return void
	 * **/
	void delete(Ssh	ssh);
}
