package com.bjsasc.avidm.mq.message;

// 分发：数据中心 --> 接收方
public abstract class DistributeReqReceiverMessage extends Message {

	public DistributeReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_DISTRIBUTE_RECEIVER);
	}
}
