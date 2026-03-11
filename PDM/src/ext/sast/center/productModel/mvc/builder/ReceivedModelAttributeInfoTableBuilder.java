package ext.sast.center.productModel.mvc.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import ext.sast.center.productModel.bean.ReceivedModelAttrInfo;
import ext.sast.center.productModel.util.SyncModeTypeHelper;
import wt.util.WTException;

@ComponentBuilder("ext.sast.center.productModel.mvc.builder.ReceivedModelAttributeInfoTableBuilder")
public class ReceivedModelAttributeInfoTableBuilder extends AbstractComponentBuilder{
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams componentparams) throws Exception {
		List<ReceivedModelAttrInfo> result = new ArrayList<ReceivedModelAttrInfo>();
		NmCommandBean commandBean = ((JcaComponentParams)componentparams).getHelperBean().getNmCommandBean();
		String sastModelTypeId = null;
		String sastModelTypeName = null;
		String localModelTypeId = null;
		String localModelTypeName = null;
		String showEditCombox = commandBean.getTextParameter("showEditCombox");
		if(showEditCombox != null && "true".equals(showEditCombox)) {
			sastModelTypeId = (String) commandBean.getText().get("sastModelType_US");
			sastModelTypeName = (String) commandBean.getText().get("sastModelType");
			localModelTypeId = (String) commandBean.getText().get("localModelType_US");
			localModelTypeName = (String) commandBean.getText().get("localModelType");
		}else {
			String queryString = commandBean.getRequest().getQueryString();
			if(queryString != null && queryString.length()>0) {
				String[] params = queryString.split("&");
				for(int i=0;i<params.length;i++) {
					String queryParams = params[i];
					String key = queryParams.split("=")[0];
					String value = queryParams.split("=")[1];
					if("sast_modeltypeid".equals(key)) {
						sastModelTypeId = value;
					}else if("sast_modeltypename".equals(key)) {
						sastModelTypeName = value;
					}else if("local_modeltypename".equals(key)) {
						localModelTypeName = value;
					}else if("local_modeltypeid".equals(key)) {
						localModelTypeId = value;
					}
				}
			}
		}
		Map<String,String> map = SyncModeTypeHelper.getReceiveSastModelAttrByModelType(sastModelTypeId);
		Iterator<Entry<String,String>> it = map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,String> entry = it.next();
			ReceivedModelAttrInfo info = new ReceivedModelAttrInfo();
			info.setSastModelTypeId(sastModelTypeId);
			info.setSastModelTypeName(sastModelTypeName);
			info.setLocalModelTypeId(localModelTypeId);
			info.setLocalModelTypeName(localModelTypeName);
			
			info.setSastModelAttr_us(entry.getKey());
			info.setSastModelAttr_zh(entry.getValue());
			
			String[] localAttr = SyncModeTypeHelper.getReceivedModelAttr(entry.getKey(),sastModelTypeId,localModelTypeId);
			if(localAttr != null) {
				info.setLocalModelAttr_us(localAttr[0]);
				info.setLocalModelAttr_zh(localAttr[1]);
			}else {
				info.setLocalModelAttr_us("");
				info.setLocalModelAttr_zh("");
			}
			
			result.add(info);
		}
		return result;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_LABEL"));
		table.setShowCount(true);
		table.setMenubarName("receivedModelTypeAttributes"); 

		ColumnConfig col = factory.newColumnConfig("sastModelAttr_zh", true);
		col.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_03"));
		col.setWidth(200);
		//col.setDataUtilityId("ModelInfoDatautility");
		table.addComponent(col);
		
		col = factory.newColumnConfig("sastModelAttr_us", true);
		col.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_04"));
		col.setWidth(200);
		//col.setDataUtilityId("ModelInfoDatautility");
		table.addComponent(col);

		col = factory.newColumnConfig("localModelAttr_zh", true);
		col.setWidth(200);
		col.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_01"));
		col.setDataUtilityId("ModelInfoDatautility");
		table.addComponent(col);

		col = factory.newColumnConfig("localModelAttr_us", true);
		col.setLabel(messageSource.getMessage("SASTMODELATTRIBUTELISTTABLEBUILDER_02"));
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		table.addComponent(col);
		
		return table;
	}

}
