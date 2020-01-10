package com.qxs.plugin.factory.exception;

/**
 * @author qixingshen
 * **/
public class PluginConfigParseException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public PluginConfigParseException(String message) {
		super(message);
	}

	public PluginConfigParseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
