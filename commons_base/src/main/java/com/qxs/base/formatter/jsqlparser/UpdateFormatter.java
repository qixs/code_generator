package com.qxs.base.formatter.jsqlparser;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.update.Update;

/**
 * @author qixingshen
 * **/
public class UpdateFormatter {
	
	/**
	 * 最大并排的列数(列数超过该个数则竖向排列)
	 * **/
	private static final int MAX_TRANSVERSE_COLUMN_SIZE = 3;

	public String format(Update update) {
		StringBuilder b = new StringBuilder("UPDATE ");
        b.append(PlainSelect.getStringList(update.getTables(), true, false)).append(" SET ");

        if (!update.isUseSelect()) {
        	String temp = update.getColumns().size() > MAX_TRANSVERSE_COLUMN_SIZE ? "\r\n\t" : "";
        	
            for (int i = 0; i < update.getColumns().size(); i++) {
                if (i != 0) {
                    b.append(",");
                }
                b.append(temp + update.getColumns().get(i)).append(" = ");
                b.append(update.getExpressions().get(i));
            }
        } else {
            if (update.isUseColumnsBrackets()) {
                b.append("(");
            }
            for (int i = 0; i < update.getColumns().size(); i++) {
                if (i != 0) {
                    b.append(", ");
                }
                b.append(update.getColumns().get(i));
            }
            if (update.isUseColumnsBrackets()) {
                b.append(")");
            }
            b.append(" = ");
            b.append("(").append(update.getSelect()).append(")");
        }

        if (update.getFromItem() != null) {
            b.append(" FROM ").append(update.getFromItem());
            if (update.getJoins() != null) {
                for (Join join : update.getJoins()) {
                    if (join.isSimple()) {
                        b.append(", ").append(join);
                    } else {
                        b.append(" ").append(join);
                    }
                }
            }
        }

        if (update.getWhere() != null) {
        	String temp = update.getWhere() instanceof AndExpression
                    || update.getWhere() instanceof OrExpression
                    || update.getColumns().size() > MAX_TRANSVERSE_COLUMN_SIZE ? "\r\n" : " ";
            b.append(temp + "WHERE ");
            b.append(formatWhere(update.getWhere()));
        }
        if (update.getOrderByElements() != null) {
            b.append(PlainSelect.orderByToString(update.getOrderByElements()));
        }
        if (update.getLimit() != null) {
            b.append(update.getLimit());
        }

        if (update.isReturningAllColumns()) {
            b.append(" RETURNING *");
        } else if (update.getReturningExpressionList() != null) {
            b.append(" RETURNING ").append(PlainSelect.
                    getStringList(update.getReturningExpressionList(), true, false));
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
