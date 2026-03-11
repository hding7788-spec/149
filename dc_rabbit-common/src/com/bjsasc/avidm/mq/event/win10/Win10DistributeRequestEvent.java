package com.bjsasc.avidm.mq.event.win10;

import com.bjsasc.avidm.mq.event.DistributeRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 发放：发起方（Win10系统） --> 数据中心
public class Win10DistributeRequestEvent extends DistributeRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN10 + "/" + DC_REQUEST_DISTRIBUTE_DC;
	}
}
