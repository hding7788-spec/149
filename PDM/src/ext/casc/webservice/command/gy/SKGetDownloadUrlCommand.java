package ext.casc.webservice.command.gy;

import com.glaway.mpm.util.ReferenceFactory;
import com.ptc.wvs.common.ui.Representer;
import com.ptc.wvs.common.ui.VisualizationHelper;
import ext.casc.common.util.CommonValuesUtil;
import ext.casc.integrate.senKe.SenKeSynchHelper;
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
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.representation.Representation;

import java.net.URL;

@Component
public class SKGetDownloadUrlCommand implements WebServiceCommand, InitializingBean {
    private static final Logger LOGGER = LogR.getLogger(SKGetDownloadUrlCommand.class.getName());

    public static final String METHOD_NAME = "getDownloadUrl";

    @Override
    public String execute(String params) {
        String rtnCode ="S";
        String rtnMsg ="获取成功";
        JSONArray jparams = null;
        try {
            jparams = new JSONArray(params);
        } catch (JSONException e) {
            rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
        }
        JSONObject jrtnObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < jparams.length(); i++){
            JSONObject jparam = jparams.getJSONObject(i);
            String oid = jparam.optString("oid");
            String role = jparam.optString("role");
            if(Tools.isNull(role)){
                rtnCode = "N";
                rtnMsg = rtnMsg +oid+"role必填：PVZ或PRIMARY";
                continue;
            }
            if(!Tools.isNull(oid) ){
                try {
                    JSONObject downloadJson = new JSONObject();
                    downloadJson.put("oid", oid);
                    JSONObject dataUrl = new JSONObject();
                    downloadJson.put("dataUrl", dataUrl);
                    if("PVZ".equals(role)){
                        EPMDocument contentHolder = (EPMDocument)ReferenceFactory.getObjectbyOid(oid);
                        downloadJson.put("dataUrl", SenKeSynchHelper.getPvsZipUrl(contentHolder));
                    }else{
                        ContentHolder contentHolder = (ContentHolder)ReferenceFactory.getObjectbyOid(oid);
                        downloadJson.put("dataUrl", SenKeSynchHelper.getDataUrl(contentHolder));
                        downloadJson.put("printDataUrl", SenKeSynchHelper.getDataUrl2(contentHolder));
                    }
                    jsonArray.put(downloadJson);
                } catch (Exception e) {
                    rtnMsg = "查询出错"+e.getLocalizedMessage();
                    rtnCode = "N";
                    e.printStackTrace();
                }
            }else{
                rtnCode = "N";
                rtnMsg = rtnMsg +oid+"不允许为空；";
            }
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

    public static String getRefFromObject(Persistable persistable) {
        try {
            wt.fc.ReferenceFactory referencefactory = new wt.fc.ReferenceFactory();
            return referencefactory.getReferenceString(ObjectReference.newObjectReference(persistable.getPersistInfo()
                    .getObjectIdentifier()));
        } catch (Exception exception) {
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }
}
