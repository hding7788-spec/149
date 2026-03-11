package com.bjsasc.avidm.mq.message.a4;

import com.bjsasc.avidm.mq.message.syndata.SynUserRespDcMessage;

/**
 * 用户数据同步到中心域：厂所--》中心域
 * @author YHJ
 *
 */
public class A4SynUserRespDcMessage extends SynUserRespDcMessage{

	public A4SynUserRespDcMessage() {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

	}

	public A4SynUserRespDcMessage(String content) {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}	
}
