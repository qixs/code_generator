package com.qxs.generator.web.service.ip.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.qxs.generator.web.service.ip.IpService;
import com.qxs.generator.web.util.IPSeeker;

@Service
public class IpServiceImpl implements IpService {

	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 * **/
    public static final String CACHE_KEY = "'ipSeeker_'";
    /**
     * value属性表示使用哪个缓存策略，缓存策略在ehcache.xml
    */
    public static final String CACHE_NAME = "eternal";
	
    @Cacheable(value = CACHE_NAME , key= CACHE_KEY + "+#ip")
	@Override
	public String findIpAddress(String ip) {
		return IPSeeker.getInstance().getAddress(ip);
	}
}
