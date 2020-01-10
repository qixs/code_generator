package com.qxs.generator.web.util;

import org.springframework.util.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.qxs.generator.web.config.DbUrlWarpper;
import com.qxs.generator.web.model.connection.Database;

/**
 * @author qixingshen
 * **/
public final class DataSourceUtil {
	
	public static DruidDataSource getDataSource(Database database) {
		Assert.notNull(database, "database参数不能为空");
		String url = DbUrlWarpper.warp(database.getType(), database.getUrl(), database.getPort(), 
				database.getDatabaseName());
		
		DruidDataSource dataSource = new DruidDataSource();

		dataSource.setUrl(url);
		dataSource.setUsername(database.getUsername());
		dataSource.setPassword(database.getPassword());
		dataSource.setValidationQueryTimeout(60);
		dataSource.setDriverClassName(database.getDriver());
		return dataSource;
	}
}
