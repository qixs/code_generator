package com.qxs.plugin.factory.parser;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.yaml.snakeyaml.Yaml;

import com.qxs.plugin.factory.exception.PluginConfigParseException;
import com.qxs.plugin.factory.model.PluginConfig;

import jodd.io.UnicodeInputStream;

/**
 * 配置文件解析器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
public class PluginConfigParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginConfigParser.class);

	private InputStream inputStream;

	public PluginConfigParser(String path) {
		Assert.notNull(path, "path参数不能为空");

		try {
			InputStream inputStream = new FileInputStream(path);
			this.inputStream = new UnicodeInputStream(inputStream, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public PluginConfigParser(String dirPath, String fileName) {
		this(dirPath, fileName, false);
	}

	@SuppressWarnings("resource")
	public PluginConfigParser(String dirPath, String fileName, boolean isJar) {
		Assert.notNull(dirPath, "dirPath参数不能为空");
		Assert.notNull(fileName, "fileName参数不能为空");

		JarFile jarFile = null;
		InputStream inputStream = null;
		if (isJar) {
			try {
				jarFile = new JarFile(dirPath);
				ZipEntry zipEntry = jarFile.getEntry(fileName);
				if(null == zipEntry) {
					throw new PluginConfigParseException(String.format("未找到[%s]文件", fileName));
				}
				inputStream = jarFile.getInputStream(zipEntry);
				if(null == inputStream) {
					throw new PluginConfigParseException(String.format("未找到[%s]文件", fileName));
				}
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream));
				
				this.inputStream = new UnicodeInputStream(byteArrayInputStream, "UTF-8");
				
			} catch (MalformedURLException e) {
				throw new PluginConfigParseException(e);
			} catch (IOException e) {
				throw new PluginConfigParseException(e);
			} finally {
				if(inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(jarFile != null) {
					try {
						jarFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} else {
			try {
				dirPath = dirPath.replaceAll("\\\\", "/");
				inputStream = new FileInputStream((dirPath.endsWith("/") ? dirPath : dirPath + "/") + fileName);
				
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream));
				
				this.inputStream = new UnicodeInputStream(byteArrayInputStream, "UTF-8");
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if(inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
	}

	public PluginConfigParser(InputStream inputStream) {
		Assert.notNull(inputStream, "inputStream参数不能为空");

		if (inputStream instanceof UnicodeInputStream) {
			this.inputStream = inputStream;
		} else {
			this.inputStream = new UnicodeInputStream(inputStream, "UTF-8");
		}
	}

	/**
	 * 解析配置文件
	 **/
	@SuppressWarnings("unchecked")
	public PluginConfig parse() {
		Assert.notNull(inputStream, "inputStream不能为空");

		Yaml yaml = new Yaml();
		Map<String, Object> map = yaml.loadAs(inputStream, Map.class);
		if (!map.containsKey(PluginConfig.ROOT_TAG_NAME)) {
			throw new PluginConfigParseException(String.format("缺少配置项:[%s]", PluginConfig.ROOT_TAG_NAME));
		}
		Map<String, Object> pluginConfigMap = (Map<String, Object>) map.get(PluginConfig.ROOT_TAG_NAME);

		PluginConfig pluginConfig = new PluginConfig();
		Class<PluginConfig> clazz = PluginConfig.class;
		for (Map.Entry<String, Object> entry : pluginConfigMap.entrySet()) {
			Object value = entry.getValue();
			if (value != null) {
				String fieldName = entry.getKey();
				String methodName = String.format("set%s%s", fieldName.substring(0, 1).toUpperCase(),
						fieldName.substring(1));
				try {
					Method method = clazz.getDeclaredMethod(methodName, value.getClass());
					method.invoke(pluginConfig, value);
				} catch (NoSuchMethodException e) {
					LOGGER.debug("未找到方法:[{}] 参数类型:[{}]", methodName, value.getClass(), e);
				} catch (SecurityException e) {
					LOGGER.debug("未找到方法:[{}] 参数类型:[{}]", methodName, value.getClass(), e);
				} catch (IllegalAccessException e) {
					LOGGER.debug("方法调用失败:[{}] 参数类型:[{}]", methodName, value.getClass(), e);
				} catch (IllegalArgumentException e) {
					LOGGER.debug("方法调用失败:[{}] 参数类型:[{}]", methodName, value.getClass(), e);
				} catch (InvocationTargetException e) {
					LOGGER.debug("方法调用失败:[{}] 参数类型:[{}]", methodName, value.getClass(), e);
				}
			}
		}

		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pluginConfig;
	}
}
