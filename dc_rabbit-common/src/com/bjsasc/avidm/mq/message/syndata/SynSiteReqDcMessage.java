package com.bjsasc.avidm.mq.message.syndata;

import com.bjsasc.avidm.mq.message.Message;

public abstract class SynSiteReqDcMessage extends Message{

	public SynSiteReqDcMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SYNSITE_DC);
	}
}
