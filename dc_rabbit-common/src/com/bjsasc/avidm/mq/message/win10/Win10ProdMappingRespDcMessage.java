package com.bjsasc.avidm.mq.message.win10;

import com.bjsasc.avidm.mq.message.ProdMappingRespDcMessage;

/**
 * 型号映射厂所同步到中心域消息：厂所--》中心域
 * @author YHJ
 *
 */
public class Win10ProdMappingRespDcMessage extends ProdMappingRespDcMessage{

	public Win10ProdMappingRespDcMessage() {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

	}

	public Win10ProdMappingRespDcMessage(String content) {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}	
}
