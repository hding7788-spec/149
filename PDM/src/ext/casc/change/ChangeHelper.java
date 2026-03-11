package ext.casc.change;

import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.netmarkets.work.workResource;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.resource.MPMResourceHelper;
import ext.casc.change.bean.GLEcnCollectData;
import ext.casc.common.util.CommonValuesUtil;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.util.DBUtil;
import ext.casc.util.GwIBAUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.version.VersionCommonHelper;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import wt.change2.*;
import wt.content.*;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.fc.collections.WTValuedHashMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleServerHelper;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServerHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class ChangeHelper implements RemoteAccess, Serializable {
    private static final long serialVersionUID = 7893374111354803286L;

    public static final String TYPE = "ECN_TYPE";
    private static final String RESOURCE = "com.ptc.netmarkets.work.workResource";

    /**
     * 给ecn增加受影响的工艺文件   add by hding 20121122
     * @param ecn
     * @param afterAffected
     * @return
     */
    public static WTChangeOrder2 addAffectedDataRemote(WTChangeOrder2 ecn, Vector afterAffected){
        WTChangeActivity2 ca = null;
        WTChangeActivity2 existedCa = null;
        boolean isModify = false;
        String defaultCAName = "EI41_CA__" + ecn.getNumber();
        HashMap caAttributes = new HashMap();
        Vector beforeAffected = new Vector();

        caAttributes.put(CSCChange.DESCRIPTION, "EI41 ECN DEFAULT CA");
        caAttributes.put(CSCChange.NEED_DATE, ecn.getNeedDate());

        //察看是否有存在系统创建的CA
        ArrayList<WTChangeActivity2> cAs = CSCChange.getReleatedCA(ecn, false);


        for (int i = 0; i < cAs.size(); i++) {
            existedCa = cAs.get(i);
            if(existedCa.getName().indexOf("EI41_CA__") > -1){      //该CA为系统创建的CA
                CSCChange.deleteCA(existedCa);                                                  //删除
            }
        }
        ca = CSCChange.createCA(defaultCAName, caAttributes, ecn);      //创建默认CA

        //得到受影响前数据
        for (int i = 0; i < afterAffected.size(); i++) {
            WTObject wtPostObject = (WTObject)afterAffected.get(i);
            WTObject wtPreObject = getPredecessorObject(wtPostObject);
            if(wtPreObject != null){
                beforeAffected.add(wtPreObject);
            }
        }

        if(ca != null){
            CSCChange.setCAAffectedData(ca, afterAffected);                                //受影响前
            //CSCChange.setCAResultItem(ca, afterAffected);                                   //受影响后

            //CSCDebug.outDebugInfo("Add Change Notice" + ecn.getNumber() + " - " + ecn.getName() + " Affected Data Successful!");
        }

        return ecn;
    }

    /**
     * 得到该物件的变更前的版本<br>
     *
     * @param wtobject
     * @return
     */
    public static WTObject getPredecessorObject(WTObject wtobject) {
        try {
            if (!RemoteMethodServer.ServerFlag) {
                return (WTObject) RemoteMethodServer.getDefault().invoke("getPredecessorObjectRemote",
                        ChangeHelper.class.getName(), null,
                        new Class[] { WTObject.class },
                        new Object[] { wtobject });
            } else {
                return getPredecessorObjectRemote(wtobject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WTObject getPredecessorObjectRemote(WTObject wtobject) throws WTRuntimeException,
            VersionControlException {
        WTObject result = null;
        if (wtobject instanceof WTDocument) {
            WTDocument postDoc = (WTDocument) wtobject;
            WTDocument preDoc = (WTDocument) VersionControlServerHelper.getControlBranch(postDoc).getBranchPoint()
                    .getObject();
            result = preDoc;
        } else if (wtobject instanceof WTPart) {
            WTPart postPart = (WTPart) wtobject;
            WTPart prePart = (WTPart) VersionControlServerHelper.getControlBranch(postPart).getBranchPoint()
                    .getObject();
            result = prePart;
        } else if (wtobject instanceof EPMDocument) {
            EPMDocument postEPMDoc = (EPMDocument) wtobject;
            EPMDocument preEPMDoc = (EPMDocument) VersionControlServerHelper.getControlBranch(postEPMDoc)
                    .getBranchPoint().getObject();
            result = preEPMDoc;
        }
        return result;
    }

    public static List<WTChangeRequest2> getChangeRequestByChangeIssue(ChangeIssueIfc ci) {// 通过问题报告查询变更请求
        List<WTChangeRequest2> result = new ArrayList<WTChangeRequest2>();
        QueryResult qr = new QueryResult();
        try {
            qr = ChangeHelper2.service.getChangeRequest(ci);
            while (qr.hasMoreElements()) {
                result.add((WTChangeRequest2) qr.nextElement());
            }
        } catch (ChangeException2 e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static List<WTChangeOrder2> getChangeOrderByChangeRequest(ChangeRequestIfc cr) {// 通过变更请求查询变更通告
        List<WTChangeOrder2> result = new ArrayList<WTChangeOrder2>();
        QueryResult qr = new QueryResult();
        try {
            qr = ChangeHelper2.service.getChangeOrders(cr);
            while (qr.hasMoreElements()) {
                result.add((WTChangeOrder2) qr.nextElement());
            }
        } catch (ChangeException2 e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static List<WTChangeActivity2> getChangeActivityByChangeOrder(ChangeOrderIfc co) {// 通过变更通告查询变更任务
        List<WTChangeActivity2> result = new ArrayList<WTChangeActivity2>();
        QueryResult qr = new QueryResult();
        try {
            qr = ChangeHelper2.service.getChangeActivities(co);
            while (qr.hasMoreElements()) {
                result.add((WTChangeActivity2) qr.nextElement());
            }
        } catch (ChangeException2 e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static WTChangeOrder2 getChangeOrderByChangeActivity(ChangeActivityIfc ca) {// 通过变更任务查询变更通告
        QueryResult qr = new QueryResult();
        try {
            qr = ChangeHelper2.service.getChangeOrder(ca);
            if (qr.hasMoreElements()) {
                return (WTChangeOrder2) qr.nextElement();
            }
        } catch (ChangeException2 e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isCreateChangeOrder(WTChangeRequest2 cr) {
        boolean flag = false;
        List coList = getChangeOrderByChangeRequest(cr);
        if (coList.size() > 0) {
            flag = true;
        }
        return flag;
    }

    public static boolean checkAssociateChangeRequestState(WTChangeIssue ci, String state) {
        boolean flag = true;
        List crList = getChangeRequestByChangeIssue(ci);
        if (crList == null || crList.size() == 0) {
            return false;
        }

        for (int i = 0; i < crList.size(); i++) {
            LifeCycleManaged lcm = (LifeCycleManaged) crList.get(i);
            State tempState = lcm.getLifeCycleState();
            String tempStateStr = tempState.toString();
            if (!tempStateStr.equals(state)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static boolean checkAssociateChangeOrderState(WTChangeRequest2 cr, String state) {
        boolean flag = true;
        List coList = getChangeOrderByChangeRequest(cr);
        if (coList == null || coList.size() == 0) {
            return false;
        }
        for (int i = 0; i < coList.size(); i++) {
            LifeCycleManaged lcm = (LifeCycleManaged) coList.get(i);
            State tempState = lcm.getLifeCycleState();
            String tempStateStr = tempState.toString();
            if (!tempStateStr.equals(state)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static boolean checkChangeActivityState(WTChangeOrder2 co, String state) {
        List list = getChangeActivityByChangeOrder(co);
        boolean flag = true;
        if (list == null || list.size() == 0) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {

            LifeCycleManaged ca = (LifeCycleManaged) list.get(i);
            State tempState = ca.getLifeCycleState();
            String tempStateStr = tempState.toString();
            if (!tempStateStr.equals(state)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static void setAssosiateChangeTaskState(WTChangeOrder2 co, String stateStr) {
        List list = getChangeActivityByChangeOrder(co);
        State state = null;
        state = State.toState(stateStr);

        for (int i = 0; i < list.size(); i++) {
            WTChangeActivity2 ca = (wt.change2.WTChangeActivity2) list.get(i);
            try {
                LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) ca, state);
            } catch (WTException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void checkHaveECNOrNot(Persistable persistable) throws WTException {
        if (persistable instanceof WTChangeRequest2) {
            try {
                QueryResult qr = ChangeHelper2.service.getChangeOrders((WTChangeRequest2) persistable);
                if (qr.size() == 0) {
                    throw new WTException(RESOURCE, workResource.COMPLETE_CREATECHANGEREQUEST_VALIDATION_ERROR, null);
                }
            } catch (Exception e) {
                throw new WTException(RESOURCE, workResource.COMPLETE_CREATECHANGENOTICE_VALIDATION_ERROR, null);
            }
        }
    }

    /**
     * 得到变更影响数据<br>
     * <br>
     *
     * @param ecn
     * @return
     * @throws WTException
     * @throws ChangeException2
     */
    public static List getChangeResultAllItem(WTChangeOrder2 ecn) throws ChangeException2, WTException {
        ArrayList<WTChangeActivity2> cAs = getReleatedCA(ecn, false);
        ArrayList resultItems = new ArrayList();
        Set<WTPart> partMember = new HashSet<WTPart>();
        Set<WTDocument> docMember = new HashSet<WTDocument>();
        Set<String> docNumbers = new HashSet<String>();
        Set<EPMDocument> cadMember = new HashSet<EPMDocument>();
        Set<String> cadNumbers = new HashSet<String>();
        Set<MPMProcessPlan> mpmplanMember = new HashSet<MPMProcessPlan>();
        for (int i = 0; i < cAs.size(); i++) {
            WTChangeActivity2 ca = cAs.get(i);
            QueryResult qr = ChangeHelper2.service.getChangeablesAfter(ca);
            while (qr.hasMoreElements()) {
                WTObject wtobject = (WTObject) qr.nextElement();
                if (wtobject instanceof WTPart) {
                    WTPart part = (WTPart)wtobject;
                    partMember.add(part);
                } else if (wtobject instanceof WTDocument) {
                    WTDocument document = (WTDocument)wtobject;
                    docMember.add(document);
                    docNumbers.add(document.getNumber());
                } else if (wtobject instanceof EPMDocument) {
                    EPMDocument epmDocument = (EPMDocument)wtobject;
                    cadMember.add(epmDocument);
                    cadNumbers.add(epmDocument.getNumber());
                }else if (wtobject instanceof MPMProcessPlan) {
                    MPMProcessPlan plan = (MPMProcessPlan)wtobject;
                    mpmplanMember.add(plan);
                }
            }
        }
        resultItems.add(partMember);
        resultItems.add(docMember);
        resultItems.add(docNumbers);
        resultItems.add(cadMember);
        resultItems.add(cadNumbers);
        resultItems.add(mpmplanMember);
        return resultItems;
    }

    /**
     * 得到变更影响数据<br>
     * <br>
     *
     * @param ecn
     * @return
     * @throws WTException
     * @throws ChangeException2
     */
    public static List getChangeResultAllItemNoPart(WTChangeOrder2 ecn) throws ChangeException2, WTException {
        ArrayList<WTChangeActivity2> cAs = getReleatedCA(ecn, false);
        ArrayList resultItems = new ArrayList();
        for (int i = 0; i < cAs.size(); i++) {
            WTChangeActivity2 ca = cAs.get(i);
            QueryResult qr = ChangeHelper2.service.getChangeablesAfter(ca);
            while (qr.hasMoreElements()) {
                WTObject wtobject = (WTObject) qr.nextElement();
                if (!(wtobject instanceof WTPart)) {
                    resultItems.add(wtobject);
                }
            }
        }
        return resultItems;
    }

    /**
     * 得到变更影响数据<br>
     * <br>
     *
     * @param ecn
     * @return
     * @throws WTException
     * @throws ChangeException2
     */
    public static List getChangeResultDocItem(WTChangeOrder2 ecn) throws ChangeException2, WTException {
        ArrayList<WTChangeActivity2> cAs = getReleatedCA(ecn, false);
        List<String> resultItems = new ArrayList<String>();
        for (int i = 0; i < cAs.size(); i++) {
            WTChangeActivity2 ca = cAs.get(i);
            QueryResult qr = ChangeHelper2.service.getChangeablesAfter(ca);
            while (qr.hasMoreElements()) {
                WTObject wtobject = (WTObject) qr.nextElement();
                if (wtobject instanceof WTDocument) {
                    WTDocument doc = (WTDocument)wtobject;
                    resultItems.add(doc.getNumber());
                }
            }
        }
        return resultItems;
    }

    /**
     * 得到变更影响数据<br>
     * <br>
     *
     * @param ecn
     * @return
     * @throws WTException
     * @throws ChangeException2
     */
    public static List getChangeResultCadItem(WTChangeOrder2 ecn) throws ChangeException2, WTException {
        ArrayList<WTChangeActivity2> cAs = getReleatedCA(ecn, false);
        List<String> resultItems = new ArrayList<String>();
        for (int i = 0; i < cAs.size(); i++) {
            WTChangeActivity2 ca = cAs.get(i);
            QueryResult qr = ChangeHelper2.service.getChangeablesAfter(ca);
            while (qr.hasMoreElements()) {
                WTObject wtobject = (WTObject) qr.nextElement();
                if (wtobject instanceof EPMDocument) {
                    EPMDocument cadDocument = (EPMDocument)wtobject;
                    resultItems.add(cadDocument.getNumber());
                }
            }
        }
        return resultItems;
    }

    /**
     * 得到变更影响数据<br>
     * <br>
     *
     * @param ecn
     * @return
     */
    public static ArrayList getChangeResultItem(WTChangeOrder2 ecn) {
        try {
            if (!RemoteMethodServer.ServerFlag) {
                return (ArrayList) RemoteMethodServer.getDefault().invoke("getChangeResultItemRemote",
                        ChangeHelper.class.getName(), null,
                        new Class[] { WTChangeOrder2.class },
                        new Object[] { ecn });
            } else {
                return getChangeResultItemRemote(ecn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList getChangeResultItemRemote(WTChangeOrder2 ecn) {
        ArrayList<WTChangeActivity2> cAs = getReleatedCA(ecn, false);
        ArrayList resultItems = new ArrayList();

        for (int i = 0; i < cAs.size(); i++) {
            WTChangeActivity2 ca = cAs.get(i);
            resultItems = getCAResultItem(ca);
        }
        return resultItems;
    }

    /**
     * 得到变更影响数据<br>
     * <br>
     * @param ecn
     * @return
     */
    public static ArrayList getChangeAffectItem(WTChangeOrder2 ecn){
        try {
            if (!RemoteMethodServer.ServerFlag) {
                return (ArrayList) RemoteMethodServer.getDefault().invoke("getChangeAffectItemRemote", ChangeHelper.class.getName(), null,
                        new Class[] {WTChangeOrder2.class},
                        new Object[] {ecn});
            } else {
                return getChangeAffectItemRemote(ecn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList getChangeAffectItemRemote(WTChangeOrder2 ecn){
        ArrayList<WTChangeActivity2> cAs = getReleatedCA(ecn, false);
        ArrayList resultItems = new ArrayList();

        for (int i = 0; i < cAs.size(); i++) {
            WTChangeActivity2 ca = cAs.get(i);
            resultItems = getCAAffectedData(ca);
        }
        return resultItems;
    }

    public static ArrayList<WTObject> getCAAffectedData(WTChangeActivity2 ca){
        ArrayList<WTObject> result = new ArrayList<WTObject>();

        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QueryResult qr = ChangeHelper2.service.getChangeablesBefore(ca);
            while (qr.hasMoreElements()) {
                WTObject wtobject = (WTObject)qr.nextElement();
                result.add(wtobject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!"".equals(user)){
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }

        return result;
    }

    /**
     *
     * @param ecn
     * @param onlyNumber
     * @return
     */
    public static ArrayList getReleatedCA(WTChangeOrder2 ecn, boolean onlyNumber) {
        ArrayList list = new ArrayList();
        try {
            QueryResult qr = ChangeHelper2.service.getChangeActivities(ecn);
            while (qr.hasMoreElements()) {
                WTChangeActivity2 ca = (WTChangeActivity2) qr.nextElement();
                if (onlyNumber)
                    list.add(ca.getNumber());
                else list.add(ca);
            }
        } catch (Exception e) {
        }

        return list;
    }

    public static ArrayList<WTObject> getCAResultItem(WTChangeActivity2 ca) {
        ArrayList<WTObject> result = new ArrayList<WTObject>();

        String user = "";
        try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        try {
            QueryResult qr = ChangeHelper2.service.getChangeablesAfter(ca);
            while (qr.hasMoreElements()) {
                WTObject wtobject = (WTObject) qr.nextElement();
                result.add(wtobject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }

        return result;
    }

    /**
     * 得到变更请求影响数据<br>
     * <br>
     * @param ecn
     * @return
     */
    public static ArrayList getChangeRelatedItem(WTChangeRequest2 ecr){
        try {
            if (!RemoteMethodServer.ServerFlag) {
                return (ArrayList) RemoteMethodServer.getDefault().invoke("getChangeRelatedItemRemote", ChangeHelper.class.getName(), null,
                        new Class[] {WTChangeRequest2.class},
                        new Object[] {ecr});
            } else {
                return getChangeRelatedItemRemote(ecr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList getChangeRelatedItemRemote(WTChangeRequest2 ecr) throws ChangeException2, WTException{
        ArrayList resultItems = new ArrayList();

        QueryResult qr = ChangeHelper2.service.getChangeables(ecr);
        while (qr.hasMoreElements()) {
            WTObject wtobject = (WTObject)qr.nextElement();
            resultItems.add(wtobject);
        }

        return resultItems;
    }

    /**
     * 得到问题报告影响数据<br>
     * <br>
     * @param ecn
     * @return
     */
    public static ArrayList getChangeIssueItem(WTChangeIssue pr){
        try {
            if (!RemoteMethodServer.ServerFlag) {
                return (ArrayList) RemoteMethodServer.getDefault().invoke("getChangeIssueItemRemote", ChangeHelper.class.getName(), null,
                        new Class[] {WTChangeIssue.class},
                        new Object[] {pr});
            } else {
                return getChangeIssueItemRemote(pr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList getChangeIssueItemRemote(WTChangeIssue pr) throws ChangeException2, WTException{
        ArrayList resultItems = new ArrayList();

        QueryResult qr = ChangeHelper2.service.getChangeables(pr);
        while (qr.hasMoreElements()) {
            WTObject wtobject = (WTObject)qr.nextElement();
            resultItems.add(wtobject);
        }

        return resultItems;
    }
    public static void saveCollectDatas(String oid, String data){
        if(!Tools.isNull(data)){
            String docOid = oid;
            try {
                Persistable persistable =  PersistenceCommonHelper.getPersistable(oid);
                if(persistable instanceof MPMProcessPlan){
                    MPMProcessPlan pplan = (MPMProcessPlan)persistable;
                    Collection collection = MPMResourceHelper.service.getAssociatedDescribeDocuments(pplan);
                    Iterator iterator = collection.iterator();
                    while(iterator.hasNext()) {
                        ObjectReference oref = (ObjectReference) iterator.next();
                        WTDocument tmpdoc = (WTDocument) oref.getObject();
                        docOid = VersionCommonHelper.getVR(tmpdoc);
                    }
                }
            } catch (WTException e) {
                throw new RuntimeException(e);
            }
            DBUtil.deleteByTableAndKey("GLECNCOLLECTDATA","PARENTDOCOID",docOid);
            DBUtil.deleteByTableAndKey("GLECNCOLLECTDATA","CHILDDOCOID",docOid);
            String[] ss = data.split("\\$");
            int index = 1;
            for(String s:ss){
                if(!"".equals(s)){
                    GLEcnCollectData collectData = new GLEcnCollectData();
                    collectData.setKeyId(docOid+"_"+index);
                    collectData.setParentDocOid(docOid);
                    collectData.setChildDocOid(s);
                    if(docOid.equals(s)){
                        continue;
                    }
                    try {
                        CmPersistenceHelper.manager.insert(collectData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    index ++;
                }

            }
        }
    }

    public static void batchCreateWTChangeOrder(WTChangeOrder2 ecnOri)throws Exception{
        WTDocument beforeDoc = null;
        QueryResult qr = ChangeHelper2.service.getChangeablesBefore(ecnOri);
        while(qr.hasMoreElements()){
            Object beforeData = qr.nextElement();
            if(beforeData instanceof WTDocument){
                beforeDoc = (WTDocument) beforeData;
                break;
            }else if(beforeData instanceof MPMProcessPlan){
                MPMProcessPlan  pplan = (MPMProcessPlan) beforeData;
                Collection collection = MPMResourceHelper.service.getAssociatedDescribeDocuments(pplan);
                Iterator iterator = collection.iterator();
                while(iterator.hasNext()) {
                    ObjectReference oref = (ObjectReference) iterator.next();
                    WTDocument tmpdoc = (WTDocument) oref.getObject();
                    if (pplan.getNumber().equals(tmpdoc.getNumber())) {
                        beforeDoc = (WTDocument) VersionControlHelper.service.getLatestIteration(tmpdoc, true);
                        break;
                    }
                }
            }
        }
        if(beforeDoc!=null){
            String parentOid =  VersionCommonHelper.getVR(beforeDoc);
            CmQuerySpec qs = new CmQuerySpec(GLEcnCollectData.class);
            qs.appendWhere(GLEcnCollectData.PARENTDOCOID, CmQuerySpec.EQUAL, parentOid);
            CmQueryResult cmQueryResultqr = CmPersistenceHelper.manager.find(qs);
            List<String> filePaths = new ArrayList<String>();
            boolean isDownload = false;
            while (cmQueryResultqr.hasNext()) {
                GLEcnCollectData data =  (GLEcnCollectData) cmQueryResultqr.next();
                if(!isDownload){
                    downloadFiles(filePaths,ecnOri);
                    isDownload = true;
                }
                doCreateChangeOrder(ecnOri,data,filePaths);
            }
            DBUtil.deleteByTableAndKey("GLECNCOLLECTDATA","PARENTDOCOID",parentOid);
        }

    }

    private static void downloadFiles(List<String> filePaths,WTChangeOrder2 ecn) {
        FileOutputStream fos  = null;
        try {
            String filePath  = CommonValuesUtil.TEMP_FOLDER+ File.separator+"batchCreateEcn"+File.separator+ecn.getNumber();
            File file = new File(filePath);
            if(!file.exists()){
                file.mkdirs();
            }
            wt.content.ContentHolder contentHolder = ContentHelper.service.getContents(ecn);
            Vector apps = ContentHelper.getApplicationData(contentHolder);
            for (int j = 0; j < apps.size(); j++) {
                ApplicationData applicationdata = (ApplicationData) apps.elementAt(j);
                String role = applicationdata.getRole().toString();
                String fileName = applicationdata.getFileName();
                if ("SECONDARY".equalsIgnoreCase(role)&&!"PDFPreview.pdf".equals(fileName)) {
                    byte[] bytes = WTDocumentUtil.applicationDataToByte(applicationdata);
                    String downloadFile = filePath + java.io.File.separator + fileName;
                    fos = new FileOutputStream(downloadFile);
                    fos.write(bytes);
                    fos.flush();
                    filePaths.add(downloadFile);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
              if(fos!=null){
                  try {
                      fos.close();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
        }
    }

    private static void doCreateChangeOrder(WTChangeOrder2 ecnOri, GLEcnCollectData data,List<String> filePaths) {
        WTUser currentuser = null;
        try {
            currentuser = (WTUser) SessionHelper.manager.getPrincipal();
            WTUser creator = (WTUser) ecnOri.getCreator().getObject();
            SessionHelper.manager.setPrincipal(creator.getAuthenticationName());

            WTDocument doc = (WTDocument) PersistenceCommonHelper.getPersistable(data.getChildDocOid());
            WTChangeOrder2 changeOrder = WTChangeOrder2.newWTChangeOrder2(ecnOri.getName());
            changeOrder.setChangeNoticeComplexity(ChangeNoticeComplexity.BASIC);
            TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference("wt.change2.WTChangeOrder2|casc.sast.149.PROCESS_ECN");
            changeOrder.setTypeDefinitionReference(tdr);
            WTContainer product = ecnOri.getContainer();
            WTContainerRef containerRef = WTContainerRef.newWTContainerRef(product);
            changeOrder.setContainerReference(containerRef);
            Folder location = ecnOri.getFolderingInfo().getFolder();
            if (location == null) {
                location = FolderHelper.service.saveFolderPath(
                        "/Default/02工艺文件/05工艺更改单", containerRef);
            }
            WTValuedHashMap map = new WTValuedHashMap();
            map.put(changeOrder, location);
            FolderHelper.assignLocations(map);
            changeOrder.setNeedDate(ecnOri.getNeedDate());
            changeOrder.setChangeNoticeComplexity(ecnOri.getChangeNoticeComplexity());

            changeOrder = (WTChangeOrder2) LifeCycleHelper.setLifeCycle(changeOrder, ecnOri.getLifeCycleTemplate());

            changeOrder = (WTChangeOrder2) LifeCycleServerHelper.setState(changeOrder, State.toState("INWORK"));

            changeOrder = (WTChangeOrder2) PersistenceHelper.manager.save(changeOrder);

            //GwIBAUtil.copyIBAValues(ecnOri,changeOrder);

            Map<String, Object> copyIBAs = new HashMap<String, Object>();
            String phaseCode = IBAHelper.getIBAStringValue(doc, "PHASE_CODE");
            copyIBAs.put("PHASE_CODE", phaseCode);

            GwIBAUtil.copyIBAValues(ecnOri, changeOrder, copyIBAs);


            String typeId = "wt.change2.WTChangeActivity2";
            TypeDefinitionReference tdrECA = TypedUtility.getTypeDefinitionReference(typeId);
            WTChangeActivity2 changeActivity = WTChangeActivity2.newWTChangeActivity2(changeOrder.getName());
            changeActivity.setTypeDefinitionReference(tdrECA);
            changeActivity.setContainer(changeOrder.getContainer());
            //LifeCycleState lcs = LifeCycleState.newLifeCycleState();
            // LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate("Change Activity Life Cycle");
            //lcs.setLifeCycleId(lct.getLifeCycleTemplateReference());
            //changeActivity.setState(lcs);

            changeActivity = (WTChangeActivity2) ChangeHelper2.service.saveChangeActivity(changeOrder, changeActivity);

            AffectedActivityData affectedActivityData = AffectedActivityData.newAffectedActivityData(doc, changeActivity);
            affectedActivityData = (AffectedActivityData) PersistenceHelper.manager.save(affectedActivityData);

            uploadEcnAttachements(changeOrder,filePaths);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
            } catch (WTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static void uploadEcnAttachements(WTChangeOrder2 newEcn, List<String> filePaths) {
        try {
            for(String filePath:filePaths){
                    ApplicationData  appData = ApplicationData.newApplicationData(newEcn);
                    appData.setRole(ContentRoleType.SECONDARY);
                    ContentServerHelper.service.updateContent(newEcn, appData, filePath);
            }
           // newEcn = (WTChangeOrder2) PersistenceServerHelper.manager.restore(newEcn);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
