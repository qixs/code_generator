
$(document).ready(function(){
	//点击菜单打开对应的页面
	$(".left-col a").each(function(){
		var a = $(this);
		var href = a.attr("href");
		var id = "menu_id_" + new Date().getTime() + "_" + (Math.random().toString().replace(".",""));
		a.attr("href","javascript:void(0)").attr("id",id).click(function(){
			window.top.$("#tab").tabAdd({
				menuId: $(this).attr("id"),
				title: $(this).text(),
				tabContent: $("#tabContent"),
				href: href
			});
		});
	});
	
	//用户信息
	$("a.userInfo").click(function(){
		$.modal({
			title: "用户信息",
			cancelButtonTitle: "关闭",
			body: function(){
				var body = null;
				$.ajax({
					type: "get", 
				    url: "/user/info/userInfoModal", 
				    cache:false, 
				    async:false, 
				    success: function(data){ 
				    	body = data;
				    } 
				});
				return body;
			},
			beforeShow: function(modal){
				modal.find("button.ok").remove();
			}
		});
	});
	//退出
	$("a.logout").click(function(){
		$.confirm('确认退出系统？',function(c){  
	        if(c){
	        	var form = $("<form method='post' id='logoutform'>" 
	        				+"	<input name='_csrf' value='" + $("meta[name='_csrf']").attr("content") + "'/>"
	        				+"</form>")
	        	form.attr("action","/logout");
	        	$("body").append(form);
	        	form.submit();
	        }
		});
	});
});
