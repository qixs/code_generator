package com.qxs.base.formatter.jsqlparser;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author qixingshen
 * **/
public class UpdateTest {
	
	@Test
	public void testUpdate3() {
		String sql = "update a set a = a , b = b , c = c where a= a and b = b and c = c";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("UPDATE a SET a = a,b = b,c = c\r\n" + 
				"WHERE a = a \r\n" + 
				"AND b = b \r\n" + 
				"AND c = c ", formatSql);
	}
	@Test
	public void testUpdate1() {
		String sql = "update a set a = a where a= a and b = b and c = c";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("UPDATE a SET a = a\r\n" + 
				"WHERE a = a \r\n" + 
				"AND b = b \r\n" + 
				"AND c = c ", formatSql);
	}
	@Test
	public void testUpdate4() {
		String sql = "update a set a = a, a= b , c = d, a = a where a= a and b = b and c = c";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("UPDATE a SET \r\n" + 
				"	a = a,\r\n" + 
				"	a = b,\r\n" + 
				"	c = d,\r\n" + 
				"	a = a\r\n" + 
				"WHERE a = a \r\n" + 
				"AND b = b \r\n" + 
				"AND c = c ", formatSql);
	}
	@Test
	public void testUpdateWhere1() {
		String sql = "update a set a = a where a= a";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("UPDATE a SET a = a WHERE a = a ", formatSql);
	}
	@Test
	public void testUpdateWhereNo() {
		String sql = "update a set a = a";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("UPDATE a SET a = a", formatSql);
	}
}
