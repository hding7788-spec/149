package ext.sast.center.productModel.mvc.builder;

import java.util.List;

import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;

import ext.sast.center.productModel.bean.ReceivedModelTypeInfo;
import ext.sast.center.productModel.util.SyncModeTypeHelper;
import wt.util.WTException;

@ComponentBuilder("ext.sast.center.productModel.mvc.builder.ReceiveScopeModelTypeBuilder")
public class ReceiveScopeModelTypeBuilder extends AbstractComponentBuilder{
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		List<ReceivedModelTypeInfo> list = SyncModeTypeHelper.getReceiveScopeype();
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("接收类型范围设置");
		table.setSelectable(true);
		table.setShowCount(true);
		table.setMenubarName("receiveActionModelTypes");

		ColumnConfig col = factory.newColumnConfig("parent_modelType_name", true);
		col.setWidth(200);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_20"));
		table.addComponent(col);

		col = factory.newColumnConfig("parent_modelType_id", true);
		col.setWidth(200);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_21"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("model_type_name", true);
		col.setWidth(200);
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_01"));
		table.addComponent(col);

		col = factory.newColumnConfig("model_type_id", true);
		col.setWidth(200);
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_02"));
		table.addComponent(col);

	
		return table;
	}

}
