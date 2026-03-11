package com.glaway.mpm.intf;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.model.data.CmBaseline;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.util.PersistableUtil;
import com.glaway.mpm.print.*;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.*;
import com.glaway.mpm.print.model.GWPrintApplyRecord;
import com.glaway.mpm.print.model.GWPrintDistributeRecord;
import com.glaway.mpm.print.model.GWPrintRecoverRecord;
import com.glaway.mpm.print.util.FolderUtil;
import com.glaway.mpm.print.util.*;
import com.glaway.mpm.util.*;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.access.AccessAdminUtil;
import ext.casc.util.CommonUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.fc.*;
import wt.inf.container.WTContainer;
import wt.inf.container._WTContainer;
import wt.method.RemoteAccess;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representable;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PrintToWCIntfRMI implements RemoteAccess {

    private static VaLogger logger = VaLogger.getLogger(PrintToWCIntfRMI.class.getName());
    private static int index[] = {0};
    public static String HEAD = "<a href=\"netmarkets/jsp/ext/glaway/mpm/startFileTableForm.jsp?oid=";
    public static String HEAD1 = "<a href=\"netmarkets/jsp/ext/glaway/mpm/startChangeFileRecover.jsp?oid=";
    public static String HEAD2 = "<a href=\"netmarkets/jsp/ext/glaway/mpm/startChangeFileRecover1.jsp?oid=";
    public static String BODY = "\" target=\"_blank\">";
    public static String ROOT = "</a>";

    //获得对象附件
    public static List<ApplicationData> getAttachments(ContentHolder holder) throws WTException {
        List<ApplicationData> atts = new ArrayList<ApplicationData>();

        QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
        if (qr != null && qr.size() != 0) {
            while (qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof ApplicationData) {
                    ApplicationData data = (ApplicationData) obj;
                    atts.add(data);
                }
            }
        }
        return atts;
    }

    /**
     * 查询组下面所有的用户
     *
     * @param group
     * @param userList
     * @throws WTException
     */
    public static void searchGroupUserList(WTGroup group, List<WTUser> userList) throws WTException { // add by lkc 2018.1.25
        PrintUtil.searchGroupUserList(group, userList);
    }

    /**
     * add by zhuhao
     *
     * @param workitemName    自行定义的流程变量
     * @param name
     * @param processRoleName
     * @param self
     */
