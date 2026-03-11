package com.bjsasc.avidm.mq.message.a5;

import com.bjsasc.avidm.mq.message.ProdMappingRespDcMessage;

/**
 * 型号映射厂所同步到中心域消息：厂所--》中心域
 * @author YHJ
 *
 */
public class A5ProdMappingRespDcMessage extends ProdMappingRespDcMessage{

	public A5ProdMappingRespDcMessage() {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

	}

	public A5ProdMappingRespDcMessage(String content) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}	
}
