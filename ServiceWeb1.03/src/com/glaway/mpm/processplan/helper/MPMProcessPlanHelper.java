package com.glaway.mpm.processplan.helper;

import java.util.ArrayList;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

import com.ptc.windchill.mpml.processplan.MPMPartToProcessPlanLink;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

public class MPMProcessPlanHelper {
	private static int index[] = { 0 };

	public static List<WTPart> getWTParts(MPMProcessPlan pplan) {
		List<WTPart> parts = new ArrayList<WTPart>();
		try {

			QuerySpec qs = new QuerySpec(MPMPartToProcessPlanLink.class);
			qs.appendWhere(new SearchCondition(MPMPartToProcessPlanLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, pplan.getPersistInfo().getObjectIdentifier().getId()), index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements()) {
				MPMPartToProcessPlanLink link = (MPMPartToProcessPlanLink) qr.nextElement();
				parts.add((WTPart) link.getRoleAObject());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return parts;
	}

	public static boolean isLinked(MPMProcessPlan pplan,WTPart part) {
		try {
			QuerySpec qs = new QuerySpec(MPMPartToProcessPlanLink.class);
			qs.appendWhere(new SearchCondition(MPMPartToProcessPlanLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, pplan.getPersistInfo().getObjectIdentifier().getId()), index);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(MPMPartToProcessPlanLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, part.getPersistInfo().getObjectIdentifier().getId()), index);

			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements()) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
}
