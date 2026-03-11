package com.bjsasc.avidm.mq.event.dc;

import com.bjsasc.avidm.mq.event.DistributeResponseEvent;
import com.bjsasc.avidm.mq.message.Based;
//发放任务操作同步：发起方 <-- 数据中心
public class DcDistributeTaskSynResponseEvent extends DistributeResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_DC_A4 + "/" + DC_RESPONSE_DISTRIBUTE_TASKSYN_DC;
	}
}
