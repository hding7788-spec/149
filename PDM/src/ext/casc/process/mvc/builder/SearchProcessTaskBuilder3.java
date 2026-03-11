package ext.casc.process.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptorHelper;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;

@ComponentBuilder("SearchProcessTask_table_id_gongyiyuan2")
public class SearchProcessTaskBuilder3 extends AbstractConfigurableTableBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        System.out.println("-------work:" + params.getParameter("work"));
        String work = String.valueOf(params.getParameter("work"));
        if ("search".equals(work)) {
            String number = ProcessUtil.getNotNullParam(params.getParameter("objnumber"));
            String name = ProcessUtil.getNotNullParam(params.getParameter("objname"));
            String gongyiyuan = ProcessUtil.getNotNullParam(params.getParameter("gongyiyuan"));
            String iszhuzhi = ProcessUtil.getNotNullParam(params.getParameter("iszhuzhi"));
            String tasktype = ProcessUtil.getNotNullParam(params.getParameter("tasktype"));
            String taskstate = ProcessUtil.getNotNullParam(params.getParameter("taskstate"));
            String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
            String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));
            System.out.println("-----number:" + number + "  name:" + name + "  gongyiyuan:" + gongyiyuan + "  iszhuzhi:"
                        + iszhuzhi + "  tasktype:" + tasktype + "  startDate:" + startDate + "  endDate:" + endDate);
            String[] par = {number,name,gongyiyuan,tasktype,taskstate,startDate,endDate};
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            TableViewDescriptor currTableViewDescriptor = TableViewDescriptorHelper.getCurrentActiveView(
                    "SearchProcessTask_table_id_gongyiyuan2", SessionHelper.getLocale());
            // System.out.println("--------currTableViewDescriptor:"+currTableViewDescriptor);
            String select = currTableViewDescriptor.getName();
            System.out.println("--------select:" + select);
            if ("".equals(number)&&"".equals(name)) {//如果没有输入部件编号和部件名称，则按照其他输入条件进行查询
                return ProcessUtil.queryProcessTaskItemForSearch(currentUser, select,par,"gongyiyuan");
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
                List<ProcessTaskItem> allProcessTasks = new ArrayList<ProcessTaskItem>();
                String partNumber = "";
                String partName = "";
                for (WTPart childPart : parts) {
                    partNumber = childPart.getNumber();
                    partName = childPart.getName();
                    String[] arrayPar = {partNumber,partName,gongyiyuan,tasktype,taskstate,startDate,endDate};
                    QueryResult qResult = ProcessUtil.queryProcessTaskItemForSearch(currentUser, select,arrayPar,"gongyiyuan");
                    System.out.println("---------qResult:"+qResult.size());
                    while(qResult.hasMoreElements()){
                        allProcessTasks.add((ProcessTaskItem)qResult.nextElement());
                    }
                }
                System.out.println("---------allProcessTasks:"+allProcessTasks);
                return allProcessTasks;
            }
        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(false);
        tableConfig.setConfigurable(true);
        tableConfig.setId("SearchProcessTask_table_id_gongyiyuan2");
//        tableConfig.setActionModel("custom_process_search_actions");

        tableConfig.setLabel("工艺任务活动列表");

//         ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
//         ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_search_actions");
//         tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig taskItemName = factory.newColumnConfig("taskItemName", true);
        taskItemName.setAutoSize(true);
        taskItemName.setLabel("任务名称");
        tableConfig.addComponent(taskItemName);

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

        ColumnConfig owner = factory.newColumnConfig("owner", true);
        owner.setLabel("负责人");
        owner.setId("owner");
        owner.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(owner);

        ColumnConfig taskState = factory.newColumnConfig("taskState", true);
        taskState.setLabel("任务状态");
        taskState.setId("taskState");
        taskState.setDataUtilityId("ProcessTaskItemDataUtility");
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
        return new ProcessTaskItemListViews();
    }

}
