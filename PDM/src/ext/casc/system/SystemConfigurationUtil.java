package ext.casc.system;

import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;

import java.util.HashMap;
import java.util.Map;

public class SystemConfigurationUtil {

    public static SystemConfigurationBean getSystemConfigurationBean(String key) {
        try {
            CmQuerySpec spec = new CmQuerySpec(SystemConfigurationBean.class);
            spec.appendWhere(SystemConfigurationBean.KEY, CmQuerySpec.EQUAL, key);
            CmQueryResult qr = CmPersistenceHelper.manager.find(spec);
            if(qr.hasNext()){
                return (SystemConfigurationBean) qr.next();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getValue(String key) {
        String value = "";
        SystemConfigurationBean bean = getSystemConfigurationBean(key);
        if(bean != null) {
            value = bean.getValue();
        }
        return value;
    }

    public static Map<String, String> getAllSystemConfigurationBean() {
        Map<String, String> map = new HashMap<>();
        try {
            CmQuerySpec spec = new CmQuerySpec(SystemConfigurationBean.class);
            CmQueryResult qr = CmPersistenceHelper.manager.find(spec);
            while(qr.hasNext()) {
                SystemConfigurationBean bean = (SystemConfigurationBean) qr.next();
                map.put(bean.getKey(), bean.getValue());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
