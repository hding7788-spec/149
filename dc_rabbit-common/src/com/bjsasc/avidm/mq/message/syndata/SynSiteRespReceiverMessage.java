package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynSiteRespReceiverMessage extends Message{

	public SynSiteRespReceiverMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SYNSITE_RECEIVER);
	}
}
