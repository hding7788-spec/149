package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.SignResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 会签：数据中心 <-- 接收方（A4系统）
public class A4SignResponseEvent extends SignResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_RESPONSE_SIGN_RECEIVER;
	}
}
