package com.qxs.generator.web.service.user.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.qxs.generator.web.model.user.UserPluginChangeHistoryDetail;
import com.qxs.generator.web.repository.user.IUserPluginChangeHistoryDetailRepository;
import com.qxs.generator.web.service.user.IUserPluginChangeHistoryDetailService;

@Service
public class UserPluginChangeHistoryDetailServiceImpl implements IUserPluginChangeHistoryDetailService {

	@Autowired
	private IUserPluginChangeHistoryDetailRepository userPluginChangeHistoryDetailRepository;
	
	@Transactional
	@Override
	public void insert(List<UserPluginChangeHistoryDetail> pluginChangeHistoryDetails) {
		userPluginChangeHistoryDetailRepository.saveAll(pluginChangeHistoryDetails);
	}

	@Transactional
	@Override
	public List<UserPluginChangeHistoryDetail> findList(String pluginChangeHistoryId) {
		return userPluginChangeHistoryDetailRepository.findAll(
				new Specification<UserPluginChangeHistoryDetail>() {
					private static final long serialVersionUID = -7443994505461831568L;
					@Override
					public Predicate toPredicate(Root<UserPluginChangeHistoryDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						List<Predicate> predicate = new ArrayList<>();
	                    predicate.add(cb.equal(root.get("changeHistoryId").as(String.class), pluginChangeHistoryId));
		                
		                return query.where(cb.or(predicate.toArray(new Predicate[predicate.size()]))).getRestriction();
					}
				});
	}

	
}
