$(document).ready(function(){
	$('#pluginTable').bootstrapTable({
		url: "/plugin/findPluginList?plugin.groupName=" + ($("select[name='groupName']").val() ? $("select[name='groupName']").val() : ''),
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}},
			{field: 'groupName',title: '插件组名'},
	    	{field: 'name',title: '插件名称'}, 
	    	{field: 'description',title: '插件描述'}, 
	    	{field: 'dependencies',title: '插件依赖', formatter: function (value, row, index) {
	    			if(!value){
	    				return null;
	    			}
					return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
				}
			},
	    	{field: 'modifyTemplate',title: '修改参数',formatter : function(value, row, index){
	    			return "<input type='button'  value='修改参数' onclick=\"configEdit('修改参数','" + row.groupName + "', '" + row.name + "','/config/edit/plugin/" + row.groupName + "/" + row.name + "?t=" + new Date().getTime() + "','/config/edit/plugin/generateCode','/plugin/savePluginConfig', '/plugin/loadPluginConfig', '/plugin/loadSystemConfig')\"/>";
	        	}
	    	},
	    	{field: 'modifyTemplate',title: '修改模板',formatter : function(value, row, index){
	    			return "<input type='button'  value='修改模板' onclick=\"sourceEdit('修改模板','" + row.groupName + "', '" + row.name + "','/source/edit/plugin/template/" + row.groupName + "/" + row.name + "?t=" + new Date().getTime() + "','/source/edit/plugin/template/generateCode','/plugin/savePluginGeneratorTemplateContent', '/plugin/loadPluginTemplate', '/plugin/loadSystemTemplate')\"/>";
	        	}
	    	},
	    	{field: 'modifySource',title: '修改生成器代码',formatter : function(value, row, index){
	        		return "<input type='button'  value='修改生成器代码' onclick=\"sourceEdit('修改生成器代码','" + row.groupName + "', '" + row.name + "','/source/edit/plugin/source/" + row.groupName + "/" + row.name + "?t=" + new Date().getTime() + "','/source/edit/plugin/source/generateCode','/plugin/savePluginGeneratorSourceContent', '/plugin/loadPluginSource', '/plugin/loadSystemSource')\"/>";
	        	}
	    	},
	    	{field: 'status',title: '禁用',formatter : function(value, row, index){
		        	return value == 0 ? "<input type='button' class='status' _status='" + row.status + "' id='" + row.id + "' value='禁用'/>" : "<input type='button' class='status' _status='" + row.status + "' id='" + row.id + "' value='启用'/>";
		        }
	    	},
	    ],
	    onLoadSuccess: function(rows){
	    	$("table#pluginTable input[type='button'].status").each(function(){
				var button = $(this);
				button.click(function(){
					$("div.error,div.success").hide();
					
					//启用
					if($(this).attr("_status") == 1){
						$.post("/plugin/enablePlugin/" + $(this).attr("id"), {"_csrf" : $("meta[name='_csrf']").attr("content")}, function(data){
							if(data && data.errorMessage){
								$("span.alertMsg").text(data.errorMessage);
								$("div.error,div.alert").show();
							}else{
								$("span.successMsg").text("启用成功");
								$("div.success,div.alert").show();
							}
							$('#pluginTable').bootstrapTable('refresh', {silent: true});
						});
					}else{
						//禁用
						$.post("/plugin/disablePlugin/" + $(this).attr("id"), {"_csrf" : $("meta[name='_csrf']").attr("content")}, function(data){
							if(data && data.errorMessage){
								$("span.alertMsg").text(data.errorMessage);
								$("div.error,div.alert").show();
							}else{
								$("span.successMsg").text("禁用成功");
								$("div.success,div.alert").show();
							}
							$('#pluginTable').bootstrapTable('refresh', {silent: true});
						});
					}
				});
			});
	    	
	    	//鼠标放上去增加提示信息
	    	$("table#pluginTable tbody tr").each(function(index){
	    		var row = rows[index];
				var title = "插件组名：" + row.groupName + "\r\n";
				title = title + "插件名称：" + row.name + "\r\n";
	    		title = title + "插件介绍：" + row.description + "\r\n";
	    		title = title + "插件模板文件路径：" + row.templatePath + "\r\n";
	    		title = title + "插件生成器全路径名：" + row.generator + "\r\n";
	    		title = title + "代码相对目录：\/" + row.fileRelativeDir + "\/"+ row.prefix + "文件名" + row.suffix + row.fileSuffix + "\r\n";
	    		title = title + "插件地址：" + row.pluginPath + "\r\n";
	    		title = title + "依赖的插件：" + row.dependencies + "\r\n";
	    		title = title + "状态：" + (row.status == 0 ? "启用" : "禁用") + "\r\n";
	    		
	    		$(this).attr("title",title);
	    	});
	    }
	});

	$("select[name='groupName']").change(function () {
		var groupName = $(this).val();
		$('#pluginTable').bootstrapTable('refresh', {silent: true, url: "/plugin/findPluginList?plugin.groupName=" + groupName});
	});
	
	//上传插件
	$(".chooseFile").click(function(){
		$("input[type='file'].file").trigger("click");
	});
	$("input[type='file'].file").change(function(){
		var val = $(this).val();
		var fileName = null;
		if(val){
			fileName = val.substring(val.lastIndexOf("\\") + 1);
		}else{
			fileName = "选择文件";
		}
		
		$("input.fileName").attr("placeholder",fileName).attr("title",fileName);
	});
	
	$("button#uploadPlugin").click(function(){
		$("div.success,div.alert").hide();
		//校验是否选择插件文件
		var pluginName = $("input[type='file'].file").val();
		if(!pluginName){
			$("span.alertMsg").text("请先选择插件");
			$("div.error,div.alert").show();
			return;
		}
		//校验插件文件是否以.jar结尾
		if(pluginName.substring(pluginName.length - 4) != '.jar'){
			$("span.alertMsg").text("插件文件名必须以.jar结尾");
			$("div.error,div.alert").show();
			return;
		}
		
		$("div.error,div.alert").hide();
		
		//上传插件
		$("form#uploadPluginForm").ajaxSubmit({
			method: "post",
			url: "/plugin/uploadPlugin",
			data: {"_csrf": $("meta[name='_csrf']").attr("content")},
			success: function(data){
				//上传失败提示原因
				if(data && data.errorMessage){
					$("span.alertMsg").text("上传插件失败：" + data.errorMessage);
					$("div.error,div.alert").show();
				}else{
					if(data){
						$("span.successMsg").text("上传插件成功");
						$("div.success,div.alert").show();
						
						//上传成功提示上传成功并刷新插件列表
						document.getElementById("uploadPluginForm").reset();
						
						$("input.fileName").attr("placeholder","请先选择插件").attr("title","请先选择插件");
						
						//重新加载插件
						$('#pluginTable').bootstrapTable('refresh', {silent: true, url: "/plugin/findPluginList?plugin.groupName=" + ($("select[name='groupName']").val() ? $("select[name='groupName']").val() : '')});
					}else{
						$("span.alertMsg").text("上传插件失败");
						$("div.error,div.alert").show();
					}
				}
			},
			error: function(context){
				//上传失败提示原因
				$("span.alertMsg").text("上传插件失败：" + context.responseJSON.message);
				$("div.error,div.alert").show();
			}
		});
	});

	$("div.error a.close").click(function () {
		$("div.error").hide();
	});
	$("div.success a.close").click(function () {
		$("div.success").hide();
	});
});