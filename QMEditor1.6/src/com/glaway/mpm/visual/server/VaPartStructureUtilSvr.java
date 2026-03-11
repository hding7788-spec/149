/**
 * <br>Created on 2010-10-25
 * @author Dennis Huang - ���ٽ�
 */
package com.glaway.mpm.visual.server;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.vecmath.Matrix4d;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.fc.ObjectIdentifier;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryKey;
import wt.fc.QueryResult;
import wt.fc.WTReference;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTKeyedHashMap;
import wt.fc.collections.WTKeyedMap;
import wt.method.RemoteAccess;
import wt.occurrence.OccurrenceHelper;
import wt.part.PartUsesOccurrence;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.RepresentationHelper;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;

import com.glaway.mpm.visual.bean.VaInstanceData;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.bean.VaPartUsesOcc;
import com.glaway.mpm.visual.bean.VaPartWithOcc;
import com.glaway.mpm.visual.biz.VaBizObjUtil;
import com.glaway.mpm.visual.conf.VaConstants;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.query.QMLHelper;
import com.glaway.mpm.visual.util.VaSearchHelper;
import com.ptc.core.foundation.configspec.common.ConfigSpecHelper;

/**
 * <br>
 * Created on 2010-10-25
 * 
 * @author Dennis Huang - ���ٽ�
 */
public class VaPartStructureUtilSvr implements RemoteAccess, Serializable {
	private static final long serialVersionUID = -6029963305260357027L;

	private static final VaLogger log = VaLogger.getLogger(VaPartStructureUtilSvr.class);

	private static final String getBBoxHashMap_qmlPath = "com/glaway/mpm/visual/biz/GetBBoxInfo.qml";

	// public static CmLightPartCBTreeNode buildStructureStopOn(WTPart parent,
	// TypeIdentifier stopType) {
	// CmLightPartCBTreeNode ret = null;
	//
	// if (parent != null) {
	// CmLightPart lightPart = CmBizObjUtil.buildCmLightPartFromWTPart(parent);
	// ret = new CmLightPartCBTreeNode(lightPart);
	// ConfigSpec configSpec = new LatestConfigSpec();
	//
	// // String typeDisplayName =
	// CmSettings.getSection(CmSettings.SECTION_MBOM).get("ebom.structure.leaf.type");
	// String typeDisplayName = "PRT";
	// TypeIdentifier DCIType = CmTypeHelper.getLightType(typeDisplayName,
	// true).getTypeIdentifier();
	//
	// buildStructureStopOn(ret, parent, configSpec, stopType, DCIType);
	// }
	//
	// return ret;
	// }

	// private static CmLightPartCBTreeNode
	// buildStructureStopOn(CmLightPartCBTreeNode parentNode, WTPart parent,
	// ConfigSpec configSpec,
	// TypeIdentifier stopType, TypeIdentifier dciType) {
	// // 1. �����dciType��չ����ֱ�ӷ���
	// // 2. parentNode��������stopType
	// if (CmTypeHelper.getLightType(parent).isA(dciType))
	// return parentNode;
	//
	// try {
	// QueryResult qr = WTPartHelper.service.getUsesWTParts(parent, configSpec);
	// while (qr.hasMoreElements()) {
	// Persistable[] objs = (Persistable[]) qr.nextElement();
	// if (objs[1] instanceof WTPart) {
	// WTPart part = (WTPart) objs[1];
	//
	// if (!CmTypeHelper.getLightType(part).isA(dciType) &&
	// CmTypeHelper.getLightType(part).isA(stopType))
	// continue;
	//
	// CmLightPart lightPart = CmBizObjUtil.buildCmLightPartFromWTPart(part);
	// // CmLightPartCBTreeNode partNode = new CmLightPartCBTreeNode(lightPart);
	//
	// parentNode.add(partNode);
	// buildStructureStopOn(partNode, part, configSpec, stopType, dciType);
	// }
	// }
	// } catch (WTException e) {
	// }
	//
	// return parentNode;
	// }

