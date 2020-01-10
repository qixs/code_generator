package com.qxs.plugin.factory.exception;

/**
 * @author qixingshen
 * **/
public class PluginNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public PluginNotFoundException(String message) {
		super(message);
	}
	
}
