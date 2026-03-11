package ext.ases.techMaterial.mvc.builder;

import java.util.List;

import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.techMaterial.model.TechnicsMaterialLink;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;

@ComponentBuilder("ext.ases.techMaterial.mvc.builder.TechMaterialDictionaryTableBuilder")
public class TechMaterialDictionaryTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
		NmCommandBean cb = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
		String oid = cb.getActionOid().getOidObject().toString();
		List<TechnicsMaterialLink> list = TechnicsMaterialUtils.getDicIdByTechMaterialId(oid);
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setLabel("数据字典列表");
		tableConfig.setSelectable(true);
		tableConfig.setActionModel("tech_material_dictionary_table_actions");
		

		ColumnConfig nameColumnConfig = factory.newColumnConfig("dictionaryid", true);
		nameColumnConfig.setLabel("数据字典名称");
		nameColumnConfig.setAutoSize(true);
		tableConfig.addComponent(nameColumnConfig);

		ColumnConfig creatorColumnConfig = factory.newColumnConfig("tmcreator", true);
		creatorColumnConfig.setLabel("维护人");
		creatorColumnConfig.setAutoSize(true);
		tableConfig.addComponent(creatorColumnConfig);

		ColumnConfig createDataColumnConfig = factory.newColumnConfig("tmcreatetime", true);
		createDataColumnConfig.setLabel("维护时间");
		createDataColumnConfig.setAutoSize(true);
		tableConfig.addComponent(createDataColumnConfig);

		return tableConfig;
	}

}
