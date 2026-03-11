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

public class TableConfigurationBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		List<TableConfigBean> result = TableConfigTreeHander.search();
		return result;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setActionModel("ccr_tableConfig_actions");
		table.setLabel("套表配置表");
		//table.setSelectable(true);
		ColumnConfig selectable = factory.newColumnConfig("selectTable", true);
		selectable.setLabel("");
		selectable.setWidth(20);
		selectable.setDataUtilityId("TableConfigDataUtility");
		table.addComponent(selectable);

		ColumnConfig childNumber = factory.newColumnConfig("gwKeyId",true);
		childNumber.setLabel("编号");
		childNumber.setWidth(100);
		childNumber.setRequired(true);
		childNumber.setDataUtilityId("TableConfigDataUtility");
		table.addComponent(childNumber);

		ColumnConfig childValue = factory.newColumnConfig("name",true);
		childValue.setLabel("套表名称");
		childValue.setWidth(100);
		childValue.setRequired(true);
		childValue.setDataUtilityId("TableConfigDataUtility");
		table.addComponent(childValue);


		return table;
	}
}
