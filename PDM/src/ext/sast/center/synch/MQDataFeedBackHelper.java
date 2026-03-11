/**
 *
 */
package ext.sast.center.synch;

import com.bjsasc.avidm.mq.fileserver.FSUtil;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.Message;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.message.win10.Win10SignTaskSynRespReceiverMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import com.bjsasc.avidm.mq.util.StackTraceUtil;
import com.ptc.extend.ixb.center.MQExpImpConstants;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.ases.part.ASESHuiqianSignature;
import ext.ases.part.SignLink;
import ext.casc.constants.Constants;
import ext.casc.ixb.IXBConstants;
import ext.casc.part.SignatureHelper;
import ext.casc.preview.Preview;
import ext.casc.synch.SoapCall;
import ext.casc.synch.StandardDataReceiveService;
import ext.casc.util.Deserialize;
import ext.casc.util.Tools;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureRecord;
import ext.casc.workflow.tree.GenerateJson;
import ext.sast.center.util.JsonConvertUtil;
import ext.sast.center.util.PropertiesUtil;
import org.apache.soap.SOAPException;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.*;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author cfire
 *
 */
@SuppressWarnings({ "deprecation", "unused","rawtypes" })
public class MQDataFeedBackHelper {
	private static int SAVE_TIME = 20000;//20秒
	public static String feedBack(ObjectReference self, WTObject pbo,
			String route, String fileName) throws WTException {
		Persistable object = self.getObject();
		WfProcess process = null;
		if(object instanceof WfActivity) {
			WfActivity activity = (WfActivity) object;
			process = activity.getParentProcess();
		} else if(object instanceof WfProcess) {
			process = (WfProcess) object;
		}
		ProcessData pData = process.getContext();
        String sendFrom = (String) pData.getValue("sendFrom");
        String mqmessage = (String) pData.getValue("mqmessage");
        if(mqmessage!=null&&!"".equals(mqmessage)){
            return feedBack(self,pbo,route,fileName,sendFrom,mqmessage);
        }else{
        	try {
				StandardDataReceiveService.processFeedback(self,pbo,route,fileName);
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return "";
	}

	public static String feedBack(ObjectReference self, WTObject pbo,
			String route, String fileName,String sendFrom,String mqmessage) throws WTException {
		processFeedback(self, pbo, route, fileName,mqmessage);
		return "";
	}





	/**
	 * 149各会签流程信息反馈入口
	 *
	 * @author liaojun
	 * @param self
	 *            活动引用
	 * @param pbo
	 *            流程主业务对象
	 * @param route
	 *            反馈的路由 值为 通过 或 驳回
	 * @param fileName
	 *            模型的注释集打包文集全路径
	 * @throws WTException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */
	public static void processFeedback(ObjectReference self, WTObject pbo,
			String route, String fileName,String mqmessage) throws WTException{
		boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
		if (pbo instanceof ProcessEnvelope) {
			ProcessEnvelope pe = (ProcessEnvelope) pbo;
			if(GenerateJson.isPreviewPkg(pe)){
				processFeedbackForPreview(self, pe, route, fileName,mqmessage);
			}else {
				processFeedbackForEnvelope(self, pe, route, fileName,mqmessage);
			}
		} else if (pbo instanceof ChangeRequest) {
		    ChangeRequest cr = (ChangeRequest) pbo;
			processFeedbackChangeRequest(self, cr, route, fileName,mqmessage);
		} else if (pbo instanceof ChangePackaged) {
            ChangePackaged changePackaged = (ChangePackaged) pbo;
            processFeedbackChangePackaged(self, changePackaged, route, fileName,mqmessage);
        }  else if (pbo instanceof WTDocument) {
			WTDocument document = (WTDocument) pbo;
			processFeedbackForWTdocument(self, document, route,mqmessage);
		}  else if (pbo instanceof Preview) {
			Preview preview = (Preview) pbo;
//			processFeedbackForPreview(self, preview, route, fileName,mqmessage);
		}else {
		}
		SessionServerHelper.manager.setAccessEnforced(enforced);
	}

	/**
	 * 149变更签审包会签流程信息反馈入口
	 *
	 * @author liaojun
	 * @param self
	 *            活动引用
	 * @param pbo
	 *            流程主业务对象 变更签审包
	 * @param route
	 *            反馈的路由 值为 通过 或 驳回
	 * @param fileName
	 *            模型的注释集打包文集全路径
	 * @param mqmessage
	 * @throws WTException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	private static void processFeedbackChangePackaged(ObjectReference self,
			ChangePackaged changePackaged, String route, String fileName, String mqmessage)
			throws WTException{
		Message msg = new Win10SignTaskSynRespReceiverMessage();
		Message msgfirst = new Win10SignTaskSynRespReceiverMessage();

		Sender sender = Sender.getInstance();

		JSONObject fromMsg = new JSONObject(mqmessage);
		String id = fromMsg.getString(Based.MSG_ID);

		LogMessage logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"变更会签信息采集中...");
		sender.addLog(logMsg);

		SignatureGYZZXMLParser parser =  new SignatureGYZZXMLParser((ContentHolder)changePackaged);
		HashMap inputparams = new HashMap();
		//SoapCall sopaCall = new SoapCall();
		WfActivity activity = (WfActivity) self.getObject();
		WfProcess process = activity.getParentProcess();
		String activityOid149 = self.getObjectId().toString();
		Map<String, Object> feedbacbMap = new HashMap<String, Object>();
		ProcessData pData = process.getContext();
		String activityOidGYS = (String) pData.getValue("activityOidGYS");
		String wfProcessOid = (String) pData.getValue("wfProcessOid");
		//805老流程
        String activityOid805 = (String) pData.getValue("activityOid805");
        String activityName = (String) pData.getValue("activityName");//工艺路线
        String activityTemplateID = (String) pData.getValue("activityTemplateID");
        JSONObject j_src_site = fromMsg.getJSONObject(Based.J_SRC_SITE);
		String sendFrom =j_src_site.getString("id");
        String pre_sendFrom = (String) inputparams.get("sendFrom");
		if(!Tools.isNull(pre_sendFrom)){
			sendFrom = pre_sendFrom;
		}
		try{
			String newSendFrom = fromMsg.getString("sendFrom");
			if(!Tools.isNull(newSendFrom)){
				sendFrom = newSendFrom;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
        String orderIID = (String) pData.getValue("orderIID");
        String feedback = "149";

		//判断是否为 带问题通过
		boolean isErrorAgree = checkIsErrorAgree(pData);

		ReferenceFactory rf = new ReferenceFactory();
		WTReference ativityGYS = rf.getReference(activityOidGYS);
		WfAssignedActivity tempActivity = (WfAssignedActivity) ativityGYS
				.getObject();
		String activityName149 = tempActivity.getName();
		String comments = null;
		WorkItem workItem = null;
		String userName = "";
		String zhurengongyishi ="";
		String ida3a4 = activityOidGYS.substring(
				activityOidGYS.indexOf(":") + 1, activityOidGYS.length());

		QuerySpec qs = new QuerySpec(WorkItem.class);
		String ida3a4XHS ="";
		String activityOidXHS = (String) pData.getValue("activityOidXHS");
		if(activityOidXHS!=null){
			ida3a4XHS = activityOidXHS.substring(
					activityOidXHS.indexOf(":") + 1, activityOidXHS.length());
		}
		if(ida3a4XHS!=null&&!"".equals(ida3a4XHS)){
			qs.appendOpenParen();
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					"=", Long.valueOf(ida3a4)), new int[1]);
			qs.appendOr();
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					"=", Long.valueOf(ida3a4XHS)), new int[1]);
			qs.appendCloseParen();
		}else{
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					"=", Long.valueOf(ida3a4)), new int[1]);
		}

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WorkItem.class, "status", "=",
				"COMPLETED"), new int[1]);
		ClassAttribute modifyStampA2 = new ClassAttribute(WorkItem.class,
				WorkItem.MODIFY_TIMESTAMP);
		qs.appendOrderBy(new OrderBy(modifyStampA2, true));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		int tempnum = 0;
		JSONArray ja_tasks_response = new JSONArray();
		JSONArray ja_tasks_response_frist = new JSONArray();
		JSONArray ja_signs_response = new JSONArray();
		while (qr.hasMoreElements()) {
			tempnum++;
			if(tempnum>2){
				break;
			}

			workItem = (WorkItem) qr.nextElement();
			WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
			String wfActName =  wfAct.getName();
			pData = workItem.getContext();
			if(Constants.ACTIVITYNAME_GYHQHZ.equals(wfActName)){

				if(comments!=null){
					comments = comments + " "+ pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}

				Ownership ownership = workItem.getOwnership();
				userName = ownership.getOwner().getFullName();
				String endTime = WTStandardDateFormat.format(
						workItem.getModifyTimestamp(), "yyyy-MM-dd");
				userName = userName.replace(", ", "");
				userName = userName.replace(",", "");
				// 电子签名用 主任工艺师
			    zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;
			}else if(Constants.ACTIVITYNAME_XHJSFZRHQ.equals(wfActName)){
				if(comments!=null){
					comments = comments + " "+ pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}
			}
			else if(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG.equals(wfActName)){
				if(comments!=null){
					comments = comments + " "+ pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}
				Ownership ownership = workItem.getOwnership();
				userName = ownership.getOwner().getFullName();
				String endTime = WTStandardDateFormat.format(
						workItem.getModifyTimestamp(), "yyyy-MM-dd");
				userName = userName.replace(", ", "");
				userName = userName.replace(",", "");
				// 电子签名用 主任工艺师
			    zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;
			}
			Ownership ownership = workItem.getOwnership();
			WTUser owner = (WTUser) ownership.getOwner().getPrincipal();
			JSONObject task = new JSONObject();
			task.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			task.put(Based.TASK_NAME, wfActName);
			task.put(Based.TASK_USER_IID, owner.getPersistInfo().getObjectIdentifier().getId()+"");
			task.put(Based.TASK_USER_ID, owner.getName());
			task.put(Based.TASK_USER_NAME, owner.getFullName());
			task.put(Based.TASK_STATE, "8");//必须可转为int类型，暂时不清楚神软需要传什么值，临时写死
			task.put(Based.TASK_PARENT_IID, -1);
			task.put(Based.TASK_CREATE_TIME, workItem.getCreateTimestamp().getTime());

			task.put(Based.TASK_FORWARDMODEL, "0");
			task.put(Based.TASK_ISTRANSMIT, "1");
			task.put(Based.TASK_ISWAITCHILD, "1");
			task.put(Based.TASK_ISLEADUP, "0");

			JSONObject task2 = new JSONObject();
			task2.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			task2.put(Based.TASK_NAME, wfActName);
			task2.put(Based.TASK_USER_IID, owner.getPersistInfo().getObjectIdentifier().getId()+"");
			task2.put(Based.TASK_USER_ID, owner.getName());
			task2.put(Based.TASK_USER_NAME, owner.getFullName());
			task2.put(Based.TASK_STATE, "0");//必须可转为int类型，暂时不清楚神软需要传什么值，临时写死
			task2.put(Based.TASK_PARENT_IID, -1);
			task2.put(Based.TASK_CREATE_TIME, workItem.getCreateTimestamp().getTime());

			task2.put(Based.TASK_FORWARDMODEL, "0");
			task2.put(Based.TASK_ISTRANSMIT, "1");
			task2.put(Based.TASK_ISWAITCHILD, "1");
			task2.put(Based.TASK_ISLEADUP, "0");
			ja_tasks_response_frist.put(task2);

			ja_tasks_response.put(task);

			JSONObject sign = new JSONObject();
			sign.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			sign.put(Based.SIGN_DIV_NAME, "");
			if("通过".equals(route) || "带问题通过".equals(route)){
				sign.put(Based.SIGN_IS_AGREE, "1");
			}else if("驳回".equals(route)){
				sign.put(Based.SIGN_IS_AGREE, "0");
			}
			sign.put(Based.SIGN_CONTENT, pData.getTaskComments());
			sign.put(Based.SIGN_USER_NAME, owner.getFullName().replace(", ", ""));

			sign.put(Based.SIGN_TYPE, "person");
			sign.put(Based.SIGN_TIME, workItem.getCreateTimestamp().getTime());
			sign.put(Based.SIGN_DIV_NAME, MQExpImpConstants.VALUE_DOMAINNAME);


			ja_signs_response.put(sign);
		}

		if(isErrorAgree){
			if(sendFrom.contains("805")){
				feedbacbMap.put("route", "带问题通过");
			}else{
				if (comments == null || comments.trim().equals("")) {
					comments = "带问题通过";
				}else{
					comments = "带问题通过:"+comments;
				}
				feedbacbMap.put("route", route);
			}
		}else{
			if (comments == null || comments.trim().equals("")) {
				comments = "无";
			}
			feedbacbMap.put("route", route);
		}

		feedbacbMap.put("comments", comments);
        feedbacbMap.put("activityOid", activityOid149);
        feedbacbMap.put("wfProcessOid", wfProcessOid);
        //805用
        feedbacbMap.put("activityOid805", activityOid805);
        feedbacbMap.put("activityOid149", activityOid149);

        feedbacbMap.put("approvedType", "ChangePackaged");//先不动此参数
        feedbacbMap.put("orderIID", orderIID);

        feedbacbMap.put("feedback", feedback);//返回单位
        feedbacbMap.put("activityTemplateID", activityTemplateID);
        feedbacbMap.put("activityName", activityName);
        feedbacbMap.put("feedbackType", "UNFORMAL");

		/*Ownership ownership = workItem.getOwnership();
		String userName = ownership.getOwner().getFullName();
		String endTime = WTStandardDateFormat.format(
				workItem.getModifyTimestamp(), "yyyy-MM-dd");
		userName = userName.replace(", ", "");
		userName = userName.replace(",", "");
		// 电子签名用 主任工艺师
		String zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;*/
		String feetbackStr = null;
		List<WTObject> memberList = new ArrayList<WTObject>();
		feedbacbMap.put("packagedNumber", changePackaged.getNumber());
		feedbacbMap.put("zhurengongyishi", userName);
		memberList.add(changePackaged);

		qr = PersistenceHelper.manager.navigate(changePackaged,
				ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
				ChangePackagedResultLink.class, true);
		while (qr.hasMoreElements()) {
			memberList.add((WTObject) qr.nextElement());

		}
		WfAssignedActivity zhipaiZuhang = getZhipaiGongyiZuZhangActivity(process);

		String numberStr = "";
		for (int i = 0; memberList != null && i < memberList.size(); i++) {
			WTObject wto = memberList.get(i);
			String signName = "";
			if (!(wto instanceof WTPart)) {
				QueryResult qr2 = PersistenceHelper.manager.navigate(wto,
						SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);// 取得对象所有审签信息
				List<ASESHuiqianSignature> list = new ArrayList<ASESHuiqianSignature>();
				while (qr2.hasMoreElements()) {// 遍历审签信息
					ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2
							.nextElement();

					//假设保存时间是30秒
					if(zhipaiZuhang!=null&&zhipaiZuhang.getModifyTimestamp().getTime()>(tempSign.getModifyTimestamp().getTime()+SAVE_TIME)){
						continue;
					}
					// 判断是否是此流程的
					String aOid = tempSign.getActivity();
					WfAssignedActivity sActivity  =  getWfActivity(aOid);
                    if(sActivity==null){
                    	continue;
                    }

					WfProcess sP = sActivity.getParentProcess();
					if (sP.equals(process)) {
						list.add(tempSign);
					}
				}

				// 指派工艺组长者会签记录返回805
				String signforzrgys = "";
				try {
					signforzrgys = getSignValue(list, Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);

				} catch (WTPropertyVetoException e) {

					e.printStackTrace();
				}

				// 用于电子签名,组织会签的时候才取 工艺员及其会签记录
				String signforgyy = "";
				String gongyiyuan = "";
				if (activityName149.equals("工艺会签汇总")) {
					try {
						signforgyy = getSignValue(list, "工艺会签");

						gongyiyuan = getSignatureValue(list, "工艺会签");
					} catch (WTPropertyVetoException e) {
						e.printStackTrace();
					}
				}

				if (signforzrgys.equals("")) {
					signforzrgys = signforgyy;
				} else {
					if (!signforgyy.equals("")) {
						signforzrgys = signforzrgys + ";" + signforgyy;
					}
				}
				gongyiyuan = sortSignatureData(gongyiyuan,wto,changePackaged,parser);
				if (!gongyiyuan.equals("")) {
					signName =zhurengongyishi+ ";" + gongyiyuan  ;
				}else{
					signName = zhurengongyishi;
				}
				// 会签信息及会签人员
				signforzrgys = signforzrgys + "pppqqq" + signName;
				if (wto instanceof WTDocument) {
					WTDocument temp = (WTDocument) wto;
					String number ="D"+ temp.getNumber();
					numberStr = numberStr + ";;;" + number;
					feedbacbMap.put(number, signforzrgys);

					System.out.println(number+"=signforzrgys:"+signforzrgys);

				} else if (wto instanceof EPMDocument) {
					EPMDocument temp = (EPMDocument) wto;
					String number ="E"+ temp.getNumber();
					numberStr = numberStr + ";;;" + number;
					feedbacbMap.put(number, signforzrgys);

					System.out.println(number+"=signforzrgys:"+signforzrgys);

				} else if (wto instanceof ChangePackaged) {
					ChangePackaged change = (ChangePackaged) wto;
					String number = "C"+change.getNumber();
					numberStr = numberStr + ";;;" + number;
					feedbacbMap.put(number, signforzrgys);

					System.out.println(number+"=signforzrgys:"+signforzrgys);

				}
			}
		}
		WTProperties prop=null;
		String hostName ="";
		try {
			prop = WTProperties.getLocalProperties();
			hostName = prop.getProperty("java.rmi.server.hostname");
		} catch (IOException e) {
			e.printStackTrace();
		}

		String url = "http://" + hostName + "/" + fileName;
		feedbacbMap.put("number", numberStr);

		feetbackStr = Deserialize.serializeMap(feedbacbMap);
		inputparams.put("URL", url);
		inputparams.put("pbonumber", changePackaged.getNumber());
		inputparams.put("pboname", changePackaged.getName());
		inputparams.put("result", feetbackStr);
		inputparams.put("sendFrom", "149");
		inputparams.put("zhurengongyishi", userName);
		//改为消息队列
		try {
			msg.put(Based.MSG_ID, fromMsg.getString(Based.MSG_ID));
			msg.put(Based.ID, fromMsg.getString(Based.ID));
			msg.put(Based.NAME, changePackaged.getName());
			msg.put(Based.MSG_DESCRIPTION,"变更会签反馈");

			msgfirst.put(Based.MSG_ID, fromMsg.getString(Based.MSG_ID));
			msgfirst.put(Based.ID, fromMsg.getString(Based.ID));
			msgfirst.put(Based.NAME, fromMsg.getString(Based.NAME));
			msgfirst.put(Based.MSG_DESCRIPTION,"任务创建成功");

			String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator + fileName;
			String upload_url = MQConstants.DC_UPLOAD;
			File file = new File(localPath);
			if(file != null && file.exists() && file.isFile()){
				JSONObject j_file = FSUtil.upload(localPath, upload_url);
				System.out.println("j_file == " + j_file);
				logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149 + "变更会签反馈数据包上传成功");
				sender.addLog(logMsg);
				msg.put(Based.J_FILE, j_file);
			}else{
				msg.put(Based.J_FILE, "");
			}


			msg.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());
			msgfirst.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());
			msg.put(Based.JA_TASKS_RESPONSE, ja_tasks_response);
			msgfirst.put(Based.JA_TASKS_RESPONSE, ja_tasks_response_frist);
			msg.put(Based.JA_SIGNS_RESPONSE, ja_signs_response);
			msg.put(Based.JA_SIGNS_RESPONSE, ja_signs_response);
			msg.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			msg.put(Based.TARGET_SITE_IID, MQConstants.SITEIID_149);
			JSONObject j_src_site_local = new JSONObject();
			j_src_site_local.put(Based.IID, MQConstants.SITEIID_149);
			j_src_site_local.put(Based.ID, MQConstants.SITEID_149);
			j_src_site_local.put(Based.NAME, MQConstants.SITENAME_149);
			msg.put(Based.J_SRC_SITE, j_src_site_local);

