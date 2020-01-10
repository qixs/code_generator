package com.qxs.generator.web.repository.init.wizard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.init.wizard.Complete;

@Repository
public interface ICompleteRepository extends JpaRepository<Complete, String>,JpaSpecificationExecutor<Complete> {
	
	
}