	public static VaPartWithOcc buildStructure(WTPart parent) {
		VaPartWithOcc ret = null;

		log.debug("view:"+parent.getViewName()+" enter parent=", parent == null ? null : parent.getIdentity());
		if (parent != null) {
			Map<WTPartUsageLink, VaPartWithOcc> partUsageLinkMap = new HashMap<WTPartUsageLink, VaPartWithOcc>();
			List<VaPartWithOcc> leafNodeList = new ArrayList<VaPartWithOcc>();

			VaLightPart lightPart = VaBizObjUtil.buildVaLightPartFromWTPart(parent);
			VaInstanceData instanceData = VaBizObjUtil.buildInstanceData(lightPart, null, null);
			ret = VaPartWithOcc.newVaPartWithOcc(lightPart, VaPartUsesOcc.newVaPartUseOcc(instanceData));

			ConfigSpec configSpec = null;
			try {
				configSpec = ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			populateStructure(ret, parent, configSpec, partUsageLinkMap, leafNodeList);
			populateOccurences(partUsageLinkMap);
			populatePViewURLs(leafNodeList);
			populateBBoxes(new ArrayList(partUsageLinkMap.values()));
		}

		return ret;
	}

	private static VaPartWithOcc populateStructure(VaPartWithOcc parentNode, WTPart parent, ConfigSpec configSpec,
			Map<WTPartUsageLink, VaPartWithOcc> partUsageLinkMap, List<VaPartWithOcc> leafNodeList) {
		try {
//			QuerySpec qs = new QuerySpec(WTPart.class);
//			View view = VaSearchHelper.getViewByName(parent.getViewName());
//			qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, view.getPersistInfo().getObjectIdentifier()
//					.getId()), new int[]{ 0 });
//			configSpec.appendSearchCriteria(qs);
			
			QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(parent);//getUsesWTParts(parent,configSpec);
			log.debug("getUsesWTParts(", parent.getNumber(), ", ", configSpec.getClass().getName(), ") ret.size=", qr
					.size());
			
//			QuerySpec qs = new QuerySpec(WTPart.class);
//			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, parent), ZERO);
//			qs.appendAnd();
//			qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewId), ZERO);
//			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			
			
			if (!qr.hasMoreElements())
				leafNodeList.add(parentNode);

			while (qr.hasMoreElements()) {
				WTPartUsageLink ulink = (WTPartUsageLink) qr.nextElement();
				QuerySpec qspart = new QuerySpec(WTPart.class);
				QuerySpec qs = new QuerySpec(WTPart.class);
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, ulink.getUses().getNumber()), new int[]{0});
				qs.appendAnd();
				View view = VaSearchSvrHelper.getViewByNameRMI(parent.getViewName());
				qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, view.getPersistInfo().getObjectIdentifier().getId()), new int[]{0});
				QueryResult qrp = PersistenceHelper.manager.find((StatementSpec) qs);
				LatestConfigSpec lcs = new LatestConfigSpec();
				qrp = lcs.process(qrp);
				if(qrp.hasMoreElements()){
					WTPart part = (WTPart) qrp.nextElement();
					VaLightPart lightPart = VaBizObjUtil.buildVaLightPartFromWTPart(part);
					VaPartWithOcc partNode = VaPartWithOcc.newVaPartWithOcc(lightPart);
					log.debug("ulink.getQuantity()=", ulink.getQuantity().getAmount());
					partNode.setQuantity((int) (ulink.getQuantity().getAmount()));
					partUsageLinkMap.put(ulink, partNode);
					if (parentNode != null)
						parentNode.addChild(partNode);
					populateStructure(partNode, part, configSpec, partUsageLinkMap, leafNodeList);
					
				}
//				if (objs[1] instanceof WTPartMaster) {
//					WTPartUsageLink ulink = (WTPartUsageLink) objs[0];
//					WTPart part = (WTPart) objs[1];
//
//					VaLightPart lightPart = VaBizObjUtil.buildVaLightPartFromWTPart(part);
//					VaPartWithOcc partNode = VaPartWithOcc.newVaPartWithOcc(lightPart);
//					log.debug("ulink.getQuantity()=", ulink.getQuantity().getAmount());
//					partNode.setQuantity((int) (ulink.getQuantity().getAmount()));
//					partUsageLinkMap.put(ulink, partNode);
//					if (parentNode != null)
//						parentNode.addChild(partNode);
//					populateStructure(partNode, part, configSpec, partUsageLinkMap, leafNodeList);
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parentNode;
	}

	private static void populateOccurences(Map<WTPartUsageLink, VaPartWithOcc> partUsageLinkMap) {
		log.debug("enter populateOccurences partUsageLinkMap.size()" + partUsageLinkMap.size());
		if (partUsageLinkMap.size() > 0) {
			try {
				WTKeyedMap occHm = OccurrenceHelper.service.getUsesOccurrences(new WTArrayList(partUsageLinkMap
						.keySet()));
				WTKeyedHashMap uHMap = new WTKeyedHashMap(partUsageLinkMap);
				for (Iterator usageIt = uHMap.wtKeySet().persistableIterator(); usageIt.hasNext();) {
					WTPartUsageLink ulink = (WTPartUsageLink) usageIt.next();
					WTHashSet occCol = (WTHashSet) occHm.get(ulink);

					VaPartWithOcc nwoc = ((VaPartWithOcc) partUsageLinkMap.get(ulink));
					Vector<VaPartUsesOcc> occsV = nwoc.getOccurences();
					if (occCol == null || occCol.size() == 0) {
						VaInstanceData instanceData = VaBizObjUtil.buildInstanceData(nwoc.getPart(), ulink, null);
						VaPartUsesOcc usesOcc = VaPartUsesOcc.newVaPartUseOcc(instanceData);
						occsV.add(usesOcc);
					} else {
						for (Iterator it = occCol.persistableIterator(); it.hasNext();) {
							PartUsesOccurrence partUsesOcc = (PartUsesOccurrence) it.next();

							Matrix4d m4d = partUsesOcc.isHasTransform() ? partUsesOcc.toMatrix4d()
									: VaPartUsesOcc.STD_MATRIX4D;

							VaInstanceData instanceData = VaBizObjUtil.buildInstanceData(nwoc.getPart(), ulink,
									partUsesOcc);
							VaPartUsesOcc usesOcc = VaPartUsesOcc.newVaPartUsesOcc(instanceData, m4d);

							occsV.add(usesOcc);
						}
					}
				}
			} catch (WTException wte) {
				log.error(wte);
			}
		}
	}

	private static void populatePViewURLs(List<VaPartWithOcc> leafNodes) {
		if (leafNodes.size() > 0) {
			HashSet<ObjectIdentifier> oidSet = new HashSet<ObjectIdentifier>(leafNodes.size());
			for (Iterator it = leafNodes.iterator(); it.hasNext();)
				oidSet
						.add(new ObjectIdentifier(WTPart.class,
								new Long(((VaPartWithOcc) it.next()).getPart().getOid())));

			try {
				HashMap pvUrlHm = getPViewURLHashMap(oidSet, VaConstants.PVIEW_OL);
				for (Iterator it = leafNodes.iterator(); it.hasNext();) {
					VaLightPart partNode = ((VaPartWithOcc) it.next()).getPart();
					URL url = (URL) pvUrlHm.get(new Long(partNode.getOid()));
					if (url != null)
						partNode.setPvURL(url);
				}
			} catch (WTException e) {
				log.error(e);
			}
		}
	}

	public static HashMap<Long, URL> getPViewURLHashMap(Collection oidKeys, String extension) throws WTException {
		HashMap<Long, URL> partUrlHM = new HashMap<Long, URL>(oidKeys.size());
		WTArrayList holderParts = new WTArrayList(oidKeys);

		WTKeyedMap theRepresentationKMap = RepresentationHelper.service.getRepresentations(holderParts);
		if (theRepresentationKMap == null || theRepresentationKMap.size() == 0)
			return partUrlHM;
		WTHashSet derivedImgSet = new WTHashSet(theRepresentationKMap.size());
		for (Iterator partIt = theRepresentationKMap.values().iterator(); partIt.hasNext();)
			derivedImgSet.addAll((WTCollection) partIt.next());
		WTKeyedMap appDataKMap = VaConstants.PVIEW_PVS.equalsIgnoreCase(extension) ? ContentHelper.service
				.getContentsByRole(derivedImgSet, ContentRoleType.PRODUCT_VIEW_ED) : ContentHelper.service
				.getContentsByRole(derivedImgSet, ContentRoleType.SECONDARY);

		if (appDataKMap == null || appDataKMap.size() == 0)
			return partUrlHM;

//		for (Iterator partIt = theRepresentationKMap.wtKeySet().persistableIterator(); partIt.hasNext();) {
			for (Iterator partIt = theRepresentationKMap.wtKeySet().referenceIterator(); partIt.hasNext();) {
			boolean foundOl4Part = false;
//			WTPart part = (WTPart) partIt.next();
			WTReference ref = (WTReference) partIt.next();
			WTPart part = (WTPart)ref.getObject();
			WTCollection reps = (WTCollection) theRepresentationKMap.get(part);
			if (reps == null || reps.size() == 0)
				continue;
			for (Iterator repIt = reps.queryKeyIterator(); repIt.hasNext() && !foundOl4Part;) {
				WTCollection contItems = (WTCollection) appDataKMap.get((QueryKey) repIt.next());
				if (contItems == null || contItems.size() == 0)
					continue;
				for (Iterator contIt = contItems.persistableIterator(); contIt.hasNext() && !foundOl4Part;) {
					ContentItem cItem = (ContentItem) contIt.next();
					if (cItem != null && cItem instanceof ApplicationData
							&& ((ApplicationData) cItem).getFileName().endsWith(VaConstants.PVIEW_OL)) {
						partUrlHM.put(new Long(part.getPersistInfo().getObjectIdentifier().getId()), VaUtilSvr
								.getDownloadURL(part, (ApplicationData) cItem));
						foundOl4Part = true;
					}
				}
			}
		}
		return partUrlHM;
	}

	public static void populateBBoxes(List<VaPartWithOcc> leafNodes) {
		// use qml approach here? can reuse oid list of pre-task...
		// bboxes are only required for leaf nodes so get them first and build
		// appropriate hashs
		if (leafNodes.size() > 0) {
			HashSet oidSet = new HashSet(leafNodes.size());
			for (Iterator<VaPartWithOcc> it = leafNodes.iterator(); it.hasNext();)
				oidSet.add(new Long(it.next().getPart().getOid()));
			StringBuffer oidKeys = new StringBuffer();
			for (Iterator it = oidSet.iterator(); it.hasNext();)
				oidKeys.append(it.next()).append(",");

			HashMap bboxHm = getBBoxHashMap(oidKeys.toString());
			// log.debug("bboxHm: "+bboxHm);
			for (Iterator it = leafNodes.iterator(); it.hasNext();) {
				VaPartWithOcc occNode = (VaPartWithOcc) it.next();
				// log.debug("leaf: "+occNode.getPart().getOid()+", its bbox: "+bboxHm.get(new
				// Long(occNode.getPart().getOid())));
				occNode.setWvsBboxes((Vector) bboxHm.get(new Long(occNode.getPart().getOid())));
			}
			// if(log.isDebugEnabled()){
			// for (Iterator it = leafNodes.iterator(); it.hasNext();) {
			// VaPartWithOcc node = (VaPartWithOcc) it.next();
			// log.debug("__leaf node w/ bbox: " + node.getWvsBboxes());
			// }
			// }
		}
	}

	private static HashMap getBBoxHashMap(String oidKeys) {
		HashMap bboxHm = new HashMap();

		Hashtable paramHash = new Hashtable();
		paramHash.put("oidKeys", oidKeys);
		// paramHash.put("isDefaultRep", "1");
		// paramHash.put("appDataDesc", "BBOX*");
		// paramHash.put("appDataRole", "SECONDARY");
		// QueryResult qr = QMLTemplateCache.query(qmlPath, paramHash);
		try {
			QueryResult qr = QMLHelper.query(getBBoxHashMap_qmlPath, paramHash);

			// int cc = 0;
			while (qr.hasMoreElements()) {
				Object[] resObjs = (Object[]) qr.nextElement();
				// if(log.isDebugEnabled()){
				// for (int i = 0; i < resObjs.length; i++) {
				// log.debug("..(" + (cc++) + ").. resObjs[" + i + "]: "
				// + resObjs[i]);
				// if (resObjs[i] != null)
				// log.debug(" of class: " + resObjs[i].getClass());
				// }
				// }
				String bbox = (String) resObjs[2];
				if (bbox == null || bbox.trim().length() == 0) {
					continue;
				}
				String s[] = new String[2];
				s[0] = bbox.substring("BBOX ".length());
				Vector v = new Vector(1);
				v.add(s);
				log.debug(new Long(((BigDecimal) resObjs[0]).longValue()) + ":" + s[0]);
				bboxHm.put(new Long(((BigDecimal) resObjs[0]).longValue()), v);
			}
		} catch (WTException ex) {
			ex.printStackTrace();
		}
		return bboxHm;
	}
}
