package com.bjsasc.avidm.mq.log;

import org.json.JSONObject;

// 进度日志消息
public class LogMessage extends JSONObject {

	public final static String ID = "msg_id";

	public final static String TIME = "msg_time";

	public final static String SITE = "msg_site";

	// 发起该日志的站点唯一标识
	public final static String SITE_IID = "msg_site_iid";

	public final static String STATE = "msg_state";

	public final static String EXCEPTION_STACKTRACE = "msg_exception_stacktrace";

	public final static String STATE_PROCESSING = "processing";

	// 已完成
	public final static String STATE_FINISHED = "finished";

	// 异常
	public final static String STATE_EXCEPTION = "exception";

	public final static String CONTENT = "msg_content";

	// 使用当前时间
	public LogMessage(String id, String siteName, String siteIID, String content) {
		put(ID, id);
		put(TIME, System.currentTimeMillis());
		put(SITE, siteName);
		put(SITE_IID, siteIID);
		put(CONTENT, content);
		put(STATE, STATE_PROCESSING);
	}

	// 设置为已完成
	public void setFinished() {
		put(STATE, STATE_FINISHED);
	}

	// 设置为异常
	public void setException(String Exception) {
		put(STATE, STATE_EXCEPTION);
		put(EXCEPTION_STACKTRACE, Exception);
	}
}
