package ext.casc.webservice.command.gy;

import com.ptc.extend.ixb.CmExpImpSearchHelper;
import ext.casc.persistence.PersistenceCommonHelper;
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
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.log4j.LogR;

import java.net.URL;

@Component
public class GetProcessPlanInfoCommand implements WebServiceCommand, InitializingBean {
    private static final Logger LOGGER = LogR.getLogger(GetProcessPlanInfoCommand.class.getName());

    public static final String METHOD_NAME = "getProcessPlanInfo";

    @Override
    public String execute(String params) {
        String rtnCode ="S";
        String rtnMsg ="";
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
            String processFileNum = jparam.optString("processFileNum");
            String processFileVersion = jparam.optString("processFileVersion");
            if(Tools.isNull(processFileNum)){
                rtnCode = "N";
                rtnMsg = rtnMsg +"processFileNum必填；";
                continue;
            }
            if(Tools.isNull(processFileVersion)){
                rtnCode = "N";
                rtnMsg = rtnMsg +processFileNum+"的processFileVersion必填；";
                continue;
            }

            try {
                JSONObject processPlanInfo = new JSONObject();
                String version = processFileVersion;
                if(processFileVersion.contains(".")){
                    String[] ss = processFileVersion.split("\\.");
                    version=ss[0];
                }
                WTDocument doc =  (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(WTDocument.class,processFileNum,version);
                if(doc!=null){
                    processPlanInfo.put("oid", PersistenceCommonHelper.getOid(doc));
                    processPlanInfo.put("number", doc.getNumber());
                    processPlanInfo.put("name", doc.getName());
                    processPlanInfo.put("version", VersionCommonHelper.getVersion(doc));
                    QueryResult qr = wt.content.ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
                    ApplicationData appData = null;
                    if (qr.hasMoreElements()) {
                        appData = (ApplicationData) qr.nextElement();
                        URL localUrl = wt.content.ContentHelper.service.getDownloadURL(doc,appData);
                        processPlanInfo.put("downloadUrl", localUrl.toString());
                    }
                    jsonArray.put(processPlanInfo);

                    rtnMsg =rtnMsg +processFileNum +"获取成功；";
                }else{
                    rtnCode = "N";
                    rtnMsg = rtnMsg +"编号："+processFileNum+"的工艺文件未找到；";
                }

            } catch (Exception e) {
                rtnMsg = "查询出错"+e.getLocalizedMessage();
                rtnCode = "N";
                e.printStackTrace();
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
