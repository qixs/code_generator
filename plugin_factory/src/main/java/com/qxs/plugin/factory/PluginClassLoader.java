package com.qxs.plugin.factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import com.qxs.plugin.factory.exception.PluginLoadException;

/**
 * 插件类加载器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
public class PluginClassLoader extends ClassLoader{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginClassLoader.class);
	
	private static final byte[] JAR_MAGIC = { 'P', 'K', 3, 4 };
	
	private String path;
	
	/**
	 * jar文件名字
	 * **/
	public PluginClassLoader(String path) {
		Assert.hasLength(path,"path参数不能为空");
		
		this.path = path;
	}
	
	@SuppressWarnings("resource")
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		//判断是否是jar文件
		if(!isJar(path)) {
			throw new PluginLoadException("插件加载失败，["+path+"]文件读取失败");
		}
		
		InputStream inputStream = null;
		JarInputStream jarInputStream = null;
		JarFile jarFile = null;
		byte[] bytes = null;
		try {
			jarFile = new JarFile(path);
			
			LOGGER.debug("path:[{}]",path);
			
			String temp = name.replaceAll("\\.", "/") + ".class";
			jarInputStream = new JarInputStream(new FileInputStream(path));
			JarEntry jarEntry = jarInputStream.getNextJarEntry();
			while(jarEntry != null) {
				if(!jarEntry.isDirectory() && jarEntry.getName().equals(temp)) {
					break;
				}
				jarEntry = jarInputStream.getNextJarEntry();
			}

			LOGGER.debug("name:[{}],  jarEntry:[{}]",name,jarEntry);
			
			if(jarEntry != null) {
				inputStream = jarFile.getInputStream(jarEntry);
				bytes = StreamUtils.copyToByteArray(inputStream);
			}else {
				return super.findClass(name);
			}
			
		} catch (MalformedURLException e) {
			throw new ClassNotFoundException(e.getMessage(),e);
		} catch (IOException e) {
			throw new ClassNotFoundException(e.getMessage(),e);
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(jarInputStream != null) {
				try {
					jarInputStream.close();
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
		
		return defineClass(name, bytes, 0, bytes.length);
	}
	
	
	
	private boolean isJar(String path) {
	    InputStream is = null;
	    byte[] buffer = new byte[JAR_MAGIC.length];
	    try {
	      is = new FileInputStream(path);
	      is.read(buffer, 0, JAR_MAGIC.length);
	      if (Arrays.equals(buffer, JAR_MAGIC)) {
	    	LOGGER.debug("Found JAR: [{}]" , path);
	        return true;
	      }
	    } catch (Exception e) {
	      // Failure to read the stream means this is not a JAR
	    } finally {
	      if (is != null) {
	        try {
	          is.close();
	        } catch (Exception e) {
	          // Ignore
	        }
	      }
	    }
	    return false;
	  }
}
