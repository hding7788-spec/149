package com.glaway.mpm.sjzyk;

import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.util.WTPartUtil;

public class UpdateSjzykTest {

	public static void update(String number) {
		try {
			WTPart part = WTPartUtil.getLatestPartByNumberAndView(number, "Design");
			System.out.println("part:"+part);
			if(part != null) {
				SjzykSchedule.updateSjzykMiddleTable(part);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String number = args[0];
		UpdateSjzykTest.update(number);
	}

}
