package com.qxs.generator.web.repository.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.log.Generate;

@Repository
public interface IGenerateRepository extends JpaRepository<Generate, String>,JpaSpecificationExecutor<Generate> {
	
}