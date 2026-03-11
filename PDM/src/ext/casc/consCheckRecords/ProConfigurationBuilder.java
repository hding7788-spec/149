package ext.casc.consCheckRecords;

import java.util.List;

import wt.util.WTException;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.consCheckRecords.ProConfigBean;
import ext.casc.consCheckRecords.ProConfigTreeHander;
import ext.casc.doc.technology.TechnicsTechnologyTreeHander;
import ext.casc.doc.technology.TechnologyBean;

public class ProConfigurationBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		List<ProConfigBean> result = ProConfigTreeHander.search();
		return result;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setActionModel("ccr_proConfig_actions");
		table.setLabel("项目配置表");
		//table.setSelectable(true);
		ColumnConfig selectable = factory.newColumnConfig("selectConfig",true);
		selectable.setLabel("");
		selectable.setWidth(20);
		selectable.setDataUtilityId("ProConfigDataUtility");
		table.addComponent(selectable);

		ColumnConfig childNumber = factory.newColumnConfig("gwKeyId",true);
		childNumber.setLabel("编号");
		childNumber.setWidth(100);
		childNumber.setRequired(true);
		childNumber.setHidden(true);
		childNumber.setDataUtilityId("ProConfigDataUtility");
		table.addComponent(childNumber);

		ColumnConfig childValue = factory.newColumnConfig("value",true);
		childValue.setLabel("项目名");
		childValue.setWidth(100);
		childValue.setRequired(true);
		childValue.setDataUtilityId("ProConfigDataUtility");
		table.addComponent(childValue);

		ColumnConfig childType = factory.newColumnConfig("type1",true);
		childType.setLabel("类型(检测类/记录类)");
		childType.setWidth(100);
		childType.setRequired(true);
		childType.setDataUtilityId("ProConfigDataUtility");
		table.addComponent(childType);

		return table;
	}
}
