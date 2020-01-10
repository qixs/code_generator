package com.qxs.generator.web.service.init.wizard;

import com.qxs.generator.web.exception.BusinessException;

/**
 * @author qixingshen
 * **/
public interface IInitWizardService {
	
	/**
	 * 完成初始化
	 * @return void
	 * @throws BusinessException
	 * **/
	void complete();
}
