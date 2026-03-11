package com.bjsasc.avidm.mq.event.a5;

import com.bjsasc.avidm.mq.event.DistributeResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

//发放任务操作同步（任务转发，删除子任务，任务签署）：数据中心 <-- 接收方（A5系统）
public class A5DistributeTaskSynResponseEvent extends DistributeResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A5 + "/" + DC_RESPONSE_DISTRIBUTE_TASKSYN_RECEIVER;
	}
}
