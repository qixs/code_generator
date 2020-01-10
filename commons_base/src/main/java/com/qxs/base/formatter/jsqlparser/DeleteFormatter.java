package com.qxs.base.formatter.jsqlparser;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * @author qixingshen
 * **/
public class DeleteFormatter {

	public String format(Delete delete) {
		StringBuilder b = new StringBuilder("DELETE");

        if (delete.getTables() != null && delete.getTables().size() > 0) {
            b.append(" ");
            for (Table t : delete.getTables()) {
                b.append(t.toString());
            }
        }

        b.append(" FROM ");
        b.append(delete.getTable());

        if (delete.getJoins() != null) {
            for (Join join : delete.getJoins()) {
                if (join.isSimple()) {
                    b.append(", ").append(join);
                } else {
                    b.append(" ").append(join);
                }
            }
        }

        if (delete.getWhere() != null) {
        	String temp = delete.getWhere() instanceof AndExpression || delete.getWhere() instanceof OrExpression ? "\r\n" : " ";
            b.append(temp + "WHERE ").append(formatWhere(delete.getWhere()));
        }

        if (delete.getOrderByElements() != null) {
            b.append(PlainSelect.orderByToString(delete.getOrderByElements()));
        }

        if (delete.getLimit() != null) {
            b.append(delete.getLimit());
        }
        return b.toString();
	}
	
	public String formatWhere(Expression expression) {
		String sql = "";
		Expression tmp = expression;
		//w1 LIKE 'C%' AND w2 = a OR w3 = a AND w4 = a OR w5 = a AND a IN (1) AND NOT EXISTS (SELECT 1) AND EXISTS (SELECT 1)
		while(tmp instanceof BinaryExpression && ((BinaryExpression)tmp).getLeftExpression() instanceof BinaryExpression) {
			BinaryExpression binaryExpression = (BinaryExpression) tmp;
			Expression rightExpression = binaryExpression.getRightExpression();
			//不是and or条件
			if(rightExpression instanceof ComparisonOperator 
					|| rightExpression instanceof LikeExpression
					|| rightExpression instanceof ExistsExpression
					|| rightExpression instanceof InExpression) {
				sql = "\r\n" + binaryExpression.getStringExpression() + " " + rightExpression.toString() + " " + sql;
			}else {
				sql = "\r\n" + binaryExpression.getStringExpression() + " " + formatWhere(rightExpression) + " " + sql;
			}
			tmp =  ((BinaryExpression)tmp).getLeftExpression();
		}
		return tmp + " " + sql;
	}
}
