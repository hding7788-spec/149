package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.site.RegisterRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

public class DcRegisterRequestEvent extends RegisterRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_REGISTER_RECEIVER;
	}
}
