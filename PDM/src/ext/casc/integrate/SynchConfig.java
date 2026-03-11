package ext.casc.integrate;

import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import ext.casc.constants.PDMConfig;

public class SynchConfig {
    public  static String SENKE_VIEW_URL ;
    public  static String SENKE_SEND_URL ;
    public  static String NC_SEND_URL ;
    public  static String NC_SEND_URL2 ;
    static {
        PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.GLAWAY_149_CONFIG_PATH);
        if(PDMConfig.isZS){
            SENKE_SEND_URL = propertiesUtil.getProperty("SENKE_SEND_URL_ZS");
            SENKE_VIEW_URL = propertiesUtil.getProperty("SENKE_VIEW_URL_ZS");

            NC_SEND_URL = propertiesUtil.getProperty("NC_SEND_URL_ZS");
            NC_SEND_URL2 = propertiesUtil.getProperty("NC_SEND_URL2_ZS");
        }else{
            SENKE_SEND_URL = propertiesUtil.getProperty("SENKE_SEND_URL");
            SENKE_VIEW_URL = propertiesUtil.getProperty("SENKE_VIEW_URL");

            NC_SEND_URL = propertiesUtil.getProperty("NC_SEND_URL");
            NC_SEND_URL2 = propertiesUtil.getProperty("NC_SEND_URL2");
        }



    }
}
