package com.bjsasc.avidm.mq.event.a5;

import com.bjsasc.avidm.mq.event.ShareResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 共享：数据中心 <-- 接收方（A5系统）
public class A5ShareResponseEvent extends ShareResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A5 + "/" + DC_RESPONSE_SHARE_RECEIVER;
	}
}
