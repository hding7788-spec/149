package com.bjsasc.avidm.mq.message.share;

import com.bjsasc.avidm.mq.message.Message;

// 预审任务操作同步：接收方--》数据中心
public abstract class ShareTaskSynRespReceiverMessage extends Message {

	public ShareTaskSynRespReceiverMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SHARE_TASKSYN_RECEIVER);
	}
}
