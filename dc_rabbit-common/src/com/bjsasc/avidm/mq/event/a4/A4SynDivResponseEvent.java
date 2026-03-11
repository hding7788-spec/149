package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.syndata.SynDivisionResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 同步组织：厂所发到数据中心
public class A4SynDivResponseEvent extends SynDivisionResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_RESPONSE_SYNDIV_RECEIVER;
	}
}
