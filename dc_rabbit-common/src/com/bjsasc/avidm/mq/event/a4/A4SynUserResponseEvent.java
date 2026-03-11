package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.syndata.SynUserResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 同步用户：厂所发到数据中心
public class A4SynUserResponseEvent extends SynUserResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_RESPONSE_SYNUSER_RECEIVER;
	}
}
