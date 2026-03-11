package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.site.SiteDeleteRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 删除域：数据中心 --> 接收方
public class DcSiteDeleteRequestEvent extends SiteDeleteRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_SITEDELETE_RECEIVER;
	}
}
