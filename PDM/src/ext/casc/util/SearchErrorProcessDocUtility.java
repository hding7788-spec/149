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
import wt.session.SessionHelper;
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
 *
 */
public class SearchErrorProcessDocUtility  implements RemoteAccess, Serializable {
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
		try {
			process();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void process() throws WTException, IOException, PropertyVetoException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process";
			Class[] types = { };
			Object[] vals = {};

			SessionHelper.manager.setPrincipal("administrator");
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			rms.setUserName("wcadmin");
			rms.setPassword("wcadmin");
			try {
				 rms.invoke(method,
						SearchErrorProcessDocUtility.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		WTProperties pro = WTProperties.getLocalProperties();
		String wt_temp = pro.getProperty("wt.temp");
		String zip_temp_dir = wt_temp;
		String log = "";
		ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
		for (TypeIdentifier ti : list) {
			String type = SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
			System.out.println(type+" "+ti.toString());
			List<WTDocument> docs = DocUtil.getAllGongyiWenJian(ti.toString().substring(7));

			for(WTDocument doc :docs){
				if (doc != null) {
					System.out.println("update "+doc.getNumber()+" "+doc.getName());
					 ApplicationData data = (ApplicationData) ContentHelper.service.getPrimary(doc);
					if(data == null) {
						continue;
					}

					String zipFilePath = zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()+File.separator+doc.getNumber();
					File techDir = new File(zipFilePath);
					if(!techDir.exists()) {
						techDir.mkdirs();
					}

					String xmlFile = zipFilePath+File.separator+doc.getNumber()+".xml";
					InputStream inputStream = ContentServerHelper.service.findContentStream(data);
					byte[] bytes = fileToBytes(inputStream);
					ZipUtil.unZip(bytes, zipFilePath);

					File file = new File(xmlFile);
					if(!file.exists()||file.length()==0) {
						System.out.println(doc.getNumber()+" xmlFile is not exist!");
						log = log +doc.getNumber()+"\r\n";
					}
					//删除临时文件
					CldeUtil.deleteFiles(new File(zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()));
				}
			}

		}
		genLog(log,wt_temp);

	}

	public static void process2() throws WTException, IOException, PropertyVetoException, DocumentException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process2";
			Class[] types = { };
			Object[] vals = {};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				 rms.invoke(method,
						SearchErrorProcessDocUtility.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		WTProperties pro = WTProperties.getLocalProperties();
		String wt_temp = pro.getProperty("wt.temp");
		String zip_temp_dir = wt_temp;
		String log = "";
		ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
		for (TypeIdentifier ti : list) {
			String type = SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
			System.out.println(type+" "+ti.toString());
			List<WTDocument> docs = DocUtil.getAllGongyiWenJian(ti.toString().substring(7));

			for(WTDocument doc :docs){
				if (doc != null) {
					System.out.println("update "+doc.getNumber()+" "+doc.getName());
					 ApplicationData data = (ApplicationData) ContentHelper.service.getPrimary(doc);
					if(data == null) {
						continue;
					}

					String zipFilePath = zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()+File.separator+doc.getNumber();
					File techDir = new File(zipFilePath);
					if(!techDir.exists()) {
						techDir.mkdirs();
					}

					String xmlFile = zipFilePath+File.separator+doc.getNumber()+".xml";
					InputStream inputStream = ContentServerHelper.service.findContentStream(data);
					byte[] bytes = fileToBytes(inputStream);
					ZipUtil.unZip(bytes, zipFilePath);

					File file = new File(xmlFile);
					if(file.exists()&&file.length()>0) {
						SAXReader reader = new SAXReader();
				        Document document = reader.read(file);
				        Element rootElement = document.getRootElement();

				        //工艺文件信息
				        List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
				        Element element = null;
				        for (Object object : pplanList) {
							element = (Element)object;
							String partNumber = element.attributeValue("partNumber");
							QueryResult qr = ProcessPlanHelper.searchAllIteratedByNumberVersionView(WTPart.class,partNumber,"space","Manufacturing");
							if(qr.hasMoreElements()){
								WTPart part = (WTPart)qr.nextElement();
								if(getLinkByPartAndDoc(part, doc)==null){
									log = log +part.getNumber()+"    "+doc.getNumber()+"\r\n";
									WTPartUtil.createWTPartDescribeLink(part, doc);
								}

							}
				        }
					}
					//删除临时文件
					CldeUtil.deleteFiles(new File(zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()));
				}
			}

		}
		genLog(log,wt_temp);

	}
	public static void process3() throws WTException, IOException, PropertyVetoException, DocumentException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process3";
			Class[] types = { };
			Object[] vals = {};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				 rms.invoke(method,
						SearchErrorProcessDocUtility.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		WTProperties pro = WTProperties.getLocalProperties();
		String wt_temp = pro.getProperty("wt.temp");
		String zip_temp_dir = wt_temp;
		ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
		for (TypeIdentifier ti : list) {
			String type = SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
			System.out.println(type+" "+ti.toString());
			List<WTDocument> docs = DocUtil.getAllGongyiWenJian(ti.toString().substring(7));

			for(WTDocument doc :docs){
				if (doc != null) {
					System.out.println("update "+doc.getNumber()+" "+doc.getName());
					 ApplicationData data = (ApplicationData) ContentHelper.service.getPrimary(doc);
					if(data == null) {
						continue;
					}

					String zipFilePath = zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()+File.separator+doc.getNumber();
					File techDir = new File(zipFilePath);
					if(!techDir.exists()) {
						techDir.mkdirs();
					}

					String xmlFile = zipFilePath+File.separator+doc.getNumber()+".xml";
					InputStream inputStream = ContentServerHelper.service.findContentStream(data);
					byte[] bytes = fileToBytes(inputStream);
					ZipUtil.unZip(bytes, zipFilePath);

					File file = new File(xmlFile);
					if(file.exists()) {
						SAXReader reader = new SAXReader();
	        	        Document dom = reader.read(xmlFile);
	        	        Element rootElement = dom.getRootElement();

	        	        boolean flag = false;
	        	        //工艺文件信息
	        	        List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
	        	        for (Object object : pplanList) {
	        				Element element = (Element)object;
	        				Map map =new HashMap();
	        				String CINDEX = element.attributeValue("CINDEX");
							String MINDEX = element.attributeValue("MINDEX");
							String PINDEX = element.attributeValue("PINDEX");
							String PPLANTYPE = element.attributeValue("PPLANTYPE");
							String ZFFLAG = element.attributeValue("ZFFLAG");
							String DEPT = element.attributeValue("DEPT");
							String PHASE_CODE = element.attributeValue("PHASE_CODE");
							String KEYCOMPONENT = element.attributeValue("KEYCOMPONENT");
							if(CINDEX!=null &&!"".equals(CINDEX))
								map.put("CINDEX", CINDEX);
							if(MINDEX!=null &&!"".equals(MINDEX))
								map.put("MINDEX", MINDEX);
							if(PINDEX!=null &&!"".equals(PINDEX))
								map.put("PINDEX", PINDEX);
							if(PPLANTYPE!=null &&!"".equals(PPLANTYPE))
								map.put("PPLANTYPE", PPLANTYPE);
							if(ZFFLAG!=null &&!"".equals(ZFFLAG))
								map.put("ZFFLAG", ZFFLAG);
							if(DEPT!=null &&!"".equals(DEPT))
								map.put("DEPT", DEPT);
							if(PHASE_CODE!=null &&!"".equals(PHASE_CODE))
								map.put("PHASE_CODE", PHASE_CODE);
							if(KEYCOMPONENT!=null &&!"".equals(KEYCOMPONENT))
								map.put("KEYCOMPONENT", KEYCOMPONENT);

							IBAHelper attrHelper = new IBAHelper(doc);
							attrHelper.setIBAValue(doc, map);
	        	        }
					}
					//删除临时文件
					CldeUtil.deleteFiles(new File(zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()));
				}
			}

		}

	}

	public static void process4(String number,String phaseCode) throws WTException, IOException, PropertyVetoException, DocumentException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process4";
			Class[] types = { };
			Object[] vals = {};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				 rms.invoke(method,
						SearchErrorProcessDocUtility.class.getName(), null, types, vals);
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
		getAllChildPart(p,parts1,"Design");
		parts1.add(p);
		for(WTPart part :parts1){
			IBAUtility ibaUtility = new IBAUtility(part);
			ibaUtility.setIBAValue("PHASE_CODE",phaseCode);
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
		getAllChildPart(p2,parts2,"Manufacturing");
		parts2.add(p2);
		for(WTPart part :parts2){
			IBAUtility ibaUtility = new IBAUtility(part);
			ibaUtility.setIBAValue("PHASE_CODE",phaseCode);
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
