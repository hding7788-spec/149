package com.bjsasc.avidm.mq.message.win11;

import com.bjsasc.avidm.mq.message.ProdMappingRespDcMessage;

/**
 * 型号映射厂所同步到中心域消息：厂所--》中心域
 * @author YHJ
 *
 */
public class Win11ProdMappingRespDcMessage extends ProdMappingRespDcMessage{

	public Win11ProdMappingRespDcMessage() {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

	}

	public Win11ProdMappingRespDcMessage(String content) {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}	
}
