package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.DistributeRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 发放：发起方（A4系统） --> 数据中心
public class A4DistributeRequestEvent extends DistributeRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_REQUEST_DISTRIBUTE_DC;
	}
}
