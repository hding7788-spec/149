package ext.casc.listener;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.print.util.MBAUtil;
import com.glaway.mpm.util.FolderUtil;
import com.ptc.core.security.utils.SecurityLabelsHelper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.integrate.task.SyncTaskUtil;
import ext.casc.number.NumberMgt;
import ext.casc.photo.processors.ImportPhotoTemplateProcessor;
import ext.casc.process.ProcessTaskItem;
import ext.casc.util.DBConn;
import ext.casc.util.IBAUtility;
import ext.casc.util.Tools;
import wt.access.AccessControlServerHelper;
import wt.access.SecurityLabeled;
import wt.access.configuration.SecurityLabelsConfiguration;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeOrder2Master;
import wt.change2.WTChangeOrder2MasterIdentity;
import wt.doc.WTDocument;
import wt.doc.WTDocumentDependencyLink;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.enterprise.RevisionControlled;
import wt.enterprise.SequenceGenerator;
import wt.events.KeyedEvent;
import wt.events.KeyedEventListener;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.work.WorkItem;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.*;

public class ExtStandardListenerService extends StandardManager implements CascService, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static Properties localprops;
	public static boolean VERBOSE = false;
	static SecurityLabelsConfiguration slConfiguration = null;

	private static List<String> secretGroupNames = new ArrayList<String>();
	private static List<String> secretValues = new ArrayList<String>();

	// --- Attribute Section ---

	private static final String CLASSNAME = ExtStandardListenerService.class.getName();
	private KeyedEventListener listener;

	public String getConceptualClassname() {
		return CLASSNAME;
	}

	// ---- Operation Section ---
	public static ExtStandardListenerService newExtStandardListenerService() throws WTException {
		ExtStandardListenerService instance = new ExtStandardListenerService();
		instance.initialize();
		return instance;
	}

	/*
	 * Internal Listener class
	 */
	class AsesEventListener extends ServiceEventListenerAdapter {

		public AsesEventListener(String manager_name) {
			super(manager_name);
		}

		/**
		 * Listening for event - POST_STORE
		 *
		 * @seenotifyVetoableEvent
		 * @param Object
		 *            event The event for which service to be created
		 * @throws Exception
		 **/
		public void notifyVetoableEvent(Object event) throws Exception {
			if (!(event instanceof KeyedEvent)) {
				return;
			}
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			KeyedEvent keyedEvent = (KeyedEvent) event;
			Object target = keyedEvent.getEventTarget();
			//System.out.println(target);
			//System.out.println(keyedEvent.getEventType());

			if (keyedEvent.getEventType().equals(PersistenceManagerEvent.POST_STORE) || keyedEvent.getEventType().equals(PersistenceManagerEvent.POST_MODIFY)) {
				setSecret(target);
			}

			if (keyedEvent.getEventType().equals(PersistenceManagerEvent.POST_STORE) || keyedEvent.getEventType().equals(PersistenceManagerEvent.POST_MODIFY)) {
			if (target instanceof WTDocument) {
				if (keyedEvent.getEventType().equals(PersistenceManagerEvent.POST_STORE)) {
					WTDocument doc = (WTDocument) target;
					String number = doc.getNumber();
					String partNumberString = "";
					String partNameString = "";
					String batchString = "";
					String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
					if (docType.endsWith("casc.sast.149.PROCESS_NOTICE")) {
						if (!number.startsWith("YJRZ") && !number.startsWith("YPRZ")) {
							WTContainer container = doc.getContainer();
							String bumenLeibieString = "";
							String xhlxString = "";
							Map<String, String> map = getDescriptionByGroup();
							// String number = changeOrder2Master.getNumber();
							WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
							Enumeration groups = currentUser.parentGroups(false);
							while (groups.hasMoreElements()) {
								WTPrincipalReference principalRef = (WTPrincipalReference) groups.nextElement();
								WTGroup group = (WTGroup) principalRef.getPrincipal();
								// WTGroup group = (WTGroup)
								// groups.nextElement();
								String description = group.getDescription();
								if (!"".equals(description) && !"null".equals(description) && description != null) {
									if (map.containsKey(description)) {
										bumenLeibieString = map.get(description);
										break;
									}
								}
							}
							if ("".equals(bumenLeibieString)) {
								throw new WTException("您不在相应的部门车间内，无法获取到部门编码，请联系管理员维护您的账号到对应的部门组里。必须放在描述为<部门_>的组内。");
							}
							com.glaway.mpm.util.IBAHelper docIBA = new com.glaway.mpm.util.IBAHelper((IBAHolder) doc);
							String EFFDATE = docIBA.getIBAValue("EFFDATE");
							String EFFDATE2 = docIBA.getIBAValue("EFFDATE2");
							if((EFFDATE==null||"".equals(EFFDATE))&&(EFFDATE2==null||"".equals(EFFDATE2))){
								throw new WTException("两个有效期限必填其一！");
							}
							com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) container);
							xhlxString = ibaHelper.getIBAValue("XHLX");
							if ("运载型号".equals(xhlxString)) {
								xhlxString = "1";
							} else if ("飞船型号".equals(xhlxString)) {
								xhlxString = "2";
							} else if ("战术型号".equals(xhlxString)) {
								xhlxString = "3";
							} else if ("其他".equals(xhlxString)) {
								xhlxString = "4";
							} else {
								xhlxString = "";
							}
							String numberString = "";
							if ("".equals(xhlxString)) {
								long gengaiNumber = getGengaiNumber(1, "YPrz273");
								numberString = "YPrz" + "273" + "-" + Long.toString(gengaiNumber);
							} else {
								long gengaiNumber = getGengaiNumber(1, "YPrz" + bumenLeibieString + xhlxString);
								numberString = "YPrz" + bumenLeibieString + xhlxString + "-" + Long.toString(gengaiNumber);
							}
							WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
							WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
							idy.setNumber(numberString);
							master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);

							//设置工艺技术通知单关联工艺文件
							String processDocNum = docIBA.getIBAValue("PROCESSDOCNUM");
							if(!Tools.isNull(processDocNum)){
								String[] docIds = processDocNum.split(";");
								for(String docId : docIds) {
									if(!Tools.isNull(docId)){
										WTDocument document = (WTDocument) com.glaway.mpm.util.ReferenceFactory.getObjectbyOid(docId);
										if(document != null) {
											WTDocumentDependencyLink link = WTDocumentDependencyLink.newWTDocumentDependencyLink(doc, document);
											PersistenceServerHelper.manager.insert(link);
										}
									}
								}
							}
						}
					} else if (docType.endsWith("casc.sast.149.GONGYIZONGFANGAN")) {
						String xhlxString = "";
						String numberString = "";
						if (!number.startsWith("RZ/")) {
							WTContainer container = doc.getContainer();
							com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) container);
							xhlxString = ibaHelper.getIBAValue("XHJH");
							if (!"".equals(xhlxString) && xhlxString != null && !"null".equals(xhlxString)) {
								long gengaiNumber = getGengaiNumber(1, "GONGYIZONGFANGAN");
								numberString = "RZ/" + xhlxString + "-" + "FA" + "-" + Long.toString(gengaiNumber);
								WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
								WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
								idy.setNumber(numberString);
								master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);

							} else {
								throw new WTException("该产品下的型号简号属性为空,无法创建工艺总方案。请联系管理员！");
							}


						}
					} else if (docType.endsWith("casc.sast.149.GONGYIFENFANGAN")) {
						String xhlxString = "";
						String numberString = "";
						//String pindex="";
						if (!number.startsWith("RZ/")) {
							WTContainer container = doc.getContainer();
							com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) container);
							xhlxString = ibaHelper.getIBAValue("XHJH");
							if (!"".equals(xhlxString) && xhlxString != null && !"null".equals(xhlxString)) {
								long gengaiNumber = getGengaiNumber(1, "GONGYIZONGFANGAN");
								numberString = "RZ/" + xhlxString + "-" + "FAF" + "-" + Long.toString(gengaiNumber);
								WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
								WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
								idy.setNumber(numberString);
								master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
							} else {
								throw new WTException("该产品下的型号简号属性为空,无法创建工艺总方案。请联系管理员！");
							}

						}

					}else if (docType.endsWith("casc.sast.149.TECHNOLOGY_AGREEMENT")) {
						if (!number.startsWith("RZ/")) {
							String mindex="";
							WTContainer container = doc.getContainer();
							com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) container);
							mindex = ibaHelper.getIBAValue("XHJH");
							if (!"".equals(mindex)&&!"null".equals(mindex)&&mindex!=null) {
								String jsxyNumber="RZ/"+mindex+"-JX-";
								jsxyNumber =jsxyNumber+getJsxyNumber(2, jsxyNumber);
								WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
								WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
								idy.setNumber(jsxyNumber);
								master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);

								String pindex = ext.casc.util.IBAHelper.getIBAStringValue(doc, "PINDEX");
								if(pindex!=null&&pindex.contains("/")){
									throw new WTException("产品代号属性不允许有'/'，请替换为'_'!");
								}
							}else{
								throw new WTException("该产品下的产品代号为空,无法创建技术协议。请联系管理员！");
							}
						}

					}else if(docType.endsWith("casc.sast.149.GONGYIFENXICEHUAZONGJIE")){//工艺分析策划总结
						if (!number.startsWith("RZ/")) {
							StringBuffer numberBuffer = new StringBuffer();
							numberBuffer.append("Rz/");
							String mindex="";
							WTContainer container = doc.getContainer();
							com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) container);
							mindex = ibaHelper.getIBAValue("XHJH");
							if (!"".equals(mindex)&&!"null".equals(mindex)&&mindex!=null) {
								numberBuffer.append(mindex);
								numberBuffer.append("-JBGY-");
								String seq=SequenceGenerator.generateValue("GONGYIFENXICEHUAZONGJIE_SEQ");
								int i=5;
								if (seq.length() < i) {
									StringBuffer fillChar = new StringBuffer(i-seq.length());
									for (int j = 0; j< i - seq.length(); j++)
										fillChar.append("0");
									numberBuffer.append(fillChar.toString());
								}
								numberBuffer.append(seq);
								WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
								WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
								idy.setNumber(numberBuffer.toString());
								master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
							}else{
								throw new WTException("该产品下的产品代号为空,无法创建技术协议。请联系管理员！");
							}
						}
					}else if(docType.endsWith("casc.sast.149.GONGYIJIANDING")){//工艺鉴定
						if (!number.startsWith("RZ/")) {
							StringBuffer numberBuffer = new StringBuffer();
							numberBuffer.append("Rz/");
							String mindex="";
							WTContainer container = doc.getContainer();
							com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) container);
							mindex = ibaHelper.getIBAValue("XHJH");
							if (!"".equals(mindex)&&!"null".equals(mindex)&&mindex!=null) {
								numberBuffer.append(mindex);
								numberBuffer.append("-JBJD-");
								String seq=SequenceGenerator.generateValue("GONGYIJIANDING_SEQ");
								int i=5;
								if (seq.length() < i) {
									StringBuffer fillChar = new StringBuffer(i-seq.length());
									for (int j = 0; j< i - seq.length(); j++)
										fillChar.append("0");
									numberBuffer.append(fillChar.toString());
								}
								numberBuffer.append(seq);
								WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
								WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
								idy.setNumber(numberBuffer.toString());
								master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
							}else{
								throw new WTException("该产品下的产品代号为空,无法创建技术协议。请联系管理员！");
							}
						}
					}else if(docType.endsWith("casc.sast.149.GONGYIDINGXING")){//工艺定型
						if (!number.startsWith("RZ/")) {
							StringBuffer numberBuffer = new StringBuffer();
							numberBuffer.append("Rz/");
							String mindex="";
							WTContainer container = doc.getContainer();
							com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) container);
							mindex = ibaHelper.getIBAValue("XHJH");
							if (!"".equals(mindex)&&!"null".equals(mindex)&&mindex!=null) {
								numberBuffer.append(mindex);
								numberBuffer.append("-JBDX-");
								String seq=SequenceGenerator.generateValue("GONGYIDINGXING_SEQ");
								int i=5;
								if (seq.length() < i) {
									StringBuffer fillChar = new StringBuffer(i-seq.length());
									for (int j = 0; j< i - seq.length(); j++)
										fillChar.append("0");
									numberBuffer.append(fillChar.toString());
								}
								numberBuffer.append(seq);
								WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
								WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
								idy.setNumber(numberBuffer.toString());
								master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
							}else{
								throw new WTException("该产品下的产品代号为空,无法创建技术协议。请联系管理员！");
							}
						}
					}else if(docType.endsWith("casc.sast.149.JISHUKETI")){//技术课题
						if (!number.startsWith("RZ/")) {
							StringBuffer numberBuffer = new StringBuffer();
							numberBuffer.append("Rz/KT-JB-");
							String seq = SequenceGenerator.generateValue("JISHUKETI_SEQ");
							int i = 5;
							if (seq.length() < i) {
								StringBuffer fillChar = new StringBuffer(i - seq.length());
								for (int j = 0; j < i - seq.length(); j++)
									fillChar.append("0");
								numberBuffer.append(fillChar.toString());
							}
							numberBuffer.append(seq);
							WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
							WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
							idy.setNumber(numberBuffer.toString());
							master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
						}
					}else if(docType.endsWith("casc.sast.149.TEST_REPORT")){//综合测试报告
						if (!number.startsWith("RZ/")) {
							StringBuffer numberBuffer = new StringBuffer();
							numberBuffer.append("Rz/");
							String mindex="";
							WTContainer container = doc.getContainer();
							com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) container);
							mindex = ibaHelper.getIBAValue("XHJH");
							if (!"".equals(mindex)&&!"null".equals(mindex)&&mindex!=null) {
								numberBuffer.append(mindex);
								//PDM-TODO:编号规则需确定
								numberBuffer.append("-JBZC-");
								String seq=SequenceGenerator.generateValue("TEST_REPORT_SEQ");
								int i=5;
								if (seq.length() < i) {
									StringBuffer fillChar = new StringBuffer(i-seq.length());
									for (int j = 0; j< i - seq.length(); j++)
										fillChar.append("0");
									numberBuffer.append(fillChar.toString());
								}
								numberBuffer.append(seq);
								WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
								WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
								idy.setNumber(numberBuffer.toString());
								master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
							}else{
								throw new WTException("该产品下的产品代号为空,无法创建技术协议。请联系管理员！");
							}
						}
					}else if(docType.endsWith("casc.sast.149.PhotoTemplate")){
						if (!number.startsWith("ZPYZ")) {
							String zpyz = "ZPYZ"+ImportPhotoTemplateProcessor.getZPYZSeqNumber("ZPYZ");
							WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
							WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
							idy.setNumber(zpyz);
							master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);


						}
						String PDCJMC = (String)MBAUtil.getValue(doc, "PDCJMC");
						if(PDCJMC!=null &&!"".equals(PDCJMC)){
							Map<String, Object> map = new HashMap<String, Object>();
							map.put( "PDCJBH", Constants.photoValueMap.get(PDCJMC));
							map.put( "PDCJMC", PDCJMC);
							MBAUtil.setValue(doc,map);

						}

						if(!WorkInProgressHelper.isWorkingCopy(doc) &&!WorkInProgressHelper.isCheckedOut(doc)) {
							String photoType = (String)MBAUtil.getValue(doc, "photoType");
							if(photoType!=null &&!"".equals(photoType)){
								String docFolder = "/Default/照片样张库/"+photoType.replaceAll("/","、");
								String folderPath = doc.getFolderPath() ;
								if(docFolder!=null &&!folderPath.equals(docFolder)){
									Folder folder = FolderUtil.getFolder(docFolder, WTContainerRef.newWTContainerRef(doc.getContainer()));
									if (folder == null) {
										try {
											folder = FolderHelper.service.saveFolderPath(docFolder,  WTContainerRef.newWTContainerRef(doc.getContainer()));
										} catch (Exception e) {
											// TODO: handle exception
											folder = null;
										}
									}
									FolderHelper.service.changeFolder(doc, folder);
								}
							}

						}


					}
				}else if(keyedEvent.getEventType().equals(PersistenceManagerEvent.POST_MODIFY)){

					WTDocument doc = (WTDocument) target;
					String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);

					if(docType.endsWith("casc.sast.149.PhotoTemplate")){

						if (!doc.getNumber().startsWith("ZPYZ")) {
							String zpyz ="ZPYZ"+ ImportPhotoTemplateProcessor.getZPYZSeqNumber("ZPYZ");
							WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
							WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
							idy.setNumber(zpyz);
							master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
						}
						String PDCJMC = (String)MBAUtil.getValue(doc, "PDCJMC");
						if(PDCJMC!=null &&!"".equals(PDCJMC)){
							Map<String, Object> map = new HashMap<String, Object>();
							map.put( "PDCJBH", Constants.photoValueMap.get(PDCJMC));
							map.put( "PDCJMC", PDCJMC);
							MBAUtil.setValue(doc,map);
						}
						if(!WorkInProgressHelper.isWorkingCopy(doc) &&!WorkInProgressHelper.isCheckedOut(doc)) {
							String photoType = (String)MBAUtil.getValue(doc, "photoType");
							if(photoType!=null &&!"".equals(photoType)){

								String docFolder = "/Default/照片样张库/"+photoType.replaceAll("/","、");
								String folderPath = doc.getFolderPath() ;
								if(docFolder!=null &&!folderPath.equals(docFolder)){
									Folder folder = FolderUtil.getFolder(docFolder, WTContainerRef.newWTContainerRef(doc.getContainer()));
									if (folder == null) {
										try {
											folder = FolderHelper.service.saveFolderPath(docFolder,  WTContainerRef.newWTContainerRef(doc.getContainer()));
										} catch (Exception e) {
											// TODO: handle exception
											folder = null;
										}
									}
									FolderHelper.service.changeFolder(doc, folder);
								}
							}

						}

					}
				}

			} else if (target instanceof WTChangeOrder2) {
				WTChangeOrder2 changeOrder2 = (WTChangeOrder2) target;
				com.glaway.mpm.util.IBAHelper ibaHelper = new com.glaway.mpm.util.IBAHelper((IBAHolder) changeOrder2);
				if (keyedEvent.getEventType().equals(PersistenceManagerEvent.POST_STORE)) {
					String changeOrder2Type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(changeOrder2);
					if (changeOrder2Type.endsWith("casc.sast.149.PROCESS_ECN") || changeOrder2Type.endsWith("casc.sast.149.Process_reportTechnics_ECN")
							|| changeOrder2Type.endsWith("casc.sast.149.DOCUMENT_ECN")) {
						String numberString = "";
						WTContainer container = changeOrder2.getContainer();
						String containerName = container.getName();
						String bumenLeibieString = "";
						String xhlxString = "";
						Map<String, String> map = getDescriptionByGroup();
						// String number = changeOrder2Master.getNumber();
						WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
						Enumeration groups = currentUser.parentGroups(false);
						while (groups.hasMoreElements()) {
							WTPrincipalReference principalRef = (WTPrincipalReference) groups.nextElement();
							WTGroup group = (WTGroup) principalRef.getPrincipal();
							// WTGroup group = (WTGroup) groups.nextElement();
							String description = group.getDescription();
							if (!"".equals(description) && !"null".equals(description) && description != null) {
								if (map.containsKey(description)) {
									bumenLeibieString = map.get(description);
									break;
								}
							}
						}
						if ("".equals(bumenLeibieString)) {
							bumenLeibieString = "67";
							//throw new WTException("您不在相应的部门车间内，无法获取到部门编码，请联系管理员维护您的账号到对应的部门组里。必须放在描述为<部门_>的组内。");
						}
						com.glaway.mpm.util.IBAHelper ibaHelper2 = new com.glaway.mpm.util.IBAHelper((IBAHolder) container);
						xhlxString = ibaHelper2.getIBAValue("XHLX");

						if ("运载型号".equals(xhlxString)) {
							xhlxString = "1";
						} else if ("飞船型号".equals(xhlxString)) {
							xhlxString = "2";
						} else if ("战术型号".equals(xhlxString)) {
							xhlxString = "3";
						} else if ("其他".equals(xhlxString)) {
							xhlxString = "4";
						} else if (container instanceof WTLibrary) {
							String stationNo = ibaHelper.getIBAValue("ISSOP");
							if("true".equals(stationNo)){
								xhlxString = "9";
							}else{
								xhlxString = "0";
							}
						}else {
							xhlxString = "";
						}
						if ("".equals(xhlxString)) {
							long gengaiNumber = getGengaiNumber(1, "YGrz273");
							numberString = "YGrz" + "273" + "-" + Long.toString(gengaiNumber);
						} else {
							long gengaiNumber = getGengaiNumber(1, "YGrz" + bumenLeibieString + xhlxString);
							numberString = "YGrz" + bumenLeibieString + xhlxString + "-" + Long.toString(gengaiNumber);
						}
						WTChangeOrder2Master master = (WTChangeOrder2Master) changeOrder2.getMaster();
						WTChangeOrder2MasterIdentity idy = (WTChangeOrder2MasterIdentity) master.getIdentificationObject();
						idy.setNumber(numberString);
						master = (WTChangeOrder2Master) IdentityHelper.service.changeIdentity(master, idy);
					}
				}

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

				IBAUtility iba = new IBAUtility((IBAHolder) changeOrder2);
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
				}
			}
			}


			if(target instanceof ProcessTaskItem) {
				//门户集成
				if(PersistenceManagerEvent.POST_MODIFY.equals(keyedEvent.getEventType())
						|| PersistenceManagerEvent.POST_STORE.equals(keyedEvent.getEventType())) {
					String oid = PersistenceHelper.getObjectIdentifier((Persistable) target).toString();
					SyncTaskUtil.createBpmQueue(oid, keyedEvent.getEventType());
				}
			} else if(target instanceof WorkItem) {
				//门户集成
				if(PersistenceManagerEvent.POST_MODIFY.equals(keyedEvent.getEventType())
						|| PersistenceManagerEvent.PRE_REMOVE.equals(keyedEvent.getEventType())
						|| PersistenceManagerEvent.INSERT.equals(keyedEvent.getEventType())) {
					String oid = PersistenceHelper.getObjectIdentifier((Persistable) target).toString();
					SyncTaskUtil.createBpmQueue(oid,keyedEvent.getEventType());
				}
			}
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

	}

	public static synchronized long getNumber(Integer type, String pre) {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getNumber";
			Class[] types = { Integer.class, String.class };
			Object[] vals = { type, pre };
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				return (Long) rms.invoke(method, NumberMgt.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		StringBuilder isql = new StringBuilder("insert into ");
		StringBuilder usql = new StringBuilder("update ");

		sql.append("GL_GYFA_SEQ ").append("WHERE PRE='").append(pre).append("'");
		isql.append(" GL_GYFA_SEQ (PRE,NUM) values ('").append(pre).append("',");
		usql.append(" GL_GYFA_SEQ set NUM=");

		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 1;
				isql.append(num).append(")");
				conn.executeUpdate(isql.toString());
			} else {
				num = num + 1;
				usql.append(num).append(" WHERE PRE='").append(pre).append("'");
				conn.executeUpdate(usql.toString());
			}
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

		return num;
	}

	public static synchronized long getGengaiNumber(Integer type, String pre) {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getNumber";
			Class[] types = { Integer.class, String.class };
			Object[] vals = { type, pre };
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				return (Long) rms.invoke(method, NumberMgt.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		StringBuilder isql = new StringBuilder("insert into ");
		StringBuilder usql = new StringBuilder("update ");

		sql.append("GL_GYFA_SEQ ").append("WHERE PRE='").append(pre).append("'");
		isql.append(" GL_GYFA_SEQ (PRE,NUM) values ('").append(pre).append("',");
		usql.append(" GL_GYFA_SEQ set NUM=");

		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 10000;
				isql.append(num).append(")");
				conn.executeUpdate(isql.toString());
			} else {
				num = num + 1;
				usql.append(num).append(" WHERE PRE='").append(pre).append("'");
				conn.executeUpdate(usql.toString());
			}
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

		return num;
	}

	protected void performStartupProcess() throws ManagerException {
		listener = new AsesEventListener(this.getConceptualClassname());
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.POST_STORE));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.POST_MODIFY));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.INSERT));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.PRE_REMOVE));
		// getManagerService().addEventListener(listener,PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.PRE_DELETE));
		// getManagerService().addEventListener(listener,PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.POST_DELETE));
		// getManagerService().addEventListener(listener,PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.PRE_MODIFY));
	}

	public static SecurityLabeled setSecurityLabels(SecurityLabeled obj, String securityLabel, String securityValue) {
		try {
			if (RemoteMethodServer.ServerFlag) {
				if (slConfiguration == null)
					slConfiguration = SecurityLabelsConfiguration.getSecurityLabelsConfiguration();
				if (slConfiguration != null)
					AccessControlServerHelper.manager.setSecurityLabel(obj, securityLabel, securityValue, true);
			} else {
				Class[] rmiArgTypes = { SecurityLabeled.class, String.class, String.class };
				Object[] rmiArgs = { obj, securityLabel, securityValue };
				RemoteMethodServer.getDefault().invoke("setSecurityLabels", ExtStandardListenerService.class.getName(), null, rmiArgTypes, rmiArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	protected synchronized void setSecret(Object target) throws WTException, WTPropertyVetoException {
		ReferenceFactory rf = new ReferenceFactory();
		String secretValue = null;
		if (target instanceof Persistable) {

			String pOid = rf.getReferenceString((Persistable) target);
			if (!SecurityLabelsHelper.isSecurityLabelsExposed(NmOid.newNmOid(pOid))) {
				return;
			}
			Persistable p = (Persistable) target;
			secretValue = ext.casc.util.IBAHelper.getIBAStringValue((WTObject) p, "SECRET");
			if (secretValue == null || "".equals(secretValue))
				return;
			if(Constants.miji.get(secretValue) == null || "".equals(Constants.miji.get(secretValue))){
				if(secretValue.contains("秘密")) {
					secretValue = "秘密★10年";
				} else if(secretValue.contains("机密")) {
					secretValue = "机密★20年";
				} else {
					return;
				}
			}
			if (p instanceof RevisionControlled) {
				RevisionControlled rc = (RevisionControlled) p;
				setSecurityLabels(rc, "MIJI", Constants.miji.get(secretValue));
			} else if(p instanceof WTChangeOrder2) {
				WTChangeOrder2 change = (WTChangeOrder2) p;
				setSecurityLabels(change, "MIJI", Constants.miji.get(secretValue));
			}
		}
		/*if (target instanceof wt.iba.value.StringValue) {
			wt.iba.value.StringValue svalue = (wt.iba.value.StringValue)target;
			if(svalue.getDefinitionReference()!=null&&svalue.getIBAHolderReference()!=null){
				String type = ((StringDefinition)svalue.getDefinitionReference().getObject()).getName();
				if("SECRET".equals(type)){
					//String v = svalue.getValue();
					if(svalue.getIBAHolderReference().getObject() instanceof RevisionControlled){
						setSecurityLabels((RevisionControlled)svalue.getIBAHolderReference().getObject(), "MIJI", miji.get(svalue.getValue()));
					}
				}
			}

		}*/

	}

	public static Map<String, String> getDescriptionByGroup() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("部门_一车间", "51");
		map.put("部门_二车间", "52");
		map.put("部门_三车间", "53");
		map.put("部门_四车间", "54");
		map.put("部门_五车间", "55");
		map.put("部门_六车间", "56");
		map.put("部门_七车间", "57");
		map.put("部门_八车间", "58");
		map.put("部门_九车间", "59");
		map.put("部门_十车间", "62");
		map.put("部门_十一车间", "66");
		map.put("部门_十一分厂", "66");

		map.put("部门_运载项目部", "61");
		map.put("部门_武器项目一部", "62");
		map.put("部门_武器项目二部", "62");
		map.put("部门_飞行器项目部", "63");
		map.put("部门_新产品项目部", "64");
		map.put("部门_后勤部", "65");
		return map;
	}

	/**
	 * 创建技术协议的时候自动取号
	 *author：Mchen
	 *date：2016/5/3
	 *
	 * */

	public static String getJsxyNumber(Integer type, String pre) {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getNumber";
			Class[] types = { Integer.class, String.class };
			Object[] vals = { type, pre };
		}
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		StringBuilder isql = new StringBuilder("insert into ");
		StringBuilder usql = new StringBuilder("update ");

		sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
		if (2 == type) {
			isql.append(" GL_BBLGY_SEQ (PRE,NUM) values ('").append(pre).append("',");
			usql.append(" GL_BBLGY_SEQ set NUM=");
		}
		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 10001;
				isql.append(num).append(")");
				conn.executeUpdate(isql.toString());
			} else {
				num = num + 1;
				usql.append(num).append(" WHERE PRE='").append(pre).append("'");
				conn.executeUpdate(usql.toString());
			}
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

		return Long.toString(num);
	}


}
