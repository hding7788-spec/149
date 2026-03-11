package com.bjsasc.avidm.mq.handler;

import com.bjsasc.avidm.mq.event.dc.DcSynUserRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.message.win10.Win10SynUserRespDcMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import ext.sast.center.bean.UserBean;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.util.JsonConvertUtil;
import ext.sast.center.util.QueryUtil;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.util.WTException;

import java.util.List;

public class DcSynchUserHandler extends AbstractHandler<DcSynUserRequestEvent> implements Based{
	protected final Logger logger = Logger.getLogger(DcSynchUserHandler.class.getName());
	@Override
	public void onEvent(DcSynUserRequestEvent event) {
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
				logger.info("DcSynchUserHandler id is not found");
			}
			logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, "收到中心域发起的同步用户请求，开始处理...");
			sender.addLog(logMsg);

			List<UserBean> list = QueryUtil.getAllUserAndGroup().getUserMessageBean().getJa_users_response();
//			List<UserBean> list = QueryUtil.getAllUser();
			System.out.println("同步用户 @@@@ = " + list.size());
			msg = new Win10SynUserRespDcMessage();
			msg.put(Based.MSG_ID,msg_id);
			msg.put(Based.ID,id);
			msg.put(Based.NAME,name);
			msg.put(Based.JA_USERS_RESPONSE, JsonConvertUtil.convertUserListToJson(list));
			msg.put(Based.RESPONSE_SITE_IID,MQConstants.SITEIID_149);
			System.out.println("反馈的msg @@@ = "+msg);
			sender.send(msg);

//			metaMsg.setMsgId(id);
//			metaMsg.setMsgType(Based.META_MSG_TYPE_SYNUSER);
//			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
//			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
//			metaMsg.setdDstSiteInfo(MQConstants.DC_SITENAME);
//			JSONArray sites = new JSONArray();
//			sites.put(MQConstants.DC_SITEIID);
//			metaMsg.setdDstSites(sites);

			logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, "处理完成，返回中心域");
			sender.addLog(logMsg);
		} catch (WTException e) {
			e.printStackTrace();

			metaMsg.setMsgId(msg_id);
			metaMsg.setMsgType(Based.META_MSG_TYPE_SYNUSER);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITENAME);
			JSONArray sites = new JSONArray();
			sites.put(MQConstants.DC_SITEIID);
			metaMsg.setdDstSites(sites);

			logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, "厂所处理出错：" + e.getMessage());
			logMsg.setException(e.getMessage());
			sender.addLog(logMsg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
