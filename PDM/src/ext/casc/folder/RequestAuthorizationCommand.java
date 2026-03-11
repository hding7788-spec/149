package ext.casc.folder;

import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WorkflowUtil;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;
import ext.casc.util.CommonUtil;
import ext.casc.util.IBAHelper;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.fc.collections.WTValuedHashMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestAuthorizationCommand implements RemoteAccess, Serializable {

    private static final String ROLE_AUTHORISE_FORM_RECEIVE = "AUTHORISE_RECEIVE";

    private static final long serialVersionUID = 1L;

    public RequestAuthorizationCommand() {
    }

    public static FormResult requestAuthorization(NmCommandBean nmcommandbean) throws WTException {
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        FeedbackMessage message = new FeedbackMessage();
        Object refObject = nmcommandbean.getPrimaryOid().getRefObject();
        WTUser currentUser = (WTUser) SessionHelper.manager.getPrincipal();
        if(refObject instanceof WTDocument) {
            WTDocument doc = (WTDocument) refObject;
            String number = IBAHelper.getIBAStringValue(doc,"PPNUMBER");
            List<WTDocument> documents = WTDocumentUtil.getAllLatestWTDocumentByNumber(doc.getNumber());
            WTUser modifier = (WTUser) doc.getModifier().getPrincipal();
            if(currentUser.getName().equals(modifier.getName())) {
                message.addMessage("修改者为本人，申请授权失败！");
                result.addFeedbackMessage(message);
                result.setStatus(FormProcessingStatus.FAILURE);
                return result;
            } else {
                boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
                try {
                    //典道新增代码 begin
//                    try {
//                        ext.cirpoint.securitymgr.log.util.AuditRecordUtil.insertShouQuanLogs(objs, receiveName);
//                    } catch(Exception e) {
//                        e.printStackTrace();
//                    }
                    //典道新增代码end
                    List<Persistable> persistables = new ArrayList<>();
                    persistables.addAll(documents);
                    String authoriseFile = AuthoriserPermissionUtil.writeExcel(modifier.getName(), currentUser.getName(), "", persistables);
                    WTDocument authoriseDoc = WTDocument.newWTDocument();
                    TypeIdentifier authoriseType = TypeIdentifierHelper.getTypeIdentifier("WCTYPE|wt.doc.WTDocument|casc.sast.149.REQUESTAUTHORISE_FORM");
                    authoriseDoc = (WTDocument) CoreMetaUtility.setType(authoriseDoc, authoriseType);
                    ReferenceFactory rf = new ReferenceFactory();
                    String authoriseFolder = "/Default/09授权单";
                    WTContainerRef containerRef = (WTContainerRef) rf.getReference(doc.getContainer());

                    authoriseDoc.setName(number + "授权单");
                    String genNumber = QuickDynamicAuthorizationProcessor.genNumber(currentUser.getName());
                    WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(genNumber);
                    if(document != null) {
                        Thread.sleep(100);
                        genNumber = QuickDynamicAuthorizationProcessor.genNumber(currentUser.getName());
                    }
                    authoriseDoc.setNumber(genNumber);
                    authoriseDoc.setContainerReference(containerRef);
                    Folder location = null;
                    try {
                        location = FolderHelper.service.getFolder(authoriseFolder, containerRef);
                    } catch(Exception e) {
                        location = null;
                    }
                    if(location == null)
                        location = FolderHelper.service.saveFolderPath(authoriseFolder, containerRef);
                    if(location != null) {
                        WTValuedHashMap map = new WTValuedHashMap();
                        map.put(authoriseDoc, location);
                        FolderHelper.assignLocations(map);
                    }
                    authoriseDoc = (WTDocument) PersistenceHelper.manager.save(authoriseDoc);
                    authoriseDoc = (WTDocument) PersistenceHelper.manager.refresh(authoriseDoc);
                    ApplicationData appData = ApplicationData.newApplicationData(authoriseDoc);
                    appData.setRole(ContentRoleType.PRIMARY);
                    ContentServerHelper.service.updateContent(authoriseDoc, appData, authoriseFile);
                    AuthoriserPermissionUtil.assignUserToRole(currentUser, authoriseDoc, ROLE_AUTHORISE_FORM_RECEIVE);
                    File file = new File(authoriseFile);
                    if(file.exists()) {
                        file.delete();
                    }
                    IBAHelper.setIBAStringValue(authoriseDoc, "PPNUMBER", doc.getNumber());
                    WorkflowUtil.startWfProcess(authoriseDoc.getContainerReference(), authoriseDoc, "发起授权申请审批流程", new HashMap<>());
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    SessionServerHelper.manager.setAccessEnforced(enforce);
                }
            }
        }
        message.addMessage("申请授权成功！");
        result.addFeedbackMessage(message);
        return result;
    }

    public static void doAuthorize(ObjectReference self, WTObject pbo) {
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WfProcess process = (WfProcess) self.getObject();
            WTUser receiveUser = (WTUser) process.getCreator().getObject();
            String zhipaiName = receiveUser.getName();
            String receiveOid = receiveUser.getPersistInfo().getObjectIdentifier().toString();
            if(pbo != null && pbo instanceof WTDocument) {
                WTDocument doc = (WTDocument) pbo;
                List<Persistable> persistables = new ArrayList<>();
                TypeIdentifier typeIdentifier = TypedUtility.getTypeIdentifier(doc);
                if(typeIdentifier.getTypename().contains("casc.sast.149.AUTHORISE_FORM")
                        || typeIdentifier.getTypename().contains("casc.sast.149.REQUESTAUTHORISE_FORM")) {
                    String number = IBAHelper.getIBAStringValue(doc, "PPNUMBER");
                    List<WTDocument> list = WTDocumentUtil.getAllLatestWTDocumentByNumber(number);
                    persistables.addAll(list);
                } else {
                    persistables.add(doc);
                }
                for(Persistable persistable : persistables) {
                    WTDocument document = (WTDocument) persistable;
                    TypeIdentifier identifier = TypedUtility.getTypeIdentifier(document);
                    String typename = identifier.getTypename();
                    if(typename.contains("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN")
                            || typename.contains("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.SOPDoc")) {
                        if("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149.reportTechnics".equals(typename)) {
                            QuickDynamicAuthorizationProcessor.replaceModifierInXML(document, zhipaiName, "baobiao", receiveOid);
                        } else {
                            QuickDynamicAuthorizationProcessor.replaceModifierInXML(document, zhipaiName, "putong", receiveOid);
                        }
                    }
                }
                boolean isManagerFlag = CommonUtil.isSiteOrOrgAdmin();
                AuthoriserPermissionUtil.setAllObjModifier(persistables, WTPrincipalReference.newWTPrincipalReference(receiveUser), isManagerFlag, receiveUser.getFullName());
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    public static void setGongYiYuan(ObjectReference self, WTObject pbo) {
        try {
            WfProcess process = (WfProcess) self.getObject();
            Role role = Role.toRole("GONGYIYUAN");
            if(role != null && pbo instanceof WTDocument) {
                WTDocument doc = (WTDocument) pbo;
                List<WTUser> userList = new ArrayList<WTUser>();
                TypeIdentifier typeIdentifier = TypedUtility.getTypeIdentifier(doc);
                if(typeIdentifier.getTypename().contains("casc.sast.149.AUTHORISE_FORM")
                        || typeIdentifier.getTypename().contains("casc.sast.149.REQUESTAUTHORISE_FORM")) {
                    String number = IBAHelper.getIBAStringValue(doc, "PPNUMBER");
                    WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(number);
                    if(document != null) {
                        userList.add((WTUser) document.getModifier().getPrincipal());
                    }
                } else {
                    userList.add((WTUser) doc.getModifier().getPrincipal());
                }
                if(userList.size() > 0) {
                    Team team = (Team) process.getTeamId().getObject();
                    for(WTUser user : userList) {
                        team.addPrincipal(role, user);
                    }
                    team = (Team) PersistenceHelper.manager.refresh(team);
                    team = (Team) PersistenceHelper.manager.save(team);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
