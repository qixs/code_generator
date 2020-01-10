package com.qxs.generator.web.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qxs.base.model.Table;
import com.qxs.base.util.ApplicationContextUtil;

/**
 * @author qixingshen
 * **/
//@RunWith(SpringRunner.class)
//@EnableAutoConfiguration
//@WebMvcTest(TableController.class)
//@AutoConfigureMockMvc
//@AutoConfigureDataJpa
public class TableControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ApplicationContext applicationContext;
	
	private String username = "code_generator@126.com";
	
	@Before
	public void before() {
		ApplicationContextUtil.setApplicationContext(applicationContext);
	}
	
//	@Test
	@SuppressWarnings("serial")
	public void getTableListNoSsh() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("database.type", Lists.newArrayList("MySQL"));
		params.put("database.driver", Lists.newArrayList("com.mysql.jdbc.Driver"));
		params.put("database.url", Lists.newArrayList("47.104.198.224"));
		params.put("database.port", Lists.newArrayList("3306"));
		params.put("database.username", Lists.newArrayList("mysql_test2"));
		params.put("database.password", Lists.newArrayList("q123456Q!"));
		params.put("database.databaseName", Lists.newArrayList("test"));
		params.put("database.connectionUrl", Lists.newArrayList("jdbc:mysql://47.104.198.224:3306/test?characterEncoding=UTF-8"));
		
		
		String strs = null;
		try {
			strs = this.mockMvc.perform(get("/table/getTableList").with(csrf()).params(params).with(user(username)))
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
		
		List<Table> tables = gson.fromJson(strs, new TypeToken<List<Table>>() {}.getType());
		
		Assert.assertTrue(tables.size() > 0);
	}
	
//	@Test
	@SuppressWarnings("serial")
	public void getTableListWithSsh() {
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
		
		String strs = null;
		try {
			strs = this.mockMvc.perform(get("/table/getTableList").with(csrf()).params(params).with(user(username)))
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
		
		List<Table> tables = gson.fromJson(strs, new TypeToken<List<Table>>() {}.getType());
		
		Assert.assertTrue(tables.size() > 0);
	}
	
	
	private RequestPostProcessor csrf() {
		return new CsrfRequestPostProcessor();
	}
}
