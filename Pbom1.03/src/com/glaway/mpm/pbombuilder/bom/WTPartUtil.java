package com.glaway.mpm.pbombuilder.bom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

import com.glaway.mpm.pbombuilder.util.LoadConfig;

public class WTPartUtil {

	/*
	 * 获取零件的子皆零�?
	 */
	@SuppressWarnings("deprecation")
	public static List<WTPart> getChildPart(WTPart part) throws WTException {
		List<WTPart> nodeList = new ArrayList<WTPart>();

		if (part != null) {
			QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part,
					getConfigSpec());
			if (qr != null) {
				while (qr.hasMoreElements()) {
					Persistable[] per = (Persistable[]) qr.nextElement();
					nodeList.add((WTPart) per[1]);
				}
			}
		}
		return nodeList;
	}

	/*
	 * 获取零件与子皆零件的关联关系
	 */

	@SuppressWarnings("deprecation")
	public static List<WTPartUsageLink> getWTPartUsageLink(WTPart part)
			throws WTException {
		List<WTPartUsageLink> nodeList = new ArrayList<WTPartUsageLink>();

		if (part != null) {
			QueryResult qr = WTPartHelper.service.getUsesWTParts((WTPart) part,
					getConfigSpec());
			if (qr != null) {
				while (qr.hasMoreElements()) {
					Persistable[] per = (Persistable[]) qr.nextElement();
					nodeList.add((WTPartUsageLink) per[0]);
				}
			}
		}

		return nodeList;
	}

	/*
	 * 获取零件的子皆零件和零件与子皆零件的关联关系
	 */

	@SuppressWarnings("deprecation")
	public static Map<WTPart, WTPartUsageLink> getChildPartAndLink(WTPart part) {
		Map<WTPart, WTPartUsageLink> nodeMap = new HashMap<WTPart, WTPartUsageLink>();
		try {
			if (part != null) {
				QueryResult qr = WTPartHelper.service.getUsesWTParts(
						(WTPart) part, getConfigSpec());
				if (qr != null) {
					while (qr.hasMoreElements()) {
						Persistable[] per = (Persistable[]) qr.nextElement();
						nodeMap.put((WTPart) per[1], (WTPartUsageLink) per[0]);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return nodeMap;
	}

	/*
	 * 获取ConfigSpec
	 */
	private static ConfigSpec getConfigSpec() throws WTException {

		return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);

	}

	/*
	 * 根据零件编号获取�?��版本�?��版序的零�?
	 */
	public static WTPart getLatestPartByPartNumber(String partNumber)
			throws WTException {
		WTPart part = null;
		int[] index = new int[] { 0 };

		QuerySpec qSpec = new QuerySpec(WTPart.class);
		qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER,
				SearchCondition.EQUAL, partNumber), index);
		QueryResult qResult = PersistenceHelper.manager
				.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		while (qResult.hasMoreElements()) {
			part = (WTPart) qResult.nextElement();
		}

		return part;
	}

	/*
	 * 根据Master获取�?��版本�?��版序的零�?
	 */
	public static WTPart getLatestPartByMaster(WTPartMaster partMaster)
			throws WTException {
		WTPart part = null;

		if (partMaster != null) {
			QueryResult qr = VersionControlHelper.service
					.allVersionsOf(partMaster);
			if (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
			}
		}

		return part;
	}

	/*
	 * 根据零件编号获取零件的Master
	 */
	public static WTPartMaster getWTPartMasterByNumber(String partNumber) throws WTException {
		WTPartMaster partMaster = null;
		int[] index = new int[] { 0 };

		QuerySpec qs = new QuerySpec(WTPartMaster.class);
		qs.appendWhere(new SearchCondition(WTPartMaster.class,
				WTPartMaster.NUMBER, SearchCondition.EQUAL, partNumber), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			partMaster = (WTPartMaster) qr.nextElement();
		}

		return partMaster;

	}

	/*
	 * 通过编号 获取零件相关联的图档
	 */
	public static List<EPMDocument> getEPMDocumentByPartNumber(String number)
			throws WTException {
		List<EPMDocument> list = new ArrayList<EPMDocument>();
		WTPart part = getLatestPartByPartNumber(number);

		if (part != null) {
			QueryResult qr = WTPartHelper.service.getDescribedByDocuments(part,
					true);
			while (qr.hasMoreElements()) {
				Object ob = qr.nextElement();
				if (ob instanceof EPMDocument) {
					list.add((EPMDocument) ob);
				}
			}
		}

		return list;
	}


	/**
	 * 获取最新的设计视图
	 * @param partMaster
	 * @return
	 * @throws WTException
	 */
	public static WTPart getLatestDesinPartByMaster(WTPartMaster partMaster)
			throws WTException {
		WTPart part = null,designPart = null;
		if (partMaster != null) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(partMaster);
			while (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
				if("Design".equals(part.getViewName())){
					designPart = part;
					break;
				}
			}
		}

		return designPart;
	}

	/**
	 * 获取Manufacturing对应的设计视图part
	 * @param partMaster
	 * @return
	 * @throws WTException
	 */
	public static WTPart getDesinPart(WTPart mpart) throws WTException {
		WTPart part = null;
		if (mpart != null) {
			String mVersion = mpart.getVersionInfo().getIdentifier().getValue();
			QueryResult qr = VersionControlHelper.service.allVersionsOf(mpart.getMaster());
			while (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
				String dVersion = part.getVersionInfo().getIdentifier().getValue();
				if("Design".equals(part.getViewName()) && mVersion.equals(dVersion)){
					return part;
				}
			}
		}
		return null;
	}

	/**
	 * 获取Design对应的设计视图part
	 * @param partMaster
	 * @return
	 * @throws WTException
	 */
	public static WTPart getManufacturingPart(WTPart mpart) throws WTException {
		WTPart part = null;
		if (mpart != null) {
			String mVersion = mpart.getVersionInfo().getIdentifier().getValue();
			QueryResult qr = VersionControlHelper.service.allVersionsOf(mpart.getMaster());
			while (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
				String dVersion = part.getVersionInfo().getIdentifier().getValue();
				if("Manufacturing".equals(part.getViewName()) && mVersion.equals(dVersion)){
					return part;
				}
			}
		}
		return null;
	}

	public static WTPart getLatestManufacturingPartByMaster(WTPartMaster partMaster)
			throws WTException {
		WTPart part = null,designPart = null;
		if (partMaster != null) {
			QueryResult qr = VersionControlHelper.service.allVersionsOf(partMaster);
			while (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
				if(LoadConfig.getInstance().getPbomView().equals(part.getViewName())){
					designPart = part;
					break;
				}
			}
		}

		return designPart;
	}

	public static WTPart getPartByOid(long oid) throws WTException {
		QuerySpec qs = new QuerySpec(WTPart.class);
		int[] index = { 0 };
		SearchCondition sc = new SearchCondition(WTPart.class,
				"thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL,
				oid);
		qs.appendWhere(sc, index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			WTPart part = (WTPart) qr.nextElement();
			return part;
		}
		return null;
	}
}
