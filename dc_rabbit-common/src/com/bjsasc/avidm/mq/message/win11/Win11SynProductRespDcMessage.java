package com.bjsasc.avidm.mq.message.win11;

import com.bjsasc.avidm.mq.message.syndata.SynProductRespDcMessage;

/**
 * 产品数据同步到中心域：厂所--》中心域
 * @author YHJ
 *
 */
public class Win11SynProductRespDcMessage extends SynProductRespDcMessage{

	public Win11SynProductRespDcMessage() {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

	}

	public Win11SynProductRespDcMessage(String content) {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}
}
