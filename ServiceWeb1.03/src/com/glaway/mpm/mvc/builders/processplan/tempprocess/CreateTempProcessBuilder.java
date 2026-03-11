package com.glaway.mpm.mvc.builders.processplan.tempprocess;

import wt.util.WTException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.jca.mvc.components.JcaAttributePanelConfig;
import com.ptc.mvc.components.AttributeConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentId;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.CustomizableViewConfig;
import com.ptc.mvc.components.GroupConfig;

@ComponentBuilder( { "com.glaway.mpm.mvc.builders.processplan.tempprocess.CreateTempProcessBuilder" })
public class CreateTempProcessBuilder extends AbstractAttributesComponentBuilder {
	private static final String DATAUTILITY = "CreateGZCardDatautility";

	@Override
	protected CustomizableViewConfig buildAttributesComponentConfig(ComponentParams paramComponentParams)
			throws WTException {

		ComponentConfigFactory factory = getComponentConfigFactory();
		ComponentMode localComponentMode = getComponentMode(paramComponentParams);
		JcaAttributePanelConfig panelConfig = (JcaAttributePanelConfig) factory
				.newAttributePanelConfig(ComponentId.ATTRIBUTE_PANEL_ID);
		panelConfig.setComponentMode(localComponentMode);

		GroupConfig generalGroupConfig = factory.newGroupConfig("General");
		generalGroupConfig.setLabel("基本属性");
		AttributeConfig number = factory.newAttributeConfig(AttributeConstants.number, "编号", 1, 0);
		number.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(number);

		AttributeConfig productNumber = factory.newAttributeConfig(AttributeConstants.productNumber, "生产令号", 2, 0);
		productNumber.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(productNumber);

		AttributeConfig xiangJian = factory.newAttributeConfig(AttributeConstants.insertPart, "产品代号", 2, 1);
		xiangJian.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(xiangJian);

		AttributeConfig produceNumber = factory.newAttributeConfig(AttributeConstants.productionNumber, "整件图号", 3, 0);
		produceNumber.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(produceNumber);

		AttributeConfig audit = factory.newAttributeConfig(AttributeConstants.isReview, "零件图号", 3, 1);
		audit.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(audit);

		AttributeConfig publicGZ = factory.newAttributeConfig(AttributeConstants.isCommonTools, "零件名称", 4, 0);
		publicGZ.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(publicGZ);

		AttributeConfig model = factory.newAttributeConfig(AttributeConstants.isTestPart, "试模件", 4, 1);
		model.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(model);

		AttributeConfig partNumber = factory.newAttributeConfig(AttributeConstants.partNumber, "零件图号", 5, 0);
		partNumber.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(partNumber);

		AttributeConfig isRegularlyTools = factory
				.newAttributeConfig(AttributeConstants.isRegularlyTools, "常用工装", 5, 1);
		isRegularlyTools.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(isRegularlyTools);

		AttributeConfig workShop = factory.newAttributeConfig(AttributeConstants.workShop, "主制车间", 6, 0);
		workShop.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(workShop);

		panelConfig.addComponent(generalGroupConfig);
		return panelConfig;
	}

}
