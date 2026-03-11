package ext.casc.sop.mvc.builders;

import com.glaway.mpm.util.GLLogger;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.sop.constants.SopConstants;
import wt.util.WTException;

@ComponentBuilder({ "ext.casc.sop.mvc.builders.CreateSOPZYBuilder" })
public class CreateSOPZYBuilder extends AbstractAttributesComponentBuilder {
	private static final String CLASSNAME = CreateSOPZYBuilder.class.getName();
	private static String DATAUTILITY = "SopResourceDataUtility";

	@Override
	protected CustomizableViewConfig buildAttributesComponentConfig(ComponentParams paramComponentParams) throws WTException {
		NmCommandBean commandBean = ((JcaComponentParams) paramComponentParams).getHelperBean().getNmCommandBean();
		String mpmResourceType = (String) paramComponentParams.getParameter("mpmResourceType");
		String typeName = commandBean.getTextParameter("typeName");
		GLLogger.debug(CLASSNAME, "mpmResourceType--" + mpmResourceType);
		GLLogger.debug(CLASSNAME, "typeName--" + typeName);
		ComponentConfigFactory factory = getComponentConfigFactory();
		ComponentMode localComponentMode = getComponentMode(paramComponentParams);
		AttributePanelConfig panelConfig = factory.newAttributePanelConfig(ComponentId.ATTRIBUTE_PANEL_ID);
		panelConfig.setComponentMode(localComponentMode);
		GroupConfig generalGroupConfig = factory.newGroupConfig("General");
		generalGroupConfig.setLabel("基本属性");
		AttributeConfig id = factory.newAttributeConfig(SopConstants.SOP_ATTR_NUMBER);
		id.setLabel("编号");
		id.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(id);

		if (mpmResourceType.equals(SopConstants.SOP_STR_CSXM)) {
			AttributeConfig zylb1 = factory.newAttributeConfig(SopConstants.SOP_IBA_SPECIALIZEDTYPE);
			zylb1.setDataUtilityId(DATAUTILITY);
			zylb1.setLabel("专业类别");
			generalGroupConfig.addComponent(zylb1);

			AttributeConfig name = factory.newAttributeConfig(SopConstants.SOP_IBA_PARAMETERSNAME);
			name.setLabel("参数项目名称");
			name.setDataUtilityId(DATAUTILITY);
			generalGroupConfig.addComponent(name);
		} else {
			AttributeConfig name = factory.newAttributeConfig(SopConstants.SOP_ATTR_NAME);
			name.setLabel("名称");
			name.setDataUtilityId(DATAUTILITY);
			generalGroupConfig.addComponent(name);
		}

		if (mpmResourceType.equals(SopConstants.SOP_STR_GXMC)) {// 工序名称
			initCreateGXMCAttributePanel(generalGroupConfig, factory);
		} else if (mpmResourceType.equals(SopConstants.SOP_STR_CSXM)) {// 参数项目
			initCreateCSXMAttributePanel(generalGroupConfig, factory);
		} else if (mpmResourceType.equals(SopConstants.SOP_STR_CZGW)) {// 操作岗位
			initCreateCZGWAttributePanel(generalGroupConfig, factory);
		} else if (mpmResourceType.equals(SopConstants.SOP_STR_ZYLB)) {// 专业类别
			initCreateZYLBAttributePanel(generalGroupConfig, factory);
		} else if (mpmResourceType.equals(SopConstants.SOP_STR_CSXMMC)) {// 参数项目名称
			initCreateCSXMMCAttributePanel(generalGroupConfig, factory);
		} else if (mpmResourceType.equals(SopConstants.SOP_STR_WZLB)) {// 物资类别
			initCreateWZLBAttributePanel(generalGroupConfig, factory);
		}

		AttributeConfig des = factory.newAttributeConfig(SopConstants.SOP_ATTR_REMARK);
		des.setDataUtilityId(DATAUTILITY);
		des.setLabel("说明");
		generalGroupConfig.addComponent(des);

		panelConfig.addComponent(generalGroupConfig);
		return panelConfig;
	}

