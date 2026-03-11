package ext.casc.service.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.org.WTUser;

public class StoredCacheBean implements Serializable {
    private static final long serialVersionUID = 1L;
    Map<String, List<WTUser>> map = new HashMap<String, List<WTUser>>();

    public Map<String, List<WTUser>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<WTUser>> map) {
        this.map = map;
    }

}
