package ext.sast.center.productModel.mvc.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCTypeDefinition;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.dsvcore.server.utils.PersistableHelper;

import wt.util.WTException;

@ComponentBuilder("ext.sast.center.productModel.mvc.builder.ModelAttributeInfoTableBuilder")
public class ModelAttributeInfoTableBuilder extends AbstractComponentBuilder{
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams componentparams) throws Exception {
		List<AttributeDefinitionReadView> list = new ArrayList<AttributeDefinitionReadView>();
		NmCommandBean commandBean = ((JcaComponentParams)componentparams).getHelperBean().getNmCommandBean();
		List<?> selectedList = commandBean.getSelectedOidForPopup();
		String oid = selectedList.get(0).toString();
		LWCTypeDefinition lwcType = (LWCTypeDefinition)PersistableHelper.findPersistable(oid);
		String displayType = lwcType.getDisplayIdentifier().getLocalizedMessage(Locale.CHINA);
		TypeDefinitionReadView ty = TypeDefinitionServiceHelper.service.getTypeDefView(displayType);
		Collection<AttributeDefinitionReadView> collection = ty.getAllAttributes();
		Iterator<AttributeDefinitionReadView> iterator = collection.iterator();
		
		String showIBA = commandBean.getTextParameter("showIBA");
		while(iterator.hasNext()) {
			AttributeDefinitionReadView adrv = iterator.next();
			if("true".equals(showIBA)) {
				if(adrv.getIBARefView() != null) {
					list.add(adrv);
				}
			}else {
				if(adrv.getIBARefView() == null) {
					list.add(adrv);
				}
			}
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_LABEL"));
		table.setShowCount(true);
		table.setMenubarName("syncActionModelTypeAttributes"); 

		ColumnConfig col = factory.newColumnConfig("ATTRIBUTE_ZH", true);
		col.setWidth(200);
		col.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_01"));
		col.setDataUtilityId("ModelInfoDatautility");
		table.addComponent(col);

		col = factory.newColumnConfig("ATTRIBUTE_US", true);
		col.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_02"));
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		table.addComponent(col);
		
		col = factory.newColumnConfig("SAST_ATTRIBUTE_ZH", true);
		col.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_03"));
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		table.addComponent(col);
		
		col = factory.newColumnConfig("SAST_ATTRIBUTE_US", true);
		col.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_04"));
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		table.addComponent(col);

		return table;
	}

}
