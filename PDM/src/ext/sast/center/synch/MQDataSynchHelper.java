package ext.sast.center.synch;

import com.bjsasc.avidm.mq.fileserver.FSUtil;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.Message;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.message.win10.Win10DistributeReqDcMessage;
import com.bjsasc.avidm.mq.message.win10.Win10SignReqDcMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import com.bjsasc.avidm.mq.util.StackTraceUtil;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.extend.ixb.*;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedUtil;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.ixb.CmExportHandler;
import ext.casc.ixb.ExpImpLogger;
import ext.sast.center.ixb.util.Deserialize;
import ext.sast.center.record.GWMQRecordService;
import ext.sast.center.record.bean.GWMQRecord;
import ext.sast.center.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMFamily;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.*;
import wt.ixb.clientAccess.StandardIXBService;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class MQDataSynchHelper {
	public  static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void reviewDataToPackages(WTObject pbo, WTReference self, Map unitToFileMap) {
		String prompt = null;
		unitToFileMap = null;
		ArrayList<String> sendUnits = new ArrayList<String>();
		try {
			for(int i=0;i<sendUnits.size();i++) {
				String sendUnit = sendUnits.get(i);
				if(pbo instanceof ProcessEnvelope) {
					ProcessEnvelope pe = (ProcessEnvelope) pbo;
						String fileName = MQDataExportHelper.exportProcessEnvelopeTargets(pe, sendUnit);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}


	}
	/**
	 * 数据发送入口
	 * @param pbo
	 * @param wfprocess
	 * @param self
	 * @param unitToFileMap
	 * @param sendSites
	 * @param sendFlag
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void deliveryDataToSelectedUnit(WTObject pbo, WfProcess wfprocess,WTReference self, Map unitToFileMap, List sendSites,Map sendFlag)
			throws Exception {
		// 改为消息机制发送
		Message msg = null;
		Sender sender = Sender.getInstance();
		MetaMessage metaMsg = new MetaMessage();
		LogMessage logMsg = null;
//		try {
//			MethodContext mc = MethodContext.getContext(Thread.currentThread());
//			if (mc == null)
//				mc = new MethodContext(null, null);
//			if (mc.getAuthentication() == null) {
//				SessionAuthenticator sa = new SessionAuthenticator();
//				mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
//			}
//		} catch (Throwable t) {
//			t.printStackTrace();
//			System.err.println("Error create service session context.");
//		}
		List memberList = null;
		//HashMap inputparams = new HashMap();
		String reviewType = "PROCESSENVELOPE";
		String workflowType = "";
		ProcessData pd = wfprocess.getContext();
		String pboNumber = "";
		String pboName = "";
		String productIID = "";
		String productName = "";
		WTUser creator = null;

		if (pbo instanceof WTDocument) {
			// 文档签审只有工艺会签
			reviewType = "PROCESSNOTICE";
			WTDocument doc = (WTDocument) pbo;
			reviewType = "WTDOCUMENT";
			productName = ((WTDocument) pbo).getContainerName();
			creator = (WTUser) doc.getCreator().getPrincipal();
		} else if (pbo instanceof ProcessEnvelope) {
			reviewType = "PROCESSENVELOPE";
			ProcessEnvelope pe = (ProcessEnvelope) pbo;
			productIID = PersistenceHelper.getObjectIdentifier(pe).getId() + "";
			productName = pe.getContainerName();
			pboNumber = pe.getNumber();
			pboName = pe.getName();
			creator = (WTUser) pe.getCreator().getPrincipal();
			//获取所有对象
			memberList = CmExpImpSearchHelper.searchAllEnvelopeMemberLink(pe);


		} else if (pbo instanceof ChangePackaged) {
			reviewType = "WTCHANGEORDER";
			ChangePackaged co = (ChangePackaged) pbo;
			productIID = PersistenceHelper.getObjectIdentifier(co).getId() + "";
			productName = co.getContainerName();
			pboNumber = co.getNumber();
			pboName = co.getName();
			creator = (WTUser) co.getCreator().getPrincipal();
			//获取所有对象

		}
		String processOid = wfprocess.getPersistInfo().getObjectIdentifier().getStringValue();
		Map activityOidMap = (Map) pd.getValue("activityOidMap");


		//组装消息数据
		JSONArray jsonArray = new JSONArray();
		msg = new Win10SignReqDcMessage(jsonArray);
		msg.put(Based.MSG_ID, pboNumber);
		msg.put(Based.ID, pboNumber);
		msg.put(Based.NAME, pboName);
		msg.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());
		metaMsg.setMsgType(Based.META_MSG_TYPE_SIGNATURE);
		//创建者
		JSONObject j_creator = new JSONObject();
		j_creator.put(Based.IID, creator.getPersistInfo().getObjectIdentifier().getId()+"");
		j_creator.put(Based.ID, creator.getName());
		j_creator.put(Based.NAME, creator.getFullName());
		msg.put(Based.J_CREATOR, j_creator);
		//原始站点
		JSONObject src_site = new JSONObject();
		src_site.put(Based.IID, MQConstants.SITEIID_149);
		src_site.put(Based.ID, MQConstants.SITEID_149);
		src_site.put(Based.NAME, MQConstants.SITENAME_149);
		msg.put(Based.J_SRC_SITE, src_site);
		//目标站点
		JSONArray dst_siteArray = new JSONArray();
		for(int i=0;i<sendSites.size();i++) {
			JSONObject jsonUnit = QuerySiteUtil.getDstSite((String)sendSites.get(i));
			dst_siteArray.put(jsonUnit);
		}
		msg.put(Based.JA_DST_SITES, dst_siteArray);
		//原始型号
		JSONObject j_product = new JSONObject();
		j_product.put(Based.IID, productIID);
		j_product.put(Based.ID, productName);
		j_product.put(Based.NAME, productName);
		msg.put(Based.J_PRODUCT, j_product);
		//主型号
		JSONObject j_std_product = ProductConvertUtil.getSastProdcutInfo(productName);
		msg.put(Based.J_STD_PRODUCT, j_std_product);

		//超库产品信息
		JSONArray exceedProductArray = new JSONArray();
		JSONArray j_std_products = QueryUtil.getAllMappingProductInfo(productName,memberList,exceedProductArray);
		msg.put("j_std_products", j_std_products);
		//对象信息
		JSONArray ja_objects = QueryUtil.getAllObjectInfo(pbo,memberList);
		msg.put(Based.JA_OBJECTS_REQUEST, ja_objects);

		//JSONArray j_files = new JSONArray();
		//JSONArray j_soapparams = new JSONArray();
		try{
			logMsg = new LogMessage(pboNumber, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.SITENAME_149+"准备将文件上传到REST文件服务系统...");
			sender.addLog(logMsg);

			String fileName_803 = null;
			for(int i=0;i<sendSites.size();i++){



			}

			logMsg = new LogMessage(pboNumber, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"文件加密并上传成功");
			sender.addLog(logMsg);

		}catch (Exception e) {
			e.printStackTrace();

			logMsg = new LogMessage(pboNumber, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"文件加密并上传失败，等待处理...");
			sender.addLog(logMsg);
		}

	}
	/**
	 *  工艺技术通知单发送入口
	 * @param pbo
	 * @param self
	 * @param approvedType
	 * @param route
	 * @param sendTo
	 * @throws WTException
	 */
	public static void distributeProcessNotice(WTObject pbo,ObjectReference self, String approvedType,String route,String sendTo) throws WTException {
		WfActivity activity = (WfActivity) self.getObject();
		WfProcess process = activity.getParentProcess();
		ProcessData pd = process.getContext();
		String selectUnitValue = (String) pd.getValue("selectUnitValue");

		if(pbo instanceof WTDocument){
			WTDocument doc = (WTDocument) pbo;

			String msgId = "";
			GWMQRecord record = null;
			try {
				msgId = ((WTDocument)pbo).getNumber();
				record = GWMQRecordService.getRecordByMsgId(msgId);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(record == null){
				record = new GWMQRecord();
			}
			record.setKeyId(msgId);
			record.setMsgId(msgId);
			record.setSender("149");

			String siteId = selectUnitValue.substring(selectUnitValue.lastIndexOf("(") + 1, selectUnitValue.lastIndexOf(")"));
			JSONObject siteInfo = QuerySiteUtil.getDstSite(siteId);
			String dstSiteName = siteInfo.getString("name");

			String selectUsersValue = (String) pd.getValue("selectUsersValue");

			//String userId = selectUsersValue.substring(0,selectUsersValue.indexOf("("));
			String userName = selectUsersValue.substring(selectUsersValue.lastIndexOf("(")+1,selectUsersValue.lastIndexOf(")"));
			if(userName.length()>0){
				record.setReceiver(dstSiteName+"("+userName+")");
			}else{
				record.setReceiver(dstSiteName);
			}

			Calendar c = Calendar.getInstance();
			c.add(Calendar.HOUR, 8);
			Date now = c.getTime();

			String sendTime = now.getTime()+"";
			record.setOrderNumber(doc.getNumber());
			record.setOrderName(doc.getName());
			record.setProductName(doc.getContainerName());
			record.setSendType("送审单");
			record.setSendState("数据打包中");
			record.setSendTime(sendTime);
			record.setUpdateTime(sendTime);
			record.setProcessName("149工艺技术通知单流程");
			record.setOrderOwner(doc.getModifier().getFullName());
			record.setCreateTimeStamp(sendTime);
			record.setMsgState("处理中");

			String fileName = ext.sast.center.synch.MQDataExportHelper.exportProcessNotice((WTDocument)pbo, selectUnitValue);
			if(fileName != null) {
				record.setSendState("数据打包成功");
				try {
					deliveryData(self,fileName,approvedType,route, selectUnitValue);
					record.setSendState("数据发送成功");
				} catch (Exception e) {
					e.printStackTrace();
					record.setSendState("数据发送失败");
				}

			}else{
				record.setSendState("数据打包失败");
			}

			try {
				GWMQRecordService.add(record);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("##################该类型不是文档##################");
		}
	}


	public static void distributeCommonProcess(WTObject pbo,ObjectReference self,String route) throws WTException {
		WfProcess process = null;
		if( self.getObject() instanceof  WfActivity){
			WfActivity activity = (WfActivity) self.getObject();
			process = activity.getParentProcess();
		}else if( self.getObject() instanceof  WfProcess){
			process = (WfProcess) self.getObject();
		}
		WTDocument doc = null;
		String msgId = "";
		GWMQRecord record = null;

		if(pbo instanceof WTDocument){
			doc = (WTDocument) pbo;
			msgId = doc.getNumber();
		}else if(pbo instanceof WTChangeOrder2){
			WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
			msgId = ecn.getNumber();
			QueryResult qResult2 = ChangeHelper2.service.getChangeablesAfter(ecn);
			while (qResult2.hasMoreElements()) {
				Object object = qResult2.nextElement();
				if (object instanceof WTDocument) {
					doc = (WTDocument) object;
					break;
				}
			}
		}

		if(doc==null) return;

		try {
			record = GWMQRecordService.getRecordByMsgId(msgId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(record == null){
			record = new GWMQRecord();
		}
		record.setKeyId(msgId);
		record.setMsgId(msgId);
		record.setSender("149");

		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, 8);
		Date now = c.getTime();

		String sendTime = now.getTime()+"";
		record.setOrderNumber(doc.getNumber());
		record.setOrderName(doc.getName());
		record.setProductName(doc.getContainerName());
		record.setSendType("发放单");
		record.setSendState("数据打包中");
		record.setSendTime(sendTime);
		record.setUpdateTime(sendTime);
		if(process!=null){
			record.setProcessName(process.getName());
		}
		record.setOrderOwner(doc.getModifier().getFullName());
		record.setCreateTimeStamp(sendTime);
		record.setMsgState("处理中");
		String fileName = ext.sast.center.synch.MQDataExportHelper.exportCommonProcess(doc);
		if(fileName != null) {
			record.setSendState("数据打包成功");
			try {
				deliveryCommonData(self,fileName,route);
				record.setSendState("数据发送成功");
			} catch (Exception e) {
				e.printStackTrace();
				record.setSendState("数据发送失败");
			}

		}else{
			record.setSendState("数据打包失败");
		}
		try {
			GWMQRecordService.add(record);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 工艺技术通知单发送程序
	 * @param self
	 * @param fileName
	 * @param approvedType
	 * @param route
	 * @param selectUnitValue
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String deliveryData(ObjectReference self, String fileName, String approvedType, String route,
			String selectUnitValue) throws WTException {
		String result = "";
		Sender sender = Sender.getInstance();
		MetaMessage metaMsg = new MetaMessage();
		Message msg = null;
		LogMessage logMsg = null;
		String msg_id = null;
		try {
			String activityOid149 = self.getObjectId().toString();
			WfActivity activity = (WfActivity) self.getObject();
			HashMap inputparams = new HashMap();
			WfProcess process = activity.getParentProcess();
			ProcessData pd = process.getContext();
			WTDocument doc = (WTDocument) pd.getValue("primaryBusinessObject");
			if(doc != null) {
				msg_id = doc.getNumber();
			}else {
				return "pbo is null";
			}
			//TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
			//if (TypeHelper.isA(ti, TypeHelper.getTypeIdentifier("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE"))) {
			approvedType = "PROCESSNOTICE";
			//}
			pd.setValue("activityOidTemp", activityOid149);
			// 外部会签单位等待活动oid
			Object activityOid = pd.getValue("activityOid");
			String selectUsersValue = (String) pd.getValue("selectUsersValue");
			String siteAndUserIId = (String) pd.getValue("siteAndUserIId");
			String useriids = "";
			if (siteAndUserIId != null && siteAndUserIId.contains("-")){
				useriids = siteAndUserIId.split("-")[1];
			}
			PersistenceHelper.manager.save(process);
			// 使用反馈方法中的相关配置信息，故将sendTo 的参数变为sendFrom
			inputparams.put("sendFrom", selectUnitValue);
			inputparams.put("sendUnit", "149");
			inputparams.put("usersValue", selectUsersValue);
			if (activityOid != null && !"".equals(activityOid)) {
				if ("create".equals(route)) {
					inputparams.put("route", "back");
				} else if ("pass".equals(route)) {
					inputparams.put("route", "pass");
				}
			} else {
				inputparams.put("route", "");
			}
			if (fileName != null) {

				metaMsg.setMsgId(msg_id);
				metaMsg.setOrderID(msg_id);
				metaMsg.setOrderName(doc.getName());
				metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
				metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
				metaMsg.setMsgType(Based.META_MSG_TYPE_SIGNATURE);
				String siteId = selectUnitValue.substring(selectUnitValue.indexOf("(") + 1, selectUnitValue.indexOf(")"));

				List sendSites = new ArrayList();
				sendSites.add(siteId);
				JSONObject dst = QuerySiteUtil.getDstSiteInfoForMetaMessage(sendSites);
				metaMsg.setdDstSiteInfo(dst.getString("META_DSTNAME"));
				JSONArray dstSiteArray = dst.getJSONArray("META_DSTIID");
				metaMsg.setdDstSites(dstSiteArray);
				System.out.println("流程监控信息 metaMsg@@@ = "+metaMsg);
				sender.addMetaMessage(metaMsg);

				JSONObject siteInfo = QuerySiteUtil.getDstSite(siteId);
				String dstSiteName = siteInfo.getString("name");

				logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_1+"【"+dstSiteName+"】【"+fileName+"】");
				sender.addLog(logMsg);

				WTProperties prop = WTProperties.getLocalProperties();
				String hostName = prop.getProperty("java.rmi.server.hostname");
				String url = "http://" + hostName + "/" + fileName;
				inputparams.put("URL", url);
				inputparams.put("activityOidSF", activityOid149);
				inputparams.put("approvedType", approvedType);

				System.out.println("工艺技术通知单发往总体所@@@@@@@@@@@@@@"+selectUnitValue);

				JSONArray array = new JSONArray();
				msg = new Win10SignReqDcMessage(array);

				msg.put(Based.MSG_ID, msg_id);
				msg.put(Based.ID, msg_id);
				msg.put(Based.NAME, doc.getName());
				msg.put(Based.MSG_DESCRIPTION, "工艺技术通知单");
				msg.put(Based.MSG_CREATED_TIME, System.currentTimeMillis());
				JSONArray j_receivers = new JSONArray();
				if(selectUsersValue.contains(";")){
					String[] ss = selectUsersValue.split(";");
					String[] useriid = useriids.split(";");
					int i =0;
					if(useriid.length==ss.length){
						for(String s:ss){
							if("()".equals(s)){
								continue;
							}
							JSONObject j_receiver = new JSONObject();
							String userId =	 s.substring(0,s.indexOf("("));
							String userName = s.substring(s.indexOf("(")+1,s.indexOf(")"));

							j_receiver.put(Based.SITE_IID,siteInfo.get(Based.IID));
							j_receiver.put(Based.IID,useriid[i]);
							j_receiver.put(Based.ID,userId);
							j_receiver.put(Based.NAME,userName);
							j_receivers.put(j_receiver);
							i++;
						}
					}else{
						JSONObject j_receiver = new JSONObject();
						String userId =	 selectUsersValue.substring(0,selectUsersValue.indexOf("("));
						String userName = selectUsersValue.substring(selectUsersValue.indexOf("(")+1,selectUsersValue.indexOf(")"));
						j_receiver.put(Based.SITE_IID,siteInfo.get(Based.IID));
						j_receiver.put(Based.IID,useriids);
						j_receiver.put(Based.ID,userId);
						j_receiver.put(Based.NAME,userName);
						j_receivers.put(j_receiver);
					}

				}else{
					JSONObject j_receiver = new JSONObject();
					String userId =	 selectUsersValue.substring(0,selectUsersValue.indexOf("("));
					String userName = selectUsersValue.substring(selectUsersValue.indexOf("(")+1,selectUsersValue.indexOf(")"));
					j_receiver.put(Based.SITE_IID,siteInfo.get(Based.IID));
					j_receiver.put(Based.IID,useriids);
					j_receiver.put(Based.ID,userId);
					j_receiver.put(Based.NAME,userName);
					j_receivers.put(j_receiver);
				}

				msg.put(Based.JA_RECEIVERS, j_receivers);

				String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp" + File.separator
						+ fileName;
				String url_upload = ext.sast.center.synch.MQConstants.DC_UPLOAD;
				JSONObject j_file = FSUtil.upload(localPath, url_upload);
				msg.put(Based.J_FILE, j_file);

				JSONArray j_files = new JSONArray();
				j_file.put(Based.SITE_IID, siteInfo.get(Based.IID));
				j_files.put(j_file);
				msg.put("j_files", j_files);


				// 原始发起单位信息
				JSONObject j_src_site = new JSONObject();
				j_src_site.put(Based.IID, MQConstants.SITEIID_149);
				j_src_site.put(Based.ID, MQConstants.SITEID_149);
				j_src_site.put(Based.NAME, MQConstants.SITENAME_149);
				msg.put(Based.J_SRC_SITE, j_src_site);

				String productName = doc.getContainerName();
				String productIID = doc.getContainer().getPersistInfo().getObjectIdentifier().getId() + "";
				// 原始型号
				JSONObject j_product = new JSONObject();
				j_product.put(Based.IID, productIID);
				j_product.put(Based.ID, productName);
				j_product.put(Based.NAME, productName);
				msg.put(Based.J_PRODUCT, j_product);
				// 主型号
				JSONObject j_std_product = ProductConvertUtil.getSastProdcutInfo(productName);
				if(!j_std_product.has(Based.PRODUCT_IID)){
					logMsg = new LogMessage(doc.getNumber(), MQConstants.SITENAME_149, MQConstants.SITEIID_149,"型号映射错误，需重新进行型号映射！");
					logMsg.setException("需149管理员进行型号映射后重新发送！");
					sender.addLog(logMsg);
				}
				j_std_product.put(Based.IID, j_std_product.getString(Based.PRODUCT_IID));
				msg.put(Based.J_STD_PRODUCT, j_std_product);
				// 映射的型号
				JSONArray ja_std_products = new JSONArray();
				ja_std_products.put(j_std_product);
				msg.put("ja_std_products", ja_std_products);

				JSONObject j_creator = new JSONObject();
				WTUser creator = (WTUser) doc.getCreator().getPrincipal();
				j_creator.put(Based.IID, creator.getPersistInfo().getObjectIdentifier().getId() + "");
				j_creator.put(Based.ID, creator.getName());
				j_creator.put(Based.NAME, creator.getFullName());
				msg.put(Based.J_CREATOR, j_creator);

				JSONArray ja_dst_sites = new JSONArray();
				ja_dst_sites.put(siteInfo);
				msg.put(Based.JA_DST_SITES, ja_dst_sites);

				JSONArray ja_objects_request = new JSONArray();
				JSONObject object = new JSONObject();
				object.put(Based.OBJECT_OID, IxbHndHelper.getObjectIdImage(doc));
				object.put(Based.OBJECT_MASTER_IID,
						doc.getMaster().getPersistInfo().getObjectIdentifier().getId() + "");
				object.put(Based.OBJECT_ID, doc.getNumber());
				object.put(Based.OBJECT_NAME, doc.getName());
				object.put(Based.OBJECT_STATE, doc.getLifeCycleState().getDisplay(Locale.CHINA));
				object.put(Based.OBJECT_VERSION, doc.getVersionInfo().getIdentifier().getValue()+"."+doc.getIterationInfo().getIdentifier().getValue());
				object.put(Based.OBJECT_CLASSNAME,
						TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc).toString());
				object.put(Based.OBJECT_TYPE, Based.OBJECT_TYPE_DOC);
				ja_objects_request.put(object);
				msg.put(Based.JA_OBJECTS_REQUEST, ja_objects_request);

				JSONArray j_soapparams = new JSONArray();
				JSONObject soap = new JSONObject();
				soap.put(Based.SITE_IID, siteInfo.get(Based.IID));

				String inputparamsStr = Deserialize.serializeMap(inputparams);
				soap.put("soapparams", inputparamsStr);
				j_soapparams.put(soap);
				msg.put("j_soapparams", j_soapparams);

				System.out.println("工艺技术通知单 @@@@@@@@@@@@@@ = " + msg);

				msg.put("sendFrom", "149");

				logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_3);
				sender.addLog(logMsg);

				sender.send(msg);

				JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"Send");

				//GWMQRecordService.updateStateByMsgId(msg_id,"数据发送成功");
			}else {
				logMsg = new LogMessage(doc.getNumber(), MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_2);
				logMsg.setException("数据打包异常，请联系管理员重新发送");
				sender.addLog(logMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getLocalizedMessage()!=null&&e.getLocalizedMessage().contains("未初始化连接")){
				throw new WTException("发送消息工具-无法发送消息：未初始化连接");
			}
			metaMsg.setMsgId(msg_id);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			sender.addMetaMessage(metaMsg);

			logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_4);
			logMsg.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(logMsg);

			GWMQRecordService.updateStateByMsgId(msg_id,"数据发送失败");
		}
		return result;
	}

	public static String deliveryCommonData(ObjectReference self, String fileName,String route) throws WTException {
		String result = "";
		Sender sender = Sender.getInstance();
		MetaMessage metaMsg = new MetaMessage();
		Message msg = null;
		LogMessage logMsg = null;
		String msg_id = null;
		try {
			//String activityOid149 = self.getObjectId().toString();
			WfActivity activity = (WfActivity) self.getObject();
			WfProcess process = activity.getParentProcess();
			ProcessData pd = process.getContext();
			WTDocument doc = (WTDocument) pd.getValue("primaryBusinessObject");
			if(doc != null) {
				msg_id = doc.getNumber();
			}else {
				return "pbo is null";
			}
			if("发放".equals(route)){
				msg_id = msg_id+"_FF";
			}
			//pd.setValue("activityOidTemp", activityOid149);
			// 外部会签单位等待活动oid
			String selectUsersValue = (String) pd.getValue("selectUsersValue");
			String selectUnitValue = (String) pd.getValue("selectUnitValue");
			String siteAndUserIId = (String) pd.getValue("siteAndUserIId");
			String useriids = "";
			if (siteAndUserIId != null && siteAndUserIId.contains("-")){
				useriids = siteAndUserIId.split("-")[1];
			}
			PersistenceHelper.manager.save(process);

			if (fileName != null) {

				metaMsg.setMsgId(msg_id);
				metaMsg.setOrderID(doc.getNumber());
				metaMsg.setOrderName(doc.getName());
				metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
				metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
				if("发放".equals(route)){
					metaMsg.setMsgType(Based.META_MSG_TYPE_DISTRIBUTE);
				}else{
					metaMsg.setMsgType(Based.META_MSG_TYPE_SIGNATURE);
				}
				String siteId = selectUnitValue.substring(selectUnitValue.indexOf("(") + 1, selectUnitValue.indexOf(")"));

				List sendSites = new ArrayList();
				sendSites.add(siteId);
				JSONObject dst = QuerySiteUtil.getDstSiteInfoForMetaMessage(sendSites);
				metaMsg.setdDstSiteInfo(dst.getString("META_DSTNAME"));
				JSONArray dstSiteArray = dst.getJSONArray("META_DSTIID");
				metaMsg.setdDstSites(dstSiteArray);
				System.out.println("流程监控信息 metaMsg@@@ = "+metaMsg);
				sender.addMetaMessage(metaMsg);

				JSONObject siteInfo = QuerySiteUtil.getDstSite(siteId);
				String dstSiteName = siteInfo.getString("name");

				logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_1+"【"+dstSiteName+"】【"+fileName+"】");
				sender.addLog(logMsg);
				JSONArray array = new JSONArray();
				if("发放".equals(route)){
					msg = new Win10DistributeReqDcMessage();
				}else{
					msg = new Win10SignReqDcMessage(array);
				}
				msg.put(Based.MSG_TYPE,Based.DC_REQUEST_DISTRIBUTE_DC);

				msg.put(Based.MSG_ID, msg_id);
				msg.put(Based.ID, msg_id);
				msg.put(Based.NAME, doc.getName());
				msg.put(Based.MSG_DESCRIPTION, "工艺跨域会签");
				msg.put(Based.MSG_CREATED_TIME, System.currentTimeMillis());
				JSONArray j_receivers = new JSONArray();
				if(selectUsersValue.contains(";")){
					String[] ss = selectUsersValue.split(";");
					String[] useriid = useriids.split(";");
					int i =0;
					if(useriid.length==ss.length){
						for(String s:ss){
							if("()".equals(s)){
								continue;
							}
							JSONObject j_receiver = new JSONObject();
							String userId =	 s.substring(0,s.indexOf("("));
							String userName = s.substring(s.indexOf("(")+1,s.indexOf(")"));

							j_receiver.put(Based.SITE_IID,siteInfo.get(Based.IID));
							j_receiver.put(Based.IID,useriid[i]);
							j_receiver.put(Based.ID,userId);
							j_receiver.put(Based.NAME,userName);
							j_receivers.put(j_receiver);
							i++;
						}
					}else{
						JSONObject j_receiver = new JSONObject();
						String userId =	 selectUsersValue.substring(0,selectUsersValue.indexOf("("));
						String userName = selectUsersValue.substring(selectUsersValue.indexOf("(")+1,selectUsersValue.indexOf(")"));
						j_receiver.put(Based.SITE_IID,siteInfo.get(Based.IID));
						j_receiver.put(Based.IID,useriids);
						j_receiver.put(Based.ID,userId);
						j_receiver.put(Based.NAME,userName);
						j_receivers.put(j_receiver);
					}

				}else{
					JSONObject j_receiver = new JSONObject();
					String userId =	 selectUsersValue.substring(0,selectUsersValue.indexOf("("));
					String userName = selectUsersValue.substring(selectUsersValue.indexOf("(")+1,selectUsersValue.indexOf(")"));
					j_receiver.put(Based.SITE_IID,siteInfo.get(Based.IID));
					j_receiver.put(Based.IID,useriids);
					j_receiver.put(Based.ID,userId);
					j_receiver.put(Based.NAME,userName);
					j_receivers.put(j_receiver);
				}

				msg.put(Based.JA_RECEIVERS, j_receivers);

				String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp" + File.separator
						+ fileName;
				String url_upload = ext.sast.center.synch.MQConstants.DC_UPLOAD;
				JSONObject j_file = FSUtil.upload(localPath, url_upload);
				msg.put(Based.J_FILE, j_file);

				/*JSONArray j_files = new JSONArray();
				j_file.put(Based.SITE_IID, siteInfo.get(Based.IID));
				j_files.put(j_file);
				msg.put("j_files", j_files);*/

				// 原始发起单位信息
				JSONObject j_src_site = new JSONObject();
				j_src_site.put(Based.IID, MQConstants.SITEIID_149);
				j_src_site.put(Based.ID, MQConstants.SITEID_149);
				j_src_site.put(Based.NAME, MQConstants.SITENAME_149);
				msg.put(Based.J_SRC_SITE, j_src_site);

				String productName = doc.getContainerName();
				String productIID = doc.getContainer().getPersistInfo().getObjectIdentifier().getId() + "";
				// 原始型号
				JSONObject j_product = new JSONObject();
				j_product.put(Based.IID, productIID);
				j_product.put(Based.ID, productName);
				j_product.put(Based.NAME, productName);
				msg.put(Based.J_PRODUCT, j_product);
				// 主型号
				JSONObject j_std_product = ProductConvertUtil.getSastProdcutInfo(productName);
				if(!j_std_product.has(Based.PRODUCT_IID)){
					logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,"型号映射错误，需重新进行型号映射！");
					logMsg.setException("需149管理员进行型号映射后重新发送！");
					sender.addLog(logMsg);
				}
				j_std_product.put(Based.IID, j_std_product.getString(Based.PRODUCT_IID));
				msg.put(Based.J_STD_PRODUCT, j_std_product);
				// 映射的型号
				JSONArray ja_std_products = new JSONArray();
				ja_std_products.put(j_std_product);
				msg.put("ja_std_products", ja_std_products);

				JSONObject j_creator = new JSONObject();
				WTUser creator = (WTUser) doc.getCreator().getPrincipal();
				j_creator.put(Based.IID, creator.getPersistInfo().getObjectIdentifier().getId() + "");
				j_creator.put(Based.ID, creator.getName());
				j_creator.put(Based.NAME, creator.getFullName());
				msg.put(Based.J_CREATOR, j_creator);

				JSONArray ja_dst_sites = new JSONArray();
				ja_dst_sites.put(siteInfo);
				msg.put(Based.JA_DST_SITES, ja_dst_sites);

				JSONArray ja_objects_request = new JSONArray();
				JSONObject object = new JSONObject();
				object.put(Based.OBJECT_OID, IxbHndHelper.getObjectIdImage(doc));
				object.put(Based.OBJECT_MASTER_IID,
						doc.getMaster().getPersistInfo().getObjectIdentifier().getId() + "");
				object.put(Based.OBJECT_ID, doc.getNumber());
				object.put(Based.OBJECT_NAME, doc.getName());
				object.put(Based.OBJECT_STATE, doc.getLifeCycleState().getDisplay(Locale.CHINA));
				object.put(Based.OBJECT_VERSION, doc.getVersionInfo().getIdentifier().getValue()+"."+doc.getIterationInfo().getIdentifier().getValue());
				object.put(Based.OBJECT_CLASSNAME,
						TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc).toString());
				object.put(Based.OBJECT_TYPE, Based.OBJECT_TYPE_DOC);
				ja_objects_request.put(object);
				msg.put(Based.JA_OBJECTS_REQUEST, ja_objects_request);

				msg.put("sendFrom", "149");

				/*JSONObject soap = new JSONObject();
				soap.put(Based.SITE_IID, siteInfo.get(Based.IID));
				JSONArray j_soapparams = new JSONArray();
				HashMap inputparams = new HashMap();
				inputparams.put("approvedType","commonProcess");
				inputparams.put("usersValue",selectUsersValue);
				inputparams.put("activityOidFB","已作废，只做代码兼容使用");
				inputparams.put("activityOidSF","已作废，只做代码兼容使用");
				String inputparamsStr = Deserialize.serializeMap(inputparams);
				soap.put("soapparams", inputparamsStr);
				j_soapparams.put(soap);
				msg.put("j_soapparams", j_soapparams);*/

				logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_3);
				sender.addLog(logMsg);

				sender.send(msg);

				JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"Send");

				//GWMQRecordService.updateStateByMsgId(msg_id,"数据发送成功");
			}else {
				logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_2);
				logMsg.setException("数据打包异常，请联系管理员重新发送");
				sender.addLog(logMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getLocalizedMessage()!=null&&e.getLocalizedMessage().contains("未初始化连接")){
				throw new WTException("发送消息工具-无法发送消息：未初始化连接");
			}
			metaMsg.setMsgId(msg_id);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			sender.addMetaMessage(metaMsg);

			logMsg = new LogMessage(msg_id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_4);
			logMsg.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(logMsg);

			GWMQRecordService.updateStateByMsgId(msg_id,"数据发送失败");
		}
		return result;
	}

	public static List<Persistable>  collectingData(List<Persistable> member) throws WTException {
		List<Persistable> retriveObject = new ArrayList<Persistable>();
		Set<Persistable> hasCollect = new HashSet<Persistable>();
		for(Persistable p:member){
			if(p instanceof EPMDocument){
				EPMDocument asm =(EPMDocument)p;

				navigateAsmStructure(asm,hasCollect,retriveObject);
			}
		}

		List<Persistable> resultList = new ArrayList<Persistable>();
		resultList.addAll(member);
		retriveObject.addAll(member);
		for(Object o :retriveObject){
			if(o instanceof  EPMDocument){
				EPMDocument reEpm = (EPMDocument) o;
				if(reEpm.getContainerName().startsWith("八院") || reEpm.getContainerName().contains("标准件库")) {
					continue;
				}
				if(!resultList.contains(reEpm)){
					resultList.add(reEpm);
				}
				QueryResult qr = PersistenceHelper.manager.navigate(reEpm,
						EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,
						true);
				while (qr.hasMoreElements()) {
					WTPart obj = (WTPart) qr.nextElement();
					if(!resultList.contains(obj)){
						resultList.add(obj);
					}
				}
			}
		}


		return resultList;
	}



	public static void navigateAsmStructure(EPMDocument asm, Set hasCollect,
											List retriveObject) throws WTException {
		// 如果当前装配已遍历过子件则略过
		if (!hasCollect.contains(asm)) {
			QueryResult qr = EPMStructureHelper.service
					.navigateUsesToIteration(asm, null, true,
							new LatestConfigSpec());
			// 把当前已遍历过的装配放到hasRetriveSet中
			hasCollect.add(asm);
			while (qr.hasMoreElements()) {
				EPMDocument epm = (EPMDocument) qr.nextElement();
				// 把当前装配子件添加到遍历得到的retriveObject中
				if(!retriveObject.contains(epm)){
					retriveObject.add(epm);
				}
				String epmdoctype = epm.getDocType().toString();
				// 如果子件是装配则递归遍历
				if (epmdoctype.equalsIgnoreCase("CADASSEMBLY")) {
					// 遍历非成套件下的子件
					navigateAsmStructure(epm, hasCollect, retriveObject);
				}
			}
		}
	}


	public static String  exportTargets(List<Persistable> resultMember) throws WTException {
		String fileNames = exportObjects(resultMember,true);
		return fileNames;
	}

	public static String exportObjects(List objects,boolean isCm)
			throws WTException {
		ExpImpLogger logs = ExpImpLogger.getInstance();
		if (objects.size() <= 0) {
			logs.log("==>Nothing to export.");
			return null;
		}
		logs.log("Begin to export objects to jar file...");

		File fileonserver = StandardIXBService.getSaveFileOnServer();
		System.out.println("这里是打包文件 ====》》》"+fileonserver);
		CmExportHandler exphnd = new CmExportHandler(fileonserver);
		String url = CmExportHandler.getLocalExchangeContainerURL();
		exphnd.writeManifest(url);
		Iterator it = objects.iterator();
		StringBuffer fileNames = new StringBuffer("");
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				if(part.getContainer().getName().startsWith("八院") || part.getContainerName().contains("标准件")) {
					continue;
				}
				CmExpImpWTPart cmExpImpWTPart = new CmExpImpWTPart(exphnd);
				cmExpImpWTPart.exportObject(part);

				if (judgeFileMoreOneGB(fileonserver)) {
					exphnd.finalizeJar();
					String fullfile = fileonserver.getName();
					fileNames = fileNames.append(fullfile).append(",");
					fileonserver =StandardIXBService.getSaveFileOnServer();
					exphnd = new CmExportHandler(fileonserver);
				}
			} else if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				if(doc.getContainer().getName().startsWith("八院") || doc.getContainerName().contains("标准件")) {
					continue;
				}
				CmExpImpWTDocument cmExpImpWTDocument = new CmExpImpWTDocument(exphnd);
				cmExpImpWTDocument.exportObject(doc);
				if (judgeFileMoreOneGB(fileonserver)) {
					exphnd.finalizeJar();
					String fullfile = fileonserver.getName();
					fileNames = fileNames.append(fullfile).append(",");
					fileonserver =StandardIXBService.getSaveFileOnServer();
					exphnd = new CmExportHandler(fileonserver);
				}
			} else if (obj instanceof ProcessEnvelope) {
				ProcessEnvelope pe = (ProcessEnvelope) obj;

				CmExpImpProcessEnvelope expimp = new CmExpImpProcessEnvelope(exphnd);
				expimp.setPackagedType(MQConstants.PACKAGED_TYPE_ALL);
				expimp.exportObject(pe);

			} else if (obj instanceof ChangePackaged) {
				ChangePackaged cp = (ChangePackaged) obj;
				CmExpImpChangePackaged expimp = new CmExpImpChangePackaged(exphnd);
				expimp.setPackagedType(MQConstants.PACKAGED_TYPE_ALL);
				expimp.exportObject(cp);
			}else if (obj instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) obj;
				if(epm.getContainer().getName().startsWith("八院") || epm.getContainerName().contains("标准件")) {
					continue;
				}
				CmExpImpEPMDocument cmExpImpEPMDocument = new CmExpImpEPMDocument(exphnd);
				cmExpImpEPMDocument.exportObject(epm);
				/* 通过通用、实例导出整个族表 */
				if (epm.isGeneric() || epm.isInstance()) {
					EPMFamily family = EPMFamily.getEPMFamily(epm);
					if (family != null) {
						CmExpImpEPMSepFamilyTable cmExpImpEPMSepFamilyTable = new CmExpImpEPMSepFamilyTable(exphnd);
						cmExpImpEPMSepFamilyTable.exportObject(family);
					}
					List members = family.getFamilyMembers();
					for (int i = 0; i < members.size(); i++) {
						EPMDocument member = (EPMDocument) members.get(i);
						CmExpImpEPMDocument CmExpImpEPMDocument = new CmExpImpEPMDocument(exphnd);
						CmExpImpEPMDocument.exportObject(member);
					}
				}

				if (judgeFileMoreOneGB(fileonserver)) {
					exphnd.finalizeJar();
					String fullfile = fileonserver.getName();
					fileNames = fileNames.append(fullfile).append(",");
					fileonserver =StandardIXBService.getSaveFileOnServer();
					exphnd = new CmExportHandler(fileonserver);
				}
			}  else if (obj instanceof ChangePackaged) {
				ChangePackaged order = (ChangePackaged) obj;
				new CmExpImpChangePackaged(exphnd).exportObject(order);
			} else {
				System.out.println((new StringBuilder()).append("==>Unsupported object for export:").append(obj.getClass().getName()).toString());
			}
		}
		exphnd.finalizeJar();


		String fullfile = fileonserver.getName();
		fileNames = fileNames.append(fullfile);

		System.out.println("打包结束 = "+fileNames);
		System.out.println((new StringBuilder()).append(objects.size() + " Object have ").append("Done Export to File:").append(fullfile).toString());
		return fileNames.toString();
	}
	private static boolean judgeFileMoreOneGB(File file) {
		boolean flag = false;
		long length = file.length();
		if(length>1024*1024*1024) {
			int size = (int)(length/1024.0/1024/1024);
			if(size == 1) {
				flag = true;
			}
		}
		return flag;
	}

	public static Collection<? extends Persistable> collectingChangePackaged(List<Persistable> member) throws WTException {
		List<Persistable> resultList = new ArrayList<Persistable>();
		for(Persistable m :member){
			if(m instanceof WTDocument ||m instanceof EPMDocument||m instanceof WTPart){
				ChangePackaged cp = ChangePackagedUtil.getChangeChangePackagedByAfterData(m);
				if(cp!=null&&!resultList.contains(cp)){
					resultList.add(cp);
				}
			}
		}
		return resultList;
	}
}
