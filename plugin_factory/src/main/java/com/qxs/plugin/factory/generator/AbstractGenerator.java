package com.qxs.plugin.factory.generator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.qxs.base.model.Table;
import com.qxs.plugin.factory.exception.CodeGenerateException;
import com.qxs.plugin.factory.model.PluginConfig;

import freemarker.core.InvalidReferenceException;
import freemarker.core.NonHashException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;

/**
 * 代码生成器基类
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-2-21
 * @version Revision: 1.0
 */
public abstract class AbstractGenerator implements IGenerator{

	protected static final Version FREEMARKER_VERSION = Configuration.VERSION_2_3_27;
	
	protected transient Logger logger = LoggerFactory.getLogger(getClass());
	
	/***
	 * 表名及字段名单词分隔符
	 * **/
	private static final String NAME_SPLIT_CHARS = "_";
	private static final List<String> NAME_SPLIT_CHAR_LIST = Arrays.asList(NAME_SPLIT_CHARS.split(""));
	
	/**
	 * 代码生成时间
	 * **/
	private String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
	/***
	 * 代码作者
	 * **/
	private String author;
	/**
	 * 数据源
	 * **/
	protected DataSource dataSource;
	
	/**
	 * 根据模板生成数据流
	 * 
	 * @param templateStream 模板流
	 * @param dataModel 变量
	 * @return ByteArrayOutputStream 生成之后的流
	 * **/
	protected ByteArrayOutputStream generator(InputStream templateStream,Object dataModel) throws IOException, TemplateException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] bytes = StreamUtils.copyToByteArray(templateStream);
		
		logger.debug("templateStream: {}",templateStream);
		
		ByteArrayInputStream templateByteArrayStream = new ByteArrayInputStream(bytes);
		
		Configuration cfg = new Configuration(FREEMARKER_VERSION);
		
