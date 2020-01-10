package com.qxs.generator.web.service.config.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.config.Email;
import com.qxs.generator.web.repository.config.IEmailRepository;
import com.qxs.generator.web.service.config.IEmailService;
import com.qxs.generator.web.service.mail.IMailService;

@Service
public class EmailServiceImpl implements IEmailService {
	
	private transient Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 * **/
    public static final String CACHE_KEY = "'configEmail_'";
    /**
     * value属性表示使用哪个缓存策略，缓存策略在ehcache.xml
    */
    public static final String CACHE_NAME = "eternal";
    
    @Value("${technicalSupport.email}")
	private String technicalSupportEmail;
    
	@Autowired
	private IEmailRepository emailRepository;
	@Autowired
	private IMailService mailService;

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY)
	@Override
	public long count() {
		long count = emailRepository.count();
		
		logger.debug("email配置信息条数:[{}]", count);
		
		return count;
	}

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY)
	@Override
	public Email findEmail() {
		List<Email> emailList = emailRepository.findAll();
		if(emailList.size() > 1) {
			throw new BusinessException("email配置信息有多条");
		}
		
		logger.debug("email配置信息:[{}]", emailList);
		
		return emailList.isEmpty() ? null : emailList.get(0);
	}

	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void save(Email email) {
		logger.debug("保存email配置信息:[{}]", email);
		
		emailRepository.save(email);
	}
	
	@Transactional
	@Override
	public void send(String emailFrom, String emailTo, String emailCarbonCopy, String subject, MultipartFile attachment,
			String content) {
		//校验发件人和配置是否一致
		Email email = findEmail();
		if(!email.getEmailFrom().equals(emailFrom)) {
			throw new BusinessException("发件人和系统中配置的发件人不一致，发送失败");
		}
		//校验收件人和配置是否一致
		if(!technicalSupportEmail.equals(emailTo)) {
			throw new BusinessException("收件人和系统中配置的收件人不一致，发送失败");
		}
		
		mailService.send(emailTo, emailCarbonCopy, subject, attachment, content, email);
	}

}