//	public static void setDeptToProcessTeamRole(String workitemName,String name,String processRoleName,ObjectReference self){
// 		 boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
// 		 List<WTUser> userList = new ArrayList<WTUser>();
// 		 List<String> list = new ArrayList<String>();
// 		 Role targetRole = Role.toRole(processRoleName);
// 		 WfProcess process = (WfProcess) self.getObject();
// 		Object pbo = null;
// 		try {
//// 			ReferenceFactory rf = new ReferenceFactory();
//// 			String workflowProcessOid = rf.getReferenceString(process);
//// 			String workflowProcessOid = String.valueOf(self.getObjectId());
// 			pbo = TaskConfigrationHelper.getPBOByWfProcess(process);
// 			WTDocument doc = (WTDocument) pbo;
// 			String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
//			list = getDeptByPbooid(pboOid , workitemName);
//			if(list != null && !list.isEmpty()){
//				for(String dept : list){
//					String groupName = dept + name;
//					WTGroup group = AccessAdminUtil.getGroupByName(groupName);
//					searchGroupUserList(group, userList);
//				}
//				Team team = (Team) process.getTeamId().getObject();
//			  	for (int i = 0; i < userList.size(); i++) {
//		            WTUser user = userList.get(i);
//					team.addPrincipal(targetRole, user);
//		        }
//				team = (Team) PersistenceHelper.manager.refresh(team);
//				team = (Team) PersistenceHelper.manager.save(team);
//			}
//		} catch (WTRuntimeException e) {
//			e.printStackTrace();
//		} catch (WTException e) {
//			e.printStackTrace();
//		}
// 		wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
// 	}
//
//     private static List<String> getDeptByPbooid(String pboOid, String workitemName) {
//    	 List<String> list = new ArrayList<String>();
//    	 list = PrintDataBuildUtil.getDeptByPbooid(pboOid,workitemName);
//		return list;
//	}

    //查询当前扫码用户是否是指定回收人员
    public static Boolean checkUserByStore(String userName, List<String> strList) {
        Boolean flag = false;
        List<WTUser> userList = new ArrayList<WTUser>();
        List<String> deptList = new ArrayList<String>();
        deptList = PrintUserCodeProcessor.getDeptByUuidOfStore(strList);
        for (int i = 0; i < deptList.size(); i++) {
            if ("档案室".equals(deptList.get(i).toString())) {
                String groupName = deptList.get(i).toString() + "库管理员组";
                WTGroup group = AccessAdminUtil.getGroupByName(groupName);
                try {
                    searchGroupUserList(group, userList);
                } catch (WTException e) {
                    e.printStackTrace();
                }
            } else {
                String groupName = deptList.get(i).toString() + "资料员组";
                WTGroup group = AccessAdminUtil.getGroupByName(groupName);
                try {
                    searchGroupUserList(group, userList);
                } catch (WTException e) {
                    e.printStackTrace();
                }
            }
        }
        for (int i = 0; i < userList.size(); i++) {
            WTUser user = userList.get(i);
            if (userName.equals(user.getName())) {
                flag = true;
                return flag;
            }
        }
        return flag;
    }

    //查询当前扫码用户是否是指定回收人员
    public static Boolean checkUserByRecover(String userName, List<String> strList) {
        Boolean flag = false;
        List<WTUser> userList = new ArrayList<WTUser>();
        List<String> deptList = new ArrayList<String>();
        deptList = PrintUserCodeProcessor.getDeptByUuidOfRecover(strList);
        for (int i = 0; i < deptList.size(); i++) {
            if ("档案室".equals(deptList.get(i).toString())) {
                String groupName = deptList.get(i).toString() + "库管理员组";
                WTGroup group = AccessAdminUtil.getGroupByName(groupName);
                try {
                    searchGroupUserList(group, userList);
                } catch (WTException e) {
                    e.printStackTrace();
                }
            } else {
                String groupName = deptList.get(i).toString() + "资料员组";
                WTGroup group = AccessAdminUtil.getGroupByName(groupName);
                try {
                    searchGroupUserList(group, userList);
                } catch (WTException e) {
                    e.printStackTrace();
                }
            }
        }
        for (int i = 0; i < userList.size(); i++) {
            WTUser user = userList.get(i);
            if (userName.equals(user.getName())) {
                flag = true;
                return flag;
            }
        }
        return flag;
    }

    public static String saveLoseInfo(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.saveLoseInfo(list);
    }

    public static String saveLoseInfoOfRecover(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.saveLoseInfoOfRecover(list);
    }

    public static String saveWfprocessLoseInfoOfRecover(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.saveWfprocessLoseInfoOfRecover(list);
    }

    public static String saveLoseInfoOfStore(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.saveLoseInfoOfStore(list);
    }

    public static String showRecoverOne(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=CXHSXX" + BODY + "查看回收信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showRecoverTwo(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=HSZZWJ" + BODY + "回收纸质文件" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showRecoverThree(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=HSWJQR" + BODY + "回收文件确认" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showRecoverThree(WTObject pbo, ObjectReference self, String dept) { // add by lb 2019.4.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        String wfaOid = PersistenceHelper.getObjectIdentifier(wfa).toString();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&dept=" + dept + "&wfaOid=" + wfaOid + "&type=HSWJQR" + BODY + "回收文件确认" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showDelayOne(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=CKYCXX" + BODY + "查看延迟信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showDelayTwo(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=XGYCXX" + BODY + "查看延迟信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showLose(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=YSXX" + BODY + "遗失文件信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String updateLose(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=XGYSXX" + BODY + "遗失文件信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showStore(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=CKFCXX" + BODY + "封存文件信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showStoreByDept(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=CKXXBYDEPT" + BODY + "封存文件信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showStoreSecond(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=FCZZWJHS" + BODY + "封存文件回收" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showStoreThird(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=FCZZWJQR" + BODY + "封存文件信息确认" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showOpenStore(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=CKFCXX" + BODY + "查看启封信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showOverTimeFileInfo(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=YCHSYQWJXX" + BODY + "延迟回收逾期文件信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String showDirectDelayFileInfo(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD + oid + "&pboOid=" + pboOid + "&type=HSYCWJXX" + BODY + "需要回收的延迟文件信息" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String changeFileRecover(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String ecnOid = proc.getContext().getValue("ecnOid").toString();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD1 + oid + "&pboOid=" + pboOid + "&ecnOid=" + ecnOid + "&type=GGWJHSQR" + BODY + "更改文件回收确认" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String changeFileRecover1(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD2 + oid + "&pboOid=" + pboOid + "&type=GGHSZZWJ" + BODY + "更改纸质文件回收" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String changeFileRecover2(WTObject pbo, ObjectReference self) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD2 + oid + "&pboOid=" + pboOid + "&type=GGHSWJQR" + BODY + "更改回收文件信息确认" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String changeFileRecover2(WTObject pbo, ObjectReference self, String dept) { // add by lkc 2018.1.25
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        WfProcess proc;
        try {
            proc = wfa.getParentProcess();
            String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(proc));
            String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
            String link = HEAD2 + oid + "&pboOid=" + pboOid + "&dept=" + dept + "&type=GGHSWJQR" + BODY + "回收纸质文件" + ROOT;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Boolean checkChangeRecoverFile(WTObject pbo) {

        String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
        return PrintUserCodeProcessor.checkChangeRecoverFile(pboOid);
    }


    public static List<CmPrintRecordInfoBean> queryReprintInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryReprintInfo(cmPrintRecordQueryBean);
    }

    public static CmPrintRecordInfoBean getInfoByBarCode(String barCode) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.getInfoByBarCode(barCode);
    }

    public static CmPrintRecordInfoBean getInfoByUserName(String userName) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.getInfoByUserName(userName);
    }

    public static void updateReprintInfo(List<CmPrintRecordInfoBean> listBean, String time) { // add by lkc 2018.1.25
        PrintUserCodeProcessor.updateReprintInfo(listBean, time);
    }

    public static List<CmPrintRecordInfoBean> queryLoseInfo(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryLoseInfo(list);
    }

    public static Boolean saveUpdateLoseInfo(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.saveUpdateLoseInfo(list);
    }

    public static List<CmPrintRecordInfoBean> updateBean(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.updateBean(list);
    }

    public static void deleteLoseInfo(ObjectReference self) throws RemoteException, WTException { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        Boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
        Boolean isFromStore = (Boolean) proc.getContext().getValue("isFromStore");
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.deleteLoseInfo(list, isFromRecover, isFromStore);
    }

    public static void updateLoseEndStatus(ObjectReference self) throws RemoteException, WTException { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        Boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
        Boolean isFromStore = (Boolean) proc.getContext().getValue("isFromStore");
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateLoseEndStatus(list, isFromRecover, isFromStore);
    }

    public static void updateLoseStatus(ObjectReference self) throws RemoteException, WTException { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        Boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
        Boolean isFromStore = (Boolean) proc.getContext().getValue("isFromStore");
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateLoseStatus(list, isFromRecover, isFromStore);
    }

    /**
     * 封存流程取消后删除封存表中数据
     *
     * @param self
     * @throws RemoteException
     * @throws WTException
     * @author jyx
     * @date 2018-5-17
     */
    public static void deleteStoreInfo(ObjectReference self) throws RemoteException, WTException { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.deleteStoreInfo(list);
    }

    public static void updateImmediateDelay(ObjectReference self) {
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        Boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateImmediateDelay(list, isFromRecover);
    }

    public static void updateImmediateDelayStatus(ObjectReference self) {
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        Boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateImmediateDelayStatus(list, isFromRecover);
    }

    public static WfProcess getProcessByOid(String oid) throws WTException {
        WfProcess process = null;
        QuerySpec qs = new QuerySpec(WfProcess.class);
        long longId = Long.valueOf(oid);
        int[] index = {0};
        SearchCondition sc = new SearchCondition(WfProcess.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, longId);
        qs.appendWhere(sc, index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            process = (WfProcess) qr.nextElement();
        }
        return process;
    }

    public static List<CmBaseline> getBaseline(String oid, String fileType) {
        try {
            return PrintUtil.getBaseline(oid, fileType);
        } catch (NumberFormatException e) {
            logger.error(e);
        } catch (WTException e) {
            logger.error(e);
        }
        return new ArrayList<CmBaseline>();
    }

    public static Vector<String> getDistributeDept() {
        return PrintUtil.getDistributeDept();
    }

    public static Vector<String> getOutsideDept() {
        return PrintUtil.getOutsideDept();
    }

    //更新回收表中的数据为之前状态
    public static void updateDelayInfo(ObjectReference self) throws RemoteException, WTException { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        Boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateDelayInfo(list, isFromRecover);
    }

    public static void updateDelayEndStatus(ObjectReference self) throws RemoteException, WTException { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        Boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateDelayEndStatus(list, isFromRecover);
    }

    public static void updateDelayStatus(ObjectReference self) throws RemoteException, WTException { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        Boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateDelayStatus(list, isFromRecover);
    }

    public static Boolean updateStatusByRecover(WfProcess process) { // add by lkc 2018.1.25

        String dept;
        try {
            dept = PrintUtil.getUserDepartment();
            String infor = process.getContext().getValue("infor").toString();
            List<String> list = new ArrayList<String>();
            String[] temp = infor.split(":");
            for (String string : temp) {
                list.add(string);
            }
            return PrintUserCodeProcessor.updateStatusByRecover(list, dept);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean updateStatusByStore(WfProcess process) { // add by lkc 2018.1.25

        String dept;
        try {
            dept = PrintUtil.getUserDepartment();
            String infor = process.getContext().getValue("infor").toString();
            List<String> list = new ArrayList<String>();
            String[] temp = infor.split(":");
            for (String string : temp) {
                list.add(string);
            }
            return PrintUserCodeProcessor.updateStatusByStore(list, dept);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean checkSureOfRecover(WfProcess process) { // add by lkc 2018.1.25

        String infor = process.getContext().getValue("infor").toString();
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        return PrintUserCodeProcessor.checkSureOfRecover(list);
    }

    public static Boolean checkSureOfStore(WfProcess process) { // add by lkc 2018.1.25

        String infor = process.getContext().getValue("infor").toString();
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        return PrintUserCodeProcessor.checkSureOfStore(list);
    }

    public static Boolean updateStatusOfChange(WTObject pbo) { // add by lkc 2018.1.25
        String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
        String dept = null;
        try {
            dept = PrintUtil.getUserDepartment();
            return PrintUserCodeProcessor.updateStatusOfChange(pboOid, dept);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean checkSureOfChangeRecover(WTObject pbo) {
        String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo));
        return PrintUserCodeProcessor.checkSureOfChangeRecover(pboOid);
    }

    //将数据库中的文件状态改为已回收
    public static void updateRecoverStatus(ObjectReference self) { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateRecoverStatus(list);
    }

    public static void updateStoreStatusBySelf(List<String> list) { // add by lkc 2018.1.25
        PrintUserCodeProcessor.updateStoreStatusBySelf(list);
    }

    /**
     * 点击启封确认时修改文件状态
     *
     * @param list
     * @throws RemoteException
     * @throws InvocationTargetException
     * @author jyx
     * @date 2018-5-21
     */
    public static void updateFileStatusOfOpenStore(List<String> list) { // add by lkc 2018.1.25
        PrintUserCodeProcessor.updateFileStatusOfOpenStore(list);
    }

    public static List<String> queryRecoverTableID(String pboOid) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryRecoverTableID(pboOid);
    }

    public static void updateRecoverStatusOfChange(WTObject pbo) { // add by lkc 2018.1.25
        ReferenceFactory rf = new ReferenceFactory();
        String pboOid;
        try {
            pboOid = rf.getReferenceString((Persistable) pbo);
            PrintUserCodeProcessor.updateRecoverStatusOfChange(pboOid);
        } catch (WTException e) {
            e.printStackTrace();
        }
    }

    public static void updateChangeRecoverData(WTObject pbo) { // add by lkc 2018.1.25
        ReferenceFactory rf = new ReferenceFactory();
        String pboOid;
        try {
            pboOid = rf.getReferenceString((Persistable) pbo);
            PrintUserCodeProcessor.updateChangeRecoverData(pboOid);
        } catch (WTException e) {
            e.printStackTrace();
        }
    }

    public static void updateRecoverTime(ObjectReference self) { // add by lkc 2018.1.25
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String time = sdf.format(nowDate);
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateRecoverTime(list, time);
    }

    public static void updateRecoverTimeOfDelay(ObjectReference self) { // add by lkc 2018.1.25
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String time = sdf.format(nowDate);
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        Boolean isFromRecover = (Boolean) proc.getContext().getValue("isFromRecover");
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateRecoverTimeOfDelay(list, time, isFromRecover);
    }


    public static void updateFileStatus(ObjectReference self) { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateFileStatus(list);
    }

    public static void updateStoreTime(ObjectReference self) { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateStoreTime(list);
    }

    public static void updateOpenStore(ObjectReference self) { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateOpenStore(list);
    }

    public static void updateFileStatusOfOpenStore(ObjectReference self) { // add by lkc 2018.1.25
        WfProcess proc = (WfProcess) self.getObject();
        String infor = proc.getContext().getValue("infor").toString();
        List<String> list = new ArrayList<String>();
        String[] temp = infor.split(":");
        for (String string : temp) {
            list.add(string);
        }
        PrintUserCodeProcessor.updateFileStatusOfOpenStore(list);
    }

    //启动自行发起回收流程
    public static Boolean startProcessOfRecover(String name, List<String> list) throws WTPropertyVetoException, WTException, IOException { // add by lkc 2018.1.25
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        Boolean flag = false;
        try {
            tx.start();
            WTContainer wtContainer = getContainerByName("打印分发管理库");
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            String userFullName = user.getFullName();//获取用户名
            String todayDate = DateUtil.getTodayDate("yyyy-MM-dd");
            String docName = userFullName + "-" + todayDate + "-回收申请单";
            WTDocument doc = WTDocumentUtil.createDocument(null, docName, wtContainer, "Default/回收申请单", "casc.sast.149.PRINTRECOVERFORM");
            if ("自行发起回收".equals(name)) {
                WorkflowUtil.startRecoverWfProcess(doc.getContainerReference(), doc, "打印文件回收流程", new HashMap<String, String>(), list);
                flag = true;
            } else if ("直接延迟回收".equals(name)) {
                WorkflowUtil.startSubmitDelayWfProcessByRecover(doc.getContainerReference(), doc, "直接提交文件延迟流程", new HashMap<String, String>(), list);
                flag = true;
            } else if ("直接延迟封存".equals(name)) {
                WorkflowUtil.startSubmitDelayWfProcessByStore(doc.getContainerReference(), doc, "直接提交文件延迟流程", new HashMap<String, String>(), list);
                flag = true;
            }
            tx.commit();
            tx = null;
        } catch (PersistenceException e) {

            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return flag;
    }

    //启动自行发起厂内工艺文件回收流程
    public static Boolean startProcessOfRecoverInFactory(String name, List<String> list) throws WTPropertyVetoException, WTException, IOException { // add by lkc 2018.1.25
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        Boolean flag = false;
        try {
            tx.start();
            WTContainer wtContainer = getContainerByName("打印分发管理库");
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            String userFullName = user.getFullName();//获取用户名
            String todayDate = DateUtil.getTodayDate("yyyy-MM-dd");
            String docName = userFullName + "-" + todayDate + "-回收申请单";
            WTDocument doc = WTDocumentUtil.createDocument(null, docName, wtContainer, "Default/回收申请单", "casc.sast.149.PRINTRECOVERFORM");
            if ("自行发起回收".equals(name)) {
                WorkflowUtil.startRecoverWfProcess(doc.getContainerReference(), doc, "厂内工艺文件回收流程", new HashMap<String, String>(), list);
                flag = true;
            }
            tx.commit();
            tx = null;
        } catch (PersistenceException e) {

            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return flag;
    }

    //回收中发起的延迟流程
    public static String startProcessOfDelay(String name, List<String> list) throws WTPropertyVetoException, WTException, IOException { // add by lkc 2018.1.25
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        Boolean flag = false;
        String pboNumber = null;
        try {
            tx.start();
            WTContainer wtContainer = getContainerByName("打印分发管理库");
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            String userFullName = user.getFullName();//获取用户名
            String todayDate = DateUtil.getTodayDate("yyyy-MM-dd");
            String docName = userFullName + "-" + todayDate + "-回收申请单";
            WTDocument doc = WTDocumentUtil.createDocument(null, docName, wtContainer, "Default/回收申请单", "casc.sast.149.PRINTRECOVERFORM");
            pboNumber = doc.getNumber();
            if ("回收延迟申请".equals(name)) {
                WorkflowUtil.startDelayWfProcessOfRecover(doc.getContainerReference(), doc, "文件延迟申请流程", new HashMap<String, String>(), list);
                flag = true;
            } else if ("封存延迟申请".equals(name)) {
                WorkflowUtil.startDelayWfProcessOfStore(doc.getContainerReference(), doc, "文件延迟申请流程", new HashMap<String, String>(), list);
                flag = true;
            }
            tx.commit();
            tx = null;
        } catch (PersistenceException e) {
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return pboNumber;
    }

    //启动遗失流程
    public static String startProcessOfLose(String name, List<String> list) throws WTPropertyVetoException, WTException, IOException { // add by lkc 2018.1.25
        String result = "";
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        try {
            tx.start();
            WTContainer wtContainer = getContainerByName("打印分发管理库");
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            String userFullName = user.getFullName();//获取用户名
            String todayDate = DateUtil.getTodayDate("yyyy-MM-dd");
            String docName = userFullName + "-" + todayDate + "-遗失申请单";
            WTDocument doc = WTDocumentUtil.createDocument(null, docName, wtContainer, "Default/遗失申请单", "casc.sast.149.PRINTLOSEFORM");
            if ("回收遗失申请".equals(name)) {
                WorkflowUtil.startLoseWfProcessOfRecover(doc.getContainerReference(), doc, "文件遗失申请流程", new HashMap<String, String>(), list);
                result = doc.getNumber();
            } else if ("遗失申请".equals(name)) {
                WorkflowUtil.startLoseWfProcess(doc.getContainerReference(), doc, "文件遗失申请流程", new HashMap<String, String>(), list);
                result = doc.getNumber();
            } else if ("封存遗失申请".equals(name)) {
                WorkflowUtil.startLoseWfProcessOfStore(doc.getContainerReference(), doc, "文件遗失申请流程", new HashMap<String, String>(), list);
                result = doc.getNumber();
            }
            tx.commit();
            tx = null;
        } catch (PersistenceException e) {

            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return result;
    }

    //启动封存与启封流程
    public static Boolean startProcessOfStore(String name, List<String> list) throws WTPropertyVetoException, WTException, IOException { // add by lkc 2018.1.25
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        Boolean flag = false;
        try {
            tx.start();
            WTContainer wtContainer = getContainerByName("打印分发管理库");
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            String userFullName = user.getFullName();//获取用户名
            String todayDate = DateUtil.getTodayDate("yyyy-MM-dd");
            String docName = userFullName + "-" + todayDate + "-封存申请单";
            WTDocument doc = WTDocumentUtil.createDocument(null, docName, wtContainer, "Default/封存申请单", "casc.sast.149.PRINTSTOREFORM");
            if ("文件封存".equals(name)) {
                WorkflowUtil.startStoreWfProcess(doc.getContainerReference(), doc, "纸质文件封存流程", new HashMap<String, String>(), list);
                flag = true;
            } else if ("文件启封".equals(name)) {
                WorkflowUtil.startOpenStoreWfProcess(doc.getContainerReference(), doc, "文件启封流程", new HashMap<String, String>(), list);
                flag = true;
            }
            tx.commit();
            tx = null;
        } catch (PersistenceException e) {

            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
        return flag;
    }

    //延迟回收逾期通知流程
    public static void startProcessOfOverTime(List<String> list) throws WTPropertyVetoException, WTException, IOException { // add by lkc 2018.1.25
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        try {
            tx.start();
            WTContainer wtContainer = getContainerByName("打印分发管理库");
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            String userFullName = user.getFullName();//获取用户名
            String todayDate = DateUtil.getTodayDate("yyyy-MM-dd");
            String docName = userFullName + "-" + todayDate + "-回收申请单";
            WTDocument doc = WTDocumentUtil.createDocument(null, docName, wtContainer, "Default/回收申请单", "casc.sast.149.PRINTRECOVERFORM");
            WorkflowUtil.startOutTimeWfProcess(doc.getContainerReference(), doc, "延迟回收逾期通知流程", new HashMap<String, String>(), list);
            tx.commit();
            tx = null;
        } catch (PersistenceException e) {

            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();
        }
    }


//	public static boolean hasOpenRunningWorkflow(Persistable p) throws WTException {
//		Boolean flag = false;
//		WTContained contained = (WTContained) p;
//		WfProcess process = null;
//		int i = 0;
//		Enumeration enums = WfEngineHelper.service.getAssociatedProcesses(p, WfState.OPEN,
//				contained.getContainerReference());
//		while(enums.hasMoreElements()) {
//			process = (WfProcess) enums.nextElement();
//			i = i + 1;
//		}
//		if(i != 1){
//			flag = false;
//		}else{
//			flag = true;
//		}
//		return flag;
//	}

    @SuppressWarnings("deprecation")
    public static WTContainer getContainerByName(String name) throws WTException { // add by lkc 2018.1.25
        WTContainer container = null;
        QuerySpec querySpec = new QuerySpec(WTContainer.class);
        querySpec.appendWhere(new SearchCondition(WTContainer.class, _WTContainer.NAME, SearchCondition.EQUAL, name));
        QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
        if (queryResult.hasMoreElements()) {
            container = (WTContainer) queryResult.nextElement();
        }
        return container;
    }


    public static String createGwPrintApplyRecords(List<CmPrintInfoBean> list) {
        StringBuffer buf = new StringBuffer();
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        try {
            tx.start();
            //创建“文件打印单”文档对象
            WTDocument doc = PrintRecordHelper.service.createProcessPrintDoc(list);
            //对工艺文件PDF文件加盖技术状态章、生成分发部门和份数信息、生成二维码信息
            File zipFile = PrintRecordHelper.service.generalPDFFile(list, doc.getPersistInfo().getObjectIdentifier().getId());
            //上传PDF文件包
            PrintRecordHelper.service.uploadPDFFile(doc, zipFile);
            //删除临时目录
            PrintUtil.deleteTempFiles(String.valueOf(doc.getPersistInfo().getObjectIdentifier().getId()));
            for (CmPrintInfoBean cmPrintInfoBean : list) {
                cmPrintInfoBean.setProcessPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
                //创建打印申请记录
                String result1 = PrintRecordHelper.service.createGwPrintApplyRecord(cmPrintInfoBean);
                if (result1.equals("")) {
                    //创建打印分发记录
                    String result2 = PrintRecordHelper.service.createGwPrintDistributeRecord(cmPrintInfoBean);
                    buf.append(result2);
                } else {
                    buf.append(result1);
                }
            }
            //启动工艺文件打印申请签审流程
            if (buf.toString().equals("")) {
                WorkflowUtil.startWfProcess(doc.getContainerReference(), doc, PrintServerConstants.WORKFLOWNAME_PRINTAPPLY, new HashMap<String, String>());
                buf.append("工艺文件打印申请成功！");
            }
            tx.commit();
            tx = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
                buf.setLength(0);
                buf.append("工艺文件打印申请失败");
            }
            SessionServerHelper.manager.setAccessEnforced(access);
        }

        return buf.toString();
    }

    public static String saveGwPrintApplyRecords(List<CmPrintInfoBean> list, String pboOid) {
        StringBuffer buf = new StringBuffer();
        try {
            String result = PrintRecordHelper.service.saveGwPrintApplyRecord(list, pboOid);
            buf.append(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString();
    }


    public static String getPrintObjNumber(String objType, String preFix) {
        return PrintUtil.getPrintObjNumber(objType, preFix);
    }

    public static String updatePrintState(String qrCodeNumber, long userOid) {
        String result = "";
        Transaction tx = new Transaction();
        try {
            tx.start();
            GWPrintApplyRecord gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByQRCode(qrCodeNumber);
            if (gwPrintApplyRecord != null) {
                gwPrintApplyRecord.setPrintStatus(PrintServerConstants.PRINTSTATUS_YDY);
                //设置打印人
                gwPrintApplyRecord.setPrintor(userOid);

                //设置打印日期
                Date date = new Date();
                gwPrintApplyRecord.setPrintDate(new Timestamp(date.getTime()));
                GwPersistenceHelper.manager.save(gwPrintApplyRecord);
            } else {
                result = "更新打印状态失败！\n尚未生成打印记录！";
            }
            tx.commit();
            tx = null;
        } catch (Exception e) {
            result = "更新打印状态失败！\n";
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
            }
        }
        return result;
    }

    public static String updateRejectState(List<String[]> list) throws RemoteException, InvocationTargetException {
        String result = "";
        Transaction tx = new Transaction();
        try {
            tx.start();
            GWPrintApplyRecord gwPrintApplyRecord = null;
            for (String[] str : list) {
                String qrCodeNumber = str[0];
                String rejectRemark = str[1];
                gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByQRCode(qrCodeNumber);
                if (gwPrintApplyRecord != null) {
                    gwPrintApplyRecord.setRejectStatus(PrintServerConstants.REJECTSTATUS_YBH);
                    gwPrintApplyRecord.setRejectRemark(rejectRemark);
                    GwPersistenceHelper.manager.save(gwPrintApplyRecord);
                } else {
                    result = "更新驳回状态失败！\n尚未生成打印记录！";
                }
            }
            tx.commit();
            tx = null;
        } catch (Exception e) {
            result = "更新驳回状态失败！\n";
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
            }
        }
        return result;
    }

    //通过编号获得最近版本文件的版本
    public static String getLatestDocumentVersionByNumber(String number) throws WTException {
        String bigVersion = null;
        String smallVersion = null;
        String lastVersion = null;
        WTDocument doc = null;
        QuerySpec qSpec = new QuerySpec(WTDocument.class);
        qSpec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL,
                number), index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        qResult = new LatestConfigSpec().process(qResult);
        while (qResult.hasMoreElements()) {
            doc = (WTDocument) qResult.nextElement();
            //LastVersion = doc.getVersionIdentifier().toString();
            bigVersion = doc.getVersionInfo().getIdentifier().getValue();
            smallVersion = doc.getIterationInfo().getIdentifier().getValue();
            lastVersion = bigVersion + "." + smallVersion;
        }
        return lastVersion;
    }

    public static String getNumberByOid(String oid) {
        String number = null;
        ReferenceFactory renferenceFactory = new ReferenceFactory();
        try {
            Object object = renferenceFactory.getReference(oid).getObject();
            if (object instanceof WTDocument) {
                WTDocument doc = (WTDocument) object;
                number = doc.getNumber();
            }
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return number;
    }

    public static List<CmAttachment> getPdfByOid(List<String> list) {
        List<CmAttachment> attachList = new ArrayList<CmAttachment>();
        for (String oid : list) {
            Object obj = null;
            if (oid == null || "".equals(oid)) {
                return null;
            }
            if (oid.indexOf("%3A") > -1) {
                oid = oid.replaceAll("%3A", ":");
            }
            String QRName = oid.substring(oid.indexOf("|") + 1, oid.length());
            oid = oid.substring(0, oid.indexOf("|"));
            ReferenceFactory renferenceFactory = new ReferenceFactory();
            WTReference wtReference;
            try {
                wtReference = renferenceFactory.getReference(oid);
                obj = wtReference.getObject();
                if (obj instanceof Persistable) {
                    Persistable per = (Persistable) obj;
                    CmAttachment attachment = PrintUtil.getPDFSignFileAttachment((ContentHolder) per, oid);
                    if (attachment != null) {
                        attachment.setFileName(QRName + "_sign.pdf");
                        attachList.add(attachment);
                        attachment.setNumber(oid);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return attachList;
    }

    public static List<CmAttachment> getPdfFiles(List<String> list, String oid) throws Exception {
        List<CmAttachment> attachList = new ArrayList<CmAttachment>();
        for (String obj : list) {
            String objOid = obj.substring(0, obj.indexOf("|"));
            String QRName = obj.substring(obj.indexOf("|") + 1, obj.length());
            Persistable per = PersistableUtil.getPersistable(objOid);
            if (oid == null || oid.length() == 0) {
                return null;
            }
            CmAttachment attachment = PrintUtil.getPDFSignFileAttachment(per, oid);
            if (attachment != null) {
            	String sc = "";
                if(per instanceof WTObject){
                    String secret = IBAHelper.getIBAStringValue((WTObject) per, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        sc = "（" + secret + "）";
                    }
                }
                attachment.setFileName(QRName + "_sign"+sc+".pdf");
                attachment.setNumber(objOid);
                attachList.add(attachment);
            }
        }
        return attachList;
    }

    public static List<CmAttachment> printPDFAndReturn(List<CmPrintInfoBean> list) throws Exception {
        List<CmAttachment> attachList = PrintPDFUtil.printPDFAndReturn(list);
        //删除临时目录
        FileUtil.makeTmpDir(PrintServerConstants.FOLDER_PRINT);
        return attachList;
    }

    public static List<CmPrintInfoBean> addPrintFiles(CmPrintQueryBean cmPrintQueryBean) throws Exception {
        return PrintRecordHelper.service.queryPrintFiles(cmPrintQueryBean);
    }

    public static List<CmPrintInfoBean> addPrintApplicationFiles(CmPrintQueryBean cmPrintQueryBean) throws Exception {
        return PrintRecordHelper.service.queryPrintApplicationFiles(cmPrintQueryBean);
    }

    public static List<CmPrintInfoBean> addFilesOnBom(CmPrintQueryBean cmPrintQueryBean) throws Exception {
        return PrintRecordHelper.service.queryFilesOnBom(cmPrintQueryBean);
    }

    public static List<CmPrintInfoBean> addBomFiles(CmPrintInfoBean cmPrintInfoBean) throws Exception {
        return PrintRecordHelper.service.queryBomFiles(cmPrintInfoBean);
    }

    public static List<String> getAllChildPartOid(CmPrintInfoBean cmPrintInfoBean) throws Exception {
    	return PrintRecordHelper.service.getAllChildPartOid(cmPrintInfoBean);
    }

    public static List<CmPrintInfoBean> queryBomFilesByPart(String partOid, String mainTechnics) throws Exception {
    	return PrintRecordHelper.service.queryBomFilesByPart(partOid, mainTechnics);
    }

    public static Map<String, String> getReceiptPerson(String userName) throws Exception {
        return PrintRecordHelper.service.queryWTUserInfo(userName);
    }

    public static List<CmPrintInfoBean> printFilesMgt(CmPrintQueryBean cmPrintQueryBean, boolean isZxdy) throws Exception {
        return PrintRecordHelper.service.printFilesMgt(cmPrintQueryBean, isZxdy);
    }

    public static String isCanGet(String barCode, String receiptDept) throws Exception {
        String result = "";
        GWPrintDistributeRecord gwPrintDistributeRecord = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCodeAndDept(barCode, receiptDept);
        if (gwPrintDistributeRecord != null) {
            long distributeCount = gwPrintDistributeRecord.getDistributeQuantity();
            long getCount = gwPrintDistributeRecord.getGetQuantity();
            if (distributeCount == getCount) {
                result = receiptDept + "的工艺文件已全部领取！";
            }
        } else {
            result = receiptDept + "没有分发工艺文件！";
        }
        return result;
    }

    public static List<CmPrintInfoBean> getReceiptFile(String barCode, String receiptDept) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        list.add(PrintDataBuildUtil.buildReceiptFileInfo(barCode, receiptDept));
        return list;
    }

    public static String updateReceiptInfo(List<CmPrintInfoBean> list) {
        StringBuffer result = new StringBuffer();
        Transaction tx = new Transaction();
        try {
            tx.start();
            for (CmPrintInfoBean cmPrintInfoBean : list) {
                result.append(GWPrintDistributeRecordManager.updateReceiptInfo(cmPrintInfoBean));
            }
            tx.commit();
            tx = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
            }
        }
        return result.toString();
    }

    public static String isCanRecover(String barCode, String recoverDept) throws Exception {
        String result = "";
        GWPrintDistributeRecord gwPrintDistributeRecord = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCodeAndDept(barCode, recoverDept);
        if (gwPrintDistributeRecord != null) {
            String getDate = CommonUtil.objectToString(gwPrintDistributeRecord.getGetDate());
            String getDept = CommonUtil.objectToString(gwPrintDistributeRecord.getGetDept());
            String getUser = CommonUtil.objectToString(gwPrintDistributeRecord.getGetUser());
            long getCount = gwPrintDistributeRecord.getGetQuantity();
            if (!getDate.equals("") && !getDept.equals("") && !getUser.equals("") && getCount != 0) {
                GWPrintRecoverRecord gwPrintRecoverRecord = GWPrintRecoverRecordManager.queryGWPrintRecoverRecordByBarCodeAndDept(barCode, recoverDept);
                if (gwPrintRecoverRecord != null) {
                    long recoverCount = gwPrintRecoverRecord.getRecoverQuantity();
                    if (getCount == recoverCount) {
                        result = "该工艺文件所有已领取文件已全部退回！";
                    }
                }
            } else {
                result = "未查询到" + recoverDept + "的领取信息！";
            }
        } else {
            result = "未查询到" + recoverDept + "的分发信息！";
        }
        return result;
    }

    public static List<CmPrintInfoBean> ylqFileAddQuery(CmPrintQueryBean cmPrintQueryBean) throws Exception {
        return PrintRecordHelper.service.ylqFileAddQuery(cmPrintQueryBean);
    }

    public static List<CmPrintInfoBean> getRecoverFile(String barCode, String recoverDept) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        list.add(PrintDataBuildUtil.buildRecoverFileInfo(barCode, recoverDept));
        return list;
    }

    public static String createGwPrintRecoverRecords(List<CmPrintInfoBean> list, String oid) {
        StringBuffer buf = new StringBuffer();
        WTDocument doc = null;
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        try {
            tx.start();
            if (oid != null && !"".equals(oid)) {
                doc = (WTDocument) PersistableUtil.getPersistable(oid);
            } else {
                //创建“文件退回申请单”文档对象
                doc = PrintRecordHelper.service.createProcessPrintRecoverDoc(list);
            }
            List<GWPrintRecoverRecord> gwPrintRecoverRecords = GWPrintRecoverRecordManager.queryGWPrintRecoverRecordByPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
            for (GWPrintRecoverRecord gwPrintRecoverRecord : gwPrintRecoverRecords) {
                GwPersistenceHelper.manager.delete(gwPrintRecoverRecord);
            }
            for (CmPrintInfoBean cmPrintInfoBean : list) {
                cmPrintInfoBean.setProcessPrintFileOid(doc.getPersistInfo().getObjectIdentifier().getId());
                //创建退回申请记录
                String result1 = PrintRecordHelper.service.createGwPrintRecoverRecord(cmPrintInfoBean);
                if (!result1.equals("")) {
                    buf.append(result1);
                    return buf.toString();
                }
            }
            if (oid == null || "".equals(oid)) {
                //启动工艺文件退回申请签审流程
                WorkflowUtil.startWfProcess(doc.getContainerReference(), doc, PrintServerConstants.WORKFLOWNAME_RECOVERAPPLY, new HashMap<String, String>());
                if (buf.toString().equals("")) {
                    buf.append("工艺文件退回申请成功！");
                }
            } else {
                if (buf.toString().equals("")) {
                    buf.append("工艺文件退回申请修改成功！");
                }
            }
            tx.commit();
            tx = null;
        } catch (Exception e) {
            buf.append("工艺文件打印回收（销毁）签审流程启动失败！");
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
            }
            //收回权限
            SessionServerHelper.manager.setAccessEnforced(access);
        }

        return buf.toString();
    }

    public static String updateRecoverInfo(List<CmPrintInfoBean> list) {
        StringBuffer result = new StringBuffer();
        Transaction tx = new Transaction();
        try {
            tx.start();
            for (CmPrintInfoBean cmPrintInfoBean : list) {
                result.append(GWPrintRecoverRecordManager.updateRecoverInfo(cmPrintInfoBean));
            }
            tx.commit();
            tx = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
            }
        }
        return result.toString();
    }

    public static Map<String, String> queryUserInfoByCode(String userCode) throws Exception {
        return PrintRecordProcessor.queryUserInfoByCode(userCode);
    }

    public static String cancelPrintState(String qrCodeNumber) {
        String result = "";
        Transaction tx = new Transaction();
        try {
            tx.start();
            GWPrintApplyRecord gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByQRCode(qrCodeNumber);
            if (gwPrintApplyRecord != null) {
                gwPrintApplyRecord.setPrintStatus(PrintServerConstants.PRINTSTATUS_WDY);
                //设置打印人
                gwPrintApplyRecord.setPrintor(0);
                //设置打印日期
                gwPrintApplyRecord.setPrintDate(null);
                GwPersistenceHelper.manager.save(gwPrintApplyRecord);
            } else {
                result = "更新打印状态失败！\n尚未生成打印记录！";
            }
            tx.commit();
            tx = null;
        } catch (Exception e) {
            result = "更新打印状态失败！\n";
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
            }
        }
        return result;
    }

    public static List<CmPrintInfoBean> getDistributeAndRecoverInfo(String qrCodeNumber) throws Exception {
        List<CmPrintInfoBean> cmPrintInfoBeans = new ArrayList<CmPrintInfoBean>();
        List<GWPrintDistributeRecord> gwPrintDistributeRecords = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCode(qrCodeNumber);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        for (GWPrintDistributeRecord gwPrintDistributeRecord : gwPrintDistributeRecords) {
            CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
            long userOid = gwPrintDistributeRecord.getGetUser();
            String getUser = "";
            if (userOid != 0) {
                getUser = UserUtil.getWTUser(userOid).getFullName();
                Date getDate = gwPrintDistributeRecord.getGetDate();
                String getDept = gwPrintDistributeRecord.getGetDept();

                cmPrintInfoBean.setReceipter(getUser);
                if (getDate != null) {
                    cmPrintInfoBean.setReceiptDate(dateFormat.format(getDate));
                }
                cmPrintInfoBean.setReceiptDept(getDept);

                GWPrintRecoverRecord gwPrintRecoverRecord = GWPrintRecoverRecordManager.queryGWPrintRecoverRecordByBarCodeAndDept(qrCodeNumber, getDept);
                if (gwPrintRecoverRecord != null) {
                    long recoverUserOid = gwPrintRecoverRecord.getRecoverUser();
                    String recoverUser = "";
                    if (recoverUserOid != 0) {
                        recoverUser = UserUtil.getWTUser(recoverUserOid).getFullName();
                    }
                    Date recoverDate = gwPrintRecoverRecord.getRecoverDate();
                    String recoverDept = gwPrintRecoverRecord.getRecoverDept();
                    String recoverRemark = gwPrintRecoverRecord.getRecoverRemark();
                    long receiverUserOid = gwPrintRecoverRecord.getReceiveUser();
                    String receiverUser = "";
                    if (receiverUserOid != 0) {
                        receiverUser = UserUtil.getWTUser(receiverUserOid).getFullName();
                    }
                    Date receiverDate = gwPrintRecoverRecord.getReceiveDate();
                    long receiverQuantity = gwPrintRecoverRecord.getReceiveQuantity();

                    cmPrintInfoBean.setRecoverPerson(recoverUser);
                    if (recoverDate != null) {
                        cmPrintInfoBean.setRecoverDate(dateFormat.format(recoverDate));
                    }
                    cmPrintInfoBean.setRecoverDept(recoverDept);
                    cmPrintInfoBean.setRecoverRemark(recoverRemark);
                    cmPrintInfoBean.setReceivePerson(receiverUser);
                    if (receiverDate != null) {
                        cmPrintInfoBean.setReceiveDate(dateFormat.format(receiverDate));
                    }
                    cmPrintInfoBean.setReceiveCount(String.valueOf(receiverQuantity));
                }

                cmPrintInfoBeans.add(cmPrintInfoBean);
            }
        }

        return cmPrintInfoBeans;
    }

    public static Map<String, byte[]> getPdfFileForLookUp(List<CmPrintInfoBean> list) throws Exception {
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        for (CmPrintInfoBean cmPrintInfoBean : list) {
            String oid = cmPrintInfoBean.getOid();
            Persistable per = PersistableUtil.getPersistable(oid);
            ApplicationData appData = null;
            if (per instanceof MPMProcessPlan
                    || per instanceof WTDocument) {
                appData = WCUtil.getRepresentation((Representable) per);
            }

            if (appData == null) {
                appData = PrintUtil.getPDFFile((ContentHolder) per);
            }

            if (appData != null) {
                long id = per.getPersistInfo().getObjectIdentifier().getId();
                byte[] bytes = CommonUtil.applicationDataToByte(appData);
                map.put(String.valueOf(id), bytes);
            }
        }
        return map;
    }

    public static Map<String, byte[]> checkIsHasPrint(List<CmPrintInfoBean> list) throws Exception {
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        for (CmPrintInfoBean cmPrintInfoBean : list) {
            String oid = cmPrintInfoBean.getOid();
            Persistable per = PersistableUtil.getPersistable(oid);
            ApplicationData appData = null;

            if (appData == null) {
                appData = PrintUtil.getPDFFile((ContentHolder) per);
            }

            if (appData != null) {
                long id = per.getPersistInfo().getObjectIdentifier().getId();
                byte[] bytes = CommonUtil.applicationDataToByte(appData);
                map.put(String.valueOf(id), bytes);
            }
        }
        return map;
    }

    public static List<CmPrintRecordInfoBean> queryDelayInfo(CmPrintRecordInfoBean cmPrintRecordQueryBean, List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryDelayInfo(cmPrintRecordQueryBean, list);
    }

    //文件入库管理中查看文件分发信息
    public static List<CmPrintRecordInfoBean> queryDistributeInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean, String category) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryDistributeInfo(cmPrintRecordQueryBean, category);
    }

    //厂内文件封存查看分发信息
    public static List<CmPrintRecordInfoBean> inFactoryQueryDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean, String category) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.inFactoryQueryDistributeInfoOfStore(cmPrintRecordQueryBean, category);
    }

    //外来文件封存查看分发信息
    public static List<CmPrintRecordInfoBean> outsideQueryDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.outsideQueryDistributeInfoOfStore(cmPrintRecordQueryBean);
    }

    //厂内纸质文件封存查看分发信息
    public static List<CmPrintRecordInfoBean> queryPaperDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryPaperDistributeInfoOfStore(cmPrintRecordQueryBean);
    }

    //保存封存文件封存时间
    public static String saveInfoOfStore(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.saveInfoOfStore(list);
    }

    //启封流程校验
    public static String checkInfoOfOpenStore(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.checkInfoOfOpenStore(list);
    }

    //直接提交延迟流程校验
    public static String checkInfoOfDelayByRecover(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.checkInfoOfDelayByRecover(list);
    }

    public static String checkInfoOfDelayByStore(List<CmPrintRecordInfoBean> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.checkInfoOfDelayByStore(list);
    }

    public static void updateOpenStoreStatus(List<String> list) { // add by lkc 2018.1.25
        PrintUserCodeProcessor.updateOpenStoreStatus(list);
    }

    public static void updateDelayStatusByRecover(List<String> list, String userName, String userDept) { // add by lkc 2018.1.25
        PrintUserCodeProcessor.updateDelayStatusByRecover(list, userName, userDept);
    }

    public static void updateDelayStatusByStore(List<String> list, String userName, String userDept) { // add by lkc 2018.1.25
        PrintUserCodeProcessor.updateDelayStatusByStore(list, userName, userDept);
    }

    public static List<CmPrintRecordInfoBean> storeInfoByDept(List<String> list, String dept) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.storeInfoByDept(list, dept);
    }

    public static List<CmPrintRecordInfoBean> storeInfo(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.storeInfo(list);
    }

    //	public static List<String> queryBarCodeIsDelay(List<String> list){
