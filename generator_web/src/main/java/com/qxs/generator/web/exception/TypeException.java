package com.qxs.generator.web.exception;

import com.qxs.base.exception.AbstractBusinessException;

/**
 * 类型转换异常.
 * 
 * @author <a href="mailto:wuzhiqiang@novacloud.com">wuzq</a>
 * @date 2012-11-16下午4:10:07
 * @version Revision: 1.0
 */
public class TypeException extends AbstractBusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public TypeException(String message) {
		super(message);
	}

	public TypeException(Throwable cause) {
		super(cause);
	}

	public TypeException(String message, Throwable cause) {
		super(message, cause);
	}

}
