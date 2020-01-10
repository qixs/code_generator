package com.qxs.base.formatter.jsqlparser;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * @author qixingshen
 * **/
public class InsertFormatter {

	/**
	 * 最大并排的列数(列数超过该个数则竖向排列)
	 * **/
	public static final int MAX_TRANSVERSE_COLUMN_SIZE = 5;
	
	public String format(Insert insert) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT ");
		if (insert.getModifierPriority() != null) {
			sql.append(insert.getModifierPriority().name()).append(" ");
		}

		if (insert.isModifierIgnore()) {
			sql.append("IGNORE ");
		}

		sql.append("INTO ");
		sql.append(insert.getTable()).append(" ");
		if (insert.getColumns() != null) {
			if(insert.getColumns().size() > MAX_TRANSVERSE_COLUMN_SIZE){
				String lineStr = "\r\n" ;

				String comma = ",";

				if (insert.getColumns() != null) {
					sql.append("(");

					for(int i = 0; i < insert.getColumns().size(); ++i) {
						sql.append(lineStr + "\t" + insert.getColumns().get(i)).append(i < insert.getColumns().size() - 1 ? comma : "");
					}
					sql.append(lineStr + ")").append(" ");
				}
			}else{
				sql.append(PlainSelect.getStringList(insert.getColumns(), true, true)).append(" ");
			}

		}

		if (insert.isUseValues()) {
			sql.append("\r\nVALUES ");
		}

		if (insert.getItemsList() != null) {
			ExpressionList expressionList = (ExpressionList)insert.getItemsList();
			String lineStr = expressionList.getExpressions().size() > MAX_TRANSVERSE_COLUMN_SIZE ? "\r\n\t" : "" ;

			String comma = ",";
			if (insert.getItemsList() != null) {
				sql.append("(");

				for(int i = 0; i < expressionList.getExpressions().size(); ++i) {
					Expression expression = expressionList.getExpressions().get(i);
					if(expression instanceof JdbcParameter){
						sql.append(lineStr + expression).append(i < expressionList.getExpressions().size() - 1 ? comma : "");
					}else if(expression instanceof CustomJdbcNamedParameter){
						sql.append(lineStr + ((CustomJdbcNamedParameter)expression).getName()).append(i < expressionList.getExpressions().size() - 1 ? comma : "");
					}
				}
				sql.append((expressionList.getExpressions().size() > MAX_TRANSVERSE_COLUMN_SIZE ? "\r\n" : "") + ")").append(" ");
			}
		} else {
			if (insert.isUseSelectBrackets()) {
				sql.append("(");
			}

			if (insert.getSelect() != null) {
				sql.append("\r\n" + new SelectFormatter().format(insert.getSelect()));
			}

			if (insert.isUseSelectBrackets()) {
				sql.append(")");
			}
		}

		if (insert.isUseDuplicate()) {
			sql.append(" ON DUPLICATE KEY UPDATE ");

			for(int i = 0; i < insert.getDuplicateUpdateColumns().size(); ++i) {
				if (i != 0) {
					sql.append(", ");
				}

				sql.append(insert.getDuplicateUpdateColumns().get(i)).append(" = ");
				sql.append(insert.getDuplicateUpdateExpressionList().get(i));
			}
		}

		if (insert.isReturningAllColumns()) {
			sql.append(" RETURNING *");
		} else if (insert.getReturningExpressionList() != null) {
			sql.append(" RETURNING ").append(PlainSelect.getStringList(insert.getReturningExpressionList(), true, false));
		}

		return sql.toString();
	}

}
