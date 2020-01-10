package com.qxs.generator.web.config.security.filter;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.qxs.generator.web.config.security.resources.ResourceAccessDecisionManager;
import com.qxs.generator.web.config.security.resources.ResourceFilterInvocationSecurityMetadataSource;
import com.qxs.generator.web.config.security.resources.ResourceSecurityInterceptor;
import com.qxs.generator.web.service.user.IUserService;

/**
 * @author qixingshen
 * **/
@Configuration
@EnableWebSecurity
public class WebSecurityConfigurerAdapter extends org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter{
	protected static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfigurerAdapter.class);
	
	protected static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
	
	private static final Pattern ALLOWED_METHODS = Pattern.compile("^(GET|HEAD|OPTIONS|TRACE)$");
	/**
	 * 静态资源文件
	 * **/
	@Value("${spring.security.permitUrls}")
	private String[] permitUrls;
	
	private AntPathRequestMatcher[] csrfRequestMatchers = new AntPathRequestMatcher[] {
			new AntPathRequestMatcher("/init/wizard/**")};
	
	private static final String loginPage = "/login";
	
	@Autowired
	@Qualifier("dataSource")
	protected DataSource dataSource;
	@Autowired
	private IUserService userService;
	@Autowired
	private ProjectInitFilter projectInitFilter;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private ResourceFilterInvocationSecurityMetadataSource resourceFilterInvocationSecurityMetadataSource;
	@Autowired
	private LogoutSuccessHandler logoutSuccessHandler;
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;

	@Bean
	public GenerateCodeSemaphoreFilter generateCodeSemaphoreFilter(){
		return new GenerateCodeSemaphoreFilter();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// 系统初始化Filter
		http.addFilterBefore(projectInitFilter, FilterSecurityInterceptor.class);

		// 生成代码请求限流Filter
		http.addFilterAfter(generateCodeSemaphoreFilter(), FilterSecurityInterceptor.class);

		//静态资源等不走认证
		http.authorizeRequests().antMatchers(permitUrls).permitAll().anyRequest().authenticated();
		
		//退出
		http.logout().logoutSuccessHandler(logoutSuccessHandler).logoutSuccessUrl("/logout").permitAll();
		
		//资源权限过滤器
		http.addFilterAfter(
				new ResourceSecurityInterceptor(resourceFilterInvocationSecurityMetadataSource, 
						new ResourceAccessDecisionManager(),authenticationManager, permitUrls), 
				FilterSecurityInterceptor.class);
		
		FormLoginConfigurer<HttpSecurity> formLoginConfigurer = http.formLogin();
		
		//因为springboot默认的UsernamePasswordAuthenticationFilter不支持校验验证码,
		//这里通过反射替换掉默认的UsernamePasswordAuthenticationFilter
		Field field = AbstractAuthenticationFilterConfigurer.class.getDeclaredField("authFilter");
		field.setAccessible(true);
		field.set(formLoginConfigurer, new CustomUsernamePasswordAuthenticationFilter(http.logout()));

		//登录
		http.formLogin().successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler).loginPage(loginPage).permitAll();
		
		http.rememberMe().tokenValiditySeconds(86400).tokenRepository(tokenRepository());
		
		//csrf
		http.csrf().requireCsrfProtectionMatcher(csrfRequestMatcher()).csrfTokenRepository(csrfTokenRepository());

		// 解决不允许显示在iframe的问题
		http.headers().frameOptions().sameOrigin();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
		auth.userDetailsService(userService).passwordEncoder(
				passwordEncoder());
		// 不删除凭据，以便记住用户
		auth.eraseCredentials(false);
	}
	
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new LoginFailHandler(loginPage + "?error");
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setUserDetailsService(userService);
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);

		return daoAuthenticationProvider;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean()
			throws Exception {
		return super.authenticationManagerBean();
	}
	
	protected RequestMatcher csrfRequestMatcher() {
		return new RequestMatcher() {
			@Override
			public boolean matches(HttpServletRequest request) {
				if (ALLOWED_METHODS.matcher(request.getMethod()).matches()) {
					return false;
				}

				for (AntPathRequestMatcher matcher : csrfRequestMatchers) {
					if (matcher.matches(request)) {
						return false;
					}
				}
				return true;
			}
		};
	}

	protected CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName(CSRF_HEADER_NAME);
		return repository;
	}

	@Bean
	public JdbcTokenRepositoryImpl tokenRepository() {
		JdbcTokenRepositoryImpl jtr = new CustomJdbcTokenRepositoryImpl();
		jtr.setDataSource(dataSource);
		return jtr;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new SCryptPasswordEncoder();
	}

}
