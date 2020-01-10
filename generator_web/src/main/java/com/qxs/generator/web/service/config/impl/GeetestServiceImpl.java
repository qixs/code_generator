package com.qxs.generator.web.service.config.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.qxs.generator.web.config.geetest.GeetestConfig;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.config.Geetest;
import com.qxs.generator.web.repository.config.IGeetestRepository;
import com.qxs.generator.web.service.config.IGeetestService;
import com.qxs.generator.web.util.RequestUtil;

@Service
public class GeetestServiceImpl implements IGeetestService {
	
	private transient Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 这里的单引号不能少，否则会报错，被识别是一个对象
	 * **/
    public static final String CACHE_KEY = "'configGeetest_'";
    /**
     * value属性表示使用哪个缓存策略，缓存策略在ehcache.xml
    */
    public static final String CACHE_NAME = "eternal";
    
    private static final List<Geetest> GEETEST_LIST = new ArrayList<>();
    
	@Autowired
	private IGeetestRepository geetestRepository;

	@Transactional
	@Override
	public List<Geetest> findAll(Sort sort) {
		return sort == null ? geetestRepository.findAll() : geetestRepository.findAll(sort);
	}

	/**
	 * 查询所有的Geetest配置信息(注:带缓存)
	 * 
	 * @return List<Geetest>
	 * **/
	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY)
	private List<Geetest> findAll() {
		return findAll(null);
	} 

	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void insert(Geetest geetest) {
		batchInsert(Lists.newArrayList(geetest));
	}
	
	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void batchInsert(List<Geetest> geetestList) {
		//校验重复
		//id不能重复
		//key不能重复
		List<String> ids = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		for(Geetest geetest : geetestList) {
			String id = geetest.getId();
			if(ids.contains(id)) {
				throw new BusinessException("id不能重复");
			}
			String key = geetest.getKey();
			if(keys.contains(key)) {
				throw new BusinessException("key不能重复");
			}
			
			ids.add(id);
			keys.add(key);
		}
		
		//校验数据库中的id和key是否重复
		long idCount = geetestRepository.count(new Specification<Geetest>() {
			private static final long serialVersionUID = -7121666686084423174L;

			@Override
			public Predicate toPredicate(Root<Geetest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				list.add(root.get("id").in(ids));
				
				Predicate[] p = new Predicate[list.size()];
				query.where(cb.and(list.toArray(p)));
				
				return query.getRestriction();
			}
		});
		if(idCount > 0) {
			throw new BusinessException("id不能重复");
		}
		
		long keyCount = geetestRepository.count(new Specification<Geetest>() {
			private static final long serialVersionUID = -7121666686084423174L;

			@Override
			public Predicate toPredicate(Root<Geetest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				list.add(root.get("key").in(keys));
				
				Predicate[] p = new Predicate[list.size()];
				query.where(cb.and(list.toArray(p)));
				
				return query.getRestriction();
			}
		});
		if(keyCount > 0) {
			throw new BusinessException("key不能重复");
		}
		
		geetestRepository.saveAll(geetestList);
		
		//重新加载Geetest配置信息
		reloadGeetest();
	}

	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void update(Geetest geetest) {
		batchUpdate(Lists.newArrayList(geetest));
	}

	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void batchUpdate(List<Geetest> geetestList) {
		geetestRepository.saveAll(geetestList);
		
		//重新加载Geetest配置信息
		reloadGeetest();
	}
	
	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void deleteById(String id) {
		deleteByIds(Lists.newArrayList(id));
	}

	@Transactional
	@CacheEvict(value = CACHE_NAME, allEntries = true)
	@Override
	public void deleteByIds(List<String> ids) {
		geetestRepository.deleteByIds(ids);
		
		//重新加载Geetest配置信息
		reloadGeetest();
	}

	@Transactional
	@Cacheable(value = CACHE_NAME , key= CACHE_KEY + "+#id")
	@Override
	public Geetest findById(String id) {
		return geetestRepository.findById(id).orElse(null);
	}
	
	/**
	 * 重新加载Geetest配置信息
	 * **/
	private synchronized void reloadGeetest() {
		List<Geetest> geetestList = findAll();
		
		synchronized (GEETEST_LIST) {
			GEETEST_LIST.clear();
			
			if(geetestList.size() == 1){
				//如果geetestList只有一条记录不管权重是多少只记录一个参数即可
				GEETEST_LIST.add(geetestList.get(0));
			}else {
				//geetestList有多条记录则需要按照权重增加Geetest配置信息
				for(Geetest geetest : geetestList) {
					int weight = geetest.getWeight();
					for(int i = 0 ; i < weight ; i ++) {
						GEETEST_LIST.add(geetest);
					}
				}
			}
		}
	}

	@Override
	public Geetest nextGeetest() {
		if(GEETEST_LIST.isEmpty()) {
			reloadGeetest();
		}
		if(GEETEST_LIST.isEmpty()) {
			return null;
		}
		if(GEETEST_LIST.size() == 1) {
			return GEETEST_LIST.get(0);
		}
		int next = new Random().nextInt(GEETEST_LIST.size()) + 1;
		
		return GEETEST_LIST.get(next - 1);
	}

	@Override
	public String register() {
		Geetest geetest = nextGeetest();
		
		HttpSession session = RequestUtil.getRequest().getSession();
		session.setAttribute(GeetestConfig.SESSION_GEETEST_ID, geetest.getId());
		session.setAttribute(GeetestConfig.SESSION_GEETEST_KEY, geetest.getKey());
		
		//进行验证预处理
		GeetestResponseEntity geetestResponseEntity = geetestPreProcess(geetest);
		
		//将服务器状态设置到session中
		session.setAttribute(GeetestConfig.GEETEST_SERVER_STATUS, geetestResponseEntity.getSuccess());
		
		return new Gson().toJson(geetestResponseEntity);
	}
	/**
	 * 对验证码进行预处理
	 * **/
	private GeetestResponseEntity geetestPreProcess(Geetest geetest) {
		HttpServletRequest request = RequestUtil.getRequest();
		
		MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
		params.add("gt", geetest.getId());
		params.add("json_format", GeetestConfig.JSON_FORMAT);
		params.add("client_type", GeetestConfig.CLIENT_TYPE);
		params.add("user_id", request.getSession().getId());
		params.add("ip_address", RequestUtil.getIpAddr(request));
		
		HttpHeaders headers = new HttpHeaders();
		//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(GeetestConfig.REGISTER_URL,requestEntity, String.class);
		
		String result = responseEntity.getBody();
		
		logger.debug("result:{}", result);
		
		GeetestResponseEntity geetestResponseEntity = new Gson().fromJson(result, GeetestResponseEntity.class);
	    String challenge = geetestResponseEntity.getChallenge();
	
		if (challenge.length() == 32) {
			geetestResponseEntity.setSuccess(1).setGt(geetest.getId())
				.setChallenge(this.md5Encode(geetestResponseEntity.getChallenge() + geetest.getKey()));
		}else {
			Long rnd1 = Math.round(Math.random() * 100);
			Long rnd2 = Math.round(Math.random() * 100);
			String md5Str1 = md5Encode(rnd1 + "");
			String md5Str2 = md5Encode(rnd2 + "");
			
			geetestResponseEntity = new GeetestResponseEntity(0, geetest.getId(), md5Str1 + md5Str2.substring(0, 2), true);
		}
		return geetestResponseEntity;
	}
	
	/**
	 * md5 加密
	 * 
	 * @time 2014年7月10日 下午3:30:01
	 * @param plainText
	 * @return
	 */
	private String md5Encode(String plainText) {
		String result = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}

			result = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	@Override
	public void enhencedValidate() {
		HttpServletRequest request = RequestUtil.getRequest();
		
		String challenge = request.getParameter(GeetestConfig.GEETEST_CHALLENGE);
		String validate = request.getParameter(GeetestConfig.GEETEST_VALIDATE);
		String seccode = request.getParameter(GeetestConfig.GEETEST_SECCODE);
		
		HttpSession session = RequestUtil.getRequest().getSession();
		Object geetestId = session.getAttribute(GeetestConfig.SESSION_GEETEST_ID);
		Object geetestKey = session.getAttribute(GeetestConfig.SESSION_GEETEST_KEY);
		Object geetestServerStatus = session.getAttribute(GeetestConfig.GEETEST_SERVER_STATUS);
		if(geetestId == null || geetestKey == null || geetestServerStatus == null) {
			// 验证失败
			throw new BusinessException("验证码验证失败，请重试");
		}
		
		Geetest geetest = new Geetest();
		geetest.setId(geetestId.toString());
		geetest.setKey(geetestKey.toString());
		
		//从session中获取gt-server状态
		int geetestServerStatusCode = (Integer) geetestServerStatus;
		
		int success = 0;

		if (geetestServerStatusCode == 1) {
			//gt-server正常，向gt-server进行二次验证
			success = enhencedValidateRequest(geetest,challenge, validate, seccode);
		} else {
			// gt-server非正常情况下，进行failback模式验证
			success = failbackValidateRequest(challenge, validate, seccode);
		}

		if (success != 1) {
			// 验证失败
			throw new BusinessException("验证码验证错误，请重试");
		}
	}
	
	/**
	 * failback使用的验证方式
	 * 
	 * @param challenge
	 * @param validate
	 * @param seccode
	 * @return 验证结果,1表示验证成功0表示验证失败
	 */
	public int failbackValidateRequest(String challenge, String validate, String seccode) {
		if (!resquestIsLegal(challenge, validate, seccode)) {
			return 0;
		}
		
		return 1;
	}
	
	/**
	 * 服务正常的情况下使用的验证方式,向gt-server进行二次验证,获取验证结果
	 * 
	 * @param challenge
	 * @param validate
	 * @param seccode
	 * @return 验证结果,1表示验证成功0表示验证失败
	 */
	public int enhencedValidateRequest(Geetest geetest,String challenge, String validate, String seccode) {
		if (!resquestIsLegal(challenge, validate, seccode)) {
			return 0;
		}
		
		if (validate.length() <= 0) {
			return 0;
		}

		if (!checkResultByPrivate(geetest.getKey(), challenge, validate)) {
			return 0;
		}
		
		HttpServletRequest request = RequestUtil.getRequest();
		
		MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
		params.add("challenge", challenge);
		params.add("validate", validate);
		params.add("seccode", seccode);
		params.add("json_format", GeetestConfig.JSON_FORMAT);
		params.add("client_type", GeetestConfig.CLIENT_TYPE);
		params.add("user_id", request.getSession().getId());
		params.add("ip_address", RequestUtil.getIpAddr(request));
		
		HttpHeaders headers = new HttpHeaders();
		//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(GeetestConfig.VALIDATE_URL,requestEntity, String.class);
		
		String response = responseEntity.getBody();
		
		logger.debug("response:{}", response);
		
		GeetestResponseEntity geetestResponseEntity = new Gson().fromJson(response, GeetestResponseEntity.class);;
		
		return geetestResponseEntity.getSeccode().equals(md5Encode(seccode)) ? 1 : 0;
	}
	protected boolean checkResultByPrivate(String key,String challenge, String validate) {
		String encodeStr = md5Encode(key + "geetest" + challenge);
		return validate.equals(encodeStr);
	}
	/**
	 * 检查客户端的请求是否合法,三个只要有一个为空，则判断不合法
	 * 
	 * @param request
	 * @return
	 */
	private boolean resquestIsLegal(String challenge, String validate, String seccode) {
		if (!StringUtils.hasLength(challenge)) {
			return false;
		}

		if (!StringUtils.hasLength(validate)) {
			return false;
		}

		if (!StringUtils.hasLength(seccode)) {
			return false;
		}

		return true;
	}
	
	private class GeetestResponseEntity{
		
		private int success;
		
		private String gt;
		
		private String challenge;
		
		private Boolean new_captcha;
		
		private String seccode;
		
		private GeetestResponseEntity() {}
		
		private GeetestResponseEntity(int success,String gt,String challenge,boolean new_captcha) {
			this.success = success;
			this.gt = gt;
			this.challenge = challenge;
			this.new_captcha = new_captcha;
		}

		public int getSuccess() {
			return success;
		}

		public GeetestResponseEntity setSuccess(int success) {
			this.success = success;
			return this;
		}
		@SuppressWarnings("unused")
		public String getGt() {
			return gt;
		}

		public GeetestResponseEntity setGt(String gt) {
			this.gt = gt;
			return this;
		}

		public String getChallenge() {
			return challenge;
		}

		@SuppressWarnings("unused")
		public GeetestResponseEntity setChallenge(String challenge) {
			this.challenge = challenge;
			return this;
		}
		@SuppressWarnings("unused")
		public boolean isNew_captcha() {
			return new_captcha;
		}
		@SuppressWarnings("unused")
		public GeetestResponseEntity setNew_captcha(boolean new_captcha) {
			this.new_captcha = new_captcha;
			return this;
		}

		public String getSeccode() {
			return seccode;
		}
		@SuppressWarnings("unused")
		public void setSeccode(String seccode) {
			this.seccode = seccode;
		}
		
	}
}
