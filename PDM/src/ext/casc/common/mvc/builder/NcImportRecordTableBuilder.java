package ext.casc.common.mvc.builder;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.*;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.integrate.nc.ErpSynchHelper;

import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.util.WTException;

@ComponentBuilder("ext.casc.common.mvc.builder.NcImportRecordTableBuilder")
public class NcImportRecordTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
		NmCommandBean cb = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
		Object p = cb.getPageOid().getRefObject();
		if(p instanceof  WTDocument){
			WTDocument doc = (WTDocument) p;
			return ErpSynchHelper.queryDocRecord(doc.getNumber());

		}else if(p instanceof WTPart){
			WTPart part = (WTPart) p;
			return ErpSynchHelper.queryPartRecord(part.getNumber());
		}
		return null;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		JcaTableConfig tableConfig = (JcaTableConfig) factory.newTableConfig();
		tableConfig.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
		tableConfig.setSelectable(false);

		tableConfig.setLabel("NC导入记录");

		ColumnConfig objectNumber = factory.newColumnConfig("objectNumber", false);
		objectNumber.setLabel("编号");
		tableConfig.addComponent(objectNumber);

		ColumnConfig partVersion = factory.newColumnConfig("objectVersion", false);
		partVersion.setLabel("导入版本");
		tableConfig.addComponent(partVersion);

		ColumnConfig viewName = factory.newColumnConfig("viewName", false);
		viewName.setLabel("视图");
		tableConfig.addComponent(viewName);

		ColumnConfig synchState = factory.newColumnConfig("synchState", false);
		synchState.setLabel("导入状态");
		tableConfig.addComponent(synchState);

		ColumnConfig importer = factory.newColumnConfig("importer", false);
		importer.setLabel("导入者");
		tableConfig.addComponent(importer);

		ColumnConfig note = factory.newColumnConfig("note", false);
		note.setLabel("备注");
		tableConfig.addComponent(note);

		ColumnConfig synchTime = factory.newColumnConfig("synchTime", false);
		synchTime.setLabel("导入时间");
		synchTime.setSortable(true);
		tableConfig.addComponent(synchTime);

		return tableConfig;
	}

}
