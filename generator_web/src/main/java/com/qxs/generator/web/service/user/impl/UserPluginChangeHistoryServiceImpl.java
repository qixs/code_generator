package com.qxs.generator.web.service.user.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.UserPluginChangeHistory;
import com.qxs.generator.web.repository.user.IUserPluginChangeHistoryRepository;
import com.qxs.generator.web.service.user.IUserPluginChangeHistoryService;

@Service
public class UserPluginChangeHistoryServiceImpl implements IUserPluginChangeHistoryService {

	@Autowired
	private IUserPluginChangeHistoryRepository userPluginChangeHistoryRepository;
	
	@Transactional
	@Override
	public String insert(UserPluginChangeHistory pluginChangeHistory) {
		return userPluginChangeHistoryRepository.saveAndFlush(pluginChangeHistory).getId();
	}
	
	@Transactional
	@Override
	public Page<UserPluginChangeHistory> findList(String search, Integer offset, Integer limit, String sort, String order) {
		Sort s = Sort.unsorted();
		Pageable pageable = null;
		if(StringUtils.hasLength(sort)) {
			s = new Sort(Direction.fromOptionalString(order).orElse(Direction.ASC), sort);
		}
		if(offset != null) {
			pageable = PageRequest.of(offset / limit, limit, s);
		}
		
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		UserPluginChangeHistory userPluginChangeHistory = new UserPluginChangeHistory();
		userPluginChangeHistory.setUserId(user.getId());
		
		return StringUtils.isEmpty(search) ? 
				userPluginChangeHistoryRepository.findAll(Example.of(userPluginChangeHistory), pageable) : userPluginChangeHistoryRepository.findAll(
						new Specification<UserPluginChangeHistory>() {
							
							private static final long serialVersionUID = -3066008312509276295L;

							@Override
							public Predicate toPredicate(Root<UserPluginChangeHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
								List<Predicate> orPredicate = new ArrayList<>();
				                if(StringUtils.hasLength(search)){
				                    //插件名
				                    orPredicate.add(cb.like(root.get("pluginName").as(String.class), "%" + search + "%"));
				                    //插件描述
				                    orPredicate.add(cb.like(root.get("pluginDescription").as(String.class), "%" + search + "%"));
				                    //变更时间
				                    orPredicate.add(cb.like(root.get("updateDate").as(String.class), "%"+search+"%"));
				                }
				                
				                List<Predicate> andPredicate = new ArrayList<>();
				                //用户id字段
				                andPredicate.add(cb.equal(root.get("userId").as(String.class), userPluginChangeHistory.getUserId()));

				                return query.where(cb.and(andPredicate.toArray(new Predicate[andPredicate.size()])),
				                		cb.or(orPredicate.toArray(new Predicate[orPredicate.size()]))).getRestriction();
							}
						}, pageable);
	}
	
}
