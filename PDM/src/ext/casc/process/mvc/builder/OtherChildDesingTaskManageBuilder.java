
package ext.casc.process.mvc.builder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import wt.fc.PersistenceHelper;
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
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;

import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessPlan;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;


@ComponentBuilder("ext.casc.process.mvc.builder.OtherChildDesingTaskManageBuilder")
public class OtherChildDesingTaskManageBuilder extends
		AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
    	   NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
    	   NmOid nmOid = cb.getPageOid();
    	   long longId=0;
    	   Object object = nmOid.getRefObject();
			if (object instanceof ProcessPlan) {
			   ProcessPlan plan = (ProcessPlan) object;
				longId = PersistenceHelper.getObjectIdentifier(plan)
						.getId();
			}

           if(longId!=0){
              return ProcessUtil.getAllProcessTaskByProcessPlanId(longId);
           }
    	   return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("型号技术工作计划");
        tableConfig.setSelectable(true);
        tableConfig.setConfigurable(true);
        tableConfig.setId("ext.casc.process.mvc.builder.OtherChildDesingTaskManageBuilder");
        tableConfig.setActionModel("custom_FeiGongYiTask");



        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);


        ColumnConfig tasknameColumnConfig = factory.newColumnConfig("taskname", true);
        tasknameColumnConfig.setAutoSize(true);
        tasknameColumnConfig.setId("taskname");
        tasknameColumnConfig.setLabel("名称");
        tasknameColumnConfig.setDataUtilityId("SearchDownloadDataUtility");
        tableConfig.addComponent(tasknameColumnConfig);

        ColumnConfig zhuzhichejianColumnConfig = factory.newColumnConfig("zhuzhichejian", false);
        zhuzhichejianColumnConfig.setAutoSize(true);
        zhuzhichejianColumnConfig.setId("zhuzhichejian");
        zhuzhichejianColumnConfig.setLabel("主制车间");
        tableConfig.addComponent(zhuzhichejianColumnConfig);

        ColumnConfig endDateColumnConfig = factory.newColumnConfig("endDate", true);
        endDateColumnConfig.setLabel("计划完成时间");
        endDateColumnConfig.setAutoSize(true);
        endDateColumnConfig.setId("endDate");
        tableConfig.addComponent(endDateColumnConfig);

        ColumnConfig renwuyaoqiuColumnConfig = factory.newColumnConfig("renwuyaoqiu", true);
        renwuyaoqiuColumnConfig.setLabel("任务要求");
        renwuyaoqiuColumnConfig.setAutoSize(true);
        renwuyaoqiuColumnConfig.setId("renwuyaoqiu");
        tableConfig.addComponent(renwuyaoqiuColumnConfig);

        ColumnConfig renwujinduColumnConfig = factory.newColumnConfig("renwujindu", true);
        renwujinduColumnConfig.setLabel("任务进度");
        renwujinduColumnConfig.setAutoSize(true);
        renwujinduColumnConfig.setDataUtilityId("SearchDownloadDataUtility");
        renwujinduColumnConfig.setId("renwujindu");
        tableConfig.addComponent(renwujinduColumnConfig);

        ColumnConfig wanchengshijianColumnConfig = factory.newColumnConfig("wanchengshijian", true);
        wanchengshijianColumnConfig.setLabel("完成时间");
        wanchengshijianColumnConfig.setAutoSize(true);
        wanchengshijianColumnConfig.setDataUtilityId("SearchDownloadDataUtility");
        wanchengshijianColumnConfig.setId("wanchengshijian");
        tableConfig.addComponent(wanchengshijianColumnConfig);


        return tableConfig;
    }

}
