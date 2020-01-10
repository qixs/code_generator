package com.qxs.plugin.factory;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.qxs.base.util.ApplicationContextUtil;
import com.qxs.base.util.ProjectUtil;

/**
 * @author qixingshen
 * **/
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({ "classpath*:test.xml" }) // 加载配置文件
public class PluginLoaderTest {

	@Autowired
	private PluginLoader pluginLoader;
	@Autowired
	private ApplicationContext context;

	
	// @Test
	public void loadAllPlugin()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// test bean
		
		Class<?> clazz = ApplicationContextUtil.class;
		Field field = clazz.getDeclaredField("applicationContext");
		field.setAccessible(true);// 只有这里/..设置为true才可以修改
		field.set(null, context);

		pluginLoader.loadAllPlugin(ProjectUtil.getProjectPath(getClass()));

		Assert.assertNotNull(pluginLoader.getPluginConfigList());
		Assert.assertTrue(pluginLoader.getPluginConfigList().size() > 0);
	}

//	@Test
	public void test()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// test bean
		Class<?> clazz = ApplicationContextUtil.class;
		Field field = clazz.getDeclaredField("context");
		field.setAccessible(true);// 只有这里设置为true才可以修改
		field.set(null, context);

		String name = pluginLoader.loadPlugin(ProjectUtil.getProjectPath(getClass())+"generator_plugin_entity-1.0.jar");

		Assert.assertNotNull("entity".equals(name));

		// System.out.println(context.getBean(pluginLoader.getPluginConfig(name).getGeneratorClass()));
//
//		Assert.assertNotNull(context.getBean(pluginLoader.getPluginConfig(name).getGeneratorClass()));
//		Assert.assertTrue(
//				context.getBean(pluginLoader.getPluginConfig(name).getGeneratorClass()) instanceof IGenerator);
//
//		System.out.println(ApplicationContextUtil.getApplicationContext());
//
//		Assert.assertNotNull(ApplicationContextUtil.getApplicationContext());
//
//		System.out.println(ApplicationContextUtil.getApplicationContext().getBean(GeneratorTest.class));
//		Assert.assertNotNull(ApplicationContextUtil.getApplicationContext().getBean(GeneratorTest.class));
//
//		Assert.assertTrue(ApplicationContextUtil.getApplicationContext().getBean(GeneratorTest.class) instanceof IGenerator);
	}
}
