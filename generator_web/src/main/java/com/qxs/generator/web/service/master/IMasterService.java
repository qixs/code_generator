package com.qxs.generator.web.service.master;

public interface IMasterService {
	
	/**
	 * 查询数据库表是否存在
	 * @param tableName 表名
	 * @return Long
	 * **/
	boolean findTableExists(String tableName);
}