package ext.casc.analysisActivity.helper;

import cn.hutool.core.util.StrUtil;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.bean.AnalysisObjHistoryRecord;
import ext.casc.analysisActivity.bean.GWDealProductRecord;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import wt.change2.WTAnalysisActivity;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisUtil {

    /**
     * 方法功能: 通过编号获取影响分析
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static WTAnalysisActivity getWTAnalysisActivityByNumber(String number) {
        try {
            QuerySpec qs = new QuerySpec(WTAnalysisActivity.class);
            qs.appendWhere(new SearchCondition(WTAnalysisActivity.class, WTAnalysisActivity.NUMBER, SearchCondition.EQUAL, number), new int[]{0});
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
            while(qr.hasMoreElements()) {
                WTAnalysisActivity analysisActivity = (WTAnalysisActivity) qr.nextElement();
                if(analysisActivity != null) {
                    return analysisActivity;
                }
            }
        } catch(QueryException e) {
            e.printStackTrace();
        } catch(WTException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author cjh
     * @date 2024/8/28
     */
    public static String getVerOidByObject(WTObject object) {
        String verOid = "";
        try {
            if(object != null) {
                ReferenceFactory rf = new ReferenceFactory();
                verOid = rf.getReference(object).toString();
                verOid = verOid.replaceAll(">", ":");
            }
        } catch(QueryException e) {
            e.printStackTrace();
        } catch(WTException e) {
            e.printStackTrace();
        }
        return verOid;
    }

    /**
     * 方法功能: 获取影响分析列表数据
     *
     * @author cjh
     * @date 2024/8/23
     */
    public static List<AnalysisObjEntry> getAnalysisObjEntries(Map<String, String> map) {
        List<AnalysisObjEntry> list = new ArrayList<AnalysisObjEntry>();
        try {
            CmQuerySpec qs = new CmQuerySpec(AnalysisObjEntry.class);
            qs.appendWhere(AnalysisObjEntry.KEY_ID, CmQuerySpec.NOT_NULL);
            for(String key : map.keySet()) {
                String value = map.get(key);
                qs.appendAnd();
                qs.appendWhere(key, CmQuerySpec.EQUAL, value);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                AnalysisObjEntry entry = (AnalysisObjEntry) qr.next();
                list.add(entry);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 方法功能: 获取影响分析列表数据
     *
     * @author cjh
     * @date 2024/8/23
     */
    public static List<AnalysisObjEntry> getAnalysisObjEntries(String analysisNumber, String verOid, String dataType) {
        List<AnalysisObjEntry> list = new ArrayList<AnalysisObjEntry>();
        try {
            CmQuerySpec qs = new CmQuerySpec(AnalysisObjEntry.class);
            qs.appendWhere(AnalysisObjEntry.KEY_ID, CmQuerySpec.NOT_NULL);
            if(StrUtil.isNotEmpty(analysisNumber)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.ANALYSISNUMBER, CmQuerySpec.EQUAL, analysisNumber);
            }
            if(StrUtil.isNotEmpty(verOid)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.VEROID, CmQuerySpec.EQUAL, verOid);
            }
            if(StrUtil.isNotEmpty(dataType)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.DATATYPE, CmQuerySpec.LIKE, "%" + dataType + "%");
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                AnalysisObjEntry entry = (AnalysisObjEntry) qr.next();
                list.add(entry);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static AnalysisObjEntry getAnalysisObjEntry(String analysisNumber, String verOid, String dataType) {
        try {
            CmQuerySpec qs = new CmQuerySpec(AnalysisObjEntry.class);
            qs.appendWhere(AnalysisObjEntry.KEY_ID, CmQuerySpec.NOT_NULL);
            if(StrUtil.isNotEmpty(analysisNumber)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.ANALYSISNUMBER, CmQuerySpec.EQUAL, analysisNumber);
            }
            if(StrUtil.isNotEmpty(verOid)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.VEROID, CmQuerySpec.EQUAL, verOid);
            }
            if(StrUtil.isNotEmpty(dataType)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.DATATYPE, CmQuerySpec.EQUAL, dataType);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            if(qr.hasNext()) {
                AnalysisObjEntry entry = (AnalysisObjEntry) qr.next();
                return entry;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 方法功能: 获取修改时数据列表，不包含已完成的 只会是pbom或工艺
     *
     * @author cjh
     * @date 2024/8/29
     */
    public static List<AnalysisObjEntry> getModifyAnalysisObjEntries(String analysisNumber, String verOid, String dataType) {
        List<AnalysisObjEntry> list = new ArrayList<AnalysisObjEntry>();
        try {
            CmQuerySpec qs = new CmQuerySpec(AnalysisObjEntry.class);
            qs.appendWhere(AnalysisObjEntry.KEY_ID, CmQuerySpec.NOT_NULL);
            qs.appendAnd();
            qs.appendOpenParen();
            qs.appendWhere(AnalysisObjEntry.DEALSTATUS, CmQuerySpec.IS_NULL);
            qs.appendOr();
            qs.appendWhere(AnalysisObjEntry.DEALSTATUS, CmQuerySpec.NOT_EQUAL, AnalysisConstant.DEAL_STATUS_FINISH);
            qs.appendCloseParen();
            if(StrUtil.isNotEmpty(analysisNumber)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.ANALYSISNUMBER, CmQuerySpec.EQUAL, analysisNumber);
            }
            if(StrUtil.isNotEmpty(verOid)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.VEROID, CmQuerySpec.EQUAL, verOid);
            }
            if(StrUtil.isNotEmpty(dataType)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.DATATYPE, CmQuerySpec.EQUAL, dataType);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                AnalysisObjEntry entry = (AnalysisObjEntry) qr.next();
                list.add(entry);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 方法功能: 获取影响分析历史处理数据
     *
     * @author cjh
     * @date 2024/8/23
     */
    public static List<AnalysisObjHistoryRecord> getAnalysisObjHistoryRecords(String analysisNumber, String workItemOid, String verOid, String dataType) {
        List<AnalysisObjHistoryRecord> list = new ArrayList<AnalysisObjHistoryRecord>();
        try {
            CmQuerySpec qs = new CmQuerySpec(AnalysisObjHistoryRecord.class);
            qs.appendWhere(AnalysisObjHistoryRecord.KEY_ID, CmQuerySpec.NOT_NULL);
            if(StrUtil.isNotEmpty(analysisNumber)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjHistoryRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, analysisNumber);
            }
            if(StrUtil.isNotEmpty(workItemOid)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjHistoryRecord.WORKITEMOID, CmQuerySpec.EQUAL, workItemOid);
            }
            if(StrUtil.isNotEmpty(verOid)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjHistoryRecord.VEROID, CmQuerySpec.EQUAL, verOid);
            }
            if(StrUtil.isNotEmpty(dataType)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjHistoryRecord.DATATYPE, CmQuerySpec.LIKE, "%" + dataType + "%");
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                AnalysisObjHistoryRecord record = (AnalysisObjHistoryRecord) qr.next();
                list.add(record);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 方法功能: 获取影响分析历史处理数据
     *
     * @author cjh
     * @date 2024/8/23
     */
    public static Map<String, String> getProductMap(String analysisNumber, String verOid) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            CmQuerySpec qs = new CmQuerySpec(AnalysisObjEntry.class);
            qs.appendWhere(AnalysisObjEntry.DATATYPE, CmQuerySpec.LIKE, "%" + AnalysisConstant.PRODUCT + "%");
            if(StrUtil.isNotEmpty(analysisNumber)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.ANALYSISNUMBER, CmQuerySpec.EQUAL, analysisNumber);
            }
            if(StrUtil.isNotEmpty(verOid)) {
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.VEROID, CmQuerySpec.EQUAL, verOid);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                AnalysisObjEntry entry = (AnalysisObjEntry) qr.next();
                if(AnalysisConstant.TYPE_ZAIZHIPIN.equals(entry.getDataType())) {
                    map.put("number", entry.getAnalysisNumber());
                    map.put("verOid", entry.getVerOid());
                    map.put("zaizhipin", entry.getProduct());
                    map.put("zcount", entry.getCount().toString());
                    map.put("zrepaircount", entry.getRepaircount().toString());
                    map.put("responser", entry.getResponser());
                    map.put("zdealstatus", entry.getDealStatus());
                    map.put("zsendstatus", entry.getSendStatus());
                } else if(AnalysisConstant.TYPE_YIZHIPIN.equals(entry.getDataType())) {
                    map.put("number", entry.getAnalysisNumber());
                    map.put("verOid", entry.getVerOid());
                    map.put("yizhipin", entry.getProduct());
                    map.put("ycount", entry.getCount().toString());
                    map.put("yrepaircount", entry.getRepaircount().toString());
                    map.put("jidiaoyuan", entry.getResponser());
                    map.put("ydealstatus", entry.getDealStatus());
                    map.put("ysendstatus", entry.getSendStatus());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 方法功能:查询整件外协处理完成数量
     *
     * @author cjh
     * @date 2024/9/10
     */
    public static Integer queryZjwxDealCount(String oid, String number) throws Exception {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        Integer count = 0;
        try {
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, oid);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, number);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, AnalysisConstant.SOURCE_ZAIZHIPIN);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.KEY_ID, CmQuerySpec.LIKE, "%ZJWX%");
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.STATUS, CmQuerySpec.NOT_NULL);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                count += record.getCount();
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return count;
    }


    /**
     * 方法功能:查询制品是否处理完毕
     *
     * @author cjh
     * @date 2024/10/10
     */
    public static boolean checkProductDealResult(AnalysisObjEntry entry,boolean checkAgain) {
        try {
            if(AnalysisConstant.DEAL_STATUS_FINISH.equals(entry.getDealStatus()) && !checkAgain){
                return true;
            }
            if(StrUtil.isEmpty(entry.getReceiveStatus())) {
                return false;
            }
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, entry.getAnalysisNumber());
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, entry.getVerOid());
            qs.appendAnd();
            if(AnalysisConstant.TYPE_ZAIZHIPIN.equals(entry.getDataType())) {
                qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, AnalysisConstant.SOURCE_ZAIZHIPIN);
                qs.appendAnd();
                qs.appendWhere(GWDealProductRecord.KEY_ID, CmQuerySpec.LIKE, "%MES%");
            } else {
                qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, AnalysisConstant.SOURCE_YIZHIPIN);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            if(qr.size() == 0) {
                return false;
            }
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                String status = record.getStatus();
                if(StrUtil.isEmpty(status)) {
                    return false;
                }
            }
            //校验整件外协是否处理完毕
            if(AnalysisConstant.TYPE_ZAIZHIPIN.equals(entry.getDataType())) {
                AnalysisObjEntry yizhipin = AnalysisUtil.getAnalysisObjEntry(entry.getAnalysisNumber(), entry.getVerOid(), AnalysisConstant.TYPE_YIZHIPIN);
                if(yizhipin != null) {
                    if(StrUtil.isEmpty(yizhipin.getReceiveStatus())) {
                        return false;
                    } else if(entry.getZjwxcount() > 0) {
                        Integer zjwxDealCount = AnalysisUtil.queryZjwxDealCount(entry.getVerOid(), entry.getAnalysisNumber());
                        if(zjwxDealCount < entry.getZjwxcount()) {
                            return false;
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Boolean isAllNoAffected(String analysisNumber, String verOid) {
        Map<String, String> productMap = AnalysisUtil.getProductMap(analysisNumber, verOid);
        if(AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(productMap.get("zaizhipin"))
                && AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(productMap.get("yizhipin"))){
            return true;
        }
        return false;
    }


    public static void deleteAnalysisEntry(String number) {
        List<AnalysisObjEntry> entries = getAnalysisObjEntries(number, null, null);
        for(AnalysisObjEntry entry : entries) {
            try {
                CmPersistenceHelper.manager.delete(entry);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteGwDealProductRecord(String number) {
        try {
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, number);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                CmPersistenceHelper.manager.delete(record);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
