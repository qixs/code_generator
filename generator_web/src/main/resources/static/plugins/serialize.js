//将表单序列化成json对象
$.fn.serializeObject = function() {
	$("input[type='text'],input[type='file'],input[type='password']").blur();//调用blur事件,解决serializeArray获取不到值的问题
	var o = {};
	var a = this.serializeArray();
	$.each(a, function() {
		if (o[this.name]) {
			if (!o[this.name].push) {
				o[this.name] = [ o[this.name] ];
			}
			var v = typeof(this.value) == undefined || this.value ==null || this.value.trim() == '' ? null : this.value.trim();
			if(v){
				o[this.name].push(v || '');
			}
		} else {
			var v = typeof(this.value) == undefined || this.value ==null || this.value.trim() == '' ? null : this.value.trim();
			if(v){
				o[this.name] = v || '';
			}
			
		}
	});
	
	//checkbox
	$(this).find("input[type='checkbox'][checkedValue][notCheckedValue]").each(function(){
		var $this = $(this);
		if($this.attr("name")){
			o[$this.attr("name")] = $this.is(":checked") ? $this.attr("checkedValue") : $this.attr("notCheckedValue");
		}
	});
	return o;
};
