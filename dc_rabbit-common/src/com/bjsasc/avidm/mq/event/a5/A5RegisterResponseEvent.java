package com.bjsasc.avidm.mq.event.a5;

import com.bjsasc.avidm.mq.event.site.RegisterResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

public class A5RegisterResponseEvent extends RegisterResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A5 + "/" + DC_RESPONSE_REGISTER_RECEIVER;
	}
}
