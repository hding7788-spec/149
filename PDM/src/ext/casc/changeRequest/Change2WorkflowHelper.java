package ext.casc.changeRequest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.glaway.mpm.constants.ProcessPlanConstants;
import com.glaway.mpm.util.DateUtil;
import com.glaway.mpm.util.FolderUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.glaway.mpm.util.WorkflowUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changepackaged.ChangePackagedUtil;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.changerequest.ChangeRequestAffectLink;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.ases.technotice.TechNoticeHelper;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.bean.AnalysisToSourceLink;
import ext.casc.analysisActivity.helper.AnalysisActivityHelper;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.common.PartCommonHelper;
import ext.casc.common.mvc.builder.analysis.RelatedAnalysisFileBuilder;
import ext.casc.constants.Constants;
import ext.casc.constants.PDMConfig;
import ext.casc.sop.util.SopUtil;
import ext.casc.util.CSCPrincipal;
import ext.casc.util.IBAHelper;
import ext.casc.workflow.PrintHelper;
import ext.casc.workflow.signtrue.zp.SignatureService;
import ext.sast.common.fc.CmPersistenceHelper;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.change2.*;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.*;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc._IterationInfo;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Change2WorkflowHelper {

    /**
     * 方法功能:流程 校验更改请求是手动创建还是自动创建
     * 自动创建直接批准创建更改影响分析
     *
     * @author cjh
     * @date 2024/3/1
     */
    public static boolean checkECRCreateType(WTObject pbo) {
        boolean flag = false;

        try {
            if(pbo instanceof WTChangeRequest2) {
                WTChangeRequest2 ecr = (WTChangeRequest2) pbo;
                if(Category.OTHER.equals(ecr.getCategory())) {
                    flag = true;
                }
            }
        } catch(WTRuntimeException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 方法功能:流程 自动创建更改请求
     *
     * @author cjh
     * @date 2024/3/4
     */
    public static void createWTChangeRequest2Auto(WTObject pbo, Object obj) {
        try {
            if(pbo instanceof WorkItem) {
                try {
                    pbo = (WTObject) ((WorkItem) pbo).getPrimaryBusinessObject().getObject();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            boolean isHas = Change2WorkflowHelper.isHasWorkingAnalysisActivity(pbo);
            if(isHas) {
                return;
            }
            WTUser zhurengongyishi = (WTUser) SessionHelper.getPrincipal();
            WTContainer container = null;
            WTContainerRef containerRef = null;
            String oid = "";
            String name = "";

            if(pbo instanceof ChangePackaged) {
                ChangePackaged packaged = (ChangePackaged) pbo;
                container = packaged.getContainer();
                containerRef = packaged.getContainerReference();
                oid = ChangePackaged.class.getName() + ":" + packaged.getPersistInfo().getObjectIdentifier().getId();
                name = packaged.getName();
            } else if(pbo instanceof ChangeRequest) {
                ChangeRequest cr = (ChangeRequest) pbo;
                container = cr.getContainer();
                containerRef = cr.getContainerReference();
                oid = ChangeRequest.class.getName() + ":" + cr.getPersistInfo().getObjectIdentifier().getId();
                name = cr.getName();
            } else if(pbo instanceof ProcessEnvelope) {
                ProcessEnvelope pe = (ProcessEnvelope) pbo;
                container = pe.getContainer();
                containerRef = pe.getContainerReference();
                oid = ProcessEnvelope.class.getName() + ":" + pe.getPersistInfo().getObjectIdentifier().getId();
                name = pe.getName();

                //如果是发放单 关联对象是偏离单 遍历对应偏离单所有的关联的签审包找到指派工艺组长者
                ArrayList<WTObject> members = ProcessEnvelopeUtil.getAllMembers(pe);
                for(WTObject member : members) {
                    if(member instanceof WTDocument){
                        WTDocument document = (WTDocument) member;
                        TypeIdentifier identifier = TypedUtility.getTypeIdentifier(document);
                        String typeName = identifier.getTypename();
                        if(typeName.contains("casc.sast.149.TECHNOTICE_DOC")) {
                            QueryResult qr = PersistenceHelper.manager.navigate(document, "theProcessEnvelope", EnvelopeMemberLink.class, false);
                            while(qr.hasMoreElements()) {
                                EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
                                ProcessEnvelope processEnvelope = link.getProcessEnvelope();
                                QueryResult processes = WfEngineHelper.service.getAssociatedProcesses(processEnvelope, null, null);
                                if(processes.hasMoreElements()){
                                    WfProcess process = (WfProcess) processes.nextElement();
                                    Role role = Role.toRole("ZHIPAIGONGYIZUZHANGZHE");
                                    Team team = (Team) process.getTeamId().getObject();
                                    Map map = team.getRolePrincipalMap();
                                    List tempUserList = (List) map.get(role);
                                    if(tempUserList != null && tempUserList.size() > 0){
                                        zhurengongyishi = (WTUser) ((WTPrincipalReference) tempUserList.get(0)).getObject();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //如果名称包含“发复材” 则不创建更改影响分析
            if(name.contains("发复材")) {
                return;
            }
            if(obj != null) {
                if(obj instanceof ObjectReference){
                    WfProcess process = (WfProcess) ((ObjectReference) obj).getObject();
                    Role role = Role.toRole("ZHIPAIGONGYIZUZHANGZHE");
                    Team team = (Team) process.getTeamId().getObject();
                    Map map = team.getRolePrincipalMap();
                    List tempUserList = (List) map.get(role);
                    if(tempUserList != null && tempUserList.size() > 0) {
                        zhurengongyishi = (WTUser) ((WTPrincipalReference) tempUserList.get(0)).getObject();
                    }
                } else if(obj instanceof WTUser) {
                    zhurengongyishi = (WTUser) obj;
                }
            }
            WTChangeRequest2 ecr = WTChangeRequest2.newWTChangeRequest2();
            TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference("casc.sast.149.PROCESS_ECR");
            ecr.setTypeDefinitionReference(typeRef);
            ecr.setName("CHANGE_REQUEST");
            ecr.setDescription(name);
            ecr.setCategory(Category.OTHER);
            ecr.setContainer(container);
            String folderPath = "Default/02工艺文件/20变更申请单/系统生成";
            Folder folder = FolderUtil.getFolder(folderPath, containerRef);
            if(folder == null) {
                try {
                    FolderHelper.service.saveFolderPath(folderPath, containerRef);
                } catch(Exception e) {
                }
            }
            FolderHelper.assignFolder(ecr, folder);
            VersionControlHelper.assignIterationCreator(ecr, WTPrincipalReference.newWTPrincipalReference(zhurengongyishi));
            ecr = (WTChangeRequest2) ChangeHelper2.service.saveChangeRequest(ecr);
            String numberString = "SYSAUTO-" + ecr.getNumber();
            WTChangeRequest2Master master = (WTChangeRequest2Master) ecr.getMaster();
            WTChangeRequest2MasterIdentity idy = (WTChangeRequest2MasterIdentity) master.getIdentificationObject();
            idy.setNumber(numberString);
            master = (WTChangeRequest2Master) IdentityHelper.service.changeIdentity(master, idy);
            IBAHelper.setIBAStringValue(ecr, "SECRET", "公开");
            IBAHelper.setIBAStringValue(ecr, "CHANGECAUSE", oid);
        } catch(WTException e) {
            e.printStackTrace();
        } catch(WTPropertyVetoException e) {
            e.printStackTrace();
        } catch(RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法功能:自动回收已经存在的处于执行更改阶段的更改影响分析任务
     *
     * @author cjh
     * @date 2024/3/4
     */
    public static boolean rejectWTAnalysisActivityAuto(WTObject object) {
        boolean isRejected = false;
        try {
            if(object != null) {
                QuerySpec qs = new QuerySpec(WTChangeRequest2.class);
                qs.setAdvancedQueryEnabled(true);
                qs.appendWhere(new SearchCondition(WTChangeRequest2.class, WTChangeRequest2.CATEGORY, SearchCondition.EQUAL, "OTHER"), new int[]{0});
                qs.appendAnd();
                ClassAttribute caId = new ClassAttribute(WTChangeRequest2.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
                String value = "";
                if(object instanceof ChangePackaged) {
                    value = ChangePackaged.class.getName() + ":" + object.getPersistInfo().getObjectIdentifier().getId();
                } else if(object instanceof ChangeRequest) {
                    value = ChangeRequest.class.getName() + ":" + object.getPersistInfo().getObjectIdentifier().getId();
                } else if(object instanceof ProcessEnvelope) {
                    value = ProcessEnvelope.class.getName() + ":" + object.getPersistInfo().getObjectIdentifier().getId();
                }
                SubSelectExpression subSelectExpression = SopUtil.getStringIBAQuery("CHANGECAUSE", value);
                qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), new int[]{0});
                QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
                qr = new LatestConfigSpec().process(qr);
                while(qr.hasMoreElements()) {
                    WTChangeRequest2 request2 = (WTChangeRequest2) qr.nextElement();
                    QueryResult result = ChangeHelper2.service.getLatestChangeProposal(request2);
                    if(result.hasMoreElements()) {
                        WTChangeProposal proposal = (WTChangeProposal) result.nextElement();
                        QueryResult queryResult = ChangeHelper2.service.getLatestAnalysisActivity(proposal);
                        if(queryResult.hasMoreElements()) {
                            WTAnalysisActivity analysisActivity = (WTAnalysisActivity) queryResult.nextElement();
                            if(!Constants.STATE_APPROVED.equals(analysisActivity.getLifeCycleState().toString())) {
                                isRejected = true;
                                QueryResult wfResult = WfEngineHelper.service.getAssociatedProcesses(analysisActivity, null, null);
                                if(wfResult.hasMoreElements()) {
                                    WfProcess process = (WfProcess) wfResult.nextElement();
                                    List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
                                    activityList = PrintHelper.getActivities(process, activityList);
                                    Iterator iterator = activityList.iterator();
                                    while(iterator.hasNext()) {
                                        WfAssignedActivity activity = (WfAssignedActivity) iterator.next();
                                        String wfactivityName = activity.getName();
                                        if(wfactivityName.equals("执行更改") && "OPEN_RUNNING".equals(activity.getState().toString())) {
                                            ArrayList<WorkItem> workItemList = WorkflowUtil.getWorkItemFromActivityAndState(activity, "POTENTIAL");
                                            if(workItemList.size() != 0) {
                                                for(WorkItem workitem : workItemList) {
                                                    ProcessData pd = activity.getContext();
                                                    ProcessData pdc = pd.copy();
                                                    if(pdc != null) {
                                                        pdc.setTaskComments("驳回");
                                                        workitem.setContext(pdc);
                                                        workitem = (WorkItem) PersistenceHelper.manager.save(workitem);
                                                    }
                                                    Vector vector = new Vector();
                                                    vector.addElement("驳回");
                                                    WorkflowHelper.service.workComplete(workitem, workitem.getOwnership().getOwner(), vector);
                                                    WfEventHelper.createVotingEvent(null, activity, workitem,
                                                            workitem.getOwnership().getOwner(), "驳回", vector, false, workitem.isRequired());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(QueryException e) {
            e.printStackTrace();
        } catch(WTException e) {
            e.printStackTrace();
        } catch(RemoteException e) {
            e.printStackTrace();
        }
        return isRejected;
    }

    /**
     * 方法功能:流程 更改请求批准后 创建更改影响分析
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static void createAnalysisActivity(WTObject object) {
        try {
            if(object instanceof WTChangeRequest2) {
                Persistable persistable = null;
                WTChangeRequest2 ecr = (WTChangeRequest2) object;
                List<RevisionControlled> list = new ArrayList();
                if(Category.OTHER.equals(ecr.getCategory())) {
                    String oid = IBAHelper.getIBAStringValue(ecr, "CHANGECAUSE");
                    if(StrUtil.isNotEmpty(oid)) {
                        ReferenceFactory rf = new ReferenceFactory();
                        WTReference rfReference = rf.getReference(oid);
                        if(rfReference != null) {
                            persistable = rfReference.getObject();
                            if(persistable instanceof ChangePackaged) {
                                ChangePackaged packaged = (ChangePackaged) persistable;
                                QueryResult qr = ChangePackagedUtil.getChangeAfterDataByChangePackaged(packaged);
                                Map<String, String> map = new HashMap<String, String>();
                                while(qr.hasMoreElements()) {
                                    Object obj = qr.nextElement();
                                    if(obj instanceof WTPart) {
                                        WTPart part = (WTPart) obj;
                                        if("Design".equals(part.getViewName())) {
                                            WTPart pbom = WTPartUtil.getLatestPartByNumberAndView(part.getNumber(), "Manufacturing");
                                            if(pbom != null) {
                                                part = pbom;
                                            }
                                        }
                                        map.put(WTPart.class.getName() + part.getNumber(), part.getNumber());
                                        list.add(part);
                                        List<WTDocument> documentList = PartCommonHelper.getDescribedByWTDocuments(part);
                                        for(WTDocument document : documentList) {
                                            String pplantype = IBAHelper.getIBAStringValue(document, "PPLANTYPE");
                                            if("正式工艺文件".equals(pplantype)) {
                                                list.add(document);
                                                map.put(WTDocument.class.getName() + document.getNumber(), document.getNumber());
                                            }
                                        }
                                    } else if(obj instanceof WTDocument) {
                                        WTDocument document = (WTDocument) obj;
                                        String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
                                        if(typeName.contains("PROCESS_PLAN")) {
                                            list.add(document);
                                            map.put(WTDocument.class.getName() + document.getNumber(), document.getNumber());
                                        } else {
                                            //根据文档编号查找相应的部件，若果有就加入
                                            WTPart part = WTPartUtil.getLatestPartByNumberAndView(document.getNumber(), "Manufacturing");
                                            if(part == null) {
                                                part = WTPartUtil.getLatestPartByNumberAndView(document.getNumber(), "Design");
                                            }
                                            if(part != null && !map.containsKey(WTPart.class.getName() + part.getNumber())) {
                                                list.add(part);
                                                map.put(WTPart.class.getName() + part.getNumber(), part.getNumber());
                                            }
                                        }
                                    }
                                }
                                qr = ChangePackagedUtil.getChangeBeforeDataByChangePackaged(packaged);
                                while(qr.hasMoreElements()) {
                                    Object obj = qr.nextElement();
                                    if(obj instanceof WTPart && !map.containsKey(WTPart.class.getName() + ((WTPart) obj).getNumber())) {
                                        WTPart part = (WTPart) obj;
                                        if("Design".equals(part.getViewName())) {
                                            WTPart pbom = WTPartUtil.getLatestPartByNumberAndView(part.getNumber(), "Manufacturing");
                                            if(pbom != null) {
                                                part = pbom;
                                            }
                                        }
                                        list.add(part);
                                        map.put(WTPart.class.getName() + part.getNumber(), part.getNumber());
                                        List<WTDocument> documentList = PartCommonHelper.getDescribedByWTDocuments(part);
                                        for(WTDocument document : documentList) {
                                            String pplantype = IBAHelper.getIBAStringValue(document, "PPLANTYPE");
                                            if("正式工艺文件".equals(pplantype)) {
                                                list.add(document);
                                                map.put(WTDocument.class.getName() + document.getNumber(), document.getNumber());
                                            }
                                        }
                                    } else if(obj instanceof WTDocument && !map.containsKey(WTDocument.class.getName() + ((WTDocument) obj).getNumber())) {
                                        WTDocument document = (WTDocument) obj;
                                        String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
                                        if(typeName.contains("PROCESS_PLAN")) {
                                            list.add(document);
                                            map.put(WTDocument.class.getName() + document.getNumber(), document.getNumber());
                                        } else {
                                            //根据文档编号查找相应的部件，若果有就加入
                                            WTPart part = WTPartUtil.getLatestPartByNumberAndView(document.getNumber(), "Manufacturing");
                                            if(part == null) {
                                                part = WTPartUtil.getLatestPartByNumberAndView(document.getNumber(), "Design");
                                            }
                                            if(part != null && !map.containsKey(WTPart.class.getName() + part.getNumber())) {
                                                list.add(part);
                                                map.put(WTPart.class.getName() + part.getNumber(), part.getNumber());
                                            }
                                        }
                                    }
                                }
                            } else if(persistable instanceof ChangeRequest) {
                                ChangeRequest cr = (ChangeRequest) persistable;
                                QueryResult qr = PersistenceHelper.manager.navigate(cr, "theRevisionControlled", ChangeRequestAffectLink.class, false);
                                while(qr.hasMoreElements()) {
                                    ChangeRequestAffectLink link = (ChangeRequestAffectLink) qr.nextElement();
                                    RevisionControlled controlled = link.getRevisionControlled();
                                    if(controlled instanceof WTPart) {
                                        WTPart part = (WTPart) controlled;
                                        if("Design".equals(part.getViewName())) {
                                            WTPart pbom = WTPartUtil.getLatestPartByNumberAndView(part.getNumber(), "Manufacturing");
                                            if(pbom != null) {
                                                part = pbom;
                                            }
                                        }
                                        list.add(part);
                                        List<WTDocument> documentList = PartCommonHelper.getDescribedByWTDocuments(part);
                                        for(WTDocument document : documentList) {
                                            String pplantype = IBAHelper.getIBAStringValue(document, "PPLANTYPE");
                                            if("正式工艺文件".equals(pplantype)) {
                                                list.add(document);
                                            }
                                        }
                                    } else if(controlled instanceof WTDocument) {
                                        WTDocument document = (WTDocument) controlled;
                                        String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
                                        if(typeName.contains("PROCESS_PLAN")) {
                                            list.add(document);
                                        } else {
                                            WTPart part = WTPartUtil.getLatestPartByNumberAndView(document.getNumber(), "Manufacturing");
                                            if(part == null) {
                                                part = WTPartUtil.getLatestPartByNumberAndView(document.getNumber(), "Design");
                                            }
                                            if(part != null) {
                                                list.add(part);
                                            }
                                        }
                                    }
                                }
                            } else if(persistable instanceof ProcessEnvelope) {
                                ProcessEnvelope pe = (ProcessEnvelope) persistable;
                                ArrayList<WTObject> members = ProcessEnvelopeUtil.getAllMembers(pe);
                                for(WTObject obj : members) {
                                    if(obj instanceof WTDocument) {
                                        WTDocument document = (WTDocument) obj;
                                        TypeIdentifier identifier = TypedUtility.getTypeIdentifier(document);
                                        String typeName = identifier.getTypename();
                                        if(typeName.contains("casc.sast.149.TECHNOTICE_DOC")) {
                                            ArrayList before = TechNoticeHelper.service.getTechNoticeBeforeMembers(document);
                                            for(Object o : before) {
                                                if(o instanceof WTDocument) {
                                                    WTDocument doc = (WTDocument) o;
                                                    WTPart part = WTPartUtil.getLatestPartByNumberAndView(doc.getNumber(), "Manufacturing");
                                                    if(part == null) {
                                                        part = WTPartUtil.getLatestPartByNumberAndView(doc.getNumber(), "Design");
                                                    }
                                                    if(part != null) {
                                                        list.add(part);
                                                    }
                                                }
                                            }
                                            ArrayList after = TechNoticeHelper.service.getTechNoticeAfterMembers(document);
                                            for(Object o : after) {
                                                if(o instanceof WTPart) {
                                                    list.add((WTPart) o);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    persistable = ecr;
                }
                WTAnalysisActivity activity = WTAnalysisActivity.newWTAnalysisActivity();
                activity.setContainer(ecr.getContainer());
                if(Category.OTHER.equals(ecr.getCategory()) && StrUtil.isNotEmpty(ecr.getDescription())) {
                    activity.setName("更改影响分析(" + ecr.getDescription() + ")");
                } else {
                    activity.setName("更改影响分析(" + ecr.getName() + ")");
                }
                WTChangeProposal proposal = WTChangeProposal.newWTChangeProposal();
                proposal.setContainer(ecr.getContainer());
                proposal.setName("CHANGE PROPOSAL");
                proposal = (WTChangeProposal) ChangeHelper2.service.saveChangeProposal(ecr, proposal);
                activity = (WTAnalysisActivity) ChangeHelper2.service.saveAnalysisActivity(proposal, activity);
                String ecrNumber = ecr.getNumber();
                if(ecrNumber.contains("SYSAUTO-")) {
                    ecrNumber = ecrNumber.substring(8);
                }
                String number = "IA-" + ecrNumber;
                WTAnalysisActivity activityByNumber = AnalysisUtil.getWTAnalysisActivityByNumber(number);
                if(activityByNumber != null) {
                    number = "IA-" + activity.getNumber();
                }
                WTAnalysisActivityMaster master = (WTAnalysisActivityMaster) activity.getMaster();
                WTAnalysisActivityMasterIdentity idy = (WTAnalysisActivityMasterIdentity) master.getIdentificationObject();
                idy.setNumber(number);
                IdentityHelper.service.changeIdentity(master, idy);
                IBAHelper.setIBAStringValue(activity, "SECRET", IBAHelper.getIBAStringValue(ecr, "SECRET"));

                try {
                    WTUser user = (WTUser) ecr.getCreator().getObject();
                    WTPrincipalReference ref = WTPrincipalReference.newWTPrincipalReference(user);
                    Class[] pp = new Class[]{WTPrincipalReference.class};
                    //修改创建者
                    Method setCreator = _IterationInfo.class.getDeclaredMethod("setCreator", pp);
                    setCreator.setAccessible(true);
                    setCreator.invoke(activity.getIterationInfo(), new Object[]{ref});
                    //修改修改者
                    Method setModifier = _IterationInfo.class.getDeclaredMethod("setModifier", pp);
                    setModifier.setAccessible(true);
                    setModifier.invoke(activity.getIterationInfo(), new Object[]{ref});
                    PersistenceServerHelper.manager.update(activity);
                } catch(Exception e) {
                    e.printStackTrace();
                }

                if(CollUtil.isNotEmpty(list)) {
                    for(RevisionControlled controlled : list) {
                        try {
                            String verOid = AnalysisUtil.getVerOidByObject(controlled);
                            List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(number, verOid, null);
                            for(AnalysisObjEntry entry : entries) {
                                CmPersistenceHelper.manager.delete(entry);
                            }
                            if(controlled instanceof WTPart) {
                                AnalysisObjEntry pbom = new AnalysisObjEntry(number, verOid, AnalysisConstant.TYPE_PBOM);
                                CmPersistenceHelper.manager.save(pbom);
                                AnalysisObjEntry zaizhipin = new AnalysisObjEntry(number, verOid, AnalysisConstant.TYPE_ZAIZHIPIN);
                                CmPersistenceHelper.manager.save(zaizhipin);
                                AnalysisObjEntry yizhipin = new AnalysisObjEntry(number, verOid, AnalysisConstant.TYPE_YIZHIPIN);
                                CmPersistenceHelper.manager.save(yizhipin);
                            } else if(controlled instanceof WTDocument) {
                                WTDocument document = (WTDocument) controlled;
                                if(ProcessPlanConstants.LIFECYCLE_OBSOLESCENCE.equals(document.getState().getState().getDisplay(Locale.CHINA))){
                                    continue;
                                }
                                AnalysisObjEntry technics = new AnalysisObjEntry(number, verOid, AnalysisConstant.TYPE_TECHNICS);
                                CmPersistenceHelper.manager.save(technics);
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                //创建影响分析和影响源的关联关系
                if(persistable != null && persistable instanceof WTObject) {
                    AnalysisToSourceLink link = AnalysisToSourceLink.newAnalysisToSourceLink(activity, (WTObject) persistable);
                    if(persistable instanceof ProcessEnvelope) {
                        ProcessEnvelope pe = (ProcessEnvelope) persistable;
                        ArrayList members = ProcessEnvelopeUtil.getAllMembers(pe);
                        for(Object member : members) {
                            if(member instanceof WTDocument) {
                                WTDocument document = (WTDocument) member;
                                TypeIdentifier identifier = TypedUtility.getTypeIdentifier(document);
                                String typeName = identifier.getTypename();
                                if(typeName.contains("casc.sast.149.TECHNOTICE_DOC")) {
                                    link.setPlNumber(document.getNumber());
                                }
                            }
                        }
                    }
                    PersistenceHelper.manager.save(link);
                    //记录已创建影响分析字段
                    IBAHelper.setIBAStringValue((WTObject) persistable, "hasAnalysis", "已创建");
                }
            }
        } catch(WTException e) {
            e.printStackTrace();
        } catch(WTPropertyVetoException e) {
            e.printStackTrace();
        } catch(PropertyVetoException e) {
            e.printStackTrace();
        } catch(DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 方法功能:流程 设置更改影响分析流程主任工艺师角色
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static void setTeamRoleUserToWfTeamRole(ObjectReference self, WTObject pbo, String roleName) throws WTException {
        Role role = Role.toRole(roleName);
        if(role == null) {
            return;
        }
        Set<WTUser> users = new HashSet<WTUser>();
        if(pbo instanceof WTAnalysisActivity) {
            WTAnalysisActivity activity = (WTAnalysisActivity) pbo;
            WTUser zhurengongyishi = (WTUser) activity.getModifier().getObject();
            //如果创建者是管理员,则根据影响分析源找到产品库所有主任工艺师
            if("Administrator".equals(zhurengongyishi.getName())) {
                ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) activity.getContainer());
                List<WTPrincipalReference> list = containerTeam.getAllPrincipalsForTarget(role);
                for (WTPrincipalReference reference : list) {
                    WTPrincipal principal = (WTPrincipal) reference.getObject();
                    if (principal instanceof WTUser) {
                        users.add((WTUser)principal);
                    } else if (principal instanceof WTGroup) {
                        WTGroup group = (WTGroup) principal;
                        SignatureService.getUserFromWTGroup(group, users);                    }
                }
            }else {
                users.add(zhurengongyishi);
            }
        }

        Persistable persistable = self.getObject();
        if(persistable instanceof WfProcess && users.size() > 0) {
            Transaction tx = new Transaction();
            tx.start();
            WfProcess process = (WfProcess) persistable;
            Team team = (Team) process.getTeamId().getObject();
            for(WTUser user : users) {
                team.addPrincipal(role, user);
            }
            team = (Team) PersistenceHelper.manager.refresh(team);
            team = (Team) PersistenceHelper.manager.save(team);
            tx.commit();
            tx = null;
        }
    }


    /**
     * 方法功能:流程 判断对象是否有关联的更改影响分析有正在进行的流程
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static boolean isHasWorkingAnalysisActivity(WTObject object) {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            if(object != null) {
                QueryResult qr = PersistenceHelper.manager.navigate(object, "analysisActivity", AnalysisToSourceLink.class);
                while(qr.hasMoreElements()) {
                    WTAnalysisActivity activity = (WTAnalysisActivity) qr.nextElement();
                    QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(activity, WfState.OPEN_RUNNING, null);
                    if(qrProcs.size() > 0) {
                        return true;
                    }
                }
            }
        } catch(QueryException e) {
            e.printStackTrace();
        } catch(WTException e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return false;
    }

    /**
     * 方法功能:流程 工艺更改单批准后根据属性判断是否关联更改影响分析，完成处理受影响工艺
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static void finishDealRelatedTechnics(WTObject pbo) {
        try {
            if(pbo != null && pbo instanceof WTChangeOrder2) {
                WTChangeOrder2 order2 = (WTChangeOrder2) pbo;
                String analysisNumber = IBAHelper.getIBAStringValue(order2, "ANALYSISNUMBER");
                if(StrUtil.isNotEmpty(analysisNumber)) {
                    String[] strs = analysisNumber.split("@!@");
                    if(strs.length == 2) {
                        String number = strs[0];
                        String docId = strs[1];

                        AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(number, docId, AnalysisConstant.TYPE_TECHNICS);
                        if(entry != null) {
                            entry.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                            entry.setAffectedGyy(AnalysisConstant.ANALYSIS_AFFECTED_HAS);
                            try {
                                CmPersistenceHelper.manager.update(entry);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch(WTException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法功能:流程 校验包是否是偏离单的包
     *
     * @author cjh
     * @date 2024/8/7
     */
    public static boolean isPianli(WTObject object) {
        boolean isPianli = false;
        try {
            if(object != null && object instanceof ProcessEnvelope) {
                ProcessEnvelope pe = (ProcessEnvelope) object;
                ArrayList<WTObject> list = ProcessEnvelopeUtil.getAllMembers(pe);
                for(WTObject obj : list) {
                    if(obj instanceof WTDocument) {
                        TypeIdentifier identifier = TypedUtility.getTypeIdentifier(obj);
                        String typeName = identifier.getTypename();
                        if(typeName.contains("casc.sast.149.TECHNOTICE_DOC")) {
                            QueryResult qr = PersistenceHelper.manager.navigate(obj, ChangePackagedResultLink.ROLE_AOBJECT_ROLE, ChangePackagedResultLink.class, true);
                            while(qr.hasMoreElements()) {
                                ChangePackaged packaged = (ChangePackaged) qr.nextElement();
                                if(isHasWorkingAnalysisActivity(packaged)) {
                                    return false;
                                }
                            }
                            isPianli = true;
                            break;
                        }
                    }
                }
            } else if(object instanceof ChangePackaged) {
                isPianli = true;
            }
        } catch(WTException e) {
            e.printStackTrace();
        }
        return isPianli;
    }

    /**
     * 方法功能:流程 校验制品是否处理完毕
     *
     * @author cjh
     * @date 2024/8/7
     */
    public static boolean checkProductDealResult(ObjectReference self, WTObject pbo) {
        try {
            WTAnalysisActivity activity = (WTAnalysisActivity) pbo;
            List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(activity.getNumber(), null, AnalysisConstant.PRODUCT);
            for(AnalysisObjEntry entry : entries) {
                if(!AnalysisUtil.isAllNoAffected(entry.getAnalysisNumber(), entry.getVerOid())) {
                    if(!AnalysisUtil.checkProductDealResult(entry, false)) {
                        return false;
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 方法功能:流程 完成更改影响分析活动后，根据影响分析条目设置工艺员角色人员
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static void setGyyToAnalysisWorkflow(ObjectReference self, WTObject pbo) throws WTException {
        Map<String, WTUser> userMap = new HashMap<String, WTUser>();
        if(pbo != null && pbo instanceof WTAnalysisActivity) {
            ReferenceFactory rf = new ReferenceFactory();
            WTAnalysisActivity activity = (WTAnalysisActivity) pbo;
            List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(activity.getNumber(), null, null);
            for(AnalysisObjEntry entry : entries) {
                if((AnalysisConstant.TYPE_PBOM.equals(entry.getDataType()) || AnalysisConstant.TYPE_TECHNICS.equals(entry.getDataType()))
                    && !AnalysisConstant.DEAL_STATUS_FINISH.equals(entry.getDealStatus()) && !AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(entry.getAffected())
                    && StrUtil.isNotEmpty(entry.getResponser())) {
                    String responser = entry.getResponser();
                    try {
                        WTUser user = (WTUser) rf.getReference(responser).getObject();
                        userMap.put(user.getName(), user);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess process = wfa.getParentProcess();
        if(process != null) {
            List<WTUser> users = new ArrayList<WTUser>();
            users.addAll(userMap.values());
            AnalysisActivityHelper.saveTeamRole("GONGYIYUAN", users, process);
        }
    }

    /**
     * 方法功能:流程 完成更改影响分析活动后，根据影响分析条目向NC发送制品影响分析结果
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static void sendAnalysisResult(WTObject pbo) throws WTException {
        if(pbo != null && pbo instanceof WTAnalysisActivity) {
            WTAnalysisActivity analysisActivity = (WTAnalysisActivity) pbo;
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();

            Set<String> partIds = new HashSet<String>();
            List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(analysisActivity.getNumber(), null, AnalysisConstant.PRODUCT);
            if(entries.size() > 0) {
                ReferenceFactory rf = new ReferenceFactory();
                String changeNumber = "";
                List list = RelatedAnalysisFileBuilder.getObjByWTAnalysisActivity(analysisActivity);
                for(Object o : list) {
                    if(o instanceof ChangePackaged) {
                        changeNumber = ((ChangePackaged) o).getNumber();
                    } else if(o instanceof ChangeRequest) {
                        changeNumber = ((ChangeRequest) o).getNumber();
                    } else if(o instanceof ProcessEnvelope) {
                        changeNumber = ((ProcessEnvelope) o).getNumber();
                    }
                }
                for(AnalysisObjEntry entry : entries) {
                    //已经发送过的不重复发送
                    if(AnalysisConstant.SEND_STATUS_OK.equals(entry.getSendStatus())) {
                        continue;
                    }
                    if(partIds.contains(entry.getVerOid())) {
                        continue;
                    } else {
                        partIds.add(entry.getVerOid());
                    }
                    String vrOid = "VR:" + entry.getVerOid();
                    Persistable persistable = rf.getReference(vrOid).getObject();
                    if(persistable != null && persistable instanceof WTPart) {
                        WTPart part = (WTPart) persistable;
                        AnalysisObjEntry zaizhipin = AnalysisUtil.getAnalysisObjEntry(analysisActivity.getNumber(), entry.getVerOid(), AnalysisConstant.TYPE_ZAIZHIPIN);
                        AnalysisObjEntry yizhipin = AnalysisUtil.getAnalysisObjEntry(analysisActivity.getNumber(), entry.getVerOid(), AnalysisConstant.TYPE_YIZHIPIN);
                        if(zaizhipin != null && yizhipin != null) {
                            //在制品处理意见
                            String zdeal = zaizhipin.getProduct();
                            //已制品处理意见
                            String ydeal = yizhipin.getProduct();
                            //都是无影响不发送意见
                            if(AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(zdeal) && AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(ydeal)){
                                continue;
                            }
                            //有空意见的为问题数据，不发送意见
                            if(StrUtil.isEmpty(zdeal) || StrUtil.isEmpty(ydeal)){
                                continue;
                            }
                            //更改要求
                            String requirement = entry.getRequirement();
                            //要求完成时间
                            String completeTime = entry.getCompleteTime();
                            if(StrUtil.isEmpty(completeTime)) {
                                completeTime = DateUtil.afterNDay("yyyy-MM-dd HH:mm:ss", 5);
                            } else {
                                try {
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                                    Date date = df.parse(completeTime);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    completeTime = format.format(date);
                                } catch(ParseException e) {
                                    e.printStackTrace();
                                    completeTime = DateUtil.afterNDay("yyyy-MM-dd HH:mm:ss", 5);
                                }
                            }
                            //工艺员
                            String responser = zaizhipin.getResponser();
                            String gongyiyuan = "";
                            if(StrUtil.isNotEmpty(responser)) {
                                try {
                                    Persistable object = rf.getReference(responser).getObject();
                                    if(object != null && object instanceof WTUser) {
                                        WTUser user = (WTUser) object;
                                        gongyiyuan = user.getName();
                                    }
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //计调人员
                            String jidiaoyuan = yizhipin.getResponser();
                            String jidiaoName = "";
                            if(StrUtil.isNotEmpty(jidiaoyuan)) {
                                try {
                                    Persistable object = rf.getReference(jidiaoyuan).getObject();
                                    if(object != null && object instanceof WTUser) {
                                        WTUser user = (WTUser) object;
                                        jidiaoName = user.getName();
                                    }
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //阶段标记
                            String phaseCode = IBAHelper.getIBAStringValue(part, "PHASE_CODE");
                            if(!AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(yizhipin.getProduct()) || !AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(zaizhipin.getProduct())) {
                                //影响分析编号
                                String number = analysisActivity.getNumber();
                                //图号
                                String partNumber = part.getNumber();
                                String partName = part.getName();

                                JSONArray array = new JSONArray();
                                JSONObject object = new JSONObject();
                                object.put("yxfxcode", number);//影响分析编号
                                object.put("def9", changeNumber);//设计更改单号
                                object.put("figurecode", partNumber);//图号
                                object.put("figurename", partName);//图号名称
                                object.put("partid", entry.getVerOid());//部件ID
                                object.put("gongyiyuan", gongyiyuan);//工艺员
                                object.put("dispatcher", jidiaoName);//计调人员
                                object.put("changeyq", requirement);//更改要求
                                object.put("yqgctime", completeTime);//要求完成时间
                                object.put("def1", AnalysisConstant.SOURCE_YIZHIPIN + ydeal);//已制品处理结论
                                object.put("def2", AnalysisConstant.SOURCE_ZAIZHIPIN + zdeal);//在制品处理结论
                                object.put("def3", phaseCode);//阶段标记
                                object.put("def8", "");//设计更改单word下载链接  改用接口形式 一调一用
                                object.put("billmaker", currentUser.getName());//影响分析单据负责人
                                array.put(object);
                                System.out.println("制品更改影响分析推送参数 json: " + array);
                                String url = "http://10.125.237.4:80/service/PdmtoNCservlet";
                                if(!PDMConfig.isZS){
                                    url = "http://10.125.237.23:8099/service/PdmtoNCservlet";
                                }
                                try {
                                    String body = HttpRequest.post(url).body(array.toString(), "application/json").execute().body();
                                    JSONObject result = new JSONObject(body);
                                    System.out.println("制品更改影响分析推送结果 result: " + result);
                                    String code = result.optString("code");
                                    if("200".equals(code)) {
                                        //设置通知状态为已发送
                                        zaizhipin.setSendStatus(AnalysisConstant.SEND_STATUS_OK);
                                        yizhipin.setSendStatus(AnalysisConstant.SEND_STATUS_OK);
                                        CmPersistenceHelper.manager.update(zaizhipin);
                                        CmPersistenceHelper.manager.update(yizhipin);
                                    } else {
                                        throw new WTException(result.optString("msg"));
                                    }
                                } catch(Exception e) {
                                    throw new WTException("制品" + partNumber + "往NC发送处理意见失败，请联系管理员！失败信息：" + e.getMessage());
                                }
                            }
                        } else {
                            throw new WTException("未查询到制品" + part.getNumber() + "记录，请联系管理员！");
                        }
                    }
                }
            }
        }
    }


    /*
     * 流程 指定主任工艺师并修改影响分析创建者为完成的主任工艺师
     */
    public static void appointZhuRenGongYiShji(Object obj, WTObject pbo) {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WfProcess process = (WfProcess) obj;
            Enumeration enumeration = WfEngineHelper.service.getProcessSteps(process, null);
            String zhuRenGongyishi = null;
            while(enumeration.hasMoreElements()) {
                WfActivity wfactivity = (WfActivity) enumeration.nextElement();
                if(wfactivity instanceof WfAssignedActivity) {
                    WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
                    if("更改影响分析".equals(wfassignedactivity.getName())) {
                        QuerySpec qs = new QuerySpec(WorkItem.class);
                        qs.setAdvancedQueryEnabled(true);
                        qs.appendWhere(new SearchCondition(WorkItem.class,
                                "source.key.id", SearchCondition.EQUAL, wfassignedactivity.getPersistInfo().getObjectIdentifier().getId()), new int[0]);
                        TableColumn ca = new TableColumn("A0", "createstampa2");
                        OrderBy orderBy = new OrderBy(ca, true);
                        qs.appendOrderBy(orderBy, new int[0]);
                        QueryResult qr = PersistenceServerHelper.manager.query(qs);
                        if(qr.hasMoreElements()) {
                            WorkItem it = (WorkItem) qr.nextElement();
                            zhuRenGongyishi = it.getCompletedBy();
                        }

                    }
                }
            }
            if(zhuRenGongyishi != null && !"".equals(zhuRenGongyishi)) {
                WTUser user = CSCPrincipal.getUserByName(zhuRenGongyishi);
                if(user != null) {
                    Team team = (Team) process.getTeamId().getObject();
                    Role targetRole = Role.toRole("ZHIPAIGONGYIYUANZHE");
                    HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
                    List tempUserList = (List) rolePrincipalListMap.get(targetRole);
                    for(int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                        WTUser tuser = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
                        team.deletePrincipalTarget(targetRole, tuser);
                    }
                    team.addPrincipal(targetRole, user);
                    team = (Team) PersistenceHelper.manager.refresh(team);
                    team = (Team) PersistenceHelper.manager.save(team);

                    if(pbo != null && pbo instanceof WTAnalysisActivity){
                        try {
                            WTAnalysisActivity activity = (WTAnalysisActivity) pbo;
                            WTPrincipalReference ref = WTPrincipalReference.newWTPrincipalReference(user);
                            Class[] pp = new Class[]{WTPrincipalReference.class};
                            //修改创建者
                            Method setCreator = _IterationInfo.class.getDeclaredMethod("setCreator", pp);
                            setCreator.setAccessible(true);
                            setCreator.invoke(activity.getIterationInfo(), new Object[]{ref});
                            //修改修改者
                            Method setModifier = _IterationInfo.class.getDeclaredMethod("setModifier", pp);
                            setModifier.setAccessible(true);
                            setModifier.invoke(activity.getIterationInfo(), new Object[]{ref});
                            PersistenceServerHelper.manager.update(activity);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch(WTException e) {
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

}
