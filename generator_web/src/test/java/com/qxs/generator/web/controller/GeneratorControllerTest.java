package com.qxs.generator.web.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qxs.base.util.ApplicationContextUtil;
import com.qxs.base.util.ProjectUtil;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.repository.user.IUserRepository;
import com.qxs.plugin.factory.PluginParameterKeys;

/**
 * @author qixingshen
// * **/
//@RunWith(SpringRunner.class)
//@EnableAutoConfiguration
//@WebMvcTest(GeneratorController.class)
//@AutoConfigureMockMvc
//@AutoConfigureDataJpa
public class GeneratorControllerTest {
	
	private final Logger logger = LoggerFactory.getLogger(getClass()); 
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ApplicationContext applicationContext;
	
	private MockHttpSession session;
	
	@Autowired
	private IUserRepository userRepository;
	
	private String username = "code_generator@126.com";
	
	@Transactional
	@Before
	public void before() {
		//插件地址
		System.setProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME, ProjectUtil.getProjectPath(getClass())+"../../src/main/resources/plugins");
		
		logger.debug("插件地址:[{}]",System.getProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME));
		
		ApplicationContextUtil.setApplicationContext(applicationContext);
		
		userRepository.deleteAll();
		
		session();
	}
	
	/**
	 * 不使用ssh
	 * **/
