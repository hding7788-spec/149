package ext.casc.nc.bean;

import ext.sast.common.fc.CmPersistable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class GLZYKPartMapping implements CmPersistable {
    public GLZYKPartMapping() {
    }

    private static final long serialVersionUID = 4007533850133929576L;

    public static final String OLDPARTNUMBER = "oldPartNumber";
    public static final String NEWPARTNUMBER = "newPartNumber";
    public static final String STATE = "state";
    public static final String SYNCHTIME = "synchtime";


    private String keyId = "";
    private String oldPartNumber;
    private String newPartNumber;
    private String state;
    private String synchtime;



    @Override
    public GLZYKPartMapping getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setOldPartNumber(rs.getString(OLDPARTNUMBER));
            setNewPartNumber(rs.getString(NEWPARTNUMBER));
            setState(rs.getString(STATE));
            setSynchtime(rs.getString(SYNCHTIME));

        }
        return this;
    }


    @Override
    public Object getKeyId() {
        return this.keyId;
    }

    @Override
    public Map<String, Object> getUpdateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(OLDPARTNUMBER, oldPartNumber);
        ret.put(NEWPARTNUMBER, newPartNumber);
        ret.put(STATE, state);
        ret.put(SYNCHTIME, synchtime);

        return ret;
    }

    @Override
    public Map<String, Object> getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(OLDPARTNUMBER, oldPartNumber);
        ret.put(NEWPARTNUMBER, newPartNumber);
        ret.put(STATE, state);
        ret.put(SYNCHTIME, synchtime);
        return ret;
    }

    @Override
    public String toString() {
        return "GLNCPartMapping{" +
                "keyId='" + keyId + '\'' +
                ", oldPartBumber='" + oldPartNumber + '\'' +
                ", newPartNumber='" + newPartNumber + '\'' +
                ", state='" + state + '\'' +
                ", synchtime='" + synchtime + '\'' +
                '}';
    }

    public String getOldPartNumber() {
        return oldPartNumber;
    }

    public void setOldPartNumber(String oldPartNumber) {
        this.oldPartNumber = oldPartNumber;
    }

    public String getNewPartNumber() {
        return newPartNumber;
    }

    public void setNewPartNumber(String newPartNumber) {
        this.newPartNumber = newPartNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSynchtime() {
        return synchtime;
    }

    public void setSynchtime(String synchtime) {
        this.synchtime = synchtime;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}

