package ext.casc.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wt.annotation.AnnotationBaseline;
import wt.build._BuildHistory;
import wt.change2.AffectedActivityData;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeRecord2;
import wt.change2.Changeable2;
import wt.change2.RelevantRequestData2;
import wt.doc.WTDocument;
import wt.doc.WTDocumentDependencyLink;
import wt.doc.WTDocumentHelper;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentUsageLink;
import wt.doc._WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm._EPMDocument;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.workspaces.EPMWorkspace;
import wt.epm.workspaces.EPMWorkspaceHelper;
import wt.fc.ObjectReference;
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
import wt.introspection.LinkInfo;
import wt.introspection.WTIntrospector;
import wt.maturity.MaturityBaseline;
import wt.maturity.MaturityHelper;
import wt.maturity.Promotable;
import wt.maturity.PromotionNotice;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.part.WTPartAlternateLink;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartReferenceLink;
import wt.part.WTPartSubstituteLink;
import wt.part.WTPartUsageLink;
import wt.part._WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.VersionToVersionLink;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineHelper;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.Baselineable;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.engine.WfEngineHelper;

/**
 * 删除系统中业务对象
 * @author TUWENBIN
 *
 */
public class PurgeDataUtil implements RemoteAccess {

	private static final long serialVersionUID = 1L;
	private static final String PRODUCT_ID = "containerReference.key.id";
	private static final String ROLEB_ID = "roleBObjectRef.key.id";
	private static final String ROLEA_ID = "roleAObjectRef.key.id";
	private static final String REF_ROLEA_ID = "roleAObjectRef.key.branchId";
	private static final String REF_ROLEB_ID = "roleBObjectRef.key.branchId";



	public static void process() throws WTException{

		QueryResult partQr = getPartForContainer() ;
		System.out.println(partQr.size()) ;
		QueryResult epmQr = getEPMForContainer() ;
		System.out.println(epmQr.size()) ;


		QueryResult workSpaceQr = getEPMWorkspace() ;
		// 删除工作区中的数据
		deleteWorkSpaceData(workSpaceQr);
		// 删除零件
		deletePart(partQr);
		deleteCAD(epmQr);

		QueryResult docResult = getDocForContainer() ;
		System.out.println(docResult.size()) ;
		deleteDoc(docResult);


	}




	/**
	 * 查询所有工作区
	 * @return
	 * @throws WTException
	 */
	public static QueryResult getEPMWorkspace() throws WTException{
		QuerySpec qs = new QuerySpec(EPMWorkspace.class) ;
		QueryResult qr = PersistenceHelper.manager.find(qs);
		return qr ;
	}

	/**
	 * 根据容器查询部件
	 * @param container
	 * @return
	 * @throws WTException
	 */
	private static QueryResult getPartForContainer() throws WTException{
    	QuerySpec qs = new QuerySpec(WTPart.class);
        qs.appendWhere(new SearchCondition(WTPart.class, "master>name", SearchCondition.LIKE, "%作废%"), new int[1]);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        LatestConfigSpec latestConfigSpec = new LatestConfigSpec() ;
        QueryResult qrProcess = latestConfigSpec.process(qr) ;
        return qrProcess ;
	}

	private static QueryResult getDocForContainer() throws WTException{
    	QuerySpec qs = new QuerySpec(WTDocument.class);
        qs.appendWhere(new SearchCondition(WTDocument.class, "master>name", SearchCondition.LIKE, "%作废%"), new int[1]);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        LatestConfigSpec latestConfigSpec = new LatestConfigSpec() ;
        QueryResult qrProcess = latestConfigSpec.process(qr) ;
        return qrProcess ;
	}

