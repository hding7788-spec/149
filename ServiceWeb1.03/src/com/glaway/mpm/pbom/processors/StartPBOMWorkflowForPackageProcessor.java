package com.glaway.mpm.pbom.processors;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import wt.auth.Authentication;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeamHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.projmgmt.admin.Project2;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.meta.common.impl.WCTypeIdentifier;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedUtil;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.EnvelopeTopObjLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;

public class StartPBOMWorkflowForPackageProcessor extends DefaultObjectFormProcessor {

    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult form = super.doOperation(commandBean, objectBeans);
        try {
            WorkItem wi = (WorkItem) commandBean.getPageOid().getRefObject();
            Object obj = wi.getPrimaryBusinessObject().getObject();
            List<Object> list = new ArrayList<Object>();
            WTContainer wtContainer = null;
            QueryResult attachResult = null;
            String number = "";
            String name = "";
            RevisionControlled revisionControlled = null;
            String folder = null;
            if(obj instanceof ProcessEnvelope) {
                ProcessEnvelope envelope = (ProcessEnvelope)obj;
                list = ProcessEnvelopeUtil.getAllMembers(envelope);
                revisionControlled = ProcessEnvelopeUtil.getTopObject(envelope);
                wtContainer = envelope.getContainer();
                number = envelope.getNumber();
                name = envelope.getName();
                folder = envelope.getFolderPath();
            } else if (obj instanceof ChangePackaged) {
                ChangePackaged changePackaged = (ChangePackaged)obj;
                QueryResult qr = ChangePackagedUtil.getChangeAfterDataByChangePackaged(changePackaged);
                Vector vector = qr.getObjectVectorIfc().getVector();
                list.addAll(vector);
                wtContainer = changePackaged.getContainer();
                number = changePackaged.getNumber();
                name = changePackaged.getName();
                folder = changePackaged.getFolderPath();
                attachResult = ContentHelper.service.getContentsByRole(changePackaged, ContentRoleType.SECONDARY);
            }

            if((list != null) && !list.isEmpty()) {
                //判断是否存在EBOM数据，如果不存在，则不启动PBOM构建流程。
                boolean hasPart = false;
                for(Object tempObj:list) {
                    if(tempObj instanceof WTPart) {
                        hasPart = true;
                        break;
                    }
                }

                if(hasPart) {
                    ProcessEnvelope newProcessEnvelope = ProcessEnvelope.newProcessEnvelope();
                    String newNumber = "PBOM_"+number;
                    String newName = "PBOM_"+name;
                    QueryResult qr = ProcessEnvelopeUtil.getProcessEnvelopeByNumber(newNumber);
                    if(qr.size()==0) {
                        newProcessEnvelope.setNumber(newNumber);
                        newProcessEnvelope.setName(newName);

                        newProcessEnvelope.setContainer(wtContainer);

                        folder = "/Default";
                        Folder fol = FolderHelper.service.getFolder(folder, WTContainerRef.newWTContainerRef(wtContainer));
                        FolderHelper.assignLocation((FolderEntry) newProcessEnvelope, fol);

                        String typeStr=repairSoftTypeId("ext.ases.envelope.ProcessEnvelope|PBOMPackage");
                        newProcessEnvelope.setTypeDefinitionReference(TypedUtility.getTypeDefinitionReference(typeStr));

                        //新建PBOM数据包对象
                        newProcessEnvelope = (ProcessEnvelope)PersistenceHelper.manager.save(newProcessEnvelope);

                        //将数据关联到PBOM数据包
                        saveRelatedObjLink(list, newProcessEnvelope);

                        //设置顶层部件link
                        if(revisionControlled != null) {
                        	saveTopObjLink(revisionControlled, newProcessEnvelope);
                        }

                        //如果是变更，则将变更单附件关联到PBOM数据包
                        if(attachResult != null) {
                            ApplicationData ad = null;
                            while(attachResult.hasMoreElements()) {
                                ad = (ApplicationData)attachResult.nextElement();
                                InputStream is = ContentServerHelper.service.findContentStream(ad);
                                ApplicationData data = ApplicationData.newApplicationData(newProcessEnvelope);
                                data.setRole(ContentRoleType.SECONDARY);
                                data.setFileName(ad.getFileName());
                                data.setUploadedFromPath(ad.getFileName());
                                data = ContentServerHelper.service.updateContent(newProcessEnvelope, data, is);
                                PersistenceServerHelper.manager.update(data);
                            }
                        }

                        form.setStatus(FormProcessingStatus.SUCCESS);
                        FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "PBOM构建流程已经启动！");
                        form.addFeedbackMessage(message);
                        form.setNextAction(FormResultAction.REFRESH_OPENER);
                    } else {
                        form.setStatus(FormProcessingStatus.FAILURE);
                        FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "PBOM构建流程已经存在，不能重复创建！");
                        form.addFeedbackMessage(message);
                        form.setNextAction(FormResultAction.REFRESH_OPENER);
                    }
                } else {
                    form.setStatus(FormProcessingStatus.FAILURE);
                    FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "没有EBOM数据，PBOM构建流程没有启动！");
                    form.addFeedbackMessage(message);
                    form.setNextAction(FormResultAction.REFRESH_OPENER);
                }
            } else {
                form.setStatus(FormProcessingStatus.FAILURE);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "没有数据，PBOM构建流程没有启动！");
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
            }
        } catch (Exception e) {
            form.setStatus(FormProcessingStatus.FAILURE);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "PBOM构建流程启动失败，请联系管理员！");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_OPENER);
            e.printStackTrace();
        }

        return form;
    }

    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainer container,WTObject persistable) throws Exception{
        return initiateWfProcess(templateName, data, WTContainerRef.newWTContainerRef(container),persistable);
     }

    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainerRef conref,WTObject persistable) throws Exception {
        try {
            WfProcessDefinition wfProcessDef = WfDefinerHelper.service.getProcessDefinition(templateName, conref);

            if (wfProcessDef == null) {
                return null;
            }

            WTContainer container = conref.getReferencedContainer();
            wt.inf.team.ContainerTeam team = null;
            if (container instanceof PDMLinkProduct) {
                team = ContainerTeamHelper.service.getContainerTeam((PDMLinkProduct) container);
            } else if (container instanceof WTLibrary) {
                team = ContainerTeamHelper.service.getContainerTeam((WTLibrary) container);
            } else if (container instanceof Project2) {
                team = ContainerTeamHelper.service.getContainerTeam((Project2) container);
            }
            WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDef, team, conref);

            wfProcess.setName(templateName + "_" + System.currentTimeMillis());
            String user = Authentication.getUserName();
            WTUser wtuser = OrganizationServicesHelper.manager.getAuthenticatedUser(user);
            WTPrincipalReference ref = WTPrincipalReference.newWTPrincipalReference(wtuser);
            wfProcess.setCreator(ref);

            wfProcess = WfEngineServerHelper.service.setPrimaryBusinessObject(wfProcess, persistable);

            ProcessData pData = wfProcess.getContext();
            Iterator keys = data.keySet().iterator();
            while (keys.hasNext()) {
                String paramName = (String) keys.next();
                Object paramValue = data.get(paramName);
                pData.setValue(paramName, paramValue);
            }
            wfProcess = wfProcess.start(pData, true, conref);

            return wfProcess;
        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    private void saveRelatedObjLink(List<Object> list,ProcessEnvelope processEnvelope) throws WTException, WTPropertyVetoException{
        for(int i = 0 ; i < list.size() ; i++){
            Object object = list.get(i);
            EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(processEnvelope,(RevisionControlled)object);
            envelopememberlink.setDescription("");
            PersistenceHelper.manager.save(envelopememberlink);
        }
    }

    private void saveTopObjLink(RevisionControlled revisionControlled,ProcessEnvelope processEnvelope) throws WTException{
		try
		{
			EnvelopeTopObjLink envelotopobjlink = EnvelopeTopObjLink.newEnvelopeTopObjLink(processEnvelope,revisionControlled);
			PersistenceHelper.manager.save(envelotopobjlink);
		}
		catch (wt.util.WTException e){
			e.printStackTrace();
		}
	}

    /**
     * 获取SoftType的全名, 类型名前可有WCTYPE开始。如WCTYPE|wt.doc.WTDocument|CAPPDOC ==>
     * wt.doc.WTDocument|com.ptc.lld.CAPPDOC
     *
     * @param st
     * @return
     * @throws WTException
     */
    public static String repairSoftTypeId(String st) throws WTException {
        String siteDomain = WTContainerHelper.service.getExchangeContainer().getInternetDomain();
        String reversedDomain = "";
        String[] segs = siteDomain.split("\\.");
        for (int i = segs.length - 1; i >= 0; i--) {
            if (i < segs.length - 1)
                reversedDomain += ".";
            reversedDomain += segs[i].trim();
        }

        segs = st.split("\\|");
        String result = "";
        for (int i = 0; i < segs.length; i++) {
            if (i > 0)
                result += WCTypeIdentifier.HIERARCHY_SEPARATOR;
            if (segs[i].indexOf(".") < 0 && (i != 0 || !segs[i].equals(WCTypeIdentifier.PROTOCOL)))
                result += reversedDomain + ".";
            result += segs[i];
        }

        return result;
    }

}
