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
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;

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
public class Test1  implements RemoteAccess, Serializable {

	public static String process(String key,String value) throws WTException, IOException, PropertyVetoException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process";
			Class[] types = {String.class ,String.class};
			Object[] vals = {key,value};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				return (String) rms.invoke(method,
						Test1.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 	String result = "";
		 	QuerySpec qSpec = new QuerySpec(WfProcess.class);
	        int[] index = { 0 };
	        SearchCondition sCondition = new SearchCondition(WfProcess.class, WfProcess.NAME,
	                SearchCondition.LIKE, "149_%");
	        qSpec.appendWhere(sCondition, index);
	        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
	        int i=0;
	        while (qResult.hasMoreElements()) {
	        	WfProcess wf = (WfProcess) qResult.nextElement();
	        	System.out.println("查到流程："+(i++));
	        	ProcessData processData = wf.getContext();
	        	  String orderIID = (String) processData.getValue(key);
	              if(value.equals(orderIID)){
	            	  result =result+wf.getPersistInfo().getObjectIdentifier().getId()+",";
	              }
	        }
	        System.out.println(result);
	        return result ;
		}


}
