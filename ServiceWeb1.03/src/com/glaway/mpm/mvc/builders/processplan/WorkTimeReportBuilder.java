package com.glaway.mpm.mvc.builders.processplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.util.WTException;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;

@ComponentBuilder("com.glaway.mpm.mvc.builders.processplan.WorkTimeReportBuilder")
public class WorkTimeReportBuilder extends AbstractComponentBuilder {
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String number = (String) arg1.getParameter("number");
		MPMProcessPlan processPlan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(number);
		QueryResult queryResult = MPMProcessPlanUtil.getChildMPMOperation(processPlan);
		while (queryResult.hasMoreElements()) {

			MPMOperation operation = (MPMOperation) ((Persistable[]) queryResult.nextElement())[1];
			list.add(setMap(processPlan, operation, null));

			QueryResult qr = MPMProcessPlanUtil.getChildMPMOperation(operation);
			while (qr.hasMoreElements()) {
				MPMOperation subOperation = (MPMOperation) ((Persistable[]) qr.nextElement())[1];
				list.add(setMap(processPlan, operation, subOperation));
			}
		}
		return list;

	}

	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();

		TableConfig table = componentconfigfactory.newTableConfig();
		table.setSelectable(false);

		ColumnConfig processPlanName = componentconfigfactory.newColumnConfig();
		processPlanName.setId("ProcessPlanName");
		processPlanName.setLabel("工艺名称");
		table.addComponent(processPlanName);

		ColumnConfig operationName = componentconfigfactory.newColumnConfig();
		operationName.setId("OperationName");
		operationName.setLabel("工序名称");
		table.addComponent(operationName);

		ColumnConfig subOperationName = componentconfigfactory.newColumnConfig();
		subOperationName.setId("SubOperationName");
		subOperationName.setLabel("工步号");
		table.addComponent(subOperationName);

		ColumnConfig prepareWorkHours = componentconfigfactory.newColumnConfig();
		prepareWorkHours.setId("PrepareWorkHours");
		prepareWorkHours.setLabel("准备工时");
		table.addComponent(prepareWorkHours);

		ColumnConfig taktTime = componentconfigfactory.newColumnConfig();
		taktTime.setId("TaktTime");
		taktTime.setLabel("单件工时");
		table.addComponent(taktTime);

		ColumnConfig numberOfGroup = componentconfigfactory.newColumnConfig();
		numberOfGroup.setId("NumberOfGroup");
		numberOfGroup.setLabel("每组数量(个)");
		table.addComponent(numberOfGroup);

		return table;
	}

	private static Map<String, String> setMap(MPMProcessPlan processPlan, MPMOperation operation,
			MPMOperation subOperation) throws WTException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ProcessPlanName", processPlan.getName());
		map.put("OperationName", operation.getName());
		if (subOperation != null) {
			map.put("SubOperationName", subOperation.getName());
			operation = subOperation;

		}
		IBAHelper ibaHelper = new IBAHelper(operation);
		map.put("PrepareWorkHours", ibaHelper.getIBAValue("PrepareWorkHours"));
		map.put("TaktTime", ibaHelper.getIBAValue("TaktTime"));
		map.put("NumberOfGroup", ibaHelper.getIBAValue("NumberOfGroup"));

		return map;
	}
}
