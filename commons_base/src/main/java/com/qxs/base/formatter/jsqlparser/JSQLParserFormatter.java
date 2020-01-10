package com.qxs.base.formatter.jsqlparser;

import com.qxs.base.exception.SQLFormatException;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;

/**
 * @author qixingshen
 * **/
public class JSQLParserFormatter {

	public static String format(String sql) {
		try {
			return format(CCJSqlParserUtil.parse(sql));
		} catch (JSQLParserException e) {
			throw new SQLFormatException("sql格式化失败", e);
		}
	}

	public static String format(Statement statement) {
		if(statement instanceof Alter) {
			return new AlterFormatter().format((Alter)statement);
		}else if(statement instanceof AlterView || statement instanceof CreateView) {
			//TODO
		}else if(statement instanceof CreateIndex) {
			//TODO
		}else if(statement instanceof CreateTable) {
			//TODO
		}else if(statement instanceof Delete) {
			return new DeleteFormatter().format((Delete)statement);
		}else if(statement instanceof Insert) {
			return new InsertFormatter().format((Insert)statement);
		}else if(statement instanceof Merge) {
			//TODO
		}else if(statement instanceof Replace) {
			//TODO
		}else if(statement instanceof Select) {
			return new SelectFormatter().format((Select)statement);
		}else if(statement instanceof SetStatement) {
			//TODO
		}else if(statement instanceof Update) {
			return new UpdateFormatter().format((Update)statement);
		}else if(statement instanceof Upsert) {
			//TODO
		}

		return statement.toString();
	}

}
