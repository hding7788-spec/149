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

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.meta.common.TypeIdentifier;

import ext.casc.integrate.util.CldeUtil;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.part.CSCPart;

/**
 * update glcilink t set t.cipartnumber = 'ML'||t.cipartnumber
update glcipartlink t set t.cipartnumber = 'ML'||t.cipartnumber
 * @author Administrator
 *windchill ext.casc.util.ChangeIBAValueUtility wcadmin wcadmin S1000100001 PHASE_CODE S 1
 */
public class ChangeIBAValueUtility   {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = null;
		String passwd = null;
		String number = null;
		String key = null;
		String value = null;
		String isAll = null;
		if (args.length >= 2) {
			username = args[0];
			passwd = args[1];
			number = args[2];
			key = args[3];
			value = args[4];
			isAll = args[5];
			if (username == null)
				username = "wcadmin";

			if (passwd == null)
				passwd = "wcadmin";
		}
		System.out.println("------user:"+username+"    password:"+passwd);
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		try {
			process(number,key,value,isAll);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void process(String number,String key,String phaseCode,String isAll) throws WTException, IOException, PropertyVetoException, DocumentException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process";
			Class[] types = {String.class,String.class,String.class,String.class };
			Object[] vals = { number, key, phaseCode, isAll};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				 rms.invoke(method,
						ChangeIBAValueUtility.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//WTPart p = (WTPart)searchLatestIteratedByNumberVersionView(WTPart.class,number,"Design");
		WTPart p = CSCPart.getPartByNumberAndViewName(number,"Design");

		List<WTPart> parts1 = new ArrayList<WTPart>();
		if("1".equals(isAll)){
			getAllChildPart(p,parts1,"Design");
		}
		parts1.add(p);
		for(WTPart part :parts1){
			IBAUtility ibaUtility = new IBAUtility(part);
			ibaUtility.setIBAValue(key,phaseCode);//PHASE_CODE
			try {
				part =(WTPart)ibaUtility.updateAttributeContainer(part);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ibaUtility.updateIBAHolder(part);
		}

		WTPart p2 = CSCPart.getPartByNumberAndViewName(number,"Manufacturing");
		List<WTPart> parts2 = new ArrayList<WTPart>();
		if("1".equals(isAll)){
			getAllChildPart(p2,parts2,"Manufacturing");
		}
		parts2.add(p2);
		for(WTPart part :parts2){
			IBAUtility ibaUtility = new IBAUtility(part);
			ibaUtility.setIBAValue(key,phaseCode);
			try {
				part =(WTPart)ibaUtility.updateAttributeContainer(part);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ibaUtility.updateIBAHolder(part);
		}
	}
	private static ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }
	public static void getAllChildPart(WTPart ppart,List<WTPart> list,String view) throws WTException {
		QueryResult qr = WTPartHelper.service.getUsesWTParts(ppart, getDefaultConfigSpec());
		WTPart cpart = null;
		while(qr.hasMoreElements()) {
			Persistable[] per = (Persistable[])qr.nextElement();
			Persistable pper = per[1];
			if(pper instanceof WTPart) {
				cpart = (WTPart)per[1];
				cpart = CSCPart.getPartByNumberAndViewName(cpart.getNumber(),view);
			} else if (pper instanceof WTPartMaster) {
				cpart = CSCPart.getPartByNumberAndViewName(((WTPartMaster)pper).getNumber(),view);
			}

			if(cpart != null) {
				if(!list.contains(cpart)){
					list.add(cpart);
				}
				getAllChildPart(cpart,list,view);
			}
		}
	}
	public static Iterated searchLatestIteratedByNumberVersionView(Class klass, String number, String viewname) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(klass);
            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number), new int[1]);
            if ((viewname != null) && (!viewname.equals(""))) {
                qs.appendAnd();
                View view = ViewHelper.service.getView(viewname);
                qs.appendWhere(
                        new SearchCondition(WTPart.class, "view.key.id",
                                "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            }

            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            qs.setAdvancedQueryEnabled(true);

            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                Iterated localIterated = VersionControlHelper.getLatestIteration((Iterated) qr.nextElement(), true);
                return localIterated;
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);

        return null;
    }
	public static WTPartDescribeLink getLinkByPartAndDoc(WTPart part, WTDocument doc) throws WTException {
		int index[] = { 0 };
		QuerySpec qs = new QuerySpec(WTPartDescribeLink.class);
		qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, part.getPersistInfo().getObjectIdentifier().getId()), index);
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, doc.getPersistInfo().getObjectIdentifier().getId()), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			return (WTPartDescribeLink) qr.nextElement();
		}
		return null;
	}
	public static byte[] fileToBytes(InputStream inputStream) {
		ByteArrayOutputStream baos = null;
		try {
			byte[] bytes = new byte[1024];
			int length;
			baos = new ByteArrayOutputStream();
			while ((length = inputStream.read(bytes)) != -1) {
				baos.write(bytes, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != baos) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baos.toByteArray();
	}

	public static void genLog(String log,String tempPath) {
		try {
			String logPath = tempPath + File.separator + "materialLogs";
			File file = new File(logPath);
			if (!file.exists()) {
				file.mkdir();
			}
			String logFile = logPath + File.separator + "xmlFile.log";
			file = new File(logFile);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(log.getBytes("GBK"));
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
