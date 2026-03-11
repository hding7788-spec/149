package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynUserRespDcMessage extends Message {

	public SynUserRespDcMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SYNUSER_RECEIVER);
	}
}
