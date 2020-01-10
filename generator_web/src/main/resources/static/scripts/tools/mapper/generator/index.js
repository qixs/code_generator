$(document).ready(function(){
	//生成
	$("button.generate").click(function () {
		var tableName = $("input[name='tableName']").val();
		if(!tableName){
			$.alert("请先输入表名");
			return;
		}
		//表名只能包含字母，数字，下划线
		if(!(new RegExp("^[0-9_a-zA-z]+$")).test(tableName)){
			$.alert("表名只能包含字母，数字，下划线");
			return;
		}

		var columnNames = $("textarea[name='columnNames']").val();
		if(!columnNames){
			$.alert("请先输入字段名");
			return;
		}
		//字段名只能包含字母，数字，下划线和英文逗号
		if(!new RegExp("^[0-9_a-zA-z,]+$").test(columnNames)){
			$.alert("字段名只能包含字母，数字，下划线和英文逗号");
			return;
		}

		$.post("/tools/mapper/generator/generate", $("#mapperGenerateForm").serialize(), function (data) {
			if(data && $.isPlainObject(data) && data.errorMessage){
				$("div#mapper").text(data.errorMessage);
			}else{
				$("div#mapper").html(data.replace(/\r\n/g, "<br/>"));
			}
		});
	});
});