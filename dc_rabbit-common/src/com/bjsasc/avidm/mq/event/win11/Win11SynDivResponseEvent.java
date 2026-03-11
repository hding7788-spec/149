package com.bjsasc.avidm.mq.event.win11;

import com.bjsasc.avidm.mq.event.syndata.SynDivisionResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 同步组织：厂所发到数据中心
public class Win11SynDivResponseEvent extends SynDivisionResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN11 + "/" + DC_RESPONSE_SYNDIV_RECEIVER;
	}
}
