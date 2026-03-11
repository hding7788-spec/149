package ext.casc.constants;

import wt.util.WTProperties;

public class PDMConfig {
    public  static boolean isZS = true;//是否正式机
    public  static String WCADMIN_PASSWORD = "Admin@149.941";
    static{
        try{
            String hostName = WTProperties.getLocalProperties().getProperty("java.rmi.server.hostname");
            System.out.println("hostName:"+hostName);
            if(!"pdm.149.sast.casc".equals(hostName)){
                isZS =false;
                WCADMIN_PASSWORD = "wcadmin";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
