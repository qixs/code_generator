package com.qxs.generator.web.service.table;

import java.util.List;

import com.qxs.base.model.Table;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.Ssh;

/**
 * @author qixingshen
 * **/
public interface ITableService {
	
	List<Table> getTableList(Database database,Ssh ssh);
}
