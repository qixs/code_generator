package com.qxs.generator.web.service.config;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.qxs.generator.web.model.config.Geetest;

/**
 * Geetest 验证码配置信息
 * **/
public interface IGeetestService {

	/**
	 * 获取所有的验证码配置信息
	 * @param sort 排序信息
	 * @return List<Geetest>
	 * **/
	List<Geetest> findAll(Sort sort);
	
	/**
	 * 新增Geetest配置信息
	 * 
	 * @param geetest 配置信息
	 * 
	 * @return void
	 * **/
	void insert(Geetest geetest);
	
	/**
	 * 批量新增Geetest配置信息
	 * 
	 * @param geetestList 配置信息列表
	 * 
	 * @return void
	 * **/
	void batchInsert(List<Geetest> geetestList);
	
	/**
	 * 更新Geetest配置信息
	 * 
	 * @param geetest 配置信息
	 * 
	 * @return void
	 * **/
	void update(Geetest geetest);
	
	/**
	 * 批量更新Geetest配置信息
	 * 
	 * @param geetestList 配置信息列表
	 * 
	 * @return void
	 * **/
	void batchUpdate(List<Geetest> geetestList);
	/**
	 * 根据id删除配置信息
	 * 
	 * @param id 
	 * 
	 * @return void
	 * **/
	void deleteById(String id);
	
	/**
	 * 根据id集合删除配置信息
	 * 
	 * @param ids 
	 * 
	 * @return void
	 * **/
	void deleteByIds(List<String> ids);
	
	/**
	 * 根据id查询Geetest配置信息
	 * 
	 * @param id
	 * 
	 * @return Geetest
	 * **/
	Geetest findById(String id);
	
	/**
	 * 下一个Geetest的配置信息
	 * 
	 * @return Geetest
	 * **/
	Geetest nextGeetest();
	
	/**
	 * 获取验证码
	 * **/
	String register();
	
	/**
	 * 二次验证
	 * **/
	void enhencedValidate();
}
