package com.qxs.generator.web.exception;

import com.qxs.base.exception.AbstractBusinessException;

/**
 * 异常信息
 * 
 * @author qxs
 * @date 2015-9-18
 */
public class ConfigException extends AbstractBusinessException {
	
	private static final long serialVersionUID = 2951810003621618378L;

	public ConfigException(String message){
		super(message);
	}
	
	public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public ConfigException(Throwable cause) {
        super(cause);
    }
	
}
