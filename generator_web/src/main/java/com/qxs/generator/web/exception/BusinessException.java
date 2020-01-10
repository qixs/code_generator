package com.qxs.generator.web.exception;

/**
 * 异常信息
 * 
 * @author qxs
 * @date 2015-9-18
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = -4396971401070584129L;

	public BusinessException(String message){
		super(message);
	}
	public BusinessException(String message,Exception e){
		super(message,e);
	}
	public BusinessException(Exception e){
		super(e);
	}
}
