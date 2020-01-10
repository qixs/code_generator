$(document).ready(function(){
	$('#logLoginTable').bootstrapTable({
		url: '/log/login/getList',
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
		pageList: [10, 25, 50, 100],
		sortOrder: 'desc',
		sortName: 'loginDate',
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}}, 
	    	{field: 'user.username',title: '用户名', sortable: true}, 
	    	{field: 'loginDate',title: '登录时间', sortable: true}, 
	    	{field: 'loginIp',title: '登录IP', sortable: true}, 
	    	{field: 'exitDate',title: '退出时间', sortable: true}, 
	    ]
	});
});
