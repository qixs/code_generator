package com.qxs.plugin.factory.exception;

/**
 * @author qixingshen
 * **/
public class PluginUnloadException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public PluginUnloadException(String message) {
		super(message);
	}
	public PluginUnloadException(String message,Throwable throwable) {
		super(message,throwable);
	}
}
