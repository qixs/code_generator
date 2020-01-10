package com.qxs.base.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 异常信息
 * 
 * @author qxs
 * @date 2015-9-18
 */
public class SQLFormatException extends AbstractBusinessException {
	
	private static final long serialVersionUID = -7631087787767241321L;
	
	protected transient final Log log = LogFactory.getLog(getClass());

	public SQLFormatException(String message){
		super(message);
		if(log.isDebugEnabled()){
        	log.debug(message);
        }
	}
	
	public SQLFormatException(String message, Throwable cause) {
        super(message, cause);
        if(log.isDebugEnabled()){
        	log.debug(message, cause);
        }
    }
	
	public SQLFormatException(Throwable cause) {
        super(cause);
        if(log.isDebugEnabled()){
        	log.debug(cause.getMessage(), cause);
        }
    }
	
	@Override
	public String getMessage() {
		return super.getMessage();
	}
	
}