//	@Test
	@Transactional
	public void noSsh() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("database.type", Lists.newArrayList("MySQL"));
		params.put("database.driver", Lists.newArrayList("com.mysql.jdbc.Driver"));
		params.put("database.url", Lists.newArrayList("47.104.198.224"));
		params.put("database.port", Lists.newArrayList("3306"));
		params.put("database.username", Lists.newArrayList("mysql_test2"));
		params.put("database.password", Lists.newArrayList("q123456Q!"));
		params.put("database.databaseName", Lists.newArrayList("test"));
		params.put("database.connectionUrl", Lists.newArrayList("jdbc:mysql://47.104.198.224:3306/test?characterEncoding=UTF-8"));
		
		params.put("generateParameter.pluginNames", Lists.newArrayList(getPluginNames()));
		
		String zipFileRandomPath = zipFileRandomPath();
		
		logger.debug("生成的代码文件地址:[{}]",zipFileRandomPath);
		
		OutputStream outputStream = new FileOutputStream(zipFileRandomPath);
		byte[] bytes = this.mockMvc.perform(post("/generate").with(csrf()).params(params).session(session).with(user(username)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
			.andReturn().getResponse().getContentAsByteArray();
		outputStream.write(bytes);
		outputStream.flush();
		outputStream.close();
		
		//扫描zip文件内容
		scanZipFile(zipFileRandomPath);

		System.gc();
		new File(zipFileRandomPath).delete();
		System.gc();
	}
	/**
	 * 使用ssh
	 * **/
//	@Test
	@Transactional
	public void withSsh() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("database.type", Lists.newArrayList("MySQL"));
		params.put("database.driver", Lists.newArrayList("com.mysql.jdbc.Driver"));
		params.put("database.url", Lists.newArrayList("localhost"));
		params.put("database.port", Lists.newArrayList("3306"));
		params.put("database.username", Lists.newArrayList("mysql_test"));
		params.put("database.password", Lists.newArrayList("q123456Q!"));
		params.put("database.databaseName", Lists.newArrayList("test"));
		params.put("database.connectionUrl", Lists.newArrayList("jdbc:mysql://localhost:3306/test?characterEncoding=UTF-8"));
		
		params.put("ssh.host", Lists.newArrayList("47.104.198.224"));
		params.put("ssh.port", Lists.newArrayList("22"));
		params.put("ssh.username", Lists.newArrayList("mysql_test"));
		params.put("ssh.password", Lists.newArrayList("q123456Q!"));
		
		params.put("generateParameter.pluginNames", Lists.newArrayList(getPluginNames()));
		
		String zipFileRandomPath = zipFileRandomPath();
		
		logger.debug("生成的代码文件地址:[{}]",zipFileRandomPath);
		
		OutputStream outputStream = new FileOutputStream(zipFileRandomPath);
		byte[] bytes = this.mockMvc.perform(post("/generate").with(csrf()).params(params).session(session).with(user(username)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
			.andReturn().getResponse().getContentAsByteArray();
		outputStream.write(bytes);
		outputStream.flush();
		outputStream.close();
		
		//扫描zip文件内容
		scanZipFile(zipFileRandomPath);

		System.gc();
		new File(zipFileRandomPath).delete();
		System.gc();
	}
	
	/**
	 * 指定表名
	 * **/
//	@Test
	@Transactional
	public void withTableNames() throws Exception {
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("database.type", Lists.newArrayList("MySQL"));
		params.put("database.driver", Lists.newArrayList("com.mysql.jdbc.Driver"));
		params.put("database.url", Lists.newArrayList("47.104.198.224"));
		params.put("database.port", Lists.newArrayList("3306"));
		params.put("database.username", Lists.newArrayList("mysql_test2"));
		params.put("database.password", Lists.newArrayList("q123456Q!"));
		params.put("database.databaseName", Lists.newArrayList("test"));
		params.put("database.connectionUrl", Lists.newArrayList("jdbc:mysql://47.104.198.224:3306/test?characterEncoding=UTF-8"));
		
		params.put("generateParameter.tableNames", Lists.newArrayList("t_t2_test,t_data"));
		
		params.put("generateParameter.pluginNames", Lists.newArrayList(getPluginNames()));
		
		String zipFileRandomPath = zipFileRandomPath();
		
		logger.debug("生成的代码文件地址:[{}]",zipFileRandomPath);
		
		OutputStream outputStream = new FileOutputStream(zipFileRandomPath);
		byte[] bytes = this.mockMvc.perform(post("/generate").with(csrf()).params(params).session(session).with(user(username)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
			.andReturn().getResponse().getContentAsByteArray();
		outputStream.write(bytes);
		outputStream.flush();
		outputStream.close();

		//扫描zip文件内容
		scanZipFile(zipFileRandomPath);

		System.gc();
		new File(zipFileRandomPath).delete();
		System.gc();
	}
	
	/**
	 * 指定切割前缀
	 * **/
//	@Test
	@Transactional
	public void withPrefix() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("database.type", Lists.newArrayList("MySQL"));
		params.put("database.driver", Lists.newArrayList("com.mysql.jdbc.Driver"));
		params.put("database.url", Lists.newArrayList("47.104.198.224"));
		params.put("database.port", Lists.newArrayList("3306"));
		params.put("database.username", Lists.newArrayList("mysql_test2"));
		params.put("database.password", Lists.newArrayList("q123456Q!"));
		params.put("database.databaseName", Lists.newArrayList("test"));
		params.put("database.connectionUrl", Lists.newArrayList("jdbc:mysql://47.104.198.224:3306/test?characterEncoding=UTF-8"));
		
		params.put("generateParameter.removePrefixs", Lists.newArrayList("t_t2_,t_"));
		
		params.put("generateParameter.pluginNames", Lists.newArrayList(getPluginNames()));
		
		String zipFileRandomPath = zipFileRandomPath();
		
		logger.debug("生成的代码文件地址:[{}]",zipFileRandomPath);
		
		OutputStream outputStream = new FileOutputStream(zipFileRandomPath);
		byte[] bytes = this.mockMvc.perform(post("/generate").with(csrf()).params(params).session(session).with(user(username)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
			.andReturn().getResponse().getContentAsByteArray();
		outputStream.write(bytes);
		outputStream.flush();
		outputStream.close();

		//扫描zip文件内容
		scanZipFile(zipFileRandomPath);

		System.gc();
		new File(zipFileRandomPath).delete();
		System.gc();
	}
	
	/**
	 * 指定切割前缀
	 * **/
//	@Test
	@Transactional
	public void withPluginName() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("database.type", Lists.newArrayList("MySQL"));
		params.put("database.driver", Lists.newArrayList("com.mysql.jdbc.Driver"));
		params.put("database.url", Lists.newArrayList("47.104.198.224"));
		params.put("database.port", Lists.newArrayList("3306"));
		params.put("database.username", Lists.newArrayList("mysql_test2"));
		params.put("database.password", Lists.newArrayList("q123456Q!"));
		params.put("database.databaseName", Lists.newArrayList("test"));
		params.put("database.connectionUrl", Lists.newArrayList("jdbc:mysql://47.104.198.224:3306/test?characterEncoding=UTF-8"));
		
		params.put("generateParameter.pluginNames", Lists.newArrayList("dao,entity"));
		
		String zipFileRandomPath = zipFileRandomPath();
		
		logger.debug("生成的代码文件地址:[{}]",zipFileRandomPath);
		
		OutputStream outputStream = new FileOutputStream(zipFileRandomPath);
		byte[] bytes = this.mockMvc.perform(post("/generate").with(csrf()).params(params).session(session).with(user(username)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
			.andReturn().getResponse().getContentAsByteArray();
		outputStream.write(bytes);
		outputStream.flush();
		outputStream.close();

		//扫描zip文件内容
		scanZipFile(zipFileRandomPath);
		
		System.gc();
		new File(zipFileRandomPath).delete();
		System.gc();
	}
	
	@SuppressWarnings("serial")
	private String getPluginNames() {
		String strs = null;
		try {
			strs = this.mockMvc.perform(get("/plugin/findPluginList").with(csrf()).session(session).with(user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {  
            
          @Override  
          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
              return fieldAttributes.getDeclaredType().equals(Class.class) || fieldAttributes.getDeclaredType().equals(ClassLoader.class);  
          }
          @Override  
          public boolean shouldSkipClass(Class<?> clazz) {
        	  return clazz.equals(Class.class);
          }  
		}).create();  
		
		List<Plugin> plugins = gson.fromJson(strs, new TypeToken<List<Plugin>>() {}.getType());
		
		StringBuilder sb = new StringBuilder();
		for(Plugin plugin : plugins) {
			if(sb.length() > 0) {
				sb.append(",");
			}
			sb.append(plugin.getName());
		}
		return sb.toString();
	}
	
	private RequestPostProcessor csrf() {
		return new CsrfRequestPostProcessor();
	}
	
	/**
	 * 扫描zip文件目录
	 * **/
	private void scanZipFile(String path) {
		logger.debug("扫描文件:[{}]",path);
		File file = new File(path);
		
		logger.debug("文件大小:[{}]K",file.length() / 1024.0);
		
		try {
			ZipFile zip = new ZipFile(file);
			for (Enumeration<ZipEntry> entries = zip.getEntries(); entries
					.hasMoreElements();) {
				ZipEntry entry = entries.nextElement();
				String zipEntryName = entry.getName();
				logger.debug("代码文件zip包地址:[{}], 文件相对路径:[/{}]",path,zipEntryName);
			}
			if(zip != null){
				zip.close();
			}
		} catch (IOException e) {
			logger.debug("读取压缩文件失败", e);
		}
	}
	
	private String zipFileRandomPath() {
		//获取当前用户工作目录
		String projectPath = ProjectUtil.getProjectPath(getClass());
		String randomFilePath = 
				String.format("%s%s/%s/", projectPath,new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S").format(new Date()),UUID.randomUUID().toString());
		
		new File(randomFilePath).mkdirs();
		
		return randomFilePath + UUID.randomUUID().toString() + ".zip";
	}

	public void session() {
		this.session = new MockHttpSession();
		userRepository.deleteAll();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("user.username", Lists.newArrayList("12345@qqadmin1.com"));
		params.put("user.password", Lists.newArrayList("12345@qqadmin1.com"));
		params.put("user.name", Lists.newArrayList("12345@qqadmin1.com"));
		
		String strs = null;
		try {
			strs = this.mockMvc.perform(post("/user/admin").with(csrf()).params(params).with(user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Assert.assertNotNull(strs);
		
		params = new LinkedMultiValueMap<>();
		params.put("user.username", Lists.newArrayList("234@admin.com"));
		params.put("user.password", Lists.newArrayList("234@admin.com"));
		params.put("user.name", Lists.newArrayList("234@admin.com"));
		
		params.put("user.userPluginList[0].name", Lists.newArrayList("entity"));
		params.put("user.userPluginList[1].name", Lists.newArrayList("dao"));
		params.put("user.userPluginList[2].name", Lists.newArrayList("mapper"));
		params.put("user.userPluginList[3].name", Lists.newArrayList("serviceImpl"));
		params.put("user.userPluginList[4].name", Lists.newArrayList("serviceInterface"));
		
		try {
			strs = this.mockMvc.perform(post("/user").with(csrf()).params(params).session(session).with(user(username).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Assert.assertNotNull(strs);
		
	}
	
	
}
