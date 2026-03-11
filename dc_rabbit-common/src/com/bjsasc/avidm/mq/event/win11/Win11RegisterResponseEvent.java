package com.bjsasc.avidm.mq.event.win11;

import com.bjsasc.avidm.mq.event.site.RegisterResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

/**
 * 域注册反馈事件
 * @author YHJ
 *
 */
public class Win11RegisterResponseEvent extends RegisterResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN11 + "/" + DC_RESPONSE_REGISTER_RECEIVER;
	}
}
