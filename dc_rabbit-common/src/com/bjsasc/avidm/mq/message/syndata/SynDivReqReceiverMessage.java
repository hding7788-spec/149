package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynDivReqReceiverMessage extends Message {

	public SynDivReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SYNDIV_RECEIVER);
	}
}
