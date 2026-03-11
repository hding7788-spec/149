package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.ShareResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 共享：数据中心 <-- 接收方（A4系统）
public class A4ShareResponseEvent extends ShareResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_RESPONSE_SHARE_RECEIVER;
	}
}
