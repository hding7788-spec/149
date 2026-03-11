package com.bjsasc.avidm.mq.message.dc;


import com.bjsasc.avidm.mq.message.StandardProdReqReceiverMessage;

// 标准型号下发：数据中心 --> 其他厂所
public class DcStandardProdReqReceiverMessage extends StandardProdReqReceiverMessage {

	public DcStandardProdReqReceiverMessage() {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

	}

	public DcStandardProdReqReceiverMessage(String content) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}
