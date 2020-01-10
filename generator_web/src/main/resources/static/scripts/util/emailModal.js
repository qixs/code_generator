/**
 * 编辑配置
 * @param subject 主题
 * @param attachmentNotNul 附件是否能够为空参数
 * @param content 邮件内容
 * @param emailCarbonCopy 抄送人
 * **/
function emailModal(subject,attachmentNotNul, content, emailCarbonCopy){
	$.modal({
		title: "发送邮件",
		okButtonTitle: "发送",
		width: 1000,
		height: 445,
		body: function(){
			var body = null;
			$.ajax({
				type: "get", 
			    url: "/config/email/emailModal?subject=" + encodeURIComponent(subject) + (content ? "&content=" + encodeURIComponent(content) : "") + (emailCarbonCopy ? "&emailCarbonCopy=" + encodeURIComponent(emailCarbonCopy) : ""),
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
				var sendMail = function(){
					var f = true;
					var complete = false;
					modal.find("#emailForm").ajaxSubmit({
						method: "post",
						async: false,
						url: "/config/email/send",
						success: function(data){
							complete = true;
							//上传失败提示原因
							if(data && data.errorMessage){
								$.alert("发送失败：" + data.errorMessage);
								f = false;
							}else{
								$.alert("发送成功");
							}
						},
						error: function(context){
							complete = true;
							$.alert("发送失败：" + context.responseJSON.message);
							f = false;
						}
					});
					//这里进行自旋,模拟同步提交
					while(!complete){}
					return f;
				}
				
				var params = modal.find("#emailForm").serializeObject();
				if(!params.emailFrom){
					$.alert("发件人不能为空");
					return false;
				}
				if(!params.emailTo){
					$.alert("收件人不能为空");
					return false;
				}
				//附加不能为空
				if(!modal.find("input[name='attachment']").val() && attachmentNotNul === false){
					$.alert("附件不能为空");
					return false;
				}
				
				if(!params.subject){
					$.confirm("主题为空，您确定继续发送这封邮件吗？",function(flag){
						if(flag){
							return sendMail();
						}
					});
				}else{
					return sendMail();
				}
			}
		},
		modalVisibleCallback: function(modal){
			//上传插件
			modal.find(".chooseFile").click(function(){
				modal.find("input[type='file'].file").trigger("click");
			});
			//上传文件完毕
			modal.find("input[type='file'].file").change(function(){
				var value = $(this).val();
				modal.find("input[type='text'].fileName").val(value);
			});
		},
	});
}

