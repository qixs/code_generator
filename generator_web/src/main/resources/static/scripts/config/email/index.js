$(document).ready(function(){
	$("a.close").click(function(){
		$(".error,.success").hide();
	});
	
	var ssl = $("input[type='checkbox'][name='email.ssl']");
	if(ssl.val() == '0'){
		ssl.attr("checked",false);
	}else{
		ssl.attr("checked",true);
	}
	
	$("input[type='checkbox'][name='email.ssl']").change(function(){
		var sslCheckBox = $(this);
		if(sslCheckBox.is(":checked")){
			sslCheckBox.val("1");
			$("input[name='email.port']").val(465);
		}else{
			sslCheckBox.val("0");
			$("input[name='email.port']").val(25);
		}
	});
	//校验方法
	var valid = function(){
		//校验账号是否为空
		var emailFrom = $("input[name='email.emailFrom']").val();
		if(!emailFrom){
			$("span.alertMsg").text("账号不能为空");
			$("div.error,div.alert").show();
			$("input[name='email.emailFrom']").focus();
			return false;
		}
		//校验账号是否是邮箱
		var reg = new RegExp(regexp.constants.email);
		if(!reg.test(emailFrom)){
			$("span.alertMsg").text("账号必须为邮箱");
			$("div.error,div.alert").show();
			$("input[name='email.emailFrom']").focus();
			return false;
		}
		
		//校验密码是否为空
		var password = $("input[name='email.password']").val();
		if(!password){
			$("span.alertMsg").text("密码不能为空");
			$("div.error,div.alert").show();
			$("input[name='email.password']").focus();
			return false;
		}
		
		//校验发件服务器是否为空
		var host = $("input[name='email.host']").val();
		if(!host){
			$("span.alertMsg").text("发件服务器不能为空");
			$("div.error,div.alert").show();
			$("input[name='email.host']").focus();
			return false;
		}
		
		//校验发件服务器端口是否为空
		var port = $("input[name='email.port']").val();
		if(!port){
			$("span.alertMsg").text("发件服务器端口不能为空");
			$("div.error,div.alert").show();
			$("input[name='email.port']").focus();
			return false;
		}
		
		//检查发件服务器端口是否是数字
		var reg = new RegExp(regexp.constants.integer);
		if(!reg.test(port)){
			$("span.alertMsg").text("发件服务器端口必须为数字");
			$("div.error,div.alert").show();
			$("input[name='email.port']").focus();
			return false;
		}
		
		//检查发件服务器端口是否超过限制(1至65535)
		var port = parseInt(port);
		if(port < 1 || port > 65535){
			$("span.alertMsg").text("发件服务器端口必须在1至65535之间");
			$("div.error,div.alert").show();
			$("input[name='email.port']").focus();
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
		
		//提醒会发送提醒邮件
		$.confirm("继续保存会先发送测试邮件，发送成功之后会继续保存，是否继续？", function(flag){
			if(flag){
				//校验邮箱地址是否为空
				var testMail = $("input[name='testMail']").val();
				if(!testMail){
					$("span.alertMsg").text("测试账号不能为空");
					$("div.error,div.alert").show();
					$("input[name='testMail']").focus();
					return;
				}
				var reg = new RegExp(regexp.constants.email);
				if(!reg.test(testMail)){
					$("span.alertMsg").text("测试账号必须为邮箱");
					$("div.error,div.alert").show();
					$("input[name='testMail']").focus();
					return false;
				}
				
				//发送测试邮件
				$.post("/config/email/sendValidMail", $("form#emailForm").serialize(),function(data){
					if(data && data.errorMessage){
						//邮件发送失败
						$("span.alertMsg").text(data.errorMessage);
						$("div.error,div.alert").show();
					}else{
						//邮件发送成功
						//保存邮箱配置信息
						$.post("/config/email", $("form#emailForm").serialize(), function(d){
							if(d && d.errorMessage){
								$("span.alertMsg").text(d.errorMessage);
								$("div.error,div.alert").show();
							}else{
								$("span.successMsg").text("保存成功");
								$("div.success,div.alert").show();
							}
						});
					}
				});
			}
		});
	});
	
	//重置
	$("button#rest").click(function(){
		document.getElementById("emailForm").reset();
	});
});
