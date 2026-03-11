/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.IBAHelper;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.bean.GWDealProductRecord;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.analysisActivity.log.AnalysisSyncLogger;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTaskItem;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.change2.WTAnalysisActivity;
import wt.fc.*;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.*;
import wt.session.SessionServerHelper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 类功能：接收制品处理结果接口
 * NC/MES返回制品处理结果
 * <p>
 * 影响分析编号、图号、已制品数量、在制品数量
 *
 * @author chenjianhui
 * @date 2024/04/01
 */
@Component
public class ExtGetProductDealResultCommand implements WebServiceCommand, InitializingBean {
    // 方法标识
    public static final String METHOD_NAME = "getProductDealResult";

    private static String PARA_ANALYSISNUMBER = "analysisNumber";
    private static String PARA_PARTID = "partId";
    private static String PARA_SOURCE = "source";
    private static String PARA_DATA = "data";
    private static String PARA_DEALTYPE = "dealType";
    private static String PARA_COUNT = "count";
    private static String PARA_CODE = "code";
    private static String PARA_CARD = "card";
    private static String PARA_DEALSTATUS = "dealStatus";
    private static String PARA_RESPONSIBLEUNIT = "responsibleUnit";
    private static String PARA_RESPONSER = "responser";
    private static String PARA_FINISHTIME = "finishTime";
    private static String PARA_REPAIRCOUNT = "repairCount";

