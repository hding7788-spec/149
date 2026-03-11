/**
 *
 */
package com.bjsasc.avidm.mq.handler;

import java.io.File;
import java.util.Map;

import ext.sast.center.record.GWMQRecordHelper;
import ext.sast.center.record.GWMQRecordService;
import ext.sast.center.record.bean.GWMQRecord;
import ext.sast.center.synch.MQDataImportHandler;
import ext.sast.center.synch.MQKRSynchHelper;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import wt.log4j.LogR;

import com.bjsasc.avidm.mq.exception.RetryException;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import com.bjsasc.avidm.mq.util.StackTraceUtil;

import ext.casc.util.Tools;
import ext.casc.workflow.CmWorkflowHelper;
import ext.sast.center.ixb.util.Deserialize;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.synch.MQDataReceiveHelper;
import ext.sast.center.util.JsonConvertUtil;

/**
 * @author cfire
 *
 */
public class HandlerProcess {
	private static final Logger logger  = LogR.getLogger(HandlerProcess.class.getName());

	public static void doDcDistributeRequestHandler(JSONObject msg) throws RetryException{
		Sender sender = Sender.getInstance();
        MetaMessage metaMsg = new MetaMessage();
        LogMessage logMsg = null;
        String id = null;
        String src_site_iid = "";

		//跨域协同记录
		try{
			GWMQRecordHelper.processMQRecord(msg);
		}catch(Exception e){
			e.printStackTrace();
		}
		String sendFromName = "";
        try {
			id = msg.getString(Based.MSG_ID);
			System.out.println("msg @@@@ = " + msg);

			boolean  isCm = true;
			try{
				JSONObject j_src_site = msg.getJSONObject(Based.J_SRC_SITE);
				src_site_iid = j_src_site.getString(Based.IID);
				sendFromName = j_src_site.getString(Based.NAME);
				msg.getString("sendFrom");
			}catch(Exception e){
				e.printStackTrace();
				isCm = false;
			}
			//不支持接收单文档会签 begin
			if(!isCm){
				try {
					JSONArray ja_objects = msg.getJSONArray(Based.JA_OBJECTS_REQUEST);
					if(ja_objects.length()==1){
						JSONObject obj = ja_objects.getJSONObject(0);
						String obj_type = obj.getString(Based.OBJECT_TYPE);
						String version = obj.getString(Based.OBJECT_VERSION);
						if(Based.OBJECT_TYPE_DOC.equals(obj_type)){

							sendFromName = sendFromName.replaceAll("测试","" );
							sendFromName = sendFromName.replaceAll("正式","" );
							sendFromName = sendFromName.replaceAll("系统","" );
							sendFromName = sendFromName.replaceAll("二期","" );
							sendFromName = sendFromName.replaceAll("所","" );
							sendFromName = sendFromName.replaceAll("厂","" );

							String docNumber = obj.getString(Based.OBJECT_ID);
							//导入单文档
							String reuslt = MQDataReceiveHelper.receiveData(msg);
							//创建会签单，建立link
							MQDataImportHandler.importProcessEnvelope(docNumber,sendFromName,version,msg,"disOrder");

							if("".equals(reuslt)) {
								logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_9);
								sender.addLog(logMsg);
							}else {
								if(reuslt==null||"null".equals(reuslt)){
									reuslt="导入出错，请联系接收方管理员检查数据问题。";
								}
								metaMsg.setMsgId(id);
								metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
								metaMsg.setSrcSiteIID(src_site_iid);
								sender.updateMetaMessage(metaMsg);

								logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_10 );
								logMsg.setException(reuslt);
								sender.addLog(logMsg);
								throw new RetryException(reuslt);
							}
							GWMQRecordService.updateStateByMsgId(id,"数据导入成功");

//							logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_9);
//							logMsg.setException("接收方目前暂不支持单文档接收");
//							sender.addLog(logMsg);
//
//							metaMsg.setMsgId(id);
//							metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
//							metaMsg.setSrcSiteIID(src_site_iid);
//							sender.updateMetaMessage(metaMsg);

							return;
						}
					}else{
						boolean hasOrder = false;
						for (int i = 0; i < ja_objects.length(); i++) {
							JSONObject object = ja_objects.getJSONObject(i);
							String obj_type = object.getString(Based.OBJECT_TYPE);
							if(Based.OBJECT_TYPE_ORDER.equals(obj_type)){
								hasOrder = true;
								break;
							}
						}
						if(!hasOrder){
							logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_8);
							logMsg.setException("接收方暂时不支持该类数据会签，会签接收必须通过送审单或更改单");
							sender.addLog(logMsg);

							metaMsg.setMsgId(id);
							metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
							metaMsg.setSrcSiteIID(src_site_iid);
							sender.updateMetaMessage(metaMsg);
							return;
						}

					}

				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			//不支持接收单文档会签 end

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_7);
			sender.addLog(logMsg);

