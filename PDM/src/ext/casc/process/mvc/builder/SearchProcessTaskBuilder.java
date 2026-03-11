package ext.casc.process.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
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

import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.util.ProcessUtil;

@ComponentBuilder("SearchProcessTask_table_id6")
public class SearchProcessTaskBuilder extends AbstractConfigurableTableBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        System.out.println("-------work:" + params.getParameter("work"));
        String work = String.valueOf(params.getParameter("work"));
        if ("search".equals(work)) {
            String number = ProcessUtil.getNotNullParam(params.getParameter("objnumber"));
            String name = ProcessUtil.getNotNullParam(params.getParameter("objname"));
            String chejian = ProcessUtil.getNotNullParam(params.getParameter("chejian"));
            String iszhuzhi = ProcessUtil.getNotNullParam(params.getParameter("iszhuzhi"));
            String tasktype = ProcessUtil.getNotNullParam(params.getParameter("tasktype"));
            String taskstate = ProcessUtil.getNotNullParam(params.getParameter("taskstate"));
            String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
            String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));
            System.out.println("-----number:" + number + "  name:" + name + "  chejian:" + chejian + "  iszhuzhi:"
                        + iszhuzhi + "  tasktype:" + tasktype + "  startDate:" + startDate + "  endDate:" + endDate);
            String[] par = {number,name,chejian,tasktype,taskstate,startDate,endDate};
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            TableViewDescriptor currTableViewDescriptor = TableViewDescriptorHelper.getCurrentActiveView(
                    "SearchProcessTask_table_id6", SessionHelper.getLocale());
            // System.out.println("--------currTableViewDescriptor:"+currTableViewDescriptor);
            String select = currTableViewDescriptor.getName();
            // System.out.println("--------select:" + select);
            if ("".equals(number)&&"".equals(name)) {//如果没有输入部件编号和部件名称，则按照其他输入条件进行查询
                return ProcessUtil.queryProcessTaskForSearch(currentUser, select,par);
            }else {
                //如果输入了部件编号或名称，则首先查询出编号或名称的部件，然后已此部件为父件，查询其下所有的子部件
                List<WTPart> parts = new ArrayList<WTPart>();
                if (!"".equals(number)) {
                	parts = ProcessUtil.getPartsLikeNumber(number, ProcessConstants.PART_VIEW_MANUFACTURING);
                }else {
                	parts = ProcessUtil.getPartsLikeName(name, ProcessConstants.PART_VIEW_MANUFACTURING);
                }
                //不存在编号或名称下视图为Manufacturing的零部件
                if (parts.isEmpty()) {
                    return null;
                }
                //获取子部件的工艺任务
                List<ProcessTask> allProcessTasks = new ArrayList<ProcessTask>();
                String partNumber = "";
                String partName = "";
                for (WTPart childPart : parts) {
                    partNumber = childPart.getNumber();
                    partName = childPart.getName();
                    String[] arrayPar = {partNumber,partName,chejian,tasktype,taskstate,startDate,endDate};
                    QueryResult qResult = ProcessUtil.queryProcessTaskForSearch(currentUser, select,arrayPar);
                    System.out.println("---------qResult:"+qResult);
                    while(qResult.hasMoreElements()){
                        allProcessTasks.add((ProcessTask)qResult.nextElement());
                    }
                }
                return allProcessTasks;
            }
        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(true);
        tableConfig.setConfigurable(true);
        tableConfig.setId("SearchProcessTask_table_id6");
        tableConfig.setActionModel("custom_process_task_delete_actions");

        tableConfig.setLabel("工艺任务列表");

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
        tableConfig.addComponent(fuzhuchejian);

        ColumnConfig createDate = factory.newColumnConfig("thePersistInfo.createStamp", true);
        createDate.setLabel("创建时间");
        createDate.setId("thePersistInfo.createStamp");
        tableConfig.addComponent(createDate);

        ColumnConfig endDate = factory.newColumnConfig("endDate", true);
        endDate.setLabel("计划完成时间");
        endDate.setId("endDate");
        endDate.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(endDate);

        ColumnConfig completedDate = factory.newColumnConfig("thePersistInfo.modifyStamp", true);
        completedDate.setLabel("完成时间");
        completedDate.setId("thePersistInfo.modifyStamp");
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

        ColumnConfig user = factory.newColumnConfig("userName", false);
        user.setLabel("负责人");
        user.setId("userName");
        user.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(user);


        return tableConfig;
    }

    @Override
    public ConfigurableTable buildConfigurableTable(String arg0) throws WTException {
        return new MyProcessTaskListViews();
    }

}
