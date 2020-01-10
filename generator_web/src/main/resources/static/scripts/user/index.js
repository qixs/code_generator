$(document).ready(function(){
	$('#userTable').bootstrapTable({
		url: '/user/getList',
		height: $("body .bootstrapTable").height(),
		dataField: "content",
		pagination: true,
		totalField: "totalElements",
		singleSelect: true,
		clickToSelect: true,
		showRefresh: true,
		search: true,
		searchOnEnterKey: true,
		sidePagination: 'server',
		sortName: 'updateDate',
		sortOrder: 'desc',
		pageNumber: 1,
		pageList: [10, 25, 50, 100],
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}}, 
	    	{field: 'username',title: '用户名', sortable: true}, 
	    	{field: 'name',title: '姓名', sortable: true}, 
	    	{field: 'admin',title: '管理员', sortable: true,formatter : function(value, row, index){
    			return value == 1 ? '是' : '否';
        	}},
	    	{field: 'updateDate',title: '最后更新时间', sortable: true},
	    	{field: '',title: '修改',formatter : function(value, row, index){
	    			return "<input type='button' id='" + row.id + "' value='修改' class='modify'/>";
	        	}
	    	},
	    	{field: '',title: '重设密码',formatter : function(value, row, index){
	    			//如果用户id是当前登录用户则不显示
	    			if(row.id == $("#currentUserId").val()){
	    				return "";
	    			}
	    			return "<input type='button' id='" + row.id + "' value='重设密码' class='password'/>";
	        	}
	    	},
	    	{field: '',title: '分配插件',formatter : function(value, row, index){
		    		//如果用户id是当前登录用户则不显示
	    			if(row.id == $("#currentUserId").val()){
	    				return "";
	    			}
	    			return "<input type='button' id='" + row.id + "' value='分配插件' class='allocationPlugin'/>";
	        	}
	    	},
	    	{field: 'status',title: '禁用',formatter : function(value, row, index){
		    		//如果用户id是当前登录用户则不显示
	    			if(row.id == $("#currentUserId").val()){
	    				return "";
	    			}
		        	return value == 0 ? "<input type='button' class='status' _status='" + row.status + "' id='" + row.id + "' value='禁用'/>" : "<input type='button' class='status' _status='" + row.status + "' id='" + row.id + "' value='启用'/>";
		        }
	    	},
	    ],
	    onLoadSuccess: function(rows){
	    	if($("button.addUser").length <= 0){
	    		var b = $("<button type='button' style='margin-top: 10px;margin-left: 10px;' class='btn btn-default addUser'>添加用户</button>");
		    	//添加按钮
		    	$("div.fixed-table-toolbar").prepend(b);
		    	
		    	b.click(function(){
		    		$("div.error,div.success").hide();
					$("div.error a,div.success a").trigger("click");
					
	    			$.modal({
		    			title: "添加用户",
		    			body: function(){
		    				var body = null;
		    				$.ajax({
		    					type: "get", 
		    				    url: "/user/editNew", 
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
		    					var params = modal.find("#userForm").serializeObject();
		    					if(!params["user.username"]){
		    						$.alert("用户名不能为空");
		    						return false;
		    					}
		    					if(!params["user.name"]){
		    						$.alert("姓名不能为空");
		    						return false;
		    					}
		    					if(!params["user.password"]){
		    						$.alert("密码不能为空");
		    						return false;
		    					}
		    					if(!params["passwordRepeat"]){
		    						$.alert("重复密码不能为空");
		    						return false;
		    					}
		    					if(params["user.password"] != params["passwordRepeat"]){
		    						$.alert("密码和重复密码必须一致");
		    						return false;
		    					}
		    					
		    					var f = true;
		    					$.ajax({
		    						type: "post", 
		    					    url: "/user", 
		    					    data: params,
		    					    cache:false, 
		    					    async:false, 
		    					    success: function(data){ 
		    					    	if(data && $.isPlainObject(data) && data.errorMessage){
		    								$.alert("保存失败：" + data.errorMessage);
		    								f = false;
		    							}else{
		    								$("div.success").show();
		    								$("span.successMsg").text("保存成功，已经发送激活邮件，请提醒“" + params["user.name"] + "”查收");
		    								//重新渲染表格
		    								$('#userTable').bootstrapTable('resetView', {height : $("body").height() - $("body .success").height() - 20});
		    								
		    								//重新刷新列表
		    								$('#userTable').bootstrapTable('refresh', {silent: true});
		    							}
		    					    } 
		    					});
		    					
		    					return f;
		    				}
		    			}
	    			});
		    	});
	    	}
	    	
	    	//修改
	    	$("table#userTable input[type='button'].modify").each(function(){
	    		var button = $(this);
	    		button.click(function(){
	    			$("div.error,div.success").hide();
					$("div.error a,div.success a").trigger("click");
					
	    			$.modal({
		    			title: "修改用户信息",
		    			body: function(){
		    				var body = null;
		    				$.ajax({
		    					type: "get", 
		    				    url: "/user/edit/" + button.attr("id"), 
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
		    					var params = modal.find("#userForm").serializeObject();
		    					if(!params["user.username"]){
		    						$.alert("用户名不能为空");
		    						return false;
		    					}
		    					if(!params["user.name"]){
		    						$.alert("姓名不能为空");
		    						return false;
		    					}
		    					
		    					var f = true;
		    					$.ajax({
		    						type: "post", 
		    					    url: "/user", 
		    					    data: params,
		    					    cache:false, 
		    					    async:false, 
		    					    success: function(data){ 
		    					    	if(data && $.isPlainObject(data) && data.errorMessage){
		    								$.alert("保存失败：" + data.errorMessage);
		    								f = false;
		    							}else{
		    								$("div.success").show();
		    								$("span.successMsg").text("保存成功");
		    								//重新渲染表格
		    								$('#userTable').bootstrapTable('resetView', {height : $("body").height() - $("body .success").height() - 20});
		    								
		    								//重新刷新列表
		    								$('#userTable').bootstrapTable('refresh', {silent: true});
		    							}
		    					    } 
		    					});
		    					
		    					return f;
		    				}
		    			}
	    			});
	    		});
	    	});
	    	//重设密码
	    	$("table#userTable input[type='button'].password").each(function(){
	    		var button = $(this);
	    		button.click(function(){
	    			$("div.error,div.success").hide();
					$("div.error a,div.success a").trigger("click");
					
	    			$.modal({
	    				title: "重设密码",
	    				okButtonTitle: '确定',
	    				body: function(){
	    					var body = null;
	    					$.ajax({
	    						type: "get", 
	    					    url: "/user/password/" + button.attr("id"), 
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
	    						var params = modal.find("#passwordForm").serializeObject();
	    						
	    						//新密码不能为空
	    						if(!params["newPassword"]){
	    							$.alert("密码不能为空");
	    							return false;
	    						}
	    						//密码在8位到18位之间
	    						if(params["newPassword"].length < 8 || params["newPassword"].length > 18){
	    							$.alert("密码长度必须在8位到18位之间");
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
	    						    url: "/user/resetPassword", 
	    						    data: params,
	    						    cache:false, 
	    						    async:false, 
	    						    success: function(data){ 
	    						    	if(data && $.isPlainObject(data) && data.errorMessage){
	    									$.alert("修改密码失败：" + data.errorMessage);
	    									f = false;
	    								}else{
	    									$("div.success").show();
		    								$("span.successMsg").text("修改密码成功");
		    								//重新渲染表格
		    								$('#userTable').bootstrapTable('resetView', {height : $("body").height() - $("body .success").height() - 20});
		    								
		    								//重新刷新列表
		    								$('#userTable').bootstrapTable('refresh', {silent: true});
	    								}
	    						    } 
	    						});
	    						
	    						return f;
	    					}
	    				},
	    			});
	    		});
	    	});
	    	
	    	//分配插件
	    	$("table#userTable input[type='button'].allocationPlugin").each(function(){
	    		var button = $(this);
	    		button.click(function(){
	    			$("div.error,div.success").hide();
					$("div.error a,div.success a").trigger("click");
					
	    			$.modal({
	    				title: "分配插件",
	    				cancelButtonTitle: '关闭',
	    				height: 440,
	    				width: 800,
	    				body: function(){
	    					var body = null;
	    					$.ajax({
	    						type: "get", 
	    					    url: "/user/plugin/allocation/index", 
	    					    cache:false, 
	    					    async:false, 
	    					    success: function(data){ 
	    					    	body = data;
	    					    } 
	    					});
	    					return body;
	    				},
	    				//modal显示之前的回调事件
	    				beforeShow: function(modal){
	    					modal.find("button.ok").remove();
	    				},
	    				//modal显示之后执行的回调方法
	    				modalVisibleCallback: function(modal){
	    					var tableHeight = 390;
	    					//插件表
	    					modal.find('#pluginTable').bootstrapTable({
	    						url: '/user/plugin/allocation/findList/' + button.attr("id"),
	    						height: tableHeight,
	    					    columns: [
	    					    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}}, 
	    					    	{field: 'name',title: '插件名称'}, 
	    					    	{field: 'description',title: '插件描述'}, 
	    					    	{field: 'dependencies',title: '插件依赖'},
	    					    	{field: 'allocationStatus',title: '插件已分配',formatter : function(value, row, index){
	    						        	return "<input type='checkbox' _allocationStatus='" + value + "' name='" + row.name + "' " + (value == 1 ? 'checked' : '') + "/>";
	    						        }
	    					    	},
	    					    ],
	    					    onLoadSuccess: function(rows){
	    					    	modal.find("table#pluginTable input[type='checkbox']").each(function(){
	    								var checkbox = $(this);
	    								checkbox.click(function(){
	    									modal.find("div.error,div.success").hide();
	    									modal.find('#pluginTable').bootstrapTable('resetView', {height : tableHeight});
	    									
	    									if($(this).attr("_allocationStatus") == 1){
	    										//取消分配
	    										$.post("/user/plugin/allocation/recovery/" + button.attr("id") + "/" + checkbox.attr("name"),
	    												{"_csrf": window.top.$("meta[name='_csrf']").attr("content")}, 
	    												function(data){
			    											if(data && data.errorMessage){
			    												modal.find("span.alertMsg").text(data.errorMessage);
			    												modal.find("div.error,div.alert").show();
			    											}else{
			    												modal.find("span.successMsg").text("取消分配成功");
			    												modal.find("div.success,div.alert").show();
			    											}
			    											//重新刷新列表
			    											modal.find('#pluginTable').bootstrapTable('refresh', {silent: true});
			    											
			    											modal.find('#pluginTable').bootstrapTable('resetView', {height : tableHeight - 72});
	    										});
	    									}else{
	    										//分配
	    										$.post("/user/plugin/allocation/allocation/" + button.attr("id") + "/" + checkbox.attr("name"),
	    												{"_csrf": window.top.$("meta[name='_csrf']").attr("content")}, 
	    												function(data){
			    											if(data && data.errorMessage){
			    												modal.find("span.alertMsg").text(data.errorMessage);
			    												modal.find("div.error,div.alert").show();
			    											}else{
			    												modal.find("span.successMsg").text("分配成功");
			    												modal.find("div.success,div.alert").show();
			    											}
			    											//重新刷新列表
			    											modal.find('#pluginTable').bootstrapTable('refresh', {silent: true});
			    											modal.find('#pluginTable').bootstrapTable('resetView', {height : tableHeight - 72});
	    										});
	    									}
	    								});
	    							});
	    					    	
	    					    	//鼠标放上去增加提示信息
	    					    	modal.find("table#pluginTable tbody tr").each(function(index){
	    					    		var row = rows[index];
	    					    		var title = "插件名称：" + row.name + "\r\n";
	    					    		title = title + "插件介绍：" + row.description + "\r\n";
	    					    		title = title + "插件模板文件路径：" + row.templatePath + "\r\n";
	    					    		title = title + "插件生成器全路径名：" + row.generator + "\r\n";
	    					    		title = title + "代码相对目录：\/" + row.fileRelativeDir + "\/"+ row.prefix + "文件名" + row.suffix + row.fileSuffix + "\r\n";
	    					    		title = title + "插件地址：" + row.pluginPath + "\r\n";
	    					    		title = title + "依赖的插件：" + row.dependencies + "\r\n";
	    					    		
	    					    		$(this).attr("title",title);
	    					    	});
	    					    }
	    					});
	    					
	    					modal.find("div.error a").click(function(){
	    						//重新渲染表格
	    						modal.find('#pluginTable').bootstrapTable('resetView', {height : tableHeight});
	    						modal.find(".error").hide();
	    					});
	    					modal.find("div.success a").click(function(){
	    						//重新渲染表格
	    						modal.find('#pluginTable').bootstrapTable('resetView', {height : tableHeight});
	    						modal.find(".success").hide();
	    					});
	    				}
	    			});
	    		});
	    	});
	    	
	    	//启用禁用
	    	$("table#userTable input[type='button'].status").each(function(){
				var button = $(this);
				button.click(function(){
					$("div.error,div.success").hide();
					$("div.error a,div.success a").trigger("click");
					
					//启用
					if($(this).attr("_status") == 1){
						$.post("/user/enable/" + $(this).attr("id"), {"_csrf" : window.top.$("meta[name='_csrf']").attr("content")}, function(data){
							if(data && data.errorMessage){
								$("span.alertMsg").text(data.errorMessage);
								$("div.error,div.alert").show();
								//重新渲染表格
								$('#userTable').bootstrapTable('resetView', {height : $("body").height() - $("body .error").height() - 20});
							}else{
								$("span.successMsg").text("启用成功");
								$("div.success,div.alert").show();
								//重新渲染表格
								$('#userTable').bootstrapTable('resetView', {height : $("body").height() - $("body .success").height() - 20});
							}
							$('#userTable').bootstrapTable('refresh', {silent: true});
						});
					}else{
						//禁用
						$.post("/user/disable/" + $(this).attr("id"), {"_csrf" : window.top.$("meta[name='_csrf']").attr("content")}, function(data){
							if(data && data.errorMessage){
								$("span.alertMsg").text(data.errorMessage);
								$("div.error,div.alert").show();
								//重新渲染表格
								$('#userTable').bootstrapTable('resetView', {height : $("body").height() - $("body .error").height() - 20});
							}else{
								$("span.successMsg").text("禁用成功");
								$("div.success,div.alert").show();
								//重新渲染表格
								$('#userTable').bootstrapTable('resetView', {height : $("body").height() - $("body .success").height() - 20});
							}
							
							$('#userTable').bootstrapTable('refresh', {silent: true});
						});
					}
				});
			});
	    }
	});
	
	$("div.error a").click(function(){
		//重新渲染表格
		$('#userTable').bootstrapTable('resetView', {height : $("body").height()});
		$(".error").hide();
	});
	$("div.success a").click(function(){
		//重新渲染表格
		$('#userTable').bootstrapTable('resetView', {height : $("body").height()});
		$(".success").hide();
	});
});
