package com.bjsasc.avidm.mq.event.win10;

import com.bjsasc.avidm.mq.event.ProdMappingResponseEvent;
import com.bjsasc.avidm.mq.message.Based;

public class Win10ProdMappingResponseEvent extends ProdMappingResponseEvent implements Based {

	public String matchID() {
		return SYS_VERSION_WIN10 + "/" + DC_RESPONSE_PROD_MAPPING_RECEIVER;
	}
}
