package ext.casc.process.mvc.builder;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;

@ComponentBuilder("ext.casc.process.mvc.builder.AddProAssignTaskBuilder")
public class AddProAssignTaskBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        NmOid nmOid = cb.getActionOid();
        Object object = nmOid.getRefObject();

        if (object instanceof ProcessTask) {
            ProcessTask processTask = (ProcessTask)object;
            long longId = PersistenceHelper.getObjectIdentifier(processTask).getId();
            WTPart part = ProcessUtil.getWtPartByProcessTask(longId);
            return part;
        }else if (object instanceof ProcessTaskItem) {
            ProcessTaskItem taskItem = (ProcessTaskItem)object;
            long longId = taskItem.getPartId();
            WTPart part = ProcessUtil.getWtPart(longId);
            return part;
        }

        if (object instanceof WTPart) {
            return (WTPart)object;
        }

        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("对象列表");

        ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
        ((JcaColumnConfig) nmActionsCol).setActionModel("custom_process_task_partTable_actions");
        tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        tableConfig.addComponent(icon);

        ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
        numberColumnConfig.setAutoSize(true);
        numberColumnConfig.setInfoPageLink(true);
        tableConfig.addComponent(numberColumnConfig);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        nameColumnConfig.setInfoPageLink(true);
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig versionColumnConfig = factory.newColumnConfig("version", false);
        versionColumnConfig.setAutoSize(true);
        tableConfig.addComponent(versionColumnConfig);

        ColumnConfig tuhao = factory.newColumnConfig("CINDEX",false);
        tuhao.setAutoSize(true);
        tuhao.setLabel("图号");
        tableConfig.addComponent(tuhao);

        ColumnConfig mindex = factory.newColumnConfig("MINDEX",false);
        mindex.setAutoSize(true);
        mindex.setLabel("所属型号");
        tableConfig.addComponent(mindex);

        ColumnConfig cmat = factory.newColumnConfig("CMAT",false);
        cmat.setAutoSize(true);
        cmat.setLabel("材料");
        tableConfig.addComponent(cmat);

        ColumnConfig taskType = factory.newColumnConfig("taskType",false);
        taskType.setAutoSize(true);
        taskType.setLabel("任务类型");
        taskType.setDataUtilityId("AddProAssignTaskDatautility");
        tableConfig.addComponent(taskType);

        ColumnConfig phase_code = factory.newColumnConfig("PHASE_CODE",false);
        phase_code.setAutoSize(true);
        phase_code.setLabel("当前阶段");
        tableConfig.addComponent(phase_code);

        ColumnConfig zhuzhichejian = factory.newColumnConfig("zhuzhichejian",false);
        zhuzhichejian.setLabel("主制车间");
        zhuzhichejian.setId("zhuzhichejian");
        zhuzhichejian.setDataUtilityId("AddProAssignTaskDatautility");
        zhuzhichejian.setWidth(150);
        tableConfig.addComponent(zhuzhichejian);

        ColumnConfig fuzhuchejian = factory.newColumnConfig("fuzhichejian",false);
        fuzhuchejian.setLabel("辅制车间");
        fuzhuchejian.setId("fuzhichejian");
        fuzhuchejian.setDataUtilityId("AddProAssignTaskDatautility");
        fuzhuchejian.setWidth(150);
        tableConfig.addComponent(fuzhuchejian);

        ColumnConfig addchejian = factory.newColumnConfig("addchejian",false);
        addchejian.setLabel("*补加车间");
        addchejian.setId("addchejian");
        addchejian.setDataUtilityId("AddProAssignTaskDatautility");
        addchejian.setWidth(150);
        tableConfig.addComponent(addchejian);

        ColumnConfig columnConfig3 = factory.newColumnConfig("jihuawanchengshijian",false);
        columnConfig3.setLabel("计划完成时间");
        columnConfig3.setId("jihuawanchengshijian");
        columnConfig3.setDataUtilityId("AddProAssignTaskDatautility");
        tableConfig.addComponent(columnConfig3);

        ColumnConfig columnConfig4 = factory.newColumnConfig("renwuyaoqiu",false);
        columnConfig4.setLabel("任务要求");
        columnConfig4.setId("renwuyaoqiu");
        columnConfig4.setDataUtilityId("AddProAssignTaskDatautility");
        tableConfig.addComponent(columnConfig4);

        return tableConfig;
    }

}
