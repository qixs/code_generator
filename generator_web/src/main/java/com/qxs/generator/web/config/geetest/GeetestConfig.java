package com.qxs.generator.web.config.geetest;


/**
 * Geetest配置
 * @author qixingshen
 * @date 2018-06-29
 * **/
public class GeetestConfig {
	
	public static final String API_URL = "http://api.geetest.com";
	
	public static final String REGISTER_URL = API_URL + "/register.php";
	
	public static final String VALIDATE_URL = API_URL + "/validate.php";
	
	public static final String CLIENT_TYPE = "web";
	
	public static final String JSON_FORMAT = "1";
	
	public static final String GEETEST_SERVER_STATUS = "gt_server_status";
	
	/**
	 * 极验验证二次验证表单数据 chllenge
	 */
	public static final String GEETEST_CHALLENGE = "geetest_challenge";
	
	/**
	 * 极验验证二次验证表单数据 validate
	 */
	public static final String GEETEST_VALIDATE = "geetest_validate";
	
	/**
	 * 极验验证二次验证表单数据 seccode
	 */
	public static final String GEETEST_SECCODE = "geetest_seccode";
	/**
	 * session中的键
	 * **/
	public static final String SESSION_GEETEST_ID = "GEETEST_ID";
	public static final String SESSION_GEETEST_KEY = "GEETEST_KEY";
}
