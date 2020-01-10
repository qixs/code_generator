package com.qxs.generator.web.config.security.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.qxs.generator.web.constant.Constants;

public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {
	
	public LoginFailHandler(String defaultFailureUrl) {
		super(defaultFailureUrl);
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		HttpSession session = request.getSession();
		Object errorNum = session.getAttribute(Constants.CAPTCHA_SESSION_ERROR_NUM_KEY);
		if(errorNum == null) {
			errorNum = 0;
		}
		
		session.setAttribute(Constants.CAPTCHA_SESSION_ERROR_NUM_KEY, ((int)errorNum) + 1);
		
		super.onAuthenticationFailure(request, response, exception);
	}

}
