package com.bjsasc.avidm.mq.event.win11;

import com.bjsasc.avidm.mq.event.ShareResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 预审：数据中心 <-- 接收方（Win11系统）
public class Win11ShareResponseEvent extends ShareResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN11 + "/" + DC_RESPONSE_SHARE_RECEIVER;
	}
}
