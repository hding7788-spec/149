package com.bjsasc.avidm.mq.message.a5;

import com.bjsasc.avidm.mq.message.syndata.SynPrincipalRespDcMessage;

/**
 * 组织和用户关系数据同步到中心域：厂所--》中心域
 * @author YHJ
 *
 */
public class A5SynPrincipalRespDcMessage extends SynPrincipalRespDcMessage{

	public A5SynPrincipalRespDcMessage() {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

	}

	public A5SynPrincipalRespDcMessage(String content) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}	
}
