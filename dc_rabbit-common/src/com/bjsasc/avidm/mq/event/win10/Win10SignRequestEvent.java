package com.bjsasc.avidm.mq.event.win10;

import com.bjsasc.avidm.mq.event.SignRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 会签：发起方（Win10系统） --> 数据中心
public class Win10SignRequestEvent extends SignRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN10 + "/" + DC_REQUEST_SIGN_DC;
	}
}
