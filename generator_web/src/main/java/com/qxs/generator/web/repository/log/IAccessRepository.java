package com.qxs.generator.web.repository.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qxs.generator.web.model.log.Access;

@Repository
public interface IAccessRepository extends JpaRepository<Access, String>,JpaSpecificationExecutor<Access> {

    @Modifying
    @Query("delete from Access a where a.accessDate > :date")
    void deleteByAccessDate(@Param("date") String date);
}