package com.qxs.generator.web.exception;

import com.qxs.base.exception.AbstractBusinessException;

/**
 * 服务启动异常.
 * 
 */
public class StartupException extends AbstractBusinessException {

	private static final long serialVersionUID = 5329282992053083568L;

	public StartupException(String message) {
		super(message);
	}

	public StartupException(Throwable cause) {
		super(cause);
	}

	public StartupException(String message, Throwable cause) {
		super(message, cause);
	}

}
