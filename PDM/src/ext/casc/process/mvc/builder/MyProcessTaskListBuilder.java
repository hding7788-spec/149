package ext.casc.process.mvc.builder;

import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptorHelper;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

import ext.casc.process.util.ProcessUtil;

@ComponentBuilder("MyProcessTask_table_id3")
public class MyProcessTaskListBuilder extends AbstractConfigurableTableBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
        TableViewDescriptor currTableViewDescriptor = TableViewDescriptorHelper.getCurrentActiveView(
                "MyProcessTask_table_id3", SessionHelper.getLocale());
        // System.out.println("--------currTableViewDescriptor:"+currTableViewDescriptor);
        String select = currTableViewDescriptor.getName();
        //System.out.println("--------select:" + select);
        return ProcessUtil.queryProcessTaskByUser(currentUser,select);
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(true);
        tableConfig.setConfigurable(true);
        tableConfig.setId("MyProcessTask_table_id3");
        tableConfig.setActionModel("custom_process_task_delete_actions");
        tableConfig.setLabel("我的工艺任务");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_task_delete_actions");
        tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
        numberColumnConfig.setAutoSize(true);
        numberColumnConfig.setInfoPageLink(true);
        tableConfig.addComponent(numberColumnConfig);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig versionColumnConfig = factory.newColumnConfig("version", false);
        versionColumnConfig.setAutoSize(true);
        tableConfig.addComponent(versionColumnConfig);

        ColumnConfig zhuzhichejian = factory.newColumnConfig("zhuzhichejian", true);
        zhuzhichejian.setLabel("主制车间");
        zhuzhichejian.setId("zhuzhichejian");
        tableConfig.addComponent(zhuzhichejian);

        ColumnConfig fuzhuchejian = factory.newColumnConfig("fuzhichejian", true);
        fuzhuchejian.setLabel("辅制车间");
        fuzhuchejian.setId("fuzhichejian");
        fuzhuchejian.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(fuzhuchejian);

        ColumnConfig createDate = factory.newColumnConfig("thePersistInfo.createStamp", true);
        createDate.setLabel("创建时间");
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

        ColumnConfig taskState = factory.newColumnConfig("taskState", true);
        taskState.setLabel("任务状态");
        taskState.setId("taskState");
        tableConfig.addComponent(taskState);

        ColumnConfig taskType = factory.newColumnConfig("taskType", true);
        taskType.setLabel("任务类型");
        taskType.setId("taskType");
        taskType.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(taskType);

        ColumnConfig renwuyaoqiu = factory.newColumnConfig("renwuyaoqiu", false);
        renwuyaoqiu.setLabel("任务要求");
        renwuyaoqiu.setId("renwuyaoqiu");
        renwuyaoqiu.setDataUtilityId("");
        tableConfig.addComponent(renwuyaoqiu);

        return tableConfig;
    }

    @Override
    public ConfigurableTable buildConfigurableTable(String arg0) throws WTException {
        return new MyProcessTaskListViews();
    }

}
