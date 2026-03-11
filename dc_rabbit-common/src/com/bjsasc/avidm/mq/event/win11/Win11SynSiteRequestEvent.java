package com.bjsasc.avidm.mq.event.win11;

import com.bjsasc.avidm.mq.event.syndata.SynSiteRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 同步域信息：厂所发到数据中心
public class Win11SynSiteRequestEvent extends SynSiteRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN11 + "/" + DC_REQUEST_SYNSITE_DC;
	}
}
