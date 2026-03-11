package ext.casc.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

public class EPMDocumentHelper {
	
	public static EPMDocumentType[] CADDcouemntType = new EPMDocumentType[] {
			EPMDocumentType.toEPMDocumentType("CADASSEMBLY"),
			EPMDocumentType.toEPMDocumentType("CADDRAWING"),
			EPMDocumentType.toEPMDocumentType("CADCOMPONENT")};
	
	
	public static EPMDocumentType[] CADD3DcouemntType = new EPMDocumentType[] {
		EPMDocumentType.toEPMDocumentType("CADASSEMBLY"),
		EPMDocumentType.toEPMDocumentType("CADCOMPONENT")};
	
	
	public static EPMAuthoringAppType[] PROE_AUTH = new EPMAuthoringAppType[] {
			EPMAuthoringAppType.toEPMAuthoringAppType("PROE")};
	
	
	
	/**
	 * EPM 文档是不是CAD类型并且是ProE创建的
	 * @param epmdoc
	 * @return
	 */
	public static boolean isEPMCADandProE(EPMDocument epmdoc) {
		EPMDocumentType docType = epmdoc.getDocType();
		EPMAuthoringAppType docAuthType = epmdoc.getAuthoringApplication();
//		System.out.println("docType: " + docType + "docAuthType is: " + docAuthType);
		return Arrays.asList(EPMDocumentHelper.CADD3DcouemntType).contains(docType) && Arrays.asList(EPMDocumentHelper.PROE_AUTH).contains(docAuthType);
	}
	
	
	/**
	 * ywu 2010.11.26 replace with getTopEPMDocument2
	 * 获取单个empdoc的顶层empdoc
	 * @param empDoc
	 * @return
	 */
	public static List<EPMDocument> getTopEPMDocument(EPMDocument epmDoc) throws WTException {
		// TODO 测试此方法
		List<EPMDocument> epmReferDocs  = new ArrayList<EPMDocument>();
		// 判断EPMDoc 是不是PRO/E模型
		if (EPMDocumentHelper.isEPMCADandProE(epmDoc)) {
			EPMDocumentMaster epmDocMaster = (EPMDocumentMaster) (epmDoc.getMaster());
			QuerySpec qs = new QuerySpec(EPMMemberLink.class);
			QueryResult qr = EPMStructureHelper.service.navigateUsedBy(epmDocMaster, qs, true);
			if (qr.size() == 0) {
				// 没有顶层，此epmdoc为父层
				epmReferDocs.add(epmDoc);
			} else {
				// 有其他父节点
				while (qr.hasMoreElements()) {
					EPMDocument refDoc = (EPMDocument) qr.nextElement();
					System.out.println("refDoc is:" + refDoc.getIdentity());
					epmReferDocs.addAll(getTopEPMDocument(refDoc));
				}
			}
		}
		return epmReferDocs;
	}
	
	/**
	 * ywu 2010.11.26
	 * 获取单个empdoc的顶层empdoc
	 * 仅追溯最新版本分支
	 * @param empDoc
	 * @return
	 */
	 public static List<EPMDocument> getTopEPMDocument2(EPMDocument epmDoc) throws WTException {
		// TODO 测试此方法
		List<EPMDocument> epmReferDocs  = new ArrayList<EPMDocument>();
		// 判断EPMDoc 是不是PRO/E模型
		if (EPMDocumentHelper.isEPMCADandProE(epmDoc)) {
			EPMDocumentMaster epmDocMaster = (EPMDocumentMaster) (epmDoc.getMaster());
			QuerySpec qs = new QuerySpec(EPMMemberLink.class);
			QueryResult qr = EPMStructureHelper.service.navigateUsedBy(epmDocMaster, qs, true);
			if (qr.size() == 0) {
				// 没有顶层，此epmdoc为父层
				epmReferDocs.add(epmDoc);
			} else {
				// 有其他父节点
				while (qr.hasMoreElements()) {
					EPMDocument refDoc = (EPMDocument) qr.nextElement();
					if(isLatestVersionAndIteration(refDoc))
					{
						epmReferDocs.addAll(getTopEPMDocument2(refDoc));
					}
				}
			}
		}
		return epmReferDocs;
	}
	
	/**
	 * 测试EPM是否最新版本
	 * @param epmDoc
	 * @return
	 * @throws 
	 */
	private static boolean isLatestVersionAndIteration(EPMDocument epm)
	{
		if(!epm.isLatestIteration())
			return false;
		try {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(epm);
			return epm.equals((EPMDocument) qr.nextElement());
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
			
	}
	
	/**
	 * 获取emp创建者
	 * @param epmDoc
	 * @return
	 * @throws WTException
	 */
	public static List<WTUser> getTopCreaterOfEPMDocument(EPMDocument epmDoc) throws WTException {
		List<EPMDocument> epmReferDocs = getTopEPMDocument2(epmDoc);
		List<WTUser> creatorList = new ArrayList<WTUser>();
		for (EPMDocument subDoc : epmReferDocs) {
			WTUser creator = (WTUser) subDoc.getCreator().getObject();
			if (creatorList != null && !creatorList.contains(creator)) {
				creatorList.add(creator);
			}
		}
		
		return creatorList;
	}


}