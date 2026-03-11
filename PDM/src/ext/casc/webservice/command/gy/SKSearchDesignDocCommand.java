package ext.casc.webservice.command.gy;

import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.common.util.CommonValuesUtil;
import ext.casc.integrate.senKe.SenKeSynchHelper;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.query.CommonQueryHelper;
import ext.casc.util.Tools;
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
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.net.URL;

@Component
public class SKSearchDesignDocCommand implements WebServiceCommand, InitializingBean {
    private static final Logger LOGGER = LogR.getLogger(SKSearchDesignDocCommand.class.getName());

    public static final String METHOD_NAME = "searchDesignDoc";

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

        String number = jparams.optString("number");
        String name = jparams.optString("name");
        JSONArray jsonArray = new JSONArray();
        if(!Tools.isNull(number) ){
            try {
                WTDocument doc =  CommonQueryHelper.queryDocByNumberAndName(number+".DWG",name);
                if(doc!=null) {
                    JSONObject docJson = new JSONObject();
                    docJson.put("oid", PersistenceCommonHelper.getOid(doc));
                    docJson.put("number", doc.getNumber());
                    docJson.put("name", doc.getName());
                    docJson.put("version", VersionCommonHelper.getVersion(doc));
                    docJson.put("dataUrl", SenKeSynchHelper.getDataUrl(doc));
                    docJson.put("printDataUrl", SenKeSynchHelper.getDataUrl2(doc, true));
                    jsonArray.put(docJson);
                }

                if(number.toUpperCase().endsWith("PRT")||number.toUpperCase().endsWith("ASM")){
                    number = number.substring(0,number.length()-4)+".DRW";
                }
                EPMDocument epm =  CommonQueryHelper.queryCadByNumberAndName(number,null);
               if(epm!=null){
                   JSONObject docJson = new JSONObject();
                   docJson.put("oid", PersistenceCommonHelper.getOid(epm));
                   docJson.put("number", epm.getNumber());
                   docJson.put("name", epm.getName());
                   docJson.put("cadName", epm.getCADName());
                   docJson.put("version", VersionCommonHelper.getVersion(epm));
                   docJson.put("dataUrl", SenKeSynchHelper.getDataUrl(epm));
                   docJson.put("printDataUrl", SenKeSynchHelper.getDataUrl2(epm,true));
                   jsonArray.put(docJson);
               }

            } catch (Exception e) {
                rtnMsg = "查询设计文件出错"+e.getLocalizedMessage();
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
        jsonArray.put(jsonObject);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }
}
