package com.qxs.generator.web.web.socket;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.google.gson.Gson;
import com.qxs.generator.web.model.GenerateResult;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.service.IGeneratorService;

/**
 * 生成代码处理器
 * 
 * **/
@Service
public class GenerateCodeHandler extends AbstractWebSocketHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCodeHandler.class);

	@Autowired
	private IGeneratorService generatorService;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		Map<String, Object> params = new Gson().fromJson(message.getPayload() , Map.class);
		
		Database database = readObject(Database.class, "database", params);
		Ssh ssh = readObject(Ssh.class, "ssh", params);
		GenerateParameter generateParameter = readObject(GenerateParameter.class, "generateParameter", params);
		
		GenerateResult generateResult = generatorService.generate(session, database, ssh, generateParameter);
		
		//生成成功
		if(generateResult.getStatus() == GenerateResult.Status.SUCCESS) {
			//生成失败
			session.sendMessage(new TextMessage("{\"status\":\"" + generateResult.getStatus() + "\"}"));
		}else {
			//生成失败
			session.sendMessage(new TextMessage("{\"status\":\"" + generateResult.getStatus() + "\",\"message\":\"" + generateResult.getMessage() + "\"}"));
		}
	}
	
	private <T> T readObject(Class<T> clazz, String prefix, Map<String,Object> map) throws InstantiationException, IllegalAccessException{
		T t = clazz.newInstance();
		Map<String, Object> m = new HashMap<>();
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			if(key.startsWith(prefix + ".")) {
				String fieldName = key.substring(key.indexOf(".") + 1);
				Object value = entry.getValue();
				m.put(fieldName, value);
			}
		}
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			String fieldName = field.getName();
			Object value = m.get(fieldName);
			if(value != null) {
				if(!field.isAccessible()) {
					field.setAccessible(true);
				}
				Class<?> fieldType = field.getType();
				
				if(fieldType.isAssignableFrom(Integer.class)) {
					value = Integer.valueOf(value.toString());
				}
				field.set(t, value);
			}
		}
		return t;
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
