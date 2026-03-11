package com.bjsasc.avidm.mq.message.dc;


import com.bjsasc.avidm.mq.message.StandardModelReqReceiverMessage;

// 标准模型及属性下发：数据中心 --> 其他厂所
public class DcStandardModelReqReceiverMessage extends StandardModelReqReceiverMessage {

	public DcStandardModelReqReceiverMessage() {
		super();

		// 系统版本为A4中心域
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

	}

	public DcStandardModelReqReceiverMessage(String content) {
		super();

		// 系统版本为A4中心域
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}
