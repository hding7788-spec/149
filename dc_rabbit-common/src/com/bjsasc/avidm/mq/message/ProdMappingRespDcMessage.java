package com.bjsasc.avidm.mq.message;

// 型号映射消息：厂所--->数据中心
public abstract class ProdMappingRespDcMessage extends Message {

	public ProdMappingRespDcMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_PROD_MAPPING_RECEIVER);
	}
}
