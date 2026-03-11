package ext.casc.ixb;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import com.ptc.wpcfg.utilities.PrincipalHelper;
import org.apache.soap.SOAPException;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTValuedHashMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.team.Team;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;

import ext.casc.synch.SoapCall;
import ext.casc.util.IBAHelper;

public class DataPreviewProcess implements RemoteAccess {

    private static final String TYPE_PREVIEW_FORM = "PREVIEWFORM";
    private static final String ROLE_GONGYIYUAN = "GONGYIHUIQIANZHE";
    private static final String WORKFLOW_TEMPLATE = "149设计方案工艺审查流程";
    private static final String FOLDER_PATH = "/Default/08外来文件/01设计文件工艺审查单";

    /**
     * 创建设计方案预审单
     * @param fileName 预审单压缩包文件名称
     * @param params 预审单属性参数
     * @throws WTException
     * @throws RemoteException
     * @throws InvocationTargetException
     */
    public static void createPreviewForm(String fileName, HashMap params) throws WTException, RemoteException,
            InvocationTargetException {

        if (!RemoteMethodServer.ServerFlag) {
            RemoteMethodServer.getDefault().invoke(
                    "createPreviewForm",
                    DataPreviewProcess.class.getName(),
                    null,
                    new Class[] { String.class, HashMap.class },
                    new Object[] { fileName, params });
        }

        String name = (String) params.get(IXBConstants.OBJECT_NAME);
        String productName = (String) params.get(IXBConstants.PRODUCT_NAME);
        String previewReason = (String) params.get(IXBConstants.PREVIEW_REASON);
        String designer = (String) params.get(IXBConstants.DESIGNER);
        String previewUser = (String) params.get(IXBConstants.PREVIEWUSER);
        String wfActivityOid = (String) params.get(IXBConstants.WFACTIVITY_OID);

        WTDocument doc = WTDocument.newWTDocument();
        TypeDefinitionReference typeDefinitionRef = TypedUtility.getTypeDefinitionReference(IXBUtil.getDomainValue()+"."+TYPE_PREVIEW_FORM);
        ReferenceFactory rf = new ReferenceFactory();
        PDMLinkProduct product = getProductByName(productName);
        WTContainerRef containerRef = (WTContainerRef) rf
                    .getReference(product);
        if (product == null) {
            throw new WTException("Please create product");
        }
        try {
            doc.setName(name);
            doc.setContainerReference(containerRef);
            doc.setTypeDefinitionReference(typeDefinitionRef);

            Folder location = null;
            try {
                location = FolderHelper.service.getFolder(FOLDER_PATH,
                            containerRef);
            } catch (Exception e) {
                location = null;
            }
            if (location == null)
                location = FolderHelper.service.saveFolderPath(
                        FOLDER_PATH, containerRef);
            if (location != null) {
                WTValuedHashMap map = new WTValuedHashMap();
                map.put(doc, location);
                FolderHelper.assignLocations(map);
            }

            LifeCycleTemplate lCycleTemplate = LifeCycleHelper.service.getLifeCycleTemplate("149_DesignPlanPreReview_LC", containerRef);
            if(lCycleTemplate != null){
                doc = (WTDocument)LifeCycleHelper.setLifeCycle(doc, lCycleTemplate);
            }

            doc = (WTDocument) PersistenceHelper.manager.save(doc);
            doc = (WTDocument) PersistenceHelper.manager.refresh(doc);

            IBAHelper.setIBAStringValue(doc, "PreviewReason", previewReason);
            IBAHelper.setIBAStringValue(doc, "DESIGNER", designer);
            IBAHelper.setIBAStringValue(doc, "COMPANY", "805");

            ApplicationData appData = ApplicationData
                        .newApplicationData(doc);
            appData.setRole(ContentRoleType.PRIMARY);
            ContentServerHelper.service.updateContent(doc, appData,
                        fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        startWorkflowRemote(WORKFLOW_TEMPLATE, doc, wfActivityOid, previewUser);

    }

    /**
     * 根据产品名称获取对应的产品
     * @param productName
     * @return
     * @throws WTException
     */
    public static PDMLinkProduct getProductByName(String productName) throws WTException {
        PDMLinkProduct product = null;
        QuerySpec queryspec = new QuerySpec(wt.pdmlink.PDMLinkProduct.class);
        SearchCondition searchcondition = new SearchCondition(
                PDMLinkProduct.class, "containerInfo.name", SearchCondition.EQUAL, productName);
        queryspec.appendWhere(searchcondition, 0);
        QueryResult qr = PersistenceHelper.manager
                .find(queryspec);

        if (qr.hasMoreElements()) {
            product = (PDMLinkProduct) qr.nextElement();
        }

        return product;
    }


    /**
     * 获取流程指定活动的备注信息
     * @param self 流程self变量
     * @param activityName 流程活动名称
     * @return
     * @throws WTException
     */
    public static String getUserComment(ObjectReference self, String activityName) throws WTException {
        String userComment = "";
        WfProcess wfp = (WfProcess) self.getObject();
        WTCollection votingCol = WfEngineHelper.service.getVotingEvents(
                wfp, null, null, null);

        for (Iterator it = votingCol.persistableIterator(); it.hasNext();) {
            WfVotingEventAudit audit = (WfVotingEventAudit) it.next();

            if (audit.getActivityName().equals(activityName)) {
                String comment = audit.getUserComment();
                String userName = audit.getAssigneeRef().getFullName();
                userComment += userName + ":" + comment + ";";
            }
        }

        System.out.println(userComment);

        return userComment;

    }

    public static String getUserComment(WfProcess wfp, String activityName) throws WTException {
        String userComment = "";
        WTCollection votingCol = WfEngineHelper.service.getVotingEvents(
                wfp, null, null, null);

        for (Iterator it = votingCol.persistableIterator(); it.hasNext();) {
            WfVotingEventAudit audit = (WfVotingEventAudit) it.next();

            if (audit.getActivityName().equals(activityName)) {
                userComment = audit.getUserComment();
            }
        }

        System.out.println(userComment);

        return userComment;

    }

    /**
     * 设置流程团队指定角色参与人员
     * @param wfProcess 流程对象
     * @param roleName 角色名称
     * @param userName 用户名称
     * @throws WTException
     */
    public static void setPrinciple2Role(WfProcess wfProcess, String roleName, String userName) throws WTException {
        Role role = Role.toRole(roleName);
        Team team = (Team) wfProcess.getTeamId().getObject();
        System.out.println(userName);
        String[]  name=userName.split(";");
        for (int i = 0; i < name.length; i++) {
        	if (!name.equals("")) {
        		WTPrincipal principal = (WTPrincipal) PrincipalHelper.getPrincipal(name[i]).getObject();
                team.addPrincipal(role, principal);
        	}
        }
        team = (Team) PersistenceHelper.manager.refresh(team);
        team = (Team) PersistenceHelper.manager.save(team);
    }

    /**
     * 在149厂PDM系统中根据流程模板，启动流程，并设置流程变量和参与人会员
     *
     * @param workFlowName
     *            流程模板名称
     * @param pbo
     *            流程关联主对象
     * @param workFlowOid
     *            805所设计方案预审流程oid
     * @param userName
     *            工艺会签人员名称
     * @throws RemoteException
     * @throws InvocationTargetException
     */
    public static void startWorkflowRemote(String workFlowName, Object pbo, String wfActivityOid, String userName)
            throws RemoteException,
            InvocationTargetException {

        if (!RemoteMethodServer.ServerFlag) {
            RemoteMethodServer.getDefault().invoke(
                    "startWorkflowRemote",
                    DataPreviewProcess.class.getName(), null,
                    new Class[] { String.class, Object.class, String.class, String.class },
                    new Object[] { workFlowName, pbo, wfActivityOid, userName });
        }
        WTContainerRef containerRef = null;
        long WORKFLOW_PRIORITY = 1;

        // boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
            containerRef = WTContainerHelper.service.getExchangeRef();

            if (pbo instanceof WTContained) {
                WTContained contained = (WTContained) pbo;
                containerRef = contained.getContainerReference();
            }

            WTProperties wtproperties = WTProperties.getLocalProperties();
            WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty(
                    "wt.lifecycle.defaultWfProcessPriority", "1"));

            WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
                    .getProcessDefinition(workFlowName);
            if (wfprocessdefinition == null) {
                return;
            }

            WfProcess wfprocess = null;
            wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null, containerRef);

            if (pbo != null)
                wfprocess.setName(workFlowName + " - " + (((WTObject) pbo).getIdentity()));

            ProcessData processdata = wfprocess.getContext();

            processdata.setValue("primaryBusinessObject", pbo);// 设置流程主对象
            processdata.setValue(IXBConstants.WFACTIVITY_OID, wfActivityOid); // 保存805所PDS中设计方案预审流程oid到149厂流程进程中
            setPrinciple2Role(wfprocess, ROLE_GONGYIYUAN, userName);// 设置805所设计师选择的工艺会签人员

            WfEngineHelper.service.startProcessImmediate(wfprocess,
                    processdata, WORKFLOW_PRIORITY);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    /**
     * 根据活动名称获取流程活动
     * @param wfprocess 流程对象
     * @param activity 活动名称
     * @return
     */
    public static WfAssignedActivity getWfAssignedActivity(WfProcess wfprocess,
            String activity) {
        if (wfprocess == null || activity == null
                || activity.trim().length() == 0) {
            return null;
        }
        try {
            QueryResult qr = wfprocess.getContainerNodes();
            while (qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof WfAssignedActivity) {
                    WfAssignedActivity wfaa = (WfAssignedActivity) obj;
                    if (activity.equals(wfaa.getName().trim())) {
                        return wfaa;
                    }
                }
            }
        } catch (WTException e) {
        }
        return null;
    }

