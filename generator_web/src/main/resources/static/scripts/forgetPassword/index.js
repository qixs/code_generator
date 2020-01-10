
$(document).ready(function(){
	var notice = function(text){
		var timeout = null;
		if(timeout != null){
			window.clearTimeout(timeout);
		}
		$(".notice").show().removeClass("hide").text(text);
		timeout = setTimeout(function () {
            $(".notice").addClass("hide").hide().text("");
        },5000);
	};
	
	$("input[type='button'].submit").click(function (e) {
		notice("请先等验证码加载完毕");
	});
	
	var handler = function (captchaObj) {
		$("input[type='button'].submit").unbind("click");
		
        $("input[type='button'].submit").click(function (e) {
        	//用户名不能为空
    		if(!$("input[name='username']").val()){
    			notice("用户名不能为空");
    			$("input[name='username']").focus();
    			e.preventDefault();
    			return;
    		}
    		//用户名必须是邮件
    		var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
    		if(!reg.test($("input[name='username']").val())){
    			notice("用户名必须为邮箱");
    			$("input[name='username']").focus();
    			e.preventDefault();
    			return;
    		}
    		
            var result = captchaObj.getValidate();
            if (!result) {
            	notice("请先点击按钮进行验证");
            } else {
            	$("form[name='login']").submit();
            }
            e.preventDefault();
        });
        // 将验证码加到id为captcha的元素里，同时会有三个input的值用于表单提交
        captchaObj.appendTo(".captcha");
        captchaObj.onReady(function () {
            $(".wait").removeClass("show").hide();
        });
        // 更多接口参考：http://www.geetest.com/install/sections/idx-client-sdk.html
    };
    $.ajax({
        url: "/captcha/geetest/register?t=" + (new Date()).getTime(), // 加随机数防止缓存
        type: "get",
        dataType: "json",
        success: function (data) {
            // 调用 initGeetest 初始化参数
            // 参数1：配置参数
            // 参数2：回调，回调的第一个参数验证码对象，之后可以使用它调用相应的接口
            initGeetest({
                gt: data.gt,
                challenge: data.challenge,
                new_captcha: data.new_captcha, // 用于宕机时表示是新验证码的宕机
                offline: !data.success, // 表示用户后台检测极验服务器是否宕机，一般不需要关注
                product: "popup", // 产品形式，包括：float，popup
                width: "100%"
                // 更多配置参数请参见：http://www.geetest.com/install/sections/idx-client-sdk.html#config
            }, handler);
        }
    });
});
