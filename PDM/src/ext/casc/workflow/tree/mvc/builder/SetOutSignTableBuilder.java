package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.SetOutSignTableBuilder")
public class SetOutSignTableBuilder extends AbstractComponentBuilder{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object buildComponentData(ComponentConfig componentconfig,ComponentParams componentparams) throws Exception {
		List<Object> list = new ArrayList<Object>();
		try{
			NmCommandBean commandBean = ((JcaComponentParams)componentparams).getHelperBean().getNmCommandBean();
			NmOid nmOid = commandBean.getActionOid();
			Object obj = nmOid.getRefObject();
	        if(obj instanceof WorkItem){
	        	WorkItem item = (WorkItem)obj;
	            WfActivity activity = (WfActivity) item.getSource().getObject();
	            ProcessData processData = activity.getContext();
	           	list = (ArrayList)processData.getValue("outSignInfo");
	        }
        } catch (WTException e) {
            e.printStackTrace();
        }
		return list;
	}

	public ComponentConfig buildComponentConfig(ComponentParams componentparams)throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		JcaTableConfig tableConfig = (JcaTableConfig)factory.newTableConfig();
		tableConfig.setDataSourceMode(DataSourceMode.ASYNCHRONOUS);
		tableConfig.setSelectable(true);
		tableConfig.setLabel("外部会签");
        tableConfig.setId("outSignTable");
        tableConfig.setActionModel("outSign table");

        ColumnConfig col_name = factory.newColumnConfig("signName", "会签人", true);
        col_name.setDataUtilityId("TableColumnDataUtility");
        col_name.setRequired(true);
		tableConfig.addComponent(col_name);
		ColumnConfig col_company = factory.newColumnConfig("signCompany", "会签单位", false);
		col_company.setDataUtilityId("TableColumnDataUtility");
		col_company.setRequired(true);
		tableConfig.addComponent(col_company);
		ColumnConfig col_date = factory.newColumnConfig("signDate", "会签日期(yyyyMMdd)", false);
		col_date.setDataUtilityId("TableColumnDataUtility");
		col_date.setRequired(true);
		col_date.setDateDisplayFormat("YYYYMMdd");
		tableConfig.addComponent(col_date);
		ColumnConfig col_remark = factory.newColumnConfig("signRemark", "会签意见", true);
		col_remark.setDataUtilityId("TableColumnDataUtility");
		tableConfig.addComponent(col_remark);
		return tableConfig;
	}

}
