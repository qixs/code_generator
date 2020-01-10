package com.qxs.generator.web.model.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.qxs.generator.web.constant.IntConstants;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.id.generator.IdGenerator;
import com.qxs.generator.web.validate.group.Create;
import com.qxs.generator.web.validate.group.Update;

/**
 * 用户表 user
 * 
 * @author qixingshen
 * @date 2018-05-29
 **/
@Entity
@Table(name = "user")
public class User implements UserDetails, Cloneable{
	
	private static final long serialVersionUID = 827491776461741925L;
	/**
	 * 是管理员
	 * **/
	public static final int ADMIN_STATUS_IS_ADMIN = 1;
	/**
	 * 不是管理员
	 * **/
	public static final int ADMIN_STATUS_IS_NOT_ADMIN = 0;

	/**
	 * 主键
	 **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	@NotBlank(groups= {Update.class})
	private String id;
	
	/**
	 * 用户名
	 **/
	@NotBlank(groups= {Create.class,Update.class,CreateAdmin.class} , message = "用户名不能为空")
	@Length(max=200 , groups= {Create.class,Update.class,CreateAdmin.class} , message = "用户名最大长度为200")
	//邮箱
	@Pattern(regexp="^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$" , message = "用户名必须为邮箱" , groups= {Create.class,Update.class,CreateAdmin.class})
	private String username;
	/**
	 * 密码
	 **/
	@NotBlank(groups= {Create.class} , message = "密码不能为空")
	@Length(min=8 , max=18 , groups= {Create.class}  , message = "密码长度必须在8位到18位之间")
	private String password;
	/**
	 * 姓名
	 **/
	@NotBlank(groups= {Create.class,Update.class,CreateAdmin.class}  , message = "姓名不能为空")
	private String name;
	/**
	 * 状态
	 **/
	private Integer status;
	/**
	 * 是否是管理员
	 **/
	private Integer admin;
	/**
	 * 创建人id
	 **/
	private String createUserId;
	/**
	 * 创建日期
	 **/
	private String createDate;
	/**
	 * 最后一次更新人id
	 **/
	private String updateUserId;
	/**
	 * 最后一次更新人姓名
	 * **/
	@Transient
	private String updateUserName;
	/**
	 * 更新日期
	 **/
	private String updateDate;
	
	/**
	 * 登录日志id
	 * **/
	@Transient
	private String loginLogId;
	
	/**
	 * 主键 自增
	 **/
	public String getId() {
		return id;
	}
	/**
	 * 主键 自增
	 **/
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 用户名
	 **/
	@Override
	public String getUsername() {
		return username;
	}
	/**
	 * 用户名
	 **/
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 密码
	 **/
	@Override
	public String getPassword() {
		return password;
	}
	/**
	 * 密码
	 **/
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 姓名
	 **/
	public String getName() {
		return name;
	}
	/**
	 * 姓名
	 **/
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 状态
	 **/
	public Integer getStatus() {
		return status;
	}
	/**
	 * 状态
	 **/
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 是否是管理员
	 **/
	public Integer getAdmin() {
		return admin;
	}
	/**
	 * 是否是管理员
	 **/
	public void setAdmin(Integer admin) {
		this.admin = admin;
	}
	/**
	 * 创建人id
	 **/
	public String getCreateUserId() {
		return createUserId;
	}
	/**
	 * 创建人id
	 **/
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	/**
	 * 创建日期
	 **/
	public String getCreateDate() {
		return createDate;
	}
	/**
	 * 创建日期
	 **/
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	/**
	 * 最后一次更新人id
	 **/
	public String getUpdateUserId() {
		return updateUserId;
	}
	/**
	 * 最后一次更新人id
	 **/
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	/**
	 * 更新日期
	 **/
	public String getUpdateDate() {
		return updateDate;
	}
	/**
	 * 更新日期
	 **/
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthoritys = new ArrayList<>();
		//普通用户角色
		grantedAuthoritys.add(new SimpleGrantedAuthority("ROLE_USER"));
		//如果是管理员
		if(ADMIN_STATUS_IS_ADMIN == admin) {
			grantedAuthoritys.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		return grantedAuthoritys;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return IntConstants.STATUS_ENABLE.getCode() == status;
	}
	/**
	 * 最后一次更新人姓名
	 * **/
	public String getUpdateUserName() {
		return updateUserName;
	}
	/**
	 * 最后一次更新人姓名
	 * **/
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}
	
	@Override
	public User clone() {
		try {
			return (User) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new BusinessException(e);
		}
	}
	/**
	 * 登录日志id
	 * **/
	public String getLoginLogId() {
		return loginLogId;
	}
	/**
	 * 登录日志id
	 * **/
	public void setLoginLogId(String loginLogId) {
		this.loginLogId = loginLogId;
	}
	@Override
	public String toString() {
		return String.format("User{id = %s , username = %s , password = %s , name = %s , status = %s , admin = %s , "
				+ "createUserId = %s , createDate = %s , updateUserId = %s , updateDate = %s }", 
				id, username, password, name, status, admin, createUserId, createDate, updateUserId, updateDate 
				);
	}
	
	public interface CreateAdmin {
		
	}
}
