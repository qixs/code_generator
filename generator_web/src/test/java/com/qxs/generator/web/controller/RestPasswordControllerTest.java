package com.qxs.generator.web.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.common.collect.Lists;
import com.qxs.base.util.ApplicationContextUtil;
import com.qxs.base.util.ProjectUtil;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.UserPasswordCheckCode;
import com.qxs.generator.web.repository.user.IUserPasswordCheckCodeRepository;
import com.qxs.generator.web.repository.user.IUserRepository;
import com.qxs.generator.web.service.user.IUserService;
import com.qxs.plugin.factory.PluginParameterKeys;

//@RunWith(SpringRunner.class)
//@EnableAutoConfiguration
//@WebMvcTest(ForgetPasswordController.class)
//@AutoConfigureMockMvc
//@AutoConfigureDataJpa
public class RestPasswordControllerTest {
	
	private final Logger logger = LoggerFactory.getLogger(getClass()); 

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserPasswordCheckCodeRepository userPasswordCheckCodeRepository;
	@Autowired
	private IUserRepository userRepository;
	
	private MockHttpSession session;
	
	private String username = "code_generator@126.com";
	
	@Before
	public void before() {
		//插件地址
		System.setProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME, ProjectUtil.getProjectPath(getClass())+"../../src/main/resources/plugins");
		
		logger.debug("插件地址:[{}]",System.getProperty(PluginParameterKeys.PLUGINS_DIR_PARAMETER_NAME));
		
		ApplicationContextUtil.setApplicationContext(applicationContext);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		
		session();
		
		List<User> users = userService.findList(null);
		
		boolean flag = false;
		
		for(User user : users) {
			if(username.equals(user.getUsername())) {
				flag = true;
				break;
			}
		}
		String strs = null;
		
		if(!flag) {

			params = new LinkedMultiValueMap<>();
			params.put("user.username", Lists.newArrayList(username));
			params.put("user.password", Lists.newArrayList(username.substring(0,10)));
			params.put("user.name", Lists.newArrayList(username));
			
			params.put("user.userPluginList[0].name", Lists.newArrayList("entity"));
			params.put("user.userPluginList[1].name", Lists.newArrayList("dao"));
			
			try {
				strs = this.mockMvc.perform(post("/user").with(csrf()).params(params).session(session).with(user("123@qq.com")))
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
	}
	
//	@Test
	public void repeatSendMail() throws InterruptedException {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("username", Lists.newArrayList(username));
		
		try {
			this.mockMvc.perform(post("/forgetPassword/repeatSendMail").with(csrf()).params(params).session(session))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/html;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
//	@Test
	public void restPasswordPage() throws InterruptedException {
		repeatSendMail();
		
		User user = userService.findByUsername(username);
		UserPasswordCheckCode userPasswordCheckCode = new UserPasswordCheckCode();
		userPasswordCheckCode.setUserId(user.getId());
		String checkCode = userPasswordCheckCodeRepository.findAll(Example.of(userPasswordCheckCode),new Sort(Direction.DESC,"sendDate")).get(0).getCheckCode();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("username", Lists.newArrayList(username));
		
		try {
			this.mockMvc.perform(get("/forgetPassword/restPassword/"+username+"/"+checkCode).with(csrf()).params(params).session(session))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/html;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
//	@Test
	public void restPassword() throws InterruptedException {
		repeatSendMail();
		
		User user = userService.findByUsername(username);
		UserPasswordCheckCode userPasswordCheckCode = new UserPasswordCheckCode();
		userPasswordCheckCode.setUserId(user.getId());
		String checkCode = userPasswordCheckCodeRepository.findAll(Example.of(userPasswordCheckCode),new Sort(Direction.DESC,"sendDate")).get(0).getCheckCode();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("username", Lists.newArrayList(username));
		params.put("checkCode", Lists.newArrayList(checkCode));
		params.put("password", Lists.newArrayList("12345678"));
		
		try {
			this.mockMvc.perform(post("/forgetPassword/restPassword").with(csrf()).params(params).session(session))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/html;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private void session() {
		this.session = new MockHttpSession();
		userRepository.deleteAll();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.put("user.username", Lists.newArrayList("123@qq.com"));
		params.put("user.password", Lists.newArrayList("123@qq.com"));
		params.put("user.name", Lists.newArrayList("123@qq.com"));
		
		String strs = null;
		try {
			strs = this.mockMvc.perform(post("/user/admin").with(csrf()).params(params))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Assert.assertNotNull(strs);
		
	}
	
	private RequestPostProcessor csrf() {
		return new CsrfRequestPostProcessor();
	}
}
