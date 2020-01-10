package com.qxs.base.util;

import java.net.BindException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * ssh client
 * @author qixingshen
 * @date 2018-04-10
 * @version 1.0
 * **/
public class SshClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(SshClient.class);
	/**
	 * 日期格式
	 * **/
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
	
	private static final int MIN_FORWARD_PORT = 1025;
	private static final int MAX_FORWARD_PORT = 65536;
	/**
	 * 默认端口号
	 * **/
	private static final int DEFAULT_PORT = 22;
	/**
	 * 默认超时时间(5分钟)
	 * **/
	private static final int DEFAULT_TIMEOUT = 1000 * 60 * 5;
	/**
	 * 连接时间
	 * **/
	private long connectTime;
	
	/**
	 * 跳板机ssh地址
	 * **/
	private String host;
	/**
	 * 跳板机ssh端口号
	 * **/
	private int port;
	/**
	 * 跳板机ssh用户名
	 * **/
	private String username;
	/**
	 * 跳板机ssh密码
	 * **/
	private String password;
	
	private Session session;
	/**
	 * 超时时间
	 * **/
	private int timeout = DEFAULT_TIMEOUT;
	
	/**
	 * @param host 跳板机ssh地址(默认端口号:22)
	 * @param username 跳板机ssh用户名
	 * @param password 跳板机ssh密码
	 * **/
	public SshClient(String host,String username,String password) {
		this(host, DEFAULT_PORT, username, password);
	}
	/**
	 * @param host 跳板机ssh地址
	 * @param port 跳板机ssh端口号
	 * @param username 跳板机ssh用户名
	 * @param password 跳板机ssh密码
	 * **/
	public SshClient(String host,int port,String username,String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	/**
	 * ssh连接配置信
	 * **/
	private Properties config() {
		Properties properties = new Properties();
		properties.put("StrictHostKeyChecking", "no");
		properties.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
		return properties;
	}
	/**
	 * 连接客户端
	 * 
	 * @return void
	 * @throws JSchException
	 * **/
	public void connect() throws JSchException {
		JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);
        session.setPassword(password);
        session.setTimeout(timeout);
        session.setConfig(config());
        
        LOGGER.debug("开始连接服务器,服务器地址:[{}],服务器端口号:[{}],服务器用户名:[{}],超时时间:[{}]毫秒",host,port,username,timeout);
        
        long time = System.currentTimeMillis();
        session.connect();
        
        LOGGER.debug("服务器连接成功,连接耗费时间:[{}]毫秒",System.currentTimeMillis() - time);
        
        Date date = new Date();
        LOGGER.info("服务器连接开始时间:[{}]",DATE_FORMAT.format(date));
        connectTime = date.getTime();
	}
	/**
	 * 设置隧道
	 * @param localAddress 源主机
	 * @param remoteAddress 侦听端口
	 * @param port 目标主机
	 * @return int 目标端口
	 * **/
	@SuppressWarnings("unused")
	public int forward(String localAddress, String remoteAddress, int port) throws JSchException, BindException {
		long time = System.currentTimeMillis();
		
		if(session == null) {
			throw new JSchException("连接未创建");
		}
		if(!session.isConnected()) {
			throw new JSchException("连接已经关闭");
		}
		 
		//本地端口可能已经被使用,如果被使用则重试
		for(int i = 1 , max = 100 ; i <= max ; i ++) {
			try {
				//绑定的本地端口
				int localPort = randomPort();
				
				LOGGER.debug("开始尝试创建隧道,源主机:[{}],侦听端口:[{}],目标主机:[{}],目标端口:[{}]",localAddress,localPort,remoteAddress,port);
				
				//端口映射 转发
				session.setPortForwardingL(localAddress,localPort,remoteAddress, port);
				
				LOGGER.info("隧道创建成功,源主机:[{}],侦听端口:[{}],目标主机:[{}],目标端口:[{}],花费时间:[{}]毫秒",localAddress,localPort,remoteAddress,port,System.currentTimeMillis() - time);
				
				return localPort;
			}catch(JSchException e) {
				if(e.getCause() instanceof BindException) {
					if(i == max) {
						throw (BindException)e.getCause();
					}
					LOGGER.info("隧道创建失败,端口号被占用,开始第[{}]次尝试",i + 1);
				}
				throw e;
			}
		}
		
		return -1;
	}
	/**
	 * 生成随机端口号(指定的端口号可能会被占用,所以生成随机端口号减少冲突几率)
	 * 
	 * @return int 端口号
	 * **/
	private int randomPort() {
		int randomPort = new Random().nextInt(MAX_FORWARD_PORT);
		
		//端口号不能小于1024,小于1024的端口号只有root用户可以使用
		//端口号最大为65536
		//如果端口号不符合规则则重新生成
		while(randomPort < MIN_FORWARD_PORT || randomPort > MAX_FORWARD_PORT) {
			LOGGER.debug("端口号[{}]不符合规则,端口号必须在1025至65536之间,重新生成", randomPort);
			randomPort = new Random().nextInt(MAX_FORWARD_PORT);
		}
		LOGGER.debug("生成端口号:[{}]", randomPort);
		return randomPort;
	}
	/**
	 * 断开连接
	 * 
	 * @return void
	 * 
	 * **/
	public void disconnect() {
		if(session != null && session.isConnected()) {
			session.disconnect();
			session = null;
			
			Date date = new Date();
			LOGGER.info("服务器连接结束时间:[{}]",DATE_FORMAT.format(date));
			LOGGER.info("服务器连接总时间:[{}]毫秒", date.getTime() - connectTime);
		}
	}
	/**
	 * 设置超时时间
	 * @param timeout 超时时间(毫秒)
	 * 
	 * @return SshClient
	 * **/
	public SshClient setTimeout(int timeout) {
		this.timeout = timeout;
		
		return this;
	}

}
