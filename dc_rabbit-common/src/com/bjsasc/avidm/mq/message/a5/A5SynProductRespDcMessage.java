package com.bjsasc.avidm.mq.message.a5;

import com.bjsasc.avidm.mq.message.syndata.SynProductRespDcMessage;

/**
 * 产品数据同步到中心域：厂所--》中心域
 * @author YHJ
 *
 */
public class A5SynProductRespDcMessage extends SynProductRespDcMessage{

	public A5SynProductRespDcMessage() {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

	}

	public A5SynProductRespDcMessage(String content) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}	
}