	/**
	 * 根据容器查询模型
	 * @param container
	 * @return
	 * @throws WTException
	 */
	private static QueryResult getEPMForContainer() throws WTException{
    	QuerySpec qs = new QuerySpec(EPMDocument.class);
        qs.appendWhere(new SearchCondition(EPMDocument.class, "master>name", SearchCondition.LIKE, "%作废%"), new int[1]);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        LatestConfigSpec latestConfigSpec = new LatestConfigSpec() ;
        QueryResult qrProcess = latestConfigSpec.process(qr) ;
        return qrProcess ;
	}

	private static WTSet deletePart(QueryResult partResult) {
		//Transaction tx = new Transaction();
		WTSet allDataSet = new WTHashSet();
		try {
			//tx.start();

			WTArrayList arrayList = null;
			WTPart wtPart = null;
			while (partResult.hasMoreElements()) {
				wtPart = (WTPart) partResult.nextElement();
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

				System.out.println("删除部件：" + wtPart.getNumber() + "," + wtPart.getVersionIdentifier().getValue() + "." + wtPart.getIterationIdentifier().getValue()) ;
				try {
					wtPart = (WTPart)PersistenceHelper.manager.refresh(wtPart);
					PersistenceHelper.manager.delete(wtPart) ;
				}catch (Exception e) {
					//tx.rollback();
					e.printStackTrace();
					System.out.println("删除EPM异常：" + e.getMessage()) ;
				}
				//Iterated iterated = VersionControlHelper.service.getLatestIteration(wtPart, false);
				//allDataSet.add(iterated);
			}

			// 执行删除所有垃圾业务数据
			System.out.println("delete all data---------allDataSet:" + allDataSet);
			//tx.commit();
		} catch (WTException e) {
			//tx.rollback();
			e.printStackTrace();
			System.out.println("删除部件异常：" + e.getMessage()) ;
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
			System.out.println("删除部件异常：" + e.getMessage()) ;
		}
		return allDataSet ;
	}

	private static void deleteDoc(QueryResult docResult) {
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

	private static WTSet deleteCAD(QueryResult cadResult) {

		//Transaction tx = new Transaction();
		WTArrayList arrayList = null;
		WTSet allDataSet = new WTHashSet();
		try {
			//tx.start();
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

				System.out.println("删除EPM：" + epmDocument.getNumber() + "," + epmDocument.getVersionIdentifier().getValue() + "." + epmDocument.getIterationIdentifier().getValue()) ;
				try {
					PersistenceHelper.manager.delete(epmDocument) ;
				}catch (Exception e) {
					//tx.rollback();
					e.printStackTrace();
					System.out.println("删除EPM异常：" + e.getMessage()) ;
				}
				//Iterated iterated = VersionControlHelper.service.getLatestIteration(epmDocument, false);
				//allDataSet.add(iterated);
			}

			// 执行删除所有垃圾业务数据
			System.out.println("delete all data---------allDataSet:" + allDataSet);

			//tx.commit();
		} catch (WTException e) {
			//tx.rollback();
			e.printStackTrace();
			System.out.println("删除EPM异常：" + e.getMessage()) ;
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
			System.out.println("删除EPM异常：" + e.getMessage()) ;
		}
		return allDataSet ;
	}

