$(document).ready(function(){
	$('#pluginChangeHistoryDetailTable').bootstrapTable({
		url: '/user/plugin/change/history/detail/getList/' + $("#pluginChangeHistoryId").val(),
		height: $("body .bootstrapTable").height(),
		clickToSelect: true,
		height: 300,
		sortName: 'changeFieldComment',
		sortOrder: 'asc',
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}}, 
	    	{field: 'changeFieldComment',title: '变更项名称', sortable: true}, 
	    	{field: 'changeFieldName',title: '变更项字段名', sortable: true},
	    	{field: 'changeBefore',title: '变更前的值', sortable: true, formatter : function(value, row, index){
    			return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
    		}},
	    	{field: 'changeAfter',title: '变更后的值', sortable: true, formatter : function(value, row, index){
    			return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
    		}},
	    ],
	});
});