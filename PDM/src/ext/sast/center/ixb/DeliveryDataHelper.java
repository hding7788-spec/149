package ext.sast.center.ixb;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import wt.admin.AdministrativeDomainHelper;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.session.SessionAuthenticator;

import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.Message;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.message.win10.Win10DistributeReqDcMessage;
import com.bjsasc.avidm.mq.sender.Sender;

import ext.sast.center.ixb.util.Deserialize;
import ext.sast.center.ixb.util.SendDataHelper;
import ext.sast.center.synch.MQConstants;

@SuppressWarnings("rawtypes")
public class DeliveryDataHelper implements RemoteAccess{
	private static final Logger log = Logger.getLogger(DeliveryDataHelper.class);
	/**
	 * 中心域--》各厂所
	 * @param obj
	 * @param msgType 会签/预审/发放
	 * @param file
	 * @param inputparams
	 */
	@SuppressWarnings("unchecked")
	public static void sendMessage(Object pbo,String msgType,File file,HashMap inputparams) {
		Sender sender = null;
		MetaMessage metaMsg = null;
		Message msg = null;
		String context = "Unknown";
		LogMessage logMsg = null;
		MethodContext mc = null;
		try {
			mc = MethodContext.getContext(Thread.currentThread());
            if (mc == null)
                mc = new MethodContext(null, null);
            if (mc.getAuthentication() == null) {
                SessionAuthenticator sa = new SessionAuthenticator();
                mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
            }
            context = mc.getId().toString();
            System.out.println("**********设置上下文**********"+context);

		metaMsg = new MetaMessage();
		msg = new Win10DistributeReqDcMessage();
		log.info(msgType + "消息发送开始！");
			JSONObject j_file = SendDataHelper.getFileInfo(file);
			JSONObject j_src_site = SendDataHelper.getSendFromInfo(inputparams);
			JSONObject j_creator = SendDataHelper.getCreatorInfo(inputparams);
			JSONObject j_product = SendDataHelper.getProductInfo(pbo);
			JSONArray ja_dst_sites = SendDataHelper.getSendUnitInfo(inputparams);
			JSONArray ja_receivers = SendDataHelper.getReceiversInfo(pbo);
			JSONArray ja_objects_request = SendDataHelper.getSendObjects(pbo,inputparams);
			String inputparamsStr = Deserialize.serializeObjectMap(inputparams);

			msg.put(Based.MSG_ID,(String)inputparams.get("pbonumber"));
			msg.put(Based.ID, (String)inputparams.get("pbonumber"));
			msg.put(Based.NAME, (String)inputparams.get("pboname"));
			msg.put(Based.J_FILE,j_file);
//			if(msgType.contains("会签")) {
//				msg.put(Based.MSG_TYPE,Based.DC_REQUEST_SIGN_DC);
//			}else if(msgType.contains("发放")) {
//				msg.put(Based.MSG_TYPE,Based.DC_REQUEST_DISTRIBUTE_DC);
//			}else if(msgType.contains("预审")) {
//				msg.put(Based.MSG_TYPE,Based.DC_RESPONSE_SHARE_DC);
//			}
			msg.put(Based.MSG_DESCRIPTION,msgType);
			msg.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());
			//原始发起单位的系统版本
//			msg.put(Based.SYS_VERSION_INITIAL,Based.SYS_VERSION_WIN10);
//			//当前发起请求的系统版本
//			msg.put(Based.SYS_VERSION_REQUEST,Based.SYS_VERSION_WIN10);
			//原始发起单位信息
			msg.put(Based.J_SRC_SITE,j_src_site);

			msg.put(Based.J_PRODUCT,j_product);
			//添加一个主型号信息
			msg.put(Based.J_STD_PRODUCT, "");

			msg.put(Based.J_CREATOR, j_creator);
			//目标站点
			msg.put(Based.JA_DST_SITES,ja_dst_sites);
			msg.put(Based.JA_RECEIVERS,ja_receivers);
			//送审也要放    workpacket转换
			msg.put(Based.JA_OBJECTS_REQUEST,ja_objects_request);
			//msg.put(Based.DC_REQUEST_DISTRIBUTE_DC,ja_objects_request);
			//msg.put(Based.SOAPPARAMS,inputparamsStr);
			log.info(msgType + "消息发送 @@@ = " + inputparamsStr);
			msg.put("soapparams",inputparamsStr);

			sender = Sender.getInstance();
			sender.send(msg);

			metaMsg.setMsgId((String)inputparams.get("pbonumber"));
			metaMsg.setOrderID((String)inputparams.get("pbonumber"));
			metaMsg.setOrderName("");
			//本域唯一标识
			metaMsg.setSrcSiteIID("CASC");
			//本域名称
			metaMsg.setSrcSiteName("八部A4测试");
			//流程监控类型
			metaMsg.setMsgType(Based.META_MSG_TYPE_SIGNATURE);
			//发起人
			metaMsg.setUserIID("");
			metaMsg.setUserID("");
			metaMsg.setUserName("");
			// 接收单位的概要信息（如有几个接收单位，可合并在一块）
			//接收单位名称
			metaMsg.setdDstSiteInfo(MQConstants.SITENAME_149);
			JSONArray dstSites = new JSONArray();
			//iid,
			dstSites.put(MQConstants.SITEIID_149);
			//接收单位的IID集合
			metaMsg.setdDstSites(ja_dst_sites);

			sender.addMetaMessage(metaMsg);

			//本域的信息
			logMsg = new LogMessage((String)inputparams.get("pbonumber"), "no8", "no8IID", "消息发送成功");
			sender.addLog(logMsg);
			log.info(msgType + "消息发送成功！");
		} catch (Exception e) {
			e.printStackTrace();

			metaMsg.setMsgId((String)inputparams.get("pbonumber"));
			//当前站点
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			sender.updateMetaMessage(metaMsg);

			logMsg = new LogMessage((String)inputparams.get("pbonumber"), "no8", "no8IID", "消息发送失败");
			//e.getMessage  可能不全
			logMsg.setException(getErrorMessage(e));
			sender.addLog(logMsg);
		} finally {
		if(mc!=null){
			mc.unregister();
		}
	}
	}
	public static String getErrorMessage(Exception e) {
		String errorMsg = null;
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			errorMsg = sw.toString();
			sw.close();
			pw.close();
			errorMsg = "\r\n"+errorMsg+"\r\n";
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return errorMsg;
	}
}
