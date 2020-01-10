package com.qxs.generator.web.service.connection.impl;

import java.net.BindException;
import java.sql.SQLException;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.jcraft.jsch.JSchException;
import com.qxs.base.database.config.DatabaseTypeConfig;
import com.qxs.base.util.SshClient;
import com.qxs.generator.web.config.DbUrlWarpper;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.connection.Connection;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.repository.connection.IConnectionRepository;
import com.qxs.generator.web.service.connection.IConnectionService;
import com.qxs.generator.web.service.connection.IDatabaseService;
import com.qxs.generator.web.service.connection.IGenerateParameterService;
import com.qxs.generator.web.service.connection.ISshService;
import com.qxs.generator.web.util.DataSourceUtil;

@Service
public class ConnectionServiceImpl implements IConnectionService {
	
	private transient final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private IConnectionRepository connectionRepository;
	@Autowired
	private IDatabaseService databaseService;
	@Autowired
	private ISshService sshService;
	@Autowired
	private IGenerateParameterService generateParameterService;

	@Transactional
	@Override
	public List<Connection> listByUser() {
		Connection connection = new Connection();
		connection.setUserId(getUserId());
		
		return connectionRepository.findAll(Example.of(connection));
	}

	@Override
	public Connection getById(String id) {
		return connectionRepository.getOne(id);
	}

	@Transactional
	@Override
	public String insert(Connection connection, Database database, Ssh ssh, GenerateParameter generateParameter) {
		connection.setUserId(getUserId());
		
		//校验链接名称是否存在
		if(connectionRepository.count(Example.of(connection)) > 0) {
			throw new BusinessException("链接名称已经存在");
		}
		
		connection = connectionRepository.saveAndFlush(connection);
		String connectionId = connection.getId();
		
		database.setConnectionId(connectionId);
		databaseService.saveAndFlush(database);
		
		ssh.setConnectionId(connectionId);
		sshService.saveAndFlush(ssh);
		
		generateParameter.setConnectionId(connectionId);
		generateParameterService.saveAndFlush(generateParameter);
		
		return connectionId;
	}

	@Transactional
	@Override
	public String update(Connection connection, Database database, Ssh ssh, GenerateParameter generateParameter) {
		connection = connectionRepository.getOne(connection.getId());
		
		String connectionId = connection.getId();
		
		database.setConnectionId(connectionId);
		databaseService.saveAndFlush(database);
		
		ssh.setConnectionId(connectionId);
		sshService.saveAndFlush(ssh);
		
		generateParameter.setConnectionId(connectionId);
		generateParameterService.saveAndFlush(generateParameter);
		
		return connectionId;
	}
	
	@Transactional
	@Override
	public void deleteById(String id) {
		//删除链接
		//删除数据库信息
		databaseService.delete(new Database(id));
		//删除ssh信息
		sshService.delete(new Ssh(id));
		//删除生成参数信息
		generateParameterService.delete(new GenerateParameter(id));
		
		connectionRepository.deleteById(id);
	}
	

	@Override
	public void validConnection(Database database, Ssh ssh) {
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
			
			//执行校验sql
			dataSource.setValidationQuery(DatabaseTypeConfig.getDatabaseType(database.getType()).getValidSql());
			dataSource.setValidationQueryTimeout(60 * 1000);//超时时间
			//快速失败
			dataSource.setFailFast(true);
			//pool向数据库请求连接失败后标记整个pool为block并close
			dataSource.setBreakAfterAcquireFailure(true);
			
			conn = dataSource.getConnection();
			dataSource.validateConnection(conn);
			
		} catch (JSchException e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException("ssh服务器连接失败:"+e.getMessage());
		} catch (BindException e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException("ssh服务器创建隧道失败:"+e.getMessage());
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new BusinessException("数据库连接失败:"+e.getMessage());
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

	private String getUserId() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		
		Authentication authentication = securityContext.getAuthentication();
		
		if(authentication == null) {
			throw new BusinessException("未获取到登录用户");
		}
		
		User user = (User) authentication.getPrincipal();
		return user.getId();
	}
	
}
