package com.bjsasc.avidm.mq.message;

// 共享：数据中心 <-- 接收方
public abstract class ShareRespReceiverMessage extends Message {

	public ShareRespReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SHARE_RECEIVER);
	}
}
