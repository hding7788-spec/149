package ext.sast.center.record;

import com.bjsasc.avidm.mq.message.Based;
import ext.sast.center.processor.InvokeRestServiceProcessor;
import ext.sast.center.record.bean.GWMQRecord;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.util.PropertiesUtil;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.admin.AdministrativeDomainHelper;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.method.MethodContext;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pom.WTConnection;
import wt.session.SessionAuthenticator;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author YaQii
 */
public class GWMQRecordService {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

    public static GWMQRecord add(GWMQRecord record) {
        try {
            CmPersistenceHelper.manager.save(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return record;
    }

    public static GWMQRecord update(GWMQRecord record) throws Exception {
        try {
            CmPersistenceHelper.manager.update(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return record;
    }

    public static GWMQRecord getRecord(String id) throws Exception {
        GWMQRecord record = (GWMQRecord) CmPersistenceHelper.manager.find(GWMQRecord.class, id);
        return record;
    }

    public static GWMQRecord getRecordByMsgId(String msgId) throws Exception {
        CmQuerySpec qs = new CmQuerySpec(GWMQRecord.class);
        qs.appendWhere(GWMQRecord.MSGID, CmQuerySpec.EQUAL, msgId);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        if (qr.hasNext()) {
            return (GWMQRecord) qr.next();
        }
        return null;
    }

    public static GWMQRecord updateState(String id, String sendState, String msgState, String errorMsg, String msgId) {
        GWMQRecord record = null;
        try {
            record = getRecord(id);
            if (record != null) {
                record.setSendState(sendState);
                if (msgState != null && !"".equals(msgState)) {
                    record.setMsgState(msgState);
                }
                if (errorMsg != null && !"".equals(errorMsg)) {
                    record.setErrorMsg(errorMsg);
                }
                if (msgId != null && !"".equals(msgId)) {
                    record.setMsgId(msgId);
                }
                CmPersistenceHelper.manager.update(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return record;
    }

    public static GWMQRecord updateState(String id, String sendState, String msgState, String errorMsg) {
        return updateState(id, sendState, msgState, errorMsg, null);
    }

    public static GWMQRecord updateStateByMsgId(String msgId, String sendState) {
        MethodContext mc = null;
        GWMQRecord record = null;
        WTConnection wtconnection = null;
        Connection conn = null;
        StringBuilder sb = null;
        PreparedStatement modState = null;
        try {
            mc = MethodContext.getContext(Thread.currentThread());
            if (mc == null)
                mc = new MethodContext(null, null);
            if (mc.getAuthentication() == null) {
                SessionAuthenticator sa = new SessionAuthenticator();
                mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
            }
            String context = mc.getId().toString();
            System.out.println("**********设置上下文**********"+context);

            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            conn = wtconnection.getConnection();

            sb = new StringBuilder();
            sb.append("update GWMQRECORD set SENDSTATE = '"+sendState+"' where MSGID = '"+msgId+"'");
            modState = conn.prepareStatement(sb.toString());
            int num = modState.executeUpdate();
            if(num>0){
                conn.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(modState != null){
                    modState.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(mc != null){
                mc.unregister();
            }
        }
        return record;
    }

    public static List<GWMQRecord> query(String id) throws Exception {
        List<GWMQRecord> records = new ArrayList<GWMQRecord>();
        CmQuerySpec qs = new CmQuerySpec(GWMQRecord.class);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            records.add((GWMQRecord) qr.next());
        }
        return records;
    }

    public static List<GWMQRecord> queryByTime(String startTime) throws Exception {
        List<GWMQRecord> records = new ArrayList<GWMQRecord>();
        CmQuerySpec qs = new CmQuerySpec(GWMQRecord.class);
        qs.appendWhere(GWMQRecord.CREATETIMESTAMP, CmQuerySpec.NO_LESS_THAN, startTime);
        qs.appendAnd();
        qs.appendWhere(GWMQRecord.MSGSTATE, CmQuerySpec.NOT_EQUAL, "已完成");
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            records.add((GWMQRecord) qr.next());
        }
        return records;
    }

    public static List<GWMQRecord> query(Map<String, String> equalsparams, Map<String, String> likeparams, String startTime, String endTime) throws Exception {
        Map<String, JSONObject> allMetaMsg = getAllMetaMsg();

        List<GWMQRecord> records = new ArrayList<GWMQRecord>();
        CmQuerySpec qs = new CmQuerySpec(GWMQRecord.class);
        qs.appendWhere("1", CmQuerySpec.EQUAL, "1");
//        WTPrincipal currentUser = SessionHelper.getPrincipal();
//        String userName = currentUser.getName();
//        if (StringUtils.isNotEmpty(userName)) {
//            if (!"149".equals(userName) && !userName.contains("admin") && !userName.contains("Admin")) {
//                String fullName = ((WTUser) currentUser).getFullName();
//                qs.appendAnd();
//                qs.appendOpenParen();
//                qs.appendWhere(GWMQRecord.RECEIVER, CmQuerySpec.LIKE, "%" + fullName + ",%");
//                qs.appendOr();
//                qs.appendWhere(GWMQRecord.ORDEROWNER, CmQuerySpec.EQUAL, fullName);
//                qs.appendCloseParen();
//            }
//        }
        Set<Map.Entry<String, String>> set = equalsparams.entrySet();
        for (Map.Entry<String, String> entry : set) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isNotEmpty(value)) {
                qs.appendAnd();
                qs.appendWhere(key, CmQuerySpec.EQUAL, value);
            }
        }
        Set<Map.Entry<String, String>> set2 = likeparams.entrySet();
        for (Map.Entry<String, String> entry : set2) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isNotEmpty(value)) {
                qs.appendAnd();
                qs.appendWhere(key, CmQuerySpec.LIKE, "%" + value + "%");
            }
        }
        if (StringUtils.isNotEmpty(startTime)) {
            qs.appendAnd();
            qs.appendWhere(GWMQRecord.CREATETIMESTAMP, CmQuerySpec.NO_LESS_THAN, startTime);
        }
        if (StringUtils.isNotEmpty(endTime)) {
            qs.appendAnd();
            qs.appendWhere(GWMQRecord.CREATETIMESTAMP, CmQuerySpec.LESS_THAN, endTime);
        }
        qs.appendOrderBy(GWMQRecord.CREATETIMESTAMP, true);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            GWMQRecord gwmqRecord = (GWMQRecord) qr.next();
            if(StringUtils.isNotEmpty(gwmqRecord.getUpdateTime())){
                gwmqRecord.setUpdateTime(simpleDateFormat.format(Long.parseLong(gwmqRecord.getUpdateTime())));
            }
            if(StringUtils.isNotEmpty(gwmqRecord.getCreateTimeStamp())){
                gwmqRecord.setCreateTimeStamp(simpleDateFormat.format(Long.parseLong(gwmqRecord.getCreateTimeStamp())));
            }
            if(StringUtils.isNotEmpty(gwmqRecord.getSendTime())){
                gwmqRecord.setSendTime(simpleDateFormat.format(Long.parseLong(gwmqRecord.getSendTime())));
            }
            if(allMetaMsg != null && allMetaMsg.size() != 0){
                JSONObject jsonObject = allMetaMsg.get(gwmqRecord.getMsgId());
                String msgState = "失败";
                if(jsonObject != null){
                    msgState = jsonObject.getString(Based.MSG_STATUS);
                    if("1".equals(msgState)){
                        msgState = "新建";
                    }else if("2".equals(msgState)){
                        msgState = "处理中";
                    }else if("3".equals(msgState)){
                        msgState = "处理中（有异常）";
                    }else if("11".equals(msgState)){
                        msgState = "已完成";
                    }else if("12".equals(msgState)){
                        msgState = "失败";
                    }
                    if(jsonObject.has("msg_exception_stacktrace")){
                        String msgException = jsonObject.getString("msg_exception_stacktrace");
                        if(msgException.length()>3000){
                            msgException = msgException.substring(0,2999);
                        }
                        gwmqRecord.setErrorMsg(msgException);
                    }
                }
                gwmqRecord.setMsgState(msgState);

            }
            records.add(gwmqRecord);
        }
        return records;
    }

    public static List<GWMQRecord> defaultQuery() throws Exception {
        Map<String, JSONObject> allMetaMsg = getAllMetaMsg();

        List<GWMQRecord> records = new ArrayList<GWMQRecord>();
        CmQuerySpec qs = new CmQuerySpec(GWMQRecord.class);
        qs.appendWhere("1", CmQuerySpec.EQUAL, "1");

//        WTPrincipal currentUser = SessionHelper.getPrincipal();
//        String userName = currentUser.getName();
//        if (StringUtils.isNotEmpty(userName)) {
//            if (!"149".equals(userName) && !userName.contains("admin") && !userName.contains("Admin")) {
//                String fullName = ((WTUser) currentUser).getFullName();
//                qs.appendAnd();
//                qs.appendOpenParen();
//                qs.appendWhere(GWMQRecord.RECEIVER, CmQuerySpec.LIKE, "%" + fullName + ",%");
//                qs.appendOr();
//                qs.appendWhere(GWMQRecord.ORDEROWNER, CmQuerySpec.EQUAL, fullName);
//                qs.appendCloseParen();
//            }
//        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -3);
        String startTime = c.getTime().getTime() + "";
        qs.appendAnd();
        qs.appendWhere(GWMQRecord.CREATETIMESTAMP, CmQuerySpec.NO_LESS_THAN, startTime);

        qs.appendOrderBy(GWMQRecord.CREATETIMESTAMP, true);


        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);

        while (qr.hasNext()) {
            GWMQRecord gwmqRecord = (GWMQRecord) qr.next();

            if(StringUtils.isNotEmpty(gwmqRecord.getUpdateTime())){
                gwmqRecord.setUpdateTime(simpleDateFormat.format(Long.parseLong(gwmqRecord.getUpdateTime())));
            }
            if(StringUtils.isNotEmpty(gwmqRecord.getCreateTimeStamp())){
                gwmqRecord.setCreateTimeStamp(simpleDateFormat.format(Long.parseLong(gwmqRecord.getCreateTimeStamp())));
            }
            if(StringUtils.isNotEmpty(gwmqRecord.getSendTime())){
                gwmqRecord.setSendTime(simpleDateFormat.format(Long.parseLong(gwmqRecord.getSendTime())));
            }

            if(allMetaMsg != null && allMetaMsg.size() != 0){
                JSONObject jsonObject = allMetaMsg.get(gwmqRecord.getMsgId());
                String msgState = "失败";
                if(jsonObject != null){
                    msgState = jsonObject.getString(Based.MSG_STATUS);
                    if("1".equals(msgState)){
                        msgState = "新建";
                    }else if("2".equals(msgState)){
                        msgState = "处理中";
                    }else if("3".equals(msgState)){
                        msgState = "处理中（有异常）";
                    }else if("11".equals(msgState)){
                        msgState = "已完成";
                    }else if("12".equals(msgState)){
                        msgState = "失败";
                    }
                    if(jsonObject.has("msg_exception_stacktrace")){
                        String msgException = jsonObject.getString("msg_exception_stacktrace");
                        if(msgException.length()>3000){
                            msgException = msgException.substring(0,2999);
                        }
                        gwmqRecord.setErrorMsg(msgException);
                    }
                }
                gwmqRecord.setMsgState(msgState);
            }
            records.add(gwmqRecord);
        }
        return records;
    }

    public static String getRecordId(WTObject o) {
        ReferenceFactory rf = new ReferenceFactory();
        String keyId = null;
        try {
            keyId = rf.getReferenceString(o);
        } catch (WTException e) {
            e.printStackTrace();
        }
        return keyId;
    }

    public static GWMQRecord updateMsgStateByMsgId(String msgId) {
        GWMQRecord record = null;
        try {
            record = getRecordByMsgId(msgId);
            if (record != null) {
                record.setMsgState("已完成");
                CmPersistenceHelper.manager.update(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return record;
    }

    public static Map<String,JSONObject> getAllMetaMsg(){
        Map<String, JSONObject> map = null;
        try {
            map = new HashMap<String, JSONObject>();
            String url = "";
            PropertiesUtil propertiesUtil = new PropertiesUtil(File.separator + "ext/sast/center/center.properties");
            String isDevelop = propertiesUtil.getProperty("DEVELOP");
            if(isDevelop != null && isDevelop.length()>0 && "true".equals(isDevelop)){
                url = "http://10.112.1.213:8080/avidm/rest/dc/message/meta/query_by_site_types?start=0&end="
                        +System.currentTimeMillis()+"&type=meta_msg_type_signature&type=meta_msg_type_distribute&type=meta_msg_type_share&site="+ MQConstants.SITEIID_149;
            }else{
                url = "http://10.112.1.33:8080/avidm/rest/dc/message/meta/query_by_site_types?start=0&end="
                        +System.currentTimeMillis()+"&type=meta_msg_type_signature&type=meta_msg_type_distribute&type=meta_msg_type_share&site="+ MQConstants.SITEIID_149;
            }
            System.out.println("url ####################### "+url);
            String workflowInfo = InvokeRestServiceProcessor.invokeRestService(url);
            if(!workflowInfo.isEmpty()) {
                JSONArray jsonArray = new JSONArray(workflowInfo);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String msgId = jsonObject.getString(Based.MSG_ID);
                    map.put(msgId,jsonObject);
                }
            }
            System.out.println("metamsg size ####################### "+map.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
