package com.qxs.base.util;

import java.io.File;


/**
 * 操作系统类型
 * @author qxs
 */
public enum OS {
	/**
	 * AIX[1]  （Advanced Interactive eXecutive）是IBM基于AT&T Unix System V开发的一套类UNIX操作
	 * 系统，运行在IBM专有的Power系列芯片设计的小型机硬件系统之上。它符合Open group的UNIX 98行业标准
	 * （The Open Group UNIX 98 Base Brand），通过全面集成对32-位和64-位应用的并行运行支持，
	 * 为这些应用提供了全面的可扩展性。它可以在所有的IBM ~ p系列和IBM RS/6000工作站、服务器和大型并行超级计算机
	 * 上运行。
	 * **/
	AIX,
	/**
	 * 苹果操作系统
	 * **/
	MAC,
	/**
	 * Linux
	 * **/
	LINUX,
	/***
	 * Unix
	 * **/
	UNIX,
	/***
	 * windows
	 * **/
	WINDOWS,
	/***
	 * UNKNOWN
	 * **/
	UNKNOWN;
	
	private static final OS OS;
	
	static{
		String osName = System.getProperty("os.name").toLowerCase();
		
		if("aix".equals(osName)){
			OS = AIX;
		}else if ("darwin".contains(osName) || "mac".contains(osName)) {
			OS = MAC;
		}else if ("linux".contains(osName)) {
			OS = LINUX;
		}else if (":".equals(File.pathSeparator)) {
			OS = UNIX;
		}else if (";".equals(File.pathSeparator)) {
			OS = WINDOWS;
		}else{
			OS = UNKNOWN;
		}
	}
	
	public static final OS getOs() {
		return OS;
	}
}