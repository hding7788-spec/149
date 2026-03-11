package ext.casc.part;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wt.doc.DocumentVersion;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTKeyedMap;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.part.PartDocHelper;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.PersistenceException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;

import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.util.IBAHelper;

public class PackagedPartHelper {

	/**
	 * 设置成套件的生命周期
	 * 
	 */
	public static void SetBOMTreeLifecycle(WTPart wtPart, State state, boolean isParent) throws WTException {
		if (wtPart == null)
			throw new WTException("传入 part 为空");
		// 获取子元素
		QueryResult childPartsQs = WTPartHelper.service.getUsesWTPartMasters(wtPart);
		while (childPartsQs.hasMoreElements()) {
			WTPartMaster childPartMaster = ((WTPartMaster) ((WTPartUsageLink) childPartsQs.nextElement())
					.getRoleBObjectRef().getObject());
			// 遍历子part
			QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(childPartMaster);
			if (allVersionsQs.hasMoreElements()) {
				WTPart childLatestPart = (WTPart) allVersionsQs.getEnumeration().nextElement();
				SetBOMTreeLifecycle(childLatestPart, state, false);
			}
		}
		LifeCycleHelper.service.setLifeCycleState(wtPart, state);
	}

	/**
	 * 
	 * 建立成套件升版后的相关的文件关系 CA before CA after
	 * 
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static List<WTPart> BOMTreeCreateReferPart(List<WTPart> preList) throws WTException, WTPropertyVetoException {
		List<WTPart> afterParts = new ArrayList<WTPart>();
		for (WTObject wtPreObj : preList) {
			if (wtPreObj instanceof WTPart) {
				WTPart wtPrePart = (WTPart) wtPreObj;
				// 升级此部件
				Versioned result = VersionControlHelper.service.newVersion((Versioned) wtPrePart);
				WTPart wtAfterPart = (WTPart) PersistenceHelper.manager.save((Persistable) result);
				QueryResult childPartsQs = WTPartHelper.service.getUsesWTPartMasters(wtPrePart);
				// 获取列表中的Pre 的childPartMaster
				while (childPartsQs.hasMoreElements()) {
					// 建立新usageLink
					WTPartMaster childPartMaster = ((WTPartMaster) ((WTPartUsageLink) childPartsQs.nextElement())
							.getRoleBObjectRef().getObject());
					WTPartUsageLink.newWTPartUsageLink(wtAfterPart, childPartMaster);
				}
				// 保存
				afterParts.add((WTPart) PersistenceHelper.manager.save(wtAfterPart));
			}
		}
		// 返回已加入
		return afterParts;
	}

	/**
	 * 过滤部件
	 * 
	 * @param wtPart
	 * @param isParent
	 * @return
	 * @throws WTException
	 */
	public static List<WTPart> FilterPackagedPart(WTPart wtPart, WTContainer container , int level) throws WTException {
		List<WTPart> resultList = new ArrayList<WTPart>();
		if (wtPart == null) throw new WTException("传入 part 为空");
		String isPackagedPart = ext.casc.util.IBAHelper.getIBAStringValue(wtPart, "SETMARK");
		// 借用件
		boolean isJieYong = !wtPart.getContainer().equals(container);
		
		QueryResult childPartsQs = WTPartHelper.service.getUsesWTPartMasters(wtPart);
		// 获取子元素
//		if (isPackagedPart!=null&&!isPackagedPart.equals("是") && !isJieYong ){
			resultList.add(wtPart);
			while (childPartsQs.hasMoreElements()) {
				WTPartMaster childPartMaster = ((WTPartMaster)((WTPartUsageLink) childPartsQs.nextElement()).getRoleBObjectRef().getObject());
				// 遍历子part
				QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(childPartMaster);
				// 最新的,在第一个元素是Manufacturing，第二个是Design.
				if (allVersionsQs.hasMoreElements()) {
				    WTPart childLatestPart = (WTPart) allVersionsQs.nextElement();
				    String viewName = childLatestPart.getViewName();
				    if ("Design".equals(viewName)) {
	                    resultList.addAll(FilterPackagedPart(childLatestPart, container,level));
                    }else {
                        childLatestPart = (WTPart) allVersionsQs.nextElement();
                        resultList.addAll(FilterPackagedPart(childLatestPart, container,level));
                    }
				   
				} else {
					 //找不到最新版本，系统错误
					throw new WTException(childPartMaster.getDisplayIdentifier() + " 无法找到最新的版本！");
				}
			}
//		} else{
//			if(level++ == 0){
//				while (childPartsQs.hasMoreElements()) {
//					WTPartMaster childPartMaster = ((WTPartMaster)((WTPartUsageLink) childPartsQs.nextElement()).getRoleBObjectRef().getObject());
//					// 遍历子part
//					QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(childPartMaster);
//					// 最新的在第一个元素
//					if (allVersionsQs.hasMoreElements()) {
//						WTPart childLatestPart = (WTPart) allVersionsQs.getEnumeration().nextElement();
//						resultList.addAll(FilterPackagedPart(childLatestPart, container,level));
//					} else {
//						// 找不到最新版本，系统错误
//						throw new WTException(childPartMaster.getDisplayIdentifier() + " 无法找到最新的版本！");
//					}
//				}
//			}
//		}
		
		return resultList;
	}
	
