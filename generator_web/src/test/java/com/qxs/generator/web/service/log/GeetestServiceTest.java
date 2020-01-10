package com.qxs.generator.web.service.log;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.qxs.generator.web.model.config.Geetest;
import com.qxs.generator.web.service.config.IGeetestService;
import com.qxs.generator.web.service.config.impl.GeetestServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:test.xml" }) // 加载配置文件
@SpringBootTest(properties="classpath*:application.yml")
public class GeetestServiceTest {
	
	@Autowired
	private IGeetestService geetestService;
	
	@Before
	@Transactional
	public void before() {
		List<Geetest> geetestList = geetestService.findAll(null);
		
		geetestService.deleteByIds(geetestList.stream().map(Geetest::getId).collect(Collectors.toList()));
		
	}
	@After
	@Transactional
	public void after() {
		List<Geetest> geetestList = geetestService.findAll(null);
		
		geetestService.deleteByIds(geetestList.stream().map(Geetest::getId).collect(Collectors.toList()));
		
		Geetest geetest = new Geetest();
		geetest.setId("ca98f9688c2f3c5abee8f14b496e5375");
		geetest.setKey("be29c5e6d597b97a2a27e3071245dcd3");
		geetest.setWeight(1);
		geetestService.insert(geetest);
	}
	@Test
	public void testNoConfig() {
		Geetest geetest = geetestService.nextGeetest();
		Assert.assertNull(geetest);
	}
	@Test
	public void test1Config() {
		Geetest geetest = new Geetest();
		geetest.setId("ca98f9688c2f3c5abee8f14b496e5375");
		geetest.setKey("be29c5e6d597b97a2a27e3071245dcd3");
		geetest.setWeight(1);
		geetestService.insert(geetest);
		
		Geetest geetest2 = geetestService.nextGeetest();
		Assert.assertNotNull(geetest2);
		Assert.assertEquals(geetest.getId(), geetest2.getId());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test10Config() {
		int size = 0;
		for(int i = 0 ; i < 10 ; i ++) {
			Geetest geetest = new Geetest();
			geetest.setId("id_"+i);
			geetest.setKey("key_"+i);
			geetest.setWeight(i);
			geetestService.insert(geetest);
			
			size = size + i;
		}
		for(int i = 0 ; i < 10 ; i ++) {
			Geetest geetest = geetestService.nextGeetest();
			Assert.assertNotNull(geetest);
		}
		
		
		try {
			Field field = GeetestServiceImpl.class.getDeclaredField("GEETEST_LIST");
			field.setAccessible(true);
			List<Geetest> geetestList = (List<Geetest>)field.get(geetestService);
			Assert.assertEquals(size, geetestList.size());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
}
