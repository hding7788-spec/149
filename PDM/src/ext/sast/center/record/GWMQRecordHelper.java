/**
 * 南京国睿信维软件有限公司
 */
package ext.sast.center.record;

import com.bjsasc.avidm.mq.message.Based;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.ixb.IXBConstants;
import ext.sast.center.record.bean.GWMQRecord;
import ext.sast.center.util.ProductConvertUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.admin.AdministrativeDomainHelper;
import wt.doc.WTDocument;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.method.MethodContext;
import wt.session.SessionAuthenticator;
import wt.util.WTException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类功能：
 *
 * @author YaQii
 * @date 2021/1/6
 */
public class GWMQRecordHelper {
    public  static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String FFD = "发放单";
    public static final String SSD = "送审单";
    public static final String GGD = "更改单";
    public static final String YYD = "预审单";
    public static final String GGSQ = "更改申请单";

    public static void processMQRecord(JSONObject msg) {
        MethodContext mc = null;
        GWMQRecord record = null;
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

            String msgId = msg.getString(Based.MSG_ID);
            record = GWMQRecordService.getRecordByMsgId(msgId);
            if(record == null){
                record = new GWMQRecord();
            }
            record.setKeyId(msgId);
            record.setMsgId(msgId);
            Map<String,String> infoMsg = getOrderInfo(msg);
            JSONObject sendFromJson = msg.getJSONObject(Based.J_SRC_SITE);
            String sendFrom = sendFromJson.getString(Based.NAME);
            record.setSender(sendFrom);
            if(infoMsg.get(GWMQRecord.RECEIVER)!=null&&!"".equals(infoMsg.get(GWMQRecord.RECEIVER))){
                record.setReceiver("149("+infoMsg.get(GWMQRecord.RECEIVER)+")");
            }else{
                record.setReceiver("149");
            }
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR, 8);
            Date now = c.getTime();
            long sendTime = now.getTime();

            String orderName = infoMsg.get(GWMQRecord.ORDERNAME);

            record.setOrderNumber(infoMsg.get(GWMQRecord.ORDERNUMBER) != null ? infoMsg.get(GWMQRecord.ORDERNUMBER) : "");
            record.setOrderName(orderName != null ? orderName : "");
            record.setProductName(infoMsg.get(GWMQRecord.PRODUCTNAME));
            String processName = "";
            String msg_type  = msg.getString(Based.MSG_TYPE);
            if("avidm_dc_distribute_req_receiver".equals(msg_type)){
                record.setSendType(FFD);
            }else if("avidm_dc_countersign_req_receiver".equals(msg_type)){
                if (orderName != null && (orderName.contains("更改单") || orderName.contains("变更单"))) {
                    record.setSendType(GGD);
                    processName = "149变更包工艺会签流程";
                } else if (orderName != null && (orderName.contains("更改申请") || orderName.contains("变更请求"))) {
                    record.setSendType(GGSQ);
                    processName = "149变更申请工艺会签流程";
                } else {
                    record.setSendType(SSD);
                    processName = "149签审包工艺会签流程";
                }
            }else if("avidm_dc_share_req_receiver".equals(msg_type)){
                record.setSendType(YYD);
                processName = IXBConstants.PREVIEWWORKFLOWNAME;
            }
            record.setSendState("接收到会签请求");
            record.setSendTime(sendTime+"");
            record.setUpdateTime(sendTime+"");


            record.setProcessName(processName);
            record.setOrderOwner(infoMsg.get(GWMQRecord.ORDEROWNER));
            record.setCreateTimeStamp(sendTime + "");
            //需要重新处理
            record.setMsgState("处理中");

