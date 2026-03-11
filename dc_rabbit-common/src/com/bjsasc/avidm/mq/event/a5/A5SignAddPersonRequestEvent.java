package com.bjsasc.avidm.mq.event.a5;

import com.bjsasc.avidm.mq.event.SignRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

// 会签增加人员：发起方（A5系统） --> 数据中心
public class A5SignAddPersonRequestEvent extends SignRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A5 + "/" + DC_REQUEST_SIGN_ADDPERSON_DC;
	}
}
