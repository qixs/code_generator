$(document).ready(function(){
	//修改用户信息
	$("button.modify").click(function(){
		$.modal({
			title: "修改用户信息",
			body: function(){
				var body = null;
				$.ajax({
					type: "get", 
				    url: "/user/info/edit", 
				    cache:false, 
				    async:false, 
				    success: function(data){ 
				    	body = data;
				    } 
				});
				return body;
			},
			callback: function(modal,flag){
				if(flag){
					var f = true;
					$.ajax({
						type: "post", 
					    url: "/user/info", 
					    data: modal.find("#userForm").serialize(),
					    cache:false, 
					    async:false, 
					    success: function(data){ 
					    	if(data && $.isPlainObject(data) && data.errorMessage){
								$.alert("保存失败：" + data.errorMessage);
								f = false;
							}else{
								$("div.success").show();
								$("span.successMsg").text("保存成功");
								
								$("span.name").text(data.name);
								//更新页面显示用户名
								window.top.$("span.name").text(data.name);
							}
					    } 
					});
					
					return f;
				}
			},
		});
	});
	//修改密码
	$("button.changePassword").click(function(){
		$.modal({
			title: "修改密码",
			okButtonTitle: '确定',
			body: function(){
				var body = null;
				$.ajax({
					type: "get", 
				    url: "/user/info/password", 
				    cache:false, 
				    async:false, 
				    success: function(data){ 
				    	body = data;
				    } 
				});
				return body;
			},
			callback: function(modal,flag){
				if(flag){
					var params = window.top.$("#passwordForm").serializeObject();
					
					//原密码不能为空
					if(!params["oldPassword"]){
						$.alert("原密码不能为空");
						return false;
					}
					
					//密码在8位到18位之间
					if(params["oldPassword"].length < 8 || params["oldPassword"].length > 18){
						$.alert("原密码长度必须在8位到18位之间");
						return false;
					}
					
					//邮箱验证码不能为空
					if(!params["captcha"]){
						$.alert("邮箱验证码不能为空");
						return false;
					}
					
					//新密码不能为空
					if(!params["newPassword"]){
						$.alert("新密码不能为空");
						return false;
					}
					//密码在8位到18位之间
					if(params["newPassword"].length < 8 || params["newPassword"].length > 18){
						$.alert("新密码长度必须在8位到18位之间");
						return false;
					}
					//重复密码不能为空
					if(!params["newPasswordRepeat"]){
						$.alert("重复密码不能为空");
						return false;
					}
					//新密码和重复密码必须一直
					if(params["newPassword"] != params["newPasswordRepeat"]){
						$.alert("新密码和重复密码不一致");
						return false;
					}
					
					var f = true;
					$.ajax({
						type: "post", 
					    url: "/user/info/changePassword", 
					    data: params,
					    cache:false, 
					    async:false, 
					    success: function(data){ 
					    	if(data && $.isPlainObject(data) && data.errorMessage){
								$.alert("修改密码失败：" + data.errorMessage);
								f = false;
							}else{
								//退出
								$.post("/logout", {"_csrf": window.top.$("meta[name='_csrf']").attr("content")});
								
								$.alert("修改密码成功，请重新登录",function(){
									window.top.location.href = "/login";
								});
							}
					    } 
					});
					
					return f;
				}
			},
		});
	});

	$("div.success a.close").click(function () {
		$("div.success").hide();
	});
});
