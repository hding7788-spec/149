package ext.casc.distribute.service;

import cn.hutool.core.util.StrUtil;
import ext.casc.analysisActivity.bean.GWDealProductRecord;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.distribute.contant.DistributeConstants;
import ext.casc.distribute.integration.OtherSystemIntegrationHelper;
import ext.casc.distribute.util.DWSqlUtil;
import ext.casc.distribute.vo.DWGraphVo;
import ext.casc.distribute.vo.DWNodeVo;
import ext.casc.distribute.vo.NcResponseDataVo;
import ext.casc.distribute.vo.NcZZPZZDetailEntryVo;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;

import org.apache.commons.lang3.StringUtils;
import wt.session.SessionServerHelper;
import wt.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManufactoryDataService {

    public static void refreshTaskInfo(Map<String,Object> contextMap, DWGraphVo dWGraphVo, String changeNumber, String partId, DWNodeVo parentDwNodeVo, DWNodeVo dwNodeVo)
    {
        List<GWDealProductRecord> gwDealProductRecordList00 =
                getDealRecordDataList(partId, changeNumber,
                        dwNodeVo.getProperties().getTaskType(), null, null);

        List<GWDealProductRecord> gwDealProductRecordList =
                getDealRecordDataList(partId, changeNumber,
                        dwNodeVo.getProperties().getTaskType(), null, dwNodeVo.getProperties().getConditionItemType());

        NodeHierarchyService nodeHierarchyService = new NodeHierarchyService();

        //是否有实例分支，或者需要多实例分支
        NodeOperationService nodeOperationService = new NodeOperationService();
        boolean deleteTempFlag = false;
        if(dwNodeVo.getId().startsWith("id_在制品是否有影响_在制品数量>0_NC推送MES在制品影响分析任务" + DistributeConstants.ID_SPLIT_STR))
        {
            NcResponseDataVo ncResponseDataVo = (NcResponseDataVo)contextMap.get(NcResponseDataVo.class.getName());
            NcZZPZZDetailEntryVo ncZZPZZDetailEntryVo = OtherSystemIntegrationHelper.getNcZZPZZDetailEntryVo(ncResponseDataVo);

            if(ncZZPZZDetailEntryVo==null)
            {
                deleteTempFlag = true;
            }
        }else if("0_1".equals(dwNodeVo.getProperties().getInstanceCountRange()))
        {
            if("整件外协".equals(dwNodeVo.getProperties().getConditionItemType()))
            {
                int zjwxCount = DWSqlUtil.getSqlResultInt(
                        "SELECT ZJWXCOUNT FROM AnalysisObjEntry WHERE datatype='zproduct'  " +
                                "AND ANALYSISNUMBER ='" + changeNumber + "'  AND veroid='"+ partId +"' ");
                if(zjwxCount==0 && ">0".equals(dwNodeVo.getProperties().getConditionCount()))
                {
                    deleteTempFlag = true;
                }else if(zjwxCount>0 && "=0".equals(dwNodeVo.getProperties().getConditionCount()))
                {
                    deleteTempFlag = true;
                }
            }else
            {
                //没有条目（没有其它系统返回数据）
                if(gwDealProductRecordList00.size()==0)
                {
                    deleteTempFlag = true;
                }else if(gwDealProductRecordList.size()==0 && ">0".equals(dwNodeVo.getProperties().getConditionCount()))
                {
                    deleteTempFlag = true;
                }else if(gwDealProductRecordList.size()>0)
                {
                    int allCount = 0;
                    for(GWDealProductRecord gwDealProductRecord : gwDealProductRecordList) {
                        allCount += gwDealProductRecord.getCount();
                    }
                    if(allCount==0 && ">0".equals(dwNodeVo.getProperties().getConditionCount()))
                    {
                        deleteTempFlag = true;
                    }else if(allCount>0 && "=0".equals(dwNodeVo.getProperties().getConditionCount()))
                    {
                        deleteTempFlag = true;
                    }
                }
            }

        }else if("0_n".equals(dwNodeVo.getProperties().getInstanceCountRange()))
        {
            if(gwDealProductRecordList.size()==0)
            {
                deleteTempFlag = true;
            }else if(gwDealProductRecordList.size()==1)
            {
                //do nothing
            }else
            {
                for(int i=1;i<gwDealProductRecordList.size();i++)
                {
                    //复制节点
                    DWGraphVo tempDWGraphVo = nodeOperationService.copyNodeAndChildren(dWGraphVo, parentDwNodeVo, dwNodeVo);
                    dWGraphVo.getNodes().addAll(tempDWGraphVo.getNodes());
                    dWGraphVo.getEdges().addAll(tempDWGraphVo.getEdges());
                }
            }

            List<DWNodeVo> tempDWNodeVoList = new ArrayList<>();
            for(DWNodeVo dwNodeVoTemp: dWGraphVo.getNodes())
            {
                if(dwNodeVoTemp.getId().startsWith(dwNodeVo.getId())) //当前节点和当前复制的顶层节点
                {
                    tempDWNodeVoList.add(dwNodeVoTemp);

                }
            }
            Assert.ASSERT(gwDealProductRecordList.size()==tempDWNodeVoList.size()
                            || (gwDealProductRecordList.size()==0 && tempDWNodeVoList.size()==1),
                    "具体项次数不符！" + dwNodeVo.getId() + ":" + gwDealProductRecordList.size() + ":" + tempDWNodeVoList.size());
            Map<String, List<DWNodeVo>> parentToChildrenMap = nodeHierarchyService.getParentToChildrenMap(dWGraphVo);

            for(int i=0;i<gwDealProductRecordList.size();i++)
            {
                tempDWNodeVoList.get(i).getProperties().setTaskItemObjectNumber(gwDealProductRecordList.get(i).getCard());
                setNodePropertiesCycle(parentToChildrenMap, tempDWNodeVoList.get(i));
            }
        }

        if(deleteTempFlag)
        {
            nodeOperationService.removeNodeAndChildren(dWGraphVo, dwNodeVo);
            return;
        }

        //子节点信息处理，仅处理当前节点，不要处理复制出来的节点
        Map<String, List<DWNodeVo>> parentToChildrenMap = nodeHierarchyService.getParentToChildrenMap(dWGraphVo);
        List<DWNodeVo> childToChildrenList = parentToChildrenMap.get(dwNodeVo.getId());
        if(childToChildrenList!=null)
        {
            for(DWNodeVo childDWNodeVo: childToChildrenList)
            {
                refreshTaskInfo(contextMap, dWGraphVo, changeNumber, partId, dwNodeVo, childDWNodeVo);
            }
        }
    }

    public static void setNodePropertiesCycle(Map<String, List<DWNodeVo>> parentToChildrenMap, DWNodeVo dwNodeVo)
    {
        List<DWNodeVo> childToChildrenList = parentToChildrenMap.get(dwNodeVo.getId());
        if(childToChildrenList!=null)
        {
            for(DWNodeVo childDWNodeVo: childToChildrenList)
            {
                childDWNodeVo.getProperties().setTaskItemObjectNumber(dwNodeVo.getProperties().getTaskItemObjectNumber());
                setNodePropertiesCycle(parentToChildrenMap, childDWNodeVo);
            }
        }
    }

    public static List<GWDealProductRecord> getDealRecordDataList(String oid, String number, String type, String taskItemObjectNumber, String conditionItemType) {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        List<GWDealProductRecord> result = new ArrayList<>();
        CmQuerySpec qs = null;
        try {
            qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, oid);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, number);

            if(!"在制品或已制品".equals(type) && StrUtil.isNotEmpty(type))
            {
                qs.appendAnd();
                qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, type);
            }

            if(StrUtil.isNotEmpty(taskItemObjectNumber))
            {
                if(AnalysisConstant.SOURCE_YIZHIPIN.equals(type))
                {
                    qs.appendAnd();
                    qs.appendOpenParen();
                    qs.appendWhere(GWDealProductRecord.CARD, CmQuerySpec.EQUAL, taskItemObjectNumber);
                    qs.appendOr();
                    qs.appendWhere(GWDealProductRecord.CARD, CmQuerySpec.LIKE, taskItemObjectNumber+"_");
                    qs.appendCloseParen();
                }else
                {
                    qs.appendAnd();
                    qs.appendWhere(GWDealProductRecord.CARD, CmQuerySpec.EQUAL, taskItemObjectNumber);
                }

            }

            if(StrUtil.isNotEmpty(conditionItemType))
            {
                if("整件外协".equals(conditionItemType))
                {
                    qs.appendAnd();
                    qs.appendWhere("CMKEYID", CmQuerySpec.LIKE, "ZJWX");
                }
                else if("返修".equals(conditionItemType))
                {
                    qs.appendAnd();
                    qs.appendWhere(GWDealProductRecord.DEALTYPE, CmQuerySpec.EQUAL, "返修");
                }else if("报废".equals(conditionItemType))
                {
                    qs.appendAnd();
                    qs.appendOpenParen();
                    qs.appendWhere(GWDealProductRecord.DEALTYPE, CmQuerySpec.EQUAL, "报废");
                    qs.appendOr();
                    qs.appendWhere(GWDealProductRecord.DEALTYPE, CmQuerySpec.EQUAL, "已提前报废");
                    qs.appendCloseParen();
                }else
                {
                    qs.appendAnd();
                    qs.appendWhere(GWDealProductRecord.DEALTYPE, CmQuerySpec.EQUAL, conditionItemType);
                }

            }

            qs.appendOrderBy(GWDealProductRecord.CREATETIME, false);
            //System.out.println("qs = " + qs);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();

                String card = StrUtil.isEmpty(record.getCard()) ? "" : record.getCard();
                if(AnalysisConstant.SOURCE_YIZHIPIN.equals(record.getSource()) && card.contains("_")){
                    card = card.split("_")[0];
                }
                record.setCard(card);
                result.add(record);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e + "  qs = " + qs);
        }finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return result;
    }

    public static boolean isNoEffect(String changeNumber, String partId)
    {
        String effect1 = DWSqlUtil.getSqlResultString(
                "SELECT product FROM AnalysisObjEntry WHERE datatype='yproduct'  " +
                        "AND ANALYSISNUMBER ='" + changeNumber + "'  AND veroid='"+ partId +"' ");

        String effect2 = DWSqlUtil.getSqlResultString(
                "SELECT product FROM AnalysisObjEntry WHERE datatype='zproduct'  " +
                        "AND ANALYSISNUMBER ='" + changeNumber + "'  AND veroid='"+ partId +"' ");

        if("无影响".equals(effect1) && "无影响".equals(effect2) )
        {
            return true;
        }

        return false;
    }


}
