package com.qxs.generator.web.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.qxs.generator.web.exception.BusinessException;

public class DateUtil {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String currentDate() {
		return DATE_FORMAT.format(new Date());
	}
	public static String formatDate(Date date) {
		return DATE_FORMAT.format(date);
	}
	public static Date parse(String date) {
		try {
			return DATE_FORMAT.parse(date);
		} catch (ParseException e) {
			throw new BusinessException("时间解析失败",e);
		}
	}
}
