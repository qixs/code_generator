package com.qxs.generator.web.service.init.wizard.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.init.wizard.Complete;
import com.qxs.generator.web.repository.init.wizard.ICompleteRepository;
import com.qxs.generator.web.service.init.wizard.ICompleteService;

@Service
public class CompleteServiceImpl implements ICompleteService {
	
	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 * **/
    public static final String CACHE_KEY = "'complete_'";
    /**
     * value属性表示使用哪个缓存策略，缓存策略在ehcache.xml
    */
    public static final String CACHE_NAME = "eternal";
	
	@Autowired
	private ICompleteRepository completeRepository;

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY)
	@Override
	public Complete findComplete() {
		List<Complete> completeList = completeRepository.findAll();
		if(completeList.size() > 1) {
			throw new BusinessException("系统初始化-完成初始化登记表含有多条记录");
		}
		return completeList.isEmpty() ? null : completeList.get(0);
	}

	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void save() {
		Complete complete = new Complete();
		complete.setStatus(Complete.STATUS_COMPLETE);
		
		completeRepository.save(complete);
	}

}
