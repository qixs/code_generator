package com.qxs.base.formatter.jsqlparser;

import java.util.List;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperation;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;

/**
 * @author qixingshen
 * **/
public class SetOperationListFormatter {
	
	public String format(SetOperationList setOperationList) {
		List<SelectBody> selects = setOperationList.getSelects();
		StringBuilder sb = new StringBuilder();
		List<SetOperation> operations = setOperationList.getOperations();
		for(int i = 0 , length = selects.size() ; i < length ; i ++){
			SelectBody selectBody = selects.get(i);
			if(i > 0){
				sb.append("\r\n" + operations.get(i - 1).toString() + "\r\n");
			}
			sb.append(formatSelectBody(selectBody,""));
		}
		return sb.toString();
	}
	private String formatWithItem(WithItem withItem) {
		return (withItem.isRecursive() ? "RECURSIVE " : "") + withItem.getName() + ((withItem.getWithItemList() != null) ?
				" " + PlainSelect.getStringList(withItem.getWithItemList(), true, true) : "")
				+ " AS (\r\n\t" + formatSelectBody(withItem.getSelectBody(),"\t") + "\r\n)\r\n";
	}
	private String formatSelectBody(SelectBody selectBody,String tab) {
		if(selectBody instanceof PlainSelect) {
			return PlainSelectFormatter.format(new PlainSelectFormatter(tab), (PlainSelect)selectBody);
		}else if(selectBody instanceof SetOperationList) {
			return new SetOperationListFormatter().format((SetOperationList)selectBody);
		}else if(selectBody instanceof WithItem) {
			return formatWithItem((WithItem)selectBody);
		}
		return null;
	}
}
