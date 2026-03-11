package com.glaway.mpm.visual.server;

import java.io.Serializable;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaSearchHelper;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;

public class VaSearchSvrHelper implements RemoteAccess, Serializable{
	private static final VaLogger	log		= VaLogger.getLogger(VaSearchSvrHelper.class);
	private static final int[]		ZERO	= { 0 };

	public static WTPart getPartByNumberAndViewRMI(String number, long viewId) {
		WTPart part = null;
		try {
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number), ZERO);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewId), ZERO);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			qr = new LatestConfigSpec().process(qr);
			if (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
			}
		} catch (WTException qe) {
			log.error(qe);
		}
		return part;
	}

	public static View getViewByNameRMI(String viewName) {
		View view = null;
		try {
			QuerySpec qs = new QuerySpec(View.class);
			qs.appendWhere(new SearchCondition(View.class, View.NAME, SearchCondition.EQUAL, viewName, false), ZERO);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements()) {
				view = (View) qr.nextElement();
			}
		} catch (WTException qe) {
			log.error(qe);
		}
		return view;
	}

	public static WTContainer getContainerRMI(String name) {
		WTContainer ret = null;

		try {
			QuerySpec qs = new QuerySpec(WTContainer.class);
			qs.appendWhere(new SearchCondition(WTContainer.class, WTContainer.NAME, SearchCondition.EQUAL, name), ZERO);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			if (qr.hasMoreElements())
				ret = (WTContainer) qr.nextElement();
		} catch (WTException qe) {
			log.error(qe);
		}

		return ret;
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
			log.error(qe);
		}

		return ret;
	}
}