    @Override
    public String execute(String params) {
        AnalysisSyncLogger logger = AnalysisSyncLogger.getInstance();
        JSONObject rtnMsgObj = new JSONObject();
        String errorMsg = "";
        JSONObject jparams = null;
        try {
            jparams = new JSONObject(params);
            logger.log("getProductDealResult jparams=" + jparams);
        } catch(JSONException e) {
            errorMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
            rtnMsgObj.put("status", "N");
            rtnMsgObj.put("result", errorMsg);
            logger.log(rtnMsgObj.toString());
            return rtnMsgObj.toString();
        }

        Transaction tx = new Transaction();
        try {
            if(StrUtil.isEmpty(errorMsg)) {
                tx.start();
                String analysisNumber = jparams.optString(PARA_ANALYSISNUMBER);
                String partId = jparams.optString(PARA_PARTID);
                String source = jparams.optString(PARA_SOURCE);
                //判断是否是整件外协
                boolean isZjwx = false;
                if(AnalysisConstant.SOURCE_ZAIZHIPINZJWX.equals(source)){
                    isZjwx = true;
                    source = AnalysisConstant.SOURCE_ZAIZHIPIN;
                }
                String type = AnalysisConstant.TYPE_ZAIZHIPIN;
                if(AnalysisConstant.SOURCE_YIZHIPIN.equals(source)) {
                    type = AnalysisConstant.TYPE_YIZHIPIN;
                }

                if(StrUtil.isNotEmpty(analysisNumber) && StrUtil.isNotEmpty(partId)) {
                    WTAnalysisActivity activity = AnalysisUtil.getWTAnalysisActivityByNumber(analysisNumber);
                    if(activity == null) {
                        errorMsg = "未查询到编号为" + analysisNumber + "的更改影响分析。";
                        rtnMsgObj.put("status", "N");
                        rtnMsgObj.put("result", errorMsg);
                        logger.log(rtnMsgObj.toString());
                        return rtnMsgObj.toString();
                    }
                } else {
                    errorMsg = "影响分析编号、部件id不允许为空！";
                    rtnMsgObj.put("status", "N");
                    rtnMsgObj.put("result", errorMsg);
                    logger.log(rtnMsgObj.toString());
                    return rtnMsgObj.toString();
                }
                AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, type);
                if(entry == null) {
                    errorMsg = "未查询到当前影响分析单部件ID为" + partId + "的制品条目";
                    rtnMsgObj.put("status", "N");
                    rtnMsgObj.put("result", errorMsg);
                    logger.log(rtnMsgObj.toString());
                    return rtnMsgObj.toString();
                }

                JSONArray data = jparams.optJSONArray(PARA_DATA);
                if(data != null && data.length() > 0) {
                    for(int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        String card = object.optString(PARA_CARD);
                        String repairCount = jparams.optString(PARA_REPAIRCOUNT);
                        String count = jparams.optString(PARA_COUNT);
                        if(StrUtil.isEmpty(card)) {
                            if(AnalysisConstant.SOURCE_YIZHIPIN.equals(source)) {
                                errorMsg = "库存批次号不允许为空！";
                            } else {
                                errorMsg = "路卡号/离散订单号不允许为空！";
                            }
                            rtnMsgObj.put("status", "N");
                            rtnMsgObj.put("result", errorMsg);
                            logger.log(rtnMsgObj.toString());
                            return rtnMsgObj.toString();
                        }
                        if(StrUtil.isNotEmpty(repairCount)) {
                            try {
                                Integer.valueOf(repairCount);
                            } catch(Exception e) {
                                errorMsg = "实际返修数量必须为整数！";
                                rtnMsgObj.put("status", "N");
                                rtnMsgObj.put("result", errorMsg);
                                logger.log(rtnMsgObj.toString());
                                return rtnMsgObj.toString();
                            }
                        }
                        if(StrUtil.isNotEmpty(count)) {
                            try {
                                Integer.valueOf(count);
                            } catch(Exception e) {
                                errorMsg = "制品数量必须为整数！";
                                rtnMsgObj.put("status", "N");
                                rtnMsgObj.put("result", errorMsg);
                                logger.log(rtnMsgObj.toString());
                                return rtnMsgObj.toString();
                            }
                        }
                    }
                }

                if(StrUtil.isEmpty(errorMsg)) {
                    List<GWDealProductRecord> add = new ArrayList<GWDealProductRecord>();
                    List<GWDealProductRecord> update = new ArrayList<GWDealProductRecord>();
                    if(data != null && data.length() > 0) {
                        for(int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            logger.log(object);
                            String count = object.optString(PARA_COUNT);
                            String dealType = object.optString(PARA_DEALTYPE);
                            String code = object.optString(PARA_CODE);
                            String card = object.optString(PARA_CARD);
                            String dealStatus = object.optString(PARA_DEALSTATUS);
                            String responser = object.optString(PARA_RESPONSER);
                            String responsibleUnit = object.optString(PARA_RESPONSIBLEUNIT);
                            String finishTime = object.optString(PARA_FINISHTIME);
                            Integer repairCount = 0;
                            if(StrUtil.isNotEmpty(object.optString(PARA_REPAIRCOUNT))) {
                                repairCount = Integer.valueOf(object.optString(PARA_REPAIRCOUNT));
                            }

                            GWDealProductRecord history = queryDealRecordData(partId, analysisNumber, source, card);
                            if(StrUtil.isNotEmpty(count) && StrUtil.isNotEmpty(dealType)) {
                                //NC 整件外协 数量和处理结果一起传过来
                                // 重复传
                                if(history != null) {
                                    history.setAnalysisNumber(analysisNumber);
                                    history.setSource(source);
                                    history.setVerOid(partId);
                                    history.setDealType(dealType);
                                    history.setCount(Integer.valueOf(count));
                                    history.setRepairCount(repairCount);
                                    history.setCode(code);
                                    history.setCard(card);
                                    if(StrUtil.isNotEmpty(dealStatus)) {
                                        history.setStatus(dealStatus);
                                    } else {
                                        history.setStatus(AnalysisConstant.DEALRECORD_STATUS_OK);
                                    }
                                    history.setResponser(responser);
                                    history.setResponsibleUnit(responsibleUnit);
                                    history.setFinishTime(finishTime);
                                    update.add(history);
                                } else {
                                    GWDealProductRecord record = new GWDealProductRecord();
                                    if(isZjwx){
                                        record.setKeyId(UUID.randomUUID() + "_ZJWX");
                                    }else {
                                        record.setKeyId(UUID.randomUUID() + "_MES");
                                        entry.setReceiveStatus(AnalysisConstant.DEAL_STATUS_RECEIVECOUNT);
                                        CmPersistenceHelper.manager.update(entry);
                                    }
                                    record.setAnalysisNumber(analysisNumber);
                                    record.setSource(source);
                                    record.setVerOid(partId);
                                    record.setDealType(dealType);
                                    record.setCount(Integer.valueOf(count));
                                    record.setRepairCount(repairCount);
                                    record.setCode(code);
                                    record.setCard(card);
                                    if(StrUtil.isNotEmpty(dealStatus)) {
                                        record.setStatus(dealStatus);
                                    } else {
                                        record.setStatus(AnalysisConstant.DEALRECORD_STATUS_OK);
                                    }
                                    record.setResponser(responser);
                                    record.setResponsibleUnit(responsibleUnit);
                                    record.setFinishTime(finishTime);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
                                    String date = sdf.format(new Date());
                                    Timestamp timestamp = Timestamp.valueOf(date);
                                    record.setCreateTimeStamp(timestamp);
                                    record.setUpdateTimeStamp(timestamp);
                                    add.add(record);
                                }
                            } else {
                                if(history != null) {
                                    history.setCode(code);
                                    history.setCard(card);
                                    if(StrUtil.isNotEmpty(dealStatus)) {
                                        history.setStatus(dealStatus);
                                    } else {
                                        history.setStatus(AnalysisConstant.DEALRECORD_STATUS_OK);
                                    }
                                    history.setResponser(responser);
                                    history.setResponsibleUnit(responsibleUnit);
                                    history.setFinishTime(finishTime);
                                    history.setRepairCount(repairCount);
                                    update.add(history);
                                } else {
                                    errorMsg += "未查询到当前制品处理记录，完成失败！";
                                }
                            }
                        }
                        if(StrUtil.isEmpty(errorMsg)) {
                            for(GWDealProductRecord record : add) {
                                CmPersistenceHelper.manager.save(record);
                            }
                            for(GWDealProductRecord record : update) {
                                CmPersistenceHelper.manager.update(record);
                            }

                            //重新计算制品数量
                            if(AnalysisConstant.SOURCE_ZAIZHIPIN.equals(source)) {
                                Integer zCount = ExtGetProductDealResultCommand.queryDealProductCount(partId, analysisNumber, AnalysisConstant.SOURCE_ZAIZHIPIN, 1);
                                Integer zrepairCount = ExtGetProductDealResultCommand.queryDealProductCount(partId, analysisNumber, AnalysisConstant.SOURCE_ZAIZHIPIN, 2);
                                entry.setCount(zCount);
                                entry.setRepaircount(zrepairCount);
                            } else {
                                Integer yCount = ExtGetProductDealResultCommand.queryDealProductCount(partId, analysisNumber, AnalysisConstant.SOURCE_YIZHIPIN, 1);
                                Integer yrepairCount = ExtGetProductDealResultCommand.queryDealProductCount(partId, analysisNumber, AnalysisConstant.SOURCE_YIZHIPIN, 2);
                                entry.setCount(yCount);
                                entry.setRepaircount(yrepairCount);
                            }
                            //查询制品处理情况，更新制品处理状态
                            boolean deal = AnalysisUtil.checkProductDealResult(entry, true);
                            if(deal) {
                                entry.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                            }
                            CmPersistenceHelper.manager.update(entry);
                        }
                    }
                }
            }
            tx.commit();
            tx = null;
        } catch(Exception e) {
            e.printStackTrace();
            errorMsg += e.getMessage();
        } finally {
            if(tx != null) {
                tx.rollback();
            }
        }
        try {
            if(errorMsg != null && !"".equals(errorMsg)) {
                rtnMsgObj.put("status", "N");
                rtnMsgObj.put("result", errorMsg);
            } else {
                rtnMsgObj.put("status", "Y");
                rtnMsgObj.put("result", "调用成功");
            }
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            logger.log("getProductDealResult返回结果：" + rtnMsgObj);
        }
        return rtnMsgObj.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }

    /**
     * 方法功能:查询制品处理记录
     *
     * @author cjh
     * @date 2024/6/26
     */
    public static GWDealProductRecord queryDealRecordData(String oid, String number, String type, String cardCode) throws Exception {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, oid);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, number);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, type);
            if(StrUtil.isNotEmpty(cardCode)) {
                qs.appendAnd();
                qs.appendWhere(GWDealProductRecord.CARD, CmQuerySpec.EQUAL, cardCode);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            if(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                return record;
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return null;
    }


    /**
     * 方法功能:查询制品处理总数
     *
     * @author cjh
     * @date 2024/6/26
     */
    public static Integer queryDealProductCount(String oid, String number, String type, int i) throws Exception {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        int count = 0;
        try {
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, oid);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, number);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, type);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                if(record.getKeyId().toString().contains("ZJWX")){
                    continue;
                }
                if(i == 1) {
                    count += record.getCount();
                } else if(i == 2) {
                    count += record.getRepairCount();
                }
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return count;
    }

    public static List<ProcessTaskItem> queryProcessTaskItems(String oid, String number, String type) throws Exception {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        List<ProcessTaskItem> list = new ArrayList<>();
        ReferenceFactory rf = new ReferenceFactory();
        try {
            WTPart part = (WTPart) rf.getReference("VR:" + oid).getObject();
            int[] index = {0};
            QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
            qs.setAdvancedQueryEnabled(true);
            ClassAttribute caId = new ClassAttribute(ProcessTaskItem.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
            qs.appendWhere(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, part.getNumber()), index);
            qs.appendAnd();
            String yijv = type + AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU;
            qs.appendWhere(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.RENWUYIJU, SearchCondition.EQUAL, yijv), index);
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_ITEM_NAME, SearchCondition.EQUAL, ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU), index);
            qs.appendAnd();
            qs.appendOpenParen();
            SubSelectExpression se = IBAHelper.getStringIBAQuery("ANALYSISNUMBER", number);
            qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, se), index);
            qs.appendCloseParen();
            qs.appendOrderBy(new OrderBy(new ClassAttribute(ProcessTaskItem.class, ProcessTaskItem.CREATE_TIMESTAMP), true), index);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
            while(qr.hasMoreElements()) {
                ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
                list.add(taskItem);
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return list;
    }

    /**
     * 方法功能:查询制品条目
     *
     * @author cjh
     * @date 2025/6/26
     */
    public static List<GWDealProductRecord> queryERPDealRecordDatas(String oid, String number, String type) throws Exception {
        List<GWDealProductRecord> list = new ArrayList<>();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, oid);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, number);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, type);
            if(AnalysisConstant.SOURCE_ZAIZHIPIN.equals(type)) {
                qs.appendAnd();
                qs.appendWhere(GWDealProductRecord.KEY_ID, CmQuerySpec.LIKE, "ZJWX");
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                list.add(record);
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return list;
    }

    /**
     * 方法功能:查询整件外协
     *
     * @author cjh
     * @date 2024/7/22
     */
    public static List queryZjwxProductInfo(String oid, String number) throws Exception {
        List list = new ArrayList();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        int count = 0;
        String fzr = "";
        try {
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, oid);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, number);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, AnalysisConstant.SOURCE_ZAIZHIPIN);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.KEY_ID, CmQuerySpec.LIKE, "ZJWX");
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                count += record.getCount();
                if(StrUtil.isNotEmpty(fzr)) {
                    fzr += "，";
                }
                if(StrUtil.isNotEmpty(record.getResponser())) {
                    fzr += record.getResponser();
                }
            }
            list.add(count);
            list.add(fzr);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return list;
    }
}
