package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.DistributeResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 发放：数据中心 <-- 接收方（A4系统）
public class A4DistributeResponseEvent extends DistributeResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_RESPONSE_DISTRIBUTE_RECEIVER;
	}
}
