package ext.casc.webservice.command.gy;

import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import ext.casc.common.util.CommonValuesUtil;
import ext.casc.common.PartCommonHelper;
import ext.casc.util.Tools;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.doc.WTDocument;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.util.List;
@Component
public class PDMSearchProcessPlans implements WebServiceCommand, InitializingBean {
    private static final Logger LOGGER = LogR.getLogger(PDMSearchProcessPlans.class.getName());

    public static final String METHOD_NAME = "searchProcessPlans";

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
        String processPlanType = jparams.optString("processPlanType");
        JSONArray jsonArray = new JSONArray();
        if(!Tools.isNull(partNumber) ){
            try {
                WTPart part =  WTPartUtil.getLatestPartByNumberAndView(partNumber, "Manufacturing");
                if(part!=null){
                    List<WTDocument> docList = PartCommonHelper.getDescribedByWTDocuments(part);
                    for(WTDocument doc:docList){
                        String state = doc.getState().getState().toString();
                        if(!"APPROVED".equals(state)){
                            continue;
                        }
                        String typeName = TypedUtility.getTypeIdentifier(doc).getTypename();
                        if (typeName.contains("PROCESS_PLAN")&&!typeName.contains("reportTechnics")) {
                            if(Tools.isNull(processPlanType)){
                                genDocJson(jsonArray, doc);
                            }else{
                                String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
                                if(docType!=null && docType.contains(processPlanType)){
                                    genDocJson(jsonArray, doc);
                                }
                            }
                        }
                    }
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
            jrtnObj.put(WSConstants.DATA, jsonArray);

        } catch (JSONException ex) {
            LOGGER.error("Error building JSON: ", ex);
        }

        return jrtnObj.toString();
    }

    private void genDocJson(JSONArray jsonArray, WTDocument doc) throws WTException, PropertyVetoException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("processPlanNumber",doc.getNumber());
        jsonObject.put("processPlanName",doc.getName());
        String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc,  CommonValuesUtil.IXBEXPIMP_FOLDER);
        jsonObject.put("fileUrl", CommonValuesUtil.HOST_URL +zipFileName);
        jsonObject.put("version", doc.getIterationDisplayIdentifier().toString());
        jsonArray.put(jsonObject);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }
}
