package com.qxs.generator.web.service.log;

import org.springframework.data.domain.Page;

import com.qxs.generator.web.model.log.Generate;
import com.qxs.generator.web.model.user.User;

/**
 * 生成代码日志
 * 
 * @author qixingshen
 *
 */
public interface IGenerateService {
	
	/**
	 * 插入
	 * @param generate 生成代码日志
	 * @return int 日志id
	 * **/
	String insert(User user,Generate generate);
	/**
	 * 根据id查询
	 * @param id id
	 * @return Generate
	 * **/
	Generate getById(String id);
	/**
	 * 查询日志列表
	 * 
	 * @param search
	 *            查询内容
	 * @return List<Generate> 用户信息
	 **/
	Page<Generate> findList(String search, Integer offset, Integer limit, 
			String sort, String order);
}
