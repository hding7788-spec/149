package com.glaway.mpm.pbombuilder.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

	public static String getDateStr(Date date,String format) {
		if(date==null)return ""	;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String d = formatter.format(date);
		return d;
	}
}
