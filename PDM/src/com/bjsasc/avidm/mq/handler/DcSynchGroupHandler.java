package com.bjsasc.avidm.mq.handler;

import com.bjsasc.avidm.mq.event.dc.DcSynDivRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.message.win10.Win10SynDivRespDcMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import ext.sast.center.bean.GroupBean;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.util.JsonConvertUtil;
import ext.sast.center.util.QueryUtil;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DcSynchGroupHandler extends AbstractHandler<DcSynDivRequestEvent> implements Based{
	protected final Logger logger = Logger.getLogger(DcSynchGroupHandler.class.getName());
	@Override
	public void onEvent(DcSynDivRequestEvent event) {
		Log.info("接收中心域同步用户请求");
		MetaMessage metaMsg = new MetaMessage();
		Sender sender = Sender.getInstance();
		LogMessage logMsg = null;

		JSONObject msg = event.getContent();
		logger.info("msg @@@ == "+msg);
		String msg_id = "";
		String id = "";
		String name = "";
		try {
			msg_id = msg.getString(Based.MSG_ID);
			name = msg.getString(Based.NAME);
			try {
				id = msg.getString(ID);
			} catch (JSONException e) {
				logger.info("DcSynchGroupHandler id is not found");
			}
			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, "接收方-已收到同步组织和用户关系请求，开始处理...");
			sender.addLog(logMsg);

			List<GroupBean> list = QueryUtil.getAllUserAndGroup().getGroupMessageBean().getJa_divs_response();
//			List<GroupBean> list = QueryUtil.getAllGroup();
			System.out.println("同步组织 @@@@ = " + list.size());
			msg = new Win10SynDivRespDcMessage();
			msg.put(Based.MSG_ID,msg_id);
			msg.put(Based.ID,id);
			msg.put(Based.NAME,name);
			msg.put(Based.RESPONSE_SITE_IID,MQConstants.SITEIID_149);
			msg.put(Based.JA_DIVS_RESPONSE, JsonConvertUtil.convertGroupToJson(list));
			System.out.println("同步组织 @@@@ = " + msg);
			sender.send(msg);


//			metaMsg.setMsgId(id);
//			metaMsg.setMsgType(Based.META_MSG_TYPE_SYNDIV);
//			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
//			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
//			metaMsg.setdDstSiteInfo(MQConstants.DC_SITENAME);
//			JSONArray sites = new JSONArray();
//			sites.put(MQConstants.DC_SITEIID);
//			metaMsg.setdDstSites(sites);

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, "接收方-已处理完同步组织和用户关系请求");
			sender.addLog(logMsg);
		} catch (Exception e) {
			e.printStackTrace();

			metaMsg.setMsgId(id);
			metaMsg.setMsgType(Based.META_MSG_TYPE_SYNDIV);
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITENAME);
			JSONArray sites = new JSONArray();
			sites.put(MQConstants.DC_SITEIID);
			metaMsg.setdDstSites(sites);

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, "厂所处理出错："+e.getMessage());
			logMsg.setException(e.getMessage());
			sender.addLog(logMsg);
		}
	}

}
