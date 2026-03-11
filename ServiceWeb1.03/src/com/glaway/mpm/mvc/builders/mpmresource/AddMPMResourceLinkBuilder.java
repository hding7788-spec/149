package com.glaway.mpm.mvc.builders.mpmresource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wt.util.WTException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.Util;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmCommandBean;

@ComponentBuilder(value = "com.glaway.mpm.mvc.builders.mpmresource.AddMPMResourceLinkBuilder")
public class AddMPMResourceLinkBuilder extends AbstractComponentBuilder {

	private static final String RESOURCE = "com.glaway.mpm.mpmresource.ui.MPMResourceRB";
	ClientMessageSource messageSource = getMessageSource(RESOURCE);

	public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws Exception {
		List list = new ArrayList();
		NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		String mpmResourceType = (String) params.getParameter("mpmResourceType");
		HashMap textMap = commandBean.getText();
		String number = (String) textMap.get(AttributeConstants.number);
		String name = (String) textMap.get(AttributeConstants.name);
		if ((number != null && number.toString().trim() != "") || (name != null && name.toString().trim() != "")) {
			number = Util.formatSearchString(number);
			name = Util.formatSearchString(name);
			if (mpmResourceType.equals(Constants.GW)) {
				list = MPMResourceUtil.getWorkSpaceByLike(number, name);
			} else if (mpmResourceType.equals(Constants.GZhong)) {
				list = MPMResourceUtil.getSkillByLike(number, name);
			} else if (mpmResourceType.equals(Constants.SB)) {
				list = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.SB);
			} else if (mpmResourceType.equals(Constants.GXMC)) {
				list = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.GXMC);
			} else if (mpmResourceType.equals(Constants.GYCYY)) {
				list = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.GYCYY);


			} else if (mpmResourceType.equals(Constants.DMSB)) {
                list = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.DMSB);
			} else if (mpmResourceType.equals(Constants.GJ)) {
				list = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.GJ);
			} else if (mpmResourceType.equals(Constants.ChildSB)) {
				list = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.SB);
			} else if (mpmResourceType.equals(Constants.GZhong2)) {
				list = MPMResourceUtil.getSkillByLike(number, name);
			}
		}
		return list;
	}

	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		String mpmResourceType = (String) arg0.getParameter("mpmResourceType");
		ComponentConfigFactory factory = getComponentConfigFactory();

		TableConfig table = factory.newTableConfig();
		table.setSelectable(true);
		table.setId("AddMPMResourceLinkTable");
		if (mpmResourceType.equals(Constants.GW)) {
			table.setLabel("工位");
		} else if (mpmResourceType.equals(Constants.GZhong)) {
			table.setLabel("工种");
		} else if (mpmResourceType.equals(Constants.SB)) {
			table.setLabel("设备");
		} else if (mpmResourceType.equals(Constants.GXMC)) {
			table.setLabel("工序名称");
		} else if (mpmResourceType.equals(Constants.GYCYY)) {
			table.setLabel("工艺常用语");



		} else if (mpmResourceType.equals(Constants.DMSB)) {
            table.setLabel("地面设备");
		} else if (mpmResourceType.equals(Constants.GJ)) {
			table.setLabel("工量具");
		} else if (mpmResourceType.equals(Constants.ChildSB)) {
			table.setLabel("设备");
		}

		ColumnConfig icon = factory.newColumnConfig("type_icon", false);
		table.addComponent(icon);

		ColumnConfig number = factory.newColumnConfig(AttributeConstants.number, false);
		table.addComponent(number);

		ColumnConfig name = factory.newColumnConfig(AttributeConstants.name, false);
		table.addComponent(name);

		return table;
	}

}
