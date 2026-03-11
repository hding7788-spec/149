package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.syndata.SynUserRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

public class DcSynUserRequestEvent extends SynUserRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_SYNUSER_RECEIVER;
	}
	
}
