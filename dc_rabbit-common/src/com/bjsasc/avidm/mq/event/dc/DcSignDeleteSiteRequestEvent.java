package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.SignRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 会签删除单位：数据中心 --> 接收方
public class DcSignDeleteSiteRequestEvent extends SignRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_SIGN_DELETESITE_RECEIVER;
	}
}
