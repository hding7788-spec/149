package com.bjsasc.avidm.mq.handler;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.event.dc.DcSynPrincipalRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.message.win10.Win10SynPrincipalRespDcMessage;
import com.bjsasc.avidm.mq.sender.Sender;

import ext.sast.center.bean.SychnUserGroupBean;
import ext.sast.center.bean.message.UserGroupLinkMessageBean;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.util.JsonConvertUtil;
import ext.sast.center.util.QueryUtil;

public class DcsynchPrincipalHandler extends AbstractHandler<DcSynPrincipalRequestEvent> implements Based{
	protected final Logger logger = Logger.getLogger(DcsynchPrincipalHandler.class.getName());
	@Override
	public void onEvent(DcSynPrincipalRequestEvent event) {
		MetaMessage metaMsg = new MetaMessage();
		Sender sender = Sender.getInstance();
		LogMessage logMsg = null;
		String msg_id = "";
		String id = "";
		String name = "";
		try {
			JSONObject msg = event.getContent();
			logger.info("msg @@@ == "+msg);
			msg_id = msg.getString(Based.MSG_ID);
			name = msg.getString(Based.NAME);
			try {
				id = msg.getString(ID);
			} catch (JSONException e) {
				logger.info("DcsynchPrincipalHandler id is not found");
			}
			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, "收到中心域发起的同步用户和组织关系请求，开始处理...");
			sender.addLog(logMsg);

			SychnUserGroupBean bean = QueryUtil.getAllUserAndGroup();
			UserGroupLinkMessageBean link = bean.getUserGroupLinkMessageBean();
			JSONArray arry = JsonConvertUtil.convertUserGroupLinkToJson(link.getJa_principals_response());

			msg = new Win10SynPrincipalRespDcMessage();
			msg.put(Based.MSG_ID,msg_id);
			msg.put(Based.ID,id);
			msg.put(Based.NAME,name);
			msg.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			msg.put(Based.JA_PRINCIPALS_RESPONSE, arry);
			System.out.println("同步用户和组织关系反馈 @@@@ = "+msg);
			sender.send(msg);

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, "处理完成，返回中心域");
			sender.addLog(logMsg);
		} catch (Exception e) {
			e.printStackTrace();

			metaMsg.setMsgId(id);
			metaMsg.setMsgType(Based.META_MSG_TYPE_SYNPRINCIPAL);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITENAME);
			JSONArray sites = new JSONArray();
			sites.put(MQConstants.DC_SITEIID);
			metaMsg.setdDstSites(sites);

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, "厂所处理出错：" + e.getMessage());
			logMsg.setException(e.getMessage());
			sender.addLog(logMsg);
		}

	}

}
