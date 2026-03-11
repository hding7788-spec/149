package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.syndata.SynPrincipalResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 同步组织和用户关系：厂所发到数据中心
public class A4SynPrincipalResponseEvent extends SynPrincipalResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_RESPONSE_SYNPRINCIPAL_RECEIVER;
	}
}
