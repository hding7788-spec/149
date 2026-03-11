package ext.casc.webservice.command.gy;

import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import ext.casc.integrate.bom.ERPBomHelper;
import ext.casc.integrate.bom.ERPCacheHelper;
import ext.casc.integrate.bom.GLErpElement;
import ext.casc.integrate.model.GLErpMaterialsBean;
import ext.casc.integrate.model.GLErpPbomPartBean;
import ext.casc.integrate.model.GLErpPeiTaoPartBean;
import ext.casc.integrate.service.ERPBomDataService;
import ext.casc.integrate.service.ERPMaterialDataService;
import ext.casc.integrate.util.Constants;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
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
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class GetMaterialBomCommand implements WebServiceCommand, InitializingBean {
    private static final Logger LOGGER = LogR.getLogger(GetMaterialBomCommand.class.getName());
    private static final int CACHE_FLAG = 1;
    public static final String METHOD_NAME = "getMaterialBom";

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

        String docOid = jparams.optString("docOid");
        String partOid = jparams.optString("partOid");

        JSONArray jsonArray = new JSONArray();

        List<GLErpPbomPartBean> erpMatchPartList =null;
        List<GLErpPbomPartBean>  erpNewPartList =null ;
        List<GLErpPeiTaoPartBean>  erpPeiTaoPartBeans = null;
        List<GLErpMaterialsBean> materialsBeanList = null;
        List<GLErpElement> elementList = null;
        try {
            WTPart part = null;
            WTDocument doc = null;
            if(!Tools.isNull(docOid)){
                doc = (WTDocument) PersistenceCommonHelper.getPersistable(docOid);
            }else{
                String docNumber = jparams.optString("docNumber");
                String docVersion = jparams.optString("docVersion");
                if(docVersion.contains(".")){
                    String[] ss = docVersion.split("\\.");
                    docVersion = ss[0];
                }
                doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(WTDocument.class,docNumber,docVersion);
            }

            if(doc!=null){
                if(Tools.isNull(partOid)){
                    QueryResult qr = PartDocServiceCommand.getAssociatedDescParts(doc);
                    if(qr.hasMoreElements()) {
                        part = (WTPart) qr.nextElement();
                    }
                }else{
                    part = (WTPart) PersistenceCommonHelper.getPersistable(partOid);
                }
                docOid = PersistenceCommonHelper.getOid(doc);

                IBAUtility parentUtility = new IBAUtility(part);
                String parentPhase = parentUtility.getIBAValue("PHASE_CODE");//上级图号研制阶段
                String parentType = parentUtility.getIBAValue("MTYPE");
                String parentBATCH = parentUtility.getIBAValue("BATCH");//批次号
                String parentSETMARK = parentUtility.getIBAValue("SETMARK");
                String parentzzcj = parentUtility.getIBAValue("ZZCJ");

                boolean needDataFromXml = true;
                if(CACHE_FLAG==1) {//开启缓存
                    erpMatchPartList = new ArrayList<GLErpPbomPartBean>();
                    erpNewPartList = new ArrayList<GLErpPbomPartBean>();

                    materialsBeanList = ERPMaterialDataService.query(docOid);
                    //erpMatchPartList = ERPBomDataService.query(docOid, ERPBomDataService.MATCH);
                    List<GLErpPbomPartBean> erpPartList = ERPBomDataService.query(docOid);
                    for(GLErpPbomPartBean bean:erpPartList){
                        if(ERPBomDataService.MATCH.equals(bean.getIsMatch())){
                            erpMatchPartList.add(bean);
                        }else{
                            erpNewPartList.add(bean);
                        }
                    }

                    erpPeiTaoPartBeans = ERPBomDataService.queryPeiTao(docOid);
                    if (erpPartList.isEmpty() && materialsBeanList.isEmpty()  && erpPeiTaoPartBeans.isEmpty()) {
                        needDataFromXml = true;
                    }else{
                        needDataFromXml = false;
                    }

                }

                if(needDataFromXml) {

                    List<WTDocument> documentList = new ArrayList<WTDocument>();
                    documentList.add(doc);

                    elementList  = ERPBomHelper.getElementList(documentList);

                    //未缓存的工艺定额匹配
                    erpMatchPartList = ERPBomHelper.getPBOMMatchInfo(part,elementList);

                    if(Constants.TYPE_ZIZHIJIAN.equals(parentType)||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(parentType)||Constants.TYPE_WAIPEITAOJIAN.equals(parentType)){
                        // 未缓存的材料定额
                        materialsBeanList =  ERPBomHelper.getCldeInfo(elementList);
                    }
                    // 未缓存的工艺定额
                    erpNewPartList = ERPBomHelper.getPBOMNewPartInfo(part,elementList);
                    //未缓存的配套
                    erpPeiTaoPartBeans = ERPBomHelper.getPeiTaoPbomInfo(part,elementList,"");

                    if(CACHE_FLAG==1) {//开启缓存
                        //保存未缓存的工艺定额新增
                        Transaction trx = new Transaction();
                        try {
                            trx.start();
                            ERPCacheHelper.saveErpNewPartCache(erpNewPartList);
                            ERPCacheHelper.savePeiTaoPbomCache(erpPeiTaoPartBeans);
                            //保存未缓存的材料定额
                            ERPCacheHelper.saveMaterialCache(materialsBeanList);
                            ERPCacheHelper.saveErpMatchPartCache(erpMatchPartList);

                            trx.commit();
                            trx = null;
                        }catch (Exception exception){
                            exception.printStackTrace();
                        } finally {
                            if(trx!=null)  trx.rollback();
                        }
                    }

                }

                HashMap<String,String> partAttris = new HashMap<String,String>();
                partAttris.put("parentNumber",part.getNumber());
                partAttris.put("parentName",part.getName());
                partAttris.put("parentVersion",VersionCommonHelper.getVersion(part));
                partAttris.put("parentType",parentType);
                partAttris.put("parentPhase",parentPhase);
                partAttris.put("parentFactory",parentzzcj);
                partAttris.put("SETMARK",parentSETMARK);
                partAttris.put("parentBATCH",parentBATCH);

                if(!materialsBeanList.isEmpty()){
                    ERPBomHelper.getYclJson(partAttris,materialsBeanList,jsonArray);
                }
                if(!erpNewPartList.isEmpty()){
                    ERPBomHelper.getNewPartJson(partAttris,erpNewPartList,jsonArray);

                }
                if(!erpMatchPartList.isEmpty()){
                    ERPBomHelper.getMatchPartJson(partAttris,erpMatchPartList,jsonArray);

                }
                if(!erpPeiTaoPartBeans.isEmpty()){
                    ERPBomHelper.getPeiTaoPbomJson(partAttris,erpPeiTaoPartBeans,jsonArray);
                }

            }else{
                rtnCode = "N";
                rtnMsg = "获取不到工艺！";
            }


        } catch (Exception e) {
            rtnMsg = "获取工艺出错"+e.getLocalizedMessage();
            rtnCode = "N";
            e.printStackTrace();
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

    private void genDocJson(JSONArray jsonArray, WTDocument doc) throws WTException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oid", PersistenceCommonHelper.getOid(doc));
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
