package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynPrincipalReqReceiverMessage extends Message {

	public SynPrincipalReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SYNPRINCIPAL_RECEIVER);
	}
}
