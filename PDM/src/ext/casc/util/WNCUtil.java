package ext.casc.util;

import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.netmarkets.model.NmOid;

import ext.casc.integrate.util.CldeUtil;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.part.CSCPart;
import ext.casc.workflow.PrintHelper;

/**
 * update glcilink t set t.cipartnumber = 'ML'||t.cipartnumber
update glcipartlink t set t.cipartnumber = 'ML'||t.cipartnumber
 * @author Administrator
 *
 */
public class WNCUtil  implements RemoteAccess, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = null;
		String passwd = null;
		if (args.length >= 2) {
			username = args[0];
			passwd = args[1];
			if (username == null)
				username = "wcadmin";

			if (passwd == null)
				passwd = "wcadmin";
		}
		System.out.println("------user:"+username+"    password:"+passwd);
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
	}
	public static void process(String oid,String key,Map<String,String> value) throws Exception{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process";
			Class[] types = {String.class,String.class ,Map.class};
			Object[] vals = {oid,key,value};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				 rms.invoke(method,
						WNCUtil.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		WfProcess wf = (WfProcess) WCUtil.getPersistable(oid);
		ProcessData processData = wf.getContext();
        ArrayList<NmOid> outSignInfo = new ArrayList<NmOid>();

		NmOid nmOid = new NmOid();
		nmOid.setAdditionalInfo((HashMap) value);
		outSignInfo.add(nmOid);
		processData.setValue(key, outSignInfo);
		PersistenceHelper.manager.save(wf);

		List<WfBlock> allWfBlocks = PrintHelper.getAllBlock(wf);
        // activityList new function
		List activityList = new ArrayList();
        activityList = PrintHelper.getActivities(wf, activityList);
        for (WfBlock wfBlock : allWfBlocks) {
            PrintHelper.getActivities(wfBlock, activityList);
        }
        WfAssignedActivity wfaactivity = PrintHelper.getLatestCompleteActivity(wf, activityList);
        Iterator iterator1 = activityList.iterator();
        do {
            if (!iterator1.hasNext()) {
                break;
            }
            WfAssignedActivity wfactivity = (WfAssignedActivity) iterator1.next();
            if (wfactivity.getName().indexOf("外部会签") > -1) {
            	ProcessData processData2 = wfactivity.getContext();
            	processData2.setValue(key, outSignInfo);
            	PersistenceHelper.manager.save(wfactivity);
            }
        } while (true);

		//PersistenceHelper.manager.refresh(wf);
	}

	public static void process2(String oid,String key,String value) throws WTException, IOException, PropertyVetoException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process2";
			Class[] types = {String.class,String.class ,String.class};
			Object[] vals = {oid,key,value};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				 rms.invoke(method,
						WNCUtil.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		WfProcess wf = (WfProcess) WCUtil.getPersistable(oid);
		ProcessData processData = wf.getContext();
		processData.setValue(key, value);
		PersistenceHelper.manager.save(wf);
		PersistenceHelper.manager.refresh(wf);
	}


}
