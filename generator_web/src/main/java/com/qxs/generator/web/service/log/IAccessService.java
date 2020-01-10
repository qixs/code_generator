package com.qxs.generator.web.service.log;

import org.springframework.data.domain.Page;

import com.qxs.generator.web.model.log.Access;

/**
 * 访问日志
 * 
 * @author qixingshen
 *
 */
public interface IAccessService {
	
	/**
	 * 插入
	 * @param access 访问日志实体
	 * @return int 日志id
	 * **/
	String insert(Access access);
	/**
	 * 查询日志列表
	 * 
	 * @param search
	 *            查询内容
	 * @return List<Login> 用户信息
	 **/
	Page<Access> findList(String search, Integer offset, Integer limit, 
			String sort, String order);
	
	/**
	 * 根据id查询访问系统日志详情
	 * 
	 * @param id 日志id
	 * @return Access
	 * **/
	Access getById(String id);
	
	/**
	 * 生成访问日志文件流
	 * 
	 * @param id 访问日志id
	 * @return byte[]
	 * **/
	byte[] generateAccessLogFile(String id);


	/**
	 * 清除指定日期之前的数据
	 * @param date 日期
	 * **/
	void clear(String date);
}
