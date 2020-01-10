
$(document).ready(function(){
	//如果不支持WebSocket则关闭当前浏览器
	if(!window.WebSocket){
		alert("当前浏览器不支持WebSocket，无法执行初始化操作");
		window.close();
		return;
	}
	//校验是否正在进行初始化操作
	var checkInitializing = function(){
		var host =  window.location.host;
		var  wsServer = "ws://" + host + "/checkInitializing"; 
		var  websocket = new WebSocket(wsServer); 
		websocket.onopen = function (evt) {
			console.debug("连接服务器成功");
		}; 
		websocket.onclose = function (evt) {
			console.debug("服务器断开连接");
		}; 
		websocket.onmessage = function (evt) {
			var data = evt.data;
			console.debug('收到服务器消息:' + data);
			data = JSON.parse(data);
			if(data.level == 'warn'){
				new PNotify({
				    title: '提醒',
				    text: data.description
				});
			}else if(data.level == 'error'){
				new PNotify({
				    title: '警告',
				    text: data.description,
				    type: 'error'
				});
				
				//如果发出警告则认为不能执行初始化操作,取消当前页面所有事件
				$("input[type='button']").unbind().hide();
			}
		}; 
		websocket.onerror = function (evt) {
			console.error('连接服务器出错');
			//连接服务器出错,取消当前页面所有事件
			$("input[type='button']").unbind().hide();
		}; 
		
		
		//添加状态判断，当为OPEN时，发送消息
	    if (websocket.readyState===1) {
	    	websocket.send("checkInitializing");//客户端向服务器发送消息
	    }else{
	        //do something
	    }
	};
	
	//校验是否正在进行初始化操作
	checkInitializing();
	
	//重新加载页面状态
	var reloadWizardStatus = function(currentStepNum){
		$.get("/init/wizard/" + currentStepNum,function(step){
			$("span.stepName").text(step.stepName);
			$("input#currentStepNum").val(step.stepNum);
			
			var maxStepNum = $("input#maxStepNum").val();
			//当前步骤为1则需要隐藏上一步、完成按钮,显示下一步按钮
			if(currentStepNum == 1){
				$("input[type='button'].preStepButton,input[type='button'].completeButton").hide();
				$("input[type='button'].nextStepButton").show();
			}else if(currentStepNum == maxStepNum){
				//如果是最后一步则隐藏下一步按钮,显示上一步和完成按钮
				$("input[type='button'].preStepButton,input[type='button'].completeButton").show();
				$("input[type='button'].nextStepButton").hide();
			}else{
				$("input[type='button'].preStepButton,input[type='button'].nextStepButton").show();
				$("input[type='button'].completeButton").hide();
			}
			
			//加载当前步骤的页面
			$("iframe[name='content']").attr("src" , step.stepUrl+"?t=" + new Date());
		});
		$.get("/init/wizard/findStepList",function(stepList){
			for(var i = 0 , length = stepList.length ; i < length ; i ++){
				var step = stepList[i];
				var li = $(".bodyLeft ul li:eq("+i+")");
				if(step.stepNum == currentStepNum){
					li.addClass("current");
				}else{
					li.removeClass("current");
				}
			}
		});
		//更新步骤号
		$.post("/init/wizard/updateStepNum/" + currentStepNum);
	};
	
	//上一步
	$("input[type='button'].preStepButton").click(function(){
		//当前步骤号
		var currentStepNum = $("input#currentStepNum").val();
		if(currentStepNum <= 1){
			alert("当前步骤编号为1，不能点击上一步！");
			return;
		}
		//重新加载页面状态
		reloadWizardStatus(currentStepNum - 1);
	});
	//下一步
	$("input[type='button'].nextStepButton").click(function(){
		//当前步骤号
		var currentStepNum = $("input#currentStepNum").val();
		var maxStepNum = $("input#maxStepNum").val();
		if(currentStepNum == maxStepNum){
			alert("当前步骤编号已经是最大，不能点击下一步！");
			return;
		}
		
		//调用iframe中的校验方法
		var flag = eval("window.top.document.getElementById('content').contentWindow.valid()");
		if(!flag){
			return;
		}
		
		//执行保存配置操作
		var data = eval("window.top.document.getElementById('content').contentWindow.save()");
		if(data){
			return;
		}
		
		//重新加载页面状态
		reloadWizardStatus(parseInt(currentStepNum) + 1);
	});
	//完成
	$("input[type='button'].completeButton").click(function(){
		$.confirm("确定完成初始化？",function(flag){
			if(flag){
				var csrf = $("meta[name='_csrf']").attr("content");
				var csrf_header = $("meta[name='_csrf_header']").attr("content");
				$.post("/init/wizard/complete",{_csrf : csrf , _csrf_header : csrf_header},function(data){
					//上传失败提示原因
					if(data && data.errorMessage){
						$.alert(data.errorMessage);
					}else{
						$.alert({message:"初始化完毕，点击关闭按钮跳转到主页",title:"提示"},function(){
							window.top.location.href = "/"; 
						});
					}
				});
			}
		});
	});
	
});
