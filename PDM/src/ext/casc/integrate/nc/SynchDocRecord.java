package ext.casc.integrate.nc;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class SynchDocRecord implements CmPersistable {
    private static final long serialVersionUID = 1L;
    public static final String INSTANCEID = "instanceId";
    public static final String OBJECTNUMBER = "objectNumber";
    public static final String OBJECTVERSION = "objectVersion";
    public static final String SYNCHSTATE = "synchState";
    public static final String IMPORTER = "importer";
    public static final String NOTE = "note";
    public static final String SYNCHTIME = "synchTime";

    private String keyId = "";
    private String instanceId = "";
    private String objectNumber = "";
    private String objectVersion = "";
    private String synchState = "";
    private String importer = "";
    private String note = "";
    private String synchTime = "";
    @Override
    public CmPersistable getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setInstanceId(rs.getString(INSTANCEID));
            setObjectNumber(rs.getString(OBJECTNUMBER));
            setObjectVersion(rs.getString(OBJECTVERSION));
            setSynchState(rs.getString(SYNCHSTATE));
            setImporter(rs.getString(IMPORTER));
            setNote(rs.getString(NOTE));
            setSynchTime(rs.getString(SYNCHTIME));
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
        ret.put(INSTANCEID, instanceId);
        ret.put(OBJECTNUMBER, objectNumber);
        ret.put(OBJECTVERSION, objectVersion);
        ret.put(SYNCHSTATE, synchState);
        ret.put(IMPORTER, importer);
        ret.put(NOTE, note);
        ret.put(SYNCHTIME, synchTime);
        return ret;
    }

    @Override
    public Map getCreateMap() {
        HashMap ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(INSTANCEID, instanceId);
        ret.put(OBJECTNUMBER, objectNumber);
        ret.put(OBJECTVERSION, objectVersion);
        ret.put(SYNCHSTATE, synchState);
        ret.put(IMPORTER, importer);
        ret.put(NOTE, note);
        ret.put(SYNCHTIME, synchTime);
        return ret;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
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

    public String getSynchState() {
        return synchState;
    }

    public void setSynchState(String synchState) {
        this.synchState = synchState;
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