	/**
	 * 参数项目页面定制
	 *
	 * @param generalGroupConfig
	 * @param factory
	 */
	private static void initCreateCSXMAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig gxmc = factory.newAttributeConfig(SopConstants.SOP_IBA_PROCEDUCENAME);
		gxmc.setDataUtilityId(DATAUTILITY);
		gxmc.setLabel("工序名称");
		generalGroupConfig.addComponent(gxmc);

		AttributeConfig wzlb = factory.newAttributeConfig(SopConstants.SOP_IBA_MATERIALCATEGORY);
		wzlb.setDataUtilityId(DATAUTILITY);
		wzlb.setLabel("物资类别");
		generalGroupConfig.addComponent(wzlb);

		AttributeConfig csz = factory.newAttributeConfig(SopConstants.SOP_IBA_CANSHUZHI);
		csz.setDataUtilityId(DATAUTILITY);
		csz.setLabel("参数值");
		generalGroupConfig.addComponent(csz);
	}

	/**
	 * 操作岗位页面定制
	 *
	 * @param generalGroupConfig
	 * @param factory
	 */
	private static void initCreateCZGWAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig zzcj = factory.newAttributeConfig(SopConstants.SOP_IBA_ZZCJ);
		zzcj.setDataUtilityId(DATAUTILITY);
		zzcj.setLabel("车间");
		generalGroupConfig.addComponent(zzcj);

	}

	/**
	 * 专业类别页面定制
	 *
	 * @param generalGroupConfig
	 * @param factory
	 */
	private static void initCreateZYLBAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig zydh = factory.newAttributeConfig(SopConstants.SOP_IBA_PROFESSIONALCODE);
		zydh.setDataUtilityId(DATAUTILITY);
		zydh.setLabel("专业代号");
		generalGroupConfig.addComponent(zydh);

	}

	/**
	 * 工序名称页面定制
	 *
	 * @param generalGroupConfig
	 * @param factory
	 */
	private static void initCreateGXMCAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig GXJH = factory.newAttributeConfig(SopConstants.SOP_IBA_GONGXUJIANHAO);
		GXJH.setDataUtilityId(DATAUTILITY);
		GXJH.setLabel("工序简号");
		generalGroupConfig.addComponent(GXJH);

		AttributeConfig englishName = factory.newAttributeConfig(SopConstants.SOP_IBA_ENGLISHNAME);
		englishName.setLabel("英文名称");
		englishName.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(englishName);

		AttributeConfig zylb1 = factory.newAttributeConfig(SopConstants.SOP_IBA_SPECIALIZEDTYPE);
		zylb1.setDataUtilityId(DATAUTILITY);
		zylb1.setLabel("专业类别");
		generalGroupConfig.addComponent(zylb1);

		AttributeConfig ZZCJ = factory.newAttributeConfig(SopConstants.SOP_IBA_ZZCJ);
		ZZCJ.setDataUtilityId(DATAUTILITY);
		ZZCJ.setLabel("主制车间");
		generalGroupConfig.addComponent(ZZCJ);
	}

	/**
	 * 参数项目名称页面定制
	 *
	 * @param generalGroupConfig
	 * @param factory
	 */
	private static void initCreateCSXMMCAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig zylb1 = factory.newAttributeConfig(SopConstants.SOP_IBA_SPECIALIZEDTYPE);
		zylb1.setDataUtilityId(DATAUTILITY);
		zylb1.setLabel("专业类别");
		generalGroupConfig.addComponent(zylb1);
	}

	/**
	 * 物资类别页面定制
	 *
	 * @param generalGroupConfig
	 * @param factory
	 */
	private static void initCreateWZLBAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig zylb1 = factory.newAttributeConfig(SopConstants.SOP_IBA_SPECIALIZEDTYPE);
		zylb1.setDataUtilityId(DATAUTILITY);
		zylb1.setLabel("专业类别");
		generalGroupConfig.addComponent(zylb1);
	}

}
