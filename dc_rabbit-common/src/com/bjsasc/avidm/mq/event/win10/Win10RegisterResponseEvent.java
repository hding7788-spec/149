package com.bjsasc.avidm.mq.event.win10;

import com.bjsasc.avidm.mq.event.site.RegisterResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

public class Win10RegisterResponseEvent extends RegisterResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN10 + "/" + DC_RESPONSE_REGISTER_RECEIVER;
	}
}
