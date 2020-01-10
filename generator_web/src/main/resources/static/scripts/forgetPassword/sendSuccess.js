
$(document).ready(function(){
	$("a.repeatSendMail").click(function () {
        var username = $("input[name='username']").val();
        if(!username){
            $.alert("未获取到邮箱地址");
            return;
        }
        $.post("/forgetPassword/repeatSendMail", {username: username, "_csrf": $("meta[name='_csrf']").attr("content")}, function (data) {
            if(data && $.isPlainObject(data) && data.errorMessage){
                $.alert(data.errorMessage);
            }else{
                $.alert("发送成功");
            }
        });
    });
});
