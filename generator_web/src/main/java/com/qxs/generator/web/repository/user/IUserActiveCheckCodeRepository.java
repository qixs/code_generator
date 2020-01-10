package com.qxs.generator.web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.user.UserActiveCheckCode;

@Repository
public interface IUserActiveCheckCodeRepository 
	extends JpaRepository<UserActiveCheckCode, String>,JpaSpecificationExecutor<UserActiveCheckCode> {
	
	/**
	 * 根据用户id更新状态
	 * @param 
	 * **/
	@Modifying
	@Query("update UserPasswordCheckCode cc set cc.status = :status where cc.userId = :userId and cc.status != :status")
	int updateStatusByUserId(@Param("status")int status,@Param("userId")String userId);
}