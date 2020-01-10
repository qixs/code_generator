$(document).ready(function(){
	$("a.close").click(function(){
		$(".error,.success").hide();
	});
	$('#geetestTable').bootstrapTable({
		url: '/config/geetest/findList',
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}}, 
	    	{field: 'id',title: 'ID'}, 
	    	{field: 'key',title: 'KEY'}, 
	    	{field: 'weight',title: '权重'},
	    	{field: 'id',title: '操作',formatter : function(value, row, index){
	    		return "<button type=\"button\" class=\"delete smallButtonMouseOut\" onmousemove=\"this.className='smallButtonMouseOver'\" onmouseout=\"this.className='smallButtonMouseOut'\" >删除</button>";
	    	}},
	    ],
		onLoadSuccess: function(rows){
			//删除
			$("#geetestTable button.delete").each(function(index){
				$(this).click(function(){
					$("div.success,div.alert,div.error,div.alert").hide();
					
					$.confirm("确定要删除该配置信息？<br/>ID：" + rows[index].id + " <br/>KEY：" + rows[index].key + "<br/>权重：" + rows[index].weight,function(flag){
						if(flag){
							$.post("/init/wizard/deleteGeetest/" + rows[index].id,function(data){
								if(data && data.errorMessage){
									$("span.alertMsg").text("删除Geetest配置失败：" + data.errorMessage);
									$("div.error,div.alert").show();
								}else{
									$("span.successMsg").text("删除Geetest配置成功");
									$("div.success,div.alert").show();
								}
								//重新加载插件
								$('#geetestTable').bootstrapTable('refresh', {silent: true});
							});
						}
					});
				});
			});
		}
	});
	
	//添加geetest配置信息
	$("#geetestForm button#add").click(function(){
		$("div.success,div.alert").hide();
		//校验是否添加id
		var id = $("form#geetestForm input[name='geetest.id']").val();
		if(!id){
			$("span.alertMsg").text("请先录入ID");
			$("div.error,div.alert").show();
			$("form#geetestForm input[name='geetest.id']").focus();
			return;
		}
		//校验是否添加key
		var key = $("form#geetestForm input[name='geetest.key']").val();
		if(!key){
			$("span.alertMsg").text("请先录入KEY");
			$("div.error,div.alert").show();
			$("form#geetestForm input[name='geetest.key']").focus();
			return;
		}
		//校验是否添加权重
		var weight = $("form#geetestForm input[name='geetest.weight']").val();
		if(!weight){
			$("span.alertMsg").text("请先录入权重");
			$("div.error,div.alert").show();
			$("form#geetestForm input[name='geetest.weight']").focus();
			return;
		}
		//权重是否为正整数
		var reg = new RegExp(regexp.constants.integer);
		if(!reg.test(weight)){
			$("span.alertMsg").text("权重必须为正整数");
			$("div.error,div.alert").show();
			$("form#geetestForm input[name='geetest.weight']").focus();
			return;
		}
		
		$("div.error,div.alert").hide();
		
		$.post("/init/wizard/addGeetest",$("form#geetestForm").serialize(),function(data){
			//上传失败提示原因
			if(data && data.errorMessage){
				$("span.alertMsg").text("添加Geetest配置失败：" + data.errorMessage);
				$("div.error,div.alert").show();
			}else{
				if(data){
					$("span.successMsg").text("添加Geetest配置成功");
					$("div.success,div.alert").show();
					
					//上传成功提示上传成功并刷新插件列表
					document.getElementById("geetestForm").reset();
					
					//重新加载插件
					$('#geetestTable').bootstrapTable('refresh', {silent: true});
				}else{
					$("span.alertMsg").text("添加Geetest配置失败");
					$("div.error,div.alert").show();
				}
			}
		});
	});
});
