package com.bjsasc.avidm.mq.message.win11;

import com.bjsasc.avidm.mq.message.syndata.SynSiteReqDcMessage;

/**
 * ：厂所--》中心域
 * @author YHJ
 *
 */
public class Win11SynSiteReqDcMessage extends SynSiteReqDcMessage{

	public Win11SynSiteReqDcMessage() {
		super();

		// 系统版本为win11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

	}

	public Win11SynSiteReqDcMessage(String content) {
		super();

		// 系统版本为win11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}
}
