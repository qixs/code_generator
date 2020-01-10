$(document).ready(function(){
	$('#logGenerateTable').bootstrapTable({
		url: '/log/generate/getList',
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
		sortName: 'generateStartDate',
		sortOrder: 'desc',
		pageList: [10, 25, 50, 100],
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}}, 
	    	{field: 'generateStartDate',title: '开始时间', sortable: true}, 
	    	{field: 'generateStopDate',title: '结束时间', sortable: true}, 
	    	{field: 'generateTime',title: '耗时', sortable: true}, 
//	    	{field: 'generateParameterDatabase',title: '数据库参数', sortable: true,formatter : function(value, row, index){
//					return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
//				}
//	    	}, 
//	    	{field: 'generateParameterSsh',title: 'SSH连接参数', sortable: true,formatter : function(value, row, index){
//					return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
//				}
//	    	}, 
//	    	{field: 'generateParameterParameter',title: '配置参数', sortable: true,formatter : function(value, row, index){
//					return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
//				}
//	    	}, 
	    	{field: 'generateResult',title: '生成结果', sortable: true,formatter : function(value, row, index){
	    			return value == 1 ? '成功' : '失败';
	    		}
	    	}, 
	    	{field: 'failReason',title: '失败原因', sortable: true,formatter : function(value, row, index){
		    		if(value){
		    			return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
		    		}
    				return value;
    			}
	    	}, 
	    	{field: 'id',title: '查看详情', formatter : function(value, row, index){
					return "<input type='button' id='" + value + "' value='查看详情'/>";
				}
	    	}, 
	    	//{field: 'exception',title: '异常信息', sortable: true}, 
	    ],
	    onLoadSuccess: function(rows){
	    	$("table#logGenerateTable input[type='button']").each(function(){
				var button = $(this);
				button.click(function(){
					$.modal({
						title: "生成代码日志详情",
						cancelButtonTitle: "关闭",
						width: 900,
						body: function(){
							var body = null;
							$.ajax({
								type: "get", 
							    url: "/log/generate/detail/" + button.attr("id"), 
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
							//添加重新生成按钮
							var generateButton = $("<button type='button' class='btn btn-primary'>重新生成</button>");
							modal.find("div.modal-footer").prepend(generateButton);
							
							var  websocket = null;
							generateButton.click(function(){
								if(websocket != null){
									$.alert("目前正在生成代码，请等待当前任务完成之后再生成代码");
									return;
								}
								var host =  window.location.host;
								var wsServer = "ws://" + host + "/log/generator/generate"; 
								websocket = new WebSocket(wsServer); 
								
								var zip = new JSZip();
								
								websocket.onopen = function (evt) {
									console.info("创建连接");
									//添加状态判断，当为OPEN时，发送消息
								    if (websocket.readyState===1) {
								    	//向服务器端发送生成参数
								    	websocket.send(button.attr("id"));
								    }
								    
								    modal.find(".progress-striped").show();
								}; 
								websocket.onclose = function (evt) {
									console.info("关闭连接");
									
									modal.find(".progress-striped").hide();
									modal.find(".progress-bar").css("width", "0%");
								}; 
								websocket.onmessage = function (evt) {
									var data = evt.data;
									console.debug('收到服务器消息:' + data);
									//如果接收到的信息是json且信息里包含status字段则认为生成代码结束，关闭连接
									if(data){
										try{
											data = JSON.parse(data);
										}catch(e){
										}
									}
									//总数
									if($.isPlainObject(data) && data.generateTotalNum){
										modal.find("input[name='generateTotalNum']").val(data.generateTotalNum);
									}
									//更新进度
									if($.isPlainObject(data) && data.generatedNum){
										var rate = data.generatedNum / modal.find("input[name='generateTotalNum']").val();
										//js计算存在精度问题，如果已经生成的数量等于要生成的总数量则把比例值为1
										if(data.generatedNum == modal.find("input[name='generateTotalNum']").val()){
											rate = 1;
										}
										console.log("当前进度：" + (rate.toFixed(2)) +" 总代码文件数：" + modal.find("input[name='generateTotalNum']").val() + "  当前是第" + data.generatedNum + "个文件");
										
										modal.find(".progress-bar").css("width", parseInt(rate * 100) + "%");
									}
									
									//代码文件,代码文件转换json可能会出错，不使用转换json的方式
									if(typeof(data) == 'string' && data.indexOf("file:") == 0){
										var filePathIndex = data.indexOf(":", "file:".length);
										filePath = data.substring("file:".length,filePathIndex);
										
										var contentIndex = data.indexOf(":", filePathIndex + 1);
										var content = data.substring(contentIndex + 1);
										
										zip.file(filePath, content);
									}
									
									//代码生成完毕关闭连接
									if($.isPlainObject(data) && data.status){
										//关闭连接
										websocket.close();
										websocket = null;
										//生成成功
										if(data.status == 'SUCCESS'){
											$.alert("生成代码成功");
											//打包
											zip.generateAsync({type:"blob"})
											.then(function(content) {
											    // see FileSaver.js
												var date = new Date();
											    saveAs(content, "code_" + date.getFullYear() + "_" + (date.getMonth() + 1) + "_" + date.getDate() + "_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getSeconds() + ".zip");
											});
										}else{
											//生成失败
											$.alert("生成代码失败：" + data.message);
										}
									}
									
								}; 
								websocket.onerror = function (evt) {
									console.error('连接服务器出错');
									//连接服务器出错，关闭连接
									websocket.close();
									websocket = null;
								}; 
							});
						},
						modalVisibleCallback: function(modal){
							modal.find("div.detailContainer").height(modal.find("div.modal-body").height() - 32);
						}
					});
				});
			});
	    	
	    	//鼠标放上去增加提示信息
	    	$("table#logGenerateTable tbody tr").each(function(index){
	    		if(rows && rows.content && rows.content.length > 0){
	    			var row = rows.content[index];
		    		
		    		var title = "开始时间：" + row.generateStartDate + "\r\n";
		    		title = title + "结束时间：" + row.generateStopDate + "\r\n";
		    		title = title + "耗时：" + row.generateTime + "\r\n";
		    		title = title + "生成结果：" + (row.generateResult == 1 ? '成功' : '失败') + "\r\n";
		    		title = title + "失败原因：" + (row.failReason ? row.failReason : '') + "\r\n";
		    		
		    		$(this).attr("title",title);
	    		}
	    	});
	    }
	});
});
