package com.qxs.generator.web.repository.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.master.Master;

@Repository
public interface IMasterRepository extends JpaRepository<Master, Integer>,JpaSpecificationExecutor<Master> {
	
}