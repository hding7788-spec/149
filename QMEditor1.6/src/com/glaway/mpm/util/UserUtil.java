package com.glaway.mpm.util;

import java.util.List;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.wcIntf.UserIntf;

public class UserUtil {
	private static VaLogger logger = VaLogger.getLogger(UserUtil.class.getName());
	private static List<String> userlist;

	public static List<String> getCurrentUserOid() {
		if (userlist == null) {
			userlist = UserIntf.getCurrentUserInfo();
			logger.debug("当前用户信息:" + userlist);
		}
		return userlist;
	}
}
