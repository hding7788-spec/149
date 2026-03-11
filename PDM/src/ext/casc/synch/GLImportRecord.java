package ext.casc.synch;

import ext.sast.common.fc.CmPersistable;
import org.eclipse.swt.internal.win32.MSG;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLImportRecord implements CmPersistable {
    public static final String OBJECTTYPE = "objectType";
    public static final String OBJECTNUMBER = "objectNumber";
    public static final String OBJECTVERSION = "objectVersion";
    public static final String CURRENTOBJECTVERSION = "currentObjectVersion";
    public static final String IMPORTER = "importer";
    public static final String NOTE = "note";
    public static final String MSGID = "msgId";
    public static final String FILENAME = "fileName";
    public static final String SYNCHTIME = "synchTime";
    public static final String EXTMSG = "extMsg";

    private String keyId = "";
    private String objectType = "";
    private String objectNumber = "";
    private String objectVersion = "";

    private String currentObjectVersion = "";
    private String importer = "";
    private String note = "";
    private String msgId = "";
    private String fileName = "";
    private String synchTime = "";
    private String extMsg = "";
    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setObjectType(rs.getString(OBJECTTYPE));
            setObjectNumber(rs.getString(OBJECTNUMBER));
            setObjectVersion(rs.getString(OBJECTVERSION));
            setCurrentObjectVersion(rs.getString(CURRENTOBJECTVERSION));
            setImporter(rs.getString(IMPORTER));
            setNote(rs.getString(NOTE));
            setMsgId(rs.getString(MSGID));
            setFileName(rs.getString(FILENAME));
            setSynchTime(rs.getString(SYNCHTIME));
            setExtMsg(rs.getString(EXTMSG));
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
        ret.put(OBJECTTYPE, objectType);
        ret.put(OBJECTNUMBER, objectNumber);
        ret.put(OBJECTVERSION, objectVersion);
        ret.put(CURRENTOBJECTVERSION, currentObjectVersion);
        ret.put(IMPORTER, importer);
        ret.put(NOTE, note);
        ret.put(MSGID, msgId);
        ret.put(FILENAME, fileName);
        ret.put(EXTMSG, extMsg);
        ret.put(SYNCHTIME, synchTime);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(OBJECTTYPE, objectType);
        ret.put(OBJECTNUMBER, objectNumber);
        ret.put(OBJECTVERSION, objectVersion);
        ret.put(CURRENTOBJECTVERSION, currentObjectVersion);
        ret.put(IMPORTER, importer);
        ret.put(NOTE, note);
        ret.put(MSGID, msgId);
        ret.put(FILENAME, fileName);
        ret.put(EXTMSG, extMsg);
        ret.put(SYNCHTIME, synchTime);
        return ret;
    }

    public String getExtMsg() {
        return extMsg;
    }

    public void setExtMsg(String extMsg) {
        this.extMsg = extMsg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getCurrentObjectVersion() {
        return currentObjectVersion;
    }

    public void setCurrentObjectVersion(String currentObjectVersion) {
        this.currentObjectVersion = currentObjectVersion;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getObjectNumber() {
        return objectNumber;
    }

    public void setObjectNumber(String objectNumber) {
        this.objectNumber = objectNumber;
    }

    public String getObjectVersion() {
        return objectVersion;
    }

    public void setObjectVersion(String objectVersion) {
        this.objectVersion = objectVersion;
    }



    public String getImporter() {
        return importer;
    }

    public void setImporter(String importer) {
        this.importer = importer;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSynchTime() {
        return synchTime;
    }

    public void setSynchTime(String synchTime) {
        this.synchTime = synchTime;
    }
}
