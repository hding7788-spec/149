package ext.casc.synch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.soap.SOAPException;

import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.method.RemoteAccess;
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
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.util.WTStandardDateFormat;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.definer.WfBlockTemplate;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.ases.part.ASESHuiqianSignature;
import ext.ases.part.SignLink;
import ext.casc.constants.Constants;
import ext.casc.ixb.IXBConstants;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewObject;
import ext.casc.preview.PreviewUtil;
import ext.casc.util.Deserialize;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureRecord;

/**
 * 149会签流程信息反馈至805所
 *
 * @since 2012-06-03
 * @author liaojun
 * @throws WTException
 * @throws IOException
 * @throws WTPropertyVetoException
 */
public class StandardDataReceiveService implements RemoteAccess {

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
	 * @throws SOAPException
	 */
	public static void processFeedback(ObjectReference self, WTObject pbo,
			String route, String fileName) throws WTException, IOException,
			WTPropertyVetoException, SOAPException {
		boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
		if (pbo instanceof ProcessEnvelope) {
			ProcessEnvelope pe = (ProcessEnvelope) pbo;
			processFeedbackForEnvelope(self, pe, route, fileName);
		} else if (pbo instanceof ChangeRequest) {
		    ChangeRequest cr = (ChangeRequest) pbo;
			processFeedbackChangeRequest(self, cr, route, fileName);
		} else if (pbo instanceof ChangePackaged) {
            ChangePackaged changePackaged = (ChangePackaged) pbo;
            processFeedbackChangePackaged(self, changePackaged, route, fileName);
        }  else if (pbo instanceof WTDocument) {
			WTDocument document = (WTDocument) pbo;
			processFeedbackForWTdocument(self, document, route);
		}  else if (pbo instanceof Preview) {
			Preview preview = (Preview) pbo;
			processFeedbackForPreview(self, preview, route, fileName);
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
	 * @throws WTException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */
	private static void processFeedbackChangePackaged(ObjectReference self,
			ChangePackaged changePackaged, String route, String fileName)
			throws WTException{
		// TODO Auto-generated method stub
		SignatureGYZZXMLParser parser =  new SignatureGYZZXMLParser((ContentHolder)changePackaged);
		HashMap<String, Object> inputparams = new HashMap<String, Object>();
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
        String sendFrom = (String) pData.getValue("sendFrom");
        if(sendFrom==null){
            sendFrom="805";
        }
        String orderIID = (String) pData.getValue("orderIID");
        String feedback = "149";

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
		}
		if (comments == null || comments.trim().equals("")) {
			comments = "无";
		}

		feedbacbMap.put("comments", comments);
        feedbacbMap.put("activityOid", activityOid149);
        feedbacbMap.put("wfProcessOid", wfProcessOid);
        //805用
        feedbacbMap.put("activityOid805", activityOid805);
        feedbacbMap.put("activityOid149", activityOid149);

        feedbacbMap.put("approvedType", "ChangePackaged");//先不动此参数
        feedbacbMap.put("route", route);
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
					// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
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
				} else if (wto instanceof EPMDocument) {
					EPMDocument temp = (EPMDocument) wto;
					String number ="E"+ temp.getNumber();
					numberStr = numberStr + ";;;" + number;
					feedbacbMap.put(number, signforzrgys);
				} else if (wto instanceof ChangePackaged) {
					ChangePackaged change = (ChangePackaged) wto;
					String number = "C"+change.getNumber();
					numberStr = numberStr + ";;;" + number;
					feedbacbMap.put(number, signforzrgys);
				}
			}
		}
		WTProperties prop=null;
		String hostName ="";
		try {
			prop = WTProperties.getLocalProperties();
			hostName = prop.getProperty("java.rmi.server.hostname");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = "http://" + hostName + "/" + fileName;
		feedbacbMap.put("number", numberStr);

		feetbackStr = Deserialize.serializeMap(feedbacbMap);
		inputparams.put("URL", url);
		inputparams.put("pbonumber", changePackaged.getNumber());
		inputparams.put("pboname", changePackaged.getName());
		inputparams.put("result", feetbackStr);
		inputparams.put("sendFrom", sendFrom);
		inputparams.put("zhurengongyishi", userName);
		ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
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
     * @throws WTException
     * @throws IOException
     * @throws WTPropertyVetoException
     */
    private static void processFeedbackChangeRequest(ObjectReference self,
            ChangeRequest request, String route, String fileName)
            throws WTException{
        // TODO Auto-generated method stub
    	SignatureGYZZXMLParser parser =  new SignatureGYZZXMLParser((ContentHolder)request);
        HashMap<String, Object> inputparams = new HashMap<String, Object>();
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
        String sendFrom = (String) pData.getValue("sendFrom");
        if(sendFrom==null){
            sendFrom="805";
        }
        String orderIID = (String) pData.getValue("orderIID");
        String feedback = "149";

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
		}
		if (comments == null || comments.trim().equals("")) {
			comments = "无";
		}

        feedbacbMap.put("comments", comments);
        feedbacbMap.put("activityOid", activityOid149);
        feedbacbMap.put("wfProcessOid", wfProcessOid);
        //805用
        feedbacbMap.put("activityOid805", activityOid805);
        feedbacbMap.put("activityOid149", activityOid149);

        feedbacbMap.put("approvedType", "ChangeRequest");//先不动此参数
        feedbacbMap.put("route", route);
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
                    // TODO Auto-generated catch block
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
                        // TODO Auto-generated catch block
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
                } else if (wto instanceof EPMDocument) {
                    EPMDocument temp = (EPMDocument) wto;
                    String number = "E"+ temp.getNumber();
                    numberStr = numberStr + ";;;" + number;
                    feedbacbMap.put(number, signforzrgys);
                } else if (wto instanceof ChangeRequest) {
                    ChangeRequest changeRequest = (ChangeRequest) wto;
                    String number = "R"+ changeRequest.getNumber();
                    numberStr = numberStr + ";;;" + number;
                    feedbacbMap.put(number, signforzrgys);
                }
            }
        }
        WTProperties prop=null;
        String hostName ="";
        try {
            prop = WTProperties.getLocalProperties();
            hostName = prop.getProperty("java.rmi.server.hostname");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String url = "http://" + hostName + "/" + fileName;
        feedbacbMap.put("number", numberStr);

        feetbackStr = Deserialize.serializeMap(feedbacbMap);
        inputparams.put("URL", url);
        inputparams.put("result", feetbackStr);
        inputparams.put("sendFrom", sendFrom);
        inputparams.put("zhurengongyishi", userName);
        ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
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
	 * @throws WTException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */
	private static void processFeedbackForEnvelope(ObjectReference self,
			ProcessEnvelope pe, String route, String fileName)
			throws WTException{
		SignatureGYZZXMLParser parser =  new SignatureGYZZXMLParser((ContentHolder)pe);
		HashMap<String, Object> inputparams = new HashMap<String, Object>();
		Map<String, Object> feedbacbMap = new HashMap<String, Object>();
		SoapCall sopaCall = new SoapCall();
		WfActivity activity = (WfActivity) self.getObject();
		WfProcess process = activity.getParentProcess();
		String activityOid149 = self.getObjectId().toString();
		ProcessData pData = process.getContext();
		String activityOidGYS = (String) pData.getValue("activityOidGYS");

		String wfProcessOid = (String) pData.getValue("wfProcessOid");
		//805老流程
		String activityOid805 = (String) pData.getValue("activityOid805");

		String activityName = (String) pData.getValue("activityName");//工艺路线
		String activityTemplateID = (String) pData.getValue("activityTemplateID");
        String sendFrom = (String) pData.getValue("sendFrom");

        if(sendFrom ==null){
            sendFrom = "805";
        }
        String orderIID = (String) pData.getValue("orderIID");
        if("orderidaaabb".equals(orderIID)){
        	 sendFrom = "805";
        }
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
					comments = comments + " "+pData.getTaskComments();
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

		}
		if (comments == null || comments.trim().equals("")) {
			comments = "无";
		}
		feedbacbMap.put("comments", comments);
		feedbacbMap.put("activityOid", activityOid149);
		feedbacbMap.put("wfProcessOid", wfProcessOid);
		//805用
		feedbacbMap.put("activityOid805", activityOid805);
		feedbacbMap.put("activityOid149", activityOid149);


		feedbacbMap.put("approvedType", "ProcessEnvelope");//先不动此参数
		feedbacbMap.put("route", route);
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
		for (int i = 0; memberList != null && i < memberList.size(); i++) {
			WTObject wto = (WTObject)memberList.get(i);
			String signName = "";
			if (!(wto instanceof WTPart)) {
				QueryResult qr2 = PersistenceHelper.manager.navigate(wto,
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


				// 指派工艺组长者会签记录返回805
				String signforzrgys = "";
				try {
					signforzrgys = getSignValue(list, Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);


				} catch (WTPropertyVetoException e) {
					// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
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
				} else if (wto instanceof EPMDocument) {
					EPMDocument temp = (EPMDocument) wto;
					String number = "E"+ temp.getNumber();
					numberStr = numberStr + ";;;" + number;
					feedbacbMap.put(number, signforzrgys);
				}
			}
		}
		WTProperties prop=null;
		String hostName ="";
		try {
			prop = WTProperties.getLocalProperties();
			hostName = prop.getProperty("java.rmi.server.hostname");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = "http://" + hostName + "/" + fileName;
		feedbacbMap.put("number", numberStr);
		feetbackStr = Deserialize.serializeMap(feedbacbMap);
		inputparams.put("URL", url);
		inputparams.put("pbonumber", pe.getNumber());
		inputparams.put("pboname", pe.getName());
		inputparams.put("result", feetbackStr);
		inputparams.put("sendFrom", sendFrom);
		inputparams.put("zhurengongyishi", userName);
		ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
	}
	public static void noticeZongTiSuo(ObjectReference self)  {
		Persistable p = self.getObject();
		WfProcess process = null;
		if (p instanceof WfActivity) {
			WfActivity activity = (WfActivity) self.getObject();
			try {
				process = activity.getParentProcess();
			} catch (WTException e) {
				// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTException e) {
				// TODO Auto-generated catch block
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
	 * @throws WTException
	 * @throws IOException
	 * @throws WTPropertyVetoException
	 */

	private static void processFeedbackForWTdocument(ObjectReference self,
			WTDocument document, String route) throws WTException {
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
			// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
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
			if (wfAct.getName().equalsIgnoreCase(activityName)) {
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
			if (wfAct.getName().equalsIgnoreCase(activityName)) {
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
			String route, String activityOid805) throws MalformedURLException, SOAPException{
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	private static void processFeedbackForPreview(ObjectReference self,
			Preview preview, String route, String fileName)
			throws WTException, MalformedURLException, SOAPException{
		HashMap<String, Object> inputparams = new HashMap<String, Object>();
		Map<String, Object> feedbacbMap = new HashMap<String, Object>();
		SoapCall sopaCall = new SoapCall();
		WfActivity activity = (WfActivity) self.getObject();
		WfProcess process = activity.getParentProcess();
		String activityOid = self.getObjectId().toString();
		ProcessData pData = process.getContext();
		String activityOidGYS = (String) pData.getValue("activityOidGYYS");
		String wfProcessOid = (String) pData.getValue("wfProcessOid");
		String activityName = (String) pData.getValue("activityName");//工艺路线
		String activityTemplateID = (String) pData.getValue("activityTemplateID");
		String sendFrom = (String) pData.getValue("sendFrom");
		String orderIID = (String) pData.getValue("orderIID");
		String feedback = "149";
		ReferenceFactory rf = new ReferenceFactory();
		WTReference ativityGYS = rf.getReference(activityOidGYS);
		WfAssignedActivity tempActivity = (WfAssignedActivity) ativityGYS
				.getObject();
		String activityName149 = tempActivity.getName();
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
		if (qr.hasMoreElements()) {
			workItem = (WorkItem) qr.nextElement();
			pData = workItem.getContext();
			comments = pData.getTaskComments();
		}
		if (comments == null || comments.trim().equals("")) {
			comments = "无";
		}
		feedbacbMap.put("comments", comments);
		feedbacbMap.put("activityOid", activityOid);
		feedbacbMap.put("wfProcessOid", wfProcessOid);
		//兼容805的老代码
		feedbacbMap.put("activityOid805", wfProcessOid);
		feedbacbMap.put("activityOid149", activityOid);

		feedbacbMap.put("approvedType", "Preview");
		feedbacbMap.put("route", route);
		feedbacbMap.put("orderIID", orderIID);

        feedbacbMap.put("feedback", feedback);//返回单位
        feedbacbMap.put("activityTemplateID", activityTemplateID);
        feedbacbMap.put("activityName", activityName);
        feedbacbMap.put("feedbackType", "UNFORMAL");
		String feetbackStr = null;
		feedbacbMap.put("previewNumber", preview.getNumber());
		List memberList = PreviewUtil.getAllMembers(preview);
		String numberStr = "";
		for (int i = 0; memberList != null && i < memberList.size(); i++) {
			WTObject wto = (WTObject)memberList.get(i);
			QueryResult qr2 = PersistenceHelper.manager.navigate(wto,
					SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);// 取得对象所有审签信息
			List<ASESHuiqianSignature> list = new ArrayList<ASESHuiqianSignature>();
			while (qr2.hasMoreElements()) {// 遍历审签信息
				ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2
						.nextElement();
				// 判断是否是此流程的
				String aOid = tempSign.getActivity();
				try {

					WfAssignedActivity sActivity  =  getWfActivity(aOid);
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

			String signforgyy = "";
			if (activityName149.equals(Constants.ACTIVITYNAME_SHEJISHUJUGONGYIYUSHEN)) {
				// 预审意见汇总
				try {
					// 预审流程
					signforgyy = getSignValue(list,
							Constants.ACTIVITYNAME_SHEJISHUJUGONGYIYUSHEN);
					// gongyiyuan = getSignatureValue(list, "工艺会签");
				} catch (WTPropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


			if (wto instanceof PreviewObject) {
				PreviewObject temp = (PreviewObject) wto;
				String number = temp.getNumber();
				numberStr = numberStr + ";;;" + number;
				feedbacbMap.put(number, signforgyy);
			}

		}
		WTProperties prop=null;
		String hostName ="";
		try {
			prop = WTProperties.getLocalProperties();
			hostName = prop.getProperty("java.rmi.server.hostname");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = "http://" + hostName + "/" + fileName;
		feedbacbMap.put("number", numberStr);
		feedbacbMap.put("feedback", feedback);
		feedbacbMap.put("feedbackType", "UNFORMAL");
		feetbackStr = Deserialize.serializeMap(feedbacbMap);
		inputparams.put("URL", url);
		inputparams.put("sendFrom", sendFrom);
		inputparams.put("result", feetbackStr);
		ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
	}
	public static WfAssignedActivity getWfActivity(String tempActOid)  {
   	 ReferenceFactory rf = new ReferenceFactory();
   	 WfAssignedActivity wfAct = null;
        try {
			 wfAct = (WfAssignedActivity) rf.getReference(tempActOid).getObject();
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return wfAct;
   }


}