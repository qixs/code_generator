$(document).ready(function(){
	$('#pluginChangeHistoryTable').bootstrapTable({
		url: '/plugin/change/history/getList',
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
		sortName: 'updateDate',
		sortOrder: 'desc',
		pageList: [10, 25, 50, 100],
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}},
			{field: 'plugin.groupName',title: '插件组名', sortable: true},
	    	{field: 'plugin.name',title: '插件名称', sortable: true},
	    	{field: 'plugin.description',title: '插件描述', sortable: true, formatter:function (value, row, index) {
	    			if(!value){
	    				return null;
					}
					return "<span style='display: block;width: 100px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap' title='" + value + "'>" + value + "</span>";
				}
			},
	    	{field: 'plugin.dependencies',title: '插件依赖', sortable: true, formatter:function (value, row, index) {
					if(!value){
						return null;
					}
					return "<span style='display: block;width: 100px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap' title='" + value + "'>" + value + "</span>";
				}
			},
	    	{field: 'updateUserName',title: '变更人', sortable: true},
	    	{field: 'updateDate',title: '变更时间', sortable: true, formatter:function (value, row, index) {
					return "<span style='display: block;width: 140px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap' title='" + value + "'>" + value + "</span>";
				}
			},
	    	{field: 'status',title: '禁用',formatter : function(value, row, index){
		        	return "<input type='button' id='" + row.id + "' value='详情'/>";
		        }
	    	},
	    ],
	    onLoadSuccess: function(rows){
	    	$("table#pluginChangeHistoryTable input[type='button']").each(function(){
				var button = $(this);
				button.click(function(){
					$.modal({
						title: "插件变更记录详情",
						cancelButtonTitle: "关闭",
						width: 900,
						body: function(){
							var body = null;
							$.ajax({
								type: "get", 
							    url: "/plugin/change/history/detail/index/" + button.attr("id"), 
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
						},
					});
				});
			});
	    }
	});
	
});