package ext.casc.ixb;

import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.synch.SoapCall;
import ext.casc.util.DBConn;
import ext.casc.util.Deserialize;
import ext.casc.util.WCUtil;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pds.oracle81.OracleDataSource;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WorkItem;

import java.sql.Date;
import java.text.MessageFormat;
import java.sql.*;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ReleaseDataAdvisBackHelper {
	private static Logger LOGGER=Logger.getLogger(ReleaseDataAdvisBackHelper.class);
	/**
	 * 发放部门类型纸质
	 */
	public static final String SEND_DEPARTMENT_PAPER="Paper";
	
	/**
	 * 发放部门类型电子
	 */
	public static final String SEND_DEPARTMENT_ELECTRONIC="Electronic";


    public static String processReleaseDataAdvise(ObjectReference self, WTObject pbo){
        String errorMsg = "ok";
        try {
            WfProcess process = (WfProcess) self.getObject();
            ProcessData pData = process.getContext();
            String wfProcessOid = (String) pData.getValue("wfProcessOid");
            String activityName = (String) pData.getValue("activityName");//工艺路线
            String activityTemplateID = (String) pData.getValue("activityTemplateID");
            String sendFrom = (String) pData.getValue("sendFrom");
            if(sendFrom ==null){
                sendFrom = "509";
            }
            String orderIID = (String) pData.getValue("orderIID");
            Enumeration enumeration = WfEngineHelper.service.getProcessSteps(process, null);
            StringBuffer resultMsg = new StringBuffer();
            while (enumeration.hasMoreElements()) {
                WfActivity wfactivity = (WfActivity) enumeration.nextElement();
                String wfactivityName = wfactivity.getName();
                System.out.println("----->>>>>>wfactivityName:" + wfactivityName);
                if (wfactivityName.equals("")
                        || wfactivityName.equals("")) {
                    if (wfactivity instanceof WfAssignedActivity) {
                        WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
                        resultMsg.append(wfassignedactivity.getName());
                        Enumeration enumer = wfassignedactivity.getAllAssignments();
                        while (enumer.hasMoreElements()) {
                            WfAssignment assignment = (WfAssignment) enumer.nextElement();
                            WorkItem workItem = getWorkItemByWfAssignment(assignment);
                            if (workItem != null) {
                                WTUser user = (WTUser)workItem.getOwnership().getOwner().getPrincipal();
                                String userName = user.getFullName();
                                String comments = workItem.getContext().getTaskComments();
                                if(comments == null) {
                                    comments = "";
                                }
                                System.out.println("----->>>>>>wfactivityName:" + wfactivityName + "   userName:"+userName + "   comments:" + comments);
                                resultMsg.append(";");
                                resultMsg.append(userName);
                                resultMsg.append(";");
                                resultMsg.append(comments);
                            }
                        }
                        resultMsg.append("/");
                    }
                }
            }

            //反馈回509
            Map<String, Object> feedbacbMap = new HashMap<String, Object>();
            feedbacbMap.put("comments", resultMsg.toString());
            feedbacbMap.put("wfProcessOid", wfProcessOid);
            feedbacbMap.put("approvedType", "DISORDER");//先不动此参数
            feedbacbMap.put("orderIID", orderIID);
            feedbacbMap.put("feedback", "812");//返回单位
            feedbacbMap.put("activityTemplateID", activityTemplateID);
            feedbacbMap.put("activityName", activityName);

            SoapCall sopaCall = new SoapCall();
            HashMap<String, Object> inputparams = new HashMap<String, Object>();
            String feetbackStr = Deserialize.serializeMap(feedbacbMap);
            inputparams.put("result", feetbackStr);
            inputparams.put("sendFrom", sendFrom);
            ArrayList resultList = sopaCall.callToServer("Feedback", inputparams);
        } catch (WTRuntimeException e) {
            errorMsg = e.getLocalizedMessage();
            e.printStackTrace();
        } catch (WTException e) {
            errorMsg = e.getLocalizedMessage();
            e.printStackTrace();
        }
        return errorMsg;
    }

    public static WorkItem getWorkItemByWfAssignment(WfAssignment wfAssignment) throws WTException {
        QuerySpec qs = new QuerySpec(WorkItem.class);
        int[] index = { 0 };
        long longIda3c4 = PersistenceHelper.getObjectIdentifier(wfAssignment).getId();
        SearchCondition sc = new SearchCondition(WorkItem.class, "parentWA.key.id", SearchCondition.EQUAL, longIda3c4);
        qs.appendWhere(sc, index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        if (qr.hasMoreElements()) {
            return (WorkItem) qr.nextElement();
        }
        return null;
    }

    public static void setCommentValueVar(ObjectReference self, String var) throws WTException {
        Object obj = self.getObject();
        if (obj instanceof WorkItem) {
            WorkItem workItem = (WorkItem) obj;
            WfActivity currentActivity = (WfActivity) workItem.getSource().getObject();
            WfProcess process = currentActivity.getParentProcess();
            ProcessData processData = workItem.getContext();
            String comments = processData.getTaskComments();
            processData.setValue(var, comments);
            PersistenceHelper.manager.save(process);
        }
    }

    public static void setSelDepartValue(String workItemOid, String values)  {
        // values值的格式为：wt.epm.EPMDocument:99141~一室:6;三室:3@wt.epm.EPMDocument:99108~二室:8;六室:2@
        DBConn conn = null;
        try {
            conn = new DBConn();
            String[] data = values.split("@");
            for (String value : data) {
                if (value != null && !"".equals(value)) {
                    String[] str = value.split("~");
                    String objOid = str[0];
                    Persistable per = WCUtil.getPersistable(objOid);
                    String number = "";
                    String name = "";
                    String ver = "";
                    String product_name = "";
                    Date date = new Date(System.currentTimeMillis());
                    if (per instanceof WTDocument) {
                        WTDocument doc = (WTDocument) per;
                        number = doc.getNumber();
                        name = doc.getName();
                        ver = doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue();
                        product_name = doc.getContainerName();
                    } else if (per instanceof EPMDocument) {
                        EPMDocument epm = (EPMDocument) per;
                        number = epm.getNumber();
                        name = epm.getName();
                        ver = epm.getVersionIdentifier().getValue() + "." + epm.getIterationIdentifier().getValue();
                        product_name = epm.getContainerName();
                    }
                    String selValue = str[1];

                    if (selValue.contains(";")) {
                        String[] sels = selValue.split(";");
                        for (String string : sels) {
                            String dep = string.split(":")[0];
                            String n = string.split(":")[1];
                            StringBuffer sql = new StringBuffer();
                            sql.append("insert into ASES_DATA_PRINT_TABLE ");
                            sql.append("(OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,DEPARTMENT,AMOUNT,PRODUCT_NAME,PRINT_DATE) ");
                            sql.append("values('" + number + "','" + name + "','" + ver + "','" + objOid + "','" + dep
                                    + "','" + n + "','" + product_name + "'," + "date '" + date + "')");
                            System.out.println("---->>>sql:" + sql.toString());
                            conn.executeUpdate(sql.toString());
                            conn.commit();
                        }
                    } else {
                        String dep = selValue.split(":")[0];
                        String n = selValue.split(":")[1];
                        StringBuffer sql = new StringBuffer();
                        sql.append("insert into ASES_DATA_PRINT_TABLE ");
                        sql.append("(OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,DEPARTMENT,AMOUNT,PRODUCT_NAME,PRINT_DATE) ");
                        sql.append("values('" + number + "','" + name + "','" + ver + "','" + objOid + "','" + dep
                                + "','" + n + "','" + product_name + "'," + "date '" + date + "')");
                        System.out.println("---->>>sql:" + sql.toString());
                        conn.executeUpdate(sql.toString());
                        conn.commit();
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void setSelDepartAndtoGYYValue(String workItemOid, String values) throws WTException, SQLException {
        // values值的格式为：wt.epm.EPMDocument:99141~一室:6;三室:3~OR:wt.org.WTUser:126762;OR:wt.org.WTUser:126714;@
    						//wt.epm.EPMDocument:99108~二室:8;六室:2~OR:wt.org.WTUser:126762;OR:wt.org.WTUser:126714;@

        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        conn.setAutoCommit(false);
        Statement state = conn.createStatement();
        String[] data = values.split("@");
        String userId = "";
        for (String value : data) {
            if (value != null && !"".equals(value)) {
                String[] str = value.split("~");

                String objOid = str[0];
                Persistable per = WCUtil.getPersistable(objOid);
                String number = "";
                String name = "";
                String ver = "";
                String product_name = "";
                Date date = new Date(System.currentTimeMillis());
                if (per instanceof WTDocument) {
                    WTDocument doc = (WTDocument) per;
                    number = doc.getNumber();
                    name = doc.getName();
                    ver = doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue();
                    product_name = doc.getContainerName();
                } else if (per instanceof EPMDocument) {
                    EPMDocument epm = (EPMDocument) per;
                    number = epm.getNumber();
                    name = epm.getName();
                    ver = epm.getVersionIdentifier().getValue() + "." + epm.getIterationIdentifier().getValue();
                    product_name = epm.getContainerName();
                }
                String toGYY = "";
                String userName = "";
                if(str.length>2){
                	toGYY = str[2];
                	String[] users = toGYY.split("split");
                    userId = users[0];
                    userName = users[1];
                }

                String selValue = str[1];
                String[] sendDepartment=selValue.split("###");
                String paperDepartment=sendDepartment[0];
                LOGGER.debug(MessageFormat.format("=====>setSelDepartAndtoGYYValue.paperDepartment {0}", paperDepartment));
                if (paperDepartment.contains(";")) {
                    String[] sels = paperDepartment.split(";");
                    for (String string : sels) {
                        String dep = string.split(":")[0];
                        String n = string.split(":")[1];
                        StringBuffer sql = new StringBuffer();
                        sql.append("insert into ASES_DATA_PRINT_TABLE ");
                        sql.append("(OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,TOGYY,GYYNAME,OBJ_OID,DEPARTMENT,AMOUNT,PRODUCT_NAME,PRINT_DATE,SDTYPE) ");
                        sql.append("values('" + number + "','" + name + "','" + ver + "','"+ userId +"','"+ userName + "','" + objOid + "','" + dep
                                + "','" + n + "','" + product_name + "'," + "date '" + date + "','"+ SEND_DEPARTMENT_PAPER + "')");
                        LOGGER.debug(MessageFormat.format("---->>>sql {0}", sql.toString()));
                        state.execute(sql.toString());
                        conn.commit();
                    }
                } else {
                    String dep = paperDepartment.split(":")[0];
                    String n = paperDepartment.split(":")[1];
                    StringBuffer sql = new StringBuffer();
                    sql.append("insert into ASES_DATA_PRINT_TABLE ");
                    sql.append("(OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,TOGYY,GYYNAME,OBJ_OID,DEPARTMENT,AMOUNT,PRODUCT_NAME,PRINT_DATE,SDTYPE) ");
                    sql.append("values('" + number + "','" + name + "','" + ver + "','"+ userId + "','"+ userName +"','" + objOid + "','" + dep
                            + "','" + n + "','" + product_name + "'," + "date '" + date + "','"+ SEND_DEPARTMENT_PAPER + "')");
                    LOGGER.debug(MessageFormat.format("---->>>sql {0}", sql.toString()));
                    state.execute(sql.toString());
                    conn.commit();
                }

                if(sendDepartment.length>1){
                    /**保存电子发放部门,Starting....***/
                    String electronicDepartment=sendDepartment[1];
                    LOGGER.debug(MessageFormat.format("=====>setSelDepartAndtoGYYValue.electronicDepartment {0}", electronicDepartment));
                    if(StringUtils.isNotBlank(electronicDepartment)) {
                        if (electronicDepartment.contains(";")) {
                            String[] sels = electronicDepartment.split(";");
                            for (String string : sels) {
                                String dep = string.split(":")[0];
                                String n = string.split(":")[1];
                                StringBuffer sql = new StringBuffer();
                                sql.append("insert into ASES_DATA_PRINT_TABLE ");
                                sql.append("(OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,TOGYY,GYYNAME,OBJ_OID,DEPARTMENT,AMOUNT,PRODUCT_NAME,PRINT_DATE,SDTYPE) ");
                                sql.append("values('" + number + "','" + name + "','" + ver + "','"+ userId +"','"+ userName + "','" + objOid + "','" + dep
                                        + "','" + n + "','" + product_name + "'," + "date '" + date + "','"+ SEND_DEPARTMENT_ELECTRONIC + "')");
                                LOGGER.debug(MessageFormat.format("---->>>electronic.sql {0}", sql.toString()));
                                state.execute(sql.toString());
                                conn.commit();
                            }
                        } else {
                            String dep = electronicDepartment.split(":")[0];
                            String n = electronicDepartment.split(":")[1];
                            StringBuffer sql = new StringBuffer();
                            sql.append("insert into ASES_DATA_PRINT_TABLE ");
                            sql.append("(OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,TOGYY,GYYNAME,OBJ_OID,DEPARTMENT,AMOUNT,PRODUCT_NAME,PRINT_DATE,SDTYPE) ");
                            sql.append("values('" + number + "','" + name + "','" + ver + "','"+ userId + "','"+ userName +"','" + objOid + "','" + dep
                                    + "','" + n + "','" + product_name + "'," + "date '" + date + "','"+ SEND_DEPARTMENT_ELECTRONIC + "')");
                            LOGGER.debug(MessageFormat.format("---->>>electronic.sql {0}", sql.toString()));
                            state.execute(sql.toString());
                            conn.commit();
                        }
                    }
                    /**保存电子发放部门,end....***/
                }

            }

          //设置流程角色
            if(!"".equals(userId) && userId!=null){
            	Role role = Role.toRole("GONGYIYUAN");
                boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
                String[] userIds = userId.split(";");
                ReferenceFactory rf = new ReferenceFactory();
                WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
                WfActivity activity = (WfActivity) wi.getSource().getObject();
                WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
                WfProcess process = null;
                if (wfcont instanceof WfBlock) {
                    WfBlock wfBlock = (WfBlock) wfcont;
                    process = wfBlock.getParentProcess();
                } else {
                    process = (WfProcess) wfcont;
                }
                Team team = (Team) process.getTeamId().getObject();
                HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
                List tempUserList = (List) rolePrincipalListMap.get(role);
                for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                    WTUser user1 = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
                    team.deletePrincipalTarget(role, user1);
                }
                try {
                	for (String userID : userIds) {
                		WTUser user = (WTUser) com.glaway.mpm.util.ReferenceFactory.getObjectbyOid(userID);
                        team.addPrincipal(role,user);
                	}
                	team = (Team) PersistenceHelper.manager.refresh(team);
                	team = (Team) PersistenceHelper.manager.save(team);
                } catch (WTException e) {
                    e.printStackTrace();
                } finally {
                    wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
                }
            }

        }
    }


    public static String getSelDepartValue(String number) {
        String values = "";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultset = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("select re.DEPARTMENT,re.AMOUNT from ASES_DATA_PRINT_TABLE re where re.OBJ_NUMBER='"+number+"' ");
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(selectSQL.toString());
            resultset = ps.executeQuery();
            while (resultset.next()) {
                String dep = resultset.getString(1);
                String n = resultset.getString(2);
                if("".equals(values)) {
                    values = dep+":"+n;
                } else {
                    values = values+";"+dep+":"+n;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultset != null){
                    resultset.close();
                }
                if(ps != null){
                    ps.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return values;
    }
    
    /** 
      * @Description: 根据编号和发送部门类型查询选择的部门数据
      * @date 2025年11月5日下午2:15:18
      * @author Liluwen
      * @param number
      * @param sdType
      * @return  
      * @return 
    */
    public static String getSelDepartValue(String number,String sdType) {
        String values = "";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultset = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("select re.DEPARTMENT,re.AMOUNT from ASES_DATA_PRINT_TABLE re where re.OBJ_NUMBER='"+number+"' ");
            selectSQL.append(" and re.SDTYPE='"+sdType+"' ");
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(selectSQL.toString());
            resultset = ps.executeQuery();
            while (resultset.next()) {
                String dep = resultset.getString(1);
                String n = resultset.getString(2);
                if("".equals(values)) {
                    values = dep+":"+n;
                } else {
                    values = values+";"+dep+":"+n;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultset != null){
                    resultset.close();
                }
                if(ps != null){
                    ps.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return values;
    }
    
    /** 
      * @Description: 兼容历史数据，根据编号和排除的发送部门类型查询选择的部门数据
      * @date 2025年11月5日下午2:36:11
      * @author Liluwen
      * @param number
      * @param sdType
      * @return  
      * @return 
    */
    public static String getSelDepartValueNoSdType(String number,String sdType) {
        String values = "";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultset = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("select re.DEPARTMENT,re.AMOUNT from ASES_DATA_PRINT_TABLE re where re.OBJ_NUMBER='"+number+"' ");
            selectSQL.append(" and re.SDTYPE!='"+sdType+"' ");
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(selectSQL.toString());
            resultset = ps.executeQuery();
            while (resultset.next()) {
                String dep = resultset.getString(1);
                String n = resultset.getString(2);
                if("".equals(values)) {
                    values = dep+":"+n;
                } else {
                    values = values+";"+dep+":"+n;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultset != null){
                    resultset.close();
                }
                if(ps != null){
                    ps.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    public static String getSelToGYYValue(String number) {
        String values = "";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultset = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("select re.GYYNAME from ASES_DATA_PRINT_TABLE re where re.OBJ_NUMBER='"+number+"' ");
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(selectSQL.toString());
            resultset = ps.executeQuery();
            while (resultset.next()) {
                String gyyName = resultset.getString(1);
                values = gyyName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultset != null){
                    resultset.close();
                }
                if(ps != null){
                    ps.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    public static void setSelDepartValue2(String workItemOid, String values) {
        // values值的格式为：wt.epm.EPMDocument:99141~一室:6;三室:3@wt.epm.EPMDocument:99108~二室:8;六室:2@
        DBConn conn = null;
        try {
            conn = new DBConn();
            String[] data = values.split("@");
            for (String value : data) {
                if (value != null && !"".equals(value)) {
                    String[] str = value.split("~");
                    String objOid = str[0];
                    Persistable per = WCUtil.getPersistable(objOid);
                    String number = "";
                    String name = "";
                    String ver = "";
                    String product_name = "";
                    Date date = new Date(System.currentTimeMillis());
                    if (per instanceof WTPart) {
                        WTPart part = (WTPart) per;
                        number = part.getNumber();
                        name = part.getName();
                        ver = part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue();
                        product_name = part.getContainerName();
                    } else if (per instanceof MPMProcessPlan) {
                        MPMProcessPlan pplan = (MPMProcessPlan) per;
                        number = pplan.getNumber();
                        name = pplan.getName();
                        ver = pplan.getVersionIdentifier().getValue() + "." + pplan.getIterationIdentifier().getValue();
                        product_name = pplan.getContainerName();
                    }
                    String selValue = str[1];

                    if (selValue.contains(";")) {
                        String[] sels = selValue.split(";");
                        for (String string : sels) {
                            String dep = string.split(":")[0];
                            String n = string.split(":")[1];
                            StringBuffer sql = new StringBuffer();
                            sql.append("insert into MPMPPLAN_DATA_PRINT_TABLE ");
                            sql.append("(OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,DEPARTMENT,AMOUNT,PRODUCT_NAME,PRINT_DATE) ");
                            sql.append("values('" + number + "','" + name + "','" + ver + "','" + objOid + "','" + dep
                                    + "','" + n + "','" + product_name + "'," + "date '" + date + "')");
                            System.out.println("---->>>sql:" + sql.toString());
                            conn.executeUpdate(sql.toString());
                            conn.commit();
                        }
                    } else {
                        String dep = selValue.split(":")[0];
                        String n = selValue.split(":")[1];
                        StringBuffer sql = new StringBuffer();
                        sql.append("insert into MPMPPLAN_DATA_PRINT_TABLE ");
                        sql.append("(OBJ_NUMBER,OBJ_NAME,OBJ_VERSION,OBJ_OID,DEPARTMENT,AMOUNT,PRODUCT_NAME,PRINT_DATE) ");
                        sql.append("values('" + number + "','" + name + "','" + ver + "','" + objOid + "','" + dep
                                + "','" + n + "','" + product_name + "'," + "date '" + date + "')");
                        System.out.println("---->>>sql:" + sql.toString());
                        conn.executeUpdate(sql.toString());
                        conn.commit();
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static String getSelSignValue2(String oid) throws WTException, SQLException {
    	String values = "";
	    ReferenceFactory rf = new ReferenceFactory();
	    Object obj =  rf.getReference(oid).getObject();
	    WfProcess process = null;
	    if(obj instanceof WorkItem){
	    	WorkItem item = (WorkItem) obj;
	    	WfActivity activity = (WfActivity) item.getSource().getObject();
    	    process = activity.getParentProcess();
	    } else if(obj instanceof WfProcess){
	    	process = (WfProcess) obj;
	    }

		ProcessData data = process.getContext();
		values = (String) data.getValue("outSignInfos");
		return values;
}

    public static String getSelDepartValue2(String number) {
        String values = "";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("select re.DEPARTMENT,re.AMOUNT from MPMPPLAN_DATA_PRINT_TABLE re where re.OBJ_NUMBER='"+number+"' ");
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(selectSQL.toString());
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String dep = resultSet.getString(1);
                String n = resultSet.getString(2);
                if("".equals(values)) {
                    values = dep+":"+n;
                } else {
                    values = values+";"+dep+":"+n;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultSet != null){
                    resultSet.close();
                }
                if(ps != null){
                    ps.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    public static void setSelSignValue2(String oid, String values) throws WTException, SQLException {
	    ReferenceFactory rf = new ReferenceFactory();
	    Object obj =  rf.getReference(oid).getObject();
	    WfProcess process = null;
	    if(obj instanceof WorkItem){
	    	WorkItem item = (WorkItem) obj;
	    	WfActivity activity = (WfActivity) item.getSource().getObject();
    	    process = activity.getParentProcess();
	    } else if(obj instanceof WfProcess){
	    	process = (WfProcess) obj;
	    }
	    String newValus = values;
	    String[] datas = values.split("@");
        for (String value : datas) {
            if (value != null && !"".equals(value)) {
                String[] str = value.split("~");
                String objOid = str[0];
                Persistable per = WCUtil.getPersistable(objOid);
                String number = "";
                if(per instanceof WTDocument){
                	WTDocument doc = (WTDocument) per;
                	number = doc.getNumber();
                	newValus = newValus.replaceAll(objOid+"~", number+"~");
                }
                else if(per instanceof WTChangeOrder2){
                	WTChangeOrder2 ecn = (WTChangeOrder2) per;
                	number = ecn.getNumber();
                	newValus = newValus.replaceAll(objOid+"~", number+"~");
                }

            }
        }
        System.out.println("newValus=="+newValus);
		ProcessData data = process.getContext();
		data.setValue("outSignInfos", newValus);
		PersistenceHelper.manager.save(process);
}
}
