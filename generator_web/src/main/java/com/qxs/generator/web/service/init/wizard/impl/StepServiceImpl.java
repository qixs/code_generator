package com.qxs.generator.web.service.init.wizard.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.qxs.generator.web.model.init.wizard.Step;
import com.qxs.generator.web.repository.init.wizard.IStepRepository;
import com.qxs.generator.web.service.init.wizard.IStepService;

@Service
public class StepServiceImpl implements IStepService {
	
	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 * **/
    public static final String CACHE_KEY = "'configStep_'";
    /**
     * value属性表示使用哪个缓存策略，缓存策略在ehcache.xml
    */
    public static final String CACHE_NAME = "eternal";
	
	@Autowired
	private IStepRepository stepRepository;

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY + "+#stepNum")
	@Override
	public Step findStepByStepNum(int stepNum) {
		Step step = new Step();
		step.setStepNum(stepNum);
		return stepRepository.findOne(Example.of(step)).orElse(null);
	}

	@Transactional
	@Override
	public void validStep() {
		List<Step> stepList = stepRepository.findAll(Sort.by(Direction.ASC, "stepNum"));
		//校验步骤号是否连续,必须是1 2 3 4 5 6类型的数字
		for(int i = 0 , length = stepList.size() ; i < length ; i ++) {
			Assert.isTrue(i + 1 == stepList.get(i).getStepNum(), "步骤号配置不正确，请核实步骤号");
		}
	}

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY + "'maxStepNum'")
	@Override
	public long maxStepNum() {
		return stepRepository.count();
	}

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY + "'findStepUrlList'")
	@Override
	public List<String> findStepUrlList() {
		List<Step> stepList = stepRepository.findAll();
		return stepList.stream().map(Step::getStepUrl).collect(Collectors.toList());
	}

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY + "'findStepList'")
	@Override
	public List<Step> findStepList() {
		return stepRepository.findAll(Sort.by(Direction.ASC, "stepNum"));
	}
	
}
