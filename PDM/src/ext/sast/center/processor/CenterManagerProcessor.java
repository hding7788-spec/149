package ext.sast.center.processor;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.Message;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.message.win10.Win10SynSiteReqDcMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import com.bjsasc.avidm.mq.util.TUUID;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.sast.center.synch.MQConstants;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/12
 * @ Description：
 * @ Modified By：
 */
public class CenterManagerProcessor {

    private static final Logger logger = Logger.getLogger(CenterManagerProcessor.class.getName());

    /**
     * 同步域信息
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult synchDomainInfo(NmCommandBean commandBean) throws WTException {
    	logger.debug("同步域信息开始！");
        FormResult formResult = new FormResult();
        FeedbackMessage feedBackMsg = null;
        String message = "同步成功";
        Message msg  = null;
        MetaMessage metaMsg = new MetaMessage();;
        Sender sender = Sender.getInstance();;
        String iid = "";
        LogMessage logMsg = null;
        try {
        	iid = TUUID.getUUID();
        	
        	msg = new Win10SynSiteReqDcMessage();
        	msg.put(Based.MSG_ID, iid);
        	msg.put(Based.ID, iid);
        	msg.put(Based.NAME, "域信息同步");
        	//msg.put(Based.MSG_TYPE, Based.DC_RESPONSE_REGISTER_RECEIVER);
        	msg.put(Based.MSG_DESCRIPTION, "厂所发起域同步请求");
        	msg.put(Based.MSG_CREATED_TIME, System.currentTimeMillis());
        	//msg.put(Based.VERSION, Based.SYS_VERSION_WIN10);
        	msg.put(Based.JA_SITES_RESPONSE, "");
        	
        	JSONObject j_src_site = new JSONObject();
        	j_src_site.put(Based.SRC_SITE_IID, MQConstants.SITEIID_149);
        	j_src_site.put(Based.SRC_SITE_NAME, MQConstants.SITENAME_149);
        	msg.put(Based.J_SRC_SITE, j_src_site);
        	msg.put(Based.SRC_SITE_IID, MQConstants.SITEIID_149);
        	msg.put(Based.SRC_SITE_NAME, MQConstants.SITENAME_149);
        	System.out.println("msg @@@@ = "+msg);
        	
        	//metaMsg.setMsgStatus(Based.MSG_STATUS_FINISHED);
			metaMsg.setMsgId(iid);
			metaMsg.setOrderID(iid);
			metaMsg.setOrderName("域信息同步");
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgType(Based.META_MSG_TYPE_SYNSITE);
			WTUser current = (WTUser) SessionHelper.manager.getPrincipal();
			metaMsg.setUserIID(current.getPersistInfo().getObjectIdentifier().getId()+"");//发起人
			metaMsg.setUserID(current.getName());
			metaMsg.setUserName(current.getFullName());
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITENAME);
			JSONArray dstSites = new JSONArray();
			dstSites.put(MQConstants.DC_SITEID);
			metaMsg.setdDstSites(dstSites);
			sender.addMetaMessage(metaMsg);
        	
        	logMsg = new LogMessage(iid,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.SITENAME_149+"发起域同步请求");
			sender.addLog(logMsg);
			
			sender.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
			
			metaMsg.setMsgId(iid);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			sender.updateMetaMessage(metaMsg);
			
			logMsg = new LogMessage(iid,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.SITENAME_149+"发起域同步请求失败");
			logMsg.setException(e.getMessage());
			sender.addLog(logMsg);
		}
        
        feedBackMsg = new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null, new String[]{message});
        formResult.addFeedbackMessage(feedBackMsg);
        formResult.setNextAction(FormResultAction.NONE);
        logger.debug("同步域信息开始！");
        return formResult;
    }
}
