package com.glaway.mpm.sjzyk;

import java.sql.Timestamp;
import java.util.Date;

import wt.scheduler.ScheduleItem;
import wt.scheduler.SchedulingHelper;
import wt.session.SessionHelper;
import wt.util.WTException;

public class AddSjzykScheduleItem {

	public static void createScheduleItem() {
		try {
			long periodicity = 24*60*60;
			ScheduleItem schItem = ScheduleItem.newScheduleItem();
			schItem.setItemDescription("Sjzyk middle table load");
			schItem.setItemName("sjzyk");
			schItem.setTargetClass("com.glaway.mpm.sjzyk.SjzykSchedule");
			schItem.setTargetMethod("process");
			schItem.setImmediateMode(false);
			schItem.setPeriodicity(periodicity);
			schItem.setQueueName("SjzykScheduleQueue");
			schItem.setToBeRun(-1);
			schItem.setPrincipalRef(SessionHelper.manager.getPrincipalReference());

			//set the current time
//			Calendar c = Calendar.getInstance(Locale.CHINA);
//			int HOURS = 4;
//			int MINUTES = 0;
//			int SECONDS = 0;
//			c.set(Calendar.HOUR_OF_DAY, HOURS);
//			c.set(Calendar.MINUTE, MINUTES);
//			c.set(Calendar.SECOND, SECONDS);
//			Timestamp timestamp = new Timestamp(c.getTimeInMillis());
//			timestamp = new Timestamp(c.getTime().getTime()+32*60*60*1000);
//			Date date = new Date(timestamp.getTime());
//			System.out.println("-----date---"+date.toLocaleString());
//			schItem.setNextTime(timestamp);

			Date today = new Date();
	        Timestamp tt = new Timestamp(today.getTime()); //这四个参数依次为小时，分，秒，毫秒
			schItem.setStartDate(tt);

			System.out.println("\n");
			System.out.println("Loading " + schItem.getItemName() + " to " + schItem.getQueueName() + " with the following Data:\n");
			System.out.println("Description:   " + schItem.getItemDescription());
			System.out.println("Target class:  " + schItem.getTargetClass());
			System.out.println("Target method: " + schItem.getTargetMethod());
			System.out.println("Periodicity:   " + schItem.getPeriodicity());
			System.out.println("Number runs:   " + schItem.getToBeRun());
			System.out.println("Loaded as:     " + schItem.getPrincipalRef().getName());

			// schedule the item
			SchedulingHelper.service.addItem(schItem, null);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AddSjzykScheduleItem.createScheduleItem();
	}

}
