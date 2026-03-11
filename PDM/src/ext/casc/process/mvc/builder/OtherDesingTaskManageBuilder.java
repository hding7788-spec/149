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
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;


@ComponentBuilder("ext.casc.process.mvc.builder.OtherDesingTaskManageBuilder")
public class OtherDesingTaskManageBuilder extends
		AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
    	WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
        return ProcessUtil.queryProcessPlan(currentUser);
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(false);
        tableConfig.setConfigurable(true);
        tableConfig.setId("ext.casc.process.mvc.builder.OtherDesingTaskManageBuilder");
        tableConfig.setActionModel("custom_newTask");

        tableConfig.setLabel("非工艺计划活动列表");

         ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
         ((JcaColumnConfig) nmActionsCol).setActionModel("custom_newProcessPlan");
         tableConfig.addComponent(nmActionsCol);

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig plannumberColumnConfig = factory.newColumnConfig("plannumber", true);
        plannumberColumnConfig.setId("plannumber");
        plannumberColumnConfig.setLabel("编号");
        plannumberColumnConfig.setDataUtilityId("SearchDownloadDataUtility");
        plannumberColumnConfig.setAutoSize(true);
        tableConfig.addComponent(plannumberColumnConfig);

        ColumnConfig processPlanNameColumnConfig = factory.newColumnConfig("name", true);
        processPlanNameColumnConfig.setAutoSize(true);
        tableConfig.addComponent(processPlanNameColumnConfig);

        ColumnConfig zhutiColumnConfig = factory.newColumnConfig("zhuti", false);
        zhutiColumnConfig.setAutoSize(true);
        zhutiColumnConfig.setId("zhuti");
        zhutiColumnConfig.setLabel("主题");
        tableConfig.addComponent(zhutiColumnConfig);

        ColumnConfig xiangmubuColumnConfig = factory.newColumnConfig("xiangmubu", true);
        xiangmubuColumnConfig.setLabel("项目部");
        xiangmubuColumnConfig.setAutoSize(true);
        xiangmubuColumnConfig.setId("xiangmubu");
        tableConfig.addComponent(xiangmubuColumnConfig);

        ColumnConfig xinghaoColumnConfig = factory.newColumnConfig("xinghao", true);
        xinghaoColumnConfig.setLabel("型号");
        xinghaoColumnConfig.setDataUtilityId("SearchDownloadDataUtility");
        xinghaoColumnConfig.setAutoSize(true);
        xinghaoColumnConfig.setId("xinghao");
        tableConfig.addComponent(xinghaoColumnConfig);


        return tableConfig;
    }


}
