package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.ShareRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 共享：发起方（A4系统） --> 数据中心
public class A4ShareRequestEvent extends ShareRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_REQUEST_SHARE_DC;
	}
}
