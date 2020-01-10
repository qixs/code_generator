package com.qxs.base.formatter.jsqlparser;

import java.util.Iterator;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;

/**
 * @author qixingshen
 * **/
public class SelectFormatter {
	
	public String format(Select select) {
		StringBuilder retval = new StringBuilder();
        if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
            retval.append("WITH ");
            for (Iterator<WithItem> iter = select.getWithItemsList().iterator(); iter.hasNext();) {
                WithItem withItem = iter.next();
                retval.append(formatWithItem(withItem));
                if (iter.hasNext()) {
                    retval.append(",");
                }
//                retval.append(" ");
            }
        }
        retval.append(formatSelectBody(select.getSelectBody(),""));
        return retval.toString();
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
