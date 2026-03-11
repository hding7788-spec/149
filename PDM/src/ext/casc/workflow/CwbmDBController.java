package ext.casc.workflow;

import com.glaway.mpm.util.DBConnUtil;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.constants.Constants;
import ext.casc.util.Tools;
import ext.casc.util.WCUtil;
import wt.fc.PersistenceHelper;
import wt.fc.WTReference;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pds.oracle81.OracleDataSource;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;

public class CwbmDBController {

    public static List<Map<String, Object>> getTempletaByName(String name) throws SQLException {
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        StringBuffer selectSQL = new StringBuffer();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        Map<String, Object> data = null;
        selectSQL.append("SELECT b.* from TEMPLATEMODEL a,TEMPLATEDETAIL b WHERE  a.TEMPLATE_NAME='" + name + "' AND a.ID=b.CONTACT_ID");
        try {
            ps = conn.prepareStatement(selectSQL.toString());
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = ps.getMetaData();
            // 取得结果集列数
            int columnCount = rsmd.getColumnCount();
            // 构造泛型结果集
            // 循环结果集
            while (rs.next()) {
                data = new HashMap<String, Object>();
                // 每循环一条将列名和列值存入Map
                for (int i = 1; i < columnCount; i++) {
                    data.put(rsmd.getColumnLabel(i), rs.getObject(rsmd.getColumnLabel(i)));
                }
                // 将整条数据的Map存入到List中
                datas.add(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

        return datas;

    }

    public static Map<String, String> selectTemplateIdAndTemplateName(String oid) throws Exception {
        WTUser user = null;
        user = (WTUser) SessionHelper.manager.getPrincipal();
        String userName = user.getName();
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        StringBuffer selectSQL = new StringBuffer();
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            if (oid != null && !"null".equals(oid) && !"".equals(oid)) {
                String value = "OR:wt.workflow.work.WorkItem:" + oid;
                wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
                WTReference reference = factory.getReference(value);
                if (reference.getObject() != null && !"".equals(reference.getObject())) {
                    WorkItem workItem = (WorkItem) reference.getObject();
                    WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
                    String name = wfAct.getParentProcess().getTemplate().getName();
                    selectSQL.append("SELECT a.TEMPLATE_NAME,a.id from TEMPLATEMODEL a WHERE  a.TEMPLATE_STYLE='" + name + "'AND a.CREATOR='" + userName + "'");
                    ps = conn.prepareStatement(selectSQL.toString());
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        String ID = rs.getString("ID");
                        String TEMPLATE_NAME = rs.getString("TEMPLATE_NAME");
                        map.put(ID, TEMPLATE_NAME);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

        return map;

    }

    public static Map<String, String> selectTemplate(String oid) throws Exception {
        WTUser user = null;
        user = (WTUser) SessionHelper.manager.getPrincipal();
        String userName = user.getName();
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        StringBuffer selectSQL = new StringBuffer();
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            if (oid != null && !"null".equals(oid) && !"".equals(oid)) {
                String value = "OR:wt.workflow.work.WorkItem:" + oid;
                wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
                WTReference reference = factory.getReference(value);
                if (reference.getObject() != null && !"".equals(reference.getObject())) {
                    WorkItem workItem = (WorkItem) reference.getObject();
                    WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
                    String name = wfAct.getParentProcess().getTemplate().getName();
                    selectSQL.append("SELECT a.TEMPLATE_NAME,b.* from TEMPLATEMODEL a,TEMPLATEDETAIL b WHERE  a.TEMPLATE_STYLE='" + name + "'AND a.ID=b.CONTACT_ID AND a.CREATOR='" + userName + "'");
                    ps = conn.prepareStatement(selectSQL.toString());
                    rs = ps.executeQuery();
                    Map<String, List<SelectRoleBean>> mapBeans = new HashMap<String, List<SelectRoleBean>>();
                    while (rs.next()) {
                        String TEMPLATE_NAME = rs.getString("TEMPLATE_NAME");
                        String ROLE_NAME = rs.getString("ROLE_NAME");
                        String USER = rs.getString("USER");
                        String CONCAT_ID = rs.getString("CONTACT_ID");
                        String ROLE_FULL_NAME = rs.getString("ROLE_FULL_NAME");
                        SelectRoleBean bean = new SelectRoleBean();
                        bean.setCONCAT_ID(CONCAT_ID);
                        bean.setROLE_NAME(ROLE_NAME);
                        bean.setTEMPLATE_NAME(TEMPLATE_NAME);
                        bean.setUSER(USER);
                        bean.setROLE_FULL_NAME(ROLE_FULL_NAME);
                        if (mapBeans.get(CONCAT_ID + "@_@" + TEMPLATE_NAME) == null) {
                            List<SelectRoleBean> beans = new ArrayList<SelectRoleBean>();
                            beans.add(bean);
                            mapBeans.put(CONCAT_ID + "@_@" + TEMPLATE_NAME, beans);
                        } else {
                            List<SelectRoleBean> beans = mapBeans.get(CONCAT_ID + "@_@" + TEMPLATE_NAME);
                            beans.add(bean);
                        }
                    }
                    Set<Entry<String, List<SelectRoleBean>>> entrys = mapBeans.entrySet();
                    for (Entry<String, List<SelectRoleBean>> entry : entrys) {
                        String id = entry.getKey();
                        List<SelectRoleBean> beans = entry.getValue();
                        String tempValue = "";
                        for (SelectRoleBean bean : beans) {
                            tempValue = tempValue + bean.getROLE_NAME() + "_" + bean.getUSER() + "_" + bean.getROLE_FULL_NAME() + "---";
                        }
                        map.put(id, tempValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }

        return map;

    }


    public static void insertData(Map<String, String> map, String mingcheng, String mblx) throws WTException {
        WTUser user = null;
        user = (WTUser) SessionHelper.manager.getPrincipal();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        String id = UUID.randomUUID().toString();
        DBConnUtil conn = null;
        try {
            conn = new DBConnUtil();
            String jiaoduiName = "";
            String pizhunzheName = "";
            String dayinzheName = "";
            String shenhezheName = "";
            String neibuhuiqianzheName = "";
            String biaoshenzheName = "";
            String gongshidiongeyuanName = "";
            String waibuhuiqianzheName = "";
            if (!Tools.isNull(map.get("xiaodui"))) {
                WTUser u = (WTUser) WCUtil.getPersistable(map.get("xiaodui"));
                jiaoduiName = u.getFullName();
            }

            if (!Tools.isNull(map.get("pizhunzhe"))) {
                WTUser u = (WTUser) WCUtil.getPersistable(map.get("pizhunzhe"));
                pizhunzheName = u.getFullName();
            }
            if (!Tools.isNull(map.get("dayinzhe"))) {
                WTUser u = (WTUser) WCUtil.getPersistable(map.get("dayinzhe"));
                dayinzheName = u.getFullName();
            }
            if (!Tools.isNull(map.get("shenhezhe"))) {
                WTUser u = (WTUser) WCUtil.getPersistable(map.get("shenhezhe"));
                shenhezheName = u.getFullName();
            }
            if (!Tools.isNull(map.get("neibuhuiqianzhe"))) {
                String nbhqzs = map.get("neibuhuiqianzhe");
                String[] nbhqz = nbhqzs.split("&&");
                for (String str : nbhqz) {
                    if(!"".equals(str)){
                        WTUser u = (WTUser) WCUtil.getPersistable(str);
                            neibuhuiqianzheName += "&&" + u.getFullName();
                    }
                }
                neibuhuiqianzheName = neibuhuiqianzheName.substring(2);
            }
            if (!Tools.isNull(map.get("biaoshenzhe"))) {
                WTUser u = (WTUser) WCUtil.getPersistable(map.get("biaoshenzhe"));
                biaoshenzheName = u.getFullName();
            }
            if (!Tools.isNull(map.get("gongshidiongeyuan"))) {
                WTUser u = (WTUser) WCUtil.getPersistable(map.get("gongshidiongeyuan"));
                gongshidiongeyuanName = u.getFullName();
            }
            if (!Tools.isNull(map.get("waibuhuiqianzhe"))) {
                WTUser u = (WTUser) WCUtil.getPersistable(map.get("waibuhuiqianzhe"));
                waibuhuiqianzheName = u.getFullName();
            }
            String[] queries = {"INSERT into TEMPLATEMODEL  values ('" + id + "','" + time + "','" + user.getName() + "','','','" + mingcheng + "','" + mblx + "')",
                    "INSERT INTO  TEMPLATEDETAIL  values('xiaodui','" + jiaoduiName + "','" + map.get("xiaodui") + "','" + id + "')",
                    "INSERT INTO  TEMPLATEDETAIL  values('pizhunzhe','" + pizhunzheName + "','" + map.get("pizhunzhe") + "','" + id + "')",
                    "INSERT INTO  TEMPLATEDETAIL  values('dayinzhe','" + dayinzheName + "','" + map.get("dayinzhe") + "','" + id + "')",
                    "INSERT INTO  TEMPLATEDETAIL  values('shenhezhe','" + shenhezheName + "','" + map.get("shenhezhe") + "','" + id + "')",
                    "INSERT INTO  TEMPLATEDETAIL  values('neibuhuiqianzhe','" + neibuhuiqianzheName + "','" + map.get("neibuhuiqianzhe") + "','" + id + "')",
                    "INSERT INTO  TEMPLATEDETAIL  values('biaoshenzhe','" + biaoshenzheName + "','" + map.get("biaoshenzhe") + "','" + id + "')",
                    "INSERT INTO  TEMPLATEDETAIL  values('gongshidiongeyuan','" + gongshidiongeyuanName + "','" + map.get("gongshidiongeyuan") + "','" + id + "')",
                    "INSERT INTO  TEMPLATEDETAIL  values('waibuhuiqianzhe','" + waibuhuiqianzheName + "','" + map.get("waibuhuiqianzhe") + "','" + id + "')",};
            for (String query : queries) {
                conn.executeUpdate(query);
            }

            conn.commit();

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.getStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void deleteTemplateById(String id) {
        DBConnUtil conn = null;
        try {
            conn = new DBConnUtil();

            String[] queries = {"DELETE from TEMPLATEDETAIL t where t.CONTACT_ID='" + id + "'",
                    "DELETE from TEMPLATEMODEL t where t.ID='" + id + "'",};
            for (String query : queries) {
                conn.executeUpdate(query);
            }
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.getStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void setValuesIntoTeam(String oid, Map<String, String> map) throws WTException {
        Role xiaodui = Role.toRole("JIAODUIZHE");
        Role dayinzhe = Role.toRole("DAYINZHE");
        Role gongshidingeyuan = Role.toRole("GONGSHIDINGEYUAN");
        Role approver = Role.toRole("APPROVER");
        Role shenhezhe = Role.toRole("SHENHEZHE");
        Role neibuhuiqianzhe = Role.toRole("NEIBUHUIQIANZHE");
        Role waibuhuiqian = Role.toRole("WAIBUHUIQIANZHE");
        Role biaoshenzhe = Role.toRole("BIAOSHENZHE");
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        WTUser xiaoduiUser = getWTuserByString(map.get("xiaodui"));
        String neibu = map.get("neibuhuiqianzhe");
        WTUser neibuhuiqianzheUser = null;
        WTUser neibuhuiqianzheUser1 = null;
        String[] split = neibu.split("&&");
        WTUser[] neibuhuiqianzheUserArg = new WTUser[split.length];
        for (int i = 0; i < split.length; i++) {
            neibuhuiqianzheUserArg[i] = getWTuserByString(split[i]);
        }
//        if (split.length==1) {
//            neibuhuiqianzheUser = getWTuserByString(map.get("neibuhuiqianzhe"));
//        }else if (split.length==2) {
//            neibuhuiqianzheUser = getWTuserByString(split[0]);
//            neibuhuiqianzheUser1= getWTuserByString(split[1]);
//        }
        WTUser shenhezheUser = getWTuserByString(map.get("shenhezhe"));
        WTUser biaoshenzheUser = getWTuserByString(map.get("biaoshenzhe"));
        WTUser pizhunzheUser = getWTuserByString(map.get("pizhunzhe"));
        WTUser dayinzheUser = getWTuserByString(map.get("dayinzhe"));
        WTUser gongshidiongeyuanUser = getWTuserByString(map.get("gongshidiongeyuan"));
        WTUser waibuhuiqianzheUser = getWTuserByString(map.get("waibuhuiqianzhe"));
        if (oid != null && !"null".equals(oid) && !"".equals(oid)) {
            String value = "OR:wt.workflow.work.WorkItem:" + oid;
            wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
            WTReference reference = factory.getReference(value);
            if (reference.getObject() != null && !"".equals(reference.getObject())) {
                WorkItem workItem = (WorkItem) reference.getObject();
                WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
                String name = wfAct.getParentProcess().getTemplate().getName();
                WfProcess process = wfAct.getParentProcess();
                Team team = (Team) process.getTeamId().getObject();
                HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
                List tempUserList = (List) rolePrincipalListMap.get(xiaodui);
                for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                    WTUser user = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
                    team.deletePrincipalTarget(xiaodui, user);
                }
                List dayinzheList = (List) rolePrincipalListMap.get(dayinzhe);
                for (int i = 0; dayinzheList != null && i < dayinzheList.size(); i++) {
                    WTUser user = (WTUser) ((WTPrincipalReference) dayinzheList.get(i)).getObject();
                    team.deletePrincipalTarget(dayinzhe, user);
                }
                List gongshidingeyuanList = (List) rolePrincipalListMap.get(gongshidingeyuan);
                for (int i = 0; gongshidingeyuanList != null && i < gongshidingeyuanList.size(); i++) {
                    WTUser user = (WTUser) ((WTPrincipalReference) gongshidingeyuanList.get(i)).getObject();
                    team.deletePrincipalTarget(gongshidingeyuan, user);
                }
                List approverList = (List) rolePrincipalListMap.get(approver);
                for (int i = 0; approverList != null && i < approverList.size(); i++) {
                    WTUser user = (WTUser) ((WTPrincipalReference) approverList.get(i)).getObject();
                    team.deletePrincipalTarget(approver, user);
                }
                List shenhezheList = (List) rolePrincipalListMap.get(shenhezhe);
                for (int i = 0; shenhezheList != null && i < shenhezheList.size(); i++) {
                    WTUser user = (WTUser) ((WTPrincipalReference) shenhezheList.get(i)).getObject();
                    team.deletePrincipalTarget(shenhezhe, user);
                }
                List neibuhuiqianzheList = (List) rolePrincipalListMap.get(neibuhuiqianzhe);
                for (int i = 0; neibuhuiqianzheList != null && i < neibuhuiqianzheList.size(); i++) {
                    WTUser user = (WTUser) ((WTPrincipalReference) neibuhuiqianzheList.get(i)).getObject();
                    team.deletePrincipalTarget(neibuhuiqianzhe, user);
                }
                List waibuhuiqianList = (List) rolePrincipalListMap.get(waibuhuiqian);
                for (int i = 0; waibuhuiqianList != null && i < waibuhuiqianList.size(); i++) {
                    WTUser user = (WTUser) ((WTPrincipalReference) waibuhuiqianList.get(i)).getObject();
                    team.deletePrincipalTarget(waibuhuiqian, user);
                }
                List biaoshenzheList = (List) rolePrincipalListMap.get(biaoshenzhe);
                for (int i = 0; biaoshenzheList != null && i < biaoshenzheList.size(); i++) {
                    WTUser user = (WTUser) ((WTPrincipalReference) biaoshenzheList.get(i)).getObject();
                    team.deletePrincipalTarget(biaoshenzhe, user);
                }
                team.addPrincipal(xiaodui, xiaoduiUser);
                team.addPrincipal(dayinzhe, dayinzheUser);
                team.addPrincipal(gongshidingeyuan, gongshidiongeyuanUser);
                team.addPrincipal(approver, pizhunzheUser);
                team.addPrincipal(shenhezhe, shenhezheUser);
                for (int i = 0; i < neibuhuiqianzheUserArg.length; i++) {
                    team.addPrincipal(neibuhuiqianzhe, neibuhuiqianzheUserArg[i]);
                }
//                if (split.length==1) {
//                    team.addPrincipal(neibuhuiqianzhe, neibuhuiqianzheUser);
//                }else if (split.length==2) {
//                    team.addPrincipal(neibuhuiqianzhe, neibuhuiqianzheUser);
//                    team.addPrincipal(neibuhuiqianzhe, neibuhuiqianzheUser1);
//                }
                team.addPrincipal(waibuhuiqian, waibuhuiqianzheUser);
                team.addPrincipal(biaoshenzhe, biaoshenzheUser);
                team = (Team) PersistenceHelper.manager.refresh(team);
                team = (Team) PersistenceHelper.manager.save(team);
                SessionServerHelper.manager.setAccessEnforced(enforce);
            }
        }
    }

    public static WTUser getWTuserByString(String oid) throws WTException {

        WTUser user = null;
        if (oid != null && !"".equals(oid) && !"null".equals(oid)) {
            wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
            WTReference reference = factory.getReference(oid);
            if (reference.getObject() != null && !"".equals(reference.getObject())) {
                user = (WTUser) reference.getObject();
            }
        }
        return user;
    }

    public static String getTemplateNameById(String oid) throws WTException {
        String name = "";
        if (oid != null && !"".equals(oid) && !"null".equals(oid)) {
            String id = "OR:wt.workflow.work.WorkItem:" + oid;
            wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
            WTReference reference = factory.getReference(id);
            if (reference.getObject() != null && !"".equals(reference.getObject())) {
                WorkItem workItem = (WorkItem) reference.getObject();
                WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
                name = wfAct.getParentProcess().getTemplate().getName();
            }

        }
        return name;

    }

    public static String getWorkItemStatuByid(NmCommandBean com) throws WTException {
        String flag = "";
        String[] oids = com.getTextParameterValues("oid");
        String id = oids[0];
        wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
        WTReference reference = factory.getReference(id);
        if (reference.getObject() != null && !"".equals(reference.getObject())) {
            WorkItem workItem = (WorkItem) reference.getObject();
            String status = workItem.getStatus().getDisplay(Locale.CHINA);
            //已完成的活动隐藏保存按钮
            if (Constants.WF_WORKITEM_STATUS_COMPLETED.equals(status)) {
                flag = "COMPLETED";
            }
        }
        return flag;
        // TODO Auto-generated method stub

    }

}
