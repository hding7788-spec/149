package com.bjsasc.avidm.mq.event.win10;

import com.bjsasc.avidm.mq.event.DistributeResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 发放：数据中心 <-- 接收方（win10系统）
public class Win10DistributeResponseEvent extends DistributeResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN10 + "/" + DC_RESPONSE_DISTRIBUTE_RECEIVER;
	}
}
