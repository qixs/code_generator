package com.qxs.generator.web.repository.plugin;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.qxs.generator.web.model.plugin.Plugin;

@Repository
public interface IPluginRepository 
	extends CrudRepository<Plugin, String>,JpaRepository<Plugin, String>,JpaSpecificationExecutor<Plugin> {
	
	/**
	 * 根据插件id集合更新插件状态
	 * 
	 * @param status 插件状态
	 * @param ids 插件id集合
	 * 
	 * @return int
	 * **/
	@Transactional
	@Modifying
	@Query("update Plugin p set p.status = :status where p.id in :ids")
	int updateStatusByIds(@Param("status")int status,@Param("ids")List<String> ids);
	
	/***
	 * 根据插件集合名称获取插件列表
	 *
	 * @param pluginGroupName 插件组名
	 * @param pluginNames 插件名称集合
	 * 
	 * @return List<Plugin>
	 * **/
	@Query("select p from Plugin p where p.groupName = :pluginGroupName and p.name in :pluginNames")
	List<Plugin> findByNameIn(@Param("pluginGroupName")String pluginGroupName, @Param("pluginNames")Collection<String> pluginNames);

	/**
	 * 查询插件组名列表
	 * **/
	@Query("select distinct p.groupName from Plugin p order by p.groupName")
	List<String> findPluginGroupNameList();
}