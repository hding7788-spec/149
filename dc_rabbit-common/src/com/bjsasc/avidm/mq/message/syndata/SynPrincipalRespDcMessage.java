package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynPrincipalRespDcMessage extends Message {

	public SynPrincipalRespDcMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SYNPRINCIPAL_RECEIVER);
	}
}
