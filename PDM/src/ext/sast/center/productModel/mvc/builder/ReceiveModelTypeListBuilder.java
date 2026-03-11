package ext.sast.center.productModel.mvc.builder;

import java.util.List;

import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;

import ext.sast.center.productModel.util.SyncModeTypeHelper;
import wt.util.WTException;

@ComponentBuilder("ext.sast.center.productModel.mvc.builder.ReceiveModelTypeListBuilder")
public class ReceiveModelTypeListBuilder extends AbstractComponentBuilder{
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		List<TypeDefinitionReadView> list = SyncModeTypeHelper.getAllType();
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_LABEL"));
		table.setSelectable(true);

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
		return table;
	}

}
