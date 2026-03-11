package com.bjsasc.avidm.mq.message;

// 分发：数据中心 <-- 接收方
public abstract class DistributeRespReceiverMessage extends Message {

	public DistributeRespReceiverMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_DISTRIBUTE_RECEIVER);
	}
}
