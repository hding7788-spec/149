package com.bjsasc.avidm.mq.message;

// 分发：发起方 <-- 数据中心
public abstract class DistributeRespDcMessage extends Message {

	public DistributeRespDcMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_DISTRIBUTE_DC);
	}
}
