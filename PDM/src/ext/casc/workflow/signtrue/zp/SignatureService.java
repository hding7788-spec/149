package ext.casc.workflow.signtrue.zp;

import cn.hutool.core.util.StrUtil;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.netmarkets.model.NmException;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.wp.WorkPackage;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.ases.part.ASESHuiqianSignature;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.part.FaCiBomHelper;
import ext.casc.part.SignatureHelper;
import ext.casc.preview.PreviewObject;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.ExcelFileGenerator;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import ext.casc.workflow.CmWorkflowHelper;
import ext.casc.workflow.TaskConfigrationHelper;
import ext.sast.common.fc.CmPersistenceHelper;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import wt.change2.ChangeHelper2;
import wt.change2.ChangeOrder2;
import wt.change2.WTAnalysisActivity;
import wt.change2.WTChangeOrder2;
import wt.content.*;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.*;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.log4j.LogR;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.*;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.*;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Map.Entry;

public class SignatureService {

    public static String PROCESSNAMES = "零部件签审流程,ECN流程,文档签审流程,149签审包工艺会签流程," +
    		"149签审包技术会签流程,149变更签审包工艺会签流程,149变更签审包技术会签流程,149变更申请包工艺会签流程,149变更申请包技术会签流程";
    private static final long serialVersionUID = -7475066888632154216L;
    private static final Logger log;
    static {
        try {
            log = LogR.getLogger(SignatureService.class.getName());
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static List<String> getZhuZhiStandardSort(){
        List<String> list = new ArrayList<String>();
        for (String string : Constants.ALL_ZHUZHI_BUMEN_VALUE) {
            list.add(string);
        }
        return list;
    }

    public static List<String> getFuZhiStandardSort(){
        List<String> list = new ArrayList<String>();
        for (String string : Constants.ALL_FUZHI_BUMEN_VALUE) {
            list.add(string);
        }
        return list;
    }

    public static Map<String,String> sortZhuZhiChejian(Map<String,String> users) {
    	Map<String,String> result = new LinkedHashMap<String,String>();
        List<String> standardSort = getZhuZhiStandardSort();
        Set<String> set = users.keySet();
        for (String tvalue : standardSort) {
            result.put(tvalue, users.get(tvalue));
        }

        return result;
    }
    public static  Map<String,String>  sortFuZhiChejian(Map<String,String> users) {
    	Map<String,String> result = new LinkedHashMap<String,String>();
        List<String> standardSort = getFuZhiStandardSort();
        Set<String> set = users.keySet();
        for (String tvalue : standardSort) {
            result.put(tvalue, users.get(tvalue));
        }
        return result;
    }
    public static void zpGYZZ(String workItemOid, String wtObjectOids) throws WTRuntimeException,
            WTException,
            WTPropertyVetoException {
        if (wtObjectOids == null || "".equals(wtObjectOids)) {
            return;
        }
        try {
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
            WfActivity activity = (WfActivity) wi.getSource().getObject();
            String wtObjectOidArray[] = wtObjectOids.split(";;;ppp");
            WTPrincipal wtp = SessionHelper.getPrincipal();
            Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");

            WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
            WfProcess process = null;
            if (wfcont instanceof WfBlock) {
                WfBlock wfBlock = (WfBlock) wfcont;
                process = wfBlock.getParentProcess();
            } else {
                process = (WfProcess) wfcont;
            }
            String newVersion = activity.getTripCount() + "";
            WTProperties props = WTProperties.getLocalProperties();
            String tempFolder = props.getProperty("wt.temp");
            String filePath = "";
            String fileName = "";
            String uuid = UUID.randomUUID().toString();
            fileName = "signature_emps.xml";

            String path =  tempFolder + File.separator + uuid;
            File pathFile = new File(path);
            if(!pathFile.exists()){
            	pathFile.mkdirs();
            }
            filePath = tempFolder + File.separator + uuid+File.separator+"signature_emps.xml";
            Element root = DocumentHelper.createElement("root");
            root.addAttribute("version", newVersion);
            Document document = DocumentHelper.createDocument(root);
            Map<String, WTUser> users = new HashMap<String, WTUser>();
            for (int i = 0; i < wtObjectOidArray.length; i++) {
                String signValueArray[] = wtObjectOidArray[i].split(";;;qqq");
                // String advise = signValueArray[1];
                // String select = signValueArray[2];
                String hqPersons = "";
                String finalhqPersons = "";
                if (signValueArray.length > 4) {
                    hqPersons = signValueArray[4];
                }
                String hqPersonsDis = "";
                if (signValueArray.length > 5) {
                    hqPersonsDis = signValueArray[5];
                }
                String zhuzhichejian = "";
                if (signValueArray.length > 6) {
                	zhuzhichejian = signValueArray[6];
                }
                String fuzhichejian = "";
                if(signValueArray.length>7){
                	fuzhichejian = signValueArray[7];
                }
                String tempOid = "";
                if(signValueArray.length>8){
                    tempOid = signValueArray[8];
                }
                if (hqPersons != null && !"".equals(hqPersons)) {
                    String[] hqPerson = hqPersons.split(";");
                    for (String sp : hqPerson) {
                        if (!"".equals(sp.trim()) && sp.contains("-")) {
                            String[] sp2s = sp.split("-");
                            WTUser user = (WTUser) rf.getReference(sp2s[1]).getObject();
                            finalhqPersons = finalhqPersons + sp2s[1] + ";";
                            users.put(sp2s[0], user);
                        }
                    }
                }
                // if (!select.equalsIgnoreCase("无需会签")) {
                tempOid = tempOid.replaceAll(">", ":");
                Element element = root.addElement("EMPHQZZ");
                element.addAttribute("oid", tempOid);
                // element.addAttribute("advise", advise);
                // element.addAttribute("select", select);
                element.addAttribute("approver", finalhqPersons);
                element.addAttribute("zpr", wtp.getPersistInfo().getObjectIdentifier().toString());
                element.addAttribute("approverDisplay", finalhqPersons);
                element.addAttribute("zhuzhichejian", zhuzhichejian);
                element.addAttribute("fuzhichejian", fuzhichejian);
                element.addAttribute("version", newVersion);
                element.addAttribute("reassign", "");
                // }
            }
            File file = new File(filePath);
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file));
            xmlWriter.write(document);
            xmlWriter.close();
            saveTeamRole("ZHIPAIGONGYIYUANZHE", users, process, true);
            genPBOAttachments((ContentHolder) pbo, fileName,filePath);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private static void initZPGYYAttachmentForOld(ContentHolder pbo) {
    	InputStream is = getAttachmentsFromPBO(pbo,ContentRoleType.SECONDARY,"signature_emps2.xml");
    	if(is!=null){
    		try {
                boolean enforce = SessionServerHelper.manager
                        .setAccessEnforced(false);
                ContentHolder holder = ContentHelper.service.getContents(pbo);
                Vector vector = ContentHelper.getContentList(holder);
                if (vector != null) {
                    for (int i = 0; i < vector.size(); i++) {
                        ContentItem contentitem1 = (ContentItem) vector
                                .elementAt(i);
                        if (contentitem1 instanceof ApplicationData) {
                            ApplicationData data = (ApplicationData) contentitem1;
                            if (data.getFileName().equals("signature_emps2_old.xml")||data.getFileName().equals("signature_emps2.xml")) {
                                ContentServerHelper.service.deleteContent(holder,
                                        contentitem1);
                                break;
                            }
                        }

                    }
                }
                Transaction tx = new Transaction();
                tx.start();
                PersistenceHelper.manager.lockAndRefresh(pbo);
                ApplicationData appData = ApplicationData.newApplicationData(pbo);
                appData.setFileName("signature_emps2_old.xml");
                appData.setRole(ContentRoleType.SECONDARY);
                appData.setDescription(String.valueOf(Calendar.getInstance()
                        .getTimeInMillis()));
                appData.setComments("自动生成");
                appData = ContentServerHelper.service.updateContent(pbo, appData,
                		is);
                tx.commit();
                tx = null;
                PersistenceHelper.manager.refresh(pbo);
                SessionServerHelper.manager.setAccessEnforced(enforce);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
        		try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
    	}

	}

	public static synchronized void zpGYY(String workItemOid, String wtObjectOids) throws WTRuntimeException,
            WTException,
            WTPropertyVetoException {
        if (wtObjectOids == null || "".equals(wtObjectOids)) {
            return;
        }
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
            WfActivity activity = (WfActivity) wi.getSource().getObject();
            String srole = wi.getRole().toString();
            activity.getTripCount();
            String wtObjectOidArray[] = wtObjectOids.split("@");
            WTPrincipal wtp = SessionHelper.getPrincipal();
            String zpr = wtp.getPersistInfo().getObjectIdentifier().toString();
            Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");
            WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();

            WfProcess process = null;
            if (wfcont instanceof WfBlock) {
                WfBlock wfBlock = (WfBlock) wfcont;
                process = wfBlock.getParentProcess();
            } else {
                process = (WfProcess) wfcont;
            }
            SignatureRecord record = null;
            WTProperties props = WTProperties.getLocalProperties();
            String tempFolder = props.getProperty("wt.temp");
            String filePath = "";
            String fileName = "";
            String uuid = UUID.randomUUID().toString();
            String path =  tempFolder + File.separator + uuid;
            File pathFile = new File(path);
            if(!pathFile.exists()){
            	pathFile.mkdirs();
            }
            filePath = tempFolder + File.separator + uuid+File.separator +"signature_emps2.xml";
            fileName = "signature_emps2.xml";
            InputStream is = getAttachmentsFromPBO((ContentHolder) pbo,
                    ContentRoleType.SECONDARY, fileName);
            Map<String, SignatureRecord> map = new HashMap<String, SignatureRecord>();
           // String version = "1";
            if (is != null) {
                SignatureGYYXMLParser xmp = new SignatureGYYXMLParser(is);
                map = xmp.getMap();
               // version = xmp.getVersion();
            }

            //清空上次指派的记录
            clearLastRecordByZPR(map,zpr);

           // String newVersion = getMaxTripCount("1");
            Set<WTUser> users = new HashSet<WTUser>();
            for (int i = 0; i < wtObjectOidArray.length; i++) {
                String signValueArray[] = wtObjectOidArray[i].split("~");
                String tempOid = signValueArray[0];
                String hqPersons = signValueArray[1];
                String hqPersonsDis = signValueArray[2];

              //  hqPersons = hqPersons.replaceAll(";", "");

                if (hqPersons != null && !"".equals(hqPersons)) {
                    String[] hqPerson = hqPersons.split(";");
                    for (String sp : hqPerson) {
                        if (!"".equals(sp.trim())) {
                            WTUser user = (WTUser) rf.getReference(sp).getObject();
                            users.add(user);
                        }
                    }
                }


                /*if (hqPersons != null && !"".equals(hqPersons.trim())) {
                    WTUser user = (WTUser) rf.getReference(hqPersons).getObject();
                    users.add(user);
                }*/
                record = new SignatureRecord(tempOid, "", "", hqPersons, hqPersonsDis, zpr, "1", "");
                map.put(tempOid + "_" + zpr, record);
            }
            Set<Entry<String, SignatureRecord>> set = map.entrySet();
            Element root = DocumentHelper.createElement("root");
            root.addAttribute("version", "1");
            Document document = DocumentHelper.createDocument(root);
            for (Entry<String, SignatureRecord> entry : set) {
                entry.getKey();
                record = entry.getValue();
                /*
                 * String approver = record.getPersons();
                 * if (!"".equals(approver)&&record.getVersion().equals(newVersion)) {
                 * approver = approver.replaceAll(";", "");
                 * WTUser user = (WTUser) rf.getReference(approver).getObject();
                 * users.add(user);
                 * }
                 */
                Element element = root.addElement("EMPGYY");
                element.addAttribute("oid", record.getEmpoid());
                // element.addAttribute("advise", record.getAdvise());
                // element.addAttribute("select", record.getResult());
                element.addAttribute("zpr", record.getZpr());
                element.addAttribute("approver", record.getPersons());
                element.addAttribute("approverDisplay", record.getPersonsDis());
                element.addAttribute("version","1");
                element.addAttribute("reassign", "");
            }
            File file = new File(filePath);
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file));
            xmlWriter.write(document);
            xmlWriter.close();
            saveTeamRole("GONGYIHUIQIANZHE", new ArrayList<WTUser>(users),process, true,srole);
            genPBOAttachments((ContentHolder) pbo, fileName,filePath);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    private static void clearLastRecordByApprover(
			Map<String, SignatureRecord> map, String zpr, String hqPersons) {
   	 	Set<Entry<String, SignatureRecord>> set = map.entrySet();
   	 	SignatureRecord record = null;
        for (Entry<String, SignatureRecord> entry : set) {
            record = entry.getValue();
            if(!record.getZpr().equals(zpr)&&record.getPersons().equals(hqPersons)){
            	record.setVersion("0");
            }
        }
	}

	private static void clearLastRecordByZPR(Map<String, SignatureRecord> map,String zpr) {
    	Map<String, SignatureRecord>  tempMap = new HashMap<String, SignatureRecord>(map);
    	 Set<Entry<String, SignatureRecord>> set = tempMap.entrySet();
    	 SignatureRecord record = null;
         for (Entry<String, SignatureRecord> entry : set) {
             String key = entry.getKey();
             record = entry.getValue();
             if(record.getZpr().equals(zpr)){
            	 map.remove(key);
             }
         }
	}

	public static void saveTeamRole(String roleName, Map<String, WTUser> users, WfProcess process, boolean clear) {
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        try {
            tx.start();
          //  String processName = process.getName();
            Team team = (Team) process.getTeamId().getObject();
           // Persistable pbo = (Persistable) process.getContext().getValue("primaryBusinessObject");
            HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
            Map<Role,Set<WTUser>> roleUserMap = new HashMap<Role,Set<WTUser>>();
            if (users != null) {
                Iterator<String> iterator = users.keySet().iterator();
                while(iterator.hasNext()){
                    Role role = null;
                    String chejian = iterator.next();
                     for(String s:Constants.allChejian){
                    	 if (s.equals(chejian)) {
                             role = Role.toRole(Constants.allChejianToWorkFlowGYZZRoleMap.get(s));
                             break;
                    	 }
                     }
                    log.debug("*********role:" + role);
                    if (role == null) {
                        continue;
                    }
                    Set<WTUser> list = roleUserMap.get(role);
                    if(list==null){
                    	list = new HashSet<WTUser>();
                    }
                    list.add(users.get(chejian));
                    roleUserMap.put(role, list);
                }
            }

            for(String s:Constants.allChejian){
                  Role  role = Role.toRole(Constants.allChejianToWorkFlowGYZZRoleMap.get(s));
                  List tempUserList = (List) rolePrincipalListMap.get(role);
                  if (clear) {
                      for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                          WTUser user1 = (WTUser) ((WTPrincipalReference) tempUserList.get(i))
                                  .getObject();
                          team.deletePrincipalTarget(role, user1);
                      }
                  }
            }

            Set<Entry<Role, Set<WTUser>>>  setRU = roleUserMap.entrySet();

            for (Iterator iterator = setRU.iterator(); iterator.hasNext();) {
				Entry<Role, Set<WTUser>> entry = (Entry<Role, Set<WTUser>>) iterator
						.next();
				Role roleR = entry.getKey();
				Set<WTUser> userSet = entry.getValue();

				List tempUserList = (List) rolePrincipalListMap.get(roleR);
                if (clear) {
                    for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                        WTUser user1 = (WTUser) ((WTPrincipalReference) tempUserList.get(i))
                                .getObject();
                        team.deletePrincipalTarget(roleR, user1);
                    }
                }
                // 将该用户保存至流程团队角色
                for(WTUser user : userSet){
                	team.addPrincipal(roleR, user );
                }
			}
            team = (Team) PersistenceHelper.manager.refresh(team);
            team = (Team) PersistenceHelper.manager.save(team);

            tx.commit();
            tx = null;
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
                tx = null;
            }
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }

    }

    public static void saveTeamRole(String roleName, List<WTUser> userList, Object self, boolean clear,String srole) {
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        try {
            tx.start();
            WfProcess process = (WfProcess) self;
          //  String processName = process.getName();
            Team team = (Team) process.getTeamId().getObject();
            Persistable pbo = (Persistable) process.getContext().getValue("primaryBusinessObject");
            HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);

            // 获取容器
            WTContainer wtContainer = null;
            if (pbo instanceof WTPart) {
                wtContainer = ((WTPart) pbo).getContainer();
            } else if (pbo instanceof WTDocument) {
                wtContainer = ((WTDocument) pbo).getContainer();
            } else if (pbo instanceof EPMDocument) {
                wtContainer = ((EPMDocument) pbo).getContainer();
            } else if (pbo instanceof WTChangeOrder2) {
                wtContainer = ((WTChangeOrder2) pbo).getContainer();
            } else if (pbo instanceof MPMProcessPlan) {
                wtContainer = ((MPMProcessPlan) pbo).getContainer();
            } else if (pbo instanceof ProcessEnvelope) {
                wtContainer = ((ProcessEnvelope) pbo).getContainer();
            } else if (pbo instanceof ChangePackaged) {
                wtContainer = ((ChangePackaged) pbo).getContainer();
            }
            log.debug("*********wtContainer:" + wtContainer);
            log.debug("*********roleName:" + roleName);
            Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
            Set<String> set = map.keySet();

            // 设置流程角色的参与者

            Map<Role,Set<WTUser>> roleUserMap = new HashMap<Role,Set<WTUser>>();
            if ("GONGYIHUIQIANZHE".equals(roleName)) {// 保存工艺员
//                if (processName.indexOf(Constants.WF_DOCUMENT_APPROVAL) > -1) {
//                    Role role = Role.toRole(roleName);
//                    List tempUserList = (List) rolePrincipalListMap.get(role);
//                    if (clear) {
//                        for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
//                            WTUser user = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
//                            team.deletePrincipalTarget(role, user);
//                        }
//                    }
//                    for (int i = 0; i < userList.size(); i++) {
//                        WTUser user = userList.get(i);
//                        team.addPrincipal(role, user);
//                    }
//                } else {
                    WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
                    log.debug("*********currentUser:" + currentUser.getFullName());
                    for (WTUser user : userList) {
                        // 如果是项目办型号主管，则设置"项目部主任工艺员"流程角色
                        Role role = Role.toRole(Constants.allChejianToWorkFlowGYZZTOGYYRoleMap.get(srole));
                        if (role == null) {
                            continue;
                        }
                        Set<WTUser> list = roleUserMap.get(role);
                        if(list==null){
                        	list = new HashSet<WTUser>();
                        }
                        list.add(user);
                        roleUserMap.put(role, list);
                    }
//                }
            } else if ("ZHIPAIGONGYIYUANZHE".equals(roleName)) {// 保存工艺组长
//                if (processName.indexOf(Constants.WF_DOCUMENT_APPROVAL) > -1) {
//                    Role role = Role.toRole(roleName);
//                    List tempUserList = (List) rolePrincipalListMap.get(role);
//                    if (clear) {
//                        for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
//                            WTUser user = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
//                            team.deletePrincipalTarget(role, user);
//                        }
//                    }
//                    for (int i = 0; i < userList.size(); i++) {
//                        WTUser user = userList.get(i);
//                        team.addPrincipal(role, user);
//                    }
//                } else {
                    for (WTUser user : userList) {
                        // 如果是主任工艺师，则设置"项目部工艺组长"流程角色
                        boolean flag = checkWhetherIsZhuRenGongYiShi(wtContainer, user);
                        log.debug("*********flag:" + flag);
                        if (flag) {
                            Role role = Role.toRole("XIANGMUBUGONGYIZUZHANG");
                            log.debug("*********role:" + role);
                            if (role == null) {
                                continue;
                            }
                            Set<WTUser> list = roleUserMap.get(role);
                            if(list==null){
                            	list = new HashSet<WTUser>();
                            }
                            list.add(user);
                            roleUserMap.put(role, list);
                        }
                        for (String chejian : set) {
                            Role role = null;
                            log.debug("*********chejian:" + chejian);
                            List<WTUser> allUsers = map.get(chejian);
                            if (allUsers.contains(user)) {
                            	for(String s: Constants.allChejianExceptXiangMuBan){
                            		if(s.equals(chejian)){
                            			 role = Role.toRole(Constants.allChejianToWorkFlowGYZZRoleMap.get(chejian));
                             		     break;
                            		}
                            	}

                            }
                            log.debug("*********role:" + role);
                            if (role == null) {
                                continue;
                            }
                            Set<WTUser> list = roleUserMap.get(role);
                            if(list==null){
                            	list = new HashSet<WTUser>();
                            }
                            list.add(user);
                            roleUserMap.put(role, list);
                        }
                    }
//                }
            }
            Set<Entry<Role, Set<WTUser>>>  setRU = roleUserMap.entrySet();

            for (Iterator iterator = setRU.iterator(); iterator.hasNext();) {
				Entry<Role, Set<WTUser>> entry = (Entry<Role, Set<WTUser>>) iterator
						.next();
				Role roleR = entry.getKey();
				Set<WTUser> userSet = entry.getValue();

				List tempUserList = (List) rolePrincipalListMap.get(roleR);
                if (clear) {
                    for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                        WTUser user1 = (WTUser) ((WTPrincipalReference) tempUserList.get(i))
                                .getObject();
                        team.deletePrincipalTarget(roleR, user1);
                    }
                }
                // 将该用户保存至流程团队角色
                for(WTUser user : userSet){
                	team.addPrincipal(roleR, user );
                }
			}

            // added by Leo ,add the role and users to pbo team

            team = (Team) PersistenceHelper.manager.refresh(team);
            team = (Team) PersistenceHelper.manager.save(team);

            tx.commit();
            tx = null;
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
                tx = null;
            }
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    /**
     * 获取该会签对象的可会签人员
     *
     * @param processEnvelope
     * @param object
     * @return
     */
    public static List<String> getHQObjectLinkUsers(Object pbo, Object object) {
        InputStream is = null;
        if (pbo instanceof ProcessEnvelope) {
            is = getAttachmentsFromPBO((ContentHolder) pbo, ContentRoleType.SECONDARY, "signature_emps2.xml");
        } else if (pbo instanceof WTChangeOrder2) {
            is = getAttachmentsFromPBO((ContentHolder) pbo, ContentRoleType.SECONDARY, "signature_emps2.xml");
        }
        List<String> userList = new ArrayList<String>();
        String oid = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
        if (is != null) {
            SignatureGYYXMLParser sxp = new SignatureGYYXMLParser(is);
            String users = sxp.getUsersByObjectOid(oid);
            if (users != null && !"".equals(users)) {
                if (users.contains(";")) {
                    String[] user = users.split(";");
                    for (String u : user) {
                        userList.add(u);
                    }
                } else {
                    userList.add(users);
                }
            }
        }
        return userList;
    }

    //增加发次BOM支持
    public static List getGYHQDealMembers(ProcessEnvelope processEnvelope,
                                          WTUser currentuser, WorkItem wi,ISignatureParser parser) throws WTException, RemoteException {
        try {
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            WfProcess wfProcess = wfAct.getParentProcess();
            if(FaCiBomHelper.FACI_BOM_WORKFLOW.equals(wfProcess.getTemplate().getName()))
            {
                ArrayList list = ProcessEnvelopeUtil.getAllFaCiBom(processEnvelope);
                if(list.size()>0)
                {
                    return list;
                }
            }

            return getGYHQDealMembersImpl(processEnvelope, currentuser, wi, parser);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * 过滤工艺会签处理的数据
     * @throws WTException
     * @throws RemoteException
     */
    @SuppressWarnings("unchecked")
    private static List getGYHQDealMembersImpl(ProcessEnvelope processEnvelope,
            WTUser currentuser, WorkItem wi,ISignatureParser parser) throws WTException, RemoteException {
        // ##begin getAllMembers%4B4FF6C900FEg.body preserve=yes
        String user = "";
        try {
            user = SessionHelper.manager.getPrincipal().getName();
            SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope,
                "theRevisionControlled",
                EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        SessionHelper.manager.setAdministrator();
        ArrayList envelopeMembers = new ArrayList();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        String activityName = wfAct.getName();
        for (; qr.hasMoreElements();) {
            obj = ((EnvelopeMemberLink) qr.nextElement())
                    .getRevisionControlled();
            if (obj != null) {
                if (obj instanceof EPMDocument || obj instanceof WTDocument) {
                	if(obj instanceof WTDocument){
                		WTDocument doc = (WTDocument) obj;
                		String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
            			if(docType.endsWith("casc.sast.149.GONGYIJIANCHABAOGAO")){
            				continue;
            			}
                	}
                    String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
                    if (parser==null||parser.hasPrivilege(currentuser.getPersistInfo()
                            .getObjectIdentifier().toString(), oid, wi)) {
                        envelopeMembers.add(obj);
                    }
                }
            }
        }
        if (!"".equals(user)) {
            try {
                SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return envelopeMembers;
        // ##end getAllMembers%4B4FF6C900FEg.body
    }
    /**
     * 过滤工艺会签处理的数据
     * @throws WTException
     */
    @SuppressWarnings("unchecked")
    public static List getGYHQDealMembers(List list,
            WTUser currentuser, WorkItem wi,ISignatureParser parser) throws WTException {
    	List result = new ArrayList();
    	for(Object obj :list){
    		if (obj instanceof EPMDocument || obj instanceof WTDocument|| obj instanceof WTChangeOrder2|| obj instanceof ChangePackaged || obj instanceof PreviewObject) {
    			if(parser==null||parser.hasPrivilege((Persistable)obj, currentuser, wi)){
    				result.add(obj);
                }
            }
    	}
        return result;
    }
    public static ArrayList getGYZZDealMembers(ChangePackaged packaged,
            WTUser currentuser, WorkItem wi) throws WTException {
        // ##begin getAllMembers%4B4FF6C900FEg.body preserve=yes
        String user = "";
        try {
            user = SessionHelper.manager.getPrincipal().getName();
            SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(packaged,
                "theRevisionControlled",
                ChangePackagedResultLink.class, false);
        RevisionControlled obj = null;
        SessionHelper.manager.setAdministrator();
        ArrayList packagedMembers = new ArrayList();
        InputStream is = getAttachmentsFromPBO(packaged,
                ContentRoleType.SECONDARY, "signature_emps.xml");
        if (is != null) {
            SignatureGYZZXMLParser sxp = new SignatureGYZZXMLParser(is);
            for (; qr.hasMoreElements();) {
                obj = ((ChangePackagedResultLink) qr.nextElement())
                        .getRevisionControlled();
                if (obj != null) {
                    if (obj instanceof EPMDocument || obj instanceof WTDocument) {
                        String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
                        if (sxp.hasPrivilege(currentuser.getPersistInfo()
                                .getObjectIdentifier().toString(), oid, wi)) {
                            packagedMembers.add(obj);
                        }
                    } else {
                        packagedMembers.add(obj);
                    }
                }

            }
        }
        if (!"".equals(user)) {
            try {
                SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return packagedMembers;
        // ##end getAllMembers%4B4FF6C900FEg.body
    }

    /**
     * 过滤工艺员处理的文档列表
     *
     * @param processEnvelope
     * @param string
     * @param newAssignnee
     * @return
     * @throws WTException
     */
    public static ArrayList getGYYDealMembers(ProcessEnvelope processEnvelope,
            WTUser currentuser, WorkItem wi) throws WTException {
        // ##begin getAllMembers%4B4FF6C900FEg.body preserve=yes
        String user = "";
        try {
            user = SessionHelper.manager.getPrincipal().getName();
            SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope,
                "theRevisionControlled",
                EnvelopeMemberLink.class, false);
        RevisionControlled obj = null;
        ArrayList envelopeMembers = new ArrayList();
        ArrayList envelopeMembers2 = new ArrayList();
        InputStream is = getAttachmentsFromPBO(processEnvelope,
                ContentRoleType.SECONDARY, "signature_emps2.xml");
        for (; qr.hasMoreElements(); envelopeMembers.add(obj)) {
            obj = ((EnvelopeMemberLink) qr.nextElement())
                    .getRevisionControlled();
        }
        if (is != null) {
            SignatureGYYXMLParser sxp = new SignatureGYYXMLParser(is);
            for (Object o : envelopeMembers) {
                if (o instanceof EPMDocument || o instanceof WTDocument) {
                    String oid = PersistenceHelper.getObjectIdentifier((Persistable) o).toString();
                    if (sxp.hasPrivilege(currentuser.getPersistInfo()
                            .getObjectIdentifier().toString(), oid, wi)) {
                        envelopeMembers2.add(o);
                    }
                } else {
                    envelopeMembers2.add(o);
                }
            }
        }
        if (!"".equals(user)) {
            try {
                SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return envelopeMembers2;
        // ##end getAllMembers%4B4FF6C900FEg.body
    }

    public static ArrayList getGYYDealMembers(ChangePackaged packaged,
            WTUser currentuser, WorkItem wi) throws WTException {
        // ##begin getAllMembers%4B4FF6C900FEg.body preserve=yes
        String user = "";
        try {
            user = SessionHelper.manager.getPrincipal().getName();
            SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        QueryResult qr = PersistenceHelper.manager.navigate(packaged,
                "theRevisionControlled",
                ChangePackagedResultLink.class, false);
        RevisionControlled obj = null;
        ArrayList packagedMembers = new ArrayList();
        ArrayList packagedMembers2 = new ArrayList();
        InputStream is = getAttachmentsFromPBO(packaged,
                ContentRoleType.SECONDARY, "signature_emps2.xml");
        for (; qr.hasMoreElements(); packagedMembers.add(obj)) {
            obj = ((ChangePackagedResultLink) qr.nextElement())
                    .getRevisionControlled();
        }
        if (is != null) {
            SignatureGYYXMLParser sxp = new SignatureGYYXMLParser(is);
            for (Object o : packagedMembers) {
                if (o instanceof EPMDocument || o instanceof WTDocument) {
                    String oid = PersistenceHelper.getObjectIdentifier((Persistable) o).toString();
                    if (sxp.hasPrivilege(currentuser.getPersistInfo()
                            .getObjectIdentifier().toString(), oid, wi)) {
                        packagedMembers2.add(o);
                    }
                } else {
                    packagedMembers2.add(o);
                }
            }
        }
        if (!"".equals(user)) {
            try {
                SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return packagedMembers2;
    }

    public static InputStream getAttachmentsFromPBO(ContentHolder holder, ContentRoleType role, String fileName) {
        if (holder == null)
            return null;
        try {
            QueryResult qr = ContentHelper.service.getContentsByRole(holder, role);
            while (qr.hasMoreElements()) {
                ApplicationData ap = (ApplicationData) qr.nextElement();
                String name = ap.getFileName();
                if (name.equals(fileName)) {
                    Streamed streamed = (Streamed) ap.getStreamData().getObject();
                    InputStream ips = streamed.retrieveStream();
                    return ips;
                }
            }
        } catch (Exception e) {
            // CSCDebug.outDebugInfo(e.getLocalizedMessage());
        }

        return null;
    }

    @SuppressWarnings("rawtypes")
    public static void genPBOAttachments(ContentHolder pbo, String fileName,String filePath) {
        try {
            boolean enforce = SessionServerHelper.manager
                    .setAccessEnforced(false);
            ContentHolder holder = ContentHelper.service.getContents(pbo);
            Vector vector = ContentHelper.getContentList(holder);
            if (vector != null) {
                for (int i = 0; i < vector.size(); i++) {
                    ContentItem contentitem1 = (ContentItem) vector
                            .elementAt(i);
                    if (contentitem1 instanceof ApplicationData) {
                        ApplicationData data = (ApplicationData) contentitem1;
                        if (data.getFileName().equals(fileName)) {
                            ContentServerHelper.service.deleteContent(holder,
                                    contentitem1);
                            break;
                        }
                    }

                }
            }
            Transaction tx = new Transaction();
            tx.start();
            PersistenceHelper.manager.lockAndRefresh(pbo);
            ApplicationData appData = ApplicationData.newApplicationData(pbo);
            appData.setFileName(fileName);
            appData.setRole(ContentRoleType.SECONDARY);
            appData.setDescription(String.valueOf(Calendar.getInstance()
                    .getTimeInMillis()));
            appData.setComments("自动生成");
            appData = ContentServerHelper.service.updateContent(pbo, appData,
                    filePath);
            tx.commit();
            tx = null;
            PersistenceHelper.manager.refresh(pbo);
            SessionServerHelper.manager.setAccessEnforced(enforce);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 通过当前用户（工艺组长），得到该用户作为该车间下的所有工艺员
     *
     * @param currentuser
     * @return key:user full name,value:useroid
     */
    public static Map<String, String> getUsersByCheJianGroup(WTUser currentuser) {
        ReferenceFactory rf = new ReferenceFactory();
        Map<String, String> result = new HashMap<String, String>();
        try {
            Map<String, List<WTUser>> gyzzs = ProcessUtil.getGroupAndUsersInOrgContainer();
            Map<String, List<WTUser>> gyys = ProcessUtil.getCheJianGroupAndUsers();
            Set<Entry<String, List<WTUser>>> gyzzSet = gyzzs.entrySet();
            for (Iterator iterator = gyzzSet.iterator(); iterator.hasNext();) {
                Entry<String, List<WTUser>> entry = (Entry<String, List<WTUser>>) iterator.next();
                String chejian = entry.getKey();
                List<WTUser> gyzzsUsers = entry.getValue();
                for (WTUser u : gyzzsUsers) {
                    if (u.getPersistInfo().getObjectIdentifier().getId() == currentuser.getPersistInfo()
                            .getObjectIdentifier().getId()) {
                        List<WTUser> gyyUsers = gyys.get(chejian);
                        for (WTUser gu : gyyUsers) {
                            String fullName = gu.getFullName();
                            if (fullName.contains(",")) {
                                fullName = fullName.replaceAll(",", "");
                            }
                            String uoid = rf.getReferenceString(gu);
                            String userStr = gu.getName() + "(" + fullName + ")";
                            result.put(userStr, uoid);
                        }
                        return result;
                    }
                }

            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 过滤工艺员需要会签的工艺文件
     *
     * @param rootList
     * @param ecn
     * @param currentuser
     * @return
     */
    public static List<Object> fiterECNAffectOrResultItems(List<Object> rootList, WTChangeOrder2 ecn,
            WTUser currentuser, WorkItem wi) {
        List<Object> resultList = new ArrayList<Object>();
        try {
            InputStream is = getAttachmentsFromPBO(ecn, ContentRoleType.SECONDARY, "signature_emps2.xml");
            if (is != null) {
                SignatureGYYXMLParser sxp = new SignatureGYYXMLParser(is);
                for (Object o : rootList) {
                    if (o instanceof EPMDocument || o instanceof WTDocument || o instanceof WTChangeOrder2) {
                        String oid = PersistenceHelper.getObjectIdentifier((Persistable) o).toString();
                        if (sxp.hasPrivilege(currentuser.getPersistInfo().getObjectIdentifier().toString(), oid, wi)) {
                            resultList.add(o);
                        }
                    } else {
                        resultList.add(o);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 过滤工艺组长需要会签的工艺文件
     *
     * @param rootList
     * @param ecn
     * @param currentuser
     * @return
     */
    public static List<Object> fiterECNAffectOrResultZPGYYItems(List<Object> rootList, WTChangeOrder2 ecn,
            WTUser currentuser, WorkItem wi) {
        List<Object> resultList = new ArrayList<Object>();
        try {
            InputStream is = getAttachmentsFromPBO(ecn, ContentRoleType.SECONDARY, "signature_emps.xml");
            if (is != null) {
                SignatureGYZZXMLParser sxp = new SignatureGYZZXMLParser(is);
                for (Object o : rootList) {
                    if (o instanceof EPMDocument || o instanceof WTDocument || o instanceof WTChangeOrder2) {
                        String oid = PersistenceHelper.getObjectIdentifier((Persistable) o).toString();
                        if (sxp.hasPrivilege(currentuser.getPersistInfo().getObjectIdentifier().toString(), oid, wi)) {
                            resultList.add(o);
                        }
                    } else {
                        resultList.add(o);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 通过指定的容器和车间角色获取所有工艺员
     *
     * @param wtContainer
     *            产品容器
     * @param chejian
     *            指定车间
     * @return List<WTUser> 用户集合
     * @throws WTException
     */
    public static Set<WTUser> getUsersByCheJian(WTContainer wtContainer, String chejianRole) throws WTException {
        Set<WTUser> list = new HashSet<WTUser>();
        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
        Role role = Role.toRole(chejianRole);
        if (role == null) {
            return list;
        }
        ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
        for (WTPrincipalReference reference : arrayList) {
            Object object2 = reference.getPrincipal();
            if (object2 instanceof WTUser) {
                WTUser user = (WTUser) object2;
                list.add(user);
            } else if (object2 instanceof WTGroup) {
                getUserFromWTGroup((WTGroup) object2, list);
            }
        }
        return list;
    }

    /**
     * 循环组并且获取组里面的用户
     *
     * @param group
     * @param list
     * @throws WTException
     */
    public static void getUserFromWTGroup(WTGroup group, Set<WTUser> list) throws WTException {
        if (group == null || list == null) {
            return;
        }
        Enumeration member = group.members();
        while (member.hasMoreElements()) {
            WTPrincipal principal = (WTPrincipal) member.nextElement();
            if (principal instanceof WTUser) {
                list.add((WTUser) principal);
            } else if (principal instanceof WTGroup) {
                getUserFromWTGroup((WTGroup) principal, list);
            }
        }
    }

    /**
     * 通过当前用户（工艺组长），得到该用户作为该车间下的所有团队中车间工艺员
     *
     * @param currentuser
     * @return key:user full name,value:useroid
     */
    public static Map<String, String> getUsersByCheJianTeam(WTUser currentuser, WTContainer wtContainer) {
        ReferenceFactory rf = new ReferenceFactory();
        Map<String, String> result = new HashMap<String, String>();
        try {
            Map<String, List<WTUser>> gyzzs = ProcessUtil.getGroupAndUsersInOrgContainer();
            Set<Entry<String, List<WTUser>>> gyzzSet = gyzzs.entrySet();
            for (Iterator iterator = gyzzSet.iterator(); iterator.hasNext();) {
                Entry<String, List<WTUser>> entry = (Entry<String, List<WTUser>>) iterator.next();
                String chejian = entry.getKey();
                List<WTUser> gyzzsUsers = entry.getValue();
                for (WTUser u : gyzzsUsers) {
                    if (u.getPersistInfo().getObjectIdentifier().getId() == currentuser.getPersistInfo()
                            .getObjectIdentifier().getId()) {
                        Set<WTUser> gyyUsers = new HashSet<WTUser>();
                        String chejianRole =  Constants.allChejianToWorkFlowGYYRoleMap.get(chejian);
                        if(chejianRole!=null&&!"".equals(chejianRole)){
                        	 gyyUsers = getUsersByCheJian(wtContainer, chejianRole);
                        }

                       /* if ("1".equals(chejian)) {
                            gyyUsers = getUsersByCheJian(wtContainer, ProcessConstants.ROLE_CHEJIAN_1);
                        } else if ("2".equals(chejian)) {
                            gyyUsers = getUsersByCheJian(wtContainer, ProcessConstants.ROLE_CHEJIAN_2);
                        } else if ("3".equals(chejian)) {
                            gyyUsers = getUsersByCheJian(wtContainer, ProcessConstants.ROLE_CHEJIAN_3);
                        } else if ("4".equals(chejian)) {
                            gyyUsers = getUsersByCheJian(wtContainer, ProcessConstants.ROLE_CHEJIAN_4);
                        } else if ("5".equals(chejian)) {
                            gyyUsers = getUsersByCheJian(wtContainer, ProcessConstants.ROLE_CHEJIAN_5);
                        } else if ("6".equals(chejian)) {
                            gyyUsers = getUsersByCheJian(wtContainer, ProcessConstants.ROLE_CHEJIAN_6);
                        } else if ("7".equals(chejian)) {
                            gyyUsers = getUsersByCheJian(wtContainer, ProcessConstants.ROLE_CHEJIAN_7);
                        } else if ("8".equals(chejian)) {
                            gyyUsers = getUsersByCheJian(wtContainer, ProcessConstants.ROLE_CHEJIAN_8);
                        }else if ("9".equals(chejian)) {
                            gyyUsers = getUsersByCheJian(wtContainer, ProcessConstants.ROLE_CHEJIAN_9);
                        }*/
                        for (WTUser gu : gyyUsers) {
                            String fullName = gu.getFullName();
                            if (fullName.contains(",")) {
                                fullName = fullName.replaceAll(",", "");
                            }
                            String uoid = rf.getReferenceString(gu);
                            String userStr = gu.getName() + "(" + fullName + ")";
                            result.put(userStr, uoid);
                        }
                        return result;

                    }
                }

            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static List filtrate2(List list) throws WTException {
        List resultList = new ArrayList();
        List resultList2 = new ArrayList();
        List partList = new ArrayList();
        for (Object o : list) {
            if (o instanceof WTPart) {
                WTPart part = (WTPart) o;
                partList.add(part);
                QueryResult qResult = WCUtil.getEPMBuildRoles(part);
                while (qResult.hasMoreElements()) {
                    Object object = qResult.nextElement();
                    if (object instanceof EPMBuildRule) {
                        EPMBuildRule rule = (EPMBuildRule) object;
                        Object ruleA = rule.getRoleAObject();
                        if (ruleA instanceof EPMDocument) {
                            EPMDocument epmDocument = (EPMDocument) ruleA;
                            epmDocument = (EPMDocument) getEffectiveObject((Master) epmDocument.getMaster(), list);
                            Set<EPMDocument> set2 = new HashSet<EPMDocument>();
                            try {
                                if (epmDocument != null) {
                                	if( !resultList.contains(epmDocument)){
                                		resultList.add(epmDocument);
                                	}
                                    set2 = WCUtil.get2DesignDocs(epmDocument);
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            Iterator<EPMDocument> iterator = set2.iterator();
                            while (iterator.hasNext()) {
                                EPMDocument epm2 = (EPMDocument) iterator.next();
                                epm2 = (EPMDocument) getEffectiveObject((Master) epm2.getMaster(), list);
                                if (epm2 != null&&!resultList.contains(epm2)) {
                                    resultList.add(epm2);
                                }
                            }
                        }
                    }
                }
            } else if (o instanceof WTChangeOrder2 || o instanceof WTDocument || o instanceof EPMDocument) {
            	if( !resultList.contains(o))
            		resultList2.add(o);
            }
        }
        resultList.addAll(resultList2);
        // resultList.addAll(partList);
        return resultList;
    }

    public static RevisionControlled getEffectiveObject(Master master, List list) throws WTException {
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(master);
        while (queryResult.hasMoreElements()) {
            RevisionControlled result = (RevisionControlled) queryResult.nextElement();
            if (list.contains(result)) {
                return result;
            }
        }
        return null;
    }

    public static void filtrate(List list) {
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof WTPart && o2 instanceof EPMDocument) {
                    return 1;
                } else if (o1 instanceof EPMDocument && o2 instanceof WTPart) {
                    return -1;
                }else if (o1 instanceof EPMDocument && o2 instanceof WTDocument) {
                    return -1;
                } else if (o1 instanceof EPMDocument && o2 instanceof ChangeOrder2) {
                    return -1;
                } else if (o1 instanceof EPMDocument && o2 instanceof ChangePackaged) {
                    return -1;
                }  else if (o1 instanceof EPMDocument && o2 instanceof EPMDocument) {
                    String num1 = ((EPMDocument) o1).getNumber();
                    String num2 = ((EPMDocument) o2).getNumber();
                    num1 = filtString(num1);
                    num2 = filtString(num2);
                    return num1.compareTo(num2);
                }
                return 0;
            }

        });
    }

    private static String filtString(String val) {
        if (val.startsWith("ASM") || val.startsWith("PRT") || val.startsWith("DRW")) {
            if (val.endsWith("ASM") || val.endsWith("PRT") || val.endsWith("DRW")) {
                val = val.substring(3, val.length() - 3);
            } else {
                val = val.substring(3);
            }
        }
        return val;
    }

    /**
     * 获取团队里的 工艺组长角色 人员
     *
     * @param cb
     * @return key 全名 value 工艺组长oid
     * @throws RemoteException
     * @throws InvocationTargetException
     */
    public static Map<String, String> getGYZZ(NmCommandBean cb) throws RemoteException, InvocationTargetException {
        Map<String, String> map = new HashMap<String, String>();
        if (!RemoteMethodServer.ServerFlag) {
            String method = "getContainerTeamRoles";
            return (Map<String, String>) RemoteMethodServer.getDefault().invoke(method,
                    SignatureService.class.getName(), null,
                     new Class[] { NmCommandBean.class }, new Object[] { cb });
        }

        try {
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem workItem = getWorkItem(cb);
            Persistable pbo = workItem.getPrimaryBusinessObject().getObject();
            WTContained contained = null;
            if (pbo instanceof WTChangeOrder2) {
                QueryResult qr = ChangeHelper2.service.getChangeablesBefore((WTChangeOrder2) pbo);
                boolean checkedOut = false;
                while (qr.hasMoreElements()) {
                    Object o = qr.nextElement();
                    if (o instanceof WTDocument) {
                        WTDocument document = (WTDocument) o;
                        contained = document.getContainer();
                    } else if (o instanceof EPMDocument) {
                        EPMDocument epmDocument = (EPMDocument) o;
                        contained = epmDocument.getContainer();
                    } else if (o instanceof WTPart) {
                        WTPart part = (WTPart) o;
                        contained = part.getContainer();
                    } else if (o instanceof ProcessEnvelope) {
                        ProcessEnvelope processEnvelope = (ProcessEnvelope) o;
                        contained = processEnvelope.getContainer();
                    } else if (o instanceof MPMProcessPlan) {
                        MPMProcessPlan processPlan = (MPMProcessPlan) o;
                        contained = processPlan.getContainer();
                    }
                    if (o instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) o)) {
                        checkedOut = true;
                        break;
                    }
                }
                if (checkedOut) {
                    throw new NmException("ext.casc.workflow.workflowResource", "workflow.workitem.docIsCheckout", null);
                }
            }
            if (pbo instanceof WTDocument) {
                WTDocument document = (WTDocument) pbo;
                contained = document.getContainer();
            } else if (pbo instanceof WTPart) {
                WTPart part = (WTPart) pbo;
                contained = part.getContainer();
            } else if (pbo instanceof EPMDocument) {
                EPMDocument epmDocument = (EPMDocument) pbo;
                contained = epmDocument.getContainer();
            } else if (pbo instanceof ManagedBaseline) {
                ManagedBaseline mbl = (ManagedBaseline) pbo;
                contained = mbl.getContainer();
            } else if (pbo instanceof WorkPackage) {
                WorkPackage wp = (WorkPackage) pbo;
                contained = wp.getContainer();
            } else if (pbo instanceof ProcessEnvelope) {
                ProcessEnvelope processEnvelope = (ProcessEnvelope) pbo;
                contained = processEnvelope.getContainer();
            } else if (pbo instanceof MPMProcessPlan) {
                MPMProcessPlan processPlan = (MPMProcessPlan) pbo;
                contained = processPlan.getContainer();
            } else if (pbo instanceof ChangePackaged) {
                ChangePackaged changePackaged = (ChangePackaged) pbo;
                contained = changePackaged.getContainer();
            }
            ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
            ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
            Role role = Role.toRole("GONGYIZUZHANG");
            ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
            Set<WTUser> users = new HashSet<WTUser>();
            for (WTPrincipalReference ref : allUser) {
                Persistable per = ref.getObject();
                if (per instanceof WTUser) {
                    WTUser user = (WTUser) per;
                    users.add(user);
                }
                if (per instanceof WTGroup) {
                    getUserFromWTGroup((WTGroup) per, users);
                }
            }
            for (WTUser user : users) {
                String fullName = user.getFullName();
                if (fullName.contains(",")) {
                    fullName = fullName.replaceAll(",", "");
                }
                String uoid = rf.getReferenceString(user);
                String userStr = user.getName() + "(" + fullName + ")";
                map.put(userStr, uoid);
            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    public static WorkItem getWorkItem(NmCommandBean cb) throws WTException {
        WorkItem ret = null;
        NmOid oid = cb.getPageOid() == null ? cb.getPrimaryOid() == null ? null : cb.getPrimaryOid() : cb.getPageOid();
        if (oid != null && oid.getRef() instanceof WorkItem) {
            ret = (WorkItem) oid.getRef();
        }
        return ret;
    }

    /**
     * 得到变更影响数据<br>
     * <br>
     *
     * @param ecn
     * @return
     * @throws WTException
     */
    public static List getChangeAffectItem(WTChangeOrder2 ecn) throws WTException {
        List resultList = new ArrayList();
        List partList = new ArrayList();
        List resultList2 = new ArrayList();
        List list = ChangeHelper.getChangeAffectItem(ecn);
        for (Object o : list) {
            if (o instanceof WTPart) {
                WTPart part = (WTPart) o;
                partList.add(part);
                resultList.add(part);
                QueryResult qResult = WCUtil.getEPMBuildRoles(part);
                while (qResult.hasMoreElements()) {
                    Object object = qResult.nextElement();
                    if (object instanceof EPMBuildRule) {
                        EPMBuildRule rule = (EPMBuildRule) object;
                        Object ruleA = rule.getRoleAObject();
                        if (ruleA instanceof EPMDocument) {
                            EPMDocument epmDocument = (EPMDocument) ruleA;
                            resultList.add(epmDocument);
                            try {
                                EPMDocument epmDocument3 = get2DesignDocs(epmDocument);
                                if (epmDocument3 != null) {
                                    resultList.add(epmDocument3);
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else if (o instanceof WTDocument) {
                resultList2.add(o);
            }
        }
        resultList.addAll(resultList2);
        // resultList.addAll(partList);
        return resultList;
    }

    /**
     * 获取3维CAD模型对应的2维CAD图样文档上一个大版本的最新小版本
     *
     * @param part
     *            WTPart对象
     * @return 设计文档集合
     * @throws Exception
     */
    public static EPMDocument get2DesignDocs(EPMDocument epm3d) throws Exception {
        EPMDocument epm2dBefore = null;
        // 找3d模型文件的2d图样文件
        QueryResult qr2d = PersistenceHelper.manager.navigate(epm3d.getMaster(),
                EPMReferenceLink.REFERENCED_BY_ROLE, EPMReferenceLink.class, false);
        while (qr2d.hasMoreElements()) {
            EPMReferenceLink link = (EPMReferenceLink) qr2d.nextElement();
            if (link.getDepType() != 4) // 不是图纸关联关系
                continue;
            EPMDocument epm2d = link.getReferencedBy();
            Mastered master = epm2d.getMaster();
            QueryResult qr3 = VersionControlHelper.service.allVersionsOf(master);
            int i = 0;
            while (qr3.hasMoreElements()) {
                epm2dBefore = (EPMDocument) qr3.nextElement();
                if (i == 1) {
                    return epm2dBefore;
                }
                i++;
            }
        }
        return epm2dBefore;
    }

    /**
     * 获取3维CAD模型对应的2维CAD图样文档最新小版本
     *
     * @param part
     *            WTPart对象
     * @return 设计文档集合
     * @throws Exception
     */
    public static EPMDocument getLastestIter2DesignDocs(EPMDocument epm3d) {
        EPMDocument epm2dBefore = null;
        // 找3d模型文件的2d图样文件

        try {
            QueryResult qr2d = PersistenceHelper.manager.navigate(epm3d.getMaster(),
                    EPMReferenceLink.REFERENCED_BY_ROLE, EPMReferenceLink.class, false);
            while (qr2d.hasMoreElements()) {
                EPMReferenceLink link = (EPMReferenceLink) qr2d.nextElement();
                if (link.getDepType() != 4) // 不是图纸关联关系
                    continue;
                EPMDocument epm2d = link.getReferencedBy();
                Mastered master = epm2d.getMaster();
                QueryResult qr3 = VersionControlHelper.service.allIterationsOf(master);
                if (qr3.hasMoreElements()) {
                    epm2dBefore = (EPMDocument) qr3.nextElement();
                }
            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return epm2dBefore;
    }

    public static String getVersionId(Object obj) {
        Versioned version = null;
        String veroid = "";
        try {
            ReferenceFactory rf = new ReferenceFactory();
            QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
            if (qr.hasMoreElements()) {
                version = (Versioned) qr.nextElement();
            }
            veroid = rf.getReference(version).toString();
            veroid = veroid.replaceAll(">", ":");
        } catch (Exception e) {
            e.printStackTrace();

        }
        return veroid;
    }

    public static File exportReport(String oid, NmCommandBean commandBean) {
        ArrayList<String> titles = buildTitles();
        ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();

        try {
            Map<WTObject, List<ASESHuiqianSignature>> signMap = SignatureHelper.getSignature(commandBean.getActionOid()
                    .getOid().toString());
            List values = buildValues(oid);
            for (Object obj : values) {
                ArrayList<String> list = new ArrayList<String>();
                if (obj instanceof EPMDocument) {
                    EPMDocument epm = (EPMDocument) obj;
                    list.add(epm.getNumber());
                    list.add(epm.getName());
                    list.add(VersionControlHelper.getVersionDisplayIdentifier(epm).toString());
                    list.add(getSignValue(epm, signMap, "内部会签"));
                    list.add(getSignValue(epm, signMap, "外部会签"));
                    list.add(getSignValue(epm, signMap, "工艺会签"));
                    list.add(getSignValue(epm, signMap, "物资会签"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "CMAT"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "CMAT_UP"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "CMAT_DOWN"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "CSIZE"));

                    EPMDocument epmDocument = (EPMDocument) obj;
                    EPMMemberLink link = WCUtil.getEpmMemberLinkByChild(epmDocument);
                    if (link != null) {
                        list.add(link.getQuantity().getAmount() + "");
                    }

                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "DESIGNER"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "PTC_MATERIAL_NAME"));
                } else if (obj instanceof WTDocument) {
                    WTDocument epm = (WTDocument) obj;
                    list.add(epm.getNumber());
                    list.add(epm.getName());
                    list.add(VersionControlHelper.getVersionDisplayIdentifier(epm).toString());
                    list.add(getSignValue(epm, signMap, "内部会签"));
                    list.add(getSignValue(epm, signMap, "外部会签"));
                    list.add(getSignValue(epm, signMap, "工艺会签"));
                    list.add(getSignValue(epm, signMap, "物资会签"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "CMAT"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "CMAT_UP"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "CMAT_DOWN"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "CSIZE"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "count"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "DESIGNER"));
                    list.add(IBAHelper.getStringIBAValueOfObject(epm, "PTC_MATERIAL_NAME"));
                } else if (obj instanceof WTChangeOrder2) {
                    WTChangeOrder2 changeOrder = (WTChangeOrder2) obj;
                    list.add(changeOrder.getNumber());
                    list.add(changeOrder.getName());
                    list.add("");
                    list.add(getSignValue(changeOrder, signMap, "内部会签"));
                    list.add(getSignValue(changeOrder, signMap, "外部会签"));
                    list.add(getSignValue(changeOrder, signMap, "工艺会签"));
                    list.add(getSignValue(changeOrder, signMap, "物资会签"));
                    list.add(IBAHelper.getStringIBAValueOfObject(changeOrder, "CMAT"));
                    list.add(IBAHelper.getStringIBAValueOfObject(changeOrder, "CMAT_UP"));
                    list.add(IBAHelper.getStringIBAValueOfObject(changeOrder, "CMAT_DOWN"));
                    list.add(IBAHelper.getStringIBAValueOfObject(changeOrder, "CSIZE"));
                    list.add(IBAHelper.getStringIBAValueOfObject(changeOrder, "count"));
                    list.add(IBAHelper.getStringIBAValueOfObject(changeOrder, "DESIGNER"));
                    list.add(IBAHelper.getStringIBAValueOfObject(changeOrder, "PTC_MATERIAL_NAME"));
                }
                if (!list.isEmpty()) {
                    allList.add(list);
                }
            }
            ExcelFileGenerator gen = new ExcelFileGenerator(titles, allList);
            WTProperties wtp = WTProperties.getLocalProperties();
            File file = null;
            String temp = wtp.getProperty("wt.temp");
            file = new File(temp + File.separator + "会签意见报表.xls");
            gen.expordExcel(new FileOutputStream(file));
            return file;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static List buildValues(String oid) throws WTRuntimeException, RemoteException, WTException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
        List<Object> rootList = new ArrayList<Object>();
        if (pbo instanceof ProcessEnvelope) {
            ProcessEnvelope pe = (ProcessEnvelope) pbo; // 获取pbo对象
            rootList.addAll(ProcessEnvelopeUtil.getAllMembers(pe));
        } else if (pbo instanceof ChangePackaged) {
            ChangePackaged packaged = (ChangePackaged) pbo; // 获取pbo对象
            QueryResult qr = PersistenceHelper.manager.navigate(packaged,
                    ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                    ChangePackagedResultLink.class, true);
            while (qr.hasMoreElements()) {
                rootList.add(qr.nextElement());
            }
        } else if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
            String objectType = "";
            try {
                objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // 添加作废申请单的判断
            if (objectType.indexOf("CHANGE_ECN") > -1 || objectType.indexOf("PROCESS_ECN") > -1) {
                rootList.addAll(ChangeHelper.getChangeResultItem(ecn));
                rootList.add(pbo);
            } else if (objectType.indexOf("ZUOFEI_ECN") > -1) {
                rootList.addAll(ChangeHelper.getChangeAffectItem(ecn));
                // rootList.add(pbo);
            }
        } else if (pbo instanceof WTDocument) {
            rootList.add(pbo);
        } else if (pbo instanceof MPMProcessPlan) {
            rootList.add(pbo);
        }
        rootList = SignatureService.filtrate2(rootList);
        return rootList;
    }

    public static ArrayList<String> buildTitles() {
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("编号");
        titles.add("名称");
        titles.add("版本");
        titles.add("内部会签");
        titles.add("外部会签");
        titles.add("工艺会签");
        titles.add("物资会签");
        titles.add("材料");
        titles.add("材料上标");
        titles.add("材料下标");
        titles.add("规格");
        titles.add("数量");
        titles.add("设计者");
        titles.add("材料名称");
        return titles;
    }

    public static String getOrginalPrincipalFromWorkItem(WorkItem it, WfActivity activity, WTUser currentuser) {
        if (!it.isReassigned())
            return null;

        WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
        WfProcess process = null;
        if (wfcont instanceof WfBlock) {
            WfBlock wfBlock = (WfBlock) wfcont;
            try {
                process = wfBlock.getParentProcess();

            } catch (WTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            process = (WfProcess) wfcont;
        }
        try {
            WfAssignmentEventAudit waea = getPreviousWfAssignmentEventAudit(currentuser, activity.getKey(),
                    process.getKey(), activity.getTripCount());
            return waea.getNewAssigneeRef().toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static WfAssignmentEventAudit getPreviousWfAssignmentEventAudit(WTPrincipal currentuser, long activityKey,
            long processKey, int tripcount) {
        WfAssignmentEventAudit waea = null;
        try {
            QuerySpec qs = new QuerySpec(WfAssignmentEventAudit.class);
            qs.appendWhere(new SearchCondition(WfAssignmentEventAudit.class, "activityKey", SearchCondition.EQUAL,
                    activityKey));
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WfAssignmentEventAudit.class, "processKey", SearchCondition.EQUAL,
                    processKey));
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WfAssignmentEventAudit.class, "newAssigneeRef.key.id",
                    SearchCondition.EQUAL, currentuser.getPersistInfo().getObjectIdentifier().getId()));
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WfAssignmentEventAudit.class, "tripCount", SearchCondition.EQUAL,
                    tripcount));
            ClassAttribute clsAttr = new ClassAttribute(WfAssignmentEventAudit.class,
                    WfAssignmentEventAudit.MODIFY_TIMESTAMP);
            OrderBy order = new OrderBy((OrderByExpression) clsAttr, true);
            qs.appendOrderBy(order);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);

            if (qr.hasMoreElements()) {
                waea = (WfAssignmentEventAudit) qr.nextElement();
            }
            if (waea.getOldAssigneeRef() != null) {
                waea = getPreviousWfAssignmentEventAudit(waea.getOldAssigneeRef().getPrincipal(), activityKey,
                        processKey, tripcount);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return waea;
    }

    private static String getSignValue(Object obj, Map<WTObject, List<ASESHuiqianSignature>> signMap,
            String activityName)
            throws WTException, WTPropertyVetoException {
        String value = "";
        ReferenceFactory rf = new ReferenceFactory();
        if (!(obj instanceof WTPart)) {
            List<ASESHuiqianSignature> tempSignList = signMap.get(obj);
            String tempValue = "";
            Hashtable ht = new Hashtable();
            for (int i = 0; i < tempSignList.size(); i++) {
                ASESHuiqianSignature tempSign = tempSignList.get(i);
                String tempActOid = tempSign.getActivity();
                WfActivity wfAct = (WfActivity) rf.getReference(tempActOid).getObject();
                if (wfAct.getName().equalsIgnoreCase(activityName)) {
                    String conclution = tempSign.getConclusion();
                    if (conclution == null) {
                        continue;
                    }
                    String userName = "";
                    if (conclution.contains("同意") && !conclution.contains("不同意")) {
                        userName = conclution.substring(0, conclution.length() - 2);
                    } else if (conclution.contains("不同意")) {
                        userName = conclution.substring(0, conclution.length() - 3);
                    } else {// 无需会签
                        userName = conclution.substring(0, conclution.length() - 4);
                    }
                    ASESHuiqianSignature tempSign1 = (ASESHuiqianSignature) ht.get(userName);
                    if (tempSign1 != null) {
                        if (tempSign.getCreateTimestamp().after(tempSign1.getCreateTimestamp())) {
                            ht.put(userName, tempSign);
                        }
                    } else {
                        ht.put(userName, tempSign);
                    }
                }
            }
            if (ht.size() > 0) {
                Enumeration enum1 = ht.keys();
                while (enum1.hasMoreElements()) {
                    Object obj1 = enum1.nextElement();
                    ASESHuiqianSignature tempSign = (ASESHuiqianSignature) ht.get(obj1);

                    String tempActOid = tempSign.getActivity();
                    WfActivity wfAct = (WfActivity) rf.getReference(tempActOid).getObject();
                    String name = wfAct.getName();
                    String realConclusion = tempSign.getConclusion();

                    String conclusion = "";
                    if (conclusion != null) {
                        if (name.equalsIgnoreCase("内部会签") || name.equalsIgnoreCase("外部会签")
                                || name.equalsIgnoreCase("内部工艺会签") || name.equalsIgnoreCase("用户会签")
                                || name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG) || name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZPGYHQ)
                                || name.equalsIgnoreCase("工艺会签")||name.equalsIgnoreCase(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
                            if (name.equalsIgnoreCase("外部会签")) {
                                String proxy = (String) TaskConfigrationHelper.getActivityVariableValue(wfAct, "proxy");
                                conclusion = proxy
                                        + ""
                                        + realConclusion
                                                .substring(realConclusion.indexOf(" "), realConclusion.length());
                            } else {
                                conclusion = realConclusion;
                            }

                        } else {
                            conclusion = realConclusion.substring(realConclusion.indexOf(" ") + 1,
                                    realConclusion.length());
                        }
                    } else {
                        conclusion = "";
                    }

                    String sig = tempSign.getSignature();
                    if (sig == null) {
                        sig = "";
                    }
                    String opinion = tempSign.getOpinion();
                    if (opinion == null) {
                        opinion = "";
                    }
                    if (tempValue.equals("")) {
                        if (sig == null || "".equals(sig)) {
                            tempValue = conclusion + ";" + opinion;
                        } else {
                            tempValue = conclusion + ";" + sig + ";" + opinion;
                        }
                    } else {
                        if (sig == null || "".equals(sig)) {
                            tempValue = tempValue + "/" + conclusion + ";" + opinion;
                        } else {
                            tempValue = tempValue + "/" + conclusion + ";" + sig + ";" + opinion;
                        }
                    }
                    value = tempValue;
                    if (value != null && !"".equals(value) && value.contains(",")) {
                        value = value.replaceAll(",", "");
                    }
                }
            }

        }
        return value;
    }

    public static void reassign2(NmCommandBean cb) {
        try {
            @SuppressWarnings("unused")
            WTPrincipal user = SessionHelper.manager.getPrincipal();
            String userOid = user.getPersistInfo().getObjectIdentifier().toString();
            List assignToUser = (List) cb.getComboBox().get("assignTo");
            String assignTo = "";
            if (assignToUser != null) {
                assignTo = assignToUser.get(0).toString().replaceAll("OR:", "");
            }
            Map map = cb.getRequestData().getParameterMap();
            Object oidO = map.get("oid"); // 读取任务页面的流程oid
            String oid = "";
            if (oidO instanceof String[]) {
                oid = ((String[]) oidO)[0];
            } else {
                oid = oidO.toString();
            } // 读取任务页面的流程oid
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
            WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef().getObject();

            WfProcess process = null;
            if (wfcont instanceof WfBlock) {
                WfBlock wfBlock = (WfBlock) wfcont;
                process = wfBlock.getParentProcess();
            } else {
                process = (WfProcess) wfcont;
            }
            WfProcessTemplate template = (WfProcessTemplate) process.getTemplate().getObject();

            if (!PROCESSNAMES.contains(template.getName())) {
                return;
            }
            WTProperties props = WTProperties.getLocalProperties();
            String tempFolder = props.getProperty("wt.temp");
            String filePath = "";
            String fileName = "";
            SignatureRecord record = null;
           String uuid =  UUID.randomUUID().toString();
            if (Constants.ACTIVITYNAME_ZPGYHQ.equals(wfAct.getName()) || "指派工艺员".equals(wfAct.getName())) {
            	String path =  tempFolder + File.separator + uuid;
            	File pathFile = new File(path);
            	if(!pathFile.exists()){
            		pathFile.mkdirs();
            	}
                filePath = tempFolder + File.separator +uuid+File.separator+ "signature_emps.xml";
                fileName = "signature_emps.xml";
                InputStream is = getAttachmentsFromPBO((ContentHolder) pbo,
                        ContentRoleType.SECONDARY, fileName);
                Map<String, SignatureRecord> sigmap = new HashMap<String, SignatureRecord>();
                if (is != null) {
                    SignatureGYZZXMLParser xmp = new SignatureGYZZXMLParser(is);
                    sigmap = xmp.getMap();
                }
                Set<Entry<String, SignatureRecord>> set = sigmap.entrySet();
                Element root = DocumentHelper.createElement("root");
                Document document = DocumentHelper.createDocument(root);
                for (Entry<String, SignatureRecord> entry : set) {
                    entry.getKey();
                    record = entry.getValue();
                    String approver = record.getPersons();
                    String reassign = record.getReassign();
                    if (!wi.isReassigned() && approver.contains(userOid)) {
                        Element element = root.addElement("EMPHQZZ");
                        element.addAttribute("oid", record.getEmpoid());
                        // element.addAttribute("advise", record.getAdvise());
                        // element.addAttribute("select", record.getResult());
                        element.addAttribute("zpr", record.getZpr());
                        element.addAttribute("approver", record.getPersons());
                        element.addAttribute("approverDisplay", record.getPersonsDis());
                        element.addAttribute("version", record.getVersion());
                        if (!reassign.contains(assignTo)) {
                            element.addAttribute("reassign", reassign + assignTo + ";");
                        } else {
                            element.addAttribute("reassign", reassign);
                        }

                    } else if (wi.isReassigned() && reassign.contains(userOid)) {
                        Element element = root.addElement("EMPHQZZ");
                        element.addAttribute("oid", record.getEmpoid());
                        // element.addAttribute("advise", record.getAdvise());
                        // element.addAttribute("select", record.getResult());
                        element.addAttribute("zpr", record.getZpr());
                        element.addAttribute("approver", record.getPersons());
                        element.addAttribute("approverDisplay", record.getPersonsDis());
                        element.addAttribute("version", record.getVersion());
                        if (!reassign.contains(assignTo)) {
                            element.addAttribute("reassign", reassign + assignTo + ";");
                        } else {
                            element.addAttribute("reassign", reassign);
                        }

                    } else {
                        Element element = root.addElement("EMPHQZZ");
                        element.addAttribute("oid", record.getEmpoid());
                        // element.addAttribute("advise", record.getAdvise());
                        // element.addAttribute("select", record.getResult());
                        element.addAttribute("zpr", record.getZpr());
                        element.addAttribute("approver", record.getPersons());
                        element.addAttribute("approverDisplay", record.getPersonsDis());
                        element.addAttribute("version", record.getVersion());
                        element.addAttribute("reassign", reassign);
                    }
                }
                File file = new File(filePath);
                XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file));
                xmlWriter.write(document);
                xmlWriter.close();
                genPBOAttachments((ContentHolder) pbo, fileName,filePath);
            } else if ("工艺会签".equals(wfAct.getName())) {
            	String path =  tempFolder + File.separator + uuid;
            	File pathFile = new File(path);
            	if(!pathFile.exists()){
            		pathFile.mkdirs();
            	}
                filePath = tempFolder + File.separator +uuid+File.separator+ "signature_emps2.xml";

                fileName = "signature_emps2.xml";
                InputStream is = getAttachmentsFromPBO((ContentHolder) pbo,
                        ContentRoleType.SECONDARY, fileName);
                Map<String, SignatureRecord> sigmap = new HashMap<String, SignatureRecord>();
                if (is != null) {
                    SignatureGYZZXMLParser xmp = new SignatureGYZZXMLParser(is);
                    sigmap = xmp.getMap();
                }
                Set<Entry<String, SignatureRecord>> set = sigmap.entrySet();
                Element root = DocumentHelper.createElement("root");
                Document document = DocumentHelper.createDocument(root);
                for (Entry<String, SignatureRecord> entry : set) {
                    entry.getKey();
                    record = entry.getValue();
                    String approver = record.getPersons();
                    String reassign = record.getReassign();
                    if (!wi.isReassigned() && approver.contains(userOid)) {
                        Element element = root.addElement("EMPGYY");
                        element.addAttribute("oid", record.getEmpoid());
                        // element.addAttribute("advise", record.getAdvise());
                        // element.addAttribute("select", record.getResult());
                        element.addAttribute("zpr", record.getZpr());
                        element.addAttribute("approver", record.getPersons());
                        element.addAttribute("approverDisplay", record.getPersonsDis());
                        element.addAttribute("version", record.getVersion());
                        if (!reassign.contains(assignTo)) {
                            element.addAttribute("reassign", reassign + assignTo + ";");
                        } else {
                            element.addAttribute("reassign", reassign);
                        }

                    } else if (wi.isReassigned() && reassign.contains(userOid)) {
                        Element element = root.addElement("EMPGYY");
                        element.addAttribute("oid", record.getEmpoid());
                        // element.addAttribute("advise", record.getAdvise());
                        // element.addAttribute("select", record.getResult());
                        element.addAttribute("zpr", record.getZpr());
                        element.addAttribute("approver", record.getPersons());
                        element.addAttribute("approverDisplay", record.getPersonsDis());
                        element.addAttribute("version", record.getVersion());
                        if (!reassign.contains(assignTo)) {
                            element.addAttribute("reassign", reassign + assignTo + ";");
                        } else {
                            element.addAttribute("reassign", reassign);
                        }

                    } else {
                        Element element = root.addElement("EMPHQZZ");
                        element.addAttribute("oid", record.getEmpoid());
                        // element.addAttribute("advise", record.getAdvise());
                        // element.addAttribute("select", record.getResult());
                        element.addAttribute("zpr", record.getZpr());
                        element.addAttribute("approver", record.getPersons());
                        element.addAttribute("approverDisplay", record.getPersonsDis());
                        element.addAttribute("version", record.getVersion());
                        element.addAttribute("reassign", reassign);
                    }
                }
                File file = new File(filePath);
                XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file));
                xmlWriter.write(document);
                xmlWriter.close();
                genPBOAttachments((ContentHolder) pbo, fileName,filePath);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void reassign(NmCommandBean cb) {
        try {
            @SuppressWarnings("unused")
            WTPrincipal user = SessionHelper.manager.getPrincipal();
           // String userOid = user.getPersistInfo().getObjectIdentifier().toString();
            List assignToUser = (List) cb.getComboBox().get("assignTo");
            String assignTo = "";
            if (assignToUser != null) {
                assignTo = assignToUser.get(0).toString().replaceAll("OR:", "");
            }
            Map map = cb.getRequestData().getParameterMap();
            Object oidO = map.get("oid"); // 读取任务页面的流程oid
            String oid = "";
            if (oidO instanceof String[]) {
                oid = ((String[]) oidO)[0];
            } else {
                oid = oidO.toString();
            } // 读取任务页面的流程oid
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();

            if(wfAct.getTemplate().getName().equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)){
            	//setReassignMap(wi,wfAct);
            }

            Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
            WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef().getObject();

            WfProcess process = null;
            if (wfcont instanceof WfBlock) {
                WfBlock wfBlock = (WfBlock) wfcont;
                process = wfBlock.getParentProcess();
            } else {
                process = (WfProcess) wfcont;
            }
            WfProcessTemplate template = (WfProcessTemplate) process.getTemplate().getObject();

            //如果是更改影响分析执行更改 需要修改附件中存储的人员id
            modifyUserIdForAnalysisProcess(process,wfAct,template,assignToUser.get(0).toString());

            if (!PROCESSNAMES.contains(template.getName())) {
                return;
            }
            if (!wi.isReassigned()) {
                setWorkItemOidMapOwnerShip(wi);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 方法功能:
     *
     * @author cjh
     * @date 2024/8/29
     */
    private static void modifyUserIdForAnalysisProcess(WfProcess process, WfActivity wfAct, WfProcessTemplate template, String assignTo) {
        try {
            if("更改影响分析执行流程".equals(template.getName()) && "执行更改".equals(wfAct.getName()) && StrUtil.isNotEmpty(assignTo)) {
                ReferenceFactory rf = new ReferenceFactory();
                WTPrincipal user = SessionHelper.manager.getPrincipal();
                String userOid = rf.getReferenceString(user);
                WTObject pbo = (WTObject) process.getBusinessObjectReference(new ReferenceFactory()).getObject();
                if(pbo != null && pbo instanceof WTAnalysisActivity) {
                    WTAnalysisActivity activity = (WTAnalysisActivity) pbo;
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(AnalysisObjEntry.ANALYSISNUMBER, activity.getNumber());
                    map.put(AnalysisObjEntry.RESPONSER, userOid);
                    List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(map);
                    for(AnalysisObjEntry entry : entries) {
                        if(AnalysisConstant.TYPE_PBOM.equals(entry.getDataType()) || AnalysisConstant.TYPE_TECHNICS.equals(entry.getDataType())){
                            entry.setResponser(assignTo);
                            try {
                                CmPersistenceHelper.manager.update(entry);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch(WTException e) {
            e.printStackTrace();
        }
    }

    public static void setReassignMap(WorkItem wi,WfActivity wfAct) throws WTException {
    	HashMap reassignMap = (HashMap)TaskConfigrationHelper.getActivityVariableValue(wfAct, "reassignMap");
    	String workOid = wi.getPersistInfo().getObjectIdentifier().toString();
    	String orginOwnerShip = "";
        if(reassignMap == null){
        	  reassignMap = new HashMap();
    	}
    	if( reassignMap.get(workOid)!=null){
    		orginOwnerShip = reassignMap.get(workOid).toString();
    	}
        Ownership os = wi.getOwnership();
        WTPrincipalReference wtpr = os.getOwner();
        String ownerShipOid = wtpr.getObject().getPersistInfo().getObjectIdentifier().toString();
        if("".equals(orginOwnerShip)){
        	 reassignMap.put(workOid, ownerShipOid);
        }else{
        	 reassignMap.put(workOid, orginOwnerShip+";"+ownerShipOid);
        }
        if (wfAct != null) {
            TaskConfigrationHelper.setActivityVariableValue(wfAct, "reassignMap", reassignMap);
        }
    }
    public static void setWorkItemOidMapOwnerShip(WorkItem wi) {
        // workItemToUserMap
    	String user = "";
   	 	try{
   	 		user = SessionHelper.manager.getPrincipal().getName();
   	 		SessionHelper.manager.setAdministrator();
            Map<String, String> map = new HashMap<String, String>();
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            Object workItemToUserMapValue = TaskConfigrationHelper.getActivityVariableValue(wfAct, "workItemToUserMap");
            if (workItemToUserMapValue != null) {
                map = parseStringToMap(workItemToUserMapValue.toString());
            }
            String workOid = wi.getPersistInfo().getObjectIdentifier().toString();
            Ownership os = wi.getOwnership();
            WTPrincipalReference wtpr = os.getOwner();
            String ownerShipOid = wtpr.getObject().getPersistInfo().getObjectIdentifier().toString();
            map.put(workOid, ownerShipOid);
            String value = parseMapToString(map);
            if (wfAct != null) {
                TaskConfigrationHelper.setActivityVariableValue(wfAct, "workItemToUserMap", value);
            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
	   		 try {
	             if (user != null && !user.equals(""))
	                 SessionHelper.manager.setPrincipal(user);
	         } catch (Exception e) {
	             // TODO: handle exception
	         }
        }
    }

    public static String getWorkItemOrginalOwnerShip(WorkItem wi) {
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        ProcessData pd = wfAct.getContext();
        Object workItemToUserMap = pd.getValue("workItemToUserMap");
        if (workItemToUserMap != null) {
            Map<String, String> map = parseStringToMap(workItemToUserMap.toString());
            return map.get(wi.getPersistInfo().getObjectIdentifier().toString());
        }
        return null;

    }

    public static Map<String, String> parseStringToMap(String value) {
        Map<String, String> map = new HashMap<String, String>();
        if (value != null && !"".equals(value)) {
            String[] vs = value.split(";");
            for (String v : vs) {
                if (!"".equals(v)) {
                    String[] vs2 = v.split("=");
                    map.put(vs2[0], vs2[1]);
                }
            }
        }
        return map;
    }

    public static String parseMapToString(Map<String, String> map) {
        Set<Entry<String, String>> set = map.entrySet();
        String value = "";
        for (Entry<String, String> entry : set) {
            value = value + entry.getKey() + "=" + entry.getValue() + ";";
        }
        return value;
    }

    public static List<WorkItem> getWorkItemByActivityOid(String wfActivityOid) throws WTException {
        List<WorkItem> workItems = new ArrayList<WorkItem>();
        String temp = wfActivityOid.substring(
                wfActivityOid.lastIndexOf(":") + 1, wfActivityOid.length());
        Long ida3a4 = Long.parseLong(temp);
        WorkItem workitem = null;
        QueryResult queryresult;
        QuerySpec queryspec = new QuerySpec(WorkItem.class);
        queryspec.setAdvancedQueryEnabled(true);
        SearchCondition searchcondition = new SearchCondition(WorkItem.class,
                "source.key.id", "=", ida3a4);
        queryspec.appendWhere(searchcondition, 0);
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(WorkItem.class, "status",
                "=", "POTENTIAL"), 0);
        TableColumn ca = new TableColumn("A0", "createstampa2");
        OrderBy orderBy = new OrderBy(ca, false);
        queryspec.appendOrderBy(orderBy, new int[0]);
        queryresult = PersistenceServerHelper.manager.query(queryspec);
        while (queryresult.hasMoreElements()) {
            workitem = (WorkItem) queryresult.nextElement();
            workItems.add(workitem);
        }
        return workItems;

    }

    public static boolean checkWhetherIsZhuRenGongYiShi(WTContainer wtContainer, WTUser user) {
        try {
            if (wtContainer == null) {
                return false;
            }
            ContainerTeam containerTeam = ContainerTeamHelper.service
                    .getContainerTeam((ContainerTeamManaged) wtContainer);
            Role role = Role.toRole("XIANGMUBUXINGHAOZHUGUANG");
            if (role == null) {
                return false;
            }
            ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
            for (WTPrincipalReference reference : arrayList) {
                Object object2 = reference.getPrincipal();
                if (object2 instanceof WTUser) {
                    WTUser tempUser = (WTUser) object2;
                    if (user.getName().equals(tempUser.getName())) {
                        return true;
                    }
                }
            }
        } catch (WTInvalidParameterException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void filtrateTreeRoot(Set<WTDocument> allDocs, Set<EPMDocument> allCads, WTUser currentuser,
            WorkItem wi, ISignatureParser parser) {
        Set<WTDocument> tempallDocs = new HashSet<WTDocument>(allDocs);
        Set<EPMDocument> tempallCads = new HashSet<EPMDocument>(allCads);
        for (WTDocument document : tempallDocs) {
            if (parser != null && !parser.hasPrivilege(document, currentuser, wi)) {
                allDocs.remove(document);
            }
        }
        for (EPMDocument epm : tempallCads) {
            if (parser != null && !parser.hasPrivilege(epm, currentuser, wi)) {
                allCads.remove(epm);
            }
        }

    }

    public static QueryResult getWfAsActivityByWfProcess(Persistable process) throws WTException {
        QuerySpec qSpec = new QuerySpec(WfAssignedActivity.class);
        int index[] = { 0 };
        long longId = PersistenceHelper.getObjectIdentifier(process).getId();
        SearchCondition sCondition = new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id",
                SearchCondition.EQUAL, longId);
        qSpec.appendWhere(sCondition, index);
        qSpec.appendAnd();
        qSpec.appendOpenParen();
        sCondition = new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.NAME, SearchCondition.EQUAL,
        		Constants.ACTIVITYNAME_ZPGYHQ);
        qSpec.appendWhere(sCondition, index);
        qSpec.appendOr();
        sCondition = new SearchCondition(WfAssignedActivity.class, WfAssignedActivity.NAME, SearchCondition.EQUAL,
                "指派工艺员");
        qSpec.appendWhere(sCondition, index);
        qSpec.appendCloseParen();

        return PersistenceHelper.manager.find((StatementSpec) qSpec);
    }

    public static String getMaxTripCount(Persistable process) throws WTException {
        QueryResult qr = getWfAsActivityByWfProcess(process);
        int count = 0;
        while (qr.hasMoreElements()) {
            WfAssignedActivity activity = (WfAssignedActivity) qr.nextElement();

            if (activity.getTripCount() > count) {
                count = activity.getTripCount();
            }
        }
        return count + "";

    }

    public static ArrayList<String> getAllCheJian() throws RemoteException {
        ArrayList<String> list = new ArrayList<String>();
        try {
            Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
            Set<String> set = new TreeSet<String>();
            set.addAll(map.keySet());
            for (String chejian : set) {
                list.add(chejian);
            }
        } catch (WTException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取车间对应的工艺组长
     *
     * @return ep.[<1,"OR:wt.">,<2,"OR:wt.">,....]
     */
    public static Map<String, String> getAllCheJianMapGYZZ() {
        Map<String, String> treeMap = new TreeMap<String, String>();
        ReferenceFactory rf = new ReferenceFactory();
        try {
            Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
            Set<Entry<String, List<WTUser>>> set = map.entrySet();
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                Entry<String, List<WTUser>> entry = (Entry<String, List<WTUser>>) iterator
                        .next();
                if (!entry.getValue().isEmpty()) {
                    treeMap.put(entry.getKey(), rf.getReferenceString(entry.getValue().get(0)));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return treeMap;
    }

    /**
     * 获取车间和项目办对应的工艺组长
     *
     * @return ep.[<1,"OR:wt.">,<2,"OR:wt.">,....，<"项","OR:wt.">]
     */
    public static Map<String, String> getAllCheJianAndXiangMuBuMapGYZZ(WTContainer wtContainer,WTUser currentUser) {
        Map<String, String> linkedHashMap = new LinkedHashMap<String, String>();
        ReferenceFactory rf = new ReferenceFactory();
        try {

            Map<String, List<WTUser>> map =  ProcessUtil.getGroupAndUsersInOrgContainer();
            Set<Entry<String, List<WTUser>>> set = map.entrySet();
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                Entry<String, List<WTUser>> entry = (Entry<String, List<WTUser>>) iterator.next();
                if (!entry.getValue().isEmpty()) {
                    linkedHashMap.put(entry.getKey(), rf.getReferenceString(entry.getValue().get(0)));
                }
            }
            List<WTUser> xmbuser = ProcessUtil.getRoleUsersByWTContainer("XIANGMUBUXINGHAOZHUGUANG", wtContainer);
            if (!xmbuser.isEmpty()) {
                linkedHashMap.put("项", rf.getReferenceString(xmbuser.get(0)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return linkedHashMap;
    }

    public static Map<String, String> getAllCheJianAndXiangMuBuMapGYZZ(WTContainer wtContainer) {
        Map<String, String> treeMap = new TreeMap<String, String>();
        ReferenceFactory rf = new ReferenceFactory();
        try {

            Map<String, List<WTUser>> map =ProcessUtil.getGroupAndUsersInOrgContainer();
            Set<Entry<String, List<WTUser>>> set = map.entrySet();
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                Entry<String, List<WTUser>> entry = (Entry<String, List<WTUser>>) iterator
                        .next();
                if (!entry.getValue().isEmpty()) {
                	treeMap.put(entry.getKey(), rf.getReferenceString(entry.getValue().get(0)));

                }

            }
            List<WTUser> xmbuser = ProcessUtil.getRoleUsersByWTContainer("XIANGMUBUXINGHAOZHUGUANG", wtContainer);
            if (!xmbuser.isEmpty()) {
                treeMap.put("项", rf.getReferenceString(xmbuser.get(0)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return treeMap;
    }


    /**
     * 解析xml把主制车间和副制车间保存在Part的IBA属性上
     * @param pbo
     */
    public static void saveCheJiansToParts(Object pbo,boolean ishuiqian) {
    	initZPGYYAttachmentForOld((ContentHolder) pbo);
    	if(!ishuiqian){
    	    return;
    	}
    	SignatureGYZZXMLParser parser = new SignatureGYZZXMLParser((ContentHolder)pbo);
    	Map<String,SignatureRecord> map = parser.getMap();
    	Set<Entry<String,SignatureRecord>> set = map.entrySet();
    	for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Entry<String, SignatureRecord> entry = (Entry<String, SignatureRecord>) iterator
					.next();
			SignatureRecord record = entry.getValue();
			String zhuzhichejian = record.getZhuzhichejian();
			String fuzhichejian = record.getFuzhichejian();
			String epmOid = record.getEmpoid();
			if(!"".equals(zhuzhichejian)){
				zhuzhichejian = zhuzhichejian.substring(0,1);
			}
			String fuzhichejianR = "";
			String[] fzcjs = null ;
			if(fuzhichejian.contains(";")){
				fzcjs = fuzhichejian.split(";");
				for(String fs : fzcjs){
					if(!"".equals(fs))
						fuzhichejianR = fuzhichejianR + fs.substring(0,1)+"-";
				}
				if(!"".equals(fuzhichejianR)&&fuzhichejianR.contains("-"))
					fuzhichejianR = fuzhichejianR.substring(0, fuzhichejianR.lastIndexOf("-"));
			}else{
				fuzhichejianR = fuzhichejian;
			}
			try {
			    if(epmOid.contains("EPMDocument")){
    				EPMDocument epm = (EPMDocument)WCUtil.getPersistable("OR:"+epmOid);
    				if (pbo instanceof WTChangeOrder2) {
                        QueryResult qResult = VersionControlHelper.service.allVersionsOf(epm.getMaster());
                        EPMDocument epmDocument = (EPMDocument)qResult.getObjectVector().lastElement();
                        QueryResult qr = CmWorkflowHelper.getEPMBuildLinksRoles(epmDocument);
                        while(qr.hasMoreElements()){
                            EPMBuildRule link = (EPMBuildRule)qr.nextElement();
                            WTPart part = (WTPart)link.getRoleBObject();
                            QueryResult qResult2 = VersionControlHelper.service.allVersionsOf(part.getMaster());
                            if (qResult2.hasMoreElements()) {
                                part = (WTPart)qResult2.nextElement();
                                if (part.getViewName().equals("Manufacturing")) {
                                    part = (WTPart)qResult2.nextElement();
                                }
                                IBAHelper.setIBAStringValue(part, "ZZCJ", zhuzhichejian);
                                IBAHelper.setIBAStringValue(part, "FZCJ", fuzhichejianR);
                            }
                        }
                    }else {
                        QueryResult qr = CmWorkflowHelper.getEPMBuildLinksRoles(epm);
                        while(qr.hasMoreElements()){
                            EPMBuildRule link = (EPMBuildRule)qr.nextElement();
                            WTPart part = (WTPart)link.getRoleBObject();
                            IBAHelper.setIBAStringValue(part, "ZZCJ", zhuzhichejian);
                            IBAHelper.setIBAStringValue(part, "FZCJ", fuzhichejianR);
                        }
                    }
			    }


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
    }

    /**
     * 根据当前用户获取产品团队的用户所在的车间号或者项目号
     * @param wtContainer
     * @return
     * @throws WTException
     */
    public static String getCheJianOrXiangMuNumByUser(WTContainer wtContainer) throws WTException{
    	Map<String,String> map = getAllCheJianAndXiangMuBuMapGYZZ(wtContainer);
    	WTUser currentuser = (WTUser)SessionHelper.manager.getPrincipal();
    	 ReferenceFactory rf = new ReferenceFactory();
    	Set<Entry<String, String>> set= map.entrySet();
        for (Iterator<Entry<String, String>> iterator = set.iterator(); iterator.hasNext();) {
            Entry<String, String> entry = iterator.next();
            if(entry.getValue().equals(rf.getReferenceString(currentuser))){
            	return entry.getKey();
            }
        }
        return "";

    }

	public static void multireassign(NmCommandBean cb) {
		try {
            @SuppressWarnings("unused")
            WTPrincipal user = SessionHelper.manager.getPrincipal();
            String userOid = user.getPersistInfo().getObjectIdentifier().toString();
            List assignToUser = (List) cb.getComboBox().get("assignTo");
            String assignTo = "";
            if (assignToUser != null) {
                assignTo = assignToUser.get(0).toString().replaceAll("OR:", "");
            }
            Map map = cb.getMap();
            Object oidO = map.get(null); // 读取任务页面的流程oid
            if (oidO instanceof List) {
            	List<NmOid> oids = (List<NmOid>) oidO;
                for(NmOid nm:oids){
                    WorkItem wi = (WorkItem)nm.getRefObject() ;
                    WfActivity wfAct = (WfActivity) wi.getSource().getObject();
                    WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef().getObject();

                    WfProcess process = null;
                    if (wfcont instanceof WfBlock) {
                        WfBlock wfBlock = (WfBlock) wfcont;
                        process = wfBlock.getParentProcess();
                    } else {
                        process = (WfProcess) wfcont;
                    }
                    WfProcessTemplate template = (WfProcessTemplate) process.getTemplate().getObject();

                    //如果是更改影响分析执行更改 需要修改附件中存储的人员id
                    modifyUserIdForAnalysisProcess(process,wfAct,template,assignToUser.get(0).toString());
                }

                for(NmOid nm:oids){
	                ReferenceFactory rf = new ReferenceFactory();
	                WorkItem wi = (WorkItem)nm.getRefObject() ;
	                WfActivity wfAct = (WfActivity) wi.getSource().getObject();
	                Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
	                WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef().getObject();

	                WfProcess process = null;
	                if (wfcont instanceof WfBlock) {
	                    WfBlock wfBlock = (WfBlock) wfcont;
	                    process = wfBlock.getParentProcess();
	                } else {
	                    process = (WfProcess) wfcont;
	                }
	                WfProcessTemplate template = (WfProcessTemplate) process.getTemplate().getObject();

	                if (!PROCESSNAMES.contains(template.getName())) {
	                    return;
	                }
	                if (!wi.isReassigned()) {
	                    setWorkItemOidMapOwnerShip(wi);
	                }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

	}
}