	/**
	 * 过滤部件
	 * 
	 * @param wtPart
	 * @param isParent
	 * @return
	 * @throws WTException
	 */
	public static List<WTPart> FilterCHGLPackagedPart(WTPart wtPart, WTContainer container , int level) throws WTException {
		List<WTPart> resultList = new ArrayList<WTPart>();
		if (wtPart == null) throw new WTException("传入 part 为空");
		
		QueryResult childPartsQs = WTPartHelper.service.getUsesWTPartMasters(wtPart);
		resultList.add(wtPart);
		while (childPartsQs.hasMoreElements()) {
			WTPartMaster childPartMaster = ((WTPartMaster)((WTPartUsageLink) childPartsQs.nextElement()).getRoleBObjectRef().getObject());
			// 遍历子part
			QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(childPartMaster);
			if (allVersionsQs.hasMoreElements()) {
			    WTPart childLatestPart = (WTPart) allVersionsQs.nextElement();
                resultList.addAll(FilterCHGLPackagedPart(childLatestPart, container,level));
			} 
		}
		return resultList;
	}

	/**
	 * 部件启动打印申请流程时的过滤部件
	 * 
	 * @param wtPart
	 * @param isParent
	 * @return
	 * @throws WTException
	 */
	public static List<WTPart> FilterPackagedPartForRelease(WTPart wtPart, WTContainer container) throws WTException {
//		CSCDebug.outDebugInfo("Now you enter FilterPackagedPartForPrintApply function!!");
//		CSCDebug.outDebugInfo("wtpart name is: " + wtPart.getName());
		List<WTPart> resultList = new ArrayList<WTPart>();
		LifeCycleManaged lfObject = null; String objTemp = null;
		if (wtPart == null)
			throw new WTException("传入 part 为空");
		
		String isPackagedPart = IBAHelper.getIBAStringValue(wtPart, "SETMARK");
		// 借用件
		boolean isJieYong = !wtPart.getContainer().equals(container);

		QueryResult childPartsQs = WTPartHelper.service.getUsesWTPartMasters(wtPart);
//		CSCDebug.outDebugInfo("childPartsQs size is: " + childPartsQs.size());
		// 获取子元素
		while (childPartsQs.hasMoreElements()) {
			WTPartMaster childPartMaster = ((WTPartMaster) ((WTPartUsageLink) childPartsQs.nextElement())
					.getRoleBObjectRef().getObject());
			// 遍历子part
			QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(childPartMaster);
			// 最新的在第一个元素
			if (allVersionsQs.hasMoreElements()) {
				WTPart childLatestPart = (WTPart) allVersionsQs.getEnumeration().nextElement();
//				CSCDebug.outDebugInfo("childLatestPart name is: " + childLatestPart.getName());
				isPackagedPart = IBAHelper.getIBAStringValue(childLatestPart, "SETMARK");
				isJieYong = !childLatestPart.getContainer().equals(container);
				if (!isPackagedPart.equals("是") && !isJieYong) {
//					CSCDebug.outDebugInfo("不是成套件也不是借用件");
					lfObject = (LifeCycleManaged) childLatestPart;
					objTemp = lfObject.getState().toString();
					if (objTemp.equals("APPROVED")) {
						resultList.add(childLatestPart);
					}					
					resultList.addAll(FilterPackagedPartForRelease(childLatestPart, container));
				}
			} else {
				// 找不到最新版本，系统错误
				throw new WTException(childPartMaster.getDisplayIdentifier() + " 无法找到最新的版本！");
			}
		}

		return resultList;
	}

