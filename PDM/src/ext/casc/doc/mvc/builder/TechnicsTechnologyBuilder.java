package ext.casc.doc.mvc.builder;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.doc.technology.TechnicsTechnologyTreeHander;
import ext.casc.doc.technology.TechnologyBean;
import wt.util.WTException;

import java.util.List;

public class TechnicsTechnologyBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {

		NmCommandBean commandbean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
        String dalei = (String) commandbean.getText().get("dalei");
		List<TechnologyBean> result = TechnicsTechnologyTreeHander.search(dalei,null);
		return result;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setActionModel("custom_TechnicsTechnologyManager_actions");
		table.setLabel("小类列表");
		table.setSelectable(true);

		ColumnConfig childNumber = factory.newColumnConfig("childNumber",true);
		childNumber.setLabel("小类编号");
		childNumber.setWidth(100);
		childNumber.setRequired(true);
		childNumber.setDataUtilityId("TechnicsTechnologyDataUtility");
		table.addComponent(childNumber);

		ColumnConfig childName = factory.newColumnConfig("childName",true);
		childName.setLabel("小类名称");
		childName.setWidth(600);
		childName.setRequired(true);
		childName.setDataUtilityId("TechnicsTechnologyDataUtility");
		table.addComponent(childName);

		ColumnConfig status = factory.newColumnConfig("status",true);
		status.setLabel("状态");
		status.setWidth(100);
		status.setRequired(true);
		status.setDataUtilityId("TechnicsTechnologyDataUtility");
		table.addComponent(status);
		return table;
	}

}