			msgfirst.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			msgfirst.put(Based.TARGET_SITE_IID, MQConstants.SITEIID_149);

			msg.put(Based.MSG_TYPE,Based.DC_RESPONSE_SIGN_TASKSYN_RECEIVER);
			msgfirst.put(Based.MSG_TYPE,Based.DC_RESPONSE_SIGN_TASKSYN_RECEIVER);

			String inputparamsStr = ext.sast.center.ixb.util.Deserialize.serializeMap(inputparams);
			msg.put("soapparams", inputparamsStr);
			System.out.println("msg @@@@ = "+msg);
			msgfirst.put("sendFrom", "149");
			if(!MQExpImpUtil.isCmPackage(sendFrom)){
				sender.send(msgfirst);
			}

			msg.put("sendFrom", "149");
			sender.send(msg);

			saveMqMsg(msg);


			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"发起变更会签反馈请求");
			sender.addLog(logMsg);
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getLocalizedMessage()!=null&&e.getLocalizedMessage().contains("未初始化连接")){
				throw new WTException("发送消息工具-无法发送消息：未初始化连接");
			}
			MetaMessage metaMsg = new MetaMessage();
			metaMsg.setMsgId(id);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			sender.addMetaMessage(metaMsg);

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"发起变更会签反馈请求失败");
			logMsg.setException(e.getMessage());
			sender.addLog(logMsg);
		}
		//ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
	}

	/**
     * 149变更签审包会签流程信息反馈入口
     *
     * @author liaojun
     * @param self
     *            活动引用
     * @param pbo
     *            流程主业务对象 变更签审包
     * @param route
     *            反馈的路由 值为 通过 或 驳回
     * @param fileName
     *            模型的注释集打包文集全路径
	 * @param mqmessage
     * @throws WTException
     * @throws IOException
     * @throws WTPropertyVetoException
     */
	@SuppressWarnings("unchecked")
	private static void processFeedbackChangeRequest(ObjectReference self,
            ChangeRequest request, String route, String fileName, String mqmessage)
            throws WTException{
		Message msg = new Win10SignTaskSynRespReceiverMessage();
		Message msgfirst = new Win10SignTaskSynRespReceiverMessage();
		Sender sender = Sender.getInstance();
		JSONObject fromMsg = new JSONObject(mqmessage);
		String id = fromMsg.getString(Based.MSG_ID);

		LogMessage logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"变更会签信息采集中...");
		sender.addLog(logMsg);

		SignatureGYZZXMLParser parser =  new SignatureGYZZXMLParser((ContentHolder)request);
        HashMap inputparams = new HashMap();
        SoapCall sopaCall = new SoapCall();
        WfActivity activity = (WfActivity) self.getObject();
        WfProcess process = activity.getParentProcess();
        String activityOid149 = self.getObjectId().toString();
        Map<String, Object> feedbacbMap = new HashMap<String, Object>();
        ProcessData pData = process.getContext();
        String activityOidGYS = (String) pData.getValue("activityOidGYS");
        String wfProcessOid = (String) pData.getValue("wfProcessOid");
        //805老流程
        String activityOid805 = (String) pData.getValue("activityOid805");
        String activityName = (String) pData.getValue("activityName");//工艺路线
        String activityTemplateID = (String) pData.getValue("activityTemplateID");
        JSONObject j_src_site = fromMsg.getJSONObject(Based.J_SRC_SITE);
        String sendFrom =j_src_site.getString("id");
        String pre_sendFrom = (String) inputparams.get("sendFrom");
		String j_src_site_id =j_src_site.getString("id");

		if(!Tools.isNull(pre_sendFrom)){
			sendFrom = pre_sendFrom;
		}
		try{
			String newSendFrom = fromMsg.getString("sendFrom");
			if(!Tools.isNull(newSendFrom)){
				sendFrom = newSendFrom;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
        String orderIID = (String) pData.getValue("orderIID");
        String feedback = "149";

		//判断是否为 带问题通过
		boolean isErrorAgree = checkIsErrorAgree(pData);

        ReferenceFactory rf = new ReferenceFactory();
        WTReference ativityGYS = rf.getReference(activityOidGYS);
        WfAssignedActivity tempActivity = (WfAssignedActivity) ativityGYS
                .getObject();
        String activityName149 = tempActivity.getName();
        String comments = null;
        WorkItem workItem = null;
        String userName = "";
		String zhurengongyishi ="";
        String ida3a4 = activityOidGYS.substring(
                activityOidGYS.indexOf(":") + 1, activityOidGYS.length());

        QuerySpec qs = new QuerySpec(WorkItem.class);
		String ida3a4XHS ="";
		String activityOidXHS = (String) pData.getValue("activityOidXHS");
		if(activityOidXHS!=null){
			ida3a4XHS = activityOidXHS.substring(
					activityOidXHS.indexOf(":") + 1, activityOidXHS.length());
		}
		if(ida3a4XHS!=null&&!"".equals(ida3a4XHS)){
			qs.appendOpenParen();
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					"=", Long.valueOf(ida3a4)), new int[1]);
			qs.appendOr();
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					"=", Long.valueOf(ida3a4XHS)), new int[1]);
			qs.appendCloseParen();
		}else{
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					"=", Long.valueOf(ida3a4)), new int[1]);
		}

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WorkItem.class, "status", "=",
				"COMPLETED"), new int[1]);
		ClassAttribute modifyStampA2 = new ClassAttribute(WorkItem.class,
				WorkItem.MODIFY_TIMESTAMP);
		qs.appendOrderBy(new OrderBy(modifyStampA2, true));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		int tempnum = 0;
		JSONArray ja_tasks_response = new JSONArray();
		JSONArray ja_tasks_response_first = new JSONArray();
		JSONArray ja_signs_response = new JSONArray();
		while (qr.hasMoreElements()) {
			tempnum++;
			if(tempnum>2){
				break;
			}
			workItem = (WorkItem) qr.nextElement();
			WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
			String wfActName =  wfAct.getName();
			pData = workItem.getContext();
			if(Constants.ACTIVITYNAME_GYHQHZ.equals(wfActName)){

				if(comments!=null){
					comments = comments+ " " + pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}
				Ownership ownership = workItem.getOwnership();
				userName = ownership.getOwner().getFullName();
				String endTime = WTStandardDateFormat.format(
						workItem.getModifyTimestamp(), "yyyy-MM-dd");
				userName = userName.replace(", ", "");
				userName = userName.replace(",", "");
				// 电子签名用 主任工艺师
			    zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;
			}else if(Constants.ACTIVITYNAME_XHJSFZRHQ.equals(wfActName)){
				if(comments!=null){
					comments = comments+ " " + pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}
			}
			else if(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG.equals(wfActName)){
				if(comments!=null){
					comments = comments+ " " + pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}
				Ownership ownership = workItem.getOwnership();
				userName = ownership.getOwner().getFullName();
				String endTime = WTStandardDateFormat.format(
						workItem.getModifyTimestamp(), "yyyy-MM-dd");
				userName = userName.replace(", ", "");
				userName = userName.replace(",", "");
				// 电子签名用 主任工艺师
			    zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;
			}
			Ownership ownership = workItem.getOwnership();
			WTUser owner = (WTUser) ownership.getOwner().getPrincipal();
			JSONObject task = new JSONObject();
			task.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			task.put(Based.TASK_NAME, wfActName);
			task.put(Based.TASK_USER_IID, owner.getPersistInfo().getObjectIdentifier().getId()+"");
			task.put(Based.TASK_USER_ID, owner.getName());
			task.put(Based.TASK_USER_NAME, owner.getFullName());
			task.put(Based.TASK_STATE, "8");//必须可转为int类型，暂时不清楚神软需要传什么值，临时写死
			task.put(Based.TASK_PARENT_IID, -1);
			task.put(Based.TASK_CREATE_TIME, workItem.getCreateTimestamp().getTime());

			task.put(Based.TASK_FORWARDMODEL, "0");
			task.put(Based.TASK_ISTRANSMIT, "1");
			task.put(Based.TASK_ISWAITCHILD, "1");
			task.put(Based.TASK_ISLEADUP, "0");
			ja_tasks_response.put(task);

			JSONObject task2 = new JSONObject();
			task2.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			task2.put(Based.TASK_NAME, wfActName);
			task2.put(Based.TASK_USER_IID, owner.getPersistInfo().getObjectIdentifier().getId()+"");
			task2.put(Based.TASK_USER_ID, owner.getName());
			task2.put(Based.TASK_USER_NAME, owner.getFullName());
			task2.put(Based.TASK_STATE, "0");//必须可转为int类型，暂时不清楚神软需要传什么值，临时写死
			task2.put(Based.TASK_PARENT_IID, -1);
			task2.put(Based.TASK_CREATE_TIME, workItem.getCreateTimestamp().getTime());

			task2.put(Based.TASK_FORWARDMODEL, "0");
			task2.put(Based.TASK_ISTRANSMIT, "1");
			task2.put(Based.TASK_ISWAITCHILD, "1");
			task2.put(Based.TASK_ISLEADUP, "0");
			ja_tasks_response_first.put(task2);

			//搜集 反馈专业所意见
			JSONObject sign = new JSONObject();
			sign.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			sign.put(Based.SIGN_DIV_NAME, "");
			if("通过".equals(route) || "带问题通过".equals(route)){
				sign.put(Based.SIGN_IS_AGREE, "1");
			}else if("驳回".equals(route)){
				sign.put(Based.SIGN_IS_AGREE, "0");
			}
			sign.put(Based.SIGN_CONTENT, pData.getTaskComments());
			sign.put(Based.SIGN_USER_NAME, owner.getFullName().replace(", ", ""));
			sign.put(Based.SIGN_TYPE, "person");
			sign.put(Based.SIGN_TIME, workItem.getCreateTimestamp().getTime());

			ja_signs_response.put(sign);
		}
		if(isErrorAgree){
			if(sendFrom.contains("805")){
				feedbacbMap.put("route", "带问题通过");
			}else{
				if (comments == null || comments.trim().equals("")) {
					comments = "带问题通过";
				}else{
					comments = "带问题通过:"+comments;
				}
				feedbacbMap.put("route", route);
			}
		}else{
			if (comments == null || comments.trim().equals("")) {
				comments = "无";
			}
			feedbacbMap.put("route", route);
		}

        feedbacbMap.put("comments", comments);
        feedbacbMap.put("activityOid", activityOid149);
        feedbacbMap.put("wfProcessOid", wfProcessOid);
        //805用
        feedbacbMap.put("activityOid805", activityOid805);
        feedbacbMap.put("activityOid149", activityOid149);

        feedbacbMap.put("approvedType", "ChangeRequest");//先不动此参数
        feedbacbMap.put("orderIID", orderIID);

        feedbacbMap.put("feedback", feedback);//返回单位
        feedbacbMap.put("activityTemplateID", activityTemplateID);
        feedbacbMap.put("activityName", activityName);
        feedbacbMap.put("feedbackType", "UNFORMAL");

       /* Ownership ownership = workItem.getOwnership();
        String userName = ownership.getOwner().getFullName();
        String endTime = WTStandardDateFormat.format(
                workItem.getModifyTimestamp(), "yyyy-MM-dd");
        userName = userName.replace(", ", "");
        userName = userName.replace(",", "");
        // 电子签名用 主任工艺师
        String zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;*/
        String feetbackStr = null;
        List<WTObject> memberList = new ArrayList<WTObject>();
        feedbacbMap.put("requestNumber", request.getNumber());
        feedbacbMap.put("zhurengongyishi", userName);
        memberList.add(request);
        String numberStr = "";
        WfAssignedActivity zhipaiZuhang = getZhipaiGongyiZuZhangActivity(process);
        for (int i = 0; memberList != null && i < memberList.size(); i++) {
            WTObject wto = memberList.get(i);
            String signName = "";
            if (!(wto instanceof WTPart)) {
                QueryResult qr2 = PersistenceHelper.manager.navigate(wto,
                        SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);// 取得对象所有审签信息
                List<ASESHuiqianSignature> list = new ArrayList<ASESHuiqianSignature>();
                while (qr2.hasMoreElements()) {// 遍历审签信息
                    ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2
                            .nextElement();
                    if(zhipaiZuhang!=null&&zhipaiZuhang.getModifyTimestamp().getTime()>(tempSign.getModifyTimestamp().getTime()+SAVE_TIME)){
						continue;
					}
                    // 判断是否是此流程的
                    String aOid = tempSign.getActivity();
                    WfAssignedActivity sActivity  =  getWfActivity(aOid);
                    if(sActivity==null){
                    	continue;
                    }

                    WfProcess sP = sActivity.getParentProcess();
                    if (sP.equals(process)) {
                        list.add(tempSign);
                    }
                }

                // 指派工艺组长者会签记录返回805
                String signforzrgys = "";
                try {
                	signforzrgys = getSignValue(list, Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);

                } catch (WTPropertyVetoException e) {
                    e.printStackTrace();
                }

                // 用于电子签名,组织会签的时候才取 工艺员及其会签记录
                String signforgyy = "";
                String gongyiyuan = "";
                if (activityName149.equals("工艺会签汇总")) {
                    try {
                        signforgyy = getSignValue(list, "工艺会签");

                        gongyiyuan = getSignatureValue(list, "工艺会签");
                    } catch (WTPropertyVetoException e) {
                        e.printStackTrace();
                    }
                }

                if (signforzrgys.equals("")) {
                    signforzrgys = signforgyy;
                } else {
                    if (!signforgyy.equals("")) {
                        signforzrgys = signforzrgys + ";" + signforgyy;
                    }
                }
                gongyiyuan = sortSignatureData(gongyiyuan,wto,request,parser);
                if (!gongyiyuan.equals("")) {
                    signName =zhurengongyishi  + ";" + gongyiyuan;
                }else{
                    signName = zhurengongyishi;
                }
                // 会签信息及会签人员
                signforzrgys = signforzrgys + "pppqqq" + signName;
                if (wto instanceof WTDocument) {
                    WTDocument temp = (WTDocument) wto;
                    String number = "D"+temp.getNumber();
                    numberStr = numberStr + ";;;" + number;
                    feedbacbMap.put(number, signforzrgys);

					System.out.println(number+"=signforzrgys:"+signforzrgys);

                } else if (wto instanceof EPMDocument) {
                    EPMDocument temp = (EPMDocument) wto;
                    String number = "E"+ temp.getNumber();
                    numberStr = numberStr + ";;;" + number;
                    feedbacbMap.put(number, signforzrgys);

					System.out.println(number+"=signforzrgys:"+signforzrgys);

                } else if (wto instanceof ChangeRequest) {
                    ChangeRequest changeRequest = (ChangeRequest) wto;
                    String number = "R"+ changeRequest.getNumber();
                    numberStr = numberStr + ";;;" + number;
                    feedbacbMap.put(number, signforzrgys);

					System.out.println(number+"=signforzrgys:"+signforzrgys);

                }
            }
        }
        WTProperties prop=null;
        String hostName ="";
        try {
            prop = WTProperties.getLocalProperties();
            hostName = prop.getProperty("java.rmi.server.hostname");
        } catch (IOException e) {

            e.printStackTrace();
        }

        String url = "http://" + hostName + "/" + fileName;
        feedbacbMap.put("number", numberStr);

        feetbackStr = Deserialize.serializeMap(feedbacbMap);
        inputparams.put("URL", url);
        inputparams.put("result", feetbackStr);
        inputparams.put("sendFrom", "149");
        inputparams.put("zhurengongyishi", userName);

        logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"变更会签信息采集完成");
		sender.addLog(logMsg);

        //改为消息队列
  		try {
  			msg.put(Based.MSG_ID, fromMsg.getString(Based.MSG_ID));
			msg.put(Based.ID, fromMsg.getString(Based.ID));

  			msg.put(Based.NAME, request.getName());
  			msg.put(Based.MSG_DESCRIPTION,"变更会签反馈");

  			msgfirst.put(Based.MSG_ID, fromMsg.getString(Based.MSG_ID));
  			msgfirst.put(Based.ID, fromMsg.getString(Based.ID));
  			msgfirst.put(Based.NAME, request.getName());
  			msgfirst.put(Based.MSG_DESCRIPTION,"任务创建成功");

  			String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator + fileName;
  			String upload_url = MQConstants.DC_UPLOAD;
  			JSONObject j_file = FSUtil.upload(localPath, upload_url);
  			System.out.println("j_file == "+j_file);

  			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"变更会签反馈数据包上传成功");
  			sender.addLog(logMsg);

  			msg.put(Based.J_FILE, j_file);
  			msg.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());
			msgfirst.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());

  			msg.put(Based.JA_TASKS_RESPONSE, ja_tasks_response);
			msgfirst.put(Based.JA_TASKS_RESPONSE, ja_tasks_response_first);

  			msg.put(Based.JA_SIGNS_RESPONSE, ja_signs_response);
  			msg.put(Based.TARGET_SITE_IID, MQConstants.SITEIID_149);
  			msg.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			msg.put(Based.MSG_TYPE,Based.DC_RESPONSE_SIGN_TASKSYN_RECEIVER);
			JSONObject j_src_site_local = new JSONObject();
			j_src_site_local.put(Based.IID, MQConstants.SITEIID_149);
			j_src_site_local.put(Based.ID, MQConstants.SITEID_149);
			j_src_site_local.put(Based.NAME, MQConstants.SITENAME_149);
			msg.put(Based.J_SRC_SITE, j_src_site_local);

			msgfirst.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			msgfirst.put(Based.TARGET_SITE_IID, MQConstants.SITEIID_149);
			msgfirst.put(Based.MSG_TYPE,Based.DC_RESPONSE_SIGN_RECEIVER);

			msgfirst.put("sendFrom", "149");
			if(!MQExpImpUtil.isCmPackage(sendFrom)){
				sender.send(msgfirst);
			}
  			String inputparamsStr = ext.sast.center.ixb.util.Deserialize.serializeMap(inputparams);
  			msg.put("soapparams", inputparamsStr);
			msg.put("sendFrom", "149");

  			sender.send(msg);

			saveMqMsg(msg);

  			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"发起变更会签反馈请求");
  			sender.addLog(logMsg);
  		} catch (Exception e) {
  			e.printStackTrace();
			if(e.getLocalizedMessage()!=null&&e.getLocalizedMessage().contains("未初始化连接")){
				throw new WTException("发送消息工具-无法发送消息：未初始化连接");
			}
  			MetaMessage metaMsg = new MetaMessage();
  			metaMsg.setMsgId(id);
  			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
  			sender.addMetaMessage(metaMsg);

  			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"发起变更会签反馈请求失败");
  			logMsg.setException(e.getMessage());
  			sender.addLog(logMsg);
  		}
        //ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
    }



	/**
	 * 149批量签审包会签流程信息反馈入口
	 *
	 * @author liaojun
	 * @param self
	 *            活动引用
	 * @param pbo
	 *            流程主业务对象 批量签审包
	 * @param route
	 *            反馈的路由 值为 通过 或 驳回
	 * @param fileName
	 *            模型的注释集打包文集全路径
	 * @param mqmessage
	 * @throws WTException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */
	private static void processFeedbackForEnvelope(ObjectReference self,
			ProcessEnvelope pe, String route, String fileName, String mqmessage)
			throws WTException{
		JSONObject fromMsg = new JSONObject(mqmessage);

		Sender sender = Sender.getInstance();
		Message msg = new Win10SignTaskSynRespReceiverMessage();
		Message msgfirst = new Win10SignTaskSynRespReceiverMessage();

		String id = fromMsg.getString(Based.MSG_ID);

		LogMessage logMsg = null;
//		new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"会签反馈信息采集中...");
//		sender.addLog(logMsg);

		SignatureGYZZXMLParser parser =  new SignatureGYZZXMLParser((ContentHolder)pe);
		HashMap<String,String > inputparams = new HashMap<String, String>();
		Map<String, Object> feedbacbMap = new HashMap<String, Object>();
		SoapCall sopaCall = new SoapCall();
		WfActivity activity = (WfActivity) self.getObject();
		WfProcess process = activity.getParentProcess();
		String activityOid149 = self.getObjectId().toString();
		ProcessData pData = process.getContext();

		//判断是否为 带问题通过
		boolean isErrorAgree = checkIsErrorAgree(pData);

		String activityOidGYS = (String) pData.getValue("activityOidGYS");

		String wfProcessOid = (String) pData.getValue("wfProcessOid");
		//805老流程
		String activityOid805 = (String) pData.getValue("activityOid805");

		String activityName = (String) pData.getValue("activityName");//工艺路线
		String activityTemplateID = (String) pData.getValue("activityTemplateID");

		JSONObject j_src_site = fromMsg.getJSONObject(Based.J_SRC_SITE);
		String sendFrom =j_src_site.getString("id");
        String pre_sendFrom = (String) inputparams.get("sendFrom");
		if(!Tools.isNull(pre_sendFrom)){
			sendFrom = pre_sendFrom;
		}
		try{
			String newSendFrom = fromMsg.getString("sendFrom");
			if(!Tools.isNull(newSendFrom)){
				sendFrom = newSendFrom;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

        JSONArray ja_tasks_response = new JSONArray();
        JSONArray ja_tasks_response_frist = new JSONArray();
        JSONArray ja_signs_response = new JSONArray();

        String orderIID = (String) pData.getValue("orderIID");
        String feedback = "149";
		ReferenceFactory rf = new ReferenceFactory();
		WTReference ativityGYS = rf.getReference(activityOidGYS);
		WfAssignedActivity tempActivity = (WfAssignedActivity) ativityGYS
				.getObject();
		String activityName149 = tempActivity.getName();
		WorkItem workItem = null;
		String userName = "";
		String zhurengongyishi ="";
		String comments = null;
		String ida3a4 = activityOidGYS.substring(
				activityOidGYS.indexOf(":") + 1, activityOidGYS.length());
		String ida3a4XHS ="";
		String activityOidXHS = (String) pData.getValue("activityOidXHS");
		if(activityOidXHS!=null){
			ida3a4XHS = activityOidXHS.substring(
					activityOidXHS.indexOf(":") + 1, activityOidXHS.length());
		}
		QuerySpec qs = new QuerySpec(WorkItem.class);
		if(ida3a4XHS!=null&&!"".equals(ida3a4XHS)){
			qs.appendOpenParen();
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					"=", Long.valueOf(ida3a4)), new int[1]);
			qs.appendOr();
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					"=", Long.valueOf(ida3a4XHS)), new int[1]);
			qs.appendCloseParen();
		}else{
			qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
					"=", Long.valueOf(ida3a4)), new int[1]);
		}

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WorkItem.class, "status", "=",
				"COMPLETED"), new int[1]);
		ClassAttribute modifyStampA2 = new ClassAttribute(WorkItem.class,
				WorkItem.MODIFY_TIMESTAMP);
		qs.appendOrderBy(new OrderBy(modifyStampA2, true));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		int tempnum = 0;

		while (qr.hasMoreElements()) {
			tempnum++;
			if(tempnum>2){
				break;
			}
			workItem = (WorkItem) qr.nextElement();
			WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
			String wfActName =  wfAct.getName();
			pData = workItem.getContext();
			Ownership ownership = workItem.getOwnership();
			if(Constants.ACTIVITYNAME_GYHQHZ.equals(wfActName)){

				if(comments!=null){
					comments = comments+ " " + pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}
				userName = ownership.getOwner().getFullName();
				String endTime = WTStandardDateFormat.format(
						workItem.getModifyTimestamp(), "yyyy-MM-dd");
				userName = userName.replace(", ", "");
				userName = userName.replace(",", "");
				// 电子签名用 主任工艺师
				zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;


			}else if(Constants.ACTIVITYNAME_XHJSFZRHQ.equals(wfActName)){
				if(comments!=null){
					comments = comments+ " " + pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}
			}
			else if(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG.equals(wfActName)){
				if(comments!=null){
					comments = comments + " "+pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}
				userName = ownership.getOwner().getFullName();
				String endTime = WTStandardDateFormat.format(
						workItem.getModifyTimestamp(), "yyyy-MM-dd");
				userName = userName.replace(", ", "");
				userName = userName.replace(",", "");
				// 电子签名用 主任工艺师
			    zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;

			}
			WTUser owner = (WTUser) ownership.getOwner().getPrincipal();
			JSONObject task = new JSONObject();
			JSONObject task2 = new JSONObject();
			task.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			task.put(Based.TASK_NAME, wfActName);
			task.put(Based.TASK_USER_IID, owner.getPersistInfo().getObjectIdentifier().getId()+"");
			task.put(Based.TASK_USER_ID, owner.getName());
			task.put(Based.TASK_USER_NAME, owner.getFullName());
			task.put(Based.TASK_STATE, "8");//必须可转为int类型，暂时不清楚神软需要传什么值，临时写死
			task.put(Based.TASK_PARENT_IID, -1);
			task.put(Based.TASK_CREATE_TIME, workItem.getCreateTimestamp().getTime());

			task.put(Based.TASK_FORWARDMODEL, "0");
			task.put(Based.TASK_ISTRANSMIT, "1");
			task.put(Based.TASK_ISWAITCHILD, "1");
			task.put(Based.TASK_ISLEADUP, "0");

			task2.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			task2.put(Based.TASK_NAME, wfActName);
			task2.put(Based.TASK_USER_IID, owner.getPersistInfo().getObjectIdentifier().getId()+"");
			task2.put(Based.TASK_USER_ID, owner.getName());
			task2.put(Based.TASK_USER_NAME, owner.getFullName());
			task2.put(Based.TASK_STATE, "0");//必须可转为int类型，暂时不清楚神软需要传什么值，临时写死
			task2.put(Based.TASK_PARENT_IID, -1);
			task2.put(Based.TASK_CREATE_TIME, workItem.getCreateTimestamp().getTime());

			task2.put(Based.TASK_FORWARDMODEL, "0");
			task2.put(Based.TASK_ISTRANSMIT, "1");
			task2.put(Based.TASK_ISWAITCHILD, "1");
			task2.put(Based.TASK_ISLEADUP, "0");

			ja_tasks_response.put(task);
			ja_tasks_response_frist.put(task2);

			//搜集 反馈专业所意见
			JSONObject sign = new JSONObject();
			sign.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			sign.put(Based.SIGN_DIV_NAME, "");
			if("通过".equals(route) || "带问题通过".equals(route)){
				sign.put(Based.SIGN_IS_AGREE, "1");
			}else if("驳回".equals(route)){
				sign.put(Based.SIGN_IS_AGREE, "0");
			}
			sign.put(Based.SIGN_CONTENT, pData.getTaskComments());
			sign.put(Based.SIGN_USER_NAME, owner.getFullName().replace(", ", ""));
			sign.put(Based.SIGN_TYPE, "person");
			sign.put(Based.SIGN_TIME, workItem.getCreateTimestamp().getTime());

			sign.put(Based.SIGN_DIV_NAME, MQExpImpConstants.VALUE_DOMAINNAME);

			ja_signs_response.put(sign);
		}
		if(isErrorAgree){
			if(sendFrom.contains("805")){
				feedbacbMap.put("route", "带问题通过");
			}else{
				if (comments == null || comments.trim().equals("")) {
					comments = "带问题通过";
				}else{
					comments = "带问题通过:"+comments;
				}
				feedbacbMap.put("route", route);
			}
		}else{
			if (comments == null || comments.trim().equals("")) {
				comments = "无";
			}
			feedbacbMap.put("route", route);
		}

		feedbacbMap.put("comments", comments);
		feedbacbMap.put("activityOid", activityOid149);
		feedbacbMap.put("wfProcessOid", wfProcessOid);
		//805用
		feedbacbMap.put("activityOid805", activityOid805);
		feedbacbMap.put("activityOid149", activityOid149);


		feedbacbMap.put("approvedType", "ProcessEnvelope");//先不动此参数
		feedbacbMap.put("orderIID", orderIID);

        feedbacbMap.put("feedback", feedback);//返回单位
        feedbacbMap.put("activityTemplateID", activityTemplateID);
        feedbacbMap.put("activityName", activityName);
        feedbacbMap.put("feedbackType", "UNFORMAL");


		String feetbackStr = null;
		List memberList = null;
		feedbacbMap.put("envelopeNumber", pe.getNumber());
		feedbacbMap.put("zhurengongyishi", userName);
		memberList = ProcessEnvelopeUtil.getAllMembers(pe);
		String numberStr = "";
		WfAssignedActivity zhipaiZuhang = getZhipaiGongyiZuZhangActivity(process);

		for (int i = 0; memberList != null && i < memberList.size(); i++) {
			WTObject wto = (WTObject)memberList.get(i);
			String signName = "";
			if (SignatureHelper.isShowSignature(wto)) {
				QueryResult qr2 = PersistenceHelper.manager.navigate(wto,
						SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);// 取得对象所有审签信息
				List<ASESHuiqianSignature> list = new ArrayList<ASESHuiqianSignature>();
				while (qr2.hasMoreElements()) {// 遍历审签信息
					ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2
							.nextElement();
					//先修改意见，提前30秒把刚完成的指派组长意见放入有效意见
					if(zhipaiZuhang!=null&&zhipaiZuhang.getModifyTimestamp().getTime()>(tempSign.getModifyTimestamp().getTime()+SAVE_TIME)){
						continue;
					}
					// 判断是否是此流程的
					String aOid = tempSign.getActivity();

					WfAssignedActivity sActivity  =  getWfActivity(aOid);
                    if(sActivity==null){
                    	continue;
                    }

					WfProcess sP = sActivity.getParentProcess();
					if (sP.equals(process)) {
						list.add(tempSign);
					}
				}

				// 指派工艺组长者会签记录返回805
				String signforzrgys = "";
				try {
					signforzrgys = getSignValue(list, Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);


				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}

				String signforgyy = "";
				// 用于电子签名,组织会签的时候才取 工艺员及其会签记录
				String gongyiyuan = "";
				if (activityName149.equals("工艺会签汇总")) {
					try {
						signforgyy = getSignValue(list, "工艺会签");

						gongyiyuan = getSignatureValue(list, "工艺会签");
					} catch (WTPropertyVetoException e) {
						e.printStackTrace();
					}
				}

				if (signforzrgys.equals("")) {
					signforzrgys = signforgyy;
				} else {
					if (!signforgyy.equals("")) {
						signforzrgys = signforzrgys + ";" + signforgyy;
					}
				}

				gongyiyuan = sortSignatureData(gongyiyuan,wto,pe,parser);
				if (!gongyiyuan.equals("")) {
					signName = zhurengongyishi + ";" +gongyiyuan ;
				}else{
					 signName =zhurengongyishi;
				}

				// 会签信息及会签人员
				signforzrgys = signforzrgys + "pppqqq" + signName;

				if (wto instanceof WTDocument) {
					WTDocument temp = (WTDocument) wto;
					String number = "D"+ temp.getNumber();
					numberStr = numberStr + ";;;" + number;
					feedbacbMap.put(number, signforzrgys);

					System.out.println(number+"=signforzrgys:"+signforzrgys);

				} else if (wto instanceof EPMDocument) {
					EPMDocument temp = (EPMDocument) wto;
					String number = "E"+ temp.getNumber();
					numberStr = numberStr + ";;;" + number;
					feedbacbMap.put(number, signforzrgys);

					System.out.println(number+"=signforzrgys:"+signforzrgys);

				} else if(wto instanceof WTPart && SignatureHelper.isShowSignature(wto)) {
					WTPart part = (WTPart) wto;
					String number = "P"+ part.getNumber();
					numberStr = numberStr + ";;;" + number;
					feedbacbMap.put(number, signforzrgys);

					System.out.println(number+"=signforzrgys:"+signforzrgys);

				}
			}
		}