//		return PrintUserCodeProcessor.queryBarCodeIsDelay(list);
//	}
    public static List<CmPrintRecordInfoBean> queryRecoverTableIsDelay(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryRecoverTableIsDelay(list);
    }

    public static List<CmPrintRecordInfoBean> queryStoreTableIsDelay(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryStoreTableIsDelay(list);
    }

    public static Boolean isSubmitDelayByWfProcessOid(String oid) {//add by lkc 2018.01.04
        boolean isSubmit = WorkflowUtil.isSubmitDelayByWfProcessOid(oid);
        return isSubmit;
    }

    public static Boolean isSubmitDelayByWfProcessOidAndActivityNames(String oid, String activityNames) {//add by lkc 2018.01.04
        boolean isSubmit = WorkflowUtil.isSubmitDelayByWfProcessOidAndActivityNames(oid, activityNames);
        return isSubmit;
    }

    //延迟申请流程中保存收延迟原因和延迟时间
    public static String saveToRecoverTableDelayDate(List<CmPrintRecordInfoBean> list) {//add by lkc 2018.01.04
        return PrintUserCodeProcessor.saveToRecoverTableDelayDate(list);
    }

    //回收流程中延迟请求新增数据保存到数据库
    public static String saveToRecoverTableDelayInfo(List<CmPrintRecordInfoBean> list) {//add by lkc 2018.01.04
        return PrintUserCodeProcessor.saveToRecoverTableDelayInfo(list);
    }

    //封存流程中延迟请求新增数据保存到数据库
    public static String saveToStoreTableDelayInfo(List<CmPrintRecordInfoBean> list) {//add by lkc 2018.01.04
        return PrintUserCodeProcessor.saveToStoreTableDelayInfo(list);
    }

    public static Boolean saveToRecoverTableDelayInfor(List<CmPrintRecordInfoBean> list) throws WTPropertyVetoException, WTException, IOException {//add by lkc 2018.01.04
        return PrintUserCodeProcessor.saveToRecoverTableDelayInfor(list);
    }

    public static Boolean saveToStoreTableDelayInfor(List<CmPrintRecordInfoBean> list, Boolean isFromRecover) throws WTPropertyVetoException, WTException, IOException {//add by lkc 2018.01.04
        return PrintUserCodeProcessor.saveToStoreTableDelayInfor(list, isFromRecover);
    }

    public static List<String> queryBarTableID(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryBarTableID(list);
    }

    public static List<String> queryBarTableIDByOver(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryBarTableIDByOver(list);
    }

    public static List<String> queryBarTableIDByDelay(List<String> list, Boolean isFromRecover) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryBarTableIDByDelay(list, isFromRecover);
    }

    public static List<String> queryBarTableIDByChange(String pboOid) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryBarTableIDByChange(pboOid);
    }

    public static List<String> queryBarTableIDByDept(List<String> list, String dept) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryBarTableIDByDept(list, dept);
    }

    //封存流程中线下回收纸质文件中根据部门获得显示文件在条码表中id
    public static List<String> queryBarTableIDByDeptOfStore(List<String> list, String dept) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryBarTableIDByDeptOfStore(list, dept);
    }

    public static List<String> queryBarTableIDByDeptOfChange(String pboOid, String dept) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryBarTableIDByDeptOfChange(pboOid, dept);
    }

    public static List<CmPrintRecordInfoBean> queryRecoverTable(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryRecoverTable(list);
    }

    //封存流程中在纸质文件回收阶段获得待展示文件信息
    public static List<CmPrintRecordInfoBean> queryStoreTable(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryStoreTable(list);
    }

    public static List<CmPrintRecordInfoBean> queryImmediateSubmitDelayInfo(List<String> list, Boolean isFromRecover) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryImmediateSubmitDelayInfo(list, isFromRecover);
    }

    public static List<CmPrintRecordInfoBean> queryDelayFileInfo(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryDelayFileInfo(list);
    }

    public static List<CmPrintRecordInfoBean> queryDelayFileInfoByOver(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryDelayFileInfoByOver(list);
    }

    public static List<CmPrintRecordInfoBean> queryRecoverTableOfDelay(List<String> list) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryRecoverTableOfDelay(list);
    }

    public static List<CmPrintRecordInfoBean> queryStoreTableByDelay(List<String> list, Boolean isFromRecover) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.queryStoreTableByDelay(list, isFromRecover);
    }

    public static void updatePBONumberToDB(List<String> list, String pboNumber) { // add by lkc 2018.1.25
        PrintUserCodeProcessor.updatePBONumberToDB(list, pboNumber);
    }

    public static void updatePBONumberToDBByStore(List<String> list, String pboNumber) { // add by lkc 2018.1.25
        PrintUserCodeProcessor.updatePBONumberToDBByStore(list, pboNumber);
    }

    //	public static Boolean updateToDB(String barCode){
