package com.glaway.mpm.mvc.builders.mpmresource;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.GLLogger;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.util.WTException;

@ComponentBuilder( { "com.glaway.mpm.mvc.builders.mpmresource.CreateMPMResourceBuilder" })
public class CreateMPMResourceBuilder extends AbstractAttributesComponentBuilder {
	private static final String CLASSNAME = CreateMPMResourceBuilder.class.getName();
	private static final String RESOURCE = "com.glaway.mpm.mpmresource.ui.MPMResourceRB";
	private static String DATAUTILITY = "CreateMPMResourceDatautility";
	ClientMessageSource messageSource = getMessageSource(RESOURCE);

	@Override
	protected CustomizableViewConfig buildAttributesComponentConfig(ComponentParams paramComponentParams)
			throws WTException {
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
		AttributeConfig id = factory.newAttributeConfig(AttributeConstants.number, "编号");
		id.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(id);

		AttributeConfig name = factory.newAttributeConfig(AttributeConstants.name, "名称");
		name.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(name);

		//add by Mchen
		AttributeConfig englishName = factory.newAttributeConfig(AttributeConstants.ENGLISHNAME, "英文名称");
		englishName.setDataUtilityId(DATAUTILITY);
        generalGroupConfig.addComponent(englishName);


		if (mpmResourceType.equals(Constants.GZhong)||mpmResourceType.equals(Constants.GW)) {
		    AttributeConfig zhizaodanwei = factory.newAttributeConfig(AttributeConstants.zhizaodanwei, "制造单位");
	        zhizaodanwei.setDataUtilityId(DATAUTILITY);
	        generalGroupConfig.addComponent(zhizaodanwei);
		}


		if (mpmResourceType.equals(Constants.GJ)) {
			initCreateGJAttributePanel(generalGroupConfig, factory);
		} else if (mpmResourceType.equals(Constants.DJ)) {
			initCreateDJAttributePanel(generalGroupConfig, factory);
		}  else if (mpmResourceType.equals(Constants.LJ)) {
			initCreateLJAttributePanel(generalGroupConfig, factory);
		} else if (mpmResourceType.equals(Constants.YQYB)) {
			initCreateYQYBAttributePanel(generalGroupConfig, factory);
		}  else if (mpmResourceType.equals(Constants.ZZDW)) {
		} else if (mpmResourceType.equals(Constants.GW)) {
		} else if (mpmResourceType.equals(Constants.GZhong)) {
        } else if (mpmResourceType.equals(Constants.GZhuang)) {
            initCreateGZhuangAttributePanel(generalGroupConfig, factory, typeName);
		} else if (mpmResourceType.equals(Constants.SB)) {
			initCreateSBAttributePanel(generalGroupConfig, factory, typeName);
		} else if (mpmResourceType.equals(Constants.GYFL)) {
			initCreateGYFLAttributePanel(generalGroupConfig, factory);
		} else if (mpmResourceType.equals(Constants.GXMC)) {
		    initCreateGXMCAttributePanel(generalGroupConfig, factory);
		} else if (mpmResourceType.equals(Constants.GYCYY)) {
		} else if (mpmResourceType.equals(Constants.DMSB))  {
	    } else if (mpmResourceType.equals(Constants.GWWH))  {
	    	initCreateGWWHAttributePanel(generalGroupConfig, factory, typeName);
	    }


	if (!mpmResourceType.equals(Constants.DMSB)){

		AttributeConfig des = factory.newAttributeConfig(AttributeConstants.remarkKey);
		des.setDataUtilityId(DATAUTILITY);
		des.setLabel("说明");
        generalGroupConfig.addComponent(des);

	  }

		panelConfig.addComponent(generalGroupConfig);
		return panelConfig;
	}

	private static void initCreateGYFLAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig csize = factory.newAttributeConfig(AttributeConstants.csize);
		csize.setDataUtilityId(DATAUTILITY);
		csize.setLabel("规格");
		generalGroupConfig.addComponent(csize);

