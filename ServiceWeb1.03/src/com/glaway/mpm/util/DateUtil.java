package com.glaway.mpm.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

	public static String getCurrentDate(String format) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String date = formatter.format(c.getTime());
		return date;
	}

	public static String getDateAfter(String date, int i) {
		try {
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = myFormatter.parse(date);
			Calendar c = Calendar.getInstance();
			c.setTime(date1);
			c.add(Calendar.DATE, i);
			return myFormatter.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
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

	public static String afterNDay(String formatStr, int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, n);
		Date date = calendar.getTime();
		DateFormat format = new SimpleDateFormat(formatStr);
		return format.format(date);
	}
}
