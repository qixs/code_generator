
$(document).ready(function(){
	
	//数据库类型切换
	$("select[name='database.type']").change(function(){
		var value = $(this).val();
		var option = $(this).find("option[value='" + value + "']");
		$("input[name='database.driver']").val(option.attr("_driver"));
		$("input[name='database.port']").val(option.attr("_port"));
		$("input[name='database.username']").val(option.attr("_username"));
		$("input[name='database.password']").val("");
		
		reloadDatabases();
		reloadConnectionUrl();
	});
	
	$("input[name='database.url']").change(function(){
		$(this).parent().parent().removeClass("has-warning");
		if(!$(this).val()){
			$(this).parent().parent().addClass("has-warning");
		}
		
		reloadDatabases();
		reloadConnectionUrl();
	});
	$("input[name='database.port']").change(function(){
		$(this).parent().parent().removeClass("has-warning");
		var port = $(this).val();
		if(!port){
			$(this).parent().parent().addClass("has-warning");
			return;
		}
		
		//端口号必须为正整数且必须在1-65535之间
		//检查发件服务器端口是否是数字
		var reg = new RegExp(regexp.constants.integer);
		if(!reg.test(port)){
			$(this).parent().parent().addClass("has-warning");
			return;
		}
		
		//检查发件服务器端口是否超过限制(1至65535)
		var port = parseInt(port);
		if(port < 1 || port > 65535){
			$(this).parent().parent().addClass("has-warning");
			return;
		}
		
		reloadDatabases();
		reloadConnectionUrl();
	});
	$("input[name='database.username']").change(function(){
		$(this).parent().parent().removeClass("has-warning");
		if(!$(this).val()){
			$(this).parent().parent().addClass("has-warning");
		}
		reloadDatabases();
	});
	$("input[name='database.password']").change(function(){
		$(this).parent().parent().removeClass("has-warning");
		if(!$(this).val()){
			$(this).parent().parent().addClass("has-warning");
		}
		reloadDatabases();
	});
	$("select[name='database.databaseName'],input[name='database.databaseName']").change(function(){
		var option = $("select[name='database.type']").find("option[value='" + $("select[name='database.type']").val() + "']");
		$("input[name='database.driver']").val(option.attr("_driver"));
		
		databaseNameReload($(this).val());
	}).trigger("change");
});
