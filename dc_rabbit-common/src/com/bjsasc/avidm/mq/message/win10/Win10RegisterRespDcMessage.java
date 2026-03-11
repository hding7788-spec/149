package com.bjsasc.avidm.mq.message.win10;

import com.bjsasc.avidm.mq.message.site.RegisterRespDcMessage;

/**
 * 域注册消息：厂所--》中心域
 * @author YHJ
 *
 */
public class Win10RegisterRespDcMessage extends RegisterRespDcMessage{

	public Win10RegisterRespDcMessage() {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

	}

	public Win10RegisterRespDcMessage(String content) {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}	
}
