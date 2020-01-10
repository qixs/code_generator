$(document).ready(function(){
	//反馈发送邮件到技术支持并抄送当前登录人，发送到当前登录人邮箱

    $("button.submit").click(function () {
        //校验描述是否为空
        var desc = $("textarea[name='desc']").val();
        if(!desc){
            $.alert("请输入描述信息", function () {
                $("textarea[name='desc']").focus();
            });
            return;
        }
        //调用发送邮件功能
        var content = "问题类型：" + $("select[name='type']").val() + "<br/>问题描述：" + desc;
        emailModal("【重要】代码生成器" + $("select[name='type']").val(), true, content, window.top.$("input[name='authenticationUsername']").val());
    });
});
