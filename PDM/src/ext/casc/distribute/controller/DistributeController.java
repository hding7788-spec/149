package ext.casc.distribute.controller;

import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.distribute.integration.OtherSystemIntegrationHelper;
import ext.casc.distribute.service.ManufactoryDataService;
import ext.casc.distribute.service.NodeHierarchyService;
import ext.casc.distribute.service.NodeInstanceTaskService;
import ext.casc.distribute.service.NodeOperationService;
import ext.casc.distribute.util.JsonVoConverter;
import ext.casc.distribute.vo.*;
import org.json.JSONObject;
import wt.change2.WTAnalysisActivity;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.Assert;
import wt.util.WTException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributeController {

    public static String getJsonGraphInfo(String changeNumber, String partId)
    {
        String result = null;
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        JSONResultVo jsonResultVo = new JSONResultVo();
        try {

            DWGraphVo dWGraphVo = getGraphInfo(changeNumber, partId);
            jsonResultVo.setData( dWGraphVo);
            jsonResultVo.setSuccess(true);
            jsonResultVo.setMessage("执行成功！");
        }catch(Exception e)
        {
            e.printStackTrace();
            jsonResultVo.setSuccess(false);
            jsonResultVo.setMessage(e);
        }finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        result = JsonVoConverter.serialize(jsonResultVo);
        return result;
    }

    public static DWGraphVo getGraphInfo(String changeNumber, String partId)
    {
        NodeHierarchyService nodeHierarchyService = new NodeHierarchyService();

        NodeOperationService nodeOperationService = new NodeOperationService();
        DWGraphVo dWGraphVo = nodeOperationService.cloneWholeTree();

        List<DWNodeVo> rootNodes = nodeHierarchyService.getRootNode(dWGraphVo);
        Assert.ASSERT(rootNodes!=null && rootNodes.size()==1, "根节点数量不符合要求！");

        // 集成数据
        Map<String,Object> contextMap = new HashMap<>();
        NcResponseDataVo ncResponseDataVo = OtherSystemIntegrationHelper.getNcResponseDataVo(changeNumber, partId);
        MesStatusResultVo mesStatusResultVo = OtherSystemIntegrationHelper.getMesData(changeNumber);
        contextMap.put(NcResponseDataVo.class.getName(), ncResponseDataVo);
        contextMap.put(MesStatusResultVo.class.getName(), mesStatusResultVo);

        for(DWNodeVo dwNodeVo: rootNodes)
        {
            ManufactoryDataService.refreshTaskInfo(contextMap, dWGraphVo, changeNumber, partId, null, dwNodeVo);
            NodeInstanceTaskService.refreshTaskInfoNext(contextMap, dWGraphVo, changeNumber, partId, null, dwNodeVo);
            NodeInstanceTaskService.appendExtBusinessData(contextMap, dWGraphVo, changeNumber, partId);
        }

        nodeHierarchyService.formatLayout(dWGraphVo);


        return dWGraphVo;

    }

    public static Map<String, String> getBasicInfoMap(String changeNumber, String partId)
    {
        Map<String, String> map = new HashMap<>();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WTAnalysisActivity activity = AnalysisUtil.getWTAnalysisActivityByNumber(changeNumber);
            map.put("change", activity.getDisplayIdentifier().toString());
            ReferenceFactory rf = new ReferenceFactory();
            WTPart wtPart = (WTPart) rf.getReference("VR:" + partId).getObject();
            map.put("part", wtPart.getNumber() + " " + wtPart.getName()
                    + " " + wtPart.getVersionIdentifier().getValue() + "." + wtPart.getIterationIdentifier().getValue());
        } catch (WTException e) {
            throw new RuntimeException(e);
        }finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return map;
    }

}
