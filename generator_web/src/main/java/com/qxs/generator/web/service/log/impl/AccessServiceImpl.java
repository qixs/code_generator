package com.qxs.generator.web.service.log.impl;

import java.io.StringWriter;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qxs.generator.web.model.log.Access;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.repository.log.IAccessRepository;
import com.qxs.generator.web.service.log.IAccessService;

@Service
public class AccessServiceImpl implements IAccessService {
	
	@Autowired
	private IAccessRepository accessRepository;
	@Autowired
	private TemplateEngine templateEngine;

	@Override
	public String insert(Access access) {
		return accessRepository.saveAndFlush(access).getId();
	}
	
	@Transactional
	@Override
	public Page<Access> findList(String search, Integer offset, Integer limit, String sort, String order) {
		Sort s = Sort.unsorted();
		Pageable pageable = null;
		if(StringUtils.hasLength(sort)) {
			s = new Sort(Direction.fromOptionalString(order).orElse(Direction.ASC), sort);
		}
		if(offset != null) {
			pageable = PageRequest.of(offset / limit, limit, s);
		}
		
		return StringUtils.isEmpty(search) ? 
				accessRepository.findAll(pageable) : accessRepository.findAll(
						new Specification<Access>() {
							
							private static final long serialVersionUID = -3066008312509276295L;

							@Override
							public Predicate toPredicate(Root<Access> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
								List<Predicate> predicate = new ArrayList<>();
				                if(StringUtils.hasLength(search)){
				                	//用户
				                    Join<Access,User> userJoin = root.join(root.getModel().getSingularAttribute("user",User.class),JoinType.LEFT);
				                    predicate.add(cb.like(userJoin.get("username").as(String.class), "%" + search + "%"));
				                    //访问时间
				                    predicate.add(cb.like(root.get("accessDate").as(String.class), "%"+search+"%"));
				                    //参数
//				                    predicate.add(cb.like(root.get("parameters").as(String.class), "%"+search+"%"));
				                    //访问url
				                    predicate.add(cb.like(root.get("url").as(String.class), "%"+search+"%"));
				                    //结果
//				                    predicate.add(cb.like(root.get("result").as(String.class), "%"+search+"%"));
				                    //异常信息
//				                    predicate.add(cb.like(root.get("exception").as(String.class), "%"+search+"%"));
				                }
				                
				                return query.where(cb.or(predicate.toArray(new Predicate[predicate.size()]))).getRestriction();
							}
						}, pageable);
	}

	@Transactional
	@Override
	public Access getById(String id) {
		Access access = accessRepository.getOne(id);
		access.setParameters(formatJson(access.getParameters()));
		access.setResult(formatJson(access.getResult()));
		return access;
	}
	
	@Transactional
	@Override
	public byte[] generateAccessLogFile(String id) {
		Access access = getById(id);
		
		// 构造上下文(Model)
		Context context = new Context();
		context.setVariable("access", access);
		
		// 渲染模板
		StringWriter write = new StringWriter();
		templateEngine.process("log/access/mailLogFile", context, write);
		return write.toString().getBytes();
	}

	private String formatJson(String json) {
		JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
	}

	@Transactional
	@Override
	public void clear(String date) {
		accessRepository.deleteByAccessDate(date);
	}
}
