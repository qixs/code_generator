package com.qxs.generator.web.config.security.resources;

import org.springframework.security.access.AccessDeniedException;

public class ResourceAccessDeniedException extends AccessDeniedException {

	private static final long serialVersionUID = -501496606925109338L;
	
	public ResourceAccessDeniedException(String msg) {
		super(msg);
	}

	public ResourceAccessDeniedException(String msg, Throwable t) {
		super(msg, t);
	}
}
