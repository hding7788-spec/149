package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.syndata.SynDivisionRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

public class DcSynDivRequestEvent extends SynDivisionRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_SYNDIV_RECEIVER;
	}
	
}
