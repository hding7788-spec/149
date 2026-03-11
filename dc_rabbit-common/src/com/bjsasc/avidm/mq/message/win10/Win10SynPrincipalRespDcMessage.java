package com.bjsasc.avidm.mq.message.win10;

import com.bjsasc.avidm.mq.message.syndata.SynPrincipalRespDcMessage;

/**
 * 组织和用户关系数据同步到中心域：厂所--》中心域
 * @author YHJ
 *
 */
public class Win10SynPrincipalRespDcMessage extends SynPrincipalRespDcMessage{

	public Win10SynPrincipalRespDcMessage() {
		super();

		// 系统版本为win10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

	}

	public Win10SynPrincipalRespDcMessage(String content) {
		super();

		// 系统版本为win10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}	
}
