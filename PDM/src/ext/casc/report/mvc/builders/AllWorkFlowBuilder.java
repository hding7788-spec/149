package ext.casc.report.mvc.builders;

import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.report.*;;

public class AllWorkFlowBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params)
			throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();

		String beginTime = (String)commandBean.getText().get("beginTime_col_beginTime");
		String endTime = (String)commandBean.getText().get("endTime_col_endTime");
		String processName = (String)commandBean.getText().get("processName");
		if(beginTime!=null&&!"".equals(beginTime)&&endTime!=null&&!"".equals(endTime)&&processName!=null&&!"".equals(processName)){
			return  AllWorkFlowService.getWorkFlowReport(processName,beginTime,endTime);
		}else{
			return null;
		}
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("流程报表");
		table.setActionModel("custom_export_processTask");
		table.setSelectable(false);

		ColumnConfig processName = factory.newColumnConfig("processName",true);
		processName.setLabel("流程名称");
		table.addComponent(processName);

		ColumnConfig infoPageAction = factory.newColumnConfig("infoPageAction",true);
		infoPageAction.setLabel("");
		infoPageAction.setDataUtilityId("AllWorkFlowDataUtility");
		table.addComponent(infoPageAction);

		ColumnConfig template = factory.newColumnConfig("template",true);
		template.setLabel("流程模板");
		table.addComponent(template);

		ColumnConfig containerName = factory.newColumnConfig("containerName",true);
		containerName.setLabel("产品库名称");
		table.addComponent(containerName);

		ColumnConfig starter = factory.newColumnConfig("starter",true);
		starter.setLabel("启动者");
		table.addComponent(starter);

		ColumnConfig startTime = factory.newColumnConfig("startTime",true);
		startTime.setLabel("启动时间");
		table.addComponent(startTime);

		ColumnConfig runningActivity = factory.newColumnConfig("runningActivity",true);
		runningActivity.setLabel("运行的活动和未完成人员");
		table.addComponent(runningActivity);

		ColumnConfig state = factory.newColumnConfig("state",true);
		state.setLabel("状态");
		table.addComponent(state);

		ColumnConfig pboState = factory.newColumnConfig("pboState",true);
		pboState.setLabel("PBO状态");
		pboState.setDataUtilityId("AllWorkFlowDataUtility");
		table.addComponent(pboState);
		return table;
	}

}
