package com.bjsasc.avidm.mq.handler;

import ext.sast.center.util.JsonConvertUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.event.dc.DcStandardProdRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import com.bjsasc.avidm.mq.util.TUUID;

import ext.sast.center.productModel.util.SyncProductHelper;
import ext.sast.center.synch.MQConstants;

import java.io.File;

/**
 * 接收中心域标准型号
 * @author wyq
 *
 */
public class ReceivedProductInfoHandler extends AbstractHandler<DcStandardProdRequestEvent>{
	public ReceivedProductInfoHandler(){}
	@Override
	public void onEvent(DcStandardProdRequestEvent event) {
		MetaMessage metaMsg = new MetaMessage();
		Sender sender = Sender.getInstance(); ;
		LogMessage logMsg = null;
		String id = null;
		try {
			System.out.println("开始接收中心域标准型号下发");

			JSONObject msg = event.getContent();

			JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"ReceivedProductInfoHandler");

			System.out.println("msg @@@ = "+ msg);
			id = msg.getString(Based.MSG_ID);

			logMsg = new LogMessage(TUUID.getUUID(),MQConstants.SITENAME_149,MQConstants.SITEIID_149,"开始接收标准型号");
			sender.addLog(logMsg);

			String operateType = (String) msg.get(Based.OPERATE_TYPE);
			JSONArray productArray = (JSONArray) msg.get(Based.JA_PRODUCTS_REQUEST);
			if(productArray.length()>0){
				SyncProductHelper.saveDataCenterProduct(operateType,productArray);
			}

			metaMsg.setMsgId(id);
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgType(Based.META_MSG_TYPE_PRODSEND);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FINISHED);
			//目标单位
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITEID);
			JSONArray dtsSiteArray = new JSONArray();
			dtsSiteArray.put(MQConstants.DC_SITEID);
			metaMsg.setdDstSites(dtsSiteArray);
			sender.updateMetaMessage(metaMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"标准型号接收成功");
			sender.addLog(logMsg);

			System.out.println("成功接收中心域标准型号下发");
		} catch (Exception e) {
			e.printStackTrace();
			metaMsg.setMsgId(id);
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgType(Based.META_MSG_TYPE_PRODSEND);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			//目标单位
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITEID);
			JSONArray dtsSiteArray = new JSONArray();
			dtsSiteArray.put(MQConstants.DC_SITEID);
			metaMsg.setdDstSites(dtsSiteArray);
			sender.updateMetaMessage(metaMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"标准型号接收失败：" + e.getMessage());
			sender.addLog(logMsg);

		}
	}

}
