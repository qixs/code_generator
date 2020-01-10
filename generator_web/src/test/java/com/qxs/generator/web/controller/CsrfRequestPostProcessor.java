package com.qxs.generator.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.support.WebTestUtils;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.web.servlet.request.RequestPostProcessor;


/**
 * @author qixingshen
 * **/
public class CsrfRequestPostProcessor implements RequestPostProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CsrfRequestPostProcessor.class);

    @Override
    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        CsrfTokenRepository repository = WebTestUtils.getCsrfTokenRepository(request);
        CsrfToken token = repository.generateToken(request);
        repository.saveToken(token, request, new MockHttpServletResponse());
        
        request.setParameter(token.getParameterName(), token.getToken());
        request.addHeader(token.getHeaderName(), token.getToken());
       
        LOGGER.debug("token:[{}]",token.getToken());
        return request;
    }

}