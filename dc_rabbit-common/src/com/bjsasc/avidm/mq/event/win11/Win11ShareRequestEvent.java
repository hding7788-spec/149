package com.bjsasc.avidm.mq.event.win11;

import com.bjsasc.avidm.mq.event.ShareRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 预审：发起方（Win11系统） --> 数据中心
public class Win11ShareRequestEvent extends ShareRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN11 + "/" + DC_REQUEST_SHARE_DC;
	}
}
