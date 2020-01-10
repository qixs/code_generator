package com.qxs.generator.web.config.security.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.service.log.ILoginService;

@Component
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler{
	@Autowired
	private ILoginService loginService;

	private static final Logger LOGGER = LoggerFactory.getLogger(LogoutSuccessHandler.class);
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		User user = (User) authentication.getPrincipal();
		
		LOGGER.debug("用户退出，用户名:{}", user.getUsername());
		
		loginService.logout(user);
		
		super.onLogoutSuccess(request, response, authentication);
	}

}
