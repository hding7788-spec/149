package com.bjsasc.avidm.mq.handler;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.event.dc.DcRegisterRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.sender.Sender;

import ext.sast.center.bean.message.SiteMessageBean;
import ext.sast.center.processor.SaveSiteInfoProcessor;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.util.JsonConvertUtil;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/18
 * @ Description：域信息注册
 *                          中心域推送到厂所
 * @ Modified By�?
 */
public class SiteRegisterHandler extends AbstractHandler<DcRegisterRequestEvent> implements Based {
    public SiteRegisterHandler(){}
    @Override
    public void onEvent(DcRegisterRequestEvent siteRegisterEvent) {
        System.out.println("开始处理域信息注册请求..");
        
        Sender sender = Sender.getInstance();
        MetaMessage metaMsg = new MetaMessage();
        LogMessage logMsg = null;
        String id = null;
        try {
			JSONObject msg = siteRegisterEvent.getContent();
			System.out.println("msg @@@@ = " + msg);
			id = msg.getString(Based.MSG_ID);
        	
        	logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"开始接收域注册信息");
			sender.addLog(logMsg);
            
            //收集信息，构建对象,保存中心域域信息
            SiteMessageBean siteMessageCenter = JsonConvertUtil.convertJSONObjectToSiteMessageBean(msg);
            SaveSiteInfoProcessor.saveSiteInfo(siteMessageCenter,siteMessageCenter.getOperate_type());
          
            JSONObject json = new JSONObject();
            //唯一的字符串
            json.put(IID, MQConstants.SITEIID_149);
            json.put(ID, MQConstants.SITEID_149);
            json.put(NAME,MQConstants.SITENAME_149);
            json.put(VERSION,Based.SYS_VERSION_WIN10);
            json.put(IP, MQConstants.SITEIP_149);
            json.put(PORT, MQConstants.SITEPORT_149);
            json.put(ACADEMY_ID,siteMessageCenter.getJ_siteinfo_response().getAcademy_id());
            json.put(ACADEMY_NAME,siteMessageCenter.getJ_siteinfo_response().getAcademy_name());
            json.put(IS_DC,"false");
            json.put(SOAPRECEIVER_URL,MQConstants.SOAPRECEIVER_URL_149);
            
            msg.put(Based.MSG_TYPE, DC_RESPONSE_REGISTER_RECEIVER);
            msg.put(Based.SYS_VERSION_REQUEST, SYS_VERSION_WIN10);
            msg.put(Based.J_SITEINFO_RESPONSE, json);
            
            System.out.println("149域注册返回message @@ = " + msg);
            sender.send(msg);
            
            metaMsg.setMsgId(id);
			metaMsg.setSrcSiteIID((MQConstants.SITEIID_149));
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgType(Based.META_MSG_TYPE_SITEREGISTER);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FINISHED);
			//目标单位
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITENAME);
			JSONArray dtsSiteArray = new JSONArray();
			dtsSiteArray.put(MQConstants.DC_SITEIID);
			metaMsg.setdDstSites(dtsSiteArray);
			sender.addMetaMessage(metaMsg);
            
            logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"成功接收并返回域注册信息");
			sender.addLog(logMsg);
            
            System.out.println("域信息注册请求处理完毕！");
        } catch (Exception e) {
            e.printStackTrace();
            metaMsg.setMsgId(id);
            metaMsg.setSrcSiteIID((MQConstants.SITEIID_149));
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgType(Based.META_MSG_TYPE_SITEREGISTER);
            metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
            //目标单位
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITEID);
			JSONArray dtsSiteArray = new JSONArray();
			dtsSiteArray.put(MQConstants.DC_SITEID);
			metaMsg.setdDstSites(dtsSiteArray);
			sender.addMetaMessage(metaMsg);
			
			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"域注册接收失败：" + e.getMessage());
			sender.addLog(logMsg);
        }
    }
}
