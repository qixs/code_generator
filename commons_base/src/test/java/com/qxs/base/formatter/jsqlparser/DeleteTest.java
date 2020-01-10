package com.qxs.base.formatter.jsqlparser;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author qixingshen
 * **/
public class DeleteTest {
	
	@Test
	public void testDeleteAll() {
		String sql = "delete from a";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("DELETE FROM a", formatSql);
	}
	@Test
	public void testDeleteWhere1() {
		String sql = "delete from a where a = a";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("DELETE FROM a WHERE a = a ", formatSql);
	}
	@Test
	public void testDeleteWhere() {
		String sql = "delete from a where a = a and b = b and 1 = 1";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("DELETE FROM a\r\n" + 
				"WHERE a = a \r\n" + 
				"AND b = b \r\n" + 
				"AND 1 = 1 ", formatSql);
	}
}
