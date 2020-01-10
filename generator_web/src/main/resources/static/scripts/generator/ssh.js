
$(document).ready(function(){
	
	$("input[name='ssh.host'],input[name='ssh.username'],input[name='ssh.password']").change(function(){
		$(this).parent().parent().removeClass("has-warning");
		if(!$(this).val()){
			$(this).parent().parent().addClass("has-warning");
		}
		
	});
	$("input[name='ssh.port']").change(function(){
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
		
	});
	
	$("div#ssh input").trigger("change");
	
	$("input[name='ssh.host'],input[name='ssh.port'],input[name='ssh.username'],input[name='ssh.password']").change(function(){
		reloadDatabases();
	});
});
