package com.qxs.generator.web.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.common.collect.Lists;
import com.qxs.base.util.ApplicationContextUtil;
import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.repository.user.IUserRepository;
import com.qxs.generator.web.service.user.IUserService;

/**
 * @author qixingshen
 * **/
//@RunWith(SpringRunner.class)
//@EnableAutoConfiguration
//@WebMvcTest(UserController.class)
//@AutoConfigureMockMvc
//@AutoConfigureDataJpa
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	private MockHttpSession session;
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private IUserRepository userRepository;
	@Autowired
	private IUserService userService;
	
	private String username = "code_generator@126.com";
	
	
	@Transactional
	@Before
	public void before() {
		ApplicationContextUtil.setApplicationContext(applicationContext);
		
		userRepository.deleteAll();
		
		//session
		session();
//		this.session = new MockHttpSession();
		
	}
	
	private void session() {
		this.session = new MockHttpSession();
		userRepository.deleteAll();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("user.username", Lists.newArrayList("12345@qqadmin.com"));
		params.put("user.password", Lists.newArrayList("12345@qq.com"));
		params.put("user.name", Lists.newArrayList("12345@qq.com"));
		
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
		
	}
	
	/**
	 * 新增
	 * **/
//	@Test
	@Transactional
	public void create() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("user.username", Lists.newArrayList("12345@qq.com"));
		params.put("user.password", Lists.newArrayList("12345@qq.com"));
		params.put("user.name", Lists.newArrayList("12345@qq.com"));
		
		params.put("user.userPluginList[0].name", Lists.newArrayList("entity"));
		params.put("user.userPluginList[1].name", Lists.newArrayList("dao"));
		
		String strs = null;
		try {
//			OpenEntityManagerInViewInterceptor
			strs = this.mockMvc.perform(post("/user").with(csrf()).params(params).session(session).with(user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/plain;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Assert.assertNotNull(strs);
		
		User user = userService.findById(strs);
		Assert.assertNotNull(user);
		
	}

	
	/**
	 * 修改保存
	 * **/
//	@Test
	@Transactional
	public void update() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("user.username", Lists.newArrayList("12345@qq.com"));
		params.put("user.password", Lists.newArrayList("12345@qq.com"));
		params.put("user.name", Lists.newArrayList("12345@qq.com"));
		
		params.put("user.userPluginList[0].name", Lists.newArrayList("entity"));
		params.put("user.userPluginList[1].name", Lists.newArrayList("dao"));
		
		String strs = null;
		try {
//			OpenEntityManagerInViewInterceptor
			strs = this.mockMvc.perform(post("/user").with(csrf()).params(params).session(session).with(user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/plain;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Assert.assertNotNull(strs);
		
		User user = userService.findById(strs);
		Assert.assertNotNull(user);
		
		
		params = new LinkedMultiValueMap<>();
		
		params.put("user.id", Lists.newArrayList(user.getId()));
		params.put("user.username", Lists.newArrayList("12345@qq.com"));
		params.put("user.password", Lists.newArrayList("12345@qq.com"));
		params.put("user.name", Lists.newArrayList("12345@qq.com"));
		
		params.put("user.userPluginList[0].name", Lists.newArrayList("entity"));
		params.put("user.userPluginList[1].name", Lists.newArrayList("dao"));
		
		
		try {
			strs = this.mockMvc.perform(post("/user").with(csrf()).params(params).session(session).with(user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/plain;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		user = userService.findById(strs);
		Assert.assertEquals(params.get("user.username").get(0), user.getUsername());
		Assert.assertEquals(params.get("user.name").get(0), user.getName());
	}
	
//	@Test
	@Transactional
	public void disableEnable() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("user.username", Lists.newArrayList("12345@qq.com"));
		params.put("user.password", Lists.newArrayList("12345@qq.com"));
		params.put("user.name", Lists.newArrayList("12345@qq.com"));
		
		params.put("user.userPluginList[0].name", Lists.newArrayList("entity"));
		params.put("user.userPluginList[1].name", Lists.newArrayList("dao"));
		
		String strs = null;
		try {
			strs = this.mockMvc.perform(post("/user").with(csrf()).params(params).session(session).with(user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/plain;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		params = new LinkedMultiValueMap<>();
		params.set("user.id", strs);
		
		try {
			strs = this.mockMvc.perform(post("/user/disable").with(csrf()).params(params).session(session).with(user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/plain;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		User user = userService.findById(strs);
		Assert.assertEquals(IntConstants.STATUS_DISABLE.getCode(), user.getStatus().intValue());
		
		try {
			strs = this.mockMvc.perform(post("/user/enable").with(csrf()).params(params).session(session).with(user(username)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/plain;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		user = userService.findById(strs);
		Assert.assertEquals(IntConstants.STATUS_ENABLE.getCode(), user.getStatus().intValue());
	}
	
	
	private RequestPostProcessor csrf() {
		return new CsrfRequestPostProcessor();
	}
}
