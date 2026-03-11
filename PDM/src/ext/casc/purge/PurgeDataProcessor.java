package ext.casc.purge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import wt.annotation.AnnotationBaseline;
import wt.change2.AffectedActivityData;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeRecord2;
import wt.change2.Changeable2;
import wt.change2.RelevantRequestData2;
import wt.change2.ReportedAgainst;
import wt.doc.WTDocument;
import wt.doc.WTDocumentDependencyLink;
import wt.doc.WTDocumentHelper;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentUsageLink;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.workspaces.EPMWorkspace;
import wt.epm.workspaces.EPMWorkspaceHelper;
import wt.fc.ObjectReference;
import wt.fc.ObjectSetVector;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.introspection.WTIntrospector;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityHelper;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.part.WTPart;
import wt.part.WTPartAlternateLink;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartReferenceLink;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineHelper;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.Baselineable;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.struct.StructHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfEngineHelper;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.wip.WIPUtils;

public class PurgeDataProcessor implements Serializable{

    private static final long serialVersionUID = 1L;
    private static final String PRODUCT_ID = "containerReference.key.id";
    private static final String ROLEB_ID = "roleBObjectRef.key.id";
    private static final String ROLEA_ID = "roleAObjectRef.key.id";
    private static final String REF_ROLEA_ID = "roleAObjectRef.key.branchId";
    private static final String REF_ROLEB_ID = "roleBObjectRef.key.branchId";

