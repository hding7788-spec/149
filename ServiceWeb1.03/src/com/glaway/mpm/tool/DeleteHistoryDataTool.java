package com.glaway.mpm.tool;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPartDescribeLink;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

public class DeleteHistoryDataTool implements RemoteAccess {
	private static int index[] = { 0 };

	public static void deleteAllIteratorFromOneVersion(String documentOid) throws PersistenceException, WTException {
		WTDocument document = (WTDocument) com.glaway.mpm.util.Util.getObjectByOid(WTDocument.class, documentOid);
		QueryResult queryResult = VersionControlHelper.service.iterationsOf(document);
		while (queryResult.hasMoreElements()) {
			WTDocument doc = (WTDocument) queryResult.nextElement();
			System.out.println(document.getName() + "  " + document.getIterationDisplayIdentifier());
			QuerySpec qs = new QuerySpec(WTPartDescribeLink.class);
			qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id",
					SearchCondition.EQUAL, com.glaway.mpm.util.Util.getLongOid(doc)), index);
			QueryResult result = PersistenceHelper.manager.find((StatementSpec) qs);
			while (result.hasMoreElements()) {
				WTPartDescribeLink link = (WTPartDescribeLink) result.nextElement();
				System.out.println(link);
				PersistenceServerHelper.manager.remove(link);
			}
		}
	}

	public static void deleteAllIterator(String documentOid) throws WTException {
		WTDocument document = (WTDocument) com.glaway.mpm.util.Util.getObjectByOid(WTDocument.class, documentOid);
		QueryResult queryResult = VersionControlHelper.service.allVersionsOf(document);
		while (queryResult.hasMoreElements()) {
			WTDocument doc = (WTDocument) queryResult.nextElement();
			System.out.println(document.getName() + "  " + document.getIterationDisplayIdentifier());
			QuerySpec qs = new QuerySpec(WTPartDescribeLink.class);
			qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id",
					SearchCondition.EQUAL, com.glaway.mpm.util.Util.getLongOid(doc)), index);
			QueryResult result = PersistenceHelper.manager.find((StatementSpec) qs);
			while (result.hasMoreElements()) {
				WTPartDescribeLink link = (WTPartDescribeLink) result.nextElement();
				System.out.println(link);
				PersistenceServerHelper.manager.remove(link);
			}
		}
	}

	public static void deleteAllDocumentByType(String documentType) {
		List<WTDocument> list = new ArrayList<WTDocument>();
		try {
			if (true) {
				QuerySpec qs = new QuerySpec(WTDocument.class);
				TypeUtil.getTypeQuery(WTDocument.class, documentType, qs);
				QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
				while (qr.hasMoreElements()) {
					WTDocument document = (WTDocument) qr.nextElement();
					list.add(document);
				}
			}
			for (WTDocument document : list) {
				System.out.println(document.getName() + "  " + document.getIterationDisplayIdentifier());
				QuerySpec qs = new QuerySpec(WTPartDescribeLink.class);
				qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id",
						SearchCondition.EQUAL, com.glaway.mpm.util.Util.getLongOid(document)), index);
				QueryResult result = PersistenceHelper.manager.find((StatementSpec) qs);
				while (result.hasMoreElements()) {
					WTPartDescribeLink link = (WTPartDescribeLink) result.nextElement();
					System.out.println(link);
					PersistenceServerHelper.manager.remove(link);
				}
			}
			list = WTDocumentUtil.getDocumentByType(documentType);
			for (WTDocument document : list) {
				System.out.println(document.getName() + "  " + document.getIterationDisplayIdentifier());
				PersistenceHelper.manager.delete(document);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static void deleteAllProcessPlan() {
		try {
			QuerySpec querySpec = new QuerySpec(MPMProcessPlan.class);
			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				MPMProcessPlan processPlan = (MPMProcessPlan) queryResult.nextElement();
				System.out.println(processPlan.getName() + "  " + processPlan.getIterationDisplayIdentifier());
				PersistenceHelper.manager.delete(processPlan);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws RemoteException, WTException {
		if (args.length == 0) {
			System.out.println("...DeleteHistoryDataTool  usage... ");
			System.out.println("1. deleteAllIteratorFromOneVersion(String documentOid) ");
			System.out.println("2. deleteAllIterator(String documentOid) ");
			System.out.println("3. deleteAllDocumentByType(String documentType) ");
			System.out.println("4. deleteAllProcessPlan() ");

		}
		if (args.length >= 1) {
			String methodName = args[0];
			Object parameters[] = new Object[args.length - 1];
			Class types[] = new Class[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				parameters[i - 1] = args[i];
				types[i - 1] = String.class;
			}
			RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
			try {
				methodServer.invoke(methodName, DeleteHistoryDataTool.class.getName(), null, types, parameters);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

	}
}
