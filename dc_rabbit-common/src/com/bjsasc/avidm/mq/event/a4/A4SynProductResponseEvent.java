package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.syndata.SynProductResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 同步产品：厂所发到数据中心
public class A4SynProductResponseEvent extends SynProductResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_RESPONSE_SYNPRODUCT_RECEIVER;
	}
}