//		UnicodeReader unicodeReader = new UnicodeReader(new UnicodeInputStream(templateByteArrayStream, "utf-8"));
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(templateByteArrayStream));
		
		Template template = new Template("", reader, cfg);
		Writer out = new OutputStreamWriter(byteArrayOutputStream, "UTF-8");
		
		try {
			template.process(dataModel, out);
		}catch(InvalidReferenceException e) {
			logger.error("代码生成失败",e);
			logger.error("代码模板:\r\n{}",new String(bytes,"UTF-8"));
			throw e;
		}catch(NonHashException e) {
			logger.error("代码生成失败",e);
			logger.error("代码模板:\r\n{}",new String(bytes,"UTF-8"));
			throw e;
		}finally {
			if(out != null) {
				out.close();
			}
			if(reader != null) {
				reader.close();
			}
//			if(out != null) {
//				unicodeReader.close();
//			}
			if(templateStream != null) {
				templateStream.close();
			}
			if(templateByteArrayStream != null) {
				templateByteArrayStream.close();
			}
		}
		
		return byteArrayOutputStream;
	}
	
	protected BeansWrapper beansWrapper() {
		BeansWrapper wrapper = new DefaultObjectWrapper(FREEMARKER_VERSION);
		wrapper.setUseCache(false);
		return wrapper;
	}

	protected TemplateModel wrap(PluginConfig[] allPluginConfigs,PluginConfig pluginConfig,Table table,String[] removePrefixs) throws TemplateModelException{
		return beansWrapper().wrap(table);
	}
	
	@Override
	public byte[] generate(PluginConfig[] allPluginConfigs,PluginConfig pluginConfig,Table table,String[] removePrefixs,InputStream templateStream) {
		ByteArrayOutputStream stream = null;
		try {
			//复制table及column,不直接使用抽取出来的表结构,防止
			Table table2 = new Table();
			BeanUtils.copyProperties(table, table2);
			
			stream = generator(templateStream, wrap(allPluginConfigs,pluginConfig,table2, removePrefixs));
		} catch (IOException e) {
			throw new CodeGenerateException(e.getMessage(), e);
		} catch (TemplateException e) {
			throw new CodeGenerateException(e.getMessage(), e);
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		byte[] bytes = stream.toByteArray();
		
		return bytes;
	}

	@Override
	public String getFileRelativePath(Table table,PluginConfig pluginConfig,String[] removePrefixs) {
		String fileRelativeDir = pluginConfig.getFileRelativeDir();
		String fileSuffix = pluginConfig.getFileSuffix();
		Assert.hasLength(fileRelativeDir,"文件目录不能为空");
		Assert.hasLength(fileSuffix,"文件后缀名不能为空");
		
		fileRelativeDir = fileRelativeDir.trim().replaceAll("\\\\", "/");
		fileSuffix = fileSuffix.trim();
		
		fileRelativeDir = fileRelativeDir.startsWith("/") ? fileRelativeDir.substring(1) : fileRelativeDir;
		fileRelativeDir = fileRelativeDir.endsWith("/") ? fileRelativeDir.substring(0, fileRelativeDir.length() - 1) : fileRelativeDir;
		
		fileSuffix = fileSuffix.startsWith(".") ? fileSuffix.substring(1) : fileSuffix;
		
		String fileName = formatName(table.getName(), removePrefixs);

		String prefix = pluginConfig.getPrefix();
		String suffix = pluginConfig.getSuffix();

		return String.format("%s/%s.%s", fileRelativeDir,formatFileName(prefix + fileName.substring(0, 1).toUpperCase() + fileName.substring(1) + suffix),fileSuffix);
	}
	
	protected String formatFileName(String fileName) {
		return fileName.substring(0, 1).toUpperCase() + fileName.substring(1);
	}
	
	/***
	 * 把数据库名称转换为驼峰命名格式
	 * 
	 * @param name 表明或字段名
	 * @param removePrefixs 需要删除的前缀
	 * 
	 * @return String 名字
	 * **/
	protected String formatName(String name,String[] removePrefixs) {
		Assert.hasLength(name,"name参数不能为空");
		
		//移除前缀
		if(removePrefixs != null && removePrefixs.length > 0) {
			List<String> removePrefixList = Arrays.asList(removePrefixs);
			//对前缀进行排序,先切割长度最长的前缀,依次到长度最小的
			Collections.sort(removePrefixList, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.length() <= o2.length() ? 1 : -1;
				}
			});
			for(String removePrefix : removePrefixList) {
				if(StringUtils.hasLength(removePrefix) && name.toLowerCase().startsWith(removePrefix.toLowerCase())) {
					name = name.substring(removePrefix.length());
					//只需要切割一次
					break;
				}
			} 
		}
		
		//如果没有分隔符则只处理第一个字符为小写即可
		boolean flag = false;
		for(String splitChar : NAME_SPLIT_CHAR_LIST) {
			if(name.indexOf(splitChar) >= 0) {
				flag = true;
				break;
			}
		}
		if(flag) {
			StringBuilder result = new StringBuilder();
	        StringTokenizer tokens = new StringTokenizer(name, NAME_SPLIT_CHARS, true);
	       
	        while(tokens.hasMoreTokens()){
	        	String token = tokens.nextToken().toLowerCase();
	        	//分隔符
	        	if(NAME_SPLIT_CHAR_LIST.contains(token)) {
	        		continue;
	        	}
	        	result.append(token.substring(0,1).toUpperCase() + token.substring(1).toLowerCase());
	        }

			return result.substring(0,1).toLowerCase() + result.substring(1);
		}
		
		//如果第一位字符之后的所有字符全都为大写则认为是一个单词,第一位之后的直接全部转换为小写,大小写结合则认为是驼峰命名方式,不处理第一位之后字符
		for(int i = 1 , length = name.length() ; i < length ; i ++) {
			char c = name.charAt(i);
			//如果c为小写字母
			if(c >= 'a' && c <= 'z') {
				return name.substring(0,1).toLowerCase() + name.substring(1);
			}
		}
		return name.toLowerCase();
	}
	
	protected Map<String,Object> beanToMap(Object bean){
		HashMap<String,Object> map = new HashMap<String,Object>();
		if(null == bean){
			return map;
		}
		Class<?> clazz = bean.getClass();
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		for(PropertyDescriptor descriptor : descriptors){
			String propertyName = descriptor.getName();
			if(!"class".equals(propertyName)){
				Method method = descriptor.getReadMethod();
				Object result;
				try {
					result = method.invoke(bean);
					if(null != result){
						map.put(propertyName, result);
					}else{
						map.put(propertyName, "");
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		
		return map;
	}
	
	@Override
	public final void setAuthor(String author) {
		this.author = author;
	}

	protected String getAuthor() {
		//如果未设置作者则认为是当前操作系统登录用户
		if(!StringUtils.hasLength(author)) {
			return System.getProperty("user.name");
		}
		return author;
	}
	
	protected String getDate() {
		return date;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	

	protected PluginConfig getPluginConfig(PluginConfig[] allPluginConfigs,String groupName, String pluginName) {
		logger.debug("allPluginConfigs:[{}]  groupName:[{}] pluginName:[{}]", allPluginConfigs, groupName,pluginName);

		for(PluginConfig pluginConfig : allPluginConfigs) {
			if(groupName.equals(pluginConfig.getGroupName()) && pluginName.equals(pluginConfig.getName())) {
				return pluginConfig;
			}
		}
		return null;
	}
}
