package com.qxs.generator.web.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qxs.generator.web.exception.StartupException;
import com.qxs.generator.web.service.IDatabaseUpdateService;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;

@Service
public class DatabaseUpdateServiceImpl implements IDatabaseUpdateService {

	private transient final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private EntityManagerFactoryInfo entityManagerFactoryInfo;
	
	
	@Override
	public void update(String sql) {
		if(!StringUtils.hasLength(sql)) {
			logger.error("未读取到数据库脚本");
			
			return;
		}
		logger.debug("更新脚本:[{}]", sql);
		
		//解析sql
		String[] sqls = sql.split(";");
		
		//没错误的sql集合
		List<String> sqlList = new ArrayList<>();
		//错误的sql集合
		List<String> errorSqlList = new ArrayList<>();
		
		for(String s : sqls) {
			if(s == null || s.trim().length() == 0) {
				continue;
			}
			try {
				CCJSqlParserUtil.parse(s.trim());
				
				//sql没报异常,认为sql没问题
				sqlList.add(s.trim());
			} catch (JSQLParserException e) {
				logger.error("sql校验失败:[{}]",s.trim(),e);
				
				//sql格式有问题
				errorSqlList.add(s.trim());
			}
		}
		
		if(errorSqlList.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for(int i = 0 ; i < errorSqlList.size() ; i ++) {
				String s = errorSqlList.get(i);
				sb.append((i + 1) + ". " + s.trim().replaceAll("\\s+", " ")+" \r\n");
			}
			logger.error("以下sql格式存在问题：\r\n\r\n{}\r\n", sb);
			throw new StartupException("部分sql格式有问题,详见日志");
		}
		
		EntityManager entityManager = entityManagerFactoryInfo.getNativeEntityManagerFactory().createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		try {
			entityTransaction.begin();
			for(String s : sqlList) {
				entityManager.createNativeQuery(s).executeUpdate();
			}
			
			entityTransaction.commit();
		}catch(RuntimeException e) {
			logger.error("sql执行出现错误,sql:[{}]",sql, e);
			entityTransaction.rollback();
		}
	}
	
}
