package ext.casc.distribute.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import ext.casc.analysisActivity.bean.GWDealProductRecord;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.analysisActivity.process.GenerateRelatedAnalysisJson;
import ext.casc.distribute.contant.DistributeConstants;
import ext.casc.distribute.integration.OtherSystemIntegrationHelper;
import ext.casc.distribute.util.DWSqlUtil;
import ext.casc.distribute.util.JsonVoConverter;
import ext.casc.distribute.vo.*;
import org.apache.commons.lang3.StringUtils;
import wt.change2.WTAnalysisActivity;
import wt.fc.ReferenceFactory;
import wt.method.MethodContext;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.WTConnection;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NodeInstanceTaskService {
    public static void refreshTaskInfoNext(Map<String,Object> contextMap, DWGraphVo dWGraphVo, String changeNumber, String partId,
                                           DWNodeVo parentDwNodeVo, DWNodeVo dwNodeVo)
    {
        List<GWDealProductRecord> gwDealProductRecordList =
                ManufactoryDataService.getDealRecordDataList(partId, changeNumber,
                        dwNodeVo.getProperties().getTaskType(), dwNodeVo.getProperties().getTaskItemObjectNumber(),
                        dwNodeVo.getProperties().getConditionItemType());
        GWDealProductRecord gWDealProductRecord = null;
        if(gwDealProductRecordList.size()>0)
        {
            gWDealProductRecord = gwDealProductRecordList.get(0);
        }

        //重置数据
        setTaskInstanceInfo(dwNodeVo, null, null,
                null, null, null);

        DWPropertiesVo parentProperties = null;
        if(parentDwNodeVo!=null)
        {
            parentProperties = parentDwNodeVo.getProperties();
        }

        if(parentProperties!=null)
        {
            if(isEqualsFinished(parentProperties.getTaskState())==false)
            {
                setTaskInstanceInfo(dwNodeVo,null, DistributeConstants.TASK_STATE_NOT_START,
                        null, null, null);
                return;
            }
        }

        //取消任务名称前缀
//        if(gWDealProductRecord!=null)
//        {
//            dwNodeVo.getProperties().setTaskName(gWDealProductRecord.getDealType() + "-"
//                    + (dwNodeVo.getProperties().getTaskName()==null?"":dwNodeVo.getProperties().getTaskName()) );
//        }

        if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_在制品报废_在制品报废审批中" + DistributeConstants.ID_SPLIT_STR)) {
            dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "在制品"));
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord!=null)
            {
                dwNodeVo.getProperties().setTaskEndDate(convertDateToString(gWDealProductRecord.getCreateTimeStamp()));
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            }else  {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }

        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_在制品报废_在制品报废完成" + DistributeConstants.ID_SPLIT_STR)) {
            dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "在制品"));
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord!=null && StrUtil.isNotEmpty(gWDealProductRecord.getStatus()))
            {
                dwNodeVo.getProperties().setTaskEndDate(gWDealProductRecord.getFinishTime().toString());
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            }else  {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_在制品报废_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState())?DistributeConstants.TASK_STATE_FINISHED:DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_工艺升版_在制品工艺升版意见返回PDM" + DistributeConstants.ID_SPLIT_STR)) {
            dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "在制品"));
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord!=null)
            {
                dwNodeVo.getProperties().setTaskEndDate(convertDateToString(gWDealProductRecord.getCreateTimeStamp()));
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            }else  {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_工艺升版_在制品工艺升版完成" + DistributeConstants.ID_SPLIT_STR)) {
            dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "在制品"));
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord!=null && StrUtil.isNotEmpty(gWDealProductRecord.getStatus()))
            {
                dwNodeVo.getProperties().setTaskEndDate(gWDealProductRecord.getFinishTime().toString());
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            }else  {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_工艺升版_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState())?DistributeConstants.TASK_STATE_FINISHED:DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_已提前返修_在制品已提前返修意见返回PDM" + DistributeConstants.ID_SPLIT_STR)) {
            dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "在制品"));
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord!=null)
            {
                dwNodeVo.getProperties().setTaskEndDate(convertDateToString(gWDealProductRecord.getCreateTimeStamp()));
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            }else  {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_已提前返修_在制品已提前返修完成" + DistributeConstants.ID_SPLIT_STR)) {
            dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "在制品"));
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord!=null && StrUtil.isNotEmpty(gWDealProductRecord.getStatus()))
            {
                dwNodeVo.getProperties().setTaskEndDate(gWDealProductRecord.getFinishTime().toString());
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            }else  {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_已提前返修_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState())?DistributeConstants.TASK_STATE_FINISHED:DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_外协返修_外协返修" + DistributeConstants.ID_SPLIT_STR)) {
            dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "在制品"));
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord!=null && StrUtil.isNotEmpty(gWDealProductRecord.getStatus()))
            {
                dwNodeVo.getProperties().setTaskEndDate(gWDealProductRecord.getFinishTime().toString());
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            }else  {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_外协返修_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState())?DistributeConstants.TASK_STATE_FINISHED:DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_无影响_在制品无影响" + DistributeConstants.ID_SPLIT_STR)) {
            dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "在制品"));
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord!=null)
            {
                dwNodeVo.getProperties().setTaskEndDate(parentProperties.getTaskEndDate());
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            }else  {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_无影响_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState())?DistributeConstants.TASK_STATE_FINISHED:DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量=0_无影响_在制品无影响" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null, DistributeConstants.TASK_STATE_FINISHED, parentProperties.getTaskEndDate(), parentProperties.getTaskEndDate(), null);
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量=0_无影响_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState())?DistributeConstants.TASK_STATE_FINISHED:DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_开始" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null, DistributeConstants.TASK_STATE_FINISHED, null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_NC处理影响分析任务" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskBySystemReceivedImpactAnalysisTask(changeNumber, partId, dwNodeVo, gWDealProductRecord);
        } else if (dwNodeVo.getId().startsWith("id_整件外协是否有影响" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null, DistributeConstants.TASK_STATE_FINISHED, null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_已制品是否有影响" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null, DistributeConstants.TASK_STATE_FINISHED, null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_在制品是否有影响" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null, DistributeConstants.TASK_STATE_FINISHED, null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_整件外协是否有影响_外协数量>0_NC返回闭环结果" + DistributeConstants.ID_SPLIT_STR)) {
            Timestamp taskTime = DWSqlUtil.getSqlResultTimestamp(
                    "SELECT TASKTIME FROM AnalysisObjEntry WHERE datatype='zproduct'  " +
                            "AND ANALYSISNUMBER ='" + changeNumber + "'  AND veroid='"+ partId +"' ");

            NcResponseDataVo ncResponseDataVo = (NcResponseDataVo)contextMap.get(NcResponseDataVo.class.getName());
            NcZZPWXDetailEntryVo ncZZPWXDetailEntryVo = OtherSystemIntegrationHelper.getNcZZPWXDetailEntryVo(ncResponseDataVo);

            if(ncZZPWXDetailEntryVo!=null && StringUtils.isBlank(ncZZPWXDetailEntryVo.getZzpwxendtime())==false)
            {
                setTaskInstanceInfo(dwNodeVo, ncZZPWXDetailEntryVo.getWxname(), DistributeConstants.TASK_STATE_FINISHED,
                        convertDateToString(taskTime), ncZZPWXDetailEntryVo.getZzpwxendtime(), null);

            }else if(ncZZPWXDetailEntryVo!=null && StringUtils.isBlank(ncZZPWXDetailEntryVo.getZzpwxendtime()))
            {
                setTaskInstanceInfo(dwNodeVo, ncZZPWXDetailEntryVo.getWxname(), DistributeConstants.TASK_STATE_IN_WORK,
                        convertDateToString(taskTime), null, null);
            }else
            {
                setTaskInstanceInfo(dwNodeVo, null, DistributeConstants.TASK_STATE_IN_WORK,
                        convertDateToString(taskTime), null, null);
            }
        } else if (dwNodeVo.getId().startsWith("id_整件外协是否有影响_外协数量>0_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState())?DistributeConstants.TASK_STATE_FINISHED:DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品返修_已制品返修工艺编制中" + DistributeConstants.ID_SPLIT_STR)) {
            setWipReworkPdmRptTaskInfo(dwNodeVo, changeNumber, partId,"已制品返修");
        } else if (dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品返修_NC收到返修工艺任务" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, "系统自动", DistributeConstants.TASK_STATE_FINISHED,
                    parentProperties.getTaskEndDate(), parentProperties.getTaskEndDate(), null);
        } else if (dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品返修_已制品返修工艺计划下达" + DistributeConstants.ID_SPLIT_STR)) {
            NcResponseDataVo ncResponseDataVo = (NcResponseDataVo)contextMap.get(NcResponseDataVo.class.getName());
            NcYzpDetailEntryVo ncYzpDetailEntryVo = OtherSystemIntegrationHelper.getNcYzpDetailEntryVoByCode(ncResponseDataVo, dwNodeVo.getProperties().getTaskItemObjectNumber());
            if(ncYzpDetailEntryVo != null && "是".equals(ncYzpDetailEntryVo.getIsdmo())
                    && StringUtils.isBlank(ncYzpDetailEntryVo.getDmostatus()) == false) {
                setTaskInstanceInfo(dwNodeVo, ncYzpDetailEntryVo.getDmodept(), DistributeConstants.TASK_STATE_FINISHED,
                        parentProperties.getTaskEndDate(), ncYzpDetailEntryVo == null ? null : ncYzpDetailEntryVo.getDmoplanstarttime(), null);
            } else {
                setTaskInstanceInfo(dwNodeVo, ncYzpDetailEntryVo.getDmodept(), DistributeConstants.TASK_STATE_IN_WORK,
                        parentProperties.getTaskEndDate(), null, null);
            }
            if(ncYzpDetailEntryVo != null && "是".equals(ncYzpDetailEntryVo.getIsdmo())){
                dwNodeVo.getProperties().setTaskItemObjectNumber(ncYzpDetailEntryVo.getDmocode());
            }
        } else if (dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品返修_已制品返修执行中" + DistributeConstants.ID_SPLIT_STR)) {
            NcResponseDataVo ncResponseDataVo = (NcResponseDataVo)contextMap.get(NcResponseDataVo.class.getName());
            NcYzpDetailEntryVo ncYzpDetailEntryVo = OtherSystemIntegrationHelper.getNcYzpDetailEntryVoByCode(ncResponseDataVo, dwNodeVo.getProperties().getTaskItemObjectNumber());
            if(ncYzpDetailEntryVo != null && "是".equals(ncYzpDetailEntryVo.getIsdmo())
                    && ("完工".equals(ncYzpDetailEntryVo.getDmostatus()) || "入库".equals(ncYzpDetailEntryVo.getDmostatus()))) {
                setTaskInstanceInfo(dwNodeVo, ncYzpDetailEntryVo.getDmodept(), DistributeConstants.TASK_STATE_FINISHED,
                        parentProperties.getTaskEndDate(), ncYzpDetailEntryVo.getDmoplanendtime(), null);
            } else {
                setTaskInstanceInfo(dwNodeVo, ncYzpDetailEntryVo.getDmodept(), DistributeConstants.TASK_STATE_IN_WORK,
                        parentProperties.getTaskEndDate(), null, null);
            }
            if(ncYzpDetailEntryVo != null && "是".equals(ncYzpDetailEntryVo.getIsdmo())){
                dwNodeVo.getProperties().setTaskItemObjectNumber(ncYzpDetailEntryVo.getDmocode());
            }
        } else if (dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品返修_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState())?DistributeConstants.TASK_STATE_FINISHED:DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if (dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品已提前返修_已制品已提前返修意见返回PDM" + DistributeConstants.ID_SPLIT_STR)) {
            dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "已制品"));
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord!=null)
            {
                dwNodeVo.getProperties().setTaskEndDate(convertDateToString(gWDealProductRecord.getCreateTimeStamp()));
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            }else  {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }
        } else if(dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品已提前返修_已制品已提前返修完成" + DistributeConstants.ID_SPLIT_STR)) {
            NcResponseDataVo ncResponseDataVo = (NcResponseDataVo)contextMap.get(NcResponseDataVo.class.getName());
            NcYzpDetailEntryVo ncYzpDetailEntryVo = OtherSystemIntegrationHelper.getNcYzpDetailEntryVoByCode(ncResponseDataVo, dwNodeVo.getProperties().getTaskItemObjectNumber());
            if(ncYzpDetailEntryVo != null) {
                dwNodeVo.getProperties().setTaskOwner(ncYzpDetailEntryVo.getDmodept());
            } else {
                dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "已制品"));
            }
            dwNodeVo.getProperties().setTaskStartDate(parentProperties.getTaskEndDate());
            if(gWDealProductRecord != null && StrUtil.isNotEmpty(gWDealProductRecord.getStatus())) {
                dwNodeVo.getProperties().setTaskEndDate(gWDealProductRecord.getFinishTime().toString());
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            } else {
                dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
            }
        } else if(dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品已提前返修_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState()) ? DistributeConstants.TASK_STATE_FINISHED : DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if(dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品报废_已制品报废中" + DistributeConstants.ID_SPLIT_STR)) {
            NcResponseDataVo ncResponseDataVo = (NcResponseDataVo) contextMap.get(NcResponseDataVo.class.getName());
            NcYzpDetailEntryVo ncYzpDetailEntryVo = OtherSystemIntegrationHelper.getNcYzpDetailEntryVoByCode(ncResponseDataVo, dwNodeVo.getProperties().getTaskItemObjectNumber());
            if(ncYzpDetailEntryVo != null && "是".equals(ncYzpDetailEntryVo.getIsscrapform())) {
                setTaskInstanceInfo(dwNodeVo, ncResponseDataVo == null ? null : ncResponseDataVo.getDispatcher(),
                        DistributeConstants.TASK_STATE_FINISHED,
                        gWDealProductRecord == null ? null : convertDateToString(gWDealProductRecord.getCreateTimeStamp()),
                        ncYzpDetailEntryVo == null ? null : ncYzpDetailEntryVo.getDmoplanstarttime(), null);
            } else {
                setTaskInstanceInfo(dwNodeVo, ncResponseDataVo == null ? null : ncResponseDataVo.getDispatcher(), DistributeConstants.TASK_STATE_IN_WORK,
                        gWDealProductRecord == null ? null : convertDateToString(gWDealProductRecord.getCreateTimeStamp()),
                        null, null);
            }
            //dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "已制品"));
        } else if(dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品报废_已制品报废完成" + DistributeConstants.ID_SPLIT_STR)) {
            NcResponseDataVo ncResponseDataVo = (NcResponseDataVo) contextMap.get(NcResponseDataVo.class.getName());
            NcYzpDetailEntryVo ncYzpDetailEntryVo = OtherSystemIntegrationHelper.getNcYzpDetailEntryVoByCode(ncResponseDataVo, dwNodeVo.getProperties().getTaskItemObjectNumber());
            if(ncYzpDetailEntryVo != null && "签字".equals(ncYzpDetailEntryVo.getScrapstatus())) {
                setTaskInstanceInfo(dwNodeVo, ncYzpDetailEntryVo.getKgy(),
                        DistributeConstants.TASK_STATE_FINISHED,
                        parentProperties.getTaskEndDate(), ncYzpDetailEntryVo == null ? null : ncYzpDetailEntryVo.getDmoplanendtime(), null);
            } else {
                setTaskInstanceInfo(dwNodeVo, ncYzpDetailEntryVo == null ? null : ncYzpDetailEntryVo.getKgy(), DistributeConstants.TASK_STATE_IN_WORK,
                        parentProperties.getTaskEndDate(), null, null);
            }
        } else if(dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品报废_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState()) ? DistributeConstants.TASK_STATE_FINISHED : DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if(dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品无影响_已制品无影响" + DistributeConstants.ID_SPLIT_STR)) {
            if(gWDealProductRecord != null) {
                String state = DistributeConstants.TASK_STATE_FINISHED;
                if(StrUtil.isEmpty(gWDealProductRecord.getStatus())) {
                    state = DistributeConstants.TASK_STATE_IN_WORK;
                }
                setTaskInstanceInfo(dwNodeVo, getPlanningDispatcherName(changeNumber, partId, "已制品"), state,
                        convertDateToString(gWDealProductRecord.getCreateTimeStamp()), gWDealProductRecord.getFinishTime(), null);
            } else {
                setTaskInstanceInfo(dwNodeVo, getPlanningDispatcherName(changeNumber, partId, "已制品"),
                        DistributeConstants.TASK_STATE_FINISHED,
                        null, null, null);
            }
        } else if(dwNodeVo.getId().startsWith("id_已制品是否有影响_已制品无影响_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState()) ? DistributeConstants.TASK_STATE_FINISHED : DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if(dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_NC推送MES在制品影响分析任务" + DistributeConstants.ID_SPLIT_STR)) {

            setTaskInstanceInfo(dwNodeVo, getPlanningDispatcherName(changeNumber, partId, "已制品"),
                    DistributeConstants.TASK_STATE_FINISHED, null, null, null);

            WorkItem workItem = getSystemReceivedImpactAnalysisTask(changeNumber);
            if(workItem != null) {
                WfActivity wfactivity = (WfActivity) workItem.getSource().getObject();
                dwNodeVo.getProperties().setTaskStartDate(convertDateToString(wfactivity.getCreateTimestamp()));
            }

            NcResponseDataVo ncResponseDataVo = (NcResponseDataVo) contextMap.get(NcResponseDataVo.class.getName());
            NcZZPZZDetailEntryVo ncZZPZZDetailEntryVo = OtherSystemIntegrationHelper.getNcZZPZZDetailEntryVo(ncResponseDataVo);

            if(ncZZPZZDetailEntryVo != null) {
                dwNodeVo.getProperties().setTaskEndDate(ncZZPZZDetailEntryVo.getZzpzzendtime());
            }

        } else if(dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_工艺员指定处理意见" + DistributeConstants.ID_SPLIT_STR)) {
            Iterator<GWDealProductRecord> iteratorTemp = gwDealProductRecordList.iterator();
            GWDealProductRecord tempGWDealProductRecord = null;
            while(iteratorTemp.hasNext()) {
                GWDealProductRecord gwDealProductRecord = iteratorTemp.next();
                if(gwDealProductRecord.getKeyId().toString().endsWith("MES")) {
                    tempGWDealProductRecord = gwDealProductRecord;
                    break;
                }
            }

            if(tempGWDealProductRecord != null) {
                setTaskInstanceInfo(dwNodeVo, getPlanningDispatcherName(changeNumber, partId, "在制品"),
                        DistributeConstants.TASK_STATE_FINISHED,
                        parentProperties.getTaskEndDate(), convertDateToString(tempGWDealProductRecord.getCreateTimeStamp()), null);
            } else {
                setTaskInstanceInfo(dwNodeVo, getPlanningDispatcherName(changeNumber, partId, "在制品"),
                        DistributeConstants.TASK_STATE_IN_WORK,
                        parentProperties.getTaskEndDate(), null, null);
            }
        } else if(dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修工艺编制中" + DistributeConstants.ID_SPLIT_STR)) {
            setWipReworkPdmRptTaskInfo(dwNodeVo, changeNumber, partId, "在制品返修");
        } else if(dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_在制品返修_MES收到返修工艺任务" + DistributeConstants.ID_SPLIT_STR)) {
            if(gWDealProductRecord != null) {
                setTaskInstanceInfo(dwNodeVo, gWDealProductRecord.getResponser(),
                        DistributeConstants.TASK_STATE_FINISHED,
                        parentProperties.getTaskEndDate(), parentProperties.getTaskEndDate(), null);
            } else {
                setTaskInstanceInfo(dwNodeVo, null,
                        DistributeConstants.TASK_STATE_FINISHED,
                        parentProperties.getTaskEndDate(), parentProperties.getTaskEndDate(), null);
            }
        } else if(dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修工艺跳转" + DistributeConstants.ID_SPLIT_STR)) {
            MesStatusResultVo mesStatusResultVo = (MesStatusResultVo) contextMap.get(MesStatusResultVo.class.getName());
            MesDataEntryVo mesDataEntryVo = OtherSystemIntegrationHelper.getMesDataEntryVoByCode(mesStatusResultVo, dwNodeVo.getProperties().getTaskItemObjectNumber());
            if(mesStatusResultVo != null && mesDataEntryVo != null
                    && ("已跳转".equals(mesDataEntryVo.getCONTAINERSTATUS()) || "已完成".equals(mesDataEntryVo.getCONTAINERSTATUS()))) {
                setTaskInstanceInfo(dwNodeVo, mesDataEntryVo.getTRXUSER(),
                        DistributeConstants.TASK_STATE_FINISHED,
                        parentProperties.getTaskStartDate(), mesDataEntryVo.getTRXTIME(), null);
            } else {
                setTaskInstanceInfo(dwNodeVo, mesDataEntryVo.getTRXUSER(), DistributeConstants.TASK_STATE_IN_WORK, parentProperties.getTaskStartDate(), null, null);
            }
        } else if(dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_在制品返修_在制品返修返修执行中" + DistributeConstants.ID_SPLIT_STR)) {
            MesStatusResultVo mesStatusResultVo = (MesStatusResultVo) contextMap.get(MesStatusResultVo.class.getName());
            MesDataEntryVo mesDataEntryVo = OtherSystemIntegrationHelper.getMesDataEntryVoByCode(mesStatusResultVo, dwNodeVo.getProperties().getTaskItemObjectNumber());
            if(mesStatusResultVo != null && mesDataEntryVo != null && "已完成".equals(mesDataEntryVo.getCONTAINERSTATUS())) {
                setTaskInstanceInfo(dwNodeVo, mesDataEntryVo.getTRXUSER(),
                        DistributeConstants.TASK_STATE_FINISHED,
                        parentProperties.getTaskEndDate(), mesDataEntryVo.getTRXTIME(), null); //todo 完成时间:MES返回PDM闭环结果时间(现有接口)
            } else {
                setTaskInstanceInfo(dwNodeVo, mesDataEntryVo.getTRXUSER(), DistributeConstants.TASK_STATE_IN_WORK, parentProperties.getTaskEndDate(), null, null);
            }
        } else if(dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_在制品返修_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState()) ? DistributeConstants.TASK_STATE_FINISHED : DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else if(dwNodeVo.getId().startsWith("id_整件外协是否有影响_外协数量=0_结束" + DistributeConstants.ID_SPLIT_STR)) {
            setTaskInstanceInfo(dwNodeVo, null,
                    isEqualsFinished(parentProperties.getTaskState()) ? DistributeConstants.TASK_STATE_FINISHED : DistributeConstants.TASK_STATE_NOT_START,
                    null, null, null);
        } else {
            throw new RuntimeException("未知id:" + dwNodeVo.getId());
        }
        //子节点信息处理
        NodeHierarchyService nodeHierarchyService = new NodeHierarchyService();
        Map<String, List<DWNodeVo>> parentToChildrenMap = nodeHierarchyService.getParentToChildrenMap(dWGraphVo);
        List<DWNodeVo> childToChildrenList = parentToChildrenMap.get(dwNodeVo.getId());
        if (childToChildrenList != null) {
            for (DWNodeVo childDWNodeVo : childToChildrenList) {
                refreshTaskInfoNext(contextMap, dWGraphVo, changeNumber, partId, dwNodeVo, childDWNodeVo);
            }
        }
    }

    private static void setTaskInstanceInfo(DWNodeVo dwNodeVo, String taskOwner, String taskState, String taskStartDate, String taskEndDate, String taskItemObjectNumber)
    {
        dwNodeVo.getProperties().setTaskOwner(taskOwner);
        dwNodeVo.getProperties().setTaskState(taskState);
        dwNodeVo.getProperties().setTaskStartDate(taskStartDate);
        dwNodeVo.getProperties().setTaskEndDate(taskEndDate);
        //dwNodeVo.getProperties().setTaskItemObjectNumber(taskItemObjectNumber);

        DWStyleVo dwStyleVo = dwNodeVo.getProperties().getStyle();
        if(dwStyleVo==null)
        {
            dwStyleVo = new DWStyleVo();
        }

        dwNodeVo.getProperties().setStyle(dwStyleVo);
    }

    /**
     * 有记录就算已完成任务
     * @param dwNodeVo
     */
    private static void doSetTaskInfoWhenHasRecord(DWPropertiesVo parentProperties, GWDealProductRecord gWDealProductRecord , DWNodeVo dwNodeVo)
    {
        if(isEqualsFinished(parentProperties.getTaskState()) )
        {
            if(gWDealProductRecord!=null) //有记录就算完成
            {
                setTaskInstanceInfo(dwNodeVo, gWDealProductRecord.getResponser(),
                        DistributeConstants.TASK_STATE_FINISHED,
                        parentProperties.getTaskEndDate(), gWDealProductRecord.getFinishTime(), null);
            }else
            {
                setTaskInstanceInfo(dwNodeVo, null,
                        DistributeConstants.TASK_STATE_IN_WORK,
                        parentProperties.getTaskEndDate(), null, null);
            }

        }else
        {
            setTaskInstanceInfo(dwNodeVo, null, DistributeConstants.TASK_STATE_NOT_START,
                    parentProperties.getTaskEndDate(), null, null);
        }
    }

    /**
     *根据父节点状态设置任务信息
     */
    public static void doSetTaskInfoHasRecordStatus(DWPropertiesVo parentProperties, GWDealProductRecord gWDealProductRecord , DWNodeVo dwNodeVo)
    {
        if(isEqualsFinished(parentProperties.getTaskState()) && gWDealProductRecord!=null)
        {
            if(isEqualsFinished(gWDealProductRecord.getStatus())
                    || DistributeConstants.TASK_STATE_CLOSED_LOOP.equals(gWDealProductRecord.getStatus()))
            {
                setTaskInstanceInfo(dwNodeVo, gWDealProductRecord.getResponser(),
                        DistributeConstants.TASK_STATE_FINISHED,
                        convertDateToString(gWDealProductRecord.getCreateTimeStamp()), gWDealProductRecord.getFinishTime(), null);
            }else
            {
                if(gWDealProductRecord!=null)
                {
                    setTaskInstanceInfo(dwNodeVo, gWDealProductRecord.getResponser(),
                            DistributeConstants.TASK_STATE_IN_WORK,
                            convertDateToString(gWDealProductRecord.getCreateTimeStamp()), null, null);
                }else
                {
                    setTaskInstanceInfo(dwNodeVo, null,
                            DistributeConstants.TASK_STATE_IN_WORK,
                            null, null, null);
                }

            }

        }else
        {
            setTaskInstanceInfo(dwNodeVo, null, DistributeConstants.TASK_STATE_NOT_START, null, null, null);
        }
    }

    /**
     * 设置 在制品返修/已制品返修 返修工艺编制中 的任务信息
     */
    private static void setWipReworkPdmRptTaskInfo(DWNodeVo currentDwNodeVo, String changeNumber, String partId, String routeType) {

        DWPropertiesVo currentDwPropertiesVo = currentDwNodeVo.getProperties();
        String cardNumber = currentDwPropertiesVo.getTaskItemObjectNumber();
        if (StringUtils.isBlank(cardNumber)) {
            throw new RuntimeException("未能获取路卡号！" + currentDwNodeVo.getId());
        }
        String sql = "SELECT PROCESSTASKID, IDA2A2, EXECUTORROLE, TASKITEMSTATE, CREATESTAMPA2, OWNER FROM PROCESSTASKITEM " +
                " WHERE IDA2A2 IN (SELECT ida3a4 FROM STRINGVALUE" +
                " WHERE CLASSNAMEKEYA4='ext.casc.process.ProcessTaskItem' " +
                " and value='" + changeNumber + "') and tasktype='临时工艺任务'" ;

        if ("已制品返修".equals (routeType))
        {
            if (StringUtils.isBlank(cardNumber)==false)
            {
                if(cardNumber.contains("_")) {
                    cardNumber = cardNumber.split("_")[0];
                }
            }
            sql = sql + "AND RENWUYAOQIU LIKE '%库存批次号：" + cardNumber + "%'";
        } else if ("在制品返修".equals (routeType))
        {
            sql = sql + "AND RENWUYAOQIU LIKE '%路卡号：" + cardNumber + "%'";
        } else
        {
            throw new RuntimeException ("未知 routeType:" + routeType);
        }
        sql = sql + "and RENWUYIJU='" + routeType + "'";

        //返修工艺编制责任人都取工艺员
        currentDwPropertiesVo.setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "在制品"));
        currentDwPropertiesVo.setTaskState(DistributeConstants.TASK_STATE_IN_WORK);
        currentDwPropertiesVo.setTaskStartDate(null);
        currentDwPropertiesVo.setTaskEndDate(null);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WTConnection wtconnection = null;
        try {
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            pstmt = wtconnection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                currentDwPropertiesVo.setTaskStartDate(rs.getTimestamp("CREATESTAMPA2")==null?null:convertDateToString(rs.getTimestamp("CREATESTAMPA2")));

                String processtaskid = rs.getString("PROCESSTASKID");

                List<Map<String, Object>> listMapTemp = DWSqlUtil.getSqlResultListMap("SELECT IDA3A7, TASKSTATE, CREATESTAMPA2, UPDATESTAMPA2 FROM PROCESSTASK WHERE ida2a2='" + processtaskid + "'");

                if(listMapTemp.size()>0)
                {
                    if (isEqualsFinished(listMapTemp.get(0).get("TASKSTATE")==null?null:listMapTemp.get(0).get("TASKSTATE").toString()))
                    {
                        currentDwPropertiesVo.setTaskState(DistributeConstants.TASK_STATE_FINISHED);
                    }
                    currentDwPropertiesVo.setTaskEndDate(
                            listMapTemp.get(0).get("UPDATESTAMPA2")==null?null:convertDateToString((Timestamp) listMapTemp.get(0).get("UPDATESTAMPA2")));
                    currentDwPropertiesVo.setTaskStartDate(
                            listMapTemp.get(0).get("CREATESTAMPA2")==null?null:convertDateToString((Timestamp) listMapTemp.get(0).get("CREATESTAMPA2")));
                }

            }
            rs.close();
            pstmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isEqualsFinished(String strState)
    {
        if (DistributeConstants.TASK_STATE_FINISHED.equals(strState)
                || DistributeConstants.TASK_STATE_CLOSED_LOOP.equals(strState)
                || "已完工".equals(strState) )
        {
            return true;
        }else
        {
            return false;
        }
    }

    /**
     * 获取计调人
     * @param changeNumber
     * @param partId
     * @param dataType
     * @return
     */
    public static String getPlanningDispatcherName(String changeNumber, String partId, String dataType)
    {
        ReferenceFactory rf = new ReferenceFactory();

        String tempDataType = "";
        if("已制品".equals(dataType))
        {
            tempDataType = "yproduct";
        }else if("在制品".equals(dataType))
        {
            tempDataType = "zproduct";
        }else
        {
            throw new RuntimeException("未知 dataType:" + dataType);
        }

        String wtuserOid = DWSqlUtil.getSqlResultString("SELECT RESPONSER FROM AnalysisObjEntry WHERE datatype='" + tempDataType + "' " +
                " AND ANALYSISNUMBER ='" + changeNumber + "' " +
                " AND veroid='" + partId + "' ");
        if(StringUtils.isBlank(wtuserOid)==false)
        {
            try {
                WTUser wtuser = (WTUser) rf.getReference(wtuserOid).getObject();
                return wtuser.getFullName();
            } catch (WTException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

    private static WorkItem getSystemReceivedImpactAnalysisTask(String changeNumber)
    {
        ReferenceFactory rf = new ReferenceFactory();
        String branchId = DWSqlUtil.getSqlResultString("SELECT BRANCHIDITERATIONINFO FROM WTAnalysisActivity " +
                " WHERE IDA3MASTERREFERENCE = " +
                " (SELECT ida2a2 FROM WTANALYSISACTIVITYMASTER WHERE WTCHGANALYSISNUMBER ='" + changeNumber + "')");
        String ida2a2 = DWSqlUtil.getSqlResultString(
                "SELECT IDA2A2 FROM WorkItem WHERE CLASSNAMEKEYB4='VR:wt.change2.WTAnalysisActivity:" + branchId + "'" +
                        "  and ida3a4 IN (  SELECT ida2a2 FROM WfAssignedActivity WHERE name='更改影响分析' )");
        try {
            WorkItem workItem = (WorkItem) rf.getReference("OR:wt.workflow.work.WorkItem:" + ida2a2).getObject();
            return workItem;
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    private static void setTaskBySystemReceivedImpactAnalysisTask(String changeNumber, String partId, DWNodeVo dwNodeVo, GWDealProductRecord gWDealProductRecord)
    {
        dwNodeVo.getProperties().setTaskOwner(getPlanningDispatcherName(changeNumber, partId, "已制品"));
        WorkItem workItem = getSystemReceivedImpactAnalysisTask(changeNumber);
        if(workItem!=null)
        {
            WfActivity wfactivity = (WfActivity) workItem.getSource().getObject();
            if(wfactivity!=null)
            {
                dwNodeVo.getProperties().setTaskStartDate(convertDateToString(wfactivity.getEndTime()));
            }
        }

        if(gWDealProductRecord!=null)
        {
            dwNodeVo.getProperties().setTaskState(DistributeConstants.TASK_STATE_FINISHED);
            dwNodeVo.getProperties().setTaskEndDate(convertDateToString(gWDealProductRecord.getCreateTimeStamp()));
        }
    }

    public static String convertDateToString(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        DateTime dateTime = new DateTime(timestamp);
        return DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static void appendExtBusinessData(Map<String,Object> contextMap, DWGraphVo dWGraphVo, String changeNumber, String partId)
    {
        List<DWNodeVo> nodeList = dWGraphVo.getNodes();
        ReferenceFactory rf = new ReferenceFactory();
        try {
            WTPart part = (WTPart) rf.getReference("VR:" + partId).getObject();
            WTAnalysisActivity wtAnalysisActivity = AnalysisUtil.getWTAnalysisActivityByNumber(changeNumber);
            String wtAnalysisActivityOid = rf.getReferenceString(wtAnalysisActivity);

            GenerateRelatedAnalysisJson GenerateRelatedAnalysisJson = new GenerateRelatedAnalysisJson(wtAnalysisActivityOid);

            NodeHierarchyService nodeHierarchyService = new NodeHierarchyService();
            Map<String, List<DWNodeVo>> parentToChildrenMap = nodeHierarchyService.getParentToChildrenMap(dWGraphVo);

            for (DWNodeVo dwNodeVo : nodeList) {
                List<GWDealProductRecord> gwDealProductRecordList =
                        ManufactoryDataService.getDealRecordDataList(partId, changeNumber,
                                dwNodeVo.getProperties().getTaskType(), dwNodeVo.getProperties().getTaskItemObjectNumber(),
                                dwNodeVo.getProperties().getConditionItemType());
                GWDealProductRecord gWDealProductRecord = null;
                if(gwDealProductRecordList.size()>0)
                {
                    gWDealProductRecord = gwDealProductRecordList.get(0);
                }

                String taskType = dwNodeVo.getProperties().getTaskType();
                if("在制品".equals(taskType))
                {
                    if("在制品返修工艺编制中".equals(dwNodeVo.getText()) || "在制品报废审批中".equals(dwNodeVo.getText())==false
                            || "在制品工艺升版".equals(dwNodeVo.getText()) || "外协返修".equals(dwNodeVo.getText())
                            || "在制品无影响".equals(dwNodeVo.getText()) || "在制品已提前返修".equals(dwNodeVo.getText()))
                    {
                        if(gWDealProductRecord!=null)
                        {
                            dwNodeVo.getProperties().setQuantity(Integer.toString(gWDealProductRecord.getCount()));
                            dwNodeVo.getProperties().setActualQuantity(Integer.toString(gWDealProductRecord.getRepairCount()));
                            dwNodeVo.getProperties().setExtNumber(gWDealProductRecord.getCode());
                        }

                        String repairprocessNumber = GenerateRelatedAnalysisJson.getDataValue("zrepairtec", null, part, partId, "product");
                        repairprocessNumber = trimAtCharBefore(repairprocessNumber);
                        dwNodeVo.getProperties().setRepairProcessNumber(repairprocessNumber);

                        setBranchFlowCurrentNode(dwNodeVo, parentToChildrenMap);
                    }

                    if("整件外协执行中".equals(dwNodeVo.getText()) ) //整件外协
                    {
                        if(gWDealProductRecord!=null)
                        {
                            dwNodeVo.getProperties().setQuantity(Integer.toString(gWDealProductRecord.getCount()));
                            dwNodeVo.getProperties().setActualQuantity(Integer.toString(gWDealProductRecord.getRepairCount()));
                            dwNodeVo.getProperties().setExtNumber(gWDealProductRecord.getCode());
                        }
                        dwNodeVo.getProperties().setRepairProcessNumber(trimAtCharBefore(dwNodeVo.getProperties().getTaskItemObjectNumber()));

                        setBranchFlowCurrentNode(dwNodeVo, parentToChildrenMap);
                    }

                }else if("已制品".equals(taskType))
                {
                    if("已制品返修工艺编制中".equals(dwNodeVo.getText()) || "已制品报废中".equals(dwNodeVo.getText())
                            || "已制品无影响".equals(dwNodeVo.getText()) || "已制品已提前返修".equals(dwNodeVo.getText()))
                    {
                        if(gWDealProductRecord!=null)
                        {
                            dwNodeVo.getProperties().setQuantity(Integer.toString(gWDealProductRecord.getCount()));
                            dwNodeVo.getProperties().setActualQuantity(Integer.toString(gWDealProductRecord.getRepairCount()));
                            dwNodeVo.getProperties().setExtNumber(gWDealProductRecord.getCode());
                        }

                        String repairprocessNumber = GenerateRelatedAnalysisJson.getDataValue("yrepairtec", null, part, partId, "product");
                        repairprocessNumber = trimAtCharBefore(repairprocessNumber);
                        dwNodeVo.getProperties().setRepairProcessNumber(repairprocessNumber);

                        setBranchFlowCurrentNode(dwNodeVo, parentToChildrenMap);
                    }
                }


            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取分支进行中的节点，或最后一个节点
     * @param dwNodeVo
     * @param parentToChildrenMap
     * @return
     */
    private static DWNodeVo getBranchFlowCurrentNode(DWNodeVo dwNodeVo, Map<String, List<DWNodeVo>> parentToChildrenMap)
    {
        if(DistributeConstants.TASK_STATE_NOT_START.equals(dwNodeVo.getProperties().getTaskState())
            || DistributeConstants.TASK_STATE_IN_WORK.equals(dwNodeVo.getProperties().getTaskState()) )
        {
            return dwNodeVo;
        }else if(DistributeConstants.TASK_STATE_FINISHED.equals(dwNodeVo.getProperties().getTaskState())
                || DistributeConstants.TASK_STATE_CLOSED_LOOP.equals(dwNodeVo.getProperties().getTaskState()) )
        {
            List<DWNodeVo> childToChildrenList = parentToChildrenMap.get(dwNodeVo.getId());
            if(childToChildrenList==null || childToChildrenList.size()==0)
            {
                return dwNodeVo;
            }else if(childToChildrenList.size()==1)
            {
                return getBranchFlowCurrentNode(childToChildrenList.get(0), parentToChildrenMap);
            }
            else
            {
                //throw new RuntimeException("流程节点有多个分支，不支持！");
                //do nothing
            }

        }
        return null;
    }

    public static DWNodeVo getBranchFlowKeyNode(DWGraphVo dWGraphVo, String taskItemObjectNumber, String dealType, String taskType)
    {
        System.out.println("getBranchFlowKeyNode start:" + taskItemObjectNumber + " / " + dealType + " / " + taskType);

        if(StringUtils.isBlank(dealType))
        {
            return null;
        }

        if(StringUtils.isBlank(taskType))
        {
            return null;
        }

        String conditionItemTypeTemp = null;
        if(dealType.contains("ZJWX"))
        {
            conditionItemTypeTemp = "整件外协";
        }else if(dealType.contains("返修") || dealType.contains("已提前返修"))
        {
            conditionItemTypeTemp = "返修";
        }else if(dealType.contains("报废") || dealType.contains("已提前报废"))
        {
            conditionItemTypeTemp = "报废";
        }else{
            conditionItemTypeTemp = dealType;
        }

        System.out.println("getBranchFlowKeyNode conditionItemTypeTemp:" + conditionItemTypeTemp);

        for(DWNodeVo dwNodeVo: dWGraphVo.getNodes())
        {
            DWPropertiesVo dwPropertiesVo = dwNodeVo.getProperties();
            if(dwPropertiesVo==null)
            {
                continue;
            }
            String taskTypeTemp =  org.apache.commons.lang.StringUtils.trimToEmpty(dwPropertiesVo.getTaskType());
            if(taskTypeTemp==null)
            {
                continue;
            }

            if("整件外协".equals(taskType))
            {
                if(conditionItemTypeTemp==null || conditionItemTypeTemp.equals(dwPropertiesVo.getConditionItemType())==false)
                {
                    continue;
                }
            }

            if(taskItemObjectNumber!=null && taskItemObjectNumber.equals(dwPropertiesVo.getTaskItemObjectNumber())==false)
            {
                continue;
            }

            if("在制品".equals(taskType))
            {
                if("在制品返修工艺编制中".equals(dwNodeVo.getText())==false && "在制品报废审批中".equals(dwNodeVo.getText())==false
                        && "在制品工艺升版".equals(dwNodeVo.getText())==false && "外协返修".equals(dwNodeVo.getText())==false
                        && "在制品无影响".equals(dwNodeVo.getText())==false && "在制品已提前返修".equals(dwNodeVo.getText())==false)
                {
                    continue;
                }

                if("在制品".equals(dwPropertiesVo.getTaskType())==false)
                {
                    continue;
                }
            }else if("已制品".equals(taskType))
            {
                if("已制品返修工艺编制中".equals(dwNodeVo.getText())==false && "已制品报废中".equals(dwNodeVo.getText())==false
                        && "已制品无影响".equals(dwNodeVo.getText())==false && "已制品已提前返修".equals(dwNodeVo.getText())==false)
                {
                    continue;
                }

                if("已制品".equals(dwPropertiesVo.getTaskType())==false)
                {
                    continue;
                }
            }else if("整件外协".equals(taskType))
            {
                if("整件外协执行中".equals(dwNodeVo.getText())==false )
                {
                    continue;
                }

                if("在制品".equals(dwPropertiesVo.getTaskType())==false)
                {
                    continue;
                }
            }else
            {
                continue;
            }
            System.out.println("getBranchFlowKeyNode end:" + dwNodeVo.getText());
            return dwNodeVo;
        }

        return null;
    }

    private static void setBranchFlowCurrentNode(DWNodeVo dwNodeVo, Map<String, List<DWNodeVo>> parentToChildrenMap)
    {
        DWNodeVo branchFlowCurrentNode = getBranchFlowCurrentNode(dwNodeVo, parentToChildrenMap);
        if(branchFlowCurrentNode!=null)
        {
            dwNodeVo.getProperties().setBranchFlowCurrentNodeName(branchFlowCurrentNode.getProperties().getTaskName());
            dwNodeVo.getProperties().setBranchFlowCurrentNodeState(branchFlowCurrentNode.getProperties().getTaskState());
            dwNodeVo.getProperties().setBranchFlowCurrentNodeOwner(branchFlowCurrentNode.getProperties().getTaskOwner());
            dwNodeVo.getProperties().setBranchFlowCurrentNodeStartDate(branchFlowCurrentNode.getProperties().getTaskStartDate());
            dwNodeVo.getProperties().setBranchFlowCurrentNodeEndDate(branchFlowCurrentNode.getProperties().getTaskEndDate());
            dwNodeVo.getProperties().setBranchFlowCurrentNodeHead(branchFlowCurrentNode.getProperties().getHead());
            dwNodeVo.getProperties().setBranchFlowCurrentCard(branchFlowCurrentNode.getProperties().getTaskItemObjectNumber());

            if(org.apache.commons.lang.StringUtils.isBlank(branchFlowCurrentNode.getProperties().getBranchFlowCurrentNodeName()) && "进行中".equals(branchFlowCurrentNode.getProperties().getBranchFlowCurrentNodeState()))
            {
                System.out.println("debug warn 进行中，却没有任务名称的异常:" + JsonVoConverter.serialize(branchFlowCurrentNode));
            }else if(org.apache.commons.lang.StringUtils.isBlank(branchFlowCurrentNode.getProperties().getBranchFlowCurrentNodeName())==false && org.apache.commons.lang.StringUtils.isBlank(branchFlowCurrentNode.getProperties().getBranchFlowCurrentNodeOwner()) )
            {
                System.out.println("debug warn 有任务名称确没有负责人的异常:" + JsonVoConverter.serialize(branchFlowCurrentNode));
            }
        }
    }

    private static String trimAtCharBefore(String str)
    {
        if(StringUtils.isBlank(str))
        {
            return str;
        }

        if(StringUtils.contains(str, "@"))
        {
            return StringUtils.substringBefore(str, "@");
        }else {
            return str;
        }

    }


}