	private static void deleteWorkSpaceData(QueryResult qResult) throws WTException {
		ObjectVector oVector = new ObjectVector();
		while (qResult.hasMoreElements()) {
			Object obj = qResult.nextElement();
			System.out.println("******************* obj :" + obj);
			if (!(obj instanceof EPMWorkspace)) {
				continue;
			}
			EPMWorkspace workspace = (EPMWorkspace) obj;
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
		if (part == null) {
			System.out.println("deleteLinkP2D--------part is null!");
		}
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
	public static void deleteLinkD2D(WTDocument doc) throws WTException {
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
	public static void deleteLinkD2P(WTDocument doc) throws WTException {
		System.out.println("-------------------deleteLinkD2P  doc:" + doc.getName());
		List list = getDocDescribeLinksByDoc(doc);
		System.out.println("----------------list1:" + list.size());
		WTPartDescribeLink wtPartDescribeLink = null;
		for (int i = 0; i < list.size(); i++) {
			wtPartDescribeLink = (WTPartDescribeLink) list.get(i);
			System.out.println("-------------wtPartDescribeLink:" + wtPartDescribeLink);
			PersistenceServerHelper.manager.remove(wtPartDescribeLink);
		}

		List list2 = getDocReferenceLinksByDoc(doc);
		System.out.println("----------------list2:" + list2.size());
		WTPartReferenceLink wtPartReferenceLink = null;
		for (int i = 0; i < list2.size(); i++) {
			wtPartReferenceLink = (WTPartReferenceLink) list2.get(i);
			System.out.println("------------wtPartReferenceLink:" + wtPartReferenceLink);
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
	private static void deleteLinkE2E(EPMDocument epmDocument) throws WTException {
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
			// add at 2012.11.20 移除失败
			try {
				PersistenceServerHelper.manager.remove(referenceLink);
			} catch (WTException e) {
				System.out.println("--------Exception------");
				e.printStackTrace();
			}
			// PersistenceHelper.manager.delete(referenceLink);
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
	private static void deleteSubstituteLink(WTPartUsageLink useagelink) throws WTException {
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
	private static void deleteAlternatelink(WTPartMaster childPartMaster) throws WTException {
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
		 * WTContainerRef containerRef =
		 * WTContainerRef.newWTContainerRef(product.getContainer()); QueryResult
		 * qResult = WfEngineHelper.service.getAssociatedProcesses(persistable,
		 * null, containerRef); WfProcess process = null; while
		 * (qResult.hasMoreElements()) { process = (WfProcess)
		 * qResult.nextElement(); System.out.println("-------process:" +
		 * process); }
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
	private static void removeFromBaseline(Baselineable baselineableAll) throws WTException {
		List<Baselineable> list = new ArrayList<Baselineable>() ;
		if(baselineableAll instanceof WTPart){
			WTPart allParts = (WTPart) baselineableAll ;
			list = getPartsAllIteration(allParts.getNumber()) ;
		}else if(baselineableAll instanceof WTDocument){
			WTDocument doc = (WTDocument)baselineableAll ;
			list = getDocsAllIteration(doc.getNumber()) ;
		}else if(baselineableAll instanceof EPMDocument){
			EPMDocument epm = (EPMDocument)baselineableAll ;
			list = getEPMAllIteration(epm.getNumber()) ;
		}

		for(Baselineable baselineable : list){
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

	// add for check
	private static void addCheckFunction(Changeable2 paramChangeable2) {
		System.out.println("----------------checkFunction-------------------");
		Class paramClass = AffectedActivityData.class;
		String paramString = "theChangeActivity2";
		try {
			LinkInfo localLinkInfo = WTIntrospector.getLinkInfo(paramClass);
			String str = null;
			boolean bool = VersionToVersionLink.class.isAssignableFrom(paramClass);
			long longId = PersistenceHelper.getObjectIdentifier(paramChangeable2).getId();
			if (localLinkInfo.isRoleA(paramString))
				str = bool ? "roleBObjectRef.key.branchId" : "roleBObjectRef.key.id";
			else if (localLinkInfo.isRoleB(paramString))
				str = bool ? "roleAObjectRef.key.branchId" : "roleAObjectRef.key.id";
			else {
				throw new WTRuntimeException(new StringBuilder().append(" Role ").append(paramString)
						.append(" doesn't exist for ").append(paramClass.getName()).toString());
			}
			QuerySpec localQuerySpec1 = new QuerySpec(paramClass);

			SearchCondition scCondition = new SearchCondition(paramClass,str, SearchCondition.EQUAL,
					longId);
			localQuerySpec1.appendWhere(scCondition);
			QueryResult qr = PersistenceHelper.manager.find(localQuerySpec1);
			while(qr.hasMoreElements()){
				AffectedActivityData affectedActivityData = (AffectedActivityData) qr.nextElement();
				System.out.println("***************delete affectedActivityData:" + affectedActivityData);
				// 删除受影响数据的link
				try {
					PersistenceHelper.manager.delete(affectedActivityData);
				} catch (WTException e) {
					System.out.println("---------Exception----------");
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void addCheckFunction1(Changeable2 paramChangeable2) {
		System.out.println("----------------checkFunction-------------------");
		Class paramClass = ChangeRecord2.class;
		String paramString = "theChangeActivityIfc";
		try {
			LinkInfo localLinkInfo = WTIntrospector.getLinkInfo(paramClass);
			String str = null;
			boolean bool = VersionToVersionLink.class.isAssignableFrom(paramClass);
			long longId = PersistenceHelper.getObjectIdentifier(paramChangeable2).getId();
			if (localLinkInfo.isRoleA(paramString))
				str = bool ? "roleBObjectRef.key.branchId" : "roleBObjectRef.key.id";
			else if (localLinkInfo.isRoleB(paramString))
				str = bool ? "roleAObjectRef.key.branchId" : "roleAObjectRef.key.id";
			else {
				throw new WTRuntimeException(new StringBuilder().append(" Role ").append(paramString)
						.append(" doesn't exist for ").append(paramClass.getName()).toString());
			}
			QuerySpec localQuerySpec1 = new QuerySpec(paramClass);

			SearchCondition scCondition = new SearchCondition(paramClass,str, SearchCondition.EQUAL,
					longId);
			localQuerySpec1.appendWhere(scCondition);
			QueryResult qr = PersistenceHelper.manager.find(localQuerySpec1);
			while(qr.hasMoreElements()){
				ChangeRecord2 changeRecord2 = (ChangeRecord2) qr.nextElement();
				System.out.println("***************delete changeRecord2:" + changeRecord2);
				// 删除受影响数据的link
				try {
					PersistenceHelper.manager.delete(changeRecord2);
				} catch (WTException e) {
					System.out.println("---------Exception----------");
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void addCheckFunction2(Changeable2 paramChangeable2) {
		System.out.println("----------------checkFunction-------------------");
		Class paramClass = RelevantRequestData2.class;
		String paramString = "theChangeRequest2";
		try {
			LinkInfo localLinkInfo = WTIntrospector.getLinkInfo(paramClass);
			String str = null;
			boolean bool = VersionToVersionLink.class.isAssignableFrom(paramClass);
			long longId = PersistenceHelper.getObjectIdentifier(paramChangeable2).getId();
			if (localLinkInfo.isRoleA(paramString))
				str = bool ? "roleBObjectRef.key.branchId" : "roleBObjectRef.key.id";
			else if (localLinkInfo.isRoleB(paramString))
				str = bool ? "roleAObjectRef.key.branchId" : "roleAObjectRef.key.id";
			else {
				throw new WTRuntimeException(new StringBuilder().append(" Role ").append(paramString)
						.append(" doesn't exist for ").append(paramClass.getName()).toString());
			}
			QuerySpec localQuerySpec1 = new QuerySpec(paramClass);

			SearchCondition scCondition = new SearchCondition(paramClass,str, SearchCondition.EQUAL,
					longId);
			localQuerySpec1.appendWhere(scCondition);
			QueryResult qr = PersistenceHelper.manager.find(localQuerySpec1);
			while(qr.hasMoreElements()){
				RelevantRequestData2 relevantRequestData2 = (RelevantRequestData2) qr.nextElement();
				System.out.println("***************delete changeRecord2:" + relevantRequestData2);
				// 删除受影响数据的link
				try {
					PersistenceHelper.manager.delete(relevantRequestData2);
				} catch (WTException e) {
					System.out.println("---------Exception----------");
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// end add

	/**
	 * 通过指定对象找到其所有关联的变更请求、变更通过、问题报告，并将此对象从中删除。
	 *
	 * @param persistable
	 * @throws ChangeException2
	 * @throws WTException
	 */
	public static void removeFromChange(Persistable persistable) throws ChangeException2, WTException {
		//add at 2012.11.27删除变更通告中的Link
		addCheckFunction((Changeable2) persistable);
		addCheckFunction1((Changeable2) persistable);
		addCheckFunction2((Changeable2) persistable);
		//end add
		// 通过指定的变更结果对象查找与其相关的所有ChangeRecord2，并从中移除。
		QueryResult caResult = ChangeHelper2.service.getChangingChangeActivities((Changeable2) persistable, false);
		System.out.println("****************caResult:" + caResult.size());
		while (caResult.hasMoreElements()) {
			ChangeRecord2 changeRecord2 = (ChangeRecord2) caResult.nextElement();
			System.out.println("****************delete changeRecord2:" + changeRecord2);
			// 删除最后所得项的link
			ChangeHelper2.service.deleteChangeRecord(changeRecord2);
		}
		// 通过指定的受影响数据查找所有与其相关的AffectedActivityData，并从中移除。
		QueryResult caResult2 = ChangeHelper2.service.getAffectingChangeActivities((Changeable2) persistable, false);
		System.out.println("**************caResult2:" + caResult2.size());
		while (caResult2.hasMoreElements()) {
			AffectedActivityData affectedActivityData = (AffectedActivityData) caResult2.nextElement();
			System.out.println("***************delete affectedActivityData:" + affectedActivityData);
			// 删除受影响数据的link
			System.out.println("======>" + affectedActivityData.getChangeActivity2().getNumber());

			try {
				ChangeHelper2.service.deleteAffectedActivityData(affectedActivityData);
				// PersistenceHelper.manager.delete(affectedActivityData);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				System.out.println("---------Exception----------");
				e.printStackTrace();
			}

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
				if (name.endsWith("作废")) {
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
				if (name.endsWith("作废")) {
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
	 * @param persistable
	 *            指定的对象
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
				SearchCondition.EQUAL, longId);
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
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
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
		long longId = PersistenceHelper.getObjectIdentifier(doc.getMaster()).getId();
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
	 * 通过指定的部件查找与其相关的所有EPMBuildLinksRule对象。 EPMBuildLinksRule对象是连接CAD文档和部件的link。
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

	private static void removeFormEPMBuildHistory(EPMDocument epm) throws WTException {
		QueryResult qr = PersistenceHelper.manager.navigate(epm, _BuildHistory.BUILT_ROLE, EPMBuildHistory.class,
				false);
		while (qr.hasMoreElements()) {
			EPMBuildHistory link = (EPMBuildHistory) qr.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}

	public static List<Baselineable> getPartsAllIteration(String num) throws WTException {
		List<Baselineable> list = new ArrayList<Baselineable>() ;
		QuerySpec qs = new QuerySpec(WTPart.class);
		qs.appendWhere(new SearchCondition(WTPart.class, _WTPart.NUMBER,
				SearchCondition.EQUAL, num), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			WTPart part = (WTPart) qr.nextElement();
			list.add(part) ;
		}
		return list ;
	}

	public static List<Baselineable> getDocsAllIteration(String num) throws WTException {
		List<Baselineable> list = new ArrayList<Baselineable>() ;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, _WTDocument.NUMBER,
				SearchCondition.EQUAL, num), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			WTDocument doc = (WTDocument) qr.nextElement();
			list.add(doc) ;
		}
		return list ;
	}

	public static List<Baselineable> getEPMAllIteration(String num) throws WTException {
		List<Baselineable> list = new ArrayList<Baselineable>() ;
		QuerySpec qs = new QuerySpec(EPMDocument.class);
		qs.appendWhere(new SearchCondition(EPMDocument.class, _EPMDocument.NUMBER,
				SearchCondition.EQUAL, num), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			EPMDocument epm = (EPMDocument) qr.nextElement();
			list.add(epm) ;
		}
		return list ;
	}

}
