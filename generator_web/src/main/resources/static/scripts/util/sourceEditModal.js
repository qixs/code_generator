//编辑源码
function sourceEdit(title, groupName, pluginName, sourceEditUrl, generateCodeUrl, saveUrl, setDefaultUrl, setSystemConfigUrl){
	//计算浏览器窗口大小
	var windowWidth = $(window.top).width();
	var windowHeight = $(window.top).height();
	var modalWidth = windowWidth - 100;
	var modalHeight = windowHeight - 180;
	
	//清除页面所有modal,防止页面渲染出错
	window.top.$("div.modal,div.modal-dialog").remove();
	
	//弹出新增modal
	$.modal({
		title: title,
		height: modalHeight,
		width: modalWidth,
		callback: function(modal,flag){
			if(flag){
				var f = true;
				$.ajax({
					type: "post", 
				    url: saveUrl, 
				    data: modal.find("#sourceForm").serialize(),
				    cache:false, 
				    async:false, 
				    success: function(data){ 
				    	if(data && $.isPlainObject(data) && data.errorMessage){
							$.alert("保存失败：" + data.errorMessage);
							f = false;
						}else{
							$.alert("保存成功");
						}
				    } 
				});
				
				return f;
			}
		},
		body: function(){
			var body = null;
			$.ajax({
				type: "get", 
			    url: sourceEditUrl, 
			    cache:false, 
			    async:false, 
			    success: function(data){
					if(data && $.isPlainObject(data) && data.errorMessage){
						$.alert(data.errorMessage);
					}else{
						body = data;
					}
			    } 
			});
			return body;
		},
		//modal显示之后执行的回调方法
		modalVisibleCallback: function(modal){
			//重置左右窗格大小
			//左边窗格比例
			var sourceLeftRatio = 0.5;
			var collapseWidth = window.top.$("#collapse").width();
			
			var containerWidth = modalWidth - 34;
			
			var leftWidth = (containerWidth - collapseWidth) * sourceLeftRatio - 9;
			var rightWidth = containerWidth - collapseWidth - leftWidth ;
			window.top.$("#sourceLeft").width(leftWidth);
			window.top.$("#sourceRight").width(rightWidth);
			
			window.top.$("#sourceLeft").attr("_width", leftWidth);
			window.top.$("#sourceRight").attr("_width", rightWidth);
			
			window.top.$("#sourceContainer").height(modalHeight - 30);
			window.top.$("#collapse").height(modalHeight - 32);
			window.top.$("#sourceLeft,#sourceRight").height(modalHeight - 30);
			
			//计算按钮位置
			window.top.$("div.toRight").css("margin-top", (window.top.$("#collapse").height() - 44) / 2 + "px").show();
			window.top.$("div.toLeft").css("margin-top", "").show();

			//自定义插件不显示恢复系统参数按钮
			if(modal.find("input[name='customPlugin']").length > 0 && modal.find("input[name='customPlugin']").val() == 1){
				modal.find("button.setSystemDefault").remove();
			}
			
		},
		//modal显示之前的回调事件
		beforeShow: function(modal){
			//添加生成代码按钮
			var generateCodeButton = window.top.$("<button type='button' class='btn btn-primary'>生成代码</button>");
			modal.find("div.modal-footer").prepend(generateCodeButton);
			
			//处理title
			modal.find(".modal-title").text(title + "( " + pluginName + " )");
			
			//生成代码
			generateCodeButton.click(function(){
				$.post(generateCodeUrl,{
					"source" : modal.find("textarea").val(),
					"pluginName" : pluginName,
					"groupName" : groupName,
					"_csrf" : window.top.$("meta[name='_csrf']").attr("content")},
					function(data){
						var message = data;
						if(data && $.isPlainObject(data) && data.errorMessage){
							message = data.errorMessage;
						}
						modal.find("#sourceEditTestResult").text(message);
					});
			});
			
			//添加恢复默认按钮
			var setDefaultButton = window.top.$("<button type='button' class='btn btn-primary'>恢复初始值</button>");
			modal.find("div.modal-footer").prepend(setDefaultButton);
			
			//恢复默认
			setDefaultButton.click(function(){
				$.get(setDefaultUrl,{"pluginName" : pluginName, "groupName" : groupName},function(data){
					var message = data;
					if(data && $.isPlainObject(data) && data.errorMessage){
						message = data.errorMessage;
					}
					modal.find("textarea").val(message);
				});
			});

			//恢复系统插件配置
			//添加恢复系统插件配置按钮
			var setSystemConfigButton = window.top.$("<button type='button' class='btn btn-primary setSystemDefault'>恢复系统插件配置</button>");
			modal.find("div.modal-footer").prepend(setSystemConfigButton);
			
			//恢复默认
			setSystemConfigButton.click(function(){
				$.get(setSystemConfigUrl,{"pluginName" : pluginName, "groupName" : groupName},function(data){
					var message = data;
					if(data && $.isPlainObject(data) && data.errorMessage){
						message = data.errorMessage;
					}
					modal.find("textarea").val(message);
				});
			});
			
		}
	});
}

