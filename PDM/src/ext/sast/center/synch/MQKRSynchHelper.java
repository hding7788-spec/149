package ext.sast.center.synch;

import com.bjsasc.avidm.mq.fileserver.FSUtil;
import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.Message;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.message.win10.Win10SignReqDcMessage;
import com.bjsasc.avidm.mq.message.win11.Win11DistributeReqDcMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import com.glaway.mpm.util.DBConnUtil;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.extend.util.ObjectProperty;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedUtil;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.ases.part.ASESHuiqianSignature;
import ext.ases.part.SignLink;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.workflow.CmWorkflowHelper;
import ext.casc.workflow.PrintHelper;
import ext.casc.workflow.signtrue.zp.ISignatureParser;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.sast.center.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.admin.AdministrativeDomainHelper;
import wt.content.ContentHolder;
import wt.fc.*;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.session.SessionAuthenticator;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MQKRSynchHelper {

    public static boolean needKr(WTObject pbo, ObjectReference self){
        try {
            List<Persistable> memberList = getMemberList(pbo);
            if(memberList!=null){
                for(Persistable o :memberList){
                    if(hasKRPrivilege(ObjectProperty.getNumber(o))){
                       return true;
                    }
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 正式发放
     * @param pbo
     * @param self
     * @return
     */
    public static String distribute(WTObject pbo, ObjectReference self){
        WfProcess process =(WfProcess) self.getObject();
        MetaMessage metaMsg = new MetaMessage();
        LogMessage logMsg = null;
        Sender sender = Sender.getInstance();
        String msgId = "";
        String pboNumber ="";
        String pboName ="";
        try {
            if(pbo instanceof ProcessEnvelope){
                ProcessEnvelope pe = (ProcessEnvelope)pbo;
                pboNumber = pe.getNumber();
                pboName = pe.getName();
            }else if(pbo instanceof ChangePackaged){
                ChangePackaged cp = (ChangePackaged)pbo;
                pboNumber = cp.getNumber();
                pboName = cp.getName();
            }else if(pbo instanceof ChangeRequest){
                ChangeRequest cr = (ChangeRequest)pbo;
                pboNumber = cr.getNumber();
                pboName = cr.getName();
            }
            ProcessData pd = process.getContext();
            WfVariable variable = pd.getVariable("mqmessage");
            String mqmessage = (String) variable.getValue();
            JSONObject lightMsg = new JSONObject(mqmessage);
            String preMsgId = lightMsg.get(Based.MSG_ID).toString();
            String preJsonString = JsonConvertUtil.getJsonFromFile(preMsgId, JsonConvertUtil.DcDistributeRequestHandler);
            JSONObject preJson = new JSONObject(preJsonString);
            msgId = preMsgId + "_"+MQConstants.SITEID_149KRS+"_FF";
            String oldSendFrom = preJson.getString("sendFrom");
            long currentTime = System.currentTimeMillis();
            // 流程监控
            metaMsg.setMsgId(msgId);
            //metaMsg.setOrderIID(pboNumber+ "_"+MQConstants.SITEID_149KRS+"_"+currentTime);
            metaMsg.setOrderID(pboNumber+"_FF_"+MQConstants.SITEID_149KRS);
            metaMsg.setOrderName(pboName);

            metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
            metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
            List sendSites = new ArrayList();
            sendSites.add(MQConstants.SITEID_149KRS);
            JSONObject dst = QuerySiteUtil.getDstSiteInfoForMetaMessage(sendSites);
            metaMsg.setdDstSiteInfo(dst.getString("META_DSTNAME"));
            JSONArray dstSiteArray = dst.getJSONArray("META_DSTIID");
            metaMsg.setdDstSites(dstSiteArray);
            metaMsg.setMsgType(Based.META_MSG_TYPE_DISTRIBUTE);

            sender.addMetaMessage(metaMsg);

            System.out.println("metaMsg @@@ = " + metaMsg);

            Message msg = new Win11DistributeReqDcMessage();
            msg.put(Based.MSG_ID, msgId);
            msg.put(Based.ID, pboNumber+"_"+MQConstants.SITEID_149KRS+"_FF");
            msg.put(Based.ORDER_ID, pboNumber+"_"+MQConstants.SITEID_149KRS+"_FF");

            msg.put(Based.NAME, preJson.get(Based.NAME));
            msg.put(Based.MSG_DESCRIPTION, preJson.get(Based.MSG_DESCRIPTION));
            msg.put(Based.MSG_CREATED_TIME, System.currentTimeMillis());
            msg.put(Based.J_PRODUCT, preJson.get(Based.J_PRODUCT));
            msg.put(Based.J_STD_PRODUCT, preJson.get(Based.J_STD_PRODUCT));
            msg.put(Based.J_CREATOR, preJson.get(Based.J_CREATOR));

            JSONObject j_src_site = new JSONObject();
            j_src_site.put(Based.IID, MQConstants.SITEIID_149);
            j_src_site.put(Based.ID, MQConstants.SITEID_149);
            j_src_site.put(Based.NAME, MQConstants.SITENAME_149);
            msg.put(Based.J_SRC_SITE, j_src_site);

            msg.put(Based.JA_OBJECTS_REQUEST, preJson.get(Based.JA_OBJECTS_REQUEST));
            JSONArray dst_siteArray = new JSONArray();
            JSONObject dst_site = QuerySiteUtil.getDstSite(MQConstants.SITEID_149KRS);
            dst_siteArray.put(dst_site);
            String dst_site_iid = dst_site.getString(Based.IID);

            msg.put(Based.JA_DST_SITES, dst_siteArray);

            JSONArray ja_receivers = new JSONArray();
            msg.put(Based.JA_RECEIVERS, ja_receivers);
            JSONArray pre_j_files = (JSONArray) preJson.get("j_files");
            JSONArray j_files = new JSONArray();
            for(int i=0;i<pre_j_files.length();i++){
                JSONObject jfile = (JSONObject) pre_j_files.get(i);
                jfile.put(Based.SITE_IID, dst_site_iid);
                j_files.put(jfile);
            }
            msg.put("j_files", j_files);

            logMsg = new LogMessage(msgId, MQConstants.SITENAME_149, MQConstants.SITEIID_149,
                    MQConstants.SITENAME_149 + "发起发放请求成功");
            sender.addLog(logMsg);
            msg.put("sendFrom", "149");

            getDistributeListJson(msg,pbo);

            metaMsg.setMsgStatus(Based.MSG_STATUS_PROCESSING);
            sender.updateMetaMessage(metaMsg);
            sender.send(msg);

            JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"Send");

        }catch (Exception e){
            e.printStackTrace();
            metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
            sender.updateMetaMessage(metaMsg);

            logMsg = new LogMessage(msgId, MQConstants.SITENAME_149, MQConstants.SITEIID_149,
                    MQConstants.SITENAME_149 + "发起会签请求失败");
            logMsg.setException(e.getMessage());
            sender.addLog(logMsg);
            return "ERROR";
        }
        return "SUCCESS";
    }
    private static void getDistributeListJson(Message msg, WTObject pbo) throws WTException {
        List<Persistable> memberList = getMemberList(pbo);
        JSONArray krMemberList = new JSONArray();
        if(memberList!=null){
            for(Persistable o :memberList){
                if(hasKRPrivilege(ObjectProperty.getNumber(o))){
                    JSONObject krMember = new JSONObject();
                    krMember.put(MQBasedKey.NUMBER,ObjectProperty.getNumber(o));
                    krMember.put(MQBasedKey.VERSION,"");
                    krMember.put(MQBasedKey.CLASSTYPE,o.getClass().getName());
                    krMemberList.put(krMember);
                }
            }
            if(pbo instanceof  ChangePackaged) {
                JSONObject krMember = new JSONObject();
                ChangePackaged cp = (ChangePackaged)pbo;
                krMember.put(MQBasedKey.NUMBER, cp.getNumber());
                krMember.put(MQBasedKey.VERSION, "");
                krMember.put(MQBasedKey.CLASSTYPE, "wt.change2.WTChangeOrder2");
                krMemberList.put(krMember);
            }
        }

        msg.put(MQBasedKey.KR_MEMBERLIST,krMemberList);
    }

    private static void getAllDistributeListJson(Message msg, WTObject pbo) throws WTException {
        List<Persistable> memberList = getMemberList(pbo);
        JSONArray krMemberList = new JSONArray();
        if(memberList!=null){
            for(Persistable o :memberList){
                JSONObject krMember = new JSONObject();
                krMember.put(MQBasedKey.NUMBER,ObjectProperty.getNumber(o));
                krMember.put(MQBasedKey.VERSION,"");
                krMember.put(MQBasedKey.CLASSTYPE,o.getClass().getName());
                krMemberList.put(krMember);
            }
            if(pbo instanceof  ChangePackaged) {
                JSONObject krMember = new JSONObject();
                ChangePackaged cp = (ChangePackaged)pbo;
                krMember.put(MQBasedKey.NUMBER, cp.getNumber());
                krMember.put(MQBasedKey.VERSION, "");
                krMember.put(MQBasedKey.CLASSTYPE, "wt.change2.WTChangeOrder2");
                krMemberList.put(krMember);
            }
        }

        msg.put(MQBasedKey.KR_MEMBERLIST,krMemberList);
    }

    public static boolean  hasKRPrivilege(String number){
        boolean need = false;
        DBConnUtil  conn= null;
        try {
            conn= new DBConnUtil();
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("select re.DEPARTMENT,re.AMOUNT from ASES_DATA_PRINT_TABLE re where re.OBJ_NUMBER='"+number+"' ");
            ResultSet resultSet  = conn.executeQuery(selectSQL.toString());
            while (resultSet.next()) {
                String dep = resultSet.getString("DEPARTMENT");
                if(dep!=null &&(dep.contains(MQConstants.DEPT_KR)||dep.contains(MQConstants.DEPT_KR_OLD))){
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(conn!=null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return need;
    }


    /**
     * 发送会签
     * @param pbo
     * @param self
     * @return
     */
    public static String send(WTObject pbo, ObjectReference self){
        WfProcess process =(WfProcess) self.getObject();
        MetaMessage metaMsg = new MetaMessage();
        LogMessage logMsg = null;
        Sender sender = Sender.getInstance();
        String msgId = "";
        String pboNumber ="";
        String pboName ="";
        try {
            if(pbo instanceof ProcessEnvelope){
                ProcessEnvelope pe = (ProcessEnvelope)pbo;
                pboNumber = pe.getNumber();
                pboName = pe.getName();
            }else if(pbo instanceof ChangePackaged){
                ChangePackaged cp = (ChangePackaged)pbo;
                pboNumber = cp.getNumber();
                pboName = cp.getName();
            }else if(pbo instanceof ChangeRequest){
                ChangeRequest cr = (ChangeRequest)pbo;
                pboNumber = cr.getNumber();
                pboName = cr.getName();
            }
            ProcessData pd = process.getContext();
            WfVariable variable = pd.getVariable("mqmessage");
            String mqmessage = (String) variable.getValue();
            JSONObject lightMsg = new JSONObject(mqmessage);
            String preMsgId = lightMsg.get(Based.MSG_ID).toString();
            String preJsonString = JsonConvertUtil.getJsonFromFile(preMsgId, JsonConvertUtil.DcSignRequestHandler);
            JSONObject preJson = new JSONObject(preJsonString);
            msgId = preMsgId + "_"+MQConstants.SITEID_149KRS;
            String oldSendFrom = preJson.optString("sendFrom");
            //long currentTime = System.currentTimeMillis();
            // 流程监控
            metaMsg.setMsgId(msgId);
            //metaMsg.setOrderIID(pboNumber+ "_"+MQConstants.SITEID_149KRS+"_"+currentTime);
            metaMsg.setOrderID(pboNumber+"_"+MQConstants.SITEID_149KRS);
            metaMsg.setOrderName(pboName);
            metaMsg.setMsgType(Based.META_MSG_TYPE_SIGNATURE);
            metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
            metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
            List sendSites = new ArrayList();
            sendSites.add(MQConstants.SITEID_149KRS);
            JSONObject dst = QuerySiteUtil.getDstSiteInfoForMetaMessage(sendSites);
            metaMsg.setdDstSiteInfo(dst.getString("META_DSTNAME"));
            JSONArray dstSiteArray = dst.getJSONArray("META_DSTIID");
            metaMsg.setdDstSites(dstSiteArray);
            sender.addMetaMessage(metaMsg);

            System.out.println("metaMsg @@@ = " + metaMsg);

            JSONArray jsonArray = new JSONArray();
            Message msg = new Win10SignReqDcMessage(jsonArray);
            msg.put(Based.MSG_ID, msgId);
            msg.put(Based.ID, pboNumber+"_"+MQConstants.SITEID_149KRS);
            msg.put(Based.ORDER_ID, pboNumber+"_"+MQConstants.SITEID_149KRS);

            msg.put(Based.NAME, preJson.get(Based.NAME));
            msg.put(Based.MSG_DESCRIPTION, preJson.get(Based.MSG_DESCRIPTION));
            msg.put(Based.MSG_CREATED_TIME, System.currentTimeMillis());
            msg.put(Based.J_PRODUCT, preJson.get(Based.J_PRODUCT));
            msg.put(Based.J_STD_PRODUCT, preJson.get(Based.J_STD_PRODUCT));
            msg.put(Based.J_CREATOR, preJson.get(Based.J_CREATOR));

            JSONObject j_src_site = new JSONObject();
            j_src_site.put(Based.IID, MQConstants.SITEIID_149);
            j_src_site.put(Based.ID, MQConstants.SITEID_149);
            j_src_site.put(Based.NAME, MQConstants.SITENAME_149);
            msg.put(Based.J_SRC_SITE, j_src_site);

            msg.put(Based.JA_OBJECTS_REQUEST, preJson.get(Based.JA_OBJECTS_REQUEST));
            JSONArray dst_siteArray = new JSONArray();
            JSONObject dst_site = QuerySiteUtil.getDstSite(MQConstants.SITEID_149KRS);
            dst_siteArray.put(dst_site);
            String dst_site_iid = dst_site.getString(Based.IID);

            msg.put(Based.JA_DST_SITES, dst_siteArray);

            JSONArray ja_receivers = new JSONArray();
            msg.put(Based.JA_RECEIVERS, ja_receivers);
            JSONArray j_files = new JSONArray();
            if(preJson.has("j_files")){
                JSONArray pre_j_files = preJson.getJSONArray("j_files");
                for(int i=0;i<pre_j_files.length();i++){
                    JSONObject jfile = (JSONObject) pre_j_files.get(i);
                    if(MQConstants.SITEIID_149.equals(jfile.getString("site_iid"))) {
                        jfile.put(Based.SITE_IID, dst_site_iid);
                        j_files.put(jfile);
                    }
                }
            }
            if(preJson.has("j_file")){
                JSONObject jfile = preJson.getJSONObject("j_file");
                jfile.put(Based.SITE_IID, dst_site_iid);
                j_files.put(jfile);
            }
            msg.put("j_files", j_files);

            logMsg = new LogMessage(msgId, MQConstants.SITENAME_149, MQConstants.SITEIID_149,
                    MQConstants.SITENAME_149 + "发起会签请求成功");
            sender.addLog(logMsg);
            msg.put("sendFrom", "149");

            getSignatureListJson(msg,pbo, oldSendFrom);

            metaMsg.setMsgStatus(Based.MSG_STATUS_PROCESSING);
            sender.updateMetaMessage(metaMsg);
            sender.send(msg);

            JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"Send");

        }catch (Exception e){
            e.printStackTrace();
            metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
            sender.updateMetaMessage(metaMsg);

            logMsg = new LogMessage(msgId, MQConstants.SITENAME_149, MQConstants.SITEIID_149,
                    MQConstants.SITENAME_149 + "发起会签请求失败");
            logMsg.setException(e.getMessage());
            sender.addLog(logMsg);
            return "ERROR";
        }
        return "SUCCESS";
    }

    /**
     * 生成工艺PDM 指派 科瑞所会签列表
     * @param msg
     * @param pbo
     * @param oldSendFrom
     */
    private static void getSignatureListJson(Message msg, WTObject pbo,String oldSendFrom) throws WTException {
        ISignatureParser parser  = new SignatureGYZZXMLParser((ContentHolder)pbo);
        List<Persistable> memberList = getMemberList(pbo);
        JSONArray krMemberList = new JSONArray();
        if(memberList!=null){
            for(Persistable o :memberList){
                if(parser.hasKRPrivilege(o)){
                    JSONObject krMember = new JSONObject();
                    krMember.put(MQBasedKey.NUMBER,ObjectProperty.getNumber(o));
                    if("no8".equalsIgnoreCase(oldSendFrom)){
                        krMember.put(MQBasedKey.VERSION,ObjectProperty.getVersionIterationDisplay(o).toUpperCase());
                    }else{
                        krMember.put(MQBasedKey.VERSION,ObjectProperty.getVersionIterationDisplay(o));
                    }
                    krMember.put(MQBasedKey.CLASSTYPE,o.getClass().getName());
                    krMemberList.put(krMember);
                }
            }
            if(pbo instanceof  ChangePackaged) {
                JSONObject krMember = new JSONObject();
                ChangePackaged cp = (ChangePackaged)pbo;
                krMember.put(MQBasedKey.NUMBER, cp.getNumber());
                krMember.put(MQBasedKey.VERSION, "");
                krMember.put(MQBasedKey.CLASSTYPE, "wt.change2.WTChangeOrder2");
                krMemberList.put(krMember);
            }
        }

        msg.put(MQBasedKey.KR_MEMBERLIST,krMemberList);
    }

    private static List<Persistable> getMemberList(WTObject pbo) throws WTException {
        boolean check = SessionServerHelper.manager.setAccessEnforced(false);
        List  result = new ArrayList();
        if(pbo instanceof ProcessEnvelope) {
            ProcessEnvelope processEnvelope = (ProcessEnvelope) pbo;
            result = ProcessEnvelopeUtil.getAllMemberLinks(processEnvelope);
        }else if(pbo instanceof ChangePackaged) {
            ChangePackaged cp = (ChangePackaged) pbo;
            result = new ArrayList();
            QueryResult qr = ChangePackagedUtil.getChangeAfterDataByChangePackaged(cp);
            Vector vector = qr.getObjectVectorIfc().getVector();
            result.addAll(vector);
        }
        SessionServerHelper.manager.setAccessEnforced(check);
        return result;
    }

    public static String feedback(JSONObject msg) {
        MethodContext mc = null;
        String context = "Unknown";

        String activityOid ="";
        try {
            mc = MethodContext.getContext(Thread.currentThread());
            if (mc == null)
                mc = new MethodContext(null, null);
            if (mc.getAuthentication() == null) {
                SessionAuthenticator sa = new SessionAuthenticator();
                mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
            }
            context = mc.getId().toString();
            String orderNumber = msg.getString(Based.ORDER_ID);
            if(orderNumber!=null &&orderNumber.endsWith("_WL")){
                orderNumber = orderNumber.replaceAll("_WL","");
            }
            WTObject pbo = CmExpImpSearchHelper.getProcessEnvelopeByNumber(orderNumber);
            if (pbo == null) {
                pbo = CmExpImpSearchHelper.getChangePackagedByNumber(orderNumber);
            }
            if (pbo != null) {
                Enumeration enumeration = WfEngineHelper.service.getAssociatedProcesses(pbo,null,null);
                WfProcess process = null;
                if (enumeration.hasMoreElements()) {
                    process = (WfProcess) enumeration.nextElement();
                    WfAssignedActivity wfAa = getOpenRunningActivity(process);
                    if(wfAa!=null){
                        activityOid = "wt.workflow.work.WfAssignedActivity:"+wfAa.getPersistInfo().getObjectIdentifier().getId()+"";
                        if(msg.has(MQBasedKey.KR_MEMBERSIGNLIST)){
                            JSONArray membersignlist = msg.getJSONArray(MQBasedKey.KR_MEMBERSIGNLIST);
                            for(int i=0;i<membersignlist.length();i++){
                                JSONObject member = (JSONObject) membersignlist.get(i);
                                String number = member.getString(MQBasedKey.NUMBER);
                                String classtype = member.getString(MQBasedKey.CLASSTYPE);
                                WTObject iterated = null;
                                if(classtype.equals("wt.change2.WTChangeOrder2")){
                                    iterated =  CmExpImpSearchHelper.getChangePackagedByNumber(number);
                                }else if(classtype.equals("wt.doc.WTDocument")||classtype.equals("wt.epm.EPMDocument")||classtype.equals("wt.part.WTPart")){
                                    iterated =  (WTObject)CmExpImpSearchHelper.searchLatestIteratedByNumber(Class.forName(classtype),number);
                                }

                                if(iterated == null) continue;

                                ASESHuiqianSignature tempSign = new ASESHuiqianSignature();
                                tempSign.setActivity(activityOid);
                                tempSign.setConclusion(member.optString(MQBasedKey.CONCLUSION));
                                tempSign.setOpinion(member.optString(MQBasedKey.OPINION));
                                tempSign.setSignature(member.optString(MQBasedKey.SIGN_NAME)+"/149厂/"+member.getString(MQBasedKey.SIGN_DATE));
                                tempSign.setImplementadvise("");
                                tempSign.setUpdatestate("");
                                tempSign = (ASESHuiqianSignature) PersistenceHelper.manager.save(tempSign);
                                SignLink sl = SignLink.newSignLink(iterated, tempSign);
                                PersistenceHelper.manager.save(sl);
                            }
                        }
                        CmWorkflowHelper.completeActivity(wfAa.getPersistInfo().getObjectIdentifier().toString(), "", "科瑞所反馈意见");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return "ERROR";
        }finally {
            if (mc != null) {
                mc.unregister();
            }
        }

        return "";
    }

    public static WfAssignedActivity getOpenRunningActivity(WfProcess wfprocess) throws Exception {
        List<WfBlock> allWfBlocks = PrintHelper.getAllBlock(wfprocess);
        for(WfBlock block :allWfBlocks){
            Enumeration enumeration = WfEngineHelper.service.getProcessSteps(block, null);
            while (enumeration.hasMoreElements()) {
                WfActivity wfactivity = (WfActivity) enumeration.nextElement();
                if (wfactivity instanceof WfAssignedActivity) {
                    WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
                    String state = wfassignedactivity.getState().toString();
                    if ("OPEN_RUNNING".equals(state)&&MQConstants.ACTIVITY_NAME_KRS.equals(wfassignedactivity.getName())) {
                        return wfassignedactivity;
                    }
                }
            }
        }

        return null;

    }

    public static String sendToKr(ProcessEnvelope pe, Map<String, String> params){
        Message msg = null;
        MetaMessage metaMsg = new MetaMessage();
        LogMessage logMsg = null;
        Sender sender = Sender.getInstance();
        String   msgId = pe.getNumber() + "_"+MQConstants.SITEID_149KRS+"_FF";
        try {
            List<Persistable> resultMember = new ArrayList<Persistable>();
            resultMember.add(pe);
            List<Persistable> member = getMemberList(pe);
            resultMember.addAll(MQDataSynchHelper.collectingData(member)) ;

            resultMember.addAll(MQDataSynchHelper.collectingChangePackaged(member)) ;
            String fileName = MQDataSynchHelper.exportTargets(resultMember);

            msg = new Win11DistributeReqDcMessage();
            metaMsg.setMsgType(Based.META_MSG_TYPE_DISTRIBUTE);
            // 流程监控
            metaMsg.setMsgId(msgId);
            metaMsg.setOrderIID(pe.getNumber()+ "_"+MQConstants.SITEID_149KRS);
            metaMsg.setOrderID(pe.getNumber());
            metaMsg.setOrderName(pe.getName());


            metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
            metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
            List sendSites = new ArrayList();
            sendSites.add(MQConstants.SITEID_149KRS);
            JSONObject dst = QuerySiteUtil.getDstSiteInfoForMetaMessage(sendSites);
            metaMsg.setdDstSiteInfo(dst.getString("META_DSTNAME"));
            JSONArray dstSiteArray = dst.getJSONArray("META_DSTIID");
            metaMsg.setdDstSites(dstSiteArray);
            metaMsg.setMsgType(Based.META_MSG_TYPE_DISTRIBUTE);

            sender.addMetaMessage(metaMsg);

            System.out.println("metaMsg @@@ = " + metaMsg);
            String fawangdanwei =   params.get(MQConstants.FAWANGDANWEI);
            if(Tools.isNull(fawangdanwei)){
                fawangdanwei = IBAHelper.getIBAStringValue(pe,MQConstants.FAWANGDANWEI);
            }
            msg.put(MQConstants.FAWANGDANWEI,fawangdanwei);
            msg.put(Based.MSG_ID, msgId);
            msg.put(Based.ID, msgId);
            msg.put(Based.ORDER_ID, msgId);

            msg.put(Based.NAME, pe.getName());
            msg.put(Based.MSG_DESCRIPTION, "149工艺补发到科瑞所");
            msg.put(Based.MSG_CREATED_TIME, System.currentTimeMillis());

            WTContainerRef wtcontainerref = pe.getContainerReference();
            String localProductId = wtcontainerref.getObject().getPersistInfo().getObjectIdentifier().getId()+"";
            String sastName = ProductConvertUtil.getStandardProductByLocalProductId(localProductId);
            JSONObject j_std_product = ProductConvertUtil.getSastProdcutInfo(sastName);
            msg.put(Based.J_STD_PRODUCT, j_std_product);
            JSONObject j_product = new JSONObject();
            j_product.put(Based.IID, localProductId);
            j_product.put(Based.ID, wtcontainerref.getName());
            j_product.put(Based.NAME, wtcontainerref.getName());
            msg.put(Based.J_PRODUCT, j_product);

            JSONObject j_creator = new JSONObject();
            j_creator.put(Based.IID, pe.getCreator().getObject().getPersistInfo().getObjectIdentifier().getId());
            j_creator.put(Based.ID, pe.getCreator().getName());
            j_creator.put(Based.NAME, pe.getCreatorFullName());
            msg.put(Based.J_CREATOR, j_creator);

            JSONObject j_src_site = new JSONObject();
            j_src_site.put(Based.IID, MQConstants.SITEIID_149);
            j_src_site.put(Based.ID, MQConstants.SITEID_149);
            j_src_site.put(Based.NAME, MQConstants.SITENAME_149);
            msg.put(Based.J_SRC_SITE, j_src_site);

            JSONArray ja_objects = QueryUtil.getAllObjectInfo(pe,member);

            msg.put(Based.JA_OBJECTS_REQUEST, ja_objects);
            JSONArray dst_siteArray = new JSONArray();
            JSONObject dst_site = QuerySiteUtil.getDstSite(MQConstants.SITEID_149KRS);
            dst_siteArray.put(dst_site);
            String dst_site_iid = dst_site.getString(Based.IID);

            msg.put(Based.JA_DST_SITES, dst_siteArray);

            JSONArray ja_receivers = new JSONArray();
            msg.put(Based.JA_RECEIVERS, ja_receivers);
            JSONArray j_files = new JSONArray();
            String filePath = PropertiesUtil.getTempPath()+ File.separator + "IXBExpImp";
            if (fileName.contains(",")) {
                String[] fileNames = fileName.split(",");
                int index = 1;
                for (String fn : fileNames) {
                    String localFilePath = filePath + File.separator + fn;
                    JSONObject j_file = FSUtil.upload(localFilePath, MQConstants.DC_UPLOAD);
                    j_file.put(Based.SITE_IID, dst_site_iid);
                    // 为了支持未来10G数据拆包
                    j_file.put("j_file_index", index);
                    j_files.put(j_file);
                    index++;
                }
            } else {
                String localFilePath = filePath + File.separator + fileName;
                JSONObject j_file = FSUtil.upload(localFilePath, MQConstants.DC_UPLOAD);
                j_file.put(Based.SITE_IID, dst_site_iid);
                // 为了支持未来10G数据拆包
                j_file.put("j_file_index", 1);
                j_files.put(j_file);
                msg.put("j_file", j_file);

            }
            msg.put("j_files", j_files);

            logMsg = new LogMessage(msgId, MQConstants.SITENAME_149, MQConstants.SITEIID_149,
                    MQConstants.SITENAME_149 + "发起发放请求成功");
            sender.addLog(logMsg);
            msg.put("sendFrom", "149");

            getAllDistributeListJson(msg,pe);

            metaMsg.setMsgStatus(Based.MSG_STATUS_PROCESSING);
            sender.updateMetaMessage(metaMsg);
            sender.send(msg);

            JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"Send");

        } catch (Exception e) {
            e.printStackTrace();
            metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
            sender.updateMetaMessage(metaMsg);

            logMsg = new LogMessage(msgId, MQConstants.SITENAME_149, MQConstants.SITEIID_149,
                    MQConstants.SITENAME_149 + "发起会签请求失败");
            logMsg.setException(e.getMessage());
            sender.addLog(logMsg);
            return e.getLocalizedMessage();
        }
        return "";
    }



    public static String sendToKr(ChangePackaged cp, Map<String, String> params){
        Message msg = null;
        MetaMessage metaMsg = new MetaMessage();
        LogMessage logMsg = null;
        Sender sender = Sender.getInstance();
        String   msgId = cp.getNumber() + "_"+MQConstants.SITEID_149KRS+"_FF";
        try {
            List<Persistable> resultMember = new ArrayList<Persistable>();
            resultMember.add(cp);
            List<Persistable> member = getMemberList(cp);
            resultMember.addAll(MQDataSynchHelper.collectingData(member)) ;

            resultMember.addAll(MQDataSynchHelper.collectingChangePackaged(member)) ;
            String fileName = MQDataSynchHelper.exportTargets(resultMember);

            msg = new Win11DistributeReqDcMessage();
            metaMsg.setMsgType(Based.META_MSG_TYPE_DISTRIBUTE);
            // 流程监控
            metaMsg.setMsgId(msgId);
            metaMsg.setOrderIID(msgId);
            metaMsg.setOrderID(cp.getNumber());
            metaMsg.setOrderName(cp.getName());

            metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
            metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
            List sendSites = new ArrayList();
            sendSites.add(MQConstants.SITEID_149KRS);
            JSONObject dst = QuerySiteUtil.getDstSiteInfoForMetaMessage(sendSites);
            metaMsg.setdDstSiteInfo(dst.getString("META_DSTNAME"));
            JSONArray dstSiteArray = dst.getJSONArray("META_DSTIID");
            metaMsg.setdDstSites(dstSiteArray);
            metaMsg.setMsgType(Based.META_MSG_TYPE_DISTRIBUTE);

            sender.addMetaMessage(metaMsg);

            System.out.println("metaMsg @@@ = " + metaMsg);
            String fawangdanwei =   params.get(MQConstants.FAWANGDANWEI);
            msg.put(MQConstants.FAWANGDANWEI,fawangdanwei);
            msg.put(Based.MSG_ID, msgId);
            msg.put(Based.ID, msgId);
            msg.put(Based.ORDER_ID, msgId);

            msg.put(Based.NAME, cp.getName());
            msg.put(Based.MSG_DESCRIPTION, "149工艺补发到科瑞所");
            msg.put(Based.MSG_CREATED_TIME, System.currentTimeMillis());

            WTContainerRef wtcontainerref = cp.getContainerReference();
            String localProductId = wtcontainerref.getObject().getPersistInfo().getObjectIdentifier().getId()+"";
            String sastName = ProductConvertUtil.getStandardProductByLocalProductId(localProductId);
            JSONObject j_std_product = ProductConvertUtil.getSastProdcutInfo(sastName);
            msg.put(Based.J_STD_PRODUCT, j_std_product);
            JSONObject j_product = new JSONObject();
            j_product.put(Based.IID, localProductId);
            j_product.put(Based.ID, wtcontainerref.getName());
            j_product.put(Based.NAME, wtcontainerref.getName());
            msg.put(Based.J_PRODUCT, j_product);

            JSONObject j_creator = new JSONObject();
            j_creator.put(Based.IID, cp.getCreator().getObject().getPersistInfo().getObjectIdentifier().getId());
            j_creator.put(Based.ID, cp.getCreator().getName());
            j_creator.put(Based.NAME, cp.getCreatorFullName());
            msg.put(Based.J_CREATOR, j_creator);

            JSONObject j_src_site = new JSONObject();
            j_src_site.put(Based.IID, MQConstants.SITEIID_149);
            j_src_site.put(Based.ID, MQConstants.SITEID_149);
            j_src_site.put(Based.NAME, MQConstants.SITENAME_149);
            msg.put(Based.J_SRC_SITE, j_src_site);

            JSONArray ja_objects = QueryUtil.getAllObjectInfo(cp,member);

            msg.put(Based.JA_OBJECTS_REQUEST, ja_objects);
            JSONArray dst_siteArray = new JSONArray();
            JSONObject dst_site = QuerySiteUtil.getDstSite(MQConstants.SITEID_149KRS);
            dst_siteArray.put(dst_site);
            String dst_site_iid = dst_site.getString(Based.IID);

            msg.put(Based.JA_DST_SITES, dst_siteArray);

            JSONArray ja_receivers = new JSONArray();
            msg.put(Based.JA_RECEIVERS, ja_receivers);
            JSONArray j_files = new JSONArray();
            String filePath = PropertiesUtil.getTempPath()+ File.separator + "IXBExpImp";
            if (fileName.contains(",")) {
                String[] fileNames = fileName.split(",");
                int index = 1;
                for (String fn : fileNames) {
                    String localFilePath = filePath + File.separator + fn;
                    JSONObject j_file = FSUtil.upload(localFilePath, MQConstants.DC_UPLOAD);
                    j_file.put(Based.SITE_IID, dst_site_iid);
                    // 为了支持未来10G数据拆包
                    j_file.put("j_file_index", index);
                    j_files.put(j_file);
                    index++;
                }
            } else {
                String localFilePath = filePath + File.separator + fileName;
                JSONObject j_file = FSUtil.upload(localFilePath, MQConstants.DC_UPLOAD);
                j_file.put(Based.SITE_IID, dst_site_iid);
                // 为了支持未来10G数据拆包
                j_file.put("j_file_index", 1);
                j_files.put(j_file);
                msg.put("j_file", j_file);

            }
            msg.put("j_files", j_files);

            logMsg = new LogMessage(msgId, MQConstants.SITENAME_149, MQConstants.SITEIID_149,
                    MQConstants.SITENAME_149 + "发起发放请求成功");
            sender.addLog(logMsg);
            msg.put("sendFrom", "149");

            getAllDistributeListJson(msg,cp);

            metaMsg.setMsgStatus(Based.MSG_STATUS_PROCESSING);
            sender.updateMetaMessage(metaMsg);
            sender.send(msg);

            JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"Send");

        } catch (Exception e) {
            e.printStackTrace();
            metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
            sender.updateMetaMessage(metaMsg);

            logMsg = new LogMessage(msgId, MQConstants.SITENAME_149, MQConstants.SITEIID_149,
                    MQConstants.SITENAME_149 + "发起会签请求失败");
            logMsg.setException(e.getMessage());
            sender.addLog(logMsg);
            return e.getLocalizedMessage();
        }
        return "";
    }
}
