package com.bjsasc.avidm.mq.message;

// 共享：发起方 <-- 数据中心
public abstract class ShareRespDcMessage extends Message {

	public ShareRespDcMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SHARE_DC);
	}
}
