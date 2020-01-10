package com.qxs.generator.web.service.config.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.qxs.generator.web.config.security.filter.GenerateCodeSemaphoreFilter;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.config.SystemParameter;
import com.qxs.generator.web.repository.config.ISystemParameterRepository;
import com.qxs.generator.web.service.config.ISystemParameterService;

@Service
public class SystemParameterServiceImpl implements ISystemParameterService {
	
	private transient Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 * **/
    public static final String CACHE_KEY = "'configSystemParameter_'";
    /**
     * value属性表示使用哪个缓存策略，缓存策略在ehcache.xml
    */
    public static final String CACHE_NAME = "eternal";

	@Autowired
	private ISystemParameterRepository systemParameterRepository;

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY)
	@Override
	public long count() {
		long count = systemParameterRepository.count();
		
		logger.debug("系统参数配置信息条数:[{}]", count);
		
		return count;
	}

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY)
	@Override
	public SystemParameter findSystemParameter() {
		List<SystemParameter> systemParameterList = systemParameterRepository.findAll();
		if(systemParameterList.size() > 1) {
			throw new BusinessException("系统参数配置信息有多条");
		}
		
		logger.debug("系统参数配置信息:[{}]", systemParameterList);
		
		return systemParameterList.isEmpty() ? null : systemParameterList.get(0);
	}

	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void save(SystemParameter systemParameter) {
		logger.debug("保存系统参数配置信息:[{}]", systemParameter);

		SystemParameter oldSystemParameter = findSystemParameter();

		//原来的最大请求数
		int oldMaxTaskCount = oldSystemParameter.getMaxTaskCount();

		systemParameterRepository.save(systemParameter);

		GenerateCodeSemaphoreFilter.updateSemaphore(oldMaxTaskCount, systemParameter.getMaxTaskCount());
	}

}
