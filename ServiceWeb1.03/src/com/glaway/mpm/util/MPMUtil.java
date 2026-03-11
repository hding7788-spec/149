package com.glaway.mpm.util;

import java.util.Vector;

import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.org.WTGroup;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.part._WTPart;
import wt.pds.StatementSpec;
import wt.query.ConstantExpression;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.session.SessionServerHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;

import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMSkill;

public class MPMUtil implements RemoteAccess{

	private static int index[] = { 0 };

	public static MPMPlant getPlantByName(String name) throws WTException {
		MPMPlant plant = null;

		QuerySpec querySpec = new QuerySpec(MPMPlant.class);
		querySpec.appendWhere(new SearchCondition(MPMPlant.class, _WTPart.NAME, SearchCondition.EQUAL, name),
				new int[] { 0 });
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			plant = (MPMPlant) queryResult.nextElement();
		}
		return plant;
	}

	public static WTPartUsageLink createWTPartUsageLink(WTPart parentPart, WTPartMaster childPartMaster)
			throws WTException {
		WTPartUsageLink partUsageLink = WTPartUsageLink.newWTPartUsageLink(parentPart, childPartMaster);
		PersistenceServerHelper.manager.insert(partUsageLink);
		return partUsageLink;

	}

	public static Object getObjectByOid(Class objectClass, String oid) throws WTException {
		Object object = null;
		long objectId = 0;
		if (null == objectClass || null == oid || "".equals(oid.trim())) {
			return object;
		}
		try {
			objectId = Long.valueOf(oid);
		} catch (Exception e) {
			if (oid.contains(":")) {
				String[] str = oid.split("\\:");
				objectId = Long.valueOf(str[str.length - 1]);
			}
		}
		QuerySpec qs = new QuerySpec(objectClass);
		qs.appendWhere(new SearchCondition(objectClass, WTAttributeNameIfc.ID_NAME, SearchCondition.EQUAL, objectId),
				index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements())
			object = qr.nextElement();
		return object;
	}

	public static Vector<String> getAllMPMPlant() {
    	Vector<String> vector = new Vector<String>();
		/*try {
			WTContainer container = WTContainerUtil.getContainerByName(MPMResourceConstants.RESOURCE_LIBRARY_NAME);
			Folder folder = FolderUtil.getFolder(MPMResourceConstants.FORDER_BM, WTContainerRef.newWTContainerRef(container));
			QueryResult qr = FolderHelper.service.findFolderContents(folder, MPMPlant.class);
			while (qr.hasMoreElements()) {
				MPMPlant plant = (MPMPlant) qr.nextElement();
				vector.add(plant.getName());
			}
		} catch (WTException e) {
			e.printStackTrace();
		}*/
    	return vector;
    }

	public static Vector<String> getAllMPMSkill() {
    	Vector<String> vector = new Vector<String>();
    	try {
			QueryResult qr = getAllTechnicsTypeMPMSkill();
			while (qr.hasMoreElements()) {
				MPMSkill skill = (MPMSkill) qr.nextElement();
				vector.add(skill.getName());
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
    	return vector;
    }

	public static QueryResult getAllTechnicsTypeMPMSkill() throws WTException {
		WTContainer container = WTContainerUtil.getContainerByName("");
		Folder folder = FolderUtil.getFolder("", WTContainerRef.newWTContainerRef(container));
		QueryResult qr = FolderHelper.service.findFolderContents(folder, MPMSkill.class);
		return qr;
	}

	/**
     * 搜索组织结构中组
     *
     * @param groupName
     * @return
     * @throws WTException
     */
    public static QueryResult queryGroup() throws WTException {
        try {
            QuerySpec queryspec = new QuerySpec(WTGroup.class);
            String A = queryspec.getFromClause().getAliasAt(0);
            TableColumn INTERNAL = new TableColumn(A, "INTERNAL");
            TableColumn CLASSNAMEKEYCONTAINERREFEREN = new TableColumn(A,
                    "CLASSNAMEKEYCONTAINERREFEREN");
            queryspec.appendWhere(new SearchCondition(INTERNAL, "=",
                    new ConstantExpression(0)), new int[] { 0 });
            queryspec.appendAnd();
            queryspec.appendWhere(new SearchCondition(
                    CLASSNAMEKEYCONTAINERREFEREN, "=", new ConstantExpression(
                            (Object) "wt.inf.container.OrgContainer")),
                    new int[] { 0 });
            QueryResult qr = PersistenceServerHelper.manager.query(queryspec);

            return qr;
        } catch (QueryException e) {
            e.printStackTrace();
        }
        return new QueryResult();
    }
	public static QueryResult getAllTechnicsTypeMPMSkillByPrint() throws WTException {
		boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
		QueryResult qr = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
			Folder folder = FolderUtil.getFolder("/Default/工艺类型", WTContainerRef.newWTContainerRef(container));
			qr = FolderHelper.service.findFolderContents(folder, MPMSkill.class);
		} catch (WTException e) {
			throw e;
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforced);
		}
		return qr;
	}
}
