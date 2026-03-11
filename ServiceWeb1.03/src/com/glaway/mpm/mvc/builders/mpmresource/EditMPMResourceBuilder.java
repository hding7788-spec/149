package com.glaway.mpm.mvc.builders.mpmresource;


import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.AbstractAttributesComponentBuilder;
import com.ptc.jca.mvc.components.JcaAttributePanelConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.util.IBAUtility;
import wt.type.TypedUtility;
import wt.util.WTException;

@ComponentBuilder({ "com.glaway.mpm.mvc.builders.mpmresource.EditMPMResourceBuilder" })
public class EditMPMResourceBuilder extends AbstractAttributesComponentBuilder {

	private static final String RESOURCE = "com.glaway.mpm.mpmresource.ui.MPMResourceRB";
	private static String DATAUTILITY = "MPMResourceInfoDatautility";
	ClientMessageSource messageSource = getMessageSource(RESOURCE);
	static String typeName3 = "";
	static boolean isSOPGXMC = true;

	@Override
	protected CustomizableViewConfig buildAttributesComponentConfig(ComponentParams paramComponentParams) throws WTException {
		NmCommandBean commandBean = ((JcaComponentParams) paramComponentParams).getHelperBean().getNmCommandBean();
		NmOid nmOid = commandBean.getActionOid();
		Object object = nmOid.getRef();
		String typename2 = TypedUtility.getTypeIdentifier(object).getTypename();
		if (typename2.equals(TypeNameConstants.CSXM) || typename2.equals(TypeNameConstants.CSXMMC) || typename2.equals(TypeNameConstants.CZGW) || typename2.equals(TypeNameConstants.DZQY)
				|| typename2.equals(TypeNameConstants.GXMC) || typename2.equals(TypeNameConstants.WZLB) || typename2.equals(TypeNameConstants.ZYLB)) {
			typeName3 = "sop_";
		}
		if (object instanceof MPMTooling && typename2.equals(TypeNameConstants.GXMC)) {
			MPMTooling tooling = (MPMTooling) object;
			IBAUtility utility = new IBAUtility(tooling);
			String zylb = utility.getIBAValue("SpecializedType");
			if ("".equals(zylb) || zylb == null || "null".equals(zylb)) {
				typeName3 = "";
				isSOPGXMC = false;
			}
		}
		ComponentConfigFactory factory = getComponentConfigFactory();
		ComponentMode localComponentMode = getComponentMode(paramComponentParams);
		JcaAttributePanelConfig panelConfig = (JcaAttributePanelConfig) factory.newAttributePanelConfig(ComponentId.ATTRIBUTE_PANEL_ID);
		panelConfig.setComponentMode(localComponentMode);

		GroupConfig generalGroupConfig = factory.newGroupConfig("General");
		generalGroupConfig.setLabel("基本属性");
		AttributeConfig id = factory.newAttributeConfig(typeName3 + AttributeConstants.number, "编号");
		id.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(id);

		if ((TypedUtility.getTypeIdentifier(object).getTypename()).equals(TypeNameConstants.CSXM)) {
			AttributeConfig ZYLB = factory.newAttributeConfig(typeName3 + AttributeConstants.SpecializedType);
			ZYLB.setDataUtilityId(DATAUTILITY);
			ZYLB.setLabel("专业类别");
			generalGroupConfig.addComponent(ZYLB);

			AttributeConfig name = factory.newAttributeConfig(typeName3 + AttributeConstants.ParametersName, "参数项目名称");
			name.setDataUtilityId(DATAUTILITY);
			generalGroupConfig.addComponent(name);

		} else {
			AttributeConfig name = factory.newAttributeConfig(typeName3 + AttributeConstants.name, "名称");
			name.setDataUtilityId(DATAUTILITY);
			generalGroupConfig.addComponent(name);
		}

		// add by Mchen
		if (object instanceof MPMTooling) {
			String typeName = TypedUtility.getTypeIdentifier(object).getTypename();
			if ((!typeName.contains("SpecializedType")) && (!typeName.contains("OperationJob")) && (!typeName.contains("Parameters")) && (!typeName.contains("CustomArea"))
					&& (!typeName.contains("ParametersName")) && (!typeName.contains("MaterialCategory"))) {
				AttributeConfig englishName = factory.newAttributeConfig(typeName3 + AttributeConstants.ENGLISHNAME, "英文名称");
				englishName.setDataUtilityId(DATAUTILITY);
				generalGroupConfig.addComponent(englishName);
			}
		} else {
			AttributeConfig englishName = factory.newAttributeConfig(AttributeConstants.ENGLISHNAME, "英文名称");
			englishName.setDataUtilityId(DATAUTILITY);
			generalGroupConfig.addComponent(englishName);
		}

		if (object instanceof MPMProcessMaterial) {
			initEditGYFLAttributePage(generalGroupConfig, factory);
		} else if (object instanceof MPMTooling) {
			String typeName = TypedUtility.getTypeIdentifier(object).getTypename();
			if (typeName.contains(TypeNameConstants.SB)) {
				initEditSBAttributePage(generalGroupConfig, factory, typeName);
			} else if (typeName.equals(TypeNameConstants.GZhuang)) {
				initCreateGZhuangAttributePanel(generalGroupConfig, factory, typeName);
			} else if (typeName.equals(TypeNameConstants.GJ)) {
				initCreateGJAttributePanel(generalGroupConfig, factory);
			} else if (typeName.equals(TypeNameConstants.DJ)) {
				initCreateDJAttributePanel(generalGroupConfig, factory);
			} else if (typeName.equals(TypeNameConstants.LJ)) {
				initCreateLJAttributePanel(generalGroupConfig, factory);
			} else if (typeName.equals(TypeNameConstants.YQYB)) {
				initCreateYQYBAttributePanel(generalGroupConfig, factory);
			} else if (typeName.equals(TypeNameConstants.GXMC)) {
				initCreateGXMCAttributePanel(generalGroupConfig, factory);
			} else if (typeName.equals(TypeNameConstants.CSXM)) {
				initCreateCSXMAttributePanel(generalGroupConfig, factory);
			} else if (typeName.equals(TypeNameConstants.CZGW)) {
				initCreateCZGWAttributePanel(generalGroupConfig, factory);
			} else if (typeName.equals(TypeNameConstants.ZYLB)) {
				initCreateZYLBAttributePanel(generalGroupConfig, factory);
			} else if (typeName.equals(TypeNameConstants.CSXMMC)) {
				initCreateCSXMMCAttributePanel(generalGroupConfig, factory);
			} else if (typeName.equals(TypeNameConstants.WZLB)) {
				initCreateWZLBAttributePanel(generalGroupConfig, factory);
			}

		}

		String typeName1 = TypedUtility.getTypeIdentifier(object).getTypename();
		if (!typeName1.equals(TypeNameConstants.DMSB)) {

			AttributeConfig des = factory.newAttributeConfig(AttributeConstants.remarkKey);
			des.setDataUtilityId(DATAUTILITY);
			des.setLabel("说明");
			generalGroupConfig.addComponent(des);

		}

		panelConfig.addComponent(generalGroupConfig);
		return panelConfig;
	}

