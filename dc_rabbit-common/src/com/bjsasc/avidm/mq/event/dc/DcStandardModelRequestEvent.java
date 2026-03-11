package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.StandardModelRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

public class DcStandardModelRequestEvent extends StandardModelRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_STANDARD_MODEL_RECEIVER;
	}
}
