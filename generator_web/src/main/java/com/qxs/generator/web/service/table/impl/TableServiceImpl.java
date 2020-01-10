package com.qxs.generator.web.service.table.impl;

import java.net.BindException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.jcraft.jsch.JSchException;
import com.qxs.base.model.Table;
import com.qxs.base.util.SshClient;
import com.qxs.database.extractor.IExtractor;
import com.qxs.generator.web.config.DbUrlWarpper;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.service.table.ITableService;
import com.qxs.generator.web.util.DataSourceUtil;

/**
 * @author qixingshen
 * **/
@Service
public class TableServiceImpl implements ITableService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TableServiceImpl.class);
	
	@Autowired
	private IExtractor extractor;

	@Override
	public List<Table> getTableList(Database database,Ssh ssh) {
		DruidDataSource dataSource = DataSourceUtil.getDataSource(database);
		
		SshClient sshClient = null;
		try {
			if(ssh != null && StringUtils.hasLength(ssh.getHost())) {
				sshClient = new SshClient(ssh.getHost(),ssh.getPort(), ssh.getUsername(), ssh.getPassword());
				sshClient.connect();
				
				int port = sshClient.forward(database.getUrl(), database.getUrl(), database.getPort());

				String url = DbUrlWarpper.warp(database.getType(), database.getUrl(), port,database.getDatabaseName());
				
				dataSource.setUrl(url);
			}
			return extractor.extractorTables(dataSource);
		} catch (JSchException e) {
			LOGGER.error(e.getMessage(),e);
			throw new RuntimeException("ssh服务器连接失败:"+e.getMessage());
		} catch (BindException e) {
			LOGGER.error(e.getMessage(),e);
			throw new RuntimeException("ssh服务器创建隧道失败:"+e.getMessage());
		} finally {
			if(sshClient != null) {
				sshClient.disconnect();
			}
			if(dataSource != null) {
				dataSource.close();
			}
		}
	}
}
