package com.qxs.generator.web.service.notice.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:test.xml" }) // 加载配置文件
@SpringBootTest(properties="classpath*:application.yml")
public class NoticeMailServiceTest {
	
	@Autowired
	private INoticeMailService noticeMailService;
	
	@Test
	public void sendActiveAccountMail() {
		noticeMailService.sendActiveAccountMail("code_generator@126.com", 30, "http://www.qb.comwww.qb.comwww.qb.comwww.qb.comwww.qb.comwww.qb.comwww.qb.comwww.qb.comwww.qb.comwww.qb.comwww.qb.comwww.qb.comwww.qb.com");
		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	
	@Test
	public void sendPasswordModifySuccessMail() {
		noticeMailService.sendPasswordModifySuccessMail("code_generator@126.com");
		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	@Test
	public void sendResetPasswordMail() {
		noticeMailService.sendResetPasswordMail("code_generator@126.com",30, "http://aaaaa");
		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	@Test
	public void sendResetPasswordSuccessMail() {
		noticeMailService.sendResetPasswordSuccessMail("code_generator@126.com", null);
		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
}
