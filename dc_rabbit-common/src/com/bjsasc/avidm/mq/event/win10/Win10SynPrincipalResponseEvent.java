package com.bjsasc.avidm.mq.event.win10;

import com.bjsasc.avidm.mq.event.syndata.SynPrincipalResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 同步组织和用户关系：厂所发到数据中心
public class Win10SynPrincipalResponseEvent extends SynPrincipalResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN10 + "/" + DC_RESPONSE_SYNPRINCIPAL_RECEIVER;
	}
}
