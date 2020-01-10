//package com.qxs.generator.web.service.plugin;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.qxs.generator.web.constant.IntConstants;
//import com.qxs.generator.web.exception.BusinessException;
//import com.qxs.generator.web.model.plugin.Plugin;
//import com.qxs.generator.web.repository.plugin.IPluginRepository;
//
//
////@RunWith(SpringJUnit4ClassRunner.class)
////@ContextConfiguration({ "classpath*:test.xml" }) // 加载配置文件
////@SpringBootTest(properties="classpath*:application.yml")
//public class PluginServiceTest {
//
//	@Autowired
//	private IPluginService pluginService;
//	@Autowired
//	private IPluginRepository pluginRepository;
//
//	@After
//	public void after() {
//		List<Plugin> pluginList = pluginRepository.findAll();
//		List<String> ids = pluginList.stream().map(Plugin::getId).collect(Collectors.toList());
//		pluginRepository.updateStatusByIds(IntConstants.STATUS_ENABLE.getCode(), ids);
//	}
//
////	@Test
//	public void disablePlugins1() {
//		String pluginName = "entity";
//		Plugin plugin = pluginService.getPluginByName(pluginName);
//
//		try {
//			//禁用entity必须报错
//			pluginService.disablePlugin(plugin.getId());
//
//			Assert.assertTrue(false);
//		}catch(BusinessException e) {
//			Assert.assertTrue(true);
//		}
//	}
////	@Test
//	public void disablePlugins2() {
//		String pluginName = "serviceImpl";
//		Plugin plugin = pluginService.getPluginByName(pluginName);
//
//		pluginService.disablePlugin(plugin.getId());
//
//		plugin = pluginService.getPluginByName(pluginName);
//
//		Assert.assertEquals(IntConstants.STATUS_DISABLE.getCode(), plugin.getStatus().intValue());
//	}
//
////	@Test
//	public void disablePlugins3() {
//		String[] pluginNames = new String[]{"serviceImpl","serviceInterface"};
//		List<Plugin> plugins = pluginService.findPluginByNames(Arrays.asList(pluginNames));
//		List<String> ids = plugins.stream().map(Plugin::getId).collect(Collectors.toList());
//		pluginService.disablePlugins(ids);
//
//		plugins = pluginService.findPluginByNames(Arrays.asList(pluginNames));
//
//		for(Plugin plugin : plugins) {
//			Assert.assertEquals(IntConstants.STATUS_DISABLE.getCode(), plugin.getStatus().intValue());
//		}
//	}
//
////	@Test
//	public void enablePlugins1() {
//		List<Plugin> pluginList = pluginRepository.findAll();
//		List<String> ids = pluginList.stream().map(Plugin::getId).collect(Collectors.toList());
//		pluginRepository.updateStatusByIds(IntConstants.STATUS_DISABLE.getCode(), ids);
//
//		String pluginName = "dao";
//		Plugin plugin = pluginService.getPluginByName(pluginName);
//
//		try {
//			//启用dao必须报错
//			pluginService.enablePlugin(plugin.getId());
//
//			Assert.assertTrue(false);
//		}catch(BusinessException e) {
//			Assert.assertTrue(true);
//		}
//	}
////	@Test
//	public void enablePlugins2() {
//		List<Plugin> pluginList = pluginRepository.findAll();
//		List<String> ids = pluginList.stream().map(Plugin::getId).collect(Collectors.toList());
//		pluginRepository.updateStatusByIds(IntConstants.STATUS_DISABLE.getCode(), ids);
//
//		String pluginName = "entity";
//		Plugin plugin = pluginService.getPluginByName(pluginName);
//
//		pluginService.enablePlugin(plugin.getId());
//
//		plugin = pluginService.getPluginByName(pluginName);
//
//		Assert.assertEquals(IntConstants.STATUS_ENABLE.getCode(), plugin.getStatus().intValue());
//	}
//
////	@Test
//	public void enablePlugins3() {
//		List<Plugin> pluginList = pluginRepository.findAll();
//		List<String> ids1 = pluginList.stream().map(Plugin::getId).collect(Collectors.toList());
//		pluginRepository.updateStatusByIds(IntConstants.STATUS_DISABLE.getCode(), ids1);
//
//		String[] pluginNames = new String[]{"dao","entity"};
//		List<Plugin> plugins = pluginService.findPluginByNames(Arrays.asList(pluginNames));
//		List<String> ids = plugins.stream().map(Plugin::getId).collect(Collectors.toList());
//		pluginService.enablePlugins(ids);
//
//		plugins = pluginService.findPluginByNames(Arrays.asList(pluginNames));
//
//		for(Plugin plugin : plugins) {
//			Assert.assertEquals(IntConstants.STATUS_ENABLE.getCode(), plugin.getStatus().intValue());
//		}
//	}
//
//}
