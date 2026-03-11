package ext.casc.part;

import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.extend.util.Debug;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.ases.part.ASESHuiqianSignature;
import ext.ases.part.SignLink;
import ext.ases.technotice.TechNoticeHelper;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewObject;
import ext.casc.preview.PreviewUtil;
import ext.casc.synch.StandardDataReceiveService;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import ext.casc.workflow.PrintHelper;
import ext.casc.workflow.TaskConfigrationHelper;
import ext.casc.workflow.signtrue.zp.SignatureService;
import ext.sast.center.synch.MQConstants;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.*;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.*;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.*;

public class SignatureHelper implements RemoteAccess, Serializable {
    /** */
    private static final long serialVersionUID = -7475066888632154216L;
    private static final Logger log;
    static {
        try {
            log = LogR.getLogger(SignatureHelper.class.getName());
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void setSignature(String workItemOid, String wtObjectOids) throws WTRuntimeException, WTException,
            WTPropertyVetoException {
        if (wtObjectOids == null || "".equals(wtObjectOids)) {
            return;
        }
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        String activityOid = PersistenceHelper.getObjectIdentifier(activity).toString();
        String wtObjectOidArray[] = wtObjectOids.split(";;;ppp");
        WTPrincipal wtp = SessionHelper.getPrincipal();
        String userName = ((WTUser) wtp).getFullName();
        // 如果是外部会签，签审人需要特殊处理
        if ("外部会签".equals(activity.getName())
        		&&!PrintHelper.processTemps.contains(activity.getParentProcess().getTemplate().getName())
        		&&!activity.getParentProcess().getTemplate().getName().equals(Constants.WFN_PROCESS_ECN)
        		&&!activity.getParentProcess().getTemplate().getName().equals(Constants.WFN_WUJIPROCESSWF)
        		) {
            String proxy = (String) TaskConfigrationHelper.getActivityVariableValue(activity, "proxy");
            userName = proxy;
        }

        userName = userName.replace(", ", "");
        userName = userName.replace(",", "");
        for (int i = 0; i < wtObjectOidArray.length; i++) {
            String signValueArray[] = wtObjectOidArray[i].split(";;;qqq");
            String tempOid = signValueArray[0];
            if (!signValueArray[1].equalsIgnoreCase("无需会签")) {
                String result = userName + " " + signValueArray[1];
                String message;
                if (signValueArray.length > 2) {
                    message = signValueArray[2];
                } else {
                    message = "";
                }
                String advise;
                if (signValueArray.length > 3) {
                    advise = signValueArray[3];
                } else {
                    advise = "";
                }
                WTObject obj = (WTObject) rf.getReference(tempOid).getObject();
                ASESHuiqianSignature tempSign = new ASESHuiqianSignature();
                tempSign.setActivity(activityOid);
                tempSign.setOpinion(advise);
                tempSign.setConclusion(result);
                tempSign.setSignature(message);
                tempSign = (ASESHuiqianSignature) PersistenceHelper.manager.save(tempSign);
                // SignActivityLink sal = SignActivityLink.newSignActivityLink(tempSign, activity);
                // PersistenceHelper.manager.save(sal);
                SignLink sl = SignLink.newSignLink(obj, tempSign);
                PersistenceHelper.manager.save(sl);
            }
        }
    }

    public static void setSignatureImplementAdvise(String workItemOid, String wtObjectOids) throws WTRuntimeException,
            WTException,
            WTPropertyVetoException {
        if (wtObjectOids == null || "".equals(wtObjectOids)) {
            return;
        }
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        String activityOid = PersistenceHelper.getObjectIdentifier(activity).toString();
        String wtObjectOidArray[] = wtObjectOids.split(";;;ppp");
        WTPrincipal wtp = SessionHelper.getPrincipal();
        String userName = ((WTUser) wtp).getFullName();
        userName = userName.replace(", ", "");
        userName = userName.replace(",", "");
        Date date = new Date();
        for (int i = 0; i < wtObjectOidArray.length; i++) {
            String signValueArray[] = wtObjectOidArray[i].split(";;;qqq");
            String tempOid = signValueArray[0];
            if (!signValueArray[1].equalsIgnoreCase("无需会签")) {
                String result = userName + " " + signValueArray[1];
                String time =  WTStandardDateFormat.format( date, "yyyy-MM-dd");
                String message  = userName + "/" + "149厂" + "/" + time;

                String advise;
                if (signValueArray.length > 2) {
                    advise = signValueArray[2];
                } else {
                    advise = "";
                }
                String implement;
                if (signValueArray.length > 3) {
                    implement = signValueArray[3];
                } else {
                    implement = "";
                }
                String state;
                if (signValueArray.length > 9) {
                    state = signValueArray[9];
                } else {
                    state = "";
                }
                WTObject obj = (WTObject) rf.getReference(tempOid).getObject();
                ASESHuiqianSignature tempSign = new ASESHuiqianSignature();
                tempSign.setActivity(activityOid);
                tempSign.setConclusion(result);
                tempSign.setOpinion(advise);
                tempSign.setSignature(message);
                tempSign.setImplementadvise(implement);
                tempSign.setUpdatestate(state);
                tempSign = (ASESHuiqianSignature) PersistenceHelper.manager.save(tempSign);
                SignLink sl = SignLink.newSignLink(obj, tempSign);
                PersistenceHelper.manager.save(sl);
            }
        }
    }

    public static void setSignatureImplementAdvise(String workItemOid, String wtObjectOids, String proxy)
            throws WTRuntimeException,
            WTException,
            WTPropertyVetoException {
        if (wtObjectOids == null || "".equals(wtObjectOids)) {
            return;
        }
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        String activityOid = PersistenceHelper.getObjectIdentifier(activity).toString();
        String wtObjectOidArray[] = wtObjectOids.split(";;;ppp");
        WTPrincipal wtp = SessionHelper.getPrincipal();
        String userName = ((WTUser) wtp).getFullName();

        for (int i = 0; i < wtObjectOidArray.length; i++) {
            String signValueArray[] = wtObjectOidArray[i].split(";;;qqq");
            String tempOid = signValueArray[0];
            if (!signValueArray[1].equalsIgnoreCase("无需会签")) {
                String result = proxy + " " + signValueArray[1];
                String message;
                if (signValueArray.length > 2) {
                    message = signValueArray[2];
                } else {
                    message = "";
                }
                String advise;
                if (signValueArray.length > 3) {
                    advise = signValueArray[3];
                } else {
                    advise = "";
                }
                String implement;
                if (signValueArray.length > 4) {
                    implement = signValueArray[4];
                } else {
                    implement = "";
                }
                WTObject obj = (WTObject) rf.getReference(tempOid).getObject();
                ASESHuiqianSignature tempSign = new ASESHuiqianSignature();
                tempSign.setActivity(activityOid);
                tempSign.setOpinion(advise);
                tempSign.setConclusion(result);
                tempSign.setSignature(message);
                tempSign.setImplementadvise(implement);
                tempSign = (ASESHuiqianSignature) PersistenceHelper.manager.save(tempSign);
                SignLink sl = SignLink.newSignLink(obj, tempSign);
                PersistenceHelper.manager.save(sl);
            }
        }
    }

    public static List<String> getReviewOid(String workItemOid) throws WTRuntimeException, WTException {
        List<String> oidList = new ArrayList<String>();
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");
        List<WTObject> memberList = null;
        if (pbo instanceof ProcessEnvelope) {
            memberList = ProcessEnvelopeUtil.getAllMembers((ProcessEnvelope) pbo);
        } else if (pbo instanceof WTChangeOrder2) {
            String objectType = "";
            try {
                objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // 添加作废申请单的判断
            if (objectType.indexOf("CHANGE_ECN") > -1 || objectType.indexOf("PROCESS_ECN") > -1) {
                memberList = ChangeHelper.getChangeResultItem((WTChangeOrder2) pbo);
                memberList.add((WTObject) pbo);
            }
            // else if(objectType.indexOf("ZUOFEI_ECN") > -1){
            // memberList = ChangeHelper.getChangeAffectItem((WTChangeOrder2)pbo);
            // memberList.add((WTObject)pbo);
            // }

        } else if (pbo instanceof WTDocument) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof MPMProcessPlan) {
            memberList = new ArrayList<WTObject>(1);
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof ChangeRequest) {
            memberList = new ArrayList<WTObject>();
            ChangeRequest changeRequest = (ChangeRequest) pbo;
            memberList.add(changeRequest);
        } else if (pbo instanceof ChangePackaged) {
            memberList = new ArrayList<WTObject>();
            ChangePackaged changePackaged = (ChangePackaged) pbo;
            memberList.add(changePackaged);
            QueryResult qr = PersistenceHelper.manager.navigate(changePackaged,
                    ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                    ChangePackagedResultLink.class, true);
            while (qr.hasMoreElements()) {
                memberList.add((WTObject) qr.nextElement());
            }

        } else if (pbo instanceof Preview) {
        	Preview pre = (Preview)pbo;
        	memberList = new ArrayList<WTObject>();
        	ArrayList<WTObject> list = PreviewUtil.getAllMembers(pre);
        	for (WTObject wtObject : list) {
        		memberList.add(wtObject);
			}
        }
        if (memberList != null) {
            for (int i = 0; i < memberList.size(); i++) {
                Object temp = memberList.get(i);
                if (temp instanceof WTPart) {
                    String partType = TypeIdentifierHelper.getType(temp).toString();
                    String ctype = IBAHelper.getIBAStringValue((WTPart)temp,"CTYPE");
                    if(partType.endsWith(FaCiBomHelper.FACI_BOM_TYPE)||"备料".equals(ctype)){
                        String tempOid = PersistenceHelper.getObjectIdentifier((Persistable) temp).toString();
                        oidList.add(tempOid);
                    }
                } else {
                    String tempOid = PersistenceHelper.getObjectIdentifier((Persistable) temp).toString();
                    oidList.add(tempOid);
                }
            }
        }

        return oidList;
    }

    public static List<String> getSignatureObject(NmCommandBean cb) throws WTException {
    	String oid = (String)cb.getRequestData().getParameterMap().get("oid");
		Map<WTObject,List<ASESHuiqianSignature>> signMap = SignatureHelper.getSignature(oid);
    	ReferenceFactory rf = new ReferenceFactory();
    	List<String> oidList = new ArrayList<String>();
		if(signMap != null) {
			Iterator iterator = signMap.keySet().iterator();
			while(iterator.hasNext()) {
				Object obj = iterator.next();
				List<ASESHuiqianSignature> tempSignList = signMap.get(obj);
				String tempValue = "";
				Hashtable ht = new Hashtable();
				for(int i = 0 ; i < tempSignList.size() ; i++){
					ASESHuiqianSignature tempSign = tempSignList.get(i);
					String tempActOid = tempSign.getActivity();
					WfActivity wfAct = (WfActivity)rf.getReference(tempActOid).getObject();
					if(wfAct.getName().equalsIgnoreCase("工艺会签")||wfAct.getName().equalsIgnoreCase("外部会签")){
						String conclution = tempSign.getConclusion();
						if(conclution == null){
							continue;
						}
						oidList.add(rf.getReference(tempSign).toString());
	                }
	            }
			}
		}
		return oidList;
    }

    public static Object getPBO(String workItemOid) throws WTRuntimeException, WTException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        return activity.getContext().getValue("primaryBusinessObject");
    }

    public static List<String> getReviewOid3(String workItemOid) throws WTRuntimeException, WTException {
        List<String> oidList = new ArrayList<String>();
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");
        List<WTObject> memberList = null;
		if (pbo instanceof WTChangeOrder2) {
			memberList = new ArrayList<WTObject>();
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			memberList.add(changeOrder2);
			WTObject targetObj = PrintHelper.getReleatedDocByECN(changeOrder2);
            if(targetObj != null){
            	memberList.add(targetObj);
            }

			QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			while (qResult.hasMoreElements()) {
				Object object = qResult.nextElement();
				if (object instanceof WTObject) {
					memberList.add((WTObject) object);
				}
			}
		} else if (pbo instanceof MPMProcessPlan) {
        	memberList = new ArrayList<WTObject>();
            memberList.add((MPMProcessPlan) pbo);
        }else if(pbo instanceof WTDocument){
        	memberList = new ArrayList<WTObject>();
        	WTDocument wtdoc = (WTDocument) pbo;
            memberList.add(wtdoc);
			try {
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtdoc);
				if(docType.contains("|")){
		       	   docType = docType.substring(docType.lastIndexOf("|")+1,docType.length());
		       	}
				if(docType.equals("casc.sast.149.PROCESS_NOTICE") || docType.equals("casc.sast.812.JSTZD_812")){
					List objects = TechNoticeHelper.service.getTechNoticeAfterMembers(wtdoc);
		        	if(objects != null && !objects.isEmpty()){
		        		memberList.addAll(objects);
		        	}
            	}
			} catch (RemoteException e) {
				e.printStackTrace();
			}

        }

        if (memberList != null) {
            for (int i = 0; i < memberList.size(); i++) {
                Object temp = memberList.get(i);
                String tempOid = PersistenceHelper.getObjectIdentifier((Persistable) temp).toString();
                oidList.add(tempOid);
            }
        }

        return oidList;
    }

