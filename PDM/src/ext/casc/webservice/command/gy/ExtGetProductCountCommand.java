/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.DateUtil;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.bean.GWDealProductRecord;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.analysisActivity.log.AnalysisSyncLogger;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.ProcessTaskLink;
import ext.casc.process.assign.ProProcessor;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAHelper;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.change2.WTAnalysisActivity;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.project.Role;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 类功能：接收制品数量分析
 * NC返回已制品在制品的数量
 * <p>
 * 影响分析编号、图号、已制品数量、在制品数量
 *
 * @author chenjianhui
 * @date 2024/04/01
 */
@Component
public class ExtGetProductCountCommand implements WebServiceCommand, InitializingBean {
    // 方法标识
    public static final String METHOD_NAME = "getProductCount";

    private static String PARA_ANALYSISNUMBER = "analysisNumber";
    private static String PARA_DATA = "data";
    private static String PARA_PARTID = "partId";
    private static String PARA_MODEL = "model";
    private static String PARA_DEALTYPE = "dealType";
    private static String PARA_YZPCOUNT = "yzpCount";
    private static String PARA_ZZPCOUNT = "zzpCount";
    private static String PARA_ZJWXCOUNT = "zjwxCount";
    private static String PARA_ZJWXFZR = "zjwxfzr";
    private static String PARA_DOCNUMBER = "docNumber";
    private static String PARA_DOCVERSION = "docVersion";
    private static String PARA_CARD = "card";
    private static String PARA_COMMENTS = "comments";

