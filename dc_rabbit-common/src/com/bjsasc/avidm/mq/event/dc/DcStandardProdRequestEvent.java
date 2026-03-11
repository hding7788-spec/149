package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.StandardProdRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

public class DcStandardProdRequestEvent extends StandardProdRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_STANDARD_PROD_RECEIVER;
	}
}