    public static Map<WTObject, List<ASESHuiqianSignature>> getSignature(String workItemOid) throws WTRuntimeException,
            WTException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        WfProcess wfprocess = getProcess(wfAct);
        Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
        return getSignatureFromPBO(pbo, wfprocess);

    }

    public static Map<WTObject, List<ASESHuiqianSignature>> getSignatureFromPBO(Persistable pbo, WfProcess wfprocess)
            throws WTRuntimeException, WTException {
        Map<WTObject, List<ASESHuiqianSignature>> map = new HashMap<WTObject, List<ASESHuiqianSignature>>();

        List<WTObject> memberList = null;
        if (pbo instanceof ProcessEnvelope) {
            memberList = ProcessEnvelopeUtil.getAllMembers((ProcessEnvelope) pbo);
        } else if (pbo instanceof WTChangeOrder2) {
            memberList = ChangeHelper.getChangeResultItem((WTChangeOrder2) pbo);
            WTObject targetObj = PrintHelper.getReleatedDocByECN((WTChangeOrder2) pbo);
            if(targetObj != null){
            	memberList.add(targetObj);
            }
            memberList.add((WTChangeOrder2) pbo);
        } else if (pbo instanceof MPMProcessPlan) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof WTDocument) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof ChangeRequest) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof ChangePackaged) {
            memberList = new ArrayList<WTObject>();
            memberList.add((ChangePackaged)pbo);
            QueryResult qr = PersistenceHelper.manager.navigate(pbo, ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                     ChangePackagedResultLink.class, true);
            while (qr.hasMoreElements()) {
                memberList.add((RevisionControlled) qr.nextElement());
            }
        }else if (pbo instanceof Preview) {
            memberList = new ArrayList<WTObject>();
            Preview preview = (Preview) pbo;
            memberList = PreviewUtil.getAllMembers(preview);
        }
        for (int i = 0; memberList != null && i < memberList.size(); i++) {
            WTObject wto = memberList.get(i);
            if (isShowSignature(wto)) {
                QueryResult qr = PersistenceHelper.manager.navigate(wto, SignLink.ROLE_BOBJECT_ROLE, SignLink.class,
                        true);
                List<ASESHuiqianSignature> tempList = new ArrayList<ASESHuiqianSignature>();
                while (qr.hasMoreElements()) {
                    ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr.nextElement();
                    String tempActOid = tempSign.getActivity();
                    WfActivity wfAct =  getWfActivity(tempActOid);
                    if(wfAct==null){
                    	continue;
                    }
                    /*ReferenceFactory rf = new ReferenceFactory();
                    WfActivity wfAct = (WfActivity) rf.getReference(tempActOid).getObject();*/
                    WfProcess wp = getProcess(wfAct);
                    if (wp.equals(wfprocess)) {
                        tempList.add(tempSign);
                    }
                }
                map.put(wto, tempList);
            }
        }
        return map;
    }
    public static WfActivity getWfActivity(String tempActOid)  {
    	 ReferenceFactory rf = new ReferenceFactory();
    	 WfActivity wfAct = null;
         try {
			 wfAct = (WfActivity) rf.getReference(tempActOid).getObject();
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         return wfAct;
    }

    // 只取最后一个
    public static Map<WTObject, List<ASESHuiqianSignature>> getSignatureFromPBO2(Persistable pbo)
            throws WTRuntimeException, WTException {
        Map<WTObject, List<ASESHuiqianSignature>> map = new HashMap<WTObject, List<ASESHuiqianSignature>>();

        List<WTObject> memberList = null;
        if (pbo instanceof ProcessEnvelope) {
            memberList = ProcessEnvelopeUtil.getAllMembers((ProcessEnvelope) pbo);
        } else if (pbo instanceof WTChangeOrder2) {
            String objectType = "";
            try {
                objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // 添加作废申请单的判断
            if (objectType.indexOf("CHANGE_ECN") > -1) {
                memberList = ChangeHelper.getChangeResultItem((WTChangeOrder2) pbo);
            } else if (objectType.indexOf("ZUOFEI_ECN") > -1) {
                memberList = ChangeHelper.getChangeAffectItem((WTChangeOrder2) pbo);
            }
            memberList.add((WTObject) pbo);

        } else if (pbo instanceof MPMProcessPlan) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof WTDocument) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        }
        for (int i = 0; memberList != null && i < memberList.size(); i++) {
            WTObject wto = memberList.get(i);
            if (!(wto instanceof WTPart)) {
                QueryResult qr = PersistenceHelper.manager.navigate(wto, SignLink.ROLE_BOBJECT_ROLE, SignLink.class,
                        true);
                int size = qr.size();
                int j = 0;
                List<ASESHuiqianSignature> tempList = new ArrayList<ASESHuiqianSignature>();
                while (qr.hasMoreElements()) {
                    j++;
                    if (j == size) {
                        ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr.nextElement();
                        tempList.add(tempSign);
                        map.put(wto, tempList);
                    }
                }

            }
        }
        return map;
    }

    public static Map<String, String> getSignatureByWTObject(WfProcess process, WTObject wto) throws Exception {
        WTObject pbo = (WTObject) process.getBusinessObjectReference(new ReferenceFactory()).getObject();
        List<String> userList = SignatureService.getHQObjectLinkUsers(pbo, wto);
        List<String> userNameList = new ArrayList<String>();
        for (String userOid : userList) {
            userOid = userOid.trim();
            if(!"".equals(userOid)){
                WTUser tempUser = (WTUser) WCUtil.getPersistable(userOid);
                userNameList.add(tempUser.getFullName());
            }
        }
        Map<String, String> signMap = new HashMap<String, String>();
        ReferenceFactory rf = new ReferenceFactory();
        List activityList = new ArrayList();
        //WfBlock wfblock = PrintHelper.getBlock(process);
        List<WfBlock> allWfBlocks = PrintHelper.getAllBlock(process);
        activityList = PrintHelper.getActivities(process, activityList);
//        if (wfblock != null) {
//            activityList = PrintHelper.getActivities(wfblock, activityList);
//        }
        for (WfBlock wfBlock : allWfBlocks) {
            PrintHelper.getActivities(wfBlock, activityList);
        }
        QueryResult qr2 = PersistenceHelper.manager.navigate(wto, SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);// 取得对象所有审签信息
        if (qr2.size() == 0) {// 考虑驳回不重新签审的情况，需要取同一版本的历史Iteration对象的会前信息
            if (wto instanceof RevisionControlled) {
                QueryResult qrVersion = VersionControlHelper.service.allIterationsFrom((RevisionControlled) wto);
                while (qrVersion.hasMoreElements()) {
                    RevisionControlled version = (RevisionControlled) qrVersion.nextElement();
                    qr2 = PersistenceHelper.manager.navigate(version, SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);
                    if (qr2.size() > 0) {
                        break;
                    }
                }
            }
        }
        Iterator iterator = activityList.iterator();
        do {
            if (!iterator.hasNext()) {
                break;
            }
            WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
            String activityName = wfactivity.getName();
            if (activityName == null) {
                activityName = "";
            }
            String rolePrincipalName = PrintHelper.getPrincipalName(wfactivity);// 获取任务执行人
            String endTime = "";
            if (rolePrincipalName == null) {
                rolePrincipalName = "";
            }
            if (wfactivity.getStartTime() != null && wfactivity.getEndTime() != null) {// 获取任务完成时间
                endTime = WTStandardDateFormat.format(wfactivity.getEndTime(), "yyyy-MM-dd");
            } else {
                endTime = "";
            }
            while (qr2.hasMoreElements()) {// 遍历审签信息
                ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2.nextElement();
                String tempActOid = tempSign.getActivity();
                WfActivity wfAct = (WfActivity) rf.getReference(tempActOid).getObject();
                String wfActName = wfAct.getName();
                if (wfactivity.equals(wfAct)) {// 如果审签信息属于当前节点
                    if (wfActName.equals("内部会签")) {
                        String value = signMap.get(wfActName);
                        log.debug(wfActName + "'s value is: " + value);
                        log.debug(rolePrincipalName + "'s value is: " + rolePrincipalName);
                        if (value == null || value.equals("")) {
                            value = rolePrincipalName;
                        } else {
                            value = rolePrincipalName;
                        }
                        log.debug(value + "'s value is: " + value);
                        signMap.put(wfActName, value);
                    } else if (wfAct.getName().equals("外部会签")) {
                        String value = signMap.get(wfActName);
                        log.debug(wfActName + "'s value is: " + value);
                        log.debug(rolePrincipalName + "'s value is: " + rolePrincipalName);
                        if (value == null || value.equals("")) {
                            value = rolePrincipalName;
                        } else {
                            value = rolePrincipalName;
                        }
                        log.debug(value + "'s value is: " + value);
                        signMap.put(wfActName, value);
                    } else if (wfAct.getName().equals(Constants.ACTIVITYNAME_ZPGYHQ)||wfAct.getName().equals(Constants.TASK_ZHIPAIGONGYIHUIQIANBUMEN)) {
                        String value = signMap.get(wfActName);
                        log.debug(wfActName + "'s value is: " + value);
                        log.debug(rolePrincipalName + "'s value is: " + rolePrincipalName);
                        if (value == null || value.equals("")) {
                            value = rolePrincipalName;
                        } else {
                            value = rolePrincipalName;
                        }
                        log.debug(value + "'s value is: " + value);
                        signMap.put(wfActName, value);
                    } else if (wfAct.getName().equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||wfAct.getName().equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
                        String value = signMap.get(wfActName);
                        log.debug(wfActName + "'s value is: " + value);
                        log.debug(rolePrincipalName + "'s value is: " + rolePrincipalName);
                        if (value == null || value.equals("")) {
                            value = rolePrincipalName;
                        } else {
                            value = rolePrincipalName;
                        }
                        log.debug(value + "'s value is: " + value);
                        signMap.put(wfActName, value);
                    } else if (wfAct.getName().equals("工艺会签")) {
                        String value = signMap.get(wfActName);
                        log.debug(wfActName + "'s value is: " + value);
                        log.debug("rolePrincipalName 's value is: " + rolePrincipalName);

                        WfProcess process2 = wfAct.getParentProcess();
                        // 零部件签审流程和ECN流程需要对"工艺会签"作特殊处理
                        if (process2.getName().contains(Constants.WF_PART_APPROVAL)
                                || process2.getName().contains(Constants.WF_SJ_ECN)) {
                            // 过滤掉不是自己会签的对象
                            if (rolePrincipalName.contains(";")) {
                                String[] str = rolePrincipalName.split(";");
                                String tempStr = "";
                                for (String string : str) {
                                    for (String tempString : userNameList) {
                                        if (string.contains(tempString)) {
                                            if ("".equals(tempStr)) {
                                                tempStr = string;
                                            } else if(!value.contains(string)){
                                                tempStr = tempStr + ";" + string;
                                            }
                                        }
                                    }
                                }
                                if (value == null || value.equals("")) {
                                    value = tempStr;
                                } else {
                                    value = value+";"+tempStr;
                                }
                                signMap.put(wfActName, value);
                            } else {
                                for (String tempString : userNameList) {
                                    if (rolePrincipalName.contains(tempString)) {
                                        if (value == null || value.equals("")) {
                                            value = rolePrincipalName;
                                        } else if(!value.contains(tempString)){
                                            value = value+";"+rolePrincipalName;
                                        }
                                        log.debug("value is: " + value);
                                        signMap.put(wfActName, value);
                                    }
                                }
                            }
                        } else {
                            if (value == null || value.equals("")) {
                                value = rolePrincipalName;
                            } else if(!value.contains(rolePrincipalName)){
                                value = value+";"+rolePrincipalName;
                            }
                            signMap.put(wfActName, value);
                        }

                    } else if (wfAct.getName().equals("工艺会签汇总")) {
                        String value = signMap.get(wfActName);
                        log.debug(wfActName + "'s value is: " + value);
                        log.debug("rolePrincipalName 's value is: " + rolePrincipalName);
                        if (value == null || value.equals("")) {
                            value = rolePrincipalName;
                        } else {
                            value = rolePrincipalName;
                        }
                        log.debug("value is: " + value);
                        signMap.put(wfActName, value);
                    }
                }
            }
            qr2.reset();
        } while (true);
        return signMap;
    }

    // 获取指定工艺组长任务的签审信息
    public static Map<WTObject, List<ASESHuiqianSignature>> getSignatureByWTObject2(WfProcess process, Persistable pbo)
            throws Exception {
        Map<WTObject, List<ASESHuiqianSignature>> map = new HashMap<WTObject, List<ASESHuiqianSignature>>();
        Map<String, String> signMap = new HashMap<String, String>();
        ReferenceFactory rf = new ReferenceFactory();
        List activityList = new ArrayList();
        QueryResult qr = getWorkItems(process, false);// 取得流程所有任务节点
        while (qr.hasMoreElements()) {// 遍历任务
            WorkItem witem = (WorkItem) qr.nextElement();
            if (witem.getSource().getObject() instanceof WfAssignedActivity) {
                WfAssignedActivity wfactivity = (WfAssignedActivity) witem.getSource().getObject();
                activityList.add(wfactivity);
            }
        }
        WfBlock wfblock = getBlock(process);
        if (wfblock != null) {
            qr = getWorkItems(wfblock, false);// 取得流程所有任务节点
            while (qr.hasMoreElements()) {// 遍历任务
                WorkItem witem = (WorkItem) qr.nextElement();
                if (witem.getSource().getObject() instanceof WfAssignedActivity) {
                    WfAssignedActivity wfactivity = (WfAssignedActivity) witem.getSource().getObject();
                    activityList.add(wfactivity);
                }
            }
        }
        List<WTObject> memberList = null;
        if (pbo instanceof ProcessEnvelope) {
            memberList = ProcessEnvelopeUtil.getAllMembers((ProcessEnvelope) pbo);
        } else if (pbo instanceof ChangePackaged) {
            QueryResult qrr = PersistenceHelper.manager.navigate((ChangePackaged) pbo,
                    ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                    ChangePackagedResultLink.class, true);
            memberList = new ArrayList<WTObject>();
            while (qrr.hasMoreElements()) {
                Object object = qrr.nextElement();
                memberList.add((WTObject) object);

            }
        } else if (pbo instanceof WTChangeOrder2) {
            String objectType = "";
            try {
                objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // 添加作废申请单的判断
            if (objectType.indexOf("CHANGE_ECN") > -1) {
                memberList = ChangeHelper.getChangeResultItem((WTChangeOrder2) pbo);
            } else if (objectType.indexOf("ZUOFEI_ECN") > -1) {
                memberList = ChangeHelper.getChangeAffectItem((WTChangeOrder2) pbo);
            }
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof WTDocument) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof MPMProcessPlan) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        }
        for (int i = 0; memberList != null && i < memberList.size(); i++) {
            WTObject wto = memberList.get(i);
            if (!(wto instanceof WTPart)) {
                QueryResult qr2 = PersistenceHelper.manager.navigate(wto, SignLink.ROLE_BOBJECT_ROLE, SignLink.class,
                        true);// 取得对象所有审签信息
                Iterator iterator = activityList.iterator();
                do {
                    if (!iterator.hasNext()) {
                        break;
                    }
                    WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
                    List<ASESHuiqianSignature> tempList = new ArrayList<ASESHuiqianSignature>();
                    while (qr2.hasMoreElements()) {// 遍历审签信息
                        ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2.nextElement();
                        String tempActOid = tempSign.getActivity();
                        WfActivity wfAct = (WfActivity) rf.getReference(tempActOid).getObject();
                        String wfActName = wfAct.getName();
                        if (wfactivity.equals(wfAct)) {// 如果审签信息属于当前节点

                            if (wfAct.getName().equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||wfAct.getName().equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
                                tempList.add(tempSign);
                            }
                        }
                    }
                    int size = tempList.size();
                    List<ASESHuiqianSignature> tempList2 = new ArrayList<ASESHuiqianSignature>();
                    for (int n = 0; n < size; n++) {
                        ASESHuiqianSignature tempSignn = (ASESHuiqianSignature) tempList.get(n);
                    }
                    if (size > 1) {
                        tempList2.add(0, tempList.get(size - 1));
                        map.put(wto, tempList2);
                    } else {
                        map.put(wto, tempList);
                    }

                    qr2.reset();
                } while (true);
            }
        }
        return map;
    }

    public static WfBlock getBlock(WfProcess wfprocess) throws Exception {
        WfBlock wfblock = null;
        QuerySpec queryspec = new QuerySpec(WfRequesterActivity.class);
        queryspec.appendWhere(new SearchCondition(WfRequesterActivity.class, "parentProcessRef.key.id",SearchCondition.EQUAL, wfprocess.getPersistInfo().getObjectIdentifier().getId()), 0);
        QueryResult queryresult = PersistenceServerHelper.manager.query(queryspec);
        while (queryresult.hasMoreElements()) {
        	WfRequesterActivity ra = (WfRequesterActivity) queryresult.nextElement();
        	Object pr = ra.getPerformerRef().getObject();
        	if(pr instanceof WfBlock){
        		return (WfBlock)pr;
        	}
        }
        return wfblock;

    }

    public static QueryResult getWorkItems(WfBlock wfblock, boolean flag) throws Exception {
        QueryResult queryresult;
        QuerySpec queryspec = new QuerySpec();
        queryspec.setAdvancedQueryEnabled(true);
        int i = queryspec.appendClassList(WfAssignedActivity.class, false);
        queryspec.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", i, false);
        queryspec.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key", "=",
                getOid(wfblock)));
        SubSelectExpression subselectexpression = new SubSelectExpression(queryspec);
        QuerySpec queryspec1 = new QuerySpec(WorkItem.class);
        queryspec1.setAdvancedQueryEnabled(true);
        if (flag)
            queryspec1.appendWhere(new SearchCondition(WorkItem.class, "completedBy", !flag), 0);
        ClassAttribute classattribute = new ClassAttribute(WorkItem.class, "source.key.id");
        if (flag)
            queryspec1.appendAnd();
        SearchCondition searchcondition = new SearchCondition(classattribute, "IN", subselectexpression);
        queryspec1.appendWhere(searchcondition, 0);
        TableColumn ca = new TableColumn("A0", "createstampa2");
        OrderBy orderBy = new OrderBy(ca, false);
        queryspec1.appendOrderBy(orderBy, new int[0]);
        queryresult = PersistenceServerHelper.manager.query(queryspec1);
        return queryresult;

    }

    public static QueryResult getWorkItems(WfProcess wfprocess, boolean flag) throws Exception {
        QueryResult queryresult;
        QuerySpec queryspec = new QuerySpec();
        queryspec.setAdvancedQueryEnabled(true);
        int i = queryspec.appendClassList(WfAssignedActivity.class, false);
        queryspec.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", i, false);
        queryspec.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key", "=",
                getOid(wfprocess)));
        SubSelectExpression subselectexpression = new SubSelectExpression(queryspec);
        QuerySpec queryspec1 = new QuerySpec(WorkItem.class);
        queryspec1.setAdvancedQueryEnabled(true);
        if (flag)
            queryspec1.appendWhere(new SearchCondition(WorkItem.class, "completedBy", !flag), 0);
        ClassAttribute classattribute = new ClassAttribute(WorkItem.class, "source.key.id");
        if (flag)
            queryspec1.appendAnd();
        SearchCondition searchcondition = new SearchCondition(classattribute, "IN", subselectexpression);
        queryspec1.appendWhere(searchcondition, 0);
        TableColumn ca = new TableColumn("A0", "createstampa2");
        OrderBy orderBy = new OrderBy(ca, false);
        queryspec1.appendOrderBy(orderBy, new int[0]);
        queryresult = PersistenceServerHelper.manager.query(queryspec1);
        return queryresult;

    }

    private static ObjectIdentifier getOid(Object obj) {
        if (obj == null)
            return null;
        if (obj instanceof ObjectReference)
            return (ObjectIdentifier) ((ObjectReference) obj).getKey();
        else return PersistenceHelper.getObjectIdentifier((Persistable) obj);
    }

    public static WfProcess getProcess(WfActivity wfAct) {
        try {
            if (!RemoteMethodServer.ServerFlag) {
                return (WfProcess) RemoteMethodServer.getDefault().invoke("getProcessRemote",
                        SignatureHelper.class.getName(), null,
                        new Class[] { WfActivity.class },
                        new Object[] { wfAct });
            } else {
                return getProcessRemote(wfAct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WfProcess getProcessRemote(WfActivity wfAct) throws WTException {
        WfProcess wfprocess = wfAct.getParentProcess();
        return wfprocess;
    }

    public static Map<String, String> getAllReviewRecord(String workItemOid,
            String objOid) {

        ReferenceFactory rf = new ReferenceFactory();
        Map<String, String> recordMap = new HashMap<String, String>();
        try {

            Object obj = rf.getReference(objOid).getObject();
            if (obj instanceof ChangePackaged||obj instanceof ChangeRequest||obj instanceof PreviewObject) {
                getIterationReviewRecord((WTObject)obj, recordMap, workItemOid);
            }else{
                QueryResult qrVersion = VersionControlHelper.service
                        .allIterationsFrom((Iterated) obj);
                while (qrVersion.hasMoreElements()) {
                    WTObject wto = (WTObject) qrVersion
                            .nextElement();
                    getIterationReviewRecord(wto, recordMap, workItemOid);
                }
            }

        } catch (WTRuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return recordMap;

    }

    protected static void getIterationReviewRecord(
            WTObject obj, Map<String, String> recordMap, String workItemOid)
            throws WTException, WTPropertyVetoException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem item = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) item.getSource().getObject();
        WfProcess process = activity.getParentProcess();
        QueryResult qr2 = PersistenceHelper.manager.navigate(
                obj, SignLink.ROLE_BOBJECT_ROLE, SignLink.class,
                true);// 取得对象所有审签信息
        String conclusion = "";
        List<ASESHuiqianSignature> list = new ArrayList<ASESHuiqianSignature>();
        while (qr2.hasMoreElements()) {// 遍历审签信息
            ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2
                    .nextElement();
            String tempActOid = tempSign.getActivity();
            WfActivity wfAct = (WfActivity) rf.getReference(tempActOid)
                    .getObject();
            WfProcess process2 = wfAct.getParentProcess();
            if (process.equals(process2)) {
                list.add(tempSign);
            }
        }

        String zuzhang = getSignValue(list,Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);
        if(obj instanceof PreviewObject){
            conclusion = getSignValue(list, Constants.TASK_GONGYYUSHEN);
        }else {
            conclusion = getSignValue(list, "工艺会签");
        }
        String number = getNumber(obj);
        String name = getName(obj);
        String vi = getVersionIterationDisplay(obj);
        Persistable  pbo = item.getPrimaryBusinessObject().getObject();
        String state = "";
        String implement ="";
        if (pbo instanceof ChangePackaged) {
        	if(obj instanceof ChangePackaged ){
        		ChangePackaged change =(ChangePackaged)obj;
        		implement = change.getImplement();
            }else if (obj instanceof ChangeRequest ){
                ChangeRequest request =(ChangeRequest)obj;
                implement = request.getImplement();
            }else{
                QueryResult qr = PersistenceHelper.manager.navigate((ChangePackaged)pbo, "theRevisionControlled",
                        ChangePackagedResultLink.class, false);
                while (qr.hasMoreElements()) {
                    ChangePackagedResultLink link = (ChangePackagedResultLink) qr.nextElement();
                    Persistable roleB = link.getRoleBObject();
                    if (obj.equals(roleB)) {
                     state = link.getDescription();
                     implement = link.getImplementadvise();
                        break;
                    }
                }
            }

        }else if(pbo instanceof ProcessEnvelope){
            QueryResult qr = PersistenceHelper.manager.navigate((Persistable) obj, "theProcessEnvelope",
                    EnvelopeMemberLink.class, false);
            while (qr.hasMoreElements()) {
                EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
                Persistable roleA = link.getRoleAObject();
                if (pbo.equals(roleA)) {
                     state = link.getDescription();
                     implement = link.getImplementadvise();
                    break;
                }
            }

        }
        if (state==null) {
            state="";
        }
        if (implement==null) {
            implement="";
        }
        if (!zuzhang.equals("")) {
            if (!conclusion.equals("")) {
                conclusion = zuzhang+";;"+conclusion;
            }else{
                conclusion = zuzhang;
            }
        }
        String all = number + ";;;qqq" + name + ";;;qqq" + vi + ";;;qqq"+state + ";;;qqq"+implement + ";;;qqq"+ conclusion;
        recordMap.put(vi, all);
    }

    protected static String getSignValue(
            List<ASESHuiqianSignature> tempSignList, String activityName)
            throws WTException, WTPropertyVetoException {
        String value = "";
        ReferenceFactory rf = new ReferenceFactory();

        String tempValue = "";
        Hashtable ht = new Hashtable();
        for (int i = 0; i < tempSignList.size(); i++) {
            ASESHuiqianSignature tempSign = tempSignList.get(i);
            String tempActOid = tempSign.getActivity();
            WfActivity wfAct = (WfActivity) rf.getReference(tempActOid)
                    .getObject();
            if (wfAct.getName().equalsIgnoreCase(activityName)||wfAct.getName().equalsIgnoreCase(MQConstants.ACTIVITY_NAME_KRS)) {
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
        if (ht.size() > 0) {
            Enumeration enum1 = ht.keys();
            while (enum1.hasMoreElements()) {
                Object obj1 = enum1.nextElement();
                ASESHuiqianSignature tempSign = (ASESHuiqianSignature) ht
                        .get(obj1);

                String tempActOid = tempSign.getActivity();
                WfActivity wfAct = (WfActivity) rf.getReference(tempActOid)
                        .getObject();
                String name = wfAct.getName();
                String realConclusion = tempSign.getConclusion();

                String conclusion = "";
                if (conclusion != null) {
                    if (name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)
                            || name.equalsIgnoreCase("工艺会签")||name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
                        conclusion = realConclusion;
                    } else {
                        conclusion = realConclusion.substring(
                                realConclusion.indexOf(" ") + 1,
                                realConclusion.length());
                    }
                } else {
                    conclusion = "";
                }
                conclusion = conclusion.replace(", ", "");
                conclusion = conclusion.replace(",", "");
                String sig = tempSign.getSignature();
                if (sig == null) {
                    sig = "";
                }
                String opinion = tempSign.getOpinion();
                if (opinion == null) {
                    opinion = "";
                }
                if (tempValue.equals("")) {
                    if (sig == null || "".equals(sig)) {
                        tempValue = conclusion + ";" + opinion;
                    } else {
                        if (activityName.equals("外部会签")||activityName.equals("外部工艺会签")) {
                            tempValue = conclusion + ";" + sig + ";" + opinion;
                        }else{
                            tempValue = conclusion + ";" + opinion;
                        }
                    }
                } else {
                    if (sig == null || "".equals(sig)) {
                        tempValue = tempValue + "/" + conclusion + ";"
                                + opinion;
                    } else {
                        if (activityName.equals("外部会签")||activityName.equals("外部工艺会签")) {
                            tempValue = tempValue + "/" + conclusion + ";" + sig
                                    + ";" + opinion;
                        }else{
                            tempValue = tempValue + "/" + conclusion + ";"
                                    + opinion;
                        }

                    }
                }
                value = tempValue;
            }
        }

        return value;
    }

    protected static String getName(Object obj) {
        String name = null;
        if (obj != null)
            try {
                Class class1 = obj.getClass();
                Method method = class1.getMethod("getName", new Class[0]);
                name = (String) method.invoke(obj, new Object[0]);
            } catch (Exception exception) {
                Debug.info(obj.toString() + " has no name");
                exception.printStackTrace();
            }
        return name;
    }

    protected static String getNumber(Object obj) {
        String number = null;
        if (obj != null)
            try {
                Class class1 = obj.getClass();
                Method method = class1.getMethod("getNumber", new Class[0]);
                number = (String) method.invoke(obj, new Object[0]);
            } catch (Exception exception) {
                Debug.info(obj.toString() + " has no number");
                exception.printStackTrace();
            }
        return number;
    }

    protected static String getVersion(Object obj) {
        if ((obj instanceof Versioned)) {
            return ((Versioned) obj).getVersionIdentifier().getValue();
        }

        return null;
    }

    protected static String getIteration(Object obj) {
        if ((obj instanceof Iterated)) {
            return ((Iterated) obj).getIterationIdentifier().getValue();
        }

        return null;
    }

    protected static String getVersionIterationDisplay(Object obj) {
        if (obj == null)
            return "null";
        String version = getVersion(obj);
        if (version == null)
            version = "";
        String iteration = getIteration(obj);
        if (iteration == null)
            iteration = "";
        StringBuilder sb = new StringBuilder();
        sb.append(version);
        if (sb.length() != 0)
            sb.append(".");
        sb.append(iteration);
        return sb.toString();
    }


    /**
     * 指派工艺组长活动如果选择不组织会签，则由对象会签记录来判断路由，只要有不同意的会签结论则驳回
     *
     * @author liaojun
     * @param self
     *            流程引用
     * @param pbo
     *            主业务对象
     * @return flag true 为有不同意的结论
     * @throws WTException
     */
    public static boolean chooseRoute(WTReference self, WTObject pbo)
            throws WTException {
        boolean flag = false;
        ReferenceFactory rf = new wt.fc.ReferenceFactory();
        WfProcess process = (WfProcess) self.getObject();
        List<WTObject> memberList = null;
        if (pbo instanceof ProcessEnvelope) {
            memberList = ProcessEnvelopeUtil
                    .getAllMembers((ProcessEnvelope) pbo);
        } else if (pbo instanceof ChangeRequest) {
            memberList = new ArrayList<WTObject>();
            memberList.add(pbo);
        } else if (pbo instanceof ChangePackaged) {
            QueryResult qr = PersistenceHelper.manager.navigate(
                    (ChangePackaged) pbo,
                    ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                    ChangePackagedResultLink.class, true);
            memberList = new ArrayList<WTObject>();
            memberList.add(pbo);
            while (qr.hasMoreElements()) {
                Object object = qr.nextElement();
                memberList.add((WTObject) object);
            }
        } else if (pbo instanceof WTChangeOrder2) {
            memberList = ChangeHelper.getChangeResultItem((WTChangeOrder2) pbo);
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof WTDocument) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        } else if (pbo instanceof MPMProcessPlan) {
            memberList = new ArrayList<WTObject>();
            memberList.add((WTObject) pbo);
        }
        for (int i = 0; memberList != null && i < memberList.size(); i++) {
            WTObject wto = memberList.get(i);
            if (!(wto instanceof WTPart)) {
                QueryResult qr2 = PersistenceHelper.manager.navigate(wto,
                        SignLink.ROLE_BOBJECT_ROLE, SignLink.class, true);// 取得对象所有审签信息
                ASESHuiqianSignature sign = null;
                while (qr2.hasMoreElements()) {// 遍历审签信息
                    ASESHuiqianSignature tempSign = (ASESHuiqianSignature) qr2
                            .nextElement();
                    String tempActOid = tempSign.getActivity();
                    WfAssignedActivity wfAct  =  StandardDataReceiveService.getWfActivity(tempActOid);
                    if(wfAct==null){
                    	continue;
                    }
                    WfProcess sp = wfAct.getParentProcess();
                    if (sp.equals(process)) {
                        if (wfAct.getName().equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||wfAct.getName().equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
                            if (sign == null) {
                                sign = tempSign;
                            } else {
                                if (sign.getCreateTimestamp().before(
                                        tempSign.getCreateTimestamp())) {
                                    sign = tempSign;
                                }
                            }
                        }
                    }
                }
                if (sign != null) {
                    String conclusion = sign.getConclusion();
                    if (conclusion != null) {
                        if (conclusion.indexOf("不同意") >= 0) {
                            flag = true;
                            // 只要有不同意的会签结论则驳回
                            break;
                        }
                    }
                }


            }
		}
		return flag;
    }

    public static void setPreviewImplementAdvise(String workItemOid, String wtObjectOids) throws WTRuntimeException, WTException, WTPropertyVetoException {
        if(wtObjectOids == null || "".equals(wtObjectOids)) {
            return;
        }
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        String activityOid = PersistenceHelper.getObjectIdentifier(activity).toString();
        String wtObjectOidArray[] = wtObjectOids.split(";;;ppp");
        WTPrincipal wtp = SessionHelper.getPrincipal();
        String userName = ((WTUser) wtp).getFullName();
        userName = userName.replace(", ", "");
        userName = userName.replace(",", "");
        Date date = new Date();
        for(int i = 0; i < wtObjectOidArray.length; i++) {
            String signValueArray[] = wtObjectOidArray[i].split(";;;qqq");
            String tempOid = signValueArray[0];
            String tempSelect = signValueArray[1];
            if(!tempSelect.equalsIgnoreCase("无需会签")) {
                String result = userName + " " + tempSelect;
                String time = WTStandardDateFormat.format(date, "yyyy-MM-dd");
                String message = userName + "/" + "149厂" + "/" + time;

                String advise;
                if(signValueArray.length > 2) {
                    advise = signValueArray[2];
                } else {
                    advise = "";
                }
                WTObject obj = (WTObject) rf.getReference(tempOid).getObject();
                ASESHuiqianSignature tempSign = new ASESHuiqianSignature();
                tempSign.setActivity(activityOid);
                tempSign.setConclusion(result);
                tempSign.setOpinion(advise);
                tempSign.setSignature(message);
                tempSign.setImplementadvise("");
                tempSign.setUpdatestate("");
                tempSign = (ASESHuiqianSignature) PersistenceHelper.manager.save(tempSign);
                SignLink sl = SignLink.newSignLink(obj, tempSign);
                PersistenceHelper.manager.save(sl);
            }
        }
    }

    public static void zpPreviewGYZZ(String workItemOid, String wtObjectOids) throws WTRuntimeException {
        if(wtObjectOids == null || "".equals(wtObjectOids)) {
            return;
        }
        try {
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
            WfActivity activity = (WfActivity) wi.getSource().getObject();
            String wtObjectOidArray[] = wtObjectOids.split(";;;ppp");
            WTPrincipal wtp = SessionHelper.getPrincipal();
            Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");

            WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
            WfProcess process = null;
            if(wfcont instanceof WfBlock) {
                WfBlock wfBlock = (WfBlock) wfcont;
                process = wfBlock.getParentProcess();
            } else {
                process = (WfProcess) wfcont;
            }
            String newVersion = activity.getTripCount() + "";
            WTProperties props = WTProperties.getLocalProperties();
            String tempFolder = props.getProperty("wt.temp");
            String filePath = "";
            String fileName = "";
            String uuid = java.util.UUID.randomUUID().toString();
            fileName = "signature_emps.xml";

            String path = tempFolder + File.separator + uuid;
            File pathFile = new File(path);
            if(!pathFile.exists()) {
                pathFile.mkdirs();
            }
            filePath = tempFolder + File.separator + uuid + File.separator + "signature_emps.xml";
            Element root = DocumentHelper.createElement("root");
            root.addAttribute("version", newVersion);
            Document document = DocumentHelper.createDocument(root);
            Map<String, WTUser> users = new HashMap<String, WTUser>();
            for(int i = 0; i < wtObjectOidArray.length; i++) {
                String signValueArray[] = wtObjectOidArray[i].split(";;;qqq");
                String tempOid = "";
                if(signValueArray.length > 0) {
                    tempOid = signValueArray[0];
                }
                String hqPersons = "";
                String finalhqPersons = "";
                if(signValueArray.length > 3) {
                    hqPersons = signValueArray[3];
                }
                String zhuzhichejian = "";
                if(signValueArray.length > 4) {
                    zhuzhichejian = signValueArray[4];
                }
                String fuzhichejian = "";
                if(signValueArray.length > 5) {
                    fuzhichejian = signValueArray[5];
                }

                if(hqPersons != null && !"".equals(hqPersons)) {
                    String[] hqPerson = hqPersons.split(";");
                    for(String sp : hqPerson) {
                        if(!"".equals(sp.trim()) && sp.contains("-")) {
                            try {
                                String[] sp2s = sp.split("-");
                                WTUser user = (WTUser) rf.getReference(sp2s[1]).getObject();
                                finalhqPersons = finalhqPersons + sp2s[1] + ";";
                                users.put(sp2s[0], user);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                tempOid = tempOid.replaceAll(">", ":");
                Element element = root.addElement("EMPHQZZ");
                element.addAttribute("oid", tempOid);
                element.addAttribute("approver", finalhqPersons);
                element.addAttribute("zpr", wtp.getPersistInfo().getObjectIdentifier().toString());
                element.addAttribute("approverDisplay", finalhqPersons);
                element.addAttribute("zhuzhichejian", zhuzhichejian);
                element.addAttribute("fuzhichejian", fuzhichejian);
                element.addAttribute("version", newVersion);
                element.addAttribute("reassign", "");
            }
            File file = new File(filePath);
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file));
            xmlWriter.write(document);
            xmlWriter.close();
            SignatureService.saveTeamRole("ZHIPAIGONGYIYUANZHE", users, process, true);
            SignatureService.genPBOAttachments((ContentHolder) pbo, fileName, filePath);
        } catch(Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static boolean isShowSignature(Object obj) {
        if(obj != null){
            if(obj instanceof WTPart ){
                try {
                    String ctype = IBAHelper.getIBAStringValue((WTObject) obj,"CTYPE");
                    if(TypeIdentifierHelper.getType(obj).toString().endsWith(FaCiBomHelper.FACI_BOM_TYPE)||"备料".equals(ctype)){
                        return true;
                    }
                } catch (WTException e) {
                    e.printStackTrace();
                }
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
