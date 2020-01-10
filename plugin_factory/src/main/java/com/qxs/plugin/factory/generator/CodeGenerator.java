package com.qxs.plugin.factory.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarFile;

import javax.sql.DataSource;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import com.qxs.base.model.Table;
import com.qxs.base.util.ApplicationContextUtil;
import com.qxs.database.extractor.IExtractor;
import com.qxs.plugin.factory.PluginLoader;
import com.qxs.plugin.factory.PluginParameterKeys;
import com.qxs.plugin.factory.exception.PluginNotFoundException;
import com.qxs.plugin.factory.generator.IGenerator;
import com.qxs.plugin.factory.model.PluginConfig;

/**
 * 代码生成器工具类
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-25
 * @version Revision: 1.0
 */
public class CodeGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(CodeGenerator.class);
	
	/***
	 * 生成代码方法
	 * 
	 * @param dataSource 数据库连接信息
	 * @param targetPath 生成的代码的存放目录
	 * @param removePrefixs 需要删除的前缀
	 * @param tableNames 要生成的表名,如果为空则认为生成所有表
	 * 
	 * @return void
	 * **/
	public static void generate(DataSource dataSource, String targetPath,String[] removePrefixs, String... tableNames) {
		Assert.notNull(dataSource, "dataSource参数不能为空");
		Assert.notNull(targetPath, "targetPath参数不能为空");
		Assert.isTrue(targetPath.toLowerCase().trim().endsWith(".zip"), "targetPath后缀名必须是.zip");
		Assert.isTrue(!new File(targetPath).isDirectory(), "targetPath参数不能是目录");

		ByteArrayOutputStream byteArrayOutputStream = generateStream(dataSource, removePrefixs, tableNames);
		
		makeZipFile(targetPath, byteArrayOutputStream);
		
	}
	/***
	 * 生成代码方法(返回文件流,不直接生成zip文件)
	 * 
	 * @param dataSource 数据库连接信息
	 * @param removePrefixs 需要删除的前缀
	 * @param tableNames 要生成的表名,如果为空则认为生成所有表
	 * 
	 * @return ByteArrayOutputStream zip文件流
	 * **/
	public static ByteArrayOutputStream generateStream(DataSource dataSource, String[] removePrefixs,
			String... tableNames) {
		Assert.notNull(dataSource, "dataSource参数不能为空");
		
		//获取所有的插件名称
		ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
		PluginLoader pluginLoader = applicationContext.getBean(PluginLoader.class);
		
		pluginLoader.loadAllPlugin(System.getProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME));
		
		List<PluginConfig> pluginList = pluginLoader.getPluginConfigList();
		String[] pluginNames = new String[pluginList.size()];
		for(int i = 0 ; i < pluginList.size() ; i ++) {
			pluginNames[i] = pluginList.get(i).getName();
		}
		return generateStream(dataSource, pluginNames, removePrefixs, tableNames);
	}

	/***
	 * 生成代码方法
	 * 
	 * @param dataSource 数据库连接信息
	 * @param targetPath 生成的代码的存放目录
	 * @param pluginNames 要生成代码的插件的名称
	 * @param removePrefixs 需要删除的前缀
	 * @param tableNames 要生成的表名,如果为空则认为生成所有表
	 * 
	 * @return void
	 * **/
	public static void generate(DataSource dataSource, String targetPath, String[] pluginNames, String[] removePrefixs, 
			String... tableNames) {
		Assert.notNull(dataSource, "dataSource参数不能为空");
		Assert.notNull(targetPath, "targetPath参数不能为空");
		Assert.isTrue(targetPath.toLowerCase().trim().endsWith(".zip"), "targetPath后缀名必须是.zip");
		Assert.isTrue(!new File(targetPath).isDirectory(), "targetPath参数不能是目录");
		Assert.notEmpty(pluginNames, "插件名称不能为空");

		ByteArrayOutputStream byteArrayOutputStream = generateStream(dataSource, pluginNames, removePrefixs, tableNames);
		
		makeZipFile(targetPath, byteArrayOutputStream);
		
	}
	/***
	 * 生成代码方法(返回文件流,不直接生成zip文件)
	 * 
	 * @param dataSource 数据库连接信息
	 * @param pluginNames 要生成代码的插件的名称
	 * @param removePrefixs 需要删除的前缀
	 * @param tableNames 要生成的表名,如果为空则认为生成所有表
	 * 
	 * @return ByteArrayOutputStream zip文件流
	 * **/
	@SuppressWarnings("resource")
	public static ByteArrayOutputStream generateStream(DataSource dataSource, String[] pluginGroupNames, String[] pluginNames,
			String[] removePrefixs,String... tableNames) {
		Assert.notNull(dataSource, "dataSource参数不能为空");
		Assert.notEmpty(pluginGroupNames, "插件组名不能为空");
		Assert.notEmpty(pluginNames, "插件名称不能为空");
		Assert.isTrue(pluginGroupNames.length == pluginNames.length, "pluginGroupNames和pluginNames参数长度必须一致");
		
		//首先需要加载所有的插件
		ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
		PluginLoader pluginLoader = applicationContext.getBean(PluginLoader.class);
		
		pluginLoader.loadAllPlugin(System.getProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME));
		
		//根据插件名称抽取插件中的模板流
		InputStream[] templateStreams = new InputStream[pluginNames.length];
		PluginConfig[] pluginConfigs = new PluginConfig[pluginNames.length];
		for(int i = 0 , length = pluginNames.length ; i < length ; i ++) {
			String pluginGroupName = pluginGroupNames[i];
			String pluginName = pluginNames[i];
			PluginConfig pluginConfig = pluginLoader.getPluginConfig(pluginGroupName, pluginName);
			if(null == pluginConfig) {
				throw new PluginNotFoundException("插件["+pluginName+"]未找到");
			}
			String templatePath = pluginConfig.getTemplatePath();
			
			try {
				JarFile jarFile = new JarFile(pluginConfig.getPluginPath());
				java.util.zip.ZipEntry zipEntry = jarFile.getEntry(templatePath);
				templateStreams[i] = jarFile.getInputStream(zipEntry);
			} catch (IOException e) {
				LOGGER.error("生成代码失败",e);
			}
			pluginConfigs[i] = pluginConfig;
		}
		
		return generateStream(dataSource, pluginConfigs, templateStreams, removePrefixs, tableNames);
	}
	
	/***
	 * 生成代码方法
	 * 
	 * @param dataSource 数据库连接信息
	 * @param targetPath 生成的代码的存放目录
	 * @param pluginConfigs 要生成代码的插件配置信息
	 * @param templateStreams 要生成代码的模板流
	 * @param removePrefixs 需要删除的前缀
	 * @param tableNames 要生成的表名,如果为空则认为生成所有表
	 * 
	 * @return void
	 * **/
	public static void generate(DataSource dataSource, String targetPath, PluginConfig[] pluginConfigs,
			InputStream[] templateStreams,String[] removePrefixs, String... tableNames) {
		Assert.notNull(dataSource, "dataSource参数不能为空");
		Assert.notNull(targetPath, "targetPath参数不能为空");
		Assert.isTrue(targetPath.toLowerCase().trim().endsWith(".zip"), "targetPath后缀名必须是.zip");
		Assert.isTrue(!new File(targetPath).isDirectory(), "targetPath参数不能是目录");
		Assert.notEmpty(pluginConfigs, "插件配置不能为空");
		Assert.notEmpty(templateStreams, "模板文件流不能为空");
		Assert.isTrue(pluginConfigs.length == templateStreams.length, "pluginConfigs参数长度和templateStreams参数长度不一致");

		ByteArrayOutputStream byteArrayOutputStream = generateStream(dataSource, pluginConfigs,templateStreams,
				removePrefixs, tableNames);
		
		makeZipFile(targetPath, byteArrayOutputStream);
		
	}
	/***
	 * 生成代码方法(返回文件流,不直接生成zip文件)
	 * 
	 * @param dataSource 数据库连接信息
	 * @param pluginConfigs 要生成代码的插件的配置信息
	 * @param templateStreams 要生成代码的模板流
	 * @param removePrefixs 需要删除的前缀
	 * @param tableNames 要生成的表名,如果为空则认为生成所有表
	 * 
	 * @return ByteArrayOutputStream zip文件流
	 * **/
	@SuppressWarnings("resource")
	public static ByteArrayOutputStream generateStream(DataSource dataSource, PluginConfig[] pluginConfigs,
			InputStream[] templateStreams,String[] removePrefixs,String... tableNames) {
		Assert.notNull(dataSource, "dataSource参数不能为空");
		Assert.notEmpty(pluginConfigs, "插件配置不能为空");
		Assert.notEmpty(templateStreams, "模板文件流不能为空");
		Assert.isTrue(pluginConfigs.length == templateStreams.length, "pluginConfigs参数长度和templateStreams参数长度不一致");
		
		//首先需要加载所有的插件
		ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
		PluginLoader pluginLoader = applicationContext.getBean(PluginLoader.class);
		
		pluginLoader.loadAllPlugin(System.getProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME));
		
		//抽取所有表结构
		IExtractor extractor = applicationContext.getBean(IExtractor.class);
		List<Table> tables = extractor.extractorTables(dataSource, tableNames);
		
		Assert.notEmpty(tables,"未获取到表信息");
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		ZipOutputStream zipStream = new ZipOutputStream(byteArrayOutputStream);
		
		//模板信息
		byte[][] templateBytes = new byte[templateStreams.length][];
		for(int i = 0 , length = templateStreams.length ; i < length ; i ++) {
			try {
				templateBytes[i] = StreamUtils.copyToByteArray(templateStreams[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//循环插件以及表名,生成代码文件
		for(int i = 0 , length = pluginConfigs.length ; i < length ; i ++) {
			PluginConfig pluginConfig = pluginConfigs[i];
			IGenerator generator = (IGenerator) applicationContext.getBean(pluginConfig.getGeneratorClass());
			generator.setDataSource(dataSource);
			//生成代码
			for(Table table : tables) {
				byte[] bytes = generator.generate(pluginConfigs,pluginConfig,table,removePrefixs,new ByteArrayInputStream(templateBytes[i]));
				String fileRelativePath = generator.getFileRelativePath(table,pluginConfig,removePrefixs);
				
				ZipEntry ze = new ZipEntry(fileRelativePath);
				try {
					zipStream.putNextEntry(ze);
					zipStream.write(bytes);
				} catch (IOException e) {
					LOGGER.error(e.getMessage(),e);
				}
			}
		}
		
		try {
			zipStream.flush();
			zipStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream;
	}
	
	/**
	 * 把流转换成zip文件
	 * 
	 * @param targetPath zip文件目录(包括文件名)
	 * @param byteArrayOutputStream 数据流
	 * 
	 * @return void
	 * **/
	private static void makeZipFile(String targetPath, ByteArrayOutputStream byteArrayOutputStream) {
		// 如果目录不存在则会new FileOutputStream()会报错,先创建目录
		String path = targetPath.replaceAll("\\\\", "/");
		String dir = path.substring(0, path.lastIndexOf("/"));

		new File(dir).mkdirs();

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(targetPath);
			outputStream.write(byteArrayOutputStream.toByteArray());
			outputStream.flush();
		} catch (FileNotFoundException e) {
			LOGGER.error("生成代码文件失败", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOGGER.error("生成代码文件失败", e);
			throw new RuntimeException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
