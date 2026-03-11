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
import wt.util.WTException;


@ComponentBuilder("ext.sast.center.mvc.builders.SynchDataRecordBuilder")
public class SynchDataRecordBuilder extends AbstractComponentBuilder {
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams configParams) throws Exception {
		List<SynchRecord> list = new ArrayList<SynchRecord>();
		NmCommandBean cb = ((JcaComponentParams)configParams).getHelperBean().getNmCommandBean();
		Object invoice_number = cb.getText().get("invoice_number");
		System.out.println("invoice_number = "+invoice_number);
		Object invoice_type = cb.getText().get("invoice_type");
		System.out.println("invoice_type = "+invoice_type);
		Object pbo_number = cb.getText().get("pbo_number");
		System.out.println("pbo_number = "+pbo_number);
		Object pbo_name = cb.getText().get("pbo_name");
		System.out.println("pbo_name = "+pbo_name);
		Object searchFrom = cb.getText().get("searchFrom");
		System.out.println("searchFrom = "+searchFrom);
		Object search_End = cb.getText().get("search_End");
		System.out.println("search_End = "+search_End);
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

		ColumnConfig col = factory.newColumnConfig("synch_invoice", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_09"));
		table.addComponent(col);

		col = factory.newColumnConfig("synch_type", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_10"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_processStatus", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_11"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_pboNumber", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_12"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_pboName", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_13"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_pboType", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_14"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_pboCreator", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_15"));
		table.addComponent(col);
		
		col = factory.newColumnConfig("synch_startTime", true);
		col.setLabel(messageSource.getMessage("SYNCHRECORD_16"));
		table.addComponent(col);
		return table;
	}

	
}
