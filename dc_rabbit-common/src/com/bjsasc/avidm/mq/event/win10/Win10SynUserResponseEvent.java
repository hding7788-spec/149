package com.bjsasc.avidm.mq.event.win10;

import com.bjsasc.avidm.mq.event.syndata.SynUserResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 同步用户：厂所发到数据中心
public class Win10SynUserResponseEvent extends SynUserResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN10 + "/" + DC_RESPONSE_SYNUSER_RECEIVER;
	}
}
