package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.syndata.SynSiteResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 反馈同步域信息：数据中心发到厂所
public class DcSynSiteResponseEvent extends SynSiteResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_RESPONSE_SYNSITE_RECEIVER;
	}
}
