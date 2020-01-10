$(document).ready(function(){
	//发送验证码
	$("button#sendCaptcha").click(function(){
		var sendCaptcha = $(this);
		var username = $("input[name='user.username']").val();
		//发送验证码邮件
		$.post("/user/info/sendCaptcha", {username: username, "_csrf": $("meta[name='_csrf']").attr("content") },function(data){
			$.alert("验证码发送成功，请到“" + username + "”邮箱查看，如果未收到请重新发送");
			
			//设置发送验证码按钮倒计时为1分钟
			$("button#sendCaptcha").attr("disabled","disabled").removeClass("buttonMouseOver").addClass("buttonMouseOut");
			
			var second = 59;
			var interval = setInterval(function(){
				if(second <= 0){
					//取消正在执行的周期任务
					window.clearInterval(interval);
					sendCaptcha.removeAttr("disabled");
					sendCaptcha.text("重新发送");
					
				}else{
					sendCaptcha.text(second + " 秒后重新发送");
					
					second -- ;
				}
			},1000);
		});
	});
});
