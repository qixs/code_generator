package com.qxs.generator.web.service.version.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.model.version.Version;
import com.qxs.generator.web.repository.version.IVersionRepository;
import com.qxs.generator.web.service.version.IVersionService;
import com.qxs.generator.web.util.DateUtil;

@Service
public class VersionServiceImpl implements IVersionService {

	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 * **/
    public static final String CACHE_KEY = "'versionCache_'";
    /**
     * value属性表示使用哪个缓存策略，缓存策略在ehcache.xml
    */
    public static final String CACHE_NAME = "eternal";
	
	@Autowired
	private IVersionRepository versionRepository;
	
	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY)
	@Override
	public Version findVersion() {
		Version version = new Version();
		version.setStatus(IntConstants.STATUS_ENABLE.getCode());
		return versionRepository.findOne(Example.of(version)).get();
	}
	
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Transactional
	@Override
	public String updateVersion(String version) {
		//更新版本信息为无效状态
		versionRepository.updateStatusByStatus(IntConstants.STATUS_DISABLE.getCode(), IntConstants.STATUS_ENABLE.getCode());
		
		Version v = new Version();
		v.setVersion(version);
		v.setUpdateDate(DateUtil.currentDate());
		v.setStatus(IntConstants.STATUS_ENABLE.getCode());
		
		return versionRepository.save(v).getId();
	}

}
