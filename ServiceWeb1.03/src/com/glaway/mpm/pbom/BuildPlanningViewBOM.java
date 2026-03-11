package com.glaway.mpm.pbom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.views.ViewHelper;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.FolderUtil;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTPartUtil;
import com.glaway.mpm.util.SWXMLUtil;

/**
 * 建立BOM视图
 * 
 * @author lbzhang
 * 
 */
public class BuildPlanningViewBOM {

	/**
	 * 根据PBOM的XML，创建PBOM
	 * 
	 * @author lbzhang
	 * @date 2012-11-19下午08:19:53
	 * @param ebomPart
	 */
	@SuppressWarnings("unchecked")
	public static void buildPlanningPBOM(WTPart ebomPart) {
		GLLogger.debug("-------start structurePBOM----------");
		// Transaction transaction = null;
		// InputStream inputStream = null;
		// try {
		// transaction = new Transaction();
		// transaction.start();
		// ArrayList<String> partNumberList = new ArrayList<String>();
		// WTCollection linkCollection = new WTArrayList();
		// GLLogger.debug("start buildPalnningPBOM!");
		// inputStream = WTPartUtil.getInputStreamOfDescriptedDoc(ebomPart);
		// WTContainerRef containerRef = ebomPart.getContainerReference();
		// GLLogger.debug("containerRef==>" + containerRef.getName());
		// XMLUtil xmlUtil = new XMLUtil(inputStream);
		// Element rootElement = xmlUtil.getRootElement();
		// GLLogger.debug("---rootElement-" + rootElement.getName());
		// if ("Product".equals(rootElement.getName())) {
		// Element partElement = rootElement.getChild("parts");
		// if (partElement != null) {
		// for (Element partAttrElement : (List<Element>)
		// partElement.getChildren("QMPartInfo")) {
		// GLLogger.debug("---partAttrElement-" + partAttrElement.getName());
		// // create part, according partAttrElement create WTPart;
		// WTPart part = createWTPart(partAttrElement, containerRef);
		// partNumberList.add(part.getNumber());
		//
		// // 处理childPart
		// for (Element partChildElement : (List<Element>)
		// partAttrElement.getChildren("childs")) {
		// GLLogger.debug("---partChildElement-" + partChildElement.getName());
		// structureChildPart(part, partChildElement, linkCollection,
		// partNumberList, containerRef);
		// }
		// }
		// }
		// }
		//
		// if (linkCollection.size() > 0) {
		// PersistenceServerHelper.manager.insert(linkCollection);
		// }
		//
		GLLogger.debug("-------end structurePBOM----------");
		// transaction.commit();
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (inputStream != null) {
		// try {
		// inputStream.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }
	}

	/**
	 * 递归创建PBOM
	 * 
	 * @author lbzhang
	 * @date 2012-11-19下午08:20:40
	 * @param parentPart
	 * @param childPartElement
	 * @param linkCollection
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static void structureChildPart(WTPart parentPart, Element childPartElement, WTCollection linkCollection,
			ArrayList<String> partNumberList, WTContainerRef containerRef) throws Exception {

		for (Element childPartAttrElement : (List<Element>) childPartElement.getChildren("QMPartInfo")) {
			GLLogger.debug("---childPartAttrElement-" + childPartAttrElement.getName());

			WTPart childPart = null;
			if (partNumberList.contains(childPartAttrElement.getAttributeValue("partNumber"))) {
				childPart = WTPartUtil.getLatestPartByPartNumber(childPartAttrElement.getAttributeValue("partNumber"));
			} else {
				// create sun WTPart according element;
				childPart = createWTPart(childPartAttrElement, containerRef);
				partNumberList.add(childPartAttrElement.getAttributeValue("partNumber"));
			}

			String useCount = "";
			// create link according parent part and son part;
			if ("".equals(childPartAttrElement.getAttributeValue("useCount"))) {
				useCount = "1";
			} else {
				useCount = childPartAttrElement.getAttributeValue("useCount");
			}

			WTPartUsageLink link = buildWTPartUsageLink(parentPart, childPart, Double.parseDouble(useCount));
			linkCollection.add(link);
			for (Element subChildPartElement : (List<Element>) childPartAttrElement.getChildren("childs")) {
				GLLogger.debug("---subChildPartElement-" + subChildPartElement.getName());
				structureChildPart(childPart, subChildPartElement, linkCollection, partNumberList, containerRef);
			}
		}
	}

	/**
	 * 创建零件(工艺辅件/工艺中间件),若零件存在，则先删除其结构关联Link，再建立Planning视图零件；
	 * 若不存在则直接建立Planning视图的零件
	 * 
	 * @author lbzhang
	 * @date 2012-11-19下午08:21:04
	 * @param e
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private static WTPart createWTPart(Element e, WTContainerRef containerRef) throws Exception {
		String oid = e.getAttributeValue("oid");
		if (!"".equals(oid)) {// the part is existed! then build new planning
								// part based on oringal part

			GLLogger.debug("origalOid==>" + oid);
			WTPart part = (WTPart) ReferenceFactory.getObjectbyOid("wt.part.WTPart:" + oid);// 获取最新版本的零件
			String system = Util.getSoftAttribute(part, "System");
			GLLogger.debug("part System is===>" + system);
			if(system == null || "".equals(system)){
				system = "";
			}else{
				system = "/" + system;
			}
			String partType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(part);
			GLLogger.debug("partType===>" + partType);
			if (!partType.endsWith("com.nriet.PurchasedPart")) {
				WTPart newViewPart = (WTPart) ViewHelper.service.newBranchForView(part, Constants.planning);
				GLLogger.debug("newViewPart====>" + newViewPart.getName() + "   " + newViewPart.getNumber());
				LifeCycleHelper.setLifeCycle(newViewPart, part.getLifeCycleTemplate());// 设置新视图版本零件的生命周期

				newViewPart.setTeamTemplateId(part.getTeamTemplateId());// 设置新视图版本的专案团队
				newViewPart = (WTPart) PersistenceHelper.manager.store(newViewPart);// 保存新视图版本零件
				newViewPart = (WTPart) PersistenceHelper.manager.refresh(newViewPart);// 同步数据库中的数据
//				Folder folder = FolderHelper.service.getFolder("/Default/70：PBOM" + system, containerRef);
				Folder folder = FolderUtil.getFolder( "/Default/70：PBOM" + system,containerRef);
				FolderHelper.service.changeFolder(newViewPart, folder);
				// 设置零件的软属性
				newViewPart = WTPartUtil.setSoftAttributeOfPlanningPart(newViewPart, e);
				// 设置零件的状态为已归档
				// WTPartUtil.setPartLifecycle(newViewPart, Constants.RELEASED);
				newViewPart = (WTPart) PersistenceHelper.manager.refresh(newViewPart);
				WTPartUtil.deleteWTPartOriginalLinkByPart(newViewPart);

				return newViewPart;
			} else {
				return part;
			}

		} else {// the oid part is not exist! so build new planning part.
			WTPart part = WTPartUtil.createPart(e, containerRef);// 获取新建的Planning视图零件对象,现创建的为Design视图的零件
			System.out.println("part===>" + part.getName() + "   " + part.getViewName());
			part = (WTPart) PersistenceHelper.manager.refresh(part);
			part = WTPartUtil.setSoftAttributeOfPlanningPart(part, e);
			// 设置零件的状态为已归档
			 Util.setLifecycle(part, Constants.RELEASED);
			WTPart newViewPart = (WTPart) PersistenceHelper.manager.refresh(part);
			
			newViewPart = (WTPart) ViewHelper.service.newBranchForView(newViewPart,  Constants.planning);//再创建Planning视图的零件
			LifeCycleHelper.setLifeCycle(newViewPart, part.getLifeCycleTemplate());// 设置新视图版本零件的生命周期
			newViewPart.setTeamTemplateId(part.getTeamTemplateId());// 设置新视图版本的专案团队
			newViewPart = (WTPart) PersistenceHelper.manager.store(newViewPart);// 保存新视图版本零件
			newViewPart = (WTPart) PersistenceHelper.manager.refresh(newViewPart);// 同步数据库中的数据
			GLLogger.debug("newViewPart=====>" + newViewPart.getName() + "   " + newViewPart.getViewName());
			return newViewPart;
		}
	}

	/**
	 * 保存零件之间的关系
	 * 
	 * @author lbzhang
	 * @date 2012-11-19下午08:21:45
	 * @param parentPart
	 * @param childrenPart
	 * @param d
	 * @return
	 * @throws WTException
	 */
	private static WTPartUsageLink buildWTPartUsageLink(WTPart parentPart, WTPart childrenPart, double d)
			throws WTException {
		WTPartUsageLink partLink = WTPartUsageLink.newWTPartUsageLink(parentPart, (WTPartMaster) childrenPart
				.getMaster());
		partLink.setQuantity(Quantity.newQuantity(d, QuantityUnit.toQuantityUnit("ea")));
		return partLink;

	}
}
