package ext.sast.center.mvc.builders;

import java.util.ArrayList;
import java.util.List;

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

import ext.sast.center.bean.SynchRecord;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;

@ComponentBuilder("ext.sast.center.mvc.builders.SynchWfRecordBuilder")
public class SynchWfRecordBuilder extends AbstractComponentBuilder {
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams configParams) throws Exception {
		List<SynchRecord> list = new ArrayList<SynchRecord>();
		NmCommandBean cb = ((JcaComponentParams)configParams).getHelperBean().getNmCommandBean();
		String showDatas = cb.getTextParameter("showDatas");
		if(!"true".equals(showDatas)) {
			return list;
		}
		Object obj = cb.getPageOid().getRefObject();
		if(obj instanceof PDMLinkProduct) {
			PDMLinkProduct product = (PDMLinkProduct) obj;
		}
		cb.getTextParameter("invoice_type");
		Object invoice_number = cb.getText().get("invoice_number");
		System.out.println("invoice_number = "+invoice_number);
		Object invoice_name = cb.getText().get("invoice_name");
		System.out.println("invoice_number = "+invoice_name);
		Object invoice_type = cb.getText().get("invoice_type");
		System.out.println("invoice_type = "+invoice_type);
		Object unit = cb.getText().get("unit");
		System.out.println("unit = "+unit);
		Object synch_pboCreator = cb.getText().get("synch_pboCreator");
		System.out.println("synch_pboCreator = "+synch_pboCreator);
		Object synch_processStatus = cb.getText().get("synch_processStatus");
		System.out.println("synch_processStatus = "+synch_processStatus);
		Object searchFrom = cb.getText().get("searchFrom");
		System.out.println("searchFrom = "+searchFrom);
		Object synch_receiveTime = cb.getText().get("synch_receiveTime");
		System.out.println("synch_receiveTime = "+synch_receiveTime);
		
		
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		
		
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel(messageSource.getMessage("SYNCHRECORD_LABEL"));
		table.setMenubarName("custom_process_search_result_actions");
		/**
		 * <model name="custom_process_search_result_actions"><action name="exportTableXLS" type="object"/></model>
		 */
		table.setSelectable(false);
		table.setShowCount(true);

		ColumnConfig col = factory.newColumnConfig("synch_invoice_number", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_09"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_invoice_name", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_17"));
		table.addComponent(col);

		col = factory.newColumnConfig("synch_type", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_10"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("unit", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_01"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_pboCreator", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_12"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_processStatus", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_11"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_startTime", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_16"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("principal", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_03"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_receiveTime", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_18"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_detials", true);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("SYNCHRECORD_19"));
		table.addComponent(col);
		return table;
	}

	
}
