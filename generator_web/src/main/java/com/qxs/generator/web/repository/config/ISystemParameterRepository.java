package com.qxs.generator.web.repository.config;

import com.qxs.generator.web.model.config.SystemParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ISystemParameterRepository extends JpaRepository<SystemParameter, String>,JpaSpecificationExecutor<SystemParameter> {

}