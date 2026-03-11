package ext.sast.center.productModel.datautility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.sast.center.productModel.bean.DocDsubdivisionInfo;
import ext.sast.center.productModel.bean.ReceivedModelAttrInfo;
import ext.sast.center.productModel.bean.ReceivedModelTypeInfo;
import ext.sast.center.productModel.util.NmTableGUIComponent;
import ext.sast.center.productModel.util.SyncModeTypeHelper;
import ext.sast.center.productModel.util.SyncProductHelper;
import wt.fc.PersistenceHelper;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;

@SuppressWarnings("unchecked")
public class ModelInfoDatautility extends AbstractDataUtility{
	static Map<String,String> modelTypeMap = new HashMap<String,String>();
	static Map<String,ArrayList<String>> displayMap = new HashMap<String,ArrayList<String>>();
	static Map<String,ArrayList<String>> interValueMap = new HashMap<String,ArrayList<String>>();
	@Override
	public Object getDataValue(String componentId, Object object, ModelContext context) throws WTException {
		NmCommandBean cb = context.getNmCommandBean();
		String actionMethod = cb.getActionMethod();
		if(object instanceof PDMLinkProduct){
			PDMLinkProduct product = (PDMLinkProduct)object;
			String productOid = PersistenceHelper.getObjectIdentifier(product).getId()+"";
			if("sast_model_name".equals(componentId)){
				return SyncProductHelper.getSastProductNameByProductOid(productOid);
			}else if("sast_model_index".equals(componentId)){
				return SyncProductHelper.getSastProductIndexByProductOid(productOid);
			}
		}
		if(object instanceof TypeDefinitionReadView){
			String showEditCombox = cb.getTextParameter("showEditCombox");
			TypeDefinitionReadView typeIdentifier = (TypeDefinitionReadView) object;
			//String parentType = TypeDefinitionServiceHelper.service.getRootTypeDefView(typeIdentifier).getName();			
			String zhType = PropertyHolderHelper.getDisplayName(typeIdentifier,Locale.CHINA);
			String usType = typeIdentifier.getName();
			String result = SyncModeTypeHelper.getSastModelTypeInfoByLocalHostType_US(usType);
			if("localHostType_ZH".equals(componentId)) {
				return zhType;
			}else if("localHostType_US".equals(componentId)) {
				return usType;
			}else if("sastType_ZH".equals(componentId)){
				SyncModeTypeHelper.getSastModelType(modelTypeMap);
				
				ComboBox comboBox = new ComboBox();
				comboBox.setName(componentId);
				if(result != null && result.indexOf(",")>0) {
					comboBox.setSelected(result.split(",")[0]);
				}
				comboBox.setId(usType);
				comboBox.addStyleClass("comboxStyle");
				ArrayList<String> displayList = (ArrayList<String>) IteratorUtils.toList(modelTypeMap.values().iterator());
				displayList.add(0,"--请选择--");
				ArrayList<String> interValueList = (ArrayList<String>) IteratorUtils.toList(modelTypeMap.keySet().iterator());	
				interValueList.add(0,"--请选择--");				
				comboBox.setValues(displayList);
				comboBox.setInternalValues(interValueList);
				comboBox.addJsAction("onChange", "showSastModelTypeUs(this)");
				if("true".equals(showEditCombox) && !"saveModelType".equals(actionMethod)) {
					comboBox.setEditable(true);
				}else {
					comboBox.setEditable(false);
				}
				return comboBox;
			}else if("sastType_US".equals(componentId)){
				String val = "";
				if(result != null && result.indexOf(",")>0) {
					val = result.split(",")[1];
				}
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
				String value = "<input type='text' value='"+val+"' id='"+usType+"_SASTTYPE_US' name='"+usType+"_SASTTYPE_US' readonly='readonly' style='border:0;'>";
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
			    guicomponentarrayMain.addGUIComponent(gui);
			    return guicomponentarrayMain;
				
			}
		}else if(object instanceof AttributeDefinitionReadView) {
			String sastModelType_US = (String) cb.getRequest().getAttribute("sastModelType_US");
			
			AttributeDefinitionReadView localAttributeDefinitionReadView = (AttributeDefinitionReadView) object;
			
			String localHostType_ZH = PropertyHolderHelper.getDisplayName(localAttributeDefinitionReadView, Locale.CHINA);
			String localHostType_US = PropertyHolderHelper.getName(localAttributeDefinitionReadView);
			
			String result = SyncModeTypeHelper.getSastModelTypeAttrInfoByAttr_US(localHostType_US,sastModelType_US);
			System.out.println("resultresultresult === "+result);
			String showEditCombox = cb.getTextParameter("showEditCombox");
			if("ATTRIBUTE_ZH".equals(componentId)) {
				return localHostType_ZH;
			}else if("ATTRIBUTE_US".equals(componentId)) {
				return localHostType_US;
			}else if("SAST_ATTRIBUTE_ZH".equals(componentId)) {
				Map<String,ArrayList<String>> map = SyncModeTypeHelper.getSastModelAttrByModelType(sastModelType_US);
				
				ComboBox comboBox = new ComboBox();
				comboBox.addStyleClass("comboxStyle");
				comboBox.setName(componentId);
				comboBox.setId(localHostType_US);
				if(result != null && result.indexOf(",")>0) {
					comboBox.setSelected(result.split(",")[0]);
				}
				ArrayList<String> displayList = map.get("DisplayValues");
				displayList.add(0,"--请选择--");
				ArrayList<String> interValueList = map.get("InternalValues");
				interValueList.add(0,"--请选择--");
				comboBox.setValues(displayList);
				comboBox.setInternalValues(interValueList);
				comboBox.addJsAction("onChange", "showSastModelTypeAttributes(this)");
				if(showEditCombox != null && "true".equals(showEditCombox)) {
					comboBox.setEditable(true);
				}else {
					comboBox.setEditable(false);
				}
				return comboBox;
			}else if("SAST_ATTRIBUTE_US".equals(componentId)) {
				String val = "";
				if(result != null && result.indexOf(",")>0) {
					val = result.split(",")[1];
				}
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
				String value = "<input type='text' value='"+val+"' id='"+localHostType_US+"_SAST_ATTRIBUTE_US' name='"+localHostType_US+"_SAST_ATTRIBUTE_US' readonly='readonly' style='border:0;'>";
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
			    guicomponentarrayMain.addGUIComponent(gui);
			    return guicomponentarrayMain;
			}
		}
		if(object.getClass().toString().contains("ReceivedModelTypeInfo")) {
			ReceivedModelTypeInfo info = (ReceivedModelTypeInfo)object;
			String id = info.getInnerId();
			String sastId = info.getSast_modeltypeid();
			String name = info.getSast_modeltypename();
			String localId = info.getLocal_modeltypeid();
			String localName = info.getLocal_modeltypename();
			if("sast_modeltypename".equals(componentId)) {
				if("SAST_NAME".equals(name)){
					Map<String,ArrayList<String>> map = SyncModeTypeHelper.getSastComboxList();
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					ComboBox comboBox = new ComboBox();
					comboBox.addStyleClass("comboxStyle");
					comboBox.setId(id);
					comboBox.setName(componentId);
					ArrayList<String> displayList = map.get("DisplayValues");
					displayList.add(0,"--请选择--");
					ArrayList<String> interValueList = map.get("InternalValues");
					interValueList.add(0,"--请选择--");
					comboBox.setValues(displayList);
					comboBox.setInternalValues(interValueList);
					comboBox.addJsAction("onChange", "getSastValue(this)");
					guicomponentarrayMain.addGUIComponent(comboBox);
					return guicomponentarrayMain;
				}else {
					return name;
				}
			}else if("sast_modeltypeid".equals(componentId)) {
				if(sastId == null || sastId.length()<=0 || "null".equalsIgnoreCase(sastId)){
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					String value = "<input type='text' value='' id='"+id+"_SAST_MODELTYPEID' name='"+id+"_SAST_MODELTYPEID' readonly='readonly' style='border:0;'>";
			        NmTableGUIComponent gui = new NmTableGUIComponent(value);
			        guicomponentarrayMain.addGUIComponent(gui);
					return guicomponentarrayMain;
				}else {
					return sastId;
				}
			}else if("local_modeltypename".equals(componentId)) {
				if("LOCAL_NAME".equals(localName)){
					Map<String,ArrayList<String>> map = SyncModeTypeHelper.getLocalComboxList();
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					ComboBox comboBox = new ComboBox();
					comboBox.addStyleClass("comboxStyle");
					comboBox.setId(id);
					comboBox.setName(componentId);
					ArrayList<String> displayList = map.get("DisplayValues");
					displayList.add(0,"--请选择--");
					ArrayList<String> interValueList = map.get("InternalValues");
					interValueList.add(0,"--请选择--");
					comboBox.setValues(displayList);
					comboBox.setInternalValues(interValueList);
					comboBox.addJsAction("onChange", "getLocalValue(this)");
					guicomponentarrayMain.addGUIComponent(comboBox);
					
					return guicomponentarrayMain;
				}else {
					return localName;
				}
			}else if("local_modeltypeid".equals(componentId)) {
				if(localId == null || localId.length()<=0 || "null".equalsIgnoreCase(localId)){
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					String value = "<input type='text' value='' id='"+id+"_LOCAL_MODELTYPEID' name='"+id+"_SAST_MODELTYPEID' readonly='readonly' style='border:0;'>";
			        NmTableGUIComponent gui = new NmTableGUIComponent(value);
			        guicomponentarrayMain.addGUIComponent(gui);
					return guicomponentarrayMain;
				}else {
					return localId;
				}
			}
		}else if(object.getClass().toString().contains("ReceivedModelAttrInfo")){
			ReceivedModelAttrInfo info = (ReceivedModelAttrInfo) object;
			String sastAttr_US = info.getSastModelAttr_us();
			//String sastAttr_ZH = info.getSastModelAttr_zh();
			String localAttr_US = info.getLocalModelAttr_us();
			String localAttr_ZH = info.getLocalModelAttr_zh();
			String showEditCombox = cb.getTextParameter("showEditCombox");
			
			if("localModelAttr_us".equals(componentId)) {
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
				String value = "<input type='text' value='"+localAttr_US+"' id='"+sastAttr_US+"_RECEIVEDATTR' name='"+sastAttr_US+"_RECEIVEDATTR' readonly='readonly' style='border:0;'>";
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
			    guicomponentarrayMain.addGUIComponent(gui);
			    return guicomponentarrayMain;
			}else if("localModelAttr_zh".equals(componentId)) {
				Map<String,ArrayList<String>> map = SyncModeTypeHelper.getLocalModelAttrByModelType(info.getLocalModelTypeId());
				GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
				ComboBox comboBox = new ComboBox();
				comboBox.addStyleClass("comboxStyle");
				comboBox.setName(componentId);
				comboBox.setId(sastAttr_US);
				comboBox.setSelected(localAttr_ZH);
				comboBox.setValues(map.get("DisplayValues"));
				comboBox.setInternalValues(map.get("InternalValues"));
				comboBox.addJsAction("onChange", "showSastModelTypeAttributes(this)");
				if(showEditCombox != null && "true".equals(showEditCombox)) {
					comboBox.setEditable(true);
				}else {
					comboBox.setEditable(false);
				}
				guicomponentarrayMain.addGUIComponent(comboBox);
				return guicomponentarrayMain;
			}
		}else if(object.getClass().toString().contains("DocDsubdivisionInfo")){
			List<TypeDefinitionReadView> allType = SyncModeTypeHelper.getAllType();
			ArrayList<String> interValueList = new ArrayList<String>();
			ArrayList<String> displayList = new ArrayList<String>();
			for(TypeDefinitionReadView ty : allType) {
				String localHostType_ZH = PropertyHolderHelper.getDisplayName(ty, Locale.CHINA);
				String localHostType_US = PropertyHolderHelper.getName(ty);
				interValueList.add(localHostType_US);
				displayList.add(localHostType_ZH);
			}
			DocDsubdivisionInfo info = (DocDsubdivisionInfo) object;
			String id = info.getInnerId();
			String siteName = info.getComeFromSiteName();
			String docType = info.getDocTypeName();
			String docTypeInnerName = info.getDocTypeInnerName();
			String docLocalType = info.getLocalDocTypeName();
			String docLocalTypeInnerName = info.getLocalDocTypeInnerName();
			if("comeFromSiteName".equals(componentId)) {
				if("COMEFROMSITENAME".equals(siteName)) {
					ArrayList<String> list = new ArrayList<String>();
					list.add("--请选择--");
					list.add("805");
					list.add("149");
					list.add("812");
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					ComboBox comboBox = new ComboBox();
					comboBox.addStyleClass("comboxStyle");
					comboBox.setName(componentId);
					comboBox.setId(id);
					comboBox.setValue("");
					comboBox.setInternalValues(list);
					comboBox.setValues(list);
					comboBox.addJsAction("onChange", "setSiteName(this)");
					guicomponentarrayMain.addGUIComponent(comboBox);
					
					String value = "<input type='hidden' value='' id='"+id+"_SITENAME' name='"+id+"_SITENAME'>";
			        NmTableGUIComponent gui = new NmTableGUIComponent(value);
					guicomponentarrayMain.addGUIComponent(gui);
					return guicomponentarrayMain;
				}else {
					return siteName;
				}
			}else if("docTypeName".equals(componentId)) {
				if("DOCTYPENAME".equals(docType)) {
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					String value = "<input type='text' value='' id='"+id+"_DOCTYPENAME' name='"+id+"_DOCTYPENAME'>";
			        NmTableGUIComponent gui = new NmTableGUIComponent(value);
					guicomponentarrayMain.addGUIComponent(gui);
					return guicomponentarrayMain;
				}else {
					return docType;
				}
			}else if("docTypeInnerName".equals(componentId)) {
				if("DOCTYPENAME".equals(docType)) {
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					String value = "<input type='text' value='' id='"+id+"_DOCTYPEINNERNAME' name='"+id+"_DOCTYPEINNERNAME'>";
			        NmTableGUIComponent gui = new NmTableGUIComponent(value);
					guicomponentarrayMain.addGUIComponent(gui);
					return guicomponentarrayMain;
				}else {
					return docTypeInnerName;
				}
			}else if("localDocTypeName".equals(componentId)) {
				if("LOCALDOCTYPENAME".equals(docLocalType)) {
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					ComboBox comboBox = new ComboBox();
					comboBox.addStyleClass("comboxStyle");
					comboBox.setName(componentId);
					comboBox.setId(id);
					interValueList.add(0,"--请选择--");
					displayList.add(0,"--请选择--");
					comboBox.setInternalValues(interValueList);
					comboBox.setValues(displayList);
					comboBox.addJsAction("onChange", "setLocalInnerName(this)");
					guicomponentarrayMain.addGUIComponent(comboBox);
					return guicomponentarrayMain;
				}else {
					return docLocalType;
				}
			}else if("localDocTypeInnerName".equals(componentId)) {
				if("LOCALDOCTYPENAME".equals(docLocalType)) {
					GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
					String value = "<input type='text' value='' id='"+id+"_LOCALDOCTYPEINNERNAME' name='"+id+"_LOCALDOCTYPEINNERNAME' readonly='readonly' style='border:0;'>";
			        NmTableGUIComponent gui = new NmTableGUIComponent(value);
					guicomponentarrayMain.addGUIComponent(gui);
					return guicomponentarrayMain;
				}else {
					return docLocalTypeInnerName;
				}
			}
		}
		return null;
	}


}
