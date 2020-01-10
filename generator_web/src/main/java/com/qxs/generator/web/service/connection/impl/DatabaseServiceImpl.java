package com.qxs.generator.web.service.connection.impl;

import java.net.BindException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import com.jcraft.jsch.JSchException;
import com.qxs.base.util.SshClient;
import com.qxs.database.extractor.IExtractor;
import com.qxs.generator.web.config.DbDefaultDatabaseName;
import com.qxs.generator.web.config.DbUrlWarpper;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.connection.Connection;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.DatabaseName;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.repository.connection.IDatabaseRepository;
import com.qxs.generator.web.service.connection.IDatabaseService;
import com.qxs.generator.web.util.DataSourceUtil;

@Service
public class DatabaseServiceImpl implements IDatabaseService {
	
	private transient Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IDatabaseRepository databaseRepository;
	@Autowired
	private IExtractor extractor;

	@Transactional
	@Override
	public Database findByConnection(Connection connection) {
		if(connection == null || StringUtils.isEmpty(connection.getId())) {
			return null;
		}
		Database database = new Database();
		database.setConnectionId(connection.getId());
		return databaseRepository.findOne(Example.of(database)).orElseThrow(()->new BusinessException("数据库参数配置查询出错"));
	}

	@Override
	public List<DatabaseName> findDatabaseNameList(Database database, Ssh ssh) {
		//如果database是从数据库查出来的话直接改databaseName会导致数据库数据发生变更
		database = database.clone();
		
		List<DatabaseName> databaseNames = null;
		
		database.setDatabaseName(DbDefaultDatabaseName.getDefaultName(database));
		
		DruidDataSource dataSource = DataSourceUtil.getDataSource(database);
		//快速失败
		dataSource.setFailFast(true);
		//pool向数据库请求连接失败后标记整个pool为block并close
		dataSource.setBreakAfterAcquireFailure(true);
		
		SshClient sshClient = null;
		try {
			if(ssh != null && StringUtils.hasLength(ssh.getHost())) {
				sshClient = new SshClient(ssh.getHost(),ssh.getPort(), ssh.getUsername(), ssh.getPassword());
				sshClient.connect();
				
				int port = sshClient.forward(database.getUrl(), database.getUrl(), database.getPort());
				
				String url = DbUrlWarpper.warp(database.getType(), database.getUrl(), port,database.getDatabaseName());
				
				dataSource.setUrl(url);
			}
			
			List<com.qxs.database.model.Database> databases = extractor.extractorDatabases(dataSource);
			if(databases.isEmpty()) {
				return Lists.newArrayList(new DatabaseName("", "未查询到数据库信息", "未查询到数据库信息"));
			}
			databaseNames = new ArrayList<>(databases.size());
			for(com.qxs.database.model.Database db : databases) {
				databaseNames.add(new DatabaseName(db.getDatabase(), db.getDatabaseName(), db.getDatabaseDesc()));
			}
		} catch (JSchException e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException("ssh服务器连接失败:"+e.getMessage());
		} catch (BindException e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException("ssh服务器创建隧道失败:"+e.getMessage());
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			return Lists.newArrayList(new DatabaseName("", "读取数据库列表失败:" + e.getMessage(), e.getMessage()));
		} finally {
			if(sshClient != null) {
				sshClient.disconnect();
			}
			if(dataSource != null) {
				dataSource.close();
			}
		}
		
		return databaseNames;
	}

	@Override
	public Database saveAndFlush(Database database) {
		return databaseRepository.saveAndFlush(database);
	}

	@Override
	public void delete(Database database) {
		database = databaseRepository.findOne(Example.of(database)).orElse(null);
		if(database != null) {
			databaseRepository.delete(database);
		}
	}

}
