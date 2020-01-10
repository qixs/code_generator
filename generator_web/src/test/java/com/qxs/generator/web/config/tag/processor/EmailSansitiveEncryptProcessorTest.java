package com.qxs.generator.web.config.tag.processor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class EmailSansitiveEncryptProcessorTest {
	
	@Test
	public void encrypt() {
		EmailSansitiveEncryptProcessor processor = new EmailSansitiveEncryptProcessor("tt", "tt", 1);
		
		List<Model> list = new ArrayList<>();
		list.add(new Model("123@qq.com", "123@qq.com"));
		list.add(new Model("123123@qq.com", "123123@qq.com"));
		list.add(new Model("1231234@qq.com", "123***234@qq.com"));
		
		
		for(Model model : list) {
			Assert.assertEquals(model.getExpected(), processor.encrypt(model.getSource()));
		}
		
	}
	
	private class Model{
		private String source;
		private String expected;
		
		public Model(String source,String expected) {
			this.source = source;
			this.expected = expected;
		}
		public String getSource() {
			return source;
		}
		
		public String getExpected() {
			return expected;
		}
		
	}
}
