package ext.casc.integrate.pfmea;

import ext.casc.integrate.pfmea.client.FMEAImplService;
import ext.casc.integrate.pfmea.client.IFmea;
import ext.casc.integrate.pfmea.client.UnsupportedEncodingException_Exception;

public class PfmeaClientRMI {

    /**
     * 调用pfmea新建项目接口
     * @param pid 项目id
     * @return
     * @throws UnsupportedEncodingException_Exception pfmea异常
     */
    public static String createProject(Long pid) throws Exception {
        FMEAImplService fmeaImplService = new FMEAImplService();
        IFmea fmeaImplPort = fmeaImplService.getFMEAImplPort();
        return fmeaImplPort.createProject(pid);
    }
}
