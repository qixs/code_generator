package com.qxs.base.util;

import java.net.BindException;
import java.sql.SQLException;

import org.junit.Assert;

import com.jcraft.jsch.JSchException;

/**
 * @author qixingshen
 * **/
public class SshClientTest {
	
//	@Test
	public void forward() throws JSchException, BindException, ClassNotFoundException, SQLException {
		SshClient sshClient = new SshClient("47.104.198.224", "test", "q123456Q!");
		sshClient.connect();
		int port = sshClient.forward("localhost","172.31.96.71", 3306);
		sshClient.disconnect();
		Assert.assertTrue(port > 0);
		
	}
	
}
