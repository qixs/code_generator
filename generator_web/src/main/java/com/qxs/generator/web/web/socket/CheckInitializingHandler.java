package com.qxs.generator.web.web.socket;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.google.gson.Gson;
import com.qxs.generator.web.model.init.wizard.Complete;
import com.qxs.generator.web.service.init.wizard.ICompleteService;

/**
 * 校验是否正在执行初始化操作处理器
 * 
 * **/
@Service
public class CheckInitializingHandler extends AbstractWebSocketHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckInitializingHandler.class);

	private static final Map<String,WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();
	/**
	 * 当前正在初始化的session信息
	 * **/
	private static InitializingSession INITIALIZING_SESSION;
	
	@Autowired
	private ICompleteService completeService;
	
	/**
	 * 建立连接
	 * **/
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String sessionId = session.getId();
		String remoteAddress = session.getRemoteAddress().toString().substring(1);
		
		SESSION_MAP.put(remoteAddress, session);
		
		LOGGER.debug("WebSocket建立连接，SessionId:[{}] RemoteAddress:[{}] ", sessionId, remoteAddress);
		
		Gson gson = new Gson();
		if(INITIALIZING_SESSION == null) {
			session.sendMessage(new TextMessage(gson.toJson(
					new InitializingDescription(
							InitializingDescription.LEVEL_NOT_NOTICE, "当前无其他用户正在初始化，执行初始化操作"))));
			
			synchronized(InitializingSession.class) {
				INITIALIZING_SESSION = new InitializingSession(sessionId, remoteAddress);
			}
		}else {
			session.sendMessage(new TextMessage(gson.toJson(
					new InitializingDescription(
							InitializingDescription.LEVEL_ERROR, "有其他用户正在执行初始化操作，您暂无权限执行初始化操作"))));
			
			//通知正在进行初始化操作的人员有其他人尝试进行初始化操作失败
			SESSION_MAP.get(INITIALIZING_SESSION.getRemoteAddress())
				.sendMessage(new TextMessage(gson.toJson(
						new InitializingDescription(
								InitializingDescription.LEVEL_WARN, "有其他用户尝试执行初始化操作，已被拦截，用户地址：" + remoteAddress))));
		}
		
	}

	/**
	 * 连接被关闭
	 * **/
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		String remoteAddress = session.getRemoteAddress().toString().substring(1);
		
		SESSION_MAP.remove(remoteAddress);
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("WebSocket连接关闭，SessionId:[{}] RemoteAddress:[{}] CloseStatusCode:[{}] CloseStatusReason:[{}]", 
					session.getId(), remoteAddress, status.getCode(), status.getReason());
		}
		
		//如果正在执行初始化操作的人退出则校验是否初始化完毕，如果初始化完毕通知其他人初始化完毕，进行业务操作，未初始化完毕通知其他人初始化
		if(remoteAddress.equals(INITIALIZING_SESSION.getRemoteAddress())) {
			synchronized(InitializingSession.class) {
				INITIALIZING_SESSION = null;
			}
			
			String message = null;
			Complete complete = completeService.findComplete();
			//如果登记了complete表则且状态是STATUS_COMPLETE则认为初始化完毕
			if(complete != null && Complete.STATUS_COMPLETE == complete.getStatus()) {
				LOGGER.debug("当前系统初始化完毕");
				
				message = "当前系统已经初始化完毕，请刷新页面登录系统";
			}else {
				LOGGER.debug("当前系统未初始化完毕");
				
				message = "当前系统未初始化完毕，请重新刷新当前页面执行初始化操作";
			}
			
			Gson gson = new Gson();
			Collection<WebSocketSession> sessions = SESSION_MAP.values();
			for(WebSocketSession sess : sessions) {
				sess.sendMessage(new TextMessage(gson.toJson(
						new InitializingDescription(
								InitializingDescription.LEVEL_WARN, message))));
			}
		}
	}
	
	private class InitializingDescription{
		
		private static final String LEVEL_ERROR = "error";
		private static final String LEVEL_WARN = "warn";
		private static final String LEVEL_NOT_NOTICE = "LEVEL_NOT_NOTICE";
		
		private String level;
		private String description;
		
		private InitializingDescription() {}
		private InitializingDescription(String level,String description) {
			this.level = level;
			this.description = description;
		}
		@SuppressWarnings("unused")
		public String getLevel() {
			return level;
		}
		@SuppressWarnings("unused")
		public void setLevel(String level) {
			this.level = level;
		}
		@SuppressWarnings("unused")
		public String getDescription() {
			return description;
		}
		@SuppressWarnings("unused")
		public void setDescription(String description) {
			this.description = description;
		}
		
	}
	
	private class InitializingSession{
		private String sessionId;
		private String remoteAddress;
		
		private InitializingSession(String sessionId,String remoteAddress) {
			this.sessionId = sessionId;
			this.remoteAddress = remoteAddress;
		}
		@SuppressWarnings("unused")
		public String getSessionId() {
			return sessionId;
		}
		public String getRemoteAddress() {
			return remoteAddress;
		}
		
	}
}
