package com.qxs.generator.web.config.security.filter;

import java.util.Date;

import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import com.qxs.generator.web.util.DateUtil;

public class CustomJdbcTokenRepositoryImpl extends JdbcTokenRepositoryImpl{

	private String insertTokenSql = DEF_INSERT_TOKEN_SQL;
	private String updateTokenSql = DEF_UPDATE_TOKEN_SQL;
	
	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		getJdbcTemplate().update(insertTokenSql, token.getUsername(), token.getSeries(),
				token.getTokenValue(), DateUtil.formatDate(token.getDate()));
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		getJdbcTemplate().update(updateTokenSql, tokenValue, DateUtil.formatDate(lastUsed), series);
	}

}
