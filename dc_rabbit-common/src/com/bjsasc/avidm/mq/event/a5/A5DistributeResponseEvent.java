package com.bjsasc.avidm.mq.event.a5;

import com.bjsasc.avidm.mq.event.DistributeResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 发放：数据中心 <-- 接收方（A5系统）
public class A5DistributeResponseEvent extends DistributeResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A5 + "/" + DC_RESPONSE_DISTRIBUTE_RECEIVER;
	}
}
