package com.glaway.mpm.mvc.builders.pbom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.core.components.descriptor.DescriptorConstants;
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
 * 工艺签审过程中查看PBOM
 * @author lbzhang
 *
 */
@ComponentBuilder("com.glaway.mpm.mvc.builders.pbom.TechnicSignedBrowsePBOMBuilder")
public class TechnicSignedBrowsePBOMBuilder extends AbstractComponentBuilder {

	private static final String RESOURCE = "com.glaway.mpm.pbom.ui.PBOMResource";
	private final ClientMessageSource messageSource = getMessageSource(RESOURCE);

	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		return new TechnicSignBrowsePBOMTreeAdapter();
	}

	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();

		TreeConfig treeconfig = componentconfigfactory.newTreeConfig();

		treeconfig.setActionModel("technic_signed_browse_toolbar_actions");
		treeconfig.setLabel(messageSource.getMessage("TECHNIC_PBOM_BROWSER"));
		treeconfig.setSelectable(false);
		treeconfig.setHelpContext("plan.table.help");
		treeconfig.setNodeColumn("name");

		JcaColumnConfig jcaColumnConfig = (JcaColumnConfig)componentconfigfactory.newColumnConfig(DescriptorConstants.ColumnIdentifiers.NM_ACTIONS, false);
		jcaColumnConfig.setDescriptorProperty(DescriptorConstants.ActionProperties.ACTION_MODEL, "changed_plan_rightmenu");

		ColumnConfig icon = componentconfigfactory.newColumnConfig("type_icon",false);
		treeconfig.addComponent(icon);

		ColumnConfig columnName = componentconfigfactory.newColumnConfig();
		columnName.setId("name");
		columnName.setSortable(false);
		treeconfig.addComponent(columnName);

		ColumnConfig columnNumber = componentconfigfactory.newColumnConfig();
		columnNumber.setId("number");
		columnNumber.setSortable(false);
		treeconfig.addComponent(columnNumber);

		ColumnConfig columnVersion = componentconfigfactory.newColumnConfig();
		columnVersion.setId("version");
		columnVersion.setSortable(false);
		treeconfig.addComponent(columnVersion);

		return treeconfig;
	}
}

class TechnicSignBrowsePBOMTreeAdapter extends TreeHandlerAdapter{

	@SuppressWarnings(value={"unchecked"})
	public Map<Object, List> getNodes(List arg0) throws WTException {

		HashMap map = new HashMap();
		for (Object obj : arg0) {
			WTPart part = (WTPart) obj;
			ArrayList<WTPart> list = (ArrayList<WTPart>) WTPartUtil.getChildPart(part);
			ArrayList<WTPart> partlist = new ArrayList<WTPart>();
			for(int i = 0; i < list.size(); i++){
				WTPart temp = list.get(i);
//				temp = WTPartUtil.getSameVersionPlanningPart(temp);
				temp = WTPartUtil.getLatestPartByNumberAndView(temp,  Constants.planning);
				partlist.add(temp);
			}
			map.put(part, partlist);
		}
		return map;
	}

	@SuppressWarnings(value={"unchecked"})
	public List<Object> getRootNodes() throws WTException {
		NmCommandBean commandBean = getModelContext().getNmCommandBean();
		HttpServletRequest request = commandBean.getRequest();
		String oid = request.getParameter("oid");
		GLLogger.debug("oid====>" + oid);
		WTPart part = (WTPart) ReferenceFactory.getObjectbyOid("wt.part.WTPart:" + oid);//主对象为Planning视图的零件

//		WTPart pbomPart = WTPartUtil.getSameVersionPlanningPart(part);
		WTPart pbomPart = WTPartUtil.getLatestPartByNumberAndView(part,  Constants.planning);
		ArrayList list = new ArrayList();
		list.add(pbomPart);
		return list;
	}
}
