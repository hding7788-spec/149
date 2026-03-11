package ext.casc.sop.mvc.builders;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.util.WTException;

@ComponentBuilder("ext.casc.process.mvc.builder.TaskItemRouteAndHistoryBuilder")
public class SopTaskItemRouteAndHistoryBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        NmOid nmOid = cb.getPageOid();
        Object object = nmOid.getRefObject();
        if (object instanceof ProcessTaskItem) {
            ProcessTaskItem taskItem = (ProcessTaskItem)object;
            QueryResult qResult = ProcessUtil.getAllProcessTaskItemByPTask(taskItem.getProcessTaskId());
            return qResult;
        } else if (object instanceof ProcessTask) {
            ProcessTask processTask = (ProcessTask)object;
            long longId = PersistenceHelper.getObjectIdentifier(processTask).getId();
            QueryResult qResult = ProcessUtil.getAllProcessTaskItemByPTask(longId);
            return qResult;
        }
        cb.getRequest().getSession().setAttribute("TABLE_ID", "taskitem_completed_state");
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(false);
        tableConfig.setConfigurable(false);
        tableConfig.setId("taskitem_completed_state");
        tableConfig.setActionModel("custom_process_search_actions");
        tableConfig.setSelectable(true);
        tableConfig.setLabel("处理状态");
        
        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_search_actions");
        tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig taskItemName = factory.newColumnConfig("taskItemName", true);
        taskItemName.setAutoSize(true);
        taskItemName.setId("taskItemName");
        taskItemName.setLabel("任务名称");
        //taskItemName.setInfoPageLink(true);
        tableConfig.addComponent(taskItemName);
        
        ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
        numberColumnConfig.setAutoSize(true);
        numberColumnConfig.setInfoPageLink(false);
        tableConfig.addComponent(numberColumnConfig);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig renwuleixing = factory.newColumnConfig("taskType", false);
        renwuleixing.setLabel("任务类型");
        renwuleixing.setId("taskType");
        renwuleixing.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(renwuleixing);
        
        ColumnConfig renwuzhuangtai = factory.newColumnConfig("taskItemState", false);
        renwuzhuangtai.setLabel("状况");
        renwuzhuangtai.setId("taskItemState");
        tableConfig.addComponent(renwuzhuangtai);
        
        ColumnConfig owner = factory.newColumnConfig("owner", false);
        owner.setLabel("负责人");
        owner.setId("owner");
        owner.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(owner);

        ColumnConfig completedBy = factory.newColumnConfig("completedBy", false);
        completedBy.setLabel("完成者");
        completedBy.setId("completedBy");
        tableConfig.addComponent(completedBy);
        
        ColumnConfig routeSelect = factory.newColumnConfig("routeSelect", false);
        routeSelect.setLabel("路由");
        routeSelect.setId("routeSelect");
        tableConfig.addComponent(routeSelect);
        
        ColumnConfig endDate = factory.newColumnConfig("thePersistInfo.modifyStamp", false);
        endDate.setLabel("完成时间");
        endDate.setId("thePersistInfo.modifyStamp");
        endDate.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(endDate);
        
        ColumnConfig chejian = factory.newColumnConfig("chejian", false);
        chejian.setLabel("车间");
        chejian.setId("chejian");
        chejian.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(chejian);
        
//        ColumnConfig iszhuzhi = factory.newColumnConfig("iszhuzhi", false);
//        iszhuzhi.setLabel("是否主制");
//        iszhuzhi.setId("iszhuzhi");
//        iszhuzhi.setDataUtilityId("ProcessTaskItemDataUtility");
//        tableConfig.addComponent(iszhuzhi);
        
        ColumnConfig executorRole = factory.newColumnConfig("executorRole", false);
        executorRole.setLabel("角色");
        executorRole.setId("executorRole");
        tableConfig.addComponent(executorRole);

        ColumnConfig renwuyaoqiu = factory.newColumnConfig("renwuyaoqiu", false);
        renwuyaoqiu.setLabel("任务要求");
        renwuyaoqiu.setId("renwuyaoqiu");
        tableConfig.addComponent(renwuyaoqiu);
        
        ColumnConfig description = factory.newColumnConfig("description",false);
        description.setLabel("意见");
        description.setId("description");
        tableConfig.addComponent(description);
        
        return tableConfig;
    }

}
