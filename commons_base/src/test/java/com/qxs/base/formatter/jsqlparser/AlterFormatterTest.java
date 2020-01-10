package com.qxs.base.formatter.jsqlparser;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author qixingshen
 * **/
public class AlterFormatterTest {
	
	@Test
	public void testOne() {
		String sql = "ALTER TABLE Persons\r\n" + 
				"ADD "
				+ "Birthday date ";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql.replaceAll("\\s+", " "));
//		System.out.println(sql.replaceAll("\\s+", " "));
//		System.out.println();
		Assert.assertEquals("ALTER TABLE Persons ADD COLUMN Birthday date", formatSql.replaceAll("\\s+", " "));
	}
	
	@Test
	public void testMore() {
		String sql = "alter table T_BA_OVER_IOU\r\n" + 
				"add (\r\n" + 
				"COAGENCY_CODE VARCHAR2(50 BYTE) ,\r\n" + 
				"DEAL_STATUS VARCHAR2(30 BYTE) ,\r\n" + 
				"RESULT_DESC VARCHAR2(3000 BYTE) ,\r\n" + 
				"EXTEND_DAYS NUMBER \r\n" + 
				")";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
//		System.out.println();
		Assert.assertEquals(formatSql, "ALTER TABLE T_BA_OVER_IOU \r\n" + 
				"ADD (\r\n" + 
				" COAGENCY_CODE VARCHAR2 (50, BYTE),\r\n" + 
				" DEAL_STATUS VARCHAR2 (30, BYTE),\r\n" + 
				" RESULT_DESC VARCHAR2 (3000, BYTE),\r\n" + 
				" EXTEND_DAYS NUMBER\r\n" + 
				")");
	}
	
}
