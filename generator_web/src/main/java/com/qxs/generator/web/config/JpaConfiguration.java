package com.qxs.generator.web.config;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.sqlite.SQLiteDataSource;

/**
 * 数据库配置
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-29
 * @version Revision: 1.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", 
	transactionManagerRef = "transactionManager",basePackages = { "com.qxs" })
@ConditionalOnMissingBean(EntityManager.class)
public class JpaConfiguration {
	
	private static final String JPA_MODEL_PACKAGE_NAME = "com.qxs.generator.web.model";
	
	@Value("${spring.jpa.properties.hibernate.dialect:com.qxs.base.database.dialect.SQLiteDialect}")
	private String dialect;

	@Autowired
	private DataSource dataSource;
	@Autowired
	private JpaProperties jpaProperties;

	@Primary
	@Bean
	public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
		return entityManagerFactory(builder).getObject()
				.createEntityManager();
	}

	@Primary
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			EntityManagerFactoryBuilder builder) {
		//使用WAL模式，解决sqlite database is locked问题
		if(dataSource instanceof SQLiteDataSource) {
			SQLiteDataSource sqLiteDataSource = (SQLiteDataSource)dataSource;
			sqLiteDataSource.setJournalMode("WAL");
		}
		
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setDatabasePlatform(dialect);
		LocalContainerEntityManagerFactoryBean factoryBean = builder.dataSource(dataSource)
				.properties(jpaProperties.getProperties())
				.packages(JPA_MODEL_PACKAGE_NAME) // 设置实体类所在位置
				.persistenceUnit("primaryPersistenceUnit").build();
		
		factoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
		return factoryBean;
	}


	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(
			EntityManagerFactoryBuilder builder) {
		return new JpaTransactionManager(entityManagerFactory(builder)
				.getObject());
	}
	
}
