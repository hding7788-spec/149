package ext.casc.workflow.tree.mvc.builder;

import java.util.ArrayList;
import java.util.HashMap;
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

@ComponentBuilder("ext.casc.workflow.tree.mvc.builder.ViewOutSignTableBuilder")
public class ViewOutSignTableBuilder extends AbstractComponentBuilder{

	@SuppressWarnings({ "rawtypes", "deprecation"})
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
	           	ArrayList outSignList = (ArrayList)processData.getValue("outSignInfo");
	           	if(outSignList == null){
	           		return list;
	           	}
	           	for(int i = 0; i < outSignList.size(); i++){
	           		NmOid oid = (NmOid)outSignList.get(i);
	           		HashMap map = oid.getAdditionalInfo();
	           		list.add(map);
	           	}
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
		tableConfig.setSelectable(false);
		tableConfig.setLabel("外部会签");
        tableConfig.setId("outSignTable");

        ColumnConfig col_name = factory.newColumnConfig("signName", "会签人", true);
		tableConfig.addComponent(col_name);
		ColumnConfig col_company = factory.newColumnConfig("signCompany", "会签单位", false);
		tableConfig.addComponent(col_company);
		ColumnConfig col_date = factory.newColumnConfig("signDate", "会签日期", false);
		tableConfig.addComponent(col_date);
		ColumnConfig col_remark = factory.newColumnConfig("signRemark", "会签意见", true);
		tableConfig.addComponent(col_remark);
		return tableConfig;
	}

}
