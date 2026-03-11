package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.ShareRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 共享：数据中心 --> 接收方
public class DcShareRequestEvent extends ShareRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_REQUEST_SHARE_RECEIVER;
	}
}
