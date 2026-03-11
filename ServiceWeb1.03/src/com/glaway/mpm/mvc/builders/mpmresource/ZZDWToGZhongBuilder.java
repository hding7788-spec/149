package com.glaway.mpm.mvc.builders.mpmresource;

import java.util.ArrayList;
import java.util.List;

import wt.part.WTPart;
import wt.type.TypedUtility;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMSkill;

@ComponentBuilder(value = "com.glaway.mpm.mvc.builders.mpmresource.ZZDWToGZhongBuilder")
public class ZZDWToGZhongBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params)
			throws Exception {
		List list = new ArrayList();
		NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		Object object = commandBean.getPrimaryOid().getRef();
		if (object instanceof MPMPlant) {
			MPMPlant plant = (MPMPlant) object;
			for (WTPart childPart : WTPartUtil.getChildPart(plant)) {
				if (childPart instanceof MPMSkill) {
					String typeName = TypedUtility.getTypeIdentifier(childPart).getTypename();
					if (typeName.contains(TypeNameConstants.GZhong)) {
						list.add(childPart);
					}
				}
			}
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setId("ZZDWToGZhongLinkTable");
		table.setActionModel("deelWithZZDWToGZhongLink_toolbar");
		table.setLabel("工种");
		table.setSelectable(true);

		ColumnConfig icon = factory.newColumnConfig("type_icon", false);
		table.addComponent(icon);

		ColumnConfig number = factory.newColumnConfig(AttributeConstants.number, false);
		table.addComponent(number);

		ColumnConfig name = factory.newColumnConfig(AttributeConstants.name, false);
		table.addComponent(name);

		return table;
	}

}
