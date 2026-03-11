package ext.sast.center.record.bean;

import com.ptc.netmarkets.model.NmObject;
import com.ptc.netmarkets.model.NmSimpleOid;
import ext.sast.common.fc.CmPersistable;
import wt.util.WTException;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GWMQRecord implements CmPersistable {
    public GWMQRecord() {
    }

    private static final long serialVersionUID = 0x379d9ecdca961e68L;

    public static final String MSGID = "msgId";
    public static final String ORDERNUMBER = "orderNumber";
    public static final String ORDERNAME = "orderName";
    public static final String PROCESSNAME = "processName";
    public static final String ORDEROWNER = "orderOwner";
    public static final String PRODUCTNAME = "productName";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String SENDTYPE = "sendType";
    public static final String SENDSTATE = "sendState";
    public static final String MSGSTATE = "msgState";
    public static final String CREATETIMESTAMP = "createTimeStamp";
    public static final String SENDTIME = "sendTime";
    public static final String UPDATETIME = "updateTime";
    public static final String ERRORMSG = "errorMsg";

    private String keyId = "";
    private String msgId = "";
    private String orderNumber = "";
    private String orderName = "";
    private String processName = "";
    private String orderOwner = "";
    private String productName = "";
    private String sender = "";
    private String receiver = "";
    private String sendType = "";
    private String msgState = "";
    private String sendState = "";
    private String sendTime = "";
    private String createTimeStamp = "";
    private String updateTime = "";
    private String errorMsg = "";


    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(String createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSendState() {
        return sendState;
    }

    public void setSendState(String sendState) {
        this.sendState = sendState;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderOwner() {
        return orderOwner;
    }

    public void setOrderOwner(String orderOwner) {
        this.orderOwner = orderOwner;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getMsgState() {
        return msgState;
    }

    public void setMsgState(String msgState) {
        this.msgState = msgState;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setMsgId(rs.getString(MSGID));
            setOrderNumber(rs.getString(ORDERNUMBER));
            setOrderName(rs.getString(ORDERNAME));
            setProcessName(rs.getString(PROCESSNAME));
            setOrderOwner(rs.getString(ORDEROWNER));
            setProductName(rs.getString(PRODUCTNAME));
            setSender(rs.getString(SENDER));
            setSendType(rs.getString(SENDTYPE));
            setReceiver(rs.getString(RECEIVER));
            setMsgState(rs.getString(MSGSTATE));
            setSendState(rs.getString(SENDSTATE));
            setSendTime(rs.getString(SENDTIME));
            setUpdateTime(rs.getString(UPDATETIME));
            setErrorMsg(rs.getString(ERRORMSG));
            setCreateTimeStamp(rs.getString(CREATETIMESTAMP));
        }
        return this;
    }

    @Override
    public Object getKeyId() {
        return this.keyId;
    }

    @Override
    public Map getUpdateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(MSGID, msgId);
        ret.put(ORDERNUMBER, orderNumber);
        ret.put(ORDERNAME, orderName);
        ret.put(PROCESSNAME, processName);
        ret.put(ORDEROWNER, orderOwner);
        ret.put(PRODUCTNAME, productName);
        ret.put(SENDER, sender);
        ret.put(RECEIVER, receiver);
        ret.put(SENDTYPE, sendType);
        ret.put(MSGSTATE, msgState);
        ret.put(SENDSTATE, sendState);
        ret.put(SENDTIME, sendTime);
        ret.put(UPDATETIME, updateTime);
        ret.put(ERRORMSG, errorMsg);
        ret.put(CREATETIMESTAMP, createTimeStamp);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(MSGID, msgId);
        ret.put(ORDERNUMBER, orderNumber);
        ret.put(ORDERNAME, orderName);
        ret.put(PROCESSNAME, processName);
        ret.put(ORDEROWNER, orderOwner);
        ret.put(PRODUCTNAME, productName);
        ret.put(SENDER, sender);
        ret.put(RECEIVER, receiver);
        ret.put(SENDTYPE, sendType);
        ret.put(MSGSTATE, msgState);
        ret.put(SENDSTATE, sendState);
        ret.put(SENDTIME, sendTime);
        ret.put(UPDATETIME, updateTime);
        ret.put(ERRORMSG, errorMsg);
        ret.put(CREATETIMESTAMP, createTimeStamp);
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        boolean flag = false;
        if (obj != null && (obj instanceof GWMQRecord)) {
            GWMQRecord record = (GWMQRecord) obj;
            if (getOid().equals(record.getOid())) {
                flag = true;
            }
        }
        return flag;
    }

    public String getOid() {
        String s = (new StringBuilder()).append(this.msgId).append("_").append(this.orderNumber).toString();
        return s;
    }

    public NmObject getNmObject() throws WTException {
        NmSimpleOid nmsimpleoid = new NmSimpleOid();
        nmsimpleoid.setInternalName(getOid());
        return NmObject.newNmObject(nmsimpleoid);
    }

    @Override
    public String toString() {
        return "GWMQRecord{" +
                "keyId='" + keyId + '\'' +
                ", msgId='" + msgId + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderName='" + orderName + '\'' +
                ", processName='" + processName + '\'' +
                ", orderOwner='" + orderOwner + '\'' +
                ", productName='" + productName + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", sendType='" + sendType + '\'' +
                ", msgState='" + msgState + '\'' +
                ", sendState='" + sendState + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", createTimeStamp='" + createTimeStamp + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
