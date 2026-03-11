package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynProductRespDcMessage extends Message {

	public SynProductRespDcMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SYNPRODUCT_RECEIVER);
	}
}
