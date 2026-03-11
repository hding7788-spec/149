package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.SignResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 会签：发起方 <-- 数据中心
public class DcSignResponseEvent extends SignResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_RESPONSE_SIGN_DC;
	}
}
