package com.qxs.generator.web.repository.config;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.qxs.generator.web.model.config.Geetest;

@Repository
public interface IGeetestRepository extends JpaRepository<Geetest, String>,JpaSpecificationExecutor<Geetest> {
	
	/**
	 * 根据id集合删除Geetest配置信息
	 * 
	 * @param ids id集合
	 * 
	 * @return int
	 * **/
	@Transactional
	@Modifying
	@Query("delete from Geetest g where g.id in :ids")
	int deleteByIds(@Param("ids")List<String> ids);
}