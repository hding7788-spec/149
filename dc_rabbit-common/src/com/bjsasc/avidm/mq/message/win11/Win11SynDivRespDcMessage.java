package com.bjsasc.avidm.mq.message.win11;

import com.bjsasc.avidm.mq.message.syndata.SynDivRespDcMessage;

/**
 * 组织数据同步到中心域：厂所--》中心域
 * @author YHJ
 *
 */
public class Win11SynDivRespDcMessage extends SynDivRespDcMessage{

	public Win11SynDivRespDcMessage() {
		super();

		// 系统版本为win11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

	}

	public Win11SynDivRespDcMessage(String content) {
		super();

		// 系统版本为win11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}
}
