package com.qxs.plugin.factory.exception;

/**
 * @author qixingshen
 * **/
public class CodeGenerateException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public CodeGenerateException(String message) {
		super(message);
	}
	public CodeGenerateException(String message,Throwable throwable) {
		super(message,throwable);
	}
	public CodeGenerateException(Throwable cause) {
        super(cause);
    }
}
