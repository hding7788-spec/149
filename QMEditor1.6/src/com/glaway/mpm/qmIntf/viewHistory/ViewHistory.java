package com.glaway.mpm.qmIntf.viewHistory;

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class ViewHistory {

	private ViewHistory() {
	}

	public static void openHistoryUrl() {
		CommonUtil.openURL(getUrl());
	}

	private static String getUrl() {

		return TechnicsIntf.getHistoryUrl();
	}

}
