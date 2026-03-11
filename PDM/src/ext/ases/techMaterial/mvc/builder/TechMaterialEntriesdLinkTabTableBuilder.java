package ext.ases.techMaterial.mvc.builder;

import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.techMaterial.TechnicsMaterialEntries;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;

@ComponentBuilder("ext.ases.techMaterial.mvc.builder.TechMaterialEntriesdLinkTabTableBuilder")
public class TechMaterialEntriesdLinkTabTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
		NmCommandBean cb = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
		String oid = cb.getActionOid().getOidObject().toString();

		NmOid nmOid = cb.getActionOid();
		Object obj = nmOid.getRefObject();
		if (obj instanceof TechnicsMaterialEntries) {
			TechnicsMaterialEntries tme = (TechnicsMaterialEntries) obj;
			String descption = tme.getDescription();
			if ("标准紧固件".equals(descption)) {
				return TechnicsMaterialUtils.getTMEStandPartInfo(oid);
			} else if ("电子元器件".equals(descption)) {
				return TechnicsMaterialUtils.getEleComponentsPartInfo(oid);
			} else if ("非金属材料".equals(descption)) {
				return TechnicsMaterialUtils.getNonMetallicPartInfo(oid);
			} else if ("复合材料".equals(descption)) {
				return TechnicsMaterialUtils.getCompoundMaterialPartInfo(oid);
			} else if ("金属材料".equals(descption)) {
				return TechnicsMaterialUtils.getMetallicPartInfo(oid);
			}else if ("机电材料".equals(descption)) {
				return TechnicsMaterialUtils.getEleMachinePartInfo(oid);
			}else if ("火工品".equals(descption)) {
				return TechnicsMaterialUtils.getExpDevicePartInfo(oid);
			}
		}
		return null;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
		String descption = "";
		NmCommandBean cb = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
		NmOid nmOid = cb.getActionOid();
		Object obj = nmOid.getRefObject();
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setLabel("条目属性");
		tableConfig.setSelectable(true);
		if (obj instanceof TechnicsMaterialEntries) {
			TechnicsMaterialEntries tme = (TechnicsMaterialEntries) obj;
			descption = tme.getDescription();
		}
		if ("标准紧固件".equals(descption)) {
			for (String key : TechnicsMaterialUtils.standardInfoMap.keySet()) {
				String value = TechnicsMaterialUtils.standardInfoMap.get(key);
				ColumnConfig config = factory.newColumnConfig(value.toLowerCase(), true);
				config.setLabel(key);
				config.setAutoSize(true);
				tableConfig.addComponent(config);
			}

		} else if ("电子元器件".equals(descption)) {
			for (String key : TechnicsMaterialUtils.eleComponentsInfoMap.keySet()) {
				String value = TechnicsMaterialUtils.eleComponentsInfoMap.get(key);
				ColumnConfig config = factory.newColumnConfig(value.toLowerCase(), true);
				config.setLabel(key);
				config.setAutoSize(true);
				tableConfig.addComponent(config);
			}
		}else if("非金属材料".equals(descption)){
			for (String key : TechnicsMaterialUtils.nonmetallicInfoMap.keySet()) {
				String value = TechnicsMaterialUtils.nonmetallicInfoMap.get(key);
				ColumnConfig config = factory.newColumnConfig(value.toLowerCase(), true);
				config.setLabel(key);
				config.setAutoSize(true);
				tableConfig.addComponent(config);
			}
		}else if("复合材料".equals(descption)){
			for (String key : TechnicsMaterialUtils.compoundMaterialInfoMap.keySet()) {
				String value = TechnicsMaterialUtils.compoundMaterialInfoMap.get(key);
				ColumnConfig config = factory.newColumnConfig(value.toLowerCase(), true);
				config.setLabel(key);
				config.setAutoSize(true);
				tableConfig.addComponent(config);
			}
		}else if("金属材料".equals(descption)){
			for (String key : TechnicsMaterialUtils.metallicInfoMap.keySet()) {
				String value = TechnicsMaterialUtils.metallicInfoMap.get(key);
				ColumnConfig config = factory.newColumnConfig(value.toLowerCase(), true);
				config.setLabel(key);
				config.setAutoSize(true);
				tableConfig.addComponent(config);
			}
		}else if("机电材料".equals(descption)){
			for (String key : TechnicsMaterialUtils.eleMachineInfoMap.keySet()) {
				String value = TechnicsMaterialUtils.eleMachineInfoMap.get(key);
				ColumnConfig config = factory.newColumnConfig(value.toLowerCase(), true);
				config.setLabel(key);
				config.setAutoSize(true);
				tableConfig.addComponent(config);
			}
		}else if("火工品".equals(descption)){
			for (String key : TechnicsMaterialUtils.expDeviceInfoMap.keySet()) {
				String value = TechnicsMaterialUtils.expDeviceInfoMap.get(key);
				ColumnConfig config = factory.newColumnConfig(value.toLowerCase(), true);
				config.setLabel(key);
				config.setAutoSize(true);
				tableConfig.addComponent(config);
			}
		}
		return tableConfig;
	}

}
