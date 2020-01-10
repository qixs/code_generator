package com.qxs.base.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 表信息
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-22
 * @version Revision: 1.0
 */
public class Table implements Serializable {
	
	private static final long serialVersionUID = 9039211750146624772L;
	
	/**
	 * 表名
	 * **/
	private String name;
	/**
	 * 表注释
	 * **/
	private String comment;
	
	/**
	 * 是否是视图  是:true 否:false
	 * **/
	private boolean view;
	
	/**
	 * 列信息
	 * **/
	private List<Column> columns;
	
	/**
	 * 主键列
	 * **/
	private Column primaryKeyColumn;
	
	public Table() {}
	/**
	 * @param name 表名
	 * @param comment 表注释
	 * @param view 是否是视图  是:true 否:false
	 * @param columns 列信息
	 * **/
	public Table(String name,String comment,boolean view,List<Column> columns,Column primaryKeyColumn) {
		this.name = name;
		this.comment = comment;
		this.view = view;
		this.columns = columns;
		this.primaryKeyColumn = primaryKeyColumn;
	}
	
	/**
	 * 表名
	 * **/
	public String getName() {
		return name;
	}
	/**
	 * 表名
	 * **/
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 表注释
	 * **/
	public String getComment() {
		return comment;
	}
	/**
	 * 表注释
	 * **/
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * 是否是视图  是:true 否:false
	 * **/
	public boolean getView() {
		return view;
	}
	/**
	 * 是否是视图  是:true 否:false
	 * **/
	public void setView(boolean view) {
		this.view = view;
	}
	/**
	 * 是否是视图  是:true 否:false
	 * **/
	public void setView(String view) {
		this.view = Boolean.valueOf(view);
	}
	/**
	 * 列信息
	 * **/
	public List<Column> getColumns() {
		return columns;
	}
	/**
	 * 列信息
	 * **/
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	/**
	 * 列信息
	 * **/
	public void addColumn(Column column) {
		if(this.columns == null) {
			this.columns = new ArrayList<Column>();
		}
		this.columns.add(column);
	}
	/**
	 * 主键列
	 * **/
	public Column getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	/**
	 * 主键列
	 * **/
	public void setPrimaryKeyColumn(Column primaryKeyColumn) {
		this.primaryKeyColumn = primaryKeyColumn;
	}
	@Override
	public String toString() {
		return String.format("Table={name = %s , comment = %s , view = %s , columns = %s , primaryKeyColumn = {%s}}",
				name,comment,view,columns,primaryKeyColumn);
	}
}
