package com.qxs.database.model;

public class Database {

	private String database;

	private String databaseName;

	private String databaseDesc;

	public Database() {
	}

	public Database(String database, String databaseName, String databaseDesc) {
		this.database = database;
		this.databaseName = databaseName;
		this.databaseDesc = databaseDesc;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseDesc() {
		return databaseDesc;
	}

	public void setDatabaseDesc(String databaseDesc) {
		this.databaseDesc = databaseDesc;
	}

	@Override
	public String toString() {
		return "Database [database=" + database + ", databaseName=" + databaseName + ", databaseDesc=" + databaseDesc
				+ "]";
	}

}
