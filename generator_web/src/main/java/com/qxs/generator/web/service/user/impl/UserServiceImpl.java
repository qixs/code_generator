package com.qxs.generator.web.service.user.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.plugin.Plugin;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.UserActiveCheckCode;
import com.qxs.generator.web.model.user.UserAdminCaptcha;
import com.qxs.generator.web.model.user.UserPlugin;
import com.qxs.generator.web.repository.user.IUserRepository;
import com.qxs.generator.web.service.notice.mail.INoticeMailService;
import com.qxs.generator.web.service.plugin.IPluginService;
import com.qxs.generator.web.service.user.IUserActiveCheckCodeService;
import com.qxs.generator.web.service.user.IUserAdminCaptchaService;
import com.qxs.generator.web.service.user.IUserPluginService;
import com.qxs.generator.web.service.user.IUserService;
import com.qxs.generator.web.util.DateUtil;
import com.qxs.generator.web.util.RequestUtil;

@Service
public class UserServiceImpl implements IUserService {

	private transient final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private INoticeMailService noticeMailService;
	@Autowired
	private IUserAdminCaptchaService userAdminCaptchaService;
	@Autowired
	private IUserActiveCheckCodeService userActiveCheckCodeService;
	@Autowired
	private IPluginService pluginService;
	@Autowired
	private IUserPluginService userPluginService;
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return findByUsername(username);
	}

	@Transactional
	@Override
	public String insert(User user, String passwordRepeat) {
		//密码和重复密码必须一致
		if(!user.getPassword().equals(passwordRepeat)) {
			throw new BusinessException("密码和重复密码必须一致");
		}
		
		//校验用户名是否重复
		User u = new User();
		u.setUsername(user.getUsername());
		if(userRepository.count(Example.of(u)) > 0) {
			throw new BusinessException("用户名重复");
		}
		
		user.setStatus(IntConstants.STATUS_DISABLE.getCode());
		user.setAdmin(User.ADMIN_STATUS_IS_NOT_ADMIN);
		user.setCreateDate(DateUtil.currentDate());
		user.setUpdateDate(DateUtil.currentDate());
		//密码
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		user.setCreateUserId(getUserId());
		user.setUpdateUserId(user.getCreateUserId());
		
		user = userRepository.saveAndFlush(user);
		
		//如果新增用户是普通用户则默认分配所有已启用插件
		if(user.getAdmin() == User.ADMIN_STATUS_IS_NOT_ADMIN) {
			String userId = user.getId();
			Plugin p = new Plugin();
			p.setStatus(IntConstants.STATUS_ENABLE.getCode());
			//已启用的所有插件
			List<Plugin> pluginList = pluginService.findPluginList(p, null);
			if(!pluginList.isEmpty()) {
				List<UserPlugin> userPlugins = new ArrayList<>(pluginList.size());
				pluginList.stream().forEach(plugin -> {
					UserPlugin userPlugin = new UserPlugin();
					userPlugin.setUserId(userId);
					userPlugin.setSystemVersion(plugin.getSystemVersion());
					userPlugin.setCreateDate(DateUtil.currentDate());
					userPlugin.setUpdateDate(DateUtil.currentDate());
					userPlugin.setGroupName(plugin.getGroupName());
					userPlugin.setName(plugin.getName());
					userPlugin.setDescription(plugin.getDescription());
					userPlugin.setTemplatePath(plugin.getTemplatePath());
					userPlugin.setTemplateContent(plugin.getTemplateContent());
					userPlugin.setGenerator(plugin.getGenerator());
					userPlugin.setGeneratorSourceContent(plugin.getGeneratorSourceContent());
					userPlugin.setGeneratorContent(plugin.getGeneratorContent());
					userPlugin.setFileRelativeDir(plugin.getFileRelativeDir());
					userPlugin.setFileSuffix(plugin.getFileSuffix());
					userPlugin.setPrefix(plugin.getPrefix());
					userPlugin.setSuffix(plugin.getSuffix());
					userPlugin.setPluginPath(plugin.getPluginPath());
					userPlugin.setDependencies(plugin.getDependencies());
					userPlugin.setStatus(IntConstants.STATUS_ENABLE.getCode());
					userPlugin.setCustom(UserPlugin.CUSTOM_STATUS_IS_NOT_CUSTOM);
					
					userPlugins.add(userPlugin);
				});
				
				userPluginService.batchInsert(userPlugins);
			}
		}
		
		//发送用户添加成功提醒邮件
		UserActiveCheckCode userActiveCheckCode = new UserActiveCheckCode();
		userActiveCheckCode.setUserId(user.getId());
		userActiveCheckCode.setCheckCode(userActiveCheckCodeService.generateCheckCode());
		
		userActiveCheckCodeService.insert(userActiveCheckCode);
		
		HttpServletRequest request = RequestUtil.getRequest();
		//获取当前应用地址
		String serverUrl = request.getScheme() + "://" + request.getServerName() + ":" +request.getServerPort() + request.getServletPath();
		
		String activeUrl = serverUrl + String.format("/active/%s/%s", user.getUsername(), userActiveCheckCode.getCheckCode());
		noticeMailService.sendActiveAccountMail(user.getUsername(), userActiveCheckCode.getValidateMinutes(), activeUrl);
		
		return user.getId();
	}
	
	@Transactional
	@Override
	public String insertAdmin(User user,String captcha,String repeatPassword) {
		//如果已经设置管理员则不允许再设置管理员
		User adminUser = new User();
		adminUser.setAdmin(User.ADMIN_STATUS_IS_ADMIN);
		//如果是新增
		if(user.getId() == null) {
			//密码不能为空
			if(StringUtils.isEmpty(user.getPassword())) {
				return "密码不能为空";
			}
			
			if(!userRepository.findAll(Example.of(adminUser)).isEmpty()) {
				return "已经存在管理员，不允许继续新增管理员";
			}
		}
		
		//校验验证码是否正确
		UserAdminCaptcha userAdminCaptcha = new UserAdminCaptcha();
		userAdminCaptcha.setUsername(user.getUsername());
		userAdminCaptcha.setStatus(IntConstants.STATUS_ENABLE.getCode());
		
		userAdminCaptcha = userAdminCaptchaService.find(userAdminCaptcha);
		if(userAdminCaptcha == null || !userAdminCaptcha.getCaptcha().equals(captcha)) {
			return "验证码不正确";
		}

		//验证码已过期
		int validateMinutes = userAdminCaptcha.getValidateMinutes();
		long time = DateUtil.parse(userAdminCaptcha.getSendDate()).getTime();
		
		if(System.currentTimeMillis() - time > validateMinutes * 60 * 1000) {
			return "验证码已过期";
		}
		
		//使验证码失效
		userAdminCaptchaService.setInvalidCaptcha(userAdminCaptcha);
		
		if(!user.getPassword().equals(repeatPassword)) {
			return "确认密码和密码不一致";
		}
		
		user.setStatus(IntConstants.STATUS_ENABLE.getCode());
		user.setAdmin(User.ADMIN_STATUS_IS_ADMIN);
		user.setCreateDate(DateUtil.currentDate());
		user.setUpdateDate(DateUtil.currentDate());
		user.setCreateUserId("0");
		user.setUpdateUserId(user.getCreateUserId());
		
		String password = user.getPassword();
		//密码
		if(StringUtils.hasLength(password)) {
			if(password.length() < 8 || password.length() > 18) {
				return "密码长度必须在8位到18位之间";
			}
			
			user.setPassword(passwordEncoder.encode(password));
		}else {
			user.setPassword(userRepository.getOne(user.getId()).getPassword());
		}
		
		userRepository.saveAndFlush(user).getId();
		
		return "";
	}

	@Transactional
	@Override
	public String update(User user) {
		//校验用户名是否重复
		long count = userRepository.countByUsernameAndId(user.getUsername(), user.getId());
		if(count > 0) {
			throw new BusinessException("用户名重复");
		}

		//如果修改的用户是当前登录用户则需要更新session
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User u = (User) authentication.getPrincipal();

		User oldUser = findById(user.getId());
		
		user.setUpdateDate(DateUtil.currentDate());
		user.setUpdateUserId(u.getId());

		//如果当前登录用户是管理员则可修改其他用户是否是管理员标记,但是不能修改自己的是否是管理员标记,不管是否修改直接置为管理员即可
		if(u.getAdmin() == User.ADMIN_STATUS_IS_ADMIN) {
			//如果是管理员修改自己则直接置为管理员
			if(user.getId().equals(u.getId())) {
				user.setAdmin(User.ADMIN_STATUS_IS_ADMIN);
			}else {
				//如果是管理员修改其他人则需要重置session中的认证信息
				u.setAdmin(user.getAdmin());
			}
		}else {
			//如果当前登录用户不是管理员则只能修改自己,不能修改是否是管理员标记
			u.setAdmin(user.getAdmin());
		}
		
		if(!user.getAdmin().equals(oldUser.getAdmin())) {
			//如果是管理员改为普通用户则需要默认分配所有已启用插件
			if(oldUser.getAdmin() == User.ADMIN_STATUS_IS_ADMIN && user.getAdmin() == User.ADMIN_STATUS_IS_NOT_ADMIN) {
				String userId = user.getId();
				Plugin p = new Plugin();
				p.setStatus(IntConstants.STATUS_ENABLE.getCode());
				//已启用的所有插件
				List<Plugin> pluginList = pluginService.findPluginList(p, null);
				if(!pluginList.isEmpty()) {
					List<UserPlugin> userPlugins = new ArrayList<>(pluginList.size());
					pluginList.stream().forEach(plugin -> {
						UserPlugin userPlugin = new UserPlugin();
						userPlugin.setUserId(userId);
						userPlugin.setSystemVersion(plugin.getSystemVersion());
						userPlugin.setCreateDate(DateUtil.currentDate());
						userPlugin.setUpdateDate(DateUtil.currentDate());
						userPlugin.setName(plugin.getName());
						userPlugin.setDescription(plugin.getDescription());
						userPlugin.setTemplatePath(plugin.getTemplatePath());
						userPlugin.setTemplateContent(plugin.getTemplateContent());
						userPlugin.setGenerator(plugin.getGenerator());
						userPlugin.setGeneratorSourceContent(plugin.getGeneratorSourceContent());
						userPlugin.setGeneratorContent(plugin.getGeneratorContent());
						userPlugin.setFileRelativeDir(plugin.getFileRelativeDir());
						userPlugin.setFileSuffix(plugin.getFileSuffix());
						userPlugin.setPrefix(plugin.getPrefix());
						userPlugin.setSuffix(plugin.getSuffix());
						userPlugin.setPluginPath(plugin.getPluginPath());
						userPlugin.setDependencies(plugin.getDependencies());
						userPlugin.setStatus(IntConstants.STATUS_ENABLE.getCode());
						userPlugin.setCustom(UserPlugin.CUSTOM_STATUS_IS_NOT_CUSTOM);
						
						userPlugins.add(userPlugin);
					});
					
					userPluginService.batchInsert(userPlugins);
				}
			}
			//如果是普通用户修改为管理员用户则需要删除所有已分配插件
			if(oldUser.getAdmin() == User.ADMIN_STATUS_IS_NOT_ADMIN && user.getAdmin() == User.ADMIN_STATUS_IS_ADMIN) {
				UserPlugin userPlugin = new UserPlugin();
				userPlugin.setUserId(user.getId());
				List<UserPlugin> userPlugins = userPluginService.findList(userPlugin);
				userPluginService.deleteUserPlugins(userPlugins);
			}
		}
		
		if(user.getId().equals(u.getId())) {
			u.setUsername(user.getUsername());
			u.setName(user.getName());
			u.setStatus(user.getStatus());
		}
		
		return userRepository.saveAndFlush(user).getId();
	}

	@Transactional
	@Override
	public String disable(String id) {
		String currentUserId = getUserId();
		
		//不能禁用当前登录用户
		if(id.equals(currentUserId)) {
			throw new BusinessException("不能禁用当前登录用户");
		}
		User user = findById(id);
		if(IntConstants.STATUS_DISABLE.getCode() == user.getStatus()) {
			throw new BusinessException("该用户已禁用");
		}
		if(User.ADMIN_STATUS_IS_ADMIN == user.getAdmin()) {
			throw new BusinessException("该用户是超级管理员，不能禁用");
		}
		
		user.setStatus(IntConstants.STATUS_DISABLE.getCode());
		user.setUpdateDate(DateUtil.currentDate());
		user.setUpdateUserId(currentUserId);
		
		return userRepository.saveAndFlush(user).getId();
	}

	@Transactional
	@Override
	public String enable(String id) {
		User user = findById(id);
		if(IntConstants.STATUS_ENABLE.getCode() == user.getStatus()) {
			throw new BusinessException("该用户已启用");
		}
		
		user.setStatus(IntConstants.STATUS_ENABLE.getCode());
		user.setUpdateDate(DateUtil.currentDate());
		user.setUpdateUserId(getUserId());
		
		return userRepository.saveAndFlush(user).getId();
	}

	@Transactional
	@Override
	public User findById(String id) {
		return userRepository.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public Page<User> findList(User user, String search,Integer offset, Integer limit,String sort, String order) {
		Sort s = Sort.unsorted();
		Pageable pageable = null;
		if(StringUtils.hasLength(sort)) {
			s = new Sort(Direction.fromOptionalString(order).orElse(Direction.ASC), sort);
		}
		if(offset != null) {
			pageable = PageRequest.of(offset / limit, limit, s);
		}
		final User u = user == null ? new User() : user;
		return StringUtils.isEmpty(search) ? 
				userRepository.findAll(Example.of(u), pageable) : userRepository.findAll(
						new Specification<User>() {
							private static final long serialVersionUID = -7443994505461831568L;
							@Override
							public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
								List<Predicate> orPredicate = new ArrayList<>();
				                if(StringUtils.hasLength(search)){
				                	orPredicate.add(cb.like(root.get("username").as(String.class), "%"+search+"%"));
				                	orPredicate.add(cb.like(root.get("name").as(String.class), "%"+search+"%"));
				                	orPredicate.add(cb.like(root.get("updateDate").as(String.class), "%"+search+"%"));
				                }
				                
				                List<Predicate> andPredicate = new ArrayList<>();
				                //状态字段
				                if(u.getStatus() != null) {
				                	andPredicate.add(cb.equal(root.get("status").as(Integer.class), u.getStatus()));
				                }
				                //是否是管理员字段
				                if(u.getAdmin() != null) {
				                	andPredicate.add(cb.equal(root.get("admin").as(Integer.class), u.getAdmin()));
				                }
				                return query.where(cb.and(andPredicate.toArray(new Predicate[andPredicate.size()])),
				                		cb.or(orPredicate.toArray(new Predicate[orPredicate.size()]))).getRestriction();
							}
						}, pageable);
	}
	
	@Transactional
	@Override
	public List<User> findList(User user) {
		return userRepository.findAll(Example.of(user));
	}

	@Transactional
	@Override
	public User findByUsername(String username) {
		Assert.notNull(username, "用户名参数不能为空");
		
		User user = new User();
		user.setUsername(username);
		
		return userRepository.findOne(Example.of(user)).orElse(null);
	}
	
	@Transactional
	@Override
	public User findAdmin() {
		User user = new User();
		user.setAdmin(User.ADMIN_STATUS_IS_ADMIN);
		
		return userRepository.findOne(Example.of(user)).orElse(null);
	}
	
	@Transactional
	@Override
	public void sendAdminCaptcha(String username) {
		String captcha = userAdminCaptchaService.generateCaptcha();
		
		UserAdminCaptcha userAdminCaptcha = new UserAdminCaptcha();
		userAdminCaptcha.setUsername(username);
		userAdminCaptcha.setCaptcha(captcha);
		
		userAdminCaptchaService.insert(userAdminCaptcha);
		
		noticeMailService.sendAdminCaptchaMail(username,userAdminCaptcha.getValidateMinutes(), captcha);
	}

	@Override
	public void sendCaptcha(String username) {
		String captcha = userAdminCaptchaService.generateCaptcha();

		UserAdminCaptcha userAdminCaptcha = new UserAdminCaptcha();
		userAdminCaptcha.setUsername(username);
		userAdminCaptcha.setCaptcha(captcha);

		userAdminCaptchaService.insert(userAdminCaptcha);

		noticeMailService.sendAdminCaptchaMail(username,userAdminCaptcha.getValidateMinutes(), captcha);
	}

	@Transactional
	@Override
	public User userInfo() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		//上次更新人姓名
		
		String updateUserName = StringUtils.isEmpty(user.getUpdateUserId()) || "0".equals(user.getUpdateUserId()) ? "超级管理员" : findById(user.getUpdateUserId()).getName();
		user.setUpdateUserName(updateUserName);
		
		return user;
	}
	
	@Transactional
	@Override
	public void changePassword(String oldPassword, String captcha, String newPassword, String newPasswordRepeat) {
		//校验新密码和重复密码是否一致
		if(!newPassword.equals(newPasswordRepeat)) {
			throw new BusinessException("新密码和重复密码不一致");
		}
		
		//更新密码
		SecurityContext securityContext = SecurityContextHolder.getContext();
		
		Authentication authentication = securityContext.getAuthentication();
		
		User user = (User) authentication.getPrincipal();
		
		//校验验证码是否正确
		UserAdminCaptcha userAdminCaptcha = new UserAdminCaptcha();
		userAdminCaptcha.setUsername(user.getUsername());
		userAdminCaptcha.setStatus(IntConstants.STATUS_ENABLE.getCode());
		
		userAdminCaptcha = userAdminCaptchaService.find(userAdminCaptcha);
		if(userAdminCaptcha == null || !userAdminCaptcha.getCaptcha().equals(captcha)) {
			throw new BusinessException("验证码不正确");
		}

		//验证码已过期
		int validateMinutes = userAdminCaptcha.getValidateMinutes();
		long time = DateUtil.parse(userAdminCaptcha.getSendDate()).getTime();
		
		if(System.currentTimeMillis() - time > validateMinutes * 60 * 1000) {
			throw new BusinessException("验证码已过期");
		}
		
		//使验证码失效
		userAdminCaptchaService.setInvalidCaptcha(userAdminCaptcha);
		
		//校验和原密码是否一致
		if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new BusinessException("原密码不正确");
		}
		
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setUpdateUserId(user.getId());
		user.setUpdateDate(DateUtil.currentDate());
		
		userRepository.saveAndFlush(user);
		
		noticeMailService.sendPasswordModifySuccessMail(user.getUsername());
	}
	
	@Transactional
	@Override
	public void resetPassword(String userId, String newPassword, String newPasswordRepeat) {
		//不能重置自己的密码
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User currentUser = (User) authentication.getPrincipal();
		
		if(currentUser.getId().equals(userId)) {
			throw new BusinessException("不能重置当前登录用户的密码");
		}
				
		//校验新密码和重复密码是否一致
		if(!newPassword.equals(newPasswordRepeat)) {
			throw new BusinessException("新密码和重复密码不一致");
		}
		
		User user = findById(userId);
		
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setUpdateUserId(currentUser.getId());
		user.setUpdateDate(DateUtil.currentDate());
		
		userRepository.saveAndFlush(user);
		
		noticeMailService.sendResetPasswordSuccessMail(user.getUsername(), currentUser.getUsername());
	}
	
	@Transactional
	@Override
	public void active(String username, String checkCode, HttpServletRequest request, HttpServletResponse response) {
		try{
			UserActiveCheckCode userActiveCheckCode = new UserActiveCheckCode();
			userActiveCheckCode.setCheckCode(checkCode);
			userActiveCheckCode = userActiveCheckCodeService.find(userActiveCheckCode);
			
			User user = findByUsername(username);
			if(user.getStatus() == IntConstants.STATUS_ENABLE.getCode()) {
				logger.info("该用户（{}）已激活", username);
				return;
			}
			//未查询到验证码
			if(userActiveCheckCode == null) {
				throw new BusinessException("未查询到链接校验码");
			}
			
			//验证码已失效
			if(IntConstants.STATUS_DISABLE.getCode() == userActiveCheckCode.getStatus()) {
				throw new BusinessException("该链接已失效，请联系管理员手动激活账户");
			}
			
			long time = DateUtil.parse(userActiveCheckCode.getSendDate()).getTime();
			//验证码已过期
			if(System.currentTimeMillis() - time > userActiveCheckCode.getValidateMinutes() * 60 * 1000) {
				throw new BusinessException("该链接已失效，请联系管理员手动激活账户");
			}
			
			//验证码和用户名不匹配
			if(!user.getId().equals(userActiveCheckCode.getUserId())) {
				throw new BusinessException("校验码和用户名不匹配");
			}
			
			user.setStatus(IntConstants.STATUS_ENABLE.getCode());
			
		}catch(BusinessException e){
			request.getSession().setAttribute("activeErrorMessage", e.getMessage());
			
			try {
			    //退出认证中心
				redirectStrategy.sendRedirect(request, response, "/user/active/fail/" + username);
			} catch (UnsupportedEncodingException e1) {
				logger.error(e1.getMessage(),e);
			} catch (IOException e1) {
				logger.error(e1.getMessage(),e);
			} catch (IllegalArgumentException e1) {
				logger.error(e1.getMessage(),e);
			} 
		}
	}

	private String getUserId() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
			
		Authentication authentication = securityContext.getAuthentication();
		
		if(authentication == null) {
			throw new BusinessException("未获取到登录用户");
		}
		
		User user = (User) authentication.getPrincipal();
		
		return user.getId();
	}

	@Transactional
	@Override
	public String updateByUsername(User user) {
		user.setPassword(user.getPassword());
		user.setUpdateDate(DateUtil.currentDate());
		user.setUpdateUserName("用户找回密码");
		return userRepository.saveAndFlush(user).getId();
	}
}
