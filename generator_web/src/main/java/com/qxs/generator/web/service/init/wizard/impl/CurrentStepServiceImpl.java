package com.qxs.generator.web.service.init.wizard.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.qxs.generator.web.model.init.wizard.CurrentStep;
import com.qxs.generator.web.repository.init.wizard.ICurrentStepRepository;
import com.qxs.generator.web.service.init.wizard.ICurrentStepService;

@Service
public class CurrentStepServiceImpl implements ICurrentStepService {
	
	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 * **/
    public static final String CACHE_KEY = "'configCurrentStep_'";
    /**
     * value属性表示使用哪个缓存策略，缓存策略在ehcache.xml
    */
    public static final String CACHE_NAME = "eternal";
	
	@Autowired
	private ICurrentStepRepository currentStepRepository;

	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void save(int stepNum) {
		List<CurrentStep> currentStepList = currentStepRepository.findAll();
		CurrentStep currentStep = currentStepList.isEmpty() ? new CurrentStep() : currentStepList.get(0);
		
		currentStep.setCurrentStepNum(stepNum);
		
		currentStepRepository.save(currentStep);
	}

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY)
	@Override
	public int currentStepNum() {
		List<CurrentStep> currentStepList = currentStepRepository.findAll();
		
		return currentStepList.isEmpty() ? 1 : currentStepList.get(0).getCurrentStepNum();
	}

	
}
