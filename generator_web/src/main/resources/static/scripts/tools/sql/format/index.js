$(document).ready(function(){
	//格式化
	$("button.format").click(function () {
		var sql = $("textarea[name='sql']").val();
		if(!sql){
			$.alert("请先输入sql");
			return;
		}
		$.post("/tools/sql/format/format", {sql: sql, _csrf: window.top.$("meta[name='_csrf']").attr("content")}, function (data) {
			if(data && $.isPlainObject(data) && data.errorMessage){
				$("div#formatSql").text(data.errorMessage);
			}else{
				$("div#formatSql").html(data.replace(/\r\n/g, "<br/>"));
			}
		});
	});
});