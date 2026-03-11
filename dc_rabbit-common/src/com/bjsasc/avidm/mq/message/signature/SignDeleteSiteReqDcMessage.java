package com.bjsasc.avidm.mq.message.signature;

import com.bjsasc.avidm.mq.message.Message;

// 会签删除单位：发起方 --> 数据中心
public abstract class SignDeleteSiteReqDcMessage extends Message {

	public SignDeleteSiteReqDcMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SIGN_DELETESITE_DC);
	}
}
