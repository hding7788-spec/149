package com.glaway.mpm.mpmresource.datautilities;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.NmTableGUIComponent;
import wt.util.WTException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.TimeZone;

public class CreateMPMResourceDatautility extends AbstractDataUtility {
	private static final String CLASSNAME = CreateMPMResourceDatautility.class.getName();

	public CreateMPMResourceDatautility() {
	}

	public Object getDataValue(String componentId, Object obj, ModelContext modelcontext) throws WTException {
		NmCommandBean nmcommandbean = modelcontext.getNmCommandBean();
		Object ret = createMPMResourceFields(componentId, nmcommandbean);
		return ret;
	}

	private Object createMPMResourceFields(String componentId, NmCommandBean nmcommandbean) throws WTException {
		Object obj = null;
		if (componentId.equals(AttributeConstants.equipmentNumber)) {
			obj = getTextBox(componentId, "", false, true);
		} else if (componentId.equals(AttributeConstants.zhizaodanwei)) {
		    obj = getGUIComponentArray(componentId, "", true, true);
		} else if (componentId.equals(AttributeConstants.enableddate)||componentId.equals(AttributeConstants.applydate)) {
            obj = getDateInputComponent(componentId, "", true, false);
        } else if (componentId.equals(AttributeConstants.managestatus)) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("在用");
            list.add("封存");
            list.add("报废");
            obj = getComboBox(componentId, list, false, true);
        } else if (componentId.equals(AttributeConstants.level)) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("A");
            list.add("B");
            list.add("C");
            obj = getComboBox(componentId, list, false, true);
        } else if (componentId.equals(AttributeConstants.equipmentType)) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("车床");
            list.add("铣床");
            list.add("转运车");
            obj = getComboBox(componentId, list, false, true);
        } else if (componentId.equals(AttributeConstants.number)
                ||componentId.equals(AttributeConstants.name)){
            obj = getTextBox(componentId, "", true, true);
        } else if (componentId.equals(AttributeConstants.remarkKey)){
            obj = getTextArea(componentId, "", false, true);
        }else if (componentId.equals(AttributeConstants.ZZCJ)) {
            ArrayList<String> list = null;
            try {
                list = ProcessUtil.getGongXuCheJian();
                list.add(0, "");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            obj = getComboBox(componentId, list, false, true);
        }else if (componentId.equals(AttributeConstants.SFSBGS)) {
			//add by yfn 20250702 添加修改【是否设备工时】
			ArrayList<String> list = new ArrayList<>();
			try {
				list.add(0,"");
				list.add(1,"否");
				list.add(2,"是");
			} catch (Exception e) {
				e.printStackTrace();
			}
			obj = getComboBox(componentId, list, false, true);
		} else {
			obj = getTextBox(componentId, "", false, true);
		}

		return obj;
	}

	 private ComboBox getComboBox(String id, ArrayList<String> innerValueList, ArrayList<String> valueList, boolean isRequired, boolean isEditable) {
	        ComboBox comboBox = new ComboBox();
	        comboBox.setId(id);
	        comboBox.setName(id);
	        comboBox.setRequired(isRequired);
	        comboBox.setValues(valueList);
	        comboBox.setInternalValues(innerValueList);
	        comboBox.setEditable(isEditable);
	        return comboBox;
	    }

	private TextBox getTextBox(String componentId, String value, boolean isRequired, boolean isEditable) {
		TextBox textbox = new TextBox();
		textbox.setRequired(isRequired);
		textbox.setInputType("text");
		textbox.setName(componentId);
		textbox.setValue(value);
		textbox.setId(componentId);
		textbox.setWidth(60);
		textbox.setMaxLength(255);
		textbox.setEditable(isEditable);
		return textbox;
	}

	private TextArea getTextArea(String componentId, String value, boolean isRequired, boolean isEditable) {
	    TextArea textArea = new TextArea();
	    textArea.setId(componentId);
	    textArea.setName(componentId);
	    textArea.setEditable(isEditable);
	    textArea.setRequired(isRequired);
	    return textArea;
	}

    private DateInputComponent getDateInputComponent(String componentId, String value, boolean isRequired,
            boolean isEditable) {
        DateInputComponent date = new DateInputComponent(componentId, DateInputComponent.ValueType.DATE_ONLY);
        date.setTimeZone(TimeZone.getDefault());
        date.setId(componentId);
        date.setName(componentId);
        date.setColumnName(componentId);
        date.setReadOnly(isEditable);
        return date;
    }

	private ComboBox getComboBox(String componentId, ArrayList<String> valueList, boolean isRequired, boolean isEditable) {
		ComboBox comboBox = new ComboBox();
		comboBox.setRequired(isRequired);
		comboBox.setName(componentId);
		comboBox.setValues(valueList);
		comboBox.setInternalValues(valueList);
		comboBox.setId(componentId);
		comboBox.setEditable(isEditable);
		return comboBox;
	}

	private GUIComponentArray getGUIComponentArray(String componentId, String value, boolean isRequired, boolean isEditable) {
	    GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        guicomponentarrayMain.setValueHidden(false);
        guicomponentarrayMain.setRequired(isRequired);
        String values = "<input type=\"text\" width=\"400\" readonly name=\"zhizaodanwei\"  id=\"zhizaodanwei\" />" +
        		"<input type=\"button\" value=\"选择\" onclick=\"javascript:setSelZhiZaoDanWei(this);\"  id=\"zhizaodanwei_selected\"/>";
        NmTableGUIComponent gui = new NmTableGUIComponent(values);
        gui.setRequired(isRequired);
        guicomponentarrayMain.addGUIComponent(gui);
        return guicomponentarrayMain;
	}

}
