package ext.casc.doc.dataUtility;

import com.glaway.mpm.print.util.MBAUtil;
import com.ptc.core.components.rendering.guicomponents.*;
import com.ptc.core.meta.type.common.impl.DefaultTypeInstance;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.netmarkets.model.NmObject;
import com.ptc.netmarkets.model.NmOid;
import ext.casc.constants.Constants;
import ext.casc.util.IBAHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.httpgw.URLFactory;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;

import ext.casc.util.NmTableGUIComponent;

import java.util.ArrayList;
import java.util.Map;

public class DocDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
		//
        if ("REPLACE".equals(columnName)) {

        	String cName = AttributeDataUtilityHelper.getColumnName(columnName, obj, mc);
        	String colName = mc.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
            StringBuilder sb = new StringBuilder();
            sb.append("<input type=\"text\"  readonly name=\""+colName+"\"  id=\"" + columnName + "\" value=\"\"/>" +
            		"<a id=\"_id_SupplySelect_searchBtn\" \"=\"\" name=\"searchSupply!~objectHandle~partHandle~!\" class=\"linkfont\"> <img title=\"搜索\" alt=\"搜索\" src=\"netmarkets/images/search.gif\" hspace=\"0\" vspace=\"0\" align=\"top\" border=\"0\"> </a>");
            sb.append("							<script type=\"text/javascript\">");

    		sb.append("							searchBtn = Ext.get('_id_SupplySelect_searchBtn');");
    		sb.append("							searchBtn.on('click', function() {");
    		sb.append("									var win;");
    		sb.append("									var url = getBaseHref() + 'netmarkets/jsp/ext/casc/document/selectDoc.jsp?containerReference=';");
    		sb.append("									if(win == undefined) {");
    		sb.append("										win = window.open(url, 'choseSupply');");
    		sb.append("									}");
    		sb.append("								});");
    		sb.append("							</script>");
    		GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
    		guicomponentarrayMain.setValueHidden(false);
    		 NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
             guicomponentarrayMain.addGUIComponent(gui);
             return guicomponentarrayMain;
        }else if("number".equals(columnName)){
			if(obj instanceof DefaultTypeInstance){
				DefaultTypeInstance instance = (DefaultTypeInstance) obj;
				String form = instance.getIdentifier().toExternalForm();
				if(form.contains("PhotoTemplate")){
					return "自动生成";
				}
			}
		}else if("productNumber".equals(columnName) || "psyq".equals(columnName)
			|| "pbzz".equals(columnName) || "psdxType".equals(columnName)){
//			TextBox textBox = getTextBox(columnName, "", true, false);
//			return textBox;

        	String cName = AttributeDataUtilityHelper.getColumnName(columnName, obj, mc);

			StringInputComponent strInput = new StringInputComponent();
			strInput.setEditable(true);
			strInput.setName(cName);
			String type = mc.getDescriptorMode().toString();
			if("EDIT".equals(type)){
				Map<Object, Object> modelData = mc.getModelData();
				NmObject nmObject = (NmObject) modelData.get("nmObject");
				Persistable object = nmObject.getOid().getWtRef().getObject();
				if(object instanceof WTDocument){
					String mbaValue = (String) MBAUtil.getValue((WTDocument) object, columnName);
					strInput.setValue(mbaValue);
				}
			}else{
				strInput.setValue("");
			}
			strInput.setRequired(true);
			return strInput;
		}else if("photoType".equals(columnName)){
        	String cName = AttributeDataUtilityHelper.getColumnName(columnName, obj, mc);
			String type = mc.getDescriptorMode().toString();
			String value = "";
			if("EDIT".equals(type)){
				Map<Object, Object> modelData = mc.getModelData();
				NmObject nmObject = (NmObject) modelData.get("nmObject");
				Persistable object = nmObject.getOid().getWtRef().getObject();
				if(object instanceof WTDocument){
					value = (String) MBAUtil.getValue(object, columnName);
				}
			}
			String colName = mc.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
			GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
			guicomponentarrayMain.setValueHidden(false);
			guicomponentarrayMain.setRequired(false);
			String values = "<input type=\"text\" width=\"400\" readonly name=\"" + colName + "\"  id=\"photoType\" value=\""+value+"\"/>" +
					"<input type=\"button\" value=\"选择分类\" onclick=\"javascript:selectPhotoType(this);\"  id=\"photoType_selected\"/>";
			NmTableGUIComponent gui = new NmTableGUIComponent(values);
			gui.setRequired(false);
			guicomponentarrayMain.addGUIComponent(gui);
			return guicomponentarrayMain;
		}else if("PDCJMC".equals(columnName)){
        	String cName = AttributeDataUtilityHelper.getColumnName(columnName, obj, mc);
			String type = mc.getDescriptorMode().toString();
			String selected = "";
			if("EDIT".equals(type)){
				Map<Object, Object> modelData = mc.getModelData();
				NmObject nmObject = (NmObject) modelData.get("nmObject");
				Persistable object = nmObject.getOid().getWtRef().getObject();
				if(object instanceof WTDocument){
					selected = (String) MBAUtil.getValue(object, columnName);
				}
			}
			ComboBox comboBox = getComboBox(cName, (ArrayList) Constants.photoValueList, (ArrayList) Constants.photoValueList, true,true,selected);
			return comboBox;
		}else if("PROCESSDOCNUM".equals(columnName)){
			URLFactory factory = new URLFactory();
			String base = factory.getHREF("/netmarkets/jsp/ext/casc/document/searchProcessPlan.jsp");
			String cName = AttributeDataUtilityHelper.getColumnName(columnName, obj, mc);
			String colName = mc.getNmCommandBean().getCompContext()+"___"+cName+"_col_"+cName+"___textbox";
			String processDocNum = "";
			NmOid pageOid = mc.getNmCommandBean().getPageOid();
			if(pageOid != null){
				Object object = pageOid.getRefObject();
				if(object instanceof WTDocument){
					WTDocument document = (WTDocument) object;
					if(document != null){
						processDocNum = IBAHelper.getIBAStringValue(document, "PROCESSDOCNUM");
					}
				}
			}
			StringBuilder sb = new StringBuilder();
			sb.append("<input type=\"text\" readonly name=\"PROCESSDOCNAME\"  id=\"PROCESSDOCNAME\" value=\"\" style=\"width:355px; height:18px\"/>");
			sb.append("<a href='javascript:void(0)' onclick='window.open(\"" + base + "\",\"_blank\",\"location=no,resizable=yes,top=0,toolbar=no,height=600\")'>");
			sb.append("<img title=\"搜索\" alt=\"搜索\" src=\"netmarkets/images/search.gif\" hspace=\"0\" vspace=\"0\" align=\"top\" border=\"0\">");
			sb.append(" </a>");
			sb.append("<a id=\"_id_PROCESSDOCNUM_cleanBtn\" \"=\"\" name=\"cleanBtn!~objectHandle~partHandle~!\" class=\"linkfont\"> <img title=\"清空\" alt=\"清空\" src=\"netmarkets/images/clear_16x16.gif\" hspace=\"0\" vspace=\"0\" align=\"top\" border=\"0\"> </a>");
			sb.append("							<script type=\"text/javascript\">");
			sb.append("							cleanBtn = Ext.get('_id_PROCESSDOCNUM_cleanBtn');");
			sb.append("							cleanBtn.on('click', function() {");
			sb.append("									document.getElementById('PROCESSDOCNAME').value = '';");
			sb.append("									document.getElementById('PROCESSDOCNUM').value = '';");
			sb.append("								});");
			sb.append("							</script>");
			sb.append("<input type=\"hidden\" readonly name=\"" + colName + "\"  id=\"" + columnName + "\" value=\"" + processDocNum + "\"");
			GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
			guicomponentarrayMain.setValueHidden(false);
			NmTableGUIComponent gui = new NmTableGUIComponent(sb.toString());
			guicomponentarrayMain.addGUIComponent(gui);
			return guicomponentarrayMain;
		}
        return "";
    }

	private ComboBox getComboBox(String id, ArrayList<String> innerValueList, ArrayList<String> valueList, boolean isRequired, boolean isEditable,String selected) {
		ComboBox comboBox = new ComboBox();
		comboBox.setId(id);
		comboBox.setName(id);
		comboBox.setRequired(isRequired);
		comboBox.setValues(valueList);
		if(valueList.contains(selected)){
			comboBox.setSelected(selected);
		}
		comboBox.setInternalValues(innerValueList);
		comboBox.setEditable(isEditable);
		return comboBox;
	}

	private TextBox getTextBox(String componentId, String value, boolean isRequired, boolean isReadOnly) {
		TextBox textbox = new TextBox();
		textbox.setRequired(isRequired);
		textbox.setName(componentId);
		textbox.setValue(value);
		textbox.setId(componentId);
		textbox.setWidth(25);
		textbox.setMaxLength(1000);
		textbox.setReadOnly(isReadOnly);
		return textbox;
	}

}
