package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynUserReqReceiverMessage extends Message {

	public SynUserReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SYNUSER_RECEIVER);
	}
}
