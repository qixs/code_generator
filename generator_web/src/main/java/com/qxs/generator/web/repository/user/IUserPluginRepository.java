package com.qxs.generator.web.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.user.UserPlugin;

@Repository
public interface IUserPluginRepository 
	extends JpaRepository<UserPlugin, String>,JpaSpecificationExecutor<UserPlugin> {
	
	/**
	 * 根据插件id集合更新插件状态
	 * 
	 * @param status 插件状态
	 * @param ids 插件id集合
	 * 
	 * @return int
	 * **/
	@Modifying
	@Query("update UserPlugin p set p.status = :status where p.id in :ids")
	int updateStatusByIds(@Param("status")int status,@Param("ids")List<String> ids);
	/**
	 * 根据插件名称查询所有的插件
	 * @param names 插件名称集合
	 * **/
	@Query("select up from UserPlugin up where up.groupName = :groupName and up.name in :names and userId = :userId")
	List<UserPlugin> findAllByNames(@Param("groupName") String groupName, @Param("names")List<String> names, @Param("userId")String userId);
}