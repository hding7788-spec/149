package com.bjsasc.avidm.mq.message.a5;

import com.bjsasc.avidm.mq.message.syndata.SynSiteReqDcMessage;

/**
 * ：厂所--》中心域
 * @author YHJ
 *
 */
public class A5SynSiteReqDcMessage extends SynSiteReqDcMessage{

	public A5SynSiteReqDcMessage() {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

	}

	public A5SynSiteReqDcMessage(String content) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}	
}
