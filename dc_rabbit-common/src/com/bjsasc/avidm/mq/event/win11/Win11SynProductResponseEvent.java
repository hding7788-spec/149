package com.bjsasc.avidm.mq.event.win11;

import com.bjsasc.avidm.mq.event.syndata.SynProductResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 同步产品：厂所发到数据中心
public class Win11SynProductResponseEvent extends SynProductResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN11 + "/" + DC_RESPONSE_SYNPRODUCT_RECEIVER;
	}
}
