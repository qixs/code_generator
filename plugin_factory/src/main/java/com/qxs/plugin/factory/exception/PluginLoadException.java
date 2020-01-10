package com.qxs.plugin.factory.exception;

/**
 * @author qixingshen
 * **/
public class PluginLoadException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public PluginLoadException(String message) {
		super(message);
	}
	public PluginLoadException(String message,Throwable throwable) {
		super(message,throwable);
	}
}
