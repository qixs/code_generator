package com.qxs.generator.web.id.generator;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.AbstractUUIDGenerator;

public class IdGenerator extends AbstractUUIDGenerator {
	
	public static final String ID_GENERATOR_NAME = "idGenerator";
	public static final String ID_GENERATOR_FULL_CLASS_NAME = "com.qxs.generator.web.id.generator.IdGenerator";

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		return ObjectId.stringId();
	}
}
