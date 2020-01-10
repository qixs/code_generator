package com.qxs.generator.web.util;

import org.junit.Assert;
import org.junit.Test;


public class JavaSourceCodeUtilTest {
	@Test
	public void getClassFullName() {
		String class1 = "package   com.qxs.generator.web.util ;  \r\n"
				+ "public    class  JavaSourceCodeUtilTest{\r\n"
				+ "}";
		Assert.assertEquals("com.qxs.generator.web.util.JavaSourceCodeUtilTest", JavaSourceCodeUtil.getClassFullName(class1));
		
		String class2 = "/**\r\n"
				+ "* dafaf;\r\n"
				+ "**/\r\n"
				+ "package  com.qxs.generator.web.util;\r\n"
				+ "public class     JavaSourceCodeUtilTest{\r\n"
				+ "}\r\n";
		
		Assert.assertEquals("com.qxs.generator.web.util.JavaSourceCodeUtilTest", JavaSourceCodeUtil.getClassFullName(class2));
		
		String class3 = "/**\r\n"
				+ "* daf;;;af\r\n"
				+ "**/\r\n"
				+ "package com.qxs.generator.web.util;\r\n"
				+ "/**;;;\r\n"
				+ "* daf\r\n"
				+ "**/\r\n"
				+ "public class JavaSourceCodeUtilTest{\r\n"
				+ "}\r\n\r\n";
		
		Assert.assertEquals("com.qxs.generator.web.util.JavaSourceCodeUtilTest", JavaSourceCodeUtil.getClassFullName(class3));
		
	}
	@Test
	public void getClassSimpleName() {
		String class1 = "package   com.qxs.generator.web.util ;  \r\n"
				+ "public    class  JavaSourceCodeUtilTest{\r\n"
				+ "}";
		Assert.assertEquals("JavaSourceCodeUtilTest", JavaSourceCodeUtil.getClassSimpleName(class1));
		
		String class2 = "/**\r\n"
				+ "* dafaf;\r\n"
				+ "**/\r\n"
				+ "package  com.qxs.generator.web.util;\r\n"
				+ "public class     JavaSourceCodeUtilTest{\r\n"
				+ "}\r\n";
		
		Assert.assertEquals("JavaSourceCodeUtilTest", JavaSourceCodeUtil.getClassSimpleName(class2));
		
		String class3 = "/**\r\n"
				+ "* daf;;;af\r\n"
				+ "**/\r\n"
				+ "package com.qxs.generator.web.util;\r\n"
				+ "/**;;;\r\n"
				+ "* daf\r\n"
				+ "**/\r\n"
				+ "public class JavaSourceCodeUtilTest{\r\n"
				+ "}\r\n\r\n";
		
		Assert.assertEquals("JavaSourceCodeUtilTest", JavaSourceCodeUtil.getClassSimpleName(class3));
		
	}
	@Test
	public void getClassPackageName() {
		String class1 = "package   com.qxs.generator.web.util ;  \r\n"
				+ "public    class  JavaSourceCodeUtilTest{\r\n"
				+ "}";
		Assert.assertEquals("com.qxs.generator.web.util", JavaSourceCodeUtil.getClassPackageName(class1));
		
		String class2 = "/**\r\n"
				+ "* dafaf;\r\n"
				+ "**/\r\n"
				+ "package  com.qxs.generator.web.util;\r\n"
				+ "public class     JavaSourceCodeUtilTest{\r\n"
				+ "}\r\n";
		
		Assert.assertEquals("com.qxs.generator.web.util", JavaSourceCodeUtil.getClassPackageName(class2));
		
		String class3 = "/**\r\n"
				+ "* daf;;;af\r\n"
				+ "**/\r\n"
				+ "package com.qxs.generator.web.util;\r\n"
				+ "/**;;;\r\n"
				+ "* daf\r\n"
				+ "**/\r\n"
				+ "public class JavaSourceCodeUtilTest{\r\n"
				+ "}\r\n\r\n";
		
		Assert.assertEquals("com.qxs.generator.web.util", JavaSourceCodeUtil.getClassPackageName(class3));
		
	}
}
