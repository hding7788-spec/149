package ext.casc.process.mvc.builder;

import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptorHelper;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.util.ProcessUtil;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.util.HashMap;

@ComponentBuilder("ProcessAssignmentsBuilder_table_id13")
public class ListProcessAssignmentsBuilder extends AbstractConfigurableTableBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        HashMap hashmap = commandBean.getText();
        String s = (String) hashmap.get("TABLE_ID");
        // System.out.println("--------s:"+s);
        TableViewDescriptor currTableViewDescriptor = TableViewDescriptorHelper.getCurrentActiveView(
                "ProcessAssignmentsBuilder_table_id13", SessionHelper.getLocale());
        // System.out.println("--------currTableViewDescriptor:"+currTableViewDescriptor);
        String select = currTableViewDescriptor.getName();
        WTUser user = (WTUser)SessionHelper.getPrincipal();
        QueryResult qResult = ProcessUtil.queryProcessTask(user,select);
        commandBean.getRequest().getSession().setAttribute("TABLE_ID", "ProcessAssignmentsBuilder_table_id");
        return qResult;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(true);
        tableConfig.setConfigurable(true);
        tableConfig.setActionModel("processTaskItem_Table_actions");
        tableConfig.setId("ProcessAssignmentsBuilder_table_id13");
        params.setAttribute("TABLE_ID", "ProcessAssignmentsBuilder_table_id13");

        tableConfig.setLabel("工艺工作任务");

//        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
//        ((JcaColumnConfig) nmActionsCol).setActionModel("processTaskItem_Table_actions");
//        tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig isComplete = factory.newColumnConfig("isComplete", false);
        isComplete.setLabel("");
        isComplete.setWidth(2);
        isComplete.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(isComplete);


        ColumnConfig taskItemName = factory.newColumnConfig("taskItemName", true);
        taskItemName.setAutoSize(true);
        taskItemName.setId("taskItemName");
        taskItemName.setLabel("任务名称");
        //taskItemName.setInfoPageLink(true);
        taskItemName.setDataUtilityId("ProcessTaskItemAttrDataUtility");
        tableConfig.addComponent(taskItemName);

        ColumnConfig taskIOwner = factory.newColumnConfig("taskOwner", true);
        taskIOwner.setAutoSize(true);
        taskIOwner.setId("taskOwner");
        taskIOwner.setLabel("任务发起人");
        //taskItemName.setInfoPageLink(true);
        taskIOwner.setDataUtilityId("ProcessTaskItemAttrDataUtility");
        tableConfig.addComponent(taskIOwner);

        ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
        numberColumnConfig.setAutoSize(true);
        //numberColumnConfig.setInfoPageLink(true);
        numberColumnConfig.setDataUtilityId("ProcessTaskItemAttrDataUtility");
        tableConfig.addComponent(numberColumnConfig);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig versionColumnConfig = factory.newColumnConfig("version", true);
        versionColumnConfig.setLabel("零件版本");
        versionColumnConfig.setAutoSize(true);
        tableConfig.addComponent(versionColumnConfig);

        ColumnConfig renwuleixing = factory.newColumnConfig("taskType", true);
        renwuleixing.setLabel("任务类型");
        renwuleixing.setId("taskType");
        renwuleixing.setDataUtilityId("");
        tableConfig.addComponent(renwuleixing);

        ColumnConfig renwuzhuangtai = factory.newColumnConfig("taskItemState", true);
        renwuzhuangtai.setLabel("状况");
        renwuzhuangtai.setId("taskItemState");
        renwuzhuangtai.setDataUtilityId("");
        tableConfig.addComponent(renwuzhuangtai);

        ColumnConfig technicsStateConfig = factory.newColumnConfig("technicsState", true);
        technicsStateConfig.setLabel("工艺文件状态");
        technicsStateConfig.setId("technicsState");
        technicsStateConfig.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(technicsStateConfig);

        ColumnConfig createDate = factory.newColumnConfig("thePersistInfo.createStamp", true);
        createDate.setLabel("已分配");
        createDate.setId("thePersistInfo.createStamp");
        createDate.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(createDate);


        ColumnConfig endDate = factory.newColumnConfig("endDate", true);
        endDate.setLabel("计划完成时间");
        endDate.setId("endDate");
        endDate.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(endDate);

        ColumnConfig completedDate = factory.newColumnConfig("thePersistInfo.modifyStamp", true);
        completedDate.setLabel("完成时间");
        completedDate.setId("thePersistInfo.modifyStamp");
        completedDate.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(completedDate);

        ColumnConfig iszhuzhi = factory.newColumnConfig("iszhuzhi", true);
        iszhuzhi.setLabel("是否主制");
        iszhuzhi.setId("iszhuzhi");
        iszhuzhi.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(iszhuzhi);

        ColumnConfig executorRole = factory.newColumnConfig("executorRole", true);
        executorRole.setLabel("角色");
        executorRole.setId("executorRole");
        executorRole.setDataUtilityId("");
        tableConfig.addComponent(executorRole);

        ColumnConfig renwuyaoqiu = factory.newColumnConfig("renwuyaoqiu", false);
        renwuyaoqiu.setLabel("任务要求");
        renwuyaoqiu.setId("renwuyaoqiu");
        renwuyaoqiu.setDataUtilityId("");
        tableConfig.addComponent(renwuyaoqiu);

        ColumnConfig renwuyiju = factory.newColumnConfig("renwuyiju", false);
        renwuyiju.setLabel("任务依据");
        renwuyiju.setId("renwuyiju");
        renwuyiju.setDataUtilityId("");
        tableConfig.addComponent(renwuyiju);

        ColumnConfig description = factory.newColumnConfig("description",false);
        description.setLabel("备注");
        description.setId("description");
        tableConfig.addComponent(description);

        return tableConfig;
    }

    @Override
    public ConfigurableTable buildConfigurableTable(String id) throws WTException {
        return new ListProcessAssignmentsViews();
    }

}
