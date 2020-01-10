package com.qxs.generator.web.config.datasource;

import java.sql.SQLException;
import java.util.Properties;

import org.sqlite.jdbc4.JDBC4Connection;

public class SQLiteConnection extends JDBC4Connection{
	
	private ConnectionCloseCallback callback;
	
	public SQLiteConnection(String url, String fileName, Properties prop, ConnectionCloseCallback callback) throws SQLException {
		super(url, fileName, prop);
		this.callback = callback;
	}

	@Override
	public void close() throws SQLException {
		super.close();
		
		if(callback != null) {
			callback.close();
		}
	}
	
}
