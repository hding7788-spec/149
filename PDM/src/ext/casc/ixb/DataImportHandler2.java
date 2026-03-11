package ext.casc.ixb;

import com.glaway.mpm.sjzyk.SjzykSchedule;
import com.ptc.extend.ixb.*;
import com.ptc.extend.util.ObjectProperty;
import com.ptc.wpcfg.utilities.PrincipalHelper;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.changerequest.ChangeRequestAffectLink;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.preview.Preview;
import ext.casc.util.IBAHelper;
import ext.casc.workflow.CmWorkflowHelper;
import org.apache.soap.SOAPException;
import org.apache.soap.rpc.Call;
import org.apache.soap.rpc.Parameter;
import org.apache.soap.rpc.Response;
import org.apache.soap.transport.http.SOAPHTTPConnection;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.oracle81.OracleDataSource;
import wt.pom.Transaction;
import wt.project.Role;
import wt.team.Team;
import wt.util.WTException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DataImportHandler2 implements RemoteAccess {

    public String importData(String fileName) {
        System.out.println("==================Server=======================" + fileName);
        if (!isProductExist()) {
            createProduct();
        }

        if (!isImportTargetExist()) {
            doImportData();
        } else {
            doUpdateData();
        }

        return "Success";
    }

    private String doImportData() {
        return "Success";
    }

    private void doUpdateData() {

    }

    // ////////////////
    private boolean isProductExist() {

        return true;
    }

    private PDMLinkProduct createProduct() {
        PDMLinkProduct product = null;

        return product;
    }

    private boolean isImportTargetExist() {

        return true;
    }

    public static void main(String[] args) throws WTException {
    	System.out.println("DataImportHandler2");
    	String filePath = null;
		String userName = null;
		String password = null;
		int i = args.length;

		if (i ==0){
			System.out.println("Usage: windchill ext.casc.ixb.DataImportHandler -f <导入文件全路径> -u <用户名> -p <密码>");
			System.exit(1);
		}

		for(int j = 0; j < i; j++){
			if (args[j].equalsIgnoreCase("-f")) {
				if (++j < i) {

					filePath = new String(args[j]);
				}
				continue;
			}

			if (args[j].equalsIgnoreCase("-u")) {
				if (++j < i) {
					userName = new String(args[j]);
				}
				continue;
			}
			if (args[j].equalsIgnoreCase("-p")) {
				if (++j < i) {
					password = new String(args[j]);
				}
			}
		}

		if(filePath==null||filePath.length()<=0){
			System.out.println("请指定导入文件全路径.");
			System.exit(1);
		}
		if(!filePath.endsWith(".expimp")){
			System.out.println("导入文件格式不正确.");
			System.exit(1);
		}
		if((userName==null||userName.length()<=0)
				||(password==null||password.length()<=0)){
			System.out.println("用户名或密码不能为空.");
			System.exit(1);
		}
		RemoteMethodServer rms=RemoteMethodServer.getDefault();
		rms.setUserName(userName);
		rms.setPassword(password);
		Map<String,String> params = new HashMap<String,String>();
		params.put("fileName", filePath);
        //processReceivedData(filePath,"wf",null, null, null, null,"1","no8","orderIID","longxiuchuan");
		processZYKReceivedData(filePath, "zyk");
    }

    public static String processReceivedData2(String fileName, String wfProcessOid, String activityTemplateID,String activityName ,String activityOid,
            String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser){
    	try {
			return processReceivedData(fileName, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    }

    public static String processReceivedData(String fileName, String wfProcessOid, String activityTemplateID,String activityName ,String activityOid,
            String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser)
            throws WTException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "processReceivedData";
            Class[] types = { String.class, String.class,String.class, String.class,String.class, String.class ,String.class,String.class,String.class,String.class};
            Object[] vals = { fileName, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID ,previewUser};

            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
				return (String)rms.invoke(method, DataImportHandler.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        File file = new File(fileName);
        if(!file.exists()){
        	return "数据包异常删除";
        }
        return  processReceivedData(file, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser);
    }

    /**数据包导入接口
     * @param file 数据包文件
     * @param wfProcessOid 总体所流程oid (activityOid805)
     * @param activityOid 总装厂等待活动oid 值为 可能为null (activityOid149)
     * @param activityName 整体所等待活动 值为 外部工艺会签 技术会签等
     * @param reviewType  会签类型 值为 工艺预审、文档签审(WTDocument)、批量签审(ProcessEnvelope)、变更签审(ChangePacked) (approvedType)
     * @param workflowType 用于发放数据记录 值为 正式数据、工艺会签等 (isFormal)
     * @param sendFrom 总体所发放单位
     * @param activityTemplateID 只用于A4系统
     * @param orderIID A4系统送审单oid
     * @throws WTException
     */
    public static String processReceivedData(File file, String wfProcessOid,String activityTemplateID,String activityName, String activityOid, String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser)
            throws WTException {
    	String message = "";
        if (!RemoteMethodServer.ServerFlag)
            throw new WTException("This method must run in MethodServer.");

        CmImportHandler impHnd = new CmImportHandler(file);
        ExpImpLogger logger = ExpImpLogger.getInstance();
        ProcessEnvelope pe = null;
        ChangePackaged changePackaged = null;
        ChangeRequest request = null;
        Preview preview = null;
        try {
            ArrayList list = impHnd.getAllTopObjectXmlFileInJar();
            logger.log("All Ojbect Size Is: " + list.size());
            Iterator it = list.iterator();
            while (it.hasNext()) {
                String fname = (String) it.next();
                CmExpImpObject expimp = CmExpImpPersistable.newCmExpImpPersistable(impHnd, fname);
                if (expimp == null){
                	continue;
                }
                expimp.setSendFrom(sendFrom);
                if(sendFrom==null||"null".equals(sendFrom)){
                	 expimp.setSendFrom("805");
                }
                String typename = fname.substring(fname.indexOf("TAG-") + 4);
                if (typename.indexOf("-") > 0)
                    typename = typename.substring(0, typename.indexOf("-"));
                else typename = typename.substring(0, typename.indexOf(".xml"));
                logger.log("Start To Import Ojbect Type " + typename);
				try {
					Object object = expimp.importObject();
					if(object ==null){
						logger.log("ERROR:数据导入失败,不存在产品库："+expimp.getContainerName());
						return "数据导入失败,不存在产品库："+expimp.getContainerName();
					}
					if (object instanceof ProcessEnvelope) {
						pe = (ProcessEnvelope) object;
						// 先断掉所有的link关系
						QueryResult qr = PersistenceHelper.manager.navigate(pe,
								"theRevisionControlled",
								EnvelopeMemberLink.class, false);
						if (qr.size() > 0) {
							Transaction tx = new Transaction();
							tx.start();
							while (qr.hasMoreElements()) {
								EnvelopeMemberLink link = (EnvelopeMemberLink) qr
										.nextElement();
								RevisionControlled revision = link
										.getRevisionControlled();
								String memberNumber = ObjectProperty
										.getNumber(revision);
								if (!(revision instanceof WTPart)) {
									impHnd.putIntExistedNumberObject(
											memberNumber, revision);
								}
								PersistenceHelper.manager.delete(link);
							}
							tx.commit();

						}

					}

					if (object instanceof Preview) {
						preview = (Preview) object;
                      }


					if (object instanceof ChangeRequest) {
                        request = (ChangeRequest) object;
                        // 先断掉所有的link关系
                        QueryResult qr = PersistenceHelper.manager.navigate(request,
                                "theRevisionControlled",
                                ChangeRequestAffectLink.class, false);
                        if (qr.size() > 0) {
                            Transaction tx = new Transaction();
                            tx.start();
                            while (qr.hasMoreElements()) {
                                ChangeRequestAffectLink link = (ChangeRequestAffectLink) qr
                                        .nextElement();
                                RevisionControlled revision = link
                                        .getRevisionControlled();
                                String memberNumber = ObjectProperty
                                        .getNumber(revision);
                                if (!(revision instanceof WTPart)) {
                                    impHnd.putIntExistedNumberObject(
                                            memberNumber, revision);
                                }
                                PersistenceHelper.manager.delete(link);
                            }
                            tx.commit();

                        }

                    }

					if (object instanceof ChangePackaged) {
						changePackaged = (ChangePackaged) object;
						// 先断掉所有的link关系
						QueryResult qr = PersistenceHelper.manager.navigate(
								changePackaged, "theRevisionControlled",
								ChangePackagedResultLink.class, false);
						if (qr.size() > 0) {
							Transaction tx = new Transaction();
							tx.start();
							while (qr.hasMoreElements()) {
								ChangePackagedResultLink link = (ChangePackagedResultLink) qr
										.nextElement();
								RevisionControlled revision = link
										.getRevisionControlled();
								String memberNumber = ObjectProperty
										.getNumber(revision);
								if (!(revision instanceof WTPart)) {
									impHnd.putIntExistedNumberObject(
											memberNumber, revision);
								}
								PersistenceHelper.manager.delete(link);
							}
							tx.commit();

						}
					}
					if ("WTDocument".equals(reviewType)) {
						WTDocument document = (WTDocument) object;
						// 获取关联流程实例
						boolean isHasProcess = false;
						Enumeration enumeration = WfEngineHelper.service
								.getAssociatedProcesses(document, null);
						WfProcess process = null;
						while (enumeration.hasMoreElements()) {
							process = (WfProcess) enumeration.nextElement();
							ProcessData pd = process.getContext();
							pd.setValue("wfProcessOid", wfProcessOid);
							pd.setValue("activityName", activityName);
							pd.setValue("activityTemplateID", activityTemplateID);
							pd.setValue("sendFrom", sendFrom);
							PersistenceHelper.manager.save(process);
							isHasProcess = true;
						}
						if (!isHasProcess) {
							WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
									.getProcessDefinition("文档工艺会签流程");
							WfProcess wfprocess = null;
							wfprocess = WfEngineHelper.service.createProcess(
									wfprocessdefinition, null,
									document.getContainerReference());

							wfprocess.setName("文档工艺会签流程_"
									+ document.getNumber());

							ProcessData processdata = wfprocess.getContext();
							processdata.setValue("activityOid805",
									wfProcessOid);
							processdata.setValue("primaryBusinessObject",
									document);// 设置流程主对象
							WfEngineHelper.service.startProcess(wfprocess,
									processdata, 1);
						}

					}
					logger.log("End  To Import Ojbect Type " + typename + "\n");
				} catch (WTException e) {
					/*if (e.getClass().getName().equalsIgnoreCase(ClassicContainerNotFoundException.class.getName())) {
						logger.log(expimp.getContainerName());
						WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
						WTOrganization  userOrg = currentUser.getOrganization();
						WTContainerRef  userConRef = null;
						if(userOrg==null){
							userConRef = WTContainerHelper.service.getExchangeRef();
						}else{
							userConRef = userOrg.getContainerReference();
						}
						WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
								.getProcessDefinition("149数据接收失败处理流程");
						WfProcess wfprocess = WfEngineHelper.service.createProcess(
								wfprocessdefinition, null,userConRef);
						ProcessData processdata = wfprocess.getContext();
						processdata.setValue("wfProcessOid",wfProcessOid);
						processdata.setValue("activityOid",activityOid);
						processdata.setValue("activityName", activityName);
						processdata.setValue("reviewType",reviewType);
						processdata.setValue("workflowType",workflowType);
						processdata.setValue("activityTemplateID", activityTemplateID);
						processdata.setValue("sendFrom", sendFrom);
                        processdata.setValue("orderIID", orderIID);
						processdata.setValue("fileName",file.getAbsolutePath());
						processdata.setValue("prompt",expimp.getContainerName());
						WfEngineHelper.service.startProcess(wfprocess,processdata, 1);
					}*/
                    logger.log(e);
                    //throw new WTException(e.getMessage()+" "+expimp.getContainerName());
                    message = e.getMessage()+" "+expimp.getContainerName();
                    return message;
                }
            }
        } catch (WTException wet) {
            logger.log(wet);
            throw wet;
        }

        if (preview !=null) {
            // 获取关联流程实例
            boolean isHasProcess = false;
            Enumeration enumeration = WfEngineHelper.service.getAssociatedProcesses(preview, null);
            WfProcess process = null;
            if (enumeration.hasMoreElements()) {
                process = (WfProcess) enumeration.nextElement();
                if(!process.getName().contains("Submit_APDK-2000000049")){
                	ProcessData pd = process.getContext();
                    // pd.setValue("wfProcessOid", wfProcessOid);
                    // pd.setValue("activityName", activityName);
                    // pd.setValue("activityTemplateID", activityTemplateID);
                     pd.setValue("orderIID", orderIID);
                     pd.setValue("sendFrom", sendFrom);
                     PersistenceHelper.manager.save(process);
                     isHasProcess = true;
                }

            }
            if (!isHasProcess) {
                WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
                        .getProcessDefinition(IXBConstants.PREVIEWWORKFLOWNAME);
                process = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
                		preview.getContainerReference());
                process.setName(IXBConstants.PREVIEWWORKFLOWNAME+"_" + preview.getNumber());
                ProcessData processdata = process.getContext();
                processdata.setValue("primaryBusinessObject", preview);// 设置流程主对象
               // processdata.setValue("wfProcessOid", wfProcessOid);
                //processdata.setValue("activityName", activityName);
                //processdata.setValue("activityTemplateID", activityTemplateID);
                processdata.setValue("orderIID", orderIID);
                processdata.setValue("sendFrom", sendFrom);
                setPrinciple2Role(process, "GONGYIHUIQIANZHE", previewUser);
                WfEngineHelper.service.startProcess(process, processdata, 1);
            }
		}

        // boolean missingObjs=false;
        boolean linkImportFailed = false;
        ArrayList wtal = impHnd.getAllObjects();
        Iterator it = wtal.iterator();
        while (it.hasNext()) {
            Persistable p = (Persistable) it.next();
            String remoteId = (String) impHnd.getImportedObjectRemoteId(p);
            String dir = CmExpImpHelper.getObjectLocalIdSavePathInJar(remoteId);
            ArrayList list = impHnd.getXmlDocumentsUnderLikelyDirInJar(dir);
            if (list.size() > 0) {
                Collections.sort(list, new CmImportHandler.CmJarFileNameComparator());
            }
            for (int i = 0; i < list.size(); i++) {
                logger.log(list.get(i));
            }
            Iterator ite = list.iterator();
            while (ite.hasNext()) {
                String fname = (String) ite.next();
                CmExpImpLink expimp = CmExpImpLink.newCmExpImpLink(p, impHnd, fname);
                if (expimp == null)
                    continue;
                try {
                    expimp.importObject();
                } catch (MissingObjectException moe) {
                    impHnd.removeFromProperlyReceivedObjectSet(p);
                    // missingObjs=true;
                } catch (WTException wte) {
                    wte.printStackTrace();

                    impHnd.removeFromProperlyReceivedObjectSet(p);
                    linkImportFailed = true;
                }
            }
        }



        if (preview !=null) {
        	Hashtable table = impHnd.getNewObjects();
        	try {
				saveDataSendRecords(preview, table,workflowType);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }


        if (pe !=null) {
        	Hashtable table = impHnd.getNewObjects();
        	try {
				saveDataSendRecords(pe, table,workflowType);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        if (changePackaged !=null) {
        	Hashtable table = impHnd.getNewObjects();
        	try {
				saveDataSendRecords(changePackaged, table,workflowType);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }


        if (activityOid != null && !activityOid.equals("")) {
            // 专为发送变更包正式数据而完成流程活动而传的参数值pass
            if (reviewType.equals("pass")) {
                CmWorkflowHelper.completeActivity(activityOid, "发放", "签审通过，程序完成活动");
            } else {
               CmWorkflowHelper.completeActivity(activityOid, "驳回", "签审驳回，程序完成活动");
            }
        }
        if (linkImportFailed)
            throw new WTException("Not all Linkage of object imported properly.");
        return message;
    }


    public static void setPrinciple2Role(WfProcess wfProcess, String roleName, String userName) throws WTException {
        Role role = Role.toRole(roleName);
        Team team = (Team) wfProcess.getTeamId().getObject();
        System.out.println(userName);
        String[]  name=userName.split(";");
        for (int i = 0; i < name.length; i++) {
        	if (!name.equals("")) {
        		WTPrincipal principal = (WTPrincipal) PrincipalHelper.getPrincipal(name[i]).getObject();
                team.addPrincipal(role, principal);
        	}
        }
        team = (Team) PersistenceHelper.manager.refresh(team);
        team = (Team) PersistenceHelper.manager.save(team);
    }

    /**
	 * 数据发放管理模块，当往149厂发放数据成功时，把包及包下面的数据写到数据库表中
	 *
	 * @param primaryBusinessObject
	 * @throws WTException
	 * @throws SQLException
	 */
	public static void saveDataSendRecords(WTObject primaryBusinessObject,Hashtable table,String workflowType) throws WTException, SQLException {
		ReferenceFactory rf = new ReferenceFactory();
		Set<Persistable> set = new HashSet<Persistable>();
		String pOid =null;
		String pNumber = null;
		String pName = null;
		if (primaryBusinessObject instanceof ProcessEnvelope) {
			ProcessEnvelope pe = (ProcessEnvelope) primaryBusinessObject;
			pOid = rf.getReferenceString(pe);
			pNumber = pe.getNumber();
			pName = pe.getName();
			QueryResult qr = PersistenceHelper.manager.navigate(pe,
					"theRevisionControlled", EnvelopeMemberLink.class, false);

			while (qr.hasMoreElements()) {
				EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
				set.add(link.getRevisionControlled());
			}
		} else if (primaryBusinessObject instanceof ChangePackaged) {
			ChangePackaged change = (ChangePackaged) primaryBusinessObject;
			pOid = rf.getReferenceString(change);
			pNumber = change.getNumber();
			pName = change.getName();
			QueryResult qr = PersistenceHelper.manager.navigate(change,
					ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
					ChangePackagedResultLink.class, true);
			while (qr.hasMoreElements()) {
				set.add((Persistable) qr.nextElement());
			}
		} else if (primaryBusinessObject instanceof WTDocument) {
			WTDocument document = (WTDocument) primaryBusinessObject;
			pOid = rf.getReferenceString(document);
			pNumber = document.getNumber();
			pName = document.getName();
			set.add(document);
		} else if (primaryBusinessObject instanceof Preview) {
			Preview preview = (Preview) primaryBusinessObject;
			pOid = rf.getReferenceString(preview);
			pNumber = preview.getNumber();
			pName = preview.getName();
			set.add(preview);
		}

		Connection conn = OracleDataSource.getOracleDataSource()
				.getConnection();
		conn.setAutoCommit(false);
		Statement state = conn.createStatement();

        Enumeration enumeration = table.elements();
        while (enumeration.hasMoreElements()) {
			Persistable persistable = (Persistable) enumeration.nextElement();
			if (!persistable.equals(primaryBusinessObject)) {

				Date date = new Date(System.currentTimeMillis());
				String underReview = "否";
				StringBuffer sb2 = new StringBuffer();
				String oNumber = ObjectProperty.getNumber(persistable);
				String oName = ObjectProperty.getName(persistable);
				String oVersion = ObjectProperty.getVersionIterationDisplay(persistable);
				String oOid = persistable.getPersistInfo().getObjectIdentifier().getStringValue();
				String pIndex = IBAHelper.getIBAStringValue((WTObject) persistable, "PINDEX");
				if (set.contains(persistable)) {
					underReview = "是";
					set.remove(persistable);
				}
				sb2.append("insert into ASES_DATA_SEND_TABLE ");
				if (pIndex ==null) {
					pIndex = "";
				}
				sb2.append("(PACKAGE_NUM,PACKAGE_NAME,PACKAGE_OID,OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,PRODUCT_CODE,SEND_TYPE,UNDERREVIEW,SEND_DATE) ");
				sb2.append("values('" + pNumber + "','"+ pName + "','"+ pOid + "','" + oNumber + "','" + oName
						+ "','" + oVersion + "','" + oOid + "','"+pIndex+"','"+workflowType+"','"+underReview+"',"+ "date '" + date + "')");
				try {
					state.execute(sb2.toString());
					conn.commit();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}

			}

		}

        Iterator<Persistable> iterator = set.iterator();
        while (iterator.hasNext()) {
			Persistable persistable =  iterator.next();
			Date date = new Date(System.currentTimeMillis());
			String underReview = "是";
			StringBuffer sb2 = new StringBuffer();
			String oNumber = ObjectProperty.getNumber(persistable);
			String oName = ObjectProperty.getName(persistable);
			String oVersion = ObjectProperty.getVersionIterationDisplay(persistable);
			String oOid = persistable.getPersistInfo().getObjectIdentifier().getStringValue();
			String pIndex = IBAHelper.getIBAStringValue((WTObject) persistable, "PINDEX");
			sb2.append("insert into ASES_DATA_SEND_TABLE ");
			if (pIndex ==null) {
				pIndex = "";
			}
			sb2.append("(PACKAGE_NUM,PACKAGE_NAME,PACKAGE_OID,OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,PRODUCT_CODE,SEND_TYPE,UNDERREVIEW,SEND_DATE) ");
			sb2.append("values('" + pNumber + "','"+ pName + "','"+ pOid + "','" + oNumber + "','" + oName
					+ "','" + oVersion + "','" + oOid + "','"+pIndex+"','"+workflowType+"','"+underReview+"',"+ "date '" + date + "')");
			try {
				state.execute(sb2.toString());
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		state.close();
	}

	public static String processZYKReceivedData(String fileName,String sendFrom)
			throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "processZYKReceivedData";
			Class[] types = { String.class ,String.class};
			Object[] vals = { fileName ,sendFrom};

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				return (String) rms.invoke(method,
						DataImportHandler2.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("processZYKReceivedData2");
		String message = "";
		File file = new File(fileName);
		if (!file.exists()) {
			return "数据包异常删除";
		}
		List errorObjs = new ArrayList();
		try {
			CmImportHandler impHnd = new CmImportHandler(file);
			ExpImpLogger logger = ExpImpLogger.getInstance();
			try {
				ArrayList list = impHnd.getAllTopObjectXmlFileInJar();
				logger.log("All Ojbect Size Is: " + list.size());
				Iterator it = list.iterator();
				while (it.hasNext()) {
					String fname = (String) it.next();
					CmExpImpObject expimp = CmExpImpPersistable.newCmExpImpZykPersistable(impHnd, fname);
					if (expimp == null) {
						continue;
					}
					expimp.setSendFrom(sendFrom);
					String typename = fname.substring(fname.indexOf("TAG-") + 4);
					if (typename.indexOf("-") > 0)
						typename = typename.substring(0, typename.indexOf("-"));
					else
						typename = typename.substring(0, typename.indexOf(".xml"));
					logger.log("Start To Import Ojbect Type " + typename);
					try {
						Object object = expimp.importObject();

						if (object != null) {
							if (object instanceof ErrorImportObject) {
								ErrorImportObject eo = (ErrorImportObject) object;
								eo.setFileName(fileName);
								logger.log("ERROR:数据导入失败：" + eo.getNumber());
								errorObjs.add(eo);

							} else {
								if (object instanceof WTPart) {
									SjzykSchedule.updateSjzykMiddleTable((WTPart) object);
								}

							}
						}
					} catch (WTException wet) {
						logger.log(wet);
					}

				}
			} catch (WTException wet) {
				logger.log(wet);
			}

			boolean linkImportFailed = false;
			ArrayList wtal = impHnd.getAllObjects();
			Iterator it = wtal.iterator();
			while (it.hasNext()) {
				Persistable p = (Persistable) it.next();
				String remoteId = (String) impHnd.getImportedObjectRemoteId(p);
				String dir = CmExpImpHelper.getObjectLocalIdSavePathInJar(remoteId);
				ArrayList list = impHnd.getXmlDocumentsUnderLikelyDirInJar(dir);
				if (list.size() > 0) {
					Collections.sort(list, new CmImportHandler.CmJarFileNameComparator());
				}
				for (int i = 0; i < list.size(); i++) {
					logger.log(list.get(i));
				}
				Iterator ite = list.iterator();
				while (ite.hasNext()) {
					String fname = (String) ite.next();
					CmExpImpLink expimp = CmExpImpLink.newCmExpImpLink(p, impHnd, fname);
					if (expimp == null)
						continue;
					try {
						expimp.setSendFrom(sendFrom);
						expimp.importObject();
					} catch (MissingObjectException moe) {
						impHnd.removeFromProperlyReceivedObjectSet(p);
						// missingObjs=true;
					} catch (WTException wte) {
						wte.printStackTrace();

						impHnd.removeFromProperlyReceivedObjectSet(p);
						linkImportFailed = true;
					}
				}
			}

			if (linkImportFailed)
				throw new WTException("Not all Linkage of object imported properly.");
		} finally {
			try {
				sendErrorObject(errorObjs, fileName);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// 返回成功信息
		}

		return message;
	}

	private static String sendErrorObject(List<ErrorImportObject> errorObjs,String fileName) throws SOAPException, MalformedURLException {
		//如果errorObjs里面没数据则返回成功信息
		URL url = new URL("http://10.112.1.93/Windchill/servlet/RPC");
		SOAPHTTPConnection st = new SOAPHTTPConnection();
		st.setUserName("wcadmin");
		st.setPassword("Xht123456");
		StringBuilder sb = new StringBuilder("");
		boolean isSuccess = false;

		if(errorObjs.isEmpty()){
			isSuccess = true;
		}else{
			isSuccess = false;
			sb.append("<ErrorImportObjects>");
			for(ErrorImportObject eo:errorObjs){
				sb.append("<ErrorImportObject>");
					sb.append("<number>");
					sb.append(eo.getNumber());
					sb.append("</number>");
					sb.append("<name>");
					sb.append(eo.getName());
					sb.append("</name>");
					sb.append("<cadName>");
					sb.append(eo.getCadName());
					sb.append("</cadName>");
					sb.append("<fileName>");
					sb.append(eo.getFileName());
					sb.append("</fileName>");
					sb.append("<message>");
					sb.append(eo.getMessage());
					sb.append("</message>");

					sb.append("<type>");
					sb.append(eo.getType());
					sb.append("</type>");

					sb.append("<version>");
					sb.append(eo.getVersion());
					sb.append("</version>");

					sb.append("<xmlName>");
					sb.append(eo.getXmlName());
					sb.append("</xmlName>");


				sb.append("</ErrorImportObject>");
			}
			sb.append("</ErrorImportObjects>");
		}
		System.out.println("ErrorImportObjectsXML:"+sb.toString());
		Vector params = new Vector();
		params.addElement(new Parameter("success", java.lang.String.class, isSuccess, null));
		params.addElement(new Parameter("message", java.lang.String.class, sb.toString(), null));
		params.addElement(new Parameter("fileName", java.lang.String.class, fileName, null));
		params.addElement(new Parameter("sendFrom", java.lang.String.class, "149", null));

		Call call = new Call();
		call.setSOAPTransport(st);
		call.setTargetObjectURI("urn:ie-soap-rpc:com.infoengine.soap");
		call.setMethodName("feedBack");
		call.setEncodingStyleURI("http://schemas.xmlsoap.org/soap/encoding/");
		call.setParams(params);
		Response resp = call.invoke(url, "urn:ie-soap-rpc:com.infoengine.soap!" + "feedBack");
		if (resp.generatedFault()) {
			org.apache.soap.Fault fault = resp.getFault();
			return fault.getFaultString();
		} else {
			Parameter ret = resp.getReturnValue();
			Object result = ret.getValue();
			String backStr = result.toString();
			return backStr;
		}

	}
}
