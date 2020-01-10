$.modal = function(opts,callback){
	var options = opts;
	var defaultOptions = {
		title: "新增",
		okButtonTitle: "保存",
		cancelButtonTitle: "取消",
		width: null,  	//内容框宽度(整数)
		height: null,	//内容框高度(整数)
		body: "",
		backdrop: true, /** boolean 或 string 'static' 默认值：true  指定一个静态的背景，当用户点击模态框外部时不会关闭模态框。 **/
		keyboard: true, /** boolean 默认值：true  当按下 escape 键时关闭模态框，设置为 false 时则按键无效。 **/
		show: true,     /** boolean 默认值：true  当初始化时显示模态框。**/
		//modal显示之前的回调事件
		beforeShow: function(modal){
			
		},
		callback: function(modal, flag){
			//callback返回true可正常关闭,否则不能关闭(仅对flag参数为true有效)
			return true;
		},
		//modal显示之后执行的回调方法
		modalVisibleCallback: function(modal){
			
		}
	};
	//如果第一个参数是json则认为是通过json形式传参数
	if($.isPlainObject(options)){
		options = $.extend(defaultOptions,options);
	}else if(typeof(options) == 'string'){
		//如果第一个参数是String则认为除了回调(callback)全部取默认值
		options = defaultOptions;
		options.body = opts;
	}
	
	if(callback && $.isFunction(callback)){
		options.callback = callback;
	}
	
	//如果不是异步加载则必须设置body参数
	if(!options.body){
		throw "body参数不能为空";
	}
	
	var reg = new RegExp("^[1-9]+[0-9]*$");
	//如果width是否为数字
	if(opts.width){
		if(!reg.test(opts.width)){
			throw "width参数必须为正整数";
			return;
		}
	}
	//校验height是否为数字
	if(opts.height){
		if(opts.height <= 0){
			throw "height参数必须大于0";
			return;
		}
		if(!reg.test(opts.height)){
			throw "height参数必须为正整数";
			return;
		}
	}

	//设置modal-body高度
	var modalBodyMaxHeight = $(window).height() - 10;
	
	var id = "id_modal_" + new Date().getTime().toString() + "_" + Math.random().toString().replace(".","");
	
	var modalHtml = "<div class='modal fade' id='" + id + "' tabindex='-1' role='dialog' aria-labelledby='" + id + "_modal_label' aria-hidden='true'>"
	    +"				<div class='modal-dialog'>"
	    +"					<div class='modal-content' style='" + (opts.width ? "width:" + opts.width + "px;" : "") + "'>"
	    +"						<div class='modal-header'>"
	    +"							<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"
	    +"							<h4 class='modal-title' id='" + id + "_modal_label'>" + options.title + "</h4>"
	    +"						</div>"
	    +"						<div class='modal-body' style='max-height:" + modalBodyMaxHeight + "px;" + (opts.height ? "height:" + opts.height + "px;" : "") + "'>body</div>"
	    +"						<div class='modal-footer'>"
	    +"							<button type='button' class='btn btn-primary ok'>" + options.okButtonTitle + "</button>"
	    +"							<button type='button' class='btn btn-default cancel' data-dismiss='modal'>" + options.cancelButtonTitle + "</button>"
	    +"						</div>"
	    +"					</div>"
	    +"				</div>"
	    +"			</div>";
	
	var modal = window.top.$(modalHtml);
	
	if(opts.width){
        var modalLeft = ($(window.top).width() - opts.width) / 2 - 15;
        modal.find('.modal-dialog').css({
            'margin-left': modalLeft
        });
	}
	
	//modal显示之前的回调事件
	if(opts.beforeShow && $.isFunction(opts.beforeShow)){
		opts.beforeShow(modal);
	}
	
	window.top.$("body").append(modal);
	
	var body = options.body;
	//如果body是函数
	if($.isFunction(body)){
		body = body();
	}
	if(!body){
		throw "body参数不能为空";
	}
	//插入html
	modal.find("div.modal-body").html(body);
	
	window.top.$('#' + id).modal(options);
	
	/* 完成拖拽 */
	window.top.$('#' + id).draggable({
		cursor: "move",
		handle: '.modal-header'
	});
	
	//modal显示的时候执行回调
	modal.on('shown.bs.modal', function () {
		if(opts.modalVisibleCallback && $.isFunction(opts.modalVisibleCallback)){
			opts.modalVisibleCallback(window.top.$('#' + id));
		}
	});
	
	//隐藏模态窗体时回调callback函数
	window.top.$('#' + id).on('hide.bs.modal', function () {
		if(!window.top.$('#' + id + " button.ok").attr("click")){
			options.callback(window.top.$('#' + id), false);
		}
		
		//删除modal
		modal.remove();
	});
	//确定按钮绑定click事件
	window.top.$('#' + id + " button.ok").click(function(){
		var flag = true;
		if(options.callback && $.isFunction(options.callback)){
			flag = options.callback(window.top.$('#' + id), true);
		}
		if(flag){
			window.top.$('#' + id + " button.ok").attr("click","click");
			window.top.$('#' + id).modal('hide');
		}
	});
}