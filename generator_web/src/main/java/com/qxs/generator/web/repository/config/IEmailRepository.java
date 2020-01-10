package com.qxs.generator.web.repository.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.config.Email;

@Repository
public interface IEmailRepository extends JpaRepository<Email, String>,JpaSpecificationExecutor<Email> {
	
}