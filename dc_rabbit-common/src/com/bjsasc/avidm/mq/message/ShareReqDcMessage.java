package com.bjsasc.avidm.mq.message;

// 共享：发起方 --> 数据中心
public abstract class ShareReqDcMessage extends Message {

	public ShareReqDcMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SHARE_DC);
	}
}
