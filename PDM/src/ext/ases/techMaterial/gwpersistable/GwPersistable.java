package ext.ases.techMaterial.gwpersistable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Map;

import wt.fc.Persistable;


public interface GwPersistable extends Serializable {
  
    public static final String KEY_ID = "GWKEYID";
    
    public abstract GwPersistable getObject(ResultSet rs) throws Exception;

    public abstract Object getKeyId();

    public abstract Map<?, ?> getUpdateMap();

    public abstract Map<?, ?> getCreateMap();
}