			String reuslt = MQDataReceiveHelper.receiveData(msg);
			System.out.println("***********发放结果********************" + reuslt);

			metaMsg.setMsgId(id);
			if(!"".equals(src_site_iid)){
				metaMsg.setSrcSiteIID(src_site_iid);
			}
			if("".equals(reuslt)) {
				logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_9);
				sender.addLog(logMsg);

				metaMsg.setMsgStatus(Based.MSG_STATUS_FINISHED);
			}else {
				logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_10);
				if(reuslt==null||"null".equals(reuslt)){
					reuslt="导入出错，请联系接收方管理员检查数据问题。";
				}
				logMsg.setException(reuslt);
				sender.addLog(logMsg);

				metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);

			}
			sender.updateMetaMessage(metaMsg);
			if(!"".equals(reuslt)){
				throw new RetryException(reuslt);
			}
			GWMQRecordService.updateStateByMsgId(id,"数据导入成功");

			System.out.println("***********厂所成功接收中心域发放消息********************");
        } catch (Exception e) {
			System.out.println("异常信息："+e.getLocalizedMessage());
			metaMsg.setMsgId(id);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			metaMsg.setSrcSiteIID(src_site_iid);
			sender.updateMetaMessage(metaMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_8);
			logMsg.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(logMsg);

			GWMQRecordService.updateStateByMsgId(id,"数据导入失败");

			throw new RetryException(e.getLocalizedMessage());

		}

	}

	public static  void doDcShareRequestHandler(JSONObject msg) throws RetryException{
		Sender sender = Sender.getInstance();

        LogMessage logMsg = null;
        String id = null;
		JSONObject reponseMsg = null;
		String src_site_iid = null;

		//跨域协同记录
		try{
			GWMQRecordHelper.processMQRecord(msg);
		}catch(Exception e){
			e.printStackTrace();
		}

        try {

			JSONObject j_src_site = msg.getJSONObject(Based.J_SRC_SITE);
			src_site_iid = j_src_site.getString(Based.IID);
			//收到之后立刻返回一条消息
			reponseMsg = msg;
			reponseMsg.put(Based.MSG_TYPE, Based.DC_RESPONSE_SHARE_RECEIVER);
			reponseMsg.put(Based.TARGET_SITE_IID, MQConstants.SITEIID_149);
			reponseMsg.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			sender.send(reponseMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_7);
			sender.addLog(logMsg);

			id = msg.getString(Based.MSG_ID);
			logger.info("msg @@@@ = " + msg);

			String reuslt = MQDataReceiveHelper.receiveData(msg);
			logger.info("***********跨域会签结果********************" + reuslt);

			MetaMessage metaMsg = new MetaMessage();
			if("".equals(reuslt)) {
				logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_9);
				sender.addLog(logMsg);
			}else {
				metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);

				logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_10);
				logMsg.setException(reuslt);
				sender.addLog(logMsg);

				metaMsg.setMsgId(id);
				metaMsg.setSrcSiteIID(src_site_iid);
				sender.updateMetaMessage(metaMsg);
				throw new RetryException(reuslt);

			}

			GWMQRecordService.updateStateByMsgId(id,"数据导入成功");
        }catch(Exception e) {
        	e.printStackTrace();

        	MetaMessage metaMsg = new MetaMessage();
			metaMsg.setMsgId(id);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			metaMsg.setSrcSiteIID(src_site_iid);
			sender.updateMetaMessage(metaMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_8);
			logMsg.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(logMsg);

			GWMQRecordService.updateStateByMsgId(id,"数据导入失败");

			throw new RetryException(e);
        }
	}

	public static  void doDcSignRequestHandler(JSONObject msg) throws RetryException{
		Sender sender = Sender.getInstance();
        MetaMessage metaMsg = new MetaMessage();
        LogMessage logMsg = null;
        String id = null;
        String src_site_iid = "";
		//跨域协同记录
		try{
			GWMQRecordHelper.processMQRecord(msg);
		}catch(Exception e){
			e.printStackTrace();
		}

        try {
			id = msg.getString(Based.MSG_ID);
			JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"DcSignRequestHandler");
			logger.info("msg @@@@ = " + msg);

			boolean  isCm = true;
			String sendFromName = "";
			try{
				JSONObject j_src_site = msg.getJSONObject(Based.J_SRC_SITE);
				src_site_iid = j_src_site.getString(Based.IID);
				sendFromName = j_src_site.getString(Based.NAME);

				msg.getString("sendFrom");
			}catch(Exception e){
				e.printStackTrace();
				isCm = false;
			}
			String receiver = null;
			try {
				receiver = MQDataReceiveHelper.addReceiveLogs(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//不支持接收单文档会签 begin
			if(!isCm){
				try {
					JSONArray ja_objects = msg.getJSONArray(Based.JA_OBJECTS_REQUEST);
					if(ja_objects.length()==1){
						JSONObject obj = ja_objects.getJSONObject(0);
						String obj_type = obj.optString(Based.OBJECT_TYPE);
						String version = obj.optString(Based.OBJECT_VERSION);
						String className = obj.optString(Based.OBJECT_CLASSNAME);
						System.out.println("测试单文档会签："+obj_type);
						if(Based.OBJECT_TYPE_DOC.equals(obj_type)
								|| (className.contains("TNO") && Based.OBJECT_TYPE_ORDER.equals(obj_type))){

							sendFromName = sendFromName.replaceAll("测试","" );
							sendFromName = sendFromName.replaceAll("正式","" );
							sendFromName = sendFromName.replaceAll("系统","" );
							sendFromName = sendFromName.replaceAll("二期","" );
							sendFromName = sendFromName.replaceAll("所","" );
							sendFromName = sendFromName.replaceAll("厂","" );

							String docNumber = obj.getString(Based.OBJECT_ID);
							//导入单文档
							String reuslt = MQDataReceiveHelper.receiveData(msg);
							//创建会签单，建立link
							MQDataImportHandler.importProcessEnvelope(docNumber,sendFromName,version,msg,"signature");

							if("".equals(reuslt)) {
								String importState = MQConstants.STATUS_9 ;
								if(receiver!=null&&!"".equals(receiver)){
									importState =importState+ "【会签任务："+receiver+"】";
								}
								logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,importState);
								sender.addLog(logMsg);

								//add by wyq 2021-3-31 11:42:34 为了失败重新导入而更新状态为“处理中”
								metaMsg.setMsgId(id);
								metaMsg.setMsgStatus(Based.MSG_STATUS_PROCESSING);
								metaMsg.setSrcSiteIID(src_site_iid);
								sender.updateMetaMessage(metaMsg);
							}else {
								if(reuslt==null||"null".equals(reuslt)){
									reuslt="导入出错，请联系接收方管理员检查数据问题。";
								}
								metaMsg.setMsgId(id);
								metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
								metaMsg.setSrcSiteIID(src_site_iid);
								sender.updateMetaMessage(metaMsg);

								logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_10 );
								logMsg.setException(reuslt);
								sender.addLog(logMsg);
								throw new RetryException(reuslt);
							}
							GWMQRecordService.updateStateByMsgId(id,"数据导入成功");

//							logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_8);
//							logMsg.setException("接收方目前暂不支持单文档接收");
//							sender.addLog(logMsg);
//
//							metaMsg.setMsgId(id);
//							metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
//							metaMsg.setSrcSiteIID(src_site_iid);
//							sender.updateMetaMessage(metaMsg);
							return;
						}
					}else{
						boolean hasOrder = false;
						for (int i = 0; i < ja_objects.length(); i++) {
							JSONObject object = ja_objects.getJSONObject(i);
							String obj_type = object.getString(Based.OBJECT_TYPE);
							if(Based.OBJECT_TYPE_ORDER.equals(obj_type)){
								hasOrder = true;
								break;
							}
						}
						if(!hasOrder){
							logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_8);
							logMsg.setException("接收方暂时不支持该类数据会签，会签接收必须通过送审单或更改单");
							sender.addLog(logMsg);

							metaMsg.setMsgId(id);
							metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
							metaMsg.setSrcSiteIID(src_site_iid);
							sender.updateMetaMessage(metaMsg);
							return;
						}

					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			//不支持接收单文档会签 end

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_7);
			sender.addLog(logMsg);


			String reuslt = MQDataReceiveHelper.receiveData(msg);
			logger.info("***********跨域会签结果********************" + reuslt);
			if("".equals(reuslt)) {
				String importState = MQConstants.STATUS_9 ;
				if(receiver!=null&&!"".equals(receiver)){
					importState =importState+ "【会签任务："+receiver+"】";
				}
				logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,importState);
				sender.addLog(logMsg);

				//add by wyq 2021-3-31 11:42:34 为了失败重新导入而更新状态为“处理中”
				metaMsg.setMsgId(id);
				metaMsg.setMsgStatus(Based.MSG_STATUS_PROCESSING);
				metaMsg.setSrcSiteIID(src_site_iid);
				sender.updateMetaMessage(metaMsg);
			}else {
				if(reuslt==null||"null".equals(reuslt)){
					reuslt="导入出错，请联系接收方管理员检查数据问题。";
				}
				metaMsg.setMsgId(id);
				metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
				metaMsg.setSrcSiteIID(src_site_iid);
				sender.updateMetaMessage(metaMsg);

				logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_10 );
				logMsg.setException(reuslt);
				sender.addLog(logMsg);
				throw new RetryException(reuslt);
			}
			logger.info("***********厂所成功接收中心域会签消息********************");

			GWMQRecordService.updateStateByMsgId(id,"数据导入成功");
        } catch (Exception e) {
			System.out.println("异常信息："+e.getLocalizedMessage());

			metaMsg.setMsgId(id);
			metaMsg.setSrcSiteIID(src_site_iid);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			sender.updateMetaMessage(metaMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_8);
			logMsg.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(logMsg);

			GWMQRecordService.updateStateByMsgId(id,"数据导入失败");

			throw new RetryException(e);


		}
	}

	public static  void doDcSignTaskSynResponseHandler(JSONObject msg) throws RetryException{
		Sender sender = Sender.getInstance();
        MetaMessage metaMsg = new MetaMessage();
        LogMessage logMsg = null;
        String id = null;
        String src_site_iid = MQConstants.SITEIID_149;
        try {
			id = msg.getString(Based.MSG_ID);
			logger.info("msg @@@@ = " + msg);

//			JSONObject j_src_site = msg.getJSONObject(Based.J_SRC_SITE);
//			src_site_iid = j_src_site.getString(Based.IID);

//			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,"成功接收并开始处理会签反馈信息");
//			sender.addLog(logMsg);
			String result = null;

			if(msg.has("sendFrom")){
				String siteId = msg.getString("sendFrom");
				if(MQConstants.SITEID_149KRS.equals(siteId)) {
					result = MQKRSynchHelper.feedback(msg);
				} else {
					result = MQDataReceiveHelper.receiveProcessNoticeFeedbackData(msg);
				}
			}else{
				result = MQDataReceiveHelper.receiveProcessNoticeFeedbackData(msg);
			}

			logger.info("***********会签反馈接收********************" +result);
			metaMsg.setMsgId(id);
			if("".equals(result)) {
				metaMsg.setMsgStatus(Based.MSG_STATUS_FINISHED);
				logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_15);
				sender.addLog(logMsg);
				sender.updateMetaMessage(metaMsg);

			}else {
				metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
				metaMsg.setSrcSiteIID(src_site_iid);

				logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.STATUS_16);
				logMsg.setException(result);
				sender.addLog(logMsg);
				sender.updateMetaMessage(metaMsg);

				throw new RetryException(result);

			}

			GWMQRecordService.updateStateByMsgId(id,"会签反馈完成");

			logger.info("***********厂所成功接收中心域会签反馈的消息********************");
        } catch (Exception e) {
			e.printStackTrace();

			metaMsg.setMsgId(id);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			metaMsg.setSrcSiteIID(src_site_iid);
			sender.updateMetaMessage(metaMsg);

			logMsg = new LogMessage(id,MQConstants.SITENAME_149,MQConstants.SITEIID_149,MQConstants.SITENAME_149+"上游接收意见失败");
			logMsg.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(logMsg);

			GWMQRecordService.updateStateByMsgId(id,"会签反馈失败");

			throw new RetryException(e);

		}
	}

	public static  void doDcSignTeminateRequestHandler(JSONObject msg) throws RetryException{
		Sender sender = Sender.getInstance();
		String orderIID = "";
		LogMessage lm = null;
		String log = "";
		boolean isCm = false;
		String sendFrom = "";
		try {
			sendFrom = msg.getString("sendFrom");
			isCm = true;
		} catch (Exception e) {
			e.printStackTrace();
			isCm = false;
		}
		try {
			logger.info("DcSignTeminateRequestHandler @@@@ = "+msg);
			orderIID = msg.getString(Based.MSG_ID);

			String orderName = msg.getString(Based.NAME);
			logger.debug("开始处理数据中心转发的会签强制结束请求，会签单名称：" + orderName);
			String src_site_iid =  MQConstants.SITEIID_149;

			String targetSiteIID = msg.getString(Based.TARGET_SITE_IID);
			if(targetSiteIID.equals(MQConstants.SITEIID_149)) {
				if(isCm) {
					if(sendFrom.contains("805")){
						String pbonumber = msg.getString("pbonumber");
						String workflowType = msg.getString("workflowType");
						if(!Tools.isNull(pbonumber)&&!Tools.isNull(workflowType)){
							CmWorkflowHelper.teminateRequest(pbonumber, workflowType);
						}
					}else{
						String soapparamsStr = msg.getString("soapparams");
						Map<?,?> map = Deserialize.deserializeMap(soapparamsStr);
						String pbonumber = (String) map.get("pbonumber");
						String workflowType = (String) map.get("workflowType");
						ext.casc.workflow.CmWorkflowHelper.deletePeOrCp(pbonumber,workflowType);
					}

				}else {
					//用于强制结束专业所的流程
					if(msg.has("pbonumber")){
						String pbonumber = msg.getString("pbonumber");
						String workflowType = msg.getString("workflowType");
						if(!Tools.isNull(pbonumber)&&!Tools.isNull(workflowType)){
							CmWorkflowHelper.teminateRequest(pbonumber, workflowType);
						}
					}else{
						//待处理
					}
				}
			}
			// 已存在单据处理
			log = MQConstants.STATUS_17;
			lm = new LogMessage(orderIID, MQConstants.SITENAME_149, MQConstants.SITEIID_149, log);
			sender.addLog(lm);

			MetaMessage metaMsg = new MetaMessage();
			metaMsg.setMsgId(orderIID);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FINISHED);
			metaMsg.setSrcSiteIID(src_site_iid);
			sender.updateMetaMessage(metaMsg);
		} catch (Exception e) {
			log = MQConstants.STATUS_18;
			logger.error(log, e);

			lm = new LogMessage(orderIID, MQConstants.SITENAME_149, MQConstants.SITEIID_149, log);
			lm.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(lm);

			MetaMessage metaMsg = new MetaMessage();
			metaMsg.setMsgId(orderIID);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			sender.updateMetaMessage(metaMsg);

			throw new RetryException(e);
		}

	}
}
