package com.bjsasc.avidm.mq.event.win11;

import com.bjsasc.avidm.mq.event.DistributeResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

//发放任务操作同步（任务转发，删除子任务，任务签署）：数据中心 <-- 接收方（Win11系统）
public class Win11DistributeTaskSynResponseEvent extends DistributeResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN11 + "/" + DC_RESPONSE_DISTRIBUTE_TASKSYN_RECEIVER;
	}
}
