package com.bjsasc.avidm.mq.event.a4;

import com.bjsasc.avidm.mq.event.SignRequestEvent;
import com.bjsasc.avidm.mq.message.Based;

/**
 * // 会签增加人员：发起方（A4系统） --> 数据中心
 * @author YHJ
 *
 */
public class A4SignAddPersonRequestEvent extends SignRequestEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A4 + "/" + DC_REQUEST_SIGN_ADDPERSON_DC;
	}
}