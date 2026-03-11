package com.glaway.mpm.print.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.glaway.mpm.util.TypeUtil;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.container._WTContainer;
import wt.method.MethodContext;
import wt.method.MethodServer;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

public class FolderUtil implements RemoteAccess{
	public static WTContainer getContainerByName(String name) throws WTException { // add by lkc 2018.1.25
		WTContainer container = null;
		QuerySpec querySpec = new QuerySpec(WTContainer.class);
		querySpec.appendWhere(new SearchCondition(WTContainer.class, _WTContainer.NAME, SearchCondition.EQUAL, name));
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		if (queryResult.hasMoreElements()) {
			container = (WTContainer) queryResult.nextElement();
		}
		return container;
	}
	public static List<WTDocument> getFolder(String folderPath, WTContainerRef containerRef) throws WTException {
		SessionServerHelper.manager.setAccessEnforced(false);
		List<WTDocument> list = new ArrayList<WTDocument>();
		Folder folder = null;
		if(folderPath==null){
			folderPath="Default/";
		}
		try {
			folder = FolderHelper.service.getFolder(folderPath, containerRef);
			QueryResult qr = FolderHelper.service.findFolderContents(folder);
			while (qr.hasMoreElements()) {
				Object object = qr.nextElement();
				if (object instanceof WTDocument) {
					WTDocument doc = (WTDocument) object;
					list.add(doc);
				}
			}

		} catch (WTException e) {
			folder = FolderHelper.service.saveFolderPath(folderPath, containerRef);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return list;
	}
	public static Boolean CompareTime(String currentTime, String tempTime) throws ParseException{
		Boolean flag = false; //当前时间比模板时间小
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date date1 = sdf.parse(currentTime);
		Date date2 = sdf.parse(tempTime);
		if(date1.before(date2)){
			flag = false;
		}else{
			flag = true;
		}
		return flag;
 	}

//	public static void main(String[] args) {
//		RemoteMethodServer methodserver = RemoteMethodServer.getDefault();
//		try {
//			methodserver.invoke("execute", "com.glaway.mpm.print.util.FolderUtil", null, new Class[]{}, new Object[]{});
//		} catch (RemoteException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (InvocationTargetException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	}
	public static List<String> execute(){
		List<String> listStr = new ArrayList<String>();
		try {
			WTContainer container = getContainerByName("打印分发管理库");
			String type = "casc.sast.149.PRINTRECOVERFORM";
			List<WTDocument> list =getDocumentByType(type, container.getPersistInfo().getObjectIdentifier().getId());
			for (WTDocument wtDocument : list) {
				String number = wtDocument.getNumber();
				listStr.add(number);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listStr;
	}

	public static List<WTDocument> getDocumentByType(String type, long con) throws WTException, RemoteException {
		List<WTDocument> list = new ArrayList<WTDocument>();
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL, con), new int[]{0});
		qs.appendAnd();
		TypeUtil.getTypeQuery(WTDocument.class, type, qs);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			list.add(document);
		}
		return list;
	}

}
