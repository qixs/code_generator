package com.qxs.base.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 压缩文件生成类
 * @author <a href="mailto:459789479@qq.com">qixingshen</a>
 * @date 2018-1-26
 * @version Revision: 1.0
 * **/
public class ZipFile {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZipFile.class);
	
	private List<ZipFileEntry> fileEntryList = new ArrayList<ZipFile.ZipFileEntry>();
	
	/**
	 * 添加文件
	 * 
	 * @param bytes 文件数据
	 * @param fileName 文件名
	 * @return void
	 * **/
	public void addFile(byte[] bytes,String fileName) {
		fileEntryList.add(new ZipFileEntry(bytes, fileName));
	}
	/**
	 * 生成压缩文件(zip文件)
	 * 
	 * @return ByteArrayOutputStream 压缩文件流
	 * **/
	public ByteArrayOutputStream compress() {
		if(fileEntryList.size() == 0) {
			throw new RuntimeException("文件列表为空");
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ZipOutputStream zipStream = new ZipOutputStream(stream);
		
		for(ZipFileEntry zipFileEntry : fileEntryList){
			ZipEntry ze = new ZipEntry(zipFileEntry.getFileName());
			try {
				zipStream.putNextEntry(ze);
				zipStream.write(zipFileEntry.getBytes());
			} catch (IOException e) {
				LOGGER.error(e.getMessage(),e);
			}
		}
		try {
			zipStream.flush();
			zipStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream;
	}
	
	private class ZipFileEntry{
		/**
		 * 文件数据
		 * **/
		private byte[] bytes;
		/**
		 * 文件名
		 * **/
		private String fileName;
		/**
		 * @param bytes 文件数据
		 * @param fileName 文件名
		 * **/
		private ZipFileEntry(byte[] bytes,String fileName) {
			this.bytes = bytes;
			this.fileName = fileName;
		}
		/**
		 * 获取文件数据
		 * @return byte[]
		 * **/
		public byte[] getBytes() {
			return bytes;
		}
		/**
		 * 获取文件名
		 * @return String
		 * **/
		public String getFileName() {
			return fileName;
		}
		
	}
}
