package com.qxs.generator.web.config.datasource;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConnection;

public class SQLiteDataSource extends org.sqlite.SQLiteDataSource{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final int DEFAULT_SEMAPHORE_SIZE = 1;
	
	private static final Semaphore SEMAPHORE = new Semaphore(DEFAULT_SEMAPHORE_SIZE);
	
	private static final String PREFIX = "jdbc:sqlite:";
	
	/**
	 * 不走信号量标记
	 * **/
	private static ThreadLocal<Boolean> IGNORE_SEMAPHORE = new ThreadLocal<>();
	
	@Override
	public synchronized SQLiteConnection getConnection(String username, String password) throws SQLException {
		Properties p = super.getConfig().toProperties();
        if (username != null) {
            p.put("user", username);
        }
        if (password != null) {
            p.put("pass", password);
        }
        String url = super.getUrl();
        if (!isValidURL(url)) {
        	return null;
        }
         
        url = url.trim();
        
        ConnectionCloseCallback callback = null;
        
        if(IGNORE_SEMAPHORE.get() == null || !IGNORE_SEMAPHORE.get()) {
	        try {
				SEMAPHORE.acquire();
				
				logger.debug("获取数据库链接");
				
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
	        callback = new ConnectionCloseCallback() {
				@Override
				public void close() {
					logger.debug("释放数据库链接");
					SEMAPHORE.release();
				}
			};
        }
        SQLiteConnection connection = new com.qxs.generator.web.config.datasource.SQLiteConnection(url, extractAddress(url), p, callback);
        return connection;
	}
	
	public static final void setIgnoreSemaphore(Boolean flag) {
		IGNORE_SEMAPHORE.set(flag);
	}
	
	public static final void clearIgnoreSemaphore() {
		IGNORE_SEMAPHORE.remove();
	}
	
	 /**
     * Validates a URL
     * @param url
     * @return true if the URL is valid, false otherwise
     */
    public static boolean isValidURL(String url) {
        return url != null && url.toLowerCase().startsWith(PREFIX);
    }
    /**
     * Gets the location to the database from a given URL.
     * @param url The URL to extract the location from.
     * @return The location to the database.
     */
    static String extractAddress(String url) {
        // if no file name is given use a memory database
        return PREFIX.equalsIgnoreCase(url) ? ":memory:" : url.substring(PREFIX.length());
    }
}
