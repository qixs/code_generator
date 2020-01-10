package com.qxs.base.formatter.jsqlparser;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author qixingshen
 * **/
public class InsertTest {
	
	@Test
	public void testInsertToSelect() {
		String sql = "insert into a(a,b,v,d,e) select * from a left join a where a = a";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("INSERT INTO a (a, b, v, d, e) \r\n" +
				"SELECT * FROM a\r\n" +
				"LEFT JOIN a\r\n" +
				"WHERE a = a ", formatSql);
	}

	@Test
	public void testInsertToAll() {
		String sql = "insert into a values(?,?)";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("INSERT INTO a \r\n" +
				"VALUES (?,?) ", formatSql);
	}
	@Test
	public void testInsertToColumn() {
		String sql = "insert into a(c1,c2) values(?,?)";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("INSERT INTO a (c1, c2) \r\n" +
				"VALUES (?,?) ", formatSql);
	}
	@Test
	public void testInsertToColumn6() {
		String sql = "insert into a(c1,c2,c1,c2,c1,c2) values(?,?,?,?,?,?)";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("INSERT INTO a (\r\n" +
				"\tc1,\r\n" +
				"\tc2,\r\n" +
				"\tc1,\r\n" +
				"\tc2,\r\n" +
				"\tc1,\r\n" +
				"\tc2\r\n" +
				") \r\n" +
				"VALUES (\r\n" +
				"\t?,\r\n" +
				"\t?,\r\n" +
				"\t?,\r\n" +
				"\t?,\r\n" +
				"\t?,\r\n" +
				"\t?\r\n" +
				") ", formatSql);
	}
}
