package com.qxs.generator.web.service.rest.password.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Service;

import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.UserPasswordCheckCode;
import com.qxs.generator.web.service.config.IGeetestService;
import com.qxs.generator.web.service.notice.mail.INoticeMailService;
import com.qxs.generator.web.service.rest.password.IRestPasswordService;
import com.qxs.generator.web.service.user.IUserPasswordCheckCodeService;
import com.qxs.generator.web.service.user.IUserService;
import com.qxs.generator.web.util.DateUtil;

@Service
public class RestPasswordServiceImpl implements IRestPasswordService {
	
	private transient final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IGeetestService geetestService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserPasswordCheckCodeService userPasswordCheckCodeService;
	@Autowired
	private INoticeMailService noticeMailService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void sendMail(String username,boolean enhencedValidate,HttpServletRequest request,HttpServletResponse response) {
		
		try{
			request.getSession().removeAttribute("errorMessage");

			//验证码校验
			if(enhencedValidate) {
				geetestService.enhencedValidate();
			}
			
			//用户名存在校验
			User user = userService.findByUsername(username);
			if(user == null) {
				throw new BusinessException("用户名不存在");
			}
			
			//登记重置密码邮件校验码
			UserPasswordCheckCode userPasswordCheckCode = new UserPasswordCheckCode();
			userPasswordCheckCode.setUserId(user.getId());
			userPasswordCheckCode.setCheckCode(userPasswordCheckCodeService.generateCheckCode());
			
			userPasswordCheckCodeService.insert(userPasswordCheckCode);
			
			//获取当前应用地址
			String serverUrl = request.getScheme() + "://" + request.getServerName() + ":" +request.getServerPort() + request.getContextPath();
			
			//发送提醒邮件
			noticeMailService.sendResetPasswordMail(user.getUsername(),userPasswordCheckCode.getValidateMinutes(), serverUrl + String.format("/forgetPassword/restPassword/%s/%s", user.getUsername(), userPasswordCheckCode.getCheckCode()));
			
		}catch(BusinessException e){
			request.getSession().setAttribute("errorMessage", e.getMessage());
			
			try {
			    //退出认证中心
				redirectStrategy.sendRedirect(request, response, "/forgetPassword?error=");
			} catch (UnsupportedEncodingException e1) {
				logger.error(e1.getMessage(),e);
			} catch (IOException e1) {
				logger.error(e1.getMessage(),e);
			} catch (IllegalArgumentException e1) {
				logger.error(e1.getMessage(),e);
			} 
		}
	}
	
	@Override
	public void validCheckCode(String username, String checkCode,HttpServletRequest request,HttpServletResponse response) {
		try{
			request.getSession().removeAttribute("errorMessage");

			UserPasswordCheckCode userPasswordCheckCode = new UserPasswordCheckCode();
			userPasswordCheckCode.setCheckCode(checkCode);
			userPasswordCheckCode = userPasswordCheckCodeService.find(userPasswordCheckCode);
			
			//未查询到验证码
			if(userPasswordCheckCode == null) {
				throw new BusinessException("未查询到校验码");
			}
			
			//验证码已失效
			if(IntConstants.STATUS_DISABLE.getCode() == userPasswordCheckCode.getStatus()) {
				throw new BusinessException("校验码已失效");
			}
			
			long time = DateUtil.parse(userPasswordCheckCode.getSendDate()).getTime();
			//验证码已过期
			if(System.currentTimeMillis() - time > userPasswordCheckCode.getValidateMinutes() * 60 * 1000) {
				throw new BusinessException("校验码已过期");
			}
			
			User user = userService.findByUsername(username);
			//验证码和用户名不匹配
			if(!user.getId().equals(userPasswordCheckCode.getUserId())) {
				throw new BusinessException("校验码和用户名不匹配");
			}
			
		}catch(BusinessException e){
			request.getSession().setAttribute("errorMessage", e.getMessage());
			
			try {
			    //退出认证中心
				redirectStrategy.sendRedirect(request, response, "/forgetPassword?error=");
			} catch (UnsupportedEncodingException e1) {
				logger.error(e1.getMessage(),e);
			} catch (IOException e1) {
				logger.error(e1.getMessage(),e);
			} catch (IllegalArgumentException e1) {
				logger.error(e1.getMessage(),e);
			} 
		}
	}
	
	@Override
	public void restPassword(String username, String checkCode, String password, String repeatPassword, HttpServletRequest request,HttpServletResponse response) {
		try{
			request.getSession().removeAttribute("errorMessage");

			if(!password.equals(repeatPassword)){
				throw new BusinessException("密码和重复密码不一致");
			}

			//验证码校验
			geetestService.enhencedValidate();

			//用户名存在校验
			User user = userService.findByUsername(username);
			if(user == null) {
				throw new BusinessException("用户名不存在");
			}

			UserPasswordCheckCode userPasswordCheckCode = new UserPasswordCheckCode();
			userPasswordCheckCode.setCheckCode(checkCode);
			userPasswordCheckCode = userPasswordCheckCodeService.find(userPasswordCheckCode);

			//未查询到验证码
			if(userPasswordCheckCode == null) {
				throw new BusinessException("未查询到校验码");
			}

			//验证码已失效
			if(IntConstants.STATUS_DISABLE.getCode() == userPasswordCheckCode.getStatus()) {
				throw new BusinessException("校验码已失效");
			}

			long time = DateUtil.parse(userPasswordCheckCode.getSendDate()).getTime();
			//验证码已过期
			if(System.currentTimeMillis() - time > userPasswordCheckCode.getValidateMinutes() * 60 * 1000) {
				throw new BusinessException("校验码已过期");
			}

			//验证码和用户名不匹配
			if(!user.getId().equals(userPasswordCheckCode.getUserId())) {
				throw new BusinessException("校验码和用户名不匹配");
			}

			//更新密码
			user.setPassword(passwordEncoder.encode(password));

			//更新密码
			userService.updateByUsername(user);

			//发送密码重置成功邮件
			noticeMailService.sendResetPasswordSuccessMail(username, username);
		}catch (BusinessException e){
			request.getSession().setAttribute("errorMessage", e.getMessage());

			try {
				//跳转到发送验证码界面
				redirectStrategy.sendRedirect(request, response, "/forgetPassword?error=");
			} catch (UnsupportedEncodingException e1) {
				logger.error(e1.getMessage(),e);
			} catch (IOException e1) {
				logger.error(e1.getMessage(),e);
			} catch (IllegalArgumentException e1) {
				logger.error(e1.getMessage(),e);
			}
		}

	}

}
