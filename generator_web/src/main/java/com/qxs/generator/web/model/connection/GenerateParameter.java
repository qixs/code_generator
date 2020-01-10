package com.qxs.generator.web.model.connection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;

/**
 * 生成代码参数
 * 
 * @author qixingshen
 * **/
@Entity
@Table(name = "connection_generate_parameter")
public class GenerateParameter {
	/**
	 * id
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;
	
	/**
	 * 数据库连接主表信息
	 * **/
	private String connectionId;
	/**
	 * 要导出的表名
	 * **/
	private String tableNames;
	/**
	 * 要移除的前缀
	 * **/
	private String removePrefixs;
	/**
	 * 要导出的插件名称
	 * **/
	private String pluginNames;

	public GenerateParameter() {
		super();
	}
	public GenerateParameter(String connectionId) {
		super();
		this.connectionId = connectionId;
	}
	/**
	 * id
	 * **/
	public String getId() {
		return id;
	}
	/**
	 * id
	 * **/
	public void setId(String id) {
		this.id = id;
	}

	public String getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
	/**
	 * 要导出的表名
	 * **/
	public String getTableNames() {
		return tableNames;
	}
	/**
	 * 要导出的表名
	 * **/
	public void setTableNames(String tableNames) {
		this.tableNames = tableNames;
	}
	/**
	 * 要移除的前缀
	 * **/
	public String getRemovePrefixs() {
		return removePrefixs;
	}
	/**
	 * 要移除的前缀
	 * **/
	public void setRemovePrefixs(String removePrefixs) {
		this.removePrefixs = removePrefixs;
	}
	/**
	 * 要导出的插件名称
	 * **/
	public String getPluginNames() {
		return pluginNames;
	}
	/**
	 * 要导出的插件名称
	 * **/
	public void setPluginNames(String pluginNames) {
		this.pluginNames = pluginNames;
	}
	@Override
	public String toString() {
		return "GenerateParameter [id=" + id + ", connectionId=" + connectionId + ", tableNames=" + tableNames
				+ ", removePrefixs=" + removePrefixs + ", pluginNames=" + pluginNames + "]";
	}
	
}
