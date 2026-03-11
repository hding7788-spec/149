package com.glaway.mpm.pbombuilder.util;

import java.util.Vector;

import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

import com.glaway.mpm.util.Constant;
import com.ptc.core.meta.common.TypeIdentifier;

public class CmSearchHelper implements RemoteAccess {
	private static final int[] ZERO = { 0 };

	private static final String SERVER_CLASS = "com.glaway.mpm.pbombuilder.util.CmSearchHelper";

	public static WTContainer getContainer(String name) {
		WTContainer ret = null;

		try {
			QuerySpec qs = new QuerySpec(WTContainer.class);
			qs.appendWhere(new SearchCondition(WTContainer.class, WTContainer.NAME, SearchCondition.EQUAL, name), ZERO);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements())
				ret = (WTContainer) qr.nextElement();
		} catch (WTException qe) {
			// log.error(qe);
		}
		return ret;
	}

	public static EPMDocument getEPMDocumentByPart(WTPart part) throws WTException {
		EPMDocument epmDocument = null;
		if (part != null) {
			QueryResult qr = WTPartHelper.service.getDescribedByDocuments(part, true);
			while (qr.hasMoreElements()) {
				Object ob = qr.nextElement();
				if (ob instanceof EPMDocument) {
					epmDocument = (EPMDocument) ob;
				}
			}
		}
		return epmDocument;
	}

	public static Vector searchPart(TypeIdentifier ti, int queryLimit, String number, String name,
			String modifierFullName, String modifyTimeFrom, String modifyTimeTo) throws Exception {
		String method = "searchPart";
		Class[] argTypes = { TypeIdentifier.class, Integer.TYPE, String.class, String.class, String.class,
				String.class, String.class };
		Object[] argValues = { ti, new Integer(queryLimit), number, name, modifierFullName, modifyTimeFrom,
				modifyTimeTo };

		return (Vector) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}

	public static Persistable searchRMI(Class klass, long idA2A2) {
		Persistable ret = null;
		try {
			QuerySpec qs = new QuerySpec(klass);
			qs.appendWhere(new SearchCondition(klass, WTAttributeNameIfc.ID_NAME, SearchCondition.EQUAL, idA2A2), ZERO);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements())
				ret = (Persistable) qr.nextElement();
		} catch (WTException qe) {
//			log.error(qe);
			qe.printStackTrace();
		}

		return ret;
	}
	public static Persistable search(Class klass, long idA2A2) throws Exception {
		String method = "searchRMI";
		Class[] argTypes = { Class.class,long.class};
		Object[] argValues = { klass,idA2A2 };
		return (Persistable) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}
	public static Persistable searchRMI(Class klass, String num) {
		boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", num), new int[1]);

            qs.appendAnd();
            View view = ViewHelper.service.getView("Manufacturing");
            qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id",
                            "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if(qr.hasMoreElements()){
            	return (Persistable)qr.nextElement();
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

		return null;
	}

	public static Persistable search(Class klass, String num) throws Exception {
		String method = "searchRMI";
		Class[] argTypes = { Class.class,String.class};
		Object[] argValues = { klass,num };
		return (Persistable) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, argTypes, argValues);
	}

}
