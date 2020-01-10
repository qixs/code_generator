package com.qxs.generator.web.config.tag.processor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class MobileSansitiveEncryptProcessorTest {
	
	@Test
	public void encrypt() {
		MobileSansitiveEncryptProcessor processor = new MobileSansitiveEncryptProcessor("t", "t", 1);
		
		List<Model> list = new ArrayList<>();
		list.add(new Model("15100001111", "151****1111"));
		list.add(new Model("8615100001111", "86151****1111"));
		list.add(new Model("+8615100001111", "+86151****1111"));
		
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
