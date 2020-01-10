package com.qxs.base.formatter.jsqlparser;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author qixingshen
 * **/
public class SelectTest {
	
	@Test
	public void test1() {
		String sql = "select \t * from \r\n t";
		String formatSql = JSQLParserFormatter.format(sql);
		System.out.println(formatSql);
		Assert.assertEquals(sql.replaceAll("\\s+", " ").toLowerCase(), formatSql.toLowerCase());
	}
	@Test
	public void test2() {
		String sql = "select \t * from \r\n t where t.a = '' and b = ''";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals(("SELECT * FROM t\r\n" + 
				"WHERE t.a = '' \r\n" + 
				"AND b = '' ").toLowerCase(), formatSql.toLowerCase());
	}
	@Test
	public void testJoin(){
		String sql = "select a.* from a left join b on a.a = b.b and a.a = b.b or a.a = b.b inner join a on a = c left outer join a on a = a right join b on a = a join c on c=c where a =a or a = a and a=a";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("SELECT a.* FROM a\r\n" +
				"LEFT JOIN b ON a.a = b.b AND a.a = b.b OR a.a = b.b\r\n" +
				"INNER JOIN a ON a = c\r\n" +
				"LEFT OUTER JOIN a ON a = a\r\n" +
				"RIGHT JOIN b ON a = a\r\n" +
				"JOIN c ON c = c\r\n" +
				"WHERE a = a \r\n" +
				"OR a = a \r\n" +
				"AND a = a  ", formatSql);
	}
	@Test
	public void testUnion(){
		String sql = "select * from a union all select * from a union select * from a";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("SELECT * FROM a\r\n" +
				"UNION ALL\r\n" +
				"SELECT * FROM a\r\n" +
				"UNION\r\n" +
				"SELECT * FROM a", formatSql);
	}
	@Test
	public void testWith() {
		String sql = "with\r\n" + 
				"cr as\r\n" + 
				"(\r\n" + 
				"    select c1,c2,c3 from person.CountryRegion "
				+ "where w1 like 'C%' and w2=a or w3 = a and w4 = a or w5 = a and a in (1) and not exists(select 1) and exists(select 1)" + 
				")\r\n" + 
				"\r\n" + 
				"select * from person.StateProvince where CountryRegionCode in (select * from cr)";
		String formatSql = JSQLParserFormatter.format(sql);
//		System.out.println(formatSql);
		Assert.assertEquals("WITH cr AS (\r\n" + 
				"	SELECT \r\n" + 
				"		c1, \r\n" + 
				"		c2, \r\n" + 
				"		c3\r\n" + 
				"	FROM person.CountryRegion\r\n" + 
				"	WHERE w1 LIKE 'C%' \r\n" + 
				"	AND w2 = a \r\n" + 
				"	OR w3 = a \r\n" + 
				"	AND w4 = a  \r\n" + 
				"	OR w5 = a \r\n" + 
				"	AND a IN (1) \r\n" + 
				"	AND NOT EXISTS (SELECT 1) \r\n" + 
				"	AND EXISTS (SELECT 1)  \r\n" + 
				")\r\n" + 
				"SELECT * FROM person.StateProvince\r\n" + 
				"WHERE CountryRegionCode IN (SELECT * FROM cr) ", formatSql);
	}
}
