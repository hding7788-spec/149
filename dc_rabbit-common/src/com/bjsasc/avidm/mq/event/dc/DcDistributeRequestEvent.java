package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.DistributeRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 发放：数据中心 --> 接收方
public class DcDistributeRequestEvent extends DistributeRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_DISTRIBUTE_RECEIVER;
	}
}
