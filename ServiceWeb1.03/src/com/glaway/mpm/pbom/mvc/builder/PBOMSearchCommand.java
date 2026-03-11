package com.glaway.mpm.pbom.mvc.builder;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import wt.util.WTException;

import com.ptc.netmarkets.util.beans.NmCommandBean;

public class PBOMSearchCommand  {
	public static void search(NmCommandBean commandBean){
		String batch = (String)commandBean.getText().get("batch");
		List list = commandBean.getSelected();
		String s = (String) commandBean.getRequest().getAttribute("batch");
		commandBean.getRequest().setAttribute("bomBatch", batch);
	}
}