    public static FormResult execute(NmCommandBean cb){
        FormResult form = new FormResult();
        HttpSession session = cb.getRequest().getSession();
        QueryResult partResult = (QueryResult)session.getValue("partResult");
        QueryResult docResult = (QueryResult)session.getValue("docResult");
        QueryResult cadResult = (QueryResult)session.getValue("cadResult");
        QueryResult workspResult = (QueryResult)session.getValue("workspResult2");

        try {
            //删除零件
            deletePart(partResult);
            //删除文档
            deleteDoc(docResult);
            //删除图纸
            deleteCAD(cadResult);
            //删除工作区中的数据
            deleteWorkSpaceData(workspResult);

            form.setStatus(FormProcessingStatus.SUCCESS);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "作废数据处理完毕！");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
        } catch (WTException e) {
        	form.setStatus(FormProcessingStatus.FAILURE);
            FeedbackMessage message = null;
			try {
				message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "作废数据处理失败！");
			} catch (WTException e1) {
				e1.printStackTrace();
			}
            form.addFeedbackMessage(message);
            e.printStackTrace();
        }

        return form;
    }

    private static void deletePart(QueryResult partResult){
    	if(partResult == null) {
    		return;
    	}
        Transaction tx = new Transaction();
        try {
            tx.start();

            WTArrayList arrayList = null;
            WTPart wtPart = null;
            WTSet allDataSet = new WTHashSet();
            PDMLinkProduct product = null;
            while (partResult.hasMoreElements()) {
                wtPart = (WTPart) partResult.nextElement();

                //取消作为产品的主要成品的角色
                WTContainer container = wtPart.getContainer();
                if(container instanceof PDMLinkProduct) {
                	product =(PDMLinkProduct)container;
                	String endItemNumber = product.getProductNumber();
                	if(endItemNumber != null && wtPart.getNumber().equals(endItemNumber)) {
                		product.setProduct(null);
                		PersistenceHelper.manager.save(product);
                	}
                }

                // 如果对象被检出，则取消检出。如果对象是工作副本则忽略任何操作继续循环。
                if (WorkInProgressHelper.isCheckedOut(wtPart) && !WorkInProgressHelper.isWorkingCopy(wtPart)) {
                    wtPart = (WTPart) WorkInProgressHelper.service.undoCheckout(wtPart);
                } else if (WorkInProgressHelper.isWorkingCopy(wtPart) && WorkInProgressHelper.isCheckedOut(wtPart)) {
                    continue;
                }

                // 从基线中移除部件
                removeFromBaseline(wtPart);
                // 从变更中移除部件
                removeFromChange(wtPart);

                // 从升级对象中删除指定对象
                arrayList = new WTArrayList();
                arrayList.add(wtPart);
                removeFromPromotionNotice(arrayList, wtPart);

                // 删除部件与部件之间的Link
                deleteLinkP2P(wtPart);
                // 删除部件与文档之间的Link
                deleteLinkP2D(wtPart);
                // 删除部件与CAD文档之间的Link
                deleteLinkP2E(wtPart);

                Iterated iterated = VersionControlHelper.service.getLatestIteration(wtPart, false);
                allDataSet.add(iterated);
            }

            // 执行删除所有垃圾业务数据
            System.out.println("delete all data---------allDataSet:" + allDataSet);
            PersistenceHelper.manager.delete(allDataSet);

            tx.commit();
        } catch (WTException e) {
            tx.rollback();
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDoc(QueryResult docResult){
    	if(docResult == null) {
    		return;
    	}
        Transaction tx = new Transaction();
        WTArrayList arrayList = null;
        WTSet allDataSet = new WTHashSet();
        try {
            tx.start();
            System.out.println("doc--list:" + docResult.size());
            WTDocument wtDocument = null;
            while (docResult.hasMoreElements()) {
                wtDocument = (WTDocument) docResult.nextElement();
                // 如果对象被检出，则取消检出。如果对象是工作副本则忽略任何操作继续循环。
                if (WorkInProgressHelper.isCheckedOut(wtDocument) && !WorkInProgressHelper.isWorkingCopy(wtDocument)) {
                    wtDocument = (WTDocument) WorkInProgressHelper.service.undoCheckout(wtDocument);
                } else if (WorkInProgressHelper.isWorkingCopy(wtDocument)
                        && WorkInProgressHelper.isCheckedOut(wtDocument)) {
                    continue;
                }

                // 从基线中移除文档
                removeFromBaseline(wtDocument);
                // 从变更中移除文档
                removeFromChange(wtDocument);

                // 从升级对象中删除指定对象
                arrayList = new WTArrayList();
                arrayList.add(wtDocument);
                removeFromPromotionNotice(arrayList, wtDocument);

                // 删除文档与文档之间的Link
                deleteLinkD2D(wtDocument);
                // 删除文档与部件之间的Link
                deleteLinkD2P(wtDocument);

                Iterated iterated = VersionControlHelper.service.getLatestIteration(wtDocument, false);
                allDataSet.add(iterated);
            }

            // 执行删除所有垃圾业务数据
            System.out.println("delete all data---------allDataSet:" + allDataSet);
            PersistenceHelper.manager.delete(allDataSet);

            tx.commit();
        } catch (WTException e) {
            tx.rollback();
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }
    }

    private static void deleteCAD(QueryResult cadResult){
    	if(cadResult == null) {
    		return;
    	}
        Transaction tx = new Transaction();
        WTArrayList arrayList = null;
        WTSet allDataSet = new WTHashSet();
        try {
            tx.start();
            System.out.println("cad---List:" + cadResult.size());
            EPMDocument epmDocument = null;
            while (cadResult.hasMoreElements()) {
                epmDocument = (EPMDocument) cadResult.nextElement();
                // 如果对象被检出，则取消检出。如果对象是工作副本则忽略任何操作继续循环。
                if (WorkInProgressHelper.isCheckedOut(epmDocument) && !WorkInProgressHelper.isWorkingCopy(epmDocument)) {
                    epmDocument = (EPMDocument) WorkInProgressHelper.service.undoCheckout(epmDocument);
                } else if (WorkInProgressHelper.isWorkingCopy(epmDocument)
                        && WorkInProgressHelper.isCheckedOut(epmDocument)) {
                    continue;
                }

                // 从基线中移除CAD文档对象
                removeFromBaseline(epmDocument);
                // 从变更中移除CAD文档
                removeFromChange(epmDocument);

                // 从升级对象中删除指定对象
                arrayList = new WTArrayList();
                arrayList.add(epmDocument);
                removeFromPromotionNotice(arrayList, epmDocument);

                // 删除CAD文档之间的link
                deleteLinkE2E(epmDocument);
                // 删除CAD文档与部件之间的Link
                deleteLinkE2P(epmDocument);

                removeFormEPMBuildHistory(epmDocument);

                Iterated iterated = VersionControlHelper.service.getLatestIteration(epmDocument, false);
                allDataSet.add(iterated);
            }

            // 执行删除所有垃圾业务数据
            System.out.println("delete all data---------allDataSet:" + allDataSet);
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
    	if(qResult == null) {
    		return;
    	}
        ObjectVector oVector = new ObjectVector();
        while (qResult.hasMoreElements()) {
            EPMWorkspace workspace = (EPMWorkspace) qResult.nextElement();
            WTSet cadSet = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, EPMDocument.class);
            WTSet tempCadSet = new WTHashSet();
            Iterator cadSetIterator = cadSet.iterator();
            while (cadSetIterator.hasNext()) {
                EPMDocument epmDocument = (EPMDocument) ((ObjectReference) cadSetIterator.next()).getObject();
                String name = epmDocument.getName();
                if (name.contains("作废")) {
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
                if (name.contains("作废")) {
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
        System.out.println("-------------------deleteLinkP2P  part:" + part.getName());
        if (part == null) {
            System.out.println("----------part is null!");
        }
        // 指定部件作为父件，查找所有相关的WTPartUsageLink及其下面的替代件link
        QueryResult qResult = WTPartHelper.service.getUsesWTPartMasters(part);
        System.out.println("-----------qResult:" + qResult.size());
        while (qResult.hasMoreElements()) {
            // 获取子部件的link
            WTPartUsageLink link = (WTPartUsageLink) qResult.nextElement();
            System.out.println("-----------link:" + link);
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
        System.out.println("-----------qResult2:" + qResult2.size());
        while (qResult2.hasMoreElements()) {
            WTPartUsageLink link = (WTPartUsageLink) qResult2.nextElement();
            System.out.println("-----------link:" + link);
            PersistenceServerHelper.manager.remove(link);
        }
        // 指定部件作为替代件，查找所有相关的WTPartAlternateLink并删除
        QueryResult qResult3 = getWTPartAlternateLinksByChildPart((WTPartMaster) part.getMaster());
        System.out.println("-----------qResult3:" + qResult3.size());
        while (qResult3.hasMoreElements()) {
            WTPartAlternateLink link = (WTPartAlternateLink) qResult3.nextElement();
            System.out.println("-----------link:" + link);
            PersistenceServerHelper.manager.remove(link);
        }
        // 指定部件作为替代件，查找所有相关的WTPartSubstituteLink并删除
        QueryResult qResult4 = getWTPartSubstituteLinksByChildPart((WTPartMaster) part.getMaster());
        System.out.println("-----------qResult4:" + qResult4.size());
        while (qResult4.hasMoreElements()) {
            WTPartSubstituteLink link = (WTPartSubstituteLink) qResult4.nextElement();
            System.out.println("-----------link:" + link);
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
        System.out.println("-------------------deleteLinkP2D  part:" + part.getName());
        QueryResult qResult = WTPartHelper.service.getDescribedByWTDocuments(part, false);
        WTPartDescribeLink wtPartDescribeLink = null;
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            System.out.println("--------object:" + object);
            wtPartDescribeLink = (WTPartDescribeLink) object;
            PersistenceServerHelper.manager.remove(wtPartDescribeLink);
        }

        List list2 = getPartReferenceLinksByDoc(part);
        System.out.println("----------------list2:" + list2.size());
        WTPartReferenceLink wtPartReferenceLink = null;
        for (int i = 0; i < list2.size(); i++) {
            wtPartReferenceLink = (WTPartReferenceLink) list2.get(i);
            System.out.println("------------wtPartReferenceLink:" + wtPartReferenceLink);
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
        System.out.println("-------------------deleteLinkP2E  part:" + part.getName());
        QueryResult qResult = getEPMBuildLinksRoles(part);
        while (qResult.hasMoreElements()) {
            EPMBuildRule linksRule = (EPMBuildRule) qResult.nextElement();
            System.out.println("-----------linksRule:" + linksRule);
            // 删除此部件关联的CAD文档的link
            PersistenceHelper.manager.delete(linksRule);
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
        System.out.println("-------------------deleteLinkD2D  doc:" + doc.getName());
        if (doc == null) {
            System.out.println("deleteLinkD2D-------doc is null!");
        }
        // 通过指定文档查找所有与其相关的WTDocumentUsageLink并删除
        QueryResult qResult = WTDocumentHelper.service.getUsesWTDocumentUsageLinks(doc);
        System.out.println("-------qResult:" + qResult.size());
        while (qResult.hasMoreElements()) {
            WTDocumentUsageLink link = (WTDocumentUsageLink) qResult.nextElement();
            System.out.println("--------link:" + link);
            PersistenceServerHelper.manager.remove(link);
        }
        // 通过指定文档作为子文档查找所有与其相关的WTDocumentUsageLink并删除
        QueryResult qResult2 = getWTDocumentUsageLinksByChildPart((WTDocumentMaster) doc.getMaster());
        System.out.println("-------qResult2:" + qResult2.size());
        while (qResult2.hasMoreElements()) {
            WTDocumentUsageLink link = (WTDocumentUsageLink) qResult2.nextElement();
            System.out.println("--------link:" + link);
            PersistenceServerHelper.manager.remove(link);
        }
        // 通过指定文档查找所有与其相关的WTDocumentDependencyLink并删除
        QueryResult qResult3 = WTDocumentHelper.service.getHasDependentWTDocuments(doc, false);
        System.out.println("-------qResult3:" + qResult3.size());
        while (qResult3.hasMoreElements()) {
            WTDocumentDependencyLink link = (WTDocumentDependencyLink) qResult3.nextElement();
            System.out.println("--------link:" + link);
        }
        // 通过指定文档作为子文档查找所有与其相关的WTDocumentDependencyLink并删除
        QueryResult qResult4 = getWTDocumentDependencyLinksByChildPart(doc);
        System.out.println("-------qResult4:" + qResult4.size());
        while (qResult4.hasMoreElements()) {
            WTDocumentDependencyLink link = (WTDocumentDependencyLink) qResult4.nextElement();
            System.out.println("--------link:" + link);
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
        System.out.println("-------------------deleteLinkD2P  doc:" + doc.getName());
        List<WTPartDescribeLink> list = getAllWTPartDescribeLinkByDoc(doc);
        System.out.println("----------------list1:" + list.size());
        WTPartDescribeLink wtPartDescribeLink = null;
        for (int i = 0; i < list.size(); i++) {
            wtPartDescribeLink = list.get(i);
            System.out.println("--------wtPartDescribeLink:" + wtPartDescribeLink);
            PersistenceServerHelper.manager.remove(wtPartDescribeLink);
        }

//        WTArrayList wtarraylist = new WTArrayList();
//        wtarraylist.add(doc);
//        QueryResult qr = StructHelper.service.navigateDescribes(doc, false);
//        //QueryResult qr = WTPartHelper.service.getDescribesWTParts(doc, false);
//        System.out.println("----------------qr:" + qr.size());
//        WTPartDescribeLink wtPartDescribeLink = null;
//        while(qr.hasMoreElements()) {
//        	Object object = qr.nextElement();
//            wtPartDescribeLink = (WTPartDescribeLink) object;
//            System.out.println("--------wtPartDescribeLink:" + wtPartDescribeLink);
//            WTPart part = (WTPart)wtPartDescribeLink.getRoleAObject();
//            deleteRelationship(wtarraylist, part, false, true, true);
//        }

        List list2 = getDocReferenceLinksByDoc(doc);
        System.out.println("----------------list2:" + list2.size());
        WTPartReferenceLink wtPartReferenceLink = null;
        for (int i = 0; i < list2.size(); i++) {
            wtPartReferenceLink = (WTPartReferenceLink) list2.get(i);
            System.out.println("------------wtPartReferenceLink:" + wtPartReferenceLink);
            PersistenceHelper.manager.delete(wtPartReferenceLink);
        }
    }

    public static WTCollection deleteRelationship(WTCollection docs, WTPart part, boolean isRefDoc, boolean needAutoCheckIn, boolean isPartDoc)
    	    throws WTException
    	    {
    	        WTCollection objects = new WTArrayList();
    	        WTCollection links = new WTArrayList();
    	        //     be careful about inflating WTReference to a Persistable
    	        boolean deletedLink = false;
    	        boolean isCOByMe = WIPUtils.enableableObject(part);
    	        boolean isCOValid = WIPUtils.isCheckOutValid(part,WIPUtils.FULL);
    	        boolean needCI = false;
    	        if(isCOByMe || isCOValid) {
    	            if(!isCOByMe) {
    	                // Probably need to move this
    	                part = (WTPart) WIPUtils.getCheckOutObject(part);
    	                needCI = true;
    	            }
    	            if(!WorkInProgressHelper.isWorkingCopy((Workable) part)) {
    	           		part = (WTPart) WorkInProgressHelper.service.workingCopyOf(part);
    	            }

    	            for(Iterator it = docs.persistableIterator(); it.hasNext();) {
    	                if(isRefDoc) {
    	                    WTDocumentMaster docMaster = (WTDocumentMaster) it.next();
    	                    String REFERENCES_ROLE_OID = ((WTIntrospector.getLinkInfo(WTPartReferenceLink.class).isRoleA(WTPartReferenceLink.REFERENCES_ROLE)) ? WTPartReferenceLink.ROLE_AOBJECT_REF : WTPartReferenceLink.ROLE_BOBJECT_REF) +
    	                    "." +
    	                    ObjectReference.KEY;
    	                    QuerySpec qs = new QuerySpec(WTDocumentMaster.class, WTPartReferenceLink.class);
    	                    qs.appendWhere(new SearchCondition(WTPartReferenceLink.class, REFERENCES_ROLE_OID, SearchCondition.EQUAL,
    	                            PersistenceHelper.getObjectIdentifier(docMaster)),
    	                            1, 1);

    	                    QueryResult qr = PersistenceServerHelper.manager.expand(part, WTPartReferenceLink.REFERENCES_ROLE, qs, false);
    	                    if(qr.size() > 0) {
    	                        while(qr.hasMoreElements()) {
    	                            // delete reference links between parts and docs
    	                            Persistable refLink = PersistenceHelper.manager.delete((WTPartReferenceLink) qr.nextElement());
    	                            links.add(refLink);
    	                            deletedLink = true;
    	                        }
    	                    }
    	                }
    	                else {
    	                    WTDocument doc = (WTDocument) it.next();
    	                    // Navigate describe links
    	                    QueryResult qr = StructHelper.service.navigateDescribedBy(part, WTPartDescribeLink.class, false);
    	                    if(qr.size() > 0) {
    	                        while(qr.hasMoreElements()) {
    	                            // delete describe links between parts and docs
    	                            WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();
    	                            if(PersistenceHelper.isEquivalent(doc, link.getDescribedBy())) {
    	                                Persistable descLink = PersistenceHelper.manager.delete(link);
    	                                links.add(descLink);
    	                                deletedLink = true;
    	                                break;
    	                            }
    	                        }
    	                    }
    	                }
    	            }
    	            if(needAutoCheckIn && needCI) {
    	                if (deletedLink) {
    	                    // Some or all of the links were deleted
    	                    part = (WTPart) WIPUtils.getCheckInObject(part);
    	                } else {
    	                    // None of the links were deleted
    	                    part = (WTPart) WIPUtils.getUndoCheckOutObject(part);
    	                }
    	            }
    	            if (isPartDoc) {
    	                // Part to Doc actions
    	                objects.add(part);
    	                // add all docs as not deleted links
    	                objects.addAll(docs);

    	                for(Iterator it = links.persistableIterator(); it.hasNext();) {
    	                    // remove doc links that were removed from the message list
    	                    if (isRefDoc) {
    	                        WTPartReferenceLink refLink = (WTPartReferenceLink) it.next();
    	                        WTDocumentMaster docRef = (WTDocumentMaster) refLink.getRoleBObject();
    	                        objects.remove(docRef);
    	                    } else {
    	                        WTPartDescribeLink describeLink = (WTPartDescribeLink) it.next();
    	                        WTDocument docRef = (WTDocument) describeLink.getRoleBObject();
    	                        objects.remove(docRef);
    	                    }
    	                }
    	            } else {
    	                // Doc to Part actions
    	                // For doc part if link size is zero no link created, add weak side part
    	                if (links.size() == 0) {
    	                    objects.add(part);
    	                }
    	            }
    	        } else {
    	            if (!isPartDoc) {
    	                objects.add(part);
    	            }
    	        }
    	        return objects;
    	    }

    private static QueryResult getDescribeAssociations(WTPart wtpart, WTDocumentMaster wtdocumentmaster) throws WTException {
        QueryResult queryresult = new QueryResult();
        QuerySpec queryspec = new QuerySpec(WTPartDescribeLink.class);
        queryspec.appendClassList(WTDocument.class, true);
        queryspec.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleAObjectRef.key", "=", PersistenceHelper.getObjectIdentifier(wtpart)), new int[] {
            0
        });
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] {
            0, 1
        });
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(WTDocument.class, "masterReference.key", "=", PersistenceHelper.getObjectIdentifier(wtdocumentmaster)), new int[] {
            1
        });
        QueryResult queryresult1 = PersistenceHelper.manager.find((StatementSpec)queryspec);
        Vector vector = new Vector();
        WTPartDescribeLink wtpartdescribelink;
        for(; queryresult1.hasMoreElements(); vector.add(wtpartdescribelink)) {
            Object aobj[] = (Object[])(Object[])queryresult1.nextElement();
            wtpartdescribelink = (WTPartDescribeLink)aobj[0];
            try {
                wtpartdescribelink.setDescribes(wtpart);
                wtpartdescribelink.setDescribedBy((WTDocument)aobj[1]);
            } catch(WTPropertyVetoException wtpropertyvetoexception) {
                throw new WTException(wtpropertyvetoexception);
            }
        }

        queryresult.append(new ObjectSetVector(vector));
        return queryresult;
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
        System.out.println("-------------------deleteLinkE2E  epmDocument:" + epmDocument);
        // 查找指定EPMDoucment对象的所有EPMReferenceLink
        QueryResult referenceResult = EPMStructureHelper.service.navigateReferences(epmDocument, null, false);
        System.out.println("-----------referenceResult:" + referenceResult.size());
        while (referenceResult.hasMoreElements()) {
            EPMReferenceLink referenceLink = (EPMReferenceLink) referenceResult.nextElement();
            System.out.println("-----------referenceLink:" + referenceLink);
            // 删除referenceLink
            PersistenceServerHelper.manager.remove(referenceLink);
        }
        QueryResult referenceByResult = EPMStructureHelper.service.navigateReferencedBy(
                (EPMDocumentMaster) epmDocument.getMaster(), null, false);
        System.out.println("-----------referenceByResult:" + referenceByResult.size());
        while (referenceByResult.hasMoreElements()) {
            EPMReferenceLink referenceLink = (EPMReferenceLink) referenceByResult.nextElement();
            System.out.println("-----------referenceLink:" + referenceLink);
            // 删除referenceLink
            PersistenceServerHelper.manager.remove(referenceLink);
        }
        // 查找指定EPMDoucment对象的所有EPMMemberLink
        QueryResult useResult = EPMStructureHelper.service.navigateUses(epmDocument, null, false);
        System.out.println("-----------useResult:" + useResult.size());
        while (useResult.hasMoreElements()) {
            EPMMemberLink memberLink = (EPMMemberLink) useResult.nextElement();
            System.out.println("---------memberLink:" + memberLink);
            // 删除memberLink
            PersistenceServerHelper.manager.remove(memberLink);
        }
        QueryResult useByResult = EPMStructureHelper.service.navigateUsedBy(
                (EPMDocumentMaster) epmDocument.getMaster(), null, false);
        System.out.println("-----------useByResult:" + useByResult.size());
        while (useByResult.hasMoreElements()) {
            EPMMemberLink memberLink = (EPMMemberLink) useByResult.nextElement();
            System.out.println("---------memberLink:" + memberLink);
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
        System.out.println("-------------------deleteLinkE2P  epmDocument:" + epmDocument);
        // 查找指定EPMDoucment对象的所有EPMBuildLinksRole
        QueryResult buildRoleResult = getEPMBuildLinksRoles(epmDocument);
        System.out.println("----------buildRoleResult:" + buildRoleResult.size());
        while (buildRoleResult.hasMoreElements()) {
            EPMBuildRule linksRule = (EPMBuildRule) buildRoleResult.nextElement();
            System.out.println("----------linksRule:" + linksRule);
            // 删除linksRule
            PersistenceServerHelper.manager.remove(linksRule);
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
        System.out.println("-------------------deleteSubstituteLink");
        long linkid = PersistenceHelper.getObjectIdentifier(useagelink).getId();
        int[] index = { 0 };
        QuerySpec qs = new QuerySpec(WTPartSubstituteLink.class);
        qs.appendWhere(new SearchCondition(WTPartSubstituteLink.class, ROLEA_ID, SearchCondition.EQUAL, linkid), index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            WTPartSubstituteLink link = (WTPartSubstituteLink) qr.nextElement();
            System.out.println("WTPartSubstituteLink---------link:" + link);
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
        System.out.println("-------------------deleteAlternatelink");
        long linkid = PersistenceHelper.getObjectIdentifier(childPartMaster).getId();
        int[] index = { 0 };
        QuerySpec qs = new QuerySpec(WTPartAlternateLink.class);
        qs.appendWhere(new SearchCondition(WTPartAlternateLink.class, ROLEA_ID, SearchCondition.EQUAL, linkid), index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            WTPartAlternateLink link = (WTPartAlternateLink) qr.nextElement();
            System.out.println("WTPartAlternateLink---------link:" + link);
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
        System.out.println("completedWorkflowForPBO---------persistable:" + persistable + "  product:" + product);
        /*
         * WTContainerRef containerRef = WTContainerRef.newWTContainerRef(product.getContainer());
         * QueryResult qResult = WfEngineHelper.service.getAssociatedProcesses(persistable, null, containerRef);
         * WfProcess process = null;
         * while (qResult.hasMoreElements()) {
         * process = (WfProcess) qResult.nextElement();
         * System.out.println("-------process:" + process);
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
    public static void removeFromBaseline(Baselineable baselineable) throws WTException {
        System.out.println("removeFromBaseline-------------baselineable:" + baselineable);
        // 获取所有指定对象所在的基线
        QueryResult qResult = BaselineHelper.service.getBaselines(baselineable);
        Baseline baseline = null;
        while (qResult.hasMoreElements()) {
            baseline = (Baseline) qResult.nextElement();
            System.out.println("--------baseline:" + baseline);
            // 将对象从基线中移除,如果基线对象是ManagedBaseline，则移除对象是Baselineable。
            if (baseline instanceof ManagedBaseline) {
                BaselineHelper.service.removeFromBaseline(baselineable, baseline);
            }
        }
        // 通过指定对象查找与其相关的所有BaselineMember对象
        QueryResult qResult2 = getBaselineMembersByPer(baselineable);
        System.out.println("************qResult2:" + qResult2.size());
        BaselineMember baselineMember = null;
        while (qResult2.hasMoreElements()) {
            baselineMember = (BaselineMember) qResult2.nextElement();
            // 获取基线对象
            Persistable persistable = baselineMember.getRoleAObject();
            System.out.println("---------delete baselineMember:" + baselineMember + "-------baseline:" + persistable);
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
    public static void removeFromPromotionNotice(WTCollection collection, Persistable persistable) throws WTException {
        WTCollection colle = MaturityHelper.service.getPromotionNotices(collection);
        Iterator iterator = colle.iterator();
        PromotionNotice proNotice = null;
        while (iterator.hasNext()) {
            proNotice = (PromotionNotice) ((ObjectReference) iterator.next()).getObject();
            System.out.println("----------proNotice:" + proNotice);
            boolean isInBaseine = MaturityHelper.service.isInBaseline((Promotable) persistable, proNotice);
            MaturityBaseline baseline = proNotice.getConfiguration();
            if (isInBaseine) {
                System.out.println("---------delete from MaturityBaseline");
                BaselineHelper.service.removeFromBaseline((Baselineable) persistable, baseline);
            }
            WTHashSet hs = new WTHashSet();
            hs.add(persistable);
            System.out.println("---------delete from PromotionNotice");
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
        System.out.println("****************caResult:" + caResult.size());
        while (caResult.hasMoreElements()) {
            ChangeRecord2 changeRecord2 = (ChangeRecord2) caResult.nextElement();
            System.out.println("****************delete changeRecord2:" + changeRecord2);
            // 删除最后所得项的link
            ChangeHelper2.service.deleteChangeRecord(changeRecord2);
        }

        QueryResult crResult = getChangerecord2ByPer(persistable);
        while(crResult.hasMoreElements()) {
        	ChangeRecord2 changeRecord2 = (ChangeRecord2) crResult.nextElement();
            System.out.println("****************delete changeRecord2:" + changeRecord2);
            PersistenceHelper.manager.delete(changeRecord2);
        }

        // 通过指定的受影响数据查找所有与其相关的AffectedActivityData，并从中移除。
        QueryResult caResult2 = ChangeHelper2.service.getAffectingChangeActivities((Changeable2) persistable, false);
        System.out.println("**************caResult2:" + caResult2.size());
        while (caResult2.hasMoreElements()) {
            AffectedActivityData affectedActivityData = (AffectedActivityData) caResult2.nextElement();
            System.out.println("***************delete affectedActivityData:" + affectedActivityData);
            // 删除受影响数据的link
            ChangeHelper2.service.deleteAffectedActivityData(affectedActivityData);
        }

        QueryResult qr3 = getAffectedactivitydataByPer(persistable);
        System.out.println("**************qr3:" + qr3.size());
        while (qr3.hasMoreElements()) {
            AffectedActivityData affectedActivityData = (AffectedActivityData) qr3.nextElement();
            Object object = affectedActivityData.getRoleAObject();
            System.out.println("**************object:" + object);
//            if(object instanceof WTChangeOrder2) {
//            	continue;
//            }
            System.out.println("***************delete affectedActivityData:" + affectedActivityData);
            // 删除受影响数据的link
            //ChangeHelper2.service.deleteAffectedActivityData(affectedActivityData);
            PersistenceHelper.manager.delete(affectedActivityData);
        }

        // 通过指定的业务对象查找与其相关的所有RelevantRequestData2对象，既是ECR与受影响对象的link
        QueryResult result = getRelevantRequestData2(persistable);
        System.out.println("****************result:" + result.size());
        while (result.hasMoreElements()) {
            RelevantRequestData2 relevantRequestData2 = (RelevantRequestData2) result.nextElement();
            System.out.println("*************delete relevantRequestData2:" + relevantRequestData2);
            // 删除ECR与受影响对象的link
            PersistenceHelper.manager.delete(relevantRequestData2);
        }

        //从问题报告中移除
        QueryResult qr = getReportedAgainstByPer(persistable);
        System.out.println("****************qr:" + qr.size());
        while(qr.hasMoreElements()) {
        	Object obj = qr.nextElement();
        	System.out.println("****************obj:" + obj);
        	if(obj instanceof ReportedAgainst) {
        		ReportedAgainst ra = (ReportedAgainst)obj;
        		PersistenceHelper.manager.delete(ra);
        		System.out.println("****************delete ra:" + ra);
        	}

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
        System.out.println("------removeFromEpmWorkspace product:" + product.getName());
        QueryResult qResult = getEpmWorkspace(product);
        ObjectVector oVector = new ObjectVector();
        System.out.println("------workspace count:" + qResult.size());
        while (qResult.hasMoreElements()) {
            EPMWorkspace workspace = (EPMWorkspace) qResult.nextElement();
            WTSet cadSet = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, EPMDocument.class);
            System.out.println("------cadSet1:" + cadSet.size());
            WTSet tempCadSet = new WTHashSet();
            Iterator cadSetIterator = cadSet.iterator();
            while (cadSetIterator.hasNext()) {
                EPMDocument epmDocument = (EPMDocument) ((ObjectReference) cadSetIterator.next()).getObject();
                String name = epmDocument.getName();
                System.out.println("---------epmDocument name:" + name);
                if (name.contains("作废")) {
                    tempCadSet.add(epmDocument);
                    oVector.addElement(epmDocument);
                }
            }
            System.out.println("------tempCadSet:" + tempCadSet.size());
            EPMWorkspaceHelper.manager.removeFromWorkspace(workspace, tempCadSet);
            WTSet partSet = EPMWorkspaceHelper.manager.getObjectsInWorkspace(workspace, WTPart.class);
            System.out.println("------partSet:" + partSet.size());
            WTSet tempPartSet = new WTHashSet();
            Iterator partSetIterator = partSet.iterator();
            while (partSetIterator.hasNext()) {
                WTPart part = (WTPart) ((ObjectReference) partSetIterator.next()).getObject();
                String name = part.getName();
                System.out.println("---------part name:" + name);
                if (name.contains("作废")) {
                    tempPartSet.add(part);
                    oVector.addElement(part);
                }
            }
            System.out.println("------tempPartSet:" + tempPartSet.size());
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

    private static List<WTPartDescribeLink> getAllWTPartDescribeLinkByDoc(WTDocument doc) throws WTException {
    	List<WTPartDescribeLink> allList = new ArrayList<WTPartDescribeLink>();
    	QueryResult qr = VersionControlHelper.service.allIterationsOf(doc.getMaster());
    	while(qr.hasMoreElements()) {
    		WTDocument document = (WTDocument)qr.nextElement();
    		allList.addAll(getDocDescribeLinksByDoc(document));
    	}
    	return allList;
    }

    /**
     * 获取文档的相关部件
     *
     * @param doc
     * @return List<WTPartDescribeLink> 相关部件的link的集合
     * @throws WTException
     */
    private static List<WTPartDescribeLink> getDocDescribeLinksByDoc(WTDocument doc) throws WTException {
        List<WTPartDescribeLink> list = new ArrayList<WTPartDescribeLink>();
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
    private static QueryResult getEPMBuildLinksRoles(EPMDocument epmDocument) throws WTException {
        QuerySpec qSpec = new QuerySpec(EPMBuildRule.class);
        int[] index = { 0 };
        long longId = epmDocument.getBranchIdentifier();
        System.out.println("--------------longId:" + longId);
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
        long longId = PersistenceHelper.getObjectIdentifier(part).getId();
        SearchCondition scCondition = new SearchCondition(EPMBuildRule.class, REF_ROLEB_ID, SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(scCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        return qResult;
    }

    public static QueryResult getAffectedactivitydataByPer(Persistable per) throws WTException {
    	QuerySpec qs = new QuerySpec(AffectedActivityData.class);
    	long roleBId = PersistenceHelper.getObjectIdentifier(per).getId();
    	int[] index = { 0 };
    	SearchCondition sc = new SearchCondition(AffectedActivityData.class,"roleBObjectRef.key.id",SearchCondition.EQUAL,roleBId);
    	qs.appendWhere(sc, index);
    	return PersistenceHelper.manager.find((StatementSpec)qs);
    }

    public static QueryResult getReportedAgainstByPer(Persistable per) throws WTException {
    	QuerySpec qs = new QuerySpec(ReportedAgainst.class);
    	long roleBId = PersistenceHelper.getObjectIdentifier(per).getId();
    	int[] index = { 0 };
    	SearchCondition sc = new SearchCondition(ReportedAgainst.class,"roleBObjectRef.key.id",SearchCondition.EQUAL,roleBId);
    	qs.appendWhere(sc, index);
    	return PersistenceHelper.manager.find((StatementSpec)qs);
    }

    public static QueryResult getChangerecord2ByPer(Persistable per) throws WTException {
    	QuerySpec qs = new QuerySpec(ChangeRecord2.class);
    	long roleBId = PersistenceHelper.getObjectIdentifier(per).getId();
    	int[] index = { 0 };
    	SearchCondition sc = new SearchCondition(ChangeRecord2.class,"roleBObjectRef.key.id",SearchCondition.EQUAL,roleBId);
    	qs.appendWhere(sc, index);
    	return PersistenceHelper.manager.find((StatementSpec)qs);
    }

	private static void removeFormEPMBuildHistory(EPMDocument epm) throws WTException {
		QueryResult qr = PersistenceHelper.manager.navigate(epm, EPMBuildHistory.BUILT_ROLE, EPMBuildHistory.class, false);
		while (qr.hasMoreElements()) {
			EPMBuildHistory link = (EPMBuildHistory) qr.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}
}
