package com.bjsasc.avidm.mq.message.win11;

import com.bjsasc.avidm.mq.message.syndata.SynUserRespDcMessage;

/**
 * 用户同步到中心域：厂所--》中心域
 * @author YHJ
 *
 */
public class Win11SynUserRespDcMessage extends SynUserRespDcMessage{

	public Win11SynUserRespDcMessage() {
		super();

		// 系统版本为win11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

	}

	public Win11SynUserRespDcMessage(String content) {
		super();

		// 系统版本为win11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}
}
