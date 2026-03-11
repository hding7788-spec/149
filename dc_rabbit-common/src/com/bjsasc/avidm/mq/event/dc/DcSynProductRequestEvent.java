package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.syndata.SynProductRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

public class DcSynProductRequestEvent extends SynProductRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_SYNPRODUCT_RECEIVER;
	}
	
}