    @Override
    public String execute(String params) {
        AnalysisSyncLogger logger = AnalysisSyncLogger.getInstance();
        JSONObject rtnMsgObj = new JSONObject();
        String errorMsg = "";
        JSONObject jparams = null;
        try {
            jparams = new JSONObject(params);
            logger.log("getProductCount jparams=" + jparams);
        } catch(JSONException e) {
            errorMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
            rtnMsgObj.put("status", "N");
            rtnMsgObj.put("result", errorMsg);
            logger.log(rtnMsgObj.toString());
            return rtnMsgObj.toString();
        }
        Transaction tran = new Transaction();
        try {
            if(StrUtil.isEmpty(errorMsg)) {
                tran.start();
                //校验数据
                String analysisNumber = jparams.optString(PARA_ANALYSISNUMBER);
                if(StrUtil.isNotEmpty(analysisNumber)) {
                    WTAnalysisActivity activity = AnalysisUtil.getWTAnalysisActivityByNumber(analysisNumber);
                    if(activity == null) {
                        errorMsg = "未查询到编号为" + analysisNumber + "的更改影响分析。";
                        rtnMsgObj.put("status", "N");
                        rtnMsgObj.put("result", errorMsg);
                        logger.log(rtnMsgObj.toString());
                        return rtnMsgObj.toString();
                    }
                } else {
                    errorMsg = "影响分析编号不允许为空！";
                    rtnMsgObj.put("status", "N");
                    rtnMsgObj.put("result", errorMsg);
                    logger.log(rtnMsgObj.toString());
                    return rtnMsgObj.toString();
                }
                JSONArray data = jparams.optJSONArray(PARA_DATA);
                if(data != null && data.length() > 0) {
                    for(int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        String partId = object.optString(PARA_PARTID);
                        String card = object.optString(PARA_CARD);
                        String zzpCount = object.optString(PARA_ZZPCOUNT);//在制品数量
                        String yzpCount = object.optString(PARA_YZPCOUNT);//已制品数量
                        String zjwxCount = object.optString(PARA_ZJWXCOUNT);//整件外协数量
                        if(StrUtil.isEmpty(partId)) {
                            errorMsg = "部件id不允许为空！";
                            rtnMsgObj.put("status", "N");
                            rtnMsgObj.put("result", errorMsg);
                            logger.log(rtnMsgObj.toString());
                            return rtnMsgObj.toString();
                        }
                        //在制品校验
                        if(StrUtil.isNotEmpty(zzpCount)) {
                            try {
                                Integer.valueOf(zzpCount);
                            } catch(Exception e) {
                                errorMsg = "在制品数量必须为整数！";
                                rtnMsgObj.put("status", "N");
                                rtnMsgObj.put("result", errorMsg);
                                logger.log(rtnMsgObj.toString());
                                return rtnMsgObj.toString();
                            }
                            if(StrUtil.isEmpty(card)) {
                                errorMsg = "路卡号不允许为空！";
                                rtnMsgObj.put("status", "N");
                                rtnMsgObj.put("result", errorMsg);
                                logger.log(rtnMsgObj.toString());
                                return rtnMsgObj.toString();
                            }
                            AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, AnalysisConstant.TYPE_ZAIZHIPIN);
                            if(entry == null) {
                                errorMsg += "未查询到当前影响分析单部件ID为" + partId + "的在制品条目";
                            }
                        }
                        //已制品校验
                        if(StrUtil.isNotEmpty(yzpCount)) {
                            try {
                                Integer.valueOf(yzpCount);
                            } catch(Exception e) {
                                errorMsg = "已制品数量必须为整数！";
                                rtnMsgObj.put("status", "N");
                                rtnMsgObj.put("result", errorMsg);
                                logger.log(rtnMsgObj.toString());
                                return rtnMsgObj.toString();
                            }
                            if(StrUtil.isEmpty(card)) {
                                errorMsg = "库存批次号不允许为空！";
                                rtnMsgObj.put("status", "N");
                                rtnMsgObj.put("result", errorMsg);
                                logger.log(rtnMsgObj.toString());
                                return rtnMsgObj.toString();
                            }
                            AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, AnalysisConstant.TYPE_YIZHIPIN);
                            if(entry == null) {
                                errorMsg += "未查询到当前影响分析单部件ID为" + partId + "的已制品条目";
                            }
                        }
                        if(StrUtil.isNotEmpty(zjwxCount)) {
                            try {
                                Integer.valueOf(zjwxCount);
                            } catch(Exception e) {
                                errorMsg = "整件外协数量必须为整数！";
                                rtnMsgObj.put("status", "N");
                                rtnMsgObj.put("result", errorMsg);
                                logger.log(rtnMsgObj.toString());
                                return rtnMsgObj.toString();
                            }
                            if(StrUtil.isEmpty(card)) {
                                errorMsg = "整件外协离散订单号不允许为空！";
                                rtnMsgObj.put("status", "N");
                                rtnMsgObj.put("result", errorMsg);
                                logger.log(rtnMsgObj.toString());
                                return rtnMsgObj.toString();
                            }
                            AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, AnalysisConstant.TYPE_ZAIZHIPIN);
                            if(entry == null) {
                                errorMsg += "未查询到当前影响分析单部件ID为" + partId + "的在制品条目";
                            }
                        }
                    }
                }
                if(StrUtil.isEmpty(errorMsg)) {
                    List<GWDealProductRecord> list = new ArrayList<>();
                    List<GWDealProductRecord> update = new ArrayList<>();
                    List<GWDealProductRecord> delete = new ArrayList<>();
                    List<GWDealProductRecord> deleteTask = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
                    if(data != null && data.length() > 0) {
                        Set<String> partIds = new HashSet<>();
                        for(int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            logger.log("开始处理：" + object);
                            String partId = object.optString(PARA_PARTID);//部件id
                            String model = object.optString(PARA_MODEL);//处理模式 修改、删除、增加、不变
                            String dealType = object.optString(PARA_DEALTYPE);//处理类型 返修 报废 无影响
                            String card = object.optString(PARA_CARD);//路卡号/库存批次号
                            String docNumber = object.optString(PARA_DOCNUMBER);//已提前返修关联的工艺文件流水号
                            String docVersion = object.optString(PARA_DOCVERSION);//已提前返修关联的工艺文件版本
                            String zzpCount = object.optString(PARA_ZZPCOUNT);//在制品数量
                            String yzpCount = object.optString(PARA_YZPCOUNT);//已制品数量
                            String zjwxCount = object.optString(PARA_ZJWXCOUNT);//整件外协数量
                            String comments = object.optString(PARA_COMMENTS);//处理意见
                            String zjwxfzr = object.optString(PARA_ZJWXFZR);//整件外协负责人
                            partIds.add(partId);
                            String date = sdf.format(new Date());
                            Timestamp timestamp = Timestamp.valueOf(date);
                            //已制品
                            if(StrUtil.isNotEmpty(yzpCount)) {
                                if(AnalysisConstant.ANALYSIS_MODEL_NEW.equals(model) || AnalysisConstant.ANALYSIS_MODEL_EDIT.equals(model)) {
                                    //根据字段查询是否有历史处理记录
                                    GWDealProductRecord history = ExtGetProductDealResultCommand.queryDealRecordData(partId, analysisNumber, AnalysisConstant.SOURCE_YIZHIPIN, card);
                                    //更新或者新建
                                    if(history != null) {
                                        //更新意见后续处理
                                        if(AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU.equals(history.getDealType())
                                                && !AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU.equals(dealType)) {
                                            //记录需要删除任务的条目
                                            deleteTask.add(history);
                                            history.setTaskStatus("");
                                        }
                                        history.setCount(Integer.valueOf(yzpCount));
                                        history.setUpdateTimeStamp(timestamp);
                                        if(Integer.valueOf(yzpCount) == 0 || AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(dealType)) {
                                            history.setStatus(AnalysisConstant.DEALRECORD_STATUS_OK);
                                            history.setFinishTime(date);
                                        } else {
                                            history.setStatus("");
                                            history.setFinishTime("");
                                        }
                                        history.setDocNumber(docNumber);
                                        history.setDocVersion(docVersion);
                                        history.setDealType(dealType);
                                        history.setCard(card);
                                        history.setComments(comments);
                                        update.add(history);
                                    } else {
                                        GWDealProductRecord record = new GWDealProductRecord();
                                        if(Integer.valueOf(yzpCount) == 0 || AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(dealType)) {
                                            record.setStatus(AnalysisConstant.DEALRECORD_STATUS_OK);
                                            record.setFinishTime(date);
                                        }
                                        record.setSource(AnalysisConstant.SOURCE_YIZHIPIN);
                                        record.setCount(Integer.valueOf(yzpCount));
                                        record.setAnalysisNumber(analysisNumber);
                                        record.setVerOid(partId);
                                        record.setDealType(dealType);
                                        record.setCard(card);
                                        record.setDocNumber(docNumber);
                                        record.setDocVersion(docVersion);
                                        record.setCreateTimeStamp(timestamp);
                                        record.setUpdateTimeStamp(timestamp);
                                        record.setComments(comments);
                                        list.add(record);
                                    }
                                    AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, AnalysisConstant.TYPE_YIZHIPIN);
                                    if(entry != null && StrUtil.isEmpty(entry.getReceiveStatus())) {
                                        entry.setReceiveStatus(AnalysisConstant.DEAL_STATUS_RECEIVECOUNT);
                                        CmPersistenceHelper.manager.update(entry);
                                    }
                                } else if(AnalysisConstant.ANALYSIS_MODEL_DEL.equals(model)) {
                                    GWDealProductRecord history = ExtGetProductDealResultCommand.queryDealRecordData(partId, analysisNumber, AnalysisConstant.SOURCE_YIZHIPIN, card);
                                    if(history != null) {
                                        if(AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU.equals(history.getDealType())) {
                                            //记录需要删除任务的条目
                                            deleteTask.add(history);
                                        }
                                        delete.add(history);
                                    }
                                }
                            } else if(StrUtil.isNotEmpty(zzpCount)) {
                                //在制品
                                if(AnalysisConstant.ANALYSIS_MODEL_NEW.equals(model) || AnalysisConstant.ANALYSIS_MODEL_EDIT.equals(model)) {
                                    //根据字段查询是否有历史处理记录
                                    GWDealProductRecord history = ExtGetProductDealResultCommand.queryDealRecordData(partId, analysisNumber, AnalysisConstant.SOURCE_ZAIZHIPIN, card);
                                    //更新或者新建
                                    if(history != null) {
                                        //更新意见后续处理
                                        if(AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU.equals(history.getDealType())) {
                                            //MES不管新意见是什么，只要修改返修就删除对应任务
                                            deleteTask.add(history);
                                            history.setTaskStatus("");
                                        }
                                        history.setCount(Integer.valueOf(zzpCount));
                                        history.setUpdateTimeStamp(timestamp);
                                        if(Integer.valueOf(zzpCount) == 0 || AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(dealType)
                                                || AnalysisConstant.ANALYSIS_AFFECTED_WXFX.equals(dealType)
                                                || AnalysisConstant.ANALYSIS_AFFECTED_YCZZ.equals(dealType)) {
                                            history.setStatus(AnalysisConstant.DEALRECORD_STATUS_OK);
                                            history.setFinishTime(date);
                                        } else {
                                            history.setStatus("");
                                            history.setFinishTime("");
                                        }
                                        history.setDocNumber(docNumber);
                                        history.setDocVersion(docVersion);
                                        history.setDealType(dealType);
                                        history.setCard(card);
                                        update.add(history);
                                    } else {
                                        GWDealProductRecord record = new GWDealProductRecord();
                                        record.setKeyId(UUID.randomUUID() + "_MES");
                                        if(Integer.valueOf(zzpCount) == 0 || AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(dealType)
                                                || AnalysisConstant.ANALYSIS_AFFECTED_WXFX.equals(dealType)
                                                || AnalysisConstant.ANALYSIS_AFFECTED_YCZZ.equals(dealType)) {
                                            record.setStatus(AnalysisConstant.DEALRECORD_STATUS_OK);
                                            record.setFinishTime(date);
                                        }
                                        record.setSource(AnalysisConstant.SOURCE_ZAIZHIPIN);
                                        record.setCount(Integer.valueOf(zzpCount));
                                        record.setAnalysisNumber(analysisNumber);
                                        record.setVerOid(partId);
                                        record.setDealType(dealType);
                                        record.setCard(card);
                                        record.setDocNumber(docNumber);
                                        record.setDocVersion(docVersion);
                                        record.setCreateTimeStamp(timestamp);
                                        record.setUpdateTimeStamp(timestamp);
                                        list.add(record);
                                    }
                                    AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, AnalysisConstant.TYPE_ZAIZHIPIN);
                                    if(entry != null && StrUtil.isEmpty(entry.getReceiveStatus())) {
                                        entry.setReceiveStatus(AnalysisConstant.DEAL_STATUS_RECEIVECOUNT);
                                        CmPersistenceHelper.manager.update(entry);
                                    }
                                } else if(AnalysisConstant.ANALYSIS_MODEL_DEL.equals(model)) {
                                    GWDealProductRecord history = ExtGetProductDealResultCommand.queryDealRecordData(partId, analysisNumber, AnalysisConstant.SOURCE_ZAIZHIPIN, card);
                                    if(history != null) {
                                        if(AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU.equals(history.getDealType())) {
                                            //记录需要删除任务的条目
                                            deleteTask.add(history);
                                        }
                                        delete.add(history);
                                    }
                                }
                            } else if(StrUtil.isNotEmpty(zjwxCount) && !"0".equals(zjwxCount)) {
                                if(AnalysisConstant.ANALYSIS_MODEL_NEW.equals(model) || AnalysisConstant.ANALYSIS_MODEL_EDIT.equals(model)) {
                                    //根据字段查询是否有历史处理记录
                                    GWDealProductRecord history = ExtGetProductDealResultCommand.queryDealRecordData(partId, analysisNumber, AnalysisConstant.SOURCE_ZAIZHIPIN, card);
                                    //更新或者新建
                                    if(history != null) {
                                        history.setCount(Integer.valueOf(zjwxCount));
                                        history.setUpdateTimeStamp(timestamp);
                                        history.setStatus("");
                                        history.setFinishTime("");
                                        history.setResponser(zjwxfzr);
                                        history.setCard(card);
                                        history.setComments(comments);
                                        update.add(history);
                                    } else {
                                        GWDealProductRecord record = new GWDealProductRecord();
                                        record.setKeyId(UUID.randomUUID() + "_ZJWX");
                                        record.setSource(AnalysisConstant.SOURCE_ZAIZHIPIN);
                                        record.setCount(Integer.valueOf(zjwxCount));
                                        record.setAnalysisNumber(analysisNumber);
                                        record.setVerOid(partId);
                                        record.setCard(card);
                                        record.setResponser(zjwxfzr);
                                        record.setCreateTimeStamp(timestamp);
                                        record.setUpdateTimeStamp(timestamp);
                                        record.setComments(comments);
                                        list.add(record);
                                    }
                                }
                            }
                        }
                        if(StrUtil.isEmpty(errorMsg)) {
                            for(GWDealProductRecord record : list) {
                                CmPersistenceHelper.manager.save(record);
                            }
                            for(GWDealProductRecord record : update) {
                                CmPersistenceHelper.manager.update(record);
                            }
                            for(GWDealProductRecord record : delete) {
                                CmPersistenceHelper.manager.delete(record);
                            }
                            //处理需要删除的任务
                            for(GWDealProductRecord record : deleteTask) {
                                String card = StrUtil.isEmpty(record.getCard()) ? "" : record.getCard();
                                if(AnalysisConstant.SOURCE_YIZHIPIN.equals(record.getSource()) && card.contains("_")) {
                                    card = card.split("_")[0];
                                }
                                List<ProcessTaskItem> taskItems = ExtGetProductDealResultCommand.queryProcessTaskItems(record.getVerOid(), record.getAnalysisNumber(), record.getSource());
                                boolean ok = false;
                                for(ProcessTaskItem taskItem : taskItems) {
                                    if(ok){
                                        break;
                                    }
                                    String renwuyaoqiu = taskItem.getRenwuyaoqiu();
                                    if(StrUtil.isNotEmpty(renwuyaoqiu)) {
                                        String[] strs = renwuyaoqiu.split("<br>");
                                        if(strs.length > 1) {
                                            for(int i = 1; i < strs.length; i++) {
                                                String str = strs[i];
                                                if(str.contains(card)) {
                                                    if(strs.length == 2) {
                                                        //任务只有当前一个card，直接删除任务
                                                        logger.log("删除返修任务：" + taskItem.getNumber());
                                                        PersistenceHelper.manager.delete(taskItem);
                                                        ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
                                                        ProcessTaskLink link = ProcessUtil.getProcessTaskLinkByProcessTask(taskItem.getProcessTaskId());
                                                        if(processTask != null && link != null) {
                                                            PersistenceHelper.manager.delete(processTask);
                                                            PersistenceHelper.manager.delete(link);
                                                        }
                                                    } else {
                                                        //任务不止一个card，就修改任务要求，把不需要的片段去掉
                                                        logger.log("修改返修任务：" + taskItem.getNumber());
                                                        renwuyaoqiu = renwuyaoqiu.replaceAll(str + "<br>", "");
                                                        taskItem.setRenwuyaoqiu(renwuyaoqiu);
                                                        PersistenceHelper.manager.save(taskItem);
                                                    }
                                                    ok = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //重新计算制品数量并处理返修工艺任务
                            for(String partId : partIds) {
                                //制品数量
                                Integer zCount = ExtGetProductDealResultCommand.queryDealProductCount(partId, analysisNumber, AnalysisConstant.SOURCE_ZAIZHIPIN, 1);
                                Integer zrepairCount = ExtGetProductDealResultCommand.queryDealProductCount(partId, analysisNumber, AnalysisConstant.SOURCE_ZAIZHIPIN, 2);
                                Integer yCount = ExtGetProductDealResultCommand.queryDealProductCount(partId, analysisNumber, AnalysisConstant.SOURCE_YIZHIPIN, 1);
                                Integer yrepairCount = ExtGetProductDealResultCommand.queryDealProductCount(partId, analysisNumber, AnalysisConstant.SOURCE_YIZHIPIN, 2);
                                List zjwxInfo = ExtGetProductDealResultCommand.queryZjwxProductInfo(partId, analysisNumber);
                                AnalysisObjEntry zaizhipin = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, AnalysisConstant.TYPE_ZAIZHIPIN);
                                AnalysisObjEntry yizhipin = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, AnalysisConstant.TYPE_YIZHIPIN);
                                if(yizhipin != null && zaizhipin != null) {
                                    //已制品
                                    List<GWDealProductRecord> ylist = getNoTaskFanxiu(yizhipin, AnalysisConstant.SOURCE_YIZHIPIN);
                                    if(ylist.size() > 0) {
                                        String yaoqiu = "更改要求：" + yizhipin.getRequirement() + "； <br>";
                                        for(GWDealProductRecord record : ylist) {
                                            String card = StrUtil.isEmpty(record.getCard()) ? "" : record.getCard();
                                            if(card.contains("_")) {
                                                card = card.split("_")[0];
                                            }
                                            yaoqiu += "库存批次号：" + card + "；返修数量：" + record.getCount() + "；<br>";
                                            record.setTaskStatus(AnalysisConstant.TASK_STATUS_OK);
                                            CmPersistenceHelper.manager.update(record);
                                        }
                                        logger.log("创建已制品返修任务>>>>部件id:" + partId + " 影响分析编号：" + analysisNumber + " 任务要求：" + yaoqiu + " 责任人：" + zaizhipin.getResponser());
                                        createProcessTask(partId, yaoqiu, analysisNumber, AnalysisConstant.RENWUYIJV_YIZHIPIN, zaizhipin.getResponser());
                                    }
                                    yizhipin.setRepaircount(yrepairCount);
                                    yizhipin.setCount(yCount);
                                    //校验制品
                                    if(AnalysisUtil.checkProductDealResult(yizhipin, true)) {
                                        yizhipin.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                                    } else {
                                        yizhipin.setDealStatus(AnalysisConstant.DEAL_STATUS_WORKING);
                                    }
                                    CmPersistenceHelper.manager.update(yizhipin);

                                    //在制品
                                    List<GWDealProductRecord> zlist = getNoTaskFanxiu(zaizhipin, AnalysisConstant.SOURCE_ZAIZHIPIN);
                                    if(zlist.size() > 0) {
                                        String yaoqiu = "更改要求：" + zaizhipin.getRequirement() + "； <br>";
                                        for(GWDealProductRecord record : zlist) {
                                            yaoqiu += "路卡号：" + record.getCard() + "；返修数量：" + record.getCount() + "；<br>";
                                            record.setTaskStatus(AnalysisConstant.TASK_STATUS_OK);
                                            CmPersistenceHelper.manager.update(record);
                                        }
                                        logger.log("创建在制品返修任务>>>>部件id:" + partId + " 影响分析编号：" + analysisNumber + " 任务要求：" + yaoqiu + " 责任人：" + zaizhipin.getResponser());
                                        createProcessTask(partId, yaoqiu, analysisNumber, AnalysisConstant.RENWUYIJV_ZAIZHIPIN, zaizhipin.getResponser());
                                    }
                                    zaizhipin.setCount(zCount);
                                    zaizhipin.setRepaircount(zrepairCount);
                                    if(zjwxInfo.size() >= 2) {
                                        int count = (int) zjwxInfo.get(0);
                                        String fzr = (String) zjwxInfo.get(1);
                                        zaizhipin.setZjwxcount(count);
                                        zaizhipin.setRemarkGyy(fzr);
                                    }
                                    //校验制品
                                    if(AnalysisUtil.checkProductDealResult(zaizhipin, true)) {
                                        zaizhipin.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                                    } else {
                                        zaizhipin.setDealStatus(AnalysisConstant.DEAL_STATUS_WORKING);
                                    }
                                    CmPersistenceHelper.manager.update(zaizhipin);
                                }
                            }
                        }
                    }
                }
            }
            tran.commit();
            tran = null;

        } catch(Exception e) {
            e.printStackTrace();
            errorMsg += e.getMessage();
        } finally {
            if(tran != null) {
                tran.rollback();
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
            logger.log("getProductCount返回结果：" + rtnMsgObj);
        }
        return rtnMsgObj.toString();
    }

