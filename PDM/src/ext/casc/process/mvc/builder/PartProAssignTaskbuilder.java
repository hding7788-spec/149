package ext.casc.process.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import wt.enterprise.Master;
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

import ext.casc.process.ProcessConstants;
import ext.casc.util.WCUtil;

@ComponentBuilder("ext.casc.process.mvc.builder.PartProAssignTaskbuilder")
public class PartProAssignTaskbuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        List<WTPart> list = new ArrayList<WTPart>();
        List<String> oidList = new ArrayList<String>();
        NmOid nmOid = cb.getActionOid();
        Object object = nmOid.getRefObject();
        if (object instanceof WTPart) {
            WTPart part = (WTPart)object;
            part = WCUtil.getLatestPartByView((Master)part.getMaster(), "Manufacturing");
            oidList.add(PersistenceHelper.getObjectIdentifier(part).toString());
            list.add(part);
        }
        HttpSession session = cb.getRequest().getSession();
        session.setAttribute("TaskType", ProcessConstants.TASK_TYPE_GONGYISHEJI);
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("对象列表");
        tableConfig.setId("ext.casc.process.mvc.builder.PartProAssignTaskbuilder");
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

        ColumnConfig phase_code = factory.newColumnConfig("PHASE_CODE",false);
        phase_code.setAutoSize(true);
        phase_code.setLabel("当前阶段");
        tableConfig.addComponent(phase_code);

        ColumnConfig mtype = factory.newColumnConfig("MTYPE", false);
        mtype.setAutoSize(true);
        mtype.setLabel("零组件生产类型");
        mtype.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(mtype);

        ColumnConfig columnConfig = factory.newColumnConfig("zhuzhichejian",false);
        columnConfig.setLabel("*主制车间");
        columnConfig.setId("zhuzhichejian");
        //columnConfig.setInputFieldType("text");
        //columnConfig.setDefaultFreeze(false);
        columnConfig.setDataUtilityId("ProAssignTaskDatautility");
        columnConfig.setWidth(150);
        tableConfig.addComponent(columnConfig);

        /*
        ColumnConfig columnConfig2 = factory.newColumnConfig("fuzhuchejian",false);
        columnConfig2.setLabel("辅制车间");
        columnConfig2.setId("fuzhichejian");
        columnConfig2.setDataUtilityId("ProAssignTaskDatautility");
        //columnConfig2.setInputFieldType("ComboBox");
        columnConfig2.setWidth(150);
        tableConfig.addComponent(columnConfig2);
		*/

        ColumnConfig columnConfig3 = factory.newColumnConfig("jihuawanchengshijian",false);
        columnConfig3.setLabel("*计划完成时间");
        columnConfig3.setId("jihuawanchengshijian");
        columnConfig3.setDataUtilityId("ProAssignTaskDatautility");
        //columnConfig3.setWidth(150);
        tableConfig.addComponent(columnConfig3);

        ColumnConfig columnConfig4 = factory.newColumnConfig("renwuyaoqiu",false);
        columnConfig4.setLabel("任务要求");
        columnConfig4.setId("renwuyaoqiu");
        columnConfig4.setDataUtilityId("ProAssignTaskDatautility");
        tableConfig.addComponent(columnConfig4);

        ColumnConfig columnConfig5 = factory.newColumnConfig("renwuyiju",false);
        columnConfig5.setLabel("任务依据");
        columnConfig5.setId("renwuyiju");
        columnConfig5.setDataUtilityId("ProAssignTaskDatautility");
        tableConfig.addComponent(columnConfig5);
        return tableConfig;
    }

}
