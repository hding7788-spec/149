package com.glaway.mpm.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
	/** yyyy-MM-dd */
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static boolean isFirstDate() {
		if (Calendar.getInstance().get(Calendar.DATE) == 1) {
			return true;
		}
		return false;
	}

	public static String getTodayDate() {
		Date date=new Date();//取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE,7);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime(); //这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		String dateString = formatter.format(date);
		return dateString;
	}

	public static void main(String[] args) {
		DateUtil.getTodayDate();
	}

	public static String getTodayDate(String format) {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String dateString = formatter.format(date);
		return dateString;
	}
	public static String afterNDay(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, n);
		Date date = calendar.getTime();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}
}
