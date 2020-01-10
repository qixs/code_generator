package com.qxs.generator.web;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 数据库配置
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-29
 * @version Revision: 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@ConditionalOnMissingBean(DataSource.class)
//@ConditionalOnProperty(name = "spring.datasource.type")
public class DatabaseConfiguration {

	/**
	 * Generic DataSource configuration.
	 */
	@ConditionalOnMissingBean(DataSource.class)
	@Bean
	public DataSource dataSource() {
		DataSourceProperties properties = new DataSourceProperties();
		properties.setType(DruidDataSource.class);
		properties.setDriverClassName(DatabaseDriver.SQLITE.getDriverClassName());
		properties.setUrl("jdbc:sqlite:code_generator.db");
		
		return properties.initializeDataSourceBuilder().build();
	}
}
