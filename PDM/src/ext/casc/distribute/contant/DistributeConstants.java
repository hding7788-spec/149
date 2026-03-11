package ext.casc.distribute.contant;

import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import ext.casc.constants.PDMConfig;

public class DistributeConstants {

    public static String ID_SPLIT_STR = ":";

    public static String TASK_STATE_NOT_START = "未开始";
    public static String TASK_STATE_IN_WORK = "进行中";
    public static String TASK_STATE_FINISHED = "已完成";

    public static String TASK_STATE_CLOSED_LOOP = "已闭环";


    public static String URL_NC_SYSTEM = "http://10.125.237.4/service/GetGyrwInfo";
    public static String URL_MES_SYSTEM = "http://10.125.237.6/CamstarPortal/webservice.asmx";

    static {
        PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.GLAWAY_149_CONFIG_PATH);
        if(PDMConfig.isZS) {
            URL_NC_SYSTEM = propertiesUtil.getProperty("URL_NC_GETGYRWINFO");
            URL_MES_SYSTEM = propertiesUtil.getProperty("URL_MES");
        } else {
            URL_NC_SYSTEM = propertiesUtil.getProperty("URL_NC_GETGYRWINFO_TEST");
            URL_MES_SYSTEM = propertiesUtil.getProperty("URL_MES_TEST");
        }

    }

}
