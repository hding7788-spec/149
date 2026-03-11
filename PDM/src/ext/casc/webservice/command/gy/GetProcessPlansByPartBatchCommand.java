package ext.casc.webservice.command.gy;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.WTPartUtil;
import ext.casc.common.PartCommonHelper;
import ext.casc.integrate.util.BomUtil;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.util.WCUtil;
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
import wt.part.WTPartMaster;
import wt.util.WTException;

import java.util.List;

@Component
public class GetProcessPlansByPartBatchCommand implements WebServiceCommand, InitializingBean {
    private static final Logger LOGGER = LogR.getLogger(GetProcessPlansByPartBatchCommand.class.getName());

    public static final String METHOD_NAME = "getProcessPlansByPartBatch";

    @Override
    public String execute(String params) {
        String rtnCode ="S";
        String rtnMsg ="获取成功";
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
        String pplanType = jparams.optString("pplanType");
        if(Tools.isNull(view)){
            view ="Manufacturing";
        }
        JSONArray docJsonArray = new JSONArray();
        JSONObject partJson = new JSONObject();

        if(!Tools.isNull(partNumber) ){
            try {
                WTPart part = null;
                if(Tools.isNull(batch)){
                    part =  WTPartUtil.getLatestPartByNumberAndView(partNumber, view);
                }else{
                    WTPart tmpPart = WCUtil.getPartByNumber(partNumber);
                    part = BomUtil.getLatestPartByBatchView( (WTPartMaster)tmpPart.getMaster(), batch,view);
                }
                if(part!=null){
                    partJson= GetBomCommand.getPartJson(part);
                    List<WTDocument> docList = PartCommonHelper.getDescribedByWTDocuments(part);
                    ext.casc.util.IBAHelper helper = new ext.casc.util.IBAHelper();
                    for(WTDocument doc:docList){
                        String state = doc.getState().getState().toString();
                        if("OBSOLESCENCE".equals(state)){
                            break;
                        }
                        String CLDEZT = helper.getIBAStringValue(doc, "CLDEZT");
                        if(state.equals("APPROVED")||"已批准".equals(CLDEZT)){
                            String  PPLANTYPE = helper.getIBAStringValue(doc, "PPLANTYPE");
                            PPLANTYPE = StrUtil.isEmpty(PPLANTYPE) ? "" : PPLANTYPE;
                            if("ALL".equals(pplanType)||PPLANTYPE.equals(pplanType)){
                                genDocJson(docJsonArray, doc);
                            }
                        }
                    }

                    partJson.put("children",docJsonArray);
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

    private void genDocJson(JSONArray jsonArray, WTDocument doc) throws WTException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("docOid", PersistenceCommonHelper.getOid(doc));
        jsonObject.put("processPlanNumber",doc.getNumber());
        jsonObject.put("processPlanName",doc.getName());
        jsonObject.put("pplanType", IBAHelper.getIBAStringValue(doc,"PPLANTYPE"));
        jsonObject.put("zfflag",IBAHelper.getIBAStringValue(doc,"ZFFLAG"));
        jsonObject.put("batch",IBAHelper.getIBAStringValue(doc,"BATCH"));
        jsonObject.put("ppnumber",IBAHelper.getIBAStringValue(doc,"PPNUMBER"));
        jsonObject.put("phaseCode",IBAHelper.getIBAStringValue(doc,"PHASE_CODE"));
        jsonArray.put(jsonObject);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }
}