	public static List<RevisionControlled> getPartBOMData(String oid) throws WTRuntimeException, WTException,
			RemoteException {
		List<RevisionControlled> wtoList = new ArrayList<RevisionControlled>();
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();
		WTContainer container = part.getContainer();
		List<WTPart> partList = FilterPackagedPart(part, container,0);
		// partList.add(part);
		for (int i = 0; i < partList.size(); i++) {
			WTPart tempPart = partList.get(i);
			List<RevisionControlled> tempOBJList = getPartReleatedDOC(tempPart);
			wtoList.add(tempPart);
			if (tempOBJList.size() > 0) {
				wtoList.addAll(tempOBJList);
			}
		}
		return wtoList;
	}

	public static List<RevisionControlled> getPartBOMData(WTPart part) throws WTRuntimeException, WTException,
			RemoteException {
		List<RevisionControlled> wtoList = new ArrayList<RevisionControlled>();
		WTContainer container = part.getContainer();
		List<WTPart> partList = FilterPackagedPart(part, container,0);
		// partList.add(part);
		for (int i = 0; i < partList.size(); i++) {
			WTPart tempPart = partList.get(i);
//			CSCDebug.outDebugInfo("*********next step is getPartReleatedDoc!!");
			List<RevisionControlled> tempOBJList = getPartReleatedDOC(tempPart);
			wtoList.add(tempPart);
			if (tempOBJList.size() > 0) {
				wtoList.addAll(tempOBJList);
			}
		}
		return wtoList;
	}

	/**
	 * 获取打印申请流程中BOM结构的Part<获取子部件中已经批准的文档>
	 * 
	 * @param part
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws RemoteException
	 */
	public static List<RevisionControlled> getPartBOMDocForRelease(WTPart part) throws WTRuntimeException, WTException,
			RemoteException {
//		CSCDebug.outDebugInfo("Now you enter getPartBOMDocForRelease function!!");
		List<RevisionControlled> wtoList = new ArrayList<RevisionControlled>();
		WTContainer container = part.getContainer();
		List<WTPart> partList = FilterPackagedPartForRelease(part, container);
//		CSCDebug.outDebugInfo("获取子部件列表：");
		// partList.add(part);
		for (int i = 0; i < partList.size(); i++) {
			WTPart tempPart = partList.get(i);
//			CSCDebug.outDebugInfo("*********next step is getPartReleatedDoc!!");
			List<RevisionControlled> tempOBJList = getPartReleatedDocForRelease(tempPart);
			wtoList.add(tempPart);
			if (tempOBJList.size() > 0) {
				wtoList.addAll(tempOBJList);
			}
		}
		return wtoList;
	}
	
	
	public static QueryResult getAssociatedCADDocuments(WTPart wtpart) throws WTException {
		WTArrayList wtarraylist = new WTArrayList();
        wtarraylist.add(wtpart);
        WTKeyedMap wtkeyedmap = PartDocHelper.service.getAssociatedCADDocuments(wtarraylist);
        WTCollection wtcollection = (WTCollection)wtkeyedmap.get(wtpart);
        return getDocs(wtcollection);
	}
	
