package ext.casc.report.mvc.builders;

import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.report.BOMReprotsService;

public class BOMReportBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams params)
			throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
		String productName = (String)commandBean.getText().get("productName");
		return  BOMReprotsService.getBOMReport(productName);
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("BOM列表");
		table.setSelectable(false);

		ColumnConfig partNumber = factory.newColumnConfig("partNumber",true);
		partNumber.setLabel("编号");
		partNumber.setWidth(100);
		table.addComponent(partNumber);


		ColumnConfig partName = factory.newColumnConfig("partName",true);
		partName.setLabel("名称");
		partName.setWidth(100);
		partName.setDataUtilityId("AllWorkFlowDataUtility");
		table.addComponent(partName);

		ColumnConfig view = factory.newColumnConfig("view",true);
		view.setLabel("视图");
		view.setWidth(100);
		table.addComponent(view);

		ColumnConfig creator = factory.newColumnConfig("creator",true);
		creator.setLabel("创建者");
		creator.setWidth(100);
		table.addComponent(creator);

		ColumnConfig createTime = factory.newColumnConfig("createTime",true);
		createTime.setLabel("创建时间");
		createTime.setWidth(150);
		table.addComponent(createTime);

		return table;
	}

}
