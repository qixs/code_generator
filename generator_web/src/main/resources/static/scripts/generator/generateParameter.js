
$(document).ready(function(){
	$('#selectTableNameTable').bootstrapTable({
		url: '',
		height: $(window).height() - 100,
	    columns: [
	    	{field: '',title: '',checkbox : true}, 
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}},
	    	{field: 'name',title: '表名',searchable: true,
	    		formatter: function(value, row, index){
	    			return "<span style='display:block;overflow: hidden;text-overflow: ellipsis;overflow: hidden;white-space: nowrap;width: 130px;' title='" + value + "'>" + value + "</span>";
	    		}
	    	}, 
	    	{field: 'view',title: '视图',formatter : function(value, row, index){return value ? "是" : "否";}}, 
	    	{field: 'comment',title: '注释', searchable: true,
	    		formatter: function(value, row, index){
	    			if(!value){
	    				return value;
	    			}
	    			return "<span style='display:block;overflow: hidden;text-overflow: ellipsis;overflow: hidden;white-space: nowrap;width: 149px;' title='" + value + "'>" + value + "</span>";
	    		}
	    	}
	    ],
	    search: true,
	    searchOnEnterKey: true,
	    clickToSelect: true,
	    height: 307,
	    onPostBody: function(rows){
			$("div.pull-right.search").css({"margin-top":"0","margin-bottom":"3px"});
			if(rows.length > 0){
				//勾选已经选择的表
				if( $("#tableNames").val()){
					var tableNames = $("#tableNames").val().split(",");
					for(var i = 0 , length = rows.length ; i < length ; i ++){
						if($.inArray(rows[i].name, tableNames) >= 0){
							$('#selectTableNameTable').bootstrapTable('check', i);
						}
					}
				}else{
					$('#selectTableNameTable').bootstrapTable('checkAll');
				}
			}
			if(rows.length > 0){
				$("input[name='lodingTables']").val(0);
			}else{
				setTimeout(function () {
					$("input[name='lodingTables']").val(0);
				}, 5000);
			}

		}
	});
	$("div.pull-right.search").css({"margin-top":"0","margin-bottom":"3px"});
	$("div.selectTableNameTable div.fixed-table-container").height(270);
	
	$('#pluginTable').bootstrapTable({
		url: '/user/plugin/findUserPluginList?userPlugin.status=0',
		height: $(window).height() - 100,
	    columns: [
	    	{field: '',title: '',checkbox : true}, 
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}},
			{field: 'groupName',title: '插件组名',
				formatter: function(value, row, index){
					return "<span style='display:block;overflow: hidden;text-overflow: ellipsis;overflow: hidden;white-space: nowrap;width: 105px;' title='" + value + "'>" + value + "</span>";
				}
			},
	    	{field: 'name',title: '插件名称',
	    		formatter: function(value, row, index){
	    			return "<span style='display:block;overflow: hidden;text-overflow: ellipsis;overflow: hidden;white-space: nowrap;width: 105px;' title='" + value + "'>" + value + "</span>";
	    		}
	    	}
	    ],
	    height: 270,
	    clickToSelect: true,
		onLoadSuccess: function(rows){
			if(rows.length > 0){
				//勾选已经选择的插件
				if($("#pluginNames").val()){
					var pluginNames = $("#pluginNames").val().split(",");
					for(var i = 0 , length = rows.length ; i < length ; i ++){
						if($.inArray(rows[i].groupName + "." + rows[i].name, pluginNames) >= 0){
							$('#pluginTable').bootstrapTable('check', i);
						}
					}
				}else{
					$('#pluginTable').bootstrapTable('checkAll');
				}
			}
		}
	});
});
