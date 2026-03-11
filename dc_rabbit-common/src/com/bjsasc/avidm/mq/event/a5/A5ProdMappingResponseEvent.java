package com.bjsasc.avidm.mq.event.a5;

import com.bjsasc.avidm.mq.event.ProdMappingResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

public class A5ProdMappingResponseEvent extends ProdMappingResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_A5 + "/" + DC_RESPONSE_PROD_MAPPING_RECEIVER;
	}
}
