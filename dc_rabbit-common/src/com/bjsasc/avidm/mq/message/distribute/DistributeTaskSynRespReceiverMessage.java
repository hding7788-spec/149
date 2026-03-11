package com.bjsasc.avidm.mq.message.distribute;

import com.bjsasc.avidm.mq.message.Message;

// 发放任务操作同步：接收方 --> 数据中心
public abstract class DistributeTaskSynRespReceiverMessage extends Message {

	public DistributeTaskSynRespReceiverMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_DISTRIBUTE_TASKSYN_RECEIVER);
	}
}
