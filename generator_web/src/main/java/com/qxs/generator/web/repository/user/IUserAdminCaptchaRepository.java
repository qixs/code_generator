package com.qxs.generator.web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.user.UserAdminCaptcha;

@Repository
public interface IUserAdminCaptchaRepository 
	extends JpaRepository<UserAdminCaptcha, String>,JpaSpecificationExecutor<UserAdminCaptcha> {
	
	/**
	 * 根据用户名更新状态
	 * @param 
	 * **/
	@Modifying
	@Query("update UserAdminCaptcha ac set ac.status = :status where ac.username = :username and ac.status != :status")
	int updateStatusByUserId(@Param("status")int status,@Param("username")String username);
}