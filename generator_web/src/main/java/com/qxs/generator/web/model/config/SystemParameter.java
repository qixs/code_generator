package com.qxs.generator.web.model.config;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.qxs.generator.web.id.generator.IdGenerator;
import com.qxs.generator.web.validate.group.Update;

/**
 * 系统参数配置信息
 * **/
@Entity
@Table(name = "config_system_parameter")
public class SystemParameter {

	/**
	 * id
	 * **/
	@Id
	@GenericGenerator(name = IdGenerator.ID_GENERATOR_NAME, strategy = IdGenerator.ID_GENERATOR_FULL_CLASS_NAME)
	@GeneratedValue(generator = IdGenerator.ID_GENERATOR_NAME)
	private String id;

	/**
	 *
	 * 访问日志保留天数 默认30天
	 */
	@NotNull(message = "访问日志保留天数不能为空", groups = {Update.class})
	@Min(value = 1, message = "访问日志保留天数必须大于0", groups = {Update.class})
	@Max(value = Integer.MAX_VALUE, message = "访问日志保留天数必须小于2147483647", groups = {Update.class})
	private Integer accessLogRemainDays;

	/**
	 *
	 * 用户激活链接有效分钟数  默认30分钟
	 */
	@NotNull(message = "用户激活链接有效分钟数不能为空", groups = {Update.class})
	@Min(value = 1, message = "用户激活链接有效分钟数天数必须大于0", groups = {Update.class})
	@Max(value = Integer.MAX_VALUE, message = "用户激活链接有效分钟数必须小于2147483647", groups = {Update.class})
	private Integer userActiveMinutes;
	/**
	 *
	 * 重置密码链接有效分钟数  默认30分钟
	 */
	@NotNull(message = "重置密码链接有效分钟数不能为空", groups = {Update.class})
	@Min(value = 1, message = "重置密码链接有效分钟数必须大于0", groups = {Update.class})
	@Max(value = Integer.MAX_VALUE, message = "重置密码链接有效分钟数必须小于2147483647", groups = {Update.class})
	private Integer resetPasswordMinutes;
	/**
	 *
	 * 验证码有效分钟数
	 */
	@NotNull(message = "验证码有效分钟数不能为空", groups = {Update.class})
	@Min(value = 1, message = "验证码有效分钟数必须大于0", groups = {Update.class})
	@Max(value = Integer.MAX_VALUE, message = "验证码有效分钟数必须小于2147483647", groups = {Update.class})
	private Integer captchaExpireMinutes;
	/**
	 *
	 * 是否允许用户自定义插件格式  默认是
	 */
	@NotNull(message = "是否允许用户自定义插件不能为空", groups = {Update.class})
	@Min(value = 0, message = "是否允许用户自定义插件格式必须大于等于0", groups = {Update.class})
	@Max(value = 1, message = "是否允许用户自定义插件格式必须小于等于1", groups = {Update.class})
	private Integer enableUserCustomPlugin;
	/**
	 *
	 * 同时生成代码最大任务数(需要先进行压测,合理指定该参数防止系统压爆  为0则不限制  默认10)
	 */
	@NotNull(message = "同时生成代码最大任务数不能为空", groups = {Update.class})
	@Min(value = 0, message = "同时生成代码最大任务数必须大于等于0", groups = {Update.class})
	@Max(value = Integer.MAX_VALUE, message = "同时生成代码最大任务数必须小于2147483647", groups = {Update.class})
	private Integer maxTaskCount;
	/**
	 * id
	 * **/
	public String getId() {
		return id;
	}
	/**
	 * id
	 * **/
	public void setId(String id) {
		this.id = id;
	}
	/**
	 *
	 * 访问日志保留天数 默认30天
	 */
	public Integer getAccessLogRemainDays() {
		return accessLogRemainDays;
	}
	/**
	 *
	 * 访问日志保留天数 默认30天
	 */
	public void setAccessLogRemainDays(Integer accessLogRemainDays) {
		this.accessLogRemainDays = accessLogRemainDays;
	}

	/**
	 *
	 * 用户激活链接有效分钟数  默认30分钟
	 */
	public Integer getUserActiveMinutes() {
		return userActiveMinutes;
	}
	/**
	 *
	 * 用户激活链接有效分钟数  默认30分钟
	 */
	public void setUserActiveMinutes(Integer userActiveMinutes) {
		this.userActiveMinutes = userActiveMinutes;
	}
	/**
	 *
	 * 重置密码链接有效分钟数  默认30分钟
	 */
	public Integer getResetPasswordMinutes() {
		return resetPasswordMinutes;
	}
	/**
	 *
	 * 重置密码链接有效分钟数  默认30分钟
	 */
	public void setResetPasswordMinutes(Integer resetPasswordMinutes) {
		this.resetPasswordMinutes = resetPasswordMinutes;
	}
	/**
	 *
	 * 验证码有效分钟数
	 */
	public Integer getCaptchaExpireMinutes() {
		return captchaExpireMinutes;
	}
	/**
	 *
	 * 验证码有效分钟数
	 */
	public void setCaptchaExpireMinutes(Integer captchaExpireMinutes) {
		this.captchaExpireMinutes = captchaExpireMinutes;
	}

	/**
	 *
	 * 是否允许用户自定义插件格式  默认是
	 */
	public Integer getEnableUserCustomPlugin() {
		return enableUserCustomPlugin;
	}
	/**
	 *
	 * 是否允许用户自定义插件格式  默认是
	 */
	public void setEnableUserCustomPlugin(Integer enableUserCustomPlugin) {
		this.enableUserCustomPlugin = enableUserCustomPlugin;
	}
	/**
	 *
	 * 同时生成代码最大任务数(需要先进行压测,合理指定该参数防止系统压爆  为0则不限制  默认10)
	 */
	public Integer getMaxTaskCount() {
		return maxTaskCount;
	}
	/**
	 *
	 * 同时生成代码最大任务数(需要先进行压测,合理指定该参数防止系统压爆  为0则不限制  默认10)
	 */
	public void setMaxTaskCount(Integer maxTaskCount) {
		this.maxTaskCount = maxTaskCount;
	}

	@Override
	public String toString() {
		return "SystemParameter{" +
				"id='" + id + '\'' +
				", accessLogRemainDays=" + accessLogRemainDays +
				", userActiveMinutes=" + userActiveMinutes +
				", resetPasswordMinutes=" + resetPasswordMinutes +
				", captchaExpireMinutes=" + captchaExpireMinutes +
				", enableUserCustomPlugin=" + enableUserCustomPlugin +
				", maxTaskCount=" + maxTaskCount +
				'}';
	}

	public enum EnableUserCustomPlugin{
	    ENABLE(1, "是"),
        DISABLE(0, "否");
	    private int value;
	    private String desc;
	    private EnableUserCustomPlugin(int value, String desc){
	        this.value = value;
	        this.desc = desc;
        }

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }
}
