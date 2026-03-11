package com.bjsasc.avidm.mq.message.dc;


import com.bjsasc.avidm.mq.message.site.SiteDeleteReqReceiverMessage;

// 域删除消息：数据中心 --> 其他厂所
public class DcSiteDeleteReqReceiverMessage extends SiteDeleteReqReceiverMessage {

	public DcSiteDeleteReqReceiverMessage() {
		super();

		// 系统版本为A4中心域
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

	}

	public DcSiteDeleteReqReceiverMessage(String content) {
		super();

		// 系统版本为A4中心域
		put(SYS_VERSION_INITIAL, SYS_VERSION_DC_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_DC_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}
