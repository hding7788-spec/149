package com.glaway.mpm.pbom.mvc.builder;

import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TreeConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

@ComponentBuilder("com.glaway.mpm.pbom.mvc.builder.PBOMTechnicsReportBuilder")
public class PBOMTechnicsReportBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		NmCommandBean commandbean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
        NmOid nmOid = commandbean.getPrimaryOid();
        String batch = (String) commandbean.getText().get("batch");
        Object object = nmOid.getRefObject();
        if (object instanceof WTPart) {
            WTPart part = (WTPart)object;
            return new PBOMTechnicsReportTreeHandler(part, batch);
        }
        return null;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
        TreeConfig treeConfig = factory.newTreeConfig();
        treeConfig.setLabel("对象列表");
        treeConfig.setComponentMode(ComponentMode.VIEW);
        treeConfig.setConfigurable(false);
        treeConfig.setSelectable(false);
        treeConfig.setExpansionLevel("full");
        treeConfig.setActionModel("custom_processCompleteState_actions");

        ColumnConfig icon = factory.newColumnConfig("type_icon",false);
        treeConfig.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", true);
        numberConfig.setInfoPageLink(true);
        treeConfig.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", true);
        nameConfig.setInfoPageLink(false);
        treeConfig.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
        versionConfig.setWidth(50);
        treeConfig.addComponent(versionConfig);

        ColumnConfig gyNumber = factory.newColumnConfig("gyNumber",false);
        gyNumber.setLabel("工艺编号");
        gyNumber.setDataUtilityId("PbomTechnicsReportDataUtility");
        treeConfig.addComponent(gyNumber);

        ColumnConfig gyState = factory.newColumnConfig("gyState",false);
        gyState.setLabel("工艺状态");
        gyState.setDataUtilityId("PbomTechnicsReportDataUtility");
        treeConfig.addComponent(gyState);

        ColumnConfig sfclde = factory.newColumnConfig("sfclde",false);
        sfclde.setLabel("是否材料定额");
        sfclde.setDataUtilityId("PbomTechnicsReportDataUtility");
        treeConfig.addComponent(sfclde);

        ColumnConfig zgycldezt = factory.newColumnConfig("zgycldezt",false);
        zgycldezt.setLabel("主工艺材料定额状态");
        zgycldezt.setDataUtilityId("PbomTechnicsReportDataUtility");
        treeConfig.addComponent(zgycldezt);

        ColumnConfig gyCreator = factory.newColumnConfig("gyCreator",false);
        gyCreator.setLabel("编制者");
        gyCreator.setDataUtilityId("PbomTechnicsReportDataUtility");
        gyCreator.setInfoPageLink(false);
        treeConfig.addComponent(gyCreator);

        ColumnConfig tuhao = factory.newColumnConfig("CINDEX",false);
        tuhao.setAutoSize(true);
        tuhao.setLabel("图号");
        treeConfig.addComponent(tuhao);

        ColumnConfig phase_code = factory.newColumnConfig("PHASE_CODE",false);
        phase_code.setAutoSize(true);
        phase_code.setLabel("阶段标记");
        treeConfig.addComponent(phase_code);

        ColumnConfig mtype = factory.newColumnConfig("MTYPE", false);
        mtype.setAutoSize(true);
        mtype.setLabel("零组件生产类型");
        treeConfig.addComponent(mtype);

        ColumnConfig keyComponent = factory.newColumnConfig("KEYCOMPONENT",false);
        keyComponent.setLabel("关重件标记");
        keyComponent.setWidth(50);
        treeConfig.addComponent(keyComponent);

        ColumnConfig routing = factory.newColumnConfig("ROUTING",false);
        routing.setLabel("工艺路线");
        treeConfig.addComponent(routing);

        ColumnConfig proState = factory.newColumnConfig("proState",false);
        proState.setLabel("流程状态");
        proState.setDataUtilityId("PbomTechnicsReportDataUtility");
        treeConfig.addComponent(proState);

        treeConfig.setNodeColumn("number");

        return treeConfig;
	}
}
