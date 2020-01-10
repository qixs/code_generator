$(document).ready(function(){
	
	//插件表
	$('#pluginTable').bootstrapTable({
		url: '/user/plugin/allocation/findList/{userId}',
		height: $(window).height(),
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}}, 
	    	{field: 'name',title: '插件名称'}, 
	    	{field: 'description',title: '插件描述'}, 
	    	{field: 'dependencies',title: '插件依赖'},
	    	{field: 'status',title: '禁用',formatter : function(value, row, index){
		        	return value == 0 ? "<input type='button' _status='" + row.status + "' id='" + row.id + "' value='禁用'/>" : "<input type='button' _status='" + row.status + "' id='" + row.id + "' value='启用'/>";
		        }
	    	},
	    ],
	    onLoadSuccess: function(rows){
	    	//鼠标放上去增加提示信息
	    	$("table#pluginTable tbody tr").each(function(index){
	    		var row = rows[index];
	    		var title = "插件名称：" + row.name + "\r\n";
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
	
	$("div.error a").click(function(){
		//重新渲染表格
		$('#pluginTable').bootstrapTable('resetView', {height : $("body").height()});
		$(".error").hide();
	});
	$("div.success a").click(function(){
		//重新渲染表格
		$('#pluginTable').bootstrapTable('resetView', {height : $("body").height()});
		$(".success").hide();
	});
});
