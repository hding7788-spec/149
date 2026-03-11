package com.glaway.mpm.pbom.helper;

import com.glaway.mpm.erp.controller.XMLHelper;
import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.util.*;
import com.ptc.wvs.server.util.PublishUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.method.RemoteMethodServer;
import wt.part.*;
import wt.representation.Representation;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

import java.beans.PropertyVetoException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PBOMHelper {
	private static String CLASSNAME = PBOMHelper.class.getName();
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);

	/**
	 * 新增零件
	 *
	 * @author qianlong
	 * @date 2012-12-26
	 * @param parentOid
	 * @param number
	 * @param name
	 * @param containerId
	 * @param folderPath
	 * @param typeName
	 * @return
	 * @throws Exception
	 *
	 */
	public static WTPart createPart(String parentOid, String childOid, String invcode,String typeName, Map<String, String> ibaMap,String useCount)
			throws Exception {
		GLLogger.debug(CLASSNAME, "--createPart--parentOid--" + parentOid + "--childOid--" + childOid + "--ibaMap--"
				+ ibaMap + "--useCount--"+useCount);
		if("0".equals(useCount)){
			useCount = "1";
		}

		WTPart childPart = (WTPart) Util.getObjectByOid(WTPart.class, childOid);
		WTPart parentPart = (WTPart) Util.getObjectByOid(WTPart.class, parentOid);
		if (childPart != null && parentPart != null) {
			if (!isPlanning(childPart) || !isPlanning(parentPart)) {
				throw new Exception("零件不是Planning");
			}
			WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(parentPart, (WTPartMaster) childPart.getMaster());
			// 如果零件的link
			// -------------不存在，就新建link
			// -------------存在，如果零件是
			// ----------------------------工艺中间件和普通，零件数量+1
			// ----------------------------工艺辅件，直接写入投产数量（productionQuantity）投产比例（productionRatio）
			// useCount、numerator、denominator
			if (link == null) {
				link = WTPartUtil.createWTPartUsageLink(parentPart, (WTPartMaster) childPart.getMaster());
			}

			Quantity quantity = new Quantity();
			//quantity.setAmount(link.getQuantity().getAmount() + 1);//modify by longxiuchuan 20131229
			if(useCount != null && !"".equals(useCount)) {
				quantity.setAmount(Integer.valueOf(useCount));
			} else {
				quantity.setAmount(link.getQuantity().getAmount());
			}
			link.setQuantity(quantity);
			IBAHelper helper = new IBAHelper(link);
			if(invcode!=null&&!"".equals(invcode)){
				helper.setIBAAnyValue(link, "CHBM", invcode);
			}

			//工艺数量
			String gysl = "";
			if(ibaMap.containsKey("GYSL")) {
				gysl = ibaMap.get("GYSL");
				ibaMap.remove("GYSL");
			}
			if(gysl != null && !"".equals(gysl)) {
				helper.setIBAAnyValue(link, "GYSL", gysl);
			}

			PersistenceServerHelper.manager.update(link);
		} else {
			throw new Exception("零件不存在");
		}
		IBAHelper helper = new IBAHelper(childPart);
		helper.setIBAValue(childPart, ibaMap);
		return childPart;
	}

	/**
	 * 删除零件 只删除结构
	 *
	 * @author qianlong
	 * @date 2012-12-26
	 * @param parentOid
	 * @param childOid
	 * @throws Exception
	 *
	 */
	public static void deletePart(String parentOid, String childOid, String typeName) throws Exception {
		GLLogger.debug(CLASSNAME, "--deletePart--parentOid--" + parentOid + "--childOid--" + childOid + "--typeName--"
				+ typeName + "--");
		WTPart childPart = (WTPart) Util.getObjectByOid(WTPart.class, childOid);
		WTPart parentPart = (WTPart) Util.getObjectByOid(WTPart.class, parentOid);
		if (childPart != null && parentPart != null) {
			if (!isPlanning(childPart) || !isPlanning(parentPart)) {
				throw new Exception("零件不是Planning");
			}
			// 删除零件结构，如果是
			// -------------------工艺中间件和普通零件，需要把link中的数量信息-1，数量是1的情况直接删除link
			// -------------------工艺辅件，直接删除link
			WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(parentPart, (WTPartMaster) childPart.getMaster());
			if (link != null) {
				Double quantity = link.getQuantity().getAmount();
				if (quantity > 1.0) {
					Quantity newQuantity = new Quantity();
					newQuantity.setAmount(quantity - 1);
					link.setQuantity(newQuantity);
					PersistenceServerHelper.manager.update(link);
				} else {
					WTPartUtil.deleteWTPartUsageLink(link);
				}
			}
		} else {
			//throw new Exception("零件不存在");
			System.out.println("零件不存在");
		}

	}

	/**
	 * 修改零件结构
	 *
	 * @author qianlong
	 * @date 2012-12-26
	 * @param oldParentOid
	 * @param newParentOid
	 * @param childOid
	 * @throws Exception
	 *
	 */
	public static void modifyWTPartUsageLink(String oldParentOid, String newParentOid, String childOid,String invcode,
			String typeName, Map<String, String> ibaMap) throws Exception {
		GLLogger.debug(CLASSNAME, "--modifyWTPartUsageLink--oldParentOid--" + oldParentOid + "--newParentOid--"
				+ newParentOid + "--childOid--" + childOid + "--typeName--" + typeName + "--ibaMap--" + ibaMap
				+ "--ibaMap--" + ibaMap);
		WTPart childPart = (WTPart) Util.getObjectByOid(WTPart.class, childOid);
		WTPart oldParentPart = (WTPart) Util.getObjectByOid(WTPart.class, oldParentOid);
		WTPart newParentPart = (WTPart) Util.getObjectByOid(WTPart.class, newParentOid);
		if (childPart != null && oldParentPart != null && newParentPart != null) {
			if (!isPlanning(childPart) || !isPlanning(oldParentPart) || !isPlanning(newParentPart)) {
				throw new Exception("零件不是Planning");
			}
			// 零件结构移动，工艺中间件和工艺辅件是不可以移动的
			// 如果原有的link存在，需要link上的数量信息-1，数量信息为1，就直接删除link
			WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(oldParentPart, (WTPartMaster) childPart.getMaster());
			if (link != null) {
				Double quantity = link.getQuantity().getAmount();
				if (quantity != 1.0) {
					Quantity newQuantity = new Quantity();
					newQuantity.setAmount(quantity - 1);
					link.setQuantity(newQuantity);
					PersistenceServerHelper.manager.update(link);
				} else {
					WTPartUtil.deleteWTPartUsageLink(link);
				}
			}

			// 如果新的link不存在，就新建，link上的数量信息+1
			WTPartUsageLink newlink = WTPartUtil.getWTPartUsageLink(newParentPart, (WTPartMaster) childPart.getMaster());
			if (newlink == null) {
				newlink = WTPartUtil.createWTPartUsageLink(newParentPart, (WTPartMaster) childPart.getMaster());
			} else {
				Quantity quantity = new Quantity();
				quantity.setAmount(newlink.getQuantity().getAmount() + 1);
				newlink.setQuantity(quantity);
			}
			IBAHelper helper = new IBAHelper(newlink);
			if(invcode!=null&&!"".equals(invcode)){
				helper.setIBAAnyValue(newlink, "CHBM", invcode);
			}

			//工艺数量
			String gysl = "";
			if(ibaMap.containsKey("GYSL")) {
				gysl = ibaMap.get("GYSL");
				ibaMap.remove("GYSL");
			}
			if(gysl != null && !"".equals(gysl)) {
				helper.setIBAAnyValue(newlink, "GYSL", gysl);
			}

			PersistenceServerHelper.manager.update(newlink);
		} else {
			throw new Exception("零件不存在");
		}
		IBAHelper helper = new IBAHelper(childPart);
		helper.setIBAValue(childPart, ibaMap);
	}

	/**
	 * 修改零件属性
	 *
	 * @author qianlong
	 * @throws Exception
	 * @date 2012-12-21
	 *
	 */
	public static void modifyPart(String poid,String oid,String invcode, Map<String, String> ibaMap,String useCount) throws Exception {
		GLLogger.debug(CLASSNAME, "--modifyPart--oid--" + oid + "--ibaMap--" + ibaMap + "--useCount--"+useCount);
		if("0".equals(useCount)){
			useCount = "1";
		}
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
		if(part!=null){
			if (!isPlanning(part)) {
				throw new Exception("零件不是Planning");
			}
			System.out.println("--set IBA --ibaMap-"+ibaMap);

			//工艺数量
			String gysl = "";
			if(ibaMap.containsKey("GYSL")) {
				gysl = ibaMap.get("GYSL");
				ibaMap.remove("GYSL");
			}

			IBAHelper attrHelper = new IBAHelper(part);
			attrHelper.setIBAValue(part, ibaMap);

			WTPart ppart = (WTPart) Util.getObjectByOid(WTPart.class, poid);
			if(ppart != null) {
				String number = ppart.getNumber();
				String view1 = ppart.getViewName();
				String number2 = part.getNumber();
				String view2 = part.getViewName();
				WTPartUsageLink newlink = WTPartUtil.getWTPartUsageLink(ppart, (WTPartMaster) part.getMaster());
				if (newlink == null) {
					newlink = WTPartUtil.createWTPartUsageLink(ppart, (WTPartMaster) part.getMaster());
				} else {
					if(useCount != null && !"".equals(useCount)) {
						Quantity quantity = new Quantity();
						quantity.setAmount(Integer.valueOf(useCount));
						newlink.setQuantity(quantity);
					}
				}
				IBAHelper linkHelper = new IBAHelper(newlink);
				if(invcode!=null&&!"".equals(invcode)){
					linkHelper.setIBAAnyValue(newlink, "CHBM", invcode);
				}

				if(gysl != null && !"".equals(gysl)) {
					linkHelper.setIBAAnyValue(newlink, "GYSL", gysl);
				}
				PersistenceServerHelper.manager.update(newlink);
			}

		}
	}

	/**
	 * 修改零件版本版序
	 *
	 * @author qianlong
	 * @throws Exception
	 * @date 2012-12-21
	 *
	 */
	public static WTPart modifyVersion(String oid, String tag) throws Exception {
		GLLogger.debug(CLASSNAME, "--modifyVersion--oid--" + oid + "--tag--" + tag + "--");
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);

		//WTPart designPart =  WTPartUtil.getLatestPartByNumberAndView(part,"Design");
       // Representation designRep = RepresentationHelper.service.getDefaultRepresentation((Representable) designPart);

		//add by LongXiuChuan 2014/04/13
		part = (WTPart)VersionControlHelper.getLatestIteration(part,false);



		if (part != null) {
			if (!isPlanning(part)) {
				throw new Exception("零件不是Planning!");
			}
			if ("1".equals(tag)) {

				String state = part.getState().getState().toString();
				if(!"APPROVED".equals(state)){//非已批准才允许升小版。
					System.out.println(part.getNumber()+"非已批准允许升小版");
					part = (WTPart) WorkInProcessUtil.updateIteration(part);
				}else{
					System.out.println(part.getNumber()+"已批准不允许升小版");
				}
				/*Representation derep = RepresentationHelper.service.getDefaultRepresentation((Representable) part);
				if(derep!=null){
					RepresentationHelper.service.deleteRepresentation(derep);
				}*/
				//RepresentationHelper.service.storeRepresentation(designRep, part, "default", "default", RepresentationType.PRODUCT_VIEW);
				/*boolean flag = RepHelper.loadRepresentation(tmpfile.getCanonicalPath(), oid,
						true, "default", "default", false,
						VisualizationHelperFactory.HELPER.isThumbnailEnabled(), false);*/

				//RepresentationHelper.service.setDefaultRepresentation(part, designRep, false);

			} else if ("2".equals(tag)) {
				part = updateVersion(part);

				//删除修订版本后关联的工艺规程文档Link
				System.out.println("modifyVersion->deleteTechnicsDescribeLink："+part.getNumber());
				WTPartUtil.deleteTechnicsDescribeLink(part);
				//删除修订版本后关联的PBOM文档Link
				//WTPartUtil.deletePbomDescribeLink(part);
			}
		} else {
			throw new Exception("零件不存在!");
		}
		wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		return part;
	}

	/**
	 * 升版本
	 *
	 * @author qianlong
	 * @date 2013-6-5
	 * @param part
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	private static WTPart updateVersion(WTPart part) throws WTException, WTPropertyVetoException {
		WTPart newPart = (WTPart) VersionControlHelper.service.newVersion(part);
		Folder folder = FolderHelper.service.getFolder(newPart);
		FolderHelper.assignFolder(newPart, folder);
		newPart = (WTPart) PersistenceHelper.manager.store(newPart);
		return newPart;
	}

	/**
	 * 判断是否是Planning视图
	 *
	 * @author qianlong
	 * @date 2013-3-20
	 * @param part
	 * @return
	 *
	 */
	public static boolean isPlanning(WTPart part) {
		boolean tag = false;
		String viewName = part.getViewName();
		if (Constants.planning.equals(viewName)) {
			tag = true;
		}
		return tag;
	}

	/**
	 * 获取creoView 打开图档的URL
	 *
	 * @author qianlong
	 * @date 2013-4-9
	 * @return
	 *
	 */
	public static String getCreoViewUrl(Persistable persistable, WTContainer container) {
		String creoViewUrl = "";
		QueryResult repResult = PublishUtils.getRepresentations(persistable);
		if (repResult == null || repResult.size() == 0) {
			return creoViewUrl;
		}
		Persistable paramPersistable = (Persistable) repResult.nextElement();

		String viewUrl = "";
		String repOid = "";
		if ((paramPersistable instanceof Representation)) {
			repOid = com.ptc.wvs.server.util.Util.SandR(PublishUtils.getRefFromObject(paramPersistable), ":", "%3A");
			viewUrl = PublishUtils.getPreferedViewURL((Representation) paramPersistable, true);
		}
		if ("".equals(creoViewUrl) && !"".equals(viewUrl)) {
			String url = new StringBuilder().append(viewUrl).append("&objref=").append(repOid).toString();
			creoViewUrl = new StringBuilder().append(PropertiesUtil.getHttpCodeBase() + "/wtcore/jsp/wvs/edrview.jsp")
					.append("?url=").append(url).append("&ContainerOid=OR%3A").append(
							com.ptc.wvs.server.util.Util.SandR(container.toString(), ":", "%3A")).toString();
		}
		return creoViewUrl;
	}

	/**
	 * 获取产品结构的顶层part
	 *
	 * @author qianlong
	 * @date 2013-2-26
	 * @param part
	 * @return
	 * @throws WTException
	 *
	 */
	public static String getParent(WTPart part) throws WTException {
		String returnOid = "";
		List<WTPart> list = WTPartUtil.getParentPart(part);
		for (WTPart p : list) {
			if (p.getNumber().startsWith("AL1")) {
				returnOid = Util.getStringOid(p);
				break;
			} else {
				returnOid = getParent(p);
			}
		}
		return returnOid;
	}

	/**
	 * 获取BOM xml
	 *
	 * @author qianlong
	 * @date 2013-4-27
	 * @param oid
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 *
	 */
	public static byte[] getBOMXml(WTPart part, String endWith) throws WTException, PropertyVetoException {
		byte[] bytes = null;
		WTDocument document = getBOMXmlDoc(part, endWith);
		System.out.println("----getBOMXml---document:"+document);
		if (null != document) {
			bytes = WTDocumentUtil.applicationDataToByte(WTDocumentUtil.getPrimaryByDocument(document));
		}
		return bytes;
	}

	/**
	 * 获取BOM xml 文件
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 *
	 */
	public static WTDocument getBOMXmlDoc(WTPart part, String endWith) throws WTException, PropertyVetoException {
		WTDocument doc = null;
		List<WTDocument> docList = WTPartUtil.getDescribedDocumentByPart(part, TypeNameConstants.pbomDocTypeName);
		for (WTDocument document : docList) {
			if (document.getName().contains(endWith)) {
				doc = document;
				break;
			}
		}
		return doc;
	}

	/**
	 * 获取BOM xml 文件
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 *
	 */
	public static WTDocument getBOMXmlDoc2(WTPart part, String endWith) throws WTException, PropertyVetoException {
		System.out.println("---getBOMXmlDoc----------part:"+part.getNumber()+"----version:"
				+ part.getVersionInfo().getIdentifier().getValue()+"."
				+part.getIterationInfo().getIdentifier().getValue()
				+"      endWith:"+endWith);
		WTDocument doc = null;
		List<WTDocument> docList = WTPartUtil.getDescribedDocumentByPart(part, TypeNameConstants.pbomDocTypeName);
		System.out.println("--docList--" + docList);
		for (WTDocument document : docList) {
			if (document.getName().contains(endWith)) {
				doc = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				String docVersion = doc.getVersionInfo().getIdentifier().getValue();
				System.out.println("------PBOM doc:"+doc.getNumber()+"----version:"
						+ docVersion+"." +doc.getIterationInfo().getIdentifier().getValue());
				break;
			}
		}

		return doc;
	}

	/**
	 * 通过零件上传BOM xml
	 *
	 * @author qianlong
	 * @date 2012-11-5
	 * @param oid
	 * @param bytes
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 *
	 */
	public static boolean saveBOMXml(WTPart part, byte[] bytes, String endWith) throws WTException,
			FileNotFoundException, PropertyVetoException, IOException {
		//String partTypeName = TypedUtility.getTypeIdentifier(part).getTypename();
		WTDocument document = getBOMXmlDoc(part, endWith);
		if (document != null) {
			document = (WTDocument) WorkInProcessUtil.checkout(document);
			document = WTDocumentUtil.setPrimaryForDocument(document, part.getName() + endWith, bytes);
			document = (WTDocument) WorkInProcessUtil.checkin(document);
		} else {
			String  folderPath = propertiesUtil.getProperty("pbom-xml-document-save-folder");
			// 如果是 工装零件 文件夹结构不一样
//			if (!partTypeName.contains(TypeNameConstants.gzRootPartTypeName)
//					&& !partTypeName.contains(TypeNameConstants.gzToolingPartTypeName)) {
//				folderPath = propertiesUtil.getProperty("pbom-xml-document-save-folder");
//			} else {
//				folderPath = ((SubFolder) part.getParentFolder().getObject()).getLocation() + "/"+ Constants.gzFolderName[5];
//			}
			document = WTDocumentUtil.createDocument(part.getName() + endWith, part.getContainer(), folderPath,
					TypeNameConstants.pbomDocTypeName);

			document = WTDocumentUtil.setPrimaryForDocument(document, part.getName() + endWith, bytes);
			WTPartUtil.createWTPartDescribeLink(part, document);
		}
		return true;
	}

	public static boolean saveBOMXmlWithOutCheckOut(WTPart part, byte[] bytes, String endWith) throws WTException,
		FileNotFoundException, PropertyVetoException, IOException {
		WTDocument document = getBOMXmlDoc(part, endWith);
		if (document != null) {
			document = WTDocumentUtil.setPrimaryForDocument(document, part.getName() + endWith, bytes);
		} else {
			String  folderPath = propertiesUtil.getProperty("pbom-xml-document-save-folder");
			// 如果是 工装零件 文件夹结构不一样
		//	if (!partTypeName.contains(TypeNameConstants.gzRootPartTypeName)
		//			&& !partTypeName.contains(TypeNameConstants.gzToolingPartTypeName)) {
		//		folderPath = propertiesUtil.getProperty("pbom-xml-document-save-folder");
		//	} else {
		//		folderPath = ((SubFolder) part.getParentFolder().getObject()).getLocation() + "/"+ Constants.gzFolderName[5];
		//	}
			document = WTDocumentUtil.createDocument(part.getName() + endWith, part.getContainer(), folderPath,
					TypeNameConstants.pbomDocTypeName);

			document = WTDocumentUtil.setPrimaryForDocument(document, part.getName() + endWith, bytes);
			WTPartUtil.createWTPartDescribeLink(part, document);
		}
		return true;
}

	/**
	 * 通过零件上传BOM xml
	 *
	 * @author longxiuchuan
	 * @date 2014-1-14
	 * @param oid
	 * @param bytes
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 * @throws FileNotFoundException
	 *
	 */
	public static boolean saveBOMXml2(WTPart part, byte[] bytes, String endWith) throws WTException,
			FileNotFoundException, PropertyVetoException, IOException {
		WTDocument document = getBOMXmlDoc2(part, endWith);
		if (document != null) {
			document = (WTDocument)VersionControlHelper.service.newVersion(document);
			document = (WTDocument)PersistenceHelper.manager.store(document);
			document = WTDocumentUtil.setPrimaryForDocument(document, part.getName() + endWith, bytes);

			//删除修订版本后关联的工艺规程文档Link
			System.out.println("saveBOMXml2->deleteTechnicsDescribeLink:"+part.getNumber());
			WTPartUtil.deleteTechnicsDescribeLink(part);
			//删除修订版本后关联的PBOM文档Link
			WTPartUtil.deletePbomDescribeLink(part);

			//将修订后的PBOM文档与修订后的部件关联
			WTPartUtil.createWTPartDescribeLink(part, document);
		} else {
			GLLogger.debug(CLASSNAME, "--saveBOMXml2--document is null--");
			return false;
		}
		return true;
	}

	public static boolean saveBOMXmlForNewVersion(WTPart part, byte[] bytes, String endWith) throws WTException,
		FileNotFoundException, PropertyVetoException, IOException {
		String state = part.getState().getState().getDisplay(Locale.CHINA);
		if(!"已批准".equals(state)){
			//删除修订版本后关联的工艺规程文档Link
			System.out.println("saveBOMXmlForNewVersion->deleteTechnicsDescribeLink："+part.getNumber());
			//WTPartUtil.deleteTechnicsDescribeLink(part);
			//删除修订版本后关联的PBOM文档Link
			WTPartUtil.deletePbomDescribeLink(part);
            String folderPath = propertiesUtil.getProperty("pbom-xml-document-save-folder");
            // 如果是 工装零件 文件夹结构不一样
//			if (!partTypeName.contains(TypeNameConstants.gzRootPartTypeName)
//					&& !partTypeName.contains(TypeNameConstants.gzToolingPartTypeName)) {
//				folderPath = propertiesUtil.getProperty("pbom-xml-document-save-folder");
//			} else {
//				folderPath = ((SubFolder) part.getParentFolder().getObject()).getLocation() + "/"+ Constants.gzFolderName[5];
//			}
            WTDocument document = WTDocumentUtil.createDocument(part.getName() + endWith, part.getContainer(), folderPath,
                    TypeNameConstants.pbomDocTypeName);

            document = WTDocumentUtil.setPrimaryForDocument(document, part.getName() + endWith, bytes);
            WTPartUtil.createWTPartDescribeLink(part, document);
		}

		return true;
	}
	/**
	 * @author fly
	 * @date 2013-5-31
	 * @param isCompared
	 *            ：YES| NO
	 * @throws Exception
	 *
	 */
	public static boolean saveEbomComparedFlage(WTPart part, String isCompared) throws Exception {
		System.out.println("---saveEbomComparedFlage-------part--"+part.getNumber()+"    --isCompared--"+isCompared);
		boolean isSuccess = false;
		WTPart designPart = WTPartUtil.getLatestPartByNumberAndView(part, Constants.design);
//		WTDocument doc = getBOMXmlDoc(designPart, Constants.ebomDocEndwith);
//		if(doc==null){
			return PBOMHelper.saveEbomXml(designPart);
//		}
//		XMLUtil xml = new XMLUtil(ContentServerHelper.service.findContentStream((ApplicationData) ContentHelper.service
//				.getPrimary(doc)));
//		org.jdom.Element el = xml.getRootElement();
//		System.out.println("-------" + el.getName());
//		if (isCompared == null || "".equals(isCompared)) {
//			el.setAttribute("isCompared", "NONEED");
//		} else {
//			el.setAttribute("isCompared", isCompared);
//		}
//		String xmlString = xml.doc2ToString();
//		System.out.println("xmlString=" + xmlString);
//		isSuccess = saveBOMXml(part, xmlString.getBytes(), Constants.ebomDocEndwith);

//		return isSuccess;
	}

	/**
	 * 保存ebom的结构到design视图
	 *
	 * @author fly
	 * @date 2013-5-17
	 * @param part
	 * @return
	 * @throws WTException
	 *
	 */
	public static boolean saveEbomXml(WTPart part) {
		boolean isSuccess = true;
		try {
			Document doc = DocumentHelper.createDocument();
			doc.setXMLEncoding("GBK");
			Element product = DocumentHelper.createElement("product");
			product.addAttribute("productName", "");
			product.addAttribute("isCompared", "needCompare");
			Element partInfo1 = structEbomElement(part);
			product.add(partInfo1);
			doc.add(product);
			String xml = XMLHelper.doucmnetToXMLString(doc);
			System.out.println("PBOMHelper------saveEbomXml----xml=" + xml);

			isSuccess = saveBOMXml(part, xml.getBytes(), Constants.ebomDocEndwith);

		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}
		return isSuccess;
	}

	/**
	 * structEbomElement
	 *
	 * @author fly
	 * @date 2013-5-17
	 * @param part
	 * @return
	 * @throws WTException
	 *
	 */
	public static Element structEbomElement(WTPart part) throws WTException {
		Element partInfo = getPartInfoElementByPart(part);
		Element subChilds = getsubChildsElement();
		partInfo.add(subChilds);

//		List<WTPart> childs = WTPartUtil.getChildPart(part);
//		for (WTPart child : childs) {
//
//			Element subPartInfo = getPartInfoElementByPart(child);
//
//			subChilds.add(subPartInfo);
//
//			structSubEbomElement(child, subPartInfo);
//
//			// subPartInfo.add(el);
//		}

		//modify by LongXiuChuan 2015/8/12/ 保存EBOM零部件的数量信息
		QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part, WTPartUtil.getConfigSpec());
		while (qr.hasMoreElements()) {
			Persistable[] per = (Persistable[]) qr.nextElement();
			WTPartUsageLink link = (WTPartUsageLink)per[0];
			Object obj = per[1];
			WTPart child = null;
			if (obj instanceof WTPart) {
				child = (WTPart)obj;
			} else if (obj instanceof WTPartMaster) {
				WTPartMaster master = (WTPartMaster) obj;
				child = WTPartUtil.getLatestPartByMaster(master);
			}

			if(child != null) {
				String useCount = String.valueOf((int)link.getQuantity().getAmount());
				Element subPartInfo = getPartInfoElementByPart(child, useCount);
				subChilds.add(subPartInfo);
				structSubEbomElement(child, subPartInfo);
			}

		}

		return partInfo;

	}

	/**
	 * @author fly
	 * @date 2013-5-17
	 * @param part
	 * @return
	 * @throws WTException
	 *
	 */
	public static void structSubEbomElement(WTPart part, Element el) throws WTException {
//		List<WTPart> childs = WTPartUtil.getChildPart(part);
//		if (childs.size() > 0) {
//			Element subChilds = getsubChildsElement();
//			el.add(subChilds);
//			for (WTPart child : childs) {
//				Element subPartInfo = getPartInfoElementByPart(child);
//
//				subChilds.add(subPartInfo);
//
//				structSubEbomElement(child, subPartInfo);
//				// subPartInfo.add(el);
//			}
//		}

		//modify by LongXiuChuan 2015/8/12/ 保存EBOM零部件的数量信息
		QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part, WTPartUtil.getConfigSpec());
		if(qr.hasMoreElements()) {
			Element subChilds = getsubChildsElement();
			el.add(subChilds);
			while (qr.hasMoreElements()) {
				Persistable[] per = (Persistable[]) qr.nextElement();
				WTPartUsageLink link = (WTPartUsageLink)per[0];
				Object obj = per[1];
				WTPart child = null;
				if (obj instanceof WTPart) {
					child = (WTPart)obj;
				} else if (obj instanceof WTPartMaster) {
					WTPartMaster master = (WTPartMaster) obj;
					child = WTPartUtil.getLatestPartByMaster(master);
				}

				if(child != null) {
					String useCount = String.valueOf((int)link.getQuantity().getAmount());
					Element subPartInfo = getPartInfoElementByPart(child, useCount);
					subChilds.add(subPartInfo);
					structSubEbomElement(child, subPartInfo);
				}

			}
		}

	}

	/**
	 * 获取一个part的xml element
	 *
	 * @author fly
	 * @date 2013-5-17
	 * @param part
	 * @return
	 *
	 */
	public static Element getPartInfoElementByPart(WTPart part) {
		Element partInfo = DocumentHelper.createElement("partInfo");
		partInfo.addAttribute("partNumber", part.getNumber());
		partInfo.addAttribute("partName", part.getName());
		partInfo.addAttribute("version", part.getVersionIdentifier().getValue() + "."
				+ part.getIterationIdentifier().getValue());
		return partInfo;
	}

	/**
	 * 获取一个part的xml element
	 *
	 * @author fly
	 * @date 2013-5-17
	 * @param part
	 * @return
	 *
	 */
	public static Element getPartInfoElementByPart(WTPart part, String useCount) {
		Element partInfo = DocumentHelper.createElement("partInfo");
		partInfo.addAttribute("partNumber", part.getNumber());
		partInfo.addAttribute("partName", part.getName());
		partInfo.addAttribute("version", part.getVersionIdentifier().getValue() + "."
				+ part.getIterationIdentifier().getValue());
		partInfo.addAttribute("useCount", useCount);
		return partInfo;
	}

	public static Element getsubChildsElement() {
		return DocumentHelper.createElement("subChilds");
	}

	public static void main(String[] args) throws Exception {
		RemoteMethodServer server = RemoteMethodServer.getDefault();
		server.setUserName("wcadmin");
		server.setPassword("wcadmin");
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid("wt.part.WTPart:1181559");
		GLLogger.debug("part=" + part.getName());
		saveEbomXml(part);
		// validatePbomXml(part);
	}

	public static boolean validatePbomXml(WTPart part) throws Exception {
		WTPart pPart = WTPartUtil.getLatestPartByNumberAndView(part.getNumber(), LoadConfig.getInstance().getPbomView());
		WTDocument doc = PBOMHelper.getBOMXmlDoc(pPart, Constants.pbomDocEndwith);
		GLLogger.debug("doc=" + doc.getName());
		ApplicationData data = (ApplicationData) ContentHelper.service.getPrimary(doc);
		// Document
		// document=XmlUtil.getDocument(WTDocumentUtil.applicationDataToByte(data));

		// File file = new
		// File("C:\\Users\\Administrator\\Desktop\\副天线和差选束开关_-pbom.xml");
		SWXMLUtil xmlUtil = new SWXMLUtil(ContentServerHelper.service.findContentStream(data));
		org.jdom.Element rootElement = xmlUtil.getRootElement();
		GLLogger.debug("root element=" + rootElement.getName());
		org.jdom.Element el = rootElement.getChild("parts").getChild("QMPartInfo");
		checkSubPart(el);
		return true;

	}

	public static void checkSubPart(org.jdom.Element el) throws WTException {
		if (el.getChild("childs") != null) {
			checkPbomPartElement(el);
			List<org.jdom.Element> els = el.getChild("childs").getChildren();
			GLLogger.debug("els size=" + els.size());
			for (org.jdom.Element subEl : els) {
				if (subEl != null) {
					checkSubPart(subEl);
				} else {
					GLLogger.debug("els==null");
				}
			}
		}
	}

	/**
	 * @author fly
	 * @date 2013-5-28
	 * @param el
	 * @throws WTException
	 *
	 */
	public static void checkPbomPartElement(org.jdom.Element el) throws WTException {
		String partNumber = el.getAttributeValue("partNumber");
		if (el.getAttributeValue("workShop") == null || "".equals(el.getAttributeValue("workShop"))) {
			throw new WTException("零件（" + partNumber + "）主制单位不能为空");
		}
		if (el.getAttributeValue("materialType") == null || "".equals(el.getAttributeValue("materialType"))) {
			throw new WTException("零件（" + partNumber + "）物料类型不能为空");
		}
	}

}