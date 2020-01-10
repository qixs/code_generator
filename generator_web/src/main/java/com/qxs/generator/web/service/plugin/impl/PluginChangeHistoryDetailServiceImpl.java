package com.qxs.generator.web.service.plugin.impl;

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

import com.qxs.generator.web.model.plugin.PluginChangeHistoryDetail;
import com.qxs.generator.web.repository.plugin.IPluginChangeHistoryDetailRepository;
import com.qxs.generator.web.service.plugin.IPluginChangeHistoryDetailService;

/**
 * @author qixingshen
 * **/
@Service
public class PluginChangeHistoryDetailServiceImpl implements IPluginChangeHistoryDetailService{
	
	@Autowired
	private IPluginChangeHistoryDetailRepository pluginChangeHistoryDetailRepository;
	
	@Transactional
	@Override
	public void insert(List<PluginChangeHistoryDetail> pluginChangeHistoryDetails) {
		pluginChangeHistoryDetailRepository.saveAll(pluginChangeHistoryDetails);
	}

	@Transactional
	@Override
	public List<PluginChangeHistoryDetail> findList(String pluginChangeHistoryId) {
		return pluginChangeHistoryDetailRepository.findAll(
				new Specification<PluginChangeHistoryDetail>() {
					private static final long serialVersionUID = -7443994505461831568L;
					@Override
					public Predicate toPredicate(Root<PluginChangeHistoryDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						List<Predicate> predicate = new ArrayList<>();
	                    predicate.add(cb.equal(root.get("changeHistoryId").as(String.class), pluginChangeHistoryId));
		                
		                return query.where(cb.or(predicate.toArray(new Predicate[predicate.size()]))).getRestriction();
					}
				});
	}

	
}
