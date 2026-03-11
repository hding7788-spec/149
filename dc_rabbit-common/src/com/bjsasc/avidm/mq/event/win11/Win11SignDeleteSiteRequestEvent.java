package com.bjsasc.avidm.mq.event.win11;

import com.bjsasc.avidm.mq.event.SignRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 会签删除单位：发起方（Win11系统） --> 数据中心
public class Win11SignDeleteSiteRequestEvent extends SignRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN11 + "/" + DC_REQUEST_SIGN_DELETESITE_DC;
	}
}
