$.fn.tabAdd = function(options){
	if(!options || $.isEmptyObject(options)){
		throw "参数不能为空";
	}
	var defaultOptions = {
		menuId:"",//菜单id
		title: "",//页签名
		tabContent: $("#tabContent"), //页签内容div
		closable: true,//允许关闭
		href: "", //地址
	};
	var opts = options;
	opts = $.extend(defaultOptions,opts)
	if(!opts.title){
		throw "页签名不能为空";
	}
	
	var tab = $(this);
	
	if(tab.find("li[menuId=" + opts.menuId + "]").length > 0){
		//已经打开该菜单,直接展示该页签即可
		
		//关闭已经打开的页签
		tab.find("li.active").removeClass("active");
		opts.tabContent.find("div.active").removeClass("active").removeClass("in");
		
		//打开指定的页签
		tab.find("li[menuId=" + opts.menuId + "]").addClass("active");
		var id = tab.find("li[menuId=" + opts.menuId + "] a").attr("href");
		$("div" + id).addClass('active').addClass('in');
    	tab.find('li[menuId=" + opts.menuId + "] a').tab('show');
	}else{
		var tabId = "tab_id_" + new Date().getTime() + "_" + (Math.random().toString().replace(".",""));
		//隐藏当前展示的页签
		tab.find("li.active").removeClass("active");
		var li = $("<li class='active' menuId='" + opts.menuId + "'>" 
				+ "<a href='#" + tabId + "' data-toggle='tab'>" + opts.title + "</a>" 
				+ (opts.closable ? "<i class='glyphicon glyphicon-remove' style='font-size: 10px;position: absolute;right: 5px;top: 5px;z-index: 100;cursor: pointer;color: #94A6B0;'></i>" : "")
			+"</li>");
		tab.append(li);
		
		//关闭按钮
		li.find("i").click(function(){
			var li = $(this).parent();
			window.top.$.confirm("确定要关闭该页签？",function(flag){
				if(flag){
					//如果关闭的是当前激活的TAB，激活他的前一个TAB
			        if (li.hasClass('active')) {
			        	//获取当前li次序
			        	var i = 0;
			        	tab.find("li").each(function(idx){
			        		if($(this).hasClass("active")){
			        			i = idx;
			        			return;
			        		}
			        	});
			        	tab.find('li:eq(' + (i - 1) + ')').addClass('active');
			        	var id = tab.find('li:eq(' + (i - 1) + ') a').attr("href");
			            $("div" + id).addClass('active').addClass('in');
			        	tab.find('li:eq(' + (i - 1) + ') a').tab('show');
			        }
			        //关闭TAB
			        li.remove();
			        $("#" + tabId).remove();
				}
			});
		});
		
		opts.tabContent.find("div.active").removeClass("active").removeClass("in");
		opts.tabContent.append("<div class='tab-pane fade in active' id='" + tabId + "'>"
							+  "	<iframe style='width: 100%;height: calc(100vh - 141px);border:0px;' src='" + opts.href + "'></iframe>"
							+  "</div>");
	}
};
$.fn.tabClose = function(){
	
};