    /**
     * 完成805所流程活动
     * @param wfActivityOid
     * @param route
     * @param comments
     */
    public static void completePreviewWorkflow(String wfActivityOid, String route, String comments) {
        HashMap inputparams = new HashMap();
        inputparams.put(IXBConstants.WFACTIVITY_OID, wfActivityOid);
        inputparams.put(IXBConstants.ROUTE, route);
        inputparams.put(IXBConstants.PREVIEW_COMMENT, comments);

        try {
            (new SoapCall()).previewCallRemoteServer("completePreviewWorkItem", inputparams);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SOAPException e) {
            // TODO Auto-generated catch blockatic
            e.printStackTrace();
        }
    }

    public static String getUserListByName(String userName) throws WTException, RemoteException,
            InvocationTargetException {
        if (!RemoteMethodServer.ServerFlag) {
            RemoteMethodServer.getDefault().invoke(
                    "getUserListByName",
                    DataPreviewProcess.class.getName(), null,
                    new Class[] { String.class },
                    new Object[] { userName });
        }
        String userListString = "";

        Enumeration usernames = OrganizationServicesHelper.manager.findLikeUser("fullName", userName + "*");
        if (!usernames.hasMoreElements())
            usernames = OrganizationServicesHelper.manager.findLikeUser("name", userName + "*");
        if (usernames != null) {
            while (usernames.hasMoreElements()) {
                WTUser curUser = (WTUser) usernames.nextElement();
                String name = curUser.getName();
                String fullName = curUser.getFullName();
                userListString += fullName + "(" + name + ")`";
            }
        }

        return userListString;
    }
}
