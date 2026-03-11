package com.glaway.mpm.mvc.builders.processplan.tempprocess;

import java.util.ArrayList;
import java.util.List;

import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

@ComponentBuilder("com.glaway.mpm.mvc.builders.processplan.tempprocess.SearchTempProcessTableBuilder")
public class SearchTempProcessTableBuilder extends AbstractConfigurableTableBuilder {
	private static int index[] = { 0 };

	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();

		TableConfig table = factory.newTableConfig();
		table.setLabel("工艺文件临时更改通知单");
		table.setSelectable(true);
		table.setConfigurable(false);

		ColumnConfig col1 = factory.newColumnConfig(AttributeConstants.number, "编号", true);
		table.addComponent(col1);

		ColumnConfig col2 = factory.newColumnConfig(AttributeConstants.name, "名称", true);
		table.addComponent(col2);

		return table;
	}

	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws WTException {

		List<WTChangeRequest2> list = new ArrayList<WTChangeRequest2>();
		
		return list;
	}

	public ConfigurableTable buildConfigurableTable(String arg0) throws WTException {
		return null;
	}

	public static void serach() {
		QueryResult result=new QueryResult();
	}

}
