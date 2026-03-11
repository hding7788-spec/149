package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynProductReqReceiverMessage extends Message {

	public SynProductReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SYNPRODUCT_RECEIVER);
	}
}
