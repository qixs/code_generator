package com.qxs.generator.web.web.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.google.gson.Gson;
import com.qxs.generator.web.model.GenerateResult;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.model.log.Generate;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.service.IGeneratorService;
import com.qxs.generator.web.service.log.IGenerateService;
import com.qxs.generator.web.util.EncryptUtil;

/**
 * 生成日志重新生成代码处理器
 * 
 * **/
@Service
public class LogGenerateCodeHandler extends AbstractWebSocketHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogGenerateCodeHandler.class);

	@Autowired
	private IGeneratorService generatorService;
	@Autowired
	private IGenerateService generateService;
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		User user = (User)((UsernamePasswordAuthenticationToken) session.getPrincipal()).getPrincipal();
		
		Generate generate = generateService.getById(message.getPayload());
		
		Gson gson = new Gson();
		
		Database database = gson.fromJson(generate.getGenerateParameterDatabase(), Database.class);
		Ssh ssh = gson.fromJson(generate.getGenerateParameterSsh(), Ssh.class);
		GenerateParameter generateParameter = gson.fromJson(generate.getGenerateParameterParameter(), GenerateParameter.class);
		
		//处理密码参数
		if(StringUtils.hasLength(database.getPassword())) {
			database.setPassword(EncryptUtil.desDecode(database.getPassword(), user.getId()));
		}
		if(ssh != null && StringUtils.hasLength(ssh.getPassword())) {
			ssh.setPassword(EncryptUtil.desDecode(ssh.getPassword(), user.getId()));
		}
		
		GenerateResult generateResult = generatorService.generate(session, database, ssh, generateParameter, false);
		
		//生成成功
		if(generateResult.getStatus() == GenerateResult.Status.SUCCESS) {
			//生成失败
			session.sendMessage(new TextMessage("{\"status\":\"" + generateResult.getStatus() + "\"}"));
		}else {
			//生成失败
			session.sendMessage(new TextMessage("{\"status\":\"" + generateResult.getStatus() + "\",\"message\":\"" + generateResult.getMessage() + "\"}"));
		}
	}
	
	/**
	 * 建立连接
	 * **/
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		LOGGER.debug("建立连接");
	}

	/**
	 * 连接被关闭
	 * **/
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		LOGGER.debug("关闭连接");
	}
	
}
