package com.qxs.generator.web.service.plugin.impl;

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
import org.springframework.util.StringUtils;

import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.plugin.PluginChangeHistory;
import com.qxs.generator.web.repository.plugin.IPluginChangeHistoryRepository;
import com.qxs.generator.web.service.plugin.IPluginChangeHistoryService;

/**
 * @author qixingshen
 * **/
@Service
public class PluginChangeHistoryServiceImpl implements IPluginChangeHistoryService{
	
	@Autowired
	private IPluginChangeHistoryRepository pluginChangeHistoryRepository;
	
	@Transactional
	@Override
	public String insert(PluginChangeHistory pluginChangeHistory) {
		return pluginChangeHistoryRepository.saveAndFlush(pluginChangeHistory).getId();
	}
	
	@Transactional
	@Override
	public Page<PluginChangeHistory> findList(String search, Integer offset, Integer limit, String sort, String order) {
		Sort s = Sort.unsorted();
		Pageable pageable = null;
		if(StringUtils.hasLength(sort)) {
			s = new Sort(Direction.fromOptionalString(order).orElse(Direction.ASC), sort);
		}
		if(offset != null) {
			pageable = PageRequest.of(offset / limit, limit, s);
		}
		
		return StringUtils.isEmpty(search) ? 
				pluginChangeHistoryRepository.findAll(pageable) : pluginChangeHistoryRepository.findAll(
						new Specification<PluginChangeHistory>() {
							
							private static final long serialVersionUID = -3066008312509276295L;

							@Override
							public Predicate toPredicate(Root<PluginChangeHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
								List<Predicate> predicate = new ArrayList<>();
				                if(StringUtils.hasLength(search)){
				                	//两张表关联查询
				                    Join<PluginChangeHistory,Plugin> pluginJoin = root.join(root.getModel().getSingularAttribute("plugin",Plugin.class),JoinType.LEFT);
									//插件组名
									predicate.add(cb.like(pluginJoin.get("groupName").as(String.class), "%" + search + "%"));
				                    //插件名
				                    predicate.add(cb.like(pluginJoin.get("name").as(String.class), "%" + search + "%"));
				                    //插件描述
				                    predicate.add(cb.like(pluginJoin.get("description").as(String.class), "%" + search + "%"));
				                    //变更人
				                    predicate.add(cb.like(root.get("updateUserName").as(String.class), "%"+search+"%"));
				                    //变更时间
				                    predicate.add(cb.like(root.get("updateDate").as(String.class), "%"+search+"%"));
				                }
				                
				                return query.where(cb.or(predicate.toArray(new Predicate[predicate.size()]))).getRestriction();
							}
						}, pageable);
	}
	
}
