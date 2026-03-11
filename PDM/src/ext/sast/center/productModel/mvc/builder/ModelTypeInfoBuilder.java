package ext.sast.center.productModel.mvc.builder;

import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import ext.sast.center.productModel.util.SyncModeTypeHelper;
import wt.util.WTException;

import java.util.List;


@ComponentBuilder("ext.sast.center.productModel.mvc.builder.ModelTypeInfoBuilder")
public class ModelTypeInfoBuilder extends AbstractComponentBuilder{
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		List<TypeDefinitionReadView> list = SyncModeTypeHelper.getAllType();
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_LABEL"));
		table.setSelectable(true);
		table.setSingleSelect(true);
		table.setShowCount(true);
		table.setMenubarName("syncActionModelTypes");

		ColumnConfig col = factory.newColumnConfig("localHostType_ZH", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_01"));
		table.addComponent(col);

		col = factory.newColumnConfig("localHostType_US", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_02"));
		table.addComponent(col);

		col = factory.newColumnConfig("sastType_ZH", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_03"));
		table.addComponent(col);

		col = factory.newColumnConfig("sastType_US", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_04"));
		table.addComponent(col);
		return table;
	}

}
