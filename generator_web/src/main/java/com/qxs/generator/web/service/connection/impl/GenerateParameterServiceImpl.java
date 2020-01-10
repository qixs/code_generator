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
import com.jcraft.jsch.JSchException;
import com.qxs.base.model.Table;
import com.qxs.base.util.SshClient;
import com.qxs.database.extractor.IExtractor;
import com.qxs.generator.web.config.DbUrlWarpper;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.connection.Connection;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.repository.connection.IGenerateParameterRepository;
import com.qxs.generator.web.service.connection.IGenerateParameterService;
import com.qxs.generator.web.util.DataSourceUtil;

@Service
public class GenerateParameterServiceImpl implements IGenerateParameterService {
	
	private transient final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IGenerateParameterRepository generateParameterRepository;
	
	@Autowired
	private IExtractor extractor;
	
	@Transactional
	@Override
	public GenerateParameter findByConnection(Connection connection) {
		if(connection == null || StringUtils.isEmpty(connection.getId())) {
			return null;
		}
		GenerateParameter generateParameter = new GenerateParameter();
		generateParameter.setConnectionId(connection.getId());
		return generateParameterRepository.findOne(Example.of(generateParameter)).orElseThrow(()->new BusinessException("生成代码参数查询出错"));
	}

	@Override
	public GenerateParameter saveAndFlush(GenerateParameter generateParameter) {
		return generateParameterRepository.saveAndFlush(generateParameter);
	}

	@Override
	public void delete(GenerateParameter generateParameter) {
		generateParameter = generateParameterRepository.findOne(Example.of(generateParameter)).orElse(null);
		if(generateParameter != null) {
			generateParameterRepository.delete(generateParameter);
		}
	}

	@Override
	public List<com.qxs.database.model.Table> findTables(Database database, Ssh ssh) {
		if(database == null) {
			return null;
		}
		List<com.qxs.database.model.Table> tableList = null;
		DruidDataSource dataSource = DataSourceUtil.getDataSource(database);
		
		SshClient sshClient = null;
		java.sql.Connection conn = null;
		try {
			if(ssh != null && StringUtils.hasLength(ssh.getHost())) {
				sshClient = new SshClient(ssh.getHost(),ssh.getPort(), ssh.getUsername(), ssh.getPassword());
				sshClient.connect();
				
				int port = sshClient.forward(database.getUrl(), database.getUrl(), database.getPort());
				
				String url = DbUrlWarpper.warp(database.getType(), database.getUrl(), port,database.getDatabaseName());
				
				dataSource.setUrl(url);
			}
			//快速失败
			dataSource.setFailFast(true);
			//pool向数据库请求连接失败后标记整个pool为block并close
			dataSource.setBreakAfterAcquireFailure(true);
			
			List<Table> tables = extractor.extractorTables(dataSource);
			tableList = new ArrayList<>(tables.size());
			for(Table table : tables) {
				com.qxs.database.model.Table t = new com.qxs.database.model.Table();
				t.setName(table.getName());
				t.setView(table.getView());
				t.setComment(table.getComment());
				tableList.add(t);
			}

			return tableList;
		} catch (JSchException e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException("ssh服务器连接失败:"+e.getMessage());
		} catch (BindException e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException("ssh服务器创建隧道失败:"+e.getMessage());
		} finally {
			if(sshClient != null) {
				sshClient.disconnect();
			}
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(dataSource != null) {
				dataSource.close();
			}
		}
		
	}

}
