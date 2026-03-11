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

@ComponentBuilder("ext.sast.center.productModel.mvc.builder.ReceivedModelTypeListBuilder")
public class ReceivedModelTypeListBuilder extends AbstractComponentBuilder{
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		List<ReceivedModelTypeInfo> list = SyncModeTypeHelper.getReceivedType();
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("中心域模型转本地模型");
		table.setSelectable(true);
		table.setSingleSelect(true);
		table.setShowCount(true);
		table.setMenubarName("receivedActionModelTypes");

		ColumnConfig col = factory.newColumnConfig("innerId", true);
		col.setHidden(true);
		table.addComponent(col);
		
		col = factory.newColumnConfig("sast_modeltypename", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_03"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("sast_modeltypeid", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_04"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("local_modeltypename", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_01"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("local_modeltypeid", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("MODELTYPEINFOBUILDER_02"));
		table.addComponent(col);
		
		
		return table;
	}

}
