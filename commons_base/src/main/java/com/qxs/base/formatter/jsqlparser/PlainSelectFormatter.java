package com.qxs.base.formatter.jsqlparser;

import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

/**
 * @author qixingshen
 * **/
public class PlainSelectFormatter {
	
	private String tab;
	
	public PlainSelectFormatter() {
		this("");
	}
	public PlainSelectFormatter(String tab) {
		this.tab = tab;
	}
	
	public static String format(PlainSelectFormatter plainSelectFormatter, PlainSelect plainSelect) {
		StringBuilder sql = new StringBuilder();
		if (plainSelect.isUseBrackets()) {
			sql.append("(");
		}
		sql.append("SELECT ");

		if (plainSelect.getOracleHint() != null) {
			sql.append(plainSelect.getOracleHint()).append(" ");
		}

		if (plainSelect.getSkip() != null) {
			sql.append(plainSelect.getSkip()).append(" ");
		}

		if (plainSelect.getFirst() != null) {
			sql.append(plainSelect.getFirst()).append(" ");
		}

		if (plainSelect.getDistinct() != null) {
			sql.append(plainSelect.getDistinct()).append(" ");
		}
		if (plainSelect.getTop() != null) {
			sql.append(plainSelect.getTop()).append(" ");
		}
		sql.append(plainSelectFormatter.getStringList(plainSelect.getSelectItems()));

		if (plainSelect.getIntoTables() != null) {
			sql.append(" INTO ");
			for (Iterator<Table> iter = plainSelect.getIntoTables().iterator(); iter.hasNext();) {
				sql.append(iter.next().toString());
				if (iter.hasNext()) {
					sql.append(", ");
				}
			}
		}

		if (plainSelect.getFromItem() != null) {
			if(plainSelectFormatter.tab != null && plainSelectFormatter.tab.length() > 0) {
				sql.append(plainSelectFormatter.tab);
			}else {
				sql.append("");
			}
			sql.append("FROM ").append(plainSelect.getFromItem());
			if (plainSelect.getJoins() != null) {
				Iterator<Join> it = plainSelect.getJoins().iterator();
				while (it.hasNext()) {
					Join join = it.next();
					if (join.isSimple()) {
						sql.append(", ").append(join);
					} else {
						sql.append("\r\n").append(join);
					}
				}
			}
			if (plainSelect.getWhere() != null) {
				sql.append("\r\n");
				if(plainSelectFormatter.tab != null && plainSelectFormatter.tab.length() > 0) {
					sql.append(plainSelectFormatter.tab);
				}
				sql.append("WHERE ").append(plainSelectFormatter.formatWhere(plainSelect.getWhere()));
			}
			if (plainSelect.getOracleHierarchical() != null) {
				sql.append(plainSelect.getOracleHierarchical().toString());
			}
			sql.append(plainSelectFormatter.getFormatedList(plainSelect.getGroupByColumnReferences(), "GROUP BY"));
			if (plainSelect.getHaving() != null) {
				sql.append(" HAVING ").append(plainSelect.getHaving());
			}
			sql.append(plainSelectFormatter.orderByToString(plainSelect.isOracleSiblings(), plainSelect.getOrderByElements()));
			if (plainSelect.getLimit() != null) {
				sql.append(plainSelect.getLimit());
			}
			if (plainSelect.getOffset() != null) {
				sql.append(plainSelect.getOffset());
			}
			if (plainSelect.getFetch() != null) {
				sql.append(plainSelect.getFetch());
			}
			if (plainSelect.isForUpdate()) {
				sql.append(" FOR UPDATE");

				if (plainSelect.getForUpdateTable() != null) {
					sql.append(" OF ").append(plainSelect.getForUpdateTable());
				}

				if (plainSelect.getWait() != null) {
					// Wait's toString will do the formatting for us
					sql.append(plainSelect.getWait());
				}
			}
		} else {
			// without from
			if (plainSelect.getWhere() != null) {
				sql.append(" WHERE ").append(plainSelectFormatter.formatWhere(plainSelect.getWhere()));
			}
		}
		if (plainSelect.isUseBrackets()) {
			sql.append(")");
		}
		return sql.toString();
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
				sql = "\r\n" + tab + binaryExpression.getStringExpression() + " " + rightExpression.toString() + " " + sql;
			}else {
				sql = "\r\n" + tab + binaryExpression.getStringExpression() + " " + formatWhere(rightExpression) + " " + sql;
			}
			tmp =  ((BinaryExpression)tmp).getLeftExpression();
		}
		return tmp + " " + sql;
	}

	public String getStringList(List<?> list) {
		return getStringList(list, true, false);
	}

	public String orderByToString(boolean oracleSiblings, List<OrderByElement> orderByElements) {
		return getFormatedList(orderByElements, oracleSiblings ? "ORDER SIBLINGS BY" : "ORDER BY");
	}

	public String getFormatedList(List<?> list, String expression) {
		return getFormatedList(list, expression, true, false);
	}

	public String getFormatedList(List<?> list, String expression, boolean useComma, boolean useBrackets) {
		String sql = getStringList(list, useComma, useBrackets);

		if (sql.length() > 0) {
			if (expression.length() > 0) {
				sql = " " + expression + " " + sql;
			} else {
				sql = " " + sql;
			}
		}

		return sql;
	}

	public String getStringList(List<?> list, boolean useComma, boolean useBrackets) {
		StringBuilder ans = new StringBuilder();
		// String ans = "";
		String comma = ",";
		if (!useComma) {
			comma = "";
		}
		if (list != null) {
			if (useBrackets) {
				ans.append("(");
				// ans += "(";
			}
			
			if(list.size() > 1) {
				ans.append("\r\n");
			}
			for (int i = 0; i < list.size(); i++) {
				if(tab != null && tab.length() > 0) {
					ans.append(tab);
				}
				if(list.size() > 1) {
					ans.append("\t");
				}
				if(list.get(i) instanceof AllColumns || list.get(i) instanceof AllTableColumns){
					ans.append(list.get(i)).append(" ");
				}else{
					ans.append(list.get(i)).append((i < list.size() - 1) ? comma + " " : "");
				}

				if(list.size() > 1) {
					ans.append("\r\n");
				}
			}
			
			if (useBrackets) {
				ans.append(")");
				// ans += ")";
			}
		}
		
		return ans.toString();
	}
}
