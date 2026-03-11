package com.glaway.mpm.mvc.builders.pbom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.TreeHandlerAdapter;
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
import com.ptc.netmarkets.util.beans.NmCommandBean;

/**
 * @author Administrator
 *
 */
@ComponentBuilder("com.glaway.mpm.mvc.builders.pbom.PBOMTaskAllocateBuilder")
public class PBOMTaskAllocateBuilder extends AbstractComponentBuilder {
	private static final String RESOURCE = "com.glaway.mpm.pbom.ui.PBOMResource";
	private final ClientMessageSource messageSource = getMessageSource(RESOURCE);

	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {

		return new PBOMTaskAllocateBuilderTreeAdapter();
	}

	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();

		TreeConfig treeconfig = componentconfigfactory.newTreeConfig();
		treeconfig.setActionModel("technic toolbar actions");
		String s = messageSource.getMessage("TECHNIC_DESIGN_DISPATCHER");
		treeconfig.setLabel(s);
		treeconfig.setSelectable(true);
		treeconfig.setHelpContext("plan.table.help");
		treeconfig.setNodeColumn("name");
		treeconfig.setExpansionLevel(TableTreeProperties.FULL_EXPAND);

		JcaColumnConfig jcaColumnConfig = (JcaColumnConfig) componentconfigfactory.newColumnConfig(
				DescriptorConstants.ColumnIdentifiers.NM_ACTIONS, false);
		jcaColumnConfig.setDescriptorProperty(DescriptorConstants.ActionProperties.ACTION_MODEL,
				"changed_plan_rightmenu");
		treeconfig.addComponent(jcaColumnConfig);

		ColumnConfig columnTemp = componentconfigfactory.newColumnConfig(com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NON_SELECTABLE_COLUMN, false);
//		columnTemp.setDataUtilityId("hasBooleanValue");
		columnTemp.setDataStoreOnly(true);
		treeconfig.addComponent(columnTemp);
		treeconfig.setNonSelectableColumn(columnTemp);

		ColumnConfig columnconfig1 = componentconfigfactory.newColumnConfig();
		columnconfig1.setId("type_icon");
		columnconfig1.setSortable(false);
		treeconfig.addComponent(columnconfig1);

		ColumnConfig columnconfig2 = componentconfigfactory.newColumnConfig();
		columnconfig2.setId("smallThumbnail");
		columnconfig2.setSortable(false);
		treeconfig.addComponent(columnconfig2);

        ColumnConfig columnconfig3 = componentconfigfactory.newColumnConfig();
		columnconfig3.setId("name");
		columnconfig3.setSortable(false);
		columnconfig3.setWidth(100);
		treeconfig.addComponent(columnconfig3);

		ColumnConfig columnconfigNumber = componentconfigfactory.newColumnConfig();
		columnconfigNumber.setId("number");
		columnconfigNumber.setSortable(false);
		columnconfigNumber.setWidth(100);
		treeconfig.addComponent(columnconfigNumber);

		ColumnConfig columnconfig4 = componentconfigfactory.newColumnConfig();
		columnconfig4.setId("version");
		columnconfig4.setSortable(false);
		treeconfig.addComponent(columnconfig4);

		ColumnConfig columnconfig5 = componentconfigfactory.newColumnConfig("partTypeName", messageSource.getMessage("MATERIELTYPE"), true);
		columnconfig5.setSortable(false);
		columnconfig5.setWidth(100);
		treeconfig.addComponent(columnconfig5);

		ColumnConfig columnconfig6 = componentconfigfactory.newColumnConfig("technicGroup", messageSource.getMessage("TECHNICGROUP"), true);
		columnconfig6.setSortable(false);
		columnconfig6.setWidth(100);
		treeconfig.addComponent(columnconfig6);

		ColumnConfig columnconfig7 = componentconfigfactory.newColumnConfig("responsor", messageSource.getMessage("RESPONSER"), true);
		columnconfig7.setSortable(false);
		columnconfig7.setDataUtilityId("ShowUserName");
		treeconfig.addComponent(columnconfig7);

		ColumnConfig columnconfig8 = componentconfigfactory.newColumnConfig();
		columnconfig8.setId("modifyStamp");
		columnconfig8.setSortable(false);
		columnconfig8.setWidth(100);
		treeconfig.addComponent(columnconfig8);

		return treeconfig;
	}
}

class PBOMTaskAllocateBuilderTreeAdapter extends TreeHandlerAdapter {

	@SuppressWarnings(value={"unchecked"})
	public Map<Object, List> getNodes(List arg0) throws WTException {
		HashMap map = new HashMap();
//		for (Object obj : arg0) {
//			WTPart part = (WTPart) obj;
//
//			String lifecycleStatus = part.getLifeCycleState().getStringValue();
//
//			ArrayList<WTPart> list = (ArrayList<WTPart>) WTPartUtil.getChildPart(part);
//			ArrayList<WTPart> partlist = new ArrayList<WTPart>();
//
//			if(lifecycleStatus.endsWith(Constants.RELEASED) && PartNumberUtil.isZJNumber(part.getNumber())){
//				GLLogger.debug(part.getName() + "  is released and is zj");
//			}else{
//				for(int i = 0; i < list.size(); i++){
//					WTPart temp = list.get(i);
//					temp = WTPartUtil.getLatestPartByNumberAndView(temp,  Constants.planning);
//					partlist.add(temp);
//				}
//			}
//			map.put(part, partlist);
//		}
		return map;
	}

	@SuppressWarnings(value={"unchecked"})
	public List<Object> getRootNodes() throws WTException {
		NmCommandBean commandbean = getModelContext().getNmCommandBean();
		HttpServletRequest request = commandbean.getRequest();
		String oid = request.getParameter("oid");
		GLLogger.debug("oid====>" + oid);
		ReferenceFactory rf = new ReferenceFactory();
		WTPart ebomPart = (WTPart)rf.getReference(oid).getObject();
		WTPart pbomPart = WTPartUtil.getLatestPartByNumberAndView(ebomPart,  Constants.planning);
		ArrayList list = new ArrayList();
		list.add(pbomPart);
		return list;
	}

}
