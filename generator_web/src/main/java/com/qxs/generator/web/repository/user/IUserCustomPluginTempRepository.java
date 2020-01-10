package com.qxs.generator.web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.user.UserCustomPluginTemp;

@Repository
public interface IUserCustomPluginTempRepository 
	extends JpaRepository<UserCustomPluginTemp, String>,JpaSpecificationExecutor<UserCustomPluginTemp> {
	
}