	private static void initCreateGZhuangAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory, String typeName) {
		AttributeConfig pindex = factory.newAttributeConfig(AttributeConstants.frocktype);
		pindex.setDataUtilityId(DATAUTILITY);
		pindex.setLabel("工装类别");
		generalGroupConfig.addComponent(pindex);
	}

	private static void initEditGYFLAttributePage(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {
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

	private static void initEditSBAttributePage(GroupConfig generalGroupConfig, ComponentConfigFactory factory, String typeName) {
		AttributeConfig pindex = factory.newAttributeConfig(AttributeConstants.mindex);
		pindex.setDataUtilityId(DATAUTILITY);
		pindex.setLabel("型号");
		generalGroupConfig.addComponent(pindex);

		AttributeConfig csize = factory.newAttributeConfig(AttributeConstants.csize);
		csize.setDataUtilityId(DATAUTILITY);
		csize.setLabel("规格");
		generalGroupConfig.addComponent(csize);

		AttributeConfig equipmentType = factory.newAttributeConfig(AttributeConstants.equipmentType);
		equipmentType.setDataUtilityId(DATAUTILITY);
		equipmentType.setLabel("设备类别");
		generalGroupConfig.addComponent(equipmentType);
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

	private static void initCreateGXMCAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {
		if (isSOPGXMC) {
			AttributeConfig GXJH = factory.newAttributeConfig(typeName3 + AttributeConstants.GONGXUJIANHAO);
			GXJH.setDataUtilityId(DATAUTILITY);
			GXJH.setLabel("工序简号");
			generalGroupConfig.addComponent(GXJH);

			AttributeConfig zylb1 = factory.newAttributeConfig(typeName3 + AttributeConstants.SpecializedType);
			zylb1.setDataUtilityId(DATAUTILITY);
			zylb1.setLabel("专业类别");
			generalGroupConfig.addComponent(zylb1);
		}

		AttributeConfig ZZCJ = factory.newAttributeConfig(typeName3 + AttributeConstants.ZZCJ, "主制车间");
		ZZCJ.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(ZZCJ);

		//add by yfn 20250702 添加修改【是否设备工时】
		AttributeConfig sfsbgs = factory.newAttributeConfig(typeName3 + AttributeConstants.SFSBGS, "是否设备工时");
		sfsbgs.setDataUtilityId(DATAUTILITY);
		generalGroupConfig.addComponent(sfsbgs);

		isSOPGXMC = true;
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

	private static void initCreateCSXMAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig GXMC = factory.newAttributeConfig(typeName3 + AttributeConstants.ProceduceName);
		GXMC.setDataUtilityId(DATAUTILITY);
		GXMC.setLabel("工序名称");
		generalGroupConfig.addComponent(GXMC);

		AttributeConfig wzlb = factory.newAttributeConfig(typeName3 + AttributeConstants.MaterialCategory);
		wzlb.setDataUtilityId(DATAUTILITY);
		wzlb.setLabel("物资类别");
		generalGroupConfig.addComponent(wzlb);

		AttributeConfig csz = factory.newAttributeConfig(typeName3 + AttributeConstants.CANSHUZHI);
		csz.setDataUtilityId(DATAUTILITY);
		csz.setLabel("参数值");
		generalGroupConfig.addComponent(csz);
	}

	private static void initCreateCZGWAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig ZZCJ = factory.newAttributeConfig(typeName3 + AttributeConstants.ZZCJ);
		ZZCJ.setDataUtilityId(DATAUTILITY);
		ZZCJ.setLabel("车间");
		generalGroupConfig.addComponent(ZZCJ);

	}

	private static void initCreateZYLBAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig zydh = factory.newAttributeConfig(typeName3 + AttributeConstants.ProfessionalCode);
		zydh.setDataUtilityId(DATAUTILITY);
		zydh.setLabel("专业代号");
		generalGroupConfig.addComponent(zydh);
	}

	private static void initCreateCSXMMCAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {
		AttributeConfig ZYLB = factory.newAttributeConfig(typeName3 + AttributeConstants.SpecializedType);
		ZYLB.setDataUtilityId(DATAUTILITY);
		ZYLB.setLabel("专业类别");
		generalGroupConfig.addComponent(ZYLB);

	}

	private static void initCreateWZLBAttributePanel(GroupConfig generalGroupConfig, ComponentConfigFactory factory) {

		AttributeConfig ZYLB = factory.newAttributeConfig(typeName3 + AttributeConstants.SpecializedType);
		ZYLB.setDataUtilityId(DATAUTILITY);
		ZYLB.setLabel("专业类别");
		generalGroupConfig.addComponent(ZYLB);

	}

}
