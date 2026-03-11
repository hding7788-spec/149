package ext.casc.util;

import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import wt.change2.ChangeException2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.OneOffVersioned;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTPartUtil;
import com.glaway.mpm.util.WorkInProcessUtil;
import com.ptc.core.meta.common.TypeIdentifier;

import ext.casc.doc.CSCDoc;
import ext.casc.integrate.util.CldeUtil;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.part.CSCPart;

/**
 * update glcilink t set t.cipartnumber = 'ML'||t.cipartnumber
update glcipartlink t set t.cipartnumber = 'ML'||t.cipartnumber
 * @author Administrator
 *
 */
public class DeleteProcessDocUtility  implements RemoteAccess {
	/**
	 *
	 */
	public static void main(String[] args) {
		if (!RemoteMethodServer.ServerFlag) {
			try {
				RemoteMethodServer server = RemoteMethodServer.getDefault();
				server.setUserName("wcadmin");
				//				server.setPassword("wcadmin");
				server.setPassword("wcadmin");
				String method = "";
				Class<?>[] types = null;
				Object[] vals = null;
				method = "process";
				types = new Class<?>[] { };
				vals = new Object[] { };
				if (types != null && vals != null) {
					server.invoke(method, DeleteProcessDocUtility.class.getName(), null, types, vals);
				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public static void process() throws RemoteException, InvocationTargetException {
		if (!RemoteMethodServer.ServerFlag) {
			RemoteMethodServer.getDefault().invoke("process",
					DeleteProcessDocUtility.class.getName(),
					null,
					new Class[] {},
					new Object[] {});
		} else {
			boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
	        try {
	        	ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
	    		for (TypeIdentifier ti : list) {
	    			SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
	    			String type = ti.toString().substring(7);
	    	    	//wt.part.WTPart|casc.sast.GLCatalogItemPart
	    	    	TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
	    			long typeId = 0;
	    			if (tdr != null) {
	    				typeId = tdr.getKey().getBranchId();
	    			}
	    			QuerySpec qs = new QuerySpec(WTDocument.class);

	    		    qs.appendWhere(new SearchCondition(WTDocument.class,
	    		    "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
	    		     new int[]{0});
	    		    qs.appendAnd();
	    		    qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
	    		   // qs = new LatestConfigSpec().appendSearchCriteria(qs);
	    		    qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED", true));
	    		    QueryResult qr = PersistenceHelper.manager.find(qs);
	    		    while(qr.hasMoreElements()){
	    		    	WTDocument document = (WTDocument)qr.nextElement();
	    		    	boolean  checkedOut = WorkInProgressHelper.isCheckedOut((Workable) document);
	    		    	if(!checkedOut){

							WTPart part = WCUtil.getRelatedWTPartByDoc(document);
							if(part!=null){
								deleteIterations(document);
							}
	    		    	}
	    	            //PersistenceHelper.manager.delete(document);
	    		    }
	    		}

	        } catch (Exception e) {
	            // TODO: handle exception
	            e.printStackTrace();
	        } finally {
	            SessionServerHelper.manager.setAccessEnforced(enforce);
	        }

		}

	}

	/**
	 * @param document
	 */
	private static void deleteReps(WTDocument document) {

		try {
			String tempName = "Print_" + document.getNumber() + "_" + document.getVersionIdentifier().getValue() + "_" + document.getNumber() + ".pdf";
			QueryResult qr = RepresentationHelper.service.getRepresentations(document);
			 Representation defaultrep = RepresentationHelper.service.getDefaultRepresentation(document);
			List<Representation> deleteObjs = new ArrayList<Representation>();
			while (qr.hasMoreElements()) {
				Representation representation = (Representation) qr.nextElement();
				if (representation != null) {
					String repName = representation.getName();
					//QueryResult qr2 = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
					if(tempName.equals(repName)){
						if(defaultrep!=null&&defaultrep.getPersistInfo().getObjectIdentifier().getId()!=representation.getPersistInfo().getObjectIdentifier().getId())
						{
							try {
								RepresentationHelper.service.setDefaultRepresentation(document, representation, true);
							} catch (PropertyVetoException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}else {
						if(defaultrep!=null&&defaultrep.getPersistInfo().getObjectIdentifier().getId()!=representation.getPersistInfo().getObjectIdentifier().getId()){
							deleteObjs.add(representation);
						}
					}
				}
			}
			for (Representation rep : deleteObjs) {
				try {
					RepresentationHelper.service.deleteRepresentation(rep);
				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void processPbomXml() throws RemoteException, InvocationTargetException {
		if (!RemoteMethodServer.ServerFlag) {
			RemoteMethodServer.getDefault().invoke("processPbomXml",
					DeleteProcessDocUtility.class.getName(),
					null,
					new Class[] {},
					new Object[] {});
		} else {
			boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
	        try {
	    		String type = "wt.doc.WTDocument|casc.sast.149.PBOM";
    	    	TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
    			long typeId = 0;
    			if (tdr != null) {
    				typeId = tdr.getKey().getBranchId();
    			}
    			QuerySpec qs = new QuerySpec(WTDocument.class);

    		    qs.appendWhere(new SearchCondition(WTDocument.class,
    		    "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
    		     new int[]{0});
    		    qs.appendAnd();
    		    qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
    		    QueryResult qr = PersistenceHelper.manager.find(qs);
    		    while(qr.hasMoreElements()){
    		    	WTDocument document = (WTDocument)qr.nextElement();
    		    	boolean  checkedOut = WorkInProgressHelper.isCheckedOut((Workable) document);
    		    	if(!checkedOut){
	    		    	deleteIterations(document);
    		    	}
    		    }

	        } catch (Exception e) {
	            // TODO: handle exception
	            e.printStackTrace();
	        } finally {
	            SessionServerHelper.manager.setAccessEnforced(enforce);
	        }

		}




	}
	public static void deleteIterations(WTDocument document) {
		try {
    		System.out.println(document.getNumber()+"."+document.getVersionIdentifier().getValue()+"."+document.getIterationIdentifier().getValue());
    		removeBeforeLink(document);
    		QueryResult localQueryResult = VersionControlHelper.service.iterationsOf((Iterated)document);
    		Workable localWorkable = null;
    		while (localQueryResult.hasMoreElements()) {
    			localWorkable = (Workable)localQueryResult.nextElement();
    			if (VersionControlHelper.service.isFirstIteration(localWorkable)) {
    				break;
    			}
    		}
    		VersionControlHelper.service.rollup(localWorkable, (Iterated)document);
    		deleteReps(document);
    	} catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

	}
	private static void removeBeforeLink(WTDocument document) throws ChangeException2, WTException {
		QueryResult qr = VersionControlHelper.service.allIterationsOf(document.getMaster());
		while(qr.hasMoreElements()){
			 WTDocument doc = (WTDocument)qr.nextElement();
			 if(!doc.isLatestIteration()){
				 PurgeDataProcessor.removeFromChange(doc);
		         PurgeDataProcessor.deleteLinkD2D(doc);
		        // PurgeDataProcessor.deleteLinkD2P(doc);
			 }

		}


	}
	public static void process2() throws WTException, IOException, PropertyVetoException, DocumentException{
		if (!RemoteMethodServer.ServerFlag) {
			String method = "process2";
			Class[] types = { };
			Object[] vals = {};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				 rms.invoke(method,
						DeleteProcessDocUtility.class.getName(), null, types, vals);
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
						DeleteProcessDocUtility.class.getName(), null, types, vals);
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
						DeleteProcessDocUtility.class.getName(), null, types, vals);
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
	public static void test() throws PersistenceException, WTException {
		//3048723
		WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, "3048723");
		//WTDocument document = DocUtil.getDoc("1470895157621", false);
		Versioned vBase = null;
		QueryResult qr = VersionControlHelper.service.allVersionsFrom(document);
		while (qr.hasMoreElements()) {
			vBase = (Versioned) qr.nextElement();
			if (!(vBase instanceof OneOffVersioned) || !VersionControlHelper.isAOneOff((OneOffVersioned) vBase)){
				System.out.println(vBase.getVersionInfo().getIdentifier().getValue());
			}
		}

	}
	public static void test2(String a,String b) throws PersistenceException, WTException {
		//3048723
		EPMDocument epm = (EPMDocument) Util.getObjectByOid(EPMDocument.class, a);
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, b);
		//WTDocument document = DocUtil.getDoc("1470895157621", false);
	    EPMBuildRule epmbuildrule = EPMBuildRule.newEPMBuildRule(epm, part, 3);
        PersistenceServerHelper.manager.insert(epmbuildrule);



	}

	public static void test3(String number) throws PersistenceException, WTException {
		WTDocument doc = CSCDoc.getDoc(number);
		try {
			for(int i=0;i<=300;i++){
				doc = (WTDocument) WorkInProcessUtil.checkout(doc);
				doc = (WTDocument) WorkInProcessUtil.checkin(doc);
				System.out.println(doc.getIterationInfo().getIdentifier().getValue());
			}

		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}



}
