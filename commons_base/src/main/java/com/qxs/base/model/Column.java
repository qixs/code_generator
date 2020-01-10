package com.qxs.base.model;

import java.io.Serializable;

import com.qxs.base.formatter.jsqlparser.CustomJdbcNamedParameter;
import org.springframework.util.Assert;

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
	 * 对应的java字段名
	 * **/
	private String javaName;
	/**
	 * 对应的java字段名
	 * **/
	private CustomJdbcNamedParameter javaNameParameter;
	/**
	 * 对应的getter和setter方法名字(不带get和set前缀)
	 * **/
	private String methodName;
	/**
	 * 字段类型(数据库类型)
	 * **/
	private String jdbcType;
	
	/**
	 * mybatis的mapper文件中的jdbcType
	 * @see org.apache.ibatis.type.JdbcType
	 * **/
	private String mapperJdbcType;
	/**
	 * 字段类型(java类型)
	 * **/
	private String javaType;
	/**
	 * java类型的简短名称,如:String、Long、Short等
	 * **/
	private String javaTypeClassSimpleName;
	
	/**
	 * 是否自动生成  是:true 否:false
	 * **/
	private boolean autoIncrement;
	/**
	 * 是否可以为空 是:true 否:false
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
	
	public Column() {}
	/***
	 * @param name 字段名
	 * @param jdbcType 字段类型(数据库类型)
	 * @param javaType 字段类型(java类型)
	 * @param autoIncrement 是否自动生成  是:true 否:false
	 * @param nullable 是否可以为空 是:true 否:false
	 * @param isPrimaryKey 是否是主键  是:true 否:false
	 * @param defaultValue 默认值
	 * @param comment 注释
	 * **/
	public Column(String name,String jdbcType,String javaType,boolean autoIncrement,boolean nullable,boolean isPrimaryKey,
			String defaultValue,String comment) {
		Assert.hasLength(name,String.format("name参数不能为空,列名:[%s]", name));
		Assert.hasLength(jdbcType,String.format("jdbcType参数不能为空,列名:[%s]", name));
		Assert.notNull(javaType,String.format("javaType参数不能为空,列名:[%s]", name));
		
		this.name = name;
		this.jdbcType = jdbcType;
		this.javaType = javaType;
		this.autoIncrement = autoIncrement;
		this.nullable = nullable;
		this.isPrimaryKey = isPrimaryKey;
		this.defaultValue = defaultValue;
		this.comment = comment == null ? "" : comment;
		
		this.javaTypeClassSimpleName = javaType.indexOf(".") > 0 ? javaType.substring(javaType.lastIndexOf(".") + 1) : javaType;
	}
	
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
	 * 字段类型(数据库类型)
	 * **/
	public String getJdbcType() {
		return jdbcType;
	}
	/**
	 * 字段类型(数据库类型)
	 * **/
	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}
	/**
	 * 字段类型(java类型)
	 * **/
	public String getJavaType() {
		return javaType;
	}
	/**
	 * 字段类型(java类型)
	 * **/
	public void setJavaType(String javaType) {
		this.javaType = javaType;
		
		this.javaTypeClassSimpleName = javaType.indexOf(".") > 0 ? javaType.substring(javaType.lastIndexOf(".") + 1) : javaType;
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
		return comment == null ? "" : comment;
	}
	/**
	 * 注释
	 * **/
	public void setComment(String comment) {
		this.comment = comment == null ? "" : comment;
	}
	/**
	 * 对应的java字段名
	 * **/
	public String getJavaName() {
		return javaName;
	}
	/**
	 * 对应的java字段名
	 * **/
	public void setJavaName(String javaName) {
		this.javaName = javaName;
		CustomJdbcNamedParameter javaNameParameter = new CustomJdbcNamedParameter();
		javaNameParameter.setName("#{" + javaName + "}");
		setJavaNameParameter(javaNameParameter);
	}

	public CustomJdbcNamedParameter getJavaNameParameter() {
		return javaNameParameter;
	}

	public void setJavaNameParameter(CustomJdbcNamedParameter javaNameParameter) {
		this.javaNameParameter = javaNameParameter;
	}

	/**
	 * java类型的简短名称,如:String、Long、Short等
	 * **/
	public String getJavaTypeClassSimpleName() {
		return javaTypeClassSimpleName;
	}
	/**
	 * java类型的简短名称,如:String、Long、Short等
	 * **/
	public void setJavaTypeClassSimpleName(String javaTypeClassSimpleName) {
		this.javaTypeClassSimpleName = javaTypeClassSimpleName;
	}
	/**
	 * 对应的getter和setter方法名字(不带get和set前缀)
	 * **/
	public String getMethodName() {
		return methodName;
	}
	/**
	 * 对应的getter和setter方法名字(不带get和set前缀)
	 * **/
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	/**
	 * 是否可以为空 是:true 否:false
	 * **/
	public boolean isNullable() {
		return nullable;
	}
	/**
	 * mybatis的mapper文件中的jdbcType
	 * @see org.apache.ibatis.type.JdbcType
	 * **/
	public String getMapperJdbcType() {
		return mapperJdbcType;
	}
	/**
	 * mybatis的mapper文件中的jdbcType
	 * @see org.apache.ibatis.type.JdbcType
	 * **/
	public void setMapperJdbcType(String mapperJdbcType) {
		this.mapperJdbcType = mapperJdbcType;
	}
	@Override
	public String toString() {
		return String.format("Column={name = %s , javaName = %s , methodName = %s , jdbcType = %s , javaType = %s ,"
				+ " javaTypeClassSimpleName = %s , autoIncrement = %b , nullable = %b , isPrimaryKey = %b , "
				+ "defaultValue = %s , comment = %s, mapperJdbcType = %s}", name,javaName,methodName,jdbcType,javaType,javaTypeClassSimpleName,
				autoIncrement,nullable,isPrimaryKey,defaultValue,comment,mapperJdbcType);
	}
}
