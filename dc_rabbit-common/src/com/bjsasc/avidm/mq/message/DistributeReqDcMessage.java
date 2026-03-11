package com.bjsasc.avidm.mq.message;

public abstract class DistributeReqDcMessage  extends Message {
	
	public DistributeReqDcMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_DISTRIBUTE_DC);
	}

}
