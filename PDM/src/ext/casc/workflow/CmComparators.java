
package ext.casc.workflow;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Comparator;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.org.WTUser;


public class CmComparators {
    public static final Comparator GB_COMPARATOR        = new GBComparator();
    public static final Comparator GB_WTUSER_COMPARATOR = new GBWTUserComparator();
    public static final Comparator MODIFYSTAMP_DESC_COMPARATOR = new ModifyStampComparator().sort(true); 
    public static final Comparator MODIFYSTAMP_ASC_COMPARATOR = new ModifyStampComparator().sort(false); 
}

class GBWTUserComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        String s1 = getKey(((WTUser) o1).getFullName());
        String s2 = getKey(((WTUser) o2).getFullName());
        return s1.compareTo(s2);
    }

    protected String getKey(Object o) {
        if (o == null)
            return "";

        String s = String.valueOf(o);
        try {
            s = new String(s.getBytes("gb18030"), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return s;
    }
}

class GBComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        String s1 = getKey(o1);
        String s2 = getKey(o2);
        return s1.compareTo(s2);
    }

    protected String getKey(Object o) {
        if (o == null)
            return "";

        String s = String.valueOf(o);
        try {
            s = new String(s.getBytes("gb18030"), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return s;
    }
}

class ModifyStampComparator implements Comparator {
    private boolean desc;
    
    public Comparator sort(boolean desc) {
        this.desc = desc;
        return this;
    }
    
    public int compare(Object o1, Object o2) {
        Timestamp t1 = getKey(o1);
        Timestamp t2 = getKey(o2);
        
        if (t1 == null)
            return 1;
        if (t2 == null)
            return -1;
        return this.desc ? t2.compareTo(t1) : t1.compareTo(t2);
    }

    protected Timestamp getKey(Object o) {
        Timestamp ret = null;
        if (o instanceof Persistable)
            ret = PersistenceHelper.getModifyStamp((Persistable) o);
        else if (o instanceof Timestamp)
            ret = (Timestamp) o;
        return ret;
    }
}
