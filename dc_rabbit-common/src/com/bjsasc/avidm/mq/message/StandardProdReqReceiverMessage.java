package com.bjsasc.avidm.mq.message;

// 标准型号下发消息：数据中心 --> 接收方
public abstract class StandardProdReqReceiverMessage extends Message {

	public StandardProdReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_STANDARD_PROD_RECEIVER);
	}
}
