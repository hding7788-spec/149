package ext.casc.persistence;

import com.glaway.mpm.util.ReferenceFactory;
import wt.fc.Persistable;
import wt.util.WTException;

public class PersistenceCommonHelper {

    public static String getOid(Persistable p ){
        return "OR:"+p;
    }

    public static Persistable getPersistable(String oid ) throws WTException {
        return ReferenceFactory.getObjectbyOid(oid);
    }
}
