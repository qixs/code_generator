package com.qxs.base.database.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author qixingshen
 * **/
public class DataTypeConfigTest {
	
	@Test
	public void test() {
		Assert.assertEquals(DataTypeConfig.getJavaType("mysql", "varchar"), String.class.getName());
		Assert.assertEquals(DataTypeConfig.getJavaType("oracle", "varchar"), String.class.getName());
		Assert.assertEquals(DataTypeConfig.getJavaType("oracle", "varchar2"), String.class.getName());
		Assert.assertEquals(DataTypeConfig.getJavaType("microsoftsqlserver", "varchar"), String.class.getName());
		Assert.assertEquals(DataTypeConfig.getJavaType("mariadb", "varchar"), String.class.getName());
		Assert.assertEquals(DataTypeConfig.getJavaType("postgresql", "varchar"), String.class.getName());
		Assert.assertEquals(DataTypeConfig.getJavaType("sqlite", "text"), String.class.getName());
	}
}
