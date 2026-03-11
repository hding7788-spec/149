package ext.sast.center.productModel.mvc.builder;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.sast.center.productModel.bean.SAST_PDMLinkProduct;
import ext.sast.center.productModel.util.SyncProductHelper;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wang-ya-qi
 *
 */
@ComponentBuilder("ext.sast.center.productModel.mvc.builder.SastProductInfoTableBuilder")
public class SastProductInfoTableBuilder extends AbstractComponentBuilder{
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentparams) throws Exception {
		List<SAST_PDMLinkProduct> list = new ArrayList<SAST_PDMLinkProduct>();
		NmCommandBean commandBean = ((JcaComponentParams)componentparams).getHelperBean().getNmCommandBean();
		Object sastModel = commandBean.getText().get("sastModel");
		//System.out.println("sastModel"+sastModel);
		String sastModelStr = sastModel == null ? "":sastModel.toString();
		list = SyncProductHelper.getAllSastProductInfo(sastModelStr);
		return list;
	}
	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel(messageSource.getMessage("SASTMODELLISTTABLEBUILDER_LABEL"));
		table.setSelectable(true);
		table.setMenubarName("syncActionProducts_save");
		table.setShowCount(true);

		ColumnConfig col = factory.newColumnConfig("SAST_ProductOid", true);
		col.setLabel("型号代号");
		col.setWidth(200);
		table.addComponent(col);

		col = factory.newColumnConfig("SAST_ProductName", true);
		col.setWidth(200);
		col.setLabel(messageSource.getMessage("SASTMODELLISTTABLEBUILDER_01"));
		table.addComponent(col);

		col = factory.newColumnConfig("SAST_Remarks", true);
		col.setLabel(messageSource.getMessage("SASTMODELLISTTABLEBUILDER_02"));
		col.setWidth(200);
		table.addComponent(col);

		return table;
	}

}
