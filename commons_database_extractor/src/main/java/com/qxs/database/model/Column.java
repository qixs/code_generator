package com.qxs.database.model;

import java.io.Serializable;

/**
 * 列信息
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-22
 * @version Revision: 1.0
 */
public class Column implements Serializable {
	
	private static final long serialVersionUID = -2733540897702703001L;

	/**
	 * 字段名
	 * **/
	private String name;
	
	/**
	 * 字段类型(数据库类型)
	 * **/
	private String type;
	
	/**
	 * 是否自动生成  是:true 否:false
	 * **/
	private boolean autoIncrement;
	
	/**
	 * 是否允许是null  是:true 否:false
	 * **/
	private boolean nullable;
	
	/**
	 * 是否是主键  是:true 否:false
	 * **/ 
	private boolean isPrimaryKey;
	
	/**
	 * 默认值
	 * **/
	private String defaultValue;
	
	/**
	 * 注释
	 * **/
	private String comment;
	
	/**
	 * 字段名
	 * **/
	public String getName() {
		return name;
	}
	/**
	 * 字段名
	 * **/
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 字段类型
	 * **/
	public String getType() {
		return type;
	}
	/**
	 * 字段类型
	 * **/
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 是否自动生成  是:true 否:false
	 * **/
	public boolean getAutoIncrement() {
		return autoIncrement;
	}
	/**
	 * 是否自动生成  是:true 否:false
	 * **/
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
	/**
	 * 是否自动生成  是:true 否:false
	 * **/
	public void setAutoIncrement(String autoIncrement) {
		this.autoIncrement = Boolean.valueOf(autoIncrement);
	}
	/**
	 * 是否允许是null  是:true 否:false
	 * **/
	public boolean getNullable() {
		return nullable;
	}
	/**
	 * 是否允许是null  是:true 否:false
	 * **/
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	/**
	 * 是否允许是null  是:true 否:false
	 * **/
	public void setNullable(String nullable) {
		this.nullable = Boolean.valueOf(nullable);
	}
	/**
	 * 是否是主键  是:true 否:false
	 * **/ 
	public boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}
	/**
	 * 是否是主键  是:true 否:false
	 * **/ 
	public void setIsPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	/**
	 * 是否是主键  是:true 否:false
	 * **/ 
	public void setIsPrimaryKey(String isPrimaryKey) {
		this.isPrimaryKey = Boolean.valueOf(isPrimaryKey);
	}
	/**
	 * 默认值
	 * **/
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * 默认值
	 * **/
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * 注释
	 * **/
	public String getComment() {
		return comment;
	}
	/**
	 * 注释
	 * **/
	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
