package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.ShareResponseEvent;
import com.bjsasc.avidm.mq.message.Based;
//预审任务操作同步：发起方 <-- 数据中心
public class DcShareTaskSynResponseEvent extends ShareResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_RESPONSE_SHARE_TASKSYN_DC;
	}
}
