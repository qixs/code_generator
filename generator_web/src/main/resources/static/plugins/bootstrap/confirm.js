$.confirm = function(opts,callback){
	var options = opts;
	var defaultOptions = {
		title: "提示",
		message: "",
		backdrop: true, /** boolean 或 string 'static' 默认值：true  指定一个静态的背景，当用户点击模态框外部时不会关闭模态框。 **/
		keyboard: true, /** boolean 默认值：true  当按下 escape 键时关闭模态框，设置为 false 时则按键无效。 **/
		show: true,     /** boolean 默认值：true  当初始化时显示模态框。**/
		remote: false,  /** path 默认值：false 使用 jQuery .load 方法，为模态框的主体注入内容。如果添加了一个带有有效 URL 的 href，则会加载其中的内容。如下面的实例所示：**/
		callback: function(flag){
			
		}
	};
	//如果第一个参数是json则认为是通过json形式传参数
	if($.isPlainObject(options)){
		options = $.extend(defaultOptions,options);
	}else if(typeof(options) == 'string'){
		//如果第一个参数是String则认为除了回调(callback)全部取默认值
		options = defaultOptions;
		options.message = opts;
	}
	
	if(callback && $.isFunction(callback)){
		options.callback = callback;
	}
	
	
	if(!options.message){
		throw "message参数不能为空";
	}
	
	var id = "id_modal_" + new Date().getTime().toString() + "_" + Math.random().toString().replace(".","");
	
	var modalHtml = "<div class='modal fade' id='" + id + "' tabindex='-1' role='dialog' aria-labelledby='" + id + "_modal_label' aria-hidden='true'>"
	    +"				<div class='modal-dialog'>"
	    +"					<div class='modal-content'>"
	    +"						<div class='modal-header'>"
	    +"							<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"
	    +"							<h4 class='modal-title' id='" + id + "_modal_label'>" + options.title + "</h4>"
	    +"						</div>"
	    +"						<div class='modal-body' style='padding-left: 50px;'>" + options.message + "</div>"
	    +"						<div class='modal-footer'>"
	    +"							<button type='button' class='btn btn-primary'>确定</button>"
	    +"							<button type='button' class='btn btn-default' data-dismiss='modal'>取消</button>"
	    +"						</div>"
	    +"					</div>"
	    +"				</div>"
	    +"			</div>";
	
	window.top.$("body").append($(modalHtml));
	
	window.top.$('#' + id).modal(options);
	//隐藏模态窗体时回调callback函数
	window.top.$('#' + id).on('hide.bs.modal', function () {
		if(!window.top.$('#' + id + " button.btn-primary").attr("click")){
			options.callback(false);
		}
	});
	//确定按钮绑定click事件
	window.top.$('#' + id + " button.btn-primary").click(function(){
		window.top.$('#' + id + " button.btn-primary").attr("click","click");
		window.top.$('#' + id).modal('hide');
		options.callback(true);
	});
}