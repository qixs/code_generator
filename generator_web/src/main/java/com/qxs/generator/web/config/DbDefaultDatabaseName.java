package com.qxs.generator.web.config;

import org.springframework.util.StringUtils;

import com.qxs.base.database.config.DatabaseTypeConfig;
import com.qxs.base.database.config.DatabaseTypeConfig.DatabaseType;
import com.qxs.generator.web.model.connection.Database;


public final class DbDefaultDatabaseName {
	
	public static String getDefaultName(Database database) {
		DatabaseType databaseType = DatabaseTypeConfig.getDatabaseType(database.getType());
		String defaultDatabaseName = databaseType.getDefaultDatabaseName();
		if(StringUtils.hasLength(database.getUsername())) {
			defaultDatabaseName = defaultDatabaseName.replace("${username}", database.getUsername());
		}
		return defaultDatabaseName;		
	}
}
