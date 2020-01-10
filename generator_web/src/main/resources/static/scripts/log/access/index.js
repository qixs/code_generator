$(document).ready(function(){
	$('#logAccessTable').bootstrapTable({
		url: '/log/access/getList',
		height: $("body .bootstrapTable").height(),
		dataField: "content",
		pagination: true,
		totalField: "totalElements",
		singleSelect: true,
		clickToSelect: true,
		showRefresh: true,
		search: true,
		searchOnEnterKey: true,
		sidePagination: 'server',
		pageNumber: 1,
		sortName: 'accessDate',
		sortOrder: 'desc',
		pageList: [10, 25, 50, 100],
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}}, 
	    	{field: 'user.username',title: '用户名', sortable: true}, 
	    	//{field: 'systemVersion',title: '系统版本号', sortable: true}, 
	    	{field: 'accessDate',title: '访问时间', sortable: true}, 
	    	{field: 'time',title: '执行时间', sortable: true}, 
	    	//{field: 'parameters',title: '请求参数', sortable: true}, 
	    	{field: 'url',title: '访问地址', sortable: true,formatter : function(value, row, index){
	    			return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
	    		}
	    	}, 
	    	{field: 'resultType',title: '响应结果类型', sortable: true,formatter : function(value, row, index){
    				return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
    			}
	    	}, 
	    	{field: 'id',title: '查看详情', formatter : function(value, row, index){
					return "<input type='button' id='" + value + "' value='查看详情'/>";
				}
	    	}, 
	    	//{field: 'exception',title: '异常信息', sortable: true}, 
	    ],
	    onLoadSuccess: function(rows){
	    	$("table#logAccessTable input[type='button']").each(function(){
				var button = $(this);
				button.click(function(){
					$.modal({
						title: "访问系统日志详情",
						cancelButtonTitle: "关闭",
						width: 900,
						body: function(){
							var body = null;
							$.ajax({
								type: "get", 
							    url: "/log/access/detail/" + button.attr("id"), 
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
							//添加发送邮件按钮
							var sendMailButton = $("<button type='button' class='btn btn-primary'>发送问题反馈邮件</button>");
							modal.find("div.modal-footer").prepend(sendMailButton);
							
							sendMailButton.click(function(){
								//加载email格式的日志信息模板并调用发送邮件组件
								//请求日志以文件的形式发送
								emailModal("【重要】代码生成器问题反馈", false);
							});
							
							//添加生成日志文件按钮
							var generateAccessLogButton = $("<button type='button' class='btn btn-primary'>生成日志文件</button>");
							modal.find("div.modal-footer").prepend(generateAccessLogButton);
							
							generateAccessLogButton.click(function(){
								$.fileDownload("/log/access/downloadAccessLog/" + button.attr("id"), {
									httpMethod: 'GET',
									prepareCallback: function(url){
										console.debug("开始生成日志文件");
									},
									successCallback: function(url){
										$.alert("生成日志文件成功");
									},
									failCallback: function(html, url, error){
										$.alert("生成日志文件失败：" + (error ? ":" + error.message : ""));
									},
								});
							});
						},
						modalVisibleCallback: function(modal){
							modal.find("div.detailContainer").height(modal.find("div.modal-body").height() - 32);
						}
					});
				});
			});
	    	
	    	//鼠标放上去增加提示信息
	    	$("table#logAccessTable tbody tr").each(function(index){
	    		if(rows && rows.content && rows.content.length > 0){
	    			var row = rows.content[index];
		    		
		    		var title = "用户名：" + row.user.username + "\r\n";
		    		title = title + "系统版本号：" + row.systemVersion + "\r\n";
		    		title = title + "访问时间：" + row.accessDate + "\r\n";
		    		title = title + "执行时间：" + row.time + "\r\n";
		    		title = title + "请求参数：" + row.parameters + "\r\n";
		    		title = title + "访问地址：" + row.url + "\r\n";
		    		title = title + "响应结果类型：" + row.resultType + "\r\n";
		    		
		    		$(this).attr("title",title);
	    		}
	    	});
	    }
	});
});
