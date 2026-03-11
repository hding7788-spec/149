package com.glaway.mpm.mvc.builders.pbom;

import wt.util.WTException;

import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.core.components.descriptor.DescriptorConstants.TableTreeProperties;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TreeConfig;
import com.ptc.mvc.util.ClientMessageSource;
/**
 * 定义PBOM工艺路线Builder
 * @author Administrator
 *
 */

@ComponentBuilder("com.glaway.mpm.mvc.builders.pbom.PartLineBuilder")
public class PartLineBuilder extends AbstractComponentBuilder{
	private static final String RESOURCE = "com.glaway.mpm.pbom.ui.PBOMResource";
	private final ClientMessageSource messageSource = getMessageSource(RESOURCE);
	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return new PartTypeBuilderTreeAdapter();
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();

		TreeConfig treeconfig = componentconfigfactory.newTreeConfig();
//		treeconfig.setActionModel("technic toolbar actions");
//		String s = messageSource.getMessage("TECHNIC_DESIGN_DISPATCHER");
		treeconfig.setLabel("定义工艺路线");
		treeconfig.setSelectable(true);
//		treeconfig.setHelpContext("plan.table.help");
		treeconfig.setNodeColumn("number");
		treeconfig.setExpansionLevel(TableTreeProperties.FULL_EXPAND);

//		JcaColumnConfig jcaColumnConfig = (JcaColumnConfig) componentconfigfactory.newColumnConfig(
//				DescriptorConstants.ColumnIdentifiers.NM_ACTIONS, false);
//		jcaColumnConfig.setDescriptorProperty(DescriptorConstants.ActionProperties.ACTION_MODEL,
//				"changed_plan_rightmenu");
//		treeconfig.addComponent(jcaColumnConfig);

		ColumnConfig columnConfig1 = componentconfigfactory.newColumnConfig("number",false);
		columnConfig1.setId("number");
		columnConfig1.setLabel("part编号");
		columnConfig1.setWidth(350);
		treeconfig.addComponent(columnConfig1);

		ColumnConfig columnConfig0 = componentconfigfactory.newColumnConfig("name",false);
		columnConfig0.setId("name");
		columnConfig0.setLabel("名称");
		columnConfig1.setWidth(150);
		treeconfig.addComponent(columnConfig0);

//	     ColumnConfig columnConfig = componentconfigfactory.newColumnConfig("CTYPE",false);
//	      columnConfig.setLabel("物料类型");
//	      columnConfig.setId("CTYPE");
//	      columnConfig.setDataUtilityId("PbomBuilderFlowDataUtility");
//	      columnConfig.setWidth(200);
//	      treeconfig.addComponent(columnConfig);

//		   ColumnConfig columnConfig2 = componentconfigfactory.newColumnConfig("KEYCOMPONENT",false);
//		   columnConfig2.setLabel("关键件");
//		   columnConfig2.setId("KEYCOMPONENT");
//		   columnConfig2.setDataUtilityId("PbomBuilderFlowDataUtility");
//		   columnConfig2.setWidth(100);
//		   treeconfig.addComponent(columnConfig2);


        ColumnConfig columnConfig3 = componentconfigfactory.newColumnConfig("ZZBM",false);
        columnConfig3.setLabel("*主制车间");
        columnConfig3.setId("ZZBM");
        //columnConfig.setInputFieldType("text");
        //columnConfig.setDefaultFreeze(false);
        columnConfig3.setDataUtilityId("PbomBuilderFlowDataUtility");
        columnConfig3.setWidth(150);
        treeconfig.addComponent(columnConfig3);

        ColumnConfig columnConfig4 = componentconfigfactory.newColumnConfig("FZBM",false);
        columnConfig4.setLabel("辅制车间");
        columnConfig4.setId("FZBM");
        columnConfig4.setDataUtilityId("PbomBuilderFlowDataUtility");
        columnConfig4.setWidth(500);
        treeconfig.addComponent(columnConfig4);

		return treeconfig;
	}



}
