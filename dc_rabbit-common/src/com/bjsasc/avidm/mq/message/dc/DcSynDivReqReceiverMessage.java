package com.bjsasc.avidm.mq.message.dc;

import com.bjsasc.avidm.mq.message.syndata.SynDivReqReceiverMessage;


// 同步用户消息：数据中心 --> 其他厂所
public class DcSynDivReqReceiverMessage extends SynDivReqReceiverMessage {

	public DcSynDivReqReceiverMessage() {
		super();

		// 系统版本为A4中心域
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

	}

	public DcSynDivReqReceiverMessage(String content) {
		super();

		// 系统版本为A4中心域
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}
