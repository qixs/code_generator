package com.qxs.generator.web.constant;

public enum EncryptKeyConstants {
	/**
	 * 用户生成代码文件日志记录数据库连接密码
	 * **/
	GENERATE_LOG_DATABASE_PASSWORD("@log_generate_%s_database)"),
	/**
	 * 用户生成代码文件日志记录ssh连接密码
	 * **/
	GENERATE_LOG_SSH_PASSWORD("!log_generate_%s_ssh_key#");
	
	private String key;
	
	private EncryptKeyConstants(String key) {
		this.key = key;
	}
	
	public String getKey(Object... args) {
		return String.format(key, args);
	}
}
