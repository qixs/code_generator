package com.qxs.generator.web.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qxs.generator.web.annotation.ResourceAccessRole;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.model.user.User.CreateAdmin;
import com.qxs.generator.web.service.user.IUserService;
import com.qxs.generator.web.validate.group.Create;
import com.qxs.generator.web.validate.group.Update;

/**
 * 用户信息控制器
 * 
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-5-31
 * @version Revision: 1.0
 */
@Controller
@RequestMapping("/user")
public class UserController {
	
	@InitBinder("user")
    public void initBinderUser(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("user.");
    }
	@InitBinder("pageable")
    public void initBinderPageable(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("pageable.");
    }
	
	@Value("${notice.helpUrl:}")
	private String helpUrl;
	
	@Autowired
	private IUserService userService;
	
	/***
	* 用户信息首页
	**/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping({ "", "/", "/index" })
	public String index() {
		return "user/index";
	}
	
	/**
	* 获取列表数据
	* @param user 查询条件实体
	* @return PageList<RoleButton>
	**/
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/getList")
	@ResponseBody
	public Page<User> getList(User user, String search,
			@RequestParam(required = false)Integer offset, @RequestParam(required = false)Integer limit,
			@RequestParam(required = false)String sort, @RequestParam(required = false)String order) {
		return userService.findList(user, search, offset, limit, sort, order);
	}

	/**
	 * 新增页面
	 * */
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/editNew")
	public String editNew() {
		return "user/edit";
	}

	/**
	 * 修改
	 * */
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") String id, Model model) {
		model.addAttribute("user", userService.findById(id));
		return "user/edit";
	}

	/**
	 * 新增保存
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@PostMapping
	@ResponseBody
	public String create(@Validated({Create.class}) User user, @RequestParam String passwordRepeat) {
		return userService.insert(user, passwordRepeat);
	}
	/**
	 * 新增保存
	 * **/
	@PostMapping("/admin")
	@ResponseBody
	public String createAdmin(@Validated({CreateAdmin.class}) User user,
			@RequestParam String captcha,@RequestParam String repeatPassword) {
		return userService.insertAdmin(user, captcha, repeatPassword);
	}

	/**
	 * 修改保存
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@PutMapping
	@ResponseBody
	public String update(@Validated({Update.class})User user) {
		return userService.update(user);
	}
	
	/**
	 * 禁用用户
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@PostMapping("/disable/{id}")
	@ResponseBody
	public String disable(@PathVariable String id) {
		return userService.disable(id);
	}
	/**
	 * 启用用户
	 * **/
	@ResourceAccessRole("ROLE_ADMIN")
	@PostMapping("/enable/{id}")
	@ResponseBody
	public String enable(@PathVariable String id) {
		return userService.enable(id);
	}
	
	/**
	 * 重设密码
	 * */
	@ResourceAccessRole("ROLE_ADMIN")
	@GetMapping("/password/{id}")
	public String password(@PathVariable String id, Model model) {
		model.addAttribute("user", userService.findById(id));
		return "user/password";
	}
	
	/***
	* 重置密码
	**/
	@ResourceAccessRole("ROLE_ADMIN")
	@PostMapping("/resetPassword")
	@ResponseBody
	public void resetPassword(@RequestParam String id, @RequestParam String newPassword, @RequestParam String newPasswordRepeat) {
		userService.resetPassword(id, newPassword, newPasswordRepeat);
	}
	
	@ModelAttribute
    public User userUpdate(@RequestParam(value = "user.id" , required = false) String id) {
		if(id != null) {
			User user = userService.findById(id);
			if(user == null) {
				throw new BusinessException("未查询到用户信息，用户可能已经被删除");
			}
			return user.clone();
		}
		return new User();
    }  
	
}