    private List<GWDealProductRecord> getNoTaskFanxiu(AnalysisObjEntry zhipin, String source) throws Exception {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        List<GWDealProductRecord> list = new ArrayList<>();
        try {
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, zhipin.getVerOid());
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, zhipin.getAnalysisNumber());
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, source);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.TASKSTATUS, CmQuerySpec.IS_NULL);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.DEALTYPE, CmQuerySpec.EQUAL, AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                if(record.getKeyId().toString().contains("ZJWX")){
                    continue;
                }
                if(record.getCount() > 0) {
                    list.add(record);
                }
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return list;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }

    public static Folder getFolder(String path, WTContainer con) throws WTException {
        Folder folder = null;
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        String subPath = "";
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            subPath = subPath + "/" + token;
            if(subPath != null && !subPath.equalsIgnoreCase("")) {
                try {
                    folder = FolderHelper.service.getFolder(subPath, WTContainerRef.newWTContainerRef(con));
                } catch(FolderNotFoundException e) {
                    boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
                    folder = FolderHelper.service.createSubFolder(subPath, WTContainerRef.newWTContainerRef(con));
                    SessionServerHelper.manager.setAccessEnforced(flag);
                }
            }
        }
        return folder;
    }

    public static void createProcessTask(String partId, String requirement, String analysisNumber, String type, String userId) throws WTException, WTPropertyVetoException {
        //生成返修工艺任务
        String vrOid = "VR:" + partId;
        ReferenceFactory rf = new ReferenceFactory();
        Persistable persistable = rf.getReference(vrOid).getObject();
        String userName = "";
        if(StrUtil.isNotEmpty(userId)) {
            try {
                Persistable object = rf.getReference(userId).getObject();
                if(object != null && object instanceof WTUser) {
                    WTUser user = (WTUser) object;
                    userName = user.getName();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        String zhurengongyishi = "";
        WTAnalysisActivity activity = AnalysisUtil.getWTAnalysisActivityByNumber(analysisNumber);
        if(activity != null) {
            QueryResult result = WfEngineHelper.service.getAssociatedProcesses(activity, null, null);
            if(result.hasMoreElements()) {
                WfProcess process = (WfProcess) result.nextElement();
                Team team = (Team) process.getTeamId().getObject();
                Map map = team.getRolePrincipalMap();
                List tempUserList = (List) map.get(Role.toRole("ZHURENGONGYISHI"));
                if(CollUtil.isNotEmpty(tempUserList)) {
                    zhurengongyishi = ((WTUser) ((WTPrincipalReference) tempUserList.get(0)).getObject()).getName();
                    if(StrUtil.isEmpty(userName)) {
                        userName = zhurengongyishi;
                    }
                }
            }
        }
        if(persistable != null && persistable instanceof WTPart) {
            WTPart part = (WTPart) persistable;
            //新建工艺任务对象
            String zhuzhichejian = IBAHelper.getIBAStringValue(part, "ZZCJ");
            if(StrUtil.isEmpty(zhuzhichejian))
                zhuzhichejian = "1";
            if(StrUtil.isNotEmpty(zhuzhichejian)) {
                ProcessTask processTask = ProcessTask.newProcessTask();
                processTask.setName(part.getName());
                processTask.setNumber(part.getNumber());
                processTask.setVersion(part.getIterationDisplayIdentifier().toString());
                processTask.setZhuzhichejian(zhuzhichejian);
                processTask.setFuzhichejian("");
                processTask.setRenwuyaoqiu(requirement);
                processTask.setEndDate(Timestamp.valueOf(DateUtil.afterNDay("yyyy-MM-dd hh:mm:ss", 5)));
                processTask.setTaskState(ProcessConstants.TASK_STATE_JINGXINZHONG);
                processTask.setTaskType(ProcessConstants.TASK_TYPE_LINSHIGONGYI);
                processTask.setRenwuyiju("返工返修");
                processTask.setContainer(part.getContainer());
                Folder folder = getFolder("/Default", part.getContainer());
                FolderHelper.assignLocation((FolderEntry) processTask, folder);
                processTask = (ProcessTask) PersistenceHelper.manager.save(processTask);
                //创建工艺任务与零部件的关联
                ProProcessor.createProcessTaskLink(processTask, part);

                //创建工艺任务活动条目
                ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
                processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(processTask).getId());
                processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
                processTaskItem.setOwner(userName);
                processTaskItem.setName(part.getName());
                processTaskItem.setNumber(part.getNumber());
                processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
                processTaskItem.setZhurengongyishi(zhurengongyishi);
                processTaskItem.setChejian(zhuzhichejian);
                processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
                processTaskItem.setIszhuzhi(true);
                processTaskItem.setRenwuyaoqiu(requirement);
                processTaskItem.setRenwuyiju(type);
                processTaskItem.setTaskType(ProcessConstants.TASK_TYPE_LINSHIGONGYI);
                processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                processTaskItem.setEndDate(Timestamp.valueOf(DateUtil.afterNDay("yyyy-MM-dd hh:mm:ss", 5)));
                processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU);
                processTaskItem.setContainer(part.getContainer());
                FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);
                processTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(processTaskItem);
                IBAHelper.setIBAStringValue(processTaskItem, "ANALYSISNUMBER", analysisNumber);
            }
        }
    }
}
