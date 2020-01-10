package com.qxs.generator.web.service.connection.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.connection.Connection;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.repository.connection.ISshRepository;
import com.qxs.generator.web.service.connection.ISshService;

@Service
public class SshServiceImpl implements ISshService {
	
	@Autowired
	private ISshRepository sshRepository;

	@Transactional
	@Override
	public Ssh findByConnection(Connection connection) {
		if(connection == null || StringUtils.isEmpty(connection.getId())) {
			return null;
		}
		Ssh ssh = new Ssh();
		ssh.setConnectionId(connection.getId());
		return sshRepository.findOne(Example.of(ssh)).orElseThrow(()->new BusinessException("ssh参数查询出错"));
	}

	@Override
	public Ssh saveAndFlush(Ssh ssh) {
		return sshRepository.saveAndFlush(ssh);
	}

	@Override
	public void delete(Ssh ssh) {
		ssh = sshRepository.findOne(Example.of(ssh)).orElse(null);
		if(ssh != null) {
			sshRepository.delete(ssh);
		}
	}
	
}
