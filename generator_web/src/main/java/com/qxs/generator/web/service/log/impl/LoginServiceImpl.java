package com.qxs.generator.web.service.log.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.qxs.generator.web.model.log.Login;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.repository.log.ILoginRepository;
import com.qxs.generator.web.service.log.ILoginService;
import com.qxs.generator.web.util.DateUtil;
import com.qxs.generator.web.util.RequestUtil;

@Service
public class LoginServiceImpl implements ILoginService {

	@Autowired
	private ILoginRepository loginRepository;

	@Transactional
	@Override
	public String login(User user) {
		Assert.notNull(user, "用户参数不能为空");
		
		Login login = new Login();
		login.setUserId(user.getId());
		login.setLoginDate(DateUtil.currentDate());
		login.setLoginIp(RequestUtil.getIpAddr());
		
		return loginRepository.saveAndFlush(login).getId();
	}

	@Transactional
	@Override
	public String logout(User user) {
		String loginLogId = user.getLoginLogId();
		
		Login login = loginRepository.findById(loginLogId).get();
		login.setExitDate(DateUtil.currentDate());
		
		return loginRepository.saveAndFlush(login).getId();
	}

	@Transactional
	@Override
	public Page<Login> findList(String search, Integer offset, Integer limit, String sort, String order) {
		Sort s = Sort.unsorted();
		Pageable pageable = null;
		if(StringUtils.hasLength(sort)) {
			s = new Sort(Direction.fromOptionalString(order).orElse(Direction.ASC), sort);
		}
		if(offset != null) {
			pageable = PageRequest.of(offset / limit, limit, s);
		}
		
		return StringUtils.isEmpty(search) ? 
				loginRepository.findAll(pageable) : loginRepository.findAll(
						new Specification<Login>() {
							private static final long serialVersionUID = -7443994505461831568L;
							@Override
							public Predicate toPredicate(Root<Login> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
								List<Predicate> predicate = new ArrayList<>();
				                if(StringUtils.hasLength(search)){
				                	//两张表关联查询
				                    Join<Login,User> userJoin = root.join(root.getModel().getSingularAttribute("user",User.class),JoinType.LEFT);
				                    predicate.add(cb.like(userJoin.get("username").as(String.class), "%" + search + "%"));
				                    
				                    //登录时间
				                    predicate.add(cb.like(root.get("loginDate").as(String.class), "%"+search+"%"));
				                    //退出时间
				                    predicate.add(cb.like(root.get("exitDate").as(String.class), "%"+search+"%"));
				                }
				                
				                return query.where(cb.or(predicate.toArray(new Predicate[predicate.size()]))).getRestriction();
							}
						}, pageable);
	}
	
	
}
