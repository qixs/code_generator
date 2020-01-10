package com.qxs.generator.web.repository.connection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.connection.GenerateParameter;

@Repository
public interface IGenerateParameterRepository extends JpaRepository<GenerateParameter, String>,JpaSpecificationExecutor<GenerateParameter> {
	
}