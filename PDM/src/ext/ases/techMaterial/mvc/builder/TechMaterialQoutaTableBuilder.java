package ext.ases.techMaterial.mvc.builder;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.techMaterial.gwpersistable.GwPersistable;
import ext.ases.techMaterial.gwpersistable.GwPersistenceHelper;
import ext.ases.techMaterial.gwpersistable.GwQueryResult;
import ext.ases.techMaterial.gwpersistable.GwQuerySpec;
import ext.ases.techMaterial.model.TechnicaQuotaNumber;
import ext.ases.techMaterial.model.TechnicsMaterialLink;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ComponentBuilder("ext.ases.techMaterial.mvc.builder.TechMaterialQoutaTableBuilder")
public class TechMaterialQoutaTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
		List<TechnicaQuotaNumber> resList = new ArrayList<TechnicaQuotaNumber>();
		NmCommandBean commandBean = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
		Object object = commandBean.getPageOid().getRefObject();
		String activityName = "";
		if(object instanceof WorkItem) {
			WorkItem workItem = (WorkItem)object;
			Persistable per = workItem.getPrimaryBusinessObject().getObject();
			if(per instanceof WTDocument){
				WTDocument doc= (WTDocument) per;
				String stringValue = doc.getPersistInfo().getObjectIdentifier().getStringValue();
				GwQuerySpec qs = new GwQuerySpec(TechnicaQuotaNumber.class);
				qs.appendWhere("TECHNICSOID",GwQuerySpec.EQUAL,stringValue);
				GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
				while(qr.hasNext()){
					TechnicaQuotaNumber next = (TechnicaQuotaNumber) qr.next();
					resList.add(next);
				}
			}
		}
		return resList;

	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setLabel("申请编码列表");
		tableConfig.setSelectable(true);

		ColumnConfig nameColumnConfig = factory.newColumnConfig("quotanumber", true);
		nameColumnConfig.setLabel("物资编码");
		nameColumnConfig.setDataUtilityId("DataSendRecordUtility");
		nameColumnConfig.setAutoSize(true);
		tableConfig.addComponent(nameColumnConfig);

		ColumnConfig creatorColumnConfig = factory.newColumnConfig("tmcreator", true);
		creatorColumnConfig.setLabel("申请人");
		creatorColumnConfig.setAutoSize(true);
		tableConfig.addComponent(creatorColumnConfig);

		ColumnConfig createDataColumnConfig = factory.newColumnConfig("tmcreatetime", true);
		createDataColumnConfig.setLabel("申请时间");
		createDataColumnConfig.setAutoSize(true);
		tableConfig.addComponent(createDataColumnConfig);

		return tableConfig;
	}

}
