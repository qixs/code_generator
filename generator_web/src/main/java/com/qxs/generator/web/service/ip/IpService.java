package com.qxs.generator.web.service.ip;

/**
 * ip查询service
 * 
 * @author qixingshen
 * @date 2018-07-07
 * @version 1.0
 * **/
public interface IpService {
	
	/**
	 * 查询ip归属地
	 * **/
	String findIpAddress(String ip);
}
