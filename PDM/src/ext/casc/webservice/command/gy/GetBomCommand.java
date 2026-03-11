package ext.casc.webservice.command.gy;

import com.glaway.mpm.util.WTPartUtil;
import ext.casc.integrate.util.BomUtil;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.Tools;
import ext.casc.util.WCUtil;
import ext.casc.version.VersionCommonHelper;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;

import java.util.Locale;

@Component
public class GetBomCommand implements WebServiceCommand, InitializingBean {
    private static final Logger LOGGER = LogR.getLogger(GetBomCommand.class.getName());

    public static final String METHOD_NAME = "getBom";

    @Override
    public String execute(String params) {
        String rtnCode ="S";
        String rtnMsg ="";
        JSONObject jparams = null;
        try {
            jparams = new JSONObject(params);
        } catch (JSONException e) {
            rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
        }
        JSONObject jrtnObj = new JSONObject();

        String partNumber = jparams.optString("partNumber");
        String view = jparams.optString("view");
        String batch = jparams.optString("batch");
        if(Tools.isNull(view)){
            view ="Manufacturing";
        }
        JSONObject partJson = new JSONObject();
        if(!Tools.isNull(partNumber) ){
            try {
                WTPart parentPart = null;
                if(Tools.isNull(batch)){
                    parentPart =  WTPartUtil.getLatestPartByNumberAndView(partNumber, view);
                }else{
                    WTPart tmpPart = WCUtil.getPartByNumber(partNumber);
                    parentPart = BomUtil.getLatestPartByBatchView( (WTPartMaster)tmpPart.getMaster(), batch,view);
                }
                if(parentPart!=null){
                    partJson= getPartJson(parentPart);
                    JSONArray childPartJsonArray = new JSONArray();
                    QueryResult queryResult = WTPartHelper.service.getUsesWTPartMasters(parentPart);
                    while(queryResult.hasMoreElements()) {
                        WTPartUsageLink link = (WTPartUsageLink) queryResult.nextElement();
                        WTPartMaster partMaster = (WTPartMaster) link.getRoleBObject();
                        WTPart latePart = null;
                        if(Tools.isNull(batch)){
                            latePart =  WTPartUtil.getLatestPartByNumberAndView(partMaster.getNumber(), view);
                        }else{
                            latePart = BomUtil.getLatestPartByBatchView( partMaster, batch,view);
                        }
                        if(latePart!=null){
                            JSONObject childPartJson= getPartJson(latePart);

                            String unit = link.getQuantity().getUnit().getDisplay(Locale.CHINA);
                            String amount = link.getQuantity().getAmount()+"";
                            IBAUtility linkIBAUtility = new IBAUtility(link);
                            String gysl = linkIBAUtility.getIBAValue("GYSL");
                            if(gysl != null && !"".equals(gysl)) {
                                amount = gysl;
                            }
                            childPartJson.put("usingAmount",amount);
                            childPartJson.put("unit",unit);

                            childPartJsonArray.put(childPartJson);
                        }

                    }

                    partJson.put("children",childPartJsonArray);

                }else{
                    rtnMsg = "找不到对应的零件:"+partNumber;
                }
            } catch (Exception e) {
                rtnMsg = "查询零件出错"+e.getLocalizedMessage();
                rtnCode = "N";
                e.printStackTrace();

            }
        }else{
            rtnCode = "N";
            rtnMsg = "编号不能都为空";
        }
        try {
            jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
            jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
            jrtnObj.put(WSConstants.DATA, partJson);

        } catch (JSONException ex) {
            LOGGER.error("Error building JSON: ", ex);
        }

        return jrtnObj.toString();
    }



    public static JSONObject getPartJson(WTPart part) throws WTException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("partOid", PersistenceCommonHelper.getOid(part));
        jsonObject.put("partNumber",part.getNumber());
        jsonObject.put("partName",part.getName());
        jsonObject.put("version", VersionCommonHelper.getVersion(part));
        jsonObject.put("viewName", part.getViewName());
        jsonObject.put("containerName", part.getContainerName());
        IBAUtility ibaUtility = new IBAUtility(part);
        jsonObject.put("PHASE_CODE",  ibaUtility.getIBAValue("PHASE_CODE"));
        jsonObject.put("SETMARK",  ibaUtility.getIBAValue("SETMARK"));
        jsonObject.put("BATCH",  ibaUtility.getIBAValue("BATCH"));
        jsonObject.put("ZZCJ",  ibaUtility.getIBAValue("ZZCJ"));
        jsonObject.put("MTYPE",  ibaUtility.getIBAValue("MTYPE"));
        return  jsonObject;


    }


    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }
}
