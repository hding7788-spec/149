package com.glaway.mpm.mvc.builders.processplan;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.glaway.mpm.util.Util;
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

@ComponentBuilder("com.glaway.mpm.mvc.builders.processplan.ShowProcessPlanReviewBuilder")
public class ShowProcessPlanReviewBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		WorkItem workItem = (WorkItem)commandBean.getPageOid().getRefObject();
		WfActivity activity = (WfActivity) workItem.getSource().getObject();
        WfProcess process = activity.getParentProcess();
        ProcessData processData = process.getContext();
        String technicsOid = String.valueOf(processData.getValue("technicsOid"));
        if(technicsOid!=null&&!"".equals(technicsOid)) {
        	WTDocument doc = (WTDocument)Util.getObjectByOid(WTDocument.class, technicsOid);
        	if(doc != null) {
        		String number = doc.getNumber();
        		MPMProcessPlan pplan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(number);
        		if(pplan != null) {
        			return pplan;
        		}
        	}

        }
		Persistable per = workItem.getPrimaryBusinessObject().getObject();
		return per;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("零部件");

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setAutoSize(true);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setAutoSize(true);
        table.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setAutoSize(true);
        table.addComponent(versionConfig);

        ColumnConfig spceDepartment = factory.newColumnConfig("showSelectDepartment", false);
        spceDepartment.setLabel("分发部门");
        spceDepartment.setDataUtilityId("MpmplanReleaseDataUtility");
        spceDepartment.setAutoSize(true);
        table.addComponent(spceDepartment);

		return table;
	}

}
