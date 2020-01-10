package com.qxs.generator.web.repository.version;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.version.Version;

@Repository
public interface IVersionRepository extends JpaRepository<Version, String>,JpaSpecificationExecutor<Version> {
	
	@Modifying
	@Query("update Version v set v.status=?1 where v.status = ?2")
	int updateStatusByStatus(int status1,int status2);
}
