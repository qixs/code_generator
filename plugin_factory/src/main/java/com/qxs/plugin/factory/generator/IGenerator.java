package com.qxs.plugin.factory.generator;

import java.io.InputStream;

import javax.sql.DataSource;

import com.qxs.base.model.Table;
import com.qxs.plugin.factory.model.PluginConfig;

/**
 * 代码生成器顶级接口
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
public interface IGenerator {
	/**
	 * 获取要生成的文件在zip文件中的相对路径(如:dao/xxxDAO.java)
	 * @param removePrefixs 需要删除的前缀
	 * @param pluginConfig 插件配置信息
	 * @param table 表结构
	 * 
	 * @return String 要生成的文件在zip文件中的相对路径(如:dao/xxxDAO.java)
	 * **/
	String getFileRelativePath(Table table,PluginConfig pluginConfig,String[] removePrefixs);
	/**
	 * 生成代码
	 * @param allPluginConfigs 所有的插件列表
	 * @param pluginConfig 参数配置
	 * @param table 表结构
	 * @param removePrefixs 需要删除的前缀
	 * @param templateStream 模板流
	 * 
	 * @return byte[] 生成的代码的文件数据
	 * **/
	byte[] generate(PluginConfig[] allPluginConfigs,PluginConfig pluginConfig,Table table,String[] removePrefixs,InputStream templateStream);
	
	/**
	 * 设置代码作者
	 * 
	 * @param author 作者
	 * 
	 * @return void
	 * **/
	void setAuthor(String author);
	/**
	 * 设置dataSource
	 * @param  dataSource 要抽取的数据库的dataSource
	 * @return void
	 * **/
	void setDataSource(DataSource dataSource);
}
