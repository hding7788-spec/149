package ext.casc.workflow;

import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.DateUtil;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.extend.util.ObjectProperty;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;
import com.ptc.windchill.enterprise.dsvcore.server.utils.PersistableHelper;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedUtil;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.changerequest.ChangeRequestUtil;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.ases.part.ASESHuiqianSignature;
import ext.ases.part.SignLink;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewObject;
import ext.casc.preview.PreviewUtil;
import ext.casc.purge.DataPurger;
import ext.casc.util.DBConn;
import ext.casc.util.Deserialize;
import ext.casc.util.IBAHelper;
import ext.casc.util.PurgeDataProcessor;
import org.apache.log4j.Logger;
import wt.admin.AdministrativeDomainHelper;
import wt.annotation.AnnotationBaseline;
import wt.change2.*;
import wt.doc.*;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.workspaces.EPMWorkspace;
import wt.epm.workspaces.EPMWorkspaceHelper;
import wt.fc.*;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityHelper;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.*;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pds.oracle81.OracleDataSource;
import wt.pom.Transaction;
import wt.projmgmt.admin.Project2;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.session.SessionAuthenticator;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.*;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.definer.UserEventVector;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.*;
import java.util.*;


public final class CmWorkflowHelper implements RemoteAccess{
	private static final Logger LOGGER = Logger.getLogger(CmWorkflowHelper.class);
    private static final String PRODUCT_ID = "containerReference.key.id";
    private static final String ROLEB_ID = "roleBObjectRef.key.id";
    private static final String ROLEA_ID = "roleAObjectRef.key.id";
    private static final String REF_ROLEA_ID = "roleAObjectRef.key.branchId";
    private static final String REF_ROLEB_ID = "roleBObjectRef.key.branchId";

