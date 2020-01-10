package com.qxs.generator.web.config.security.resources;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

public class CustomAccessDeniedHandlerImpl extends AccessDeniedHandlerImpl {

	@Override
	public void handle(HttpServletRequest request,HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException,ServletException {
		
		if(accessDeniedException instanceof ResourceAccessDeniedException){
			request.setAttribute("accessDeniedException", accessDeniedException.getMessage());
		}
		
		super.handle(request, response, accessDeniedException);
	}
	
}
