package com.glaway.mpm.change.qchange.helper;

import com.glaway.mpm.constants.AttributeConstants;
import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.constants.TypeNameConstants;
import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.mpmresource.gzcard.GZCardHelper;
import com.glaway.mpm.parameter.ParameterProcessor;
import com.glaway.mpm.pdf.PDFPreviewFactory;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.*;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import ext.casc.change.CSCChange;
import ext.casc.change.ChangeHelper;
import ext.casc.doc.CSCDoc;
import ext.casc.util.IBAUtility;
import ext.casc.util.PurgeDataProcessor;
import ext.casc.util.WCUtil;
import org.jdom.Element;
import wt.associativity.NCServerHolder;
import wt.change2.*;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.httpgw.URLFactory;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.WTRoleHolder2;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.*;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.WfException;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;
public class QChangeHelper implements RemoteAccess {

	private static final String CLASSNAME = QChangeHelper.class.getName();

	/**
	 * 同步ECN是否已完工
	 *
	 * @author qianlong
	 * @date 2013-5-21
	 * @param changeRequest2
	 * @return
	 *
	 */
	public static boolean checkECNFinished(ChangeRequest2 changeRequest2) {
		try {
			QueryResult qr = ChangeHelper2.service.getChangeOrders(changeRequest2, true);
			while (qr.hasMoreElements()) {
				ChangeOrder2 changeOrder2 = (ChangeOrder2) qr.nextElement();
				if (!changeOrder2.getState().toString().equals(Constants.COMPLETED)) {
					return false;
				}
			}
			return true;
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 同步ECA是否以解决
	 *
	 * @author qianlong
	 * @date 2013-5-21
	 * @param changeOrder2
	 * @return
	 *
	 */
	public static boolean checkActivitiesFinished(WTChangeOrder2 changeOrder2) {
		try {
			QueryResult queryresult = ChangeHelper2.service.getChangeActivities(changeOrder2, true);
			while (queryresult.hasMoreElements()) {
				WTChangeActivity2 changeActivity2 = (WTChangeActivity2) queryresult.nextElement();
				if (!changeActivity2.getState().toString().equals(Constants.RESOLVED)) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 通过ECR创建ECN
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param ecr
	 * @return
	 * @throws Exception
	 *
	 */
	public static WTChangeOrder2 createECNFromECR(WTChangeRequest2 ecr) throws Exception {
		String name = ecr.getName() + "更改通知";

		return createECNFromECR(ecr, name, TypeNameConstants.processChangeNoticeTypeName);
	}

	private static WTChangeOrder2 createECNFromECR(WTChangeRequest2 ecr, String name, String typeName) throws Exception {

		WTChangeOrder2 ecn = null;
		Transaction trx = null;
		SessionContext previous = SessionContext.getContext();
		boolean check = true;
		try {
			SessionHelper.manager.setPrincipal(ecr.getCreator().getName());
			SessionServerHelper.manager.setAccessEnforced(false);
			trx = new Transaction();
			trx.start();

			ecn = WTChangeOrder2.newWTChangeOrder2(name);
			ecn.setContainer(ecr.getContainer());
			ecn.setDescription(ecr.getDescription());
			ecn.setNeedDate(ecr.getNeedDate());
			ecn.setChangeNoticeComplexity(ChangeNoticeComplexity.SIMPLE);

			TypeUtil.setType(ecn, typeName);
			Folder folder = FolderUtil.getFolder(ecr.getLocation(), ecr.getContainerReference());
			FolderHelper.assignFolder(ecn, folder);

			ecn = (WTChangeOrder2) ChangeHelper2.service.saveChangeOrder((ChangeRequestIfc) ecr, (ChangeOrderIfc) ecn);
			trx.commit();
			trx = null;

		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
			throw e;
		} catch (WTException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
			SessionContext.setContext(previous);
			SessionServerHelper.manager.setAccessEnforced(check);
		}
		return ecn;
	}

	/**
	 * 通过ECN创建ECA
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param ecn
	 * @throws Exception
	 *
	 */
	public static void createActivitiesFromECN(WTChangeOrder2 ecn) throws Exception {
		String name = ecn.getName().replace("更改通知", "更改任务");
		createActivitiesFromECN(ecn, name, TypeNameConstants.processChangeActivityTypeName);
	}

	private static void createActivitiesFromECN(WTChangeOrder2 ecn, String name, String typeName) throws Exception {
		WTChangeRequest2 ecr = null;
		try {
			ecr = getECRFromECN(ecn);
		} catch (Exception e) {
			throw e;
		}
		Transaction trx = null;
		SessionContext previous = SessionContext.getContext();

		boolean check = true;
		try {
			WTPrincipal reviewer_prince = ecn.getCreator().getPrincipal();
			SessionHelper.manager.setPrincipal(reviewer_prince.getName());
			SessionServerHelper.manager.setAccessEnforced(false);
			trx = new Transaction();
			trx.start();

			WTChangeActivity2 eca = WTChangeActivity2.newWTChangeActivity2(name);
			eca.setContainer(ecn.getContainer());
			eca.setNeedDate(ecn.getNeedDate());
			eca.setDescription(ecn.getDescription());
			TypeUtil.setType(eca, typeName);

			eca = (WTChangeActivity2) ChangeHelper2.service.saveChangeActivity(ecn, eca);

			Team ecaTeam = TeamHelper.service.getTeam(eca);

			Role reviewer_role = Role.toRole(Constants.REVIEWER);
			Role assignee_role = Role.toRole(Constants.ASSIGNEE);

			Enumeration enumReviewer = ecaTeam.getPrincipalTarget(reviewer_role);
			while (enumReviewer.hasMoreElements()) {
				WTPrincipal principal = (WTPrincipal) ((WTPrincipalReference) enumReviewer.nextElement()).getObject();
				TeamHelper.service.deleteRolePrincipalMap(reviewer_role, principal, ecaTeam);
			}
			TeamHelper.service.addRolePrincipalMap(reviewer_role, reviewer_prince, (WTRoleHolder2) ecaTeam);
			ecaTeam = (Team) PersistenceHelper.manager.refresh(ecaTeam);

			Enumeration enumAssignee = ecaTeam.getPrincipalTarget(reviewer_role);
			while (enumAssignee.hasMoreElements()) {
				WTPrincipal principal = (WTPrincipal) ((WTPrincipalReference) enumAssignee.nextElement()).getObject();
				TeamHelper.service.deleteRolePrincipalMap(assignee_role, principal, ecaTeam);
			}
			TeamHelper.service.addRolePrincipalMap(assignee_role, reviewer_prince, (WTRoleHolder2) ecaTeam);
			ecaTeam = (Team) PersistenceHelper.manager.refresh(ecaTeam);

			if (ecr != null) {
				Vector objs = new Vector();
				QueryResult qs = ChangeHelper2.service.getChangeables(ecr);
				while (qs.hasMoreElements()) {
					Changeable2 obj = (Changeable2) qs.nextElement();
					if ((obj instanceof Workable) && !WorkInProgressHelper.isCheckedOut((Workable) obj)) {
						objs.add(obj);
					}
				}
				ChangeHelper2.service.storeAssociations(AffectedActivityData.class, (ChangeItemIfc) eca, objs);
			}
			trx.commit();
			trx = null;
		} catch (WTException lfe) {
			lfe.printStackTrace();
			throw new WTException(lfe);
		} catch (WTPropertyVetoException wtpve) {
			wtpve.printStackTrace();
			throw new WTException(wtpve);
		} finally {
			if (trx != null)
				trx.rollback();
			SessionContext.setContext(previous);
			SessionServerHelper.manager.setAccessEnforced(check);
		}
	}

	/**
	 * 通过ECN获取ECR
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param ecn
	 * @return
	 * @throws Exception
	 *
	 */
	public static WTChangeRequest2 getECRFromECN(WTChangeOrder2 ecn) throws Exception {
		QueryResult qs = ChangeHelper2.service.getChangeRequest((ChangeOrderIfc) ecn);
		if (qs.hasMoreElements())
			return (WTChangeRequest2) qs.nextElement();
		else
			return null;

	}

	/**
	 * 通过ECR获取ECN
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param ecn
	 * @return
	 * @throws Exception
	 *
	 */
	public static WTChangeOrder2 getECNFromECR(WTChangeRequest2 ecr) throws Exception {
		QueryResult qs = ChangeHelper2.service.getChangeOrders((WTChangeRequest2) ecr);
		if (qs.hasMoreElements())
			return (WTChangeOrder2) qs.nextElement();
		else
			return null;

	}

	/**
	 * 通过ECR获取产生对象
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param ecn
	 * @return
	 * @throws Exception
	 *
	 */
	public static QueryResult getChangeablesAfterFromECR(WTChangeRequest2 ecr) throws Exception {
		WTChangeOrder2 ern = getECNFromECR(ecr);
		return ChangeHelper2.service.getChangeablesAfter((ChangeOrderIfc) ern);
	}


	/**
	 * 更新检验记录表在数据库中版本
	* @author zhuhao
	* @date 2018-4-1
	* @param pbo
	* @throws WTException
	 * @throws RemoteException
	 */
	public static void upDataBaseForRecordTable(WTObject pbo) throws WTException, RemoteException{
		String number = "";
		String beforeVersion = "";
		String afterVersion = "";
		if (pbo instanceof WTChangeOrder2) {
			 WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			 //取更改前
			 QueryResult qResult1 = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			 if (qResult1.hasMoreElements()) {
				 Object object = qResult1.nextElement();
				 if (object instanceof WTDocument) {
					 WTDocument doc = (WTDocument)object;
					 number = doc.getNumber();
					 beforeVersion = doc.getVersionIdentifier().getValue();
				 }
				 if (object instanceof MPMProcessPlan) {
					 MPMProcessPlan plan = (MPMProcessPlan)object;
					 WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
					 number = document.getNumber();
					 beforeVersion = document.getVersionIdentifier().getValue();
				 }
			 }
			//取更改后
			 QueryResult qResult2 = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			 if (qResult2.hasMoreElements()) {
				 Object object = qResult2.nextElement();
				 if (object instanceof WTDocument) {
					 WTDocument doc = (WTDocument)object;
					afterVersion = doc.getVersionIdentifier().getValue();
				 }
				 if (object instanceof MPMProcessPlan) {
					 MPMProcessPlan doc = (MPMProcessPlan)object;
					afterVersion = doc.getVersionIdentifier().getValue();
				 }
			 }

			 if(!"".equals(beforeVersion) && !"".equals(afterVersion)){
				 try {
					ParameterProcessor.createNewParams(number, beforeVersion, afterVersion);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }


		}
	}

	public static String improvedProcessZipDocVresion(WTObject pbo) throws WTException {
		String resultValue = "";
		if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;

            WTUser creator = (WTUser)changeOrder2.getCreator().getObject();

            QueryResult afters = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
            boolean ishasRemove = false;
            while (afters.hasMoreElements()) {
            	Object object = afters.nextElement();
            	if(object instanceof WTDocument){
            		WTDocument newDoc = (WTDocument)object;
            		String technicsOid = Util.getStringOid(newDoc);
            		WTPart part = WCUtil.getRelatedWTPartByDoc(newDoc);
            		String partOid = Util.getStringOid(part);
            		resultValue =  partOid+"@"+technicsOid;

            		List<MPMProcessPlan> pplans = MPMProcessPlanUtil.getAllProcessPlanByWTDocument(newDoc);
            		for(MPMProcessPlan pplan:pplans){
            			ext.casc.purge.PurgeDataProcessor.removeFromChange(pplan);
            			PersistenceHelper.manager.delete(pplan);
            			ishasRemove = true;
            		}
            	}else if(object instanceof MPMProcessPlan){
            		MPMProcessPlan pplan = (MPMProcessPlan)object;
            		if(!ishasRemove){
            			ext.casc.purge.PurgeDataProcessor.removeFromChange(pplan);
                		PersistenceHelper.manager.delete(pplan);
            		}

            	}
            }
            if(!"".equals(resultValue)){
            	return resultValue;
            }

			QueryResult result = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			boolean isHasProcessPlan = false;
			while(result.hasMoreElements()) {
				Object object = result.nextElement();
				if(object instanceof MPMProcessPlan) {
					isHasProcessPlan = true;
				}
			}

            QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
            if (qResult.hasMoreElements()) {

				try {
					//批量创建工艺更改单
					ChangeHelper.batchCreateWTChangeOrder(changeOrder2);
				} catch (Exception e) {
					e.printStackTrace();
				}

            	Object object = qResult.nextElement();
            	if (object instanceof MPMProcessPlan) {
            		MPMProcessPlan pplan = (MPMProcessPlan)object;
            		String number = pplan.getNumber();
        			QueryResult qr = MPMProcessPlanHelper.service.getWTParts(pplan, NCServerHolder.makeForLatestConfigSpec());
        			if(qr.hasMoreElements()) {
        				WTPart part = (WTPart)qr.nextElement();
        				String ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
        				WTDocument document = ProcessPlanHelper.getProcessZipDoc2(part,number);

        				String technicsOid = String.valueOf(PersistenceHelper.getObjectIdentifier(document).getId());
        				technicsOid = improvedProcessZipDocVresion(ida2a2, technicsOid, creator,changeOrder2);
        				if(technicsOid == null) {
        					technicsOid = "";
        				}
        				resultValue = ida2a2+"@"+technicsOid;
        			}else{
        				//处理工艺实例化和零件没关联的bug，历史数据
        				WTDocument doc = CSCDoc.getDoc(number);
        				if(doc!=null){
        					QueryResult partqr = WTPartHelper.service.getDescribesWTParts(doc);
        			        if (partqr.hasMoreElements()) {
        			            WTPart wtPart = (WTPart) partqr.nextElement();
        			            String version = wtPart.getVersionIdentifier().getValue();
        						WTPart part = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class,wtPart.getNumber(),version,wtPart.getViewName());

        						String ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
                				//WTDocument document = ProcessPlanHelper.getProcessZipDoc2(part,number);

                				String technicsOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
                				technicsOid = improvedProcessZipDocVresion(ida2a2, technicsOid, creator,changeOrder2);
                				if(technicsOid == null) {
                					technicsOid = "";
                				}
                				resultValue = ida2a2+"@"+technicsOid;
        						try {
									MPMProcessPlanUtil.createMPMPartToProcessPlanLink(part, pplan);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

        			        }
        				}

        			}
        			changeChangeType(changeOrder2,number);

            	}else if (object instanceof WTDocument) {

            		WTDocument doc = (WTDocument)object;
            		try {

						String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
						if(docType.endsWith("PROCESSPLAN") || docType.endsWith("reportTechnics") || docType.endsWith("SOPDoc")) {
							if(!isHasProcessPlan){
								MPMProcessPlan plan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(doc.getNumber());
								Class<?> class2 = Class.forName("wt.change2.AffectedActivityData");
								QueryResult caqr = ChangeHelper2.service.getChangeActivities(changeOrder2);
								WTChangeActivity2 activity = null;
								while(caqr.hasMoreElements()) {
									activity = (WTChangeActivity2) caqr.nextElement();
								}
								if(activity != null) {
									Vector<Persistable> vector = new Vector<Persistable>();
									vector.add(plan);
									ChangeHelper2.service.storeAssociations(class2,
											activity, vector);
								}
							}

							QueryResult qr = PartDocServiceCommand.getAssociatedDescParts(doc);
							if(qr.hasMoreElements()) {
								WTPart part = (WTPart) qr.nextElement();
								String ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
								String technicsOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
								technicsOid = improvedProcessZipDocVresion(ida2a2, technicsOid, creator, changeOrder2);
								if(technicsOid == null) {
									technicsOid = "";
								}
								resultValue = ida2a2 + "@" + technicsOid;
							}
						} else {
							improvedDocVresion(doc, creator, changeOrder2);
						}

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WTPropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		changeChangeType(changeOrder2,doc.getNumber());
            	}
            }else{
            	try {
    				Thread.sleep(5000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	return improvedProcessZipDocVresion( pbo);
            	//throw new WTException("更改单的受影响对象关联还未建立！！");
            }
		}
		return resultValue;
	}

	public static String improvedProcessZipDocVresion(WTObject pbo,int retryTime) throws WTException {
		String resultValue = "";
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;


			WTUser creator = (WTUser)changeOrder2.getCreator().getObject();

			QueryResult afters = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			boolean ishasRemove = false;
			while (afters.hasMoreElements()) {
				Object object = afters.nextElement();
				if(object instanceof WTDocument){
					WTDocument newDoc = (WTDocument)object;
					String technicsOid = Util.getStringOid(newDoc);
					WTPart part = WCUtil.getRelatedWTPartByDoc(newDoc);
					String partOid = Util.getStringOid(part);
					resultValue =  partOid+"@"+technicsOid;

					List<MPMProcessPlan> pplans = MPMProcessPlanUtil.getAllProcessPlanByWTDocument(newDoc);
					for(MPMProcessPlan pplan:pplans){
						ext.casc.purge.PurgeDataProcessor.removeFromChange(pplan);
						PersistenceHelper.manager.delete(pplan);
						ishasRemove = true;
					}
				}else if(object instanceof MPMProcessPlan){
					MPMProcessPlan pplan = (MPMProcessPlan)object;
					if(!ishasRemove){
						ext.casc.purge.PurgeDataProcessor.removeFromChange(pplan);
						PersistenceHelper.manager.delete(pplan);
					}

				}
			}
			if(!"".equals(resultValue)){
				return resultValue;
			}

			QueryResult result = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			boolean isHasProcessPlan = false;
			while(result.hasMoreElements()) {
				Object object = result.nextElement();
				if(object instanceof MPMProcessPlan) {
					isHasProcessPlan = true;
				}
			}

			QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			if (qResult.hasMoreElements()) {

				try {
					ChangeHelper.batchCreateWTChangeOrder(changeOrder2);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Object object = qResult.nextElement();
				if (object instanceof MPMProcessPlan) {
					MPMProcessPlan pplan = (MPMProcessPlan)object;
					String number = pplan.getNumber();
					QueryResult qr = MPMProcessPlanHelper.service.getWTParts(pplan, NCServerHolder.makeForLatestConfigSpec());
					if(qr.hasMoreElements()) {
						WTPart part = (WTPart)qr.nextElement();
						String ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
						WTDocument document = ProcessPlanHelper.getProcessZipDoc2(part,number);

						String technicsOid = String.valueOf(PersistenceHelper.getObjectIdentifier(document).getId());
						technicsOid = improvedProcessZipDocVresion(ida2a2, technicsOid, creator,changeOrder2);
						if(technicsOid == null) {
							technicsOid = "";
						}
						resultValue = ida2a2+"@"+technicsOid;
					}else{
						//处理工艺实例化和零件没关联的bug，历史数据
						WTDocument doc = CSCDoc.getDoc(number);
						if(doc!=null){
							QueryResult partqr = WTPartHelper.service.getDescribesWTParts(doc);
							if (partqr.hasMoreElements()) {
								WTPart wtPart = (WTPart) partqr.nextElement();
								String version = wtPart.getVersionIdentifier().getValue();
								WTPart part = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class,wtPart.getNumber(),version,wtPart.getViewName());

								String ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
								//WTDocument document = ProcessPlanHelper.getProcessZipDoc2(part,number);

								String technicsOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
								technicsOid = improvedProcessZipDocVresion(ida2a2, technicsOid, creator,changeOrder2);
								if(technicsOid == null) {
									technicsOid = "";
								}
								resultValue = ida2a2+"@"+technicsOid;
								try {
									MPMProcessPlanUtil.createMPMPartToProcessPlanLink(part, pplan);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}

					}
					changeChangeType(changeOrder2,number);

				}else if (object instanceof WTDocument) {

					WTDocument doc = (WTDocument)object;
					try {

						String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
						if(docType.endsWith("PROCESSPLAN")||docType.endsWith("reportTechnics") || docType.endsWith("SOPDoc")){
							MPMProcessPlan plan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(doc.getNumber());
							if(plan!=null && !isHasProcessPlan){
								Class<?> class2 = Class.forName("wt.change2.AffectedActivityData");
								QueryResult caqr = ChangeHelper2.service.getChangeActivities(changeOrder2);
								WTChangeActivity2 activity = null;
								while (caqr.hasMoreElements()) {
									activity = (WTChangeActivity2) caqr.nextElement();
								}
								if (activity != null) {
									Vector<Persistable> vector = new Vector<Persistable>();
									vector.add(plan);
									ChangeHelper2.service.storeAssociations(class2,
											activity, vector);
								}
							}

							QueryResult qr = PartDocServiceCommand.getAssociatedDescParts(doc);
							if(qr.hasMoreElements()) {
								WTPart part = (WTPart)qr.nextElement();
								String ida2a2 = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
								String technicsOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
								technicsOid = improvedProcessZipDocVresion(ida2a2, technicsOid, creator,changeOrder2);
								if(technicsOid == null) {
									technicsOid = "";
								}
								resultValue = ida2a2+"@"+technicsOid;
							}
						}else{
							improvedDocVresion( doc, creator,changeOrder2);
						}

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WTPropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					changeChangeType(changeOrder2,doc.getNumber());
				}
			}else{
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				retryTime++;
				if(retryTime<=10){//重试10次
					System.out.println(changeOrder2.getNumber()+"更改单的受影响对象关联还未建立！！");
					return improvedProcessZipDocVresion( pbo,retryTime);
				}else{
					throw new WTException("更改单的受影响对象关联还未建立！！");
				}
			}
		}
		return resultValue;
	}


	private static synchronized void changeChangeType(WTChangeOrder2 changeOrder2, String number) throws WTException {
		com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) changeOrder2);
		String ECNTYPE = ibaHelper.getIBAValue("ECNTYPE");
		String CHANGETYPE = "";
		if(ECNTYPE!=null && "作废更改".equals(ECNTYPE)){
			CHANGETYPE = "F";
		}else if(ECNTYPE!=null && "新增更改".equals(ECNTYPE)){
			CHANGETYPE = "Z";
		}else if(ECNTYPE!=null && "正常更改".equals(ECNTYPE)){
			ArrayList affectItems = ChangeHelper.getChangeAffectItem(changeOrder2);
			for(Object affectItem :affectItems){
				if(affectItem instanceof MPMProcessPlan){
					MPMProcessPlan plan = (MPMProcessPlan)affectItem;
					CHANGETYPE =ProcessEditorToWCIntfRMI.getChangeBiaoJiByTechnics(plan.getNumber());
					break;
				}else if(affectItem instanceof WTDocument){
					WTDocument doc = (WTDocument)affectItem;
					CHANGETYPE =ProcessEditorToWCIntfRMI.getChangeBiaoJiByTechnics(doc.getNumber());
					break;
				}
			}
		}

		ext.casc.util.IBAHelper.setIBAStringValue(changeOrder2,"CHANGETYPE",CHANGETYPE);
		/*IBAUtility iba = new IBAUtility((IBAHolder) changeOrder2);
		try {
			iba.setIBAValue("CHANGETYPE", CHANGETYPE);
			changeOrder2 = (WTChangeOrder2) iba.updateAttributeContainer(changeOrder2);
			iba.updateIBAHolder(changeOrder2);
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	/**
	 * '工艺更改单审核流程'升级工艺压缩包文件的版本
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param mpmPartOid
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 *
	 */
	public static String improvedProcessZipDocVresion(String partOid, String documnetOid, WTUser creator,WTChangeOrder2 ecn) {
		Versioned newDoc = null;
		Transaction tx = new Transaction();
		WTUser currentuser = null;
		try {
		    currentuser = (WTUser)SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setPrincipal(creator.getAuthenticationName());

			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
			WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, documnetOid);
			// 取最新大版本中的最新一个非一次性版本作为版本编号基础
			QueryResult qr = VersionControlHelper.service.allVersionsOf(document);
			Versioned vMax = (Versioned) qr.nextElement();

			// 取统一大版本中的最新一个非一次性版本作为新版内容基础
			Versioned vBase = null;
			qr = VersionControlHelper.service.allIterationsOf(document.getMaster());
			if (qr.hasMoreElements()) {
				vBase = (Versioned) qr.nextElement();
			}
			if (vBase == null) // 应该不可能的错误
				throw new Exception("Unknown Error, no normal version found.");

			// 检查同一大版本中最新一个非一次性版本是否被检出
			if (vBase instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) vBase))
				throw new Exception("选中版本的最新非先行更改版本正被检出，不能进行修订!");

			// 取其下一版本号和初始小版本号创建新版本
			tx.start();
			// 新建并保存修订版本
			VersionIdentifier vi = VersionControlHelper.nextVersionId(vMax);
			IterationIdentifier ii = VersionControlHelper.firstIterationId(vMax);
			newDoc = VersionControlHelper.service.newVersion(vMax, vi, ii);
			newDoc = (WTDocument) PersistenceHelper.manager.store(newDoc);


			// 替换该版本的主体文件
			WTDocumentUtil.setPrimaryForDocument((WTDocument) newDoc, (WTDocument) vBase);

			//part = WTPartUtil.getLatestPartByNumberAndView(part, "Manufacturing");
			part = WTPartUtil.getLatestPartByVersionNumberAndView(part, "Manufacturing");
			WTPartUtil.createWTPartDescribeLink(part, (WTDocument) newDoc);

			//修改工艺文件信息
			SWXMLUtil.updateTechnicsInfo((WTDocument)newDoc,creator);

			Vector v = new Vector();
			v.add(newDoc);
			//关联修订工艺文档到更改单的更改后文件
			QueryResult ecaResult = ChangeHelper2.service.getChangeActivities(ecn);
			while (ecaResult.hasMoreElements()) {
				WTChangeActivity2 activity2 = (WTChangeActivity2) ecaResult.nextElement();
				ChangeHelper2.service.storeAssociations(ChangeRecord2.class, activity2,v);
			}

			removeDocPrintPdf(newDoc);

			newDoc = (WTDocument) PersistenceHelper.manager.refresh(newDoc);
			WTDocument newWTDocument = (WTDocument)newDoc;
			IBAUtility iba = new IBAUtility(newWTDocument);
			try {
				iba.setIBAValue( "CLDEZT", "");
				iba.setIBAValue( "GongShiDingEState", "");
				newWTDocument =(WTDocument) iba.updateAttributeContainer(newWTDocument);
				iba.updateIBAHolder((IBAHolder)newWTDocument);
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			tx.commit();
			tx = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tx != null)
				tx.rollback();

			try {
				SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return Util.getStringOid(newDoc);
	}


	public static String improvedDocVresion(WTDocument document, WTUser creator,WTChangeOrder2 ecn) {
		Versioned newDoc = null;
		Transaction tx = new Transaction();
		WTUser currentuser = null;
		try {
		    currentuser = (WTUser)SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setPrincipal(creator.getAuthenticationName());

			// 取最新大版本中的最新一个非一次性版本作为版本编号基础
			QueryResult qr = VersionControlHelper.service.allVersionsOf(document);
			Versioned vMax = (Versioned) qr.nextElement();

			// 取统一大版本中的最新一个非一次性版本作为新版内容基础
			Versioned vBase = null;
			qr = VersionControlHelper.service.allVersionsFrom(document);
			while (qr.hasMoreElements()) {
				vBase = (Versioned) qr.nextElement();
				if (!(vBase instanceof OneOffVersioned) || !VersionControlHelper.isAOneOff((OneOffVersioned) vBase))
					break;
			}
			if (vBase == null) // 应该不可能的错误
				throw new Exception("Unknown Error, no normal version found.");

			// 检查同一大版本中最新一个非一次性版本是否被检出
			if (vBase instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) vBase))
				throw new Exception("选中版本的最新非先行更改版本正被检出，不能进行修订!");

			// 取其下一版本号和初始小版本号创建新版本
			tx.start();
			// 新建并保存修订版本
			VersionIdentifier vi = VersionControlHelper.nextVersionId(vMax);
			IterationIdentifier ii = VersionControlHelper.firstIterationId(vMax);
			newDoc = VersionControlHelper.service.newVersion(vMax, vi, ii);
			newDoc = (WTDocument) PersistenceHelper.manager.store(newDoc);

			// 替换该版本的主体文件
			WTDocumentUtil.setPrimaryForDocument((WTDocument) newDoc, (WTDocument) vBase);


			Vector v = new Vector();
			v.add(newDoc);
			//关联修订工艺文档到更改单的更改后文件
			QueryResult ecaResult = ChangeHelper2.service.getChangeActivities(ecn);
			while (ecaResult.hasMoreElements()) {
				WTChangeActivity2 activity2 = (WTChangeActivity2) ecaResult.nextElement();
				ChangeHelper2.service.storeAssociations(ChangeRecord2.class, activity2,v);
			}

			removeDocPrintPdf(newDoc);

			tx.commit();
			tx = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tx != null)
				tx.rollback();

			try {
				SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return Util.getStringOid(newDoc);
	}
	public static void removeDocPrintPdf(Versioned newDoc) {
		try {
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			ContentHolder holder = ContentHelper.service.getContents((ContentHolder) newDoc);
			Vector vector = ContentHelper.getContentList(holder);
			if (vector != null) {
				for (int i = 0; i < vector.size(); i++) {
					ContentItem contentitem1 = (ContentItem) vector.elementAt(i);
					if (contentitem1 instanceof ApplicationData) {
						ApplicationData data = (ApplicationData) contentitem1;
						if (data.getFileName().startsWith("Print_")) {
							ContentServerHelper.service.deleteContent(holder, contentitem1);
						}
					}

				}
			}
			SessionServerHelper.manager.setAccessEnforced(enforce);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * '工艺更改单审核流程' 获取工艺文件中的信息
	 *
	 * @author qianlong
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws WTRuntimeException
	 * @throws PropertyVetoException
	 * @date 2013-5-23
	 *
	 */
	public static Map<String, String> getInfoByProcessPlan(WTChangeRequest2 changeRequest2) {
		Map<String, String> map = new HashMap<String, String>();
		FileInputStream inputStream = null;
		try {
			QueryResult queryResult = ChangeHelper2.service.getChangeables(changeRequest2);
			while (queryResult.hasMoreElements()) {
				Object object = queryResult.nextElement();
				if (object instanceof MPMProcessPlan) {
					MPMProcessPlan processPlan = (MPMProcessPlan) object;
					String name = processPlan.getName();
					WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(processPlan);
					String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
							+ String.valueOf(new Date().getTime()) + File.separator;
					String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
					String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
					ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
					File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
					inputStream = new FileInputStream(xmlFile);

					SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
					Element rootElement = xmlUtil.getRootElement();
					if (XMLConstants.technics.equals(rootElement.getName())) {
						for (Element rootAttrElement : (List<Element>) rootElement
								.getChildren(XMLConstants.QMFawTechnicsInfo)) {
							String partOid = rootAttrElement.getAttributeValue("partOid");
							String parentPartOid = rootAttrElement.getAttributeValue("parentPartOid");
							map.put("partOid", partOid);
							map.put("parentPartOid", parentPartOid);
							map.put("documentOid", Util.getStringOid(document));
							if (name.contains(Constants.reworkProcessDocEndwith)) {
								map.put("processPlanType", Constants.reworkProcess);
							} else if (name.contains(Constants.tempProcessDocEndwith)) {
								map.put("processPlanType", Constants.tempProcess);
							} else {
								map.put("processPlanType", Constants.normalProcess);
							}

						}

					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	/**
	 * '工艺更改单审核流程' 获取启动工艺编辑器URL
	 *
	 * @author qianlong
	 * @date 2013-7-24
	 * @param partOid
	 * @param parentPartOid
	 * @param processZipDocOid
	 * @return
	 * @throws WTException
	 */
	public static String getStartProcessEditerURL(String partOid, String parentPartOid, String processZipDocOid)
			throws WTException {
		WTPart currentPart = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
		WTPart wholePart = (WTPart) Util.getObjectByOid(WTPart.class, parentPartOid);
		WTDocument wtDocument = (WTDocument) Util.getObjectByOid(WTDocument.class, processZipDocOid);
		return WorkflowUtil.getStartProcessEditorURL(wholePart, currentPart, wtDocument, "3");
	}

	/**
	 * 临时工艺 获取启动工艺编辑器URL
	 *
	 * @author qianlong
	 * @date 2013-5-23
	 * @param partOid
	 * @param parentPartOid
	 * @return
	 * @throws WTException
	 *
	 */
	public static String getStartTempProcessEditerURL(WTPart currentPart, String parentPartOid, String processZipDocOid)
			throws WTException {
		WTPart wholePart = (WTPart) Util.getObjectByOid(WTPart.class, parentPartOid);
		WTDocument wtDocument = (WTDocument) Util.getObjectByOid(WTDocument.class, processZipDocOid);
		return WorkflowUtil.getStartProcessEditorURL(wholePart, currentPart, wtDocument, "5");
	}

	/**
	 * '工艺更改单审核流程' 变更实例化工艺
	 *
	 * @author qianlong
	 * @date 2013-5-24
	 * @param partOid
	 * @throws WTException
	 *
	 */
	public static void structureChangeProcessPlan(String partOid, String documentOid) throws WTException {
		WTPart wtPart = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
		WTDocument wtDocument = (WTDocument) Util.getObjectByOid(WTDocument.class, documentOid);
		wtDocument = (WTDocument) VersionControlHelper.service.getLatestIteration(wtDocument, true);
		ChangeProcessPlanStructure structure = new ChangeProcessPlanStructure(wtPart, wtDocument);
		structure.structureProcessPlan();
	}

	/**
	 * '工艺更改单审核流程' 工艺包文件非A版本，且必须在拟制状态下， 判断是否需改过工艺
	 *
	 * @author qianlong
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws WTRuntimeException
	 * @date 2013-5-31
	 *
	 */
	public static void checkProcessZipDocImprovedVersion(String partOid) throws WTException, WTRuntimeException,
			WTPropertyVetoException {
		WTPart wtPart = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
		WTDocument document = null;
		List<WTDocument> list = ProcessPlanHelper.getProcessZipDoc(wtPart, null, Constants.normalProcess);
		if (list.size() != 0) {
			document = list.get(0);
		}
		String version = document.getVersionIdentifier().getValue();
		if (Constants.numberVersion.equals(version)) {
			throw new WTException("没有修改过工艺，任务不能完成！");
		} else {
			if (!Constants.INWORK.equals(document.getState().toString())) {
				throw new WTException("没有修改过工艺，任务不能完成！");
			}
		}
	}

	/**
	 * 完成流程活动 设置完成人
	 *
	 * @author qianlong
	 * @date 2013-7-24
	 */
	public static void completeReworkAuditWork(Object object) {
		WorkItem workItem = null;
		SessionServerHelper.manager.setAccessEnforced(false);
		try {
			if (object instanceof ObjectReference) {
				object = ((ObjectReference) object).getObject();
			}
			if (object instanceof WfAssignedActivity) {
				WfAssignedActivity activity = (WfAssignedActivity) object;
				ArrayList<WorkItem> result = WorkflowUtil.getWorkItemFromActivity(activity);
				if (result.size() != 0) {
					workItem = (WorkItem) result.get(0);
				}
			}
			if (workItem != null) {
				WorkflowHelper.service.workComplete(workItem, workItem.getOwnership().getOwner(), null);
			}
		} catch (WfException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(true);
		}
	}

	/**
	 * 在流程活动角色中添加人员
	 *
	 * @author qianlong
	 * @date 2013-4-7
	 * @param self
	 * @param wfRoleName
	 *
	 */
	public static boolean setUserToWfRole(Object self, String wfRoleName, String roleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--wfRoleName--" + wfRoleName + "--roleName--" + roleName);
		boolean flag = true;
		try {
			// 获取当前流程
			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);
			// 把人员添加到流程中的角色中
			Role role = Role.toRole(roleName);
			// 获取当前上下文的 专案团队
			WTContainer container = wf.getContainer();
			ContainerTeam containerTeam = WorkflowUtil.getContainerTeam(container);
			// 获取流程的专案团队
			Team wfTeam = (Team) wf.getTeamId().getObject();
			// 给流程中的专案团队下的角色设置人员
			Role wfRole = Role.toRole(wfRoleName);
			Enumeration<WTPrincipalReference> prinEnumRole = containerTeam.getPrincipalTarget(role);
			if (prinEnumRole == null || prinEnumRole.hasMoreElements() == false) {
				throw new Exception("角色'" + wfRole + "'不存在，或者角色中不存在人员！");
			}
			if (prinEnumRole.hasMoreElements()) {
				WTPrincipal principal = (WTPrincipal) prinEnumRole.nextElement().getObject();
				wfTeam.addPrincipal(wfRole, principal);
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 给'工艺更改单审核流程'中的'通知计划员'添加人员
	 *
	 * @author qianlong
	 * @date 2013-6-7
	 * @param self
	 * @param wfRoleName
	 *
	 */
	public static boolean setGYJHYToWfRole(Object self, String wfRoleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--wfRoleName--" + wfRoleName);
		boolean flag = true;
		try {
			// 获取当前流程
			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);
			WTPrincipalReference principalReference = WTPrincipalUtil.getGONGYIBUJIHUAYUAN(wf.getContainer());
			// 把组长添加到流程中的角色中
			GZCardHelper.setUserToWfRole(principalReference.getPrincipal(), wfRoleName, wf);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 给'工装问题通知单审核流程'中的'专业组长审核'添加人员
	 *
	 * @author
	 * @date 2013-6-6
	 * @return
	 *
	 */
	public static boolean setLeaderToGZPRWf(Object self, String wfRoleName) {
		GLLogger.debug(CLASSNAME, "--self--" + self + "--wfRoleName--" + wfRoleName);
		boolean flag = true;
		try {
			// 获取当前流程
			WfProcess wf = WorkflowUtil.getWfProcessBySelf(self);
			// 获取当前流程创建人的组长所在群组
			WTGroup leaderGroup = GZCardHelper.getLeaderByUser(wf);
			// 把组长添加到流程中的角色中
			GZCardHelper.setUserToWfRole(leaderGroup, wfRoleName, wf);

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 工装审核流程，根据人员获取当前的专业组长
	 *
	 * @author qianlong
	 * @date 2013-6-4
	 * @param wf
	 * @return
	 * @throws WTException
	 *
	 */
	private static WTGroup getLeaderByUser(WfProcess wf, String[] groupNames) throws WTException {
		WTGroup leaderGroup = null;
		WTGroup userGroup = null;

		WTPrincipal principal = (WTPrincipal) wf.getCreator().getObject();

		// 判断创建人员是 专业组下面的哪个组的人员
		for (String str : groupNames) {
			WTGroup group = WTPrincipalUtil.getGroupByName(str);
			if (group.isMember(principal)) {
				userGroup = group;
				break;
			}
		}
		// 当创建者为组长时返回该组
		// 当创建者为成员时返回该成员组的组长
		if (null != userGroup) {
			if (userGroup.getName().contains("组长")) {
				leaderGroup = userGroup;
			} else {
				leaderGroup = WTPrincipalUtil.getGroupByName(userGroup.getName() + "组长");
				if (null == leaderGroup) {
					throw new WTException("'" + userGroup.getName() + "' 没有对应的组长群组！");
				}
			}
		} else {
			throw new WTException("'" + principal.getName() + "' 不是工艺师,或者没有加入到专业组下面的子群组中！");
		}
		return leaderGroup;
	}

	/**
	 * 工艺预览
	 *
	 * @author qianlong
	 * @date 2013-7-23
	 * @param partOid
	 * @param technicName
	 * @param type
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 * @throws WTPropertyVetoException
	 */
	public static String getTechnicPreview(String documentOid) throws WTException, WTRuntimeException,
			WTPropertyVetoException {
		WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, documentOid);
		document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
		return "<a href=\"" + new URLFactory().getBaseHREF() + "app/#netmarkets/jsp/glaway/mpm/technicPreview.jsp?oid="
				+ document.toString() + "\" target=_blank>工艺预览</a>";
	}

	/**
	 * '工艺更改单审核流程' 获取变更原因
	 *
	 * @author qianlong
	 * @date 2013-7-6
	 * @return
	 */
	public static String getChangeReason(WTChangeRequest2 changeRequest2, String processPlanType) {
		String changeReason = "";
		changeReason = IBAHelper.getAnyIBAValueOfObject(changeRequest2, AttributeConstants.changeReason);
		if ("工时更改".equals(changeReason)) {
			changeReason = "工时更改";
		} else {
			if (processPlanType.equals(Constants.reworkProcess)) {
				changeReason = "返工工艺更改";
			} else {
				changeReason = "正常工艺更改";
			}
		}
		System.out.println("changeReason---" + changeReason);
		return changeReason;
	}

	/**
	 * '工艺更改单审核流程' 设置工艺包文件状态的
	 *
	 * @author qianlong
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws WTRuntimeException
	 * @date 2013-5-31
	 *
	 */
	public static void setProcessZipDocLifeCycle(String documnetOid, String stateName) throws WTException,
			WTRuntimeException, WTPropertyVetoException {
		WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, documnetOid);
		document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
		LifeCycleHelper.service.setLifeCycleState(document, State.toState(stateName));
	}

	/**
	 * 设置工装变更产生对象状态
	 *
	 * @author qianlong
	 * @date 2013-7-22
	 * @param ecr
	 * @param state
	 * @return
	 */
	public static void setChangeablesAfterLifeCycle(WTChangeRequest2 ecr, String state) {
		try {
			QueryResult result = QChangeHelper.getChangeablesAfterFromECR(ecr);
			while (result.hasMoreElements()) {
				LifeCycleManaged managed = (LifeCycleManaged) result.nextElement();
				LifeCycleHelper.service.setLifeCycleState(managed, State.toState(state));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 变更单推送erp
	 *
	 * @author qianlong
	 * @date 2013-7-29
	 * @param changeRequest
	 */
	public static void sendExcelToErp(WTChangeRequest2 changeRequest) {
		String filepath = PropertiesUtil.getWTHome() + File.separator + "erpdata" + File.separator;
		try {
			QueryResult result = WTDocumentUtil.getSecondaryByChangeRequest(changeRequest);
			if (result != null && result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				InputStream is = ContentServerHelper.service.findContentStream(data);
				String appFileName = data.getFileName();
				File file = new File(filepath);
				if (!file.exists()) {
					file.mkdirs();
				}
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(new File(filepath + File.separatorChar + appFileName));
					int i = 0;
					byte abyte[] = new byte[8192];
					while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
						fos.write(abyte, 0, i);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (null != is) {
							is.close();
						}
						if (null != fos) {
							fos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	public static void test() throws WTException {
		// improvedProcessZipDocVresion("", "1464206");
		sendExcelToErp((WTChangeRequest2) Util.getObjectByOid(WTChangeRequest2.class, "1475751"));
	}

	public static void main(String[] args) throws RemoteException, InvocationTargetException, WTRuntimeException,
			WTException, WTPropertyVetoException {

		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		methodServer.invoke("test", QChangeHelper.class.getCanonicalName(), null, null, null);

	}
	/**
	 * 撤销修订
	 * @param ecn
	 * @return
	 * @throws WTException
	 * @throws ChangeException2
	 * @throws ClassNotFoundException
	 */
	public static void undoRevise(WTObject pbo)  {

		if(pbo instanceof WTChangeOrder2){
			boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			Class class2;
			try {
				class2 = Class.forName("wt.change2.ChangeRecord2");
				ArrayList list = ChangeHelper.getChangeResultItemRemote(changeOrder2);
				WTChangeActivity2 ca = null;
				List objects = new ArrayList();
				QueryResult ecaqr = ChangeHelper2.service
						.getChangeActivities(changeOrder2);
				if (ecaqr.hasMoreElements()) {
					ca = (WTChangeActivity2) ecaqr.nextElement();
				}
				for(Object o:list){
					if(o instanceof WTDocument){
						WTDocument doc = (WTDocument)o;

						String state = doc.getState().getState().toString();
						//只允许删除正在工作和修改中的对象
		 			    if("INWORK".equals(state)||"REWORK".equals(state)){
		 			    	WTPartDescribeLink wtPartDescribeLink = null;
							//删除所有小版本Link
					        QueryResult qr =  VersionControlHelper.service.iterationsOf(doc);
					        while(qr.hasMoreElements()){
					        	WTDocument iterated = (WTDocument)qr.nextElement();
					        	if (ca != null) {
									ChangeHelper2.service.unattachChangeable(iterated,
											ca, class2,
											ChangeRecord2.CHANGE_ACTIVITY2_ROLE);
								}
					        	//if(version.equals(iterated.getVersionInfo().getIdentifier().getValue())){
				        		List linklist  = (ArrayList) getDocDescribeLinksByDoc(iterated);
					        	for (int i = 0; i < linklist.size(); i++) {
					 	            wtPartDescribeLink = (WTPartDescribeLink) linklist.get(i);
					 	            PersistenceServerHelper.manager.remove(wtPartDescribeLink);
					 	        }
					        	//}
					        }
					        Persistable p = PersistenceHelper.manager.refresh(doc);
					        PersistenceHelper.manager.delete(p);
					        MPMProcessPlan plan2 = MPMProcessPlanUtil.getMPMProcessPlanByNumber(doc.getNumber());
					        MPMProcessPlan plan = (MPMProcessPlan)searchLatestIteratedByNumber(MPMProcessPlan.class,doc.getNumber());
					        if(plan2!=null){
					        	 deleteRelevantChangeActivities(plan2,true);
					        }
		 			    }

					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ChangeException2 e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				SessionServerHelper.manager.setAccessEnforced(flag);
			}

		}
		//throw new WTException("test");

	}
	public static Iterated searchLatestIteratedByNumber(Class klass, String number) {
	       // boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
	        try {
	            QuerySpec qs = new QuerySpec(klass);
	            qs.appendWhere(new SearchCondition(klass, "master>number", "=", number), new int[1]);
	            qs.appendAnd();

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
	           // SessionServerHelper.manager.setAccessEnforced(accessFlag);
	        }

	        return null;
	    }
	 /**
     * 获取文档的相关部件
     *
     * @param doc
     * @return List<WTPartDescribeLink> 相关部件的link的集合
     * @throws WTException
     */
    public  static List getDocDescribeLinksByDoc(WTDocument doc) throws WTException {
        List list = new ArrayList();
        QuerySpec qSpec = new QuerySpec(WTPartDescribeLink.class);
        int[] index = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(doc).getId();
        SearchCondition sCondition = new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL,
                longId);
        qSpec.appendWhere(sCondition, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        WTPartDescribeLink link = null;
        while (qResult.hasMoreElements()) {
            link = (WTPartDescribeLink) qResult.nextElement();
            list.add(link);
        }
        return list;
    }
    public static void deleteRelevantChangeActivities(Changeable2 changeable, boolean isDeleteECA)
    	    throws WTException
    	  {
    	    if (changeable == null) {
    	      return;
    	    }

    	    QueryResult ecaList = ChangeHelper2.service.getAffectingChangeActivities(changeable, false);
    	    if ((ecaList == null) || (ecaList.size() == 0)) {
    	      return;
    	    }
    	    while (ecaList.hasMoreElements())
    	    {
    	      AffectedActivityData rs = (AffectedActivityData)ecaList.nextElement();
    	      if (isDeleteECA) {
    	        WTChangeActivity2 eca = (WTChangeActivity2)rs.getChangeActivity2();
    	        eca = (WTChangeActivity2)ChangeHelper2.service.deleteChangeActivity(eca);
    	      } else {
    	        PersistenceServerHelper.manager.remove(rs);
    	      }
    	    }
    	  }


	public static synchronized String creatWtChangeOrder2PDF(WTObject pbo) throws WTException, IOException, PropertyVetoException {
		if (pbo instanceof WTChangeOrder2) {
			Transaction tx = new Transaction();
            tx.start();
			WTChangeOrder2 changeOrder = (WTChangeOrder2) pbo;
			String filePath = Util.getTempPath() + File.separatorChar + "changePdf" + File.separatorChar + changeOrder.getNumber();
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			PDFPreviewFactory.previewForEcn(changeOrder, filePath, false);

			ContentHolder holder = (ContentHolder) ContentHelper.service.getContents((ContentHolder)changeOrder);
			Vector vector = ContentHelper.getContentList(holder);
            if (vector != null) {
                for (int i = 0; i < vector.size(); i++) {
                    ContentItem contentitem1 = (ContentItem) vector
                            .elementAt(i);
                    if (contentitem1 instanceof ApplicationData) {
                        ApplicationData data = (ApplicationData) contentitem1;
                        if (data.getFileName().equals("PDFPreview.pdf")) {
                            ContentServerHelper.service.deleteContent(holder,
                                    contentitem1);
                            PersistenceHelper.manager.refresh(holder);
                            break;
                        }
                    }
                }
            }
			String targeFilePath = filePath + File.separatorChar + "PDFPreview.pdf";
			InputStream is = new FileInputStream(new File(targeFilePath));
            ApplicationData data = ApplicationData.newApplicationData(holder);
            data.setRole(ContentRoleType.SECONDARY);
            data.setFileName("PDFPreview.pdf");
            ContentServerHelper.service.updateContent(holder, data, is);
            tx.commit();
            tx = null;
            PersistenceHelper.manager.refresh(holder);
            PersistenceHelper.manager.refresh(pbo);
			if (null != is) {
				is.close();
			}
//		    FileUtil.deleteFile(file);
			return targeFilePath;
		}
		return "";
	}


	public static void zuoFeiChangeObject(WTObject pbo) throws LifeCycleException, WTException {

		WTDocument afterDoc = null;
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			String ecnType = IBAHelper.getIBAValue(changeOrder2, "ECNTYPE");
			if ("作废更改".equals(ecnType)) {
				List cas = CSCChange.getReleatedCA(changeOrder2, false);
				for (int j = 0; j < cas.size(); j++) {
					WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
					ArrayList<WTObject> afters = CSCChange.getCAResultItem(ca);
					for (int i = 0; i < afters.size(); i++) {
						WTObject after = afters.get(i);
						if (after instanceof WTDocument) {
							afterDoc = (WTDocument) after;
						}
					}
				}
				if (afterDoc != null) {
					State state = State.toState("OBSOLESCENCE");
					LifeCycleHelper.service.setLifeCycleState(afterDoc, state);
				}
			}
		}
	}

	public static String isHasAfterDate(WTObject pbo) throws WTException {
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			String ecnType = IBAHelper.getIBAValue(changeOrder2, "ECNTYPE");
			if ("新增更改".equals(ecnType)) {
				List cas = CSCChange.getReleatedCA(changeOrder2, false);
				for (int j = 0; j < cas.size(); j++) {
					WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
					ArrayList<WTObject> afters = CSCChange.getCAResultItem(ca);
					for (int i = 0; i < afters.size(); i++) {
						WTObject after = afters.get(i);
						if (after instanceof WTDocument) {
							return "";
						}
					}
				}
				return "新增更改,必须编写新增工艺!";
			}
			if ("正常更改".equals(ecnType)) {
				List resultDatas = ChangeHelper.getChangeResultItemRemote(changeOrder2);
				for(Object o :resultDatas){
					if(o instanceof WTDocument){
						WTDocument afterDoc = (WTDocument) o;
						if (afterDoc != null) {
							String docInfo = afterDoc.getNumber() + "_" + afterDoc.getVersionIdentifier().getValue() + "."
									+ afterDoc.getIterationIdentifier().getValue();
							if (afterDoc.getIterationIdentifier().getValue().equals("1")) {
								return "工艺未修改,请修改工艺后再完成任务!";
							}
							if (afterDoc instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) afterDoc)) {
								return docInfo + "被检出,请检入之后再做此操作!";
							}
						}
					}
				}

				return "";
			}
		}
		return "";
	}


	public static String setWorkFlowParam(WTObject pbo) throws WTException {
		String resultValue = "";
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			//String ecnType = IBAHelper.getIBAValue(changeOrder2, "ECNTYPE");
			//if ("新增更改".equals(ecnType)) {
		           QueryResult afters = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
		            while (afters.hasMoreElements()) {
		            	Object object = afters.nextElement();
		            	if(object instanceof WTDocument){
		            		WTDocument newDoc = (WTDocument)object;
		            		String technicsOid = Util.getStringOid(newDoc);
		            		WTPart part = WCUtil.getRelatedWTPartByDoc(newDoc);
		            		String partOid = Util.getStringOid(part);
		            		resultValue =  partOid+"@"+technicsOid;
		            		System.out.println("resultValue====="+resultValue);
		            	}
		            }
			//}
		}
		return resultValue;
	}


	public static void removePartAndDocLink(WTObject pbo) throws LifeCycleException, WTException {

		WTDocument afterDoc = null;
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			String ecnType = IBAHelper.getIBAValue(changeOrder2, "ECNTYPE");
			if ("作废更改".equals(ecnType)) {
				List cas = CSCChange.getReleatedCA(changeOrder2, false);
				for (int j = 0; j < cas.size(); j++) {
					WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
					ArrayList<WTObject> afters = CSCChange.getCAResultItem(ca);
					for (int i = 0; i < afters.size(); i++) {
						WTObject after = afters.get(i);
						if (after instanceof WTDocument) {
							afterDoc = (WTDocument) after;
						}
					}
				}
				PurgeDataProcessor.deleteLinkD2P(afterDoc);
			}
		}
	}

	public static void improvedDocumentZipDocVersion(WTObject pbo) throws WTException {
		//等五秒再查询 防止还未创建好更改单
		try {
			Thread.sleep(5000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		if(pbo == null) {
			return;
		}
		System.out.println(pbo.toString());
		if(pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			WTUser creator = (WTUser) changeOrder2.getCreator().getObject();
			QueryResult afters = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			while(afters.hasMoreElements()) {
				Object object = afters.nextElement();
				if(object instanceof WTDocument) {
					return;
				}
			}

			QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			if(qResult.hasMoreElements()) {
				Object object = qResult.nextElement();
				if(object instanceof WTDocument) {
					WTDocument doc = (WTDocument) object;
					improvedDocVresion(doc, creator, changeOrder2);
					changeChangeType(changeOrder2, doc.getNumber());
				}
			} else {
				//20231117 由于用户创建有问题更改单  导致方法死循环 暂未找问题更改单创建原因 暂时隐藏循环
				/*try {
					Thread.sleep(5000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				improvedDocumentZipDocVersion(pbo);*/
			}
		}
	}

	public static void improvedDocumentZipDocVersion(WTObject pbo,int retryTime) throws WTException {
		//等五秒再查询 防止还未创建好更改单
		try {
			Thread.sleep(5000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		if(pbo == null) {
			return;
		}
		System.out.println(pbo.toString());
		if(pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
			WTUser creator = (WTUser) changeOrder2.getCreator().getObject();
			QueryResult afters = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
			while(afters.hasMoreElements()) {
				Object object = afters.nextElement();
				if(object instanceof WTDocument) {
					return;
				}
			}

			QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			if(qResult.hasMoreElements()) {
				Object object = qResult.nextElement();
				if(object instanceof WTDocument) {
					WTDocument doc = (WTDocument) object;
					improvedDocVresion(doc, creator, changeOrder2);
					changeChangeType(changeOrder2, doc.getNumber());
				}
			} else {
				try {
					Thread.sleep(5000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				retryTime ++;
				if(retryTime<=10){//重试10次
					System.out.println(changeOrder2.getNumber()+"更改单的受影响对象关联还未建立！！");
					improvedDocumentZipDocVersion(pbo);
				}
			}
		}
	}
}
