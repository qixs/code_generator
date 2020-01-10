package com.qxs.generator.web.repository.connection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.connection.Ssh;

@Repository
public interface ISshRepository extends JpaRepository<Ssh, String>,JpaSpecificationExecutor<Ssh> {
	
}