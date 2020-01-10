package com.qxs.generator.web.config.tag.dialect;

import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.qxs.generator.web.config.tag.processor.EmailSansitiveEncryptProcessor;
import com.qxs.generator.web.config.tag.processor.MobileSansitiveEncryptProcessor;
/***
 * 敏感信息加密dialect
 * @author qixingshen
 * @date 2018-07-23
 * @version 1.0
 * **/
public class SansitiveEncryptDialect extends AbstractProcessorDialect{

    private static final String PREFIX = "encrypt";
	
    public SansitiveEncryptDialect() {
        super(SansitiveEncryptDialect.class.getName(), PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }
 
	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		LinkedHashSet<IProcessor> processors = new LinkedHashSet<IProcessor>();
		processors.add(new EmailSansitiveEncryptProcessor(dialectPrefix, "email", 1001));
		processors.add(new MobileSansitiveEncryptProcessor(dialectPrefix, "mobile", 1002));
		return processors;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
