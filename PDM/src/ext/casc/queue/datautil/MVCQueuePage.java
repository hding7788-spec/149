package ext.casc.queue.datautil;

import java.util.ArrayList;
import java.util.Iterator;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.method.MethodContext;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.queue.QueueEntry;
import wt.queue.QueuePseudoMethodArgs;
import wt.queue.ScheduleQueue;
import wt.queue.WtQueue;
import wt.queue.WtQueueEntry;
import wt.util.WTException;
import wt.vc.Iterated;

import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.util.DBConn;

public class MVCQueuePage {
	public static void resetEntryState(NmCommandBean paramNmCommandBean)
			throws WTException {
		try {
			ArrayList localArrayList = paramNmCommandBean.getSelected();
			Object localObject;
			if (localArrayList.size() != 0) {
				localObject = localArrayList.iterator();
				while (((Iterator) localObject).hasNext()) {
					NmContext localNmContext = (NmContext) ((Iterator) localObject)
							.next();
					ObjectIdentifier localObjectIdentifier = localNmContext
							.getTargetOid().getOidObject();
					if (localObjectIdentifier != null) {
						resetEntry(localObjectIdentifier);
					}
				}
			} else {
				localObject = paramNmCommandBean.getActionOid().getOidObject();
				if (localObject != null) {
					resetEntry((ObjectIdentifier) localObject);
				}
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			throw new WTException(localException);
		}
	}

	private static void resetEntry(ObjectIdentifier localObjectIdentifier) {
		WtQueue localWtQueue = null;

	    MethodContext localMethodContext = MethodContext.getContext(Thread.currentThread());
	    int i = localMethodContext == null ? 1 : 0;
	    if (i != 0)
	      localMethodContext = new MethodContext(new QueuePseudoMethodArgs(MVCQueuePage.class.getName(), "resetEntries"));
	    Transaction localTransaction = new Transaction();
	    try {
	      localTransaction.start();
	      WtQueueEntry localWtQueueEntry = (WtQueueEntry)PersistenceServerHelper.manager.restore(localObjectIdentifier);
	      ObjectReference localObjectReference = localWtQueueEntry.getQueueRef();
	      localWtQueue = (WtQueue)localObjectReference.getObject();
	      if ((localWtQueue instanceof ScheduleQueue)){

	      }else{
	    	  System.out.println(((QueueEntry)localWtQueueEntry).getNumber());
	    	  QueueEntry qe = (QueueEntry)localWtQueueEntry;
	    	  setQueueEntryState(qe,"SEVERE");
	    	  // SEVERE
	      }
	       localTransaction.commit();
	      localTransaction = null;
	    }
	    catch (Exception localException) {
	      localException.printStackTrace();
	    }
	    finally {
	      if (i != 0)
	        localMethodContext.unregister();
	    }
	}

	public static void setQueueEntryState(QueueEntry qe,String state)
			throws  Exception{
		DBConn conn = null;
		try {
			conn = new DBConn();
			String sql = "update queueentry set codec5='"+state+"' where entrynumber="+qe.getNumber();
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
