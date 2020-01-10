package com.qxs.base.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.springframework.util.StreamUtils;

/**
 * @author qixingshen
 * **/
public class ZipFileTest {
	
//	@Test
	public void compress() throws IOException{
		String projectPath = ProjectUtil.getProjectPath(ZipFileTest.class);
		File f = new File(projectPath+"../../..");
		List<File> files = readAllFiles(f);
		
		ZipFile zipFile = new ZipFile();
		
		for(File file : files) {
			zipFile.addFile(StreamUtils.copyToByteArray(new FileInputStream(file)), file.getName());
		}
		
		String userHome = System.getProperty("user.home").replaceAll("\\\\", "/");
		userHome = userHome.endsWith("/") ? userHome : userHome + "/";
		ByteArrayOutputStream byteArrayOutputStream = zipFile.compress();
		File zipF = new File(userHome+"temp/ZipFileTest.zip");
		if(zipF.exists()) {
			zipF.delete();
			System.gc();
		}
		
		new File(userHome+"temp").mkdirs();
		
		FileOutputStream fileOutputStream = new FileOutputStream(zipF);
		fileOutputStream.write(byteArrayOutputStream.toByteArray());
		fileOutputStream.flush();
		fileOutputStream.close();
		byteArrayOutputStream.close();
		
		InputStream inputStream = new FileInputStream(zipF);
		
		boolean a = inputStream != null;
		
		if(a) {
			inputStream.close();
			System.gc();
			zipF.delete();
		}
		
		Assert.assertTrue(a);
	}
	
	private List<File> readAllFiles(File f){
		List<File> fileList = new ArrayList<File>();
		File[] files = f.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(Arrays.asList(new String[] {".project"}).contains(name)) {
					return false;
				}
				return !dir.isFile() || name.toLowerCase().endsWith(".class") || name.toLowerCase().endsWith(".java") || name.toLowerCase().endsWith(".sql");
			}
		});
		for(File file : files) {
			if(!file.isFile()) {
				fileList.addAll(readAllFiles(file));
			}else {
				fileList.add(file);
			}
		}
		return fileList;
	}
	
	
}
