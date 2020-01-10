package com.qxs.generator.web.constant;

public enum IntConstants {
	
	/**
	 * 状态: 可用
	 * **/
	STATUS_ENABLE(0),
	/**
	 * 状态: 禁用
	 * **/
	STATUS_DISABLE(1),
	
	
	;
	
	private int code;
	
	private IntConstants(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	public int getIntCode() {
		return code;
	}
}
