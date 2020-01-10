$(document).ready(function(){
	$("a.close").click(function(){
		$(".error,.success").hide();
	});

	//校验方法
	var valid = function(){
		//校验访问日志保留天数是否为空
		var accessLogRemainDays = $("input[name='systemParameter.accessLogRemainDays']").val();
		if(!accessLogRemainDays){
			$("span.alertMsg").text("访问日志保留天数不能为空");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.accessLogRemainDays']").focus();
			return false;
		}
		//校验访问日志保留天数是否为正整数
		var reg = new RegExp(regexp.constants.integer);
		if(!reg.test(accessLogRemainDays) || parseInt(accessLogRemainDays) < 1){
			$("span.alertMsg").text("访问日志保留天数只能为正整数且必须大于0");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.accessLogRemainDays']").focus();
			return false;
		}

		//校验用户激活链接有效分钟数是否为空
		var userActiveMinutes = $("input[name='systemParameter.userActiveMinutes']").val();
		if(!userActiveMinutes){
			$("span.alertMsg").text("用户激活链接有效分钟数不能为空");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.userActiveMinutes']").focus();
			return false;
		}
		//校验用户激活链接有效分钟数是否为正整数
		var reg = new RegExp(regexp.constants.integer);
		if(!reg.test(userActiveMinutes) || parseInt(userActiveMinutes) < 1){
			$("span.alertMsg").text("用户激活链接有效分钟数只能为正整数且必须大于0");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.userActiveMinutes']").focus();
			return false;
		}

		//校验重置密码链接有效分钟数是否为空
		var resetPasswordMinutes = $("input[name='systemParameter.resetPasswordMinutes']").val();
		if(!resetPasswordMinutes){
			$("span.alertMsg").text("重置密码链接有效分钟数不能为空");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.resetPasswordMinutes']").focus();
			return false;
		}
		//校验重置密码链接有效分钟数是否为正整数
		var reg = new RegExp(regexp.constants.integer);
		if(!reg.test(resetPasswordMinutes) || parseInt(resetPasswordMinutes) < 1){
			$("span.alertMsg").text("重置密码链接有效分钟数只能为正整数且必须大于0");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.resetPasswordMinutes']").focus();
			return false;
		}

		//校验验证码有效分钟数是否为空
		var captchaExpireMinutes = $("input[name='systemParameter.captchaExpireMinutes']").val();
		if(!captchaExpireMinutes){
			$("span.alertMsg").text("验证码有效分钟数不能为空");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.captchaExpireMinutes']").focus();
			return false;
		}
		//校验验证码有效分钟数是否为正整数
		var reg = new RegExp(regexp.constants.integer);
		if(!reg.test(captchaExpireMinutes) || parseInt(captchaExpireMinutes) < 1){
			$("span.alertMsg").text("验证码有效分钟数只能为正整数且必须大于0");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.captchaExpireMinutes']").focus();
			return false;
		}

		//校验同时生成代码最大任务数是否为空
		var maxTaskCount = $("input[name='systemParameter.maxTaskCount']").val();
		if(!maxTaskCount){
			$("span.alertMsg").text("同时生成代码最大任务数不能为空");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.maxTaskCount']").focus();
			return false;
		}
		//校验同时生成代码最大任务数是否为正整数
		var reg = new RegExp(regexp.constants.integer);
		if(!reg.test(maxTaskCount) || parseInt(maxTaskCount) < 0){
			$("span.alertMsg").text("同时生成代码最大任务数只能为正整数且必须大于等于0");
			$("div.error,div.alert").show();
			$("input[name='systemParameter.maxTaskCount']").focus();
			return false;
		}
		
		return true;
	};

	//保存
	$("button#save").click(function(){
		$(".error").hide();
		//校验
		if(!valid()){
			return;
		}

		$.post("/config/system/parameter", $("form#systemParameterForm").serialize(), function(d){
			if(d && d.errorMessage){
				$("span.alertMsg").text(d.errorMessage);
				$("div.error,div.alert").show();
			}else{
				$("span.successMsg").text("保存成功");
				$("div.success,div.alert").show();
			}
		});

	});
	
	//重置
	$("button#rest").click(function(){
		document.getElementById("systemParameterForm").reset();
	});
});