            GWMQRecordService.add(record);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mc != null){
                mc.unregister();
            }
        }
    }

    private static Map<String, String> getOrderInfo(JSONObject msg) {
        Map<String, String> infos = new HashMap<String,String>();
        try {
            if (msg.has(Based.JA_OBJECTS_REQUEST)) {
                JSONArray ja_objects_request = msg.getJSONArray(Based.JA_OBJECTS_REQUEST);
                if (ja_objects_request != null) {
                    if(ja_objects_request.length()==1) {
                        JSONObject jsonObject =ja_objects_request.getJSONObject(0);
                        String orderNumber = jsonObject.getString(Based.OBJECT_ID);
                        String orderName = jsonObject.getString(Based.OBJECT_NAME);
                        infos.put(GWMQRecord.ORDERNUMBER,orderNumber);
                        infos.put(GWMQRecord.ORDERNAME,orderName);
                    }else {
                        for(int i=0;i<ja_objects_request.length();i++) {
                            JSONObject jsonObject =ja_objects_request.getJSONObject(i);
                            String objectType = jsonObject.getString(Based.OBJECT_TYPE);
                            if("_Order".equals(objectType)){
                                String orderNumber = jsonObject.getString(Based.OBJECT_ID);
                                String orderName = jsonObject.getString(Based.OBJECT_NAME);
                                infos.put(GWMQRecord.ORDERNUMBER,orderNumber);
                                infos.put(GWMQRecord.ORDERNAME,orderName);
                                break;
                            }
                        }
                    }
                }
            }
            if (msg.has(Based.J_STD_PRODUCT)) {
                JSONObject j_product = msg.getJSONObject(Based.J_STD_PRODUCT);
                String productid =  j_product.getString(Based.ID);
                String productName = ProductConvertUtil.getLocalProductName(productid);
                infos.put(GWMQRecord.PRODUCTNAME,productName);
            }

            if (msg.has(Based.JA_RECEIVERS)) {
                JSONArray ja_receivers = msg.getJSONArray(Based.JA_RECEIVERS);
                if (ja_receivers != null) {
                    StringBuilder sb = new StringBuilder();
                    for(int i=0;i<ja_receivers.length();i++) {
                        JSONObject jsonObject = ja_receivers.getJSONObject(i);
                        String name = jsonObject.getString(Based.NAME);
                        if(sb.length()<=0){
                            sb.append(name);
                        }else{
                            sb.append(",").append(name);
                        }

                    }

                    infos.put(GWMQRecord.RECEIVER,sb.toString());
                }
            }
            if (msg.has(Based.J_CREATOR)) {
                JSONObject j_creator = msg.getJSONObject(Based.J_CREATOR);
                String owner =  j_creator.getString(Based.NAME);
                infos.put(GWMQRecord.ORDEROWNER,owner);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return  infos;
    }

    public static String getObjectOidByNumberAndType(String number,String type) throws WTException {

        String oid = "";
        if(type.contains("songshendan")){
            ProcessEnvelope pe = (ProcessEnvelope) CmExpImpSearchHelper.getProcessEnvelopeByNumber(number);
            if(pe != null){
               oid = "OR:"+ IxbHndHelper.getObjectIdImage(pe);
            }else{
                pe = (ProcessEnvelope) CmExpImpSearchHelper.getProcessEnvelopeByNumber(number + "_KYJSHQ");
                if(pe != null){
                    oid = "OR:"+ IxbHndHelper.getObjectIdImage(pe);
                }else{
                    pe = (ProcessEnvelope) CmExpImpSearchHelper.getProcessEnvelopeByNumber(number + "_KYGYHQ");
                    if(pe != null){
                        oid = "OR:"+ IxbHndHelper.getObjectIdImage(pe);
                    }
                }
            }
        }else if(type.contains("genggaidan")){
            ChangePackaged changePackaged = (ChangePackaged) CmExpImpSearchHelper.getChangePackagedByNumber(number);

            if(changePackaged != null){
                oid = "OR:"+ IxbHndHelper.getObjectIdImage(changePackaged);
            }else{
                changePackaged = (ChangePackaged) CmExpImpSearchHelper.getChangePackagedByNumber(number + "_KYJSHQ");
                if(changePackaged != null){
                    oid = "OR:"+ IxbHndHelper.getObjectIdImage(changePackaged);
                }else{
                    changePackaged = (ChangePackaged) CmExpImpSearchHelper.getChangePackagedByNumber(number + "_KYGYHQ");
                    if(changePackaged != null){
                        oid = "OR:"+ IxbHndHelper.getObjectIdImage(changePackaged);
                    }
                }
            }
        }else if(type.contains("genggaishenqing")){
            ChangeRequest changeRequest = (ChangeRequest) CmExpImpSearchHelper.getChangeRequestByNumber(number);
            if(changeRequest != null){
                oid = "OR:"+ IxbHndHelper.getObjectIdImage(changeRequest);
            }else{
                changeRequest = (ChangeRequest) CmExpImpSearchHelper.getChangeRequestByNumber(number + "_KYJSHQ");
                if(changeRequest != null){
                    oid = "OR:"+ IxbHndHelper.getObjectIdImage(changeRequest);
                }else{
                    changeRequest = (ChangeRequest) CmExpImpSearchHelper.getChangeRequestByNumber(number + "_KYGYHQ");
                    if(changeRequest != null){
                        oid = "OR:"+ IxbHndHelper.getObjectIdImage(changeRequest);
                    }
                }
            }
        }else if(type.contains("processNotice")){
            WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTDocument.class,number);
            if(doc != null){
                oid = "OR:"+ IxbHndHelper.getObjectIdImage(doc);
            }
        }
        return oid;
    }
}
