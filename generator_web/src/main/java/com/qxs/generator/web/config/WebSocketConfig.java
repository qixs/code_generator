package com.qxs.generator.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.qxs.generator.web.web.socket.CheckInitializingHandler;
import com.qxs.generator.web.web.socket.GenerateCodeHandler;
import com.qxs.generator.web.web.socket.LogGenerateCodeHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

	@Autowired
	private CheckInitializingHandler checkInitializingHandler;
	@Autowired
	private GenerateCodeHandler generateCodeHandler;
	@Autowired
	private LogGenerateCodeHandler logGenerateCodeHandler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(checkInitializingHandler, "/checkInitializing");
		registry.addHandler(generateCodeHandler, "/generator/generate");
		registry.addHandler(logGenerateCodeHandler, "/log/generator/generate");
		
	}
	
}
