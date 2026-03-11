package com.bjsasc.avidm.mq.handler;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.event.dc.DcStandardModelRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import com.bjsasc.avidm.mq.util.TUUID;

import ext.sast.center.productModel.util.SyncModeTypeHelper;
import ext.sast.center.synch.MQConstants;

/**
 * 接收中心域标准模型属性
 * @author wyq
 *
 */
public class ReceivedModelInfoHandler extends AbstractHandler<DcStandardModelRequestEvent>{
	public ReceivedModelInfoHandler(){}
	@Override
	public void onEvent(DcStandardModelRequestEvent event) {
		MetaMessage metaMsg = new MetaMessage();
		Sender sender = Sender.getInstance();
		LogMessage logMsg = null;
		String id = null;
		try {
			System.out.println("开始接收中心域标准模型下发");

			JSONObject msg = event.getContent();
			System.out.println("msg @@@ = "+ msg);
			id = msg.getString(Based.MSG_ID);

			logMsg = new LogMessage(TUUID.getUUID(),MQConstants.SITENAME_149,MQConstants.SITEIID_149,"开始接收标准模型属性");
			sender.addLog(logMsg);

			String operateType = (String) msg.get(Based.OPERATE_TYPE);
			JSONArray modelTypeArray = (JSONArray) msg.get(Based.JA_MODELS_REQUEST);
			System.out.println("modelTypeArray @@@ = " + modelTypeArray);
			JSONArray modelAttrArray = (JSONArray) msg.get(Based.JA_MODELATTRS_REQUEST);
			System.out.println("modelAttrArray @@@ = " + modelAttrArray);
			SyncModeTypeHelper.saveDataCenterModelType(operateType, modelTypeArray);
			SyncModeTypeHelper.saveDataCenterModelAttr(operateType,modelAttrArray);

			metaMsg.setMsgId(id);
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgType(Based.META_MSG_TYPE_MODELSEND);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FINISHED);
			//目标单位
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITEID);
			JSONArray dtsSiteArray = new JSONArray();
			dtsSiteArray.put(MQConstants.DC_SITEID);
			metaMsg.setdDstSites(dtsSiteArray);

			sender.addMetaMessage(metaMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"成功接收标准模型属性");
			sender.addLog(logMsg);

			System.out.println("成功接收中心域标准模型下发");
		} catch (Exception e) {
			e.printStackTrace();
			metaMsg.setMsgId(id);
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgType(Based.META_MSG_TYPE_MODELSEND);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			//目标单位
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITEID);
			JSONArray dtsSiteArray = new JSONArray();
			dtsSiteArray.put(MQConstants.DC_SITEID);
			metaMsg.setdDstSites(dtsSiteArray);

			sender.addMetaMessage(metaMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"接收标准模型属性失败："+e.getMessage());
			sender.addLog(logMsg);
		}
	}
}
