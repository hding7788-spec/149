package com.bjsasc.avidm.mq.event.win10;

import com.bjsasc.avidm.mq.event.ShareResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 预审：数据中心 <-- 接收方（Win10系统）
public class Win10ShareResponseEvent extends ShareResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN10 + "/" + DC_RESPONSE_SHARE_RECEIVER;
	}
}
