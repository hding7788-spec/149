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
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.windchill.mpml.command.server.impl.GetMPMLinkPropertiesRemoteWorker;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;

@ComponentBuilder("com.glaway.mpm.mvc.builders.processplan.MaterialBrandReportBuilder")
public class MaterialBrandReportBuilder extends AbstractComponentBuilder {
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String number = (String) arg1.getParameter("number");
		MPMProcessPlan processPlan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(number);
		QueryResult queryResult = MPMProcessPlanUtil.getChildMPMOperation(processPlan);

		while (queryResult.hasMoreElements()) {
			MPMOperation operation = (MPMOperation) ((Persistable[]) queryResult.nextElement())[1];
			System.out.println(operation.getName());
			for (Persistable[] p : getMPMOperationToConsumable(operation)) {
				System.out.println(p[0] + "--1-" + ((MPMProcessMaterial) p[1]).getName());
				list.add(setMap(processPlan, operation, null, p));
			}

			QueryResult qr = MPMProcessPlanUtil.getChildMPMOperation(operation);
			while (qr.hasMoreElements()) {
				MPMOperation subOperation = (MPMOperation) ((Persistable[]) qr.nextElement())[1];
				System.out.println(subOperation.getName());
				for (Persistable[] p : getMPMOperationToConsumable(subOperation)) {
					System.out.println(p[0] + "-2--" + ((MPMProcessMaterial) p[1]).getName());
					list.add(setMap(processPlan, operation, subOperation, p));
				}
			}
		}
		System.out.println(list);
		return list;

	}

	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();

		TableConfig table = componentconfigfactory.newTableConfig();
		table.setSelectable(false);

		ColumnConfig processPlanName = componentconfigfactory.newColumnConfig();
		processPlanName.setId("ProcessPlanName");
		processPlanName.setLabel("工艺名称");
		processPlanName.setDataUtilityId("CreateMPMResourceDatautility");
		table.addComponent(processPlanName);

		ColumnConfig operationName = componentconfigfactory.newColumnConfig();
		operationName.setId("OperationName");
		operationName.setLabel("工序名称");
		table.addComponent(operationName);

		ColumnConfig subOperationName = componentconfigfactory.newColumnConfig();
		subOperationName.setId("SubOperationName");
		subOperationName.setLabel("工步号");
		table.addComponent(subOperationName);

		ColumnConfig materialName = componentconfigfactory.newColumnConfig();
		materialName.setId("MaterialName");
		materialName.setLabel("材料名称");
		table.addComponent(materialName);

		ColumnConfig materialQuota = componentconfigfactory.newColumnConfig();
		materialQuota.setId("MaterialQuota");
		materialQuota.setLabel("材料定额");
		table.addComponent(materialQuota);
		

		return table;
	}

	private static Map<String, String> setMap(MPMProcessPlan processPlan, MPMOperation operation,
			MPMOperation subOperation, Persistable[] p) throws WTException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ProcessPlanName", processPlan.getName());
		map.put("OperationName", operation.getName());
		if (subOperation != null) {
			map.put("SubOperationName", subOperation.getName());
		}
		map.put("MaterialName", ((MPMProcessMaterial) p[1]).getName());
		IBAHelper ibaHelper = new IBAHelper((MPMOperationToConsumableLink) p[0]);
		map.put("MaterialQuota", ibaHelper.getIBAValue("materialQuota"));

		return map;
	}

	private static List<Persistable[]> getMPMOperationToConsumable(MPMOperation operation) throws WTException {
		List<Persistable[]> list = new ArrayList<Persistable[]>();
		QueryResult queryResult = MPMProcessPlanUtil.getMPMOperationToConsumableLink(operation);

		while (queryResult.hasMoreElements()) {

			Persistable[] p = (Persistable[]) queryResult.nextElement();
			if (p[1] instanceof MPMProcessMaterial) {
				list.add(p);
			}
		}
		return list;
	}
}
