package ext.casc.integrate.nc;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.constants.PDMConfig;
import ext.casc.integrate.SynchConfig;
import ext.casc.integrate.bom.ERPBomHelper;
import ext.casc.integrate.bom.ERPCacheHelper;
import ext.casc.integrate.bom.GLErpElement;
import ext.casc.integrate.model.GLErpMaterialsBean;
import ext.casc.integrate.model.GLErpPbomPartBean;
import ext.casc.integrate.model.GLErpPeiTaoPartBean;
import ext.casc.integrate.service.ERPBomDataService;
import ext.casc.integrate.service.ERPMaterialDataService;
import ext.casc.integrate.util.BomUtil;
import ext.casc.integrate.util.CloudApiUtil;
import ext.casc.integrate.util.Constants;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.TimestampConverter;
import ext.casc.util.Tools;
import ext.casc.version.VersionCommonHelper;
import ext.sast.common.fc.CmPersistable;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.clients.beans.query.WT;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import java.util.*;

public class ErpSynchHelper {
    private static final int CACHE_FLAG = 1;
    public static  String reSend(Object pbo){
        if(pbo instanceof WTPart) {
            WTPart part = (WTPart) pbo;
            try {
                return importBomStructure(part);
            } catch (WTException e) {
                return "发送失败："+e.getLocalizedMessage();
            }

        }else if(pbo instanceof WTDocument || pbo instanceof WTChangeOrder2||pbo instanceof ProcessEnvelope) {
            return importProcessStructure(pbo);
        }
        return  "";
    }
    public static String importProcessStructure(Object pbo,String type) {
        String returnMsg = "";
        WTDocument doc = null;
        String pusher = "";
        String pusherZh = "";
        try {
            QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses((Persistable) pbo, null, null);
            if (qrProcs.hasMoreElements()) {
                WfProcess proc = (WfProcess) qrProcs.nextElement();
                pusher = proc.getCreator().getName();
                WTUser user = (WTUser) proc.getCreator().getObject();
                pusherZh =  user.getFullName().replaceAll(",","").replaceAll(" ","");
            }
            if (pbo instanceof WTDocument) {
                doc = (WTDocument) pbo;
                returnMsg = sendDoc(doc,pusher,type,pusherZh);
            } else if (pbo instanceof WTChangeOrder2) {
                WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
                String ecnType = IBAHelper.getIBAValue(changeOrder2,"ECNTYPE");
                if( "作废更改".equals(ecnType)){
                    return "";
                }
                QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
                while (qResult.hasMoreElements()) {
                    Object object = qResult.nextElement();
                    if (object instanceof WTDocument) {
                        doc = (WTDocument) object;
                        returnMsg = sendDoc(doc,pusher,type,pusherZh);

                    }
                }
            }else if (pbo instanceof ProcessEnvelope) {
                List list = ProcessEnvelopeUtil.getAllMemberLinks((ProcessEnvelope)pbo);
                for(Object tmpObject :list){
                    if(tmpObject instanceof WTDocument){
                        WTDocument tmpDoc = (WTDocument) tmpObject;
                        returnMsg = returnMsg + sendDoc(tmpDoc,pusher,type,pusherZh);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            returnMsg = "同步失败："+e.getLocalizedMessage();
        }

        return returnMsg;

    }

    public static String importProcessStructure(Object pbo) {
        return importProcessStructure(pbo,"Z");
    }

    public static String sendDoc(WTDocument doc,String pusher,String type,String pusherZh) throws Exception {
        String returnMsg = "";
        if (doc != null) {
            String pplantype = IBAHelper.getIBAValue(doc, "PPLANTYPE");
            String zfflag = IBAHelper.getIBAValue(doc, "ZFFLAG");

            if("ALL".equals(type)||zfflag.equals(type)){
                //全部发送

            }else{
                return returnMsg;
            }


            QueryResult qr = PartDocServiceCommand.getAssociatedDescParts(doc);
            if(qr.hasMoreElements()) {
                WTPart parentPart = (WTPart) qr.nextElement();
                parentPart = WTPartUtil.getLatestPartByNumberAndView(parentPart, "Manufacturing");
                JSONArray jsonArray = new JSONArray();
                IBAUtility parentUtility = new IBAUtility(parentPart);
                String parentPhase = parentUtility.getIBAValue("PHASE_CODE");//上级图号研制阶段
                String parentType = parentUtility.getIBAValue("MTYPE");
                String parentBATCH = parentUtility.getIBAValue("BATCH");//批次号
                String parentSETMARK = parentUtility.getIBAValue("SETMARK");
                String parentzzcj = parentUtility.getIBAValue("ZZCJ");

                JSONObject parentJsonObject = new JSONObject();
                parentJsonObject.put("parentNumber","");
                parentJsonObject.put("parentName","");
                parentJsonObject.put("parentVersion","");
                parentJsonObject.put("parentType","");
                parentJsonObject.put("parentPhase","");
                parentJsonObject.put("childNumber",parentPart.getNumber());
                parentJsonObject.put("childName",parentPart.getName());
                parentJsonObject.put("childVersion", parentPart.getIterationDisplayIdentifier().toString());
                parentJsonObject.put("childType",parentType);
                parentJsonObject.put("childPhase",parentPhase);
                parentJsonObject.put("usingAmount","1");
                parentJsonObject.put("unit","个");
                parentJsonObject.put("batch",parentBATCH);
                parentJsonObject.put("cldelb","");
                parentJsonObject.put("xlcc","");
                parentJsonObject.put("kzjs","");
                parentJsonObject.put("sjsl","");
                parentJsonObject.put("sjcc","");
                parentJsonObject.put("sjkzjs","");
                parentJsonObject.put("sl","1");
                parentJsonObject.put("unit2","个");
                parentJsonObject.put("comment","");
                parentJsonObject.put("index","");
                parentJsonObject.put("processFileNum","");
                parentJsonObject.put("processFileName","");
                parentJsonObject.put("processFileVersion","");
                parentJsonObject.put("processFileNumber","");
                parentJsonObject.put("dataFrom","pbom");
                parentJsonObject.put("pplanType",pplantype);
                parentJsonObject.put("zfflag","");
                parentJsonObject.put("parentFactory","");
                parentJsonObject.put("childFactory",parentzzcj);
                parentJsonObject.put("dept","");
                parentJsonObject.put("SETMARK",parentSETMARK);
                jsonArray.put(parentJsonObject);

                boolean needDataFromXml = true;
                String docOid = PersistenceCommonHelper.getOid(doc);
                List<GLErpPbomPartBean> erpMatchPartList ;
                List<GLErpPbomPartBean>  erpNewPartList ;

                List<GLErpMaterialsBean> materialsBeanList = null;
                List<GLErpElement> elementList = null;
                if("临时工艺文件".equals(pplantype)) {
                    List<GLErpPeiTaoPartBean>  erpPeiTaoPartBeans = null;
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
                        erpMatchPartList = ERPBomHelper.getPBOMMatchInfo(parentPart,elementList);

                        if(Constants.TYPE_ZIZHIJIAN.equals(parentType)||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(parentType)||Constants.TYPE_WAIPEITAOJIAN.equals(parentType)){
                            // 未缓存的材料定额
                            materialsBeanList =  ERPBomHelper.getCldeInfo(elementList);
                        }
                        // 未缓存的工艺定额
                        erpNewPartList = ERPBomHelper.getPBOMNewPartInfo(parentPart,elementList);
                        //未缓存的配套
                        erpPeiTaoPartBeans = ERPBomHelper.getPeiTaoPbomInfo(parentPart,elementList,"");

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
                    partAttris.put("parentNumber",parentPart.getNumber());
                    partAttris.put("parentName",parentPart.getName());
                    partAttris.put("parentVersion", VersionCommonHelper.getVersion(parentPart));
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
                }else if("正式工艺文件".equals(pplantype)) {
                    if(CACHE_FLAG==1) {//开启缓存
                        erpMatchPartList = new ArrayList<GLErpPbomPartBean>();
                        erpNewPartList = new ArrayList<GLErpPbomPartBean>();

                        materialsBeanList = ERPMaterialDataService.query(docOid);
                        List<GLErpPbomPartBean> erpPartList = ERPBomDataService.query(docOid);
                        for(GLErpPbomPartBean bean:erpPartList){
                            if(ERPBomDataService.MATCH.equals(bean.getIsMatch())){
                                erpMatchPartList.add(bean);
                            }else{
                                erpNewPartList.add(bean);
                            }
                        }

                        if (erpPartList.isEmpty() && materialsBeanList.isEmpty() ) {
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
                        erpMatchPartList = ERPBomHelper.getPBOMMatchInfo(parentPart,elementList);

                        if(Constants.TYPE_ZIZHIJIAN.equals(parentType)||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(parentType)||Constants.TYPE_WAIPEITAOJIAN.equals(parentType)){
                            // 未缓存的材料定额
                            materialsBeanList =  ERPBomHelper.getCldeInfo(elementList);
                        }
                        // 未缓存的工艺定额
                        erpNewPartList = ERPBomHelper.getPBOMNewPartInfo(parentPart,elementList);


                        if(CACHE_FLAG==1) {//开启缓存
                            //保存未缓存的工艺定额新增
                            Transaction trx = new Transaction();
                            try {
                                trx.start();
                                ERPCacheHelper.saveErpNewPartCache(erpNewPartList);
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
                    partAttris.put("parentNumber",parentPart.getNumber());
                    partAttris.put("parentName",parentPart.getName());
                    partAttris.put("parentVersion", VersionCommonHelper.getVersion(parentPart));
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
                    QueryResult list = WTPartHelper.service.getUsesWTPartMasters(parentPart);
                    int index = 1;
                    while(list.hasMoreElements()) {
                        WTPartUsageLink link = (WTPartUsageLink) list.nextElement();
                        WTPartMaster part1 = (WTPartMaster) link.getRoleBObject();
                        WTPart childPart = null;
                        if (Tools.isNull(parentBATCH)) {
                            childPart = BomUtil.getLatestPartByView(part1, "Manufacturing");
                        } else {
                            childPart = BomUtil.getLatestPartByBatchView(part1, parentBATCH, "Manufacturing");
                        }

                        if (childPart == null) {
                            continue;
                        }
                        IBAUtility ibaUtility = new IBAUtility(childPart);

                        String partType = ibaUtility.getIBAValue("MTYPE");
                        if (partType == null || "".equals(partType)) {
                            partType = ibaUtility.getIBAValue("CTYPE");
                        }

                        if (partType != null && !partType.equals("")) {
                            String SETMARK = ibaUtility.getIBAValue("SETMARK");
                            String BATCH = ibaUtility.getIBAValue("BATCH");
                            if (BATCH == null || "null".equals(BATCH)) {
                                BATCH = "";
                            }
                            String childzzcj = ibaUtility.getIBAValue("ZZCJ");
                            String unit = link.getQuantity().getUnit().getDisplay(Locale.CHINA);
                            if("每个".equals(unit)){
                                unit = "个";
                            }
                            String childVersion = childPart.getIterationDisplayIdentifier().toString();

                            if (Constants.TYPE_ZIZHIJIAN.equals(partType)
                                    || Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
                                    || Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)
                                    || Constants.TYPE_WAIXIEJIAN.equals(partType)
                                    || Constants.TYPE_WAIPEITAOJIAN.equals(partType)) {
                                String amount = link.getQuantity().getAmount() + "";
                                IBAUtility linkIBAUtility = new IBAUtility(link);
                                //取工艺数量值
                                String gysl = linkIBAUtility.getIBAValue("GYSL");
                                if (gysl != null && !"".equals(gysl)) {
                                    amount = gysl;
                                }
                                String childPhase = ibaUtility.getIBAValue("PHASE_CODE");
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("parentNumber",partAttris.get("parentNumber"));
                                jsonObject.put("parentName",partAttris.get("parentName"));
                                jsonObject.put("parentVersion",partAttris.get("parentVersion"));
                                jsonObject.put("parentType",partAttris.get("parentType"));
                                jsonObject.put("parentPhase",partAttris.get("parentPhase"));
                                jsonObject.put("childNumber",childPart.getNumber());
                                jsonObject.put("childName",childPart.getName());
                                jsonObject.put("childVersion",childVersion);
                                jsonObject.put("childType",partType);
                                jsonObject.put("childPhase",childPhase);
                                jsonObject.put("usingAmount",amount);
                                jsonObject.put("unit",unit);
                                jsonObject.put("batch",BATCH);
                                jsonObject.put("cldelb","");
                                jsonObject.put("xlcc","");
                                jsonObject.put("kzjs","");
                                jsonObject.put("sjsl","");
                                jsonObject.put("sjcc","");
                                jsonObject.put("sjkzjs","");
                                jsonObject.put("sl",amount);
                                jsonObject.put("unit2",unit);
                                jsonObject.put("comment","");
                                jsonObject.put("index",index+"");
                                jsonObject.put("processFileNum","");
                                jsonObject.put("processFileName","");
                                jsonObject.put("processFileVersion","");
                                jsonObject.put("processFileNumber","");
                                jsonObject.put("dataFrom","pbom");
                                jsonObject.put("pplanType",pplantype);
                                jsonObject.put("zfflag","");
                                jsonObject.put("parentFactory",parentzzcj);
                                jsonObject.put("childFactory",childzzcj);
                                jsonObject.put("dept","");
                                jsonObject.put("SETMARK",SETMARK);
                                jsonArray.put(jsonObject);
                                index++;
                            }
                        }
                    }

                }
                returnMsg =  sendErp(jsonArray,doc,pplantype,pusher,pusherZh);
                //return jsonArray.toString();
            }
        }
        return returnMsg;
    }
    private static String  sendErp(JSONArray jsonArray, WTDocument doc, String pplanType, String pusher,String pusherZh) {
        System.out.println("sendErp:"+jsonArray);
        String resultMsg = "";
        long eightHoursInMillis = 0;
        if(!PDMConfig.isZS){
            eightHoursInMillis = 8L * 60 * 60 * 1000;
        }
        JSONObject  params = new JSONObject();
        long importTime = System.currentTimeMillis()-eightHoursInMillis;
        params.put("pdmData",jsonArray);
        String instanceId = doc.getNumber() +"_"+ importTime;
        params.put("instanceId",instanceId);
        params.put("pplanType",pplanType);
        params.put("pusher",pusher);
        params.put("pusherZh",pusherZh);
        params.put("importTime",String.valueOf(importTime));
        String  responseBody = CloudApiUtil.httpOnlyBody(SynchConfig.NC_SEND_URL2,params);
        String synchState ="PDM发送成功";
        if(responseBody.contains("调用失败")){
            resultMsg = responseBody;
            synchState = "NC调用失败";
        }else{
            try {
                JSONObject resultJson = new JSONObject(responseBody);
                String success = resultJson.optString("result");
                if("true".equals(success)){
                    resultMsg = "";
                }else{
                    resultMsg = doc.getName()+"-NC处理失败，错误详情：" + resultJson.optString("msg");
                    synchState = "NC处理失败";
                }
            }catch (JSONException jsonException){
                jsonException.printStackTrace();
                resultMsg = "调用成功，返回Json无法解析："+jsonException.getLocalizedMessage();
                synchState = "NC处理失败";
            }
        }
        SynchDocRecord record = new SynchDocRecord();
        record.setKeyId(instanceId);
        record.setObjectNumber(doc.getNumber());
        record.setObjectVersion(VersionCommonHelper.getVersion(doc));
        record.setSynchTime(TimestampConverter.toChinaStandardTime(importTime));
        record.setImporter(doc.getModifierName());
        if(!"".equals(resultMsg)){
            record.setNote(resultMsg);
        }else{
            record.setNote("流程受控导入");
        }
        record.setInstanceId(instanceId);
        record.setSynchState(synchState);
        try {
            CmPersistenceHelper.manager.insert(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMsg;
    }

    public static void main(String[] args) {
        try {
            Object o = ReferenceFactory.getObjectbyOid("VR:wt.doc.WTDocument:5064523");
            System.out.println(ErpSynchHelper.importProcessStructure(o));
        } catch (WTException e) {
            throw new RuntimeException(e);
        }
    }

    public static String importBomStructure(WTPart part) throws WTException {
        String resultMsg = "";
        String batch = ext.casc.util.IBAHelper.getIBAStringValue(part,"BATCH");
        JSONObject  params = new JSONObject();
        long eightHoursInMillis = 0;
        if(!PDMConfig.isZS){
            eightHoursInMillis = 8L * 60 * 60 * 1000;
        }
        long importTime = System.currentTimeMillis()-eightHoursInMillis;
        String instanceId = part.getNumber() +"_"+ importTime;
        params.put("instanceId",instanceId);
        params.put("number",part.getNumber());
        params.put("batch",batch);
        params.put("pusher",SessionHelper.getPrincipal().getName());
        WTUser user = (WTUser) SessionHelper.getPrincipal();
        String pusherZh = user.getFullName();

        params.put("pusherZh",pusherZh.replaceAll(",","").replaceAll(" ",""));
        params.put("importTime",String.valueOf(importTime));
        String  responseBody = CloudApiUtil.httpOnlyBody(SynchConfig.NC_SEND_URL,params);
        String synchState ="PDM发送成功";
        if(responseBody.contains("调用失败")){
            resultMsg = responseBody;
            synchState = "NC调用失败";
        }else{
            try {
                JSONObject resultJson = new JSONObject(responseBody);
                String success = resultJson.optString("result");
                if("true".equals(success)){
                    resultMsg = "";
                }else{
                    resultMsg = "NC处理失败，错误详情：" + resultJson.optString("msg");
                    synchState = "NC处理失败";
                }
            }catch (JSONException jsonException){
                jsonException.printStackTrace();
                resultMsg = "调用成功，返回Json无法解析："+jsonException.getLocalizedMessage();
                synchState = "NC处理失败";
            }
        }
        SynchPartRecord record = new SynchPartRecord();
        record.setKeyId(instanceId);
        record.setObjectNumber(part.getNumber());
        record.setObjectVersion(VersionCommonHelper.getVersion(part));
        record.setViewName(part.getViewName());
        record.setImporter(SessionHelper.getPrincipal().getName());
        if(!"".equals(resultMsg)){
            record.setNote(resultMsg);
        }else{
            record.setNote("PBOM一键导入");
        }
        record.setInstanceId(instanceId);
        record.setSynchState(synchState);
        record.setSynchTime(TimestampConverter.toChinaStandardTime(importTime));
        try {
            CmPersistenceHelper.manager.insert(record);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resultMsg;

    }

    public static List<SynchPartRecord> queryPartRecord(String partNumber) throws Exception {
        List<SynchPartRecord> list = new ArrayList();
        CmQuerySpec qs = new CmQuerySpec(SynchPartRecord.class);
        qs.appendWhere(SynchPartRecord.OBJECTNUMBER,CmQuerySpec.EQUAL,partNumber);
        qs.appendOrderBy(SynchDocRecord.KEY_ID,true);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            list.add((SynchPartRecord) qr.next());
        }
        return list;
    }

    public static List<SynchDocRecord> queryDocRecord(String docNumber) throws Exception {
        List<SynchDocRecord> list = new ArrayList();
        CmQuerySpec qs = new CmQuerySpec(SynchDocRecord.class);
        qs.appendWhere(SynchDocRecord.OBJECTNUMBER,CmQuerySpec.EQUAL,docNumber);
        qs.appendOrderBy(SynchDocRecord.KEY_ID,true);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            list.add((SynchDocRecord) qr.next());
        }
        return list;
    }

    public static CmPersistable getRecord(String instanceId) throws Exception {
        CmQuerySpec qs = new CmQuerySpec(SynchDocRecord.class);
        qs.appendWhere(SynchDocRecord.INSTANCEID,CmQuerySpec.EQUAL,instanceId);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        if (qr.hasNext()) {
            return (SynchDocRecord) qr.next();
        }

        qs = new CmQuerySpec(SynchPartRecord.class);
        qs.appendWhere(SynchPartRecord.INSTANCEID,CmQuerySpec.EQUAL,instanceId);
        CmQueryResult qr2 = CmPersistenceHelper.manager.find(qs);
        if (qr2.hasNext()) {
            return (SynchPartRecord) qr2.next();
        }
        return null;
    }


}
