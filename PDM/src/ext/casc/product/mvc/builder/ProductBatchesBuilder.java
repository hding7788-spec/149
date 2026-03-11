package ext.casc.product.mvc.builder;

import wt.fc.PersistenceHelper;
import wt.inf.container.WTContained;
import wt.pdmlink.PDMLinkProduct;
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

import ext.casc.util.DBUtil;

@ComponentBuilder("ext.casc.product.mvc.builder.ProductBatchesBuilder")
public class ProductBatchesBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		NmCommandBean commandbean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		WTContained container = commandbean.getContainer();
		String productName = "";
		if (container instanceof PDMLinkProduct) {
			productName = ((PDMLinkProduct) container).getName();
		}
		String productOid = String.valueOf(PersistenceHelper
				.getObjectIdentifier(container).getId());
		return DBUtil.getBatchesByProduct(productOid, productName);
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setActionModel("custom_batches_actions");
		table.setLabel("产品批次号");
		table.setSelectable(true);

		ColumnConfig no = factory.newColumnConfig("no",true);
		no.setLabel("序号");
		no.setWidth(10);
		table.addComponent(no);

		ColumnConfig productName = factory.newColumnConfig("productName",false);
		productName.setLabel("所属产品");
		productName.setAutoSize(true);
		table.addComponent(productName);

		ColumnConfig batcheName = factory.newColumnConfig("name",false);
		batcheName.setLabel("批次名称");
		batcheName.setAutoSize(true);
		table.addComponent(batcheName);

		return table;
	}
}