		AttributeConfig mindex = factory.newAttributeConfig(AttributeConstants.mindex);
		mindex.setDataUtilityId(DATAUTILITY);
		mindex.setLabel("型号");
		generalGroupConfig.addComponent(mindex);

		AttributeConfig jldw = factory.newAttributeConfig(AttributeConstants.jldw);
		jldw.setDataUtilityId(DATAUTILITY);
		jldw.setLabel("计量单位");
		generalGroupConfig.addComponent(jldw);

		AttributeConfig jstj = factory.newAttributeConfig(AttributeConstants.jstj);
		jstj.setDataUtilityId(DATAUTILITY);
		jstj.setLabel("技术条件");
		generalGroupConfig.addComponent(jstj);

		AttributeConfig fjtj = factory.newAttributeConfig(AttributeConstants.fjtj);
		fjtj.setDataUtilityId(DATAUTILITY);
		fjtj.setLabel("附加条件");
		generalGroupConfig.addComponent(fjtj);
	}

	private static void initCreateGZhuangAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory,
            String typeName) {
        AttributeConfig pindex = factory.newAttributeConfig(AttributeConstants.frocktype);
        pindex.setDataUtilityId(DATAUTILITY);
        pindex.setLabel("工装类别");
        generalGroupConfig.addComponent(pindex);
	}
	private static void initCreateGWWHAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory,
            String typeName) {
        AttributeConfig GWWZ = factory.newAttributeConfig(AttributeConstants.workplace);
        GWWZ.setDataUtilityId(DATAUTILITY);
        GWWZ.setLabel("工位位置");
        generalGroupConfig.addComponent(GWWZ);
	}
	private static void initCreateGXMCAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {
        AttributeConfig ZZCJ = factory.newAttributeConfig(AttributeConstants.ZZCJ);
        ZZCJ.setDataUtilityId(DATAUTILITY);
        ZZCJ.setLabel("主制车间");
        generalGroupConfig.addComponent(ZZCJ);

		//add by yfn 20250702 添加修改【是否设备工时】
		AttributeConfig  sfsbgs = factory.newAttributeConfig(AttributeConstants.SFSBGS);
		sfsbgs.setDataUtilityId(DATAUTILITY);
		sfsbgs.setLabel("是否设备工时");
		generalGroupConfig.addComponent(sfsbgs);
    }


	private static void initCreateSBAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory,
			String typeName) {
		AttributeConfig pindex = factory.newAttributeConfig(AttributeConstants.mindex);
		pindex.setDataUtilityId(DATAUTILITY);
		pindex.setLabel("型号");
		generalGroupConfig.addComponent(pindex);

		AttributeConfig csize = factory.newAttributeConfig(AttributeConstants.csize);
		csize.setDataUtilityId(DATAUTILITY);
		csize.setLabel("规格");
		generalGroupConfig.addComponent(csize);

		//AttributeConfig equipmentType = factory.newAttributeConfig(AttributeConstants.equipmentType);
		//equipmentType.setDataUtilityId(DATAUTILITY);
		//equipmentType.setLabel("设备类别");
		//generalGroupConfig.addComponent(equipmentType);
	}

	private static void initCreateGJAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig clph = factory.newAttributeConfig(AttributeConstants.mindex);
		clph.setDataUtilityId(DATAUTILITY);
		clph.setLabel("型号");
		generalGroupConfig.addComponent(clph);

		AttributeConfig clgg = factory.newAttributeConfig(AttributeConstants.csize);
		clgg.setDataUtilityId(DATAUTILITY);
		clgg.setLabel("规格");
		generalGroupConfig.addComponent(clgg);
	}

	private static void initCreateLJAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig clph = factory.newAttributeConfig(AttributeConstants.mindex);
		clph.setDataUtilityId(DATAUTILITY);
		clph.setLabel("型号");
		generalGroupConfig.addComponent(clph);

		AttributeConfig clgg = factory.newAttributeConfig(AttributeConstants.csize);
		clgg.setDataUtilityId(DATAUTILITY);
		clgg.setLabel("规格");
		generalGroupConfig.addComponent(clgg);
	}

	private static void initCreateYQYBAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig clph = factory.newAttributeConfig(AttributeConstants.mindex);
		clph.setDataUtilityId(DATAUTILITY);
		clph.setLabel("型号");
		generalGroupConfig.addComponent(clph);

		AttributeConfig clgg = factory.newAttributeConfig(AttributeConstants.csize);
		clgg.setDataUtilityId(DATAUTILITY);
		clgg.setLabel("规格");
		generalGroupConfig.addComponent(clgg);
	}

	private static void initCreateDJAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig clph = factory.newAttributeConfig(AttributeConstants.knifetype);
		clph.setDataUtilityId(DATAUTILITY);
		clph.setLabel("刀具类别");
		generalGroupConfig.addComponent(clph);

		AttributeConfig cmat = factory.newAttributeConfig(AttributeConstants.cmat);
		cmat.setDataUtilityId(DATAUTILITY);
		cmat.setLabel("材料");
		generalGroupConfig.addComponent(cmat);

		AttributeConfig rkzj = factory.newAttributeConfig(AttributeConstants.rkzj);
		rkzj.setDataUtilityId(DATAUTILITY);
		rkzj.setLabel("刃口直径");
		generalGroupConfig.addComponent(rkzj);

		AttributeConfig jczj = factory.newAttributeConfig(AttributeConstants.jczj);
		jczj.setDataUtilityId(DATAUTILITY);
		jczj.setLabel("夹持直径");
		generalGroupConfig.addComponent(jczj);

		AttributeConfig rkcd = factory.newAttributeConfig(AttributeConstants.rkcd);
		rkcd.setDataUtilityId(DATAUTILITY);
		rkcd.setLabel("刃口长度");
		generalGroupConfig.addComponent(rkcd);

		AttributeConfig zcd = factory.newAttributeConfig(AttributeConstants.zcd);
		zcd.setDataUtilityId(DATAUTILITY);
		zcd.setLabel("总长度");
		generalGroupConfig.addComponent(zcd);

		AttributeConfig gc = factory.newAttributeConfig(AttributeConstants.gc);
		gc.setDataUtilityId(DATAUTILITY);
		gc.setLabel("公差");
		generalGroupConfig.addComponent(gc);

		AttributeConfig zxjgcc = factory.newAttributeConfig(AttributeConstants.zxjgcc);
		zxjgcc.setDataUtilityId(DATAUTILITY);
		zxjgcc.setLabel("最小加工尺寸");
		generalGroupConfig.addComponent(zxjgcc);

		AttributeConfig zdjgcc = factory.newAttributeConfig(AttributeConstants.zdjgcc);
		zdjgcc.setDataUtilityId(DATAUTILITY);
		zdjgcc.setLabel("最大加工尺寸");
		generalGroupConfig.addComponent(zdjgcc);

		AttributeConfig jgxs = factory.newAttributeConfig(AttributeConstants.jgxs);
		jgxs.setDataUtilityId(DATAUTILITY);
		jgxs.setLabel("结构形式");
		generalGroupConfig.addComponent(jgxs);

		AttributeConfig jklx = factory.newAttributeConfig(AttributeConstants.jklx);
		jklx.setDataUtilityId(DATAUTILITY);
		jklx.setLabel("接口类型");
		generalGroupConfig.addComponent(jklx);

		AttributeConfig jsbz = factory.newAttributeConfig(AttributeConstants.jsbz);
		jsbz.setDataUtilityId(DATAUTILITY);
		jsbz.setLabel("技术备注");
		generalGroupConfig.addComponent(jsbz);

		AttributeConfig rkyjbj = factory.newAttributeConfig(AttributeConstants.rkyjbj);
		rkyjbj.setDataUtilityId(DATAUTILITY);
		rkyjbj.setLabel("刃口圆角半径");
		generalGroupConfig.addComponent(rkyjbj);

		AttributeConfig cs = factory.newAttributeConfig(AttributeConstants.cs);
		cs.setDataUtilityId(DATAUTILITY);
		cs.setLabel("齿数");
		generalGroupConfig.addComponent(cs);
	}
}
