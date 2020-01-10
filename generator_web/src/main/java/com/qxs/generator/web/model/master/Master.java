package com.qxs.generator.web.model.master;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * sqlite数据库自带的主数据表: sqlite_master
 */
@Entity
@Table(name = "sqlite_master")
public class Master {

	@Id
	private String name;
	
	private String type;

	private String tblName;

	private String rootpage;

	private String sql;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTblName() {
		return tblName;
	}

	public void setTblName(String tblName) {
		this.tblName = tblName;
	}

	public String getRootpage() {
		return rootpage;
	}

	public void setRootpage(String rootpage) {
		this.rootpage = rootpage;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {
		return String.format("Master{name = %s , type = %s , tblName = %s , rootpage = %s , sql = %s}", 
				name, type, tblName, rootpage, sql);
	}
}