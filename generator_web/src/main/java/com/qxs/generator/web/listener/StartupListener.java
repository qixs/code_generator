package com.qxs.generator.web.listener;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.qxs.base.util.code.compiler.JavaStringCompiler;
import com.qxs.generator.web.GeneratorApplication;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.qxs.base.util.ProjectUtil;
import com.qxs.generator.web.config.datasource.SQLiteDataSource;
import com.qxs.generator.web.exception.StartupException;
import com.qxs.generator.web.service.IDatabaseUpdateService;
import com.qxs.generator.web.service.master.IMasterService;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.generator.web.service.version.IVersionService;
import com.qxs.plugin.factory.exception.PluginConfigParseException;

import jodd.io.UnicodeInputStream;

/**
 * 服务启动监听器,spring容器初始化完成后会执行该监听器
 * 
 * @author qixingshen
 **/
@Component("StartupListener")
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);

	private static final byte[] JAR_MAGIC = { 'P', 'K', 3, 4 };

	@Value("${system.version}")
	private String systemVersion;

	/**
	 * 数据库建库脚本及升级脚本目录
	 **/
	private static final String DATABASE_PATH_IS_JAR = "BOOT-INF/classes/init/database/";
	/**
	 * 数据库建库脚本及升级脚本目录
	 **/
	private static final String DATABASE_PATH_IS_NOT_JAR = "init/database/";
	/**
	 * 数据库建库脚本文件名
	 **/
	private static final String DATABASE_CREATE_FILENAME = "create.sql";
	/**
	 * 判断数据库是否存在的表的名称
	 **/
	private static final String DATABASE_EXISTS_TABLE_NAME = "version";
	/**
	 * 数据库升级脚本文件名
	 **/
	private static final String DATABASE_UPGRADE_FILENAME = "upgrade.sql";
	
	private static final String SPRING_BOOT_PROJECT_PATH_SUFFIX = "!/BOOT-INF/classes!/";
	
	private static final String EXT_PATH_PREFIX = "ext";

	private static final String LIB_PATH = "BOOT-INF/lib/";

	@Autowired
	private IMasterService masterService;
	
	@Autowired
	private IDatabaseUpdateService databaseUpdateService;
	
	@Autowired
	private IVersionService versionService;
	
	@Autowired
	private IPluginService pluginService;
	
	/**
	 * 服务启动,初始化服务环境
	 **/
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// 只有context是root application context时才执行初始化操作,防止执行2遍
		if (event.getApplicationContext().getParent() == null) {
			// 当前项目目录
			String projectPath = ProjectUtil.getProjectPath(getClass());

			LOGGER.debug("当前项目路径:[{}]", projectPath);

			//springboot项目,获取到的projectPath格式为:jar包名!/BOOT-INF/classes!/,需要去掉后续的!/BOOT-INF/classes!/
			if(projectPath.endsWith(SPRING_BOOT_PROJECT_PATH_SUFFIX)) {
				projectPath = projectPath.substring(0, projectPath.indexOf(SPRING_BOOT_PROJECT_PATH_SUFFIX));
			}

			LOGGER.debug("去掉后缀之后的projectPath:[{}]", projectPath);


			//创建或升级数据库
			initDatabase(projectPath);

			//解压所有的jar文件到当前项目目录下(只有以jar文件形式启动时)
			decompression(projectPath);
		}
	}

	/**
	 * 解压所有的jar文件到当前项目目录下(只有以jar文件形式启动时)
	 * **/
	private void decompression(String projectPath){
		// 如果当前项目是jar文件(不是目录且以.jar结尾)
		if (isJar(projectPath)) {
			LOGGER.debug("当前项目是jar文件,目录:[{}]", projectPath);

			String jarPath = projectPath;

			if(ProjectUtil.isSpringBootProject(GeneratorApplication.class)){
				LOGGER.info("当前项目是Spring boot项目，jar文件目录：[{}]", projectPath);
				projectPath = projectPath.substring(0, projectPath.lastIndexOf("/") + 1);
				LOGGER.info("当前项目是Spring boot项目，项目目录：[{}]", projectPath);
			}

			//ext地址
			String extPath = projectPath + EXT_PATH_PREFIX;
			File extFile = new File(extPath);
			if(!extFile.exists()){
				extFile.mkdirs();
			}

			JarFile jarFile = null;
			try {
				jarFile = new JarFile(jarPath);
				StringBuilder jarClassPath = new StringBuilder();
				//获取BOOT-INF/lib/下的所有文件并复制到当前项目的ext目录下
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String filePath = entry.getName();

					LOGGER.debug("当前entry：[{}]", filePath);

					//是lib下的文件
					if (!entry.isDirectory() && filePath.startsWith(LIB_PATH)) {
						InputStream inputStream = jarFile.getInputStream(entry);
						String fileName = filePath.substring(filePath.lastIndexOf("/"));
						String jarFilePath = extFile.getAbsolutePath() + fileName;
						LOGGER.debug("当前jar文件路径：[{}]", jarFilePath);

						File f = new File(jarFilePath);
						if(f.exists()){
							//直接删除文件
							if(!f.delete()){
								LOGGER.error("无法删除文件：[{}]，请核实", f.getAbsolutePath());
								System.exit(0);
							}
						}

						FileOutputStream fileOutputStream = new FileOutputStream(jarFilePath);
						IOUtils.copy(inputStream, fileOutputStream);
						fileOutputStream.flush();
						fileOutputStream.close();

						if(jarClassPath.length() > 0){
							jarClassPath.append(":");
						}

						jarClassPath.append(jarFilePath);
					}
				}

				LOGGER.debug("所有的jar文件：[{}]", jarClassPath);
				//设置便衣class文件时的jar classpath
				JavaStringCompiler.addOptions("-classpath", jarClassPath.toString());

			} catch (IOException e1) {
				LOGGER.error("文件读取失败:[{}]", projectPath, e1);
				throw new StartupException(e1);
			} finally {
				if (jarFile != null) {
					try {
						jarFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 创建或升级数据库
	 * **/
	private void initDatabase(String projectPath){
		//因为调用updateVersion方法时数据库会创建多个connection,使用信号量会造成系统卡死,不使用信号量
		SQLiteDataSource.setIgnoreSemaphore(true);

		// 如果当前项目是jar文件(不是目录且以.jar结尾)
		if (isJar(projectPath)) {

			LOGGER.debug("当前项目是jar文件,目录:[{}]", projectPath);

			JarFile jarFile = null;
			try {
				jarFile = new JarFile(projectPath);

				LOGGER.debug("当前系统版本:[{}]", systemVersion);

				// 初始化数据库
				initDatabase(jarFile);
				// 升级数据库
				upgradeDatabase(jarFile);

				//重新加载数据库中的所有插件信息(必须更新完数据库之后才能更新其他数据)
				pluginService.reloadAllPlugin();
			} catch (IOException e1) {
				LOGGER.error("文件读取失败:[{}]", projectPath, e1);
				throw new StartupException(e1);
			} finally {
				if (jarFile != null) {
					try {
						jarFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			LOGGER.debug("当前项目不是jar文件,目录:[{}]", projectPath);

			File file = new File(projectPath);

			LOGGER.debug("当前系统版本:[{}]", systemVersion);

			// 初始化数据库
			initDatabase(file);
			// 升级数据库
			upgradeDatabase(file);

			//重新加载新添加的插件(历史插件信息不动)
			pluginService.reloadAllPlugin();
		}


		//校验数据库版本号是否等于当前系统版本号
		String databaseVersion = versionService.findVersion().getVersion();
		if(!databaseVersion.equals(systemVersion)) {
			LOGGER.error("系统初始化错误,当前系统版本号:[{}],数据库版本号:[{}]", systemVersion, databaseVersion);
			System.exit(0);
		}

		SQLiteDataSource.clearIgnoreSemaphore();
	}

	/**
	 * 从jar文件中读取文件集合
	 * 
	 * @param jarFile
	 *            jar文件对象
	 * @param path
	 *            文件路径
	 * @param fileName
	 *            文件名
	 * @throws IOException
	 **/
	protected List<JarFileEntry> readFileList(JarFile jarFile, String path, String fileName) {
		Enumeration<JarEntry> entries = jarFile.entries();
		List<JarFileEntry> jarFileEntrys = new ArrayList<>();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String filePath = entry.getName();
			if (!entry.isDirectory() && filePath.startsWith(path) && filePath.endsWith(fileName)) {
				jarFileEntrys.add(new JarFileEntry(entry));
			}
		}

		LOGGER.debug("读取到的文件:[{}]", jarFileEntrys);

		return jarFileEntrys;
	}
	/**
	 * 从jar文件中读取文件集合
	 * 
	 * @param file
	 *            file文件对象
	 * @param path
	 *            文件路径
	 * @param fileName
	 *            文件名
	 * @throws IOException
	 **/
	protected List<FileEntry> readFileList(File file, String path, String fileName) {
		String[] paths = path.split("/");
		//打开要扫描的目录(path参数)
		for(String p : paths) {
			String[] fileNames = file.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.equals(p);
				}
			});
			if(fileNames == null || fileNames.length == 0) {
				throw new StartupException(String.format("未读取到文件,file:[%s] path:[%s] fileName:[%s]", file.getAbsolutePath(),path,fileName));
			}
			if(fileNames.length > 1) {
				throw new StartupException(String.format("找到多个目录,file:[%s] path:[%s]", file.getAbsolutePath(),path));
			}
			String fileAbsolutePath = file.getAbsolutePath().replaceAll("\\\\", "/");
			fileAbsolutePath = fileAbsolutePath.endsWith("/") ? fileAbsolutePath : fileAbsolutePath + "/" ;
			file = new File(fileAbsolutePath + p);
		}
		
		//扫描所有下级目录,抽取所有名字为fileName的文件
		List<FileEntry> fileEntrys = new ArrayList<>();
		scanFile(fileEntrys, file, fileName);
		
		LOGGER.debug("读取到的文件:[{}]", fileEntrys);

		return fileEntrys;
	}
	
	private void scanFile(List<FileEntry> fileEntrys,File file,String fileName) {
		if(!file.isDirectory()) {
			throw new StartupException(String.format("[%s]必须是目录", file.getAbsolutePath()));
		}
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().equals(fileName);
			}
		});
		for(File f : files) {
			if(f.isDirectory()) {
				scanFile(fileEntrys, f, fileName);
			}else {
				fileEntrys.add(new FileEntry(f));
			}
		}
	}

	/***
	 * 初始化数据库
	 * 
	 * @param jarFile
	 *            jar文件对象
	 *            当前系统版本号
	 * @return void
	 **/
	protected void initDatabase(JarFile jarFile) {
		// 判断是否存在version表,如果不存在则初始化数据库
		if (!masterService.findTableExists(DATABASE_EXISTS_TABLE_NAME)) {
			LOGGER.info("数据库不存在,初始化数据库");
			// 数据库不存在,创建数据库
			// 为了提高效率,读取能获取到的版本号最大的建库脚本生成数据库,因为数据库创建完毕后还会尝试升级数据库,所以能保证数据库版本号和当前系统版本号一致
			InputStream insertInputStream = null;
			ByteArrayInputStream byteArrayInputStream = null;
			try {
				List<JarFileEntry> jarFileEntrys = readFileList(jarFile, DATABASE_PATH_IS_JAR, DATABASE_CREATE_FILENAME);

				if (null == jarFileEntrys || jarFileEntrys.isEmpty()) {
					throw new PluginConfigParseException(String.format("未找到[%s]文件", DATABASE_CREATE_FILENAME));
				}
				
				ZipEntry zipEntry = null;
				String version = null;
				
				for(JarFileEntry jarFileEntry : jarFileEntrys) {
					if(zipEntry == null) {
						zipEntry = jarFileEntry.getEntry();
					}else {
						//认为倒数第一个斜杠(/)和倒数第二个斜杠(/)之间的是版本号
						String fileName = jarFileEntry.getEntry().getName();
						String filePath = fileName.substring(0, fileName.lastIndexOf("/"));
						String tempVersion = filePath.substring(filePath.lastIndexOf("/") + 1);
						if(tempVersion.compareTo(version) > 0) {
							zipEntry = jarFileEntry.getEntry();
						}else {
							//当前jarFileEntry版本号小于上一次的文件的版本号,不处理,加上continue,防止后面代码重新计算版本号
							continue;
						}
					}
					//认为倒数第一个斜杠(/)和倒数第二个斜杠(/)之间的是版本号
					String fileName = zipEntry.getName();
					String filePath = fileName.substring(0, fileName.lastIndexOf("/"));
					version = filePath.substring(filePath.lastIndexOf("/") + 1);
				}
				
				if (null == zipEntry) {
					throw new PluginConfigParseException(String.format("未找到[%s]文件", DATABASE_CREATE_FILENAME));
				}
				InputStream inputStream = jarFile.getInputStream(zipEntry);
				if (null == inputStream) {
					throw new PluginConfigParseException(String.format("未找到[%s]文件", DATABASE_CREATE_FILENAME));
				}
				byteArrayInputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream));

				insertInputStream = new UnicodeInputStream(byteArrayInputStream, "UTF-8");

				String createDatabaseSql = StreamUtils.copyToString(insertInputStream, Charset.forName("UTF-8"));
				
				if(StringUtils.hasLength(createDatabaseSql)) {
					LOGGER.info("建库脚本:[{}]", createDatabaseSql);
					LOGGER.info("创建数据库");
					//升级数据库
					databaseUpdateService.update(createDatabaseSql);
					LOGGER.info("数据库创建成功");
					
					//新增version信息
					versionService.updateVersion(systemVersion);
				}else {
					LOGGER.info("未读取到建库脚本");
				}
				
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				throw new StartupException(e);
			} finally {
				if (insertInputStream != null) {
					try {
						insertInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (byteArrayInputStream != null) {
					try {
						byteArrayInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			LOGGER.info("数据库初始化完毕");
		}
	}
	
	/***
	 * 初始化数据库
	 * 
	 * @param file
	 *            file文件对象
	 *            当前系统版本号
	 * @return void
	 **/
	protected void initDatabase(File file) {
		// 判断是否存在version表,如果不存在则初始化数据库
		if (!masterService.findTableExists(DATABASE_EXISTS_TABLE_NAME)) {
			LOGGER.info("数据库不存在,初始化数据库");
			// 数据库不存在,创建数据库
			// 为了提高效率,读取能获取到的版本号最大的建库脚本生成数据库,因为数据库创建完毕后还会尝试升级数据库,所以能保证数据库版本号和当前系统版本号一致
			InputStream insertInputStream = null;
			ByteArrayInputStream byteArrayInputStream = null;
			try {
				List<FileEntry> fileEntrys = readFileList(file, DATABASE_PATH_IS_NOT_JAR, DATABASE_CREATE_FILENAME);

				if (null == fileEntrys || fileEntrys.isEmpty()) {
					throw new PluginConfigParseException(String.format("未找到[%s]文件", DATABASE_CREATE_FILENAME));
				}
				
				File f = null;
				String version = null;

				for(FileEntry fileEntry : fileEntrys) {
					if(f == null) {
						f = fileEntry.getFile();
					}else {
						//认为倒数第一个斜杠(/)和倒数第二个斜杠(/)之间的是版本号
						String fileName = fileEntry.getFile().getAbsolutePath().replaceAll("\\\\", "/");
						String filePath = fileName.substring(0, fileName.lastIndexOf("/"));
						String tempVersion = filePath.substring(filePath.lastIndexOf("/") + 1);
						if(tempVersion.compareTo(version) > 0) {
							f = fileEntry.getFile();
						}else {
							//当前jarFileEntry版本号小于上一次的文件的版本号,不处理,加上continue,防止后面代码重新计算版本号
							continue;
						}
					}
					//认为倒数第一个斜杠(/)和倒数第二个斜杠(/)之间的是版本号
					String fileName = fileEntry.getFile().getAbsolutePath().replaceAll("\\\\", "/");
					String filePath = fileName.substring(0, fileName.lastIndexOf("/"));
					version = filePath.substring(filePath.lastIndexOf("/") + 1);
				}
				
				if (null == f) {
					throw new PluginConfigParseException(String.format("未找到[%s]文件", DATABASE_CREATE_FILENAME));
				}
				InputStream inputStream = new FileInputStream(f);
				
				byteArrayInputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream));

				insertInputStream = new UnicodeInputStream(byteArrayInputStream, "UTF-8");

				String createDatabaseSql = StreamUtils.copyToString(insertInputStream, Charset.forName("UTF-8"));
				
				if(StringUtils.hasLength(createDatabaseSql)) {
					LOGGER.info("建库脚本:[{}]", createDatabaseSql);
					LOGGER.info("创建数据库");
					//升级数据库
					databaseUpdateService.update(createDatabaseSql);
					LOGGER.info("数据库创建成功");
					
					//新增version信息
					versionService.updateVersion(version);
					
				}else {
					LOGGER.info("未读取到建库脚本");
				}
				
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				throw new StartupException(e);
			} finally {
				if (insertInputStream != null) {
					try {
						insertInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (byteArrayInputStream != null) {
					try {
						byteArrayInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			LOGGER.info("数据库初始化完毕");
		}
	}

	/***
	 * 升级数据库
	 * 
	 * @param jarFile
	 *            jar文件对象
	 *            当前系统版本号
	 * @return void
	 **/
	protected void upgradeDatabase(JarFile jarFile) {
		//校验数据库版本号是否等于当前系统版本号,相同不升级系统
		String databaseVersion = versionService.findVersion().getVersion();
		if(databaseVersion.equals(systemVersion)) {
			LOGGER.info("当前系统版本号和数据库版本号一致,当前系统版本号:[{}],数据库版本号:[{}]", systemVersion, databaseVersion);
			return;
		}
		//当前系统版本号小于数据库版本号认为不正常,退出系统
		if(systemVersion.compareTo(databaseVersion) < 0) {
			LOGGER.info("当前系统版本号小于数据库版本号,退出系统,当前系统版本号:[{}],数据库版本号:[{}]", systemVersion, databaseVersion);
			System.exit(0);
		}
		
		LOGGER.info("数据库存在,升级数据库");
		
		// 抽取
		InputStream insertInputStream = null;
		ByteArrayInputStream byteArrayInputStream = null;
		try {
			List<JarFileEntry> jarFileEntrys = readFileList(jarFile, DATABASE_PATH_IS_JAR, DATABASE_UPGRADE_FILENAME);

			if (null == jarFileEntrys || jarFileEntrys.isEmpty()) {
				throw new PluginConfigParseException(String.format("未找到[%s]文件", DATABASE_UPGRADE_FILENAME));
			}
			
			//对jarFileEntrys按版本号排序,抽取大于数据库版本号的更新脚本执行更新操作
			Collections.sort(jarFileEntrys,new Comparator<JarFileEntry>() {
				@Override
				public int compare(JarFileEntry o1, JarFileEntry o2) {
					//认为倒数第一个斜杠(/)和倒数第二个斜杠(/)之间的是版本号
					String fileName1 = o1.getEntry().getName();
					String filePath1 = fileName1.substring(0, fileName1.lastIndexOf("/"));
					String version1 = filePath1.substring(filePath1.lastIndexOf("/") + 1);
					
					String fileName2 = o1.getEntry().getName();
					String filePath2 = fileName2.substring(0, fileName2.lastIndexOf("/"));
					String version2 = filePath2.substring(filePath2.lastIndexOf("/") + 1);
					
					return version1.compareTo(version2);
				}
			});
			
			for(JarFileEntry jarFileEntry : jarFileEntrys) {
				String fileName = jarFileEntry.getEntry().getName();
				String filePath = fileName.substring(0, fileName.lastIndexOf("/"));
				String version = filePath.substring(filePath.lastIndexOf("/") + 1);
				if(version.compareTo(databaseVersion) > 0) {
					InputStream inputStream = jarFile.getInputStream(jarFileEntry.getEntry());
					if (null == inputStream) {
						throw new PluginConfigParseException(String.format("未找到[%s]文件", DATABASE_UPGRADE_FILENAME));
					}
					byteArrayInputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream));

					insertInputStream = new UnicodeInputStream(byteArrayInputStream, "UTF-8");

					String upgradeDatabaseSql = StreamUtils.copyToString(insertInputStream, Charset.forName("UTF-8"));
					LOGGER.info("更新脚本:[{}]", upgradeDatabaseSql);
					
					//升级数据库
					databaseUpdateService.update(upgradeDatabaseSql);
					
					LOGGER.info("[{}]更新成功",fileName);
				}
			}
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new StartupException(e);
		} finally {
			if (insertInputStream != null) {
				try {
					insertInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//更新version信息
		versionService.updateVersion(systemVersion);

		LOGGER.info("数据库升级完毕");
	}
	
	/***
	 * 升级数据库
	 * 
	 * @param file
	 *            jar文件对象
	 *            当前系统版本号
	 * @return void
	 **/
	protected void upgradeDatabase(File file) {
		//校验数据库版本号是否等于当前系统版本号,相同不升级系统
		String databaseVersion = versionService.findVersion().getVersion();
		if(databaseVersion.equals(systemVersion)) {
			LOGGER.info("当前系统版本号和数据库版本号一致,当前系统版本号:[{}],数据库版本号:[{}]", systemVersion, databaseVersion);
			return;
		}
		//当前系统版本号小于数据库版本号认为不正常,退出系统
		if(systemVersion.compareTo(databaseVersion) < 0) {
			LOGGER.info("当前系统版本号小于数据库版本号,退出系统,当前系统版本号:[{}],数据库版本号:[{}]", systemVersion, databaseVersion);
			System.exit(0);
		}
		
		LOGGER.info("数据库存在,升级数据库");
		
		// 抽取
		InputStream insertInputStream = null;
		ByteArrayInputStream byteArrayInputStream = null;
		try {
			List<FileEntry> fileEntrys = readFileList(file, DATABASE_PATH_IS_NOT_JAR, DATABASE_UPGRADE_FILENAME);

			if (null == fileEntrys || fileEntrys.isEmpty()) {
				throw new PluginConfigParseException(String.format("未找到[%s]文件", DATABASE_UPGRADE_FILENAME));
			}
			
			//对jarFileEntrys按版本号排序,抽取大于数据库版本号的更新脚本执行更新操作
			Collections.sort(fileEntrys, new Comparator<FileEntry>() {
				@Override
				public int compare(FileEntry o1, FileEntry o2) {
					//认为倒数第一个斜杠(/)和倒数第二个斜杠(/)之间的是版本号
					String fileName1 = o1.getFile().getAbsolutePath().replaceAll("\\\\", "/");
					String filePath1 = fileName1.substring(0, fileName1.lastIndexOf("/"));
					String version1 = filePath1.substring(filePath1.lastIndexOf("/") + 1);
					
					String fileName2 = o1.getFile().getAbsolutePath().replaceAll("\\\\", "/");
					String filePath2 = fileName2.substring(0, fileName2.lastIndexOf("/"));
					String version2 = filePath2.substring(filePath2.lastIndexOf("/") + 1);
					
					return version1.compareTo(version2);
				}
			});
			
			for(FileEntry fileEntry : fileEntrys) {
				String fileName = fileEntry.getFile().getAbsolutePath().replaceAll("\\\\", "/");
				String filePath = fileName.substring(0, fileName.lastIndexOf("/"));
				String version = filePath.substring(filePath.lastIndexOf("/") + 1);
				if(version.compareTo(databaseVersion) > 0) {
					InputStream inputStream = new FileInputStream(fileEntry.getFile());
					
					byteArrayInputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream));

					insertInputStream = new UnicodeInputStream(byteArrayInputStream, "UTF-8");

					String upgradeDatabaseSql = StreamUtils.copyToString(insertInputStream, Charset.forName("UTF-8"));
					LOGGER.info("更新脚本:[{}]", upgradeDatabaseSql);
					
					//升级数据库
					databaseUpdateService.update(upgradeDatabaseSql);
					
					LOGGER.info("[{}]更新成功",fileName);
				}
			}
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new StartupException(e);
		} finally {
			if (insertInputStream != null) {
				try {
					insertInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//更新version信息
		versionService.updateVersion(systemVersion);

		LOGGER.info("数据库升级完毕");
	}

	public static boolean isJar(String path) {
		LOGGER.debug("文件路径:[{}]",path);
		
		if(new File(path).isDirectory()) {
			return false;
		}
		
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			return isJar(is);
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(),e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static boolean isJar(InputStream is) {
		byte[] buffer = new byte[JAR_MAGIC.length];
		try {
			is.read(buffer, 0, JAR_MAGIC.length);
			if (Arrays.equals(buffer, JAR_MAGIC)) {
				return true;
			}
			StringBuilder sb = new StringBuilder();
			for(byte b : buffer) {
				sb.append(b + ",");
			}
			LOGGER.error("当前文件不是jar文件,文件头:[{}]",sb);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

	private static class JarFileEntry {

		private JarEntry entry;
		/**
		 * 文件路径级次
		 **/
		private int dirLevel;

		private JarFileEntry(JarEntry entry) {
			this.entry = entry;
			String filePath = entry.getName();
			this.dirLevel = filePath.substring(0, filePath.lastIndexOf("/")).split("/").length;
		}

		public JarEntry getEntry() {
			return entry;
		}

		@Override
		public String toString() {
			return String.format("JarFileEntry={entry=%s, dirLevel=%s}", entry, dirLevel);
		}

	}
	private static class FileEntry {

		private File file;
		/**
		 * 文件路径级次
		 **/
		private int dirLevel;

		private FileEntry(File file) {
			this.file = file;
			String filePath = file.getAbsolutePath().replaceAll("\\\\", "/");
			this.dirLevel = filePath.substring(0, filePath.lastIndexOf("/")).split("/").length;
		}

		public File getFile() {
			return file;
		}

		@Override
		public String toString() {
			return String.format("JarFileEntry={file=%s, dirLevel=%s}", file, dirLevel);
		}

	}
}
