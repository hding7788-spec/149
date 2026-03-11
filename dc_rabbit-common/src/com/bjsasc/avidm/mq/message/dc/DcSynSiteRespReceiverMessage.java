package com.bjsasc.avidm.mq.message.dc;


import com.bjsasc.avidm.mq.message.syndata.SynSiteRespReceiverMessage;

// 域同步消息反馈：数据中心 --> 其他厂所
public class DcSynSiteRespReceiverMessage extends SynSiteRespReceiverMessage {

	public DcSynSiteRespReceiverMessage() {
		super();

		// 系统版本为A4中心域
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

	}

	public DcSynSiteRespReceiverMessage(String content) {
		super();

		// 系统版本为A4中心域
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}