//		logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"会签反馈信息采集完成");
//		sender.addLog(logMsg);
		try {
			feedbacbMap.put("number", numberStr);
			feetbackStr = Deserialize.serializeMap(feedbacbMap);
			//inputparams.put("URL", url);
			inputparams.put("pbonumber", pe.getNumber());
			inputparams.put("pboname", pe.getName());
			inputparams.put("result", feetbackStr);
			inputparams.put("sendFrom", "149");
			inputparams.put("zhurengongyishi", userName);

			//序列化inputparams 调用MQ消息队列发送处理
			msg.put(Based.MSG_ID, fromMsg.getString(Based.MSG_ID));
			msg.put(Based.ID, fromMsg.getString(Based.ID));
			msg.put(Based.NAME, fromMsg.getString(Based.NAME));
			msg.put(Based.MSG_DESCRIPTION,"会签反馈");

			msgfirst.put(Based.MSG_ID, fromMsg.getString(Based.MSG_ID));
			msgfirst.put(Based.ID, fromMsg.getString(Based.ID));
			msgfirst.put(Based.NAME, fromMsg.getString(Based.NAME));
			msgfirst.put(Based.MSG_DESCRIPTION,"任务创建成功");

			String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator + fileName;
			String url = MQConstants.DC_UPLOAD;
			JSONObject j_file = FSUtil.upload(localPath, url);
			System.out.println("j_file == "+j_file);
//			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"会签反馈数据包上传成功");
//			sender.addLog(logMsg);
			msg.put(Based.J_FILE, j_file);

			msg.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());
			msgfirst.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());
			msg.put(Based.JA_TASKS_RESPONSE, ja_tasks_response);
			msgfirst.put(Based.JA_TASKS_RESPONSE, ja_tasks_response_frist);

			msg.put(Based.JA_SIGNS_RESPONSE, ja_signs_response);
			msg.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			msg.put(Based.TARGET_SITE_IID, MQConstants.SITEIID_149);

			JSONObject j_src_site_local = new JSONObject();
			j_src_site_local.put(Based.IID, MQConstants.SITEIID_149);
			j_src_site_local.put(Based.ID, MQConstants.SITEID_149);
			j_src_site_local.put(Based.NAME, MQConstants.SITENAME_149);
			msg.put(Based.J_SRC_SITE, j_src_site_local);

			msgfirst.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			msgfirst.put(Based.TARGET_SITE_IID, MQConstants.SITEIID_149);

			msg.put(Based.MSG_TYPE,Based.DC_RESPONSE_SIGN_TASKSYN_RECEIVER);

			msgfirst.put(Based.MSG_TYPE,Based.DC_RESPONSE_SIGN_RECEIVER);

			msg.put("target_site_iid", MQConstants.SITEIID_149);
			msgfirst.put("target_site_iid", MQConstants.SITEIID_149);
			String inputparamsStr = ext.sast.center.ixb.util.Deserialize.serializeMap(inputparams);
			msg.put("soapparams", inputparamsStr);
			msgfirst.put("sendFrom", "149");
			if(!MQExpImpUtil.isCmPackage(sendFrom)){
				sender.send(msgfirst);
			}
			msg.put("sendFrom", "149");

			saveMqMsg(msg);
			sender.send(msg);

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_11);
			sender.addLog(logMsg);
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getLocalizedMessage()!=null&&e.getLocalizedMessage().contains("未初始化连接")){
				throw new WTException("发送消息工具-无法发送消息：未初始化连接");
			}
			MetaMessage metaMsg = new MetaMessage();
			metaMsg.setMsgId(id);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			sender.updateMetaMessage(metaMsg);

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.STATUS_12);
			logMsg.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(logMsg);
		}
	}




	/**
	 * @param msg
	 */
	private static void saveMqMsg(Message msg) {
		JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+JsonConvertUtil.FeedBack);
	}

	/**
	 * @param process
	 * @return
	 */
	private static WfAssignedActivity getZhipaiGongyiZuZhangActivity(WfProcess process) {
		Enumeration enumeration;
		try {
			enumeration = WfEngineHelper.service.getProcessSteps(process, null);
			 while (enumeration.hasMoreElements()) {
		            WfActivity wfactivity = (WfActivity) enumeration.nextElement();
		            if (wfactivity instanceof WfAssignedActivity) {
		                WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
		                if(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG.equals(wfassignedactivity.getName())){
		                	return wfassignedactivity;

		                }
		            }
		        }
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return null;
	}

	public static void noticeZongTiSuo(ObjectReference self)  {
		Persistable p = self.getObject();
		WfProcess process = null;
		if (p instanceof WfActivity) {
			WfActivity activity = (WfActivity) self.getObject();
			try {
				process = activity.getParentProcess();
			} catch (WTException e) {

				e.printStackTrace();
			}
		} else {
			process = (WfProcess) p;
		}
		ProcessData pData = process.getContext();
		String wfProcessOid = (String) pData.getValue("wfProcessOid");

		HashMap<String, Object> inputparams = new HashMap<String, Object>();
	    SoapCall sopaCall = new SoapCall();
	    String sendFrom = (String) pData.getValue("sendFrom");
        if(sendFrom ==null){
            sendFrom = "805";
        }
		inputparams.put("sendFrom", sendFrom);
		inputparams.put("feedback", "149");
		inputparams.put("importfail", "true");
		inputparams.put("wfProcessOid", wfProcessOid);
		try {
			ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private static String sortSignatureData(String gongyiyuan, WTObject wto,
			Object pe, SignatureGYZZXMLParser parser) {
		String result = gongyiyuan;
		String zzcjOid = getZZCJ(pe,wto,parser);
		String others = "";
		String zzcjp = "";
		if(zzcjOid==null||"".equals(zzcjOid)){
			return gongyiyuan;
		}
		if(gongyiyuan!=null &&!"".equals(gongyiyuan)&&gongyiyuan.contains(";")){
			ReferenceFactory rf = new ReferenceFactory();
			try {
				WTUser user = (WTUser) rf.getReference(zzcjOid).getObject();
				String[] ss = gongyiyuan.split(";");
				for(String person : ss){
					if(!"".equals(person)){
						if(person.contains(user.getFullName())){
							zzcjp = person;
						}else{
							others = others + person+";";
						}
					}

				}
				result = zzcjp;
				if(!"".equals(others)){
					result = result + ";"+others;
				}
				if(result.startsWith(";")){
					result = result.substring(1);
				}
				if(result.endsWith(";")){
					result = result.substring(0,result.length()-1);
				}
			} catch (WTRuntimeException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			}

		}

		return result;

	}
	private static String getZZCJ( Object pbo,Object object, SignatureGYZZXMLParser parser){
    	String zzcj = null;
    	String zzcjPerson = null;
     	String oid = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
     	SignatureRecord record = parser.getMap3().get(oid);
     	if(record!=null)
     		zzcj = record.getZhuzhichejian();
     	if(zzcj!=null&&!"".equals(zzcj)){
     		String[] zzs = zzcj.split("-");
     		zzcj = zzs[0];
     		zzcjPerson = zzs[1];
     		if(zzcjPerson!=null&&zzcjPerson.endsWith(";")){
     			zzcjPerson = zzcjPerson.substring(0,zzcjPerson.lastIndexOf(";"));
     		}
     	}

     	return zzcjPerson;
    }

	/**
	 * 149文档会签流程信息反馈入口
	 *
	 * @author liaojun
	 * @param self
	 *            活动引用
	 * @param pbo
	 *            流程主业务对象 文档
	 * @param route
	 *            反馈的路由 值为 通过 或 驳回
	 * @param mqmessage
	 * @throws WTException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */

	private static void processFeedbackForWTdocument(ObjectReference self,
			WTDocument document, String route, String mqmessage) throws WTException {
		SignatureGYZZXMLParser parser =  new SignatureGYZZXMLParser((ContentHolder)document);
		HashMap<String, String> inputparams = new HashMap<String, String>();
		SoapCall sopaCall = new SoapCall();
		WfActivity activity = (WfActivity) self.getObject();
		WfProcess process = activity.getParentProcess();
		String activityOid149 = self.getObjectId().toString();
		Map<String, Object> feedbacbMap = new HashMap<String, Object>();
		ProcessData pData = process.getContext();
		String activityOidGYS = (String) pData.getValue("activityOidGYS");
		String activityOid805 = (String) pData.getValue("activityOid805");
		feedbacbMap.put("activityOid149", activityOid149);
		feedbacbMap.put("activityOid805", activityOid805);
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem workItem = null;
		String comments = null;
		String ida3a4 = activityOidGYS.substring(
				activityOidGYS.indexOf(":") + 1, activityOidGYS.length());
		QuerySpec qs = new QuerySpec(WorkItem.class);
		qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
				"=", Long.valueOf(ida3a4)), new int[1]);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WorkItem.class, "status", "=",
				"COMPLETED"), new int[1]);
		ClassAttribute modifyStampA2 = new ClassAttribute(WorkItem.class,
				WorkItem.MODIFY_TIMESTAMP);
		qs.appendOrderBy(new OrderBy(modifyStampA2, true));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			workItem = (WorkItem) qr.nextElement();
			WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
			String wfActName =  wfAct.getName();
			if(Constants.ACTIVITYNAME_GYHQHZ.equals(wfActName)){
				pData = workItem.getContext();
				if(comments!=null){
					comments = comments + pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}

			}else if(Constants.ACTIVITYNAME_XHJSFZRHQ.equals(wfActName)){
				if(comments!=null){
					comments = comments + pData.getTaskComments();
				}else{
					comments = pData.getTaskComments();
				}
			}
		}
		feedbacbMap.put("comments", comments);
		feedbacbMap.put("activityOid149 ", activityOid149);
		feedbacbMap.put("activityOid805", activityOid805);
		feedbacbMap.put("approvedType", "WTDocument");
		feedbacbMap.put("route", route);
		Ownership ownership = workItem.getOwnership();
		String userName = ownership.getOwner().getFullName();
		String endTime = WTStandardDateFormat.format(
				workItem.getModifyTimestamp(), "yyyy-MM-dd");
		userName = userName.replace(",", "");
		userName = userName.replace(" ", "");
		// 电子签名用 主任工艺师
		String zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;
		feedbacbMap.put("zhurengongyishi", userName);
		QueryResult qr2 = PersistenceHelper.manager.navigate(document,
				SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);// 取得对象所有审签信息
		List<ASESHuiqianSignature> list = new ArrayList<ASESHuiqianSignature>();
		while (qr2.hasMoreElements()) {// 遍历审签信息
			ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2
					.nextElement();
			// 判断是否是此流程的
			String aOid = tempSign.getActivity();

			WfAssignedActivity sActivity  =  getWfActivity(aOid);
            if(sActivity==null){
            	continue;
            }

			WfProcess sP = sActivity.getParentProcess();
			if (sP.equals(process)) {
				list.add(tempSign);
			}
		}
		WTReference ativityGYS = rf.getReference(activityOidGYS);
		WfAssignedActivity tempActivity = (WfAssignedActivity) ativityGYS
				.getObject();
		String activityName = tempActivity.getName();

		// 指派工艺组长者会签记录返回805
		String signforzrgys = "";
		try {
			signforzrgys = getSignValue(list, Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);


		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}

		// 指派工艺组长者会签记录返回805 组织会签的时候才取
		String signforgyy = "";

		// 用于电子签名,组织会签的时候才取 工艺员
		String gongyiyuan = "";
		if (activityName.equals("工艺会签汇总")) {
			try {
				signforgyy = getSignValue(list, "工艺会签");

				gongyiyuan = getSignatureValue(list, "工艺会签");
			} catch (WTPropertyVetoException e) {

				e.printStackTrace();
			}
		}

		if (signforzrgys.equals("")) {
			signforzrgys = signforgyy;
		} else {
			if (!signforgyy.equals("")) {
				signforzrgys = signforzrgys + ";" + signforgyy;
			}
		}
		gongyiyuan = sortSignatureData(gongyiyuan,document,document,parser);
		if (!gongyiyuan.equals("")) {
			zhurengongyishi = zhurengongyishi + ";" +gongyiyuan ;
		}
		// 会签信息及会签人员
		signforzrgys = signforzrgys + "pppqqq" + zhurengongyishi;
		System.out.printf("会签信息及会签人员signforzrgys:"+signforzrgys);
		String number = document.getNumber();
		feedbacbMap.put(number, signforzrgys);
		feedbacbMap.put("number", number);
		feedbacbMap.put("feedbackType", "UNFORMAL");
		String feetbackStr = Deserialize.serializeMap(feedbacbMap);
		inputparams.put("result", feetbackStr);
		ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
	}

	/**
	 * 正式发放反馈结果至805
	 *
	 * @param self
	 *            活动引用
	 * @param route
	 *            反馈路由
	 * @throws WTException
	 */
	public static void formalDataFeedback(ObjectReference self, String route)
			throws WTException {
		HashMap<String, String> inputparams = new HashMap<String, String>();
		SoapCall sopaCall = new SoapCall();
		Persistable p = self.getObject();
		WfProcess process = null;
		if (p instanceof WfActivity) {
			WfActivity activity = (WfActivity) self.getObject();
			process = activity.getParentProcess();
		} else {
			process = (WfProcess) p;
		}
		Map<String, Object> feedbacbMap = new HashMap<String, Object>();
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		WTUser user = (WTUser) principal;
		String userName = user.getFullName();
		userName = userName.replaceAll(",", "");
		userName = userName.replaceAll(" ", "");
		ProcessData pData = process.getContext();
		String activityOid805 = (String) pData.getValue("activityOid805");
		String wfProcessOid = (String) pData.getValue("wfProcessOid");
		String activityTemplateID = (String) pData.getValue("activityTemplateID");
		String orderIID = (String) pData.getValue("orderIID");
		String activityName = (String) pData.getValue("activityName");
		String sendFrom = (String) pData.getValue("sendFrom");
        if(sendFrom==null){
            sendFrom="805";
        }
        String feedback = "149";

		feedbacbMap.put("activityOid805", activityOid805);

        feedbacbMap.put("wfProcessOid", wfProcessOid);


        feedbacbMap.put("route", route);
        feedbacbMap.put("orderIID", orderIID);

        feedbacbMap.put("feedback", feedback);//返回单位
        feedbacbMap.put("activityTemplateID", activityTemplateID);
        feedbacbMap.put("activityName", activityName);
        feedbacbMap.put("feedbackType", "UNFORMAL");


		feedbacbMap.put("route", route);
		feedbacbMap.put("feedbackType", "FORMAL");
		feedbacbMap.put("comments", "数据接收成功");
		feedbacbMap.put("acceptUser", userName);
		feedbacbMap.put("acceptDate", System.currentTimeMillis());
		String feetbackStr = Deserialize.serializeMap(feedbacbMap);
		inputparams.put("result", feetbackStr);
		inputparams.put("sendFrom", sendFrom);
		ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
	}

	/**
	 * 取149会签信息返回到805所
	 *
	 * @author liaojun
	 * @param tempSignList
	 *            传入对象的所有ASESHuiqianSignature记录
	 * @param activityName
	 *            返回签审记录的活动名称
	 * @return 返回特定对象活动名称为activityName的签审信息
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static String getSignValue(List<ASESHuiqianSignature> tempSignList,
			String activityName) throws WTException, WTPropertyVetoException {
		String conclusion = "";
		ReferenceFactory rf = new ReferenceFactory();
		Hashtable<String, ASESHuiqianSignature> ht = new Hashtable<String, ASESHuiqianSignature>();
		for (int i = 0; i < tempSignList.size(); i++) {
			ASESHuiqianSignature tempSign = tempSignList.get(i);
			String tempActOid = tempSign.getActivity();
			WfActivity wfAct = (WfActivity) rf.getReference(tempActOid)
					.getObject();
			if (wfAct.getName().equalsIgnoreCase(activityName)||(activityName.equalsIgnoreCase(Constants.ACTIVITYNAME_GYHQ)&&wfAct.getName().equalsIgnoreCase(MQConstants.ACTIVITY_NAME_KRS))) {
				String conclution = tempSign.getConclusion();
				if (conclution == null) {
					continue;
				}
				String userName = "";
				if (conclution.contains("同意") && !conclution.contains("不同意")) {
					userName = conclution.substring(0, conclution.length() - 2);
				} else if (conclution.contains("不同意")) {
					userName = conclution.substring(0, conclution.length() - 3);
				}

				userName = userName.replace(", ", "");
				userName =userName.replace(",", "");
				ASESHuiqianSignature tempSign1 = (ASESHuiqianSignature) ht
						.get(userName);
				if (tempSign1 != null) {
					if (tempSign.getCreateTimestamp().after(
							tempSign1.getCreateTimestamp())) {
						ht.put(userName, tempSign);
					}
				} else {
					ht.put(userName, tempSign);
				}
			}
		}
		Enumeration<String> enum1 = ht.keys();
		while (enum1.hasMoreElements()) {
			Object obj1 = enum1.nextElement();
			ASESHuiqianSignature tempSign = (ASESHuiqianSignature) ht.get(obj1);
			String temp = tempSign.getConclusion();
			temp = temp.replaceAll(", ", "");
			temp = temp.replaceAll(",", "");
			String opinion = tempSign.getOpinion();
			if (opinion == null) {
				opinion = "";
			}
			if (!opinion.trim().equals("")) {
				temp = temp + ";" + opinion;
			}
			if (conclusion.equals("")) {
				conclusion = temp;
			} else {
				conclusion = conclusion + "/" + temp;
			}
		}
		return conclusion;
	}

	/**
	 * 为了149会签人员能返回到805所用于签名，特意对149会签部分的签名人员保存在ASESHuiqianSignature的signature字段中
	 *
	 * @author liaojun
	 * @param tempSignList
	 *            传入对象的所有ASESHuiqianSignature记录
	 * @param activityName
	 *            返回签审人员的活动名称
	 * @return 返回特定对象活动名称为activityName的签审人员
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static String getSignatureValue(
			List<ASESHuiqianSignature> tempSignList, String activityName)
			throws WTException, WTPropertyVetoException {
		String value = "";
		ReferenceFactory rf = new ReferenceFactory();
		Hashtable<String, ASESHuiqianSignature> ht = new Hashtable<String, ASESHuiqianSignature>();
		for (int i = 0; i < tempSignList.size(); i++) {
			ASESHuiqianSignature tempSign = tempSignList.get(i);
			String tempActOid = tempSign.getActivity();
			WfActivity wfAct = (WfActivity) rf.getReference(tempActOid)
					.getObject();
			if (wfAct.getName().equalsIgnoreCase(activityName)||(activityName.equalsIgnoreCase(Constants.ACTIVITYNAME_GYHQ)&&wfAct.getName().equalsIgnoreCase(MQConstants.ACTIVITY_NAME_KRS))) {
				String conclution = tempSign.getConclusion();
				if (conclution == null) {
					continue;
				}
				String userName = "";
				if (conclution.contains("同意") && !conclution.contains("不同意")) {
					userName = conclution.substring(0, conclution.length() - 2);
				} else if (conclution.contains("不同意")) {
					userName = conclution.substring(0, conclution.length() - 3);
				}
				userName =userName.replace(", ", "");
				userName =userName.replace(",", "");
				ASESHuiqianSignature tempSign1 = (ASESHuiqianSignature) ht
						.get(userName);
				if (tempSign1 != null) {
					if (tempSign.getCreateTimestamp().after(
							tempSign1.getCreateTimestamp())) {
						ht.put(userName, tempSign);
					}
				} else {
					ht.put(userName, tempSign);
				}
			}
		}
		Enumeration enum1 = ht.keys();
		while (enum1.hasMoreElements()) {
			Object obj1 = enum1.nextElement();
			ASESHuiqianSignature tempSign = (ASESHuiqianSignature) ht.get(obj1);
			String sig = tempSign.getSignature();
			if (sig != null) {
				if (value.equals("")) {
					value = sig;
				} else {
					value = value + ";" + sig;
				}
			}
		}
		return value;
	}




	public static void processFeedbackForPreReview(ObjectReference self,
			String route, String activityOid805) throws WTException{
		HashMap<String, String> inputparams = new HashMap<String, String>();
		SoapCall sopaCall = new SoapCall();
		String comments = null;
		WfActivity activity = (WfActivity) self.getObject();
		ProcessData pd = activity.getContext();
		comments = pd.getTaskComments();
		inputparams.put(IXBConstants.WFACTIVITY_OID, activityOid805);
		inputparams.put(IXBConstants.ROUTE, route);
		inputparams.put(IXBConstants.PREVIEW_COMMENT, comments);
		inputparams.put("sendFrom","805");
		try {
            sopaCall.callToServer("completePreviewWorkItem", inputparams);
        } catch (WTException e) {

            e.printStackTrace();
        }
	}

	private static void processFeedbackForPreview(ObjectReference self,
												  ProcessEnvelope pe, String route, String fileName, String mqmessage)
			throws WTException{
		JSONObject fromMsg = new JSONObject(mqmessage);

		Sender sender = Sender.getInstance();
		Message msg = new Win10SignTaskSynRespReceiverMessage();
		Message msgfirst = new Win10SignTaskSynRespReceiverMessage();

		String id = fromMsg.getString(Based.MSG_ID);

		LogMessage logMsg = null;

		SignatureGYZZXMLParser parser =  new SignatureGYZZXMLParser(pe);
		HashMap<String,String > inputparams = new HashMap<String, String>();
		Map<String, Object> feedbacbMap = new HashMap<String, Object>();
		SoapCall sopaCall = new SoapCall();
		WfProcess process = (WfProcess) self.getObject();
		String activityOid149 = self.getObjectId().toString();
		ProcessData pData = process.getContext();

		String activityOidGYS = (String) pData.getValue("activityOidGYS");

		String wfProcessOid = (String) pData.getValue("wfProcessOid");
		//805老流程
		String activityOid805 = (String) pData.getValue("activityOid805");

		String activityName = (String) pData.getValue("activityName");//工艺路线
		String activityTemplateID = (String) pData.getValue("activityTemplateID");

		JSONObject j_src_site = fromMsg.getJSONObject(Based.J_SRC_SITE);
		String sendFrom =j_src_site.getString("id");
		String pre_sendFrom = inputparams.get("sendFrom");
		if(!Tools.isNull(pre_sendFrom)){
			sendFrom = pre_sendFrom;
		}
		try{
			String newSendFrom = fromMsg.getString("sendFrom");
			if(!Tools.isNull(newSendFrom)){
				sendFrom = newSendFrom;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		JSONArray ja_tasks_response = new JSONArray();
		JSONArray ja_tasks_response_frist = new JSONArray();
		JSONArray ja_signs_response = new JSONArray();

		String orderIID = (String) pData.getValue("orderIID");
		String feedback = "149";
		ReferenceFactory rf = new ReferenceFactory();
		WTReference ativityGYS = rf.getReference(activityOidGYS);
		WfAssignedActivity tempActivity = (WfAssignedActivity) ativityGYS
				.getObject();
		String activityName149 = tempActivity.getName();
		WorkItem workItem = null;
		String userName ="";
		String zhurengongyishi ="";
		String comments = null;
		String ida3a4 = activityOidGYS.substring(activityOidGYS.indexOf(":") + 1);
		QuerySpec qs = new QuerySpec(WorkItem.class);
		qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id",
				"=", Long.valueOf(ida3a4)), new int[1]);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WorkItem.class, "status", "=",
				"COMPLETED"), new int[1]);
		ClassAttribute modifyStampA2 = new ClassAttribute(WorkItem.class,
				WorkItem.MODIFY_TIMESTAMP);
		qs.appendOrderBy(new OrderBy(modifyStampA2, true));
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			workItem = (WorkItem) qr.nextElement();
			pData = workItem.getContext();
			comments = pData.getTaskComments();
			Ownership ownership = workItem.getOwnership();
			userName = ownership.getOwner().getFullName();
			String endTime = WTStandardDateFormat.format(workItem.getModifyTimestamp(), "yyyy-MM-dd");
			userName = userName.replace(", ", "");
			userName = userName.replace(",", "");
			// 电子签名用 主任工艺师
			zhurengongyishi = userName + "/" + "149厂" + "/" + endTime;

			WTUser owner = (WTUser) ownership.getOwner().getPrincipal();
			JSONObject task = new JSONObject();
			JSONObject task2 = new JSONObject();
			task.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			task.put(Based.TASK_NAME, activityName149);
			task.put(Based.TASK_USER_IID, owner.getPersistInfo().getObjectIdentifier().getId()+"");
			task.put(Based.TASK_USER_ID, owner.getName());
			task.put(Based.TASK_USER_NAME, owner.getFullName());
			task.put(Based.TASK_STATE, "8");//必须可转为int类型，暂时不清楚神软需要传什么值，临时写死
			task.put(Based.TASK_PARENT_IID, -1);
			task.put(Based.TASK_CREATE_TIME, workItem.getCreateTimestamp().getTime());

			task.put(Based.TASK_FORWARDMODEL, "0");
			task.put(Based.TASK_ISTRANSMIT, "1");
			task.put(Based.TASK_ISWAITCHILD, "1");
			task.put(Based.TASK_ISLEADUP, "0");

			task2.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			task2.put(Based.TASK_NAME, activityName149);
			task2.put(Based.TASK_USER_IID, owner.getPersistInfo().getObjectIdentifier().getId()+"");
			task2.put(Based.TASK_USER_ID, owner.getName());
			task2.put(Based.TASK_USER_NAME, owner.getFullName());
			task2.put(Based.TASK_STATE, "8");//必须可转为int类型，暂时不清楚神软需要传什么值，临时写死
			task2.put(Based.TASK_PARENT_IID, -1);
			task2.put(Based.TASK_CREATE_TIME, workItem.getCreateTimestamp().getTime());

			task2.put(Based.TASK_FORWARDMODEL, "0");
			task2.put(Based.TASK_ISTRANSMIT, "1");
			task2.put(Based.TASK_ISWAITCHILD, "1");
			task2.put(Based.TASK_ISLEADUP, "0");

			ja_tasks_response.put(task);
			ja_tasks_response_frist.put(task2);

			//搜集 反馈专业所意见
			JSONObject sign = new JSONObject();
			sign.put(Based.TASK_IID, workItem.getPersistInfo().getObjectIdentifier().getId()+"");
			sign.put(Based.SIGN_DIV_NAME, "");
			if("通过".equals(route)){
				sign.put(Based.SIGN_IS_AGREE, "1");
			}else if("驳回".equals(route)){
				sign.put(Based.SIGN_IS_AGREE, "0");
			}
			sign.put(Based.SIGN_CONTENT, pData.getTaskComments());
			sign.put(Based.SIGN_USER_NAME, owner.getFullName().replace(", ", ""));
			sign.put(Based.SIGN_TYPE, "person");
			sign.put(Based.SIGN_TIME, workItem.getCreateTimestamp().getTime());

			sign.put(Based.SIGN_DIV_NAME, MQExpImpConstants.VALUE_DOMAINNAME);

			ja_signs_response.put(sign);
		}

		if (comments == null || comments.trim().equals("")) {
			comments = "无";
		}
		feedbacbMap.put("route", route);
		feedbacbMap.put("comments", comments);
		feedbacbMap.put("activityOid", activityOid149);
		feedbacbMap.put("wfProcessOid", wfProcessOid);
		//兼容805的老代码
		feedbacbMap.put("activityOid805", activityOid805);
		feedbacbMap.put("activityOid149", activityOid149);

		feedbacbMap.put("approvedType", "Preview");
		feedbacbMap.put("orderIID", orderIID);

		feedbacbMap.put("feedback", feedback);//返回单位
		feedbacbMap.put("activityTemplateID", activityTemplateID);
		feedbacbMap.put("activityName", activityName);
		feedbacbMap.put("feedbackType", "UNFORMAL");
		String feetbackStr = null;
		feedbacbMap.put("previewNumber", pe.getNumber());
		feedbacbMap.put("zhurengongyishi", userName);
		ArrayList memberList = ProcessEnvelopeUtil.getAllMembers(pe);
		String numberStr = "";
		WfAssignedActivity zhipaiZuhang = getZhipaiGongyiZuZhangActivity(process);
		for (int i = 0; memberList != null && i < memberList.size(); i++) {
			WTObject wto = (WTObject)memberList.get(i);
			String signName = "";
			QueryResult qr2 = PersistenceHelper.manager.navigate(wto,
					SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);// 取得对象所有审签信息
			List<ASESHuiqianSignature> list = new ArrayList<ASESHuiqianSignature>();
			while (qr2.hasMoreElements()) {// 遍历审签信息
				ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2.nextElement();
				//先修改意见，提前30秒把刚完成的指派组长意见放入有效意见
				if(zhipaiZuhang!=null&&zhipaiZuhang.getModifyTimestamp().getTime()>(tempSign.getModifyTimestamp().getTime()+SAVE_TIME)){
					continue;
				}
				// 判断是否是此流程的
				String aOid = tempSign.getActivity();
				try {
					WfAssignedActivity sActivity = getWfActivity(aOid);
					if(sActivity==null){
						continue;
					}
					WfProcess sP = sActivity.getParentProcess();
					if (sP.equals(process)) {
						list.add(tempSign);
					}
				}catch(WTRuntimeException e){
					e.printStackTrace();
				}
			}

			// 指派工艺组长者会签记录返回805
			String signforzrgys = "";
			try {
				signforzrgys = getSignValue(list, Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			}

			// 用于电子签名,组织会签的时候才取 工艺员及其会签记录
			String signforgyy = "";
			String gongyiyuan = "";
			if (activityName149.equals(Constants.ACTIVITYNAME_SHEJISHUJUGONGYIYUSHEN)) {
				// 预审意见汇总
				try {
					// 预审流程
					signforgyy = getSignValue(list,Constants.TASK_GONGYYUSHEN);
					gongyiyuan = getSignatureValue(list, Constants.TASK_GONGYYUSHEN);
				} catch (WTPropertyVetoException e) {

					e.printStackTrace();
				}
			}

			if (signforzrgys.equals("")) {
				signforzrgys = signforgyy;
			} else {
				if (!signforgyy.equals("")) {
					signforzrgys = signforzrgys + ";;;" + signforgyy;
				}
			}

			gongyiyuan = sortSignatureData(gongyiyuan,wto,pe,parser);
			if (!gongyiyuan.equals("")) {
				signName = zhurengongyishi + ";;;" +gongyiyuan ;
			}else{
				signName =zhurengongyishi;
			}

			// 会签信息及会签人员
			signforzrgys = signforzrgys + "pppqqq" + signName;

			if (wto instanceof WTDocument) {
				WTDocument temp = (WTDocument) wto;
				String number = "D"+ temp.getNumber();
				numberStr = numberStr + ";;;" + number;
				feedbacbMap.put(number, signforzrgys);
			} else if (wto instanceof EPMDocument) {
				EPMDocument temp = (EPMDocument) wto;
				String number = "E"+ temp.getNumber();
				numberStr = numberStr + ";;;" + number;
				feedbacbMap.put(number, signforzrgys);
			}
		}

		try {
			feedbacbMap.put("number", numberStr);
			feetbackStr = Deserialize.serializeMap(feedbacbMap);
			inputparams.put("sendFrom", "149");
			inputparams.put("pbonumber", pe.getNumber());
			inputparams.put("pboname", pe.getName());
			inputparams.put("result", feetbackStr);
			inputparams.put("zhurengongyishi", userName);

			//序列化inputparams 调用MQ消息队列发送处理

			msg.put(Based.MSG_ID, fromMsg.getString(Based.MSG_ID));
			msg.put(Based.ID, fromMsg.getString(Based.ID));
			msg.put(Based.NAME, fromMsg.getString(Based.NAME));
			msg.put(Based.MSG_DESCRIPTION,"预审反馈");

			msgfirst.put(Based.MSG_ID, fromMsg.getString(Based.MSG_ID));
			msgfirst.put(Based.ID, fromMsg.getString(Based.ID));
			msgfirst.put(Based.NAME, fromMsg.getString(Based.NAME));
			msgfirst.put(Based.MSG_DESCRIPTION,"任务创建成功");

			String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator + fileName;
			String url = MQConstants.DC_UPLOAD;
			JSONObject j_file = FSUtil.upload(localPath, url);
			System.out.println("j_file == "+j_file);
			msg.put(Based.J_FILE, j_file);

			msg.put(Based.MSG_CREATED_TIME, System.currentTimeMillis());
			msgfirst.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());
			msg.put(Based.JA_TASKS_RESPONSE, ja_tasks_response);
			msgfirst.put(Based.JA_TASKS_RESPONSE, ja_tasks_response_frist);

			msg.put(Based.JA_SIGNS_RESPONSE, ja_signs_response);
			msg.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			msg.put(Based.TARGET_SITE_IID, MQConstants.SITEIID_149);

			JSONObject j_src_site_local = new JSONObject();
			j_src_site_local.put(Based.IID, MQConstants.SITEIID_149);
			j_src_site_local.put(Based.ID, MQConstants.SITEID_149);
			j_src_site_local.put(Based.NAME, MQConstants.SITENAME_149);
			msg.put(Based.J_SRC_SITE, j_src_site_local);

			msgfirst.put(Based.RESPONSE_SITE_IID, MQConstants.SITEIID_149);
			msgfirst.put(Based.TARGET_SITE_IID, MQConstants.SITEIID_149);

			msg.put(Based.MSG_TYPE,Based.DC_RESPONSE_SHARE_TASKSYN_RECEIVER);

			msgfirst.put(Based.MSG_TYPE,Based.DC_RESPONSE_SHARE_RECEIVER);

			msg.put("target_site_iid", MQConstants.SITEIID_149);
			msgfirst.put("target_site_iid", MQConstants.SITEIID_149);
			String inputparamsStr = ext.sast.center.ixb.util.Deserialize.serializeMap(inputparams);
			msg.put("soapparams", inputparamsStr);
			msgfirst.put("sendFrom", "149");
			if(!MQExpImpUtil.isCmPackage(sendFrom)){
				sender.send(msgfirst);
			}
			msg.put("sendFrom", "149");

			saveMqMsg(msg);
			sender.send(msg);

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149,MQConstants.STATUS_11);
			sender.addLog(logMsg);
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getLocalizedMessage()!=null&&e.getLocalizedMessage().contains("未初始化连接")){
				throw new WTException("发送消息工具-无法发送消息：未初始化连接");
			}
			MetaMessage metaMsg = new MetaMessage();
			metaMsg.setMsgId(id);
			metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
			sender.updateMetaMessage(metaMsg);

			logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.STATUS_12);
			logMsg.setException(StackTraceUtil.getStackTrace(e));
			sender.addLog(logMsg);
		}
	}

	public static WfAssignedActivity getWfActivity(String tempActOid)  {
   	 ReferenceFactory rf = new ReferenceFactory();
   	 WfAssignedActivity wfAct = null;
        try {
			 wfAct = (WfAssignedActivity) rf.getReference(tempActOid).getObject();
		} catch (WTRuntimeException e) {

			e.printStackTrace();
		} catch (WTException e) {

			e.printStackTrace();
		}
        return wfAct;
   }


   private static boolean checkIsErrorAgree(ProcessData pData){
	   boolean isErrorAgree = false;
	   Boolean dwttgFlag = (Boolean)pData.getValue("dwttgFlag");
	   if(dwttgFlag != null){
		   isErrorAgree = dwttgFlag;
	   }
	   return isErrorAgree;
   }
}
