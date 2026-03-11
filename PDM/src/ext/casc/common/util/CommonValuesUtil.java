package ext.casc.common.util;

import wt.util.WTProperties;

import java.io.File;
import java.io.IOException;

public class CommonValuesUtil {
    public static String TEMP_FOLDER = "";
    public static String IXBEXPIMP_FOLDER = "";
    public static String HOST_NAME = "";
    public static String HOST_URL = "";
    static {

        try {
            WTProperties props = WTProperties.getLocalProperties();
            TEMP_FOLDER = props.getProperty("wt.temp");
            IXBEXPIMP_FOLDER= TEMP_FOLDER + File.separator + "IXBExpImp" + File.separator;
            HOST_NAME = props.getProperty("java.rmi.server.hostname");
            HOST_URL = "http://"+HOST_NAME+"/";
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

}
