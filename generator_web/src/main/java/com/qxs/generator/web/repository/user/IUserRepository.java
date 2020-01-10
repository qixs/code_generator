package com.qxs.generator.web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.user.User;

@Repository
public interface IUserRepository 
	extends JpaRepository<User, String>,JpaSpecificationExecutor<User> {
	
	/**
	 * 查询用户名是否存在
	 * **/
	@Query("select count(*) from User u where u.username = ?1 and u.id != ?2")
	long countByUsernameAndId(String username, String id);
}