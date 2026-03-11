package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynDivRespDcMessage extends Message {

	public SynDivRespDcMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SYNDIV_RECEIVER);
	}
}
