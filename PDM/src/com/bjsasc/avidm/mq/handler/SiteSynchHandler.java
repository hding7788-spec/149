package com.bjsasc.avidm.mq.handler;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.event.dc.DcSynSiteResponseEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.sender.Sender;

import ext.sast.center.processor.SaveSiteInfoProcessor;
import ext.sast.center.synch.MQConstants;

public class SiteSynchHandler extends AbstractHandler<DcSynSiteResponseEvent> implements Based {
	protected final Logger logger = Logger.getLogger(SiteSynchHandler.class.getName());
	@Override
	public void onEvent(DcSynSiteResponseEvent event) {
        MetaMessage metaMsg = new MetaMessage();
        Sender sender = Sender.getInstance(); ;
		LogMessage logMsg = null;
		String id = null;
        try {
        	JSONObject msg = event.getContent();
        	System.out.println("域同步msg@@@@ = "+msg);
        	id = msg.getString(Based.MSG_ID);

        	logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"收到域同步信息，开始处理...");
			sender.addLog(logMsg);

        	JSONArray array = msg.getJSONArray(Based.JA_SITES_RESPONSE);


        	//收集信息，构建对象,保存中心域域信息
        	logger.info("开始保存数据库信息" );
			SaveSiteInfoProcessor.synchSiteInfo(array);

			metaMsg.setMsgId(id);
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgType(Based.META_MSG_TYPE_SYNSITE);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FINISHED);
			//目标单位
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITEID);
			JSONArray dtsSiteArray = new JSONArray();
			dtsSiteArray.put(MQConstants.DC_SITEID);
			metaMsg.setdDstSites(dtsSiteArray);
			sender.addMetaMessage(metaMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"域信息同步成功");
			sender.addLog(logMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
        logger.info("结束保存数据库信息" );
        System.out.println("结束保存数据库信息" );

	}

}
