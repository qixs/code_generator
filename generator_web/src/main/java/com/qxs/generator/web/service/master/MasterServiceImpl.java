package com.qxs.generator.web.service.master;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.qxs.generator.web.model.master.Master;
import com.qxs.generator.web.repository.master.IMasterRepository;

@Service
public class MasterServiceImpl implements IMasterService {

	@Autowired
	private IMasterRepository masterRepository;
	
	@Transactional
	@Override
	public boolean findTableExists(String tableName) {
		Master master = new Master();
		master.setTblName(tableName);
		
		return masterRepository.count(Example.of(master)) > 0;
	}

}
