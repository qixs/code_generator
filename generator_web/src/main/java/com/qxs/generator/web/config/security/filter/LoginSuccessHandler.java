package com.qxs.generator.web.config.security.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.qxs.generator.web.constant.Constants;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.service.log.ILoginService;

@Component
public class LoginSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginSuccessHandler.class);

	@Autowired
	private ILoginService loginService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		User user = (User) authentication.getPrincipal();
		
		LOGGER.debug("用户登录,用户名:{}", user.getUsername());
		
		//登记登录日志
		String loginId = loginService.login(user);
		
		//记录登录id
		user.setLoginLogId(loginId);
		
		//登录成功需要重置session中登记的错误次数信息
		HttpSession session = request.getSession();
		session.setAttribute(Constants.CAPTCHA_SESSION_ERROR_NUM_KEY, 0);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
