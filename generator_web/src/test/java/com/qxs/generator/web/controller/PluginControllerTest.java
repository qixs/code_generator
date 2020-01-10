package com.qxs.generator.web.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
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
import com.qxs.base.util.ApplicationContextUtil;
import com.qxs.base.util.ProjectUtil;
import com.qxs.generator.web.controller.plugin.PluginController;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.plugin.factory.PluginLoader;
import com.qxs.plugin.factory.PluginParameterKeys;

/**
 * @author qixingshen
 * **/
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@WebMvcTest(PluginController.class)
@AutoConfigureMockMvc
@AutoConfigureDataJpa
public class PluginControllerTest {
	
	private final Logger logger = LoggerFactory.getLogger(getClass()); 
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private PluginLoader pluginLoader;
	
	private String username = "code_generator@126.com";
	
	@Before
	public void before() {
		//插件地址
		System.setProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME, ProjectUtil.getProjectPath(getClass())+"../../src/main/resources/plugins");
		
		logger.debug("插件地址:[{}]",System.getProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME));
		
		ApplicationContextUtil.setApplicationContext(applicationContext);
	}
	
//	@Test
	@SuppressWarnings("serial")
	public void findPluginList() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("plugin.name", Lists.newArrayList("entity"));
		params.put("sortFieldName", Lists.newArrayList("name"));
		params.put("sortType", Lists.newArrayList("asc"));
		
		String strs = null;
		try {
			strs = this.mockMvc.perform(get("/plugin/findPluginList").with(csrf()).params(params).with(user(username)))
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
		
		Assert.assertTrue(plugins.size() > 0);
	}
	
//	@Test
	public void getPluginByName() {
		String strs = null;
		try {
			strs = this.mockMvc.perform(get("/plugin/getPluginByName/entity").with(csrf()).with(user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {  
            
          @Override  
          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
              return fieldAttributes.getDeclaredType().equals(Class.class);  
          }
          @Override  
          public boolean shouldSkipClass(Class<?> clazz) {
        	  return clazz.equals(Class.class);
          }  
		}).create();  
		
		Plugin plugin = gson.fromJson(strs, Plugin.class);
		
		Assert.assertNotNull(plugin);
	}
	
	@Test
	public void disablePlugin() {
//		String pluginName = "serviceImpl";
//		String strs = null;
//		try {
//			strs = this.mockMvc.perform(get("/plugin/getPluginByName/"+pluginName).with(csrf()).with(user(username)))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andReturn().getResponse().getContentAsString();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//
//		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
//
//          @Override
//          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
//              return fieldAttributes.getDeclaredType().equals(Class.class);
//          }
//          @Override
//          public boolean shouldSkipClass(Class<?> clazz) {
//        	  return clazz.equals(Class.class);
//          }
//		}).create();
//
//		Plugin plugin = gson.fromJson(strs, Plugin.class);
//
//		Assert.assertEquals(plugin.getStatus().intValue(), IntConstants.STATUS_ENABLE.getCode());
//
//		try {
//			strs = this.mockMvc.perform(post("/plugin/disablePlugin/" + plugin.getId()).with(csrf()).with(user(username)))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andReturn().getResponse().getContentAsString();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//
//
//		try {
//			strs = this.mockMvc.perform(get("/plugin/getPluginByName/"+pluginName).with(csrf()).with(user(username)))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andReturn().getResponse().getContentAsString();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//
//		gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
//
//          @Override
//          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
//              return fieldAttributes.getDeclaredType().equals(Class.class);
//          }
//          @Override
//          public boolean shouldSkipClass(Class<?> clazz) {
//        	  return clazz.equals(Class.class);
//          }
//		}).create();
//
//		plugin = gson.fromJson(strs, Plugin.class);
//
//		Assert.assertEquals(plugin.getStatus().intValue(), IntConstants.STATUS_DISABLE.getCode());
	}
	
//	@Test
//	public void enablePlugin() {
//		String serviceImpl = "entity";
//		String strs = null;
//		try {
//			strs = this.mockMvc.perform(get("/plugin/getPluginByName/entity").with(csrf()).with(user(username)))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andReturn().getResponse().getContentAsString();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		
//		Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {  
//            
//          @Override  
//          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
//              return fieldAttributes.getDeclaredType().equals(Class.class);  
//          }
//          @Override  
//          public boolean shouldSkipClass(Class<?> clazz) {
//        	  return clazz.equals(Class.class);
//          }  
//		}).create();  
//		
//		Plugin plugin = gson.fromJson(strs, Plugin.class);
//		
//		Assert.assertEquals(plugin.getStatus().intValue(), Constants.STATUS_DISABLE.getCode());
//		
//		try {
//			strs = this.mockMvc.perform(post("/plugin/enablePlugin/" + plugin.getId()).with(csrf()).with(user(username)))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andReturn().getResponse().getContentAsString();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		
//		
//		try {
//			strs = this.mockMvc.perform(get("/plugin/getPluginByName/entity").with(csrf()).with(user(username)))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//				.andReturn().getResponse().getContentAsString();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		
//		gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {  
//            
//          @Override  
//          public boolean shouldSkipField(FieldAttributes fieldAttributes) {
//              return fieldAttributes.getDeclaredType().equals(Class.class);  
//          }
//          @Override  
//          public boolean shouldSkipClass(Class<?> clazz) {
//        	  return clazz.equals(Class.class);
//          }  
//		}).create();  
//		
//		plugin = gson.fromJson(strs, Plugin.class);
//		
//		Assert.assertEquals(plugin.getStatus().intValue(), Constants.STATUS_ENABLE.getCode());
//	}
	
//	@Test
	public void uploadPlugin() {
		pluginLoader.loadAllPlugin(ProjectUtil.getProjectPath(getClass())+"../../src/main/resources/plugins");
		
		String strs = null;
		FileInputStream fis = null;
		try {
			String pluginPath = ProjectUtil.getProjectPath(getClass())+"../../src/main/resources/plugins/generator_plugin_entity-1.0.jar";
			
			fis = new FileInputStream(pluginPath);
		    MockMultipartFile multipartFile = new MockMultipartFile("file", new File(pluginPath).getName(), null, fis);
		    
			strs = this.mockMvc.perform(multipart("/plugin/uploadPlugin").file(multipartFile).with(csrf()).with(user(username)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Assert.assertNotNull(strs);
		Assert.assertTrue(Boolean.valueOf(strs));
	}
	
	
	private RequestPostProcessor csrf() {
		return new CsrfRequestPostProcessor();
	}
}
