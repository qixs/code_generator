package com.qxs.plugin.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import com.qxs.base.util.ProjectUtil;
import com.qxs.base.util.code.compiler.JavaStringCompiler;
import com.qxs.plugin.factory.exception.PluginLoadException;
import com.qxs.plugin.factory.generator.IGenerator;
import com.qxs.plugin.factory.model.PluginConfig;
import com.qxs.plugin.factory.parser.PluginConfigParser;

/**
 * 插件加载器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
@Component
public class PluginLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);

	private static final byte[] JAR_MAGIC = { 'P', 'K', 3, 4 };

	private static volatile List<PluginConfig> PLUGIN_LIST = new ArrayList<PluginConfig>();
	private static volatile Map<String, PluginConfig> PLUGIN_MAP = new HashMap<String, PluginConfig>();

	/**
	 * 加载所有的插件
	 * 
	 * @param dir
	 *            插件所在目录
	 * @return void
	 **/
	public void loadAllPlugin(String dir) {
		Assert.hasLength(dir, "dir参数不能为空");

		LOGGER.debug("插件目录:[{}]", dir);

		File file = new File(dir);

		if(!file.exists()){
			file.mkdirs();
		}
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("“" + dir + "”参数必须是目录或目录不存在");
		}

		//加载jar包中的所有插件
		loadJarPlugins(file);

		//加载插件目录下的所有插件,会覆盖jar包中的插件
		loadPluginDirPlugins(file);
	}

	/**
	 * 解压jar包中的所有插件
	 * **/
	private void loadJarPlugins(File file){
		String projectPath = ProjectUtil.getProjectPath(PluginLoader.class);

		String springboot = ".jar!/BOOT-INF/";

		//读取.jar!/BOOT-INF，如果无.jar!/BOOT-INF则认为不是Springboot项目，无需读取jar包内的插件
		if(projectPath.indexOf(springboot) >= 0){
			LOGGER.debug("当前项目是Spring boot项目，加载jar包中的插件：[{}]", projectPath);

			String jarPath = projectPath.substring(0, projectPath.indexOf(springboot) + 4);

			LOGGER.debug("jar包路径：[{}]", jarPath);

			String pluginPath = file.getAbsolutePath();
			pluginPath = pluginPath.endsWith("/") ? pluginPath : pluginPath + "/";

			try(
					FileInputStream fileInputStream = new FileInputStream(jarPath);
					JarInputStream jarInputStream = new JarInputStream(fileInputStream);
					JarFile jarFile = new JarFile(jarPath);
			){
				JarEntry jarEntry = jarInputStream.getNextJarEntry();
				while (jarEntry != null) {
					String entryName = jarEntry.getName();
					if(!entryName.startsWith("BOOT-INF/classes/plugins/") || !entryName.endsWith(".jar")){
						jarEntry = jarInputStream.getNextJarEntry();
						continue;
					}
					LOGGER.debug("jarEntry:[{}]", jarEntry);

					InputStream inputStream = jarFile.getInputStream(jarEntry);

					File pluginDir = new File(pluginPath + entryName.substring("BOOT-INF/classes/plugins/".length(), entryName.lastIndexOf("/") + 1));
					pluginDir.mkdirs();
					LOGGER.debug("插件目录:[{}]", pluginDir);

					//如果插件已经存在则不能覆盖
					String pluginFileName = entryName.substring(entryName.lastIndexOf("/") + 1);
					File pluginFile = new File(pluginDir.getAbsolutePath() + "/" + pluginFileName);
					if(!pluginFile.exists()){
						LOGGER.debug("插件:[{}]不存在，复制插件[{}]到[{}]", pluginFileName, pluginFileName, pluginFile.getAbsolutePath());
						FileOutputStream fileOutputStream = new FileOutputStream(pluginFile);
						StreamUtils.copy(inputStream, fileOutputStream);
						fileOutputStream.flush();
						fileOutputStream.close();
					}

					jarEntry = jarInputStream.getNextJarEntry();
				}
			}catch (FileNotFoundException e){
				throw new RuntimeException(e);
			}catch (IOException e){
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 加载插件目录下的所有插件
	 * **/
	private void loadPluginDirPlugins(File file){
		List<File> plugins = new ArrayList<>();
		//加载所有插件
		findPlugins(file, plugins);

		LOGGER.debug("加载到[{}]个插件", plugins.size());

		for (File plugin : plugins) {
			try {
				loadPlugin(plugin.getAbsolutePath());

				LOGGER.debug("插件加载成功[{}]", plugin.getAbsolutePath());
			} catch (PluginLoadException e) {
				LOGGER.info("插件加载失败,插件地址:[{}]", plugin.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * 检索所有的jar文件
	 * **/
	private void findPlugins(File file, List<File> pluginFileList){
		//当前是文件夹
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f : files){
				findPlugins(f, pluginFileList);
			}
		}else{
			if(file.getName().endsWith(".jar")){
				pluginFileList.add(file);
			}
		}
	}

	public PluginConfig loadPluginConfig(String path) {
		if (!isJar(path)) {
			throw new PluginLoadException("插件加载失败，[" + path + "]文件读取失败");
		}

		PluginConfigParser pluginConfigParser = new PluginConfigParser(path, PluginConfig.PLUGIN_FILE_NAME, true);
		PluginConfig pluginConfig = pluginConfigParser.parse();
		// 设置当前插件所在物理路径
		pluginConfig.setPluginPath(path);

		// 尝试加载插件class,加载失败则抛出异常
		
		JavaStringCompiler compiler = new JavaStringCompiler();
		
		try {
			//当前类加入map
			Map<String,byte[]> byteMapClone = new LinkedHashMap<>();
			//读取字节码
			byte[] bytes = readGeneratorContent(path, pluginConfig.getGenerator());
			byteMapClone.put(pluginConfig.getGenerator(), bytes);
			
			Class<?> generatorClass = compiler.loadClass(pluginConfig.getGenerator(), byteMapClone);

			// 检查是否是接口或抽象类
			if (generatorClass.isInterface()) {
				throw new PluginLoadException("插件加载失败，[" + pluginConfig.getGenerator() + "]不能是接口");
			}
			if (Modifier.isAbstract(generatorClass.getModifiers())) {
				throw new PluginLoadException("插件加载失败，[" + pluginConfig.getGenerator() + "]不能是抽象类");
			}

			// 检查是否是IGenerator的子类
			if (!IGenerator.class.isAssignableFrom(generatorClass)) {
				throw new PluginLoadException("插件加载失败，类[" + pluginConfig.getGenerator() + "]必须是IGenerator接口的子类");
			}

			pluginConfig.setGeneratorClass(generatorClass);

		} catch (ClassNotFoundException e) {
			throw new PluginLoadException("插件加载失败，类[" + pluginConfig.getGenerator() + "]加载失败", e);
		} catch (IOException e) {
			throw new PluginLoadException("插件加载失败，类[" + pluginConfig.getGenerator() + "]加载失败", e);
		}
		return pluginConfig;
	}

	/**
	 * 读取插件生成器二进制内容
	 **/
	private byte[] readGeneratorContent(String jarPath, String name) {
		// 判断是否是jar文件
		if (!isJar(jarPath)) {
			throw new PluginLoadException("插件加载失败，[" + jarPath + "]文件读取失败");
		}

		InputStream inputStream = null;
		JarInputStream jarInputStream = null;
		JarFile jarFile = null;
		byte[] bytes = null;
		try {
			jarFile = new JarFile(jarPath);

			LOGGER.debug("path:[{}]", jarPath);

			String temp = name.replaceAll("\\.", "/") + ".class";
			jarInputStream = new JarInputStream(new FileInputStream(jarPath));
			JarEntry jarEntry = jarInputStream.getNextJarEntry();
			while (jarEntry != null) {
				if (!jarEntry.isDirectory() && jarEntry.getName().equals(temp)) {
					break;
				}
				jarEntry = jarInputStream.getNextJarEntry();
			}

			LOGGER.debug("name:[{}],  jarEntry:[{}]", name, jarEntry);

			if (jarEntry != null) {
				inputStream = jarFile.getInputStream(jarEntry);
				bytes = StreamUtils.copyToByteArray(inputStream);
			}

		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jarInputStream != null) {
				try {
					jarInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bytes;
	}

	/**
	 * 获取插件名称，格式：组名@@@@插件名
	 * **/
	private String getPluginName(PluginConfig pluginConfig){
		return getPluginName(pluginConfig.getGroupName(), pluginConfig.getName());
	}
	/**
	 * 获取插件名称，格式：组名@@@@插件名
	 * **/
	private String getPluginName(String groupName, String pluginName){
		return String.format("%s@@@@%s", groupName, pluginName);
	}
	
	/**
	 * 加载指定插件
	 * 
	 * @param pluginConfig
	 *            插件全路径
	 * @return String 插件名
	 * @throws PluginLoadException
	 **/
	public void addPlugin(PluginConfig pluginConfig) {
		synchronized (PLUGIN_LIST) {
			// 如果插件已经存在则需要替换插件而非直接添加
			String pluginName = getPluginName(pluginConfig);
			if (PLUGIN_MAP.containsKey(pluginName)) {
				for (int i = 0, length = PLUGIN_LIST.size(); i < length; i++) {
					if (pluginName.equals(getPluginName(PLUGIN_LIST.get(i)))) {
						PLUGIN_LIST.set(i, pluginConfig);
						break;
					}
				}
			} else {
				PLUGIN_LIST.add(pluginConfig);
			}

			PLUGIN_MAP.put(pluginName, pluginConfig);

			// 对插件进行排序
			Collections.sort(PLUGIN_LIST, new Comparator<PluginConfig>() {
				@Override
				public int compare(PluginConfig o1, PluginConfig o2) {
					return getPluginName(o1).compareTo(getPluginName(o2));
				}
			});
		}
	}

	/**
	 * 加载指定插件
	 * 
	 * @param path
	 *            插件全路径
	 * @return String 插件名
	 * @throws PluginLoadException
	 **/
	public String loadPlugin(String path) {
		PluginConfig pluginConfig = loadPluginConfig(path);

		if(pluginConfig.getGroupName().indexOf("@") >= 0){
			throw new PluginLoadException("插件组名不能包含@，插件组名：" + pluginConfig.getGroupName() + "  插件路径：" + pluginConfig.getPluginPath());
		}
		if(pluginConfig.getName().indexOf("@") >= 0){
			throw new PluginLoadException("插件名不能包含@，插件组名：" + pluginConfig.getName() + "  插件路径：" + pluginConfig.getPluginPath());
		}

		addPlugin(pluginConfig);

		return pluginConfig.getName();
	}

	public static boolean isJar(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			boolean isJar =  isJar(is);
			if(!isJar){
				LOGGER.error("当前文件不是jar文件：[{}]", path);
			}
			return isJar;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
			}else{
				LOGGER.error("jar文件文件头：[{}]", Arrays.toString(JAR_MAGIC));
				LOGGER.error("当前文件文件头：[{}]", Arrays.toString(buffer));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 根据插件名称获取插件配置
	 * 
	 * @param groupName
	 *            插件组名
	 * @param pluginName
	 *            插件名称
	 * @return PluginConfig 插件配置
	 **/
	public PluginConfig getPluginConfig(String groupName, String pluginName) {
		return PLUGIN_MAP.get(getPluginName(groupName, pluginName));
	}

	/**
	 * 获取插件配置列表
	 * 
	 * @return List<PluginConfig>
	 **/
	public List<PluginConfig> getPluginConfigList() {
		return PLUGIN_LIST;
	}
}
