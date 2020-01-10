$(document).ready(function(){
	var columns = new Array();
	columns[columns.length] = {field: '',title: '序号',formatter : function(value, row, index){return index + 1;}};
	columns[columns.length] = { checkbox: true };
	columns[columns.length] = {field: 'groupName',title: '插件组名', sortable: true },
	columns[columns.length] = {field: 'name',title: '插件名称', sortable: true, formatter: function(value){
			return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
		}
	};
	columns[columns.length] = {field: 'description',title: '插件描述', sortable: true, formatter: function(value){
			var width = $("input[name='enableUserCustomPlugin']").val() == '1' ? "80px" : "230px";
			return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:" + width + ";display: block;' title='" + value + "'>" + value + "</span>";
		}
	};
	columns[columns.length] = {field: 'dependencies',title: '插件依赖', sortable: true, formatter: function(value){
			if(!value){
				return null;
			}
			var width = $("input[name='enableUserCustomPlugin']").val() == '1' ? "100px" : "250px";
			return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:" + width + ";display: block;' title='" + value + "'>" + value + "</span>";
		}
	};
	if($("input[name='enableUserCustomPlugin']").val() == '1'){
		columns[columns.length] = {field: 'modifyTemplate',title: '修改',formatter : function(value, row, index){
				return "<input type='button'  value='修改' onclick=\"customPluginEdit('" + row.id + "', '修改插件')\"/>";
			}
		};
		columns[columns.length] = {field: 'modifyTemplate',title: '修改模板',formatter : function(value, row, index){
				return "<input type='button'  value='修改模板' onclick=\"sourceEdit('修改模板','" + row.groupName + "', '" + row.name + "','/source/edit/user/plugin/template/" + row.groupName + "/" + row.name + "?t=" + new Date().getTime() + "','/source/edit/user/plugin/template/generateCode','/user/plugin/savePluginGeneratorTemplateContent', '/user/plugin/loadDefaultTemplate', '/user/plugin/loadSystemTemplate')\"/>";
			}
		};
		columns[columns.length] = {field: 'modifySource',title: '修改生成器代码',formatter : function(value, row, index){
				return "<input type='button'  value='修改生成器代码' onclick=\"sourceEdit('修改生成器代码','" + row.groupName + "', '" + row.name + "','/source/edit/user/plugin/source/" + row.groupName + "/" + row.name + "?t=" + new Date().getTime() + "','/source/edit/user/plugin/source/generateCode','/user/plugin/savePluginGeneratorSourceContent', '/user/plugin/loadDefaultSource', '/user/plugin/loadSystemSource')\"/>";
			}
		};
	}

	columns[columns.length] = {field: 'status',title: '禁用',formatter : function(value, row, index){
			return value == 0 ? "<input type='button' class='setStatus' _status='" + row.status + "' id='" + row.id + "' value='禁用'/>" : "<input type='button' class='setStatus' _status='" + row.status + "' id='" + row.id + "' value='启用'/>";
		}
	};

	$('#userPluginTable').bootstrapTable({
		url: '/user/plugin/findList',
		height: $(window).height(),
		dataField: "content",
		idField: 'id',
		pagination: true,
		totalField: "totalElements",
		singleSelect: true,
		clickToSelect: true,
		showRefresh: true,
		search: true,
		searchOnEnterKey: true,
		sidePagination: 'server',
		pageNumber: 1,
		pageList: [10, 25, 50, 100],
	    columns: columns,
	    onLoadSuccess: function(rows){
			//允许只定义插件且页面未初始化按钮
	    	if($("div.fixed-table-toolbar button.btn-primary").length <= 0 && $("input[name='enableUserCustomPlugin']").val() == '1'){
	    		//添加删除按钮
		    	var setDefaultButton = $("<button class='btn btn-primary' style='margin-top: 10px;margin-left: 10px;'>删除</button>");
		    	$("div.fixed-table-toolbar").prepend(setDefaultButton);
		    	setDefaultButton.click(function(){
					$(".error,.success").hide();
					$('#userPluginTable').bootstrapTable('resetView', {height : $("body").height()});

		    		var selectRows = $('#userPluginTable').bootstrapTable('getSelections');
		    		if(selectRows.length == 0){
		    			$.alert("请先选择用户插件");
		    			return;
		    		}
		    		$.confirm("该操作不可逆，确定删除“" + selectRows[0].name + "”插件？", function(flag){
		    			if(flag){
		    				$.post("/user/plugin/" + selectRows[0].id, 
				    				{"_csrf": window.top.$("meta[name='_csrf']").attr("content"), "_method": "DELETE"}, 
				    		function(data){
		    					if(data && data.errorMessage){
									$("span.alertMsg").text(data.errorMessage);
									$("div.error,div.alert").show();
								}else{
									$("span.successMsg").text("删除成功");
									$("div.success,div.alert").show();
								}
								$('#userPluginTable').bootstrapTable('refresh', {silent: true});

								$('#userPluginTable').bootstrapTable('resetView', {height : $("body").height() - 20 -
										($(".success").is(":visible") ? $(".success").height() : 0) -
										($(".error").is(":visible") ? $(".error").height() : 0)});
				    		});
		    			}
		    		});
		    	});
		    	
	    		//添加恢复为初始值按钮
		    	var setDefaultButton = $("<button class='btn btn-primary' style='margin-top: 10px;margin-left: 10px;'>恢复为初始值</button>");
		    	$("div.fixed-table-toolbar").prepend(setDefaultButton);
		    	setDefaultButton.click(function(){
					$(".error,.success").hide();
					$('#userPluginTable').bootstrapTable('resetView', {height : $("body").height()});

		    		var selectRows = $('#userPluginTable').bootstrapTable('getSelections');
		    		if(selectRows.length == 0){
		    			$.alert("请先选择用户插件");
		    			return;
		    		}
		    		$.confirm("该操作不可逆，将恢复“" + selectRows[0].name + "”插件所有属性为初始值，确定恢复为初始值？", function(flag){
		    			if(flag){
		    				$.post("/user/plugin/setDefault/" + selectRows[0].id, 
				    				{"_csrf": window.top.$("meta[name='_csrf']").attr("content")}, function(data){
		    					if(data && data.errorMessage){
									$("span.alertMsg").text(data.errorMessage);
									$("div.error,div.alert").show();
								}else{
									$("span.successMsg").text("恢复默认成功");
									$("div.success,div.alert").show();
								}
								$('#userPluginTable').bootstrapTable('refresh', {silent: true});

								$('#userPluginTable').bootstrapTable('resetView', {height : $("body").height() - 20 -
										($(".success").is(":visible") ? $(".success").height() : 0) -
										($(".error").is(":visible") ? $(".error").height() : 0)});
				    		});
		    			}
		    		});
		    	});
		    	
	    		
		    	//添加新增插件按钮
		    	var newPluginButton = $("<button class='btn btn-primary' style='margin-top: 10px;'>新增插件</button>");
		    	$("div.fixed-table-toolbar").prepend(newPluginButton);
		    	
		    	newPluginButton.click(function(){
		    		customPluginEdit("", "新增插件");
		    	});
	    	}
	    	
	    	$("table#userPluginTable input[type='button'].setStatus").each(function(){
				var button = $(this);
				button.click(function(){
					$(".error,.success").hide();
					$('#userPluginTable').bootstrapTable('resetView', {height : $("body").height()});
					
					//启用
					if($(this).attr("_status") == 1){
						$.post("/user/plugin/enablePlugin/" + $(this).attr("id"), {"_csrf" : window.top.$("meta[name='_csrf']").attr("content")}, function(data){
							if(data && data.errorMessage){
								$("span.alertMsg").text(data.errorMessage);
								$("div.error,div.alert").show();
							}else{
								$("span.successMsg").text("启用成功");
								$("div.success,div.alert").show();
							}
							$('#userPluginTable').bootstrapTable('refresh', {silent: true});

							$('#userPluginTable').bootstrapTable('resetView', {height : $("body").height() - 20 -
									($(".success").is(":visible") ? $(".success").height() : 0) -
									($(".error").is(":visible") ? $(".error").height() : 0)});
						});
					}else{
						//禁用
						$.post("/user/plugin/disablePlugin/" + $(this).attr("id"), {"_csrf" : window.top.$("meta[name='_csrf']").attr("content")}, function(data){
							if(data && data.errorMessage){
								$("span.alertMsg").text(data.errorMessage);
								$("div.error,div.alert").show();
							}else{
								$("span.successMsg").text("禁用成功");
								$("div.success,div.alert").show();
							}
							$('#userPluginTable').bootstrapTable('refresh', {silent: true});

							$('#userPluginTable').bootstrapTable('resetView', {height : $("body").height() - 20 -
									($(".success").is(":visible") ? $(".success").height() : 0) -
									($(".error").is(":visible") ? $(".error").height() : 0)});
						});
					}
				});
			});
	    	
	    	//鼠标放上去增加提示信息
	    	$("table#userPluginTable tbody tr").each(function(index){
	    		if(rows && rows.content && rows.content[index]){
	    			var row = rows.content[index];
					var title = "插件组名：" + row.groupName + "\r\n";
					title = title + "插件名称：" + row.name + "\r\n";
		    		title = title + "插件介绍：" + row.description + "\r\n";
		    		title = title + "插件模板文件路径：" + row.templatePath + "\r\n";
		    		title = title + "插件生成器全路径名：" + row.generator + "\r\n";
		    		title = title + "代码相对目录：\/" + row.fileRelativeDir + "\/"+ row.prefix + "文件名" + row.suffix + row.fileSuffix + "\r\n";
		    		title = title + "插件地址：" + row.pluginPath + "\r\n";
		    		title = title + "依赖的插件：" + (row.dependencies ? row.dependencies : '') + "\r\n";
		    		title = title + "状态：" + (row.status == 0 ? "启用" : "禁用") + "\r\n";
		    		title = title + "是否是自定义插件：" + (row.custom == 0 ? "否" : "是") + "\r\n";
		    		
		    		$(this).attr("title",title);
	    		}
	    	});
	    }
	});

	$(".error a.close").click(function () {
		$(".error").hide();
		$('#userPluginTable').bootstrapTable('resetView', {height : $("body").height()});
	});
	$(".success a.close").click(function () {
		$(".success").hide();
		$('#userPluginTable').bootstrapTable('resetView', {height : $("body").height()});
	});
});