	private static QueryResult getDocs(WTCollection wtcollection)
    {
        QueryResult queryresult = new QueryResult();
        try
        {
            if(wtcollection != null)
            {
                ObjectVector objectvector = new ObjectVector();
                DocumentVersion documentversion;
                for(Iterator iterator = wtcollection.persistableIterator(); iterator.hasNext(); objectvector.addElement(documentversion))
                {
                    documentversion = (DocumentVersion)iterator.next();
                }
                queryresult.appendObjectVector(objectvector);
            }
        }
        catch(WTException wtexception)
        {
            wtexception.printStackTrace();
        }
        return queryresult;
    }


	public static List<RevisionControlled> getPartReleatedDOC(WTPart part) throws WTException, RemoteException {
		List<RevisionControlled> docList = new ArrayList<RevisionControlled>();
		List objList = ProcessEnvelopeUtil.getRelatedWTObjectByPart(part);
		for (int i = 0; i < objList.size(); i++) {
			RevisionControlled obj = (RevisionControlled) objList.get(i);
			if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				docList.add(obj);
			}
		}
		QueryResult cadDocRs = getAssociatedCADDocuments(part);
		if (cadDocRs != null) {
			while (cadDocRs.hasMoreElements()) {
				docList.add((RevisionControlled)cadDocRs.nextElement());
			}
		}
		return docList;
	}

	/**
	 * 获取打印申请流程中Part所需要的文档（获取Part所关联的所有已经批准文档）
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws RemoteException
	 */
	public static List<RevisionControlled> getPartReleatedDocForRelease(
			WTPart part) throws WTException, RemoteException {
//		CSCDebug.outDebugInfo("Now you enter getPartReleatedDocForRelease function!");
		List<RevisionControlled> docList = new ArrayList<RevisionControlled>();
		List objList = ProcessEnvelopeUtil.getRelatedWTObjectByPart(part);
		QueryResult queryresult = null, allVersionsQs = null;
		String lifeStr = null;
		EPMDocument childLatestEPM = null;
		for (int i = 0; i < objList.size(); i++) {
			RevisionControlled obj = (RevisionControlled) objList.get(i);
			lifeStr = ((LifeCycleManaged) obj).getLifeCycleState().toString();
//			CSCDebug.outDebugInfo("lifeStr is: " + lifeStr);
			if (lifeStr.equals("APPROVED")) {
				if (obj instanceof WTDocument) {
					docList.add(obj);
				}
			}
		}
		QueryResult cadDocRs = getAssociatedCADDocuments(part);
		if (cadDocRs != null) {
			while (cadDocRs.hasMoreElements()) {
				docList.add((RevisionControlled)cadDocRs.nextElement());
			}
		}
		return docList;
	}

	public static boolean isEqualsState(WTObject wto, String state) {
		if (wto instanceof LifeCycleManaged) {
//			CSCDebug.outDebugInfo("wto is a LifeCycleManaged");
			String objState = ((LifeCycleManaged) wto).getLifeCycleState().toString();
			if (objState.equals(state)) {
//				CSCDebug.outDebugInfo("isEqualsState is true!!!");
				return true;
			}
		}
		return false;
	}

	public static boolean validataFirstVersion(WTObject wtobject) throws PersistenceException, WTException {
		if (wtobject instanceof Versioned) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf((Versioned) wtobject);
			if (qr.size() == 1) {
				return true;
			}
		}
		return false;
	}

	public static void main(String args[]) throws WTRuntimeException, RemoteException, WTException {
		List<RevisionControlled> objList = getPartBOMData("VR:wt.part.WTPart:258039");
		for (RevisionControlled rev : objList) {
			System.out.println(TypeIdentifierUtilityHelper.service.getTypeIdentifier(rev).toExternalForm() + "\t"
					+ rev.getDisplayIdentifier());
		}
	}

	public static List<WTPart> getPartBOMDataWithoutRelatedDoc(String oid) throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();
		WTContainer container = part.getContainer();
		List<WTPart> partList = FilterPackagedPart(part, container,0);
		return partList;
	}
	
	public static List<WTPart> getCHGLPartBOMData(String oid) throws WTRuntimeException, WTException {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();
		WTContainer container = part.getContainer();
		List<WTPart> partList = FilterCHGLPackagedPart(part, container,0);
		return partList;
	}
	
}
