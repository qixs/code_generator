/**
 * 自定义插件编辑页面公用方法
 * @param userPluginId 用户插件id
 * **/
function customPluginEdit(userPluginId, title){
	//计算浏览器窗口大小
	var windowWidth = $(window.top).width();
	var windowHeight = $(window.top).height();
	var modalWidth = windowWidth - 100;
	var modalHeight = windowHeight - 180;
	
	//步数
	var stepUrls = ["/user/custom/plugin/temp/config", "/user/custom/plugin/temp/template", "/user/custom/plugin/temp/source"];
	var stepTitles = ["插件配置项", "模板源码", "生成器源码"];
	var stepNum = 1;
	

	//根据步骤号加载左侧页面
	var loadByStep = function(stepNum, modal){
		modal.find(".modal-title").text(title + "(" + stepTitles[stepNum - 1] + ")");
		var stepUrl = stepUrls[stepNum - 1];
		
		modal.find("#sourceLeft div").load(stepUrl + "?id=" + modal.find("#customPluginForm input[name='userCustomPluginTemp.id']").val());
	};
	
	//清除页面所有modal,防止页面渲染出错
	window.top.$("div.modal,div.modal-dialog").remove();
	
	//弹出新增modal
	$.modal({
		title: title,
		height: modalHeight,
		width: modalWidth,
		callback: function(modal,flag){
			if(flag){
				//保存到插件表
				var f = true;
				$.ajax({
					type: "post", 
				    url: "/user/custom/plugin/temp/savePlugin", 
				    data: modal.find("#customPluginForm").serialize(),
				    cache:false, 
				    async:false, 
				    success: function(data){ 
				    	if(data && $.isPlainObject(data) && data.errorMessage){
							$.alert("保存失败：" + data.errorMessage);
							f = false;
						}else{
							$.alert("保存成功");
							
							//重新刷新表格
							$('#userPluginTable').bootstrapTable("refresh");
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
			    url: "/user/custom/plugin/temp/index?userPluginId=" + userPluginId, 
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
			var collapseWidth = modal.find("#collapse").width();
			
			var containerWidth = modalWidth - 34;
			
			var leftWidth = (containerWidth - collapseWidth) * sourceLeftRatio - 9;
			var rightWidth = containerWidth - collapseWidth - leftWidth ;
			modal.find("#sourceLeft").width(leftWidth);
			modal.find("#sourceRight").width(rightWidth);
			
			modal.find("#sourceLeft").attr("_width", leftWidth);
			modal.find("#sourceRight").attr("_width", rightWidth);
			
			modal.find("#sourceContainer").height(modalHeight - 30);
			modal.find("#collapse").height(modalHeight - 32);
			modal.find("#sourceLeft,#sourceRight").height(modalHeight - 30);
			
			//计算按钮位置
			modal.find("div.toRight").css("margin-top", (modal.find("#collapse").height() - 44) / 2 + "px").show();
			modal.find("div.toLeft").css("margin-top", "").show();
			

			//页面显示之前加载配置页
			loadByStep(stepNum, modal);
		},
		//modal显示之前的回调事件
		beforeShow: function(modal){
			//添加生成代码按钮
			var generateCodeButton = window.top.$("<button type='button' class='btn btn-primary'>生成代码</button>");
			modal.find("div.modal-footer").prepend(generateCodeButton);
			
			//生成代码
			generateCodeButton.click(function(){
				//先保存配置
				var params = modal.find("#customPluginForm").serialize();
				$.post("/user/custom/plugin/temp/save",params, function(d){
					if(d && d.errorMessage){
						$.alert("保存失败：" + d.errorMessage);
					}else{
						$.post("/user/custom/plugin/temp/generateCode", params, function(data){
							var message = data;
							if(data && $.isPlainObject(data) && data.errorMessage){
								message = data.errorMessage;
							}
							modal.find("#sourceEditTestResult").text(message);
						});
					}
				});
			});
			
			//保存左侧内容
			var save = function(){
				var params = modal.find("#customPluginForm").serializeObject();
				//校验
				if(stepNum == 1){
					//插件组名不能为空
					if(!params["userCustomPluginTemp.groupName"]){
						$.alert("插件组名不能为空",function(){
							modal.find("input[name='userCustomPluginTemp.groupName']").focus();
						});
						return;
					}
					//插件名不能为空
					if(!params["userCustomPluginTemp.name"]){
						$.alert("插件名不能为空",function(){
							modal.find("input[name='userCustomPluginTemp.name']").focus();
						});
						return;
					}
					//插件描述不能为空
					if(!params["userCustomPluginTemp.description"]){
						$.alert("插件描述不能为空");
						return;
					}
					//生成器全路径不能为空
					if(!params["userCustomPluginTemp.generator"]){
						$.alert("生成器全路径不能为空");
						return;
					}
					//代码相对目录不能为空
					if(!params["userCustomPluginTemp.fileRelativeDir"]){
						$.alert("代码相对目录不能为空");
						return;
					}
				}else if(stepNum == 2){
					//模板源码不能为空
					if(!params["userCustomPluginTemp.templateContent"]){
						$.alert("模板源码不能为空");
						return;
					}
				}else if(stepNum == 3){
					//生成器源码不能为空
					if(!params["userCustomPluginTemp.generatorSourceContent"]){
						$.alert("生成器源码不能为空");
						return;
					}
				}
				
				var flag = true;
				$.ajax({
					type: "post", 
					data: params,
				    url: "/user/custom/plugin/temp/save", 
				    cache:false, 
				    async:false, 
				    success: function(data){ 
				    	if(data && data.errorMessage){
							$.alert("保存失败：" + data.errorMessage);
							flag = false;
						}
				    },
				    error: function(){
				    	$.alert("保存失败");
				    }
				});
				return flag;
			};
			
			//添加下一步按钮
			var nextButton = window.top.$("<button type='button' class='btn btn-primary next'>下一步</button>");
			modal.find("div.modal-footer").prepend(nextButton);
			nextButton.click(function(){
				//保存
				var flag = save();
				if(!flag){
					return;
				}
				
				modal.find("button.pre").show();
				
				stepNum = stepNum + 1;
				
				//根据步骤号加载左侧页面
				loadByStep(stepNum, modal);
				//最后一步
				if(stepNum == stepUrls.length){
					modal.find("button.next").hide();
				}
			});
			
			//添加上一步按钮
			var preButton = window.top.$("<button type='button' class='btn btn-primary pre'>上一步</button>");
			modal.find("div.modal-footer").prepend(preButton);
			preButton.hide();
			preButton.click(function(){
				//保存
				var flag = save();
				if(!flag){
					return;
				}
				
				modal.find("button.show").hide();
				modal.find("button.next").show();
				stepNum = stepNum - 1;
				
				//根据步骤号加载左侧页面
				loadByStep(stepNum, modal);
				//第一步
				if(stepNum == 1){
					modal.find("button.pre").hide();
				}
			});
		}
	});
}