package ext.casc.distribute.integration;

import cn.hutool.http.HttpRequest;
import ext.casc.constants.PDMConfig;
import ext.casc.distribute.contant.DistributeConstants;
import ext.casc.distribute.util.JsonVoConverter;
import ext.casc.distribute.vo.*;
import org.apache.commons.lang3.StringUtils;

public class OtherSystemIntegrationHelper {

    public static NcResponseDataVo getNcResponseDataVo(String changeNumber, String partId)
    {

        if(isTestingServer() )
        {
            return new NcResponseDataVo();
        }
        String requsetJsonText = "[{\"GGCode\":\"" + changeNumber + "\",\"Partid\":\"" + partId + "\"}]";
        String body = HttpRequest.post(DistributeConstants.URL_NC_SYSTEM).body(requsetJsonText, "application/json").execute().body();
        System.out.println("======= NC data body: " + body);
        NcResponseDataVo ncResponseDataVo = JsonVoConverter.deserialize(body, NcResponseDataVo.class);
        return ncResponseDataVo;
    }


    public static NcYzpDetailEntryVo getNcYzpDetailEntryVoByCode(NcResponseDataVo ncResponseDataVo, String code)
    {
        if(ncResponseDataVo==null || ncResponseDataVo.getYZPDetail()==null)
        {
            return new NcYzpDetailEntryVo();
        }

        for(NcYzpDetailEntryVo ncYzpDetailEntryVo: ncResponseDataVo.getYZPDetail())
        {
            if(code.equals(ncYzpDetailEntryVo.getVbatchcode()))
            {
                return ncYzpDetailEntryVo;
            }
        }
        return new NcYzpDetailEntryVo();
    }

    public static NcZZPZZDetailEntryVo getNcZZPZZDetailEntryVo(NcResponseDataVo ncResponseDataVo)
    {
        if(ncResponseDataVo==null || ncResponseDataVo.getZZPZZDetail()==null)
        {
            return null;
        }

        for(NcZZPZZDetailEntryVo ncZZPZZDetailEntryVo: ncResponseDataVo.getZZPZZDetail())
        {
            return ncZZPZZDetailEntryVo;
        }
        return null;
    }

    public static NcZZPWXDetailEntryVo getNcZZPWXDetailEntryVo(NcResponseDataVo ncResponseDataVo)
    {
        if(ncResponseDataVo==null || ncResponseDataVo.getZZPWXDetail()==null)
        {
            return null;
        }

        for(NcZZPWXDetailEntryVo ncZZPWXDetailEntryVo: ncResponseDataVo.getZZPWXDetail())
        {
            return ncZZPWXDetailEntryVo;
        }
        return null;
    }

    public static MesStatusResultVo getMesData(String changeNumber)
    {
        if(isTestingServer() )
        {
            return new MesStatusResultVo();
        }

        String requestText = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:tem=\"http://tempuri.org/\">"
                + "<soap:Header/>" + "<soap:Body>" + "<tem:ReceiceContainerStatus>"
                + "<tem:AnalysisId>" + changeNumber + "</tem:AnalysisId>"
                + "</tem:ReceiceContainerStatus>"
                + "</soap:Body>" + "</soap:Envelope>";
        String body = HttpRequest.post(DistributeConstants.URL_MES_SYSTEM).body(requestText, "text/xml").execute().body();
        System.out.println("=======mes data body: " + body);
        String jsonStr = StringUtils.substringBetween(body, "<ReceiceContainerStatusResult>", "</ReceiceContainerStatusResult>");
        System.out.println("=======mes data jsonStr: " + jsonStr);
        MesStatusResultVo mesStatusResultVo = JsonVoConverter.deserialize(jsonStr, MesStatusResultVo.class);
        if ("true".equals(mesStatusResultVo.getSTATE()) == false) {
            throw new RuntimeException("获取mes数据失败: " + changeNumber + "/" + body);
        }
        return mesStatusResultVo;

    }
    public static MesDataEntryVo getMesDataEntryVoByCode(MesStatusResultVo mesStatusResultVo, String code)
    {
        if(mesStatusResultVo==null || mesStatusResultVo.getDATA()==null)
        {
            return new MesDataEntryVo();
        }
        for(MesDataEntryVo mesDataEntryVo: mesStatusResultVo.getDATA())
        {
            if(code.equals(mesDataEntryVo.getCONTAINERNAME()))
            {
                return mesDataEntryVo;
            }
        }
        return new MesDataEntryVo();
    }

    private static boolean isTestingServer() {
        return !PDMConfig.isZS;
    }

}
