package com.glaway.mpm.pbom.helper;

import java.util.List;
import java.util.Map;

import wt.org.WTPrincipal;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.queue.WtQueue;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.glaway.mpm.intf.PBOMEditorToWCIntfRMI;

public class SynchPbomXmlQueueHelper {

	public static boolean createProcessingQueue(List<Map<String, Object>> byteslist, Map<String, String> gysls) throws WTException{
		if(byteslist==null) return false;
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			ProcessingQueue queue = getFreeQueue("SynchPbomXmlQueue");
			//String queueName = queue.getName();
			String targetClass = PBOMEditorToWCIntfRMI.class.getName();
			if(byteslist.isEmpty()) return false;

			String targetMethod = "synchPbomXml";
			Class[] argClass = new Class[]{List.class,Map.class};
			Object[] argObj = new Object[]{byteslist,gysls};
			WTPrincipal user = SessionHelper.manager.getPrincipal();
			queue.addEntry(user, targetMethod, targetClass, argClass, argObj);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return true;
	}

	private static ProcessingQueue getFreeQueue(String queueName) throws WTException {
		WtQueue queue = QueueHelper.manager.getQueue(queueName);
		if(queue==null){
			Manager manager = ManagerServiceFactory.getDefault().getManager(wt.queue.StandardQueueService.class);
			queue = ((wt.queue.StandardQueueService)manager).createQueue(queueName,true);
		}
		return (ProcessingQueue)queue;
	}
}
