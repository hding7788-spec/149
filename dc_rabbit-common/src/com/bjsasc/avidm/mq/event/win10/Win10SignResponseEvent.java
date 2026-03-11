package com.bjsasc.avidm.mq.event.win10;

import com.bjsasc.avidm.mq.event.SignResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 会签：数据中心 <-- 接收方（Win10系统）
public class Win10SignResponseEvent extends SignResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN10 + "/" + DC_RESPONSE_SIGN_RECEIVER;
	}
}
