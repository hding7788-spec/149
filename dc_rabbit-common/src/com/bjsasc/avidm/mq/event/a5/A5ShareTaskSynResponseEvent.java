package com.bjsasc.avidm.mq.event.a5;

import com.bjsasc.avidm.mq.event.ShareResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

//预审任务操作同步（删除子任务，任务签署）：数据中心 <-- 接收方（A5系统）
public class A5ShareTaskSynResponseEvent extends ShareResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A5 + "/" + DC_RESPONSE_SHARE_TASKSYN_RECEIVER;
	}
}
