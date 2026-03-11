package com.bjsasc.avidm.mq.message.a5;


import com.bjsasc.avidm.mq.message.DistributeReqDcMessage;

// 分发：A5系统 --> 数据中心
public class A5DistributeReqDcMessage extends DistributeReqDcMessage {

	// 带的单据对象和分发对象
	public A5DistributeReqDcMessage() {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

	}

	// 带的单据对象和分发对象
	public A5DistributeReqDcMessage(String content) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}
