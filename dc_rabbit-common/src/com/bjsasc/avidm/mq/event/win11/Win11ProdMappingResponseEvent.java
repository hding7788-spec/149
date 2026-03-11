package com.bjsasc.avidm.mq.event.win11;

import com.bjsasc.avidm.mq.event.ProdMappingResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

/**
 * 型号映射事件
 * @author YHJ
 *
 */
public class Win11ProdMappingResponseEvent extends ProdMappingResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN11 + "/" + DC_RESPONSE_PROD_MAPPING_RECEIVER;
	}
}
