package com.glaway.mpm.print.util;

import com.glaway.mpm.model.PrintFileBean;
import com.glaway.mpm.print.GWPrintConnectionQR;
import com.glaway.mpm.print.PrintUserCodeProcessor;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.util.*;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.access.AccessAdminUtil;
import ext.casc.workflow.TaskConfigrationHelper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.fc.*;
import wt.fc.collections.WTCollection;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pds.oracle81.OracleDataSource;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import javax.xml.rpc.ServiceException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class PrintWorkflowUtil {

    public static String HEAD = "<a href=\"netmarkets/jsp/ext/glaway/mpm/startFileTableForm.jsp?oid=";
    public static String HEAD1 = "<a href=\"netmarkets/jsp/ext/glaway/mpm/startChangeFileRecover.jsp?oid=";
    public static String HEAD2 = "<a href=\"netmarkets/jsp/ext/glaway/mpm/startChangeFileRecover1.jsp?oid=";
    public static String BODY = "\" target=\"_blank\">";
    public static String ROOT = "</a>";

    /**
     * 打印流程获取pbooid
     *
     * @param self
     * @return
     * @author zhuhao
     * @date 2018-4-10
     */
    public static String getPboOid(ObjectReference self) {
        WfProcess process = (WfProcess) self.getObject();
        Object pbo = TaskConfigrationHelper.getPBOByWfProcess(process);
        String pboOid = "";
        if (pbo instanceof WTDocument) {
            WTDocument doc = (WTDocument) pbo;
            pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
        } else if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 changeOrder = (WTChangeOrder2) pbo;
            pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(changeOrder).getId());
        }
        return pboOid;
    }


    /**
     * 设置部门资料员
     *
     * @param workitemName    自行定义的流程变量
     * @param name
     * @param processRoleName
     * @param self
     * @author zhuhao
     * @date 2018-4-10
     */
    public static void setDeptToProcessTeamRole(String workitemName, String processRoleName, ObjectReference self, String info) {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        List<WTUser> userList = new ArrayList<WTUser>();
        List<String> list = new ArrayList<String>();
        Role targetRole = Role.toRole(processRoleName);
        WfProcess process = (WfProcess) self.getObject();
        Object pbo = null;
        try {
            String pboOid = "";
            pbo = TaskConfigrationHelper.getPBOByWfProcess(process);
            if (pbo instanceof WTDocument) {
                WTDocument doc = (WTDocument) pbo;
                pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
            } else if (pbo instanceof WTChangeOrder2) {
                WTChangeOrder2 doc = (WTChangeOrder2) pbo;
                pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
            }
            if ("DYHS".equals(workitemName)) {//打印回收
                List<String> idList = new ArrayList<String>();
                Boolean isFromRecover = (Boolean) process.getContext().getValue("isFromRecover");
                String[] temp = info.split(":");
                for (String string : temp) {
                    idList.add(string);
                }
                list = PrintUserCodeProcessor.getDeptByUuid(idList, isFromRecover);
            } else if ("GGDYHS".equals(workitemName)) {//更改打印回收
                String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(process));
                list = PrintUserCodeProcessor.getDeptByOid(oid);
            } else if ("WJFC".equals(workitemName)) {//文件封存
                List<String> idList = new ArrayList<String>();
                String[] temp = info.split(":");
                for (String string : temp) {
                    idList.add(string);
                }
                list = PrintDataQueryUtil.getDeptById(idList);
            } else {
                list = PrintDataBuildUtil.getDeptByPbooid(pboOid, workitemName, info);
            }
            if (list != null && !list.isEmpty()) {
                for (String dept : list) {
                    String groupName = "";
                    if ("档案室".equals(dept)) {
                        groupName = dept + "库管理员组";
                    } else {
                        groupName = dept + "资料员组";
                    }
                    WTGroup group = AccessAdminUtil.getGroupByName(groupName);
                    PrintUtil.searchGroupUserList(group, userList);
                }
                Team team = (Team) process.getTeamId().getObject();
                for (int i = 0; i < userList.size(); i++) {
                    WTUser user = userList.get(i);
                    team.addPrincipal(targetRole, user);
                }
                team = (Team) PersistenceHelper.manager.refresh(team);
                team = (Team) PersistenceHelper.manager.save(team);
            }
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }
    
    /**
     * 用于加盖印章管理流程中设置档案员和资料员
     *
     * @param groupName 组名称
     * @param roleName 角色key值
     * @param self ObjectReference
     * @author 龙秀川
     * @date 2019-9-21
     */
    public static void setWfProcessTeamRoleUsers(String groupName, String roleName, ObjectReference self) {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        List<WTUser> userList = new ArrayList<WTUser>();
        WfProcess process = (WfProcess) self.getObject();
        try {
        	 WTGroup group = AccessAdminUtil.getGroupByName(groupName);
        	 if(group == null) {
        		 throw new WTException("在系统中找不到对应的组：" + groupName);
        	 } else {
        		 PrintUtil.searchGroupUserList(group, userList);
        		 if (userList.isEmpty()) {
        			 throw new WTException("在系统的组中不存在用户：" + groupName);
        		 } else {
        			 Role role = Role.toRole(roleName);
            		 if(role == null) {
            			 throw new WTException("在系统中找不到对应的角色：" + roleName);
            		 } else {
            			 Team team = (Team) process.getTeamId().getObject();
                         for (int i = 0; i < userList.size(); i++) {
                             WTUser user = userList.get(i);
                             team.addPrincipal(role, user);
                         }
                         team = (Team) PersistenceHelper.manager.refresh(team);
                         team = (Team) PersistenceHelper.manager.save(team);
            		 }
        		 }
        	 }
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    /**
     * 设置档案员
     *
     * @param name
     * @param processRoleName
     * @param self
     * @throws WTException
     */
    public static void setArchivistsTeamRole(String name, String processRoleName, ObjectReference self, String info) throws WTException {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        List<WTUser> userList = new ArrayList<WTUser>();
        Role targetRole = Role.toRole(processRoleName);
        WfProcess process = (WfProcess) self.getObject();
        String pbooid = getPboOid(self);
        try {
            String groupName = PrintUtil.buildArchivists(name, pbooid, info);
            WTGroup group = AccessAdminUtil.getGroupByName(groupName);
            PrintUtil.searchGroupUserList(group, userList);
            Team team = (Team) process.getTeamId().getObject();
            HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
            List tempUserList = (List) rolePrincipalListMap.get(targetRole);
            for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                WTUser user1 = (WTUser) ((WTPrincipalReference) tempUserList.get(i))
                        .getObject();
                team.deletePrincipalTarget(targetRole, user1);
            }

            for (int i = 0; i < userList.size(); i++) {
                WTUser user = userList.get(i);
                team.addPrincipal(targetRole, user);
            }
            team = (Team) PersistenceHelper.manager.refresh(team);
            team = (Team) PersistenceHelper.manager.save(team);
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    /**
     * 设置档案室回收组(库房管理员)
     *
     * @param name
     * @param processRoleName
     * @param self
     */
    public static void setRecycleTeamRole(String name, String processRoleName, ObjectReference self) {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        List<WTUser> userList = new ArrayList<WTUser>();
        Role targetRole = Role.toRole(processRoleName);
        WfProcess process = (WfProcess) self.getObject();
        try {
            String groupName = "档案室回收组";
            WTGroup group = AccessAdminUtil.getGroupByName(groupName);
            PrintUtil.searchGroupUserList(group, userList);
            Team team = (Team) process.getTeamId().getObject();
            for (int i = 0; i < userList.size(); i++) {
                WTUser user = userList.get(i);
                team.addPrincipal(targetRole, user);
            }
            team = (Team) PersistenceHelper.manager.refresh(team);
            team = (Team) PersistenceHelper.manager.save(team);
        } catch (ObjectNoLongerExistsException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    /**
     * 设置部门主任组
     *
     * @param name
     * @param processRoleName
     * @param self
     * @throws WTException
     */
    public static void setDirectorTeamRole(String name, String processRoleName, ObjectReference self, String info) throws WTException {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        List<WTUser> userList = new ArrayList<WTUser>();
        Role targetRole = Role.toRole(processRoleName);
        WfProcess process = (WfProcess) self.getObject();
        try {
            String groupName = PrintUtil.buildDirector(name, info);
            WTGroup group = AccessAdminUtil.getGroupByName(groupName);
            PrintUtil.searchGroupUserList(group, userList);
            Team team = (Team) process.getTeamId().getObject();
            for (int i = 0; i < userList.size(); i++) {
                WTUser user = userList.get(i);
                team.addPrincipal(targetRole, user);
            }
            team = (Team) PersistenceHelper.manager.refresh(team);
            team = (Team) PersistenceHelper.manager.save(team);
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    /**
     * 设置部门领导组
     *
     * @param workitemName    自行定义的流程变量
     * @param name
     * @param processRoleName
     * @param self
     * @author zhuhao
     * @date 2018-4-10
     */
    public static void setLeadershipTeamRole(String workitemName, String processRoleName, ObjectReference self, String info) {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        List<WTUser> userList = new ArrayList<WTUser>();
        List<String> list = new ArrayList<String>();
        Role targetRole = Role.toRole(processRoleName);
        WfProcess process = (WfProcess) self.getObject();
// 		 Object pbo = null;
        try {
// 			pbo = TaskConfigrationHelper.getPBOByWfProcess(process);
// 			WTDocument doc = (WTDocument) pbo;
// 			String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
            List<String> idList = new ArrayList<String>();
            String[] temp = info.split(":");
            for (String string : temp) {
                idList.add(string);
            }
            list = PrintDataQueryUtil.getDeptById(idList);
            if (list != null && !list.isEmpty()) {
                for (String dept : list) {
                    String groupName = "";
                    if ("档案室".equals(dept)) {
                        groupName = dept + "库管理员组";
                    } else {
                        groupName = dept + "领导组";
                    }
                    WTGroup group = AccessAdminUtil.getGroupByName(groupName);
                    PrintUtil.searchGroupUserList(group, userList);
                }
                Team team = (Team) process.getTeamId().getObject();
                for (int i = 0; i < userList.size(); i++) {
                    WTUser user = userList.get(i);
                    team.addPrincipal(targetRole, user);
                }
                team = (Team) PersistenceHelper.manager.refresh(team);
                team = (Team) PersistenceHelper.manager.save(team);
            }
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    /**
     * 取消打印申请删除数据库数据
     *
     * @param self
     * @author zhuhao
     * @date 2018-4-10
     */
    public static void deleteApplyByPBO(ObjectReference self) {
        String pboOid = getPboOid(self);
        //删除数据库数据 start
        String delSql1 = "DELETE FROM gwprintbarcode WHERE applyrecordid IN " +
                "(SELECT GWKEYID FROM gwprintdistributerecord WHERE applyrecordid IN " +
                "(SELECT GWKEYID FROM gwprintapplyrecord WHERE gwprintapplyrecord.pbooid = '" + pboOid + "'))";
        String delSql2 = "DELETE FROM gwprintdistributerecord WHERE applyrecordid IN " +
                "(SELECT GWKEYID FROM gwprintapplyrecord WHERE gwprintapplyrecord.pbooid = '" + pboOid + "')";
        String delSql3 = "DELETE FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + pboOid + "'";
        DBConnUtil conn = null;
        try {
            conn = new DBConnUtil();
            conn.executeUpdate(delSql1);
            conn.executeUpdate(delSql2);
            conn.executeUpdate(delSql3);
            conn.commit();
            //删除数据库数据 end
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 交互条码系统，下载条码到服务器
     *
     * @param self
     * @throws ServiceException
     * @author zhuhao
     * @date 2018-4-10
     */
    public static void getQR(ObjectReference self) throws IOException, ServiceException {
        boolean outFile = false;
        List<String> outList = new ArrayList<String>();
        Map<String, PrintFileBean> outMap = new HashMap<String, PrintFileBean>();
        List<WTObject> list = new ArrayList<WTObject>();
        Map<WTObject, String> map = new HashMap<WTObject, String>();
        String pboOid = getPboOid(self);
        Connection conn = null;
        Statement state = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE PBOOID = '");
            sb.append(pboOid);
            sb.append("'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String number = rs.getString("TECHNICSNUMBER");
                String version = rs.getString("VERSION");
                String fileType = rs.getString("FILETYPE");
                String category = rs.getString("DOCVR");
                if ("WL".equals(category) || "ZZ".equals(category)) {//外来文件和纸质文件交互条码
                    outFile = true;
                    PrintFileBean bean = new PrintFileBean();
                    number = rs.getString("DOCNUMBER");
                    String name = rs.getString("DOCNAME");
                    String secret = rs.getString("SECRET");
                    String id = rs.getString("GWKEYID");
                    Map<String, String> map2 = PrintDataQueryUtil.getOutFileNumberAndVersionById(pboOid, "");
                    String xhlx = PrintUtil.getModelType(PrintDataQueryUtil.getCategoryByOutFile(map2));
                    bean.setFileNumber(number);
                    bean.setFileName(name);
                    bean.setVersion(version);
                    bean.setSecret(secret);
                    bean.setXhlx(xhlx);
                    outList.add(id);
                    outMap.put(id, bean);
                } else {//厂内文件交互条码
                    if ("工艺更改单".equals(fileType) || "文档更改单".equals(fileType)) {
                        WTChangeOrder2 wtChangeOrder2 = getWTChangeOrder2ByNumber(number);
                        if (wtChangeOrder2 != null) {
                            String gwkeyid = rs.getString("GWKEYID");
                            list.add(wtChangeOrder2);
                            map.put(wtChangeOrder2, gwkeyid);
                        }
                    } else {
                        List<WTDocument> docs = WTDocumentUtil.getAllDocumentByNumber(number);
                        WTDocument doc = null;
                        precise:
                        for (WTDocument doc0 : docs) {
                            String docVersion = doc0.getVersionIdentifier().getValue() + "." + doc0.getIterationIdentifier().getValue();
                            if (docVersion.equals(version)) {
                                doc = doc0;
                                break precise;
                            }
                        }
                        if (doc != null) {
                            String gwkeyid = rs.getString("GWKEYID");
                            list.add(doc);
                            map.put(doc, gwkeyid);
                        }
                    }
                }
            }
            //交互条码系统
            if (outFile) {
                if (outList == null || outList.isEmpty()) {
                    return;
                }
                GWPrintConnectionQR.connectionQRForOutFile(outList, outMap);
            } else {
                if (list == null || list.isEmpty()) {
                    return;
                }
                GWPrintConnectionQR.connectionQR(list, map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            if (state != null) {
                try {
                    state.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void getQRForOffSet(String info) throws RemoteException, ServiceException {
        List<String> list = new ArrayList<String>();
        String[] temp = info.split("&");
        for (String string : temp) {
            list.add(string);
        }
        for (String str : list) {
            String idList = str.substring(str.indexOf("#") + 1, str.length());
            GWPrintConnectionQR.connectionQRForOffSet(idList);
        }
    }

    /**
     * 在更改单签审流程中启动文件回收流程分支
     *
     * @param pbo
     * @throws WTPropertyVetoException
     * @throws WTException
     * @throws IOException
     * @date 2018-4-18
     */
    public static void startProcessOfChangeRecover(WTObject pbo) throws WTPropertyVetoException, WTException, IOException { // add by lkc 2018.1.25
//		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        String xhlx = "";
        Transaction tx = new Transaction();
        WTDocument doc = null;
        String changeOrderOR = "";//更改单OR
        String docOR = "";//更改前对象OR
        String docName = "";
        if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
            docName = changeOrder2.getNumber() + "工艺更改单" + "回收申请单";
            //获取主对象型号类型
            IBAHelper ibaHelper = new IBAHelper((IBAHolder) changeOrder2.getContainer());
            xhlx = ibaHelper.getIBAValue("XHLX");
            QueryResult qResult;
            //获取更改前文档
            qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
            while (qResult.hasMoreElements()) {
                Object object = qResult.nextElement();
                if (object instanceof WTDocument) {
                    doc = (WTDocument) object;
                } else if (object instanceof MPMProcessPlan) {
                    MPMProcessPlan plan = (MPMProcessPlan) object;
                    doc = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
                }
            }
        }
        if (doc != null) {
            System.out.println("版本：" + doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
            docOR = PrintServerConstants.OID_WTDOCUMENT + doc.getPersistInfo().getObjectIdentifier().getId();
            WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
            Iterator it = coll.iterator();
            if (it.hasNext()) {
                WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                if (ecn != null) {
                    System.out.println("编号+名称：" + ecn.getNumber() + "|" + ecn.getName());
                    changeOrderOR = PrintServerConstants.OID_WTCHANGEORDER2 + ecn.getPersistInfo().getObjectIdentifier().getId();
                }
            }
        } else {
            System.out.println("startProcessOfChangeRecover-----缺少更改前对象");
        }
        try {
            tx.start();
            WTContainer wtContainer = PrintUtil.getContainerByName("打印分发管理库");
            if ("".equals(docName)) {
                docName = "回收申请单";
            }
            WTDocument doc1 = WTDocumentUtil.createDocument(null, docName, wtContainer, "Default/回收申请单", "casc.sast.149.PRINTRECOVERFORM");
            WorkflowUtil.startChangeRecoverWfProcess(doc1.getContainerReference(), doc1, "打印文件回收流程", changeOrderOR, docOR, xhlx);
            tx.commit();
            tx = null;
        } catch (PersistenceException e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
    }

    /**
     * 在更改单签审流程中启动工艺文件回收流程分支
     *
     * @param pbo
     * @throws WTPropertyVetoException
     * @throws WTException
     * @throws IOException
     * @date 2018-4-18
     */
    public static void startProcessOfChangeRecoverInFactory(WTObject pbo,String dept) throws WTPropertyVetoException, WTException, IOException {
//		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        String xhlx = "";
        Transaction tx = new Transaction();
        WTDocument doc = null;
        String changeOrderOR = "";//更改单OR
        String docOR = "";//更改前对象OR
        String docName = "";
        if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
            docName = changeOrder2.getNumber() + "工艺更改单" + "回收申请单";
            //获取主对象型号类型
            IBAHelper ibaHelper = new IBAHelper((IBAHolder) changeOrder2.getContainer());
            xhlx = ibaHelper.getIBAValue("XHLX");
            QueryResult qResult;
            //获取更改前文档
            qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
            while (qResult.hasMoreElements()) {
                Object object = qResult.nextElement();
                if (object instanceof WTDocument) {
                    doc = (WTDocument) object;
                } else if (object instanceof MPMProcessPlan) {
                    MPMProcessPlan plan = (MPMProcessPlan) object;
                    doc = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
                }
            }
        }
        if (doc != null) {
            System.out.println("版本：" + doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
            docOR = PrintServerConstants.OID_WTDOCUMENT + doc.getPersistInfo().getObjectIdentifier().getId();
            WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
            Iterator it = coll.iterator();
            if (it.hasNext()) {
                WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                if (ecn != null) {
                    System.out.println("编号+名称：" + ecn.getNumber() + "|" + ecn.getName());
                    changeOrderOR = PrintServerConstants.OID_WTCHANGEORDER2 + ecn.getPersistInfo().getObjectIdentifier().getId();
                }
            }
            String docNumber = doc.getNumber();
            String version = doc.getIterationDisplayIdentifier().toString();
            DBConnUtil dbConnUtil = null;
            Map<String, String> gwkeyMap = new HashMap<String, String>();
            List<String> gwkeyList = new ArrayList<String>();
            try {
                dbConnUtil = new DBConnUtil();
                String sql = "select a.GWKEYID,a.BARCODE from GWPRINTBARCODE a where APPLYRECORDID in (select b.GWKEYID from GWPRINTDISTRIBUTERECORD b where b.APPLYRECORDID in (select c.GWKEYID from GWPRINTAPPLYRECORD c where c.DOCNUMBER='" + docNumber + "' and c.VERSION='" + version + "' and c.ISIMPORT is null) and b.DISTRIBUTEDEPT='"+dept+"')";
                ResultSet resultSet = dbConnUtil.executeQuery(sql);
                while (resultSet.next()) {
                    String gwkeyid = resultSet.getString("GWKEYID");
                    String barcode = resultSet.getString("BARCODE");
                    gwkeyMap.put(gwkeyid, barcode);
                    gwkeyList.add(gwkeyid);
                }
                Date nowDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String time = sdf.format(nowDate);
                for (Map.Entry<String, String> entry : gwkeyMap.entrySet()) {
                    String gwkey = entry.getKey();
                    String barcode = entry.getValue();
                    String uuid = UUID.randomUUID().toString();
                    String sql2 = "update GWPRINTBARCODE set FILESTATUS = '回收中' where GWKEYID = '" + gwkey + "'";
                    String sql3 = "insert into GWPRINTRECOVERRECORD(GWKEYID, APPLYRECORDID, PRINTBARCODEID, RECOVERDATE) values('" + uuid + "','" + gwkey + "','" + barcode + "','" + time + "')";

                    dbConnUtil.executeUpdate(sql2);
                    dbConnUtil.executeUpdate(sql3);
                }

                dbConnUtil.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(dbConnUtil != null){
                    try {
                        dbConnUtil.close();
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(gwkeyList != null && gwkeyList.size() > 0){
                try {
                    tx.start();
                    WTContainer wtContainer = PrintUtil.getContainerByName("打印分发管理库");
                    if ("".equals(docName)) {
                        docName = "回收申请单";
                    }
                    WTDocument doc1 = WTDocumentUtil.createDocument(null, docName, wtContainer, "Default/回收申请单", "casc.sast.149.PRINTRECOVERFORM");
//				WorkflowUtil.startChangeRecoverWfProcess(doc1.getContainerReference(), doc1, "厂内工艺文件回收流程", changeOrderOR, docOR, xhlx);
                    WorkflowUtil.startRecoverWfProcess(doc1.getContainerReference(), doc1, "厂内工艺文件回收流程", new HashMap<String, String>(), gwkeyList);
                    tx.commit();
                    tx = null;
                } catch (PersistenceException e) {
                    e.printStackTrace();
                } finally {
                    if (tx != null)
                        tx.rollback();
                }
            }

        } else {
            System.out.println("startProcessOfChangeRecover-----缺少更改前对象");
        }
    }

    public static String changeFileRecover(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String ecnOid = proc.getContext().getValue("ecnOid").toString();
            String docOid = proc.getContext().getValue("docOid").toString();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String link = HEAD1 + oid + "&docOid=" + docOid + "&ecnOid=" + ecnOid + "&type=GGWJHSQR" + BODY + "更改文件回收确认" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setRecoverFileState(String infor) {
        if ("".equals(infor) || infor == null) {
            return;
        }
        Connection conn = null;
        try {
            List<String> list = new ArrayList<String>();
            String[] temp = infor.split(":");
            for (String string : temp) {
                list.add(string);
            }
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            //获取条码表id
            List<String> barCodeidList = new ArrayList<String>();
            for (String id : list) {
                StringBuffer sb = new StringBuffer();
                sb.append("SELECT APPLYRECORDID FROM GWPRINTRECOVERRECORD WHERE GWKEYID = '" + id + "'");
                ResultSet rs = state.executeQuery(sb.toString());
                while (rs.next()) {
                    String barCodeid = rs.getString("APPLYRECORDID");
                    if (!barCodeidList.contains(barCodeid)) {
                        barCodeidList.add(barCodeid);
                    }
                }
            }
            //删除回收表数据
            for (String id : list) {
                StringBuffer sb = new StringBuffer();
                sb.append("DELETE GWPRINTRECOVERRECORD WHERE GWKEYID = '" + id + "'");
                state.executeUpdate(sb.toString());
            }
            conn.commit();
            updateToBarcode(barCodeidList);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void updateToBarcode(List<String> barCodeidList) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String id : barCodeidList) {
                StringBuffer sb = new StringBuffer();
                sb.append("UPDATE GWPRINTBARCODE SET FILESTATUS = '已下发' WHERE GWKEYID = '" + id + "'");
                state.executeUpdate(sb.toString());
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void setProcessState(String pboOid, String state) {
        ReferenceFactory rf = new ReferenceFactory();
        Persistable proc;
        try {
            proc = rf.getReference(pboOid).getObject();
            WfProcess process = (WfProcess) proc;
            ProcessData processData = process.getContext();
            processData.setValue("state", state);
//			System.out.println(process.getContext().getValue("state").toString());
            PersistenceHelper.manager.save(process);
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkChangeFileState(String state) {
        if ("false".equals(state)) {
            return false;
        }
        return true;
    }

    public static String getChangeFileState(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            Object state = proc.getContext().getValue("state");
            if (state == null || "".equals(state)) {
                return "请先确认回收状态";
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 回收流程检验文件是否已经全部回收
     *
     * @param pbo
     * @param self
     * @return
     * @author zhuhao
     * @date 2018-5-10
     */
    public static String checkRecyclingState(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
            if (isFromRecover) {//自行回收
                String infor = proc.getContext().getValue("infor").toString();
                List<String> list = new ArrayList<String>();
                String[] temp = infor.split(":");
                for (String string : temp) {
                    list.add(string);
                }
                Boolean flag = PrintUserCodeProcessor.checkRecyclingStateByID(list);
                if (!flag) {
                    return "请先确认文件已经全部处理";
                }
            } else {//更改回收
                String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
                Boolean flag = PrintUserCodeProcessor.checkSureOfChangeRecover(oid);
                if (!flag) {
                    return "请先确认文件已经全部处理";
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置加盖印章到实例文档的印章里
     *
     * @param pbo
     * @param self
     * @author zhuhao
     * @date 2018-5-17
     */
    public static void setAddSeal(WTObject pbo, ObjectReference self) {
        String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT GWKEYID, ADDBATCH, BATCH FROM GWPRINTBARCODE WHERE PBOOID = '" + objOid + "'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String gwkeyid = rs.getString("GWKEYID");
                String addbatch = rs.getString("ADDBATCH");
                String batch = rs.getString("BATCH");
                if (gwkeyid != null && !"".equals(gwkeyid)) {
                    updateBatch(gwkeyid, addbatch, batch);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 加盖印章流程取消后清除加盖印章的信息
     *
     * @param pbo
     * @param self
     * @author jyx
     * @date 2018-5-29
     */
    public static void deleteAddSeal(WTObject pbo, ObjectReference self) {
        String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT GWKEYID, ADDBATCH, BATCH FROM GWPRINTBARCODE WHERE PBOOID = '" + objOid + "'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String gwkeyid = rs.getString("GWKEYID");
                if (gwkeyid != null && !"".equals(gwkeyid)) {
                    updateAddBatch(gwkeyid);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据更改单编号获取文件
     *
     * @param type
     * @return
     * @throws WTException
     * @throws RemoteException
     * @author qianlong
     * @date 2012-10-23
     */
    public static WTChangeOrder2 getWTChangeOrder2ByNumber(String number) throws WTException, RemoteException {
        WTChangeOrder2 ecn = null;
        int index[] = {0};
        QuerySpec qs = new QuerySpec(WTChangeOrder2.class);
        qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.NUMBER, SearchCondition.EQUAL, number), index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        qr = new LatestConfigSpec().process(qr);
        if (qr.hasMoreElements()) {
            ecn = (WTChangeOrder2) qr.nextElement();
        }
        return ecn;
    }

    private static void updateBatch(String gwkeyid, String addbatch, String batch) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("UPDATE GWPRINTBARCODE SET BATCH = '");
            if (batch != null && !"".equals(batch)) {
                sb.append(batch + "," + addbatch);
            } else {
                sb.append(addbatch);
            }
            sb.append("', ADDBATCH = '' WHERE GWKEYID = '" + gwkeyid + "'");
            state.executeUpdate(sb.toString());
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void updateAddBatch(String gwkeyid) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("UPDATE GWPRINTBARCODE SET ADDBATCH = ''");
            sb.append("WHERE GWKEYID = '" + gwkeyid + "'");
            state.executeUpdate(sb.toString());
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String checkSealPlus(WTObject pbo) {
        String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT ADDBATCH FROM GWPRINTBARCODE WHERE PBOOID = '" + objOid + "'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String result = rs.getString("ADDBATCH");
                if (result == null || "null".equals(result) || "".equals(result)) {
                    result = "";
                }
                if (!"".equals(result)) {
                    return "请确认加盖印章";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 启动补打流程
     *
     * @param ref
     * @param persistable
     * @param workflowName
     * @param oldSet
     * @param id
     * @param barcodeId
     * @return
     * @throws WTException
     * @author zhuhao
     * @date 2018-5-24
     */
    public static void startOffSetProcess(WTContainerRef ref, Persistable persistable, String workflowName, String printInfo) {
        boolean enfore = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(workflowName, ref);
            WfProcess wfProcess = WfEngineHelper.service.createProcess(wfProcessDefinition, null, ref);
            if (persistable != null) {
                wfProcess.setName(workflowName);
            }
            if (persistable instanceof WTPart) {
                WTPart part = (WTPart) persistable;
                wfProcess.setTeamTemplateId(part.getTeamTemplateId());
            }
            if (persistable instanceof WTDocument) {
                WTDocument document = (WTDocument) persistable;
                wfProcess.setTeamTemplateId(document.getTeamTemplateId());
            }
            if (persistable instanceof MPMProcessPlan) {
                MPMProcessPlan processPlan = (MPMProcessPlan) persistable;
                WTPart temp = MPMProcessPlanUtil.getMPMProcessplanRelatedPart(processPlan);
                wfProcess.setTeamTemplateId(temp.getTeamTemplateId());
            }
            ProcessData processData = wfProcess.getContext();
            processData.setValue("info", printInfo);
            processData.setValue("primaryBusinessObject", persistable);
            WfEngineHelper.service.startProcessImmediate(wfProcess, processData, 1);
        } catch (WTException e) {
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enfore);
        }
    }

    /**
     * 补打流程结束时更新状态
     *
     * @param self
     * @author zhuhao
     * @date 2018-5-26
     */
    public static void updateForOffSet(String info) {
        List<String> list = new ArrayList<String>();
        String[] temp = info.split("&");
        for (String string : temp) {
            list.add(string);
        }
        Connection conn = null;
        try {
            for (String str : list) {
                String ids = str.substring(str.indexOf("#") + 1, str.length());
                List<String> idList = java.util.Arrays.asList(ids.split(","));
                conn = OracleDataSource.getOracleDataSource().getConnection();
                conn.setAutoCommit(false);
                Statement state = conn.createStatement();
                for (String id : idList) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("UPDATE GWPRINTBARCODE SET OFFSET = '' WHERE GWKEYID = '" + id + "'");
                    state.executeUpdate(sb.toString());
                }
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void restoreForOffSet(String printInfo) {
        List<String> list = new ArrayList<String>();
        String[] temp = printInfo.split("&");
        for (String string : temp) {
            list.add(string);
        }
        Connection conn = null;
        try {
            for (String str : list) {
                String info = str.substring(0, str.indexOf("#"));
                String ids = str.substring(str.indexOf("#") + 1, str.length());
                String id = info.substring(0, info.indexOf("@"));
                String oldSet = info.substring(info.indexOf("@") + 1, info.length());
                List<String> oldList = java.util.Arrays.asList(oldSet.split(","));
                List<String> oldDeptList = new ArrayList<String>();
                Map<String, String> oldMap = new HashMap<String, String>();
                for (String oldInfo : oldList) {
                    String oldDept = oldInfo.substring(0, oldInfo.indexOf(":"));
                    String oldCount = oldInfo.substring(oldInfo.indexOf(":") + 1, oldInfo.indexOf("份"));
                    oldDeptList.add(oldDept);
                    oldMap.put(oldDept, oldCount);
                }
                conn = OracleDataSource.getOracleDataSource().getConnection();
                conn.setAutoCommit(false);
                Statement state = conn.createStatement();
                StringBuffer sb = new StringBuffer();
                sb.append("UPDATE GWPRINTAPPLYRECORD SET DISMESSAGE = '" + oldSet + "' WHERE GWKEYID = '" + id + "'");
                state.executeUpdate(sb.toString());
                conn.commit();
                PrintDataBuildUtil.restoreLinkForOffSet(id, oldDeptList, oldMap);
                PrintDataBuildUtil.deleteBarcodeForOffSet(ids);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检验产品库是否对应
     *
     * @param pbo
     * @return
     * @author zhuhao
     * @date 2018-6-5
     */
    public static boolean checkContainer(WTObject pbo) {
        String container = "";
        if (pbo instanceof WTDocument) {
            WTDocument doc = (WTDocument) pbo;
            container = doc.getContainerName();
        } else if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
            container = ecn.getContainerName();
        }
        List<String> allContainer = new ArrayList<String>();
        WTProperties wtProperties;
        try {
            wtProperties = WTProperties.getLocalProperties();
            String codebasePath = wtProperties.getProperty("wt.codebase.location");
            String filePath = codebasePath + File.separator + "ext"
                    + File.separator + "casc"
                    + File.separator + "conf" + File.separator + "config_149.xml";
            SAXReader reader = new SAXReader();
            Document document;
            document = reader.read(new File(filePath));
            Element rootElement = document.getRootElement();
            Element deptConfig = rootElement.element("PrintProductConfig");
            List<Element> configs = deptConfig.elements("config");
            for (Element e : configs) {
                Element product = e.element("product");
                String sproduct = product.getText();
                allContainer.add(sproduct);
            }
            if (allContainer.contains(container)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检验是否是外来或者纸质文件
     *
     * @param pbo
     * @return
     * @author zhuhao
     * @date 2018-6-15
     */
    public static boolean checkFileCategory(WTObject pbo) {
        String pboOid = "";
        if (pbo instanceof WTDocument) {
            WTDocument doc = (WTDocument) pbo;
            pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
        } else if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
            pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(ecn).getId());
        }
        String category = PrintDataQueryUtil.getCategory(pboOid);
        if ("WL".equals(category) || "ZZ".equals(category)) {
            return false;
        }
        return true;
    }

    public static boolean canclePrint(Object pbo, Object self, String dept) {
        long oid = -1;
        if (pbo instanceof WTDocument) {
            WTDocument document = (WTDocument) pbo;
            oid = document.getPersistInfo().getObjectIdentifier().getId();
        } else if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
            oid = changeOrder2.getPersistInfo().getObjectIdentifier().getId();
        }
        if (oid != -1) {
            DBConnUtil dbConnUtil = null;
            try {
                dbConnUtil = new DBConnUtil();
                String sql = "select GWKEYID,DISMESSAGE from GWPRINTAPPLYRECORD where PBOOID = '" + oid + "'";
                ResultSet resultSet = dbConnUtil.executeQuery(sql);
                Map<String, String> gwkeyMap = new HashMap<String, String>();
                while (resultSet.next()) {
                    String gwkeyId = resultSet.getString("GWKEYID");
                    String dismessage = resultSet.getString("DISMESSAGE");
                    gwkeyMap.put(gwkeyId, dismessage);

                }
                for (Map.Entry<String, String> entry : gwkeyMap.entrySet()) {
                    String gwKey = entry.getKey();
                    String disMessage = entry.getValue();
                    String newDismessage = "";
                    if (disMessage != null) {
                        String[] dismessages = disMessage.split(",");
                        for (String msg : dismessages) {
                            if (msg.contains(dept)) {
                                continue;
                            }
                            if ("".equals(newDismessage)) {
                                newDismessage = msg;
                            } else {
                                newDismessage += "," + msg;
                            }
                        }
                    }
                    String sql1 = "update GWPRINTAPPLYRECORD set DISMESSAGE='" + newDismessage + "' where GWKEYID='" + gwKey + "'";
                    String sql2 = "delete from GWPRINTDISTRIBUTERECORD where APPLYRECORDID='" + gwKey + "' and DISTRIBUTEDEPT='" + dept + "'";
                    String sql3 = "delete from GWPRINTBARCODE where APPLYRECORDID in (select GWKEYID from GWPRINTDISTRIBUTERECORD where APPLYRECORDID='" + gwKey + "' and DISTRIBUTEDEPT='" + dept + "')";
                    dbConnUtil.executeUpdate(sql3);
                    dbConnUtil.executeUpdate(sql2);
                    dbConnUtil.executeUpdate(sql1);
                }
                dbConnUtil.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dbConnUtil != null) {
                        dbConnUtil.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean checkPrintOrRecoverState(Object pbo, String state, String dept) {
        long oid = -1;
        if (pbo instanceof WTDocument) {
            WTDocument document = (WTDocument) pbo;
            oid = document.getPersistInfo().getObjectIdentifier().getId();
        } else if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
            oid = changeOrder2.getPersistInfo().getObjectIdentifier().getId();
        }
        if (oid != -1) {
            DBConnUtil dbConnUtil = null;
            try {
                dbConnUtil = new DBConnUtil();
                String sql = "select c.FILESTATUS from GWPRINTBARCODE c where APPLYRECORDID in (select GWKEYID from GWPRINTDISTRIBUTERECORD b where APPLYRECORDID in (select GWKEYID from GWPRINTAPPLYRECORD a where a.PBOOID='"+oid+"') and b.DISTRIBUTEDEPT='"+dept+"')";
                ResultSet resultSet = dbConnUtil.executeQuery(sql);
                while (resultSet.next()) {
                    String filestatus = resultSet.getString("FILESTATUS");
                    if("已回收".equals(filestatus)){
                        continue;
                    }
                    if(!filestatus.equals(state)){
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dbConnUtil != null) {
                        dbConnUtil.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static boolean checkRecoverState(String info, String dept){
        if(info != null){
            DBConnUtil dbConnUtil = null;
            try {
                dbConnUtil = new DBConnUtil();
                String[] infos = info.split(":");
                for (String gwkeyid : infos) {
                    String sql = "select FILESTATUS from GWPRINTBARCODE where GWKEYID='" + gwkeyid + "' and GDEPT='"+dept+"'";
                    ResultSet resultSet = dbConnUtil.executeQuery(sql);
                    if (resultSet.next()){
                        String filestatus = resultSet.getString("FILESTATUS");
                        if(!"已回收".equals(filestatus) && !"已遗失".equals(filestatus)){
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }finally {
                try {
                    if (dbConnUtil != null) {
                        dbConnUtil.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }
}