//		return PrintUserCodeProcessor.updateToDBInfo(barCode);
//	}
    //自行发起回收保存到回收表中部分
    public static String saveToDBOfRecover(List<CmPrintRecordInfoBean> listBean) { // add by lkc 2018.1.25
        return PrintUserCodeProcessor.saveToDBOfRecover(listBean);
    }

    public static void updateCurrentUserAndDept(List<String> list, String user, String dept) { // add by lkc 2018.1.25
        PrintUserCodeProcessor.updateCurrentUserAndDept(list, user, dept);
    }

    //厂内文件自行发起回收查询
    public static List<CmPrintRecordInfoBean> queryFileInfo(String fileNumber, String fileName, String category) throws Exception {//add by lkc 2017.12.20
        return PrintUserCodeProcessor.queryFileInfo(fileNumber, fileName, category);
    }

    //更改回收查询
    public static List<CmPrintRecordInfoBean> queryFileInfoByChange(List<String> list) throws Exception {//add by lkc 2017.12.20
        return PrintUserCodeProcessor.queryFileInfoByChange(list);
    }

    public static Boolean insertChangeRecoverToDB(List<CmPrintRecordInfoBean> listBean) throws Exception {//add by lkc 2017.12.20
        return PrintUserCodeProcessor.insertChangeRecoverToDB(listBean);
    }

    //外来文件自行发起回收查询
    public static List<CmPrintRecordInfoBean> queryOutsideFileInfo(String fileNumber, String fileName) throws Exception {//add by lkc 2017.12.20
        return PrintUserCodeProcessor.queryOutsideFileInfo(fileNumber, fileName);
    }

    //厂内纸质文件自行发起回收查询
    public static List<CmPrintRecordInfoBean> queryPaperFileInfo(String fileNumber, String fileName) throws Exception {//add by lkc 2017.12.20
        return PrintUserCodeProcessor.queryPaperFileInfo(fileNumber, fileName);
    }

    public static List<CmPrintRecordInfoBean> queryInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) throws Exception {//add by lkc 2017.12.12
        return PrintUserCodeProcessor.queryInfo(cmPrintRecordQueryBean);
    }

    public static String queryMaxNumber() {//add by lkc 2017.12.12
        return PrintUserCodeProcessor.queryMaxNumber();
    }

    //印章管理查询印章
    public static List<CmSealBean> selectSeal(String sealName) {//add by lkc 2017.12.12
        return PrintUserCodeProcessor.selectSealInfo(sealName);
    }

    //印章管理添加印章
    public static Boolean addSeal(String uuid, String name, String number) {//add by lkc 2017.12.12
        return PrintUserCodeProcessor.addSealInfo(uuid, name, number);
    }

    //印章管理修改印章
    public static Boolean alterSeal(String uuid, String name) {//add by lkc 2017.12.12
        return PrintUserCodeProcessor.alterSealInfo(uuid, name);
    }

    //印章管理删除印章
    public static Boolean deleteSeal(List<String> list) {//add by lkc 2017.12.12
        return PrintUserCodeProcessor.deleteSealInfo(list);
    }

    public static void updateNumberToDB(List<CmSealBean> listBean) {//add by lkc 2017.12.12
        PrintUserCodeProcessor.updateNumberToDB(listBean);
    }

    /**
     * 获取所有的工艺类型
     *
     * @return
     * @throws InvocationTargetException
     * @throws RemoteException
     */
    public static Vector<String> getAllMPMSkill() throws RemoteException, InvocationTargetException {
        return MPMUtil.getAllMPMSkill();
    }

    public static Vector<String> getProductMindex() throws WTException {
        return WTContainerUtil.getAllProductMindex();
    }

    /**
     * 获取打印申请内容
     *
     * @param oid
     * @return
     * @throws Exception
     */
    public static List<CmPrintInfoBean> loadPrintApplication(String oid) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        try {
            list = PrintDataLoadUtil.loadPrintApplication(oid);
        } catch (WTException e) {
            logger.error(e);
        }
        return list;
    }

    public static List<CmPrintInfoBean> loadPaperFile(String oid, String category) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        list = PrintDataLoadUtil.loadPaperFile(oid, category);
        return list;
    }

    public static void setPrintStatus(List<String> list, String status, String printer, String printDate) throws Exception {
        PrintDataBuildUtil.setPrintStatus(list, status, printer, printDate);
    }

    public static List<CmPrintInfoBean> searchSealPlus(CmPrintQueryBean cmPrintQueryBean, String category) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        list = PrintDataLoadUtil.searchSealPlus(cmPrintQueryBean, category);
        return list;
    }

    public static List<String> saveInfoToDB(List<String> strList, String userName, String dept, String date) {
        return PrintUserCodeProcessor.saveInfoToDB(strList, userName, dept, date);
    }

    public static List<String> saveInfoToDB2(List<String> strList, String userName, String dept, String date) {
        return PrintUserCodeProcessor.saveInfoToDB2(strList, userName, dept, date);
    }

    public static List<String> saveInfoToDBOfStore(List<String> strList, String userName, String dept, String date) {
        return PrintUserCodeProcessor.saveInfoToDBOfStore(strList, userName, dept, date);
    }

    public static ArrayList<CmSealBean> selectAllSeal() throws Exception {
        return PrintUserCodeProcessor.selectAllSeal();
    }

    //将历史信息导入数据库
    public static void inFactoryImportInfoToDB(List<CmImportBean> list) {
        PrintUserCodeProcessor.inFactoryImportInfoToDB(list);
    }

    //将历史信息导入数据库
    public static String inFactoryImportInfoToDB2(List<CmImportBean> list) {
        return PrintUserCodeProcessor.inFactoryImportInfoToDB2(list);
    }

    //将历史信息导入数据库
    public static void outsideImportInfoToDB(List<CmImportBean> list) {
        PrintUserCodeProcessor.outsideImportInfoToDB(list);
    }


    //延迟申请逾期查询数据库中是否存在延迟文件
    public static List<String> getDelayInfoByNumber(List<String> list) {
        return PrintUserCodeProcessor.getDelayInfoByNumber(list);
    }

    public static Map<String, String> getDelayDateByID(List<String> list) {
        return PrintUserCodeProcessor.getDelayDateByID(list);
    }

    public static String updatePrintAddSeal(List<CmSealBean> list, String oid) throws Exception {
        return PrintDataBuildUtil.updatePrintAddSeal(list, oid);
    }

    public static List<CmPrintInfoBean> loadSealPlus(String oid, String category) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        list = PrintDataBuildUtil.loadSealPlus(oid, category);
        return list;
    }

    public static List<CmPrintInfoBean> getQRbarcode(String oid) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        list = PrintDataBuildUtil.getQRbarcode(oid);
        return list;
    }

    public static String getCurrentTime() {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String time = sdf.format(nowDate);
        return time;
    }

    public static void execute() {
        List<String> list = new ArrayList<String>();
        List<String> listNumber = FolderUtil.execute();
        List<String> listID = PrintToWCIntfRMI.getDelayInfoByNumber(listNumber);
        if (listID.size() > 0) {
            Map<String, String> map = PrintToWCIntfRMI.getDelayDateByID(listID);
            for (String id : map.keySet()) {
                String delayDate = map.get(id);
                String time = getCurrentTime();
                Boolean flag;
                try {
                    flag = FolderUtil.CompareTime(time, delayDate);
                    if (flag) {
                        list.add(id);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println(list.size());
            try {
                PrintToWCIntfRMI.startProcessOfOverTime(list);
            } catch (WTPropertyVetoException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<CmPrintInfoBean> addFileOnProcessDirectory(CmPrintQueryBean cmPrintQueryBean) throws Exception {
        return PrintRecordHelper.service.queryFileOnProcessDirectory(cmPrintQueryBean);
    }

    public static List<CmPrintInfoBean> getProcessFiles(CmPrintInfoBean cmPrintInfoBean) throws Exception {
        return PrintRecordHelper.service.queryProcessFiles(cmPrintInfoBean);
    }

    public static List<String> setFileStatus(List<String> list, CmDistributionBean cmDistributionBean) throws Exception {
        return PrintDataBuildUtil.setFileStatus(list, cmDistributionBean);
    }

    public static CmPrintInfoBean addOutFile(CmPrintQueryBean cmPrintQueryBean, boolean isModify, String id) throws Exception {
        return PrintDataBuildUtil.addOutFile(cmPrintQueryBean, isModify, id);
    }

    public static List<CmPrintInfoBean> searchOutFile(CmPrintQueryBean cmPrintQueryBean) throws Exception {
        return PrintDataBuildUtil.searchOutFile(cmPrintQueryBean);
    }

    public static void saveChangeNoticeInfo(CmPrintQueryBean cmPrintQueryBean, String id, boolean isModify) {
        PrintDataBuildUtil.saveChangeNoticeInfo(cmPrintQueryBean, id, isModify);
    }

    public static List<String> getAddBatch(String oid) throws Exception {
        return PrintDataBuildUtil.getAddBatch(oid);
    }

    public static Boolean synchDangan(List<CmPrintRecordInfoBean> listBean, String category) {
        PrintUtil.synchDangan(listBean, category);
        Boolean flag = PrintUserCodeProcessor.SynchDangan(listBean);
        return flag;
    }

    public static void createElement(Element root, String name, String key, String value) {
        Element prop = DocumentHelper.createElement("prop");
        prop.addElement("propname").addText(name);
        prop.addElement("propsign").addText(key);
        if (value == null) {
            value = "";
        }
        prop.addElement("propvalue").addText(value);
        root.add(prop);
    }

    /**
     * 写入xml文档
     *
     * @param root
     * @param oid
     * @throws IOException
     */
    public static void writeXML(Element root, String tempPath, String oid) throws IOException {
        XMLWriter write = null;
        Document document = DocumentHelper.createDocument(root);
        File file = new File(tempPath);
        if (!file.exists()) {
            file.mkdir();
        }
        FileOutputStream os = new FileOutputStream(tempPath + File.separator + oid + ".xml");
        write = new XMLWriter(os, OutputFormat.createPrettyPrint());
        write.write(document);
        write.close();
    }

    /**
     * 打压缩包
     *
     * @param oid
     * @return
     * @throws IOException
     */
    public static String createZip(String temppath, String oid) throws IOException {
        InputStream is = null;
        String zipFile = temppath + File.separator + oid + ".zip";
        ZipOutputStream zos = new ZipOutputStream(new File(zipFile));
        zos.setEncoding("GBK");
        byte[] buf = new byte[1024];
        File fileList = new File(temppath);
        File[] files = fileList.listFiles();

        if (files != null) {

            for (File file : files) {

                if (!file.isDirectory()) {

                    is = new FileInputStream(file);

                    if (is != null) {
                        if (file.getName().endsWith(".xml")) {
                            zos.putNextEntry(new ZipEntry(file.getName()));
                            zos.setEncoding("GBK");
                            int len = 0;
                            while ((len = is.read(buf)) >= 0) {
                                zos.write(buf, 0, len);
                            }
                            is.close();
                        } else if (file.getName().startsWith("Print")) {
                            zos.putNextEntry(new ZipEntry(oid + File.separator + file.getName()));
                            zos.setEncoding("GBK");
                            int len = 0;
                            while ((len = is.read(buf)) >= 0) {
                                zos.write(buf, 0, len);
                            }
                            is.close();
                        }

                    }
                }
            }
        }
        zos.close();
        return zipFile;
    }

    public static List<CmPrintRecordInfoBean> queryReceiveInfo(String pboOid, String dept) {
        return PrintUserCodeProcessor.queryReceiveInfo(pboOid, dept);
    }

    public static List<CmPrintInfoBean> getReceiveData(String userName, String oid, String category) {
        List<CmPrintInfoBean> list = null;
        try {
            list = PrintRecordHelper.service.queryReceiveData(userName, oid, category);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getUserGroup() throws WTException {
        return PrintUtil.getUserGroup();
    }

    public static String getUserDepartment(String userName) throws WTException {
        return PrintUtil.getUserByName(userName);
    }

    public static String getUserDepartment() throws WTException {
        return PrintUtil.getUserDept();
    }

    public static String getUserName() throws WTException {
        return PrintUtil.getUserName();
    }

    public static String[] getAllDept() {
        return PrintUtil.getAllDept();
    }

    public static CmDistributionBean getReceiveMessage(String name) throws WTException {
        return PrintUtil.getDeptMessage(name);
    }

    public static String getCategory(String oid) {
        return PrintDataQueryUtil.getCategory(oid);
    }

    public static String getUserDept(String name) {
        try {
            return PrintUtil.getUserByName(name);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getImageByte(String name) {
        return PrintUtil.getImageByte(name);
    }

    public static List<CmPrintInfoBean> loadPrintBarCode(String oid, String category, String dept) {
        return PrintDataQueryUtil.loadPrintBarCode(oid, category, dept);
    }

    public static void deleteOutFileByID(List<CmPrintInfoBean> beanList) {
        PrintDataBuildUtil.deleteOutFileByID(beanList);
    }

    public static void setPrintStatus(List<String> list) {
        PrintDataBuildUtil.setPrintStatus(list);
    }

    public static List<CmPrintInfoBean> saveImportInfo(List<CmImportBean> beanList) {
        return PrintDataBuildUtil.saveImportInfo(beanList);
    }

    public static List<String> getTechnicsNumberByBOM(String value) {
        return PrintDataQueryUtil.getTechnicsNumberByBOM(value);
    }

    public static List<CmPrintRecordInfoBean> queryPrintInfo(List<String> technicsList) {
        return PrintDataQueryUtil.queryPrintInfo(technicsList);
    }

    public static List<String> getTechnicsNumberByProcessDirectory(String value) {
        return PrintDataQueryUtil.getTechnicsNumberByProcessDirectory(value);
    }

    public static List<CmPrintRecordInfoBean> queryRecoverValueByUser(List<String> strList, String userName) {
        return PrintDataQueryUtil.queryRecoverValueByUser(strList, userName);
    }

    public static void setProcessState(String pboOid, String state) {
        PrintWorkflowUtil.setProcessState(pboOid, state);
    }

    public static void setChangeFileState(List<String> strList) {
        PrintDataBuildUtil.setChangeFileState(strList);
    }

    public static void saveLosePboNumber(String pboNumber, List<CmPrintRecordInfoBean> listBean) {
        PrintDataBuildUtil.saveLosePboNumber(pboNumber, listBean);
    }

    public static String queryLosePboNumber(String barTableID) {
        return PrintDataQueryUtil.queryLosePboNumber(barTableID);
    }

    public static String querydelayPboNumber(String barTableID) {
        return PrintDataQueryUtil.querydelayPboNumber(barTableID);
    }

    public static void saveAddSealPlus(String id, String allBatch) {
        PrintDataBuildUtil.saveAddSealPlus(id, allBatch);
    }

    public static String getUserNameBySign(String scanInput) {
        return PrintDataQueryUtil.getUserNameBySign(scanInput);
    }

    public static List<CmPrintInfoBean> searchPrintInfo(CmPrintQueryBean cmPrintQueryBean) {
        return PrintDataQueryUtil.searchPrintInfo(cmPrintQueryBean);
    }

    public static String startPrintOffSet(List<CmPrintInfoBean> list) {
        try {
            return PrintRecordHelper.service.startPrintOffSet(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean checkContainerRole(String containerName) {
        return PrintUtil.checkContainerRole(containerName);
    }

    public static boolean checkRepeatOutFile(CmPrintQueryBean cmPrintQueryBean, String category) {
        return PrintDataQueryUtil.checkRepeatOutFile(cmPrintQueryBean, category);
    }

    public static boolean checkRepeatInputFile(String fileNumber, String version, String category) {
        return PrintDataQueryUtil.checkRepeatInputFile(fileNumber, version, category);
    }

    public static boolean isWfaComplete(String wfaOid) throws WTException {
        boolean isComplete = false;
        ReferenceFactory rf = new ReferenceFactory();
        ObjectReference self = (ObjectReference)rf.getReference(wfaOid);
        WfAssignedActivity wfa = (WfAssignedActivity)self.getObject();
        if(!"OPEN_RUNNING".equals(wfa.getState().toString())){
            isComplete = true;
        }
        return isComplete;
    }

	public static String getPartType(String partNumber) throws WTException, RemoteException {
		String result = "";
		WTPart part = WTPartUtil.getLatestPartByPartNumber(partNumber);
		if (part != null) {
			result = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(part);
		}
		return result;
	}

    public static Map<String, List<String>> getPrinterByDepts(String[] deptSeal) throws Exception {
        return PrintUserCodeProcessor.getPrinterByDepts(deptSeal);
    }

    public static String createPrintTransferProcess(List<CmPrintRecordInfoBean> list) throws Exception {
        StringBuffer buf = new StringBuffer();
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        try {
            tx.start();
            //创建“转移申请单”文档对象
            WTContainer wtContainer = PrintUtil.getContainerByName("打印分发管理库");
            CmPrintRecordInfoBean infoBean = list.get(0);
            String docName = infoBean.getFileName() + "," + UserUtil.getCurrentUser().getName() + "," + DateUtil.getTodayDate("yyyy-MM-dd");
            WTDocument doc = WTDocumentUtil.createDocument(null, docName, wtContainer, PrintServerConstants.TRANSFERAPPLY_LOCATION, PrintServerConstants.OBJTYPE_TRANSFERAPPLY);
//            启动工艺文件转移流程
            if (buf.toString().equals("")) {
                List<String> oidList = new ArrayList<String>();
                for(CmPrintRecordInfoBean bean : list) {
                    oidList.add(bean.getBarTableID() + "&" + bean.getTargetDept());
                }
                String ids = StringUtils.join(oidList, ",");
                Map<String,String> map = new HashMap<String, String>();
                map.put("transferInfo",ids);
                WorkflowUtil.startWfProcess(doc.getContainerReference(), doc, PrintServerConstants.WORKFLOWNAME_TRANSFERAPPLY, map);
                buf.append("文件转移申请成功！");
            }
            tx.commit();
            tx = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
                buf.setLength(0);
                buf.append("文件转移申请失败");
            }
            SessionServerHelper.manager.setAccessEnforced(access);
        }
        return buf.toString();
    }

    public static List<CmPrintRecordInfoBean> queryTransferInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) {
        return PrintUserCodeProcessor.queryTransferInfo(cmPrintRecordQueryBean);
    }

    public static List<CmPrintRecordInfoBean> loadTransferData(String oid) throws Exception {
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        WTDocument document = WTDocumentUtil.getWTDocumentByOid(oid);
        if(document != null){
            QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(document, null, null);
            while (qrProcs.hasMoreElements()){
                WfProcess wfProcess = (WfProcess) qrProcs.nextElement();
                String transferInfo = (String) wfProcess.getContext().getValue("transferInfo");
                list = PrintUserCodeProcessor.queryTransfer(transferInfo);
            }
        }
        return list;
    }

}