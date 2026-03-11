package ext.casc.gongshidinge.processor;

import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.glaway.mpm.util.WorkflowUtil;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.constants.Constants;
import ext.casc.util.IBAHelper;
import ext.ptc.ViewWIHelper;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.io.Serializable;
import java.util.*;

public class StartGongshiSignCommand implements RemoteAccess, Serializable {

    private static final long serialVersionUID = -837413485598684871L;

    public StartGongshiSignCommand() {
    }

    public static FormResult startGongShiSign(NmCommandBean nmcommandbean) throws WTException {
        FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
        FeedbackMessage message = new FeedbackMessage();
        Object refObject = nmcommandbean.getPrimaryOid().getRefObject();
        if(refObject instanceof WTDocument) {
            WTDocument document = (WTDocument) refObject;
            if(Constants.STATE_APPROVED.equals(document.getLifeCycleState().toString())){
                Transaction tx = null;
                boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
                try {
                    tx = new Transaction();
                    tx.start();
                    List<MPMOperation> list = new ArrayList<MPMOperation>();
                    MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(document);
                    if(plan != null) {
                        List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
                        for(MPMOperationUsageLink link : links) {
                            if(link.getRoleBObject() != null && link.getRoleBObject() instanceof MPMOperationMaster){
                                MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
                                MPMOperation operation = ViewWIHelper.getMpmOperation(master.getNumber());
                                list.add(operation);
                            }
                        }
                    }

                    ProcessEnvelope pe = null;
                    if(!list.isEmpty()) {
                        pe = ProcessEnvelope.newProcessEnvelope();
                        pe.setNumber(new Date().getTime() + "");
                        WTContainer container = document.getContainer();
                        pe.setName("工时定额签审单"+"-"+document.getName()+"-"+pe.getCreatorFullName());
                        String type = "WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.GSDE";
                        TypeIdentifier id = TypeHelper.getTypeIdentifier(type);
                        pe = (ProcessEnvelope) CoreMetaUtility.setType(pe, id);

                        Folder folder = FolderHelper.service.getFolder("/Default", WTContainerRef.newWTContainerRef(container));
                        if (folder != null){
                            FolderHelper.assignLocation((FolderEntry) pe, folder);
                        }
                        pe.setContainer(container);
                        PersistenceHelper.manager.save(pe);

                        for(MPMOperation operation : list) {
                            EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(pe,operation);
                            envelopememberlink.setDescription("工序工时定额签审关联关系");
                            PersistenceHelper.manager.save(envelopememberlink);
                        }
                        boolean flag = WorkflowUtil.startProcess(pe, "工时定额签审流程", document.getName(), new HashMap<String, String>());
                        if(!flag){
                            message.addMessage("发起工时定额签审流程失败！");
                            result.addFeedbackMessage(message);
                            result.setStatus(FormProcessingStatus.FAILURE);
                            return result;
                        }
                    }
                    IBAHelper.setIBAStringValue(document, "GongShiDingEState", "进行中");
                    tx.commit();
                    tx = null;
                } catch (Exception e) {
                    if(tx!=null){
                        tx.rollback();
                    }
                    e.printStackTrace();
                }finally{
                    SessionServerHelper.manager.setAccessEnforced(enforce);
                    if(tx != null){
                        tx.rollback();
                    }
                    tx = null;
                }
            }else {
                message.addMessage("工艺未受控，发起工时定额签审流程失败！");
                result.addFeedbackMessage(message);
                result.setStatus(FormProcessingStatus.FAILURE);
                return result;
            }
        }
        message.addMessage("发起工时定额签审流程成功！");
        result.addFeedbackMessage(message);
        return result;
    }
}