    public static void main(String[] args) throws RemoteException, WTException {
		RemoteMethodServer ms = RemoteMethodServer.getDefault();
		ms.setUserName("wcadmin");
		ms.setPassword("wcadmin");
		if (!RemoteMethodServer.ServerFlag) {
			String method = "deletePeOrCp";
			String number = "TTTTTTT";
			String type = "ApproveOrder";
			Class[] cls = { String.class,String.class };
			Object[] obj = { number,type};
			try {
				RemoteMethodServer.getDefault().invoke(method, CmWorkflowHelper.class.getName(), null, cls, obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * 完成流程活动
     *
     * @param oid
     *            WorkItem oid
     * @param route
     *            路由选择事件
     * @throws WTException
     */
    public static void completeActivityBy805(String activityOid805) throws WTException {
        completeActivity(activityOid805, "通过", "签审完成，805或八部远程调用程序完成活动");
    }

    public static void completeActivity(WorkItem workitem, String route,  String comments) throws WTException{
        boolean accessFlag = SessionServerHelper.manager
                .setAccessEnforced(false);
        WfActivity wfactivity = null;
        try {
            if (workitem != null) {
                wfactivity = (WfActivity) workitem.getSource().getObject();
                ProcessData pd = wfactivity.getContext();
                ProcessData pdc = pd.copy();
                if (pdc != null) {
                    pdc.setTaskComments(comments);
                    workitem.setContext(pdc);
                    workitem = (WorkItem) PersistenceHelper.manager
                            .save(workitem);
                }
                Vector vector = new Vector();
                vector.addElement(route);
                WorkflowHelper.service.workComplete(workitem, workitem
                        .getOwnership().getOwner(), vector);
                WfEventHelper.createVotingEvent(null, wfactivity, workitem,
                        workitem.getOwnership().getOwner(), comments, vector,
                        false, workitem.isRequired());
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
    }

    /**
     * 完成流程活动
     *
     * @param oid
     *            WorkItem oid
     * @param route
     *            路由选择事件
     * @throws WTException
     */
    public static void completeActivity(String wfActivityOid, String route,
            String comments) throws WTException {
        boolean accessFlag = SessionServerHelper.manager
                .setAccessEnforced(false);
        WfActivity wfactivity = null;
        try {
            WorkItem workitem = getWorkItemByActivityOid(wfActivityOid);
            if (workitem != null) {
                wfactivity = (WfActivity) workitem.getSource().getObject();
                ProcessData pd = wfactivity.getContext();
                ProcessData pdc = pd.copy();
                if (pdc != null) {
                    pdc.setTaskComments(comments);
                    workitem.setContext(pdc);
                    workitem = (WorkItem) PersistenceHelper.manager
                            .save(workitem);
                }
                Vector vector = new Vector();
                vector.addElement(route);
                WorkflowHelper.service.workComplete(workitem, workitem
                        .getOwnership().getOwner(), vector);
                WfEventHelper.createVotingEvent(null, wfactivity, workitem,
                        workitem.getOwnership().getOwner(), comments, vector,
                        false, workitem.isRequired());
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
    }



    public static WorkItem getWorkItemByActivityOid(String wfActivityOid)
            throws WTException {
        String temp = wfActivityOid.substring(
                wfActivityOid.lastIndexOf(":") + 1, wfActivityOid.length());
        Long ida3a4 = Long.parseLong(temp);
        WorkItem workitem = null;
        QueryResult queryresult;
        QuerySpec queryspec = new QuerySpec(WorkItem.class);
        queryspec.setAdvancedQueryEnabled(true);
        SearchCondition searchcondition = new SearchCondition(WorkItem.class,
                "source.key.id", "=", ida3a4);
        queryspec.appendWhere(searchcondition, 0);
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(WorkItem.class, "status",
                "=", "POTENTIAL"), 0);
        TableColumn ca = new TableColumn("A0", "createstampa2");
        OrderBy orderBy = new OrderBy(ca, false);
        queryspec.appendOrderBy(orderBy, new int[0]);
        queryresult = PersistenceServerHelper.manager.query(queryspec);
        if (queryresult.hasMoreElements()) {
            workitem = (WorkItem) queryresult.nextElement();
        }
        return workitem;

    }

    /**
     * 完成流程活动
     *
     * @param ObjectReference
     *            WorkItemReference
     * @param route
     *            路由选择事件
     * @throws WTException
     */
    public static void completeActivity(ObjectReference self, String route, String comments)
            throws WTException {
        Vector<String> vote = new Vector<String>();
        vote.addElement(route);
        WfEngineHelper.service.complete((WfActivity) self.getObject(), vote);

    }

    /**
     * 如果149厂上次会签已通过，但被805驳回后会签不通过，则需要删除本次会签的数据
     * @param pbo
     * @throws WTException
     */
    public static void deleteDataFromThisReivew(WTObject pbo) throws WTException{
    	ProcessEnvelope pe = null;
    	if (pbo instanceof ProcessEnvelope) {
    		pe = (ProcessEnvelope) pbo;
    		List<Object> wtObjList = new ArrayList<Object>();
    		QueryResult qr = PersistenceHelper.manager.navigate(pe, EnvelopeMemberLink.ROLE_AOBJECT_REF, EnvelopeMemberLink.class, false);
    		while (qr.hasMoreElements()) {
				EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
				String desc = link.getDescription();
				if (desc!=null && !desc.equals("")) {
					wtObjList.add(link.getRevisionControlled());
				}
			}
    		if (wtObjList.size()>0) {
    			deletePart(wtObjList);
    			deleteDoc(wtObjList);
    			deleteCAD(wtObjList);
			}

        }else if(pbo instanceof ChangeRequest){

		}
    }
    private static void deletePart(List<Object> wtObjList){
        Transaction tx = new Transaction();
        try {
            tx.start();

            WTPart wtPart = null;
            WTSet allDataSet = new WTHashSet();
			Object obj = null;
            for (int i =0;i<wtObjList.size();i++) {
            	obj = wtObjList.get(i);
            	if (!(obj instanceof WTPart)) {
					continue;
				}
                // 如果对象被检出，则取消检出。如果对象是工作副本则忽略任何操作继续循环。
                if (WorkInProgressHelper.isCheckedOut(wtPart) && !WorkInProgressHelper.isWorkingCopy(wtPart)) {
                    wtPart = (WTPart) WorkInProgressHelper.service.undoCheckout(wtPart);
                } else if (WorkInProgressHelper.isWorkingCopy(wtPart) && WorkInProgressHelper.isCheckedOut(wtPart)) {
                    continue;
                }

                // 从基线中移除部件
//                removeFromBaseline(wtPart);
                // 从变更中移除部件
//                removeFromChange(wtPart);

                // 从升级对象中删除指定对象
//                arrayList = new WTArrayList();
//                arrayList.add(wtPart);
//                removeFromPromotionNotice(arrayList, wtPart);

                // 删除部件与部件之间的Link
                deleteLinkP2P(wtPart);
                // 删除部件与文档之间的Link
                deleteLinkP2D(wtPart);
                // 删除部件与CAD文档之间的Link
                deleteLinkP2E(wtPart);

                Iterated iterated = VersionControlHelper.service.getLatestIteration(wtPart, false);
                allDataSet.add(iterated);
            }
            PersistenceHelper.manager.delete(allDataSet);
            tx.commit();
        } catch (WTException e) {
            tx.rollback();
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDoc(List<Object> wtObjList){
        Transaction tx = new Transaction();
        WTSet allDataSet = new WTHashSet();
        try {
            tx.start();
            WTDocument wtDocument = null;
            Object obj = null;
            for (int i =0;i<wtObjList.size();i++) {
            	obj = wtObjList.get(i);
            	if (!(obj instanceof WTDocument)) {
					continue;
				}
                // 如果对象被检出，则取消检出。如果对象是工作副本则忽略任何操作继续循环。
                if (WorkInProgressHelper.isCheckedOut(wtDocument) && !WorkInProgressHelper.isWorkingCopy(wtDocument)) {
                    wtDocument = (WTDocument) WorkInProgressHelper.service.undoCheckout(wtDocument);
                } else if (WorkInProgressHelper.isWorkingCopy(wtDocument)
                        && WorkInProgressHelper.isCheckedOut(wtDocument)) {
                    continue;
                }

                // 删除文档与文档之间的Link
                deleteLinkD2D(wtDocument);
                // 删除文档与部件之间的Link
                deleteLinkD2P(wtDocument);

                Iterated iterated = VersionControlHelper.service.getLatestIteration(wtDocument, false);
                allDataSet.add(iterated);
            }

            PersistenceHelper.manager.delete(allDataSet);

            tx.commit();
        } catch (WTException e) {
            tx.rollback();
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private static void deleteCAD(List<Object> wtObjList){
        Transaction tx = new Transaction();
        WTSet allDataSet = new WTHashSet();
        try {
            tx.start();
            EPMDocument epmDocument = null;
            Object obj = null;
            for (int i =0;i<wtObjList.size();i++) {
            	obj = wtObjList.get(i);
            	if (!(obj instanceof EPMDocument)) {
					continue;
				}
                // 如果对象被检出，则取消检出。如果对象是工作副本则忽略任何操作继续循环。
                if (WorkInProgressHelper.isCheckedOut(epmDocument) && !WorkInProgressHelper.isWorkingCopy(epmDocument)) {
                    epmDocument = (EPMDocument) WorkInProgressHelper.service.undoCheckout(epmDocument);
                } else if (WorkInProgressHelper.isWorkingCopy(epmDocument)
                        && WorkInProgressHelper.isCheckedOut(epmDocument)) {
                    continue;
                }

                // 删除CAD文档之间的link
                deleteLinkE2E(epmDocument);
                // 删除CAD文档与部件之间的Link
                deleteLinkE2P(epmDocument);

                Iterated iterated = VersionControlHelper.service.getLatestIteration(epmDocument, false);
                allDataSet.add(iterated);
            }

            PersistenceHelper.manager.delete(allDataSet);

            tx.commit();
        } catch (WTException e) {
            tx.rollback();
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private static void deleteWorkSpaceData(QueryResult qResult) throws WTException{
        ObjectVector oVector = new ObjectVector();
        while (qResult.hasMoreElements()) {
            EPMWorkspace workspace = (EPMWorkspace) qResult.nextElement();
            WTSet cadSet = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, EPMDocument.class);
            WTSet tempCadSet = new WTHashSet();
            Iterator cadSetIterator = cadSet.iterator();
            while (cadSetIterator.hasNext()) {
                EPMDocument epmDocument = (EPMDocument) ((ObjectReference) cadSetIterator.next()).getObject();
                String name = epmDocument.getName();
                if (name.endsWith("作废")) {
                    tempCadSet.add(epmDocument);
                    oVector.addElement(epmDocument);
                }
            }
            EPMWorkspaceHelper.manager.removeFromWorkspace(workspace, tempCadSet);
            WTSet partSet = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, WTPart.class);
            WTSet tempPartSet = new WTHashSet();
            Iterator partSetIterator = partSet.iterator();
            while (partSetIterator.hasNext()) {
                WTPart part = (WTPart) ((ObjectReference) partSetIterator.next()).getObject();
                String name = part.getName();
                if (name.endsWith("作废")) {
                    tempPartSet.add(part);
                    oVector.addElement(part);
                }
            }
            EPMWorkspaceHelper.manager.removeFromWorkspace(workspace, tempPartSet);
        }
        qResult = new QueryResult(oVector);
    }

    /**
     * 删除部件到部件大之间的关系,既子部件和替代件的link
     *
     * @param part
     *            WTPart
     * @throws WTException
     */
    private static void deleteLinkP2P(WTPart part) throws WTException {
        if (part == null) {
        	return;
        }
        // 指定部件作为父件，查找所有相关的WTPartUsageLink及其下面的替代件link
        QueryResult qResult = WTPartHelper.service.getUsesWTPartMasters(part);
        while (qResult.hasMoreElements()) {
            // 获取子部件的link
            WTPartUsageLink link = (WTPartUsageLink) qResult.nextElement();
            WTPartMaster childPartMaster = (WTPartMaster) link.getRoleBObject();
            // 删除全局替代link
            deleteAlternatelink(childPartMaster);
            // 删除局部替代link
            deleteSubstituteLink(link);
            // 删除子部件的link
            PersistenceServerHelper.manager.remove(link);
        }
        // 指定部件作为子件，查找所有相关的WTPartUsageLink并删除
        QueryResult qResult2 = getWTPartUsageLinksByChildPart((WTPartMaster) part.getMaster());
        while (qResult2.hasMoreElements()) {
            WTPartUsageLink link = (WTPartUsageLink) qResult2.nextElement();
            PersistenceServerHelper.manager.remove(link);
        }
        // 指定部件作为替代件，查找所有相关的WTPartAlternateLink并删除
        QueryResult qResult3 = getWTPartAlternateLinksByChildPart((WTPartMaster) part.getMaster());
        while (qResult3.hasMoreElements()) {
            WTPartAlternateLink link = (WTPartAlternateLink) qResult3.nextElement();
            PersistenceServerHelper.manager.remove(link);
        }
        // 指定部件作为替代件，查找所有相关的WTPartSubstituteLink并删除
        QueryResult qResult4 = getWTPartSubstituteLinksByChildPart((WTPartMaster) part.getMaster());
        while (qResult4.hasMoreElements()) {
            WTPartSubstituteLink link = (WTPartSubstituteLink) qResult4.nextElement();
            PersistenceServerHelper.manager.remove(link);
        }
    }

    /**
     * 删除部件到文档之间的关系,既相关文档的link
     *
     * @param part
     *            WTPart
     * @throws WTException
     */
    private static void deleteLinkP2D(WTPart part) throws WTException {
        if (part == null) {
            return;
        }
        QueryResult qResult = WTPartHelper.service.getDescribedByWTDocuments(part, false);
        WTPartDescribeLink wtPartDescribeLink = null;
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            wtPartDescribeLink = (WTPartDescribeLink) object;
            PersistenceServerHelper.manager.remove(wtPartDescribeLink);
        }

        List list2 = getPartReferenceLinksByDoc(part);
        WTPartReferenceLink wtPartReferenceLink = null;
        for (int i = 0; i < list2.size(); i++) {
            wtPartReferenceLink = (WTPartReferenceLink) list2.get(i);
            PersistenceServerHelper.manager.remove(wtPartReferenceLink);
        }
    }

    /**
     * 删除部件与CAD文档的link
     *
     * @param part
     *            WTPart
     * @throws WTException
     */
    private static void deleteLinkP2E(WTPart part) throws WTException {
        QueryResult qResult = getEPMBuildLinksRoles(part);
        while (qResult.hasMoreElements()) {
            EPMBuildRule linksRule = (EPMBuildRule) qResult.nextElement();
            // 删除此部件关联的CAD文档的link
			PersistenceServerHelper.manager.remove(linksRule);
        }
    }

    /**
     * 删除文档与文档之间的link
     *
     * @param doc
     *            WTDocument
     * @throws WTException
     */
    private static void deleteLinkD2D(WTDocument doc) throws WTException {
        if (doc == null) {
        	return;
        }
        // 通过指定文档查找所有与其相关的WTDocumentUsageLink并删除
        QueryResult qResult = WTDocumentHelper.service.getUsesWTDocumentUsageLinks(doc);
        while (qResult.hasMoreElements()) {
            WTDocumentUsageLink link = (WTDocumentUsageLink) qResult.nextElement();
            PersistenceServerHelper.manager.remove(link);
        }
        // 通过指定文档作为子文档查找所有与其相关的WTDocumentUsageLink并删除
        QueryResult qResult2 = getWTDocumentUsageLinksByChildPart((WTDocumentMaster) doc.getMaster());
        while (qResult2.hasMoreElements()) {
            WTDocumentUsageLink link = (WTDocumentUsageLink) qResult2.nextElement();
            PersistenceServerHelper.manager.remove(link);
        }
        // 通过指定文档查找所有与其相关的WTDocumentDependencyLink并删除
        QueryResult qResult3 = WTDocumentHelper.service.getHasDependentWTDocuments(doc, false);
        while (qResult3.hasMoreElements()) {
            WTDocumentDependencyLink link = (WTDocumentDependencyLink) qResult3.nextElement();
        }
        // 通过指定文档作为子文档查找所有与其相关的WTDocumentDependencyLink并删除
        QueryResult qResult4 = getWTDocumentDependencyLinksByChildPart(doc);
        while (qResult4.hasMoreElements()) {
            WTDocumentDependencyLink link = (WTDocumentDependencyLink) qResult4.nextElement();
            PersistenceServerHelper.manager.remove(link);
        }
    }

    /**
     * 删除文档与部件之间的link
     *
     * @param doc
     *            WTDocument
     * @throws WTException
     */
    private static void deleteLinkD2P(WTDocument doc) throws WTException {
        List list = getDocDescribeLinksByDoc(doc);
        WTPartDescribeLink wtPartDescribeLink = null;
        for (int i = 0; i < list.size(); i++) {
            wtPartDescribeLink = (WTPartDescribeLink) list.get(i);
            PersistenceServerHelper.manager.remove(wtPartDescribeLink);
        }

        List list2 = getDocReferenceLinksByDoc(doc);
        WTPartReferenceLink wtPartReferenceLink = null;
        for (int i = 0; i < list2.size(); i++) {
            wtPartReferenceLink = (WTPartReferenceLink) list2.get(i);
            PersistenceServerHelper.manager.remove(wtPartReferenceLink);
        }
    }

    /**
     * 删除CAD文档与CAD文档之间的link
     *
     * @param epmDocument
     *            EPMDocument
     * @throws WTException
     */
    private static void deleteLinkE2E(EPMDocument epmDocument)
            throws WTException {
        // 查找指定EPMDoucment对象的所有EPMReferenceLink
        QueryResult referenceResult = EPMStructureHelper.service.navigateReferences(epmDocument, null, false);
        while (referenceResult.hasMoreElements()) {
            EPMReferenceLink referenceLink = (EPMReferenceLink) referenceResult.nextElement();
            // 删除referenceLink
            PersistenceServerHelper.manager.remove(referenceLink);
        }
        QueryResult referenceByResult = EPMStructureHelper.service.navigateReferencedBy(
                (EPMDocumentMaster) epmDocument.getMaster(), null, false);
        while (referenceByResult.hasMoreElements()) {
            EPMReferenceLink referenceLink = (EPMReferenceLink) referenceByResult.nextElement();
            // 删除referenceLink
            PersistenceServerHelper.manager.remove(referenceLink);
        }
        // 查找指定EPMDoucment对象的所有EPMMemberLink
        QueryResult useResult = EPMStructureHelper.service.navigateUses(epmDocument, null, false);
        while (useResult.hasMoreElements()) {
            EPMMemberLink memberLink = (EPMMemberLink) useResult.nextElement();
            // 删除memberLink
            PersistenceServerHelper.manager.remove(memberLink);
        }
        QueryResult useByResult = EPMStructureHelper.service.navigateUsedBy(
                (EPMDocumentMaster) epmDocument.getMaster(), null, false);
        while (useByResult.hasMoreElements()) {
            EPMMemberLink memberLink = (EPMMemberLink) useByResult.nextElement();
            // 删除memberLink
            PersistenceServerHelper.manager.remove(memberLink);
        }
    }

    /**
     * 删除CAD文档与部件之间的link
     *
     * @param epmDocument
     *            EPMDocument
     * @throws WTException
     */
    private static void deleteLinkE2P(EPMDocument epmDocument) throws WTException {
        // 查找指定EPMDoucment对象的所有EPMBuildLinksRole
        QueryResult buildRoleResult = getEPMBuildLinksRoles(epmDocument);
        while (buildRoleResult.hasMoreElements()) {
            EPMBuildRule linksRule = (EPMBuildRule) buildRoleResult.nextElement();
            // 删除linksRule
            PersistenceServerHelper.manager.remove(linksRule);
        }
    }
    private static void removeFormEPMBuildHistory(EPMDocument epm) throws WTException {
		QueryResult qr = PersistenceHelper.manager.navigate(epm, EPMBuildHistory.BUILT_ROLE, EPMBuildHistory.class, false);
		while (qr.hasMoreElements()) {
			EPMBuildHistory link = (EPMBuildHistory) qr.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}
    /**
     * 删除部件的WTPartSubstituteLink对象
     *
     * @param useagelink
     *            WTPartUsageLink
     * @throws WTException
     */
    private static void deleteSubstituteLink(WTPartUsageLink useagelink)
            throws WTException {
        long linkid = PersistenceHelper.getObjectIdentifier(useagelink).getId();
        int[] index = { 0 };
        QuerySpec qs = new QuerySpec(WTPartSubstituteLink.class);
        qs.appendWhere(new SearchCondition(WTPartSubstituteLink.class, ROLEA_ID, SearchCondition.EQUAL, linkid), index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            WTPartSubstituteLink link = (WTPartSubstituteLink) qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }

    /**
     * 删除部件的WTPartAlternateLink对象
     *
     * @param useagelink
     *            WTPartAlternateLink
     * @throws WTException
     */
    private static void deleteAlternatelink(WTPartMaster childPartMaster)
            throws WTException {
        long linkid = PersistenceHelper.getObjectIdentifier(childPartMaster).getId();
        int[] index = { 0 };
        QuerySpec qs = new QuerySpec(WTPartAlternateLink.class);
        qs.appendWhere(new SearchCondition(WTPartAlternateLink.class, ROLEA_ID, SearchCondition.EQUAL, linkid), index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            WTPartAlternateLink link = (WTPartAlternateLink) qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }

    /**
     * 完成指定业务对象的流程
     *
     * @param persistable
     *            指定的业务对象
     * @param product
     *            当前产品容器
     * @throws WTException
     */
    private static void completedWorkflowForPBO(Persistable persistable, PDMLinkProduct product) throws WTException {
        /*
         * WTContainerRef containerRef = WTContainerRef.newWTContainerRef(product.getContainer());
         * QueryResult qResult = WfEngineHelper.service.getAssociatedProcesses(persistable, null, containerRef);
         * WfProcess process = null;
         * while (qResult.hasMoreElements()) {
         * process = (WfProcess) qResult.nextElement();
         * }
         */
        // 终止指定对象正在运行的所有流程
        WfEngineHelper.service.terminateObjectsRunningWorkflows(persistable);
    }

    /**
     * 通过指定对象查找其所在的所有基线，并且将此对象从所有基线中移除
     * 这里的基线包括：MaturityBaseline和AnnotationBaseline
     *
     * @param baselineable
     * @throws WTException
     */
    private static void removeFromBaseline(Baselineable baselineable) throws WTException {
        // 获取所有指定对象所在的基线
        QueryResult qResult = BaselineHelper.service.getBaselines(baselineable);
        Baseline baseline = null;
        while (qResult.hasMoreElements()) {
            baseline = (Baseline) qResult.nextElement();
            // 将对象从基线中移除,如果基线对象是ManagedBaseline，则移除对象是Baselineable。
            if (baseline instanceof ManagedBaseline) {
                BaselineHelper.service.removeFromBaseline(baselineable, baseline);
            }
        }
        // 通过指定对象查找与其相关的所有BaselineMember对象
        QueryResult qResult2 = getBaselineMembersByPer(baselineable);
        BaselineMember baselineMember = null;
        while (qResult2.hasMoreElements()) {
            baselineMember = (BaselineMember) qResult2.nextElement();
            // 获取基线对象
            Persistable persistable = baselineMember.getRoleAObject();
            if (persistable instanceof AnnotationBaseline) {
                AnnotationBaseline aBaseline = (AnnotationBaseline) persistable;
                PersistenceHelper.manager.delete(aBaseline);
            } else if (persistable instanceof MaturityBaseline) {
                MaturityBaseline maturityBaseline = (MaturityBaseline) persistable;
                PersistenceHelper.manager.delete(maturityBaseline);
            }
            // 删除基线对象
            // PersistenceHelper.manager.delete(persistable);
            PersistenceHelper.manager.delete(baselineMember);

        }
    }

    /**
     * 通过指定的对象查找其所有相关的升级对象，并从中移除。
     *
     * @param collection
     *            指定对象的集合
     * @param baselineable
     *            指定的对象
     * @throws WTException
     */
    private static void removeFromPromotionNotice(WTCollection collection, Persistable persistable) throws WTException {
        WTCollection colle = MaturityHelper.service.getPromotionNotices(collection);
        Iterator iterator = colle.iterator();
        PromotionNotice proNotice = null;
        while (iterator.hasNext()) {
            proNotice = (PromotionNotice) ((ObjectReference) iterator.next()).getObject();
            boolean isInBaseine = MaturityHelper.service.isInBaseline((Promotable) persistable, proNotice);
            MaturityBaseline baseline = proNotice.getConfiguration();
            if (isInBaseine) {
                BaselineHelper.service.removeFromBaseline((Baselineable) persistable, baseline);
            }
            WTHashSet hs = new WTHashSet();
            hs.add(persistable);
            proNotice = MaturityHelper.service.deletePromotionTargets(proNotice, hs);
            MaturityHelper.service.deletePromotionSeeds(proNotice, hs);
        }
    }

    /**
     * 通过指定对象找到其所有关联的变更请求、变更通过、问题报告，并将此对象从中删除。
     *
     * @param persistable
     * @throws ChangeException2
     * @throws WTException
     */
    public static void removeFromChange(Persistable persistable) throws ChangeException2, WTException {
        // 通过指定的变更结果对象查找与其相关的所有ChangeRecord2，并从中移除。
        QueryResult caResult = ChangeHelper2.service.getChangingChangeActivities((Changeable2) persistable, false);
        while (caResult.hasMoreElements()) {
            ChangeRecord2 changeRecord2 = (ChangeRecord2) caResult.nextElement();
            // 删除最后所得项的link
            ChangeHelper2.service.deleteChangeRecord(changeRecord2);
        }
        // 通过指定的受影响数据查找所有与其相关的AffectedActivityData，并从中移除。
        QueryResult caResult2 = ChangeHelper2.service.getAffectingChangeActivities((Changeable2) persistable, false);
        while (caResult2.hasMoreElements()) {
            AffectedActivityData affectedActivityData = (AffectedActivityData) caResult2.nextElement();
            // 删除受影响数据的link
            ChangeHelper2.service.deleteAffectedActivityData(affectedActivityData);
        }

        // 通过指定的业务对象查找与其相关的所有RelevantRequestData2对象，既是ECR与受影响对象的link
        QueryResult result = getRelevantRequestData2(persistable);
        while (result.hasMoreElements()) {
            RelevantRequestData2 relevantRequestData2 = (RelevantRequestData2) result.nextElement();
            // 删除ECR与受影响对象的link
            PersistenceHelper.manager.delete(relevantRequestData2);
        }
    }

    /**
     * 从工作区移除CAD文档和部件
     *
     * @param product
     *            产品容器
     * @throws WTException
     */
    private static QueryResult removeFromEpmWorkspace(PDMLinkProduct product) throws WTException {
        QueryResult qResult = getEpmWorkspace(product);
        ObjectVector oVector = new ObjectVector();
        while (qResult.hasMoreElements()) {
            EPMWorkspace workspace = (EPMWorkspace) qResult.nextElement();
            WTSet cadSet = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, EPMDocument.class);
            WTSet tempCadSet = new WTHashSet();
            Iterator cadSetIterator = cadSet.iterator();
            while (cadSetIterator.hasNext()) {
                EPMDocument epmDocument = (EPMDocument) ((ObjectReference) cadSetIterator.next()).getObject();
                String name = epmDocument.getName();
                if (name.endsWith("作废")) {
                    tempCadSet.add(epmDocument);
                    oVector.addElement(epmDocument);
                }
            }
            EPMWorkspaceHelper.manager.removeFromWorkspace(workspace, tempCadSet);
            WTSet partSet = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, WTPart.class);
            WTSet tempPartSet = new WTHashSet();
            Iterator partSetIterator = partSet.iterator();
            while (partSetIterator.hasNext()) {
                WTPart part = (WTPart) ((ObjectReference) partSetIterator.next()).getObject();
                String name = part.getName();
                if (name.endsWith("作废")) {
                    tempPartSet.add(part);
                    oVector.addElement(part);
                }
            }
            EPMWorkspaceHelper.manager.removeFromWorkspace(workspace, tempPartSet);
        }
        qResult = new QueryResult(oVector);
        return qResult;
    }

    /**
     * 通过指定的对象查找所有与其相关的RelevantRequestData2
     *
     * @param persistable 指定的对象
     * @return QueryResult RelevantRequestData2集合
     * @throws WTException
     */
    private static QueryResult getRelevantRequestData2(Persistable persistable) throws WTException {
        QuerySpec qSpec = new QuerySpec(RelevantRequestData2.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(persistable).getId();
        SearchCondition sCondition = new SearchCondition(RelevantRequestData2.class, ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(sCondition, index);
        return PersistenceHelper.manager.find((StatementSpec) qSpec);
    }

    /**
     * 通过子部件查找所有与其相关的WTPartUsageLink
     *
     * @param master
     * @return QueryResult WTPartUsageLink集合
     * @throws WTException
     */
    private static QueryResult getWTPartUsageLinksByChildPart(WTPartMaster master) throws WTException {
        QuerySpec qSpec = new QuerySpec(WTPartUsageLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(master).getId();
        SearchCondition scCondition = new SearchCondition(WTPartUsageLink.class, ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

    /**
     * 通过子部件查找所有与其相关的WTPartAlternateLink
     *
     * @param master
     * @return QueryResult WTPartAlternateLink集合
     * @throws WTException
     */
    private static QueryResult getWTPartAlternateLinksByChildPart(WTPartMaster master) throws WTException {
        QuerySpec qSpec = new QuerySpec(WTPartAlternateLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(master).getId();
        SearchCondition scCondition = new SearchCondition(WTPartAlternateLink.class, ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

    /**
     * 通过子部件查找所有与其相关的WTPartSubstituteLink
     *
     * @param master
     * @return QueryResult WTPartSubstituteLink集合
     * @throws WTException
     */
    private static QueryResult getWTPartSubstituteLinksByChildPart(WTPartMaster master) throws WTException {
        QuerySpec qSpec = new QuerySpec(WTPartSubstituteLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(master).getId();
        SearchCondition scCondition = new SearchCondition(WTPartSubstituteLink.class, ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

    /**
     * 通过子文档查找所有与其相关的WTDocumentUsageLink
     *
     * @param master
     * @return QueryResult WTDocumentUsageLink集合
     * @throws WTException
     */
    private static QueryResult getWTDocumentUsageLinksByChildPart(WTDocumentMaster master) throws WTException {
        QuerySpec qSpec = new QuerySpec(WTDocumentUsageLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(master).getId();
        SearchCondition scCondition = new SearchCondition(WTDocumentUsageLink.class, ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

    /**
     * 通过子文档查找所有与其相关的WTDocumentDependencyLink
     *
     * @param master
     * @return QueryResult WTDocumentDependencyLink集合
     * @throws WTException
     */
    private static QueryResult getWTDocumentDependencyLinksByChildPart(WTDocument document) throws WTException {
        QuerySpec qSpec = new QuerySpec(WTDocumentDependencyLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(document).getId();
        SearchCondition scCondition = new SearchCondition(WTDocumentDependencyLink.class, ROLEB_ID,
                SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

    /**
     * 通过指定的对象查找所有与其相关的BaselineMember
     *
     * @param persistable
     *            指定的对象
     * @return QueryResult BaselineMember集合
     * @throws WTException
     */
    private static QueryResult getBaselineMembersByPer(Persistable persistable) throws WTException {
        QuerySpec qSpec = new QuerySpec(BaselineMember.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(persistable).getId();
        SearchCondition sCondition = new SearchCondition(BaselineMember.class, ROLEB_ID, SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        return PersistenceHelper.manager.find((StatementSpec) qSpec);
    }

    /**
     * 通过指定产品容器查找其下的所有工作区对象
     *
     * @param product
     *            产品容器
     * @return QueryResult 工作区集合
     * @throws WTException
     */
    private static QueryResult getEpmWorkspace(WTContained contained) throws WTException {
        QuerySpec qSpec = new QuerySpec(EPMWorkspace.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(contained).getId();
        SearchCondition sCondition = new SearchCondition(EPMWorkspace.class, PRODUCT_ID, SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager
                .find((StatementSpec) qSpec);
        return qResult;
    }

    /**
     * 获取文档的相关部件
     *
     * @param doc
     * @return List<WTPartDescribeLink> 相关部件的link的集合
     * @throws WTException
     */
    private static List getDocDescribeLinksByDoc(WTDocument doc) throws WTException {
        List list = new ArrayList();
        QuerySpec qSpec = new QuerySpec(WTPartDescribeLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(doc).getId();
        SearchCondition sCondition = new SearchCondition(WTPartDescribeLink.class, ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        WTPartDescribeLink link = null;
        while (qResult.hasMoreElements()) {
            link = (WTPartDescribeLink) qResult.nextElement();
            list.add(link);
        }
        return list;
    }

    /**
     * 获取文档的相关部件
     *
     * @param doc
     * @return List<WTPartDescribeLink> 相关部件的link的集合
     * @throws WTException
     */
    private static List getDocReferenceLinksByDoc(WTDocument doc) throws WTException {
        List list = new ArrayList();
        QuerySpec qSpec = new QuerySpec(WTPartReferenceLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier((WTDocumentMaster) doc.getMaster()).getId();
        SearchCondition sCondition = new SearchCondition(WTPartReferenceLink.class, ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        WTPartReferenceLink link = null;
        while (qResult.hasMoreElements()) {
            link = (WTPartReferenceLink) qResult.nextElement();
            list.add(link);
        }
        return list;
    }

    /**
     * 获取文档的相关部件
     *
     * @param doc
     * @return List<WTPartDescribeLink> 相关部件的link的集合
     * @throws WTException
     */
    private static List getPartReferenceLinksByDoc(WTPart part) throws WTException {
        List list = new ArrayList();
        QuerySpec qSpec = new QuerySpec(WTPartReferenceLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(part).getId();
        SearchCondition sCondition = new SearchCondition(WTPartReferenceLink.class, ROLEA_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        WTPartReferenceLink link = null;
        while (qResult.hasMoreElements()) {
            link = (WTPartReferenceLink) qResult.nextElement();
            list.add(link);
        }
        return list;
    }

    /**
     * 通过指定的CAD文档查找与其相关的所有EPMBuildLinksRule对象。
     * EPMBuildLinksRule对象是连接CAD文档和部件的link。
     *
     * @param epmDocument
     *            指定CAD文档
     * @return EPMBuildLinksRule对象集合
     * @throws WTException
     */
    public static QueryResult getEPMBuildLinksRoles(EPMDocument epmDocument) throws WTException {
        QuerySpec qSpec = new QuerySpec(EPMBuildRule.class);
        int[] index = { 0 };
        long longId = epmDocument.getBranchIdentifier();
        SearchCondition scCondition = new SearchCondition(EPMBuildRule.class, REF_ROLEA_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

    /**
     * 通过指定的部件查找与其相关的所有EPMBuildLinksRule对象。
     * EPMBuildLinksRule对象是连接CAD文档和部件的link。
     *
     * @param part
     *            部件
     * @return EPMBuildLinksRule对象集合
     * @throws WTException
     */
    private static QueryResult getEPMBuildLinksRoles(WTPart part) throws WTException {
        QuerySpec qSpec = new QuerySpec(EPMBuildRule.class);
        int[] index = { 0 };
    	long longId = part.getBranchIdentifier();
       // long longId = PersistenceHelper.getObjectIdentifier(part).getId();
        SearchCondition scCondition = new SearchCondition(EPMBuildRule.class, REF_ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }


	/**
	 * 数据发放管理模块，当往149厂发放数据成功时，把包及包下面的数据写到数据库表中
	 *
	 * @param primaryBusinessObject
	 * @throws WTException
	 * @throws SQLException
	 */
	public static void saveCarftSignDataRecords(WTObject primaryBusinessObject,boolean savePackage) throws WTException, SQLException {
		ReferenceFactory rf = new ReferenceFactory();
		ProcessEnvelope pe = (ProcessEnvelope) primaryBusinessObject;
		ArrayList list = EnvelopeHelper.service.getAllMembers(pe);
		String pNumber = pe.getNumber();
		String pName = pe.getName();
		String pOid = rf.getReferenceString(pe);
		Date date = new Date(System.currentTimeMillis());
		Connection conn = OracleDataSource.getOracleDataSource()
				.getConnection();
		conn.setAutoCommit(false);
		Statement state = conn.createStatement();

		try {
		StringBuffer sb = new StringBuffer(
				"insert into ASES_DATA_SEND_PACKAGE_TABLE ");
		sb.append("(PACKAGE_NUM,PACKAGE_NAME,PACKAGE_SEND_TYPE,PACKAGE_OID,SEND_DATE) ");
		sb.append("values('" + pNumber + "','" + pName + "','工艺会签','" + pOid + "',date '" + date + "')");
			state.execute(sb.toString());
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < list.size(); i++) {
			StringBuffer sb2 = new StringBuffer();
			Object obj = list.get(i);
			String oNumber = ObjectProperty.getNumber(obj);
			String oName = ObjectProperty.getName(obj);
			String oVersion = ObjectProperty.getVersionIterationDisplay(obj);
			Persistable p = (Persistable) obj;
			String oOid = p.getPersistInfo().getObjectIdentifier().getStringValue();
			String pIndex = IBAHelper.getIBAStringValue((WTObject) obj, "PINDEX");
			date = new Date(System.currentTimeMillis());
			sb2.append("insert into DATA_SEND_CARFTSIGN ");
			if (pIndex ==null) {
				pIndex = "";
			}
			sb2.append("(PACKAGE_OID,OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,PRODUCT_CODE,SEND_DATE) ");
			sb2.append("values('" + pOid + "','" + oNumber + "','" + oName
					+ "','" + oVersion + "','" + oOid + "','"+pIndex+"',"+ "date '" + date + "')");
			try {
				state.execute(sb2.toString());
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		state.close();
		conn.close();
	}

	public static void saveSendInDueFormDataRecords(WTObject primaryBusinessObject,boolean savePackage) throws WTException, SQLException {
		ReferenceFactory rf = new ReferenceFactory();
		ProcessEnvelope pe = (ProcessEnvelope) primaryBusinessObject;
		ArrayList list = EnvelopeHelper.service.getAllMembers(pe);
		String pNumber = pe.getNumber();
		String pName = pe.getName();
		String pOid = rf.getReferenceString(pe);
		Date date = new Date(System.currentTimeMillis());
		Connection conn = OracleDataSource.getOracleDataSource()
				.getConnection();
		conn.setAutoCommit(false);
		Statement state = conn.createStatement();

		try {
			StringBuffer sb = new StringBuffer(
					"insert into ASES_DATA_SEND_PACKAGE_TABLE ");
			sb.append("(PACKAGE_NUM,PACKAGE_NAME,PACKAGE_SEND_TYPE,PACKAGE_OID,SEND_DATE) ");
			sb.append("values('" + pNumber + "','" + pName + "','正式发放','" + pOid + "',date '" + date + "')");
			state.execute(sb.toString());
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < list.size(); i++) {
			StringBuffer sb2 = new StringBuffer();
			Object obj = list.get(i);
			String oNumber = ObjectProperty.getNumber(obj);
			String oName = ObjectProperty.getName(obj);
			String oVersion = ObjectProperty.getVersionIterationDisplay(obj);
			Persistable p = (Persistable) obj;
			String oOid = p.getPersistInfo().getObjectIdentifier().getStringValue();
			String pIndex = IBAHelper.getIBAStringValue((WTObject) obj, "PINDEX");
			date = new Date(System.currentTimeMillis());
			sb2.append("insert into DATA_SEND_INDUEFORM ");
			if (pIndex ==null) {
				pIndex = "";
			}
			sb2.append("(PACKAGE_OID,OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,PRODUCT_CODE,SEND_DATE) ");
			sb2.append("values('" + pOid + "','" + oNumber + "','" + oName
					+ "','" + oVersion + "','" + oOid + "','"+pIndex+"',"+ " date '" + date + "')");
			try {
				state.execute(sb2.toString());
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		state.close();
		conn.close();
	}

	public static String getProcessStatus(String pboNumber, String pboType,
			String version) {
		String feetbackStr = "";
		WfProcess proc = null;
		if (pboType.equalsIgnoreCase("WTDocument")) {
			WTDocument document = (WTDocument) CmExpImpSearchHelper
					.searchLatestIteratedByNumberVersion(WTDocument.class,
							pboNumber, version);
			if(document==null) return "";
			try {
				QueryResult qrProcs = WfEngineHelper.service
						.getAssociatedProcesses(document, null, null);
				if (qrProcs.hasMoreElements()) {
					proc = (WfProcess) qrProcs.nextElement();

				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (pboType.equalsIgnoreCase("Packaged")) {
			ProcessEnvelope envelope = CmExpImpSearchHelper
					.getProcessEnvelopeByNumber(pboNumber,
							"WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.APPROVEFORM");
			if(envelope==null) return "";
			try {
				QueryResult qrProcs = WfEngineHelper.service
						.getAssociatedProcesses(envelope, null, null);
				if (qrProcs.hasMoreElements()) {
					proc = (WfProcess) qrProcs.nextElement();

				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (pboType.equalsIgnoreCase("ChangePackaged")) {
			ChangePackaged change = CmExpImpSearchHelper
					.getChangePackagedByNumber(pboNumber);
			if(change==null) return "";
			try {
				QueryResult qrProcs = WfEngineHelper.service
						.getAssociatedProcesses(change, null, null);
				if (qrProcs.hasMoreElements()) {
					proc = (WfProcess) qrProcs.nextElement();

				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (proc != null) {
			try {
				NmOid nmoid = new NmOid(proc);
				QueryResult qr = NmWorkflowHelper.service
						.getRoutingStatusData(nmoid);
				while (qr.hasMoreElements()) {
					Object obj = qr.nextElement();
					if (obj instanceof WfVotingEventAudit) {
						WfVotingEventAudit vea = (WfVotingEventAudit) obj;
						String activityName = vea.getActivityName();
						activityName = activityName.replaceAll("会签", "审查");
						String assignee = ((WTUser) vea.getAssigneeRef()
								.getPrincipal()).getFullName();
						String roleName = vea.getRole()
								.getDisplay(Locale.CHINA);
						UserEventVector voteStr = vea.getEventList();
						String comment = vea.getUserComment();
						String completeBy = assignee;
						Timestamp t = vea.getModifyTimestamp();
						Map<String, Object> feedbacbMap = new HashMap<String, Object>();

						feedbacbMap.put("activityName", activityName);
						feedbacbMap.put("assignee", assignee);
						feedbacbMap.put("roleName", roleName);
						if (voteStr.size() > 0) {
							feedbacbMap.put("voteStr", (String) voteStr.get(0));
						}
						feedbacbMap.put("comment", comment);
						feedbacbMap.put("completeBy", completeBy);
						feedbacbMap.put("Timestamp", t);
						feedbacbMap.put("isComplete", true);
						System.out.println(feedbacbMap.toString());
						feetbackStr = feetbackStr + ";;;qqq"
								+ Deserialize.serializeMap(feedbacbMap);
					} else if (obj instanceof WorkItem) {
						WorkItem item = (WorkItem) obj;
						WfAssignedActivity wfaa = (WfAssignedActivity) item
								.getSource().getObject();
						String activityName = wfaa.getName();
						activityName = activityName.replaceAll("会签", "审查");
						String assignee = item.getOwnership().getOwner()
								.getFullName();
						String roleName = item.getRole().getDisplay(
								Locale.CHINA);
						Map<String, Object> feedbacbMap = new HashMap<String, Object>();
						feedbacbMap.put("activityName", activityName);
						feedbacbMap.put("assignee", assignee);
						feedbacbMap.put("roleName", roleName);
						if (item.isComplete()) {
							String comment = item.getContext()
									.getTaskComments();
							String completeBy = assignee;
							Timestamp t = wfaa.getEndTime();
							feedbacbMap.put("comment", comment);
							feedbacbMap.put("completeBy", completeBy);
							feedbacbMap.put("Timestamp", t);
							feedbacbMap.put("isComplete", true);
							feedbacbMap.put("voteStr", "");
						} else {
							feedbacbMap.put("isComplete", false);
						}
						feetbackStr = feetbackStr + ";;;qqq"
								+ Deserialize.serializeMap(feedbacbMap);
					}
				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return feetbackStr;
	}


	 /**预审落实意见反馈
	     * @param activityOid
	     * @param implementStr
	     * @throws WTException
	     * @throws WTPropertyVetoException
	     */
	    public static void implementFeedback(String activityOid,String implementStr) throws WTException, WTPropertyVetoException {
	    	if(implementStr!=null){
	    		ReferenceFactory ref = new ReferenceFactory();
	    		WfAssignedActivity wfAa = (WfAssignedActivity)ref.getReference(activityOid).getObject();
	    		Map<String, String> implementMap = Deserialize.deserializeMap(implementStr);
	    		String previewNumber = implementMap.get("previewNumber");
	    		Preview preview = PreviewUtil.getPreviewByNumber(previewNumber);
	    		List<WTObject> members = PreviewUtil.getAllMembers(preview);
	    		for (int i = 0; i < members.size(); i++) {
	    			PreviewObject previewObj = (PreviewObject)members.get(i);
	    			String number = previewObj.getNumber();
	    			String implement = implementMap.get(number);
	    			ASESHuiqianSignature signature = ASESHuiqianSignature.newASESHuiqianSignature();
	    			signature.setOpinion(implement);
	    			signature.setActivity(activityOid);
	    			PersistenceHelper.manager.save(signature);
	    			SignLink sl = SignLink.newSignLink(previewObj, signature);
	                PersistenceHelper.manager.save(sl);
	    		}
	    	}
	        completeActivity(activityOid, "通过", "签审完成，远程调用程序完成活动");
	    }

	    /**工艺通知单、更改单会签反馈
	     * @param activityOid
	     * @param implementStr
	     * @throws WTException
	     * @throws WTPropertyVetoException
	     */
	    @SuppressWarnings("unchecked")
		public static void processNoticeFeedback(String processReviewStr) throws WTException, WTPropertyVetoException {
	    	if(processReviewStr!=null){
	    		ReferenceFactory ref = new ReferenceFactory();
	    		Map<String, String> reviewMap = Deserialize.deserializeMap(processReviewStr);
	    		String activityOid = reviewMap.get("activityOidSF");//数据发出端等待活动oid
	    		String activityOidFB = reviewMap.get("activityOidFB");//数据接收端等待活动oid
//	    		String comments = reviewMap.get("comment");
	    		String route = reviewMap.get("route");//反馈路由
	    		WfAssignedActivity wfAa = (WfAssignedActivity)ref.getReference(activityOid).getObject();
	    		ProcessData pd = wfAa.getContext();
	    		List outSignInfo = (List)pd.getValue("outSignInfo");
				if(outSignInfo == null){
					outSignInfo = new ArrayList();
				}

	    		String processNumber = reviewMap.get("PROCESSNOTICE");
	    		WTDocument document = (WTDocument) CmExpImpSearchHelper
						.searchLatestIteratedByNumber(WTDocument.class,processNumber);

    			String reviewAdvise = reviewMap.get(processNumber);//会签意见
    			String signStr = reviewMap.get(processNumber+"_sign");//会签意见
    			ASESHuiqianSignature signature = ASESHuiqianSignature.newASESHuiqianSignature();
    			signature.setOpinion(reviewAdvise);
    			signature.setActivity(activityOid);
    			signature.setSignature(signStr);
    			PersistenceHelper.manager.save(signature);
    			SignLink sl = SignLink.newSignLink(document, signature);
                PersistenceHelper.manager.save(sl);
//	                String signStr = "陈若飞/一室/2013-11-19;廖钧/一室/2013-11-19";
                String[] strs = signStr.split(";");
                for(int i = 0; i < strs.length; i++){
                	String str = strs[i];
                	NmOid oid = new NmOid();
                	HashMap<String,Object> map = new HashMap<String,Object>();
                	if(str!=null&&!"".equals(str)){
	                	String[] s = str.split("/");
	                	if(s.length>=3){
	                		map.put("signName",s[0] );
		    				map.put("signCompany",s[1] );
		    				map.put("signDate",s[2] );
		    				oid.setAdditionalInfo(map);
	                	}else{
	                		throw new WTException(str+"的格式不正确");
	                	}

                	}
    				outSignInfo.add(oid);
                }
                if("pass".equals(route)){
                	route = "通过";
                }else{
                	route = "驳回";
                }
                pd.setValue("outSignInfo", outSignInfo);
                pd.setValue("activityOidTemp", activityOidFB);
                PersistenceHelper.manager.save(wfAa);
                completeActivity(activityOid, route, "");
	    	}
	    }

	    public static  boolean isFileExist(String fileName){
	    	try {
	    		 WTProperties props = WTProperties.getLocalProperties();
	             String tempFolder = props.getProperty("wt.temp");
				 String filePath = tempFolder+File.separator+"IXBExpImp"+File.separator+fileName;
				 File file = new File(filePath);
				 return file.exists();
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return false;
	    }

	    @SuppressWarnings({ "rawtypes", "unchecked" })
		public static void deletePeOrCp(String number,String type) throws Exception {
            boolean flag = false;
	    	WTContainer container = null;
	    	String errorMsg = "";
            MethodContext mc = null;
            String context = "Unknown";
	    	try {
                mc = MethodContext.getContext(Thread.currentThread());
                if (mc == null)
                    {System.out.println("*********mc is null,new MethodContext*********");
                mc = new MethodContext(null, null);}
                if (mc.getAuthentication() == null) {
                    System.out.println("*********mc.getAuthentication() is null,new SessionAuthenticator*********");
                    SessionAuthenticator sa = new SessionAuthenticator();
                    mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
                }
                context = mc.getId().toString();
                System.out.println("**********设置上下文**********"+context);
	    	    flag = SessionServerHelper.manager.setAccessEnforced(false);
	    		System.out.println("deletePeOrCp@@@number = "+number+"@@@@type = "+type);
	    		String currentDateStr = DateUtil.getCurrentDate("yyyyMMdd");
		    	List list = new ArrayList();
		    	Set<String> hasChanged = new HashSet<String>();
		    	if("ApproveOrder".equals(type)){//送审单
					QueryResult qr = ProcessEnvelopeUtil.getProcessEnvelopeByNumber(number+"_KYJSHQ");
					if(qr.hasMoreElements()){
		    			ProcessEnvelope pe = (ProcessEnvelope)qr.nextElement();
		    			container = pe.getContainer();
		    			if(!list.contains(pe)){
		    				list.add(pe);
		    			}
		    		}
					qr = ProcessEnvelopeUtil.getProcessEnvelopeByNumber(number+"_KYGYHQ");
					if(qr.hasMoreElements()){
		    			ProcessEnvelope pe = (ProcessEnvelope)qr.nextElement();
		    			if(container == null){
		    				container = pe.getContainer();
		    			}
		    			if(!list.contains(pe)){
		    				list.add(pe);
		    			}
		    		}
					for(int i=0;i<list.size();i++){
						Object obj = list.get(i);
						if(obj instanceof ProcessEnvelope){
							ProcessEnvelope pe = (ProcessEnvelope) obj;
							if(!pe.getNumber().contains("_DEL")){
								String newNumber = pe.getNumber()+"_DEL"+currentDateStr;
								//名称增加“（作废）”
								pe.setName(pe.getName()+"（作废）");
								//编号增加“_del”+”8位日期“+”2位流水号”
								pe.setNumber(newNumber+getAutoNumber(newNumber, "ProcessEnvelope"));
							    PersistenceHelper.manager.save(pe);
							    ArrayList arraylist = EnvelopeHelper.service.getAllMembers(pe);
							    for(int j=0;j<arraylist.size();j++){
							    	RevisionControlled rc = (RevisionControlled)arraylist.get(j);
							    	changeObjNumberOrDeleteObj(rc,false,hasChanged);
							    }
							}

			    			/*QueryResult qrProcs = WfEngineHelper.service
									.getAssociatedProcesses(pe, null, null);
							while (qrProcs.hasMoreElements()) {
								WfProcess pro = (WfProcess) qrProcs.nextElement();
								PersistenceHelper.manager.delete(pro);
							}*/
							WfEngineHelper.service.terminateObjectsRunningWorkflows(pe);

						}
					}
		    	}else if("ECN".equals(type)){//更改单
					QueryResult qr = ChangePackagedUtil.getChangePackagedByNumber(number+"_KYJSHQ");
					if(qr.hasMoreElements()){
						ChangePackaged cp = (ChangePackaged)qr.nextElement();
						container = cp.getContainer();
						if(!list.contains(cp)){
		    				list.add(cp);
		    			}
		    		}
					qr = ChangePackagedUtil.getChangePackagedByNumber(number+"_KYGYHQ");
					if(qr.hasMoreElements()){
						ChangePackaged cp = (ChangePackaged)qr.nextElement();
						if(container == null){
		    				container = cp.getContainer();
		    			}
						if(!list.contains(cp)){
		    				list.add(cp);
		    			}
		    		}
					for(int i=0;i<list.size();i++){
						Object obj = list.get(i);
						if(obj instanceof ChangePackaged){
							ChangePackaged cp = (ChangePackaged) obj;
							if(!cp.getNumber().contains("_DEL")){
								String newNumber = cp.getNumber()+"_DEL"+currentDateStr;
								//名称增加“（作废）”
								cp.setName(cp.getName()+"（作废）");
								//编号增加“_del”+”8位日期“+”2位流水号”
								cp.setNumber(newNumber+getAutoNumber(newNumber, "ChangePackaged"));
								PersistenceHelper.manager.save(cp);
								QueryResult qrPc = ChangePackagedUtil.getChangeAfterDataByChangePackaged(cp);
								while(qrPc.hasMoreElements()){
									Object objPc = qrPc.nextElement();
									changeObjNumberOrDeleteObj(objPc,true,hasChanged);
								}
							}
							WfEngineHelper.service.terminateObjectsRunningWorkflows(cp);
						}
					}
		    	}else if("ECR".equals(type)){//变更申请单
		    		QueryResult qr = ChangeRequestUtil.getChangeRequestByNumber(number+"_KYJSHQ");
		    		if(qr.hasMoreElements()){
		    			ChangeRequest ecr = (ChangeRequest)qr.nextElement();
		    			container = ecr.getContainer();
						if(!list.contains(ecr)){
		    				list.add(ecr);
		    			}
		    		}
		    		qr = ChangeRequestUtil.getChangeRequestByNumber(number+"_KYGYHQ");
		    		if(qr.hasMoreElements()){
		    			ChangeRequest ecr = (ChangeRequest)qr.nextElement();
		    			if(container == null){
		    				container = ecr.getContainer();
		    			}
						if(!list.contains(ecr)){
		    				list.add(ecr);
		    			}
		    		}
		    		for(int i=0;i<list.size();i++){
						Object obj = list.get(i);
						if(obj instanceof ChangeRequest){
							ChangeRequest ecr = (ChangeRequest) obj;
							if(!ecr.getNumber().contains("_DEL")){
								String newNumber = ecr.getNumber()+"_DEL"+currentDateStr;
								ecr.setNumber(newNumber+getAutoNumber(newNumber, "ChangeRequest"));
								ecr.setName(ecr.getName()+"（作废）");
								PersistenceHelper.manager.save(ecr);
								/*QueryResult qrPc = ChangeRequestUtil.getChangeBeforeDataByChangeRequest(ecr);
								while(qrPc.hasMoreElements()){
									Object objPc = qrPc.nextElement();
									changeObjNumberOrDeleteObj(objPc,true);
								}*/
							}
							WfEngineHelper.service.terminateObjectsRunningWorkflows(ecr);
						}
					}
		    	}
			} catch (Exception e) {
				e.printStackTrace();
				errorMsg = e.getLocalizedMessage();
				System.out.println("编号更改出错:"+errorMsg);
				//设置流程全家变量的值
				WfProcess process = initiateWfProcess("149编号更改失败处理流程",new HashMap(), container, null);
				if(process !=null){
					String processOid = PersistableHelper.getOid(process);
					updateVariable(process, "number", number);
					updateVariable(process, "type", type);
					updateVariable(process, "changeNumberErrorMsg", errorMsg);
					updateVariable(process, "processOid", processOid);
				}
			}finally{
				SessionServerHelper.manager.setAccessEnforced(flag);
				if(mc != null){
				    mc.unregister();
                }
			}
	    }


	    @SuppressWarnings({ "rawtypes", "unchecked" })
		public static void teminateRequest(String number,String type) throws Exception {
            boolean flag = false;
	    	WTContainer container = null;
	    	String errorMsg = "";
            MethodContext mc = null;
            String context = "Unknown";
	    	try {
                mc = MethodContext.getContext(Thread.currentThread());
                if (mc == null) {
                    System.out.println("*********mc is null,new MethodContext*********");
                    mc = new MethodContext(null, null);
                }
                if (mc.getAuthentication() == null) {
                    System.out.println("*********mc.getAuthentication() is null,new SessionAuthenticator*********");
                    SessionAuthenticator sa = new SessionAuthenticator();
                    mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
                }
                context = mc.getId().toString();
                System.out.println("**********设置上下文**********"+context);
	    	    flag = SessionServerHelper.manager.setAccessEnforced(false);
	    		System.out.println("teminateRequest@@@number = "+number+"@@@@type = "+type);
	    		String currentDateStr = DateUtil.getCurrentDate("yyyyMMdd");
		    	List list = new ArrayList();
		    	//Set<String> hasChanged = new HashSet<String>();
		    	if("ECA".equals(type)){//送审单
					QueryResult qr = ProcessEnvelopeUtil.getProcessEnvelopeByNumber(number);
					if(qr.hasMoreElements()){
		    			ProcessEnvelope pe = (ProcessEnvelope)qr.nextElement();
		    			container = pe.getContainer();
		    			if(!list.contains(pe)){
		    				list.add(pe);
		    			}
		    		}
					qr = ProcessEnvelopeUtil.getProcessEnvelopeByNumber(number);
					if(qr.hasMoreElements()){
		    			ProcessEnvelope pe = (ProcessEnvelope)qr.nextElement();
		    			if(container == null){
		    				container = pe.getContainer();
		    			}
		    			if(!list.contains(pe)){
		    				list.add(pe);
		    			}
		    		}
					for(int i=0;i<list.size();i++){
						Object obj = list.get(i);
						if(obj instanceof ProcessEnvelope){
							ProcessEnvelope pe = (ProcessEnvelope) obj;
							if(!pe.getNumber().contains("_DEL")){
								String newNumber = pe.getNumber()+"_DEL"+currentDateStr;
								//名称增加“（作废）”
								pe.setName(pe.getName()+"（作废）");
								//编号增加“_del”+”8位日期“+”2位流水号”
								pe.setNumber(newNumber+getAutoNumber(newNumber, "ProcessEnvelope"));
							    PersistenceHelper.manager.save(pe);
							    ArrayList arraylist = EnvelopeHelper.service.getAllMembers(pe);
							    for(int j=0;j<arraylist.size();j++){
							    	RevisionControlled rc = (RevisionControlled)arraylist.get(j);
							    	changeObjNumberOrDeleteObj(rc,false);
							    }
							}

							WfEngineHelper.service.terminateObjectsRunningWorkflows(pe);

						}
					}
		    	}else if("ECN".equals(type)){//更改单
					QueryResult qr = ChangePackagedUtil.getChangePackagedByNumber(number);
					if(qr.hasMoreElements()){
						ChangePackaged cp = (ChangePackaged)qr.nextElement();
						container = cp.getContainer();
						if(!list.contains(cp)){
		    				list.add(cp);
		    			}
		    		}
					qr = ChangePackagedUtil.getChangePackagedByNumber(number);
					if(qr.hasMoreElements()){
						ChangePackaged cp = (ChangePackaged)qr.nextElement();
						if(container == null){
		    				container = cp.getContainer();
		    			}
						if(!list.contains(cp)){
		    				list.add(cp);
		    			}
		    		}
					for(int i=0;i<list.size();i++){
						Object obj = list.get(i);
						if(obj instanceof ChangePackaged){
							ChangePackaged cp = (ChangePackaged) obj;
							if(!cp.getNumber().contains("_DEL")){
								String newNumber = cp.getNumber()+"_DEL"+currentDateStr;
								//名称增加“（作废）”
								cp.setName(cp.getName()+"（作废）");
								cp.setNumber(newNumber+getAutoNumber(newNumber, "ChangePackaged"));
								PersistenceHelper.manager.save(cp);
								QueryResult qrPc = ChangePackagedUtil.getChangeAfterDataByChangePackaged(cp);
                                Set hasChanged = new HashSet();
								while(qrPc.hasMoreElements()){
									Object objPc = qrPc.nextElement();
									changeObjNumberOrDeleteObj(objPc,true,hasChanged);
								}
							}
							WfEngineHelper.service.terminateObjectsRunningWorkflows(cp);
						}
					}
		    	}
			} catch (Exception e) {
				e.printStackTrace();
				errorMsg = e.getLocalizedMessage();
				System.out.println("编号更改出错:"+errorMsg);
				//设置流程全家变量的值
				WfProcess process = initiateWfProcess("149编号更改失败处理流程",new HashMap(), container, null);
				if(process !=null){
					String processOid = PersistableHelper.getOid(process);
					updateVariable(process, "number", number);
					updateVariable(process, "type", type);
					updateVariable(process, "changeNumberErrorMsg", errorMsg);
					updateVariable(process, "processOid", processOid);
				}
			}finally{
				SessionServerHelper.manager.setAccessEnforced(flag);
				if(mc != null){
				    mc.unregister();
                }
			}
	    }
	    /**
	     * 支持循环执行
	     */
		public static void deletePeOrCpAgain(String number,String type,String processOid) throws Exception {
	    	boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
	    	WTContainer container = null;
	    	String errorMsg = "";
	    	try {
	    		System.out.println("deletePeOrCpAgain@@@number = "+number+"@@@@type = "+type);
	    		String currentDateStr = DateUtil.getCurrentDate("yyyyMMdd");
		    	List list = new ArrayList();
		    	Set<String> hasChanged = new HashSet<String>();
		    	if("ApproveOrder".equals(type)){//送审单
					QueryResult qr = ProcessEnvelopeUtil.getProcessEnvelopeByNumber(number);
					if(qr.hasMoreElements()){
		    			ProcessEnvelope pe = (ProcessEnvelope)qr.nextElement();
		    			container = pe.getContainer();
		    			if(!list.contains(pe)){
		    				list.add(pe);
		    			}
		    		}
					qr = ProcessEnvelopeUtil.getProcessEnvelopeByNumber(number+"_KYGYHQ");
					if(qr.hasMoreElements()){
		    			ProcessEnvelope pe = (ProcessEnvelope)qr.nextElement();
		    			if(container == null){
		    				container = pe.getContainer();
		    			}
		    			if(!list.contains(pe)){
		    				list.add(pe);
		    			}
		    		}
					for(int i=0;i<list.size();i++){
						Object obj = list.get(i);
						if(obj instanceof ProcessEnvelope){
							ProcessEnvelope pe = (ProcessEnvelope) obj;
							if(!pe.getNumber().contains("_DEL")){
								String newNumber = pe.getNumber()+"_DEL"+currentDateStr;
								//名称增加“（作废）”
								pe.setName(pe.getName()+"（作废）");
								//编号增加“_del”+”8位日期“+”2位流水号”
								pe.setNumber(newNumber+getAutoNumber(newNumber, "ProcessEnvelope"));
							    PersistenceHelper.manager.save(pe);
							    ArrayList arraylist = EnvelopeHelper.service.getAllMembers(pe);
							    for(int j=0;j<arraylist.size();j++){
							    	RevisionControlled rc = (RevisionControlled)arraylist.get(j);
							    	changeObjNumberOrDeleteObj(rc,false,hasChanged);
							    }
							}
						}
					}
		    	}else if("ECN".equals(type)){//更改单
					QueryResult qr = ChangePackagedUtil.getChangePackagedByNumber(number+"_KYJSHQ");
					if(qr.hasMoreElements()){
						ChangePackaged cp = (ChangePackaged)qr.nextElement();
						container = cp.getContainer();
						if(!list.contains(cp)){
		    				list.add(cp);
		    			}
		    		}
					qr = ChangePackagedUtil.getChangePackagedByNumber(number+"_KYGYHQ");
					if(qr.hasMoreElements()){
						ChangePackaged cp = (ChangePackaged)qr.nextElement();
						if(container == null){
		    				container = cp.getContainer();
		    			}
						if(!list.contains(cp)){
		    				list.add(cp);
		    			}
		    		}
					for(int i=0;i<list.size();i++){
						Object obj = list.get(i);
						if(obj instanceof ChangePackaged){
							ChangePackaged cp = (ChangePackaged) obj;
							if(!cp.getNumber().contains("_DEL")){
								String newNumber = cp.getNumber()+"_DEL"+currentDateStr;
								cp.setNumber(newNumber+getAutoNumber(newNumber, "ChangePackaged"));
								cp.setName(cp.getName()+"（作废）");
								PersistenceHelper.manager.save(cp);
								QueryResult qrPc = ChangePackagedUtil.getChangeAfterDataByChangePackaged(cp);
								while(qrPc.hasMoreElements()){
									Object objPc = qrPc.nextElement();
									changeObjNumberOrDeleteObj(objPc,true,hasChanged);
								}
							}
						}
					}
		    	}else if("ECR".equals(type)){//变更申请单
		    		QueryResult qr = ChangeRequestUtil.getChangeRequestByNumber(number+"_KYJSHQ");
		    		if(qr.hasMoreElements()){
		    			ChangeRequest ecr = (ChangeRequest)qr.nextElement();
		    			container = ecr.getContainer();
						if(!list.contains(ecr)){
		    				list.add(ecr);
		    			}
		    		}
		    		qr = ChangeRequestUtil.getChangeRequestByNumber(number+"_KYGYHQ");
		    		if(qr.hasMoreElements()){
		    			ChangeRequest ecr = (ChangeRequest)qr.nextElement();
		    			if(container == null){
		    				container = ecr.getContainer();
		    			}
						if(!list.contains(ecr)){
		    				list.add(ecr);
		    			}
		    		}
		    		for(int i=0;i<list.size();i++){
						Object obj = list.get(i);
						if(obj instanceof ChangeRequest){
							ChangeRequest ecr = (ChangeRequest) obj;
							if(!ecr.getNumber().contains("_DEL")){
								String newNumber = ecr.getNumber()+"_DEL"+currentDateStr;
								ecr.setNumber(newNumber+getAutoNumber(newNumber, "ChangeRequest"));
								ecr.setName(ecr.getName()+"（作废）");
								PersistenceHelper.manager.save(ecr);
								/*QueryResult qrPc = ChangeRequestUtil.getChangeBeforeDataByChangeRequest(ecr);
								while(qrPc.hasMoreElements()){
									Object objPc = qrPc.nextElement();
									changeObjNumberOrDeleteObj(objPc,true);
								}*/
							}
						}
					}
		    	}
			} catch (Exception e) {
				errorMsg = e.getLocalizedMessage();
				System.out.println("编号更改出错:"+errorMsg);
				//设置流程全家变量的值
				WfProcess process = (WfProcess) PersistableHelper.findPersistable(processOid);
				if(process !=null){
					updateVariable(process, "number", number);
					updateVariable(process, "type", type);
					updateVariable(process, "changeNumberErrorMsg", errorMsg);
				}else{
					LOGGER.error("未找到流程！");
				}
			}finally{
				SessionServerHelper.manager.setAccessEnforced(flag);
			}
	    }

		public static QueryResult getAllVersionWTPartByView(WTPart part, String viewname) throws Exception, WTException {
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", part.getNumber()), new int[1]);

			if ((viewname != null) && (!viewname.equals(""))) {
				qs.appendAnd();
				View view = ViewHelper.service.getView(viewname);
				qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
			}
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
			qs.setAdvancedQueryEnabled(true);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			return qr;
		}

	    public static void changeObjNumberOrDeleteObj(Object obj,boolean needDelete,Set hasChanged) throws WTException{
	    	String currentDateStr = DateUtil.getCurrentDate("yyyyMMdd");
	    	if(obj==null) return ;
	    	try {
	    		if(obj instanceof WTPart){
					WTPart part = (WTPart) obj;
					//版本
					String version = part.getVersionInfo().getIdentifier().getValue();
					WTPartMaster master = (WTPartMaster) part.getMaster();
					QueryResult qrVersion = getAllVersionWTPartByView(part,"Design");
					System.out.println(qrVersion.size());
					if(qrVersion.size()>1 && needDelete){
						//如果送审对象在149厂PDM系统中已存在多个大版本数据，则删除送审流程关联版本对象的所有小版本数据
						while(qrVersion.hasMoreElements()){
							//部件所有版本
							WTPart part_Version = (WTPart) qrVersion.nextElement();
							String version_Iteration = part_Version.getVersionInfo().getIdentifier().getValue();
							if(version_Iteration.equals(version)){
								QueryResult  part_Iterations = VersionControlHelper.service.iterationsOf(part_Version);
								while(part_Iterations.hasMoreElements()){
									WTPart part_Iteration = (WTPart)part_Iterations.nextElement();
									String iterVersion = part_Iteration.getVersionInfo().getIdentifier().getValue();
									if(iterVersion.equals(version)){
										//删除link关系
										//删除部件与部件之间的Link
						                //deleteLinkP2P(part_Iteration);
						                //删除部件与文档之间的Link
						                deleteLinkP2D(part_Iteration);
						                //删除部件与CAD文档之间的Link
						                deleteLinkP2E(part_Iteration);
										//删除部件

									}

								}
								PersistenceHelper.manager.delete(part_Version);
							}
						}
					}else if(qrVersion.size()==1){
						System.out.println("修改部件编号："+part.getNumber());
						if(!part.getNumber().contains("_DEL")){
							if(hasChanged.contains("WTPart_"+part.getNumber())){
								return ;
							}
							String partNumber = part.getNumber();
							hasChanged.add("WTPart_"+part.getNumber());
							//只有一个大版本
							WTPartMasterIdentity masterIdentity = (WTPartMasterIdentity) master.getIdentificationObject();
							String newNumber = part.getNumber()+"_DEL"+currentDateStr;
							masterIdentity.setNumber(newNumber+getAutoNumber(newNumber, "WTPart"));
							masterIdentity.setName(part.getName()+"（作废）");
							IdentityHelper.service.changeIdentity(master, masterIdentity);
							LifeCycleHelper.service.setLifeCycleState(part,State.toState("OBSOLESCENCE"));

							//删除部件与部件之间的Link
			                deleteLinkP2P(part);
			                //删除部件与文档之间的Link
			                deleteLinkP2D(part);
			                //删除部件与CAD文档之间的Link
			                deleteLinkP2E(part);

                            zuofeiProcessTaskItems(partNumber);

						}
					}
					LifeCycleHelper.service.setLifeCycleState(part,State.toState("OBSOLESCENCE"));
				}else if(obj instanceof EPMDocument){
					EPMDocument epm = (EPMDocument) obj;

					//处理epm
					//版本
					String version = epm.getVersionInfo().getIdentifier().getValue();
					EPMDocumentMaster epmmaster = (EPMDocumentMaster) epm.getMaster();
					QueryResult qrVersion = VersionControlHelper.service.allVersionsOf(epmmaster);
					if(qrVersion.size()>1 && needDelete){
						//如果送审对象在149厂PDM系统中已存在多个大版本数据，则删除送审流程关联版本对象的所有小版本数据
						EPMDocument epm_Version = (EPMDocument) qrVersion.nextElement();

						String version_Iteration = epm_Version.getVersionInfo().getIdentifier().getValue();
						if(version_Iteration.equals(version)){
							QueryResult  epm_Iterations = VersionControlHelper.service.iterationsOf(epm_Version);
							while(epm_Iterations.hasMoreElements()){
								EPMDocument epm_Iteration = (EPMDocument)epm_Iterations.nextElement();
								String iterVersion = epm_Iteration.getVersionInfo().getIdentifier().getValue();
								if(iterVersion.equals(version)){
									 //deleteLinkE2E(epm_Iteration);
						                //删除CAD文档与部件之间的Link
						             deleteLinkE2P(epm_Iteration);
						                //删除CAD BuildHistory
						             removeFormEPMBuildHistory(epm_Iteration);
								}
							}
							PersistenceHelper.manager.delete(epm_Version);
						}

                    }else if(qrVersion.size()==1){
						//只有一个大版本
						System.out.println("修改模型编号："+epm.getNumber());
						if(!epm.getNumber().contains("_DEL")){
							if(hasChanged.contains("EPMDocument_"+epm.getNumber())){
								return ;
							}
							hasChanged.add("EPMDocument_"+epm.getNumber());

							EPMDocumentMasterIdentity masterIdentity = (EPMDocumentMasterIdentity) epmmaster.getIdentificationObject();
							String newNumber = epm.getNumber()+"_DEL"+currentDateStr;
							masterIdentity.setName(epm.getName()+"（作废）");
							masterIdentity.setNumber(newNumber+"_DEL"+getAutoNumber(newNumber,"EPMDocument"));
							IdentityHelper.service.changeIdentity(epmmaster, masterIdentity);
							LifeCycleHelper.service.setLifeCycleState(epm,State.toState("OBSOLESCENCE"));
						}
					}

					LifeCycleHelper.service.setLifeCycleState(epm,State.toState("OBSOLESCENCE"));

				}else if(obj instanceof WTDocument){
					WTDocument doc = (WTDocument) obj;
					String version = doc.getVersionInfo().getIdentifier().getValue();
					WTDocumentMaster docmaster = (WTDocumentMaster) doc.getMaster();
					QueryResult qrVersion = VersionControlHelper.service.allVersionsOf(docmaster);
					System.out.println(qrVersion.size());
					if(qrVersion.size()>1 && needDelete){
						//如果送审对象在149厂PDM系统中已存在多个大版本数据，则删除送审流程关联版本对象的所有小版本数据
						//QueryResult qrIterations = VersionControlHelper.service.allIterationsOf(docmaster);
						WTDocument doc_Version = (WTDocument) qrVersion.nextElement();

						String version_Iteration = doc_Version.getVersionInfo().getIdentifier().getValue();
						if(version_Iteration.equals(version)){
							QueryResult  doc_Iterations = VersionControlHelper.service.iterationsOf(doc_Version);
							while(doc_Iterations.hasMoreElements()){
								WTDocument doc_Iteration = (WTDocument)doc_Iterations.nextElement();
								String iterVersion = doc_Iteration.getVersionInfo().getIdentifier().getValue();
								if(iterVersion.equals(version)){
									//删除link关系
									//删除文档与文档之间的Link
									deleteLinkD2D(doc_Iteration);
									//删除文档与部件之间的Link
									deleteLinkD2P(doc_Iteration);
									//删除部件
								}
							}
							PersistenceHelper.manager.delete(doc_Version);
						}
                    }else if(qrVersion.size()==1){
						//只有一个大版本
						System.out.println("修改文档编号："+doc.getNumber());
						if(!doc.getNumber().contains("_DEL")){
							if(hasChanged.contains("WTDocument_"+doc.getNumber())){
								return ;
							}
							hasChanged.add("WTDocument_"+doc.getNumber());

							WTDocumentMasterIdentity masterIdentity = (WTDocumentMasterIdentity) docmaster.getIdentificationObject();
							String newNumber = doc.getNumber()+"_DEL"+currentDateStr;
							masterIdentity.setName(doc.getName()+"（作废）");
							masterIdentity.setNumber(newNumber+getAutoNumber(newNumber, "WTDocument"));
							IdentityHelper.service.changeIdentity(docmaster, masterIdentity);
							LifeCycleHelper.service.setLifeCycleState(doc,State.toState("OBSOLESCENCE"));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				//throw new WTException(e.getLocalizedMessage());
			}
	    }



    private static void zuofeiProcessTaskItems(String partNumber) {
        DBConnUtil conn = null;
        try {
            conn = new DBConnUtil();
            String updateSql1  = "update processtask set taskstate='已作废'  where processtasknumber='"+partNumber+"'" ;
            String updateSql2  =  "update processtaskitem set taskitemstate='已作废' where processtaskitemnumber='"+partNumber+"'";
            conn.executeUpdate(updateSql1);
            conn.executeUpdate(updateSql2);
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static String  changeObjNumberOrDeleteObj(Object obj,boolean needDelete) throws WTException{
	    	String currentDateStr = DateUtil.getCurrentDate("yyyyMMdd");
	    	String flag = "";
	    	try {
	    		if(obj instanceof WTPart){
					WTPart part = (WTPart) obj;
					//版本
					String version = part.getVersionInfo().getIdentifier().getValue();
					WTPartMaster master = (WTPartMaster) part.getMaster();
					QueryResult qrVersion = getAllVersionWTPartByView(part,"Design");
					if(qrVersion.size()>1 && needDelete){
						//如果送审对象在149厂PDM系统中已存在多个大版本数据，则删除送审流程关联版本对象的所有小版本数据
						while(qrVersion.hasMoreElements()){
							//部件所有版本
							WTPart part_Version = (WTPart) qrVersion.nextElement();
							String version_Iteration = part_Version.getVersionInfo().getIdentifier().getValue();
							if(version_Iteration.equals(version)){
								QueryResult  part_Iterations = VersionControlHelper.service.iterationsOf(part_Version);
								while(part_Iterations.hasMoreElements()){
									WTPart part_Iteration = (WTPart)part_Iterations.nextElement();
									String iterVersion = part_Iteration.getVersionInfo().getIdentifier().getValue();
									if(iterVersion.equals(version)){
										removeFromBaseline(part_Iteration);
						                // 从变更中移除部件
							    	    PurgeDataProcessor.removeFromChange(part_Iteration);
										//删除部件与部件之间的Link
						                deleteLinkP2P(part_Iteration);
						                //删除部件与文档之间的Link
						                deleteLinkP2D(part_Iteration);
						                //删除部件与CAD文档之间的Link
						                deleteLinkP2E(part_Iteration);
										//删除部件
						                deleteWorkSpaceData(part_Iteration);
									}

								}
								PersistenceHelper.manager.delete(part_Version);
						    	flag = "DELETE";

							}
						}
					}else{
						System.out.println("修改部件编号："+part.getNumber());
						if(!part.getNumber().contains("_DEL")){
							//只有一个大版本
							WTPartMasterIdentity masterIdentity = (WTPartMasterIdentity) master.getIdentificationObject();
							String newNumber = part.getNumber()+"_DEL"+currentDateStr;
							masterIdentity.setNumber(newNumber+getAutoNumber(newNumber, "WTPart"));
							masterIdentity.setName(part.getName()+"（作废）");
							IdentityHelper.service.changeIdentity(master, masterIdentity);
							LifeCycleHelper.service.setLifeCycleState(part,State.toState("OBSOLESCENCE"));

					    	flag = "RENAME";
					    	removeFromBaseline(part);
				                // 从变更中移除部件
					    	PurgeDataProcessor.removeFromChange(part);
							//删除部件与部件之间的Link
			                deleteLinkP2P(part);
			                //删除部件与文档之间的Link
			                deleteLinkP2D(part);
			                //删除部件与CAD文档之间的Link
			                deleteLinkP2E(part);

				            deleteWorkSpaceData(part);

						}
					}
					LifeCycleHelper.service.setLifeCycleState(part,State.toState("OBSOLESCENCE"));
				}else if(obj instanceof EPMDocument){
					EPMDocument epm = (EPMDocument) obj;

					//处理epm
					//版本
					String version = epm.getVersionInfo().getIdentifier().getValue();
					EPMDocumentMaster epmmaster = (EPMDocumentMaster) epm.getMaster();
					QueryResult qrVersion = VersionControlHelper.service.allVersionsOf(epmmaster);
					if(needDelete){
						//如果送审对象在149厂PDM系统中已存在多个大版本数据，则删除送审流程关联版本对象的所有小版本数据
						EPMDocument epm_Version = (EPMDocument) qrVersion.nextElement();

						String version_Iteration = epm_Version.getVersionInfo().getIdentifier().getValue();
						if(version_Iteration.equals(version)){
							QueryResult  epm_Iterations = VersionControlHelper.service.iterationsOf(epm_Version);
							while(epm_Iterations.hasMoreElements()){
								EPMDocument epm_Iteration = (EPMDocument)epm_Iterations.nextElement();
								String iterVersion = epm_Iteration.getVersionInfo().getIdentifier().getValue();
								if(iterVersion.equals(version)){
									 // 从基线中移除CAD文档对象
									 removeFromBaseline(epm_Iteration);
					                // 从变更中移除CAD文档
									 PurgeDataProcessor.removeFromChange(epm_Iteration);

									 deleteLinkE2E(epm_Iteration);
						                //删除CAD文档与部件之间的Link
						             deleteLinkE2P(epm_Iteration);
						                //删除CAD BuildHistory
						             removeFormEPMBuildHistory(epm_Iteration);

						             deleteWorkSpaceData(epm_Iteration);
								}
							}
							PersistenceHelper.manager.delete(epm_Version);
					    	flag = "DELETE";
						}

					}
					/*else{
						//只有一个大版本
						System.out.println("修改模型编号："+epm.getNumber());
						if(!epm.getNumber().contains("_DEL")){

							EPMDocumentMasterIdentity masterIdentity = (EPMDocumentMasterIdentity) epmmaster.getIdentificationObject();
							String newNumber = epm.getNumber()+"_DEL"+currentDateStr;
							masterIdentity.setName(epm.getName()+"（作废）");
							masterIdentity.setNumber(newNumber+"_DEL"+getAutoNumber(newNumber,"EPMDocument"));
							IdentityHelper.service.changeIdentity(epmmaster, masterIdentity);
							LifeCycleHelper.service.setLifeCycleState(epm,State.toState("OBSOLESCENCE"));
					    	flag = "RENAME";

						}
					}

					LifeCycleHelper.service.setLifeCycleState(epm,State.toState("OBSOLESCENCE"));*/

				}else if(obj instanceof WTDocument){
					WTDocument doc = (WTDocument) obj;
					String version = doc.getVersionInfo().getIdentifier().getValue();
					WTDocumentMaster docmaster = (WTDocumentMaster) doc.getMaster();
					QueryResult qrVersion = VersionControlHelper.service.allVersionsOf(docmaster);
					System.out.println(qrVersion.size());
					if(qrVersion.size()>1 && needDelete){
						//如果送审对象在149厂PDM系统中已存在多个大版本数据，则删除送审流程关联版本对象的所有小版本数据
						//QueryResult qrIterations = VersionControlHelper.service.allIterationsOf(docmaster);
						WTDocument doc_Version = (WTDocument) qrVersion.nextElement();

						String version_Iteration = doc_Version.getVersionInfo().getIdentifier().getValue();
						if(version_Iteration.equals(version)){
							QueryResult  doc_Iterations = VersionControlHelper.service.iterationsOf(doc_Version);
							while(doc_Iterations.hasMoreElements()){
								WTDocument doc_Iteration = (WTDocument)doc_Iterations.nextElement();
								String iterVersion = doc_Iteration.getVersionInfo().getIdentifier().getValue();
								if(iterVersion.equals(version)){

									removeFromBaseline(doc_Iteration);
									PurgeDataProcessor.removeFromChange(doc_Iteration);
									//删除link关系
									//删除文档与文档之间的Link
									deleteLinkD2D(doc_Iteration);
									//删除文档与部件之间的Link
									deleteLinkD2P(doc_Iteration);
									//删除部件
								}
							}
							PersistenceHelper.manager.delete(doc_Version);
					    	flag = "DELETE";

						}
					}else{
						//只有一个大版本
						System.out.println("修改文档编号："+doc.getNumber());
						if(!doc.getNumber().contains("_DEL")){

							WTDocumentMasterIdentity masterIdentity = (WTDocumentMasterIdentity) docmaster.getIdentificationObject();
							String newNumber = doc.getNumber()+"_DEL"+currentDateStr;
							masterIdentity.setName(doc.getName()+"（作废）");
							masterIdentity.setNumber(newNumber+getAutoNumber(newNumber, "WTDocument"));
							IdentityHelper.service.changeIdentity(docmaster, masterIdentity);
							LifeCycleHelper.service.setLifeCycleState(doc,State.toState("OBSOLESCENCE"));
					    	flag = "RENAME";

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				//throw new WTException(e.getLocalizedMessage());
			}
	    	return flag;
	    }
	    /**
		 * @param epm_Iteration
	     * @throws WTException
		 */
		private static void deleteWorkSpaceData(WTContained epm_Iteration) {
	        QueryResult qResult;
			try {
				qResult = DataPurger.resultWorkspace(epm_Iteration.getContainer());
				 while (qResult.hasMoreElements()) {
			            EPMWorkspace workspace = (EPMWorkspace) qResult.nextElement();
			            WTSet tempCadSet = new WTHashSet();
			            tempCadSet.add(epm_Iteration);
			            EPMWorkspaceHelper.manager.removeFromWorkspace(workspace, tempCadSet);
			     }
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		private static WTPart getPartByEPMDocument(EPMDocument epm) throws WTException {
	    	// 查找EPM关联的部件
			QueryResult qr = PersistenceHelper.manager.navigate(epm,
					EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,
					true);
			while (qr.hasMoreElements()) {
				WTPart part = (WTPart) qr.nextElement();
				return part;
			}
			return null;
		}

	    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainer container,
	            WTObject persistable) throws Exception {
	        return initiateWfProcess(templateName, data, WTContainerRef.newWTContainerRef(container), persistable);
	    }

	    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainerRef conref,
	            WTObject persistable) throws Exception {
	        try {
	            wt.workflow.definer.WfProcessDefinition wfProcessDef = wt.workflow.definer.WfDefinerHelper.service
	                    .getProcessDefinition(templateName, conref);
	            if (wfProcessDef == null) {
	                System.out.println("the woflowtemplate named " + templateName + "doesn't exist");
	                return null;
	            }
	            WTContainer container = conref.getReferencedContainer();
	            wt.inf.team.ContainerTeam team = null;
	            if (container instanceof PDMLinkProduct) {
	                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((PDMLinkProduct) container);
	            } else if (container instanceof WTLibrary) {
	                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((WTLibrary) container);
	            } else if (container instanceof Project2) {
	                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((Project2) container);
	            }
	            wt.workflow.engine.WfProcess wfProcess = wt.workflow.engine.WfEngineHelper.service.createProcess(
	                    wfProcessDef, team, conref);
	            wfProcess.setName(templateName + "_" + System.currentTimeMillis());
	            String user = wt.auth.Authentication.getUserName();
	            wt.org.WTUser wtuser = wt.org.OrganizationServicesHelper.manager.getAuthenticatedUser(user);
	            wt.org.WTPrincipalReference ref = wt.org.WTPrincipalReference.newWTPrincipalReference(wtuser);
	            wfProcess.setCreator(ref);
	            wfProcess = WfEngineServerHelper.service.setPrimaryBusinessObject(wfProcess, persistable);
	            wt.workflow.engine.ProcessData pData = wfProcess.getContext();
	            Iterator keys = data.keySet().iterator();
	            while (keys.hasNext()) {
	                String paramName = (String) keys.next();
	                Object paramValue = data.get(paramName);
	                pData.setValue(paramName, paramValue);
	            }
	            wfProcess = wfProcess.start(pData, 0, true);
	            return wfProcess;
	        } catch (Exception e) {
	            e.printStackTrace();

	        }
	        return null;
	    }

	    public static void updateVariable(WfProcess process, String variableKey,
				Object variableValue) throws WTException {
			if (!RemoteMethodServer.ServerFlag) {
				try {
					Class cla[] = { WfProcess.class, String.class, Object.class };
					Object obj[] = { process, variableKey, variableValue };
					RemoteMethodServer.getDefault().invoke(
							"updateVariable", CmWorkflowHelper.class.getName(), null, cla, obj);
				} catch (Exception e) {
					throw new WTException(e);
				}
			}
			boolean accessEnforced = false;
			WTPrincipal principal = null;
			try {
				principal = SessionHelper.manager.getPrincipal();
				SessionHelper.manager.setAdministrator();
				accessEnforced = SessionServerHelper.manager.setAccessEnforced(accessEnforced);
				ProcessData p = process.getContext();
				p.setValue(variableKey, variableValue);
				process = (WfProcess) PersistenceHelper.manager.save(process);
				process = (WfProcess) PersistenceHelper.manager.refresh(process);
			} catch (Exception e) {
				throw new WTException(e);
			} finally {
				SessionServerHelper.manager.setAccessEnforced(accessEnforced);
				if (principal != null) {
					try {
						SessionHelper.manager.setPrincipal(principal.getName());
					} catch (Exception e) {
						LOGGER.error("切换用户错误", e);
					}
				}
			}
		}

	    public static String getAutoNumber(String number,String type){
	    	String num = "";
	    	ResultSet rs = null;
	    	try {
	    		DBConn conn = new DBConn();
	    		String sql = "select * from OBJSEQ a where a.objnumber='"+number+"' and a.objtype='"+type+"'";
	    		rs = conn.executeQuery(sql);
	    		while(rs.next()){
	    			int objindex = rs.getInt("objindex");
    				objindex++;
    				num = objindex+"";
    				if(num.length()<2){
    					num = "0"+num;
    				}
    				String sql3 = "update OBJSEQ a set a.objindex='"+ num +"' where a.objnumber='"+number+"' and a.objtype='"+type+"'";
    				conn.executeUpdate(sql3);

    				conn.commit();
    	    		conn.close();

    				return num;
	    		}
    			String sql2 = "insert into OBJSEQ (objnumber,objtype,objindex)values('"+number+"','"+type+"','1')";
    			conn.executeUpdate(sql2);
    			num = "01";

	    		conn.commit();
	    		conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return num;
	    }


	    public static void setZuofeiState(WTObject primaryBusinessObject){
	    	if(primaryBusinessObject instanceof ChangePackaged){
	    		ChangePackaged cp = (ChangePackaged)primaryBusinessObject;
	    		QueryResult qrPc;
				try {
					qrPc = ChangePackagedUtil.getChangeBeforeDataByChangePackaged(cp);
					while(qrPc.hasMoreElements()){
						Object objPc = qrPc.nextElement();
						if( objPc instanceof LifeCycleManaged){
							String  chageType = IBAHelper.getIBAStringValue((WTObject)objPc, "changeType");
							if("作废".equals(chageType)){
								ext.casc.workflow.WorkflowHelper.setObjectLifeCycle((LifeCycleManaged)objPc, "OBSOLESCENCE");
							}
						}

					}
				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	    	}

	    }

		/**
		 * @param pe
		 * @param changePackaged
		 * @param string
		 * @param string2
		 */
		public static void completeActivity(ProcessEnvelope pe, ChangePackaged changePackaged, String route, String comments) {
			 	boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
		        try {
		        	WfProcess process = null;
			        WfActivity wfactivity = null;
			        if(pe!=null){
			        	Enumeration enumeration = WfEngineHelper.service.getAssociatedProcesses(pe,WfState.OPEN_RUNNING);
						while (enumeration.hasMoreElements()) {
							process = (WfProcess) enumeration.nextElement();
							WfAssignedActivity wfAa = getOpenRunningActivity(process);

							if(wfAa!=null &&("通过监听".equals(wfAa.getName())||"驳回修改".equals(wfAa.getName()))){
								wfactivity = wfAa;
							}
						}
			        }else  if(changePackaged!=null){
			        	Enumeration enumeration = WfEngineHelper.service.getAssociatedProcesses(changePackaged,WfState.OPEN_RUNNING);
						while (enumeration.hasMoreElements()) {
							process = (WfProcess) enumeration.nextElement();
							WfAssignedActivity wfAa = getOpenRunningActivity(process);
							if(wfAa!=null &&("通过监听".equals(wfAa.getName())||"驳回修改".equals(wfAa.getName()))){
								wfactivity = wfAa;
							}
						}
			        }
		        	if(wfactivity!=null){
		        		  WorkItem workitem = getWorkItemByActivityOid(wfactivity.getPersistInfo().getObjectIdentifier().getId()+"");
				            if (workitem != null) {
				                wfactivity = (WfActivity) workitem.getSource().getObject();
				                ProcessData pd = wfactivity.getContext();
				                ProcessData pdc = pd.copy();
				                if (pdc != null) {
				                    pdc.setTaskComments(comments);
				                    workitem.setContext(pdc);
				                    workitem = (WorkItem) PersistenceHelper.manager
				                            .save(workitem);
				                }
				                Vector vector = new Vector();
				                vector.addElement(route);
				                WorkflowHelper.service.workComplete(workitem, workitem
				                        .getOwnership().getOwner(), vector);
				                WfEventHelper.createVotingEvent(null, wfactivity, workitem,
				                        workitem.getOwnership().getOwner(), comments, vector,
				                        false, workitem.isRequired());
				            }
		        	}
		        } catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
		            SessionServerHelper.manager.setAccessEnforced(accessFlag);
		        }

		}

		public static WfAssignedActivity getOpenRunningActivity(WfProcess wfprocess) throws WTException {
			Enumeration enumeration = WfEngineHelper.service.getProcessSteps(wfprocess, null);
			while (enumeration.hasMoreElements()) {
				WfActivity wfactivity = (WfActivity) enumeration.nextElement();
				if (wfactivity instanceof WfAssignedActivity) {
					WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
					String state = wfassignedactivity.getState().toString();
					if ("OPEN_RUNNING".equals(state)) {
						return wfassignedactivity;
					}

				}
			}
			return null;

		}

    public static void undoRevise(WTObject obj) {
        try {
            if(obj instanceof WTPart){
                WTPart part = (WTPart) obj;
                //版本
                String version = part.getVersionInfo().getIdentifier().getValue();
                QueryResult qrVersion = getAllVersionWTPartByView(part,"Design");
                if(qrVersion.size()>1 ){
                    //如果送审对象在149厂PDM系统中已存在多个大版本数据，则删除送审流程关联版本对象的所有小版本数据
                    WTPart part_Version = (WTPart) qrVersion.nextElement();
                    String version_Iteration = part_Version.getVersionInfo().getIdentifier().getValue();
                    if(compareVersion(version,version_Iteration)>0){
                        QueryResult  part_Iterations = VersionControlHelper.service.iterationsOf(part_Version);
                        while(part_Iterations.hasMoreElements()){
                            WTPart part_Iteration = (WTPart)part_Iterations.nextElement();
                            String iterVersion = part_Iteration.getVersionInfo().getIdentifier().getValue();
                            if(iterVersion.equals(version_Iteration)){
                                removeFromBaseline(part_Iteration);
                                // 从变更中移除部件
                                PurgeDataProcessor.removeFromChange(part_Iteration);
                                //删除部件与部件之间的Link
                                deleteLinkP2P(part_Iteration);
                                //删除部件与文档之间的Link
                                deleteLinkP2D(part_Iteration);
                                //删除部件与CAD文档之间的Link
                                deleteLinkP2E(part_Iteration);
                                //删除部件
                                deleteWorkSpaceData(part_Iteration);
                            }

                        }
                        PersistenceHelper.manager.delete(part_Version);

                    }
                }
            }else if(obj instanceof EPMDocument){
                EPMDocument epm = (EPMDocument) obj;
                //处理epm
                //版本
                String version = epm.getVersionInfo().getIdentifier().getValue();
                EPMDocumentMaster epmmaster = (EPMDocumentMaster) epm.getMaster();
                QueryResult qrVersion = VersionControlHelper.service.allVersionsOf(epmmaster);
                if(qrVersion.size()>1 ){
                    //如果送审对象在149厂PDM系统中已存在多个大版本数据，则删除送审流程关联版本对象的所有小版本数据
                    EPMDocument epm_Version = (EPMDocument) qrVersion.nextElement();

                    String version_Iteration = epm_Version.getVersionInfo().getIdentifier().getValue();
                    if(compareVersion(version,version_Iteration)>0){
                        QueryResult  epm_Iterations = VersionControlHelper.service.iterationsOf(epm_Version);
                        while(epm_Iterations.hasMoreElements()){
                            EPMDocument epm_Iteration = (EPMDocument)epm_Iterations.nextElement();
                            String iterVersion = epm_Iteration.getVersionInfo().getIdentifier().getValue();
                            if(iterVersion.equals(version_Iteration)){
                                // 从基线中移除CAD文档对象
                                removeFromBaseline(epm_Iteration);
                                // 从变更中移除CAD文档
                                PurgeDataProcessor.removeFromChange(epm_Iteration);

                                deleteLinkE2E(epm_Iteration);
                                //删除CAD文档与部件之间的Link
                                deleteLinkE2P(epm_Iteration);
                                //删除CAD BuildHistory
                                removeFormEPMBuildHistory(epm_Iteration);

                                deleteWorkSpaceData(epm_Iteration);
                            }
                        }
                        PersistenceHelper.manager.delete(epm_Version);
                    }

                }

            }else if(obj instanceof WTDocument){
                WTDocument doc = (WTDocument) obj;
                String version = doc.getVersionInfo().getIdentifier().getValue();
                WTDocumentMaster docmaster = (WTDocumentMaster) doc.getMaster();
                QueryResult qrVersion = VersionControlHelper.service.allVersionsOf(docmaster);
                if(qrVersion.size()>1 ){
                    //如果送审对象在149厂PDM系统中已存在多个大版本数据，则删除送审流程关联版本对象的所有小版本数据
                    //QueryResult qrIterations = VersionControlHelper.service.allIterationsOf(docmaster);
                    WTDocument doc_Version = (WTDocument) qrVersion.nextElement();
                    String version_Iteration = doc_Version.getVersionInfo().getIdentifier().getValue();
                    if(compareVersion(version,version_Iteration)>0){
                        QueryResult  doc_Iterations = VersionControlHelper.service.iterationsOf(doc_Version);
                        while(doc_Iterations.hasMoreElements()){
                            WTDocument doc_Iteration = (WTDocument)doc_Iterations.nextElement();
                            String iterVersion = doc_Iteration.getVersionInfo().getIdentifier().getValue();
                            if(iterVersion.equals(version_Iteration)){
                                removeFromBaseline(doc_Iteration);
                                PurgeDataProcessor.removeFromChange(doc_Iteration);
                                //删除link关系
                                //删除文档与文档之间的Link
                                deleteLinkD2D(doc_Iteration);
                                //删除文档与部件之间的Link
                                deleteLinkD2P(doc_Iteration);
                            }
                        }
                        PersistenceHelper.manager.delete(doc_Version);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int compareVersion(String ver1,String ver2){
        if(ver1.equals(ver2)){
            return 0;
        }
        if("space".equals(ver1)&&!"space".equals(ver2)){
            return 1;
        }
        if("Z".equals(ver1)&&(!"Z".equals(ver2)&&!"space".equals(ver2))){
            return 1;
        }
        if(!"space".equals(ver1)&&"space".equals(ver2)){
            return -1;
        }
        return  ver2.compareTo(ver1);
    }

}
