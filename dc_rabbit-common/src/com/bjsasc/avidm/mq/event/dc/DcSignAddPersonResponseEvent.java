package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.SignResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

// 会签增加人员：发起方 <-- 数据中心
public class DcSignAddPersonResponseEvent extends SignResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_RESPONSE_SIGN_ADDPERSON_DC;
	}
}
