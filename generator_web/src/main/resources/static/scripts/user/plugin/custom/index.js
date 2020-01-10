$(document).ready(function(){
	//记录折叠框位置  left  center right
	var collapsePositionArray = ["left", "center", "right"];
	var collapsePositionIndex = 1;

	//计算向左和向右的箭头的位置
	var calcCollapsePosition = function(positionIndex){
		var position = collapsePositionArray[positionIndex];
		
		var left = $("#sourceLeft");
		var right = $("#sourceRight");
		var collapseHeight = $("#collapse").height();
		var containerWidth = $("#sourceContainer").width();
		
		if(position == 'left'){
			var leftWidth = left.width();
			
			//最左边只显示向右的箭头
			$("#sourceLeft").hide();
			
			left.hide();
			
			//左边显示全部
			right.width(containerWidth - $("#collapse").width() - 1);
			
			//隐藏向右的箭头
			$("div.toLeft").hide();
			//计算向左箭头位置
			$("div.toRight").css("margin-top", (collapseHeight - 17) / 2 + "px");
			
			$("#sourceContainer").css({"border-left": "1px solid #ccc"});
		}else if(position == 'right'){
			right.hide();
			
			//左边显示全部
			left.width(containerWidth - $("#collapse").width() - 1);
			
			//隐藏向右的箭头
			$("div.toRight").hide();
			//计算向左箭头位置
			$("div.toLeft").css("margin-top", (collapseHeight - 17) / 2 + "px");
			
			$("#sourceContainer").css({"border-right": "1px solid #ccc"});
		}else{
			//中间位置向左向右箭头都显示
			$("div.toRight").css("margin-top", (collapseHeight - 44) / 2 + "px").show();
			$("div.toLeft").css("margin-top", "").show();
			
			//中间位置左右两边都显示
			left.width(left.attr("_width")).show();
			right.width(right.attr("_width")).show();
			
			$("#sourceContainer").css({"border-left": "", "border-right": ""});
		}
	};
	
	//向右
	window.top.$("div.toRight").click(function(){
		collapsePositionIndex = collapsePositionIndex + 1;
		calcCollapsePosition(collapsePositionIndex);
	});
	//向左
	window.top.$("div.toLeft").click(function(){
		collapsePositionIndex = collapsePositionIndex - 1;
		calcCollapsePosition(collapsePositionIndex);
	});
//	
//	var isMove = false;
//	var pageX = 0;
//	
//	//拖动事件
//	window.top.$("#collapse").mousedown(function (e) {
//		isMove = true;
//        //当前鼠标相对屏幕位置
//        pageX = e.pageX;
//        
//	});
//	
});
