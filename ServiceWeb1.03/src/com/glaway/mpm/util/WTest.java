package com.glaway.mpm.util;

import java.io.IOException;
import java.util.Locale;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

public class WTest implements RemoteAccess{

	/**
	 * @author qianlong
	 * @date  2013-7-24
	 * @param args
	 * @throws IOException
	 * @throws WTException
	 *
	 */
	public static void main(String[] args) throws WTException, IOException {
		RemoteMethodServer ms=RemoteMethodServer.getDefault();
		ms.setUserName("zlb06");
		ms.setPassword("1");
		WTUser user=(WTUser)SessionHelper.getPrincipal();
		String wfList[]=new String[]{"工艺审核流程"};
		String StateList[]=new String[]{"COMPLETED"};
		QuerySpec qs=WorkflowUtil.getListWorkItem(user, wfList,StateList);
		QueryResult qr=PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()){
			Object[] obj=(Object[])qr.nextElement();
			WorkItem  wi=(WorkItem)obj[0];
			System.out.println("wi="+wi.getStatus().getLocalizedMessage(Locale.ENGLISH));
			System.out.println("wi="+wi.getParentWA());
			System.out.println("wi="+wi.getOwnership().getOwner().getName());
//			System.out.println("wi="+wi.get);

		}


	}

}
