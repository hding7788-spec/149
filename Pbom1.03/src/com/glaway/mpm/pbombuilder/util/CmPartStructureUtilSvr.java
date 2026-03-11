package com.glaway.mpm.pbombuilder.util;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryKey;
import wt.fc.QueryResult;
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
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.representation.RepresentationHelper;
import wt.util.WTException;
import wt.vc.config.ConfigSpec;
import wt.vc.views.ViewHelper;

import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.data.CmPartUsesOcc;
import com.glaway.mpm.pbombuilder.data.CmPartWithOcc;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.InforObject;

/**
 * <br>
 * Created on 2012-10-25
 *
 * @author chenyunlong
 */
public class CmPartStructureUtilSvr implements RemoteAccess, Serializable {
	private static final long serialVersionUID = -6029963305260357027L;

	private static final CmLogger log = CmLogger.getLogger(CmPartStructureUtilSvr.class);

	public static CmPartWithOcc buildStructure(WTPart parent) {
		CmPartWithOcc ret = null;

		log.debug("enter parent=", parent == null ? null : parent.getIdentity());
		if (parent != null) {


			CmScrollPaneTree.versionInfor.put(parent.getNumber(),parent.getVersionInfo().getIdentifier().getValue()+"."+parent.getIterationInfo().getIdentifier().getValue());

			Map<WTPartUsageLink, CmPartWithOcc> partUsageLinkMap = new HashMap<WTPartUsageLink, CmPartWithOcc>();
			List<CmPartWithOcc> leafNodeList = new ArrayList<CmPartWithOcc>();

			CmLightPart lightPart = CmBizObjUtil.buildCmLightPartFromWTPart(parent);
			InforObject ob =new InforObject();
			ob.setNumber(lightPart.getPartNumber());
			ob.setName(lightPart.getPartName());
			ob.setUsecount(String.valueOf(lightPart.getUseCount()));
			CmScrollPaneTree.ebomMap.put(parent.getNumber(), ob);
			CmInstanceData instanceData = CmBizObjUtil.buildInstanceDataRoot(lightPart, null, null);
			ret = CmPartWithOcc.newCmPartWithOcc(lightPart, CmPartUsesOcc.newCmPartUseOcc(instanceData));

			// ConfigSpec configSpec = new LatestConfigSpec();
			// ConfigSpec configSpec = null;
			// try {
			// configSpec =
			// ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
			// } catch (WTException e) {
			// e.printStackTrace();
			// }
			WTPartConfigSpec configSpec = null;
			try{
				configSpec = WTPartConfigSpec.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(ViewHelper.service.getView("Design"), null));
			}catch(WTException e){

			}

			populateStructure(ret, parent, configSpec, partUsageLinkMap, leafNodeList);
			populateOccurences(partUsageLinkMap);
			populatePViewURLs(leafNodeList);
			populateStructureWithSameULink(ret);
		}
		return ret;
	}

	public static void populateStructureWithSameULink(CmPartWithOcc ret){
		List<CmPartWithOcc> list = new ArrayList<CmPartWithOcc>();
		List<CmPartWithOcc> emptylist = new ArrayList<CmPartWithOcc>();
		getSameUlinkPartList(ret,list,emptylist);
		for(int i=0;i<emptylist.size();i++){
			for(int j=0;j<list.size();j++){
				if(emptylist.get(i).getUlink().equals(list.get(j).getUlink())
						&& list.get(j).getOccurences() != null
						&& list.get(j).getOccurences().size() > 0
						&& emptylist.get(i).getQuantity() == list.get(j).getQuantity()){
					for(int k=0;k<list.get(j).getOccurences().size();k++){
						emptylist.get(i).addUseOcc(list.get(j).getOccurences().get(k));
					}
				}
			}
		}
	}

	public static void getSameUlinkPartList(CmPartWithOcc ret,List<CmPartWithOcc> list,List<CmPartWithOcc> emptylist){
		Vector<CmPartWithOcc> vec = ret.getChildren();
		for(int i=0;i<vec.size();i++){
			if(null == vec.get(i).getOccurences() || vec.get(i).getOccurences().size()==0){
				emptylist.add(vec.get(i));
			}else{
				list.add(vec.get(i));
			}
			if(null != vec.get(i).getChildren() && vec.get(i).getChildren().size()>0){
				getSameUlinkPartList(vec.get(i),list,emptylist);
			}
		}
	}

	private static CmPartWithOcc populateStructure(CmPartWithOcc parentNode, WTPart parent, ConfigSpec configSpec,
			Map<WTPartUsageLink, CmPartWithOcc> partUsageLinkMap, List<CmPartWithOcc> leafNodeList) {
		try {
			QueryResult qr = WTPartHelper.service.getUsesWTParts(parent, configSpec);
			if (!qr.hasMoreElements())
				leafNodeList.add(parentNode);

			while (qr.hasMoreElements()) {
				Persistable[] objs = (Persistable[]) qr.nextElement();
				if (objs[1] instanceof WTPart) {
					WTPartUsageLink ulink = (WTPartUsageLink) objs[0];
					WTPart part = (WTPart) objs[1];


					CmScrollPaneTree.versionInfor.put(part.getNumber(),part.getVersionInfo().getIdentifier().getValue()+"."+part.getIterationInfo().getIdentifier().getValue());
					CmLightPart lightPart = CmBizObjUtil.buildCmLightPartFromWTPart(part);
					lightPart.setUseCount(((int) ulink.getQuantity().getAmount()));
					CmIBAHelper ibaHelper = new CmIBAHelper(ulink);
					try {
						String invcode = ibaHelper.getIBAValue(ulink, "CHBM");
						if(invcode!=null && "".equals(invcode)){
							lightPart.getWzk().setInvcode(invcode);
						}

						//工艺数量，默认是继承设计数量
						String gysl = ibaHelper.getIBAValue(ulink, "GYSL");
						if(gysl != null && !"".equals(gysl)) {
							lightPart.setGysl(gysl);
						} else {
							lightPart.setGysl(String.valueOf(((int) ulink.getQuantity().getAmount())));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					InforObject ob =new InforObject();
					ob.setNumber(lightPart.getPartNumber());
					ob.setName(lightPart.getPartName());
					ob.setUsecount(String.valueOf(String.valueOf(((int) ulink.getQuantity().getAmount()))));
					CmScrollPaneTree.ebomMap.put(part.getNumber(), ob);

					CmPartWithOcc partNode = CmPartWithOcc.newCmPartWithOcc(lightPart);
					partNode.setQuantity((int) (ulink.getQuantity().getAmount()));
					partNode.setUlink(ulink.toString());
					partUsageLinkMap.put(ulink, partNode);
					if (parentNode != null)
						parentNode.addChild(partNode);
					populateStructure(partNode, part, configSpec, partUsageLinkMap, leafNodeList);
				}
			}
		} catch (WTException e) {
		}

		return parentNode;
	}

	private static void populateOccurences(Map<WTPartUsageLink, CmPartWithOcc> partUsageLinkMap) {
		log.debug("enter populateOccurences partUsageLinkMap.size()" + partUsageLinkMap.size());
		if (partUsageLinkMap.size() > 0) {
			try {
				WTKeyedMap occHm = OccurrenceHelper.service.getUsesOccurrences(new WTArrayList(partUsageLinkMap
						.keySet()));
				WTKeyedHashMap uHMap = new WTKeyedHashMap(partUsageLinkMap);
				for (Iterator usageIt = uHMap.wtKeySet().persistableIterator(); usageIt.hasNext();) {
					WTPartUsageLink ulink = (WTPartUsageLink) usageIt.next();
					WTHashSet occCol = (WTHashSet) occHm.get(ulink);

					CmPartWithOcc nwoc = ((CmPartWithOcc) partUsageLinkMap.get(ulink));
					Vector<CmPartUsesOcc> occsV = nwoc.getOccurences();
					if (occCol == null || occCol.size() == 0) {
						CmInstanceData instanceData = CmBizObjUtil.buildInstanceData(nwoc.getPart(), ulink, null);
						CmPartUsesOcc usesOcc = CmPartUsesOcc.newCmPartUseOcc(instanceData);
						occsV.add(usesOcc);
						populateStructureWithoutOccurence(nwoc,occsV,ulink);
					} else {
						for (Iterator it = occCol.persistableIterator(); it.hasNext();) {
							PartUsesOccurrence partUsesOcc = (PartUsesOccurrence) it.next();
							Matrix4d m4d = partUsesOcc.isHasTransform() ? partUsesOcc.toMatrix4d() : CmPartUsesOcc.STD_MATRIX4D;
							CmInstanceData instanceData = CmBizObjUtil.buildInstanceData(nwoc.getPart(), ulink, partUsesOcc);
							CmPartUsesOcc usesOcc = CmPartUsesOcc.newCmPartUseOcc(instanceData, m4d);
							occsV.add(usesOcc);
						}
					}
				}
			} catch (WTException wte) {
				log.error(wte);
			}
		}
	}

	public static void populateStructureWithoutOccurence(CmPartWithOcc nwoc,Vector<CmPartUsesOcc> occsV,WTPartUsageLink ulink){
		if(nwoc.getQuantity() != nwoc.getOccurences().size()){
			int i= nwoc.getQuantity()- nwoc.getOccurences().size();
			for(int j=0;j<i;j++){
				CmInstanceData instanceData = CmBizObjUtil.buildInstanceData(nwoc.getPart(), ulink, null);
				CmPartUsesOcc usesOcc = CmPartUsesOcc.newCmPartUseOcc(instanceData);
				occsV.add(usesOcc);
			}

		}
	}

	private static void populatePViewURLs(List<CmPartWithOcc> leafNodes) {
		if (leafNodes.size() > 0) {
			HashSet<ObjectIdentifier> oidSet = new HashSet<ObjectIdentifier>(leafNodes.size());
			for (Iterator it = leafNodes.iterator(); it.hasNext();)
				oidSet.add(new ObjectIdentifier(WTPart.class, new Long(((CmPartWithOcc) it.next()).getPart().getOid())));

			try {
				HashMap pvUrlHm = getPViewURLHashMap(oidSet, ".ol");
				for (Iterator it = leafNodes.iterator(); it.hasNext();) {
					CmLightPart partNode = ((CmPartWithOcc) it.next()).getPart();
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
		WTKeyedMap appDataKMap = ".pvs".equalsIgnoreCase(extension) ? ContentHelper.service.getContentsByRole(
				derivedImgSet, ContentRoleType.PRODUCT_VIEW_ED) : ContentHelper.service.getContentsByRole(
				derivedImgSet, ContentRoleType.SECONDARY);

		if (appDataKMap == null || appDataKMap.size() == 0)
			return partUrlHM;

		for (Iterator partIt = theRepresentationKMap.wtKeySet().referenceIterator(); partIt.hasNext();) {
			boolean foundOl4Part = false;
			WTPart part = (WTPart)((ObjectReference) partIt.next()).getObject();
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
							&& ((ApplicationData) cItem).getFileName().endsWith(".ol")) {
						URL url = CmUtilSvr.getDownloadURL(part, (ApplicationData) cItem);
						partUrlHM.put(new Long(part.getPersistInfo().getObjectIdentifier().getId()), url);
						foundOl4Part = true;
					}
				}
			}
		}
		log.debug("getPViewURLHashMap="+partUrlHM);
		return partUrlHM;
	}
}
